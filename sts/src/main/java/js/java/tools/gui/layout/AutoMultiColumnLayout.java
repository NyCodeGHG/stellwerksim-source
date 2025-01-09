package js.java.tools.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;

public class AutoMultiColumnLayout extends ColumnLayout implements ThinkingLayout {
   private int prefW = 10;
   private int prefH = 10;
   private final AutoMultiColumnLayout.PlacingAlgorithm pa;

   public AutoMultiColumnLayout() {
      super(1);
      this.pa = new AutoMultiColumnLayout.MaxHeight();
   }

   public AutoMultiColumnLayout(AutoMultiColumnLayout.PlacingAlgorithm p) {
      super(1);
      this.pa = p;
   }

   @Override
   public Dimension preferredLayoutSize(Container parent) {
      synchronized (parent.getTreeLock()) {
         int ncomponents = parent.getComponentCount();
         int w = 0;
         int h = 0;

         for (int i = 0; i < ncomponents; i++) {
            Component comp = parent.getComponent(i);
            Dimension d = comp.getPreferredSize();
            if (w < d.width) {
               w = d.width;
            }

            if (h < d.height) {
               h = d.height;
            }
         }

         this.prefW = w;
         this.prefH = h;
         return super.preferredLayoutSize(parent);
      }
   }

   @Override
   public Dimension proveLayout(Container parent, int newWidth, int newHeight) {
      int ncomponents = parent.getComponentCount();
      int w = 0;
      int h = 0;

      for (int i = 0; i < ncomponents; i++) {
         Component comp = parent.getComponent(i);
         Dimension d = comp.getMinimumSize();
         if (w < d.width) {
            w = d.width;
         }

         if (h < d.height) {
            h = d.height;
         }
      }

      this.prefW = w;
      this.prefH = h;
      w = this.validateWidth(w);
      h = this.validateHeight(h);
      Insets insets = parent.getInsets();
      int cW = newWidth - insets.left - insets.right;
      int cH = newHeight - insets.top - insets.bottom;
      this.cols = this.calcCols(parent, cW, cH, w, h);
      Dimension ret = super.minimumLayoutSize(parent);
      if (this.pa.ignoreWidth()) {
         ret.width = -ret.width;
      }

      if (this.pa.ignoreHeight()) {
         ret.height = -ret.height;
      }

      return ret;
   }

   private int calcCols(Container parent, int containerWidth, int containerHeight, int minElementWidth, int minElementHeight) {
      return this.pa.calcCols(parent, containerWidth, containerHeight, minElementWidth, minElementHeight);
   }

   @Override
   protected int validateWidth(int w) {
      return this.pa.overideWidth() ? this.prefW : super.validateWidth(w);
   }

   @Override
   protected int validateHeight(int h) {
      return this.pa.overideHeight() ? this.prefH : super.validateHeight(h);
   }

   public static class MaxHeight extends AutoMultiColumnLayout.PlacingAlgorithm {
      @Override
      int calcCols(Container parent, int containerWidth, int containerHeight, int minElementWidth, int minElementHeight) {
         if (containerHeight > 0 && minElementHeight > 0) {
            int mh = containerHeight / minElementHeight;
            if (mh == 0) {
               mh = 1;
            }

            int c = parent.getComponentCount();
            int cols = c / mh;
            if (c % mh > 0) {
               cols++;
            }

            if (cols < 1) {
               cols = 1;
            }

            return cols;
         } else {
            return parent.getComponentCount();
         }
      }

      @Override
      public boolean overideHeight() {
         return true;
      }

      @Override
      public boolean ignoreHeight() {
         return true;
      }
   }

   public static class MaxWidth extends AutoMultiColumnLayout.PlacingAlgorithm {
      @Override
      int calcCols(Container parent, int containerWidth, int containerHeight, int minElementWidth, int minElementHeight) {
         if (containerWidth > 0) {
            int mh = containerWidth / minElementWidth;
            if (mh == 0) {
               mh = 1;
            }

            return mh;
         } else {
            return parent.getComponentCount();
         }
      }

      @Override
      public boolean overideWidth() {
         return true;
      }

      @Override
      public boolean overideHeight() {
         return true;
      }

      @Override
      public boolean ignoreWidth() {
         return true;
      }
   }

   public abstract static class PlacingAlgorithm {
      public boolean overideWidth() {
         return false;
      }

      public boolean overideHeight() {
         return false;
      }

      public boolean ignoreWidth() {
         return false;
      }

      public boolean ignoreHeight() {
         return false;
      }

      abstract int calcCols(Container var1, int var2, int var3, int var4, int var5);
   }
}
