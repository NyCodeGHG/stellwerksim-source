package js.java.isolate.sim.gleisbild;

import java.awt.event.MouseEvent;
import js.java.isolate.sim.gleis.gleis;
import js.java.tools.actions.AbstractEvent;

public class CoordinatesEvent extends AbstractEvent<gleis> {
   private final MouseEvent mevent;

   public CoordinatesEvent(gleis gl, MouseEvent e) {
      super(gl);
      this.mevent = e;
   }

   public MouseEvent getMouseEvent() {
      return this.mevent;
   }

   public gleis getGleis() {
      return (gleis)this.getSource();
   }

   public int getX() {
      try {
         return ((gleis)this.getSource()).getCol();
      } catch (NullPointerException var2) {
         return 0;
      }
   }

   public int getY() {
      try {
         return ((gleis)this.getSource()).getRow();
      } catch (NullPointerException var2) {
         return 0;
      }
   }
}
