package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class RootMenuItem extends JMenuItem {
   public RootMenuItem(ActionListener listener, String itemTitle, int mnemonic, int shortcut) {
      super(itemTitle, mnemonic);
      if (shortcut != -1) {
         this.setAccelerator(KeyStroke.getKeyStroke(shortcut, 8));
      }

      this.setActionCommand(itemTitle);
      this.addActionListener(listener);
   }
}
