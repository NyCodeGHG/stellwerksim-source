package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;

class paint_anruecker extends paint2Base {
   paint_anruecker(paint2Base p) {
      super(p);
   }

   paint_anruecker() {
      super(null);
   }

   protected void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      int x1 = 0;
      int y1 = 0;
      switch (gl.richtung) {
         case down:
         case up:
            x1 = xscal - 2;
            y1 = (int)((double)yscal / 2.0 - 2.0);
            break;
         case left:
         case right:
            x1 = (int)((double)xscal / 2.0 - 2.0);
            y1 = yscal - 2;
      }

      Color colr2 = gleis.colors.col_stellwerk_schwarz;
      g.setColor(gleis.colors.col_stellwerk_schwarz);
      if (!gl.fdata.power_off && gl.getFluentData().getStatus() == 2 && !gleis.blinkon) {
         colr2 = gleis.colors.col_stellwerk_reserviert;
      }

      if (gl.highlighted > 0 && !gleis.blinkon) {
         colr2 = gleis.colors.col_stellwerk_gruenein;
      }

      gl.setSmooth(g, true, 4);
      gl.paintSignalLED(g, x1, y1, true, colr2);
      gl.setSmooth(g, false, 4);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}
