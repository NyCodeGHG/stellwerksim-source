package js.java.isolate.sim.zug;

import java.util.ArrayList;

public class fahrplanBaseModel extends fahrplanModel {
   private final String[] cols = new String[]{"Zug", "Ankunft", "Abfahrt", "Gleis", "von", "nach", "Verspätung"};
   private final ArrayList<zug> lines = new ArrayList();

   fahrplanBaseModel() {
   }

   @Override
   public int getDefaultSortColumn() {
      return 6;
   }

   @Override
   public void clear() {
      this.lines.clear();
      this.fireTableDataChanged();
   }

   @Override
   public Class getColumnClass(int columnIndex) {
      return ZugColorText.class;
   }

   @Override
   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
   }

   @Override
   public ZugTableComparator getComparator(int columnIndex) {
      return new zugDefaultComparator();
   }

   @Override
   public int getRowCount() {
      return this.lines.size();
   }

   @Override
   public int getColumnCount() {
      return this.cols.length;
   }

   @Override
   public String getColumnName(int column) {
      return this.cols[column];
   }

   @Override
   public void addZug(zug z) {
      this.lines.add(z);
      int row = this.lines.size() - 1;
      this.freeze(z);
      this.fireTableRowsInserted(row, row);
   }

   @Override
   public void removeZug(zug z) {
      int row = this.lines.indexOf(z);
      if (row >= 0) {
         this.lines.remove(z);
         this.fireTableRowsDeleted(row, row);
      }
   }

   @Override
   public zug getZug(int index) {
      return (zug)this.lines.get(index);
   }

   @Override
   public int getUnterzugId(int index) {
      return 0;
   }

   @Override
   public int getIndexOf(zug z) {
      return this.lines.indexOf(z);
   }

   @Override
   public void updateZug(zug z) {
      int row = this.lines.indexOf(z);
      this.freeze(z);
      this.fireTableRowsUpdated(row, row);
   }

   private ZugColorText getValueAt(zug z, int columnIndex) {
      ZugColorText ret = null;
      switch (columnIndex) {
         case 0:
            ret = z.getNameCT();
            break;
         case 1:
            ret = z.getAnkunftCT();
            break;
         case 2:
            ret = z.getAbfahrtCT();
            break;
         case 3:
            ret = z.getGleisCT();
            break;
         case 4:
            ret = z.getVonCT();
            break;
         case 5:
            ret = z.getNachCT();
            break;
         case 6:
            ret = z.getVerspaetungCT();
      }

      return ret;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      zug z = (zug)this.lines.get(rowIndex);
      return this.getValueAt(z, columnIndex);
   }

   private void freeze(zug z) {
      for (int col = 0; col < this.cols.length; col++) {
         ZugColorText zct = this.getValueAt(z, col);
         zct.freeze();
      }
   }

   @Override
   public void freeze() {
      for (zug z : this.lines) {
         for (int col = 0; col < this.cols.length; col++) {
            ZugColorText zct = this.getValueAt(z, col);
            zct.freeze();
         }
      }
   }
}
