package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.mass.massLenNextGen;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class massbigtest implements dtest {
   public massbigtest() {
      super();
   }

   @Override
   public String getName() {
      return "Maßstab unendlich zu oft";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      if (glb.getMasstabCalculatorName() == 2) {
         int h = glb.getGleisHeight();
         int w = glb.getGleisWidth();
         massLenNextGen mass = (massLenNextGen)glb.getMasstabCalculator();

         for(int y = 0; y < h; ++y) {
            int mcc = 0;

            for(int x = 0; x < w; ++x) {
               gleis gl = glb.getXY_null(x, y);
               if (gl != null) {
                  if (mass.isLimitedMass(gl.getMasstab())) {
                     if (++mcc > 5) {
                        dtestresult d = new dtestresult(2, "Es wurden zu viele Maßstab 1:unendlich nebeneinander verbaut!");
                        r.add(d);
                        break;
                     }
                  } else if (mcc > 0) {
                     --mcc;
                  }
               }
            }
         }
      }

      return r;
   }
}
