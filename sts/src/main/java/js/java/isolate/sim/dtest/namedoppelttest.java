package js.java.isolate.sim.dtest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class namedoppelttest implements dtest {
   public namedoppelttest() {
      super();
   }

   @Override
   public String getName() {
      return "Name Doppelt";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5411 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      HashSet<String> names = new HashSet();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ALLE_GLEISE});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         String n = gl.getGleisExtend().getElementName();
         if (!n.isEmpty()) {
            if (names.contains(n)) {
               dtestresult d = new dtestresult(1, "Der Elementname " + n + " ist mehrfach vergeben.", gl);
               r.add(d);
            }

            names.add(n);
         }
      }

      return r;
   }
}
