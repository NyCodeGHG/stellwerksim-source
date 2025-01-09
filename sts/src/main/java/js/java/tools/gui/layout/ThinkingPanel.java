package js.java.tools.gui.layout;

import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ThinkingPanel extends JPanel {
   private Dimension size = new Dimension(1, 1);

   public Dimension getMinimumSize() {
      return this.isMinimumSizeSet() ? super.getMinimumSize() : this.size;
   }

   public void reshape(int x, int y, int width, int height) {
      if (this.getLayout() instanceof ThinkingLayout) {
         int newWidth = width;
         int newHeight = height;
         boolean invalid = false;
         Dimension d = ((ThinkingLayout)this.getLayout()).proveLayout(this, width, height);
         if (d.width > 0 && d.width > width) {
            newWidth = d.width;
            this.size.width = d.width;
            invalid = true;
         } else {
            this.size.width = 1;
            if (d.width > 0) {
               newWidth = Math.abs(d.width);
               invalid = newWidth != width;
            }
         }

         if (d.height > 0 && d.height > height) {
            newHeight = d.height;
            this.size.height = d.height;
            invalid = true;
         } else {
            this.size.height = 1;
            if (d.height > 0) {
               newHeight = Math.abs(d.height);
               invalid = newHeight != height;
            }
         }

         if (invalid && (newWidth != width || newHeight != height)) {
            Runnable callRevalidate = new ThinkingPanel.Resize(newWidth, newHeight);
            SwingUtilities.invokeLater(callRevalidate);
         }
      }

      super.reshape(x, y, width, height);
   }

   private class Resize implements Runnable {
      private final int width;
      private final int height;

      Resize(int w, int h) {
         this.width = w;
         this.height = h;
      }

      public void run() {
         ThinkingPanel.this.setSize(this.width, this.height);
         ThinkingPanel.this.revalidate();
      }
   }
}
