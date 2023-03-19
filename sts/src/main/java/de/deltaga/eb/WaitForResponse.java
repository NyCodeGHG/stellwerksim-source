package de.deltaga.eb;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WaitForResponse<E, R> {
   private R result = (R)null;
   private final Method m;
   private final Class<R> clazz;
   private final BiFunction<E, R, Boolean> validator;
   private final EventBus bus;
   private final E event;

   public WaitForResponse(EventBus bus, E event, Class<R> clazz, BiFunction<E, R, Boolean> validator) {
      super();
      this.clazz = clazz;
      this.validator = validator;
      this.bus = bus;
      this.event = event;

      try {
         this.m = this.getClass().getMethod("eventReceiver", Object.class);
      } catch (SecurityException | NoSuchMethodException var6) {
         Logger.getLogger(WaitForResponse.class.getName()).log(Level.SEVERE, null, var6);
         throw new RuntimeException(var6);
      }
   }

   public WaitForResponse(EventBus bus, E event, Class<R> clazz) {
      this(bus, event, clazz, (e, r) -> true);
   }

   public R sendAndWait() throws InterruptedException {
      this.bus.subscribe(this, this.m, this.clazz);

      try {
         synchronized(this) {
            this.bus.publish(this.event);
            this.wait();
         }
      } finally {
         this.bus.unsubscribe(this);
      }

      return this.result;
   }

   public R sendAndWait(long timeout, TimeUnit unit) throws InterruptedException {
      this.bus.subscribe(this, this.m, this.clazz);

      try {
         synchronized(this) {
            this.bus.publish(this.event);
            this.wait(unit.toMillis(timeout));
         }
      } finally {
         this.bus.unsubscribe(this);
      }

      return this.result;
   }

   protected void received(R result) {
      this.result = result;
   }

   @EventHandler
   public void eventReceiver(Object event) {
      if (this.validator.apply(this.event, event)) {
         this.received((R)event);
      }
   }
}
