package js.java.tools.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JComponent;

public class textticker extends JComponent {
   private ConcurrentLinkedQueue<String> textlist = null;
   private ConcurrentLinkedQueue<String> importanttextlist = null;
   private final Timer runner;
   private int fontsize;
   private Font f;
   private int width = 0;
   private int height = 0;
   private BufferedImage offscreen = null;
   private BufferedImage runscreen = null;
   private String text;
   private final int bordersize = 1;
   private int x = 0;
   private Color bgcolor = Color.WHITE;
   private Color bgcolor_fx = Color.WHITE;
   private final Color hintTextColor = Color.BLUE;
   private final Color standardTextColor = Color.BLACK;
   private Color textcolor = Color.BLACK;
   private boolean pause = false;
   private boolean onlyText = false;
   private int logoImage = 0;

   public textticker() {
      this(10);
   }

   public textticker(int fs) {
      super();
      this.textlist = new ConcurrentLinkedQueue();
      this.importanttextlist = new ConcurrentLinkedQueue();
      this.fontsize = fs;
      this.f = new Font("sans-serif", 0, this.fontsize);
      this.setDoubleBuffered(false);
      this.setOpaque(true);
      this.runner = new Timer("textticker");
      this.runner.scheduleAtFixedRate(new TimerTask() {
         public void run() {
            textticker.this.runticker();
         }
      }, 0L, 45L);
   }

   private void initComponents() {
      this.setDoubleBuffered(false);
      this.setLayout(new BorderLayout());
   }

   private int printtext(Graphics g, String text, Color col, int x, int y) {
      g.setFont(this.f);
      FontMetrics fm = g.getFontMetrics();
      g.setColor(this.bgcolor_fx);
      g.drawString(text, x + 1, y + fm.getAscent() + 1);
      g.drawString(text, x - 1, y + fm.getAscent() - 1);
      g.setColor(col);
      g.drawString(text, x, y + fm.getAscent());
      return fm.stringWidth(text);
   }

   private void runticker() {
      if (this.width != this.getWidth() || this.height != this.getHeight()) {
         this.runscreen = null;
      }

      if (this.runscreen == null) {
         this.width = this.getWidth();
         this.height = this.getHeight();
         if (this.width <= 0 || this.height <= 0) {
            return;
         }

         this.bgcolor = this.getBackground();
         float[] hsb = Color.RGBtoHSB(this.bgcolor.getRed(), this.bgcolor.getGreen(), this.bgcolor.getBlue(), null);
         hsb[2] = Math.max(0.0F, hsb[2] * 0.95F);
         this.bgcolor_fx = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
         GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice gs = ge.getDefaultScreenDevice();
         GraphicsConfiguration gc = gs.getDefaultConfiguration();
         this.runscreen = gc.createCompatibleImage(this.width, this.height, 1);
         Graphics2D g = this.runscreen.createGraphics();
         g.setColor(this.bgcolor);
         g.fillRect(0, 0, this.width, this.height);
         g.dispose();
         BufferedImage offscreen_tmp = gc.createCompatibleImage(this.width, this.height, 1);
         if (offscreen_tmp != null) {
            this.offscreen = offscreen_tmp;
         }
      }

      if (this.offscreen != null) {
         Graphics2D og = this.offscreen.createGraphics();
         og.drawImage(this.runscreen, 0, 0, null);
         og.dispose();
         this.repaint();
      }

      if (!this.pause) {
         Graphics2D g = this.runscreen.createGraphics();
         g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g.copyArea(0, 0, this.width, this.height, -1, 0);
         g.setColor(this.bgcolor);
         g.fillRect(this.x - 15, 0, this.width, this.height);
         if (this.text == null) {
            this.logoImage = 0;
            this.textcolor = this.standardTextColor;
            if (!this.importanttextlist.isEmpty()) {
               this.text = (String)this.importanttextlist.poll();
               this.x = this.width;
               this.logoImage = 1;
            } else if (!this.textlist.isEmpty()) {
               if (!this.onlyText) {
                  this.text = (String)this.textlist.poll();
               } else {
                  this.text = (String)this.textlist.peek();
               }

               if (this.text.startsWith("*H#")) {
                  this.text = this.text.substring(3);
                  this.textcolor = this.hintTextColor;
                  this.logoImage = 2;
               }

               this.x = this.width;
            }

            if (this.text != null) {
               this.text = this.text + " ";
            }
         }

         g.setColor(this.bgcolor);
         g.fillRect(this.width - 1, 0, 1, this.height);
         if (this.text != null) {
            int ww = 0;
            if (this.logoImage > 0) {
               switch(this.logoImage) {
                  case 1:
                     g.setColor(Color.RED);
                     g.fillRect(this.x, 2, 10, 10);
                     break;
                  case 2:
                     g.setColor(Color.BLUE);
                     g.fillOval(this.x + 2, 4, 6, 6);
               }
            }

            try {
               ww = this.printtext(g, this.text, this.textcolor, this.x + (this.logoImage > 0 ? 12 : 0), 0) + 40;
            } catch (ArrayIndexOutOfBoundsException var7) {
            }

            --this.x;
            if (this.x + ww < this.width) {
               this.text = null;
               this.x = this.width;
            }
         }

         g.dispose();
      }
   }

   public void paintComponent(Graphics g) {
      g.setColor(this.getBackground());
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      if (this.offscreen != null) {
         g.drawImage(this.offscreen, 1, 0, null);
      } else {
         super.paintComponent(g);
      }
   }

   public void setText(String t) {
      this.addText(t);
   }

   public void addText(String t) {
      this.onlyText = false;
      if (!this.textlist.contains(t)) {
         this.textlist.add(t);
      }
   }

   public void addText(String t, boolean hint) {
      this.onlyText = false;
      if (hint) {
         t = "*H#" + t;
      }

      if (!this.textlist.contains(t)) {
         this.textlist.add(t);
      }
   }

   public void addImportantText(String t) {
      this.importanttextlist.add(t);
   }

   public void setOnlyText(String t) {
      this.onlyText = true;
      this.textlist.clear();
      this.textlist.add(t);
   }

   public void stopRunning() {
      this.runner.cancel();
      this.runner.purge();
   }

   public void setPause(boolean p) {
      this.pause = p;
   }

   public boolean isPause() {
      return this.pause;
   }
}
