package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class vsignaltest1 implements dtest {
   public vsignaltest1() {
      super();
   }

   @Override
   public String getName() {
      return "Vorsignal in der Einfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5710 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_EINFAHRT});

      while(it1.hasNext()) {
         gleis gls = (gleis)it1.next();
         boolean ok = true;
         gleis before_gl = gls;
         gleis next_gl = null;
         gleis gl = gls.nextByRichtung(false);
         if (gl != null) {
            do {
               next_gl = gl.next(before_gl);
               if (gl.sameGleis(next_gl)) {
                  break;
               }

               if (next_gl != null) {
                  before_gl = gl;
                  gl = next_gl;
                  if (gleis.ALLE_STARTSIGNALE.matches(next_gl.getElement()) && next_gl.forUs(before_gl)) {
                     break;
                  }

                  if (next_gl.getElement() == gleis.ELEMENT_VORSIGNAL && next_gl.forUs(before_gl)) {
                     ok = false;
                     break;
                  }
               }
            } while(next_gl != null);
         }

         if (!ok) {
            dtestresult d = new dtestresult(0, "Ein Vorsignal befindet sich in einer Einfahrt, nicht die beste Idee.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
