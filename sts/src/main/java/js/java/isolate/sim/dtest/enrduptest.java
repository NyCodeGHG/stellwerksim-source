package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class enrduptest implements dtest {
   @Override
   public String getName() {
      return "ENR Dup";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ALLE_GLEISE});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         if (gl.getENR() > 0) {
            Iterator<gleis> it2 = glb.findIterator(new Object[]{gl.getENR()});

            while (it2.hasNext()) {
               gleis gl2 = (gleis)it2.next();
               if (gl2 != gl) {
                  List<element> partner = gl.typPartner();
                  element e = gl2.getElement();
                  boolean allowedPartner = false;

                  for (element te : partner) {
                     if (te.matches(e)) {
                        allowedPartner = true;
                        break;
                     }
                  }

                  if (!allowedPartner) {
                     dtestresult d = new dtestresult(2, "Die ENR " + gl.getENR() + " ist doppelt vergeben!", gl2);
                     r.add(d);
                  }
               }
            }
         }
      }

      return r;
   }
}
