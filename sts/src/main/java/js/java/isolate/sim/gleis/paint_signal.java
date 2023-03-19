package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class paint_signal extends paint2Base {
   private gleis paintgleis;

   paint_signal(paint2Base p) {
      super(p);
   }

   paint_signal() {
      super(null);
   }

   private boolean invers(gleis gl, gleis gleis2) {
      boolean invers = false;

      try {
         if (gleis2.telement == gleis.ELEMENT_SIGNALKNOPF) {
            this.paintgleis = gleis2;
            gleis2 = this.paintgleis.next(gl);
         }

         invers = gleis2.telement == gleis.ELEMENT_ZWERGSIGNAL
            || gleis2.telement == gleis.ELEMENT_BAHNÜBERGANG
            || gleis2.telement == gleis.ELEMENT_AUTOBAHNÜBERGANG
            || gleis2.telement == gleis.ELEMENT_ANRUFÜBERGANG
            || gleis2.telement == gleis.ELEMENT_SIGNAL
            || gleis2.telement == gleis.ELEMENT_WEICHEOBEN
            || gleis2.telement == gleis.ELEMENT_WEICHEUNTEN;
         if (!invers) {
            gleis2 = gleis2.next(gl);
            if (gleis2.telement == gleis.ELEMENT_SIGNAL || gleis2.telement == gleis.ELEMENT_ZWERGSIGNAL) {
               invers = true;
            }
         }
      } catch (NullPointerException var5) {
      }

      return invers;
   }

   private void paint(gleis gl, Graphics2D g, int xscal, int yscal, int fscal, boolean sim, boolean kopfgleis, boolean hauptZwerg) {
      int x0 = (int)((double)xscal / 2.0);
      int y0 = (int)((double)yscal / 2.0);
      Color col_rot = gl.fdata.stellung != gleisElements.ST_SIGNAL_ROT && (gl.fdata.stellung != gleisElements.ST_SIGNAL_RF || gl.signalRfStart)
         ? gleis.colors.col_stellwerk_rotaus
         : gleis.colors.col_stellwerk_rotein;
      Color col_gruen = gl.fdata.stellung == gleisElements.ST_SIGNAL_GRÜN ? gleis.colors.col_stellwerk_gruenein : gleis.colors.col_stellwerk_gruenaus;
      Color col_zs1 = gl.fdata.stellung == gleisElements.ST_SIGNAL_ZS1
         ? gleis.colors.col_stellwerk_zs1
         : (gl.fdata.stellung == gleisElements.ST_SIGNAL_RF && !gl.signalRfStart ? gleis.colors.col_stellwerk_defekt : gleis.colors.col_stellwerk_frei);
      Color col_rf = gl.fdata.stellung == gleisElements.ST_SIGNAL_RF ? gleis.colors.col_stellwerk_zs1 : gleis.colors.col_stellwerk_frei;
      Color col_signal = gleis.colors.col_stellwerk_schwarz;
      gleisElements.Stellungen refSt = gleisElements.ST_SIGNAL_AUS;
      if (gl.fdata.connectedSignal != null && gl.fdata.stellung == gleisElements.ST_SIGNAL_GRÜN) {
         if (gl.fdata.connectedSignal.getElement() == gleisElements.ELEMENT_SIGNAL) {
            refSt = gl.fdata.connectedSignal.fdata.stellung;
         } else if (gl.fdata.connectedSignal.getElement() == gleisElements.ELEMENT_ZDECKUNGSSIGNAL) {
            refSt = gleisElements.ST_SIGNAL_ROT;
         }
      }

      Color col_vrot = refSt == gleisElements.ST_SIGNAL_ROT ? gleis.colors.col_stellwerk_gelbein : gleis.colors.col_stellwerk_gelbaus;
      Color col_vgruen = refSt == gleisElements.ST_SIGNAL_GRÜN ? gleis.colors.col_stellwerk_gruenein : gleis.colors.col_stellwerk_gruenaus;
      if (!sim && gl.getGleisExtend().isEntscheider()) {
         col_signal = gleis.colors.col_pfeil;
      }

      if (gl.highlighted > 0) {
         if (gleis.blinkon) {
            col_gruen = gleis.colors.col_stellwerk_gelbein;
            col_zs1 = gleis.colors.col_stellwerk_gelbein;
         } else {
            col_rot = gleis.colors.col_stellwerk_gelbein;
         }
      } else if (gl.fdata.display_blink && gleis.blinkon_slow) {
         col_rot = gleis.colors.col_stellwerk_rotaus;
         col_gruen = gleis.colors.col_stellwerk_gruenaus;
      }

      this.paintgleis = gl;
      gleis gleis2 = gl.nextByRichtung(false);
      boolean invers = this.invers(gl, gleis2);
      if (invers) {
         gleis2 = gl.nextByRichtung(true);
         invers = !this.invers(gl, gleis2);
         if (!invers) {
            this.paintgleis = gl;
         }
      }

      invers = !invers;
      Graphics2D pg = this.shiftGraphicsTo(g, gl, this.paintgleis, xscal, yscal);
      switch(gl.richtung) {
         case right:
            if (sim) {
               gl.elementlabel(pg, gl.getShortElementName(), xscal, yscal, invers ? 180 : 0);
            }

            gl.paintSignal(
               g, 0, yscal * 3 / 4, fscal, col_signal, col_rot, col_gruen, col_zs1, col_rf, col_vrot, col_vgruen, Math.PI * 3.0 / 2.0, kopfgleis, hauptZwerg
            );
            break;
         case left:
            if (sim) {
               gl.elementlabel(pg, gl.getShortElementName(), xscal, yscal, invers ? 0 : 180);
            }

            gl.paintSignal(
               g, xscal, yscal * 1 / 4, fscal, col_signal, col_rot, col_gruen, col_zs1, col_rf, col_vrot, col_vgruen, Math.PI / 2, kopfgleis, hauptZwerg
            );
            break;
         case down:
            if (sim) {
               gl.elementlabel(pg, gl.getShortElementName(), xscal, yscal, invers ? 270 : 90);
            }

            gl.paintSignal(g, xscal * 1 / 4, 0, fscal, col_signal, col_rot, col_gruen, col_zs1, col_rf, col_vrot, col_vgruen, 0.0, kopfgleis, hauptZwerg);
            break;
         case up:
            if (sim) {
               gl.elementlabel(pg, gl.getShortElementName(), xscal, yscal, invers ? 90 : 270);
            }

            gl.paintSignal(
               g, xscal * 3 / 4, yscal, fscal, col_signal, col_rot, col_gruen, col_zs1, col_rf, col_vrot, col_vgruen, Math.PI, kopfgleis, hauptZwerg
            );
      }

      pg.dispose();
      if (kopfgleis && gl.signalRfOnlyStop && sim) {
         gl.paintSmallKnob(g, x0, y0 + 2, xscal);
      } else {
         gl.paintBigKnob(g, x0, y0, xscal);
      }
   }

   @Override
   public void paint3Sim(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      boolean kopfgleis;
      boolean hauptZwerg;
      if (gl.kopfsignaldetect == null) {
         kopfgleis = gl.isKopfSignal();
         gl.kopfsignaldetect = kopfgleis;
         hauptZwerg = gl.isHauptZwergSignal();
         gl.hauptZwergSignaldetect = hauptZwerg;
      } else {
         kopfgleis = gl.kopfsignaldetect;
         hauptZwerg = gl.hauptZwergSignaldetect;
      }

      this.paint(gl, g, xscal, yscal, fscal, true, kopfgleis, hauptZwerg);
      super.paint3Sim(gl, g, xscal, yscal, fscal);
   }

   @Override
   public void paint3Editor(gleis gl, Graphics2D g, int xscal, int yscal, int fscal) {
      boolean kopfgleis = gl.isKopfSignal();
      boolean hauptZwerg = gl.isHauptZwergSignal();
      this.paint(gl, g, xscal, yscal, fscal, false, kopfgleis, hauptZwerg);
      super.paint3Editor(gl, g, xscal, yscal, fscal);
   }
}
