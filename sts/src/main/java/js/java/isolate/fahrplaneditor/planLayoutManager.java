package js.java.isolate.fahrplaneditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

class planLayoutManager implements LayoutManager {
   private static final int LEFT = 50;
   private static final int STARTLEFT = 5;
   private static final int TM_WIDTH = 10;
   private static final int WARN_WIDTH = 7;
   private static final Color TM_COL1 = new Color(221, 221, 221);
   private static final Color TM_COL2 = new Color(187, 187, 187);
   private static final planLayoutManager.element[] elemwidth = new planLayoutManager.element[]{
      new planLayoutManager.element(24, null),
      new planLayoutManager.element(26, null),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(100, null),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(55, new Color(204, 204, 255)),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(55, new Color(204, 204, 255)),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(150, null),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(150, null),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(90, null),
      new planLayoutManager.element(100, null),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(10, TM_COL1),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(10, TM_COL2),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(10, TM_COL1),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(10, TM_COL2),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(10, TM_COL1),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(10, TM_COL2),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(10, TM_COL1),
      new planLayoutManager.element(7, 0),
      new planLayoutManager.element(10, TM_COL2),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL1, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL2, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL1, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL2, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL1, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL2, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL1, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL2, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL1, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL2, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL1, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL2, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL1, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL2, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL1, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL2, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL1, true),
      new planLayoutManager.element(7, 0, true),
      new planLayoutManager.element(10, TM_COL2, true),
      new planLayoutManager.element(50)
   };
   private int minWidth = 0;
   private int minHeight = 0;
   private int preferredWidth = 0;
   private int preferredHeight = 0;
   private int xoffset = 0;
   private boolean sizeUnknown = true;
   private boolean headingMode = false;
   private static boolean hiddenMode = false;
   private boolean lastHiddenMode = false;

   planLayoutManager(boolean headingmode, int xoffset) {
      this.headingMode = headingmode;
      this.xoffset = xoffset;
   }

   planLayoutManager() {
   }

   public static void setHiddenMode(boolean m) {
      hiddenMode = m;
   }

   public void addLayoutComponent(String name, Component comp) {
   }

   public void removeLayoutComponent(Component comp) {
   }

   private void setSizes(Container parent) {
      this.lastHiddenMode = hiddenMode;
      int nComps = Math.min(parent.getComponentCount(), elemwidth.length);
      Dimension d = null;
      Insets insets = parent.getInsets();
      int x = insets.left + 5 + this.xoffset;
      this.preferredWidth = x;
      this.preferredHeight = 0;
      this.minWidth = x;
      this.minHeight = 0;

      for (int i = 0; i < nComps; i++) {
         Component c = parent.getComponent(i);
         if (c.isVisible() && (!this.lastHiddenMode || !elemwidth[i].hideable)) {
            d = c.getPreferredSize();
            d.width = elemwidth[i].width + elemwidth[i].PAD;
            if (elemwidth[i].col != null) {
               c.setBackground(elemwidth[i].col);
            }

            this.preferredWidth = this.preferredWidth + d.width;
            this.preferredHeight = Math.max(d.height, this.preferredHeight);
            this.minWidth = this.minWidth + d.width;
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
      dim.width = this.preferredWidth + insets.left + insets.right;
      dim.height = this.preferredHeight + insets.top + insets.bottom;
      this.sizeUnknown = false;
      return dim;
   }

   public void layoutContainer(Container parent) {
      Insets insets = parent.getInsets();
      int maxHeight = this.preferredHeight - (insets.top + insets.bottom);
      int nComps = Math.min(parent.getComponentCount(), elemwidth.length);
      int x = insets.left + 5 + this.xoffset;
      int y = insets.top;
      if (this.sizeUnknown || hiddenMode != this.lastHiddenMode) {
         this.setSizes(parent);
      }

      for (int i = 0; i < nComps; i++) {
         Component c = parent.getComponent(i);
         if (c.isVisible()) {
            if (this.lastHiddenMode && elemwidth[i].hideable) {
               c.setBounds(x, y, 0, 0);
            } else {
               Dimension d = c.getPreferredSize();
               if (i < 16 || !this.headingMode) {
                  d.width = elemwidth[i].width;
               }

               d.height = maxHeight;
               c.setBounds(x, y, d.width, d.height);
               x += d.width + elemwidth[i].PAD;
            }
         }
      }
   }

   public String toString() {
      return this.getClass().getName();
   }

   static class element {
      public int width = 0;
      public Color col = null;
      public int PAD = 2;
      public boolean hideable = false;

      element(int w) {
         this.width = w;
      }

      element(int w, int p) {
         this.width = w;
         this.PAD = p;
      }

      element(int w, int p, boolean hideable) {
         this.width = w;
         this.PAD = p;
         this.hideable = hideable;
      }

      element(int w, Color c) {
         this.width = w;
         this.col = c;
      }

      element(int w, Color c, boolean hideable) {
         this.width = w;
         this.col = c;
         this.hideable = hideable;
      }
   }
}
