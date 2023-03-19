package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class buetest1 implements dtest {
   public buetest1() {
      super();
   }

   @Override
   public String getName() {
      return "Bahnübergang-ENR";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_BAHNÜBERGÄNGE});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         int x = gl.getCol();
         int y = gl.getRow() + 2;
         gleis gl2 = glb.getXY_null(x, y);
         if (gl2 != null && gl2.getElement() == gl.getElement() && gl2.getENR() != gl.getENR()) {
            dtestresult d = new dtestresult(1, "Ein neben einem Bahnübergang liegender anderer Bahnübergang hat eine andere ENR.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
