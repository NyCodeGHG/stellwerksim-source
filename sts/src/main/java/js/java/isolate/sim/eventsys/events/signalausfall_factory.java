package js.java.isolate.sim.eventsys.events;

import js.java.isolate.sim.eventsys.event;

public class signalausfall_factory extends signalstoerung_factory {
   @Override
   public String getName() {
      return "Signalausfall";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return signalausfall.class;
   }

   @Override
   public String getDescription() {
      return "Ausfall eines Signals nach eine einstellbare Zeit in Grün-Stellung, per Ersatzsignal (ErsGT) freizufahren.";
   }
}
