package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import js.java.tools.gui.mdi.scrollabledesktop.JScrollDesktopPane;

public class DesktopMediator implements DesktopConstants {
   private DesktopScrollPane desktopScrollpane = new DesktopScrollPane(this);
   private DesktopResizableToolBar desktopResizableToolbar = new DesktopResizableToolBar(this);
   private DesktopListener dListener = new DesktopListener(this);
   private DesktopMenu dMenu;

   public DesktopMediator(JScrollDesktopPane mainPane) {
      mainPane.add(this.desktopResizableToolbar, "North");
      mainPane.add(this.desktopScrollpane, "Center");
      mainPane.addComponentListener(this.dListener);
   }

   public void registerMenuBar(JMenuBar mb) {
      this.dMenu = new DesktopMenu(this);
      mb.add(this.dMenu);
      mb.setBorder(null);
   }

   public JInternalFrame add(String title, ImageIcon icon, JPanel frameContents, boolean isClosable, int x, int y) {
      JScrollInternalFrame frame = null;
      if (this.desktopScrollpane.getNumberOfFrames() < 20) {
         frame = this.desktopScrollpane.add(this.dListener, title, icon, frameContents, isClosable, x, y);
         this.createFrameAssociates(frame);
      }

      return frame;
   }

   public void add(JInternalFrame frame, int x, int y) {
      if (this.desktopScrollpane.getNumberOfFrames() < 20) {
         this.desktopScrollpane.add(this.dListener, frame, x, y);
         this.createFrameAssociates((JScrollInternalFrame)frame);
      }
   }

   private void createFrameAssociates(JScrollInternalFrame frame) {
      RootToggleButton button = null;
      button = this.desktopResizableToolbar.add(frame.getTitle());
      button.setAssociatedFrame(frame);
      frame.setAssociatedButton(button);
      if (this.dMenu != null) {
         this.dMenu.add(frame);
      }

      if (this.desktopScrollpane.getAutoTile()) {
         this.desktopScrollpane.tileInternalFrames();
      }
   }

   public void removeAssociatedComponents(JScrollInternalFrame f) {
      this.desktopResizableToolbar.remove(f.getAssociatedButton());
      if (this.dMenu != null) {
         this.dMenu.remove(f.getAssociatedMenuButton());
      }

      this.desktopScrollpane.selectNextFrame();
   }

   public JInternalFrame getSelectedFrame() {
      return this.desktopScrollpane.getSelectedFrame();
   }

   public void setSelectedFrame(JInternalFrame f) {
      this.desktopScrollpane.setSelectedFrame(f);
   }

   public void flagContentsChanged(JInternalFrame f) {
      this.desktopScrollpane.flagContentsChanged(f);
   }

   public void resizeDesktop() {
      this.desktopScrollpane.resizeDesktop();
   }

   public void revalidateViewport() {
      this.desktopScrollpane.revalidate();
   }

   public void centerView(JScrollInternalFrame f) {
      this.desktopScrollpane.centerView(f);
   }

   public void closeSelectedFrame() {
      this.desktopScrollpane.closeSelectedFrame();
   }

   public void tileInternalFrames() {
      this.desktopScrollpane.tileInternalFrames();
   }

   public void cascadeInternalFrames() {
      this.desktopScrollpane.cascadeInternalFrames();
   }

   public void setAutoTile(boolean tileMode) {
      this.desktopScrollpane.setAutoTile(tileMode);
   }

   public void actionPerformed(ActionEvent e) {
      this.dListener.actionPerformed(e);
   }
}
