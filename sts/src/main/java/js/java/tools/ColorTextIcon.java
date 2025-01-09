package js.java.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

public class ColorTextIcon implements Icon {
   private int width;
   private int height;
   static final int DEFAULT_WIDTH = 15;
   static final int DEFAULT_HEIGHT = 15;
   ColorText t = null;

   public ColorTextIcon() {
      this(15, 15);
   }

   public ColorTextIcon(ColorText _t) {
      this(15, 15, _t);
   }

   public ColorTextIcon(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public ColorTextIcon(int width, int height, ColorText _t) {
      this.width = width;
      this.height = height;
      this.t = _t;
   }

   public void setText(ColorText c) {
      this.t = c;
   }

   public int getIconHeight() {
      return this.height;
   }

   public int getIconWidth() {
      return this.width;
   }

   public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D bgfx = (Graphics2D)g.create(x, y, this.width, this.height);
      bgfx.setBackground(c.getBackground());
      bgfx.clearRect(0, 0, this.width, this.height);
      bgfx.setColor(this.t.getBGColor());
      bgfx.fillRect(0, 0, this.width - 1, this.height - 1);
      bgfx.draw3DRect(1, 1, this.width - 3, this.height - 3, false);
      bgfx.setColor(Color.BLACK);
      bgfx.drawRect(0, 0, this.width - 1, this.height - 1);
   }
}
