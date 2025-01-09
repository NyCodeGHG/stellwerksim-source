package js.java.tools.gui.multipane;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.JPanel;
import javax.swing.JPanel.AccessibleJPanel;
import javax.swing.event.MouseInputAdapter;

public class MultiSplitPane extends JPanel {
   private AccessibleContext accessibleContext = null;
   private boolean continuousLayout = true;
   private MultiSplitPane.DividerPainter dividerPainter = new MultiSplitPane.DefaultDividerPainter();
   private boolean dragUnderway = false;
   private MultiSplitLayout.Divider dragDivider = null;
   private Rectangle initialDividerBounds = null;
   private boolean oldFloatingDividers = true;
   private int dragOffsetX = 0;
   private int dragOffsetY = 0;
   private int dragMin = -1;
   private int dragMax = -1;

   public MultiSplitPane() {
      super(new MultiSplitLayout());
      MultiSplitPane.InputHandler inputHandler = new MultiSplitPane.InputHandler();
      this.addMouseListener(inputHandler);
      this.addMouseMotionListener(inputHandler);
      this.addKeyListener(inputHandler);
      this.setFocusable(true);
   }

   public final MultiSplitLayout getMultiSplitLayout() {
      return (MultiSplitLayout)this.getLayout();
   }

   public final void setModel(MultiSplitLayout.Node model) {
      this.getMultiSplitLayout().setModel(model);
   }

   public final void setDividerSize(int dividerSize) {
      this.getMultiSplitLayout().setDividerSize(dividerSize);
   }

   public void setContinuousLayout(boolean continuousLayout) {
      this.continuousLayout = continuousLayout;
      this.firePropertyChange("continuousLayout", continuousLayout, continuousLayout);
   }

   public boolean isContinuousLayout() {
      return this.continuousLayout;
   }

   public MultiSplitLayout.Divider activeDivider() {
      return this.dragDivider;
   }

   public MultiSplitPane.DividerPainter getDividerPainter() {
      return this.dividerPainter;
   }

   public void setDividerPainter(MultiSplitPane.DividerPainter dividerPainter) {
      this.dividerPainter = dividerPainter;
   }

   protected void paintChildren(Graphics g) {
      super.paintChildren(g);
      MultiSplitPane.DividerPainter dp = this.getDividerPainter();
      Rectangle clipR = g.getClipBounds();
      if (dp != null && clipR != null) {
         Graphics dpg = g.create();

         try {
            MultiSplitLayout msl = this.getMultiSplitLayout();

            for (MultiSplitLayout.Divider divider : msl.dividersThatOverlap(clipR)) {
               dp.paint(dpg, divider);
            }
         } finally {
            dpg.dispose();
         }
      }
   }

   private void startDrag(int mx, int my) {
      this.requestFocusInWindow();
      MultiSplitLayout msl = this.getMultiSplitLayout();
      MultiSplitLayout.Divider divider = msl.dividerAt(mx, my);
      if (divider != null) {
         MultiSplitLayout.Node prevNode = divider.previousSibling();
         MultiSplitLayout.Node nextNode = divider.nextSibling();
         if (prevNode != null && nextNode != null) {
            this.initialDividerBounds = divider.getBounds();
            this.dragOffsetX = mx - this.initialDividerBounds.x;
            this.dragOffsetY = my - this.initialDividerBounds.y;
            this.dragDivider = divider;
            Rectangle prevNodeBounds = prevNode.getBounds();
            Rectangle nextNodeBounds = nextNode.getBounds();
            if (this.dragDivider.isVertical()) {
               this.dragMin = prevNodeBounds.x;
               this.dragMax = nextNodeBounds.x + nextNodeBounds.width;
               this.dragMax = this.dragMax - this.dragDivider.getBounds().width;
            } else {
               this.dragMin = prevNodeBounds.y;
               this.dragMax = nextNodeBounds.y + nextNodeBounds.height;
               this.dragMax = this.dragMax - this.dragDivider.getBounds().height;
            }

            this.oldFloatingDividers = this.getMultiSplitLayout().getFloatingDividers();
            this.getMultiSplitLayout().setFloatingDividers(false);
            this.dragUnderway = true;
         } else {
            this.dragUnderway = false;
         }
      } else {
         this.dragUnderway = false;
      }
   }

