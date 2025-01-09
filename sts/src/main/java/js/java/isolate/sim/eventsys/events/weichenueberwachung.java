package js.java.isolate.sim.eventsys.events;

import java.util.LinkedList;
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

public class weichenueberwachung extends event {
   private static final String NAME = "weichenueberwachung";
   private static boolean oneIsRunning = false;
   private eventContainer ec;
   private String text = null;
   protected int dauer;
   private LinkedList<gleis> weichen = null;

   public weichenueberwachung(Simulator sim) {
      super(sim);
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

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      if (this.my_main.isRealistic()) {
         this.eventDone();
         return false;
      } else if (oneIsRunning) {
         this.eventDone();
         return false;
      } else {
         oneIsRunning = true;
         this.ec = e;
         this.dauer = e.getIntValue("dauer");
         this.weichen = new LinkedList();

         for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
            for (int x = 0; x < this.glbModel.getGleisWidth(); x++) {
               gleis gl = this.glbModel.getXY_null(x, y);
               if (gl != null && gl.getElement().matches(gleis.ALLE_WEICHEN)) {
                  gl.registerHook(eventGenerator.T_GLEIS_STELLUNG, this);
                  this.weichen.add(gl);
               }
            }
         }

         String call = "";
         if (!this.hasParent()) {
            call = this.getCallText();
            this.acceptingCall();
            this.registerCallBehaviour(new cbCallMeIn(this.dauer));
         } else {
            this.callMeIn(this.dauer);
         }

         this.text = "Die Weichenzungenüberwachung einiger Weichen ist ausgefallen! Zur Sicherheit wurden alle Weichen elektrisch verriegelt und können bis zur Behebung der Störung nicht bewegt werden. Fahrstraßen ohne Weichenbewegung sind jedoch möglich!"
            + call;
         this.showMessageNow(this.text);
         this.my_main.playAlarm(2);
         this.my_main.reportOccurance(this.getCode(), OCCU_KIND.OCCURED, "weichenueberwachung", this.code);
         return true;
      }
   }

   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      return g.getElement().matches(gleis.ALLE_WEICHEN) && g.getFluentData().getStellung() == st;
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
      for (gleis gl : this.weichen) {
         gl.unregisterHook(eventGenerator.T_GLEIS_STELLUNG, this);
      }

      this.text = "Weichenzungenüberwachung wieder einsatzbereit!";
      this.showMessageNow(this.text);
      this.my_main.reportOccurance(this.getCode(), OCCU_KIND.NORMAL, "weichenueberwachung", this.code);
      this.eventDone();
      return false;
   }

   @Override
   public String funkName() {
      return "Weichenzungenüberwachung ausgefallen!";
   }

   @Override
   public String funkAntwort() {
      if (!this.isCalled() && !this.hasParent()) {
         String call = this.getCallText();
         return "Weichenzungenüberwachung ausgefallen." + call;
      } else {
         return "Reparatur dauert noch ca. " + (this.restTime() + 1) + " Minuten.";
      }
   }
}
