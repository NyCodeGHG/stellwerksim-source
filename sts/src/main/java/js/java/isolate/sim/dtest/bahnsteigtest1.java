package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class bahnsteigtest1 implements dtest {
   @Override
   public String getName() {
      return "Bahnsteig Sig Abstand";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ALLE_BAHNSTEIGE});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         int cc = 0;
         Iterator<gleis> it2 = gl.getNachbarn();

         while (it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gleis.ALLE_SIGNALE.matches(gl2.getElement()) && gl2.getRichtung().equals(gl.getRichtung())) {
               cc++;
            }
         }

         if (cc > 0) {
            dtestresult d = new dtestresult(2, "Ein Signal ist direkt mit Bahnsteig/Haltepunkt " + gl.getSWWert() + " verbunden!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
