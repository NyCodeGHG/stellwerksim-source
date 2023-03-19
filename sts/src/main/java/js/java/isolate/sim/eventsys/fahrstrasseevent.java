package js.java.isolate.sim.eventsys;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public abstract class fahrstrasseevent extends event {
   protected fahrstrasseevent(Simulator sim) {
      super(sim);
   }

   public boolean hookSet(fahrstrasse f) {
      return true;
   }

   public boolean hookClear(fahrstrasse f) {
      return true;
   }

   @Override
   public final boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
      if (e != null && e instanceof fahrstrassemsg) {
         fahrstrassemsg ge = (fahrstrassemsg)e;
         if (typ == eventGenerator.T_FS_SETZEN) {
            return this.hookSet(ge.f);
         }

         if (typ == eventGenerator.T_FS_LOESCHEN) {
            return this.hookClear(ge.f);
         }
      }

      return true;
   }

   protected final void register(eventGenerator.TYPES typ, fahrstrasse f) {
      if (f != null) {
         f.registerHook(typ, this);
      }
   }

   protected final void unregister(eventGenerator.TYPES typ, fahrstrasse f) {
      if (f != null) {
         f.unregisterHook(typ, this);
      }
   }
}
