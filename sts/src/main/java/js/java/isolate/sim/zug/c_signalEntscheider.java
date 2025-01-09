package js.java.isolate.sim.zug;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.sim.TEXTTYPE;

@Deprecated
public class c_signalEntscheider extends baseChain2Chain {
   public c_signalEntscheider() {
      super(new c_freiefahrt(), new c_notstop());
   }

   @Override
   boolean run(zug z) {
      if (z.aus_stw == null
         && gleis.ELEMENT_SIGNAL.matches(z.next_gl.getElement())
         && z.next_gl.forUs(z.pos_gl)
         && z.next_gl.getGleisExtend().isEntscheider()
         && z.next_gl.getFluentData().getStellung().getZugStellung() == gleisElements.ZugStellungen.fahrt) {
         boolean wrongDirection = false;
         fahrstrasse fs = z.next_gl.getFluentData().getStartingFS();
         if (fs != null) {
            if (z.lastStopDone()) {
               gleis s_next_gl = null;
               gleis s_pos_gl = fs.getStop();
               gleis s_before_gl = s_pos_gl.nextByRichtung(true);
               boolean shiftNext = true;

               while (s_pos_gl != null) {
                  if (shiftNext) {
                     s_next_gl = s_pos_gl.next(s_before_gl);
                  }

                  shiftNext = true;
                  if (s_next_gl == null || s_next_gl.sameGleis(s_pos_gl)) {
                     break;
                  }

                  if (gleis.ELEMENT_AUSFAHRT.matches(s_next_gl.getElement())) {
                     if (s_next_gl.getENR() != z.aus_enr) {
                        gleis ausf = z.glbModel.findFirst(new Object[]{z.aus_enr, gleis.ELEMENT_AUSFAHRT});
                        if (ausf != null) {
                           wrongDirection = !ausf.getSWWert_special().equalsIgnoreCase(s_next_gl.getSWWert_special());
                        } else {
                           wrongDirection = true;
                        }
                     }
                     break;
                  }

                  if (gleis.ALLE_WEICHEN.matches(s_next_gl.getElement())) {
                     if (s_next_gl.weicheSpitz(s_pos_gl)) {
                        break;
                     }

                     s_before_gl = s_pos_gl;
                     s_pos_gl = s_next_gl;
                     s_next_gl = s_next_gl.weicheSpitzgleis();
                     shiftNext = false;
                  }

                  if (shiftNext) {
                     s_before_gl = s_pos_gl;
                     s_pos_gl = s_next_gl;
                  }
               }
            } else {
               String _gleis = z.zielgleis;
               if (z.befehl_zielgleis != null) {
                  _gleis = z.befehl_zielgleis;
               }

               LinkedList<gleis> weg = fs.getGleisweg();
               gleis wbefore_gl = z.pos_gl;

               for (gleis wgl : weg) {
                  if (gleis.ALLE_BAHNSTEIGE.matches(wgl.getElement()) && wgl.forUs(wbefore_gl) && z.my_main.getBahnsteige().isNeighborBahnsteigOf(_gleis, wgl)) {
                     wrongDirection = true;
                     break;
                  }

                  wbefore_gl = wgl;
               }
            }
         }

         if (wrongDirection) {
            if (!z.sichtstopp) {
               String text = "Anruf von Triebfahrzeugführer "
                  + z.getSpezialName()
                  + ": \"Achtung, Zug gestoppt! Richtungsanzeiger deutet auf falsche Fahrtrichtung!\"<p>Geben Sie Zugbefehl 'weiterfahren' um dies zu ignorieren.";
               z.my_main.showText(text, TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
               z.weiterfahren = false;
            }

            z.sichtstopp = true;
            if (!z.weiterfahren) {
               return this.callFalse(z);
            }
         }
      }

      return this.callTrue(z);
   }
}
