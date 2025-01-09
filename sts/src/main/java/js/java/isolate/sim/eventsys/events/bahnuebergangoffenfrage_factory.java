package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;

public class bahnuebergangoffenfrage_factory extends eventFactory {
   @Override
   public String getName() {
      return "Bahnübergangoffenfrage";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return bahnuebergangoffenfrage.class;
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
      return "Anruf nach 10 Minuten BÜ zu.";
   }

   @Override
   public boolean isIndependantEvent() {
      return true;
   }
}
