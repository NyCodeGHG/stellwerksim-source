package js.java.isolate.fahrplaneditor;

import javax.swing.JComboBox;

class einausSolver extends solutionInterface {
   private JComboBox box;
   private enritem item;

   einausSolver(String m, JComboBox cb, enritem i) {
      super(m);
      this.box = cb;
      this.item = i;
   }

   @Override
   void solve() {
      this.box.setSelectedItem(this.item);
   }
}
