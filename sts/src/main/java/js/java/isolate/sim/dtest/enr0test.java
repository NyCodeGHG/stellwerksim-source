package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class enr0test implements dtest {
   public enr0test() {
      super();
   }

   @Override
   public String getName() {
      return "ENR 0";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_GLEISE});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         if (gl.requiresENR()) {
            dtestresult d = new dtestresult(2, "Das Element " + gl.getCol() + "/" + gl.getRow() + ", das eine ENR benötigt, hat noch keine.", gl);
            r.add(d);
         }
      }

      return r;
   }
}
