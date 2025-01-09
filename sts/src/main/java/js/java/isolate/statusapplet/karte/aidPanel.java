package js.java.isolate.statusapplet.karte;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.GregorianCalendar;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JViewport;
import javax.swing.Timer;
import js.java.tools.gui.GraphicTools;

class aidPanel extends JComponent implements ActionListener, Comparable {
   private static final int MAXFULLSCALE = 40;
   private static final String fontname = "Dialog";
   private static final int planfontheight = 8;
   private static final int fontheight = 9;
   private static final int fontheightBig = 12;
   private final kartePanel zp;
   private final karten_container kc;
   private Color cback = Color.WHITE;
   private final Color cfront = Color.BLACK;
   private Color cborder = Color.BLACK;
   private final Color trans = new Color(0, 0, 0, 0);
   private final Font font;
   private final Font fontBig;
   private Timer tm = null;
   private JLabel buildlabel = null;
   private JViewport myvp = null;
   private GregorianCalendar c = new GregorianCalendar();

   aidPanel(kartePanel _zp, karten_container k) {
      this.zp = _zp;
      this.kc = k;
      this.updateOutput();
      this.setDoubleBuffered(true);
      this.font = new Font("Dialog", 0, 9);
      this.fontBig = new Font("Dialog", 0, 12);
      this.setPos();
      if (this.kc.aaid <= 0 || !this.kc.netznames.equalsIgnoreCase(this.zp.getRegion())) {
         this.setOpaque(false);
         this.cback = Color.LIGHT_GRAY;
      } else if (this.kc.sichtbar) {
         this.tm = new Timer(5000, this);
         this.tm.start();
      } else {
         this.cback = Color.LIGHT_GRAY;
         this.buildlabel = new JLabel();
         this.buildlabel.setIcon(new ImageIcon(this.getClass().getResource("baustelle.gif")));
         this.add(this.buildlabel);
         this.buildlabel.setSize(46, 43);
      }

      this.setDoubleBuffered(true);
   }

   public void resize() {
      this.setPos();
      this.repaint();
   }

   public void setPos() {
      int x1 = Math.round((float)this.kc.x * this.zp.getScale());
      int y1 = Math.round((float)this.kc.y * this.zp.getScale());
      int x2 = Math.round(((float)this.kc.x + 4.0F) * this.zp.getScale());
      int y2 = Math.round(((float)this.kc.y + 1.0F) * this.zp.getScale());
      if (this.buildlabel != null) {
         this.buildlabel.setVisible(false);
      }

      this.setLocation(x1, y1);
      this.setSize(x2 - x1, y2 - y1);
      if (this.buildlabel != null) {
         this.buildlabel.setLocation(this.getWidth() - this.buildlabel.getWidth() - 2, this.getHeight() - this.buildlabel.getHeight() - 2);
      }
   }

   public void actionPerformed(ActionEvent e) {
   }

   public void updateOutput() {
      this.cborder = this.cfront;

      try {
         if (this.kc.spieler != null) {
            this.setToolTipText("Fdl: " + this.kc.spieler);
            this.cborder = Color.RED;
         } else {
            this.setToolTipText("");
         }
      } catch (Exception var2) {
      }
   }

   public void setViewport(JViewport _myvp) {
      this.myvp = _myvp;
   }

   protected boolean isVisibleVP() {
      boolean isVisible = true;
      if (this.myvp != null) {
         Rectangle vrec = this.myvp.getViewRect();
         isVisible = vrec.intersects((double)this.getX(), (double)this.getY(), (double)this.getWidth(), (double)this.getHeight());
      }

      return isVisible;
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      this.updateOutput();
      if (this.isVisibleVP()) {
         try {
            Graphics2D g2 = (Graphics2D)g;
            GraphicTools.enableTextAA(g2);
            int w = this.getWidth();
            int h = this.getHeight();
            if (this.kc.erid <= 0 && this.kc.netznames.equalsIgnoreCase(this.zp.getRegion())) {
               g2.setColor(this.cback);
               g2.fillRect(0, 0, w, h);
               if (this.kc.spieler == null && !(this.zp.getScale() < 40.0F)) {
                  g2.setFont(this.fontBig);
               } else {
                  g2.setFont(this.font);
               }

               FontMetrics mfont = g2.getFontMetrics();
               String text = this.kc.namen;
               g2.setColor(this.cborder);
               g2.drawRect(0, 0, w - 1, h - 1);
               g2.setColor(this.cfront);
               g2.drawString(text, 4, mfont.getHeight() - 2);
               if (!this.kc.sichtbar) {
                  g2.drawString("(im Bau)", 4, (mfont.getHeight() - 2) * 2);
               } else if (this.kc.spieler != null) {
                  g2.setColor(Color.LIGHT_GRAY);
                  g2.fillRect(1, mfont.getHeight(), w - 3, mfont.getHeight());
                  g2.setColor(this.cfront);
                  g2.drawString(this.kc.spieler, 4, (mfont.getHeight() - 2) * 2);
                  Rectangle2D rect = mfont.getStringBounds(this.kc.stitz, g2);
                  if (this.kc.canstitz) {
                     g2.setColor(Color.BLUE);
                  }

                  g2.drawString(this.kc.stitz, w - 4 - (int)rect.getWidth(), (mfont.getHeight() - 2) * 2);
               }
            } else {
               g2.setColor(this.trans);
               g2.drawRect(0, 0, w - 1, h - 1);
               if (this.zp.getScale() > 40.0F) {
                  g2.setFont(this.fontBig);
               } else {
                  g2.setFont(this.font);
               }

               FontMetrics mfont = g2.getFontMetrics();
               String text = this.kc.namen;
               if (this.kc.erid > 0) {
                  karten_container k1 = (karten_container)this.zp.rnamen.get(this.kc.erid);
                  text = k1.rnamen;
               }

               float[] dash = new float[]{5.0F, 5.0F};
               g2.setStroke(new BasicStroke(1.0F, 0, 2, 1.0F, dash, 0.0F));
               Rectangle2D rect = mfont.getStringBounds(text, g2);
               g2.setColor(this.cback);
               g2.fillOval(0, 0, w - 1, h - 1);
               g2.setColor(this.cborder);
               g2.drawOval(0, 0, w - 1, h - 1);
               g2.setColor(this.cfront);
               g2.drawString(text, (w - (int)rect.getWidth()) / 2, (h - (int)rect.getHeight()) / 2 + mfont.getHeight() - 2);
               g2.setStroke(new BasicStroke(1.0F));
            }
         } catch (Exception var10) {
         }
      }
   }

   void update(karten_zug kz) {
   }

   void update() {
      this.repaint();
   }

   void newPlayer() {
      this.repaint();
   }

   public void remove() {
      if (this.tm != null) {
         this.tm.stop();
      }

      this.getParent().remove(this);
   }

   public int compareTo(Object o) {
      aidPanel o2 = (aidPanel)o;
      int r = 0;
      if (this.kc.y < o2.kc.y) {
         r = -1;
      } else if (this.kc.y > o2.kc.y) {
         r = 1;
      }

      if (r == 0 && this.kc.x < o2.kc.x) {
         r = -1;
      } else if (r == 0 && this.kc.x > o2.kc.x) {
         r = 1;
      }

      return r;
   }
}
