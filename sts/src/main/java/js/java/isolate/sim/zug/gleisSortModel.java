package js.java.isolate.sim.zug;

import javax.swing.table.AbstractTableModel;

public class gleisSortModel extends AbstractTableModel {
   private final gleisModel model;

   public gleisSortModel(gleisModel parent) {
      super();
      this.model = parent;
   }

   public int getDefaultSortColumn() {
      return this.model.getDefaultSortColumn();
   }

   public ZugTableComparator getComparator(int columnIndex) {
      return new gleisSortModel.extendedRowExtractor(this.model.getComparator(columnIndex));
   }

   public int getRowCount() {
      return this.model.getRowCount();
   }

   public int getColumnCount() {
      return this.model.getColumnCount();
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      return new gleisSortModel.extendedRow(this.model.getValueAt(rowIndex, columnIndex), this.model.isGleisLower(rowIndex));
   }

   public static class extendedRow {
      final Object data;
      final boolean lower;

      extendedRow(Object data, boolean lower) {
         super();
         this.data = data;
         this.lower = lower;
      }

      public boolean equals(Object o) {
         if (!(o instanceof gleisSortModel.extendedRow)) {
            return false;
         } else {
            gleisSortModel.extendedRow oo = (gleisSortModel.extendedRow)o;
            return oo.lower == this.lower && oo.data.equals(this.data);
         }
      }

      public int hashCode() {
         int hash = 7;
         hash = 83 * hash + (this.data != null ? this.data.hashCode() : 0);
         return 83 * hash + (this.lower ? 1 : 0);
      }
   }

   private static class extendedRowExtractor implements ZugTableComparator {
      private final ZugTableComparator child;

      extendedRowExtractor(ZugTableComparator child) {
         super();
         this.child = child;
      }

      public int compare(Object o1, Object o2) {
         gleisSortModel.extendedRow oo1 = (gleisSortModel.extendedRow)o1;
         gleisSortModel.extendedRow oo2 = (gleisSortModel.extendedRow)o2;
         if (oo1.lower == oo2.lower) {
            return this.child.compare(oo1.data, oo2.data);
         } else {
            return oo1.lower ? 1 : -1;
         }
      }
   }
}
