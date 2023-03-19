package js.java.tools.gui.speedometer;

import java.awt.Shape;

public abstract class NeedlePainterBase {
   public NeedlePainterBase() {
      super();
   }

   abstract Shape paint(SpeedometerPanel var1, double var2, double var4, double var6, double var8);

   boolean shouldPaintTransparent() {
      return false;
   }
}
