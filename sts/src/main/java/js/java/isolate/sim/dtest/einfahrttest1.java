package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class einfahrttest1 implements dtest {
   public einfahrttest1() {
      super();
   }

   @Override
   public String getName() {
      return "Einfahrtrichtung";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_EINFAHRT});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         int x = 0;
         int y = 0;
         switch(gl.getRichtung()) {
            case right:
               x = 1;
               break;
            case left:
               x = -1;
               break;
            case up:
               y = -1;
               break;
            case down:
               y = 1;
         }

         boolean foundit = false;
         Iterator<gleis> it2 = gl.getNachbarn();

         while(it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2.getElement() != gleis.ELEMENT_AUSFAHRT) {
               int xx = gl2.getCol();
               int yy = gl2.getRow();
               if (x != 0 && xx == gl.getCol() + x || y != 0 && yy == gl.getRow() + y) {
                  foundit = true;
               }
            }
         }

         if (!foundit) {
            dtestresult d = new dtestresult(2, "Die Einfahrt " + gl.getSWWert() + " hat vermutlich die falsche Richtung.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
