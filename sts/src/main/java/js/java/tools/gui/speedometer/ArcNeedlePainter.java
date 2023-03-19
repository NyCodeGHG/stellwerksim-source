package js.java.tools.gui.speedometer;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public class ArcNeedlePainter extends NeedlePainterBase {
   private double x0;
   private double y0;
   private int pos = 0;

   public ArcNeedlePainter() {
      this(0);
   }

   public ArcNeedlePainter(int pos) {
      super();
      this.pos = pos;
   }

   private Rectangle2D calcXY(double r) {
      double x1 = this.x0 + r * Math.cos(Math.toRadians(0.0));
      double y2 = this.y0 + r * Math.sin(Math.toRadians(270.0));
      double x3 = this.x0 + r * Math.cos(Math.toRadians(180.0));
      double y4 = this.y0 + r * Math.sin(Math.toRadians(90.0));
      return new Double(x3, y2, x1 - x3, y4 - y2);
   }

   @Override
   boolean shouldPaintTransparent() {
      return true;
   }

   @Override
   Shape paint(SpeedometerPanel parent, double x0, double y0, double r, double phi) {
      this.x0 = x0;
      this.y0 = y0;
      if (parent.isPaintLabels()) {
         r -= 25.0;
      } else {
         r -= 5.0;
      }

      double rdiff = r / 4.0;
      if (rdiff > 40.0) {
         rdiff = 40.0;
      }

      r -= (double)this.pos * (rdiff + 5.0);
      double radius = r - rdiff;
      Shape s1;
      Shape s2;
      if (parent.getMode() == 0) {
         double startphi = 180.0;
         double aphi = 360.0 - phi;
         Rectangle2D rect1 = this.calcXY(r);
         s1 = new java.awt.geom.Arc2D.Double(rect1, aphi, startphi - aphi, 0);
         Rectangle2D rect2 = this.calcXY(radius);
         s2 = new java.awt.geom.Arc2D.Double(rect2, -startphi, -(startphi - aphi), 0);
      } else {
         double startphi = 90.0;
         double aphi = phi * -1.0;
         Rectangle2D rect1 = this.calcXY(r);
         s1 = new java.awt.geom.Arc2D.Double(rect1, aphi, startphi - aphi, 0);
         Rectangle2D rect2 = this.calcXY(radius);
         s2 = new java.awt.geom.Arc2D.Double(rect2, startphi, -(startphi - aphi), 0);
      }

      GeneralPath needle = new GeneralPath(s1);
      needle.append(s2, true);
      return needle;
   }
}
