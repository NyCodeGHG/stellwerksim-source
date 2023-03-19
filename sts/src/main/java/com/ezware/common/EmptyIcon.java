package com.ezware.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.RoundRectangle2D.Float;
import javax.swing.Icon;

public class EmptyIcon implements Icon {
   private final int size;
   private boolean paintImage;

   public static final EmptyIcon visible(int size) {
      return new EmptyIcon(size, true);
   }

   public static final EmptyIcon hidden() {
      return new EmptyIcon(0, false);
   }

   private EmptyIcon(int size, boolean paintImage) {
      super();
      this.size = Math.abs(size == 0 ? 1 : size);
      this.paintImage = paintImage;
   }

   public int getIconHeight() {
      return this.size;
   }

   public int getIconWidth() {
      return this.size;
   }

   public void paintIcon(Component c, Graphics g, int x, int y) {
      if (this.paintImage && this.size > 2) {
         Graphics2D g2 = (Graphics2D)g;
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         int radius = this.size / 3;
         RoundRectangle2D r = new Float((float)x, (float)y, (float)(this.size - 1), (float)(this.size - 1), (float)radius, (float)radius);
         g2.setColor(new Color(255, 255, 0, 127));
         g2.fill(r);
         g2.setStroke(new BasicStroke(3.0F));
         g2.setColor(Color.RED);
         g2.draw(r);
         Point center = new Point(x + this.size / 2, y + this.size / 2);
         int d = this.size / 4;
         int xad = center.x - d;
         int xsd = center.x + d;
         int yad = center.y + d;
         int ysd = center.y - d;
         g2.drawLine(xad, ysd, xsd, yad);
         g2.drawLine(xad, yad, xsd, ysd);
      }
   }
}
