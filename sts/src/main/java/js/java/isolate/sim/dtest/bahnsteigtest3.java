package js.java.isolate.sim.dtest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class bahnsteigtest3 implements dtest {
   @Override
   public String getName() {
      return "Bahnsteigflächenanschluss";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_BAHNSTEIGE});
      HashSet<String> bstg = new HashSet();

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         bstg.add(gl.getSWWert());
      }

      for (String bst : bstg) {
         Set<gleis> gls = glb.findAllConnectedBahnsteig(bst, false);
         HashSet<String> seen = new HashSet();

         for (gleis g : gls) {
            seen.add(g.getSWWert());
         }

         if (seen.size() > 1) {
            dtestresult d = new dtestresult(
               1, "Der Bahnsteig/Haltepunkt " + bst + " ist über Bahnsteigfläche mit mehreren Bahnsteigen verbunden.", (gleis)glb.findBahnsteig(bst).getFirst()
            );
            r.add(d);
         }
      }

      return r;
   }
}