   private void repaintDragLimits() {
      Rectangle damageR = this.dragDivider.getBounds();
      if (this.dragDivider.isVertical()) {
         damageR.x = this.dragMin;
         damageR.width = this.dragMax - this.dragMin;
      } else {
         damageR.y = this.dragMin;
         damageR.height = this.dragMax - this.dragMin;
      }

      this.repaint(damageR);
   }

   private void updateDrag(int mx, int my) {
      if (this.dragUnderway) {
         Rectangle oldBounds = this.dragDivider.getBounds();
         Rectangle bounds = new Rectangle(oldBounds);
         if (this.dragDivider.isVertical()) {
            bounds.x = mx - this.dragOffsetX;
            bounds.x = Math.max(bounds.x, this.dragMin);
            bounds.x = Math.min(bounds.x, this.dragMax);
         } else {
            bounds.y = my - this.dragOffsetY;
            bounds.y = Math.max(bounds.y, this.dragMin);
            bounds.y = Math.min(bounds.y, this.dragMax);
         }

         this.dragDivider.setBounds(bounds);
         if (this.isContinuousLayout()) {
            this.revalidate();
            this.repaintDragLimits();
         } else {
            this.repaint(oldBounds.union(bounds));
         }
      }
   }

   private void clearDragState() {
      this.dragDivider = null;
      this.initialDividerBounds = null;
      this.oldFloatingDividers = true;
      this.dragOffsetX = this.dragOffsetY = 0;
      this.dragMin = this.dragMax = -1;
      this.dragUnderway = false;
   }

   private void finishDrag(int x, int y) {
      if (this.dragUnderway) {
         this.clearDragState();
         if (!this.isContinuousLayout()) {
            this.revalidate();
            this.repaint();
         }
      }
   }

   private void cancelDrag() {
      if (this.dragUnderway) {
         this.dragDivider.setBounds(this.initialDividerBounds);
         this.getMultiSplitLayout().setFloatingDividers(this.oldFloatingDividers);
         this.setCursor(Cursor.getPredefinedCursor(0));
         this.repaint();
         this.revalidate();
         this.clearDragState();
      }
   }

   private void updateCursor(int x, int y, boolean show) {
      if (!this.dragUnderway) {
         int cursorID = 0;
         if (show) {
            MultiSplitLayout.Divider divider = this.getMultiSplitLayout().dividerAt(x, y);
            if (divider != null) {
               cursorID = divider.isVertical() ? 11 : 8;
            }
         }

         this.setCursor(Cursor.getPredefinedCursor(cursorID));
      }
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new MultiSplitPane.AccessibleMultiSplitPane();
      }

      return this.accessibleContext;
   }

   protected class AccessibleMultiSplitPane extends AccessibleJPanel {
      protected AccessibleMultiSplitPane() {
         super(MultiSplitPane.this);
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SPLIT_PANE;
      }
   }

   private class DefaultDividerPainter extends MultiSplitPane.DividerPainter {
      private DefaultDividerPainter() {
      }

      @Override
      public void paint(Graphics g, MultiSplitLayout.Divider divider) {
         if (divider == MultiSplitPane.this.activeDivider() && !MultiSplitPane.this.isContinuousLayout()) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setColor(Color.black);
            g2d.fill(divider.getBounds());
         }
      }
   }

   public abstract static class DividerPainter {
      public abstract void paint(Graphics var1, MultiSplitLayout.Divider var2);
   }

   private class InputHandler extends MouseInputAdapter implements KeyListener {
      private InputHandler() {
      }

      public void mouseEntered(MouseEvent e) {
         MultiSplitPane.this.updateCursor(e.getX(), e.getY(), true);
      }

      public void mouseMoved(MouseEvent e) {
         MultiSplitPane.this.updateCursor(e.getX(), e.getY(), true);
      }

      public void mouseExited(MouseEvent e) {
         MultiSplitPane.this.updateCursor(e.getX(), e.getY(), false);
      }

      public void mousePressed(MouseEvent e) {
         MultiSplitPane.this.startDrag(e.getX(), e.getY());
      }

      public void mouseReleased(MouseEvent e) {
         MultiSplitPane.this.finishDrag(e.getX(), e.getY());
      }

      public void mouseDragged(MouseEvent e) {
         MultiSplitPane.this.updateDrag(e.getX(), e.getY());
      }

      public void keyPressed(KeyEvent e) {
         if (e.getKeyCode() == 27) {
            MultiSplitPane.this.cancelDrag();
         }
      }

      public void keyReleased(KeyEvent e) {
      }

      public void keyTyped(KeyEvent e) {
      }
   }
}
