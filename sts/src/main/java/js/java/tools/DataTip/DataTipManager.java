package js.java.tools.DataTip;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.security.AccessControlException;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

public class DataTipManager {
   private static DataTipManager instance;
   private ListDataTipListener listMouseListener = new ListDataTipListener();
   private TableDataTipListener tableMouseListener = new TableDataTipListener();
   private TreeDataTipListener treeMouseListener = new TreeDataTipListener();
   private Component parentComponent;
   private Window tipComponentWindow;
   private MouseEvent lastMouseEvent;
   private static boolean allowUntrustedUsage = true;

   private DataTipManager() {
      try {
         long eventMask = 131120L;
         Toolkit.getDefaultToolkit().addAWTEventListener(new DataTipManager.MouseEventModifier(), eventMask);
      } catch (AccessControlException var3) {
         if (!allowUntrustedUsage) {
            throw new RuntimeException("DataTipManager needs to run in a trusted application", var3);
         }
      }
   }

   static void enableUntrustedUsage(boolean enable) {
      allowUntrustedUsage = enable;
   }

   public static synchronized DataTipManager get() {
      if (instance == null) {
         instance = new DataTipManager();
      }

      return instance;
   }

   public synchronized void register(JList list) {
      list.addMouseListener(this.listMouseListener);
      list.addMouseMotionListener(this.listMouseListener);
      list.addComponentListener(this.listMouseListener);
   }

   public synchronized void register(JTree tree) {
      tree.addMouseListener(this.treeMouseListener);
      tree.addMouseMotionListener(this.treeMouseListener);
      tree.addComponentListener(this.treeMouseListener);
   }

   public synchronized void register(JTable table) {
      table.addMouseListener(this.tableMouseListener);
      table.addMouseMotionListener(this.tableMouseListener);
      table.addComponentListener(this.tableMouseListener);
   }

   void setTipWindow(Component parentComponent, Window dataTipComponent) {
      this.parentComponent = parentComponent;
      this.tipComponentWindow = dataTipComponent;
   }

   public boolean handleEventFromParentComponent(MouseEvent mouseEvent) {
      if (mouseEvent == this.lastMouseEvent) {
         return false;
      } else {
         Object source = mouseEvent.getSource();
         if (source != this.parentComponent) {
            return false;
         } else {
            int id = mouseEvent.getID();
            int x = mouseEvent.getX();
            int y = mouseEvent.getY();
            long when = mouseEvent.getWhen();
            int modifiers = mouseEvent.getModifiers();
            int clickCount = mouseEvent.getClickCount();
            boolean isPopupTrigger = mouseEvent.isPopupTrigger();
            if (id == 505) {
               Point point = SwingUtilities.convertPoint(this.parentComponent, x, y, this.tipComponentWindow);
               if (this.tipComponentWindow.contains(point)) {
                  MouseEvent newEvent = new MouseEvent(this.parentComponent, 503, when, modifiers, x, y, clickCount, isPopupTrigger);
                  this.parentComponent.dispatchEvent(newEvent);
                  return this.parentComponent != null;
               }
            }

            return false;
         }
      }
   }

   public void handleEventFromDataTipComponent(MouseEvent mouseEvent) {
      mouseEvent.consume();
      int id = mouseEvent.getID();
      if (id != 504) {
         int x = mouseEvent.getX();
         int y = mouseEvent.getY();
         Point point = SwingUtilities.convertPoint(mouseEvent.getComponent(), x, y, this.parentComponent);
         if (id == 505 && this.parentComponent.contains(point)) {
            return;
         }

         long when = mouseEvent.getWhen();
         int modifiers = mouseEvent.getModifiers();
         int clickCount = mouseEvent.getClickCount();
         boolean isPopupTrigger = mouseEvent.isPopupTrigger();
         MouseEvent newEvent;
         if (id == 507) {
            MouseWheelEvent mouseWheelEvent = (MouseWheelEvent)mouseEvent;
            int scrollType = mouseWheelEvent.getScrollType();
            int scrollAmount = mouseWheelEvent.getScrollAmount();
            int wheelRotation = mouseWheelEvent.getWheelRotation();
            newEvent = new MouseWheelEvent(
               this.parentComponent, id, when, modifiers, point.x, point.y, clickCount, isPopupTrigger, scrollType, scrollAmount, wheelRotation
            );
         } else {
            newEvent = new MouseEvent(this.parentComponent, id, when, modifiers, point.x, point.y, clickCount, isPopupTrigger);
         }

         Component parentComponentBackup = this.parentComponent;
         this.parentComponent.dispatchEvent(newEvent);
         if (this.parentComponent == null && id != 505) {
            MouseEvent exitEvent = new MouseEvent(parentComponentBackup, 505, when, modifiers, point.x, point.y, clickCount, isPopupTrigger);
            parentComponentBackup.dispatchEvent(exitEvent);
         }

         if (this.tipComponentWindow != null && id != 503) {
            this.tipComponentWindow.repaint();
         }
      }
   }

   private class MouseEventModifier implements AWTEventListener {
      private MouseEventModifier() {
      }

      public void eventDispatched(AWTEvent event) {
         if (DataTipManager.this.tipComponentWindow != null) {
            Object source = event.getSource();
            if (source == DataTipManager.this.parentComponent) {
               MouseEvent mouseEvent = (MouseEvent)event;
               boolean filter = DataTipManager.this.handleEventFromParentComponent(mouseEvent);
               if (filter) {
                  mouseEvent.consume();
               } else {
                  DataTipManager.this.lastMouseEvent = mouseEvent;
               }
            }
         }
      }
   }
}
