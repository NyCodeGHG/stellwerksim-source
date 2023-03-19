package js.java.isolate.sim.eventsys.events;

import java.util.LinkedList;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.gleisevent;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.element_list;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.schaltungen.chatcomng.OCCU_KIND;
import js.java.schaltungen.timesystem.TimeFormat;

public class sperreelementeaufzeit extends gleisevent {
   private static final String NAME = "sperreelementeaufzeit";
   private eventContainer ec;
   private String text = null;
   private int delay;
   private int resttime;
   private sperreelementeaufzeit.STEPS step;
   private long starttime;
   private long nametime;
   private String glstring;
   private final LinkedList<gleis> sperrgleise = new LinkedList();
   private static final element SPERRGLEISE = new element_list(gleis.ALLE_STARTSIGNALE, gleis.ELEMENT_EINFAHRT, gleis.ALLE_WEICHEN);

   public sperreelementeaufzeit(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      this.ec = e;
      this.delay = this.ec.getIntValue("dauer");
      boolean fast = this.ec.getBoolValue("schnell", false);
      this.resttime = this.delay;
      this.sperrgleise.clear();
      TreeSet<String> locktext = new TreeSet();

      for(gleis g : this.ec.getGleisList()) {
         if (SPERRGLEISE.matches(g.getElement())) {
            this.sperrgleise.add(g);
            locktext.add(g.getElementName());
         }
      }

      this.glstring = "";

      for(String n : locktext) {
         if (!this.glstring.isEmpty()) {
            this.glstring = this.glstring + ", ";
         }

         this.glstring = this.glstring + n;
      }

      int f = 15;
      if (fast) {
         f = 4;
      }

      long st = this.my_main.getSimutime() + (long)f * 60000L;
      TimeFormat tf = TimeFormat.getInstance(TimeFormat.STYLE.HM);
      this.nametime = st;
      this.text = String.format(
         "Achtung! Sperrung ab %s Uhr für ca. %d Minuten. %s<br><br> Folgende Signale und Weichen müssen gesperrt werden:<br>%s<br>Bitte die Strecke räumen! - <b href='show'>Elemente kurzzeitig darstellen</a> -",
         tf.format(st),
         this.resttime,
         this.ec.getValue("text"),
         this.glstring
      );
      if (fast) {
         this.showWarning(sperreelementeaufzeit.STEPS.SETHOOKS, true);
      } else {
         this.showWarning(sperreelementeaufzeit.STEPS.WARNING1, true);
      }

      return true;
   }

   @Override
   public boolean pong() {
      boolean ret = false;
      if (this.step != sperreelementeaufzeit.STEPS.DONE) {
         switch(this.step) {
            case WARNING1:
               this.showWarning(sperreelementeaufzeit.STEPS.WARNING2, false);
               break;
            case WARNING2:
               this.showWarning(sperreelementeaufzeit.STEPS.WARNING3, false);
               break;
            case WARNING3:
               this.showWarning(sperreelementeaufzeit.STEPS.SETHOOKS, true);
               break;
            case SETHOOKS:
               this.my_main.reportOccurance(this.getCode(), OCCU_KIND.OCCURED, "sperreelementeaufzeit", this.code);
               this.setHooks();
               ret = true;
               break;
            case WAIT4ROT:
               this.wait4Rot();
               ret = true;
               break;
            case WAIT4DELAY:
               this.wait4Delay();
               break;
            case FINISH:
               this.reenableAll();
               ret = true;
         }
      }

      return ret;
   }

   @Override
   public void abort() {
      this.resttime = 0;
      this.step = sperreelementeaufzeit.STEPS.FINISH;
   }

   @Override
   public String funkName() {
      return this.text != null ? "Sperrung " + this.my_main.getSimutimeString(this.nametime) : null;
   }

   @Override
   public String funkAntwort() {
      return "Dauert noch mindestens " + (this.resttime + 1) + " Minuten.<br><br>" + this.text;
   }

   private void showWarning(sperreelementeaufzeit.STEPS nextStep, boolean showMsg) {
      this.step = nextStep;
      this.callMeIn(4);
      if (showMsg) {
         if (this.text.contains("href")) {
            this.showCallMessageNow(this.text);
         } else {
            this.showMessageNow(this.text);
         }
      }
   }

