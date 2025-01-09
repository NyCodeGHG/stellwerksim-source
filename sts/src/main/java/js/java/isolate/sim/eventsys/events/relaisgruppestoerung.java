package js.java.isolate.sim.eventsys.events;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Vector;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.gleisevent;
import js.java.isolate.sim.eventsys.signalevent;
import js.java.isolate.sim.eventsys.weicheevent;
import js.java.isolate.sim.gleis.gleis;

public class relaisgruppestoerung extends gleisevent {
   protected int totalEvents;
   protected int level;
   protected int repdauer;
   private final HashSet<gleis> defektgl = new HashSet();
   private final String text = "";

   public relaisgruppestoerung(Simulator sim) {
      super(sim);
   }

   @Override
   protected boolean init(eventContainer e) {
      this.totalEvents = 0;
      this.level = 2;
      this.repdauer = e.getIntValue("dauer");
      boolean stark = e.getBoolValue("stark", true);
      LinkedList<gleis> ll = this.glbModel.findAllWithZugLevel(this.level, new Object[]{gleis.ELEMENT_SIGNAL});
      if (stark) {
         this.filterHeavyStellung(ll);
      }

      if (!ll.isEmpty()) {
         Collections.shuffle(ll);
         int cnt = 8;

         gleis gl;
         do {
            int dauer = random(6, 15);
            gl = (gleis)ll.pollFirst();
            if (gl != null
               && !gl.hasHook(eventGenerator.HOOKKIND.WORKER, eventGenerator.T_GLEIS_STATUS)
               && !gl.hasHook(eventGenerator.HOOKKIND.WORKER, eventGenerator.T_GLEIS_STELLUNG)
               && !gl.getFluentData().isGesperrt()) {
               if (cnt < 3) {
                  signalevent ret = null;
                  int r = random(0, 30);
                  if (r < 10) {
                     ret = new signalstoerung(this.my_main);
                     ret.setParent(this);
                  } else if (r < 15) {
                     ret = new signalausfall(this.my_main, true);
                  } else {
                     ret = new signalmeldung(this.my_main);
                     ret.setParent(this);
                  }

                  ret.glbModel = this.glbModel;
                  if (ret.init(gl.getENR(), dauer) && !(ret instanceof signalausfall)) {
                     this.totalEvents++;
                     this.defektgl.add(gl);
                  }
               } else {
                  this.defektgl.add(gl);
               }

               if (--cnt < 0) {
                  break;
               }
            }
         } while (gl != null);
      }

      ll = this.glbModel.findAllWithStellungLevel(this.level, new Object[]{gleis.ALLE_WEICHEN});
      if (stark) {
         this.filterHeavyZug(ll);
      }

      if (!ll.isEmpty()) {
         Collections.shuffle(ll);
         int cnt = 6;

         gleis gl;
         do {
            int dauer = random(6, 10);
            gl = (gleis)ll.pollFirst();
            if (gl != null
               && !gl.hasHook(eventGenerator.HOOKKIND.WORKER, eventGenerator.T_GLEIS_STATUS)
               && !gl.hasHook(eventGenerator.HOOKKIND.WORKER, eventGenerator.T_GLEIS_STELLUNG)
               && !gl.getFluentData().isGesperrt()) {
               if (cnt < 2) {
                  weicheevent retx = new weichenausfall(this.my_main);
                  retx.glbModel = this.glbModel;
                  retx.setParent(this);
                  if (retx.init(gl.getENR(), dauer)) {
                     this.totalEvents++;
                     this.defektgl.add(gl);
                  }
               } else {
                  this.defektgl.add(gl);
               }

               if (--cnt < 0) {
                  break;
               }
            }
         } while (gl != null);
      }

      if (this.totalEvents <= 0) {
         this.eventDone();
      }

      return this.totalEvents > 0;
   }

   @Override
   public Vector getStructure() {
      Vector v = super.getStructure();
      v.addElement("totelEvents");
      v.addElement(Integer.toString(this.totalEvents));
      v.addElement("Elemente");
      v.addElement(this.defektgl.size());
      return v;
   }

   @Override
   public void abort() {
      this.eventDone();
   }

   @Override
   public void done(event child) {
      if (!this.isEventDone()) {
         this.totalEvents--;
         if (this.totalEvents <= 0) {
            eventContainer ev = new eventContainer(this.glbModel, sperreelementeaufzeit.class);
            ev.setIntValue("dauer", this.repdauer);
            ev.setValue(
               "text", "Wir haben einen Defekt in einigen Relaisgruppen festgestellt, die Ursache der vielen Ausfälle war. Diese werden wir austauschen."
            );
            ev.setGleisList(this.defektgl);
            ev.setName("Relaisreparatur");
            event ee = event.createEvent(ev, this.glbModel, this.my_main);
            if (ee != null) {
               this.finishIn(10);
            }
         }
      }
   }

   @Override
   public String funkName() {
      return null;
   }

   @Override
   public String funkAntwort() {
      return "Relaisreparatur dauert noch mindestens " + (this.restTime() + 1) + " Minuten.";
   }

   @Override
   public String getText() {
      return "";
   }
}
