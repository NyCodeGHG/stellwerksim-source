package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.event.ActionListener;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

public class RootRadioButtonMenuItem extends JRadioButtonMenuItem implements FrameAccessorInterface {
   private JScrollInternalFrame associatedFrame;

   public RootRadioButtonMenuItem(ActionListener listener, String itemTitle, int mnemonic, int shortcut, boolean selected, JScrollInternalFrame associatedFrame) {
      this(listener, itemTitle, mnemonic, shortcut, selected);
      this.associatedFrame = associatedFrame;
   }

   public RootRadioButtonMenuItem(ActionListener listener, String itemTitle, int mnemonic, int shortcut, boolean selected) {
      super(itemTitle, selected);
      this.setMnemonic(mnemonic);
      if (shortcut != -1) {
         this.setAccelerator(KeyStroke.getKeyStroke(shortcut, 8));
      }

      this.setActionCommand(itemTitle + "Radio");
      this.addActionListener(listener);
   }

   @Override
   public void setAssociatedFrame(JScrollInternalFrame associatedFrame) {
      this.associatedFrame = associatedFrame;
   }

   @Override
   public JScrollInternalFrame getAssociatedFrame() {
      return this.associatedFrame;
   }
}
