package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class fahrstrassetest1 implements dtest {
   @Override
   public String getName() {
      return "Fahrstrassen 1";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      if (glb.countFahrwege() == 0) {
         dtestresult d = new dtestresult(1, "Es wurden noch keine Fahrstraßen definiert!");
         r.add(d);
      }

      return r;
   }
}
