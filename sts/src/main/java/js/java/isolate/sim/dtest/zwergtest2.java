package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class zwergtest2 implements dtest {
   @Override
   public String getName() {
      return "Schutzsignalknopf neben Signal";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SIGNALKNOPF});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         int signalOk = 0;
         Iterator<gleis> it2 = gl.getNachbarn();

         while (it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2.getElement() == gleis.ELEMENT_SIGNAL && gl2.getENR() == gl.getENR()) {
               signalOk++;
            }
         }

         if (signalOk == 0) {
            dtestresult d = new dtestresult(2, "Ein Schutzsignalknopf ist keinem Signal zugeordnet oder zuweit davon entfernt.", gl);
            r.add(d);
         } else if (signalOk > 1) {
            dtestresult d = new dtestresult(2, "Ein Schutzsignalknopf ist mehreren Signalen zugeordnet.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
