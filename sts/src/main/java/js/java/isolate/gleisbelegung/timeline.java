package js.java.isolate.gleisbelegung;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import js.java.tools.gui.GraphicTools;

class timeline extends JComponent {
   public static int MINUTEWIDTH = 10;
   public static int VON = 5;
   public static int BIS = 21;
   public static final String fontname = "Dialog";
   public static int fontheight = 14;
   public static int minifontheight = 10;
   public static Font font_normal = new Font("Dialog", 0, fontheight);
   public static Font font_bold = new Font("Dialog", 1, fontheight);
   public static Font font_mini = new Font("Dialog", 0, minifontheight);
   public static Color bgcol = new Color(187, 187, 255);

   timeline() {
      super();
      this.setBackground(bgcol);
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      GraphicTools.enableTextAA(g2);
      g2.setFont(font_bold);
      FontMetrics mfont = g2.getFontMetrics();
      int h = mfont.getHeight();
      g2.setBackground(bgcol);
      g2.clearRect(0, 0, this.getWidth(), this.getHeight());

      for(int s = VON; s < BIS; ++s) {
         g2.setColor(bgcol.darker());
         g2.drawLine((s - VON) * MINUTEWIDTH * 60 - 1, 0, (s - VON) * MINUTEWIDTH * 60 - 1, this.getHeight());
         g2.setColor(bgcol.brighter());
         g2.drawLine((s - VON) * MINUTEWIDTH * 60, 0, (s - VON) * MINUTEWIDTH * 60, this.getHeight());
         g2.setColor(Color.BLUE);

         for(int m = 0; m < 60; ++m) {
            int f = 20;
            if (m % 5 == 0) {
               f = 17;
            }

            g2.drawLine((s - VON) * MINUTEWIDTH * 60 + m * MINUTEWIDTH, mfont.getHeight() + f, (s - VON) * MINUTEWIDTH * 60 + m * MINUTEWIDTH, this.getHeight());
         }

         for(int m = 0; m < 60; m += 10) {
            g2.setColor(Color.BLUE);
            g2.drawLine((s - VON) * MINUTEWIDTH * 60 + m * MINUTEWIDTH, mfont.getHeight() + 5, (s - VON) * MINUTEWIDTH * 60 + m * MINUTEWIDTH, this.getHeight());
            g2.setColor(Color.BLACK);
            String t = ":" + m;
            g2.drawString(t, (s - VON) * MINUTEWIDTH * 60 + m * MINUTEWIDTH - 1, mfont.getHeight() * 2);
         }

         String t = s + " Uhr";
         int swidth = mfont.stringWidth(t);
         g2.setColor(Color.WHITE);
         g2.drawString(t, (s - VON) * MINUTEWIDTH * 60 + (60 * MINUTEWIDTH - swidth) / 2 + 1, mfont.getHeight() + 1);
         g2.setColor(Color.BLACK);
         g2.drawString(t, (s - VON) * MINUTEWIDTH * 60 + (60 * MINUTEWIDTH - swidth) / 2, mfont.getHeight());
      }
   }

   public Dimension getMinimumSize() {
      return this.getMinsize();
   }

   public Dimension getMaximumSize() {
      return this.getMinsize();
   }

   public Dimension getPreferredSize() {
      return this.getMinsize();
   }

   private Dimension getMinsize() {
      return new Dimension(MINUTEWIDTH * (BIS - VON) * 60, fontheight * 3);
   }
}
