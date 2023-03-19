package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class enreinfahrttest1 implements dtest {
   public enreinfahrttest1() {
      super();
   }

   @Override
   public String getName() {
      return "1x ENR Einfahrt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_EINFAHRT});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         Iterator<gleis> it2 = glb.findIterator(new Object[]{gl.getENR(), gleis.ALLE_GLEISE, gl});

         while(it2.hasNext()) {
            gleis gl2 = (gleis)it2.next();
            if (gl2 != gl && gl2.getElement() != gleis.ELEMENT_ÜBERGABEAKZEPTOR) {
               dtestresult d = new dtestresult(2, "Die ENR " + gl.getENR() + " der Einfahrt " + gl.getSWWert() + " ist mehrfach verwendet!", gl2);
               r.add(d);
            }
         }
      }

      return r;
   }
}
