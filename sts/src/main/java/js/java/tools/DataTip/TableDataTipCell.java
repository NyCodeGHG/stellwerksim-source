package js.java.tools.DataTip;

import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

class TableDataTipCell implements DataTipCell {
   private final JTable table;
   private final int rowIndex;
   private final int columnIndex;

   TableDataTipCell(JTable table, int rowIndex, int columnIndex) {
      super();
      this.table = table;
      this.rowIndex = rowIndex;
      this.columnIndex = columnIndex;
   }

   @Override
   public boolean isSet() {
      return this.rowIndex >= 0 && this.columnIndex >= 0;
   }

   @Override
   public Rectangle getCellBounds() {
      return this.table.getCellRect(this.rowIndex, this.columnIndex, false);
   }

   @Override
   public Component getRendererComponent() {
      TableCellRenderer cellRenderer = this.table.getCellRenderer(this.rowIndex, this.columnIndex);
      return this.table.prepareRenderer(cellRenderer, this.rowIndex, this.columnIndex);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TableDataTipCell cellPosition = (TableDataTipCell)o;
         if (this.columnIndex != cellPosition.columnIndex) {
            return false;
         } else {
            return this.rowIndex == cellPosition.rowIndex;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.rowIndex;
      return 29 * result + this.columnIndex;
   }
}
