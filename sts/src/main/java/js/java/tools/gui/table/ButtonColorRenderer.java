package js.java.tools.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import js.java.tools.ColorText;

public class ButtonColorRenderer extends JComponent implements TableCellRenderer, TableCellEditor {
   private ColorRenderer s = new ColorRenderer();
   protected EventListenerList listenerList = new EventListenerList();
   private boolean paintBox = true;
   private Component l = null;
   protected EventListenerList buttonPressedList = new EventListenerList();

   public ButtonColorRenderer() {
      super();
      this.setLabel(this.s);
   }

   public ButtonColorRenderer(ButtonPressedListener l) {
      this();
      this.addButtonPressedListener(l);
   }

   private void checkDarken(ButtonColorText bct, JComponent s) {
      if (bct.isDarken()) {
         s.setForeground(s.getForeground().brighter().brighter());
         s.setBackground(s.getBackground().darker());
      }
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (value instanceof ColorText) {
         ((ColorText)value).setSpecial(false);
      }

      this.s.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      if (value instanceof ButtonColorText) {
         this.paintBox = true;
         this.checkDarken((ButtonColorText)value, this.s);
      } else {
         this.paintBox = false;
      }

      return this;
   }

   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      if (value instanceof ButtonColorText) {
         this.paintBox = true;
         this.checkDarken((ButtonColorText)value, this.s);
         this.fireButtonPressed((ButtonColorText)value);
      } else {
         this.paintBox = false;
      }

      this.fireEditingStopped();
      return this.getTableCellRendererComponent(table, value, isSelected, true, row, column);
   }

   public Object getCellEditorValue() {
      return null;
   }

   public boolean isCellEditable(EventObject e) {
      return this.mouseClicked((MouseEvent)e);
   }

   public boolean shouldSelectCell(EventObject anEvent) {
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

   protected void fireEditingStopped() {
      Object[] listeners = this.listenerList.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == CellEditorListener.class) {
            ((CellEditorListener)listeners[i + 1]).editingStopped(new ChangeEvent(this));
         }
      }
   }

   protected void fireEditingCanceled() {
      Object[] listeners = this.listenerList.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == CellEditorListener.class) {
            ((CellEditorListener)listeners[i + 1]).editingCanceled(new ChangeEvent(this));
         }
      }
   }

   public void paintComponent(Graphics g) {
      if (this.paintBox) {
         g.setColor(Color.WHITE);
         g.fillRect(0, 2, 10, 10);
         g.setColor(UIManager.getDefaults().getColor("Button.background"));
         g.fill3DRect(0, 2, 10, 10, true);
         g.fill3DRect(1, 3, 8, 8, true);
         g.setColor(Color.BLACK);
         g.drawLine(3, 5, 6, 5);
         g.drawLine(5, 7, 7, 7);
         g.drawLine(4, 9, 6, 9);
      }
   }

   public void setLabel(Component a) {
      this.add(a);
      a.setLocation(11, 0);
      this.l = a;
   }

   public void reshape(int x, int y, int w, int h) {
      super.reshape(x, y, w, h);
      this.l.reshape(11, 0, w - 11, h);
   }

   public boolean mouseClicked(MouseEvent e) {
      if (e.getX() < 10) {
         this.repaint();
         return true;
      } else {
         return false;
      }
   }

   public void addButtonPressedListener(ButtonPressedListener l) {
      this.buttonPressedList.add(ButtonPressedListener.class, l);
   }

   public void removeButtonPressedListener(ButtonPressedListener l) {
      this.buttonPressedList.remove(ButtonPressedListener.class, l);
   }

   protected void fireButtonPressed(ButtonColorText f) {
      Object[] listeners = this.buttonPressedList.getListenerList();

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == ButtonPressedListener.class) {
            ((ButtonPressedListener)listeners[i + 1]).clicked(new ChangeEvent(f));
         }
      }
   }
}
