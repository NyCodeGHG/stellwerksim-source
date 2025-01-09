package com.ezware.common;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class Icons {
   private Icons() {
   }

   public static final Image asImage(Icon icon) {
      if (icon == null) {
         throw new IllegalArgumentException("The icon should not be null");
      } else if (icon instanceof ImageIcon) {
         return ((ImageIcon)icon).getImage();
      } else {
         int w = icon.getIconWidth() == 0 ? 1 : icon.getIconWidth();
         int h = icon.getIconHeight() == 0 ? 1 : icon.getIconHeight();
         Image image = new BufferedImage(w, h, 2);
         icon.paintIcon(null, image.getGraphics(), 0, 0);
         return image;
      }
   }

   public static final Image asImage(Icon icon, int newWidth, int newHeight) {
      return asImage(icon).getScaledInstance(newWidth, newHeight, 4);
   }

   public static final Icon scale(Icon icon, int newWidth, int newHeight) {
      return new ImageIcon(asImage(icon, newWidth, newHeight));
   }
}
