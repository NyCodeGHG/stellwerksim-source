package js.java.tools.DataTip;

import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JList;

class ListDataTipListener extends DataTipListener {
   @Override
   DataTipCell getCell(JComponent component, Point point) {
      JList list = (JList)component;
      int index = list.locationToIndex(point);
      return (DataTipCell)(index < 0 ? DataTipCell.NONE : new ListDataTipCell(list, index));
   }
}
