package js.java.isolate.sim.eventsys.events;

import java.util.Iterator;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.cbCallMeIn;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.eventmsg;
import js.java.isolate.sim.eventsys.gleismsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class fsspeicherstoerung extends event {
   private static final String NAME = "fsspeicherstoerung";
   private static final int REDUCEDELAY = 8;
   private int dauer;
   private String text = null;

   public fsspeicherstoerung(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      this.dauer = e.getIntValue("dauer") + (this.my_main.isRealistic() ? random(5, 30) : 0);
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         gl.registerHook(eventGenerator.T_GLEIS_FSSPEICHER, this);
      }

      String call = "";
      if (!this.hasParent()) {
         call = this.getCallText();
         this.acceptingCall();
         this.registerCallBehaviour(new cbCallMeIn(this.dauer));
      } else {
         this.callMeIn(this.dauer);
      }

      this.text = "Fahrstraßenspeicher gestört, gespeicherte Fahrstraßen werden nicht geschaltet." + call;
      this.showMessageNow(this.text);
      this.my_main.reportOccurance(this.getCode(), OCCU_KIND.OCCURED, "fsspeicherstoerung", this.code);
      return true;
   }

   @Override
   public final boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
      return e == null || !(e instanceof gleismsg) || typ != eventGenerator.T_GLEIS_FSSPEICHER;
   }

   @Override
   public void abort() {
      this.pong();
   }

   @Override
   public boolean pong() {
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         gl.unregisterHook(eventGenerator.T_GLEIS_FSSPEICHER, this);
      }

      this.text = "Fahrstraßenspeicher wieder möglich.";
      this.showMessageNow(this.text);
      this.my_main.reportOccurance(this.getCode(), OCCU_KIND.NORMAL, "fsspeicherstoerung", this.code);
      this.eventDone();
      return false;
   }

   @Override
   public String funkName() {
      return "Fahrstraßenspeicher";
   }

   @Override
   public String funkAntwort() {
      if (!this.isCalled() && !this.hasParent()) {
         String call = this.getCallText();
         return "Fahrstraßenspeicher ausgefallen." + call;
      } else {
         return "Reparatur dauert noch ca. " + (this.restTime() + 1) + " Minuten.";
      }
   }
}
