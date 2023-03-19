package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class vsignaltest3 implements dtest {
   public vsignaltest3() {
      super();
   }

   @Override
   public String getName() {
      return "Vorsignal-Kombi in der Ausfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5545 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<fahrstrasse> it = glb.fahrstrassenIterator();

      while(it.hasNext()) {
         fahrstrasse fs = (fahrstrasse)it.next();
         if (!fs.isDeleted() && !fs.isRFonly() && fs.getStop().getElement().matches(gleis.ELEMENT_AUSFAHRT) && fs.getStart().getGleisExtend().vorsignalSignal) {
            dtestresult d = new dtestresult(1, "Ein Vorsignal (Kombi-Hauptsignal) befindet sich in einer Ausfahrt.", fs.getStart(), fs);
            r.add(d);
         }
      }

      return r;
   }
}
