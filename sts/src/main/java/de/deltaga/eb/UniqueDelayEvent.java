package de.deltaga.eb;

import java.util.concurrent.TimeUnit;

public class UniqueDelayEvent extends DelayEventBase {
   public final String uniqueKey;

   public UniqueDelayEvent(Object event, int seconds, String uniqueKey) {
      super(event, seconds);
      this.uniqueKey = uniqueKey;
   }

   public UniqueDelayEvent(Object event, int delay, TimeUnit unit, String uniqueKey) {
      super(event, delay, unit);
      this.uniqueKey = uniqueKey;
   }
}
