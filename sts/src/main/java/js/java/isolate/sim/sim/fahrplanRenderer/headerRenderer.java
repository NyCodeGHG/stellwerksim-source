package js.java.isolate.sim.sim.fahrplanRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.zug.fahrplanCollection.zugPlan;
import js.java.tools.gui.GraphicTools;

public class headerRenderer extends clickableRenderer {
   private final zugPlan zp;
   private final Dimension dim;
   private final Color grey = new Color(224, 224, 224);
   private final boolean isFollower;

   public headerRenderer(zugRenderer zr, zugPlan zp, boolean isFollower) {
      super(zr);
      this.zp = zp;
      this.isFollower = isFollower;
      this.setBackground(Color.WHITE);
      int l = 2;
      if (zp.umleitungEinfahrt || zp.umleitungAusfahrt) {
         ++l;
      }

      this.dim = new Dimension(100, this.LINEHEIGHT * l + 2);
   }

   public Dimension getPreferredSize() {
      return this.dim;
   }

   public Dimension getMinimumSize() {
      return this.dim;
   }

   public Dimension getMaximumSize() {
      return this.dim;
   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableTextAA(g2);
      GraphicTools.enableGfxAA(g2);
      g2.setBackground(this.getBackground());
      g2.clearRect(0, 0, this.getWidth(), this.getHeight());
      int x = 5;
      if (this.isFollower) {
         g2.setColor(Color.WHITE.darker());
         g2.drawLine(0, 0, this.getWidth(), 0);
         g2.setColor(Color.BLACK);
         x = this.drawString(g2, this.plainFont, "Folgeplan: ", 0, 0) + 5;
      }

      int w = this.stringWidth(g2, this.boldFont, this.zp.name);
      g2.setColor(Color.BLACK);
      g2.fillRect(x - 5, 1, w + 10, this.LINEHEIGHT - 1);
      g2.setColor(Color.WHITE);
      x = this.drawString(g2, this.boldFont, this.zp.name, x, 0) + 6;
      g2.setColor(Color.BLACK);
      if (!this.isFollower) {
         if (this.zp.vorZug != null) {
            x += 3;
            g2.setColor(Color.WHITE.darker());
            g2.fillRect(x, 1, this.getWidth(), this.LINEHEIGHT - 1);
            g2.setColor(Color.BLACK);
            x += 2;
            Polygon triangle = new Polygon();
            triangle.addPoint(x, 2);
            triangle.addPoint(x, this.LINEHEIGHT - 2);
            triangle.addPoint(x + (this.LINEHEIGHT - 4), this.LINEHEIGHT / 2);
            g2.fillPolygon(triangle);
            x += this.LINEHEIGHT - 4 + 2;
            g2.setColor(Color.WHITE);
            x = this.drawString(g2, this.plainFont, "Folgeleistung aus |" + this.zp.vorZug + "|", x, 0, this.zp.vorZugZid) + 5;
            g2.setColor(Color.BLACK);
         }

         StringBuilder s = new StringBuilder();
         if (this.zp.halt) {
            if (!this.zp.anyDirection) {
               s.append(" außerplan.");
            }

            s.append(" Halt");
            if (this.zp.haltsignal != null) {
               s.append(" vor Signal ");
               s.append(this.zp.haltsignal);
            }
         }

         if (this.zp.langsam) {
            s.append(" Langsamfahrt angeordnet");
         }

         if (this.zp.warten) {
            s.append(" Warten angeordnet");
         }

         if (this.zp.notbremsung) {
            s.append(" Notbremsung");
         }

         this.drawString(g2, this.plainFont, s.toString(), x, 0);
      }

      x = 0;
      if (this.zp.von != null && !this.isFollower) {
         x = this.drawString(g2, this.plainFont, "von ", x, this.LINEHEIGHT);
         w = this.stringWidth(g2, this.plainFont, this.zp.von);
         if (this.zp.umleitungEinfahrt) {
            g2.setColor(Color.cyan);
         } else {
            g2.setColor(this.grey);
         }

         g2.fillRect(x - 2, this.LINEHEIGHT, w + 4, this.LINEHEIGHT);
         g2.setColor(Color.BLACK);
         x = this.drawString(g2, this.plainFont, this.zp.von, x, this.LINEHEIGHT) + 5;
      }

      if (this.zp.nach != null) {
         x = this.drawString(g2, this.plainFont, "nach ", x, this.LINEHEIGHT);
         w = this.stringWidth(g2, this.plainFont, this.zp.nach);
         if (this.zp.umleitungAusfahrt) {
            g2.setColor(Color.cyan);
         } else {
            g2.setColor(this.grey);
         }

         g2.fillRect(x - 2, this.LINEHEIGHT, w + 4, this.LINEHEIGHT);
         g2.setColor(Color.BLACK);
         this.drawString(g2, this.plainFont, this.zp.nach, x, this.LINEHEIGHT);
      }

      if (this.zp.umleitungEinfahrt || this.zp.umleitungAusfahrt) {
         g2.setColor(Color.BLACK);
         g2.fillRect(0, this.LINEHEIGHT * 2, this.getWidth(), this.LINEHEIGHT);
         if (gleis.blinkon_slow) {
            g2.setColor(Color.WHITE);
            int var13 = 0;
            var13 = this.drawString(g2, this.plainFont, "Zugumleitung! ", var13, this.LINEHEIGHT * 2);
         }

         this.triggerRepaint();
      }
   }
}
