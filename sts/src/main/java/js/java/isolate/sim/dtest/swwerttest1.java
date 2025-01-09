package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class swwerttest1 implements dtest {
   @Override
   public String getName() {
      return "SW-Wert vorhanden";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();

      for (gleis gl : glb) {
         if (gl.typRequiresSWwert()) {
            if (gl.getSWWert() == null || gl.getSWWert().trim().isEmpty()) {
               dtestresult d = new dtestresult(2, "Gleiselement fehlt der SW-Wert!", gl);
               r.add(d);
            }
         } else if (gl.typShouldHaveSWwert() && (gl.getSWWert() == null || gl.getSWWert().trim().isEmpty())) {
            dtestresult d = new dtestresult(1, "Gleiselement fehlt der SW-Wert!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
