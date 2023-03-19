package js.java.isolate.sim.eventsys.events;

import java.util.Vector;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.weicheevent;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class weichenausfall extends weicheevent {
   private static final String NAME = "weichenausfall";
   private gleis weiche;
   private int dauer;
   private String text = null;
   private boolean event_running = false;
   private boolean gesperrtModus = false;
   private boolean waitingCallback = false;

   public weichenausfall(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      boolean eret = false;
      int weicheenr = e.getENR();
      this.weiche = this.glbModel.findFirst(new Object[]{weicheenr, gleis.ALLE_WEICHEN});
      this.dauer = e.getIntValue("dauer") + (this.my_main.isRealistic() ? random(10, 30) : 0);
      if (this.weiche == null) {
         this.eventDone();
      } else if (this.hasRegisteredForStellung(this.weiche)) {
         this.eventDone();
      } else {
         this.registerForStellung(this.weiche);
         this.my_main.reportElementOccurance(this.weiche, OCCU_KIND.HOOKED, "weichenausfall", this.code);
         eret = true;
      }

      return eret;
   }

   @Override
   public boolean init(int enr, int _dauer) {
      boolean eret = false;
      this.weiche = this.glbModel.findFirst(new Object[]{enr, gleis.ALLE_WEICHEN});
      this.dauer = _dauer + (this.my_main.isRealistic() ? random(10, 30) : 0);
      if (this.weiche == null) {
         this.eventDone();
      } else if (this.hasRegisteredForStellung(this.weiche)) {
         this.eventDone();
      } else {
         this.registerForStellung(this.weiche);
         this.my_main.reportElementOccurance(this.weiche, OCCU_KIND.HOOKED, "weichenausfall", this.code);
         eret = true;
      }

      return eret;
   }

   @Override
   public Vector getStructure() {
      Vector v = super.getStructure();
      v.addElement("weiche enr");
      v.addElement(Integer.toString(this.weiche.getENR()));
      v.addElement("weiche name");
      v.addElement(this.weiche.getElementName());
      return v;
   }

   @Override
   public void abort() {
      this.unregisterForStellung(this.weiche);
      this.eventDone();
      this.my_main.reportElementOccurance(this.weiche, OCCU_KIND.NORMAL, "weichenausfall", this.code);
      if (this.event_running) {
         if (this.gesperrtModus) {
            this.weiche.getFluentData().setGesperrt(false);
            this.my_main.reportElementOccurance(this.weiche, OCCU_KIND.UNLOCKED, "weichenausfall", this.code);
         } else {
            this.unregisterForZug(this.weiche);
            trigger t = new trigger() {
               @Override
               public boolean ping() {
                  if (!weichenausfall.this.weiche.getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE)) {
                     this.tjm_add();
                  }

                  return false;
               }
            };
            t.ping();
         }

         this.text = "Weiche " + this.weiche.getElementName() + " wieder einsatzbereit!";
         this.showMessageNow(this.text);
         this.event_running = false;
      }
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      if (f == null && st == g.getFluentData().getStellung() && random(0, 10) < 3) {
         return !this.event_running;
      } else {
         String call = this.getCallText();
         if (f != null) {
            this.text = "Achtung: Weiche <b>"
               + this.weiche.getElementName()
               + "</b> schickt keine Rückmeldung, Weichenstörung! Fahrstraße "
               + f.getName()
               + " kann nicht geschaltet werden!"
               + call;
         } else {
            this.text = "Achtung: Weiche <b>" + this.weiche.getElementName() + "</b> schickt keine Rückmeldung, Weichenstörung!" + call;
         }

         g.getFluentData().setStellung(gleisElements.ST_WEICHE_AUS);
         if (!this.event_running) {
            this.my_main.reportElementOccurance(this.weiche, OCCU_KIND.OCCURED, "weichenausfall", this.code);
            this.showMessageNow(this.text);
            this.registerForZug(g);
            this.waitingCallback = false;
            this.acceptingCall();
         } else {
            this.text = "Achtung: Weiche <b>"
               + this.weiche.getElementName()
               + "</b> schickt keine Rückmeldung, Weichenstörung! Reparatur wurde bereits in die Wege geleitet."
               + call;
         }

         this.event_running = true;
         return false;
      }
   }

   @Override
   public boolean hookStatus(gleis g, int s, zug z) {
      return s != 2;
   }

   @Override
   public void startCall(String token) {
      String ctext = this.funkName() + ": Danke für den Anruf, die Arbeiten beginnen sofort.";
      if (this.dauer > 5 && random(0, 10) >= 4) {
         this.waitingCallback = true;
         this.text = ctext + "<br><br>" + this.callbackText();
         this.showCallMessageNow(this.text);
      } else {
         this.showMessageNow(ctext + " Die Reparatur der Weiche " + this.weiche.getElementName() + " dauert dann noch " + this.dauer + " Minuten.");
         this.callMeIn(this.dauer);
      }
   }

   @Override
   public void clicked(String url) {
      if (this.waitingCallback) {
         if (url.equals("no")) {
            this.callMeIn(this.dauer);
            this.text = "Ok, die Reparatur der Weiche " + this.weiche.getElementName() + " dauert dann noch " + this.dauer + " Minuten.";
            this.showMessageNow(this.text);
         } else {
            int ndauer = random((int)((double)this.dauer * 1.1), this.dauer * 3);
            this.callMeIn(ndauer);
            this.text = "Ok, die Weiche " + this.weiche.getElementName() + " wird verriegelt, die Reparatur dauert dann noch " + ndauer + " Minuten.";
            this.showMessageNow(this.text);
            gleisElements.Stellungen sw = gleisElements.ST_WEICHE_GERADE;
            if (url.equals("yes_abzweig")) {
               sw = gleisElements.ST_WEICHE_ABZWEIG;
            }

            this.unregisterForZug(this.weiche);
            this.unregisterForStellung(this.weiche);
            this.weiche.getFluentData().setStellung(sw);
            this.weiche.getFluentData().setGesperrt(true);
            this.my_main.reportElementOccurance(this.weiche, OCCU_KIND.LOCKED, "weichenausfall", this.code);
            this.gesperrtModus = true;
            this.registerForStellung(this.weiche);
         }

         this.waitingCallback = false;
         this.registerCallBehaviour(null);
      }
   }

   private String callbackText() {
      return "Die Reparatur der Weiche "
         + this.weiche.getElementName()
         + " dauert noch etwas, wir könnten aber die Weiche auf eine Richtung verriegeln, so dass sie wenigstens befahren werden kann. Dadurch verzögert sich aber die endgültige Reparatur.<ul><li><a href='yes_gerade'>so machen, Weiche auf gerade verriegeln</a><li><a href='yes_abzweig'>so machen, Weiche auf abzweig verriegeln</a><li><a href='no'>nein, normale Reparatur</a></ul>";
   }

   @Override
   public boolean pong() {
      this.unregisterForStellung(this.weiche);
      if (this.gesperrtModus) {
         this.weiche.getFluentData().setGesperrt(false);
         this.my_main.reportElementOccurance(this.weiche, OCCU_KIND.UNLOCKED, "weichenausfall", this.code);
      } else {
         this.unregisterForZug(this.weiche);
         trigger t = new trigger() {
            @Override
            public boolean ping() {
               if (!weichenausfall.this.weiche.getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE)) {
                  this.tjm_add();
               }

               return false;
            }
         };
         t.ping();
      }

      this.text = "Weiche " + this.weiche.getElementName() + " wieder einsatzbereit!";
      this.showMessageNow(this.text);
      this.event_running = false;
      this.eventDone();
      this.my_main.reportElementOccurance(this.weiche, OCCU_KIND.NORMAL, "weichenausfall", this.code);
      return false;
   }

   @Override
   public String funkName() {
      return this.event_running ? this.weiche.getElementName() : null;
   }

   @Override
   public String funkAntwort() {
      if (this.waitingCallback) {
         return "Wir warten auf Antwort!";
      } else if (!this.isCalled()) {
         String call = this.getCallText();
         return "Weichenstörung " + this.weiche.getElementName() + "." + call;
      } else {
         return "Reparatur " + this.weiche.getElementName() + " dauert noch ca. " + (this.restTime() + 1) + " Minuten.";
      }
   }

   public gleis getWeiche() {
      return this.weiche;
   }
}
