package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class randtest implements dtest {
   public randtest() {
      super();
   }

   @Override
   public String getName() {
      return "Randtest";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      boolean ftop = false;
      boolean fleft = false;
      boolean fright = false;
      boolean fbottom = false;
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_GLEISE});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         if (gl.getRow() == 0) {
            ftop = true;
         }

         if (gl.getCol() == 0) {
            fleft = true;
         }

         if (gl.getCol() == glb.getGleisWidth() - 1) {
            fright = true;
         }

         if (gl.getRow() == glb.getGleisHeight() - 1) {
            fbottom = true;
         }
      }

      if (ftop) {
         dtestresult d = new dtestresult(0, "Mindestens ein Gleiselement befindet sich in der obersten Zeile.");
         r.add(d);
      }

      if (fbottom) {
         dtestresult d = new dtestresult(0, "Mindestens ein Gleiselement befindet sich in der untersten Zeile.");
         r.add(d);
      }

      if (fleft) {
         dtestresult d = new dtestresult(0, "Mindestens ein Gleiselement befindet sich in der ersten linken Spalte.");
         r.add(d);
      }

      if (fright) {
         dtestresult d = new dtestresult(0, "Mindestens ein Gleiselement befindet sich in der letzten rechte Spalte.");
         r.add(d);
      }

      return r;
   }
}
