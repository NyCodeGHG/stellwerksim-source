package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class enrsprungtest1 implements dtest {
   public enrsprungtest1() {
      super();
   }

   @Override
   public String getName() {
      return "genau 1x ENR Sprung";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5740 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SPRUNG});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         int cc = 0;
         Iterator<gleis> it2 = glb.findIterator(new Object[]{gleis.ALLE_GLEISE, gl.getENR()});

         while(it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2 != gl) {
               if (gl2.getElement() != gleis.ELEMENT_SPRUNG) {
                  dtestresult d = new dtestresult(2, "Die ENR des Sprungs " + gl.getENR() + " ist noch anderweitig verwendet!", gl2);
                  r.add(d);
               } else {
                  ++cc;
               }
            }
         }

         if (cc == 0) {
            dtestresult d = new dtestresult(2, "Zum Sprung " + gl.getENR() + " fehlt das Gegenstück!", gl);
            r.add(d);
         } else if (cc > 1) {
            dtestresult d = new dtestresult(2, "Zum Sprung " + gl.getENR() + " gibt es mehrere Gegenstücke!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
