package js.java.isolate.sim.eventsys.events;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.eventsys.cbCallMeIn;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.signalevent;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class signalstoerung extends signalevent {
   private static final String NAME = "signalstoerung";
   protected gleis signal;
   protected String text = null;
   protected int dauer;
   protected boolean event_running = false;
   protected boolean zs1 = random(0, 100) >= 50;

   public signalstoerung(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      if (!e.getBoolValue("random", false)) {
         if (e.getBoolValue("zs1", false)) {
            this.zs1 = true;
         }

         return this.init(e.getENR(), e.getIntValue("dauer"));
      } else {
         int level = 2;
         LinkedList<gleis> ll = this.glbModel.findAllWithZugLevel(2, new Object[]{gleis.ELEMENT_SIGNAL});
         this.filterHeavyStellung(ll);
         if (ll.isEmpty()) {
            this.eventDone();
            return false;
         } else {
            Collections.shuffle(ll);

            gleis gl;
            do {
               gl = (gleis)ll.pollFirst();
               if (gl != null) {
                  return this.init(gl.getENR(), e.getIntValue("dauer"));
               }
            } while (gl != null);

            return false;
         }
      }
   }

   @Override
   public boolean init(int enr, int _dauer) {
      boolean eret = false;
      this.signal = this.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_SIGNAL});
      this.dauer = _dauer + (this.my_main.isRealistic() ? random(10, 30) : 0);
      if (this.signal == null) {
         this.eventDone();
      } else if (this.hasRegisteredForZug(this.signal)) {
         this.eventDone();
      } else {
         this.registerForZug(this.signal);
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.HOOKED, "signalstoerung", this.code);
         eret = true;
      }

      return eret;
   }

   @Override
   public Vector getStructure() {
      Vector v = super.getStructure();
      v.addElement("signal enr");
      v.addElement(Integer.toString(this.signal.getENR()));
      v.addElement("signal name");
      v.addElement(this.signal.getElementName());
      return v;
   }

   @Override
   public void abort() {
      if (!this.event_running) {
         this.unregisterForZug(this.signal);
         this.eventDone();
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.NORMAL, "signalstoerung", this.code);
      }
   }

   @Override
   public boolean hookStatus(gleis g, int s, zug z) {
      if (s == 0) {
         return true;
      } else if (s == 2 && g.getFluentData().getStellung() != gleisElements.ST_SIGNAL_ZS1) {
         String call = this.getCallText();
         this.text = "Achtung: Triebfahrzeugführer von "
            + z.getSpezialName()
            + " meldet Störung am Signal <b>"
            + this.signal.getElementName()
            + "</b>, Notbremsung! Geben Sie Zugbefehl 'weiterfahren' für den Zug"
            + (this.zs1 ? " oder Ersatzsignal (ErsGT)" : "")
            + "."
            + call;
         if (!this.zs1) {
            g.getFluentData().setStellung(gleisElements.ST_SIGNAL_AUS);
         } else {
            g.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
         }

         g.getFluentData().setStartingFS(null);
         if (!this.event_running) {
            this.my_main.reportElementOccurance(this.signal, OCCU_KIND.OCCURED, "signalstoerung", this.code);
            this.showMessageNow(this.text);
            this.registerForStellung(this.signal);
            this.event_running = true;
            this.acceptingCall();
            this.registerCallBehaviour(new cbCallMeIn(this.dauer));
            g.disableAutoFW();
         }

         return false;
      } else {
         return g.getFluentData().getStellung() == gleisElements.ST_SIGNAL_ZS1;
      }
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      boolean ret = true;
      if (this.event_running) {
         if (this.zs1 && f == null) {
            ret = true;
         } else {
            if (st != gleisElements.ST_SIGNAL_ROT) {
               String call = this.getCallText();
               this.text = "Achtung: Störung am Signal <b>"
                  + this.signal.getElementName()
                  + "</b>! Reparatur wurde bereits in die Wege geleitet. Eventuell vorhandene automatische Fahrstraßen abschalten!"
                  + call;
               this.showMessageNow(this.text);
               g.disableAutoFW();
            }

            ret = false;
         }
      }

      return ret;
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
               if (!signalstoerung.this.signal.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT)) {
                  this.tjm_add();
               }

               return false;
            }
         };
         t.ping();
      }

      this.event_running = false;
      this.eventDone();
      this.my_main.reportElementOccurance(this.signal, OCCU_KIND.NORMAL, "signalstoerung", this.code);
      return false;
   }

   @Override
   public String funkName() {
      return this.event_running ? this.signal.getElementName() : null;
   }

   @Override
   public String funkAntwort() {
      if (!this.isCalled()) {
         String call = this.getCallText();
         return "Signalstörung " + this.signal.getElementName() + "." + call;
      } else {
         return "Reparatur " + this.signal.getElementName() + " dauert noch ca. " + (this.restTime() + 1) + " Minuten.";
      }
   }
}
