package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class signaltest4 implements dtest {
   @Override
   public String getName() {
      return "Signalumfeld 4";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5729 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         gleisElements.RICHTUNG richtung = gl.getRichtung();
         gleis gl2 = gl.nextByRichtung(false);
         if (gl2 != null
            && (
               richtung == gleisElements.RICHTUNG.right && gl2.getElement() == gleis.ELEMENT_WEICHEOBEN
                  || richtung == gleisElements.RICHTUNG.left && gl2.getElement() == gleis.ELEMENT_WEICHEUNTEN
            )) {
            dtestresult d = new dtestresult(1, "Ein Signal überdeckt möglicherweise das Licht einer Weiche.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
