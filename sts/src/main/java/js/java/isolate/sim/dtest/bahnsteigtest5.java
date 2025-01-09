package js.java.isolate.sim.dtest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class bahnsteigtest5 implements dtest {
   @Override
   public String getName() {
      return "Bahnsteig Schreibweise";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5689 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      Map<String, String> names = new HashMap();
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_BAHNSTEIG});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         String name = gl.getSWWert_special();
         String uName = name.toLowerCase();
         if (names.containsKey(uName)) {
            if (!((String)names.get(uName)).equals(name) && ((String)names.get(uName)).equalsIgnoreCase(name)) {
               dtestresult d = new dtestresult(2, "Der Bahnsteig/Haltepunkt " + name + " hat unterschiedliche Groß-/Kleinschreibung.", gl);
               r.add(d);
            }
         } else {
            names.put(uName, name);
         }
      }

      return r;
   }
}
