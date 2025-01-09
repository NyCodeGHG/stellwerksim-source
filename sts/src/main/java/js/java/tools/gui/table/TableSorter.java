package js.java.tools.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import js.java.tools.analysisWriter;

@Deprecated
public class TableSorter extends AbstractTableModel {
   protected DefaultTableModel tableModel;
   public static final int DESCENDING = -1;
   public static final int NOT_SORTED = 0;
   public static final int ASCENDING = 1;
   private static TableSorter.Directive EMPTY_DIRECTIVE = new TableSorter.Directive(-1, 0);
   private static analysisWriter debugMode = null;
   public static final Comparator COMPARABLE_COMAPRATOR = new Comparator() {
      public int compare(Object o1, Object o2) {
         return ((Comparable)o1).compareTo(o2);
      }
   };
   public static final Comparator LEXICAL_COMPARATOR = new Comparator() {
      public int compare(Object o1, Object o2) {
         return o1.toString().compareTo(o2.toString());
      }
   };
   private TableSorter.Row[] viewToModel;
   private int[] modelToView;
   private JTableHeader tableHeader;
   private MouseListener mouseListener;
   private TableModelListener tableModelListener;
   private Map columnComparators = new HashMap();
   private List sortingColumns = new ArrayList();
   private JTable mytable = null;
   private Object selectionStorage = null;

