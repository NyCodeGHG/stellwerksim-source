package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class nolighttest1 implements dtest {
   public nolighttest1() {
      super();
   }

   @Override
   public String getName() {
      return "Licht-Los nebeneinander";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_NOLIGHT});

      label43:
      while(it.hasNext()) {
         gleis gl = (gleis)it.next();

         for(int y = -1; y < 2; ++y) {
            for(int x = -1; x < 2; ++x) {
               if (y != 0 || x != 0) {
                  gleis ngl = glb.getXY_null(gl.getCol() + x, gl.getRow() + y);
                  if (ngl != null && gleis.ALLE_NOLIGHT.matches(ngl.getElement())) {
                     dtestresult d = new dtestresult(2, "2 Lichtlose Elemente (Kreuzungsbrücke/Strecke) nebeneinander.", gl);
                     r.add(d);
                     continue label43;
                  }
               }
            }
         }
      }

      return r;
   }
}
