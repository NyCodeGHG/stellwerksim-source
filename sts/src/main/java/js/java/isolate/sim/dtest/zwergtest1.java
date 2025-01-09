package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class zwergtest1 implements dtest {
   @Override
   public String getName() {
      return "Schutzsignal neben Signal";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_ZWERGSIGNAL});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         gleis gl2 = gl.nextByRichtung(false);
         if (gl2 != null && gl2.getElement() == gleis.ELEMENT_SIGNAL) {
            gleis gl3 = gl2.nextByRichtung(true);
            if (gl3 == gl) {
               dtestresult d = new dtestresult(
                  2,
                  "Ein Schutzsignal befindet sich direkt neben einem Signal, in dem Fall muss der Schutzsignalknopf verbaut werden oder mehr Abstand sein.",
                  gl
               );
               r.add(d);
            }
         }
      }

      return r;
   }
}
