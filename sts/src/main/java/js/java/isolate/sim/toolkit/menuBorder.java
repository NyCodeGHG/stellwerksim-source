package js.java.isolate.sim.toolkit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;

public class menuBorder extends AbstractBorder {
   protected static final int EDGE_SPACING = 0;
   protected static final int TEXT_SPACING = 2;
   protected static final int TEXT_INSET_H = 5;
   public static final int LEFT = 1;
   public static final int CENTER = 2;
   public static final int RIGHT = 3;
   private String title = "";
   private Point textLoc = new Point();
   private int justification = 1;
   private boolean fillHgr = true;

   public menuBorder(String _title) {
      this.title = _title;
   }

   public void setFillBackground(boolean s) {
      this.fillHgr = s;
   }

   public boolean isFillBackground() {
      return this.fillHgr;
   }

   private static boolean computeIntersection(Rectangle dest, int rx, int ry, int rw, int rh) {
      int x1 = Math.max(rx, dest.x);
      int x2 = Math.min(rx + rw, dest.x + dest.width);
      int y1 = Math.max(ry, dest.y);
      int y2 = Math.min(ry + rh, dest.y + dest.height);
      dest.x = x1;
      dest.y = y1;
      dest.width = x2 - x1;
      dest.height = y2 - y1;
      return dest.width > 0 && dest.height > 0;
   }

   protected Color getHighlightOuterColor(Component c) {
      return c.getBackground().brighter().brighter();
   }

   protected Color getHighlightInnerColor(Component c) {
      return c.getBackground().brighter();
   }

   protected Color getShadowInnerColor(Component c) {
      return c.getBackground().darker();
   }

   protected Color getShadowOuterColor(Component c) {
      return UIManager.getDefaults().getColor("TitledBorder.titleColor");
   }

   protected void paintBgr(Component c, Graphics g, int x, int y, int width, int height, int offs) {
      Graphics2D g2 = (Graphics2D)g;
      g2.translate(x, y);
      g2.setColor(this.getShadowOuterColor(c));
      GradientPaint gp = new GradientPaint(0.0F, 0.0F, c.getBackground(), (float)(width / 20), (float)height, this.getShadowInnerColor(c), false);
      g2.setPaint(gp);
      g2.fillRect(1, offs, width - 3, height - offs);
      g2.setPaint(null);
      g2.translate(-x, -y);
   }

   protected void paintBorder2(Component c, Graphics g, int x, int y, int width, int height, int offs) {
      g.translate(x, y);
      g.setColor(this.getShadowInnerColor(c));
      g.fillRect(1, offs, width - 3, 2);
      g.fillRect(1, offs, 2, 10);
      g.fillRect(width - 4, offs, 2, 10);
      g.translate(-x, -y);
   }

   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Color oldColor = g.getColor();
      JComponent jc = c instanceof JComponent ? (JComponent)c : null;
      Font font = c.getFont();
      FontMetrics fm = c.getFontMetrics(font);
      int fontHeight = fm.getHeight();
      int descent = fm.getDescent();
      int ascent = fm.getAscent();
      int stringWidth = fm.stringWidth(this.title);
      Insets insets = this.getBorderInsets(c);
      Rectangle grooveRect = new Rectangle(x + 0, y + 0, width - 0, height - 0);
      this.textLoc.y = grooveRect.y - descent + (insets.top + ascent + descent) / 2;
      switch (this.justification) {
         case 1:
            this.textLoc.x = grooveRect.x + 5 + insets.left;
            break;
         case 2:
            this.textLoc.x = grooveRect.x + (grooveRect.width - stringWidth) / 2;
            break;
         case 3:
            this.textLoc.x = grooveRect.x + grooveRect.width - (stringWidth + 5 + insets.right);
      }

      if (this.fillHgr) {
         this.paintBgr(c, g, grooveRect.x, grooveRect.y, grooveRect.width, grooveRect.height, fontHeight / 2);
      }

      Rectangle clipRect = new Rectangle();
      Rectangle saveClip = g.getClipBounds();
      clipRect.setBounds(saveClip);
      if (computeIntersection(clipRect, x, y, this.textLoc.x - 1 - x, height)) {
         g.setClip(clipRect);
         this.paintBorder2(c, g, grooveRect.x, grooveRect.y, grooveRect.width, grooveRect.height, fontHeight / 2);
      }

      clipRect.setBounds(saveClip);
      if (computeIntersection(clipRect, this.textLoc.x + stringWidth + 1, y, x + width - (this.textLoc.x + stringWidth + 1), height)) {
         g.setClip(clipRect);
         this.paintBorder2(c, g, grooveRect.x, grooveRect.y, grooveRect.width, grooveRect.height, fontHeight / 2);
      }

      clipRect.setBounds(saveClip);
      if (computeIntersection(clipRect, this.textLoc.x - 1, this.textLoc.y + descent, stringWidth + 2, y + height - this.textLoc.y - descent)) {
         g.setClip(clipRect);
         this.paintBorder2(c, g, grooveRect.x, grooveRect.y, grooveRect.width, grooveRect.height, fontHeight / 2);
      }

      g.setClip(saveClip);
      g.setColor(c.getBackground());
      g.drawString(this.title, this.textLoc.x + 1, this.textLoc.y + 1);
      g.setColor(this.getShadowOuterColor(jc));
      g.drawString(this.title, this.textLoc.x, this.textLoc.y);
      g.setColor(oldColor);
   }

   public Insets getBorderInsets(Component c) {
      return this.getBorderInsets(c, new Insets(0, 0, 0, 0));
   }

   public Insets getBorderInsets(Component c, Insets insets) {
      Font font = c.getFont();
      FontMetrics fm = c.getFontMetrics(font);
      int height = 10;
      if (fm != null) {
         height = fm.getHeight();
      }

      insets.left = 2;
      insets.top = height + 4;
      insets.right = 2;
      insets.bottom = 0;
      return insets;
   }

   public boolean isBorderOpaque() {
      return false;
   }
}
