package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class uepdisplaytest implements dtest {
   @Override
   public String getName() {
      return "ÜP aber kein AID/StiTz-Display";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_ÜBERGABEPUNKT});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         gleis gl2 = glb.findFirst(new Object[]{gleis.ELEMENT_AIDDISPLAY});
         if (gl2 == null) {
            dtestresult d = new dtestresult(0, "Es wurden ÜPs verbaut aber keine AID/StiTz-Displays.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
