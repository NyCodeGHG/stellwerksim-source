package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class signaltest6 implements dtest {
   @Override
   public String getName() {
      return "Signalumfeld 6";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
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
               richtung == gleisElements.RICHTUNG.right && gl.getRow() == gl2.getRow() - 1
                  || richtung == gleisElements.RICHTUNG.left && gl.getRow() == gl2.getRow() + 1
                  || richtung == gleisElements.RICHTUNG.up && gl.getCol() == gl2.getCol() - 1
                  || richtung == gleisElements.RICHTUNG.down && gl.getCol() == gl2.getCol() + 1
            )) {
            dtestresult d = new dtestresult(0, "Ein Signal überdeckt möglicherweise einen Gleisbereich.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
