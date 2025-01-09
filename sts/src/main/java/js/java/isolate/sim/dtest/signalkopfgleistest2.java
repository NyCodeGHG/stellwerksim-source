package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class signalkopfgleistest2 implements dtest {
   @Override
   public String getName() {
      return "Zwergsignalkopfgleis";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_ZWERGSIGNAL});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         gleis bgl = gl;
         gleis gl2 = gl.nextByRichtung(false);
         if (gl2 == null) {
            dtestresult d = new dtestresult(2, "Hinter einem Schutztsignal endet die Strecke. Kopfgleise benötigen ein zusätzliches Gleis und Hauptsignal!", gl);
            r.add(d);
         } else {
            for (int i = 0; i < 2 && gl2 != null; i++) {
               gleis ngl2 = gl2.next(bgl);
               if (ngl2 == null) {
                  dtestresult d = new dtestresult(
                     2, "Hinter einem Schutztsignal endet die Strecke. Kopfgleise benötigen ein zusätzliches Gleis und Hauptsignal!", gl
                  );
                  r.add(d);
                  break;
               }

               bgl = gl2;
               gl2 = ngl2;
            }
         }
      }

      return r;
   }
}
