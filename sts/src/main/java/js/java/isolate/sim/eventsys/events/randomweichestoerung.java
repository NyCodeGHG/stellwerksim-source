package js.java.isolate.sim.eventsys.events;

import java.util.Collections;
import java.util.LinkedList;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.gleisevent;
import js.java.isolate.sim.eventsys.weicheevent;
import js.java.isolate.sim.gleis.gleis;

public class randomweichestoerung extends gleisevent {
   protected int dauer;
   protected int level;

   public randomweichestoerung(Simulator sim) {
      super(sim);
   }

   @Override
   protected boolean init(eventContainer e) {
      this.dauer = e.getIntValue("dauer");
      this.level = 2;
      boolean stark = e.getBoolValue("stark", true);
      LinkedList<gleis> ll = this.glbModel.findAllWithStellungLevel(this.level, new Object[]{gleis.ALLE_WEICHEN});
      if (stark) {
         this.filterHeavyZug(ll);
      }

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
               weicheevent ret = new weichenausfall(this.my_main);
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
