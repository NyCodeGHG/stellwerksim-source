package js.java.isolate.fahrplaneditor;

import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

class TMbuttonGroup extends ButtonGroup {
   private HashMap<ButtonModel, Boolean> selected = new HashMap();

   public void add(AbstractButton b) {
      if (b != null) {
         this.buttons.addElement(b);
         b.getModel().setGroup(this);
         this.selected.put(b.getModel(), b.isSelected());
      }
   }

   public void remove(AbstractButton b) {
      if (b != null) {
         this.buttons.removeElement(b);
         b.getModel().setGroup(null);
         this.selected.remove(b.getModel());
      }
   }

   public void clearSelection() {
   }

   public ButtonModel getSelection() {
      return null;
   }

   public void setSelected(ButtonModel m, boolean b) {
      if (m != null) {
         this.selected.put(m, b);
         if (!b && !this.selected.containsValue(true)) {
            this.selected.put(((AbstractButton)this.buttons.firstElement()).getModel(), true);
            ((AbstractButton)this.buttons.firstElement()).setSelected(true);
         }
      }
   }

   public boolean isSelected(ButtonModel m) {
      return (Boolean)this.selected.get(m);
   }
}