   @Override
   public void clicked(String url) {
      for(gleis g : this.sperrgleise) {
         g.setHightlight(true);
      }

      this.showCallMessageNow(this.text);
   }

   private void setHooks() {
      for(gleis g : this.sperrgleise) {
         if (g.getElement() == gleis.ELEMENT_SIGNAL) {
            if (g.getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT) {
               g.getFluentData().setGesperrt(true);
               this.my_main.reportElementOccurance(g, OCCU_KIND.LOCKED, "sperreelementeaufzeit", this.code);
            } else {
               this.registerForStellung(g);
            }
         } else if (g.getElement() == gleis.ELEMENT_EINFAHRT) {
            String a = this.ec.getValue(g, "details");
            if (a != null) {
               try {
                  int enr = Integer.parseInt(a);
                  gleis eg = this.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_EINFAHRT});
                  if (eg != null) {
                     g.getFluentData().setEinfahrtUmleitung(eg);
                  }
               } catch (Exception var6) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "init", var6);
               }
            }
         }
      }

      this.showMessageNow(
         "Wir beginnen jetzt mit der Streckensperrung! Rote Signale werden von uns bis zum Ende der Sperrung verriegelt!<br><br>Folgende Signale und Weichen müssen gesperrt werden:<br>"
            + this.glstring
      );
      this.step = sperreelementeaufzeit.STEPS.WAIT4ROT;
      this.glbModel.repaint();
      this.callMeIn(1);
   }

   private void wait4Rot() {
      boolean allLocked = true;

      for(gleis g : this.sperrgleise) {
         if (g.getElement() == gleis.ELEMENT_SIGNAL) {
            allLocked &= g.getFluentData().isGesperrt();
            if (!g.getFluentData().isGesperrt() && g.getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT && g.getFluentData().getStatus() == 0) {
               g.getFluentData().setGesperrt(true);
               this.my_main.reportElementOccurance(g, OCCU_KIND.LOCKED, "sperreelementeaufzeit", this.code);
            }
         }

         if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
            allLocked &= !g.getFluentData().hasCurrentFS();
         }
      }

      if (allLocked) {
         for(gleis g : this.sperrgleise) {
            if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
               String sw = this.ec.getValue(g, "details");
               if (sw != null) {
                  g.getFluentData().setStellung(gleisElements.Stellungen.string2stellung(sw));
               }
            }

            g.getFluentData().setGesperrt(true);
            this.my_main.reportElementOccurance(g, OCCU_KIND.LOCKED, "sperreelementeaufzeit", this.code);
         }

         this.step = sperreelementeaufzeit.STEPS.WAIT4DELAY;
         this.showMessageNow("Sperrung ist vollständig.");
         this.starttime = this.my_main.getSimutime();
      }

      this.callMeIn(1);
   }

   private void wait4Delay() {
      this.callMeIn(1);
      long rtime = (this.my_main.getSimutime() - this.starttime) / 60000L;
      this.resttime = (int)((long)this.delay - rtime);
      if (this.resttime <= 0) {
         this.resttime = 0;
         this.step = sperreelementeaufzeit.STEPS.FINISH;
      }
   }

   private void reenableAll() {
      this.showMessageNow("Streckensperrung beendet!");

      for(gleis g : this.sperrgleise) {
         this.unregisterForStellung(g);
         g.getFluentData().setGesperrt(false);
         this.my_main.reportElementOccurance(g, OCCU_KIND.UNLOCKED, "sperreelementeaufzeit", this.code);
         if (g.getElement() == gleis.ELEMENT_EINFAHRT) {
            g.getFluentData().setEinfahrtUmleitung(null);
         }
      }

      this.step = sperreelementeaufzeit.STEPS.DONE;
      this.text = null;
      this.my_main.reportOccurance(this.getCode(), OCCU_KIND.NORMAL, "sperreelementeaufzeit", this.code);
      this.eventDone();
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      if (st == gleisElements.ST_SIGNAL_ROT) {
         g.getFluentData().setGesperrt(true);
         this.my_main.reportElementOccurance(g, OCCU_KIND.LOCKED, "sperreelementeaufzeit", this.code);
         this.wait4Rot();
      }

      return true;
   }

   private static enum STEPS {
      WARNING1,
      WARNING2,
      WARNING3,
      SETHOOKS,
      WAIT4ROT,
      WAIT4DELAY,
      FINISH,
      DONE;
   }
}
