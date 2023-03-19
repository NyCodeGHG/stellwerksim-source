package js.java.isolate.sim.dtest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class zwergbstgtest implements dtest {
   public zwergbstgtest() {
      super();
   }

   @Override
   public String getName() {
      return "Zugdeckungssignal mit Bahnsteig";
   }

   @Override
   public String getVersion() {
      return "$Revision: 3717 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      element[] var10000 = new element[]{gleis.ELEMENT_ZWERGSIGNAL};
      element[] bstgelm = new element[]{gleis.ELEMENT_BAHNSTEIG};
      Iterator<fahrstrasse> it = glb.fahrstrassenIterator();

      while(it.hasNext()) {
         fahrstrasse fs = (fahrstrasse)it.next();
         HashSet<gleis> zwerge = fs.getZDSignale();
         if (!zwerge.isEmpty() && !fs.hasElements(bstgelm)) {
            dtestresult d = new dtestresult(2, "In einer Fahrstraße gibt es Zugdeckunssignale aber keine Bahnsteige.", fs.getStart(), fs);
            r.add(d);
         }
      }

      return r;
   }
}
