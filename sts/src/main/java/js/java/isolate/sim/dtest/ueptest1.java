package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class ueptest1 implements dtest {
   public ueptest1() {
      super();
   }

   @Override
   public String getName() {
      return "ÜP mit Ausfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_ÜBERGABEPUNKT});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         LinkedList<gleis> m = new LinkedList();
         Iterator<gleis> it2 = glb.findIterator(new Object[]{gleis.ELEMENT_AUSFAHRT, gl.getENR()});

         while(it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            m.add(gl2);
         }

         if (m.size() > 1) {
            dtestresult d = new dtestresult(2, "Mehrere Ausfahrten haben die selbe ENR " + gl.getENR() + " wie ein ÜP!", gl, m);
            r.add(d);
         } else if (m.size() == 0) {
            dtestresult d = new dtestresult(2, "Der ÜP " + gl.getENR() + " hat keine Ausfahrt!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
