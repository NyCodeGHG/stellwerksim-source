package js.java.isolate.sim.eventsys.events;

import java.util.Collections;
import java.util.LinkedList;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.gleisevent;
import js.java.isolate.sim.eventsys.signalevent;
import js.java.isolate.sim.gleis.gleis;

public class randomsignalstoerung extends gleisevent {
   protected int dauer;
   protected int level;

   public randomsignalstoerung(Simulator sim) {
      super(sim);
   }

   @Override
   protected boolean init(eventContainer e) {
      signalevent ret = null;
      this.dauer = e.getIntValue("dauer");
      this.level = 2;
      boolean stark = e.getBoolValue("stark", true);
      LinkedList<gleis> ll = this.glbModel.findAllWithZugLevel(this.level, new Object[]{gleis.ELEMENT_SIGNAL});
      if (stark) {
         this.filterHeavyStellung(ll);
      }

      if (ll.isEmpty()) {
         this.eventDone();
         return false;
      } else {
         boolean eret = false;
         Collections.shuffle(ll);

         gleis gl;
         do {
            gl = (gleis)ll.pollFirst();
            if (gl != null
               && !gl.hasHook(eventGenerator.HOOKKIND.WORKER, eventGenerator.T_GLEIS_STATUS)
               && !gl.hasHook(eventGenerator.HOOKKIND.WORKER, eventGenerator.T_GLEIS_STELLUNG)) {
               int r = random(0, 40);
               if (r < 10) {
                  ret = new signalstoerung(this.my_main);
               } else if (r < 20) {
                  ret = new signalausfall(this.my_main);
                  this.dauer = random(2, 5);
               } else if (r < 30) {
                  ret = new signalmeldung(this.my_main);
               } else {
                  ret = new signalled(this.my_main);
               }

               ret.glbModel = this.glbModel;
               eret = ret.init(gl.getENR(), this.dauer);
               break;
            }
         } while(gl != null);

         this.eventDone();
         return eret;
      }
   }

   @Override
   public String getText() {
      return "";
   }
}
