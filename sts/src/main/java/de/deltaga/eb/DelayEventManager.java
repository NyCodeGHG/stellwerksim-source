package de.deltaga.eb;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class DelayEventManager {
   private final EventBus bus;
   private final ScheduledExecutorService executor;
   private final ConcurrentHashMap<String, Object> uniqueEvent = new ConcurrentHashMap();

   public DelayEventManager(EventBus bus) {
      this.bus = bus;
      this.executor = Executors.newScheduledThreadPool(1);
      bus.subscribe(this);
   }

   @EventHandler
   public void handle(final DelayEvent event) {
      this.executor.schedule(new Runnable() {
         public void run() {
            DelayEventManager.this.bus.publish(event.event);
         }
      }, (long)event.delay, event.unit);
   }

   @EventHandler
   public void handle(final UniqueDelayEvent event) {
      if (!this.uniqueEvent.containsKey(event.uniqueKey)) {
         this.uniqueEvent.put(event.uniqueKey, event.event);
         this.executor.schedule(new Runnable() {
            public void run() {
               DelayEventManager.this.uniqueEvent.remove(event.uniqueKey);
               DelayEventManager.this.bus.publish(event.event);
            }
         }, (long)event.delay, event.unit);
      }
   }
}
