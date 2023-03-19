package js.java.isolate.sim.toolkit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.BevelBorder;

public class itemBorder extends BevelBorder {
   public itemBorder(int bevelType) {
      super(bevelType);
   }

   public itemBorder(int bevelType, Color highlight, Color shadow) {
      super(bevelType, highlight, shadow);
   }

   public itemBorder(int bevelType, Color highlightOuterColor, Color highlightInnerColor, Color shadowOuterColor, Color shadowInnerColor) {
      super(bevelType, highlightOuterColor, highlightInnerColor, shadowOuterColor, shadowInnerColor);
   }

   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      g.translate(x, y);
      if (this.bevelType == 0) {
         g.setColor(this.getHighlightOuterColor(c));
         g.drawLine(0, 0, width - 2, 0);
         g.drawLine(0, 0, 0, height - 2);
         g.drawLine(1, 1, 1, 1);
         g.setColor(this.getHighlightInnerColor(c));
         g.drawLine(2, 1, width - 2, 1);
         g.drawLine(1, 2, 1, height - 2);
         g.drawLine(2, 2, 2, 2);
         g.drawLine(width - 1, 0, width - 1, 0);
         g.setColor(this.getShadowOuterColor(c));
         g.drawLine(width - 1, 2, width - 1, height - 1);
      } else if (this.bevelType == 1) {
         g.setColor(this.getShadowOuterColor(c));
         g.drawLine(0, 0, width - 2, 0);
         g.drawLine(0, 0, 0, height - 2);
         g.drawLine(1, 1, 1, 1);
         g.setColor(this.getShadowInnerColor(c));
         g.drawLine(2, 1, width - 2, 1);
         g.drawLine(1, 2, 1, height - 2);
         g.drawLine(2, 2, 2, 2);
         g.drawLine(0, height - 1, 0, height - 2);
         g.drawLine(width - 1, 0, width - 1, 0);
         g.setColor(this.getHighlightOuterColor(c));
         g.drawLine(2, height - 1, width - 1, height - 1);
         g.drawLine(width - 1, 2, width - 1, height - 1);
         g.setColor(this.getHighlightInnerColor(c));
         g.drawLine(width - 2, height - 2, width - 2, height - 2);
      }

      g.translate(-x, -y);
      g.setColor(oldColor);
   }

   public Insets getBorderInsets(Component c) {
      return this.getBorderInsets(c, new Insets(0, 0, 0, 0));
   }

   public Insets getBorderInsets(Component c, Insets insets) {
      insets.top = insets.left = insets.bottom = insets.right = 3;
      return insets;
   }

   public boolean isBorderOpaque() {
      return false;
   }
}
