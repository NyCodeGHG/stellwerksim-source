package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class signaltest1 implements dtest {
   @Override
   public String getName() {
      return "Signalumfeld 1";
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
         Iterator<gleis> it2 = gl.getNachbarn();

         while (it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2.getElement() == gleis.ELEMENT_SIGNAL) {
               dtestresult d = new dtestresult(1, "Neben einem Signal befindet sich direkt ein anderes Signal.", gl);
               r.add(d);
            }
         }
      }

      return r;
   }
}
