package js.java.isolate.sim.toolkit;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;

public class twoWayQuestionIcon extends twoWayIcon {
   protected final ImageIcon knowIconLarge;
   protected final ImageIcon knowIconSmall = new ImageIcon(this.getClass().getResource("/js/java/tools/resources/question32.png"));

   public twoWayQuestionIcon() {
      super();
      this.knowIconLarge = new ImageIcon(this.getClass().getResource("/js/java/tools/resources/question48.png"));
      this.line1col = new Color(230, 80, 0, 128);
   }

   @Override
   protected void paintWay(Graphics2D g2) {
      super.paintWay(g2);
      if (this.blinkOn) {
         this.drawIcon(g2, this.knowIconLarge);
      } else {
         this.drawIcon(g2, this.knowIconSmall);
      }
   }
}
