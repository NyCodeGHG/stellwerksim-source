package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class swwertVmaxtest implements dtest {
   @Override
   public String getName() {
      return "SW-Wert Vmax";
   }

   @Override
   public String getVersion() {
      return "$Revision: 3293 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_SETVMAX});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         String sw = gl.getSWWert();
         if (sw != null && !sw.isEmpty() && sw.charAt(0) == '+') {
            dtestresult d = new dtestresult(1, "SW-Wert beim Vmax beginnt mit einem Plus-Zeichen, diese Anwendung ist vermutlich nicht so gedacht!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
