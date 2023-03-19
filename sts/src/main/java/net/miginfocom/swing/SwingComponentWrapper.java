package net.miginfocom.swing;

import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.List;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Float;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.PlatformDefaults;

public class SwingComponentWrapper implements ComponentWrapper {
   private static boolean maxSet = false;
   private static boolean vp = true;
   private static final Color DB_COMP_OUTLINE = new Color(0, 0, 200);
   private final Component c;
   private int compType = -1;
   private Boolean bl = null;
   private boolean prefCalled = false;
   private static final IdentityHashMap<FontMetrics, Float> FM_MAP = new IdentityHashMap(4);
   private static final Font SUBST_FONT = new Font("sansserif", 0, 11);
   private static Method BL_METHOD = null;
   private static Method BL_RES_METHOD = null;
   private static Method IMS_METHOD;

   public SwingComponentWrapper(Component c) {
      super();
      this.c = c;
   }

   @Override
   public final int getBaseline(int width, int height) {
      if (BL_METHOD == null) {
         return -1;
      } else {
         try {
            Object[] args = new Object[]{width < 0 ? this.c.getWidth() : width, height < 0 ? this.c.getHeight() : height};
            return BL_METHOD.invoke(this.c, args);
         } catch (Exception var4) {
            return -1;
         }
      }
   }

   @Override
   public final Object getComponent() {
      return this.c;
   }

   @Override
   public final float getPixelUnitFactor(boolean isHor) {
      switch(PlatformDefaults.getLogicalPixelBase()) {
         case 100:
            Font font = this.c.getFont();
            FontMetrics fm = this.c.getFontMetrics(font != null ? font : SUBST_FONT);
            Float p = (Float)FM_MAP.get(fm);
            if (p == null) {
               Rectangle2D r = fm.getStringBounds("X", this.c.getGraphics());
               p = new Float((float)r.getWidth() / 6.0F, (float)r.getHeight() / 13.277344F);
               FM_MAP.put(fm, p);
            }

            return isHor ? p.x : p.y;
         case 101:
            java.lang.Float s = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
            if (s != null) {
               return s;
            }

            return (float)(isHor ? this.getHorizontalScreenDPI() : this.getVerticalScreenDPI()) / (float)PlatformDefaults.getDefaultDPI();
         default:
            return 1.0F;
      }
   }

   @Override
   public final int getX() {
      return this.c.getX();
   }

   @Override
   public final int getY() {
      return this.c.getY();
   }

   @Override
   public final int getHeight() {
      return this.c.getHeight();
   }

   @Override
   public final int getWidth() {
      return this.c.getWidth();
   }

   @Override
   public final int getScreenLocationX() {
      Point p = new Point();
      SwingUtilities.convertPointToScreen(p, this.c);
      return p.x;
   }

   @Override
   public final int getScreenLocationY() {
      Point p = new Point();
      SwingUtilities.convertPointToScreen(p, this.c);
      return p.y;
   }

   @Override
   public final int getMinimumHeight(int sz) {
      if (!this.prefCalled) {
         this.c.getPreferredSize();
         this.prefCalled = true;
      }

      return this.c.getMinimumSize().height;
   }

   @Override
   public final int getMinimumWidth(int sz) {
      if (!this.prefCalled) {
         this.c.getPreferredSize();
         this.prefCalled = true;
      }

      return this.c.getMinimumSize().width;
   }

   @Override
   public final int getPreferredHeight(int sz) {
      if (this.c.getWidth() == 0 && this.c.getHeight() == 0 && sz != -1) {
         this.c.setBounds(this.c.getX(), this.c.getY(), sz, 1);
      }

      return this.c.getPreferredSize().height;
   }

   @Override
   public final int getPreferredWidth(int sz) {
      if (this.c.getWidth() == 0 && this.c.getHeight() == 0 && sz != -1) {
         this.c.setBounds(this.c.getX(), this.c.getY(), 1, sz);
      }

      return this.c.getPreferredSize().width;
   }

   @Override
   public final int getMaximumHeight(int sz) {
      return !this.isMaxSet(this.c) ? 32767 : this.c.getMaximumSize().height;
   }

   @Override
   public final int getMaximumWidth(int sz) {
      return !this.isMaxSet(this.c) ? 32767 : this.c.getMaximumSize().width;
   }

   private boolean isMaxSet(Component c) {
      if (IMS_METHOD != null) {
         try {
            return IMS_METHOD.invoke(c, (Object[])null);
         } catch (Exception var3) {
            IMS_METHOD = null;
         }
      }

      return isMaxSizeSetOn1_4();
   }

   @Override
   public final ContainerWrapper getParent() {
      Container p = this.c.getParent();
      return p != null ? new SwingContainerWrapper(p) : null;
   }

   @Override
   public final int getHorizontalScreenDPI() {
      return PlatformDefaults.getDefaultDPI();
   }

   @Override
   public final int getVerticalScreenDPI() {
      return PlatformDefaults.getDefaultDPI();
   }

   @Override
   public final int getScreenWidth() {
      try {
         return this.c.getToolkit().getScreenSize().width;
      } catch (HeadlessException var2) {
         return 1024;
      }
   }

