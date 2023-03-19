package de.deltaga.eb;

public abstract class EventFinishedEventBase implements FollowUpEventKeeper {
   public EventFinishedEventBase() {
      super();
   }

   @Override
   public Object getFollowUpEvent() {
      return new EventFinishedEventBase.FinishedEventSend(this);
   }

   public void sendAndWait(EventBus bus) throws InterruptedException {
      bus.subscribe(this);

      try {
         synchronized(this) {
            bus.publish(this);
            this.wait();
         }
      } finally {
         bus.unsubscribe(this);
      }
   }

   @EventHandler
   public void waitEventReceiver(EventFinishedEventBase.FinishedEventSend event) {
      if (event.id == this) {
         synchronized(this) {
            this.notify();
         }
      }
   }

   public static class FinishedEventSend {
      public final EventFinishedEventBase id;

      private FinishedEventSend(EventFinishedEventBase id) {
         super();
         this.id = id;
      }
   }
}
