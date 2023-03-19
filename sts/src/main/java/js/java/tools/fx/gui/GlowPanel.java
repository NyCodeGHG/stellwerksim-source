package js.java.tools.fx.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;

public class GlowPanel extends JComponent {
   private String text;
   private Font font;
   private double scale;
   private BufferedImage img = null;
   private BufferedImageOp externalFilter = null;
   private Color bgcolor = new Color(0, 0, 0, 220);

   public GlowPanel() {
      super();
      this.text = "hello world";
      this.font = new Font("lucida sans demibold", 0, 48);
      this.scale = 1.02;
      this.paintImage(this.text);
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (this.img != null) {
         Graphics2D g2 = (Graphics2D)g;
         g.setColor(Color.BLACK);
         g.fillRect(0, 0, this.getWidth(), this.getHeight());
         if (this.externalFilter != null) {
            try {
               g2.drawImage(this.img, this.externalFilter, 0, 0);
            } catch (Exception var4) {
               var4.printStackTrace();
               g2.drawImage(this.img, 0, 0, null);
            }
         } else {
            g2.drawImage(this.img, 0, 0, null);
         }
      }
   }

   public void setExtenalFilterOp(BufferedImageOp externalFilter) {
      this.externalFilter = externalFilter;
      this.repaint();
   }

   public void setText(String t) {
      this.text = t;
      this.paintImage(this.text);
      this.repaint();
   }

   protected BufferedImage createCompatibleImage(int w, int h) {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();

      try {
         return gc.createCompatibleImage(Math.max(w, 1), Math.max(h, 1), 3);
      } catch (OutOfMemoryError var7) {
         System.out.println("Out of Memory: " + var7.getMessage());
         var7.printStackTrace();
         System.gc();
         Logger.getLogger(GlowPanel.class.getName()).log(Level.SEVERE, "Out of memory", var7);
         return null;
      }
   }

   public void reshape(int x, int y, int w, int h) {
      super.reshape(x, y, w, h);
      this.paintImage(this.text);
   }

   private void paintImage(String text) {
      if (this.img == null || this.img.getWidth() != this.getWidth() || this.img.getHeight() != this.getHeight()) {
         this.img = this.createCompatibleImage(this.getWidth(), this.getHeight());
      }

      if (this.img != null) {
         Graphics2D g2 = this.img.createGraphics();
         this.paint(g2, text, this.getWidth(), this.getHeight());
         g2.dispose();
      }
   }

   private void paint(Graphics2D g2, String text, int width, int height) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setBackground(this.bgcolor);
      g2.clearRect(0, 0, width, height);
      g2.setFont(this.font);
      FontRenderContext frc = g2.getFontRenderContext();
      TextLayout textLayout = new TextLayout(text, this.font, frc);
      g2.setColor(Color.YELLOW);
      textLayout.draw(g2, 30.0F, 50.0F);
      Rectangle2D r2 = textLayout.getBounds();
      double x = ((double)width - this.scale * r2.getWidth()) / 2.0;
      double y = ((double)height + this.scale * r2.getHeight()) / 2.0;
      AffineTransform at = AffineTransform.getTranslateInstance(x, y);
      at.scale(this.scale, this.scale);
      Shape outline = textLayout.getOutline(at);
      Rectangle r = outline.getBounds();
      float x1 = (float)(r.x + r.width / 2);
      float y1 = (float)(r.y + r.height / 2);
      float y2 = (float)(height * 5 / 8);
      GradientPaint gradient = new GradientPaint(x1, y1, Color.red, x1, y2, Color.yellow, true);
      g2.setPaint(gradient);
      g2.fill(outline);
   }
}
