package js.java.isolate.sim.sim.fahrplanRenderer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class clickableRenderer extends rendererBase implements MouseListener, MouseMotionListener {
   protected static final Rectangle NULLRECT = new Rectangle();
   protected Rectangle clickArea = NULLRECT;
   protected int clickZid;
   private boolean overClickArea = false;
   private boolean lastOverValue = false;

   protected clickableRenderer(zugRenderer zr) {
      super(zr);
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
   }

   public void paintComponent(Graphics g) {
   }

   protected int drawString(Graphics2D g2, Font font, String text, int x, int y, int linkzid) {
      try {
         Color oldcol = g2.getColor();
         g2.setFont(font);
         FontMetrics fm = g2.getFontMetrics();
         String[] tokens = text.split("\\|");

         for(int i = 0; i < tokens.length; ++i) {
            if (i == 1) {
               if (this.overClickArea) {
                  g2.setColor(Color.RED);
                  this.setCursor(Cursor.getPredefinedCursor(12));
               } else {
                  g2.setColor(Color.BLUE);
                  this.setCursor(Cursor.getDefaultCursor());
               }
            }

            g2.drawString(tokens[i], x, y + fm.getAscent());
            if (i == 1) {
               int w = fm.stringWidth(tokens[i]);
               g2.drawLine(x, y + fm.getAscent() + 1, x + w - 1, y + fm.getAscent() + 1);
               g2.setColor(oldcol);
               this.clickArea = new Rectangle(x - 2, y - 2, w + 4, fm.getHeight() + 4);
               this.clickZid = linkzid;
            }

            x += fm.stringWidth(tokens[i]);
         }

         return x;
      } catch (NullPointerException var12) {
         return x;
      }
   }

   public void mouseClicked(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
      if (this.clickArea != NULLRECT && this.clickArea.contains(e.getPoint())) {
         this.zr.gotoZid(this.clickZid);
      }
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
      this.overClickArea = false;
      if (this.lastOverValue != this.overClickArea) {
         this.lastOverValue = this.overClickArea;
         this.repaint();
      }
   }

   public void mouseDragged(MouseEvent e) {
   }

   public void mouseMoved(MouseEvent e) {
      this.overClickArea = this.clickArea != NULLRECT && this.clickArea.contains(e.getPoint());
      if (this.lastOverValue != this.overClickArea) {
         this.lastOverValue = this.overClickArea;
         this.repaint();
      }
   }
}
