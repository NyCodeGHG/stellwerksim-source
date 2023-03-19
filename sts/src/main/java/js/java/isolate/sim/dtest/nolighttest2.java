package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class nolighttest2 implements dtest {
   public nolighttest2() {
      super();
   }

   @Override
   public String getName() {
      return "Licht-Los Signal";
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
                  if (ngl != null && gleis.ALLE_SIGNALE.matches(ngl.getElement())) {
                     dtestresult d = new dtestresult(2, "Lichtloses Element (Kreuzungsbrücke/Strecke) neben Signal.", gl);
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
