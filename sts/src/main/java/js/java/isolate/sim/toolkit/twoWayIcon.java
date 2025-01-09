package js.java.isolate.sim.toolkit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import js.java.tools.gui.GraphicTools;

public class twoWayIcon implements Icon {
   protected final int LEFT = 10;
   protected final int TOP = 5;
   protected final int WIDTH = 50;
   protected final int HEIGHT = 80;
   protected Color line1col = new Color(0, 0, 0, 128);
   protected Color line2col = new Color(0, 190, 0);
   protected boolean blinkOn = false;

   public void blinkSwitch() {
      this.blinkOn = !this.blinkOn;
   }

   public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D)g.create(x, y, 50, 80);
      this.paintWay(g2);
      g2.dispose();
   }

   public int getIconWidth() {
      return 50;
   }

   public int getIconHeight() {
      return 80;
   }

   protected void line(Graphics2D g2, int x1, int y1, int x2, int y2) {
      Point start = new Point(x1, y1);
      Point end = new Point(x2, y2);
      Shape arrow = GraphicTools.createArrow(start, end);
      g2.drawLine(start.x, start.y, end.x, end.y);
      g2.fill(arrow);
      g2.draw(arrow);
   }

   protected void drawIcon(Graphics2D g2, ImageIcon ic) {
      g2.drawImage(ic.getImage(), (50 - ic.getIconWidth()) / 2, (80 - ic.getIconHeight()) / 2, null);
   }

   protected void paintWay(Graphics2D g2) {
      GraphicTools.enableGfxAA(g2);
      g2.setColor(this.line2col);
      g2.setStroke(new BasicStroke(2.0F));
      this.line(g2, 10, 5, 40, 40);
      this.line(g2, 40, 40, 10, 75);
      g2.setColor(this.line1col);
      g2.setStroke(new BasicStroke(3.0F));
      this.line(g2, 10, 5, 10, 75);
   }
}
