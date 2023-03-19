package js.java.tools.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public final class ViewTooltips extends MouseAdapter implements MouseMotionListener {
   private static ViewTooltips INSTANCE = null;
   private int refcount = 0;
   private JComponent inner = null;
   private int row = -1;
   private Popup[] popups = new Popup[2];
   private ViewTooltips.ImgComp painter = new ViewTooltips.ImgComp();
   private ViewTooltips.Hider hider = null;

   private ViewTooltips() {
      super();
   }

   public static void register(JComponent comp) {
      if (INSTANCE == null) {
         INSTANCE = new ViewTooltips();
      }

      INSTANCE.attachTo(comp);
   }

   public static void unregister(JComponent comp) {
      assert INSTANCE != null : "Unregister asymmetrically called";

      if (INSTANCE.detachFrom(comp) == 0) {
         INSTANCE.hide();
         INSTANCE = null;
      }
   }

   private void attachTo(JComponent comp) {
      assert comp instanceof JTree || comp instanceof JList;

      comp.addMouseListener(this);
      comp.addMouseMotionListener(this);
      ++this.refcount;
   }

   private int detachFrom(JComponent comp) {
      assert comp instanceof JTree || comp instanceof JList;

      comp.removeMouseMotionListener(this);
      comp.removeMouseListener(this);
      return this.refcount--;
   }

   public void mouseMoved(MouseEvent e) {
      Point p = e.getPoint();
      JComponent comp = (JComponent)e.getSource();
      JScrollPane jsp = (JScrollPane)SwingUtilities.getAncestorOfClass(JScrollPane.class, comp);
      if (jsp != null) {
         p = SwingUtilities.convertPoint(comp, p, jsp);
         this.show(jsp, p);
      }
   }

   public void mouseDragged(MouseEvent e) {
      this.hide();
   }

   public void mouseEntered(MouseEvent e) {
      this.hide();
   }

   public void mouseExited(MouseEvent e) {
      this.hide();
   }

   void show(JScrollPane view, Point pt) {
      if (view.getViewport().getView() instanceof JTree) {
         this.showJTree(view, pt);
      } else if (view.getViewport().getView() instanceof JList) {
         this.showJList(view, pt);
      } else {
         assert false : "Bad component type registered: " + view.getViewport().getView();
      }
   }

   private void showJList(JScrollPane view, Point pt) {
      JList list = (JList)view.getViewport().getView();
      Point p = SwingUtilities.convertPoint(view, pt.x, pt.y, list);
      int row = list.locationToIndex(p);
      if (row == -1) {
         this.hide();
      } else {
         Rectangle bds = list.getCellBounds(row, row);
         ListCellRenderer ren = list.getCellRenderer();
         Dimension rendererSize = ren.getListCellRendererComponent(list, list.getModel().getElementAt(row), row, false, false).getPreferredSize();
         bds.width = rendererSize.width;
         if (bds != null && bds.contains(p)) {
            if (this.setCompAndRow(list, row)) {
               Rectangle visible = this.getShowingRect(view);
               Rectangle[] rects = getRects(bds, visible);
               if (rects.length > 0) {
                  this.ensureOldPopupsHidden();
                  this.painter.configure(list.getModel().getElementAt(row), view, list, row);
                  this.showPopups(rects, bds, visible, list, view);
               } else {
                  this.hide();
               }
            }
         } else {
            this.hide();
         }
      }
   }

   private void showJTree(JScrollPane view, Point pt) {
      JTree tree = (JTree)view.getViewport().getView();
      Point p = SwingUtilities.convertPoint(view, pt.x, pt.y, tree);
      int row = tree.getClosestRowForLocation(p.x, p.y);
      TreePath path = tree.getClosestPathForLocation(p.x, p.y);
      Rectangle bds = tree.getPathBounds(path);
      if (bds != null && bds.contains(p)) {
         if (this.setCompAndRow(tree, row)) {
            Rectangle visible = this.getShowingRect(view);
            Rectangle[] rects = getRects(bds, visible);
            if (rects.length > 0) {
               this.ensureOldPopupsHidden();
               this.painter.configure(path.getLastPathComponent(), view, tree, path, row);
               this.showPopups(rects, bds, visible, tree, view);
            } else {
               this.hide();
            }
         }
      } else {
         this.hide();
      }
   }

   private boolean setCompAndRow(JComponent inner, int row) {
      boolean rowChanged = row != this.row;
      boolean compChanged = inner != this.inner;
      this.inner = inner;
      this.row = row;
      return rowChanged || compChanged;
   }

   void hide() {
      this.ensureOldPopupsHidden();
      if (this.painter != null) {
         this.painter.clear();
      }

      this.setHideComponent(null, null);
      this.inner = null;
      this.row = -1;
   }

   private void ensureOldPopupsHidden() {
      for(int i = 0; i < this.popups.length; ++i) {
         if (this.popups[i] != null) {
            this.popups[i].hide();
            this.popups[i] = null;
         }
      }
   }

   private Rectangle getShowingRect(JScrollPane pane) {
      Insets ins1 = pane.getViewport().getInsets();
      Border inner = pane.getViewportBorder();
      Insets ins2;
      if (inner != null) {
         ins2 = inner.getBorderInsets(pane);
      } else {
         ins2 = new Insets(0, 0, 0, 0);
      }

      Insets ins3 = new Insets(0, 0, 0, 0);
      if (pane.getBorder() != null) {
         ins3 = pane.getBorder().getBorderInsets(pane);
      }

      Rectangle r = pane.getViewportBorderBounds();
      r.translate(-r.x, -r.y);
      r.width -= ins1.left + ins1.right;
      r.width -= ins2.left + ins2.right;
      r.height -= ins1.top + ins1.bottom;
      r.height -= ins2.top + ins2.bottom;
      r.x -= ins2.left;
      r.x -= ins3.left;
      Point p = pane.getViewport().getViewPosition();
      r.translate(p.x, p.y);
      return SwingUtilities.convertRectangle(pane.getViewport(), r, pane);
   }

   private static Rectangle[] getRects(Rectangle bds, Rectangle vis) {
      Rectangle[] result;
      if (vis.contains(bds)) {
         result = new Rectangle[0];
      } else if (bds.x < vis.x && bds.x + bds.width > vis.x + vis.width) {
         Rectangle a = new Rectangle(bds.x, bds.y, vis.x - bds.x, bds.height);
         Rectangle b = new Rectangle(vis.x + vis.width, bds.y, bds.x + bds.width - (vis.x + vis.width), bds.height);
         result = new Rectangle[]{a, b};
      } else if (bds.x < vis.x) {
         result = new Rectangle[]{new Rectangle(bds.x, bds.y, vis.x - bds.x, bds.height)};
      } else if (bds.x + bds.width > vis.x + vis.width) {
         result = new Rectangle[]{new Rectangle(vis.x + vis.width, bds.y, bds.x + bds.width - (vis.x + vis.width), bds.height)};
      } else {
         result = new Rectangle[0];
      }

      int i = 0;

      while(i < result.length) {
         ++i;
      }

      return result;
   }

   private void showPopups(Rectangle[] rects, Rectangle bds, Rectangle visible, JComponent comp, JScrollPane view) {
      boolean shown = false;

      for(int i = 0; i < rects.length; ++i) {
         Rectangle sect = rects[i];
         sect.translate(-bds.x, -bds.y);
         ViewTooltips.ImgComp part = this.painter.getPartial(sect, bds.x + rects[i].x < visible.x);
         Point pos = new Point(bds.x + rects[i].x, bds.y + rects[i].y);
         SwingUtilities.convertPointToScreen(pos, comp);
         if (comp instanceof JList) {
            --pos.y;
         }

         if (pos.x > 0) {
            this.popups[i] = getPopupFactory().getPopup(view, part, pos.x, pos.y);
            this.popups[i].show();
            shown = true;
         }
      }

      if (shown) {
         this.setHideComponent(comp, view);
      } else {
         this.setHideComponent(null, null);
      }
   }

   private static PopupFactory getPopupFactory() {
      return PopupFactory.getSharedInstance();
   }

   private void setHideComponent(JComponent comp, JScrollPane parent) {
      if (this.hider == null || !this.hider.isListeningTo(comp)) {
         if (this.hider != null) {
            this.hider.detach();
         }

         if (comp != null) {
            this.hider = new ViewTooltips.Hider(comp, parent);
         } else {
            this.hider = null;
         }
      }
   }

   private static final class Hider
      implements ChangeListener,
      PropertyChangeListener,
      TreeModelListener,
      TreeSelectionListener,
      HierarchyListener,
      HierarchyBoundsListener,
      ListSelectionListener,
      ListDataListener,
      ComponentListener {
      private final JTree tree;
      private JScrollPane pane;
      private final JList list;
      private boolean detached = false;

      Hider(JComponent comp, JScrollPane pane) {
         super();
         if (comp instanceof JTree) {
            this.tree = (JTree)comp;
            this.list = null;
         } else {
            this.list = (JList)comp;
            this.tree = null;
         }

         assert this.tree != null || this.list != null;

         this.pane = pane;
         this.attach();
      }

      private boolean isListeningTo(JComponent comp) {
         return !this.detached && (comp == this.list || comp == this.tree);
      }

      private void attach() {
         if (this.tree != null) {
            this.tree.getModel().addTreeModelListener(this);
            this.tree.getSelectionModel().addTreeSelectionListener(this);
            this.tree.addHierarchyBoundsListener(this);
            this.tree.addHierarchyListener(this);
            this.tree.addComponentListener(this);
         } else {
            this.list.getSelectionModel().addListSelectionListener(this);
            this.list.getModel().addListDataListener(this);
            this.list.addHierarchyBoundsListener(this);
            this.list.addHierarchyListener(this);
            this.list.addComponentListener(this);
         }

         this.pane.getHorizontalScrollBar().getModel().addChangeListener(this);
         this.pane.getVerticalScrollBar().getModel().addChangeListener(this);
         KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this);
      }

      private void detach() {
         KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(this);
         if (this.tree != null) {
            this.tree.getSelectionModel().removeTreeSelectionListener(this);
            this.tree.getModel().removeTreeModelListener(this);
            this.tree.removeHierarchyBoundsListener(this);
            this.tree.removeHierarchyListener(this);
            this.tree.removeComponentListener(this);
         } else {
            this.list.getSelectionModel().removeListSelectionListener(this);
            this.list.getModel().removeListDataListener(this);
            this.list.removeHierarchyBoundsListener(this);
            this.list.removeHierarchyListener(this);
            this.list.removeComponentListener(this);
         }

         this.pane.getHorizontalScrollBar().getModel().removeChangeListener(this);
         this.pane.getVerticalScrollBar().getModel().removeChangeListener(this);
         this.detached = true;
      }

      private void change() {
         if (ViewTooltips.INSTANCE != null) {
            ViewTooltips.INSTANCE.hide();
         }

         this.detach();
      }

      public void propertyChange(PropertyChangeEvent evt) {
         this.change();
      }

      public void treeNodesChanged(TreeModelEvent e) {
         this.change();
      }

      public void treeNodesInserted(TreeModelEvent e) {
         this.change();
      }

      public void treeNodesRemoved(TreeModelEvent e) {
         this.change();
      }

      public void treeStructureChanged(TreeModelEvent e) {
         this.change();
      }

      public void hierarchyChanged(HierarchyEvent e) {
         this.change();
      }

      public void valueChanged(TreeSelectionEvent e) {
         this.change();
      }

      public void ancestorMoved(HierarchyEvent e) {
         this.change();
      }

      public void ancestorResized(HierarchyEvent e) {
         this.change();
      }

      public void stateChanged(ChangeEvent e) {
         this.change();
      }

      public void valueChanged(ListSelectionEvent e) {
         this.change();
      }

      public void intervalAdded(ListDataEvent e) {
         this.change();
      }

      public void intervalRemoved(ListDataEvent e) {
         this.change();
      }

      public void contentsChanged(ListDataEvent e) {
         this.change();
      }

      public void componentResized(ComponentEvent e) {
         this.change();
      }

      public void componentMoved(ComponentEvent e) {
         this.change();
      }

      public void componentShown(ComponentEvent e) {
         this.change();
      }

      public void componentHidden(ComponentEvent e) {
         this.change();
      }
   }

   private static final class ImgComp extends JComponent {
      private BufferedImage img;
      private Dimension d = null;
      private Color bg = Color.WHITE;
      private JScrollPane comp = null;
      private Object node = null;
      private AffineTransform at = AffineTransform.getTranslateInstance(0.0, 0.0);
      boolean isRight = false;

      ImgComp() {
         super();
      }

      ImgComp(BufferedImage img, Rectangle off, boolean right) {
         super();
         this.img = img;
         this.at = AffineTransform.getTranslateInstance((double)(-off.x), 0.0);
         this.d = new Dimension(off.width, off.height);
         this.isRight = right;
      }

      public ViewTooltips.ImgComp getPartial(Rectangle bds, boolean right) {
         assert this.img != null;

         return new ViewTooltips.ImgComp(this.img, bds, right);
      }

      public boolean configure(Object nd, JScrollPane tv, JTree tree, TreePath path, int row) {
         boolean sameVn = this.setLastRendereredObject(nd);
         boolean sameComp = this.setLastRenderedScrollPane(tv);
         Component renderer = null;
         this.bg = tree.getBackground();
         boolean sel = tree.isSelectionEmpty() ? false : tree.getSelectionModel().isPathSelected(path);
         boolean exp = tree.isExpanded(path);
         boolean leaf = !exp && tree.getModel().isLeaf(nd);
         boolean lead = path.equals(tree.getSelectionModel().getLeadSelectionPath());
         renderer = tree.getCellRenderer().getTreeCellRendererComponent(tree, nd, sel, exp, leaf, row, lead);
         if (renderer != null) {
            this.setComponent(renderer);
         }

         return true;
      }

      public boolean configure(Object nd, JScrollPane tv, JList list, int row) {
         boolean sameVn = this.setLastRendereredObject(nd);
         boolean sameComp = this.setLastRenderedScrollPane(tv);
         Component renderer = null;
         this.bg = list.getBackground();
         boolean sel = list.isSelectionEmpty() ? false : list.getSelectionModel().isSelectedIndex(row);
         renderer = list.getCellRenderer().getListCellRendererComponent(list, nd, row, sel, false);
         if (renderer != null) {
            this.setComponent(renderer);
         }

         return true;
      }

      private boolean setLastRenderedScrollPane(JScrollPane comp) {
         boolean result = comp != this.comp;
         this.comp = comp;
         return result;
      }

      private boolean setLastRendereredObject(Object nd) {
         boolean result = this.node != nd;
         if (result) {
            this.node = nd;
         }

         return result;
      }

      void clear() {
         this.comp = null;
         this.node = null;
      }

      public void setComponent(Component jc) {
         Dimension d = jc.getPreferredSize();
         BufferedImage nue = new BufferedImage(d.width, d.height + 2, 3);
         SwingUtilities.paintComponent(nue.getGraphics(), jc, this, 0, 0, d.width, d.height + 2);
         this.setImage(nue);
      }

      public Rectangle getBounds() {
         Dimension dd = this.getPreferredSize();
         return new Rectangle(0, 0, dd.width, dd.height);
      }

      private void setImage(BufferedImage img) {
         this.img = img;
         this.d = null;
      }

      public Dimension getPreferredSize() {
         if (this.d == null) {
            this.d = new Dimension(this.img.getWidth(), this.img.getHeight());
         }

         return this.d;
      }

      public Dimension getSize() {
         return this.getPreferredSize();
      }

      public void paint(Graphics g) {
         g.setColor(this.bg);
         g.fillRect(0, 0, this.d.width, this.d.height);
         Graphics2D g2d = (Graphics2D)g;
         g2d.drawRenderedImage(this.img, this.at);
         g.setColor(Color.GRAY);
         g.drawLine(0, 0, this.d.width, 0);
         g.drawLine(0, this.d.height - 1, this.d.width, this.d.height - 1);
         if (this.isRight) {
            g.drawLine(0, 0, 0, this.d.height - 1);
         } else {
            g.drawLine(this.d.width - 1, 0, this.d.width - 1, this.d.height - 1);
         }
      }

      public void firePropertyChange(String s, Object a, Object b) {
      }

      public void invalidate() {
      }

      public void validate() {
      }

      public void revalidate() {
      }
   }
}
