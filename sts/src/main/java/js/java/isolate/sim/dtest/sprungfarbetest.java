package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class sprungfarbetest implements dtest {
   @Override
   public String getName() {
      return "Sprungfarbe";
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
         gleis gl2 = glb.findFirst(new Object[]{gl, gleis.ELEMENT_SPRUNG, gl.getENR()});
         if (gl2 != null && !gl2.getExtendFarbe().equalsIgnoreCase(gl.getExtendFarbe())) {
            dtestresult d = new dtestresult(1, "Beim Sprung " + gl.getENR() + " haben die beiden Elemente unterschiedliche Hintergrundfarben.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
