package js.java.tools.gui.mdi;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class MDIDesktopPane extends JDesktopPane {
   private static int FRAME_OFFSET = 20;
   private MDIDesktopManager manager = new MDIDesktopManager(this);

   public MDIDesktopPane() {
      super();
      this.setDesktopManager(this.manager);
      this.setDragMode(1);
   }

   public void setBounds(int x, int y, int w, int h) {
      super.setBounds(x, y, w, h);
      this.checkDesktopSize();
   }

   public Component add(JInternalFrame frame) {
      JInternalFrame[] array = this.getAllFrames();
      Component retval = super.add(frame);
      this.checkDesktopSize();
      Point p;
      if (array.length > 0) {
         p = array[0].getLocation();
         p.x += FRAME_OFFSET;
         p.y += FRAME_OFFSET;
      } else {
         p = new Point(0, 0);
      }

      frame.setLocation(p.x, p.y);
      if (frame.isResizable()) {
         int w = this.getWidth() - this.getWidth() / 3;
         int h = this.getHeight() - this.getHeight() / 3;
         if ((double)w < frame.getMinimumSize().getWidth()) {
            w = (int)frame.getMinimumSize().getWidth();
         }

         if ((double)h < frame.getMinimumSize().getHeight()) {
            h = (int)frame.getMinimumSize().getHeight();
         }

         frame.setSize(w, h);
      }

      this.moveToFront(frame);
      frame.setVisible(true);

      try {
         frame.setSelected(true);
      } catch (PropertyVetoException var8) {
         frame.toBack();
      }

      return retval;
   }

   public void remove(Component c) {
      super.remove(c);
      this.checkDesktopSize();
   }

   public void cascadeFrames() {
      int x = 0;
      int y = 0;
      JInternalFrame[] allFrames = this.getAllFrames();
      this.manager.setNormalSize();
      int frameHeight = this.getBounds().height - 5 - allFrames.length * FRAME_OFFSET;
      int frameWidth = this.getBounds().width - 5 - allFrames.length * FRAME_OFFSET;

      for(int i = allFrames.length - 1; i >= 0; --i) {
         allFrames[i].setSize(frameWidth, frameHeight);
         allFrames[i].setLocation(x, y);
         x += FRAME_OFFSET;
         y += FRAME_OFFSET;
      }
   }

   public void tileFrames() {
      Component[] allFrames = this.getAllFrames();
      this.manager.setNormalSize();
      int frameHeight = this.getBounds().height / allFrames.length;
      int y = 0;

      for(int i = 0; i < allFrames.length; ++i) {
         allFrames[i].setSize(this.getBounds().width, frameHeight);
         allFrames[i].setLocation(0, y);
         y += frameHeight;
      }
   }

   public void setAllSize(Dimension d) {
      this.setMinimumSize(d);
      this.setMaximumSize(d);
      this.setPreferredSize(d);
   }

   public void setAllSize(int width, int height) {
      this.setAllSize(new Dimension(width, height));
   }

   private void checkDesktopSize() {
      if (this.getParent() != null && this.isVisible()) {
         this.manager.resizeDesktop();
      }
   }
}
