package js.java.tools.DataTip;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

abstract class DataTipListener extends MouseInputAdapter implements ComponentListener {
   private DataTipPopup dataTipPopup;
   private static final Class[] NO_PARAMETERS = new Class[0];
   private static final Object[] NO_ARGUMENTS = new Object[0];
   private static Class mouseInfoClass;
   private static Method getPointerInfoMethod;
   private static Class pointerInfoClass;
   private static Method getLocationMethod;

   DataTipListener() {
      super();
   }

   abstract DataTipCell getCell(JComponent var1, Point var2);

   public void mousePressed(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent event) {
      this.checkShowOrHide(event);
   }

   public void mouseExited(MouseEvent event) {
      this.checkShowOrHide(event);
   }

   public void mouseDragged(MouseEvent event) {
      this.checkShowOrHide(event);
   }

   public void mouseMoved(MouseEvent event) {
      this.checkShowOrHide(event);
   }

   private void checkShowOrHide(MouseEvent event) {
      JComponent component = (JComponent)event.getSource();
      Point mousePosition = event.getPoint();
      this.checkShowOrHide(component, mousePosition);
   }

   private void checkShowOrHide(JComponent component, Point mousePosition) {
      Window windowAncestor = SwingUtilities.getWindowAncestor(component);
      if (windowAncestor != null && windowAncestor.isActive()) {
         DataTipCell dataTipCell = this.getCell(component, mousePosition);
         Rectangle visRect = component.getVisibleRect();
         if (!visRect.contains(mousePosition)) {
            dataTipCell = DataTipCell.NONE;
         }

         DataTipCell currentPopupCell = this.getCurrentPopupCell();
         if (!dataTipCell.equals(currentPopupCell)) {
            this.hideTip();
            if (dataTipCell.isSet()) {
               this.dataTipPopup = this.createPopup(component, mousePosition, dataTipCell);
            }
         }
      } else {
         this.hideTip();
      }
   }

   private DataTipCell getCurrentPopupCell() {
      return !this.isTipShown() ? DataTipCell.NONE : this.dataTipPopup.getCell();
   }

   private DataTipPopup createPopup(JComponent component, Point mousePosition, DataTipCell dataTipCell) {
      Rectangle cellBounds = dataTipCell.getCellBounds();
      Rectangle visRect = component.getVisibleRect();
      Rectangle visibleCellRectangle = cellBounds.intersection(visRect);
      if (!visibleCellRectangle.contains(mousePosition)) {
         return null;
      } else {
         Component rendererComponent = dataTipCell.getRendererComponent();
         Dimension rendCompDim = rendererComponent.getMinimumSize();
         Rectangle rendCompBounds = new Rectangle(cellBounds.getLocation(), rendCompDim);
         if (cellBounds.contains(rendCompBounds) && visRect.contains(rendCompBounds)) {
            return null;
         } else {
            Dimension preferredSize = rendererComponent.getPreferredSize();
            Point tipPosition = cellBounds.getLocation();
            int width = Math.max(cellBounds.width, preferredSize.width);
            int height = Math.max(cellBounds.height, preferredSize.height);
            Dimension tipDimension = new Dimension(width, height);
            return new DataTipPopup(component, dataTipCell, tipPosition, tipDimension);
         }
      }
   }

   private boolean isTipShown() {
      return this.dataTipPopup != null && this.dataTipPopup.isTipShown();
   }

   private void hideTip() {
      if (this.dataTipPopup != null) {
         this.dataTipPopup.hideTip();
         this.dataTipPopup = null;
      }
   }

   public void componentResized(ComponentEvent e) {
      this.checkShowOrHide(e);
   }

   public void componentMoved(ComponentEvent e) {
      this.checkShowOrHide(e);
   }

   public void componentShown(ComponentEvent e) {
      this.checkShowOrHide(e);
   }

   public void componentHidden(ComponentEvent e) {
      this.hideTip();
   }

   private void checkShowOrHide(ComponentEvent e) {
      JComponent component = (JComponent)e.getSource();
      Point mousePosition = getCurrentMousePosition();
      if (mousePosition == null) {
         this.hideTip();
      } else {
         SwingUtilities.convertPointFromScreen(mousePosition, component);
         this.checkShowOrHide(component, mousePosition);
      }
   }

   private static Point getCurrentMousePosition() {
      if (mouseInfoClass == null) {
         return null;
      } else {
         try {
            Object pointerInfo = getPointerInfoMethod.invoke(null, NO_ARGUMENTS);
            return (Point)getLocationMethod.invoke(pointerInfo, NO_ARGUMENTS);
         } catch (InvocationTargetException | IllegalAccessException var2) {
            return null;
         }
      }
   }

   static {
      try {
         mouseInfoClass = Class.forName("java.awt.MouseInfo");
         getPointerInfoMethod = mouseInfoClass.getMethod("getPointerInfo", NO_PARAMETERS);
         pointerInfoClass = Class.forName("java.awt.PointerInfo");
         getLocationMethod = pointerInfoClass.getMethod("getLocation", NO_PARAMETERS);
      } catch (ClassNotFoundException | NoSuchMethodException var1) {
      }
   }
}
