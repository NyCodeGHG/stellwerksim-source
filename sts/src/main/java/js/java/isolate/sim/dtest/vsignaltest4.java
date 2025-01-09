package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class vsignaltest4 implements dtest {
   @Override
   public String getName() {
      return "Trenner ohne Vorsignal";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5532 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<fahrstrasse> it = glb.fahrstrassenIterator();

      while (it.hasNext()) {
         fahrstrasse f = (fahrstrasse)it.next();
         if (!f.isDeleted() && !f.isRFonly()) {
            boolean foundVSig = f.getStart().getGleisExtend().isVorsignal();
            gleis before_gl = f.getStart();

            for (gleis w : f.getGleisweg()) {
               if (w.getElement().matches(gleis.ELEMENT_VORSIGNAL) && w.forUs(before_gl)) {
                  foundVSig = true;
               }

               if (w.getElement().matches(gleis.ELEMENT_VORSIGNALTRENNER) && w.forUs(before_gl) && !foundVSig) {
                  dtestresult d = new dtestresult(1, "Fahrstraße " + f.getName() + " enthält Vorsignaltrenner aber gar kein Vorsignal davor.", f);
                  r.add(d);
                  break;
               }
            }
         }
      }

      return r;
   }
}
