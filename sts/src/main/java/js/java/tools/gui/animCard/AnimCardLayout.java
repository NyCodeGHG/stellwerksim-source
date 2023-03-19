package js.java.tools.gui.animCard;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.Vector;

public class AnimCardLayout extends CardLayout implements LayoutManager2 {
   Vector vector = new Vector();
   boolean initial = true;
   int currentCard = 0;
   int hgap;
   int vgap;

   public AnimCardLayout() {
      this(0, 0);
   }

   public AnimCardLayout(int hgap, int vgap) {
      super();
      this.hgap = hgap;
      this.vgap = vgap;
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

   public void addLayoutComponent(Component comp, Object constraints) {
      synchronized(comp.getTreeLock()) {
         if (constraints instanceof String) {
            this.addLayoutComponent((String)constraints, comp);
         } else {
            throw new IllegalArgumentException("cannot add to layout: constraint must be a string");
         }
      }
   }

   @Deprecated
   public void addLayoutComponent(String name, Component comp) {
      synchronized(comp.getTreeLock()) {
         if (!this.vector.isEmpty()) {
            comp.setVisible(false);
         }

         for(int i = 0; i < this.vector.size(); ++i) {
            if (((AnimCardLayout.Card)this.vector.get(i)).name.equals(name)) {
               ((AnimCardLayout.Card)this.vector.get(i)).comp = comp;
               return;
            }
         }

         this.vector.add(new AnimCardLayout.Card(name, comp));
      }
   }

   public void removeLayoutComponent(Component comp) {
      synchronized(comp.getTreeLock()) {
         for(int i = 0; i < this.vector.size(); ++i) {
            if (((AnimCardLayout.Card)this.vector.get(i)).comp == comp) {
               if (comp.isVisible() && comp.getParent() != null) {
                  this.next(comp.getParent());
               }

               this.vector.remove(i);
               if (this.currentCard > i) {
                  --this.currentCard;
               }
               break;
            }
         }
      }
   }

   public Dimension preferredLayoutSize(Container parent) {
      synchronized(parent.getTreeLock()) {
         Insets insets = parent.getInsets();
         int ncomponents = parent.getComponentCount();
         int w = 0;
         int h = 0;

         for(int i = 0; i < ncomponents; ++i) {
            Component comp = parent.getComponent(i);
            Dimension d = comp.getPreferredSize();
            if (d.width > w) {
               w = d.width;
            }

            if (d.height > h) {
               h = d.height;
            }
         }

         return new Dimension(insets.left + insets.right + w + this.hgap * 2, insets.top + insets.bottom + h + this.vgap * 2);
      }
   }

   public Dimension minimumLayoutSize(Container parent) {
      synchronized(parent.getTreeLock()) {
         Insets insets = parent.getInsets();
         int ncomponents = parent.getComponentCount();
         int w = 0;
         int h = 0;

         for(int i = 0; i < ncomponents; ++i) {
            Component comp = parent.getComponent(i);
            Dimension d = comp.getMinimumSize();
            if (d.width > w) {
               w = d.width;
            }

            if (d.height > h) {
               h = d.height;
            }
         }

         return new Dimension(insets.left + insets.right + w + this.hgap * 2, insets.top + insets.bottom + h + this.vgap * 2);
      }
   }

   public Dimension maximumLayoutSize(Container target) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   public float getLayoutAlignmentX(Container parent) {
      return 0.5F;
   }

   public float getLayoutAlignmentY(Container parent) {
      return 0.5F;
   }

   public void invalidateLayout(Container target) {
   }

   public void layoutContainer(Container parent) {
      synchronized(parent.getTreeLock()) {
         Insets insets = parent.getInsets();
         int ncomponents = parent.getComponentCount();
         Component comp = null;
         boolean currentFound = false;

         for(int i = 0; i < ncomponents; ++i) {
            comp = parent.getComponent(i);
            comp.setBounds(
               this.hgap + insets.left,
               this.vgap + insets.top,
               parent.getWidth() - (this.hgap * 2 + insets.left + insets.right),
               parent.getHeight() - (this.vgap * 2 + insets.top + insets.bottom)
            );
            if (comp.isVisible()) {
               currentFound = true;
            }
         }

         if (!currentFound && ncomponents > 0) {
            parent.getComponent(0).setVisible(true);
         }
      }
   }

   void checkLayout(Container parent) {
      if (parent.getLayout() != this) {
         throw new IllegalArgumentException("wrong parent for AnimCardLayout");
      }
   }

   public void first(Container parent) {
      synchronized(parent.getTreeLock()) {
         this.checkLayout(parent);
         int ncomponents = parent.getComponentCount();

         for(int i = 0; i < ncomponents; ++i) {
            Component comp = parent.getComponent(i);
            if (comp.isVisible()) {
               comp.setVisible(false);
               break;
            }
         }

         if (ncomponents > 0) {
            this.currentCard = 0;
            parent.getComponent(0).setVisible(true);
            parent.validate();
         }
      }
   }

   public void next(Container parent) {
      synchronized(parent.getTreeLock()) {
         this.checkLayout(parent);
         int ncomponents = parent.getComponentCount();

         for(int i = 0; i < ncomponents; ++i) {
            Component comp = parent.getComponent(i);
            if (comp.isVisible()) {
               comp.setVisible(false);
               this.currentCard = (i + 1) % ncomponents;
               comp = parent.getComponent(this.currentCard);
               comp.setVisible(true);
               parent.validate();
               return;
            }
         }

         this.showDefaultComponent(parent);
      }
   }

   public void previous(Container parent) {
      synchronized(parent.getTreeLock()) {
         this.checkLayout(parent);
         int ncomponents = parent.getComponentCount();

         for(int i = 0; i < ncomponents; ++i) {
            Component comp = parent.getComponent(i);
            if (comp.isVisible()) {
               comp.setVisible(false);
               this.currentCard = i > 0 ? i - 1 : ncomponents - 1;
               comp = parent.getComponent(this.currentCard);
               comp.setVisible(true);
               parent.validate();
               return;
            }
         }

         this.showDefaultComponent(parent);
      }
   }

   void showDefaultComponent(Container parent) {
      if (parent.getComponentCount() > 0) {
         this.currentCard = 0;
         parent.getComponent(0).setVisible(true);
         parent.validate();
      }
   }

   public void last(Container parent) {
      synchronized(parent.getTreeLock()) {
         this.checkLayout(parent);
         int ncomponents = parent.getComponentCount();

         for(int i = 0; i < ncomponents; ++i) {
            Component comp = parent.getComponent(i);
            if (comp.isVisible()) {
               comp.setVisible(false);
               break;
            }
         }

         if (ncomponents > 0) {
            this.currentCard = ncomponents - 1;
            parent.getComponent(this.currentCard).setVisible(true);
            parent.validate();
         }
      }
   }

   public void show(Container parent, String name) {
      synchronized(parent.getTreeLock()) {
         this.checkLayout(parent);
         Component next = null;
         int ncomponents = this.vector.size();

         for(int i = 0; i < ncomponents; ++i) {
            AnimCardLayout.Card card = (AnimCardLayout.Card)this.vector.get(i);
            if (card.name.equals(name)) {
               next = card.comp;
               this.currentCard = i;
               break;
            }
         }

         if (next != null && !next.isVisible()) {
            ncomponents = parent.getComponentCount();

            for(int i = 0; i < ncomponents; ++i) {
               Component comp = parent.getComponent(i);
               if (comp.isVisible()) {
                  if (!this.initial) {
                     next.setVisible(true);
                     new AnimComponent(comp, next);
                  }

                  this.initial = false;
                  comp.setVisible(false);
                  break;
               }
            }

            next.setVisible(true);
            parent.validate();
         }
      }
   }

   public String toString() {
      return this.getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + "]";
   }

   class Card {
      public String name;
      public Component comp;

      Card(String cardName, Component cardComponent) {
         super();
         this.name = cardName;
         this.comp = cardComponent;
      }
   }
}
