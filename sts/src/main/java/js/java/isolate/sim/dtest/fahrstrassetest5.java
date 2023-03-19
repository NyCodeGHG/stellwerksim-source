package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassetest5 implements dtest {
   public fahrstrassetest5() {
      super();
   }

   @Override
   public String getName() {
      return "Fahrstrassen 5";
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
         element[] elms = new element[]{gleis.ELEMENT_BAHNSTEIG};
         if (!f.getExtend().isDeleted() && f.hasElements(elms)) {
            element[] elms2 = new element[]{gleis.ELEMENT_DISPLAYKONTAKT};
            if (f.hasElements(elms2)) {
               for(gleis g : f.getElements(elms2)) {
                  if (glb.getDisplayBar().isFSkontakt(g)) {
                     dtestresult d = new dtestresult(
                        1, "Fahrstraße " + f.getName() + " hat Bahnsteig und Displaykontakt! Diese Kombination kann zu Fehlern führen.", g, f
                     );
                     r.add(d);
                  }
               }
            }
         }
      }

      return r;
   }
}
