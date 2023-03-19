package js.java.tools.DataTip;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

class DataTipPopup {
   private Popup popup;
   private DataTipCell cell;

   DataTipPopup(JComponent parent, DataTipCell cell, Point tipPosition, Dimension tipDimension) {
      super();
      this.cell = cell;
      Rectangle parentVisibleRect = parent.getVisibleRect();
      Rectangle withoutBorderRectangle = parentVisibleRect.intersection(new Rectangle(tipPosition, tipDimension));
      withoutBorderRectangle.translate(-tipPosition.x, -tipPosition.y);
      DataTipComponent dataTipComponent = new DataTipComponent(cell, withoutBorderRectangle, parent.getBackground());
      Dimension tipDimensionClipped = new Dimension(tipDimension.width, tipDimension.height);
      Window windowAncestor = SwingUtilities.getWindowAncestor(parent);
      GraphicsConfiguration gc = windowAncestor.getGraphicsConfiguration();
      Rectangle screenBounds = gc.getBounds();
      Point tipScreenPosition = new Point(tipPosition.x, tipPosition.y);
      SwingUtilities.convertPointToScreen(tipScreenPosition, parent);
      Point tipPositionClipped = new Point();
      tipPositionClipped.x = Math.max(tipScreenPosition.x, screenBounds.x);
      tipPositionClipped.y = Math.max(tipScreenPosition.y, screenBounds.y);
      tipDimensionClipped.width = Math.min(screenBounds.x + screenBounds.width - tipPositionClipped.x, tipDimensionClipped.width);
      tipDimensionClipped.height = Math.min(screenBounds.y + screenBounds.height - tipPositionClipped.y, tipDimensionClipped.height);
      SwingUtilities.convertPointFromScreen(tipPositionClipped, parent);
      dataTipComponent.setPreferredSize(tipDimensionClipped);
      SwingUtilities.convertPointToScreen(tipPosition, parent);
      PopupFactory popupFactory = PopupFactory.getSharedInstance();
      this.popup = popupFactory.getPopup(parent, dataTipComponent, tipPosition.x, tipPosition.y);
      this.popup.show();
      Window componentWindow = SwingUtilities.windowForComponent(parent);
      Window tipWindow = SwingUtilities.windowForComponent(dataTipComponent);
      boolean isHeavyWeight = tipWindow != null && tipWindow != componentWindow;
      dataTipComponent.setHeavyWeight(isHeavyWeight);
      if (isHeavyWeight) {
         DataTipManager.get().setTipWindow(parent, tipWindow);
      }
   }

   DataTipCell getCell() {
      return this.cell;
   }

   void hideTip() {
      if (this.popup != null) {
         this.popup.hide();
         this.popup = null;
         DataTipManager.get().setTipWindow(null, null);
      }
   }

   public boolean isTipShown() {
      return this.popup != null;
   }
}
