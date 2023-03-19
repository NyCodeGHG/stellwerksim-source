package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class zwergtest4 implements dtest {
   public zwergtest4() {
      super();
   }

   @Override
   public String getName() {
      return "Schutzsignal in der Einfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_ZWERGSIGNAL});

      while(it1.hasNext()) {
         gleis gls = (gleis)it1.next();
         boolean ok = true;
         gleis before_gl = gls;
         gleis next_gl = null;
         gleis gl = gls.nextByRichtung(true);
         if (gl != null) {
            do {
               next_gl = gl.next(before_gl);
               if (gl.sameGleis(next_gl)) {
                  break;
               }

               if (next_gl != null) {
                  before_gl = gl;
                  gl = next_gl;
                  if (gleis.ALLE_STARTSIGNALE.matches(next_gl.getElement()) && !next_gl.forUs(before_gl) || gleis.ALLE_WEICHEN.matches(next_gl.getElement())) {
                     break;
                  }

                  if (next_gl.getElement() == gleis.ELEMENT_EINFAHRT) {
                     ok = false;
                     break;
                  }
               }
            } while(next_gl != null);
         }

         if (!ok) {
            dtestresult d = new dtestresult(2, "Ein Schutzsignal befindet sich in einer Einfahrt.", gls);
            r.add(d);
         }
      }

      return r;
   }
}
