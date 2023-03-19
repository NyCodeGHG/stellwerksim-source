package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_vorsignal extends paint2Base {
   paint_vorsignal(paint2Base p) {
      super(p);
   }

   paint_vorsignal() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim) {
      gleisElements.Stellungen refSt = gleisElements.ST_SIGNAL_AUS;
      if (gl.fdata.connectedSignal != null) {
         if (gl.fdata.connectedSignal.getElement() == gleisElements.ELEMENT_SIGNAL) {
            refSt = gl.fdata.connectedSignal.fdata.stellung;
         } else {
            refSt = gleisElements.ST_SIGNAL_ROT;
         }
      }

      Color col_rot = refSt == gleisElements.ST_SIGNAL_ROT ? gleis.colors.col_stellwerk_gelbein : gleis.colors.col_stellwerk_gelbaus;
      Color col_gruen = refSt == gleisElements.ST_SIGNAL_GRÜN ? gleis.colors.col_stellwerk_gruenein : gleis.colors.col_stellwerk_gruenaus;
      Color col_signal = gleis.colors.col_stellwerk_schwarz;
      switch(gl.richtung) {
         case right:
            gl.paintVorsignal(g, 0, yscal * 3 / 4, fscal, col_signal, col_rot, col_gruen, Math.PI * 3.0 / 2.0);
            break;
         case left:
            gl.paintVorsignal(g, xscal, yscal * 1 / 4, fscal, col_signal, col_rot, col_gruen, Math.PI / 2);
            break;
         case down:
            gl.paintVorsignal(g, xscal * 1 / 4, 0, fscal, col_signal, col_rot, col_gruen, 0.0);
            break;
         case up:
            gl.paintVorsignal(g, xscal * 3 / 4, yscal, fscal, col_signal, col_rot, col_gruen, Math.PI);
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
