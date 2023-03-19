package js.java.tools.gui.clock;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComponent;

public class bahnhofsUhr extends JComponent {
   private BufferedImage bgimage = null;
   private BufferedImage ovimage = null;
   private final Color blattcolor1 = new Color(229, 229, 229);
   private final Color blattcolor2 = new Color(208, 208, 216);
   private final Color zeigercolor = Color.BLACK;
   private final Color szeigercolor = Color.RED;
   private final Color textcolor = Color.BLUE;
   private final Color schatten = new Color(0, 0, 0, 51);
   private final Color glascolor1 = new Color(255, 255, 255, 170);
   private final Color glascolor2 = new Color(255, 255, 255, 0);
   private final Color glascolor3 = new Color(0, 0, 0, 68);
   private final Color glascolor4 = new Color(0, 0, 0, 0);
   private final Color transparent = new Color(255, 255, 255, 0);
   private int width;
   private int height;
   private int x0;
   private int y0;
   private int radius;
   private int oradius1;
   private int oradius2;
   private int radius_rand;
   private int radius_randover;
   private int radius_mini;
   private int radius_gross;
   private int radius_klein;
   private int radius_minuten;
   private int radius_5minuten;
   private int radius_15minuten;
   private int radius_24stdRandDicke;
   private int radius_24stdRand;
   private int radius_24stdRandZahlen;
   private int width_minuten;
   private int width_5minuten;
   private int width_gross;
   private int width_klein;
   private int width_sec;
   private int currentHour = 0;
   private int currentMinute = 0;
   private int currentSecond = 0;
   private int shadowX = 4;
   private int shadowY = 4;
   private bahnhofsUhr.timeDeliverer deliverer;
   private Font textfont;
   private Font zahlenfont;
   private String uhrText;
   private boolean initial = true;
   private boolean twentyFourMode = false;
   private final Timer painterTimer;
   private TimerTask painterTask = null;
   private boolean paintSeconds = true;
   private final javax.swing.Timer t;
   private int xm;
   private int ym;
   private int xh;
   private int yh;
   private int xs;
   private int ys;
   private double lastH = 0.0;
   private double lastM = 0.0;
   private double lastS = 0.0;

