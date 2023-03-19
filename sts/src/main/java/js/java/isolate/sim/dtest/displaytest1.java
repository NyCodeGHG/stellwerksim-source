package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisHelper;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class displaytest1 implements dtest {
   public displaytest1() {
      super();
   }

   @Override
   public String getName() {
      return "Displaybreite";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_DISPLAYS});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         int l = gleisHelper.calcDisplaySize(gl.getElement());

         for(int x = 1; x < l; ++x) {
            gleis gl2 = glb.getXY_null(gl.getCol() + x, gl.getRow());
            if (gl2 != null) {
               if (gleis.ALLE_GLEISE.matches(gl2.getElement())) {
                  dtestresult d = new dtestresult(1, "Ein Display überdeckt Gleise.", gl);
                  r.add(d);
                  break;
               }

               if (!gleis.ELEMENT_LEER.matches(gl2.getElement())) {
                  dtestresult d = new dtestresult(1, "Ein Display überdeckt andere Objekte.", gl);
                  r.add(d);
                  break;
               }
            }
         }
      }

      return r;
   }
}
