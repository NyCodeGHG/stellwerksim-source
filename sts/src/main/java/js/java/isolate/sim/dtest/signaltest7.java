package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class signaltest7 implements dtest {
   public signaltest7() {
      super();
   }

   @Override
   public String getName() {
      return "Signalumfeld 7";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         gleis gl2 = gl.nextByRichtung(false);
         if (gl2 != null && gl2.getElement() == gleis.ELEMENT_KREUZUNGBRUECKE) {
            dtestresult d = new dtestresult(2, "Kreuzungsbrücke vor Signal ist technisch nicht möglich, führt zu Fahrstraßenfehlern.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
