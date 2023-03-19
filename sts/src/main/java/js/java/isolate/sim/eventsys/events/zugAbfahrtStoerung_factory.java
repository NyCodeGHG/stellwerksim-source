package js.java.isolate.sim.eventsys.events;

import java.util.EnumSet;
import js.java.isolate.sim.eventsys.event;

public class zugAbfahrtStoerung_factory extends zugStoerungBaseFactory {
   public zugAbfahrtStoerung_factory() {
      super(
         EnumSet.of(
            zugStoerungBaseFactory.USEDFIELDS.BAHNSTEIG,
            zugStoerungBaseFactory.USEDFIELDS.DAUER,
            zugStoerungBaseFactory.USEDFIELDS.ZUGANTEIL,
            zugStoerungBaseFactory.USEDFIELDS.ZUGNAME,
            zugStoerungBaseFactory.USEDFIELDS.TEXT,
            zugStoerungBaseFactory.USEDFIELDS.SILENT
         )
      );
   }

   @Override
   public String getName() {
      return "Abfahrtprobleme";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return zugAbfahrtStoerung.class;
   }

   @Override
   public String getDescription() {
      return "Verzögert Abfahrt am Bahnsteig";
   }
}
