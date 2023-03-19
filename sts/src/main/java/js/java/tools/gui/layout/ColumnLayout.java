package js.java.tools.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public class ColumnLayout implements LayoutManager {
   protected int hgap;
   protected int vgap;
   protected int cols;
   protected boolean forced_rtl = false;

   public ColumnLayout() {
      this(1, 0, 0);
   }

   public ColumnLayout(int cols) {
      this(cols, 0, 0);
   }

   public ColumnLayout(int cols, int hgap, int vgap) {
      super();
      if (cols == 0) {
         throw new IllegalArgumentException("rows and cols cannot both be zero");
      } else {
         this.cols = cols;
         this.hgap = hgap;
         this.vgap = vgap;
      }
   }

   public int getColumns() {
      return this.cols;
   }

   public void setColumns(int cols) {
      if (cols == 0) {
         throw new IllegalArgumentException("rows and cols cannot both be zero");
      } else {
         this.cols = cols;
      }
   }

   public int getHgap() {
      return this.hgap;
   }

   public void setHgap(int hgap) {
      this.hgap = hgap;
   }

   public int getVgap() {
      return this.vgap;
   }

   public void setVgap(int vgap) {
      this.vgap = vgap;
   }

   public void setForcedOppositeDirection(boolean force) {
      this.forced_rtl = force;
   }

   public void addLayoutComponent(String name, Component comp) {
   }

   public void removeLayoutComponent(Component comp) {
   }

   public Dimension preferredLayoutSize(Container parent) {
      synchronized(parent.getTreeLock()) {
         Insets insets = parent.getInsets();
         int ncomponents = parent.getComponentCount();
         int ncols = this.cols;
         if (this.cols == 0) {
            return new Dimension();
         } else {
            int nrows = (ncomponents + ncols - 1) / ncols;
            int w = 0;
            int h = 0;

            for(int i = 0; i < ncomponents; ++i) {
               Component comp = parent.getComponent(i);
               Dimension d = comp.getPreferredSize();
               if (w < d.width) {
                  w = d.width;
               }

               if (h < d.height) {
                  h = d.height;
               }
            }

            return new Dimension(
               insets.left + insets.right + ncols * w + (ncols - 1) * this.hgap, insets.top + insets.bottom + nrows * h + (nrows - 1) * this.vgap
            );
         }
      }
   }

   public Dimension minimumLayoutSize(Container parent) {
      synchronized(parent.getTreeLock()) {
         Insets insets = parent.getInsets();
         int ncomponents = parent.getComponentCount();
         int ncols = this.cols;
         if (this.cols == 0) {
            return new Dimension();
         } else {
            int nrows = (ncomponents + ncols - 1) / ncols;
            int w = 0;
            int h = 0;

            for(int i = 0; i < ncomponents; ++i) {
               Component comp = parent.getComponent(i);
               Dimension d = comp.getMinimumSize();
               if (w < d.width) {
                  w = d.width;
               }

               if (h < d.height) {
                  h = d.height;
               }
            }

            return new Dimension(
               insets.left + insets.right + ncols * w + (ncols - 1) * this.hgap, insets.top + insets.bottom + nrows * h + (nrows - 1) * this.vgap
            );
         }
      }
   }

   public void layoutContainer(Container parent) {
      synchronized(parent.getTreeLock()) {
         Insets insets = parent.getInsets();
         int ncomponents = parent.getComponentCount();
         int ncols = this.cols;
         boolean rtl = this.forced_rtl || !parent.getComponentOrientation().isLeftToRight();
         if (ncomponents != 0) {
            int nrows = (ncomponents + ncols - 1) / ncols;
            int w = parent.getWidth() - (insets.left + insets.right);
            int h = parent.getHeight() - (insets.top + insets.bottom);
            w = this.validateWidth((w - (ncols - 1) * this.hgap) / ncols);
            h = this.validateHeight((h - (nrows - 1) * this.vgap) / nrows);
            int c = 0;

            for(int x = insets.left; c < ncols; x += w + this.hgap) {
               int r = 0;

               for(int y = insets.top; r < nrows; y += h + this.vgap) {
                  int i = r + c * nrows;
                  if (rtl) {
                     i = ncomponents - i - 1;
                  }

                  if (i < ncomponents) {
                     parent.getComponent(i).setBounds(x, y, w, h);
                  }

                  ++r;
               }

               ++c;
            }
         }
      }
   }

   public String toString() {
      return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + ",cols=" + this.cols + "]";
   }

   protected int validateWidth(int w) {
      return w;
   }

   protected int validateHeight(int h) {
      return h;
   }
}
