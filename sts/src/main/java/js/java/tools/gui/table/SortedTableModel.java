package js.java.tools.gui.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class SortedTableModel extends AbstractTableModel {
   private TableModel m_Model;
   private int[] m_Indexes;
   private int m_nColumn;
   private boolean m_bAscending;
   private Comparator m_Comparator;

   public SortedTableModel(TableModel model, Comparator comparator) {
      this.m_Model = model;
      this.m_Model.addTableModelListener(new TableModelListener() {
         public void tableChanged(TableModelEvent e) {
            if (e.getType() == 0 && e.getLastRow() != Integer.MAX_VALUE) {
               int nColumn = e.getColumn();

               for (int i = e.getFirstRow(); i <= e.getLastRow(); i++) {
                  int nSortedRow = SortedTableModel.this.getSortedIndex(i);
                  if (nSortedRow != -1) {
                     if (nColumn == -1) {
                        SortedTableModel.this.fireTableRowsUpdated(nSortedRow, nSortedRow);
                     } else {
                        SortedTableModel.this.fireTableCellUpdated(nSortedRow, nColumn);
                     }
                  }
               }
            } else {
               SortedTableModel.this.resort();
            }

            if (e.getType() == 0 && e.getColumn() == -1 && e.getFirstRow() == -1 && e.getLastRow() == -1) {
               SortedTableModel.this.fireTableStructureChanged();
            }
         }
      });
      this.m_Comparator = comparator;
      this.sortByColumn(0, true);
   }

   public SortedTableModel(TableModel model) {
      this(model, new SortedTableModel.DefaultComparator());
   }

   public Comparator getComparator() {
      return this.m_Comparator;
   }

   public void setComparator(Comparator comparator) {
      this.m_Comparator = comparator;
      this.resort();
   }

   public void sortByColumn(int nColumn, boolean bAscending) {
      this.m_nColumn = nColumn;
      this.m_bAscending = bAscending;
      this.resort();
   }

   public void resort() {
      this.m_Indexes = new int[this.m_Model.getRowCount()];
      int i = 0;

      while (i < this.m_Model.getRowCount()) {
         this.m_Indexes[i] = i++;
      }

      this.shuttlesort((int[])this.m_Indexes.clone(), this.m_Indexes, 0, this.m_Indexes.length);
      this.fireTableDataChanged();
   }

   public TableModel getModel() {
      return this.m_Model;
   }

   public int getSortColumn() {
      return this.m_nColumn;
   }

   public boolean isAscending() {
      return this.m_bAscending;
   }

   public int getRowCount() {
      return this.m_Indexes.length;
   }

   public int getColumnCount() {
      return this.m_Model.getColumnCount();
   }

   public String getColumnName(int columnIndex) {
      return this.m_Model.getColumnName(columnIndex);
   }

   public Class getColumnClass(int columnIndex) {
      return this.m_Model.getColumnClass(columnIndex);
   }

   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return this.m_Model.isCellEditable(this.m_Indexes[rowIndex], columnIndex);
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      return this.m_Model.getValueAt(this.m_Indexes[rowIndex], columnIndex);
   }

   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      this.m_Model.setValueAt(aValue, this.m_Indexes[rowIndex], columnIndex);
   }

   private int compareRows(int row1, int row2) {
      Class type = this.m_Model.getColumnClass(this.m_nColumn);
      Object o1 = this.m_Model.getValueAt(row1, this.m_nColumn);
      Object o2 = this.m_Model.getValueAt(row2, this.m_nColumn);
      int nResult = this.m_Comparator.compare(o1, o2);
      return this.m_bAscending ? nResult : -nResult;
   }

   private void shuttlesort(int[] from, int[] to, int low, int high) {
      if (high - low >= 2) {
         int middle = (low + high) / 2;
         this.shuttlesort(to, from, low, middle);
         this.shuttlesort(to, from, middle, high);
         int p = low;
         int q = middle;
         if (high - low >= 4 && this.compareRows(from[middle - 1], from[middle]) <= 0) {
            for (int i = low; i < high; i++) {
               to[i] = from[i];
            }
         } else {
            for (int i = low; i < high; i++) {
               if (q < high && (p >= middle || this.compareRows(from[p], from[q]) > 0)) {
                  to[i] = from[q++];
               } else {
                  to[i] = from[p++];
               }
            }
         }
      }
   }

   public void addMouseListenerToHeaderInTable(final JTable table) {
      table.setColumnSelectionAllowed(false);
      MouseAdapter listMouseListener = new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
            TableColumnModel columnModel = table.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = table.convertColumnIndexToModel(viewColumn);
            if (e.getClickCount() == 1 && column != -1) {
               int shiftPressed = e.getModifiers() & 1;
               boolean ascending = column == SortedTableModel.this.m_nColumn ? !SortedTableModel.this.m_bAscending : true;
               SortedTableModel.this.sortByColumn(column, ascending);
            }
         }
      };
      JTableHeader th = table.getTableHeader();
      th.addMouseListener(listMouseListener);
   }

   private int getSortedIndex(int nModelIndex) {
      for (int i = 0; i < this.m_Indexes.length; i++) {
         if (this.m_Indexes[i] == nModelIndex) {
            return i;
         }
      }

      return -1;
   }

   public int sortToModel(int rowIndex) {
      return this.m_Indexes[rowIndex];
   }

   public int modelToSort(int rowIndex) {
      return this.getSortedIndex(rowIndex);
   }

   public static class DefaultComparator implements Comparator {
      public int compare(Object o1, Object o2) {
         if (o1 == null && o2 == null) {
            return 0;
         } else if (o1 == null) {
            return -1;
         } else if (o2 == null) {
            return 1;
         } else if (o1 instanceof Boolean) {
            boolean b1 = (Boolean)o1;
            boolean b2 = (Boolean)o2;
            if (b1 && !b2) {
               return 1;
            } else {
               return !b1 && b2 ? -1 : 0;
            }
         } else {
            return o1 instanceof Comparable ? ((Comparable)o1).compareTo(o2) : 0;
         }
      }
   }
}
