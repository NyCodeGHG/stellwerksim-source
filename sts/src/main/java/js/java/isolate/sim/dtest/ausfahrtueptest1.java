package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class ausfahrtueptest1 implements dtest {
   public ausfahrtueptest1() {
      super();
   }

   @Override
   public String getName() {
      return "1x ÜP Ausfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_AUSFAHRT});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         int cc = 0;
         Iterator<gleis> it2 = glb.findIterator(new Object[]{gl.getENR(), gleis.ELEMENT_ÜBERGABEPUNKT});

         while(it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2 != gl) {
               if (++cc > 1) {
                  dtestresult d = new dtestresult(2, "Die Ausfahrt " + gl.getSWWert() + " hat mehrere ÜPs!", gl2);
                  r.add(d);
               }
            }
         }
      }

      return r;
   }
}
