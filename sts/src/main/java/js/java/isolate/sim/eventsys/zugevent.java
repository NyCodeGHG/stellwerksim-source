package js.java.isolate.sim.eventsys;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.zug.zug;

public abstract class zugevent extends event {
   protected zugevent(Simulator sim) {
      super(sim);
   }

   public boolean hookFahrt(zug z, gleis g) {
      return true;
   }

   public boolean hookSignalWurdeGruen(zug z, gleis g) {
      return true;
   }

   public boolean hookAbfahrtBahnsteig(zug z, gleis g) {
      return true;
   }

   public boolean hookKuppeln(zug z, zug zielzug) {
      return true;
   }

   public boolean hookFlügeln(zug z, zug zielzug) {
      return true;
   }

   public boolean hookLokFlügeln(zug z, zug zielzug) {
      return true;
   }

   @Override
   public final boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
      if (e != null && e instanceof zugmsg) {
         zugmsg ge = (zugmsg)e;
         if (typ == eventGenerator.T_ZUG_FAHRT) {
            return this.hookFahrt(ge.z, ge.g);
         }

         if (typ == eventGenerator.T_ZUG_WURDEGRUEN) {
            return this.hookSignalWurdeGruen(ge.z, ge.g);
         }

         if (typ == eventGenerator.T_ZUG_ABFAHRT) {
            return this.hookAbfahrtBahnsteig(ge.z, ge.g);
         }

         if (typ == eventGenerator.T_ZUG_KUPPELN) {
            return this.hookKuppeln(ge.z, ge.zielzug);
         }

         if (typ == eventGenerator.T_ZUG_FLÜGELN) {
            return this.hookFlügeln(ge.z, ge.zielzug);
         }

         if (typ == eventGenerator.T_ZUG_LOKFLÜGELN) {
            return this.hookLokFlügeln(ge.z, ge.zielzug);
         }
      }

      return true;
   }

   protected final void register(eventGenerator.TYPES typ, zug z) {
      if (z != null) {
         z.registerHook(typ, this);
      }
   }

   protected final void unregister(eventGenerator.TYPES typ, zug z) {
      if (z != null) {
         z.unregisterHook(typ, this);
      }
   }
}
