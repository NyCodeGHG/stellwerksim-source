package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class sizetest implements dtest {
   public sizetest() {
      super();
   }

   @Override
   public String getName() {
      return "Grössentest";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      if (glb.gl_overmaxsize() > 0) {
         dtestresult d = new dtestresult(1, "Das Gleisbild ist um " + glb.gl_overmaxsize() + " Felder zu gross.");
         r.add(d);
      }

      return r;
   }
}
