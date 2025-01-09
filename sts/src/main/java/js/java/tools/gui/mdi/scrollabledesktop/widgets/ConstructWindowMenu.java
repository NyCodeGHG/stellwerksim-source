package js.java.tools.gui.mdi.scrollabledesktop.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

public class ConstructWindowMenu implements ActionListener {
   private DesktopMediator desktopMediator;

   public ConstructWindowMenu(JMenu sourceMenu, DesktopMediator desktopMediator, boolean tileMode) {
      this.desktopMediator = desktopMediator;
      this.constructMenuItems(sourceMenu, tileMode);
   }

   private void constructMenuItems(JMenu sourceMenu, boolean tileMode) {
      sourceMenu.add(new RootMenuItem(this, "Tile", 84, -1));
      sourceMenu.add(new RootMenuItem(this, "Cascade", 67, -1));
      sourceMenu.addSeparator();
      JMenu autoMenu = new JMenu("Auto");
      autoMenu.setMnemonic(85);
      ButtonGroup autoMenuGroup = new ButtonGroup();
      JRadioButtonMenuItem radioItem = new RootRadioButtonMenuItem(this, "Tile", 84, -1, tileMode);
      autoMenu.add(radioItem);
      autoMenuGroup.add(radioItem);
      radioItem = new RootRadioButtonMenuItem(this, "Cascade", 67, -1, !tileMode);
      autoMenu.add(radioItem);
      autoMenuGroup.add(radioItem);
      sourceMenu.add(autoMenu);
      sourceMenu.addSeparator();
      sourceMenu.add(new RootMenuItem(this, "Close", 83, 90));
      sourceMenu.addSeparator();
   }

   public void actionPerformed(ActionEvent e) {
      this.desktopMediator.actionPerformed(e);
   }
}
