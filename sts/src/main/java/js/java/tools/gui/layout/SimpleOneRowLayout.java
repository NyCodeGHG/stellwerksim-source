package js.java.tools.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class SimpleOneRowLayout implements LayoutManager {
   private int minWidth = 0;
   private int minHeight = 0;
   private int preferredWidth = 0;
   private int preferredHeight = 0;
   private boolean needRecalc = true;
   private int fixedElementWidth = 0;
   private boolean bottomUp = false;

   public void addLayoutComponent(String name, Component comp) {
      this.needRecalc = true;
   }

   public void removeLayoutComponent(Component comp) {
      this.needRecalc = true;
   }

   private void setPosAndSize(Container parent) {
      int nComps = parent.getComponentCount();
      Dimension d = null;
      this.preferredWidth = this.minWidth = 0;
      this.preferredHeight = this.minHeight = 0;

      for (int i = 0; i < nComps; i++) {
         Component c = parent.getComponent(i);
         if (c.isVisible()) {
            d = c.getPreferredSize();
            this.minHeight = this.preferredHeight = Math.max(d.height, this.preferredHeight);
            if (this.fixedElementWidth > 0) {
               this.preferredWidth = this.preferredWidth + this.fixedElementWidth;
            } else {
               this.preferredWidth = this.preferredWidth + d.width;
            }
         }
      }

      this.minWidth = this.preferredWidth;
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
      int nComps = parent.getComponentCount();
      int x = insets.left;
      int y = insets.top;

      for (int i = 0; i < nComps; i++) {
         Component c;
         if (this.bottomUp) {
            c = parent.getComponent(nComps - i - 1);
         } else {
            c = parent.getComponent(i);
         }

         if (c.isVisible()) {
            Dimension d = c.getPreferredSize();
            d.height = this.minHeight;
            if (this.fixedElementWidth > 0) {
               d.width = this.fixedElementWidth;
            }

            c.setBounds(x, y, d.width, d.height);
            x += d.width;
         }
      }
   }

   public void setBottomUpDirection(boolean b) {
      this.bottomUp = b;
   }
}
