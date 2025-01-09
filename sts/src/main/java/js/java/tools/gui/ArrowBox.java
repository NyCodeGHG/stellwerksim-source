package js.java.tools.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import javax.swing.JComponent;

public class ArrowBox extends JComponent {
   public ArrowBox() {
      this.setMinimumSize(new Dimension(30, 50));
      this.setPreferredSize(new Dimension(30, 50));
      this.setMaximumSize(new Dimension(30, Integer.MAX_VALUE));
   }

   private void line(Graphics2D g2, int x1, int y1, int x2, int y2) {
      Point start = new Point(x1, y1);
      Point end = new Point(x2, y2);
      Shape arrow = GraphicTools.createArrow(start, end);
      g2.drawLine(start.x, start.y, end.x, end.y);
      g2.fill(arrow);
      g2.draw(arrow);
   }

   public void paintComponent(Graphics g) {
      g.setColor(this.getBackground());
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableGfxAA(g2);
      g2.setColor(Color.BLACK);
      g2.setStroke(new BasicStroke(2.0F));
      this.line(g2, this.getWidth() / 2, 10, this.getWidth() / 2, this.getHeight() - 10);
   }
}
