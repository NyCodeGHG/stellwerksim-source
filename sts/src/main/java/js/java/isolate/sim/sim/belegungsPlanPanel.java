package js.java.isolate.sim.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import javax.swing.JComponent;
import js.java.schaltungen.timesystem.timedelivery;
import js.java.tools.gui.GraphicTools;

class belegungsPlanPanel extends JComponent implements MouseMotionListener {
   static final int PLANWIDTH = 400;
   static final int FROMMINS = 10;
   static final int TOMINS = 60;
   static final int HEIGHTPERMINUTE = 12;
   static final int VERSPAETUNGSWIDTH = 60;
   private Dimension dim;
   private long simutime;
   private final LinkedList<belegungsPlanPanel.gleisPlan> plan = new LinkedList();
   private final int[] minutes = new int[70];
   private int columns = 1;
   private int colwidth = 0;
   private final Font font = new Font("Dialog", 0, 11);
   private final Font ifont = new Font("Dialog", 2, 11);
   private final Font bfont = new Font("Dialog", 1, 11);
   private final Color bgcol1 = new Color(255, 255, 255);
   private final Color bgcol2 = new Color(187, 187, 204);
   private timedelivery timer;
   private boolean gleisMode = false;
   private int rasterY = 0;

   public void updatePlan(LinkedList<zugUndPlanPanel.gleisPlan> bplan) {
      this.simutime = this.timer.getSimutime() - 600000L;

      for(zugUndPlanPanel.gleisPlan bgp : bplan) {
         int start = (int)((bgp.gd.an - this.simutime) / 60000L) + 1;
         int stop = (int)((bgp.gd.ab - this.simutime) / 60000L);
         if (bgp.gd.flags.hasFlag('E')) {
            --stop;
         }

         if (start > stop) {
            start = stop;
         }

         int col = this.incMinutes(start, stop);
         belegungsPlanPanel.gleisPlan gp = new belegungsPlanPanel.gleisPlan();
         gp.gp = bgp;
         gp.col = col;
         gp.y1 = 12 * Math.max(0, start);
         gp.y2 = 12 * Math.max(0, stop) + 12 - 1;
         this.plan.add(gp);
      }

      for(int i = 0; i < this.minutes.length; ++i) {
         this.columns = Math.max(this.columns, this.minutes[i]);
      }

      this.colwidth = 400 / this.columns;
   }

   belegungsPlanPanel(timedelivery timer) {
      this(timer, false);
   }

   belegungsPlanPanel(timedelivery timer, boolean gleisMode) {
      super();
      this.gleisMode = gleisMode;
      this.timer = timer;
      this.dim = new Dimension(1, 840);

      for(int i = 0; i < this.minutes.length; ++i) {
         this.minutes[i] = 0;
      }

      this.setDoubleBuffered(true);
      this.setBackground(this.bgcol1);
      this.addMouseMotionListener(this);
   }

   private int incMinutes(int start, int stop) {
      int ret = 1;

      for(int i = start; i <= stop; ++i) {
         if (i >= 0 && i < this.minutes.length) {
            this.minutes[i]++;
            ret = Math.max(ret, this.minutes[i]);
         }
      }

      return ret;
   }

   public Dimension getPreferredSize() {
      return this.dim;
   }

   public Dimension getMinimumSize() {
      return this.dim;
   }

   public void mouseDragged(MouseEvent e) {
   }

   public void mouseMoved(MouseEvent e) {
      int y = e.getY() / 12;
      if (this.rasterY != y) {
         this.rasterY = y;
         this.repaint();
      }
   }

   public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableTextAA(g2);
      GraphicTools.enableGfxAA(g2);
      this.colwidth = this.getWidth() / this.columns;
      GradientPaint bggrad = new GradientPaint(0.0F, 0.0F, this.bgcol1, 0.0F, (float)this.getHeight(), this.bgcol2, true);
      g2.setPaint(bggrad);
      g2.fillRect(0, 0, this.getWidth(), this.getHeight());
      g2.setColor(this.bgcol1.darker());

      for(int min = 0; min < 70; ++min) {
         long t = this.simutime + (long)min * 60000L;
         int h = (int)(t / 3600000L);
         int m = (int)(t / 60000L) % 60;
         if (m % 5 == 0) {
            g2.drawLine(0, min * 12, this.getWidth(), min * 12);
         }
      }

      g2.setColor(Color.WHITE);
      g2.drawLine(0, this.rasterY * 12, this.getWidth(), this.rasterY * 12);

