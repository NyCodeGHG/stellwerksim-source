package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.zug.zug;

public class zugfx extends event {
   private String cmd;
   private zug z;

   public zugfx(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return "";
   }

   @Override
   protected boolean init(eventContainer e) {
      this.cmd = e.getValue("cmd");
      int zid = Integer.parseInt(e.getValue("zid"));
      this.z = this.my_main.findZug(zid);
      if (this.z != null) {
         this.callMe();
      } else {
         this.eventDone();
      }

      return true;
   }

   @Override
   public boolean pong() {
      if (this.cmd.equals("drop")) {
         this.z.lifeRemove();
      } else if (this.cmd.equals("nextHalt")) {
         this.z.enableBstgRedirectSpecial();
      }

      this.eventDone();
      return true;
   }
}
