package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

public class DesktopMenu extends JMenu implements ActionListener {
   private DesktopMediator desktopMediator;
   private boolean tileMode;
   private int baseItemsEndIndex;
   private ButtonGroup frameRadioButtonMenuItemGroup;

   public DesktopMenu(DesktopMediator desktopMediator) {
      this(desktopMediator, false);
   }

   public DesktopMenu(DesktopMediator desktopMediator, boolean tileMode) {
      super("Window");
      this.setMnemonic(87);
      this.desktopMediator = desktopMediator;
      this.tileMode = tileMode;
      this.frameRadioButtonMenuItemGroup = new ButtonGroup();
      new ConstructWindowMenu(this, desktopMediator, tileMode);
      this.baseItemsEndIndex = this.getItemCount();
   }

   public void add(JScrollInternalFrame associatedFrame) {
      int displayedCount = this.getItemCount() - this.baseItemsEndIndex + 1;
      int currentMenuCount = displayedCount;
      if (displayedCount > 9) {
         currentMenuCount = displayedCount / 10;
      }

      RootRadioButtonMenuItem menuButton = new RootRadioButtonMenuItem(
         this, displayedCount + " " + associatedFrame.getTitle(), 48 + currentMenuCount, -1, true, associatedFrame
      );
      associatedFrame.setAssociatedMenuButton(menuButton);
      this.add(menuButton);
      this.frameRadioButtonMenuItemGroup.add(menuButton);
      menuButton.setSelected(true);
   }

   public void remove(JRadioButtonMenuItem menuButton) {
      this.frameRadioButtonMenuItemGroup.remove(menuButton);
      super.remove(menuButton);
      this.refreshMenu();
   }

   private void refreshMenu() {
      Enumeration e = this.frameRadioButtonMenuItemGroup.getElements();
      int displayedCount = 1;

      for(int currentMenuCount = 0; e.hasMoreElements(); ++displayedCount) {
         RootRadioButtonMenuItem b = (RootRadioButtonMenuItem)e.nextElement();
         currentMenuCount = displayedCount;
         if (displayedCount > 9) {
            currentMenuCount = displayedCount / 10;
         }

         b.setMnemonic(48 + currentMenuCount);
         b.setText(displayedCount + b.getAssociatedFrame().getTitle());
      }
   }

   public void actionPerformed(ActionEvent e) {
      this.desktopMediator.actionPerformed(e);
   }
}
