package de.deltaga.eb;

import java.lang.reflect.Method;

public final class EventBusService {
   private static final EventBus eventBus = new BasicEventBus();

   public EventBusService() {
      super();
   }

   public static EventBus getInstance() {
      return eventBus;
   }

   public static void subscribe(Object subscriber) {
      eventBus.subscribe(subscriber);
   }

   public static void subscribe(Object subscriber, Method handler, Class<?> eventtype) {
      eventBus.subscribe(subscriber, handler, eventtype);
   }

   public static void unsubscribe(Object subscriber) {
      eventBus.unsubscribe(subscriber);
   }

   public static void publish(Object event) {
      eventBus.publish(event);
   }

   public static boolean hasPendingEvents() {
      return eventBus.hasPendingEvents();
   }

   public static void registerTypeListener(Class<?> eventtype, EventPublishListener listener) {
      eventBus.registerTypeListener(eventtype, listener);
   }
}
