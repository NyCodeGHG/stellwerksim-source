package js.java.tools.DataTip;

import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class ListDataTipCell implements DataTipCell {
   private final JList list;
   private final int rowIndex;

   ListDataTipCell(JList list, int rowIndex) {
      super();
      this.list = list;
      this.rowIndex = rowIndex;
   }

   @Override
   public boolean isSet() {
      return this.rowIndex >= 0;
   }

   @Override
   public Rectangle getCellBounds() {
      return this.list.getCellBounds(this.rowIndex, this.rowIndex);
   }

   @Override
   public Component getRendererComponent() {
      Object item = this.list.getModel().getElementAt(this.rowIndex);
      boolean isSelected = this.list.isSelectedIndex(this.rowIndex);
      boolean isFocussed = this.list.hasFocus() && this.rowIndex == this.list.getLeadSelectionIndex();
      ListCellRenderer renderer = this.list.getCellRenderer();
      return renderer.getListCellRendererComponent(this.list, item, this.rowIndex, isSelected, isFocussed);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ListDataTipCell listDataTipCell = (ListDataTipCell)o;
         return this.rowIndex == listDataTipCell.rowIndex;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.rowIndex;
   }
}
