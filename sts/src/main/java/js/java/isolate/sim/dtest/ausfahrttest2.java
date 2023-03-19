package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class ausfahrttest2 implements dtest {
   public ausfahrttest2() {
      super();
   }

   @Override
   public String getName() {
      return "Ausfahrtanschluss";
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
         int cc = 0;

         for(Iterator<gleis> nit = gl.getNachbarn(); nit.hasNext(); ++cc) {
            nit.next();
         }

         if (cc > 1) {
            dtestresult d = new dtestresult(2, "Die Ausfahrt " + gl.getSWWert() + " ist mit mehr als einem Gleis verbunden!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
