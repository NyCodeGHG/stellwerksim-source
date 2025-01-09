package js.java.tools.DataTip;

import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JTable;

class TableDataTipListener extends DataTipListener {
   @Override
   DataTipCell getCell(JComponent component, Point point) {
      JTable table = (JTable)component;
      int rowIndex = table.rowAtPoint(point);
      int columnIndex = table.columnAtPoint(point);
      return (DataTipCell)(rowIndex >= 0 && columnIndex >= 0 ? new TableDataTipCell(table, rowIndex, columnIndex) : DataTipCell.NONE);
   }
}
