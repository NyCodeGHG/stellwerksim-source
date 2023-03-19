package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.zug.zug;

public abstract class zugStoerungStandard extends zugStoerungBase {
   protected zugStoerungStandard(Simulator sim) {
      super(sim);
   }

   @Override
   public boolean hookSignalWurdeGruen(zug z, gleis g) {
      return this.occuredFor(
         z, eventGenerator.TYPES.T_ZUG_WURDEGRUEN, "Abfahrt am Signal " + g.getElementName() + " verzögert sich um " + this.dauerFormated() + " Minuten"
      );
   }

   @Override
   public boolean hookKuppeln(zug z, zug zielzug) {
      return this.occuredFor(
         z, eventGenerator.TYPES.T_ZUG_KUPPELN, "Kupplung mit " + zielzug.getSpezialName() + " verzögert sich um " + this.dauerFormated() + " Minuten"
      );
   }

   @Override
   public boolean hookFlügeln(zug z, zug zielzug) {
      return this.occuredFor(z, eventGenerator.TYPES.T_ZUG_FLÜGELN, "Flügeln verzögert sich um " + this.dauerFormated() + " Minuten");
   }

   @Override
   public boolean hookLokFlügeln(zug z, zug zielzug) {
      return this.occuredFor(z, eventGenerator.TYPES.T_ZUG_LOKFLÜGELN, "Lok abkuppeln verzögert sich um " + this.dauerFormated() + " Minuten");
   }

   @Override
   public boolean hookAbfahrtBahnsteig(zug z, gleis g) {
      return this.occuredFor(
         z, eventGenerator.TYPES.T_ZUG_ABFAHRT, "Abfahrt am Gleis " + g.getSWWert_special() + " verzögert sich um " + this.dauerFormated() + " Minuten"
      );
   }

   protected final boolean occuredFor(zug z, eventGenerator.TYPES t, String msg) {
      if (this.bname != null && !this.bname.isEmpty() && !z.getZielGleis().startsWith(this.bname)) {
         return true;
      } else if (this.isEventDone()) {
         return true;
      } else {
         if (this.runningZug == null) {
            this.runningZug = z;
            this.unregisterAll();
            this.registered.add(z);
            this.register(t, z);
            this.text = "Meldung von " + this.runningZug.getSpezialName() + ": " + msg;
            if (this.dtext != null && !this.dtext.isEmpty()) {
               this.text = this.text + ": " + this.dtext;
            } else {
               this.text = this.text + ".";
            }

            if (!this.silent) {
               this.showCallMessageNow(this.text);
            }

            this.callMeIn(this.dauer);
         }

         return false;
      }
   }

   @Override
   public boolean pong() {
      this.runningZug = null;
      this.unregisterAll();
      this.eventDone();
      return false;
   }
}
