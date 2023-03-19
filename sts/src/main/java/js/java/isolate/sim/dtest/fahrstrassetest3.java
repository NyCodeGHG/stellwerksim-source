package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassetest3 implements dtest {
   public fahrstrassetest3() {
      super();
   }

   @Override
   public String getName() {
      return "Fahrstrassen 3";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();

      for(int i = 0; i < glb.countFahrwege(); ++i) {
         fahrstrasse f = glb.getFahrweg(i);
         if (f.hasÜP()) {
            LinkedList<gleis> gw = f.getGleisweg();
            boolean üpfound = false;

            for(gleis g : gw) {
               if (g.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT) {
                  üpfound = true;
               }

               if (üpfound
                  && g.getElement() != gleis.ELEMENT_ÜBERGABEPUNKT
                  && g.getElement() != gleis.ELEMENT_STRECKE
                  && g.getElement() != gleis.ELEMENT_AUSFAHRT
                  && g.getElement() != gleis.ELEMENT_EINFAHRT
                  && g.getElement() != gleis.ELEMENT_SPRUNG) {
                  dtestresult d = new dtestresult(2, "Fahrstraße " + f.getName() + " hat nach ÜP noch Nicht-Streckenelemente.", g, f);
                  r.add(d);
               }
            }
         }
      }

      return r;
   }
}
