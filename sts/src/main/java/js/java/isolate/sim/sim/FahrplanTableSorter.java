package js.java.isolate.sim.sim;

import javax.swing.table.TableRowSorter;
import js.java.isolate.sim.zug.fahrplanModel;

class FahrplanTableSorter extends TableRowSorter<fahrplanModel> {
   FahrplanTableSorter(fahrplanModel fahrplanMdl) {
      super(fahrplanMdl);
   }

   public void sort() {
      super.sort();
   }
}
