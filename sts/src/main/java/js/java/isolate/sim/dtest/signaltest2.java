package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class signaltest2 implements dtest {
   public signaltest2() {
      super();
   }

   @Override
   public String getName() {
      return "Signalumfeld 2";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      if (glb.gleisbildextend.getSignalversion() == 2) {
         Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

         while(it1.hasNext()) {
            gleis gl = (gleis)it1.next();
            gleisElements.RICHTUNG richtung = gl.getRichtung();
            gleis gl2 = gl.nextByRichtung(false);
            if (gl2 != null) {
               gleis gl3 = gl2.nextByRichtung(false);
               if (gl3 != null
                  && (
                     richtung == gleisElements.RICHTUNG.right && gl.getRow() == gl3.getRow() - 1
                        || richtung == gleisElements.RICHTUNG.left && gl.getRow() == gl3.getRow() + 1
                        || richtung == gleisElements.RICHTUNG.up && gl.getCol() == gl3.getCol() - 1
                        || richtung == gleisElements.RICHTUNG.down && gl.getCol() == gl3.getCol() + 1
                  )) {
                  dtestresult d = new dtestresult(0, "Ein Signal überdeckt einen Gleisbereich.", gl);
                  r.add(d);
               }
            }
         }
      }

      return r;
   }
}
