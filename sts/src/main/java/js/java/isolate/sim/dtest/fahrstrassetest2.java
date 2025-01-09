package js.java.isolate.sim.dtest;

import java.util.HashMap;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassetest2 implements dtest {
   @Override
   public String getName() {
      return "Fahrstrassen 2";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();

      for (int i = 0; i < glb.countFahrwege(); i++) {
         fahrstrasse f = glb.getFahrweg(i);
         HashMap<gleis, gleisElements.Stellungen> w = f.getWeichen();
         LinkedList<gleis> gw = f.getGleisweg();

         for (gleis g : gw) {
            if ((g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) && !w.containsKey(g)) {
               dtestresult d = new dtestresult(2, "Fahrstraße " + f.getName() + " hat mindestens eine unbekannte Weiche nach dem Laden.", g, f);
               r.add(d);
               break;
            }
         }

         for (gleis gx : w.keySet()) {
            if (!gw.contains(gx)) {
               dtestresult d = new dtestresult(2, "Fahrstraße " + f.getName() + " fehlt mindestens eine Weiche nach dem Laden.", f);
               r.add(d);
               break;
            }
         }
      }

      return r;
   }
}