      for(belegungsPlanPanel.gleisPlan gp : this.plan) {
         int x = this.colwidth * (gp.col - 1);
         Color c = Color.CYAN;
         if (gp.gp.gd.befehlgleisAlt) {
            c = c.darker();
         } else if (gp.gp.gd.befehlgleisNeu) {
            c = new Color(204, 170, 170);
         }

         GradientPaint grad = new GradientPaint(0.0F, (float)gp.y1, c, 0.0F, (float)gp.y2, c.darker(), true);
         g2.setPaint(grad);
         g2.fillRect(x, gp.y1, this.colwidth - 1 - 60, gp.y2 - gp.y1);
         g2.setFont(this.font);
         int x2 = x + this.colwidth - 1 - 60;
         int y1 = gp.y1 + gp.gp.z.getVerspaetung_num() * 12;
         int y2 = gp.y2 + gp.gp.z.getVerspaetung_num() * 12;
         if (Math.abs(gp.gp.z.getVerspaetung_num()) > 1) {
            c = Color.RED;
            if (gp.gp.gd.befehlgleisAlt) {
               c = c.darker();
            }

            grad = new GradientPaint(0.0F, (float)y1, c, 0.0F, (float)y2, c.darker(), true);
            g2.setPaint(grad);
            g2.fillRect(x2, y1, 60, y2 - y1);
            g2.fillRect(x2, gp.y1, 2, y1 - gp.y1);
            g2.setColor(Color.BLACK);
            g2.drawString(gp.gp.z.getSpezialName(), x2 + 1, y1 + 9);
         }

         g2.setColor(Color.BLACK);
         if (this.gleisMode) {
            g2.setFont(this.bfont);
            String gname = (gp.gp.gd.befehlgleisAlt ? gp.gp.gd.gleis : gp.gp.gd.sollgleis) + ":";
            FontMetrics mfont = g2.getFontMetrics();
            int w = mfont.stringWidth(gname);
            g2.drawString(gname, x + 5, gp.y1 + 9);
            x += w + 5;
            g2.setFont(this.font);
         }

         g2.drawString(gp.gp.z.getSpezialName() + " (" + gp.gp.z.getVerspaetung() + ")", x + 5, gp.y1 + 9);
         g2.setFont(this.ifont);
         FontMetrics mfont = g2.getFontMetrics();
         int w = mfont.stringWidth(gp.gp.additionalText);
         g2.drawString(gp.gp.additionalText, x2 - 5 - w, gp.y1 + 9);
         g2.setColor(new Color(255, 255, 0, 80));
         g2.drawLine(0, this.rasterY * 12, this.getWidth(), this.rasterY * 12);
      }
   }

   static class belegungsZeitPanel extends JComponent {
      private final Font font = new Font("Dialog", 1, 14);
      private final Color bgcol1 = new Color(187, 187, 255);
      private final Color bgcol2 = new Color(255, 255, 255);
      private final Dimension dim;
      private long simutime;
      private final timedelivery timer;

      public void updatePlan() {
         this.simutime = this.timer.getSimutime() - 600000L;
      }

      belegungsZeitPanel(timedelivery timer) {
         super();
         this.timer = timer;
         this.dim = new Dimension(45, 840);
         this.setBackground(this.bgcol1);
         this.updatePlan();
      }

      public Dimension getPreferredSize() {
         return this.dim;
      }

      public Dimension getMinimumSize() {
         return this.dim;
      }

      public void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g;
         GraphicTools.enableTextAA(g2);
         g2.setFont(this.font);
         GradientPaint bggrad = new GradientPaint(0.0F, 0.0F, this.bgcol1, 0.0F, (float)this.getHeight(), this.bgcol2, true);
         g2.setPaint(bggrad);
         g2.fillRect(0, 0, this.getWidth(), this.getHeight());
         int tm = (int)((this.timer.getSimutime() - this.simutime) / 1000L);
         g2.setColor(Color.RED);
         g2.drawLine(0, tm * 12 / 60, this.getWidth(), tm * 12 / 60);

         for(int min = 0; min < 70; ++min) {
            long t = this.simutime + (long)min * 60000L;
            int h = (int)(t / 3600000L);
            int m = (int)(t / 60000L) % 60;
            int minus = 3;
            if (m % 10 == 0) {
               minus = 20;
            } else if (m % 5 == 0) {
               minus = 10;
            }

            g2.setColor(this.bgcol1.darker());
            g2.drawLine(this.getWidth() - minus, min * 12, this.getWidth(), min * 12);
            g2.setColor(this.bgcol1.brighter());
            g2.drawLine(this.getWidth() - minus, min * 12 + 1, this.getWidth(), min * 12 + 1);
            if (m % 5 == 0) {
               String z = String.format("%2d.%02d", h, m);
               g2.setColor(Color.WHITE);
               g2.drawString(z, 4, min * 12 + 1);
               g2.setColor(Color.BLACK);
               g2.drawString(z, 3, min * 12);
            }
         }
      }
   }

   private class gleisPlan {
      zugUndPlanPanel.gleisPlan gp;
      int col = 0;
      int y1 = 0;
      int y2 = 0;
      int x = 0;

      private gleisPlan() {
         super();
      }
   }
}
