package js.java.tools.gui;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.plaf.LayerUI;

public class DisableInputLayerUI extends LayerUI<JPanel> {
   private BufferedImage offscreenImage;
   private BufferedImageOp fxOperation;
   private boolean isRunning = false;

   public void paint(Graphics g, JComponent c) {
      if (!this.isRunning) {
         super.paint(g, c);
      } else {
         int w = c.getWidth();
         int h = c.getHeight();
         if (w != 0 && h != 0) {
            if (this.offscreenImage == null || this.offscreenImage.getWidth() != w || this.offscreenImage.getHeight() != h) {
               this.offscreenImage = new BufferedImage(w, h, 1);
            }

            Graphics2D ig2 = this.offscreenImage.createGraphics();
            ig2.setClip(g.getClip());
            super.paint(ig2, c);
            ig2.dispose();
            Graphics2D g2 = (Graphics2D)g;
            g2.drawImage(this.offscreenImage, this.fxOperation, 0, 0);
         }
      }
   }

   public void installUI(JComponent c) {
      super.installUI(c);
      JLayer jlayer = (JLayer)c;
      jlayer.setLayerEventMask(56L);
   }

   public void uninstallUI(JComponent c) {
      JLayer jlayer = (JLayer)c;
      jlayer.setLayerEventMask(0L);
      super.uninstallUI(c);
   }

   public void eventDispatched(AWTEvent e, JLayer l) {
      if (this.isRunning && e instanceof InputEvent) {
         ((InputEvent)e).consume();
      }
   }

   public void start() {
      if (!this.isRunning) {
         this.isRunning = true;
         this.firePropertyChange("repaint", 0, 1);
      }
   }

   public void stop() {
      this.isRunning = false;
      this.firePropertyChange("repaint", 0, 1);
   }

   public void applyPropertyChange(PropertyChangeEvent pce, JLayer l) {
      if ("repaint".equals(pce.getPropertyName())) {
         l.repaint();
      }
   }

   public void setLocked(boolean off) {
      if (off) {
         this.start();
      } else {
         this.stop();
      }
   }

   public void setLockedEffects(BufferedImageOp fx) {
      this.fxOperation = fx;
   }
}
