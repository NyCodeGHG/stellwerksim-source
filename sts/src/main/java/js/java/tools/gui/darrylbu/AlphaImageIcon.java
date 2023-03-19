package js.java.tools.gui.darrylbu;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class AlphaImageIcon extends ImageIcon {
   private Icon icon;
   private Image image;
   private float alpha;

   public AlphaImageIcon(Icon icon, float alpha) {
      super();
      this.icon = icon;
      this.alpha = alpha;
   }

   public Image getImage() {
      return this.image;
   }

   public void setImage(Image image) {
      if (this.icon instanceof ImageIcon) {
         ((ImageIcon)this.icon).setImage(image);
      }
   }

   public int getImageLoadStatus() {
      return this.icon instanceof ImageIcon ? ((ImageIcon)this.icon).getImageLoadStatus() : 0;
   }

   public ImageObserver getImageObserver() {
      return this.icon instanceof ImageIcon ? ((ImageIcon)this.icon).getImageObserver() : null;
   }

   public void setImageObserver(ImageObserver observer) {
      if (this.icon instanceof ImageIcon) {
         ((ImageIcon)this.icon).setImageObserver(observer);
      }
   }

   public float getAlpha() {
      return this.alpha;
   }

   public Icon getIcon() {
      return this.icon;
   }

   public void paintIcon(Component c, Graphics g, int x, int y) {
      if (this.icon instanceof ImageIcon) {
         this.image = ((ImageIcon)this.icon).getImage();
      } else {
         this.image = null;
      }

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
