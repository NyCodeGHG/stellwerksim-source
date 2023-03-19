package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.Rectangle;
import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;

public class JScrollDesktopManager extends DefaultDesktopManager {
   private RootDesktopPane desktopPane;

   public JScrollDesktopManager(RootDesktopPane desktopPane) {
      super();
      this.desktopPane = desktopPane;
   }

   public void maximizeFrame(JInternalFrame f) {
      Rectangle p = this.desktopPane.getScrollPaneRectangle();
      f.setNormalBounds(f.getBounds());
      this.setBoundsForFrame(f, p.x, p.y, p.width, p.height);

      try {
         f.setSelected(true);
      } catch (PropertyVetoException var4) {
         System.out.println(var4.getMessage());
      }

      this.removeIconFor(f);
   }

   public void activateFrame(JInternalFrame f) {
      super.activateFrame(f);
      ((JScrollInternalFrame)f).selectFrameAndAssociatedButtons();
   }

   public void closeFrame(JInternalFrame f) {
      super.closeFrame(f);
      this.desktopPane.removeAssociatedComponents((JScrollInternalFrame)f);
      this.desktopPane.resizeDesktop();
   }
}
