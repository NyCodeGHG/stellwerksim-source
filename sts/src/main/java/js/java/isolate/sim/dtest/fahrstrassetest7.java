package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassetest7 implements dtest {
   @Override
   public String getName() {
      return "Fahrstrassen 7";
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
         boolean marked = false;
         if (!f.getExtend().isDeleted() && f.getStart().getElement() == gleis.ELEMENT_SIGNAL) {
            marked = this.check(glb, f);
            if (marked) {
               dtestresult d = new dtestresult(1, "Fahrstraße " + f.getName() + " ist auffällig, da über mehrere andere Fahrstraßen selbe Verbindung.", f);
               r.add(d);
            }
         }
      }

      return r;
   }

   private boolean check(gleisbildModelSts glb, fahrstrasse f) {
      Iterator<fahrstrasse> startit = glb.fahrstrassenIterator();
      Iterator<fahrstrasse> stopit = glb.fahrstrassenIterator();
      int cc = 0;

      LinkedList<fahrstrasse> startings;
      LinkedList<fahrstrasse> stopings;
      do {
         startings = this.getOtherStartings(f, startit);
         stopings = this.getOtherStopings(f, stopit);
         if (this.sameStartStop(startings, stopings)) {
            return true;
         }

         startit = startings.iterator();
         stopit = stopings.iterator();
         cc++;
      } while (!startings.isEmpty() && !stopings.isEmpty() && cc < 3);

      return false;
   }

   private LinkedList<fahrstrasse> getOtherStartings(fahrstrasse of, Iterator<fahrstrasse> it) {
      LinkedList<fahrstrasse> ret = new LinkedList();

      while (it.hasNext()) {
         fahrstrasse f = (fahrstrasse)it.next();
         if (of != f && !f.getExtend().isDeleted() && f.getStart().getElement() == gleis.ELEMENT_SIGNAL && f.getStart() == of.getStart()) {
            ret.add(f);
         }
      }

      return ret;
   }

   private LinkedList<fahrstrasse> getOtherStopings(fahrstrasse of, Iterator<fahrstrasse> it) {
      LinkedList<fahrstrasse> ret = new LinkedList();

      while (it.hasNext()) {
         fahrstrasse f = (fahrstrasse)it.next();
         if (of != f && !f.getExtend().isDeleted() && f.getStart().getElement() == gleis.ELEMENT_SIGNAL && f.getStop() == of.getStop()) {
            ret.add(f);
         }
      }

      return ret;
   }

   private boolean sameStartStop(LinkedList<fahrstrasse> startings, LinkedList<fahrstrasse> stopings) {
      for (fahrstrasse fs : startings) {
         for (fahrstrasse ft : stopings) {
            if (fs.getStop() == ft.getStart()) {
               return true;
            }
         }
      }

      return false;
   }
}
