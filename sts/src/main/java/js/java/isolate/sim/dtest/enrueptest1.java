package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class enrueptest1 implements dtest {
   public enrueptest1() {
      super();
   }

   @Override
   public String getName() {
      return "1x ENR ÜP";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5726 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_ÜBERGABEPUNKT});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         Iterator<gleis> it2 = glb.findIterator(new Object[]{gleis.ALLE_GLEISE, gl.getENR()});

         while(it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2 != gl && gl2.getElement() != gleis.ELEMENT_AUSFAHRT && gl2.getElement() != gleis.ELEMENT_AUSFAHRT_ZIELKNOPF) {
               dtestresult d = new dtestresult(2, "Die ENR " + gl.getENR() + " des ÜP " + gl.getENR() + " ist mehrfach verwendet!", gl2);
               r.add(d);
            }
         }
      }

      return r;
   }
}
