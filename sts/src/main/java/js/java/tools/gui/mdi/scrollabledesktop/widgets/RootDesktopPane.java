package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.Rectangle;
import javax.swing.JDesktopPane;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class RootDesktopPane extends JDesktopPane {
   private DesktopScrollPane desktopScrollpane;

   public RootDesktopPane(DesktopScrollPane desktopScrollpane) {
      this.desktopScrollpane = desktopScrollpane;
      UIDefaults defaults = UIManager.getDefaults();
      defaults.put("DesktopIconUI", this.getClass().getPackage().getName() + ".EmptyDesktopIconUI");
      this.setDesktopManager(new JScrollDesktopManager(this));
      this.setDragMode(1);
   }

   public Rectangle getScrollPaneRectangle() {
      return this.desktopScrollpane.getViewport().getViewRect();
   }

   public void removeAssociatedComponents(JScrollInternalFrame f) {
      this.desktopScrollpane.removeAssociatedComponents(f);
   }

   public void resizeDesktop() {
      this.desktopScrollpane.resizeDesktop();
   }
}
