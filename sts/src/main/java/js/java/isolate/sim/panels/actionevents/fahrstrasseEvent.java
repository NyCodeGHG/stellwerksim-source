package js.java.isolate.sim.panels.actionevents;

import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.tools.actions.AbstractEvent;

public class fahrstrasseEvent extends AbstractEvent<fahrstrasse> {
   public static final int CM_SHOW = 0;
   public static final int CM_DEL = 1;
   public static final int CM_CHANGED = 2;
   public static final int CM_MODIFIED = 3;
   private final int cmd;

   public fahrstrasseEvent(fahrstrasse m, int command) {
      super(m);
      this.cmd = command;
   }

   public fahrstrasse getFS() {
      return (fahrstrasse)this.getSource();
   }

   public int getCommand() {
      return this.cmd;
   }
}
