package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class einfahrttest2 implements dtest {
   @Override
   public String getName() {
      return "Einfahrtanschluss";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_EINFAHRT});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         int cc = 0;
         Iterator<gleis> it2 = gl.getNachbarn();

         while (it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2.getElement() != gleis.ELEMENT_AUSFAHRT) {
               cc++;
            }
         }

         if (cc > 1) {
            dtestresult d = new dtestresult(2, "Die Einfahrt " + gl.getSWWert() + " ist mit mehr als einem Gleis verbunden!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
