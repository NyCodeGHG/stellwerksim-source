package js.java.tools.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class SimpleToggleButton extends JToggleButton {
   private boolean alternativColor = false;
   private boolean cellHasFocus = false;
   protected char shortKey = ' ';
   protected static final int SHORTKEYWIDTH = 9;
   private boolean specialToolTipLocation = false;

   public SimpleToggleButton(String text) {
      super(text);
      this.setMargin(new Insets(1, 1, 1, 1));
      this.setBorder(new EmptyBorder(5, 5, 5, 5));
   }

   public SimpleToggleButton() {
      super();
      this.setMargin(new Insets(1, 1, 1, 1));
      this.setBorder(new EmptyBorder(5, 5, 5, 5));
   }

   public void setAlternativColor(boolean e) {
      this.alternativColor = e;
   }

   protected void setHasCellFocus(boolean cellHasFocus) {
      this.cellHasFocus = cellHasFocus;
   }

   public void setShortKey(char c) {
      this.shortKey = c;
   }

   public void setMnemonic(int m) {
      super.setMnemonic(m);
      this.shortKey = KeyEvent.getKeyText(m).charAt(0);
   }

   public void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D)g;
      int w = this.getWidth();
      int h = this.getHeight();
      int x = 0;
      int y = 0;

      try {
         g2.setBackground(this.getBackground());
         g2.clearRect(0, 0, w, h);
      } catch (Exception var13) {
      }

      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      Insets is = this.getMargin();
      x += is.left;
      y += is.top;
      w -= is.right + is.left;
      h -= is.bottom + is.top;
      if (this.isBorderPainted() && this.isEnabled()) {
         g2.setColor(Color.BLACK);
         g2.fillRect(x, y, w - 1, h - 1);
      }

      Color[] c = this.getColors();
      Color bgcol = c[1];
      Color fgcol = c[0];
      boolean raised = !this.isEnabled() || !this.isSelected() && !this.getModel().isArmed();
      g2.setColor(bgcol);
      if (this.isEnabled()) {
         g2.fill3DRect(x + 1, y + 1, w - 3, h - 3, raised);
      }

      GradientPaint gp;
      if (this.alternativColor) {
         gp = new GradientPaint(0.0F, 0.0F, bgcol.brighter(), (float)this.getWidth(), (float)this.getHeight(), bgcol.darker(), false);
      } else {
         gp = new GradientPaint(0.0F, 0.0F, bgcol.darker(), (float)this.getWidth(), (float)this.getHeight(), bgcol.brighter(), false);
      }

      if (this.isSelected() || !this.getModel().isRollover() || this.getModel().isPressed()) {
         g2.setPaint(gp);
      }

      g2.fillRect(x + 2, y + 2, w - 5, h - 5);
      this.paintContent(g2, x, y, w, h, fgcol, bgcol, raised);
      if (this.cellHasFocus || this.isFocusPainted() && this.hasFocus()) {
         float[] dashes = new float[]{2.0F, 4.0F};
         g2.setStroke(new BasicStroke(1.0F, 0, 2, 1.0F, dashes, 0.0F));
         g2.setColor(Color.BLACK);
         g2.drawRect(x + 4, y + 4, w - 9, h - 9);
         g2.setStroke(new BasicStroke(1.0F));
      }
   }

   protected Color[] getColors() {
      Color fgcol;
      Color bgcol;
      if (!this.isEnabled()) {
         bgcol = UIManager.getDefaults().getColor("MenuItem.background");
         fgcol = UIManager.getDefaults().getColor("MenuItem.disabledForeground");
      } else if (this.isSelected()) {
         bgcol = UIManager.getDefaults().getColor("MenuItem.selectionBackground");
         fgcol = UIManager.getDefaults().getColor("MenuItem.selectionForeground");
      } else {
         bgcol = UIManager.getDefaults().getColor("MenuItem.background");
         fgcol = UIManager.getDefaults().getColor("MenuItem.foreground");
      }

      return new Color[]{fgcol, bgcol};
   }

   protected void paintContent(Graphics2D g2, int x, int y, int w, int h, Color fgcol, Color bgcol, boolean raised) {
      Icon icn = this.getIcon();
      if (icn != null) {
         if (!this.isEnabled()) {
            icn = this.toGray(icn);
         }

         int yy = y + (h - icn.getIconHeight()) / 2;
         Graphics2D gicon = (Graphics2D)g2.create(x + 2, yy, h - 5, h - 5);
         icn.paintIcon(this, gicon, 0, 0);
      }

      g2.setColor(fgcol);
      g2.setFont(this.getFont());
      FontMetrics fm = g2.getFontMetrics();
      String text = this.getText();
      int tw = fm.stringWidth(text);
      if (icn != null) {
         g2.drawString(
            text, this.getIconTextGap() + 2 + icn.getIconWidth() + x + (raised ? 0 : 1), y + (h + fm.getHeight()) / 2 - fm.getDescent() + (raised ? 0 : 1)
         );
      } else {
         int ww = w;
         if (this.shortKey != ' ') {
            ww = w - 9;
         }

         g2.drawString(text, x + (ww - tw) / 2 + (raised ? 0 : 1), y + (h + fm.getHeight()) / 2 - fm.getDescent() + (raised ? 0 : 1));
      }

      if (this.shortKey != ' ') {
         int rx = w - 1 - 9;
         int ry = y + 1;
         g2.setColor(fgcol);
         g2.fillRect(rx, ry, 9, 9);
         g2.setColor(bgcol);
         Font nfont = this.getFont().deriveFont(4);
         g2.setFont(nfont);
         fm = g2.getFontMetrics();
         String t = Character.toString(this.shortKey);
         int sw = fm.stringWidth(t);
         g2.drawString(t, rx + (9 - sw) / 2, ry + (9 + fm.getHeight()) / 2 - fm.getDescent());
      }
   }

   protected Icon toGray(Icon icon) {
      return (Icon)(icon instanceof ImageIcon ? GraphicTools.toGray((ImageIcon)icon) : icon);
   }

   public Dimension getMaximumSize() {
      if (this.shortKey != ' ') {
         Dimension d = new Dimension(super.getMaximumSize());
         d.width += 9;
         return d;
      } else {
         return super.getMaximumSize();
      }
   }

   public Dimension getMinimumSize() {
      if (this.shortKey != ' ') {
         Dimension d = new Dimension(super.getMinimumSize());
         d.width += 9;
         return d;
      } else {
         return super.getMinimumSize();
      }
   }

   public Dimension getPreferredSize() {
      if (this.shortKey != ' ') {
         Dimension d = new Dimension(super.getPreferredSize());
         d.width += 9;
         return d;
      } else {
         return super.getPreferredSize();
      }
   }

   public Point getToolTipLocation(MouseEvent e) {
      if (this.specialToolTipLocation) {
         Rectangle r = this.getVisibleRect();
         return new Point(r.x + r.width - 5, 2);
      } else {
         return super.getToolTipLocation(e);
      }
   }

   public void setSpecialToopTipLocation(boolean enable) {
      this.specialToolTipLocation = enable;
   }

   public static class FlashingSimpleButton extends SimpleToggleButton implements ActionListener {
      private static final int FLASHSTEPS = 20;
      private boolean flashing = false;
      private Color[] heavyFlashColors;
      private Color[] lightFlashColors;
      private int flashingPos = 0;
      private int heavyFlashCount = 0;
      private Timer flashTimer = new Timer(100, this);
      private Color destinationColor = Color.RED;

      public FlashingSimpleButton() {
         super();
         this.prepareFlashColors();
      }

      public FlashingSimpleButton(String text) {
         super(text);
         this.prepareFlashColors();
      }

      public void setDestinationColor(Color d) {
         this.destinationColor = d;
      }

      public boolean isFlashing() {
         return this.flashing;
      }

      public void setFlashing(boolean f) {
         this.flashing = f;
         if (this.flashing) {
            this.flashingPos = 0;
            this.heavyFlashCount = 5;
            this.flashTimer.start();
         } else {
            this.flashTimer.stop();
            this.repaint();
         }
      }

      public void actionPerformed(ActionEvent e) {
         ++this.flashingPos;
         if (this.flashingPos >= 20) {
            this.flashingPos = 0;
            if (this.heavyFlashCount > 0) {
               --this.heavyFlashCount;
            }
         }

         this.repaint();
      }

      private int mixColor(int step, int steps, int c0, int c1) {
         int d = c0 - c1;
         return c0 - d / steps * step;
      }

      private void prepareFlashColors() {
         this.heavyFlashColors = this.prepareFlashColors(this.destinationColor);
         Color[] r = super.getColors();
         Color bgcol = r[1];
         int r0 = bgcol.getRed();
         int g0 = bgcol.getGreen();
         int b0 = bgcol.getBlue();
         int r1 = this.destinationColor.getRed();
         int g1 = this.destinationColor.getGreen();
         int b1 = this.destinationColor.getBlue();
         Color d2 = new Color(this.mixColor(1, 3, r0, r1), this.mixColor(1, 3, g0, g1), this.mixColor(1, 3, b0, b1));
         this.lightFlashColors = this.prepareFlashColors(d2);
      }

      private Color[] prepareFlashColors(Color destColor) {
         Color[] ret = new Color[20];
         Color[] r = super.getColors();
         Color bgcol = r[1];
         int j = 0;
         int r0 = bgcol.getRed();
         int g0 = bgcol.getGreen();
         int b0 = bgcol.getBlue();
         int r1 = destColor.getRed();
         int g1 = destColor.getGreen();
         int b1 = destColor.getBlue();

         for(int i = 0; i < 10; ++i) {
            ret[j] = new Color(this.mixColor(i, 10, r0, r1), this.mixColor(i, 10, g0, g1), this.mixColor(i, 10, b0, b1));
            ++j;
         }

         for(int i = 10; i > 0; --i) {
            ret[j] = new Color(this.mixColor(i, 10, r0, r1), this.mixColor(i, 10, g0, g1), this.mixColor(i, 10, b0, b1));
            ++j;
         }

         return ret;
      }

      @Override
      protected Color[] getColors() {
         Color[] r = super.getColors();
         if (!this.flashing) {
            return r;
         } else if (this.heavyFlashCount > 0) {
            Color[] ret = new Color[]{r[0], this.heavyFlashColors[this.flashingPos]};
            return ret;
         } else {
            return new Color[]{r[0], this.lightFlashColors[this.flashingPos]};
         }
      }
   }
}
