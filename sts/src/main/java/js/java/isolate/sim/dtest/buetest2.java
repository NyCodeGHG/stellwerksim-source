package js.java.isolate.sim.dtest;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.events.bahnueberganganruf;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class buetest2 implements dtest {
   @Override
   public String getName() {
      return "Anruf-Bü Anruf-Störung";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      Iterator<gleis> it = glb.findIterator(new Object[]{gleis.ELEMENT_ANRUFÜBERGANG});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         boolean found = false;

         for (eventContainer c : glb.events) {
            int e = c.getENR();
            boolean m = bahnueberganganruf.class.isAssignableFrom(c.getFactory().getEventTyp());
            if (m && e == gl.getENR()) {
               found = true;
            }
         }

         if (!found) {
            dtestresult d = new dtestresult(2, "Ein Anrufschranke braucht eine Störung 'Bahnüberganganruf'!", gl);
            r.add(d);
         }
      }

      return r;
   }
}
