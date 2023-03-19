package js.java.tools.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WindowSnapper extends ComponentAdapter {
   private boolean locked = false;
   private int snap_distance = 30;
   private final int distance;

   public WindowSnapper() {
      this(0);
   }

   public WindowSnapper(int distance) {
      super();
      this.distance = distance;
   }

   public void componentMoved(ComponentEvent evt) {
      if (!this.locked) {
         Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
         int nx = evt.getComponent().getX();
         int ny = evt.getComponent().getY();
         if (ny < this.distance + this.snap_distance) {
            ny = this.distance;
         }

         if (nx < this.distance + this.snap_distance) {
            nx = this.distance;
         }

         if ((double)nx > size.getWidth() - (double)evt.getComponent().getWidth() - (double)this.snap_distance - (double)this.distance) {
            nx = (int)(size.getWidth() - (double)evt.getComponent().getWidth() - (double)this.distance);
         }

         if ((double)ny > size.getHeight() - (double)evt.getComponent().getHeight() - (double)this.snap_distance - (double)this.distance) {
            ny = (int)(size.getHeight() - (double)evt.getComponent().getHeight() - (double)this.distance);
         }

         this.locked = true;
         evt.getComponent().setLocation(nx, ny);
         this.locked = false;
      }
   }
}
