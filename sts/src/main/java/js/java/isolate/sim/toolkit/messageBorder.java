package js.java.isolate.sim.toolkit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;
import js.java.tools.gui.GraphicTools;

@Deprecated
public class messageBorder extends AbstractBorder {
   private static final int arcWidth = 20;
   private static final int arcHeight = 20;
   private boolean enabled = false;
   private boolean rolledOver = false;
   private Color disabledColor;
   private Color enabledColor;

   public messageBorder(Color enabledColor, Color disabledColor) {
      this.enabledColor = enabledColor;
      this.disabledColor = disabledColor;
   }

   public void setEnabled(boolean e) {
      this.enabled = e;
   }

   public void setOver(boolean b) {
      this.rolledOver = b;
   }

   public Insets getBorderInsets(Component c) {
      return this.getBorderInsets(c, new Insets(0, 0, 0, 0));
   }

   public Insets getBorderInsets(Component c, Insets insets) {
      insets.left = 2;
      insets.top = 1;
      insets.right = 1;
      insets.bottom = 0;
      return insets;
   }

   public boolean isBorderOpaque() {
      return false;
   }

   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color col;
      if (this.enabled) {
         col = this.enabledColor;
      } else {
         col = this.disabledColor;
      }

      if (this.rolledOver) {
         col = col.brighter();
      }

      g.setColor(col);
      GraphicTools.enableGfxAA((Graphics2D)g);
      g.fillRoundRect(x, y, width, height, 20, 20);
   }
}
