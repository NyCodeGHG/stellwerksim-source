package js.java.isolate.sim.eventsys.events;

import java.util.EnumSet;
import js.java.isolate.sim.eventsys.event;

public class zugGruenStoerung_factory extends zugStoerungBaseFactory {
   public zugGruenStoerung_factory() {
      super(
         EnumSet.of(
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
      return "Nach rot Abfahrt Problem";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return zugGruenStoerung.class;
   }

   @Override
   public String getDescription() {
      return "Verzögert Abfahrt nach einem Halt am roten Signal";
   }
}
