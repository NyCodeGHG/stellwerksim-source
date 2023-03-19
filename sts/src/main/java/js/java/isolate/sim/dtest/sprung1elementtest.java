package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class sprung1elementtest implements dtest {
   public sprung1elementtest() {
      super();
   }

   @Override
   public String getName() {
      return "Sprunganschluss";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SPRUNG});

      while(it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         int cc = 0;

         for(Iterator<gleis> it2 = gl.getNachbarn(); it2.hasNext(); ++cc) {
            it2.next();
         }

         if (cc > 1) {
            dtestresult d = new dtestresult(2, "Der Sprung ENR " + gl.getENR() + " ist mit mehr als einem Gleis verbunden!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
