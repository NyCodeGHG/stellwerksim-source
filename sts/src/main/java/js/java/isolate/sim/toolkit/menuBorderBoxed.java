package js.java.isolate.sim.toolkit;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

public class menuBorderBoxed extends menuBorder {
   public menuBorderBoxed(String title) {
      super(title);
      this.setFillBackground(false);
   }

   @Override
   protected void paintBorder2(Component c, Graphics g, int x, int y, int width, int height, int offs) {
      super.paintBorder2(c, g, x, y, width, height, offs);
      Graphics2D g2 = (Graphics2D)g;
      g2.translate(x, y);
      g2.setColor(this.getShadowInnerColor(c));
      g2.fillRect(1, offs, 2, height - offs);
      g2.fillRect(width - 4, offs, 2, height - offs);
      g2.translate(-x, -y);
   }

   @Override
   public Insets getBorderInsets(Component c, Insets insets) {
      super.getBorderInsets(c, insets);
      insets.left += 2;
      insets.right += 2;
      return insets;
   }
}
