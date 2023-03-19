package de.deltaga.eb;

import java.util.EventObject;

public class BusExceptionEvent extends EventObject {
   private static final long serialVersionUID = 1L;
   private final Throwable cause;
   private final Object event;

   public BusExceptionEvent(Object subscriber, Throwable cause, Object event) {
      super(subscriber);
      this.cause = cause;
      this.event = event;
   }

   public Object getSubscriber() {
      return this.getSource();
   }

   public Object getEvent() {
      return this.event;
   }

   public Throwable getCause() {
      return this.cause;
   }
}
