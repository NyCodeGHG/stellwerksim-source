package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class zwergtest5 implements dtest {
   @Override
   public String getName() {
      return "Schutzsignal in der Ausfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      element[] elm = new element[]{gleis.ELEMENT_ZWERGSIGNAL, gleis.ELEMENT_ZDECKUNGSSIGNAL};
      element[] einfelm = new element[]{gleis.ELEMENT_EINFAHRT};
      Iterator<fahrstrasse> it = glb.fahrstrassenIterator();

      while (it.hasNext()) {
         fahrstrasse fs = (fahrstrasse)it.next();
         if (fs.getStop().getElement() == gleis.ELEMENT_AUSFAHRT && !fs.getExtend().isDeleted() && fs.hasElements(elm)) {
            boolean hasein = fs.hasElements(einfelm);
            boolean done = false;
            Iterator<gleis> git = fs.getGleisweg().descendingIterator();

            while (git.hasNext() && !done) {
               gleis gl = (gleis)git.next();
               if (gleis.ALLE_WEICHEN.matches(gl.getElement()) || gl.getElement() == gleis.ELEMENT_SIGNAL) {
                  done = true;
               } else if (gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL) {
                  if (hasein) {
                     done = true;
                     dtestresult d = new dtestresult(2, "Ein Schutzsignal befindet sich in einer Ausfahrtsfahrstraße.", gl, fs);
                     r.add(d);
                  }
               } else if (gl.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL) {
                  done = true;
                  dtestresult d = new dtestresult(2, "Ein Zugdeckungssignal befindet sich in einer Ausfahrtsfahrstraße.", gl, fs);
                  r.add(d);
               }
            }
         }
      }

      return r;
   }
}
