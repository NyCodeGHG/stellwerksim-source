package js.java.isolate.sim.eventsys.events;

import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventFactory;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;

public class abort_factory extends eventFactory {
   @Override
   public String getName() {
      return "Störungen abbrechen";
   }

   @Override
   public Class<? extends event> getEventTyp() {
      return null;
   }

   @Override
   protected void initGui() {
   }

   @Override
   public String getDescription() {
      return "Störungen abbrechen";
   }

   @Override
   public boolean serverEvent(eventContainer ev, gleisbildModelEventsys glb, String parameter) {
      try {
         if (!parameter.isEmpty()) {
            if (parameter.startsWith("0x")) {
               int hash = Integer.parseInt(parameter.substring(2), 16);

               for (event e : event.events) {
                  if (e.getHash() == hash) {
                     e.abort();
                     break;
                  }
               }
            } else {
               for (event ex : event.events) {
                  if (parameter.equalsIgnoreCase(ex.getClass().getSimpleName())) {
                     ex.abort();
                  }
               }
            }
         } else {
            for (event exx : event.events) {
               exx.abort();
            }
         }
      } catch (Exception var7) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "catch ", var7);
      }

      return false;
   }
}
