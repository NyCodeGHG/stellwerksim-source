package js.java.tools.formhtml;

import java.io.Serializable;
import javax.swing.DefaultComboBoxModel;

class OptionComboBoxModel<E> extends DefaultComboBoxModel<E> implements Serializable {
   private Option selectedOption = null;

   OptionComboBoxModel() {
      super();
   }

   public void setInitialSelection(Option option) {
      this.selectedOption = option;
   }

   public Option getInitialSelection() {
      return this.selectedOption;
   }
}
