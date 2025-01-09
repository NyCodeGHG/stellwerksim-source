package js.java.isolate.sim.eventsys.events;

import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class sperreelemente extends event {
   private static final String NAME = "sperreelemente";
   private eventContainer ec;
   private String text = null;
   private boolean event_running = false;

   public sperreelemente(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      this.ec = e;
      long startt = this.ec.getLongValue("starthour");
      long stopt = this.ec.getLongValue("stophour");
      long st = this.my_main.getSimutime();
      long h = st / 3600000L;
      if (eventContainer.getDebug() != null) {
         eventContainer.getDebug().writeln("SP: " + startt + " < " + h + " < " + stopt);
      }

      if (startt < h && h < stopt) {
         if (eventContainer.getDebug() != null) {
            eventContainer.getDebug().writeln("Sperre");
         }

         for (gleis g : e.getGleisList()) {
            if (g.getElement() == gleis.ELEMENT_SIGNAL
               || g.getElement() == gleis.ELEMENT_WEICHEOBEN
               || g.getElement() == gleis.ELEMENT_WEICHEUNTEN
               || g.getElement() == gleis.ELEMENT_AUSFAHRT
               || g.getElement() == gleis.ELEMENT_EINFAHRT) {
               if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
                  String sw = e.getValue(g, "details");
                  if (sw != null) {
                     g.getFluentData().setStellung(gleisElements.Stellungen.string2stellung(sw));
                  }
               } else if (g.getElement() == gleis.ELEMENT_EINFAHRT) {
                  String a = e.getValue(g, "details");
                  if (a != null) {
                     try {
                        int enr = Integer.parseInt(a);
                        gleis eg = this.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_EINFAHRT});
                        if (eg != null) {
                           if (eventContainer.getDebug() != null) {
                              eventContainer.getDebug().writeln("Einf-Um: " + g.getENR() + " -> " + enr);
                           }

                           g.getFluentData().setEinfahrtUmleitung(eg);
                        }
                     } catch (Exception var15) {
                        Logger.getLogger("stslogger").log(Level.SEVERE, "init", var15);
                     }
                  }
               }

               g.getFluentData().setGesperrt(true);
               this.my_main.reportElementOccurance(g, OCCU_KIND.LOCKED, "sperreelemente", this.code);
               if (eventContainer.getDebug() != null) {
                  eventContainer.getDebug().writeln("sperre: " + g.getENR());
               }
            }
         }

         this.text = e.getValue("text");
         if (this.text != null && !this.text.isEmpty()) {
            this.showMessageNow(this.text);
         }

         this.glbModel.repaint();
         this.event_running = true;
      }

      return this.event_running;
   }

   @Override
   public boolean pong() {
      return false;
   }

   @Override
   public String funkName() {
      return this.event_running ? "Sperrung " + this.ec.getName() : null;
   }

   @Override
   public String funkAntwort() {
      return "Dauert bis mindestens " + this.ec.getLongValue("stophour") + " Uhr.<br><br>" + this.text;
   }
}
