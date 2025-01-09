package net.miginfocom.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.layout.LayoutUtil;
import net.miginfocom.layout.PlatformDefaults;
import net.miginfocom.layout.UnitValue;

public final class MigLayout implements LayoutManager2, Externalizable {
   private final Map<Component, Object> scrConstrMap = new IdentityHashMap(8);
   private Object layoutConstraints = "";
   private Object colConstraints = "";
   private Object rowConstraints = "";
   private transient ContainerWrapper cacheParentW = null;
   private final transient Map<ComponentWrapper, CC> ccMap = new HashMap(8);
   private transient Timer debugTimer = null;
   private transient LC lc = null;
   private transient AC colSpecs = null;
   private transient AC rowSpecs = null;
   private transient Grid grid = null;
   private transient int lastModCount = PlatformDefaults.getModCount();
   private transient int lastHash = -1;
   private transient Dimension lastInvalidSize = null;
   private transient boolean lastWasInvalid = false;
   private transient Dimension lastParentSize = null;
   private transient ArrayList<LayoutCallback> callbackList = null;
   private transient boolean dirty = true;
   private long lastSize = 0L;

   public MigLayout() {
      this("", "", "");
   }

   public MigLayout(String layoutConstraints) {
      this(layoutConstraints, "", "");
   }

   public MigLayout(String layoutConstraints, String colConstraints) {
      this(layoutConstraints, colConstraints, "");
   }

   public MigLayout(String layoutConstraints, String colConstraints, String rowConstraints) {
      this.setLayoutConstraints(layoutConstraints);
      this.setColumnConstraints(colConstraints);
      this.setRowConstraints(rowConstraints);
   }

   public MigLayout(LC layoutConstraints) {
      this(layoutConstraints, null, null);
   }

   public MigLayout(LC layoutConstraints, AC colConstraints) {
      this(layoutConstraints, colConstraints, null);
   }

   public MigLayout(LC layoutConstraints, AC colConstraints, AC rowConstraints) {
      this.setLayoutConstraints(layoutConstraints);
      this.setColumnConstraints(colConstraints);
      this.setRowConstraints(rowConstraints);
   }

   public Object getLayoutConstraints() {
      return this.layoutConstraints;
   }

   public void setLayoutConstraints(Object constr) {
      if (constr != null && !(constr instanceof String)) {
         if (!(constr instanceof LC)) {
            throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
         }

         this.lc = (LC)constr;
      } else {
         constr = ConstraintParser.prepare((String)constr);
         this.lc = ConstraintParser.parseLayoutConstraint((String)constr);
      }

      this.layoutConstraints = constr;
      this.dirty = true;
   }

   public Object getColumnConstraints() {
      return this.colConstraints;
   }

   public void setColumnConstraints(Object constr) {
      if (constr != null && !(constr instanceof String)) {
         if (!(constr instanceof AC)) {
            throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
         }

         this.colSpecs = (AC)constr;
      } else {
         constr = ConstraintParser.prepare((String)constr);
         this.colSpecs = ConstraintParser.parseColumnConstraints((String)constr);
      }

      this.colConstraints = constr;
      this.dirty = true;
   }

   public Object getRowConstraints() {
      return this.rowConstraints;
   }

   public void setRowConstraints(Object constr) {
      if (constr != null && !(constr instanceof String)) {
         if (!(constr instanceof AC)) {
            throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
         }

         this.rowSpecs = (AC)constr;
      } else {
         constr = ConstraintParser.prepare((String)constr);
         this.rowSpecs = ConstraintParser.parseRowConstraints((String)constr);
      }

      this.rowConstraints = constr;
      this.dirty = true;
   }

   public Map<Component, Object> getConstraintMap() {
      return new IdentityHashMap(this.scrConstrMap);
   }

   public void setConstraintMap(Map<Component, Object> map) {
      this.scrConstrMap.clear();
      this.ccMap.clear();

      for (Entry<Component, Object> e : map.entrySet()) {
         this.setComponentConstraintsImpl((Component)e.getKey(), e.getValue(), true);
      }
   }

   public Object getComponentConstraints(Component comp) {
      synchronized (comp.getParent().getTreeLock()) {
         return this.scrConstrMap.get(comp);
      }
   }

   public void setComponentConstraints(Component comp, Object constr) {
      this.setComponentConstraintsImpl(comp, constr, false);
   }

   private void setComponentConstraintsImpl(Component comp, Object constr, boolean noCheck) {
      Container parent = comp.getParent();
      synchronized (parent != null ? parent.getTreeLock() : new Object()) {
         if (!noCheck && !this.scrConstrMap.containsKey(comp)) {
            throw new IllegalArgumentException("Component must already be added to parent!");
         } else {
            ComponentWrapper cw = new SwingComponentWrapper(comp);
            if (constr != null && !(constr instanceof String)) {
               if (!(constr instanceof CC)) {
                  throw new IllegalArgumentException("Constraint must be String or ComponentConstraint: " + constr.getClass().toString());
               }

               this.scrConstrMap.put(comp, constr);
               this.ccMap.put(cw, (CC)constr);
            } else {
               String cStr = ConstraintParser.prepare((String)constr);
               this.scrConstrMap.put(comp, constr);
               this.ccMap.put(cw, ConstraintParser.parseComponentConstraint(cStr));
            }

            this.dirty = true;
         }
      }
   }

