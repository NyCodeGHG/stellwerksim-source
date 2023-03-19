package js.java.tools.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

public class CompactLayout implements LayoutManager {
   private int minWidth = 0;
   private int minHeight = 0;
   private int preferredWidth = 0;
   private int preferredHeight = 0;
   private boolean needRecalc = true;
   private HashMap<Component, Rectangle> poslist = new HashMap();
   private int columns = 1;
   private ArrayList<Component> bestmatch = null;
   private int restsize = Integer.MAX_VALUE;
   private int dHeight;

   public CompactLayout(int c) {
      super();
      this.columns = c;
   }

   public void addLayoutComponent(String name, Component comp) {
      this.needRecalc = true;
   }

   public void removeLayoutComponent(Component comp) {
      this.needRecalc = true;
   }

   private void permutations(ArrayList<Component> list) {
      this.permutations(null, list);
   }

   private void permutations(ArrayList<Component> prefix, ArrayList<Component> suffix) {
      if (prefix == null) {
         prefix = new ArrayList();
      }

      if (suffix.size() == 1) {
         ArrayList<Component> newElement = new ArrayList(prefix);
         newElement.addAll(suffix);
         this.executor(newElement);
      } else {
         for(int i = 0; i < suffix.size(); ++i) {
            ArrayList<Component> newPrefix = new ArrayList(prefix);
            newPrefix.add(suffix.get(i));
            ArrayList<Component> newSuffix = new ArrayList(suffix);
            newSuffix.remove(i);
            this.permutations(newPrefix, newSuffix);
         }
      }
   }

   private void executor(ArrayList<Component> current) {
      int h = 0;
      int col = 0;

      for(Component cc : current) {
         Rectangle r = (Rectangle)this.poslist.get(cc);
         h += r.height;
         if (h >= this.dHeight) {
            h = 0;
            ++col;
         }
      }

      if (Math.abs(this.dHeight - h) < this.restsize) {
         this.restsize = Math.abs(this.dHeight - h);
         this.bestmatch = current;
      }
   }

   private void setPosAndSize(Container parent) {
      if (this.needRecalc) {
         this.poslist.clear();
         ArrayList<Component> plist = new ArrayList();
         Insets insets = parent.getInsets();
         int nComps = parent.getComponentCount();
         int x = 0;
         int y = insets.top;
         int totalHeight = 0;
         this.preferredWidth = this.preferredHeight = 0;
         this.minWidth = this.minHeight = 0;

         for(int i = 0; i < nComps; ++i) {
            Component c = parent.getComponent(i);
            Rectangle r = new Rectangle();
            r.setSize(c.getPreferredSize());
            this.poslist.put(c, r);
            plist.add(c);
            totalHeight += r.height;
            this.minWidth = Math.max(this.minWidth, c.getMinimumSize().width);
         }

         this.dHeight = totalHeight / this.columns;
         this.bestmatch = null;
         this.restsize = Integer.MAX_VALUE;
         this.permutations(plist);

         for(Component cc : this.bestmatch) {
            Rectangle r = (Rectangle)this.poslist.get(cc);
            r.x = x;
            r.y = y;
            y += r.height;
            this.minHeight = this.preferredHeight = Math.max(this.minHeight, y);
            if (y >= this.dHeight) {
               y = insets.top;
               ++x;
            }
         }

         this.minHeight = this.preferredHeight += insets.bottom;
         this.minWidth = this.minWidth * 2 + insets.left + insets.right;
         this.preferredWidth = this.minWidth;
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
      int startx = insets.left;
      int w = (parent.getWidth() - insets.left - insets.right) / this.columns;

      for(int i = 0; i < nComps; ++i) {
         Component c = parent.getComponent(i);
         Rectangle r = (Rectangle)this.poslist.get(c);
         c.setSize(w, r.height);
         c.setLocation(r.x * w + startx, r.y);
      }
   }
}
