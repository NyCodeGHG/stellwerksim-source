package js.java.isolate.fahrplaneditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JToggleButton;

class markerBox extends JToggleButton {
   private static final int DIM = 10;
   private Dimension myd = new Dimension(10, 10);
   private GradientPaint gp;
   private GradientPaint rollovergp;

   markerBox() {
      this.setMargin(new Insets(0, 0, 0, 0));
      this.gp = new GradientPaint(0.0F, 0.0F, new Color(153, 153, 153), 0.0F, 10.0F, new Color(221, 221, 221), true);
      this.rollovergp = new GradientPaint(0.0F, 0.0F, new Color(153, 153, 187), 0.0F, 10.0F, new Color(187, 187, 255), true);
      this.setBorder(null);
      this.setOpaque(false);
   }

   public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      int w = this.getWidth();
      int h = this.getHeight();
      int x = 0;
      int y = 0;
      w = Math.min(10, w);
      h = Math.min(10, h);
      x = (this.getWidth() - w) / 2;
      y = (this.getHeight() - h) / 2;
      if (this.getModel().isRollover()) {
         g2.setPaint(this.rollovergp);
      } else {
         g2.setPaint(this.gp);
      }

      g2.fillRect(x, y, w, h);
      g2.draw3DRect(x, y, w - 1, h - 1, true);
      if (this.isSelected()) {
         g2.setStroke(new BasicStroke(3.0F));
         g2.setColor(Color.BLACK);
         g2.drawLine(x + 2, y + 2, x + w - 2, y + h - 2);
         g2.drawLine(x + 2, y + h - 2, x + w - 2, y + 2);
         g2.setStroke(new BasicStroke(1.0F));
      }
   }

   public Dimension getMaximumSize() {
      return this.myd;
   }

   public Dimension getMinimumSize() {
      return this.myd;
   }

   public Dimension getPreferredSize() {
      return this.myd;
   }
}
