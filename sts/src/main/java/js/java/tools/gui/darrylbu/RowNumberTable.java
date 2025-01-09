package js.java.tools.gui.darrylbu;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class RowNumberTable extends JTable implements ChangeListener, PropertyChangeListener {
   private JTable main;

   public static void addRowNumbers(JTable table, JScrollPane scroller) {
      JTable rowTable = new RowNumberTable(table);
      scroller.setRowHeaderView(rowTable);
      scroller.setCorner("UPPER_LEFT_CORNER", rowTable.getTableHeader());
   }

   public RowNumberTable(JTable table) {
      this.main = table;
      this.main.addPropertyChangeListener(this);
      this.setFocusable(false);
      this.setAutoCreateColumnsFromModel(false);
      this.setModel(this.main.getModel());
      this.setSelectionModel(this.main.getSelectionModel());
      TableColumn column = new TableColumn();
      column.setHeaderValue(" ");
      this.addColumn(column);
      column.setCellRenderer(new RowNumberTable.RowNumberRenderer());
      this.getColumnModel().getColumn(0).setPreferredWidth(50);
      this.setPreferredScrollableViewportSize(this.getPreferredSize());
   }

   public void addNotify() {
      super.addNotify();
      Component c = this.getParent();
      if (c instanceof JViewport) {
         JViewport viewport = (JViewport)c;
         viewport.addChangeListener(this);
      }
   }

   public int getRowCount() {
      return this.main.getRowCount();
   }

   public int getRowHeight(int row) {
      return this.main.getRowHeight(row);
   }

   public Object getValueAt(int row, int column) {
      return Integer.toString(row + 1);
   }

   public boolean isCellEditable(int row, int column) {
      return false;
   }

   public void stateChanged(ChangeEvent e) {
      JViewport viewport = (JViewport)e.getSource();
      JScrollPane scrollPane = (JScrollPane)viewport.getParent();
      scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
   }

   public void propertyChange(PropertyChangeEvent e) {
      if ("selectionModel".equals(e.getPropertyName())) {
         this.setSelectionModel(this.main.getSelectionModel());
      }

      if ("model".equals(e.getPropertyName())) {
         this.setModel(this.main.getModel());
      }
   }

   private static class RowNumberRenderer extends DefaultTableCellRenderer {
      RowNumberRenderer() {
         this.setHorizontalAlignment(0);
      }

      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
         if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
               this.setForeground(header.getForeground());
               this.setBackground(header.getBackground());
               this.setFont(header.getFont());
            }
         }

         if (isSelected) {
            this.setFont(this.getFont().deriveFont(1));
         }

         this.setText(value == null ? "" : value.toString());
         this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
         return this;
      }
   }
}
