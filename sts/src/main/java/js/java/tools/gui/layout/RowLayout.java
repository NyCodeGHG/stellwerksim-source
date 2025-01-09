package js.java.tools.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;

public class RowLayout implements LayoutManager {
   private int minWidth = 0;
   private int minHeight = 0;
   private int preferredWidth = 0;
   private int preferredHeight = 0;
   private boolean needRecalc = true;
   private ArrayList<Integer> columnSizes = new ArrayList();
   private int maxRows;

   public RowLayout(int r) {
      this.maxRows = r;
      if (r < 1) {
         throw new IllegalArgumentException("max rows <1");
      }
   }

   public void addLayoutComponent(String name, Component comp) {
      this.needRecalc = true;
   }

   public void removeLayoutComponent(Component comp) {
      this.needRecalc = true;
   }

   private void setPosAndSize(Container parent) {
      if (this.needRecalc) {
         this.columnSizes.clear();
         this.preferredWidth = this.preferredHeight = 0;
         this.minWidth = this.minHeight = 0;
         int maxHeight = 0;
         int maxColumnWidth = 0;
         int maxWidth = 0;
         int row = 0;
         Insets insets = parent.getInsets();
         int nComps = parent.getComponentCount();

         for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);
            Dimension d = c.getPreferredSize();
            maxHeight = Math.max(maxHeight, d.height);
            maxColumnWidth = Math.max(maxColumnWidth, d.width);
            if (++row >= this.maxRows) {
               this.columnSizes.add(maxColumnWidth);
               maxWidth += maxColumnWidth;
               maxColumnWidth = 0;
               row = 0;
            }
         }

         if (maxColumnWidth > 0) {
            this.columnSizes.add(maxColumnWidth);
            maxWidth += maxColumnWidth;
         }

         this.minHeight = this.preferredHeight = maxHeight * this.maxRows + insets.bottom + insets.top;
         this.minWidth = this.preferredWidth = maxWidth + insets.left + insets.right;
         this.needRecalc = false;
      }
   }

   public Dimension preferredLayoutSize(Container parent) {
      this.setPosAndSize(parent);
      return new Dimension(this.preferredWidth, this.preferredHeight);
   }

   public Dimension minimumLayoutSize(Container parent) {
      this.setPosAndSize(parent);
      return new Dimension(this.minWidth, this.minHeight);
   }

   public void layoutContainer(Container parent) {
      this.setPosAndSize(parent);
      Insets insets = parent.getInsets();
      int nComps = parent.getComponentCount();
      int x = 0;
      int h = (parent.getHeight() - insets.bottom - insets.top) / 2;
      int column = 0;
      int row = 0;

      for (int i = 0; i < nComps; i++) {
         Component c = parent.getComponent(i);
         int w = (Integer)this.columnSizes.get(column);
         c.setSize(w, h);
         c.setLocation(insets.left + x, insets.top + row * h);
         if (++row >= this.maxRows) {
            row = 0;
            column++;
            x += w;
         }
      }
   }
}
