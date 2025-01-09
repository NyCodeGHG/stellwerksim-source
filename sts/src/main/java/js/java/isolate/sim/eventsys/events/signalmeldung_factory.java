package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;

public class signalmeldung_factory extends signalstoerung_factory {
   @Override
   public String getName() {
      return "Signalmelderstörung";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return signalmeldung.class;
   }

   @Override
   public String getDescription() {
      return "Ausfall eines Signals für eine einstellbare Zeit, Ersatzsignal (ErsGT) in 50% möglich, beim Anlegen einer Fahrstraße gemeldet.";
   }
}
