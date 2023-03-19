package js.java.tools.gui.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.BevelBorder;

public class TopLineBorder extends BevelBorder {
   public TopLineBorder(int bevelType) {
      super(bevelType);
   }

   public TopLineBorder() {
      this(0);
   }

   public TopLineBorder(int bevelType, Color highlight, Color shadow) {
      super(bevelType, highlight, shadow);
   }

   public TopLineBorder(int bevelType, Color highlightOuterColor, Color highlightInnerColor, Color shadowOuterColor, Color shadowInnerColor) {
      super(bevelType, highlightOuterColor, highlightInnerColor, shadowOuterColor, shadowInnerColor);
   }

   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      g.translate(x, y);
      if (this.bevelType == 0) {
         g.setColor(this.getHighlightInnerColor(c));
         g.drawLine(0, 0, width - 1, 0);
         g.setColor(this.getShadowInnerColor(c));
         g.drawLine(0, 1, width - 1, 1);
      } else if (this.bevelType == 1) {
         g.setColor(this.getShadowInnerColor(c));
         g.drawLine(0, 0, width - 1, 0);
         g.setColor(this.getHighlightInnerColor(c));
         g.drawLine(0, 1, width - 1, 1);
      }

      g.translate(-x, -y);
      g.setColor(oldColor);
   }

   public Insets getBorderInsets(Component c) {
      return this.getBorderInsets(c, new Insets(0, 0, 0, 0));
   }

   public Insets getBorderInsets(Component c, Insets insets) {
      insets.top = 2;
      insets.left = insets.bottom = insets.right = 0;
      return insets;
   }

   public boolean isBorderOpaque() {
      return false;
   }
}