   public boolean isManagingComponent(Component c) {
      return this.scrConstrMap.containsKey(c);
   }

   public void addLayoutCallback(LayoutCallback callback) {
      if (callback == null) {
         throw new NullPointerException();
      } else {
         if (this.callbackList == null) {
            this.callbackList = new ArrayList(1);
         }

         this.callbackList.add(callback);
      }
   }

   public void removeLayoutCallback(LayoutCallback callback) {
      if (this.callbackList != null) {
         this.callbackList.remove(callback);
      }
   }

   private void setDebug(ComponentWrapper parentW, boolean b) {
      if (b && (this.debugTimer == null || this.debugTimer.getDelay() != this.getDebugMillis())) {
         if (this.debugTimer != null) {
            this.debugTimer.stop();
         }

         ContainerWrapper pCW = parentW.getParent();
         final Component parent = pCW != null ? (Component)pCW.getComponent() : null;
         this.debugTimer = new Timer(this.getDebugMillis(), new MigLayout.MyDebugRepaintListener());
         if (parent != null) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  Container p = parent.getParent();
                  if (p != null) {
                     if (p instanceof JComponent) {
                        ((JComponent)p).revalidate();
                     } else {
                        parent.invalidate();
                        p.validate();
                     }
                  }
               }
            });
         }

         this.debugTimer.setInitialDelay(100);
         this.debugTimer.start();
      } else if (!b && this.debugTimer != null) {
         this.debugTimer.stop();
         this.debugTimer = null;
      }
   }

   private boolean getDebug() {
      return this.debugTimer != null;
   }

   private int getDebugMillis() {
      int globalDebugMillis = LayoutUtil.getGlobalDebugMillis();
      return globalDebugMillis > 0 ? globalDebugMillis : this.lc.getDebugMillis();
   }

   private void checkCache(Container parent) {
      if (parent != null) {
         if (this.dirty) {
            this.grid = null;
         }

         int mc = PlatformDefaults.getModCount();
         if (this.lastModCount != mc) {
            this.grid = null;
            this.lastModCount = mc;
         }

         if (!parent.isValid()) {
            if (!this.lastWasInvalid) {
               this.lastWasInvalid = true;
               int hash = 0;
               boolean resetLastInvalidOnParent = false;

               for (ComponentWrapper wrapper : this.ccMap.keySet()) {
                  Object component = wrapper.getComponent();
                  if (component instanceof JTextArea || component instanceof JEditorPane) {
                     resetLastInvalidOnParent = true;
                  }

                  hash ^= wrapper.getLayoutHashCode();
                  hash += 285134905;
               }

               if (resetLastInvalidOnParent) {
                  this.resetLastInvalidOnParent(parent);
               }

               if (hash != this.lastHash) {
                  this.grid = null;
                  this.lastHash = hash;
               }

               Dimension ps = parent.getSize();
               if (this.lastInvalidSize == null || !this.lastInvalidSize.equals(ps)) {
                  if (this.grid != null) {
                     this.grid.invalidateContainerSize();
                  }

                  this.lastInvalidSize = ps;
               }
            }
         } else {
            this.lastWasInvalid = false;
         }

         ContainerWrapper par = this.checkParent(parent);
         this.setDebug(par, this.getDebugMillis() > 0);
         if (this.grid == null) {
            this.grid = new Grid(par, this.lc, this.rowSpecs, this.colSpecs, this.ccMap, this.callbackList);
         }

         this.dirty = false;
      }
   }

   private void resetLastInvalidOnParent(Container parent) {
      while (parent != null) {
         LayoutManager layoutManager = parent.getLayout();
         if (layoutManager instanceof MigLayout) {
            ((MigLayout)layoutManager).lastWasInvalid = false;
         }

         parent = parent.getParent();
      }
   }

   private ContainerWrapper checkParent(Container parent) {
      if (parent == null) {
         return null;
      } else {
         if (this.cacheParentW == null || this.cacheParentW.getComponent() != parent) {
            this.cacheParentW = new SwingContainerWrapper(parent);
         }

         return this.cacheParentW;
      }
   }

   public void layoutContainer(Container parent) {
      synchronized (parent.getTreeLock()) {
         this.checkCache(parent);
         Insets i = parent.getInsets();
         int[] b = new int[]{i.left, i.top, parent.getWidth() - i.left - i.right, parent.getHeight() - i.top - i.bottom};
         if (this.grid.layout(b, this.lc.getAlignX(), this.lc.getAlignY(), this.getDebug(), true)) {
            this.grid = null;
            this.checkCache(parent);
            this.grid.layout(b, this.lc.getAlignX(), this.lc.getAlignY(), this.getDebug(), false);
         }

         long newSize = (long)this.grid.getHeight()[1] + ((long)this.grid.getWidth()[1] << 32);
         if (this.lastSize != newSize) {
            this.lastSize = newSize;
            final ContainerWrapper containerWrapper = this.checkParent(parent);
            Window win = (Window)SwingUtilities.getAncestorOfClass(Window.class, (Component)containerWrapper.getComponent());
            if (win != null) {
               if (win.isVisible()) {
                  SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                        MigLayout.this.adjustWindowSize(containerWrapper);
                     }
                  });
               } else {
                  this.adjustWindowSize(containerWrapper);
               }
            }
         }

         this.lastInvalidSize = null;
      }
   }

   private void adjustWindowSize(ContainerWrapper parent) {
      BoundSize wBounds = this.lc.getPackWidth();
      BoundSize hBounds = this.lc.getPackHeight();
      if (wBounds != null || hBounds != null) {
         Window win = (Window)SwingUtilities.getAncestorOfClass(Window.class, (Component)parent.getComponent());
         if (win != null) {
            Dimension prefSize = win.getPreferredSize();
            int targW = this.constrain(this.checkParent(win), win.getWidth(), prefSize.width, wBounds);
            int targH = this.constrain(this.checkParent(win), win.getHeight(), prefSize.height, hBounds);
            int x = Math.round((float)win.getX() - (float)(targW - win.getWidth()) * (1.0F - this.lc.getPackWidthAlign()));
            int y = Math.round((float)win.getY() - (float)(targH - win.getHeight()) * (1.0F - this.lc.getPackHeightAlign()));
            win.setBounds(x, y, targW, targH);
         }
      }
   }

   private int constrain(ContainerWrapper parent, int winSize, int prefSize, BoundSize constrain) {
      if (constrain == null) {
         return winSize;
      } else {
         int retSize = winSize;
         UnitValue wUV = constrain.getPreferred();
         if (wUV != null) {
            retSize = wUV.getPixels((float)prefSize, parent, parent);
         }

         retSize = constrain.constrain(retSize, (float)prefSize, parent);
         return constrain.getGapPush() ? Math.max(winSize, retSize) : retSize;
      }
   }

   public Dimension minimumLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()) {
         return this.getSizeImpl(parent, 0);
      }
   }

   public Dimension preferredLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()) {
         if (this.lastParentSize == null || !parent.getSize().equals(this.lastParentSize)) {
            for (ComponentWrapper wrapper : this.ccMap.keySet()) {
               Component c = (Component)wrapper.getComponent();
               if (c instanceof JTextArea
                  || c instanceof JEditorPane
                  || c instanceof JComponent && Boolean.TRUE.equals(((JComponent)c).getClientProperty("migLayout.dynamicAspectRatio"))) {
                  this.layoutContainer(parent);
                  break;
               }
            }
         }

         this.lastParentSize = parent.getSize();
         return this.getSizeImpl(parent, 1);
      }
   }

   public Dimension maximumLayoutSize(Container parent) {
      return new Dimension(32767, 32767);
   }

   private Dimension getSizeImpl(Container parent, int sizeType) {
      this.checkCache(parent);
      Insets i = parent.getInsets();
      int w = LayoutUtil.getSizeSafe(this.grid != null ? this.grid.getWidth() : null, sizeType) + i.left + i.right;
      int h = LayoutUtil.getSizeSafe(this.grid != null ? this.grid.getHeight() : null, sizeType) + i.top + i.bottom;
      return new Dimension(w, h);
   }

   public float getLayoutAlignmentX(Container parent) {
      return this.lc != null && this.lc.getAlignX() != null ? (float)this.lc.getAlignX().getPixels(1.0F, this.checkParent(parent), null) : 0.0F;
   }

   public float getLayoutAlignmentY(Container parent) {
      return this.lc != null && this.lc.getAlignY() != null ? (float)this.lc.getAlignY().getPixels(1.0F, this.checkParent(parent), null) : 0.0F;
   }

   public void addLayoutComponent(String s, Component comp) {
      this.addLayoutComponent(comp, s);
   }

   public void addLayoutComponent(Component comp, Object constraints) {
      synchronized (comp.getParent().getTreeLock()) {
         this.setComponentConstraintsImpl(comp, constraints, true);
      }
   }

   public void removeLayoutComponent(Component comp) {
      synchronized (comp.getParent().getTreeLock()) {
         this.scrConstrMap.remove(comp);
         this.ccMap.remove(new SwingComponentWrapper(comp));
      }
   }

   public void invalidateLayout(Container target) {
      this.dirty = true;
   }

   private Object readResolve() throws ObjectStreamException {
      return LayoutUtil.getSerializedObject(this);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(in));
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      if (this.getClass() == MigLayout.class) {
         LayoutUtil.writeAsXML(out, this);
      }
   }

   private class MyDebugRepaintListener implements ActionListener {
      private MyDebugRepaintListener() {
      }

      public void actionPerformed(ActionEvent e) {
         if (MigLayout.this.grid != null) {
            Component comp = (Component)MigLayout.this.grid.getContainer().getComponent();
            if (comp.isShowing()) {
               MigLayout.this.grid.paintDebug();
               return;
            }
         }

         MigLayout.this.debugTimer.stop();
         MigLayout.this.debugTimer = null;
      }
   }
}
