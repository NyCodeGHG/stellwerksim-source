package js.java.isolate.sim.eventsys.events;

import java.util.EnumSet;
import js.java.isolate.sim.eventsys.event;

public class randomzugstoerung_factory extends zugStoerungBaseFactory {
   public randomzugstoerung_factory() {
      super(
         EnumSet.of(
            zugStoerungBaseFactory.USEDFIELDS.DAUER,
            zugStoerungBaseFactory.USEDFIELDS.ZUGANTEIL,
            zugStoerungBaseFactory.USEDFIELDS.ZUGNAME,
            zugStoerungBaseFactory.USEDFIELDS.SILENT
         )
      );
   }

   @Override
   public String getName() {
      return "Zufällige Zugstörung";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return randomzugstoerung.class;
   }

   @Override
   public String getDescription() {
      return "eine der Zugstörungen";
   }
}