   public bahnhofsUhr(bahnhofsUhr.timeDeliverer del, String text) {
      super();
      this.deliverer = del;
      this.uhrText = text;
      this.setBackground(new Color(136, 136, 136));
      this.painterTimer = new Timer();
      this.calcNeedles();
      this.t = new javax.swing.Timer(250, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            bahnhofsUhr.this.timeQuery();
         }
      });
      this.t.start();
   }

   public bahnhofsUhr(bahnhofsUhr.timeDeliverer del, String text, boolean twentyFourMode) {
      this(del, text);
      this.twentyFourMode = twentyFourMode;
   }

   public void finish() {
      this.t.stop();
      this.stopTimer();
      if (this.painterTimer != null) {
         this.painterTimer.cancel();
      }
   }

   public void setSecondsDisabled(boolean e) {
      this.paintSeconds = !e;
   }

   private void startTimer() {
      if (this.painterTask == null) {
         this.painterTask = new TimerTask() {
            public void run() {
               bahnhofsUhr.this.moveNeedle();
            }
         };
         this.painterTimer.scheduleAtFixedRate(this.painterTask, 10L, 10L);
      }
   }

   private void stopTimer() {
      if (this.painterTask != null) {
         this.painterTask.cancel();
         this.painterTask = null;
      }
   }

   public void setText(String text) {
      this.uhrText = text;
      this.ovimage = null;
      this.bgimage = null;
   }

   public void set24Mode(boolean twentyfour) {
      this.twentyFourMode = twentyfour;
      this.ovimage = null;
      this.bgimage = null;
   }

   public void reshape(int x, int y, int width, int height) {
      boolean changed = width != this.getWidth() || height != this.getHeight();
      super.reshape(x, y, width, height);
      if (changed) {
         this.ovimage = null;
         this.bgimage = null;
      }
   }

   private void setValues() {
      this.width = this.getWidth();
      this.height = this.getHeight();
      this.x0 = this.width / 2;
      this.y0 = this.height / 2;
      this.radius = (int)((double)Math.min(this.x0, this.y0) * 0.95);
      this.oradius1 = (int)((double)this.radius * 1.05);
      this.oradius2 = (int)((double)this.radius * 1.02);
      this.radius_rand = (int)((double)this.radius * 0.95);
      this.radius_randover = (int)((double)this.radius * 0.98);
      this.radius_mini = (int)((double)this.radius * 0.08);
      this.radius_gross = (int)((double)this.radius * 0.9);
      if (this.twentyFourMode) {
         this.radius_klein = (int)((double)this.radius * 0.5);
      } else {
         this.radius_klein = (int)((double)this.radius * 0.55);
      }

      this.radius_minuten = (int)((double)this.radius * 0.85);
      this.radius_5minuten = (int)((double)this.radius * 0.7);
      this.radius_15minuten = (int)((double)this.radius * 0.62);
      this.radius_24stdRandDicke = (int)((double)this.radius * 0.02);
      this.radius_24stdRand = (int)((double)this.radius * 0.58);
      this.radius_24stdRandZahlen = (int)((double)this.radius * 0.55);
      this.width_5minuten = (int)((double)(this.radius_rand - this.radius_minuten) * 0.6);
      this.width_sec = this.width_5minuten / 3;
      this.width_gross = this.width_5minuten;
      this.width_klein = this.width_5minuten;
      this.width_minuten = this.width_5minuten / 2;
      this.shadowX = this.shadowY = this.radius / 100;
      this.textfont = new Font("Sans-Serif", 1, this.radius / 10);
      this.zahlenfont = new Font("Sans-Serif", 1, this.radius / 12);
   }

   public void setTime(int hour, int minute, int second) {
      this.currentHour = hour;
      this.currentMinute = minute % 60;
      this.currentSecond = second % 60;
      this.calcNeedles();
      this.initial = false;
   }

   private void timeQuery() {
      this.deliverer.timeQuery(this);
   }

   private double shiftAlpha(double w) {
      return (w + 360.0) % 360.0;
   }

   private void calcNeedles() {
      double s = this.shiftAlpha((double)((this.currentSecond - 15) * 6));
      double m = this.shiftAlpha((double)((this.currentMinute - 15) * 6));
      double h;
      if (this.twentyFourMode) {
         h = this.shiftAlpha((double)((this.currentHour - 6) * 15 + this.currentMinute / 4));
      } else {
         h = this.shiftAlpha((double)((this.currentHour % 12 - 3) * 30 + this.currentMinute / 2));
      }

      boolean moved = false;
      if (!this.initial && Math.abs(this.lastS - s) > 0.5) {
         double o = 0.6;
         this.lastS = this.shiftAlpha(this.lastS + o);
         moved = true;
      } else {
         this.lastS = this.shiftAlpha(s);
      }

      if (!this.initial && Math.abs(this.lastM - m) > 0.5) {
         double o = 0.25;
         this.lastM = this.shiftAlpha(this.lastM + o);
         moved = true;
      } else {
         this.lastM = this.shiftAlpha(m);
      }

      if (!this.initial && Math.abs(this.lastH - h) > 0.1) {
         double o = 0.25;
         this.lastH = this.shiftAlpha(this.lastH + o);
         moved = true;
      } else {
         this.lastH = this.shiftAlpha(h);
      }

      this.xm = (int)((double)this.x0 + (double)this.radius_gross * Math.cos(this.lastM * Math.PI / 180.0));
      this.ym = (int)((double)this.y0 + (double)this.radius_gross * Math.sin(this.lastM * Math.PI / 180.0));
      this.xh = (int)((double)this.x0 + (double)this.radius_klein * Math.cos(this.lastH * Math.PI / 180.0));
      this.yh = (int)((double)this.y0 + (double)this.radius_klein * Math.sin(this.lastH * Math.PI / 180.0));
      this.xm = (int)((double)this.x0 + (double)this.radius_gross * Math.cos(this.lastM * Math.PI / 180.0));
      this.ym = (int)((double)this.y0 + (double)this.radius_gross * Math.sin(this.lastM * Math.PI / 180.0));
      this.xs = (int)((double)this.x0 + (double)this.radius_gross * Math.cos(this.lastS * Math.PI / 180.0));
      this.ys = (int)((double)this.y0 + (double)this.radius_gross * Math.sin(this.lastS * Math.PI / 180.0));
      if (!moved) {
         this.stopTimer();
      } else {
         this.startTimer();
      }
   }

   private void moveNeedle() {
      this.calcNeedles();
      this.repaint();
   }

   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      this.paintImage(g);
      this.paintZeiger(g);
      this.paintOverlay(g);
   }

   private void paintZeiger(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g2.setStroke(new BasicStroke((float)this.width_klein, 0, 2));
      g2.setColor(this.schatten);
      g2.drawLine(this.x0 + this.shadowX, this.y0 + this.shadowY, this.xh + this.shadowX, this.yh + this.shadowY);
      g2.setColor(this.zeigercolor);
      g2.drawLine(this.x0, this.y0, this.xh, this.yh);
      g2.setStroke(new BasicStroke((float)this.width_gross, 0, 2));
      g2.setColor(this.schatten);
      g2.drawLine(this.x0 + this.shadowX, this.y0 + this.shadowY, this.xm + this.shadowX, this.ym + this.shadowY);
      g2.setColor(this.zeigercolor);
      g2.drawLine(this.x0, this.y0, this.xm, this.ym);
      if (this.paintSeconds) {
         g2.setStroke(new BasicStroke((float)this.width_sec, 0, 2));
         g2.setColor(this.schatten);
         g2.drawLine(this.x0 + this.shadowX, this.y0 + this.shadowY, this.xs + this.shadowX, this.ys + this.shadowY);
         g2.setColor(this.szeigercolor);
         g2.drawLine(this.x0, this.y0, this.xs, this.ys);
      }

      g2.setStroke(new BasicStroke());
      g2.setColor(this.zeigercolor);
      g2.fillOval(this.x0 - this.radius_mini, this.y0 - this.radius_mini, this.radius_mini * 2, this.radius_mini * 2);
   }

   private void paintImage(Graphics g) {
      try {
         if (this.bgimage == null) {
            this.setValues();
            this.calcNeedles();
            this.bgimage = new BufferedImage(this.width, this.height, 1);
            Graphics2D g2 = this.bgimage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(this.getBackground());
            g2.fillRect(0, 0, this.width, this.height);
            g2.setColor(this.zeigercolor);
            g2.fillOval(this.x0 - this.oradius1 - 1, this.y0 - this.oradius1 - 1, this.oradius1 * 2 + 2, this.oradius1 * 2 + 2);
            Point2D start = new Float((float)(this.x0 - this.oradius1), (float)(this.y0 - this.oradius1));
            Point2D end = new Float((float)(this.x0 + this.oradius1), (float)(this.y0 + this.oradius1));
            float[] ldist = new float[]{0.2F, 0.8F};
            Color[] lcolors1 = new Color[]{new Color(238, 238, 240), new Color(68, 68, 102)};
            LinearGradientPaint lp = new LinearGradientPaint(start, end, ldist, lcolors1);
            g2.setPaint(lp);
            g2.fillOval(this.x0 - this.oradius1, this.y0 - this.oradius1, this.oradius1 * 2, this.oradius1 * 2);
            Color[] lcolors2 = new Color[]{new Color(102, 102, 102), new Color(238, 238, 240)};
            lp = new LinearGradientPaint(start, end, ldist, lcolors2);
            g2.setPaint(lp);
            g2.fillOval(this.x0 - this.oradius2, this.y0 - this.oradius2, this.oradius2 * 2, this.oradius2 * 2);
            g2.setColor(this.zeigercolor);
            g2.fillOval(this.x0 - this.radius, this.y0 - this.radius, this.radius * 2, this.radius * 2);
            Point2D center = new Float((float)this.x0, (float)this.y0);
            float[] dist = new float[]{0.3F, 0.6F, 1.0F};
            Color[] colors = new Color[]{this.blattcolor2, this.blattcolor1, this.blattcolor2};
            RadialGradientPaint p = new RadialGradientPaint(center, (float)this.radius_rand, dist, colors);
            g2.setPaint(p);
            g2.fillOval(this.x0 - this.radius_rand, this.y0 - this.radius_rand, this.radius_rand * 2, this.radius_rand * 2);
            g2.setColor(this.schatten);
            g2.fillOval(this.x0 - this.radius_mini + this.shadowX, this.y0 - this.radius_mini + this.shadowY, this.radius_mini * 2, this.radius_mini * 2);
            g2.setColor(this.zeigercolor);
            g2.fillOval(this.x0 - this.radius_mini, this.y0 - this.radius_mini, this.radius_mini * 2, this.radius_mini * 2);

            for(int m = 0; m < 60; ++m) {
               int x1 = (int)((double)this.x0 + (double)this.radius_randover * Math.cos((double)(m * 6) * Math.PI / 180.0));
               int y1 = (int)((double)this.y0 + (double)this.radius_randover * Math.sin((double)(m * 6) * Math.PI / 180.0));
               int sw;
               int r;
               if (m % 15 == 0) {
                  sw = this.width_5minuten;
                  r = this.radius_15minuten;
               } else if (m % 5 == 0) {
                  sw = this.width_5minuten;
                  r = this.radius_5minuten;
               } else {
                  sw = this.width_minuten;
                  r = this.radius_minuten;
               }

               int x2 = (int)((double)this.x0 + (double)r * Math.cos((double)(m * 6) * Math.PI / 180.0));
               int y2 = (int)((double)this.y0 + (double)r * Math.sin((double)(m * 6) * Math.PI / 180.0));
               g2.setStroke(new BasicStroke((float)sw, 0, 2));
               g2.drawLine(x2, y2, x1, y1);
            }

            g2.setStroke(new BasicStroke());
            g2.setFont(this.textfont);
            FontRenderContext frc = g2.getFontRenderContext();
            int textwidth = (int)this.textfont.getStringBounds(this.uhrText, frc).getWidth();
            int sx = this.x0 - textwidth / 2;
            int sy = this.y0 - this.radius_klein + (this.radius - this.radius_5minuten);
            g2.setPaint(this.textcolor);
            g2.drawString(this.uhrText, sx, sy);
            if (this.twentyFourMode) {
               g2.setFont(this.zahlenfont);
               g2.setColor(this.zeigercolor);
               g2.setStroke(new BasicStroke((float)this.radius_24stdRandDicke, 0, 2));
               g2.drawOval(
                  this.x0 - this.radius_24stdRand - 1, this.y0 - this.radius_24stdRand - 1, this.radius_24stdRand * 2 + 2, this.radius_24stdRand * 2 + 2
               );

               for(int s = 0; s < 24; ++s) {
                  int x1 = (int)((double)this.x0 + (double)this.radius_24stdRand * Math.cos((double)(s * 15) * Math.PI / 180.0));
                  int y1 = (int)((double)this.y0 + (double)this.radius_24stdRand * Math.sin((double)(s * 15) * Math.PI / 180.0));
                  int x2 = (int)((double)this.x0 + (double)this.radius_24stdRandZahlen * Math.cos((double)(s * 15) * Math.PI / 180.0));
                  int y2 = (int)((double)this.y0 + (double)this.radius_24stdRandZahlen * Math.sin((double)(s * 15) * Math.PI / 180.0));
                  g2.drawLine(x2, y2, x1, y1);
                  if (s % 2 == 0) {
                     Graphics2D g3 = (Graphics2D)g2.create();
                     g3.translate(x2, y2);
                     g3.rotate((double)(90 + s * 15) * Math.PI / 180.0);
                     frc = g2.getFontRenderContext();
                     int h = (s + 6) % 24;
                     String text = h + "";
                     FontMetrics fm = g3.getFontMetrics();
                     textwidth = (int)this.textfont.getStringBounds(text, frc).getWidth() - this.radius_24stdRandDicke;
                     g3.drawString(text, -textwidth / 2, fm.getAscent());
                     textwidth = (int)this.textfont.getStringBounds("oo", frc).getWidth() - this.radius_24stdRandDicke;
                     g3.drawLine(-textwidth / 2, 0, textwidth / 2, 0);
                     g3.dispose();
                  }
               }
            }

            g2.dispose();
         }

         g.drawImage(this.bgimage, 0, 0, null);
      } catch (Exception var26) {
      }
   }

   private void paintOverlay(Graphics g) {
      try {
         if (this.ovimage == null) {
            this.ovimage = new BufferedImage(this.width, this.height, 2);
            Graphics2D g2 = this.ovimage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(this.transparent);
            g2.fillRect(0, 0, this.width, this.height);
            g2.setComposite(AlphaComposite.DstAtop);
            g2.setColor(this.glascolor1);
            g2.fillOval(this.x0 - this.radius, this.y0 - this.radius, this.radius * 2, this.radius * 2);
            g2.setColor(this.transparent);
            g2.fillArc(
               this.x0 - (int)((double)this.oradius2 * 0.89),
               this.y0 - (int)((double)this.oradius2 * 0.89),
               (int)((double)this.oradius2 * 2.7),
               (int)((double)this.oradius2 * 2.7),
               60,
               150
            );
            g2.setColor(this.transparent);
            g2.fillArc(
               this.x0 - (int)((double)this.oradius2 * 1.45),
               this.y0 - (int)((double)this.oradius2 * 1.45),
               (int)((double)this.oradius2 * 2.4),
               (int)((double)this.oradius2 * 2.4),
               240,
               150
            );
            g2.dispose();
         }

         g.drawImage(this.ovimage, 0, 0, null);
      } catch (Exception var3) {
         System.out.println(var3.getMessage());
         var3.printStackTrace();
      }
   }

   public interface timeDeliverer {
      void timeQuery(bahnhofsUhr var1);
   }
}
