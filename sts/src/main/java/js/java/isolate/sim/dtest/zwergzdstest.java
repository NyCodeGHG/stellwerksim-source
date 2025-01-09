package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class zwergzdstest implements dtest {
   @Override
   public String getName() {
      return "Schutzsignal wenn ZD-Signal";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      gleis gl = glb.findFirst(new Object[]{gleis.ELEMENT_ZDECKUNGSSIGNAL});
      if (gl != null && glb.findFirst(new Object[]{gleis.ELEMENT_ZWERGSIGNAL}) == null) {
         dtestresult d = new dtestresult(2, "Wenn Zugdeckungssignale verbaut werden, müssen auch Schutzsignale verbaut werden.", gl);
         r.add(d);
      }

      return r;
   }
}
