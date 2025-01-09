package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopIconUI;

public class EmptyDesktopIconUI extends DesktopIconUI {
   protected static EmptyDesktopIconUI desktopIconUI;

   public static ComponentUI createUI(JComponent c) {
      if (desktopIconUI == null) {
         desktopIconUI = new EmptyDesktopIconUI();
      }

      return desktopIconUI;
   }

   protected void paint(Graphics g) {
   }
}
