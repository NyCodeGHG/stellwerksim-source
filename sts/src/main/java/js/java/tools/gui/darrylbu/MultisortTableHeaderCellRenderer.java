package js.java.tools.gui.darrylbu;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableCellRenderer;

public class MultisortTableHeaderCellRenderer extends DefaultTableHeaderCellRenderer {
   private float alpha;

   public static void addMultisortHeader(JTable table) {
      table.getTableHeader().setDefaultRenderer(new MultisortTableHeaderCellRenderer());
   }

   public MultisortTableHeaderCellRenderer() {
      this(0.5F);
   }

   public MultisortTableHeaderCellRenderer(TableCellRenderer oldRenderer) {
      this(oldRenderer, 0.5F);
   }

   public MultisortTableHeaderCellRenderer(float alpha) {
      this.alpha = alpha;
   }

   public MultisortTableHeaderCellRenderer(TableCellRenderer oldRenderer, float alpha) {
      super(oldRenderer);
      this.alpha = alpha;
   }

   @Override
   public Icon getIcon(JTable table, int column) {
      float computedAlpha = 1.0F;

      for (SortKey sortKey : table.getRowSorter().getSortKeys()) {
         if (table.convertColumnIndexToView(sortKey.getColumn()) == column) {
            switch (sortKey.getSortOrder()) {
               case ASCENDING:
                  return new AlphaIcon(UIManager.getIcon("Table.ascendingSortIcon"), computedAlpha);
               case DESCENDING:
                  return new AlphaIcon(UIManager.getIcon("Table.descendingSortIcon"), computedAlpha);
            }
         }

         computedAlpha *= this.alpha;
      }

      return null;
   }
}
