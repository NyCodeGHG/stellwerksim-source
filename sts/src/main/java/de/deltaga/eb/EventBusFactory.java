package de.deltaga.eb;

@Deprecated
public class EventBusFactory {
   private static EventBus instance;

   public EventBusFactory() {
      super();
   }

   public static EventBus newEventBus(Class<? extends EventBus> eventBusClass) throws InstantiationException, IllegalAccessException {
      return (EventBus)eventBusClass.newInstance();
   }

   public static EventBus newEventBus(String eventBusClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      return newEventBus(Class.forName(eventBusClassName));
   }

   public static EventBus newEventBus() {
      try {
         return newEventBus(BasicEventBus.class);
      } catch (Exception var1) {
         var1.printStackTrace();
         return null;
      }
   }

   public static synchronized EventBus getEventBus() {
      if (instance == null) {
         instance = newEventBus();
      }

      return instance;
   }
}
