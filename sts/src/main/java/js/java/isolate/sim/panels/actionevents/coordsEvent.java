package js.java.isolate.sim.panels.actionevents;

import js.java.tools.actions.AbstractStringEvent;

public class coordsEvent extends AbstractStringEvent {
   private final int x;
   private final int y;

   public coordsEvent(int _x, int _y) {
      super();
      this.x = _x;
      this.y = _y;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }
}
