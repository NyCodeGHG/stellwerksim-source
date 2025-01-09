package js.java.tools.DataTip;

import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JTree;

class TreeDataTipListener extends DataTipListener {
   @Override
   DataTipCell getCell(JComponent component, Point point) {
      JTree tree = (JTree)component;
      int rowIndex = tree.getRowForLocation(point.x, point.y);
      return (DataTipCell)(rowIndex < 0 ? DataTipCell.NONE : new TreeDataTipCell(tree, rowIndex));
   }
}
