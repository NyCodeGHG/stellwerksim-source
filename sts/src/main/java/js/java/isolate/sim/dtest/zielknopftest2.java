package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class zielknopftest2 implements dtest {
   @Override
   public String getName() {
      return "Zielknopf aus FS zum Ziel";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5728 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SIGNAL_ZIELKNOPF, gleis.ELEMENT_AUSFAHRT_ZIELKNOPF});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         boolean anyFound = false;
         Iterator<fahrstrasse> fit = glb.fahrstrassenIterator();

         while (fit.hasNext()) {
            fahrstrasse fs = (fahrstrasse)fit.next();
            if (fs.getStopEnr() == gl.getENR()) {
               boolean found = false;

               for (gleis g : fs) {
                  if (found && gleis.ALLE_WEICHEN.matches(g.getElement())) {
                     dtestresult d = new dtestresult(2, "Weiche nach Zielknopf " + gl.getENR() + ".", gl, g);
                     r.add(d);
                  } else if (g.equals(gl)) {
                     found = true;
                     anyFound = true;
                  }
               }
            }
         }

         if (!anyFound) {
            dtestresult d = new dtestresult(2, "Der Zielknopf muss auf Fahrstraße(n) zum Ziel liegen.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
