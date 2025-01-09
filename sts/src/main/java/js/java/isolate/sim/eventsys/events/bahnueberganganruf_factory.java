package js.java.isolate.sim.eventsys.events;

import javax.swing.JSpinner;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;

public class bahnueberganganruf_factory extends eventFactory {
   private JSpinner min;
   private JSpinner max;
   private JSpinner center;

   @Override
   public String getName() {
      return "Bahnüberganganruf";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return bahnueberganganruf.class;
   }

   @Override
   protected void initGui() {
   }

   @Override
   public void showContainer(eventContainer ev, boolean editmode) {
      super.showContainer(ev, editmode);
   }

   @Override
   public void readContainer(eventContainer ev) {
      super.readContainer(ev);
   }

   @Override
   public String getDescription() {
      return "Legt Häufigkeit und Dauer der Anrufe eines Anrufbahnübergangs fest.";
   }

   @Override
   public boolean isIndependantEvent() {
      return true;
   }
}
