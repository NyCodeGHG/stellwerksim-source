package js.java.isolate.sim.dtest;

import java.awt.Rectangle;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.gleisbildWorker.areaFinder;

public class bvtest2 extends bvtest1 {
   public bvtest2() {
      super();
   }

   @Override
   public String getName() {
      return "Bildverarbeitung 2";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5039 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      areaFinder af = new areaFinder(glb, glb.getAdapter());
      LinkedList<Rectangle> a = af.getAreas();
      int sc = this.cntSprünge(glb);
      int rc = a.size();
      LinkedList<gleis> m = new LinkedList();
      af.markLines(a, m);
      if (sc / 3 > rc) {
         dtestresult d = new dtestresult(
            1, "Es wurden nur " + rc + " unabhängige Areale gefunden bei " + sc + " Sprüngen. Darunter könnte die Übersichtlichkeit leiden.", m
         );
         r.add(d);
      } else {
         dtestresult d = new dtestresult(0, "Es wurden " + rc + " unabhängige Areale gefunden.", m);
         r.add(d);
      }

      return r;
   }
}
