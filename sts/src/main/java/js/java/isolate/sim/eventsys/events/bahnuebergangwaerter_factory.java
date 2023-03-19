package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;

public class bahnuebergangwaerter_factory extends eventFactory {
   public bahnuebergangwaerter_factory() {
      super();
   }

   @Override
   public String getName() {
      return "Bahnübergangwärter";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return bahnuebergangwaerter.class;
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
      return "BÜ Wärter.";
   }

   @Override
   public boolean isIndependantEvent() {
      return true;
   }
}
