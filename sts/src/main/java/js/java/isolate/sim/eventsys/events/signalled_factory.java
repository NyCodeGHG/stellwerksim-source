package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;

public class signalled_factory extends signalstoerung_factory {
   public signalled_factory() {
      super();
   }

   @Override
   public String getName() {
      return "Signal-Licht-Störung";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return signalled.class;
   }

   @Override
   public String getDescription() {
      return "Grünlicht Ersatzdraht, Totalausfall wenn nicht repariert.";
   }
}
