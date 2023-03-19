package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_zwerg extends paint2Base {
   paint_zwerg(paint2Base p) {
      super(p);
   }

   paint_zwerg() {
      super(null);
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      boolean invers = true;
      Color col_rot = gl.fdata.stellung == gleisElements.ST_SIGNAL_ROT ? gleis.colors.col_stellwerk_rotein : gleis.colors.col_stellwerk_rotaus;
      Color col_gruen = gl.fdata.stellung != gleisElements.ST_SIGNAL_GRÜN
            && gl.fdata.stellung != gleisElements.ST_SIGNAL_RF
            && gl.fdata.stellung != gleisElements.ST_SIGNAL_ZS1
         ? gleis.colors.col_stellwerk_frei
         : gleis.colors.col_stellwerk_zs1;
      gleis gleis2 = gl.nextByRichtung(false);
      boolean noLabel = gleis2 != null
         && (
            gleis2.getElement() == gleisElements.ELEMENT_SIGNAL
               || gleis2.getElement() == gleisElements.ELEMENT_ZWERGSIGNAL
               || gleis2.getElement() == gleisElements.ELEMENT_WEICHEOBEN
               || gleis2.getElement() == gleisElements.ELEMENT_WEICHEUNTEN
               || gleis2.getElement() == gleisElements.ELEMENT_ANRUFÜBERGANG
               || gleis2.getElement() == gleisElements.ELEMENT_AUTOBAHNÜBERGANG
               || gleis2.getElement() == gleisElements.ELEMENT_BAHNÜBERGANG
         );
      switch(gl.richtung) {
         case right:
            if (sim && !noLabel) {
               gl.elementlabel(g, gl.getShortElementName(), xscal, yscal, invers ? 180 : 0);
            }

            gl.paintZwergSignal(g, 0, 0 + yscal * 3 / 4, fscal, col_rot, col_gruen, Math.PI * 3.0 / 2.0);
            break;
         case left:
            if (sim && !noLabel) {
               gl.elementlabel(g, gl.getShortElementName(), xscal, yscal, invers ? 0 : 180);
            }

            gl.paintZwergSignal(g, xscal, yscal * 1 / 4, fscal, col_rot, col_gruen, Math.PI / 2);
            break;
         case down:
            if (sim && !noLabel) {
               gl.elementlabel(g, gl.getShortElementName(), xscal, yscal, invers ? 270 : 90);
            }

            gl.paintZwergSignal(g, xscal * 1 / 4, 0, fscal, col_rot, col_gruen, 0.0);
            break;
         case up:
            if (sim && !noLabel) {
               gl.elementlabel(g, gl.getShortElementName(), xscal, yscal, invers ? 90 : 270);
            }

            gl.paintZwergSignal(g, xscal * 3 / 4, yscal, fscal, col_rot, col_gruen, Math.PI);
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
