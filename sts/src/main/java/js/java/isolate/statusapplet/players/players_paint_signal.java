package js.java.isolate.statusapplet.players;

import java.awt.Color;
import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.paint2Base;

class players_paint_signal extends paint2Base {
   players_paint_signal(paint2Base p) {
      super(p);
   }

   players_paint_signal() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim) {
      Color col_rot = gleis.colors.col_stellwerk_rotaus;
      Color col_gruen = gleis.colors.col_stellwerk_gruenaus;
      Color col_zs1 = gleis.colors.col_stellwerk_frei;
      Color col_rf = gleis.colors.col_stellwerk_frei;
      if (gl.getFluentData().getStellung() == gleis.ST_SIGNAL_GRÜN) {
         col_gruen = gleis.colors.col_stellwerk_gruenein;
      } else if (gl.getFluentData().getStellung() == gleis.ST_SIGNAL_ZS1) {
         col_zs1 = gleis.colors.col_stellwerk_zs1;
      } else if (gl.getFluentData().getStellung() == gleis.ST_SIGNAL_RF) {
         col_rf = gleis.colors.col_stellwerk_zs1;
      }

      boolean kopfgleis = gl.isKopfSignal();
      switch (gl.getRichtung()) {
         case right:
            gl.paintSignal(
               g,
               0,
               yscal * 3 / 4,
               fscal,
               gleis.colors.col_stellwerk_schwarz,
               col_rot,
               col_gruen,
               col_zs1,
               col_rf,
               gleis.colors.col_stellwerk_gelbaus,
               gleis.colors.col_stellwerk_gruenaus,
               Math.PI * 3.0 / 2.0,
               kopfgleis,
               false
            );
            break;
         case left:
            gl.paintSignal(
               g,
               xscal,
               yscal * 1 / 4,
               fscal,
               gleis.colors.col_stellwerk_schwarz,
               col_rot,
               col_gruen,
               col_zs1,
               col_rf,
               gleis.colors.col_stellwerk_gelbaus,
               gleis.colors.col_stellwerk_gruenaus,
               Math.PI / 2,
               kopfgleis,
               false
            );
            break;
         case down:
            gl.paintSignal(
               g,
               xscal * 1 / 4,
               0,
               fscal,
               gleis.colors.col_stellwerk_schwarz,
               col_rot,
               col_gruen,
               col_zs1,
               col_rf,
               gleis.colors.col_stellwerk_gelbaus,
               gleis.colors.col_stellwerk_gruenaus,
               0.0,
               kopfgleis,
               false
            );
            break;
         case up:
            gl.paintSignal(
               g,
               xscal * 3 / 4,
               yscal,
               fscal,
               gleis.colors.col_stellwerk_schwarz,
               col_rot,
               col_gruen,
               col_zs1,
               col_rf,
               gleis.colors.col_stellwerk_gelbaus,
               gleis.colors.col_stellwerk_gruenaus,
               Math.PI,
               kopfgleis,
               false
            );
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
