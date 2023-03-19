package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class ausfahrttest3 implements dtest {
   public ausfahrttest3() {
      super();
   }

   @Override
   public String getName() {
      return "Ausfahrt SW-Wert";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_AUSFAHRT});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         if (gl.getSWWert().isEmpty()) {
            dtestresult d = new dtestresult(2, "Die Ausfahrt ENR " + gl.getENR() + " hat keinen Namen (SW-Wert)!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
