package js.java.tools.gui.darrylbu;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

public class AlphaIcon implements Icon {
   private Icon icon;
   private float alpha;

   public AlphaIcon(Icon icon, float alpha) {
      this.icon = icon;
      this.alpha = alpha;
   }

   public float getAlpha() {
      return this.alpha;
   }

   public Icon getIcon() {
      return this.icon;
   }

   public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D)g.create();
      g2.setComposite(AlphaComposite.SrcAtop.derive(this.alpha));
      this.icon.paintIcon(c, g2, x, y);
      g2.dispose();
   }

   public int getIconWidth() {
      return this.icon.getIconWidth();
   }

   public int getIconHeight() {
      return this.icon.getIconHeight();
   }
}
