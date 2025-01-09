package js.java.isolate.sim.sim.fahrplanRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.TimerTask;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import js.java.tools.gui.GraphicTools;

public class hinweisRenderer extends rendererBase {
   private final String text;
   private final int line;
   private final int azid;
   private int clipWidth = 0;
   private int hinweisX = 0;
   private boolean allowTimer = true;
   private int hinweisLen;
   private BufferedImage hinweisImage = null;
   private final Dimension dim;
   private hinweisRenderer.timerRunner trunner = null;

   public hinweisRenderer(zugRenderer zr, String text, int line, int azid) {
      super(zr);
      this.text = text;
      this.line = line;
      this.azid = azid;
      this.dim = new Dimension(100, this.LINEHEIGHT);
      this.addAncestorListener(new AncestorListener() {
         public void ancestorAdded(AncestorEvent event) {
            hinweisRenderer.this.allowTimer = true;
         }

         public void ancestorRemoved(AncestorEvent event) {
            hinweisRenderer.this.allowTimer = false;
         }

         public void ancestorMoved(AncestorEvent event) {
         }
      });
   }

   public Dimension getPreferredSize() {
      return this.dim;
   }

   public Dimension getMinimumSize() {
      return this.dim;
   }

   public Dimension getMaximumSize() {
      return this.dim;
   }

   public int getLine() {
      return this.line;
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.setBackground(this.getBackground());
      g2.clearRect(0, 0, this.getWidth(), this.getHeight());
      if (this.clipWidth != this.getWidth()) {
         this.clipWidth = this.getWidth();
         this.hinweisImage = null;
      }

      if (this.hinweisImage != null) {
         g2.drawImage(this.hinweisImage, -this.hinweisX, 0, null);
         g2.drawImage(this.hinweisImage, -this.hinweisX + this.hinweisLen, 0, null);
         this.zr.setXvalue(this.azid, this.hinweisX);
      } else {
         int w = this.stringWidth(g2, this.plainFont, this.text);
         if (w < this.getWidth()) {
            GraphicTools.enableTextAA(g2);
            this.drawString(g2, this.plainFont, this.text, 0, 0);
         } else {
            this.hinweisX = this.zr.getXvalue(this.azid);
            int width = w + 20;
            this.hinweisLen = width;
            this.hinweisImage = new BufferedImage(width, this.LINEHEIGHT, 2);
            Graphics2D ig = this.hinweisImage.createGraphics();
            GraphicTools.enableTextAA(ig);
            ig.setBackground(new Color(0, 0, 0, 10));
            ig.clearRect(0, 0, width, this.LINEHEIGHT);
            ig.setColor(Color.BLACK);
            this.drawString(ig, this.plainFont, this.text, 0, 0);
            ig.dispose();
            this.timer();
            g2.drawImage(this.hinweisImage, -this.hinweisX, 0, null);
            g2.drawImage(this.hinweisImage, -this.hinweisX + this.hinweisLen, 0, null);
         }
      }
   }

   private void timer() {
      if (this.trunner == null) {
         if (this.allowTimer) {
            this.trunner = new hinweisRenderer.timerRunner();
            this.schedule(45, this.trunner);
         }
      }
   }

   private class timerRunner extends TimerTask {
      private timerRunner() {
      }

      public void run() {
         if (hinweisRenderer.this.hinweisX < hinweisRenderer.this.hinweisLen) {
            hinweisRenderer.this.hinweisX++;
         } else {
            hinweisRenderer.this.hinweisX = 0;
         }

         hinweisRenderer.this.repaint();
         if (!hinweisRenderer.this.allowTimer) {
            this.cancel();
            hinweisRenderer.this.trunner = null;
         }
      }
   }
}
