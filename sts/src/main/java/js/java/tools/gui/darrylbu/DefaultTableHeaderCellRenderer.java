package js.java.tools.gui.darrylbu;

import java.awt.Component;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.UIManager;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class DefaultTableHeaderCellRenderer extends DefaultTableCellRenderer {
   private DefaultTableCellRenderer deligate = null;

   public DefaultTableHeaderCellRenderer() {
      super();
      this.setHorizontalAlignment(0);
      this.setHorizontalTextPosition(2);
      this.setVerticalAlignment(3);
      this.setOpaque(false);
   }

   public DefaultTableHeaderCellRenderer(TableCellRenderer oldRenderer) {
      this();
      if (oldRenderer instanceof DefaultTableCellRenderer) {
         this.deligate = (DefaultTableCellRenderer)oldRenderer;
      }
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (this.deligate == null) {
         super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
         this.setIcon(this.getIcon(table, column));
         this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
         return this;
      } else {
         Component c = this.deligate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
         this.deligate.setIcon(this.getIcon(table, column));
         return c;
      }
   }

   protected Icon getIcon(JTable table, int column) {
      SortKey sortKey = this.getSortKey(table, column);
      if (sortKey != null && table.convertColumnIndexToView(sortKey.getColumn()) == column) {
         switch(sortKey.getSortOrder()) {
            case ASCENDING:
               return UIManager.getIcon("Table.ascendingSortIcon");
            case DESCENDING:
               return UIManager.getIcon("Table.descendingSortIcon");
         }
      }

      return null;
   }

   protected SortKey getSortKey(JTable table, int column) {
      RowSorter rowSorter = table.getRowSorter();
      if (rowSorter == null) {
         return null;
      } else {
         List sortedColumns = rowSorter.getSortKeys();
         return sortedColumns.size() > 0 ? (SortKey)sortedColumns.get(0) : null;
      }
   }
}
