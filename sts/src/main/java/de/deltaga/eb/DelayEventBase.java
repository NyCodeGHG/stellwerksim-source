package de.deltaga.eb;

import java.util.concurrent.TimeUnit;

public class DelayEventBase {
   public final Object event;
   public final int delay;
   public final TimeUnit unit;

   protected DelayEventBase(Object event, int seconds) {
      this(event, seconds, TimeUnit.SECONDS);
   }

   protected DelayEventBase(Object event, int delay, TimeUnit unit) {
      super();
      this.event = event;
      this.delay = delay;
      this.unit = unit;
   }
}
