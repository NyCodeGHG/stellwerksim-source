package js.java.tools.gui.speedometer;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;

public class ClassicNeedlePainter extends NeedlePainterBase {
   final double radius;

   public ClassicNeedlePainter() {
      this.radius = 5.0;
   }

   public ClassicNeedlePainter(double size) {
      this.radius = size;
   }

   @Override
   Shape paint(SpeedometerPanel parent, double x0, double y0, double r, double phi) {
      double x1 = x0 - this.radius * Math.cos(Math.toRadians(phi));
      double y1 = y0 - this.radius * Math.sin(Math.toRadians(phi));
      double phiOffset = Math.toDegrees(Math.atan2(2.5, 2.5));
      double x2 = x0 + this.radius * Math.sqrt(2.0) * Math.cos(Math.toRadians(phi - phiOffset));
      double y2 = y0 + this.radius * Math.sqrt(2.0) * Math.sin(Math.toRadians(phi - phiOffset));
      Shape s1 = new Double(x1, y1, x2, y2);
      double x3 = x0 + (r - 8.0) * Math.cos(Math.toRadians(phi));
      double y3 = y0 + (r - 8.0) * Math.sin(Math.toRadians(phi));
      Shape s2 = new Double(x2, y2, x3, y3);
      double x4 = x0 + this.radius * Math.sqrt(2.0) * Math.cos(Math.toRadians(phi + phiOffset));
      double y4 = y0 + this.radius * Math.sqrt(2.0) * Math.sin(Math.toRadians(phi + phiOffset));
      Shape s3 = new Double(x3, y3, x4, y4);
      Shape s4 = new Double(x4, y4, x1, y1);
      GeneralPath needle = new GeneralPath(s1);
      needle.append(s2, true);
      needle.append(s3, true);
      needle.append(s4, true);
      return needle;
   }
}
