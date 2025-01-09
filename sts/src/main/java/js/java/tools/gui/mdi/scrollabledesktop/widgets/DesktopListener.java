package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class DesktopListener implements ComponentListener, ActionListener {
   private DesktopMediator desktopMediator;

   public DesktopListener(DesktopMediator desktopMediator) {
      this.desktopMediator = desktopMediator;
   }

   public void componentResized(ComponentEvent e) {
      this.desktopMediator.resizeDesktop();
   }

   public void componentShown(ComponentEvent e) {
      this.desktopMediator.revalidateViewport();
   }

   public void componentMoved(ComponentEvent e) {
      this.desktopMediator.resizeDesktop();
   }

   public void componentHidden(ComponentEvent e) {
   }

   public void actionPerformed(ActionEvent e) {
      String actionCmd = e.getActionCommand();
      if (actionCmd.equals("Tile")) {
         this.desktopMediator.tileInternalFrames();
      } else if (actionCmd.equals("Cascade")) {
         this.desktopMediator.cascadeInternalFrames();
      } else if (actionCmd.equals("Close")) {
         this.desktopMediator.closeSelectedFrame();
      } else if (actionCmd.equals("TileRadio")) {
         this.desktopMediator.setAutoTile(true);
      } else if (actionCmd.equals("CascadeRadio")) {
         this.desktopMediator.setAutoTile(false);
      } else {
         JScrollInternalFrame associatedFrame = ((FrameAccessorInterface)e.getSource()).getAssociatedFrame();
         if (associatedFrame != null) {
            associatedFrame.selectFrameAndAssociatedButtons();
            this.desktopMediator.centerView(associatedFrame);
         }
      }
   }
}
