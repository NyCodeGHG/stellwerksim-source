package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class signalausfall extends signalstoerung {
   private static final String NAME = "signalausfall";
   private int cnt = 0;
   private boolean faultState = true;
   private final boolean mini;

   public signalausfall(Simulator sim) {
      super(sim);
      this.mini = false;
   }

   public signalausfall(Simulator sim, boolean mini) {
      super(sim);
      this.mini = mini;
   }

   @Override
   public boolean init(int enr, int _dauer) {
      boolean eret = false;
      this.signal = this.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_SIGNAL});
      this.dauer = _dauer;
      if (this.dauer > 5) {
         this.dauer = 5;
      }

      if (this.dauer < 1) {
         this.dauer = 1;
      }

      if (this.signal == null) {
         this.eventDone();
      } else if (this.hasRegisteredForStellung(this.signal)) {
         this.eventDone();
      } else {
         if (this.mini) {
            this.cnt = 2;
         } else {
            this.cnt = random(2, 6);
         }

         this.registerForStellung(this.signal);
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.HOOKED, "signalausfall", this.code);
         eret = true;
      }

      return eret;
   }

   @Override
   public void abort() {
      this.unregisterForStellung(this.signal);
      this.eventDone();
      this.my_main.reportElementOccurance(this.signal, OCCU_KIND.NORMAL, "signalausfall", this.code);
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      if (st == gleisElements.ST_SIGNAL_GRÜN) {
         this.callMeIn(random(1, this.dauer + 5));
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.OCCURED, "signalausfall", this.code);
      }

      return true;
   }

   @Override
   public boolean pong() {
      if (this.faultState && this.signal.getFluentData().getStellung() == gleisElements.ST_SIGNAL_GRÜN) {
         this.signal.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
         this.signal.getFluentData().setStartingFS(null);
         this.text = "Achtung: Signal <b>"
            + this.signal.getElementName()
            + "</b> auf Rot gefallen! Fahrstraße nicht aufgelöst, Streckenfreifahrt per Ersatzsignal (ErsGT) nötig!";
         this.showMessageNow(this.text);
         --this.cnt;
         if (this.cnt <= 0) {
            this.unregisterForStellung(this.signal);
            this.eventDone();
            this.my_main.reportElementOccurance(this.signal, OCCU_KIND.NORMAL, "signalausfall", this.code);
         } else {
            this.my_main.reportElementOccurance(this.signal, OCCU_KIND.HOOKED, "signalausfall", this.code);
         }
      } else {
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.HOOKED, "signalausfall", this.code);
         this.faultState = !this.faultState;
      }

      return false;
   }
}
