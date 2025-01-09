package js.java.tools.gui.prefs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;

public class TableColumnPrefsSaver implements PropertyChangeListener, TableColumnModelListener {
   private int defaultColumnWidth;
   private JTable table;
   private Preferences saveRootNode;
   private boolean changed = false;

   public TableColumnPrefsSaver(JTable aTable, String saveRootNodeName) {
      this(aTable, Preferences.userNodeForPackage(TableColumnPrefsSaver.class).node(saveRootNodeName), -1);
   }

   public TableColumnPrefsSaver(JTable aTable, Class saveRootNodeClass, String saveRootNodeName) {
      this(aTable, Preferences.userNodeForPackage(saveRootNodeClass).node(saveRootNodeName), -1);
   }

   public TableColumnPrefsSaver(JTable aTable, Preferences saveRootNode, String saveRootNodeName) {
      this(aTable, saveRootNode.node(saveRootNodeName), -1);
   }

   public TableColumnPrefsSaver(JTable aTable, Preferences saveRootNode) {
      this(aTable, saveRootNode, -1);
   }

   public TableColumnPrefsSaver(JTable aTable, Preferences saveRootNode, int defaultColumnWidth) {
      this.table = aTable;
      this.saveRootNode = saveRootNode;
      this.defaultColumnWidth = defaultColumnWidth;
      if (defaultColumnWidth > 0) {
         this.table.setAutoResizeMode(0);

         for (TableColumn column : this.getColumns()) {
            column.setPreferredWidth(defaultColumnWidth);
         }
      }

      if (!this.isSaveOrderMatching()) {
         this.resetOrder();
      }

      if (!this.isSaveSizeMatching()) {
         this.resetSize();
      }

      this.sizeColumns();

      for (TableColumn column : this.getColumns()) {
         column.addPropertyChangeListener(this);
      }

      this.orderColumns();
      this.table.getColumnModel().addColumnModelListener(this);
   }

   public void propertyChange(PropertyChangeEvent e) {
      if (e.getPropertyName().equals("width") && this.table.getTableHeader().getResizingColumn() != null) {
         this.changed = true;
         this.saveColumnWidth();
      }
   }

   public void columnAdded(TableColumnModelEvent e) {
      this.saveColumnOrdering();
   }

   public void columnMarginChanged(ChangeEvent e) {
   }

   public void columnMoved(TableColumnModelEvent e) {
      this.changed = true;
      this.saveColumnOrdering();
   }

   public void columnRemoved(TableColumnModelEvent e) {
      this.saveColumnOrdering();
   }

   public void columnSelectionChanged(ListSelectionEvent e) {
   }

   public void dispose() {
      for (TableColumn column : this.getColumns()) {
         column.removePropertyChangeListener(this);
      }

      this.table.getColumnModel().removeColumnModelListener(this);
   }

   private void resetSize() {
      try {
         this.getWidthPrefs().removeNode();
      } catch (BackingStoreException var2) {
         log().log(Level.SEVERE, "Unable to reset table column ordering", var2);
      }
   }

   private void resetOrder() {
      try {
         this.getOrderPrefs().removeNode();
      } catch (BackingStoreException var2) {
         log().log(Level.SEVERE, "Unable to reset table column ordering", var2);
      }
   }

   private void sizeColumns() {
      if (this.isSaveSizeMatching()) {
         for (TableColumn column : this.getColumns()) {
            int width = this.getWidthPrefs().getInt(this.getColumnKey(column), this.defaultColumnWidth);
            if (width > 0) {
               this.table.setAutoResizeMode(0);
               this.changed = this.changed | width != this.defaultColumnWidth;
               column.setPreferredWidth(width);
            }
         }
      }
   }

   private void saveColumnWidth(TableColumn column) {
      if (this.changed) {
         this.getWidthPrefs().putInt(this.getColumnKey(column), column.getWidth());
      }
   }

   private void saveColumnWidth() {
      if (this.changed) {
         for (int i = 0; i < this.table.getColumnModel().getColumnCount(); i++) {
            this.saveColumnWidth(this.table.getColumn(i));
         }
      }
   }

   private boolean isSaveOrderMatching() {
      List<String> prefNames;
      try {
         prefNames = Arrays.asList(this.getOrderPrefs().keys());
      } catch (BackingStoreException var3) {
         log().log(Level.SEVERE, "Failed to read table column order from prefs", var3);
         return false;
      }

      List<String> columnKeys = this.getAllColumnKeys();
      return prefNames.containsAll(columnKeys) && columnKeys.containsAll(prefNames);
   }

   private boolean isSaveSizeMatching() {
      List<String> prefNames;
      try {
         prefNames = Arrays.asList(this.getWidthPrefs().keys());
      } catch (BackingStoreException var3) {
         log().log(Level.SEVERE, "Failed to read table column order from prefs", var3);
         return false;
      }

      List<String> columnKeys = this.getAllColumnKeys();
      return prefNames.containsAll(columnKeys) && columnKeys.containsAll(prefNames);
   }

   private void orderColumns() {
      if (this.isSaveOrderMatching()) {
         for (TableColumn column : this.getColumns()) {
            int newIndex = this.getOrderPrefs().getInt(this.getColumnKey(column), 0);
            int currentIndex = this.getIndexOfColumn(column);
            int columnCount = this.table.getColumnCount();
            if (newIndex < columnCount && currentIndex < columnCount) {
               this.changed = true;
               this.table.getColumnModel().moveColumn(currentIndex, newIndex);
            }
         }
      }

      this.saveColumnOrdering();
   }

   private void saveColumnOrdering() {
      if (this.changed) {
         try {
            this.getOrderPrefs().clear();
         } catch (BackingStoreException var2) {
            log().log(Level.SEVERE, "Unable to store table column ordering", var2);
            return;
         }

         for (int i = 0; i < this.table.getColumnModel().getColumnCount(); i++) {
            this.getOrderPrefs().putInt(this.getColumnKey(this.table.getColumnName(i)), i);
         }
      }
   }

   private String getColumnName(TableColumn column) {
      return column.getHeaderValue().toString();
   }

   private String getColumnKey(TableColumn column) {
      return this.getColumnKey(this.getColumnName(column));
   }

   private String getColumnKey(String name) {
      String hashCode = "" + name.hashCode();
      int spaceForName = 80 - hashCode.length();
      String shortName = name.length() > spaceForName ? name.substring(0, spaceForName) : name;
      return shortName + hashCode;
   }

   private List<String> getAllColumnKeys() {
      List<String> keys = new ArrayList();

      for (TableColumn column : this.getColumns()) {
         keys.add(this.getColumnKey(column));
      }

      return keys;
   }

   private List<TableColumn> getColumns() {
      return Collections.list(this.table.getColumnModel().getColumns());
   }

   private int getIndexOfColumn(TableColumn column) {
      return this.getColumns().indexOf(column);
   }

   private Preferences getPrefsRoot() {
      return this.saveRootNode;
   }

   private Preferences getWidthPrefs() {
      return this.getPrefsRoot().node("width");
   }

   private Preferences getOrderPrefs() {
      return this.getPrefsRoot().node("order");
   }

   private static Logger log() {
      return Logger.getLogger(TableColumnPrefsSaver.class.getName());
   }
}
