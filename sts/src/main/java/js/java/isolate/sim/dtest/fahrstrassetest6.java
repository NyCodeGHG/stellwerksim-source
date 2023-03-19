package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassetest6 implements dtest {
   public fahrstrassetest6() {
      super();
   }

   @Override
   public String getName() {
      return "Fahrstrassen 6";
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
         LinkedList<gleis> gw = f.getGleisweg();
         boolean zdsignal = false;

         for(gleis g : gw) {
            if (g.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL && f.hasZDSignel(g)) {
               zdsignal = true;
            }

            if (!zdsignal
               || g.getElement() != gleis.ELEMENT_ÜBERGABEPUNKT
                  && g.getElement() != gleis.ELEMENT_AUSFAHRT
                  && g.getElement() != gleis.ELEMENT_EINFAHRT
                  && g.getElement() != gleis.ELEMENT_WEICHEOBEN
                  && g.getElement() != gleis.ELEMENT_WEICHEUNTEN) {
               if (zdsignal && g.getElement() == gleis.ELEMENT_ZWERGSIGNAL && f.hasZwerg(g)) {
                  dtestresult d = new dtestresult(2, "Fahrstraße " + f.getName() + " mit Zugdeckungssignal vor Schutzsignal.", g, f);
                  r.add(d);
               }
            } else {
               dtestresult d = new dtestresult(2, "Fahrstraße " + f.getName() + " mit Zugdeckungssignal vor Weiche.", g, f);
               r.add(d);
            }
         }
      }

      return r;
   }
}
