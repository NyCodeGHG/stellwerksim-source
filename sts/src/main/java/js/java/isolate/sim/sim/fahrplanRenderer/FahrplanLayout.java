package js.java.isolate.sim.sim.fahrplanRenderer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import js.java.tools.gui.layout.SimpleOneColumnLayout;

@Deprecated
public class FahrplanLayout extends SimpleOneColumnLayout {
   public FahrplanLayout() {
      super();
   }

   public void layoutContainer(Container parent) {
      if (this.needRecalc) {
         this.setPosAndSize(parent);
      }

      Insets insets = parent.getInsets();
      int maxWidth = parent.getWidth() - (insets.top + insets.bottom);
      int nComps = parent.getComponentCount();
      int x = insets.left;
      int y = insets.top;

      for(int i = 0; i < nComps; ++i) {
         Component c;
         if (this.bottomUp) {
            c = parent.getComponent(nComps - i - 1);
         } else {
            c = parent.getComponent(i);
         }

         if (c.isVisible()) {
            Dimension d = c.getPreferredSize();
            d.width = maxWidth;
            if (this.fixedLineHeight > 0) {
               d.height = this.fixedLineHeight;
            }

            int dx = x;
            if (c instanceof lineRenderer) {
               dx = x + 10;
               d.width -= 10;
            }

            c.setBounds(dx, y, d.width, d.height);
            y += d.height;
         }
      }
   }
}
