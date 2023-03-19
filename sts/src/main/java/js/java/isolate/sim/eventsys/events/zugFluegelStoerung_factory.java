package js.java.isolate.sim.eventsys.events;

import java.util.EnumSet;
import js.java.isolate.sim.eventsys.event;

public class zugFluegelStoerung_factory extends zugStoerungBaseFactory {
   public zugFluegelStoerung_factory() {
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
      return "Flügelprobleme";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return zugFluegelStoerung.class;
   }

   @Override
   public String getDescription() {
      return "Verzögert Flügeln am Bahnsteig";
   }
}
