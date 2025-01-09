package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class zielknopftest1 implements dtest {
   @Override
   public String getName() {
      return "Zielknopf hat Signal";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5726 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SIGNAL_ZIELKNOPF});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         int hasSignal = 0;
         Iterator<gleis> it2 = glb.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

         while (it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2.getElement() == gleis.ELEMENT_SIGNAL && gl2.getENR() == gl.getENR()) {
               hasSignal++;
            }
         }

         if (hasSignal == 0) {
            dtestresult d = new dtestresult(2, "Der Zielknopf muss einem Signal zugeordnet sein.", gl);
            r.add(d);
         } else if (hasSignal > 1) {
            dtestresult d = new dtestresult(2, "Der Zielknopf darf nur einem Signal zugeordnet sein.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
