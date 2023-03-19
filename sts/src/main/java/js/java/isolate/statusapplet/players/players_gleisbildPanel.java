package js.java.isolate.statusapplet.players;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildViewPanel;
import js.java.schaltungen.UserContext;
import js.java.tools.gui.GraphicTools;

public class players_gleisbildPanel extends gleisbildViewPanel implements ActionListener {
   private static final int FONTSIZE = 15;
   private final gleisbildFrame frameParent;
   private final players_gleisbildModel pmodel;
   private final players_gleisbildControl pcontrol;
   private boolean needZugRepaint = false;
   private Timer repaintTimer;

   public players_gleisbildPanel(UserContext uc, gleisbildFrame frameParent, players_gleisbildControl control, players_gleisbildModel model) {
      super(uc, control, model);
      this.pmodel = model;
      this.pcontrol = control;
      this.setDoubleBuffered(true);
      this.frameParent = frameParent;
      this.repaintTimer = new Timer(1500, this);
      this.repaintTimer.setRepeats(false);
   }

   public void forceRepaint() {
      this.paintBuffer();
      this.paintZuege();
      this.repaint();
   }

   @Override
   public void paintBuffer() {
      super.paintBuffer();
      this.pcontrol.flipImage();
   }

   public void closingFrame() {
      System.out.println("clear");
      this.setModel(null);
      this.setControl(null);
      System.gc();
   }

   @Override
   public void paintComponent(Graphics g) {
      this.setBackground(gleis.colors.col_stellwerk_back);
      g.setColor(gleis.colors.col_stellwerk_back);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      if (this.pmodel.isLoaded()) {
         BufferedImage img_zuege = this.pcontrol.getZuegeImage();
         if (img_zuege != null) {
            g.drawImage(img_zuege, 0, 0, this.getWidth(), this.getHeight(), 0, 0, img_zuege.getWidth(), img_zuege.getHeight(), null);
         }
      }
   }

   public void actionPerformed(ActionEvent e) {
      this.paintZuege();
   }

   void paintZuege() {
      BufferedImage img_gleise = this.pcontrol.getVisibleImage();
      Graphics2D g = this.pcontrol.getZuegeImage().createGraphics();
      g.drawImage(img_gleise, 0, 0, null);
      GraphicTools.enableTextAA(g);
      this.needZugRepaint = false;

      for(players_gleisbildModel.zugdata zd : this.pmodel.zuege.values()) {
         this.paintZug(g, zd);
      }

      g.dispose();
      this.repaint();
      if (this.needZugRepaint) {
         this.repaintTimer.start();
      }
   }

   private void paintZug(Graphics2D g2, players_gleisbildModel.zugdata zd) {
      String text = zd.zug.getSpezialName() + (zd.zug.verspaetung > 0 ? ": +" + zd.zug.verspaetung : "");
      int w = this.printwidth(g2, text) + 4;
      int x = zd.x;
      int y = zd.y;
      int rot = 0;
      switch(zd.richtung) {
         case right:
            if (!zd.passed) {
               x -= w;
            } else {
               x = (int)((double)x + this.control.getScaler().getXScale());
            }
            break;
         case left:
            if (!zd.passed) {
               x = (int)((double)x + this.control.getScaler().getXScale());
            } else {
               x -= w;
            }
            break;
         case up:
            rot = 90;
            x = (int)((double)x + this.control.getScaler().getXScale());
            if (!zd.passed) {
               y = (int)((double)y + this.control.getScaler().getYScale());
            } else {
               y -= w;
            }
            break;
         case down:
            rot = 90;
            if (!zd.passed) {
               y -= w;
            }

            y = (int)((double)y + this.control.getScaler().getYScale());
            x = (int)((double)x + this.control.getScaler().getXScale());
      }

      if (zd.paint_last_x > 0 && zd.paint_last_y > 0 && (zd.paint_last_x != x || zd.paint_last_y != y || zd.paint_last_rot != rot)) {
         Graphics2D g3 = this.prepareGraphics(g2, zd.paint_last_x, zd.paint_last_y, zd.paint_last_rot);
         this.printtext(g3, text, 0, 0, w, gleis.colors.col_stellwerk_schwarz);
         g3.dispose();
         this.needZugRepaint = true;
      }

      Graphics2D g3 = this.prepareGraphics(g2, x, y, rot);
      this.printtext(g3, text, 0, 0, w, gleis.colors.col_stellwerk_rot);
      zd.paint_last_x = x;
      zd.paint_last_y = y;
      zd.paint_last_rot = rot;
      g3.dispose();
   }

   private int printwidth(Graphics2D g, String text) {
      Font f = new Font("SansSerif", 1, 15);
      g.setFont(f);
      FontMetrics fm = g.getFontMetrics();
      return fm.stringWidth(text);
   }

   public Graphics2D prepareGraphics(Graphics2D g, int x0, int y0, int rot) {
      Graphics2D g2 = (Graphics2D)g.create();
      double theta = (double)rot * Math.PI / 180.0;
      g2.translate(x0, y0);
      g2.rotate(theta);
      return g2;
   }

   public void printtext(Graphics2D g2, String name, int x0, int y0, int width, Color bgcolor) {
      g2.setColor(bgcolor);
      g2.fillRect(0, 0, width, 15);
      this.printtext(g2, name, gleis.colors.col_stellwerk_weiss, 0, 0);
   }

   private void printtext(Graphics2D g2, String text, Color col, int x, int y) {
      Font f = new Font("SansSerif", 1, 15);
      g2.setColor(col);
      g2.setFont(f);
      FontMetrics fm = g2.getFontMetrics();
      g2.drawString(text, x + 2, y + fm.getAscent() - 3);
   }
}