   public static void setDebug(analysisWriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public TableSorter() {
      this.mouseListener = new TableSorter.MouseHandler();
      this.tableModelListener = new TableSorter.TableModelHandler();
   }

   public TableSorter(DefaultTableModel tableModel) {
      this();
      this.setTableModel(tableModel);
   }

   public TableSorter(JTable table, DefaultTableModel tableModel) {
      this();
      this.setTableModel(tableModel);
      this.mytable = table;
   }

   public TableSorter(DefaultTableModel tableModel, JTableHeader tableHeader) {
      this();
      this.setTableHeader(tableHeader);
      this.setTableModel(tableModel);
   }

   private void clearSortingState() {
      this.viewToModel = null;
      this.modelToView = null;
   }

   public TableModel getTableModel() {
      return this.tableModel;
   }

   public void setTableModel(DefaultTableModel tableModel) {
      if (this.tableModel != null) {
         this.tableModel.removeTableModelListener(this.tableModelListener);
      }

      this.tableModel = tableModel;
      if (this.tableModel != null) {
         this.tableModel.addTableModelListener(this.tableModelListener);
      }

      this.fireTableStructureChanged();
   }

   public TableModel getModel() {
      return this.tableModel;
   }

   public JTableHeader getTableHeader() {
      return this.tableHeader;
   }

   public void setTableHeader(JTableHeader tableHeader) {
      if (this.tableHeader != null) {
         this.tableHeader.removeMouseListener(this.mouseListener);
         TableCellRenderer defaultRenderer = this.tableHeader.getDefaultRenderer();
         if (defaultRenderer instanceof TableSorter.SortableHeaderRenderer) {
            this.tableHeader.setDefaultRenderer(((TableSorter.SortableHeaderRenderer)defaultRenderer).tableCellRenderer);
         }
      }

      this.tableHeader = tableHeader;
      if (this.tableHeader != null) {
         this.tableHeader.addMouseListener(this.mouseListener);
         this.tableHeader.setDefaultRenderer(new TableSorter.SortableHeaderRenderer(this.tableHeader.getDefaultRenderer()));
      }
   }

   public boolean isSorting() {
      return !this.sortingColumns.isEmpty();
   }

   private TableSorter.Directive getDirective(int column) {
      for (int i = 0; i < this.sortingColumns.size(); i++) {
         TableSorter.Directive directive = (TableSorter.Directive)this.sortingColumns.get(i);
         if (directive.column == column) {
            return directive;
         }
      }

      return EMPTY_DIRECTIVE;
   }

   public int getSortingStatus(int column) {
      return this.getDirective(column).direction;
   }

   private void sortingStatusChanged() {
      this.fireTableDataChanged();
      if (this.tableHeader != null) {
         this.tableHeader.repaint();
      }
   }

   public void fireTableDataChanged() {
      this.clearSortingState();
      super.fireTableDataChanged();
      if (debugMode != null) {
         debugMode.writeln("TableSorter", "TableSorter.fireTableDataChanged");
      }
   }

   public void fireTableChanged(TableModelEvent e) {
      this.clearSortingState();
      super.fireTableChanged(e);
      if (debugMode != null) {
         debugMode.writeln("TableSorter", "TableSorter.fireTableChanged");
         debugMode.dumpStack("TableSorter");
      }
   }

   public void fireTableStructureChanged() {
      this.clearSortingState();
      super.fireTableStructureChanged();
      if (debugMode != null) {
         debugMode.writeln("TableSorter", "TableSorter.fireTableStructureChanged");
      }
   }

   public void fireTableRowsUpdated(int FirstRow, int LastRow) {
      this.clearSortingState();
      super.fireTableRowsUpdated(FirstRow, LastRow);
      if (debugMode != null) {
         debugMode.writeln("TableSorter", "TableSorter.fireTableRowsUpdated");
      }
   }

   private void selectedStore() {
      if (this.mytable != null) {
         int s = this.mytable.getSelectionModel().getMinSelectionIndex();
         if (s >= 0) {
            this.selectionStorage = this.getValueAt(s, 0);
         } else {
            this.selectionStorage = null;
         }
      }
   }

   private void selectedSet() {
      if (this.mytable != null && this.selectionStorage != null) {
         for (int i = 0; i < this.getRowCount(); i++) {
            Object o = this.getValueAt(i, 0);
            if (o == this.selectionStorage) {
               if (this.mytable.getSelectionModel().getMinSelectionIndex() != i) {
                  this.mytable.getSelectionModel().setSelectionInterval(i, i);
                  this.mytable.scrollRectToVisible(this.mytable.getCellRect(i, 0, true));
               }
               break;
            }
         }
      }

      this.selectionStorage = null;
   }

   public void setSortingStatus(int column, int status) {
      TableSorter.Directive directive = this.getDirective(column);
      if (directive != EMPTY_DIRECTIVE) {
         this.sortingColumns.remove(directive);
      }

      if (status != 0) {
         this.sortingColumns.add(new TableSorter.Directive(column, status));
      }

      this.sortingStatusChanged();
   }

   protected Icon getHeaderRendererIcon(int column, int size) {
      TableSorter.Directive directive = this.getDirective(column);
      return directive == EMPTY_DIRECTIVE ? null : new TableSorter.Arrow(directive.direction == -1, size, this.sortingColumns.indexOf(directive));
   }

   private void cancelSorting() {
      this.selectedStore();
      this.sortingColumns.clear();
      this.sortingStatusChanged();
   }

   public void setColumnComparator(Class type, Comparator comparator) {
      if (comparator == null) {
         this.columnComparators.remove(type);
      } else {
         this.columnComparators.put(type, comparator);
      }
   }

   protected Comparator getComparator(int column) {
      Class columnType = this.tableModel.getColumnClass(column);
      Comparator comparator = (Comparator)this.columnComparators.get(columnType);
      if (comparator != null) {
         return comparator;
      } else {
         return Comparable.class.isAssignableFrom(columnType) ? COMPARABLE_COMAPRATOR : LEXICAL_COMPARATOR;
      }
   }

   private synchronized TableSorter.Row[] getViewToModel() {
      if (this.viewToModel == null) {
         int tableModelRowCount = this.tableModel.getRowCount();
         this.viewToModel = new TableSorter.Row[tableModelRowCount];

         for (int row = 0; row < tableModelRowCount; row++) {
            this.viewToModel[row] = new TableSorter.Row(row);
         }

         if (this.isSorting()) {
            Arrays.sort(this.viewToModel);
         }
      }

      return this.viewToModel;
   }

   public int modelIndex(int viewIndex) {
      try {
         return this.getViewToModel()[viewIndex].modelIndex;
      } catch (ArrayIndexOutOfBoundsException | NullPointerException var3) {
         return 0;
      }
   }

   private int[] getModelToView() {
      if (this.modelToView == null) {
         int n = this.getViewToModel().length;
         this.modelToView = new int[n];
         int i = 0;

         while (i < n) {
            this.modelToView[this.modelIndex(i)] = i++;
         }
      }

      return this.modelToView;
   }

   public int getRowCount() {
      return this.tableModel == null ? 0 : this.tableModel.getRowCount();
   }

   public int getColumnCount() {
      return this.tableModel == null ? 0 : this.tableModel.getColumnCount();
   }

   public String getColumnName(int column) {
      return this.tableModel.getColumnName(column);
   }

   public Class getColumnClass(int column) {
      return this.tableModel.getColumnClass(column);
   }

   public boolean isCellEditable(int row, int column) {
      return this.tableModel.isCellEditable(this.modelIndex(row), column);
   }

   public Object getValueAt(int row, int column) {
      return this.tableModel.getValueAt(this.modelIndex(row), column);
   }

   public void setValueAt(Object aValue, int row, int column) {
      this.tableModel.setValueAt(aValue, this.modelIndex(row), column);
   }

   public void addRow(Object[] rowData) {
      this.tableModel.addRow(rowData);
   }

   public void addRow(Vector rowData) {
      this.tableModel.addRow(rowData);
   }

   public void insertRow(int row, Object[] rowData) {
      this.tableModel.insertRow(this.modelIndex(row), rowData);
   }

   public void insertRow(int row, Vector rowData) {
      this.tableModel.insertRow(this.modelIndex(row), rowData);
   }

   public void removeRow(int row) {
      this.tableModel.removeRow(this.modelIndex(row));
   }

   private static class Arrow implements Icon {
      private boolean descending;
      private int size;
      private int priority;

      Arrow(boolean descending, int size, int priority) {
         this.descending = descending;
         this.size = size;
         this.priority = priority;
      }

      public void paintIcon(Component c, Graphics g, int x, int y) {
         Color color = c == null ? Color.GRAY : c.getBackground();
         int dx = (int)((double)(this.size / 2) * Math.pow(0.8, (double)this.priority));
         int dy = this.descending ? dx : -dx;
         y = y + 5 * this.size / 6 + (this.descending ? -dy : 0);
         int shift = this.descending ? 1 : -1;
         g.translate(x, y);
         g.setColor(color.darker());
         g.drawLine(dx / 2, dy, 0, 0);
         g.drawLine(dx / 2, dy + shift, 0, shift);
         g.setColor(color.brighter());
         g.drawLine(dx / 2, dy, dx, 0);
         g.drawLine(dx / 2, dy + shift, dx, shift);
         if (this.descending) {
            g.setColor(color.darker().darker());
         } else {
            g.setColor(color.brighter().brighter());
         }

         g.drawLine(dx, 0, 0, 0);
         g.setColor(color);
         g.translate(-x, -y);
      }

      public int getIconWidth() {
         return this.size;
      }

      public int getIconHeight() {
         return this.size;
      }
   }

   private static class Directive {
      private int column;
      private int direction;

      Directive(int column, int direction) {
         this.column = column;
         this.direction = direction;
      }
   }

   private class MouseHandler extends MouseAdapter {
      private MouseHandler() {
      }

      public void mouseClicked(MouseEvent e) {
         JTableHeader h = (JTableHeader)e.getSource();
         TableColumnModel columnModel = h.getColumnModel();
         int viewColumn = columnModel.getColumnIndexAtX(e.getX());
         int column = columnModel.getColumn(viewColumn).getModelIndex();
         if (column != -1) {
            int status = TableSorter.this.getSortingStatus(column);
            if (!e.isControlDown()) {
               TableSorter.this.selectedStore();
               TableSorter.this.cancelSorting();
            }

            status += e.isShiftDown() ? -1 : 1;
            status = (status + 4) % 3 - 1;
            TableSorter.this.setSortingStatus(column, status);
            TableSorter.this.selectedSet();
         }
      }
   }

   private class Row implements Comparable {
      private int modelIndex;

      Row(int index) {
         this.modelIndex = index;
      }

      public int compareTo(Object o) {
         int row1 = this.modelIndex;
         int row2 = ((TableSorter.Row)o).modelIndex;

         for (TableSorter.Directive directive : TableSorter.this.sortingColumns) {
            int column = directive.column;
            Object o1 = TableSorter.this.tableModel.getValueAt(row1, column);
            Object o2 = TableSorter.this.tableModel.getValueAt(row2, column);
            int comparison = 0;
            if (o1 == null && o2 == null) {
               comparison = 0;
            } else if (o1 == null) {
               comparison = 1;
            } else if (o2 == null) {
               comparison = -1;
            } else {
               comparison = TableSorter.this.getComparator(column).compare(o1, o2);
            }

            if (comparison != 0) {
               return directive.direction == -1 ? -comparison : comparison;
            }
         }

         return 0;
      }
   }

   private class SortableHeaderRenderer implements TableCellRenderer {
      private TableCellRenderer tableCellRenderer;

      SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
         this.tableCellRenderer = tableCellRenderer;
      }

      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
         Component c = this.tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
         if (c instanceof JLabel) {
            JLabel l = (JLabel)c;
            l.setHorizontalTextPosition(2);
            int modelColumn = table.convertColumnIndexToModel(column);
            l.setIcon(TableSorter.this.getHeaderRendererIcon(modelColumn, l.getFont().getSize()));
         }

         return c;
      }
   }

   private class TableModelHandler implements TableModelListener {
      private TableModelHandler() {
      }

      public void tableChanged(TableModelEvent e) {
         if (!TableSorter.this.isSorting()) {
            TableSorter.this.fireTableChanged(e);
         } else if (e.getFirstRow() == -1) {
            TableSorter.this.cancelSorting();
            TableSorter.this.fireTableChanged(e);
         } else {
            int column = e.getColumn();
            if (e.getFirstRow() == e.getLastRow() && column != -1 && TableSorter.this.getSortingStatus(column) == 0 && TableSorter.this.modelToView != null) {
               int viewIndex = TableSorter.this.getModelToView()[e.getFirstRow()];
               TableSorter.this.fireTableChanged(new TableModelEvent(TableSorter.this, viewIndex, viewIndex, column, e.getType()));
            } else {
               TableSorter.this.fireTableRowsUpdated(e.getFirstRow(), e.getLastRow());
            }
         }
      }
   }
}
