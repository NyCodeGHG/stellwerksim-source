package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassetest10 implements dtest {
   private static final element[] ELEMENTS_W = new element[]{gleis.ALLE_WEICHEN};

   public fahrstrassetest10() {
      super();
   }

   @Override
   public String getName() {
      return "Fahrstrassen 10";
   }

   @Override
   public String getVersion() {
      return "$Revision: 3523 $";
   }

   private gleis testAusfahrt(fahrstrasse fs) {
      gleis s_next_gl = null;
      gleis s_pos_gl = fs.getStop();
      gleis s_before_gl = s_pos_gl.nextByRichtung(true);
      boolean shiftNext = true;

      while(s_pos_gl != null) {
         if (shiftNext) {
            s_next_gl = s_pos_gl.next(s_before_gl);
         }

         shiftNext = true;
         if (s_next_gl == null || s_next_gl.sameGleis(s_pos_gl)) {
            break;
         }

         if (gleis.ELEMENT_AUSFAHRT.matches(s_next_gl.getElement())) {
            return null;
         }

         if (gleis.ALLE_WEICHEN.matches(s_next_gl.getElement())) {
            if (s_next_gl.weicheSpitz(s_pos_gl)) {
               return s_next_gl;
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

      return fs.getStart();
   }

   private boolean testBahnsteig(fahrstrasse fs) {
      LinkedList<gleis> weg = fs.getGleisweg();
      gleis wbefore_gl = null;

      for(gleis wgl : weg) {
         try {
            if (gleis.ALLE_BAHNSTEIGE.matches(wgl.getElement()) && wgl.forUs(wbefore_gl)) {
               return true;
            }
         } catch (NullPointerException var7) {
         }

         wbefore_gl = wgl;
      }

      return false;
   }

   private boolean testSpitzeWeichen(fahrstrasse fs) {
      LinkedList<gleis> weg = fs.getGleisweg();
      gleis wbefore_gl = null;

      for(gleis wgl : weg) {
         try {
            if (gleis.ALLE_WEICHEN.matches(wgl.getElement()) && wgl.weicheSpitz(wbefore_gl)) {
               return true;
            }
         } catch (NullPointerException var7) {
         }

         wbefore_gl = wgl;
      }

      return false;
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<fahrstrasse> it = glb.fahrstrassenIterator();

      while(it.hasNext()) {
         fahrstrasse f = (fahrstrasse)it.next();
         if (!f.isDeleted() && !f.isRFonly() && f.getStart().getElement().matches(gleis.ELEMENT_SIGNAL) && f.getStart().getGleisExtend().isEntscheider()) {
            if (f.hasElements(ELEMENTS_W)) {
               if (this.testSpitzeWeichen(f)) {
                  if (!gleis.ELEMENT_AUSFAHRT.matches(f.getStop().getElement())) {
                     boolean b = this.testBahnsteig(f);
                     if (!b) {
                        gleis fgleis = this.testAusfahrt(f);
                        if (fgleis != null) {
                           dtestresult d = new dtestresult(
                              2,
                              "Fahrstraße "
                                 + f.getName()
                                 + " startet mit Richtungsanzeiger aber es ist kein Bahnsteig enthalten und die Strecke führt nicht direkt auf eine Ausfahrt weiter.",
                              f,
                              fgleis
                           );
                           r.add(d);
                        }
                     }
                  }
               } else {
                  dtestresult d = new dtestresult(
                     2, "Fahrstraße " + f.getName() + " startet mit Richtungsanzeiger hat aber keine abzweigende Weiche.", f.getStart(), f
                  );
                  r.add(d);
               }
            } else {
               dtestresult d = new dtestresult(2, "Fahrstraße " + f.getName() + " startet mit Richtungsanzeiger hat aber keine Weichen.", f.getStart(), f);
               r.add(d);
            }
         }
      }

      return r;
   }
}
