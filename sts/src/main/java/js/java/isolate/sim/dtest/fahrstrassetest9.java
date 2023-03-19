package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassetest9 implements dtest {
   public fahrstrassetest9() {
      super();
   }

   @Override
   public String getName() {
      return "Fahrstrassen 9";
   }

   @Override
   public String getVersion() {
      return "$Revision: 3523 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<fahrstrasse> it = glb.fahrstrassenIterator();

      while(it.hasNext()) {
         fahrstrasse f = (fahrstrasse)it.next();
         if (f.getExtend().fstype == 8
            && (f.getStart().getElement().matches(gleis.ELEMENT_ZWERGSIGNAL) || f.getStop().getElement().matches(gleis.ELEMENT_ZWERGSIGNAL))) {
            dtestresult d = new dtestresult(2, "Fahrstraße " + f.getName() + " als reine Rangierfahrstraße, aber andere gar nicht möglich.", f);
            r.add(d);
         }
      }

      return r;
   }
}
