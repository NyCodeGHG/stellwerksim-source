package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class signalkeinefstest implements dtest {
   public signalkeinefstest() {
      super();
   }

   @Override
   public String getName() {
      return "keine FS";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ALLE_STARTSIGNALE});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         boolean found = false;
         Iterator<fahrstrasse> it2 = glb.fahrstrassenIterator();

         while(it2.hasNext()) {
            fahrstrasse fs = (fahrstrasse)it2.next();
            if (!fs.getExtend().isDeleted() && fs.getStart() == gl) {
               found = true;
               break;
            }
         }

         if (!found) {
            dtestresult d = new dtestresult(1, "An Signal beginnt keine Fahrstraße (bei Kopfgleis Meldung unkritisch).", gl);
            r.add(d);
         }
      }

      return r;
   }
}
