package js.java.tools.gui.warningPopup;

import javax.swing.JMenuItem;

public class warningItems {
   private String message = "";
   private JMenuItem menu = null;

   public warningItems(String m) {
      this.message = m;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   JMenuItem getMenu() {
      return this.menu;
   }

   void setMenu(JMenuItem menu) {
      this.menu = menu;
   }
}
