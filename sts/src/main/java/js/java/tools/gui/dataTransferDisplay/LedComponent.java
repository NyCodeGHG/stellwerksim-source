package js.java.tools.gui.dataTransferDisplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import javax.swing.JComponent;
import js.java.tools.gui.GraphicTools;

public class LedComponent extends JComponent {
   boolean mode = false;
   LedComponent.LEDCOLOR cmode = LedComponent.LEDCOLOR.GREEN;
   private LedComponent.LedPainter painter = new LedComponent.ThreeDPainter();

   public LedComponent() {
      Dimension dim = new Dimension(19, 19);
      this.setMinimumSize(new Dimension(15, 15));
      this.setPreferredSize(dim);
      this.setMaximumSize(dim);
      this.setOpaque(false);
   }

   public LedComponent(LedComponent.LEDCOLOR col) {
      this();
      this.cmode = col;
   }

   public void setPainter(LedComponent.LedPainter p) {
      this.painter = p;
   }

   public void setLed(boolean on) {
      this.mode = on;
      this.repaint();
   }

   public boolean isLed() {
      return this.mode;
   }

   public void setColor(LedComponent.LEDCOLOR col) {
      this.cmode = col;
      this.repaint();
   }

   public void paintComponent(Graphics g) {
      if (this.isOpaque()) {
         g.setColor(this.getBackground());
         g.fillRect(0, 0, this.getWidth(), this.getHeight());
      }

      this.painter.paintComponent(g, this);
   }

   public static enum LEDCOLOR {
      GREEN(new Color(68, 255, 68), new Color(0, 119, 0)),
      YELLOW(new Color(255, 255, 68), new Color(119, 119, 0)),
      RED(new Color(204, 0, 0), new Color(102, 0, 0));

      private final Color trueColor;
      private final Color falseColor;

      private LEDCOLOR(Color trueColor, Color falseColor) {
         this.trueColor = trueColor;
         this.falseColor = falseColor;
      }

      Color colTrue() {
         return this.trueColor;
      }

      Color colFalse() {
         return this.falseColor;
      }
   }

   public interface LedPainter {
      void paintComponent(Graphics var1, LedComponent var2);
   }

   public static class SimplePainter implements LedComponent.LedPainter {
      @Override
      public void paintComponent(Graphics g, LedComponent p) {
         Graphics2D g2 = (Graphics2D)g;
         GraphicTools.enableGfxAA(g2);
         int b = 4;
         int x = 4;
         int y = 4;
         int w = p.getWidth() - 8;
         int h = p.getHeight() - 8;
         int var10;
         w = var10 = Math.min(w, h);
         if (p.mode) {
            g2.setColor(p.cmode.colTrue());
         } else {
            g2.setColor(p.cmode.colFalse());
         }

         g2.fillOval(x, y, w, var10);
         g2.setColor(Color.BLACK);
         g2.drawOval(x, y, w, var10);
      }
   }

   public static class ThreeDPainter implements LedComponent.LedPainter {
      @Override
      public void paintComponent(Graphics g, LedComponent p) {
         Graphics2D g2 = (Graphics2D)g;
         GraphicTools.enableGfxAA(g2);
         int b = 4;
         int x = 4;
         int y = 4;
         int w = p.getWidth() - 8;
         int h = p.getHeight() - 8;
         int var16;
         w = var16 = Math.min(w, h);
         Point2D center = new Float((float)(x + w / 3), (float)(y + 2));
         float radius = (float)(Math.min(p.getWidth(), p.getHeight()) / 2 + 1);
         float[] dist = new float[]{0.2F, 0.7F, 1.0F};
         Color ocol = Color.BLACK;
         RadialGradientPaint rgp;
         if (p.mode) {
            Color[] colors = new Color[]{p.cmode.colTrue().brighter().brighter(), p.cmode.colTrue(), Color.BLACK};
            rgp = new RadialGradientPaint(center, radius, dist, colors);
            ocol = p.cmode.colTrue().brighter();
         } else {
            Color[] colors = new Color[]{p.cmode.colFalse(), p.cmode.colFalse().darker(), Color.BLACK};
            rgp = new RadialGradientPaint(center, radius, dist, colors);
         }

         g2.setPaint(rgp);
         g2.fillOval(x, y, w, var16);
         g2.setColor(ocol);
         g2.drawOval(x, y, w, var16);
      }
   }
}
