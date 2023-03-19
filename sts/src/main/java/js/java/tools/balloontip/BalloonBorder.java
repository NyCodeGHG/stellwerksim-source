package js.java.tools.balloontip;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.border.Border;

public class BalloonBorder implements Border {
   private Dimension lastComponentSize;
   private Insets insets = new Insets(0, 0, 0, 0);
   private Color fillColor;
   private int offset;

   public BalloonBorder(Color fillColor, int offset) {
      super();
      this.fillColor = fillColor;
      this.offset = offset;
   }

   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      width -= this.insets.left + this.insets.right;
      height -= this.insets.top + this.insets.bottom;
      Point iPoint = new Point();
      Point ePoint = new Point();
      iPoint.x = x;
      iPoint.y = y;
      ePoint.x = x + width + 1;
      ePoint.y = y;
      this.printPoints(iPoint, ePoint);
      g.drawLine(iPoint.x, iPoint.y, ePoint.x, ePoint.y);
      iPoint.setLocation(ePoint);
      ePoint.x = x + width + 1;
      ePoint.y = y + height + 1;
      this.printPoints(iPoint, ePoint);
      g.drawLine(iPoint.x, iPoint.y, ePoint.x, ePoint.y);
      iPoint.setLocation(ePoint);
      ePoint.x = x + this.offset * 2;
      ePoint.y = y + height + 1;
      this.printPoints(iPoint, ePoint);
      g.drawLine(iPoint.x, iPoint.y, ePoint.x, ePoint.y);
      iPoint.setLocation(ePoint);
      ePoint.x = x + this.offset;
      ePoint.y = y + height + this.offset + 1;
      this.printPoints(iPoint, ePoint);
      g.drawLine(iPoint.x, iPoint.y, ePoint.x, ePoint.y);
      iPoint.setLocation(ePoint);
      ePoint.x = x + this.offset;
      ePoint.y = y + height + 1;
      this.printPoints(iPoint, ePoint);
      g.drawLine(iPoint.x, iPoint.y, ePoint.x, ePoint.y);
      iPoint.setLocation(ePoint);
      ePoint.x = x;
      ePoint.y = y + height + 1;
      this.printPoints(iPoint, ePoint);
      g.drawLine(iPoint.x, iPoint.y, ePoint.x, ePoint.y);
      iPoint.setLocation(ePoint);
      ePoint.x = x;
      ePoint.y = y;
      this.printPoints(iPoint, ePoint);
      g.drawLine(iPoint.x, iPoint.y, ePoint.x, ePoint.y);
      int[] xPoints = new int[]{x + this.offset + 1, x + this.offset * 2, x + this.offset + 1};
      int[] yPoints = new int[]{y + height + 1, y + height + 1, y + height + this.offset};
      this.printTrianglePoints(xPoints, yPoints);
      g.setColor(this.fillColor);
      g.fillPolygon(xPoints, yPoints, 3);
   }

   private void printPoints(Point iPoint, Point ePoint) {
   }

   private void printTrianglePoints(int[] xPoints, int[] yPoints) {
   }

   public Insets getBorderInsets(Component c) {
      Dimension currentComponent = c.getSize();
      if (currentComponent.equals(this.lastComponentSize)) {
         return this.insets;
      } else {
         this.insets = new Insets(1, 1, this.offset + 1, 1);
         this.lastComponentSize = currentComponent;
         return this.insets;
      }
   }

   public boolean isBorderOpaque() {
      return true;
   }
}
