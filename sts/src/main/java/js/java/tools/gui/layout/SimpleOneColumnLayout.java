package js.java.tools.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class SimpleOneColumnLayout implements LayoutManager {
   protected int minWidth = 0;
   protected int minHeight = 0;
   protected int preferredWidth = 0;
   protected int preferredHeight = 0;
   protected boolean needRecalc = true;
   protected int fixedLineHeight = 0;
   protected boolean bottomUp = false;

   public SimpleOneColumnLayout() {
      super();
   }

   public void addLayoutComponent(String name, Component comp) {
      this.needRecalc = true;
   }

   public void removeLayoutComponent(Component comp) {
      this.needRecalc = true;
   }

   protected void setPosAndSize(Container parent) {
      int nComps = parent.getComponentCount();
      Dimension d = null;
      this.preferredWidth = this.minWidth = 0;
      this.preferredHeight = this.minHeight = 0;

      for(int i = 0; i < nComps; ++i) {
         Component c = parent.getComponent(i);
         if (c.isVisible()) {
            d = c.getPreferredSize();
            this.minWidth = this.preferredWidth = Math.max(d.width, this.preferredWidth);
            if (this.fixedLineHeight > 0) {
               this.preferredHeight += this.fixedLineHeight;
            } else {
               this.preferredHeight += d.height;
            }
         }
      }

      this.minHeight = this.preferredHeight;
      this.needRecalc = false;
   }

   public Dimension preferredLayoutSize(Container parent) {
      this.setPosAndSize(parent);
      Insets insets = parent.getInsets();
      Dimension dim = new Dimension();
      dim.width = parent.getWidth() - insets.left - insets.right;
      dim.height = this.preferredHeight + insets.top + insets.bottom;
      return dim;
   }

   public Dimension minimumLayoutSize(Container parent) {
      this.setPosAndSize(parent);
      Insets insets = parent.getInsets();
      Dimension dim = new Dimension();
      dim.width = this.minWidth + insets.left + insets.right;
      dim.height = this.minHeight + insets.top + insets.bottom;
      return dim;
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

            c.setBounds(x, y, d.width, d.height);
            y += d.height;
         }
      }
   }

   public void setBottomUpDirection(boolean b) {
      this.bottomUp = b;
   }
}
