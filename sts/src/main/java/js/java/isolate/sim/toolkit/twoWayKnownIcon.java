package js.java.isolate.sim.toolkit;

import java.awt.Graphics2D;
import javax.swing.ImageIcon;

public class twoWayKnownIcon extends twoWayIcon {
   protected final ImageIcon knowIconSmall = new ImageIcon(this.getClass().getResource("/js/java/tools/resources/accept16.png"));

   @Override
   protected void paintWay(Graphics2D g2) {
      super.paintWay(g2);
      this.drawIcon(g2, this.knowIconSmall);
   }
}
