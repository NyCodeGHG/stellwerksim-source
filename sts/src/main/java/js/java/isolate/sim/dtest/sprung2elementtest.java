package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class sprung2elementtest implements dtest {
   @Override
   public String getName() {
      return "Sprung an Sprung";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it1 = glb.findIterator(new Object[]{gleis.ELEMENT_SPRUNG});

      while (it1.hasNext()) {
         gleis gl = (gleis)it1.next();
         Iterator<gleis> it = gl.getNachbarn();

         while (it.hasNext()) {
            gleis gl2 = (gleis)it.next();
            if (gl2.getElement() == gleis.ELEMENT_SPRUNG) {
               dtestresult d = new dtestresult(2, "Der Sprung ENR " + gl.getENR() + " ist direkt mit einem anderen Sprung verbunden!", gl);
               r.add(d);
            }
         }
      }

      return r;
   }
}
