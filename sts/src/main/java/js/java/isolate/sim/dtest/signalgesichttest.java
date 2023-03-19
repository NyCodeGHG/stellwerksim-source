package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class signalgesichttest implements dtest {
   public signalgesichttest() {
      super();
   }

   @Override
   public String getName() {
      return "Signal Gesicht-Gesicht";
   }

   @Override
   public String getVersion() {
      return "$Revision: 3717 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ALLE_SIGNALE});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         gleis gl2 = gl.nextByRichtung(true);
         if (gl2 != null && gleis.ALLE_SIGNALE.matches(gl2.getElement())) {
            dtestresult d = new dtestresult(2, "2 Signale Gesicht zu Gesicht ist technisch nicht möglich, führt zu Fahrstraßenfehlern.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
