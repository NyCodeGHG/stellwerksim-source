package js.java.isolate.sim.sim.fahrplanRenderer;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JComponent;
import javax.swing.UIManager;
import js.java.tools.RunLater;

public class rendererBase extends JComponent {
   protected final zugRenderer zr;
   protected final Font plainFont;
   protected final Font boldFont;
   protected final int LINEHEIGHT;
   private static final Timer scheduler = new Timer();

   protected rendererBase(zugRenderer zr) {
      this.zr = zr;
      this.setOpaque(true);
      Font f = UIManager.getDefaults().getFont("defaultFont");
      this.plainFont = f.deriveFont(0, (float)f.getSize());
      this.boldFont = f.deriveFont(1, (float)f.getSize());
      this.LINEHEIGHT = this.plainFont.getSize() + 3;
      this.setFont(this.plainFont);
   }

   protected int drawString(Graphics2D g2, Font font, String text, int x, int y) {
      try {
         g2.setFont(font);
         FontMetrics fm = g2.getFontMetrics();
         g2.drawString(text, x, y + fm.getAscent());
         return x + fm.stringWidth(text);
      } catch (NullPointerException var7) {
         return x;
      }
   }

   protected int stringWidth(Graphics2D g2, Font font, String text) {
      g2.setFont(font);
      FontMetrics fm = g2.getFontMetrics();
      return fm.stringWidth(text);
   }

   protected void triggerRepaint() {
      new RunLater(100L) {
         public void run() {
            rendererBase.this.repaint();
         }
      };
   }

   protected void schedule(int millisec, TimerTask task) {
      scheduler.scheduleAtFixedRate(task, (long)millisec, (long)millisec);
   }
}
