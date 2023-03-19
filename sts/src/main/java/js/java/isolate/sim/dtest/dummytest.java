package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class dummytest implements dtest {
   public dummytest() {
      super();
   }

   @Override
   public String getName() {
      return "Dummy";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();

      for(int i = 0; i < 3; ++i) {
         dtestresult d = new dtestresult(i, "Fehler " + i);
         r.add(d);
      }

      return r;
   }
}
