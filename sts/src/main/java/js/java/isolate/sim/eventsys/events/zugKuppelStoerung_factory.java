package js.java.isolate.sim.eventsys.events;

import java.util.EnumSet;
import js.java.isolate.sim.eventsys.event;

public class zugKuppelStoerung_factory extends zugStoerungBaseFactory {
   public zugKuppelStoerung_factory() {
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
      return "Kuppelprobleme";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return zugKuppelStoerung.class;
   }

   @Override
   public String getDescription() {
      return "Verzögert Kupplung am Bahnsteig";
   }
}
