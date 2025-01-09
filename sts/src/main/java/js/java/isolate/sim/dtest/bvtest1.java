package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.gleisbildWorker.emptyLineFinder;

public class bvtest1 implements dtest {
   @Override
   public String getName() {
      return "Bildverarbeitung 1";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5039 $";
   }

   protected int cntSprünge(gleisbildModelSts glb) {
      int sc = 0;

      for (Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_SPRUNG}); it.hasNext(); sc++) {
         gleis gl = (gleis)it.next();
      }

      return sc / 2;
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      emptyLineFinder lf = new emptyLineFinder(glb, glb.getAdapter());
      LinkedList<Integer> emptyRows = new LinkedList();
      LinkedList<Integer> emptyCols = new LinkedList();
      lf.findEmptyColRows(emptyRows, emptyCols, false);
      int sc = this.cntSprünge(glb);
      int rc = emptyRows.size();
      LinkedList<gleis> m = new LinkedList();

      for (int y : emptyRows) {
         gleis gl = glb.getXY_null(0, y);
         if (gl != null) {
            lf.markHLine(gl, glb.getGleisWidth(), m);
         }
      }

      if (sc / 3 > rc) {
         dtestresult d = new dtestresult(
            1, "Es wurden nur " + rc + " leere Zeilen gefunden bei " + sc + " Sprüngen. Darunter könnte die Übersichtlichkeit leiden.", m
         );
         r.add(d);
      } else {
         dtestresult d = new dtestresult(0, "Es wurden " + rc + " leere Zeilen gefunden bei " + sc + " Sprüngen.", m);
         r.add(d);
      }

      return r;
   }
}
