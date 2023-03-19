package js.java.isolate.sim.zug;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;
import js.java.tools.ColorText;
import js.java.tools.NumColorText;
import js.java.tools.NumString;
import js.java.tools.gui.table.ButtonColorText;

public class gleisModel extends AbstractTableModel {
   private final String[] cols = new String[]{"Gleis", "Zug", "Abfahrt", "nach"};
   private final ArrayList<NumString> lines = new ArrayList();
   private final ArrayList<gleisModel.NumButtonColorText> linesCT = new ArrayList();
   private final HashMap<NumString, zug> data = new HashMap();
   private final ArrayList<Boolean> linesLower = new ArrayList();

   public gleisModel() {
      super();
   }

   public void clear() {
      this.lines.clear();
      this.linesCT.clear();
      this.data.clear();
      this.linesLower.clear();
      this.fireTableDataChanged();
   }

   public Class getColumnClass(int columnIndex) {
      return columnIndex == 0 ? ButtonColorText.class : ZugColorText.class;
   }

   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnIndex == 0;
   }

   public int getDefaultSortColumn() {
      return 2;
   }

   public ZugTableComparator getComparator(int columnIndex) {
      switch(columnIndex) {
         case 0:
            return new gleisNameComparator();
         default:
            return new gleisDefaultComparator(new zugDefaultComparator());
      }
   }

   public int getRowCount() {
      return this.lines.size();
   }

   public int getColumnCount() {
      return this.cols.length;
   }

   public void addBahnsteig(NumString b) {
      this.lines.add(b);
      this.linesCT.add(new gleisModel.NumButtonColorText(b));
      this.linesLower.add(false);
      int row = this.lines.size() - 1;
      this.fireTableRowsInserted(row, row);
   }

   public void setZugOnBahnsteig(NumString b, zug z) {
      if (z == null) {
         this.clearZugOnBahnsteig(b);
      } else {
         this.data.put(b, z);
         int row = this.lines.indexOf(b);
         this.fireTableRowsUpdated(row, row);
      }
   }

   public void clearZugOnBahnsteig(NumString b) {
      this.data.remove(b);
      int row = this.lines.indexOf(b);
      this.fireTableRowsUpdated(row, row);
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      switch(columnIndex) {
         case 0:
            return this.linesCT.get(rowIndex);
         case 1:
            if (this.data.containsKey(this.lines.get(rowIndex))) {
               zug z = (zug)this.data.get(this.lines.get(rowIndex));
               return z.getNameCT();
            }
            break;
         case 2:
            if (this.data.containsKey(this.lines.get(rowIndex))) {
               zug z = (zug)this.data.get(this.lines.get(rowIndex));
               return z.getAbfahrtCT();
            }
            break;
         case 3:
            if (this.data.containsKey(this.lines.get(rowIndex))) {
               zug z = (zug)this.data.get(this.lines.get(rowIndex));
               return z.getNachCT();
            }
      }

      return new gleisModel.emptyColorText((NumString)this.lines.get(rowIndex));
   }

   public boolean isGleisLower(int row) {
      return this.linesLower.get(row);
   }

   public void setGleisLower(int row, boolean lower) {
      this.linesLower.set(row, lower);
      ((gleisModel.NumButtonColorText)this.linesCT.get(row)).darken = lower;
      this.fireTableRowsUpdated(row, row);
   }

   public void toggleGleisLower(int row) {
      this.setGleisLower(row, !this.isGleisLower(row));
   }

   public String getColumnName(int column) {
      return this.cols[column];
   }

   public void updateZug(zug z) {
      this.fireTableDataChanged();
   }

   public zug getZug(int index) {
      return (zug)this.data.get(this.lines.get(index));
   }

   public int getIndexOf(zug z) {
      if (this.data.containsValue(z)) {
         for(NumString b : this.data.keySet()) {
            if (this.data.get(b) == z) {
               return this.lines.indexOf(b);
            }
         }
      }

      return -1;
   }

   private static class NumButtonColorText extends NumColorText implements ButtonColorText {
      private NumString n;
      boolean darken = false;

      NumButtonColorText(NumString n) {
         super(n);
         this.n = n;
      }

      public Object getData() {
         return this.n;
      }

      public boolean isDarken() {
         return this.darken;
      }
   }

   static class emptyColorText extends ColorText {
      private final NumString gleis;

      private emptyColorText(NumString gleis) {
         super();
         this.gleis = gleis;
      }

      NumString getGleis() {
         return this.gleis;
      }
   }
}
