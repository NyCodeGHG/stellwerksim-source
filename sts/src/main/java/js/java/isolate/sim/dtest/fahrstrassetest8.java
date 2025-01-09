package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassetest8 implements dtest {
   @Override
   public String getName() {
      return "Fahrstrassen 8";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<fahrstrasse> it = glb.fahrstrassenIterator();

      while (it.hasNext()) {
         fahrstrasse f = (fahrstrasse)it.next();
         if (f.getExtend().fstype == 8 && !f.allowsRf()) {
            dtestresult d = new dtestresult(2, "Fahrstraße " + f.getName() + " als reine Rangierfahrstraße, aber keine passenden Signale.", f);
            r.add(d);
         }
      }

      return r;
   }
}
