package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class signalkopfgleistest1 implements dtest {
   @Override
   public String getName() {
      return "Signalkopfgleis";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         if (gl.nextByRichtung(false) == null) {
            dtestresult d = new dtestresult(2, "Hinter einem Signal endet die Strecke. Kopfgleise benötigen ein zusätzliches Gleis!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
