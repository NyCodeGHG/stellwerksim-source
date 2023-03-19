package js.java.isolate.sim.eventsys.events;

import java.util.HashMap;
import java.util.Iterator;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.bueevent;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.chatcomng.OCCU_KIND;

@Deprecated
public class bahnuebergangstoerung extends bueevent {
   private static final String NAME = "bahnuebergangstoerung";
   private boolean waitingCallback = false;
   private final HashMap<Integer, Long> knownZug = new HashMap();
   private bahnuebergangwaerter waerter = null;
   private gleis bü = null;
   private int büenr;
   private String text = null;
   private int dauer;
   private boolean event_running = false;

   public bahnuebergangstoerung(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      this.eventDone();
      return false;
   }

   @Override
   public boolean init(int enr, int _dauer) {
      this.eventDone();
      return false;
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      if (st == g.getFluentData().getStellung() && f == null) {
         return !this.event_running;
      } else {
         g.getFluentData().setStellung(gleisElements.ST_BAHNÜBERGANG_AUS);
         if (!this.event_running) {
            String call = this.getCallText();
            this.text = "Achtung: Bahnübergang <b>"
               + this.bü.getElementName()
               + "</b> schickt keine Rückmeldung, Störung! Fahrstraße kann nicht geschaltet werden! Reparatur wurde in die Wege geleitet."
               + call;
            this.acceptingCall();
            this.showMessageNow(this.text);
            this.waitingCallback = false;
            Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_BAHNÜBERGÄNGE, this.büenr});

            while(it.hasNext()) {
               gleis gl = (gleis)it.next();
               this.my_main.reportElementOccurance(gl, OCCU_KIND.OCCURED, "bahnuebergangstoerung", this.code);
               this.registerForZug(gl);
            }
         }

         this.event_running = true;
         return false;
      }
   }

   @Override
   public void startCall(String token) {
      String text2 = this.funkName() + ": Danke für den Anruf, die Arbeiten beginnen sofort.<br><br>";
      this.text = this.callbackText();
      this.showCallMessageNow(text2 + this.text);
      this.waitingCallback = true;
   }

   @Override
   public boolean hookStatus(gleis g, int s, zug z) {
      if (z != null) {
         if (s == 2) {
            if (!this.knownZug.containsKey(z.getZID_num())) {
               this.knownZug.put(z.getZID_num(), System.currentTimeMillis());
            }

            long v = this.knownZug.get(z.getZID_num());
            return System.currentTimeMillis() - v > 30000L;
         }

         this.knownZug.remove(z.getZID_num());
      }

      return true;
   }

   @Override
   public void clicked(String url) {
      if (this.waitingCallback) {
         this.waerter = null;
         String w = "";
         if (url.equals("yes")) {
            Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_BAHNÜBERGÄNGE, this.büenr});

            while(it.hasNext()) {
               gleis g = (gleis)it.next();
               this.unregisterForStellung(g);
               this.unregisterForZug(g);
               g.getFluentData().setGesperrtAndStellung(true, true);
               this.my_main.reportElementOccurance(g, OCCU_KIND.LOCKED, "bahnuebergangstoerung", this.code);
            }

            this.bü.getFluentData().setStellung(gleisElements.ST_BAHNÜBERGANG_OFFEN);
            this.waerter = new bahnuebergangwaerter(this.my_main);
            this.waerter.glbModel = this.glbModel;
            this.waerter.init(this.büenr);
            w = " Wir schicken Wärter zur Sicherung. Bitte anliegende Fahrstraßen aufheben und erneut stellen!";
         } else {
            Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_BAHNÜBERGÄNGE, this.büenr});

            while(it.hasNext()) {
               gleis g = (gleis)it.next();
               g.getFluentData().setGesperrtAndStellung(true, true);
            }

            w = " Bitte anliegende Fahrstraßen aufheben. Zugfahrt per Ersatzsignal, Zug wird am Bahnübergang für längere Zeit zum Stehen kommen, da dort eine Sicherung der Überfahrt statt findet.";
         }

         this.callMeIn(this.dauer);
         this.text = "Ok, die Reparatur des Bahnübergangs " + this.bü.getElementName() + " dauert noch " + this.dauer + " Minuten." + w;
         this.showMessageNow(this.text);
         this.waitingCallback = false;
      }
   }

   private String callbackText() {
      return "Die Reparatur des Bahnübergangs "
         + this.bü.getElementName()
         + " dauert noch etwas, wir könnten aber ein Wärterteam hinschicken, um den geregelten Betrieb wieder aufnehmen zu können. Die Reparaturzeit könnte dadurch sogar verkürzt werden.<ul><li><a href='yes'>Wärterteam schicken</a><li><a href='no'>nein, keine Wärter, Zug sichert selbst</a></ul>";
   }

   @Override
   public boolean pong() {
      if (this.waerter != null) {
         this.waerter.close(this.büenr);
      }

      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_BAHNÜBERGÄNGE, this.büenr});

      while(it.hasNext()) {
         gleis g = (gleis)it.next();
         this.unregisterForStellung(g);
         this.unregisterForZug(g);
         g.getFluentData().setGesperrt(false);
         this.my_main.reportElementOccurance(g, OCCU_KIND.NORMAL, "bahnuebergangstoerung", this.code);
      }

      this.text = "Bahnübergang <b>" + this.bü.getElementName() + "</b> wieder einsatzbereit!";
      this.showMessageNow(this.text);
      this.event_running = false;
      if (this.bü.getElement().matches(gleis.ELEMENT_ANRUFÜBERGANG)) {
         this.bü.getFluentData().setStellung(gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN);
      } else {
         this.bü.getFluentData().setStellung(gleisElements.ST_BAHNÜBERGANG_OFFEN);
      }

      this.eventDone();
      return false;
   }

   @Override
   public String funkName() {
      return this.event_running ? this.bü.getElementName() : null;
   }

   @Override
   public String funkAntwort() {
      if (this.waitingCallback) {
         return "Wir warten auf Antwort!";
      } else if (!this.isCalled()) {
         String call = this.getCallText();
         return "BÜ-Störung " + this.bü.getElementName() + "." + call;
      } else {
         return "Reparatur " + this.bü.getElementName() + " dauert noch ca. " + (this.restTime() + 1) + " Minuten.";
      }
   }
}
