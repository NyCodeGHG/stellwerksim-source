package js.java.isolate.sim.zug;

import javax.swing.table.AbstractTableModel;

public abstract class fahrplanModel extends AbstractTableModel {
   public static fahrplanModel createModel() {
      return new fahrplanBaseModel();
   }

   public abstract ZugTableComparator getComparator(int var1);

   public abstract int getDefaultSortColumn();

   public abstract Class getColumnClass(int var1);

   public abstract boolean isCellEditable(int var1, int var2);

   public abstract int getRowCount();

   public abstract int getColumnCount();

   public abstract String getColumnName(int var1);

   public abstract void addZug(zug var1);

   public abstract void removeZug(zug var1);

   public abstract zug getZug(int var1);

   public abstract int getUnterzugId(int var1);

   public abstract int getIndexOf(zug var1);

   public abstract void updateZug(zug var1);

   public abstract Object getValueAt(int var1, int var2);

   public abstract void clear();

   public abstract void freeze();
}
