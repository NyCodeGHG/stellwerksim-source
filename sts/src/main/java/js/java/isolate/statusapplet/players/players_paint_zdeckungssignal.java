package js.java.isolate.statusapplet.players;

import java.awt.Color;
import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.paint2Base;

class players_paint_zdeckungssignal extends paint2Base {
   players_paint_zdeckungssignal(paint2Base p) {
      super(p);
   }

   players_paint_zdeckungssignal() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim) {
      Color col_rot = gleis.colors.col_stellwerk_rotaus;
      Color col_kenn = gleis.colors.col_stellwerk_frei;
      if (gl.getFluentData().getStellung() == gleis.ST_ZDSIGNAL_FESTGELEGT) {
         col_rot = gleis.colors.col_stellwerk_rotein;
      }

      switch(gl.getRichtung()) {
         case right:
            gl.paintDeckungsSignal(g, 0, yscal * 3 / 4, fscal, col_rot, col_kenn, Math.PI * 3.0 / 2.0);
            break;
         case left:
            gl.paintDeckungsSignal(g, xscal, yscal * 1 / 4, fscal, col_rot, col_kenn, Math.PI / 2);
            break;
         case down:
            gl.paintDeckungsSignal(g, xscal * 1 / 4, 0, fscal, col_rot, col_kenn, 0.0);
            break;
         case up:
            gl.paintDeckungsSignal(g, xscal * 3 / 4, yscal, fscal, col_rot, col_kenn, Math.PI);
      }
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
