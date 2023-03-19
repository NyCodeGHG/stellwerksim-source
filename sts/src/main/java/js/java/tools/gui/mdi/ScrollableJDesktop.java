package js.java.tools.gui.mdi;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JViewport;
import javax.swing.Scrollable;

public class ScrollableJDesktop extends JDesktopPane implements Scrollable {
   public ScrollableJDesktop() {
      super();
   }

   public Dimension getPreferredScrollableViewportSize() {
      return new Dimension(2000, 2000);
   }

   public Dimension getPreferredSize() {
      JInternalFrame[] frames = this.getAllFrames();
      int maxX = 300;
      int maxY = 300;
      if (this.getParent() instanceof JViewport) {
         Rectangle b = this.getParent().getBounds();
         maxX = b.width;
         maxY = b.height;
      }

      for(int i = 0; i < frames.length; ++i) {
         Rectangle r = frames[i].getBounds();
         maxX = Math.max(r.x + r.width, maxX);
         maxY = Math.max(r.y + r.height, maxY);
      }

      return new Dimension(maxX, maxY);
   }

   public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
      return 1;
   }

   public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
      return 10;
   }

   public boolean getScrollableTracksViewportWidth() {
      return false;
   }

   public boolean getScrollableTracksViewportHeight() {
      return false;
   }

   public void setDesktopManager(DesktopManager dtm) {
      super.setDesktopManager(new ScrollableJDesktop.ScrollableDesktopManager(dtm));
   }

   class ScrollableDesktopManager implements DesktopManager {
      DesktopManager dtm;

      ScrollableDesktopManager(DesktopManager d) {
         super();
         this.dtm = d;
      }

      public void openFrame(JInternalFrame f) {
         this.dtm.openFrame(f);
      }

      public void closeFrame(JInternalFrame f) {
         this.dtm.closeFrame(f);
      }

      public void maximizeFrame(JInternalFrame f) {
         this.dtm.maximizeFrame(f);
      }

      public void minimizeFrame(JInternalFrame f) {
         this.dtm.minimizeFrame(f);
      }

      public void iconifyFrame(JInternalFrame f) {
         this.dtm.iconifyFrame(f);
      }

      public void deiconifyFrame(JInternalFrame f) {
         this.dtm.deiconifyFrame(f);
      }

      public void activateFrame(JInternalFrame f) {
         this.dtm.activateFrame(f);
      }

      public void deactivateFrame(JInternalFrame f) {
         this.dtm.deactivateFrame(f);
      }

      public void beginDraggingFrame(JComponent f) {
         this.dtm.beginDraggingFrame(f);
      }

      public void dragFrame(JComponent f, int newX, int newY) {
         this.dtm.dragFrame(f, newX, newY);
      }

      public void endDraggingFrame(JComponent f) {
         this.dtm.endDraggingFrame(f);
         ScrollableJDesktop.this.revalidate();
      }

      public void beginResizingFrame(JComponent f, int direction) {
         this.dtm.beginResizingFrame(f, direction);
      }

      public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
         this.dtm.resizeFrame(f, newX, newY, newWidth, newHeight);
      }

      public void endResizingFrame(JComponent f) {
         this.dtm.endResizingFrame(f);
         ScrollableJDesktop.this.revalidate();
      }

      public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
         this.dtm.setBoundsForFrame(f, newX, newY, newWidth, newHeight);
      }
   }
}
