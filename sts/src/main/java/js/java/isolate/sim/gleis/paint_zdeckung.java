package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_zdeckung extends paint2Base {
   paint_zdeckung(paint2Base p) {
      super(p);
   }

   paint_zdeckung() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      Color col_rot = gl.fdata.stellung != gleisElements.ST_ZDSIGNAL_ROT && gl.fdata.stellung != gleisElements.ST_ZDSIGNAL_FESTGELEGT
         ? gleis.colors.col_stellwerk_rotaus
         : gleis.colors.col_stellwerk_rotein;
      Color col_kenn = gl.fdata.stellung == gleisElements.ST_ZDSIGNAL_GRÜN ? gleis.colors.col_stellwerk_zs1 : gleis.colors.col_stellwerk_frei;
      switch (gl.richtung) {
         case right:
            if (sim) {
            }

            gl.paintDeckungsSignal(g, 0, yscal * 3 / 4, fscal, col_rot, col_kenn, Math.PI * 3.0 / 2.0);
            break;
         case left:
            if (sim) {
            }

            gl.paintDeckungsSignal(g, xscal, yscal * 1 / 4, fscal, col_rot, col_kenn, Math.PI / 2);
            break;
         case down:
            if (sim) {
            }

            gl.paintDeckungsSignal(g, xscal * 1 / 4, 0, fscal, col_rot, col_kenn, 0.0);
            break;
         case up:
            if (sim) {
            }

            gl.paintDeckungsSignal(g, xscal * 3 / 4, yscal, fscal, col_rot, col_kenn, Math.PI);
      }

      gl.paintSmallKnob(g, x0, y0 + 2, xscal);
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal, true);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      this.paint(gl, g, xscal, yscal, fscal, false);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}
