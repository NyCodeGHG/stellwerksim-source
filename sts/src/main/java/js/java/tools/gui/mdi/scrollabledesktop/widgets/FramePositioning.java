package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JInternalFrame;

public class FramePositioning implements DesktopConstants {
   private DesktopScrollPane desktopScrollpane;
   private boolean autoTile;

   public FramePositioning(DesktopScrollPane desktopScrollpane) {
      this.desktopScrollpane = desktopScrollpane;
   }

   public void setAutoTile(boolean autoTile) {
      this.autoTile = autoTile;
      if (autoTile) {
         this.tileInternalFrames();
      } else {
         this.cascadeInternalFrames();
      }
   }

   public boolean getAutoTile() {
      return this.autoTile;
   }

   public void cascadeInternalFrames() {
      JInternalFrame[] frames = this.desktopScrollpane.getAllFrames();
      int frameCounter = 0;

      for (int i = frames.length - 1; i >= 0; i--) {
         JScrollInternalFrame f = (JScrollInternalFrame)frames[i];
         if (!f.isIcon() && f.isVisible()) {
            f.setSize(f.getInitialDimensions());
            f.setLocation(this.cascadeInternalFrame(f, frameCounter++));
         }
      }
   }

   public Point cascadeInternalFrame(JInternalFrame f) {
      return this.cascadeInternalFrame(f, this.desktopScrollpane.getNumberOfFrames());
   }

   private Point cascadeInternalFrame(JInternalFrame f, int count) {
      int windowWidth = f.getWidth();
      int windowHeight = f.getHeight();
      Rectangle viewP = this.desktopScrollpane.getViewport().getViewRect();
      int numFramesWide = (viewP.width - windowWidth) / 30;
      if (numFramesWide < 1) {
         numFramesWide = 1;
      }

      int numFramesHigh = (viewP.height - windowHeight) / 30;
      if (numFramesHigh < 1) {
         numFramesHigh = 1;
      }

      int xLoc = viewP.x + 30 * (count + 1 - (numFramesWide - 1) * (count / numFramesWide));
      int yLoc = viewP.y + 30 * (count + 1 - numFramesHigh * (count / numFramesHigh));
      return new Point(xLoc, yLoc);
   }

   public void tileInternalFrames() {
      Rectangle viewP = this.desktopScrollpane.getViewport().getViewRect();
      int totalNonIconFrames = 0;
      JInternalFrame[] frames = this.desktopScrollpane.getAllFrames();

      for (int i = 0; i < frames.length; i++) {
         if (!frames[i].isIcon() && frames[i].isVisible()) {
            totalNonIconFrames++;
         }
      }

      int curCol = 0;
      int curRow = 0;
      int ix = 0;
      if (totalNonIconFrames > 0) {
         int numCols = (int)Math.sqrt((double)totalNonIconFrames);
         int frameWidth = viewP.width / numCols;

         for (int var13 = 0; var13 < numCols; var13++) {
            int numRows = totalNonIconFrames / numCols;
            int remainder = totalNonIconFrames % numCols;
            if (numCols - var13 <= remainder) {
               numRows++;
            }

            int frameHeight = viewP.height / numRows;

            for (int var14 = 0; var14 < numRows; var14++) {
               while (frames[ix].isIcon() || !frames[ix].isVisible()) {
                  ix++;
               }

               frames[ix].setBounds(var13 * frameWidth, var14 * frameHeight, frameWidth, frameHeight);
               ix++;
            }
         }
      }
   }
}
