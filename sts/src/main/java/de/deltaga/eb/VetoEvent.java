package de.deltaga.eb;

import java.util.EventObject;

public class VetoEvent extends EventObject {
   private static final long serialVersionUID = 1L;

   public VetoEvent(Object event) {
      super(event);
   }
}
