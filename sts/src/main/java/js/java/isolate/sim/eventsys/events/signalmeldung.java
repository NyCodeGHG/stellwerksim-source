package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.eventsys.cbCallMeIn;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class signalmeldung extends signalstoerung {
   private static final String NAME = "signalmeldung";

   public signalmeldung(Simulator sim) {
      super(sim);
   }

   @Override
   public boolean init(int enr, int _dauer) {
      boolean eret = false;
      this.signal = this.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_SIGNAL});
      this.dauer = _dauer;
      if (this.signal == null) {
         this.eventDone();
      } else if (this.hasRegisteredForStellung(this.signal)) {
         this.eventDone();
      } else {
         this.registerForStellung(this.signal);
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.HOOKED, "signalmeldung", this.code);
         eret = true;
      }

      return eret;
   }

   @Override
   public void abort() {
      if (!this.event_running) {
         this.unregisterForStellung(this.signal);
         this.eventDone();
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.NORMAL, "signalmeldung", this.code);
      }
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      if (st == gleisElements.ST_SIGNAL_GRÜN) {
         String call = this.getCallText();
         this.text = "Achtung: Signal <b>" + this.signal.getElementName() + "</b> meldet Störung." + call;
         if (!this.zs1) {
            g.getFluentData().setStellung(gleisElements.ST_SIGNAL_AUS);
         } else {
            g.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
         }

         if (!this.event_running) {
            this.my_main.reportElementOccurance(this.signal, OCCU_KIND.OCCURED, "signalmeldung", this.code);
            this.showMessageNow(this.text);
            this.registerForZug(this.signal);
            this.event_running = true;
            this.acceptingCall();
            this.registerCallBehaviour(new cbCallMeIn(this.dauer));
            g.disableAutoFW();
         }

         return false;
      } else {
         if (this.zs1) {
            if (st == gleisElements.ST_SIGNAL_ROT) {
               return true;
            }

            if (st == gleisElements.ST_SIGNAL_ZS1) {
               return true;
            }
         } else if (st == gleisElements.ST_SIGNAL_ROT) {
            return true;
         }

         return !this.event_running;
      }
   }

   @Override
   public boolean hookStatus(gleis g, int s, zug z) {
      if (s == 0) {
         return true;
      } else if (this.event_running) {
         if (s == 2 && g.getFluentData().getStellung() != gleisElements.ST_SIGNAL_ZS1) {
            g.disableAutoFW();
            return false;
         } else {
            return g.getFluentData().getStellung() == gleisElements.ST_SIGNAL_ZS1;
         }
      } else {
         return true;
      }
   }

   @Override
   public boolean pong() {
      this.unregisterForStellung(this.signal);
      this.unregisterForZug(this.signal);
      this.text = "Signal <b>" + this.signal.getElementName() + "</b> wieder einsatzbereit!";
      this.showMessageNow(this.text);
      if (this.signal.getFluentData().getStellung() != gleisElements.ST_SIGNAL_ZS1) {
         trigger t = new trigger() {
            @Override
            public boolean ping() {
               if (!signalmeldung.this.signal.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT)) {
                  this.tjm_add();
               }

               return false;
            }
         };
         t.ping();
      }

      this.event_running = false;
      this.eventDone();
      this.my_main.reportElementOccurance(this.signal, OCCU_KIND.NORMAL, "signalmeldung", this.code);
      return false;
   }
}
