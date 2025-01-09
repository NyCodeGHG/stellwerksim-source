package js.java.tools.gui.mdi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

class WindowMenu extends JMenu {
   private MDIDesktopPane desktop;
   private JMenuItem cascade = new JMenuItem("Cascade");
   private JMenuItem tile = new JMenuItem("Tile");

   WindowMenu(MDIDesktopPane desktop) {
      this.desktop = desktop;
      this.setText("Window");
      this.cascade.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            WindowMenu.this.desktop.cascadeFrames();
         }
      });
      this.tile.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            WindowMenu.this.desktop.tileFrames();
         }
      });
      this.addMenuListener(new MenuListener() {
         public void menuCanceled(MenuEvent e) {
         }

         public void menuDeselected(MenuEvent e) {
            WindowMenu.this.removeAll();
         }

         public void menuSelected(MenuEvent e) {
            WindowMenu.this.buildChildMenus();
         }
      });
   }

   private void buildChildMenus() {
      JInternalFrame[] array = this.desktop.getAllFrames();
      this.add(this.cascade);
      this.add(this.tile);
      if (array.length > 0) {
         this.addSeparator();
      }

      this.cascade.setEnabled(array.length > 0);
      this.tile.setEnabled(array.length > 0);

      for (int i = 0; i < array.length; i++) {
         WindowMenu.ChildMenuItem menu = new WindowMenu.ChildMenuItem(array[i]);
         menu.setState(i == 0);
         menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               JInternalFrame frame = ((WindowMenu.ChildMenuItem)ae.getSource()).getFrame();
               frame.moveToFront();

               try {
                  frame.setSelected(true);
               } catch (PropertyVetoException var4) {
                  var4.printStackTrace();
               }
            }
         });
         menu.setIcon(array[i].getFrameIcon());
         this.add(menu);
      }
   }

   class ChildMenuItem extends JCheckBoxMenuItem {
      private JInternalFrame frame;

      ChildMenuItem(JInternalFrame frame) {
         super(frame.getTitle());
         this.frame = frame;
      }

      public JInternalFrame getFrame() {
         return this.frame;
      }
   }
}