   @Override
   public final int getScreenHeight() {
      try {
         return this.c.getToolkit().getScreenSize().height;
      } catch (HeadlessException var2) {
         return 768;
      }
   }

   @Override
   public final boolean hasBaseline() {
      if (this.bl == null) {
         try {
            if (BL_RES_METHOD != null && !BL_RES_METHOD.invoke(this.c).toString().equals("OTHER")) {
               Dimension d = this.c.getMinimumSize();
               this.bl = this.getBaseline(d.width, d.height) > -1;
            } else {
               this.bl = Boolean.FALSE;
            }
         } catch (Throwable var2) {
            this.bl = Boolean.FALSE;
         }
      }

      return this.bl;
   }

   @Override
   public final String getLinkId() {
      return this.c.getName();
   }

   @Override
   public final void setBounds(int x, int y, int width, int height) {
      this.c.setBounds(x, y, width, height);
   }

   @Override
   public boolean isVisible() {
      return this.c.isVisible();
   }

   @Override
   public final int[] getVisualPadding() {
      return vp && this.c instanceof JTabbedPane && UIManager.getLookAndFeel().getClass().getName().endsWith("WindowsLookAndFeel")
         ? new int[]{-1, 0, 2, 2}
         : null;
   }

   public static boolean isMaxSizeSetOn1_4() {
      return maxSet;
   }

   public static void setMaxSizeSetOn1_4(boolean b) {
      maxSet = b;
   }

   public static boolean isVisualPaddingEnabled() {
      return vp;
   }

   public static void setVisualPaddingEnabled(boolean b) {
      vp = b;
   }

   @Override
   public final void paintDebugOutline() {
      if (this.c.isShowing()) {
         Graphics2D g = (Graphics2D)this.c.getGraphics();
         if (g != null) {
            g.setPaint(DB_COMP_OUTLINE);
            g.setStroke(new BasicStroke(1.0F, 2, 0, 10.0F, new float[]{2.0F, 4.0F}, 0.0F));
            g.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
         }
      }
   }

   @Override
   public int getComponetType(boolean disregardScrollPane) {
      if (this.compType == -1) {
         this.compType = this.checkType(disregardScrollPane);
      }

      return this.compType;
   }

   @Override
   public int getLayoutHashCode() {
      Dimension d = this.c.getMaximumSize();
      int hash = d.width + (d.height << 5);
      d = this.c.getPreferredSize();
      hash += (d.width << 10) + (d.height << 15);
      d = this.c.getMinimumSize();
      hash += (d.width << 20) + (d.height << 25);
      if (this.c.isVisible()) {
         hash += 1324511;
      }

      String id = this.getLinkId();
      if (id != null) {
         hash += id.hashCode();
      }

      return hash;
   }

   private int checkType(boolean disregardScrollPane) {
      Component c = this.c;
      if (disregardScrollPane) {
         if (c instanceof JScrollPane) {
            c = ((JScrollPane)c).getViewport().getView();
         } else if (c instanceof ScrollPane) {
            c = ((ScrollPane)c).getComponent(0);
         }
      }

      if (c instanceof JTextField || c instanceof TextField) {
         return 3;
      } else if (c instanceof JLabel || c instanceof Label) {
         return 2;
      } else if (c instanceof JToggleButton || c instanceof Checkbox) {
         return 16;
      } else if (c instanceof AbstractButton || c instanceof Button) {
         return 5;
      } else if (c instanceof JComboBox || c instanceof Choice) {
         return 2;
      } else if (c instanceof JTextComponent || c instanceof TextComponent) {
         return 4;
      } else if (c instanceof JPanel || c instanceof Canvas) {
         return 10;
      } else if (c instanceof JList || c instanceof List) {
         return 6;
      } else if (c instanceof JTable) {
         return 7;
      } else if (c instanceof JSeparator) {
         return 18;
      } else if (c instanceof JSpinner) {
         return 13;
      } else if (c instanceof JProgressBar) {
         return 14;
      } else if (c instanceof JSlider) {
         return 12;
      } else if (c instanceof JScrollPane) {
         return 8;
      } else if (c instanceof JScrollBar || c instanceof Scrollbar) {
         return 17;
      } else {
         return c instanceof Container ? 1 : 0;
      }
   }

   public final int hashCode() {
      return this.getComponent().hashCode();
   }

   public final boolean equals(Object o) {
      return !(o instanceof ComponentWrapper) ? false : this.getComponent().equals(((ComponentWrapper)o).getComponent());
   }

   static {
      try {
         BL_METHOD = Component.class.getDeclaredMethod("getBaseline", Integer.TYPE, Integer.TYPE);
         BL_RES_METHOD = Component.class.getDeclaredMethod("getBaselineResizeBehavior");
      } catch (Throwable var2) {
      }

      IMS_METHOD = null;

      try {
         IMS_METHOD = Component.class.getDeclaredMethod("isMaximumSizeSet", (Class[])null);
      } catch (Throwable var1) {
      }
   }
}
