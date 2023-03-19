package js.java.isolate.fahrplaneditor;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

class lineLayoutManager implements LayoutManager {
   public static final int TM_HALT = 30;
   public static final int TM_BAHNHOF = -1;
   private int minWidth = 0;
   private int minHeight = 0;
   private int preferredWidth = 0;
   private int preferredHeight = 0;
   private boolean sizeUnknown = true;
   private int lineheight;
   private int xoffset = 0;

   lineLayoutManager(int h) {
      super();
      this.lineheight = h;
   }

   lineLayoutManager(int h, int xoffset) {
      this(h);
      this.xoffset = xoffset;
   }

   public void addLayoutComponent(String name, Component comp) {
   }

   public void removeLayoutComponent(Component comp) {
   }

   private void setSizes(Container parent) {
      int nComps = parent.getComponentCount();
      Dimension d = null;
      this.preferredWidth = this.xoffset;
      this.minWidth = this.xoffset;
      this.preferredHeight = this.minHeight = 0;

      for(int i = 0; i < nComps; ++i) {
         Component c = parent.getComponent(i);
         if (c.isVisible()) {
            d = c.getPreferredSize();
            this.minWidth = this.preferredWidth = Math.max(d.width, this.preferredWidth);
            if (this.lineheight > 0) {
               this.preferredHeight += this.lineheight;
            } else {
               this.preferredHeight += d.height;
            }

            this.minHeight = this.preferredHeight;
         }
      }
   }

   public Dimension preferredLayoutSize(Container parent) {
      Dimension dim = new Dimension(0, 0);
      this.setSizes(parent);
      Insets insets = parent.getInsets();
      dim.width = this.preferredWidth + insets.left + insets.right;
      dim.height = this.preferredHeight + insets.top + insets.bottom;
      this.sizeUnknown = false;
      return dim;
   }

   public Dimension minimumLayoutSize(Container parent) {
      Dimension dim = new Dimension(0, 0);
      this.setSizes(parent);
      Insets insets = parent.getInsets();
      dim.width = this.minWidth + insets.left + insets.right;
      dim.height = this.minHeight + insets.top + insets.bottom;
      this.sizeUnknown = false;
      return dim;
   }

   public void layoutContainer(Container parent) {
      Insets insets = parent.getInsets();
      int maxWidth = this.preferredWidth - (insets.top + insets.bottom);
      int nComps = parent.getComponentCount();
      int x = insets.left + this.xoffset;
      int y = insets.top;
      if (this.sizeUnknown) {
         this.setSizes(parent);
      }

      for(int i = 0; i < nComps; ++i) {
         Component c = parent.getComponent(i);
         if (c.isVisible()) {
            Dimension d = c.getPreferredSize();
            d.width = maxWidth;
            if (this.lineheight > 0) {
               d.height = this.lineheight;
            }

            c.setBounds(x, y, d.width, d.height);
            y += d.height;
         }
      }
   }

   public String toString() {
      return this.getClass().getName();
   }
}
