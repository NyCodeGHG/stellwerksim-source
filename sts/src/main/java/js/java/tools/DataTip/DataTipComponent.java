package js.java.tools.DataTip;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.CellRendererPane;
import javax.swing.JToolTip;

class DataTipComponent extends JToolTip {
   private DataTipCell cell;
   private CellRendererPane rendererPane;
   private Rectangle withoutBorderRectangle;
   private Color backgroundColor;
   private boolean isHeavyWeight;

   DataTipComponent(DataTipCell cell, Rectangle withoutBorderRectangle, Color backgroundColor) {
      super();
      this.cell = cell;
      this.withoutBorderRectangle = withoutBorderRectangle;
      this.backgroundColor = backgroundColor;
      this.rendererPane = new CellRendererPane();
      this.add(this.rendererPane);
      this.setFocusable(false);
      this.setBorder(null);
      this.enableEvents(131120L);
   }

   public void updateUI() {
   }

   public boolean contains(int x, int y) {
      return this.isHeavyWeight;
   }

   protected void processMouseEvent(MouseEvent e) {
      DataTipManager.get().handleEventFromDataTipComponent(e);
   }

   protected void processMouseMotionEvent(MouseEvent e) {
      DataTipManager.get().handleEventFromDataTipComponent(e);
   }

   protected void processMouseWheelEvent(MouseWheelEvent e) {
      DataTipManager.get().handleEventFromDataTipComponent(e);
   }

   public void paintComponent(Graphics g) {
      Component component = this.cell.getRendererComponent();
      g.setColor(this.backgroundColor);
      int width = this.getWidth();
      int height = this.getHeight();
      g.fillRect(0, 0, width, height);
      g.setColor(Color.black);
      g.drawRect(0, 0, width - 1, height - 1);
      if (this.withoutBorderRectangle != null) {
         Shape oldClip = g.getClip();
         g.setClip(this.withoutBorderRectangle);
         g.setColor(this.backgroundColor);
         g.fillRect(0, 0, width, height);
         g.setClip(oldClip);
      }

      g.setClip(1, 1, width - 2, height - 2);
      this.rendererPane.paintComponent(g, component, this, 0, 0, width, height);
      g.setClip(this.withoutBorderRectangle);
      this.rendererPane.paintComponent(g, component, this, 0, 0, width, height);
   }

   public void setHeavyWeight(boolean isHeavyWeight) {
      this.isHeavyWeight = isHeavyWeight;
   }
}
