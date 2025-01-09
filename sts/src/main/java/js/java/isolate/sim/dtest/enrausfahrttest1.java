package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class enrausfahrttest1 implements dtest {
   @Override
   public String getName() {
      return "1x ENR Ausfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5725 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_AUSFAHRT});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         Iterator<gleis> it2 = glb.findIterator(new Object[]{gleis.ALLE_GLEISE, gl.getENR()});

         while (it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (!gl2.sameGleis(gl) && gl2.getElement() != gleis.ELEMENT_ÜBERGABEPUNKT && gl2.getElement() != gleis.ELEMENT_AUSFAHRT_ZIELKNOPF) {
               dtestresult d = new dtestresult(2, "Die ENR " + gl.getENR() + " der Ausfahrt " + gl.getSWWert() + " ist mehrfach verwendet!", gl2);
               r.add(d);
            }
         }
      }

      return r;
   }
}
