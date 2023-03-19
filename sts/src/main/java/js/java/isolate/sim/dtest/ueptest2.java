package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class ueptest2 implements dtest {
   public ueptest2() {
      super();
   }

   @Override
   public String getName() {
      return "ÜP Richtung Ausfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 3717 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_ÜBERGABEPUNKT});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         boolean success = false;
         int i = 40;
         gleis next_gl = null;
         gleis before_gl = gl;

         for(gleis pos_gl = gl.nextByRichtung(false); pos_gl != null; pos_gl = next_gl) {
            if (pos_gl.getElement() == gleis.ELEMENT_AUSFAHRT) {
               if (pos_gl.getENR() == gl.getENR()) {
                  success = true;
               }
               break;
            }

            next_gl = pos_gl.next(before_gl);
            if (next_gl == null || pos_gl.sameGleis(next_gl) || --i <= 0) {
               break;
            }

            before_gl = pos_gl;
         }

         if (!success) {
            dtestresult d = new dtestresult(2, "Richtung oder ENR von ÜP und Ausfahrt passen nicht zusammen!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
