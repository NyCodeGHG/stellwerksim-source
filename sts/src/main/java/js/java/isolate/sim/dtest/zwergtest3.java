package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class zwergtest3 implements dtest {
   public zwergtest3() {
      super();
   }

   @Override
   public String getName() {
      return "Schutzsignalknopf vor Signal";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SIGNALKNOPF});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         boolean hasSignal = false;
         Iterator<gleis> it2 = gl.getNachbarn();

         while(it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2.getElement() == gleis.ELEMENT_SIGNAL && gl2.getENR() == gl.getENR()) {
               hasSignal = true;
               if (gl2.nextByRichtung(true) != gl) {
                  dtestresult d = new dtestresult(2, "Der Schutzsignalknopf muss vor dem zugeordneten Signal sein.", gl);
                  r.add(d);
               }
            }
         }

         if (!hasSignal) {
            dtestresult d = new dtestresult(2, "Der Schutzsignalknopf muss bei einem Signal sein.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
