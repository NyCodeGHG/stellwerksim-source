package js.java.tools.gui.table;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.EventObject;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;

public class JComponentCellEditor implements TableCellEditor, Serializable {
   protected EventListenerList listenerList = new EventListenerList();
   protected transient ChangeEvent changeEvent = null;
   protected JComponent editorComponent = null;

   public JComponentCellEditor() {
      super();
   }

   public Component getComponent() {
      return this.editorComponent;
   }

   public Object getCellEditorValue() {
      return this.editorComponent;
   }

   public boolean isCellEditable(EventObject anEvent) {
      return true;
   }

   public boolean shouldSelectCell(EventObject anEvent) {
      if (this.editorComponent != null && anEvent instanceof MouseEvent && ((MouseEvent)anEvent).getID() == 501) {
         Component dispatchComponent = SwingUtilities.getDeepestComponentAt(this.editorComponent, 3, 3);
         MouseEvent e = (MouseEvent)anEvent;
         MouseEvent e2 = new MouseEvent(dispatchComponent, 502, e.getWhen() + 100000L, e.getModifiers(), 3, 3, e.getClickCount(), e.isPopupTrigger());
         dispatchComponent.dispatchEvent(e2);
         e2 = new MouseEvent(dispatchComponent, 500, e.getWhen() + 100001L, e.getModifiers(), 3, 3, 1, e.isPopupTrigger());
         dispatchComponent.dispatchEvent(e2);
      }

      return false;
   }

   public boolean stopCellEditing() {
      this.fireEditingStopped();
      return true;
   }

   public void cancelCellEditing() {
      this.fireEditingCanceled();
   }

   public void addCellEditorListener(CellEditorListener l) {
      this.listenerList.add(CellEditorListener.class, l);
   }

   public void removeCellEditorListener(CellEditorListener l) {
      this.listenerList.remove(CellEditorListener.class, l);
   }

   public void fireEditingStopped() {
      Object[] listeners = this.listenerList.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == CellEditorListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((CellEditorListener)listeners[i + 1]).editingStopped(this.changeEvent);
         }
      }
   }

   protected void fireEditingCanceled() {
      Object[] listeners = this.listenerList.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == CellEditorListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((CellEditorListener)listeners[i + 1]).editingCanceled(this.changeEvent);
         }
      }
   }

   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      this.editorComponent = (JComponent)value;
      return this.editorComponent;
   }
}
