package js.java.isolate.sim.eventsys.events;

import java.util.Iterator;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.cbCallMeIn;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.eventmsg;
import js.java.isolate.sim.eventsys.fahrstrassemsg;
import js.java.isolate.sim.eventsys.gleismsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class weichenfsstoerung extends event {
   private static final String NAME = "weichenfsstoerung";
   private int dauer;
   private String text = null;

   public weichenfsstoerung(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      this.dauer = e.getIntValue("dauer") + (this.my_main.isRealistic() ? random(5, 15) : 0);
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_WEICHEN});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         gl.registerHook(eventGenerator.T_GLEIS_STELLUNG, this);
      }

      String call = "";
      if (!this.hasParent()) {
         call = this.getCallText();
         this.acceptingCall();
         this.registerCallBehaviour(new cbCallMeIn(this.dauer));
      } else {
         this.callMeIn(this.dauer);
      }

      this.text = "Automatischer Weichenlauf gestört, Weichen müssen manuell gestellt werden um Fahrstraße anlegen zu können." + call;
      this.showMessageNow(this.text);
      this.my_main.reportOccurance(this.getCode(), OCCU_KIND.OCCURED, "weichenfsstoerung", this.code);
      return true;
   }

   @Override
   public final boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
      if (e != null && e instanceof gleismsg) {
         gleismsg ge = (gleismsg)e;
         if (typ == eventGenerator.T_GLEIS_STATUS) {
            return this.hookStatus(ge.g, ge.s, ge.z);
         }

         if (typ == eventGenerator.T_GLEIS_STELLUNG) {
            return this.hookStellung(ge.g, ge.st, ge.f);
         }
      } else if (e != null && e instanceof fahrstrassemsg) {
         fahrstrassemsg gex = (fahrstrassemsg)e;
         if (typ == eventGenerator.T_FS_SETZEN) {
            return this.hookSet(gex.f);
         }

         if (typ == eventGenerator.T_FS_LOESCHEN) {
            return this.hookClear(gex.f);
         }
      }

      return true;
   }

   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      return f == null ? true : g.getFluentData().getStellung() == st;
   }

   public boolean hookStatus(gleis g, int s, zug z) {
      return true;
   }

   private boolean hookClear(fahrstrasse f) {
      return true;
   }

   private boolean hookSet(fahrstrasse f) {
      return true;
   }

   @Override
   public void abort() {
      this.pong();
   }

   @Override
   public boolean pong() {
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_WEICHEN});

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         gl.unregisterHook(eventGenerator.T_GLEIS_STELLUNG, this);
      }

      this.text = "Automatischer Weichenlauf wieder möglich.";
      this.showMessageNow(this.text);
      this.my_main.reportOccurance(this.getCode(), OCCU_KIND.NORMAL, "weichenfsstoerung", this.code);
      this.eventDone();
      return false;
   }

   @Override
   public String funkName() {
      return "Automatischer Weichenlauf";
   }

   @Override
   public String funkAntwort() {
      if (!this.isCalled() && !this.hasParent()) {
         String call = this.getCallText();
         return "Automatischer Weichenlauf gestört." + call;
      } else {
         return "Reparatur dauert noch ca. " + (this.restTime() + 1) + " Minuten.";
      }
   }
}
