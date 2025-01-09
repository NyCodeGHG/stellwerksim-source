package js.java.isolate.sim.eventsys.events;

import java.util.HashSet;
import java.util.Vector;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class signalled extends signalstoerung {
   private static final String NAME = "signalled";
   private int brokeCnt;
   private boolean finishWait = false;

   public signalled(Simulator sim) {
      super(sim);
   }

   @Override
   public boolean init(int enr, int _dauer) {
      boolean eret = false;
      this.signal = this.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_SIGNAL});
      this.dauer = _dauer;
      this.brokeCnt = 10;
      if (this.signal == null) {
         this.eventDone();
      } else if (this.hasRegisteredForStellung(this.signal)) {
         this.eventDone();
      } else {
         this.registerForStellung(this.signal);
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.HOOKED, "signalled", this.code);
         eret = true;
      }

      return eret;
   }

   @Override
   public Vector getStructure() {
      Vector v = super.getStructure();
      v.addElement("brokeCnt");
      v.addElement(Integer.toString(this.brokeCnt));
      v.addElement("finishWait?");
      v.addElement(Boolean.toString(this.finishWait));
      return v;
   }

   @Override
   public void abort() {
      if (!this.event_running) {
         this.unregisterForStellung(this.signal);
         this.signal.getFluentData().displayBlink(false);
         this.eventDone();
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.NORMAL, "signalled", this.code);
      }
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      this.signal.getFluentData().displayBlink(st == gleisElements.ST_SIGNAL_GRÜN);
      if (st == gleisElements.ST_SIGNAL_GRÜN) {
         if (!this.event_running) {
            this.my_main.reportElementOccurance(this.signal, OCCU_KIND.OCCURED, "signalled", this.code);
            this.event_running = true;
            this.acceptingCall();
         }

         this.brokeCnt--;
      } else if (!this.finishWait && this.brokeCnt <= 0) {
         this.unregisterForStellung(this.signal);
         this.my_main.reportElementOccurance(this.signal, OCCU_KIND.NORMAL, "signalled", this.code);
         signalmeldung ret = new signalmeldung(this.my_main);
         this.dauer = random(2, 5);
         ret.zs1 = true;
         ret.glbModel = this.glbModel;
         ret.init(this.signal.getENR(), this.dauer);
         this.eventDone();
      }

      return true;
   }

   @Override
   protected void startCall(String t) {
      this.finishWait = true;
      this.my_main.reportElementOccurance(this.signal, OCCU_KIND.NORMAL, "signalled", this.code);
      eventContainer ev = new eventContainer(this.glbModel, sperreelementeaufzeit.class);
      ev.setIntValue("dauer", 3);
      ev.setValue("text", "Wir werden die Grüne Signallampe austauschen.");
      ev.setValue("schnell", true);
      HashSet<gleis> sig = new HashSet();
      sig.add(this.signal);
      ev.setGleisList(sig);
      ev.setName("Signallampe");
      event.createEvent(ev, this.glbModel, this, this.my_main);
   }

   @Override
   public void done(event child) {
      this.signal.getFluentData().displayBlink(false);
      this.unregisterForStellung(this.signal);
      this.eventDone();
   }

   @Override
   public String funkAntwort() {
      String call = this.getCallText("um einen Lampentausch zu veranlassen.");
      return "Hauptdraht Grün von " + this.signal.getElementName() + " ausgefallen, Ersatzdraht aktiv. " + call;
   }
}
