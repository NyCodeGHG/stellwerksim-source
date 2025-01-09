package js.java.isolate.sim.eventsys.events;

import java.util.Iterator;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.anrufbueevent;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;

public class bahnueberganganruf extends anrufbueevent {
   private gleis bü;
   private int büenr;
   private String text = null;
   private int mindauer;
   private int centerdauer;
   private int maxdauer;
   private int rnddauer;
   private boolean event_running = false;
   private boolean wasopened = false;
   private int callcnt = 0;

   public bahnueberganganruf(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return this.text;
   }

   @Override
   protected boolean init(eventContainer e) {
      boolean eret = false;
      this.büenr = e.getENR();
      this.bü = this.glbModel.findFirst(new Object[]{this.büenr, gleis.ELEMENT_ANRUFÜBERGANG});
      if (this.bü == null) {
         this.eventDone();
      } else if (this.bü.getFluentData().getStellung() == gleisElements.ST_ANRUFÜBERGANG_OFFEN) {
         this.eventDone();
      } else {
         this.text = "Hallo, ich stehe hier am Bahnübergang und möchte rüberfahren, bitte die Schranke öffnen. (" + this.bü.getElementName() + ")";
         this.showCallMessageNow(this.text);
         this.event_running = true;
         this.callcnt = 0;
         this.callMeIn(3);
         Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_ANRUFÜBERGANG, this.büenr});

         while (it.hasNext()) {
            this.registerForStellung((gleis)it.next());
         }

         eret = true;
      }

      return eret;
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      if (st == gleisElements.ST_ANRUFÜBERGANG_OFFEN) {
         this.event_running = false;
         Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_ANRUFÜBERGANG, this.büenr});

         while (it.hasNext()) {
            this.unregisterForStellung((gleis)it.next());
         }

         this.resetTimer();
         this.eventDone();
      }

      return true;
   }

   @Override
   public boolean pong() {
      if (this.event_running) {
         if (this.bü.getFluentData().getStellung() == gleisElements.ST_ANRUFÜBERGANG_OFFEN) {
            this.event_running = false;
            Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_ANRUFÜBERGANG, this.büenr});

            while (it.hasNext()) {
               this.unregisterForStellung((gleis)it.next());
            }

            this.eventDone();
         } else {
            this.callcnt++;
            if (this.callcnt >= 5) {
               this.text = "Du Blödmann, dann drücke ich eben die Schranke selbst auf! (" + this.bü.getElementName() + ")";
               this.showCallMessageNow(this.text);
               this.event_running = false;
               Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_ANRUFÜBERGANG, this.büenr});

               while (it.hasNext()) {
                  this.unregisterForStellung((gleis)it.next());
               }

               this.eventDone();
               it = this.glbModel.findIterator(new Object[]{this.büenr, gleis.ELEMENT_ANRUFÜBERGANG});

               while (it.hasNext()) {
                  gleis bügl = (gleis)it.next();
                  fahrstrasse fs = bügl.getFluentData().getCurrentFS();
                  if (fs != null) {
                     this.my_main.getFSallocator().getFS(fs, fsAllocs.ALLOCM_FREE);
                  }
               }

               eventContainer ev = new eventContainer(this.glbModel, bahnuebergangstoerung.class);
               ev.setGleis(this.bü);
               ev.setValue("dauer", 10);
               event.createEvent(ev, this.glbModel, this.my_main);
            } else {
               this.text = "HEY HALLO! Ich stehe immernoch hier am Bahnübergang und möchte rüberfahren! Bitte mal die Schranke öffnen. ("
                  + this.bü.getElementName()
                  + ")";
               this.showCallMessageNow(this.text);
               this.callMeIn(3);
            }
         }
      }

      return false;
   }
}
