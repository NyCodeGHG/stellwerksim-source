package js.java.isolate.sim.toolkit;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

public class MXbuttonGroup extends ButtonGroup {
   ButtonModel selection = null;

   public MXbuttonGroup() {
      super();
   }

   public void add(AbstractButton b) {
      if (b != null) {
         this.buttons.addElement(b);
         if (b.isSelected()) {
            if (this.selection == null) {
               this.selection = b.getModel();
            } else {
               b.setSelected(false);
            }
         }

         b.getModel().setGroup(this);
      }
   }

   public void remove(AbstractButton b) {
      if (b != null) {
         this.buttons.removeElement(b);
         if (b.getModel() == this.selection) {
            this.selection = null;
         }

         b.getModel().setGroup(null);
      }
   }

   public void clearSelection() {
      if (this.selection != null) {
         ButtonModel oldSelection = this.selection;
         this.selection = null;
         oldSelection.setSelected(false);
      }
   }

   public ButtonModel getSelection() {
      return this.selection;
   }

   public void setSelected(ButtonModel m, boolean b) {
      if (m != null) {
         if (b) {
            if (this.selection != m) {
               ButtonModel oldSelection = this.selection;
               this.selection = m;
               if (oldSelection != null) {
                  oldSelection.setSelected(false);
               }

               m.setSelected(true);
            }
         } else {
            this.selection = null;
         }
      }
   }

   public boolean isSelected(ButtonModel m) {
      return m == this.selection;
   }
}
