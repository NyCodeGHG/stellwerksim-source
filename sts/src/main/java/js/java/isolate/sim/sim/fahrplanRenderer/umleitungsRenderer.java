package js.java.isolate.sim.sim.fahrplanRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import js.java.isolate.sim.zug.fahrplanCollection.zugPlan;
import js.java.tools.gui.GraphicTools;

public class umleitungsRenderer extends rendererBase {
   private final zugPlan zp;
   private final Dimension dim;
   private final Color grey = new Color(224, 224, 224);

   public umleitungsRenderer(zugRenderer zr, zugPlan zp) {
      super(zr);
      this.zp = zp;
      this.setBackground(Color.WHITE);
      int l = 1;
      int m1 = 0;
      int m2 = 0;
      if (!zp.umleitungText.newway.isEmpty()) {
         m1 = 1 + zp.umleitungText.newway.size();
      }

      if (!zp.umleitungText.skipway.isEmpty()) {
         m2 = 1 + zp.umleitungText.skipway.size();
      }

      l += Math.max(m1, m2);
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

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableTextAA(g2);
      g2.setBackground(this.getBackground());
      g2.clearRect(0, 0, this.getWidth(), this.getHeight());
      int x = 0;
      int y = 0;
      int vw = this.stringWidth(g2, this.plainFont, this.zp.umleitungText.von);
      int nw = this.stringWidth(g2, this.plainFont, this.zp.umleitungText.nach);
      g2.setColor(Color.BLACK);
      x = this.drawString(g2, this.plainFont, "Zug fährt umgeleitet von ", x, 0);
      g2.setColor(this.grey);
      g2.fillRect(x - 2, 0, vw + 4, this.LINEHEIGHT);
      g2.setColor(Color.BLACK);
      x = this.drawString(g2, this.plainFont, this.zp.umleitungText.von, x, 0) + 4;
      x = this.drawString(g2, this.plainFont, "nach ", x, 0);
      g2.setColor(this.grey);
      g2.fillRect(x - 2, 0, nw + 4, this.LINEHEIGHT);
      g2.setColor(Color.BLACK);
      x = this.drawString(g2, this.plainFont, this.zp.umleitungText.nach, x, 0) + 4;
      int w = this.drawLines(g2, "über", this.zp.umleitungText.newway, 0, 1);
      this.drawLines(g2, "umfährt dabei", this.zp.umleitungText.skipway, Math.max(this.getWidth() / 2, w), 1);
   }

   private int drawLines(Graphics2D g2, String titel, LinkedList<String> list, int x0, int y0) {
      if (list.isEmpty()) {
         return y0;
      } else {
         g2.setColor(Color.BLACK);
         int x = this.drawString(g2, this.boldFont, titel, x0 + 10, y0 * this.LINEHEIGHT) + 5;
         g2.setColor(this.getBackground().darker());
         g2.drawLine(x, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2, x0 + this.getWidth() / 2, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2);
         g2.drawLine(x0, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2, x0 + 5, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2);
         g2.setColor(this.getBackground().brighter());
         g2.drawLine(x, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2 + 1, x0 + this.getWidth() / 2, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2 + 1);
         g2.drawLine(x0, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2 + 1, x0 + 5, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2 + 1);
         g2.setColor(Color.BLACK);
         int y = y0 + 1;
         int w = x;

         for(String s : list) {
            w = Math.max(this.drawString(g2, this.plainFont, s, x0 + 5, y * this.LINEHEIGHT), w);
            ++y;
         }

         g2.setColor(this.getBackground().darker());
         g2.drawLine(x0, y * this.LINEHEIGHT, x0, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2 + 1);
         g2.setColor(this.getBackground().brighter());
         g2.drawLine(
            Math.max(w, this.getWidth() / 2) - 1, y * this.LINEHEIGHT, Math.max(w, this.getWidth() / 2) - 1, y0 * this.LINEHEIGHT + this.LINEHEIGHT / 2 + 1
         );
         return w;
      }
   }
}
