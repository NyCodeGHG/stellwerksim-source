package js.java.isolate.sim.eventsys.events;

import java.util.Collections;
import java.util.LinkedList;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.bueevent;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.gleisevent;
import js.java.isolate.sim.gleis.gleis;

public class randombuestoerung extends gleisevent {
   protected int dauer;
   protected int level;

   public randombuestoerung(Simulator sim) {
      super(sim);
   }

   @Override
   protected boolean init(eventContainer e) {
      this.dauer = (int)((Math.random() + 0.5) * (double)e.getIntValue("dauer"));
      this.level = 2;
      LinkedList<gleis> ll = this.glbModel.findAllWithStellungLevel(this.level, new Object[]{gleis.ELEMENT_BAHNÜBERGANG});
      if (ll.isEmpty()) {
         this.eventDone();
         return false;
      } else {
         Collections.shuffle(ll);
         boolean eret = false;

         gleis gl;
         do {
            gl = (gleis)ll.pollFirst();
            if (gl != null
               && !gl.hasHook(eventGenerator.HOOKKIND.WORKER, eventGenerator.T_GLEIS_STATUS)
               && !gl.hasHook(eventGenerator.HOOKKIND.WORKER, eventGenerator.T_GLEIS_STELLUNG)) {
               bueevent ret = new bahnuebergangstoerung(this.my_main);
               ret.glbModel = this.glbModel;
               eret = ret.init(gl.getENR(), this.dauer);
               break;
            }
         } while (gl != null);

         this.eventDone();
         return eret;
      }
   }

   @Override
   public String getText() {
      return "";
   }
}
