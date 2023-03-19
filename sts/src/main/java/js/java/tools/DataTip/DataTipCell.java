package js.java.tools.DataTip;

import java.awt.Component;
import java.awt.Rectangle;

interface DataTipCell {
   DataTipCell NONE = new DataTipCell() {
      @Override
      public boolean isSet() {
         return false;
      }

      @Override
      public Component getRendererComponent() {
         return null;
      }

      @Override
      public Rectangle getCellBounds() {
         return null;
      }

      public boolean equals(Object obj) {
         return false;
      }

      public int hashCode() {
         return 0;
      }
   };

   boolean isSet();

   Component getRendererComponent();

   Rectangle getCellBounds();
}
