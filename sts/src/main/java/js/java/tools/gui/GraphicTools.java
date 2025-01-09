package js.java.tools.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D.Double;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;

public class GraphicTools {
   public static void enableTextAA(Graphics2D g2) {
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
   }

   public static void disableTextAA(Graphics2D g2) {
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
   }

   public static void enableGfxAA(Graphics2D g2) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
   }

   public static void disableGfxAA(Graphics2D g2) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
   }

   public static Shape createArrow(Point lineStart, Point lineEnd) {
      double Par = 10.0;
      double slopy = Math.atan2((double)(lineEnd.y - lineStart.y), (double)(lineEnd.x - lineStart.x));
      double cosy = Math.cos(slopy);
      double siny = Math.sin(slopy);
      Double pdc = new Double();
      pdc.moveTo((double)lineEnd.x, (double)lineEnd.y);
      pdc.lineTo((double)lineEnd.x + (-10.0 * cosy - 5.0 * siny), (double)lineEnd.y + -10.0 * siny + 5.0 * cosy);
      pdc.lineTo((double)lineEnd.x + -10.0 * cosy + 5.0 * siny, (double)lineEnd.y - (5.0 * cosy + 10.0 * siny));
      pdc.lineTo((double)lineEnd.x, (double)lineEnd.y);
      return pdc;
   }

   public static ImageIcon toGray(ImageIcon icon) {
      Image grayImage = GrayFilter.createDisabledImage(icon.getImage());
      return new ImageIcon(grayImage);
   }

   public static Color darker(Color c, double FACTOR) {
      return new Color(
         Math.max((int)((double)c.getRed() * FACTOR), 0),
         Math.max((int)((double)c.getGreen() * FACTOR), 0),
         Math.max((int)((double)c.getBlue() * FACTOR), 0),
         c.getAlpha()
      );
   }

   public static Color brighter(Color c, double FACTOR) {
      int r = c.getRed();
      int g = c.getGreen();
      int b = c.getBlue();
      int alpha = c.getAlpha();
      int i = (int)(1.0 / (1.0 - FACTOR));
      if (r == 0 && g == 0 && b == 0) {
         return new Color(i, i, i, alpha);
      } else {
         if (r > 0 && r < i) {
            r = i;
         }

         if (g > 0 && g < i) {
            g = i;
         }

         if (b > 0 && b < i) {
            b = i;
         }

         return new Color(Math.min((int)((double)r / FACTOR), 255), Math.min((int)((double)g / FACTOR), 255), Math.min((int)((double)b / FACTOR), 255), alpha);
      }
   }
}
