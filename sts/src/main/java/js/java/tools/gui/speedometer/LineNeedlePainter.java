package js.java.tools.gui.speedometer;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D.Double;

public class LineNeedlePainter extends NeedlePainterBase {
   @Override
   Shape paint(SpeedometerPanel parent, double x0, double y0, double r, double phi) {
      double radius = 0.5;
      double x1 = x0 + (r - 8.0) * Math.cos(Math.toRadians(phi));
      double y1 = y0 + (r - 8.0) * Math.sin(Math.toRadians(phi));
      double phiOffset = Math.toDegrees(Math.atan2(2.5, 2.5));
      double x2 = x0 + radius * Math.sqrt(2.0) * Math.cos(Math.toRadians(phi - phiOffset));
      double y2 = y0 + radius * Math.sqrt(2.0) * Math.sin(Math.toRadians(phi - phiOffset));
      double x4 = x0 + radius * Math.sqrt(2.0) * Math.cos(Math.toRadians(phi + phiOffset));
      double y4 = y0 + radius * Math.sqrt(2.0) * Math.sin(Math.toRadians(phi + phiOffset));
      Shape s0 = new Double(x0, y0, x2, y2);
      Shape s1 = new Double(x2, y2, x1, y1);
      Shape s2 = new Double(x1, y1, x4, y4);
      Shape s3 = new Double(x4, y4, x0, y0);
      GeneralPath needle = new GeneralPath(s0);
      needle.append(s1, true);
      needle.append(s2, true);
      needle.append(s3, true);
      return needle;
   }
}
