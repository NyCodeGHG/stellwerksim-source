package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class ausfahrttest1 implements dtest {
   public ausfahrttest1() {
      super();
   }

   @Override
   public String getName() {
      return "Ausfahrtrichtung";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_AUSFAHRT});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         int x = 0;
         int y = 0;
         switch(gl.getRichtung()) {
            case right:
               x = -1;
               break;
            case left:
               x = 1;
               break;
            case up:
               y = 1;
               break;
            case down:
               y = -1;
         }

         boolean foundit = false;
         Iterator<gleis> nit = gl.getNachbarn();

         while(nit.hasNext()) {
            gleis gl2 = (gleis)nit.next();
            int xx = gl2.getCol();
            int yy = gl2.getRow();
            if (x != 0 && xx == gl.getCol() + x || y != 0 && yy == gl.getRow() + y) {
               foundit = true;
            }
         }

         if (!foundit) {
            dtestresult d = new dtestresult(2, "Die Ausfahrt " + gl.getSWWert() + " hat vermutlich die falsche Richtung.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
