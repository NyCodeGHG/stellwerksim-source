package de.deltaga.eb;

import java.util.concurrent.TimeUnit;

public class DelayEvent extends DelayEventBase {
   public DelayEvent(Object event, int seconds) {
      super(event, seconds);
   }

   public DelayEvent(Object event, int delay, TimeUnit unit) {
      super(event, delay, unit);
   }
}
