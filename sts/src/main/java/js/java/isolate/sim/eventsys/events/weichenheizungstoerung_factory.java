package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;

public class weichenheizungstoerung_factory extends eventFactory {
   @Override
   public String getName() {
      return "Weichen-Heizung-Störung";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return weichenheizungstoerung.class;
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
      return "Weichen müssen regelmäßig bewegt werden.";
   }

   @Override
   public boolean isIndependantEvent() {
      return true;
   }
}
