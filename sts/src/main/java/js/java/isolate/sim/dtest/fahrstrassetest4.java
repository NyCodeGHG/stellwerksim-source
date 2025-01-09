package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassetest4 implements dtest {
   @Override
   public String getName() {
      return "Fahrstrassen 4";
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
         if (f.hasÜP() && f.getExtend().getFSType() == 2) {
            dtestresult d = new dtestresult(0, "Fahrstraße " + f.getName() + " hat eine manuelle AutoFS mit ÜP!", f);
            r.add(d);
         }
      }

      return r;
   }
}
