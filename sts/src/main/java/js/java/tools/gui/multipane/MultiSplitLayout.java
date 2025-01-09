package js.java.tools.gui.multipane;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.UIManager;

public class MultiSplitLayout implements LayoutManager, Serializable {
   public static final int DEFAULT_LAYOUT = 0;
   public static final int NO_MIN_SIZE_LAYOUT = 1;
   public static final int USER_MIN_SIZE_LAYOUT = 2;
   private final Map<String, Component> childMap = new HashMap();
   private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
   private MultiSplitLayout.Node model;
   private int dividerSize;
   private boolean floatingDividers = true;
   private boolean removeDividers = true;
   private boolean layoutByWeight = false;
   private int layoutMode;
   private int userMinSize = 20;

   public MultiSplitLayout() {
      this(new MultiSplitLayout.Leaf("default"));
   }

   public MultiSplitLayout(boolean layoutByWeight) {
      this(new MultiSplitLayout.Leaf("default"));
      this.layoutByWeight = layoutByWeight;
   }

   public void layoutByWeight(Container parent) {
      this.doLayoutByWeight(parent);
      this.layoutContainer(parent);
   }

   private void doLayoutByWeight(Container parent) {
      Dimension size = parent.getSize();
      Insets insets = parent.getInsets();
      int width = size.width - (insets.left + insets.right);
      int height = size.height - (insets.top + insets.bottom);
      Rectangle bounds = new Rectangle(insets.left, insets.top, width, height);
      if (this.model instanceof MultiSplitLayout.Leaf) {
         this.model.setBounds(bounds);
      } else if (this.model instanceof MultiSplitLayout.Split) {
         this.doLayoutByWeight(this.model, bounds);
      }
   }

   private void doLayoutByWeight(MultiSplitLayout.Node node, Rectangle bounds) {
      int width = bounds.width;
      int height = bounds.height;
      MultiSplitLayout.Split split = (MultiSplitLayout.Split)node;
      List<MultiSplitLayout.Node> splitChildren = split.getChildren();
      double distributableWeight = 1.0;
      int unweightedComponents = 0;
      int dividerSpace = 0;

      for (MultiSplitLayout.Node splitChild : splitChildren) {
         if (splitChild.isVisible()) {
            if (splitChild instanceof MultiSplitLayout.Divider) {
               dividerSpace += this.dividerSize;
            } else {
               double weight = splitChild.getWeight();
               if (weight > 0.0) {
                  distributableWeight -= weight;
               } else {
                  unweightedComponents++;
               }
            }
         }
      }

      if (split.isRowLayout()) {
         width -= dividerSpace;
         double distributableWidth = (double)width * distributableWeight;

         for (MultiSplitLayout.Node splitChildx : splitChildren) {
            if (splitChildx.isVisible() && !(splitChildx instanceof MultiSplitLayout.Divider)) {
               double weight = splitChildx.getWeight();
               Rectangle splitChildBounds = splitChildx.getBounds();
               if (weight >= 0.0) {
                  splitChildBounds = new Rectangle(splitChildBounds.x, splitChildBounds.y, (int)((double)width * weight), height);
               } else {
                  splitChildBounds = new Rectangle(splitChildBounds.x, splitChildBounds.y, (int)(distributableWidth / (double)unweightedComponents), height);
               }

               if (this.layoutMode == 2) {
                  splitChildBounds.setSize(Math.max(splitChildBounds.width, this.userMinSize), splitChildBounds.height);
               }

               splitChildx.setBounds(splitChildBounds);
               if (splitChildx instanceof MultiSplitLayout.Split) {
                  this.doLayoutByWeight(splitChildx, splitChildBounds);
               } else {
                  Component comp = this.getComponentForNode(splitChildx);
                  if (comp != null) {
                     comp.setPreferredSize(splitChildBounds.getSize());
                  }
               }
            }
         }
      } else {
         height -= dividerSpace;
         double distributableHeight = (double)height * distributableWeight;

         for (MultiSplitLayout.Node splitChildxx : splitChildren) {
            if (splitChildxx.isVisible() && !(splitChildxx instanceof MultiSplitLayout.Divider)) {
               double weightx = splitChildxx.getWeight();
               Rectangle splitChildBoundsx = splitChildxx.getBounds();
               if (weightx >= 0.0) {
                  splitChildBoundsx = new Rectangle(splitChildBoundsx.x, splitChildBoundsx.y, width, (int)((double)height * weightx));
               } else {
                  splitChildBoundsx = new Rectangle(splitChildBoundsx.x, splitChildBoundsx.y, width, (int)(distributableHeight / (double)unweightedComponents));
               }

               if (this.layoutMode == 2) {
                  splitChildBoundsx.setSize(splitChildBoundsx.width, Math.max(splitChildBoundsx.height, this.userMinSize));
               }

               splitChildxx.setBounds(splitChildBoundsx);
               if (splitChildxx instanceof MultiSplitLayout.Split) {
                  this.doLayoutByWeight(splitChildxx, splitChildBoundsx);
               } else {
                  Component comp = this.getComponentForNode(splitChildxx);
                  if (comp != null) {
                     comp.setPreferredSize(splitChildBoundsx.getSize());
                  }
               }
            }
         }
      }
   }

   public Component getComponentForNode(MultiSplitLayout.Node n) {
      String name = ((MultiSplitLayout.Leaf)n).getName();
      return name != null ? (Component)this.childMap.get(name) : null;
   }

   public MultiSplitLayout.Node getNodeForComponent(Component comp) {
      return this.getNodeForName(this.getNameForComponent(comp));
   }

   public MultiSplitLayout.Node getNodeForName(String name) {
      if (this.model instanceof MultiSplitLayout.Split) {
         MultiSplitLayout.Split split = (MultiSplitLayout.Split)this.model;
         return this.getNodeForName(split, name);
      } else if (this.model instanceof MultiSplitLayout.Leaf) {
         return ((MultiSplitLayout.Leaf)this.model).getName().equals(name) ? this.model : null;
      } else {
         return null;
      }
   }

   public String getNameForComponent(Component child) {
      String name = null;

      for (Entry<String, Component> kv : this.childMap.entrySet()) {
         if (kv.getValue() == child) {
            name = (String)kv.getKey();
            break;
         }
      }

      return name;
   }

   public MultiSplitLayout.Node getNodeForComponent(MultiSplitLayout.Split split, Component comp) {
      return this.getNodeForName(split, this.getNameForComponent(comp));
   }

   public MultiSplitLayout.Node getNodeForName(MultiSplitLayout.Split split, String name) {
      for (MultiSplitLayout.Node n : split.getChildren()) {
         if (n instanceof MultiSplitLayout.Leaf) {
            if (((MultiSplitLayout.Leaf)n).getName().equals(name)) {
               return n;
            }
         } else if (n instanceof MultiSplitLayout.Split) {
            MultiSplitLayout.Node n1 = this.getNodeForName((MultiSplitLayout.Split)n, name);
            if (n1 != null) {
               return n1;
            }
         }
      }

      return null;
   }

   public boolean hasModel() {
      return this.model != null;
   }

   public MultiSplitLayout(MultiSplitLayout.Node model) {
      this.model = model;
      this.dividerSize = UIManager.getInt("SplitPane.dividerSize");
      if (this.dividerSize == 0) {
         this.dividerSize = 7;
      }
   }

   public void addPropertyChangeListener(PropertyChangeListener listener) {
      if (listener != null) {
         this.pcs.addPropertyChangeListener(listener);
      }
   }

   public void removePropertyChangeListener(PropertyChangeListener listener) {
      if (listener != null) {
         this.pcs.removePropertyChangeListener(listener);
      }
   }

   public PropertyChangeListener[] getPropertyChangeListeners() {
      return this.pcs.getPropertyChangeListeners();
   }

   private void firePCS(String propertyName, Object oldValue, Object newValue) {
      if (oldValue == null || newValue == null || !oldValue.equals(newValue)) {
         this.pcs.firePropertyChange(propertyName, oldValue, newValue);
      }
   }

   public MultiSplitLayout.Node getModel() {
      return this.model;
   }

   public void setModel(MultiSplitLayout.Node model) {
      if (model != null && !(model instanceof MultiSplitLayout.Divider)) {
         MultiSplitLayout.Node oldModel = this.getModel();
         this.model = model;
         this.firePCS("model", oldModel, this.getModel());
      } else {
         throw new IllegalArgumentException("invalid model");
      }
   }

   public int getDividerSize() {
      return this.dividerSize;
   }

   public void setDividerSize(int dividerSize) {
      if (dividerSize < 0) {
         throw new IllegalArgumentException("invalid dividerSize");
      } else {
         int oldDividerSize = this.dividerSize;
         this.dividerSize = dividerSize;
         this.firePCS("dividerSize", new Integer(oldDividerSize), new Integer(dividerSize));
      }
   }

   public boolean getFloatingDividers() {
      return this.floatingDividers;
   }

   public void setFloatingDividers(boolean floatingDividers) {
      boolean oldFloatingDividers = this.floatingDividers;
      this.floatingDividers = floatingDividers;
      this.firePCS("floatingDividers", new Boolean(oldFloatingDividers), new Boolean(floatingDividers));
   }

   public boolean getRemoveDividers() {
      return this.removeDividers;
   }

   public void setRemoveDividers(boolean removeDividers) {
      boolean oldRemoveDividers = this.removeDividers;
      this.removeDividers = removeDividers;
      this.firePCS("removeDividers", new Boolean(oldRemoveDividers), new Boolean(removeDividers));
   }

   public void addLayoutComponent(String name, Component child) {
      if (name == null) {
         throw new IllegalArgumentException("name not specified");
      } else {
         this.childMap.put(name, child);
      }
   }

   public void removeLayoutComponent(Component child) {
      String name = this.getNameForComponent(child);
      if (name != null) {
         this.childMap.remove(name);
      }
   }

   public void removeLayoutNode(String name) {
      if (name != null) {
         MultiSplitLayout.Node n;
         if (!(this.model instanceof MultiSplitLayout.Split)) {
            n = this.model;
         } else {
            n = this.getNodeForName(name);
         }

         this.childMap.remove(name);
         if (n != null) {
            MultiSplitLayout.Split s = n.getParent();
            s.remove(n);
            if (this.removeDividers) {
               while (s.getChildren().size() < 2) {
                  MultiSplitLayout.Split p = s.getParent();
                  if (p == null) {
                     if (s.getChildren().size() > 0) {
                        this.model = (MultiSplitLayout.Node)s.getChildren().get(0);
                     } else {
                        this.model = null;
                     }

                     return;
                  }

                  if (s.getChildren().size() == 1) {
                     MultiSplitLayout.Node next = (MultiSplitLayout.Node)s.getChildren().get(0);
                     p.replace(s, next);
                     next.setParent(p);
                  } else {
                     p.remove(s);
                  }

                  s = p;
               }
            }
         } else {
            this.childMap.remove(name);
         }
      }
   }

   public void displayNode(String name, boolean visible) {
      MultiSplitLayout.Node node = this.getNodeForName(name);
      if (node != null) {
         Component comp = this.getComponentForNode(node);
         comp.setVisible(visible);
         node.setVisible(visible);
         MultiSplitLayout.Split p = node.getParent();
         if (!visible) {
            p.hide(node);
            if (!p.isVisible()) {
               p.getParent().hide(p);
            }

            p.checkDividers(p);

            while (!p.isVisible()) {
               p = p.getParent();
               if (p == null) {
                  break;
               }

               p.checkDividers(p);
            }
         } else {
            p.restoreDividers(p);
         }
      }

      this.setFloatingDividers(false);
   }

   private Component childForNode(MultiSplitLayout.Node node) {
      if (node instanceof MultiSplitLayout.Leaf) {
         MultiSplitLayout.Leaf leaf = (MultiSplitLayout.Leaf)node;
         String name = leaf.getName();
         return name != null ? (Component)this.childMap.get(name) : null;
      } else {
         return null;
      }
   }

   private Dimension preferredComponentSize(MultiSplitLayout.Node node) {
      if (this.layoutMode == 1) {
         return new Dimension(0, 0);
      } else {
         Component child = this.childForNode(node);
         return child != null && child.isVisible() ? child.getPreferredSize() : new Dimension(0, 0);
      }
   }

   private Dimension minimumComponentSize(MultiSplitLayout.Node node) {
      if (this.layoutMode == 1) {
         return new Dimension(0, 0);
      } else {
         Component child = this.childForNode(node);
         return child != null && child.isVisible() ? child.getMinimumSize() : new Dimension(0, 0);
      }
   }

   private Dimension preferredNodeSize(MultiSplitLayout.Node root) {
      if (root instanceof MultiSplitLayout.Leaf) {
         return this.preferredComponentSize(root);
      } else if (root instanceof MultiSplitLayout.Divider) {
         if (!((MultiSplitLayout.Divider)root).isVisible()) {
            return new Dimension(0, 0);
         } else {
            int divSize = this.getDividerSize();
            return new Dimension(divSize, divSize);
         }
      } else {
         MultiSplitLayout.Split split = (MultiSplitLayout.Split)root;
         List<MultiSplitLayout.Node> splitChildren = split.getChildren();
         int width = 0;
         int height = 0;
         if (split.isRowLayout()) {
            for (MultiSplitLayout.Node splitChild : splitChildren) {
               if (splitChild.isVisible()) {
                  Dimension size = this.preferredNodeSize(splitChild);
                  width += size.width;
                  height = Math.max(height, size.height);
               }
            }
         } else {
            for (MultiSplitLayout.Node splitChildx : splitChildren) {
               if (splitChildx.isVisible()) {
                  Dimension size = this.preferredNodeSize(splitChildx);
                  width = Math.max(width, size.width);
                  height += size.height;
               }
            }
         }

         return new Dimension(width, height);
      }
   }

   public Dimension minimumNodeSize(MultiSplitLayout.Node root) {
      assert root.isVisible;

      if (root instanceof MultiSplitLayout.Leaf) {
         if (this.layoutMode == 1) {
            return new Dimension(0, 0);
         } else {
            Component child = this.childForNode(root);
            return child != null && child.isVisible() ? child.getMinimumSize() : new Dimension(0, 0);
         }
      } else if (root instanceof MultiSplitLayout.Divider) {
         if (!((MultiSplitLayout.Divider)root).isVisible()) {
            return new Dimension(0, 0);
         } else {
            int divSize = this.getDividerSize();
            return new Dimension(divSize, divSize);
         }
      } else {
         MultiSplitLayout.Split split = (MultiSplitLayout.Split)root;
         List<MultiSplitLayout.Node> splitChildren = split.getChildren();
         int width = 0;
         int height = 0;
         if (split.isRowLayout()) {
            for (MultiSplitLayout.Node splitChild : splitChildren) {
               if (splitChild.isVisible()) {
                  Dimension size = this.minimumNodeSize(splitChild);
                  width += size.width;
                  height = Math.max(height, size.height);
               }
            }
         } else {
            for (MultiSplitLayout.Node splitChildx : splitChildren) {
               if (splitChildx.isVisible()) {
                  Dimension size = this.minimumNodeSize(splitChildx);
                  width = Math.max(width, size.width);
                  height += size.height;
               }
            }
         }

         return new Dimension(width, height);
      }
   }

   public Dimension maximumNodeSize(MultiSplitLayout.Node root) {
      assert root.isVisible;

      if (root instanceof MultiSplitLayout.Leaf) {
         Component child = this.childForNode(root);
         return child != null && child.isVisible() ? child.getMaximumSize() : new Dimension(0, 0);
      } else if (root instanceof MultiSplitLayout.Divider) {
         if (!((MultiSplitLayout.Divider)root).isVisible()) {
            return new Dimension(0, 0);
         } else {
            int divSize = this.getDividerSize();
            return new Dimension(divSize, divSize);
         }
      } else {
         MultiSplitLayout.Split split = (MultiSplitLayout.Split)root;
         List<MultiSplitLayout.Node> splitChildren = split.getChildren();
         int width = Integer.MAX_VALUE;
         int height = Integer.MAX_VALUE;
         if (split.isRowLayout()) {
            for (MultiSplitLayout.Node splitChild : splitChildren) {
               if (splitChild.isVisible()) {
                  Dimension size = this.maximumNodeSize(splitChild);
                  width += size.width;
                  height = Math.min(height, size.height);
               }
            }
         } else {
            for (MultiSplitLayout.Node splitChildx : splitChildren) {
               if (splitChildx.isVisible()) {
                  Dimension size = this.maximumNodeSize(splitChildx);
                  width = Math.min(width, size.width);
                  height += size.height;
               }
            }
         }

         return new Dimension(width, height);
      }
   }

   private Dimension sizeWithInsets(Container parent, Dimension size) {
      Insets insets = parent.getInsets();
      int width = size.width + insets.left + insets.right;
      int height = size.height + insets.top + insets.bottom;
      return new Dimension(width, height);
   }

   public Dimension preferredLayoutSize(Container parent) {
      Dimension size = this.preferredNodeSize(this.getModel());
      return this.sizeWithInsets(parent, size);
   }

   public Dimension minimumLayoutSize(Container parent) {
      Dimension size = this.minimumNodeSize(this.getModel());
      return this.sizeWithInsets(parent, size);
   }

   private Rectangle boundsWithYandHeight(Rectangle bounds, double y, double height) {
      Rectangle r = new Rectangle();
      r.setBounds((int)bounds.getX(), (int)y, (int)bounds.getWidth(), (int)height);
      return r;
   }

   private Rectangle boundsWithXandWidth(Rectangle bounds, double x, double width) {
      Rectangle r = new Rectangle();
      r.setBounds((int)x, (int)bounds.getY(), (int)width, (int)bounds.getHeight());
      return r;
   }

   private void minimizeSplitBounds(MultiSplitLayout.Split split, Rectangle bounds) {
      assert split.isVisible();

      Rectangle splitBounds = new Rectangle(bounds.x, bounds.y, 0, 0);
      List<MultiSplitLayout.Node> splitChildren = split.getChildren();
      MultiSplitLayout.Node lastChild = null;
      int lastVisibleChildIdx = splitChildren.size();

      do {
         lastChild = (MultiSplitLayout.Node)splitChildren.get(--lastVisibleChildIdx);
      } while (lastVisibleChildIdx > 0 && !lastChild.isVisible());

      if (lastChild.isVisible()) {
         if (lastVisibleChildIdx >= 0) {
            Rectangle lastChildBounds = lastChild.getBounds();
            if (split.isRowLayout()) {
               int lastChildMaxX = lastChildBounds.x + lastChildBounds.width;
               splitBounds.add(lastChildMaxX, bounds.y + bounds.height);
            } else {
               int lastChildMaxY = lastChildBounds.y + lastChildBounds.height;
               splitBounds.add(bounds.x + bounds.width, lastChildMaxY);
            }
         }

         split.setBounds(splitBounds);
      }
   }

   private void layoutShrink(MultiSplitLayout.Split split, Rectangle bounds) {
      Rectangle splitBounds = split.getBounds();
      ListIterator<MultiSplitLayout.Node> splitChildren = split.getChildren().listIterator();
      MultiSplitLayout.Node lastWeightedChild = split.lastWeightedChild();
      if (split.isRowLayout()) {
         int totalWidth = 0;
         int minWeightedWidth = 0;
         int totalWeightedWidth = 0;

         for (MultiSplitLayout.Node splitChild : split.getChildren()) {
            if (splitChild.isVisible()) {
               int nodeWidth = splitChild.getBounds().width;
               int nodeMinWidth = 0;
               if (this.layoutMode == 2 && !(splitChild instanceof MultiSplitLayout.Divider)) {
                  nodeMinWidth = this.userMinSize;
               } else if (this.layoutMode == 0) {
                  nodeMinWidth = Math.min(nodeWidth, this.minimumNodeSize(splitChild).width);
               }

               totalWidth += nodeWidth;
               if (splitChild.getWeight() > 0.0) {
                  minWeightedWidth += nodeMinWidth;
                  totalWeightedWidth += nodeWidth;
               }
            }
         }

         double x = bounds.getX();
         double extraWidth = splitBounds.getWidth() - bounds.getWidth();
         double availableWidth = extraWidth;
         boolean onlyShrinkWeightedComponents = (double)(totalWeightedWidth - minWeightedWidth) > extraWidth;

         while (splitChildren.hasNext()) {
            MultiSplitLayout.Node splitChildx = (MultiSplitLayout.Node)splitChildren.next();
            if (!splitChildx.isVisible()) {
               if (splitChildren.hasNext()) {
                  splitChildren.next();
               }
            } else {
               Rectangle splitChildBounds = splitChildx.getBounds();
               double minSplitChildWidth = 0.0;
               if (this.layoutMode == 2 && !(splitChildx instanceof MultiSplitLayout.Divider)) {
                  minSplitChildWidth = (double)this.userMinSize;
               } else if (this.layoutMode == 0) {
                  minSplitChildWidth = this.minimumNodeSize(splitChildx).getWidth();
               }

               double splitChildWeight = onlyShrinkWeightedComponents ? splitChildx.getWeight() : splitChildBounds.getWidth() / (double)totalWidth;
               if (!splitChildren.hasNext()) {
                  double newWidth = Math.max(minSplitChildWidth, bounds.getMaxX() - x);
                  Rectangle newSplitChildBounds = this.boundsWithXandWidth(bounds, x, newWidth);
                  this.layout2(splitChildx, newSplitChildBounds);
               }

               if (splitChildx.isVisible()) {
                  if (availableWidth > 0.0 && splitChildWeight > 0.0) {
                     double oldWidth = splitChildBounds.getWidth();
                     double newWidth;
                     if (splitChildx instanceof MultiSplitLayout.Divider) {
                        newWidth = (double)this.dividerSize;
                     } else {
                        double allocatedWidth = Math.rint(splitChildWeight * extraWidth);
                        newWidth = Math.max(minSplitChildWidth, oldWidth - allocatedWidth);
                     }

                     Rectangle newSplitChildBounds = this.boundsWithXandWidth(bounds, x, newWidth);
                     this.layout2(splitChildx, newSplitChildBounds);
                     availableWidth -= oldWidth - splitChildx.getBounds().getWidth();
                  } else {
                     double existingWidth = splitChildBounds.getWidth();
                     Rectangle newSplitChildBounds = this.boundsWithXandWidth(bounds, x, existingWidth);
                     this.layout2(splitChildx, newSplitChildBounds);
                  }

                  x = splitChildx.getBounds().getMaxX();
               }
            }
         }
      } else {
         int totalHeight = 0;
         int minWeightedHeight = 0;
         int totalWeightedHeight = 0;

         for (MultiSplitLayout.Node splitChildx : split.getChildren()) {
            if (splitChildx.isVisible()) {
               int nodeHeight = splitChildx.getBounds().height;
               int nodeMinHeight = 0;
               if (this.layoutMode == 2 && !(splitChildx instanceof MultiSplitLayout.Divider)) {
                  nodeMinHeight = this.userMinSize;
               } else if (this.layoutMode == 0) {
                  nodeMinHeight = Math.min(nodeHeight, this.minimumNodeSize(splitChildx).height);
               }

               totalHeight += nodeHeight;
               if (splitChildx.getWeight() > 0.0) {
                  minWeightedHeight += nodeMinHeight;
                  totalWeightedHeight += nodeHeight;
               }
            }
         }

         double y = bounds.getY();
         double extraHeight = splitBounds.getHeight() - bounds.getHeight();
         double availableHeight = extraHeight;
         boolean onlyShrinkWeightedComponents = (double)(totalWeightedHeight - minWeightedHeight) > extraHeight;

         while (splitChildren.hasNext()) {
            MultiSplitLayout.Node splitChildxx = (MultiSplitLayout.Node)splitChildren.next();
            if (!splitChildxx.isVisible()) {
               if (splitChildren.hasNext()) {
                  splitChildren.next();
               }
            } else {
               Rectangle splitChildBoundsx = splitChildxx.getBounds();
               double minSplitChildHeight = 0.0;
               if (this.layoutMode == 2 && !(splitChildxx instanceof MultiSplitLayout.Divider)) {
                  minSplitChildHeight = (double)this.userMinSize;
               } else if (this.layoutMode == 0) {
                  minSplitChildHeight = this.minimumNodeSize(splitChildxx).getHeight();
               }

               double splitChildWeightx = onlyShrinkWeightedComponents ? splitChildxx.getWeight() : splitChildBoundsx.getHeight() / (double)totalHeight;
               if (!this.hasMoreVisibleSiblings(splitChildxx)) {
                  double oldHeight = splitChildBoundsx.getHeight();
                  double newHeight;
                  if (splitChildxx instanceof MultiSplitLayout.Divider) {
                     newHeight = (double)this.dividerSize;
                  } else {
                     newHeight = Math.max(minSplitChildHeight, bounds.getMaxY() - y);
                  }

                  Rectangle newSplitChildBounds = this.boundsWithYandHeight(bounds, y, newHeight);
                  this.layout2(splitChildxx, newSplitChildBounds);
                  availableHeight -= oldHeight - splitChildxx.getBounds().getHeight();
               } else if (availableHeight > 0.0 && splitChildWeightx > 0.0) {
                  double oldHeight = splitChildBoundsx.getHeight();
                  double newHeight;
                  if (splitChildxx instanceof MultiSplitLayout.Divider) {
                     newHeight = (double)this.dividerSize;
                  } else {
                     double allocatedHeight = Math.rint(splitChildWeightx * extraHeight);
                     newHeight = Math.max(minSplitChildHeight, oldHeight - allocatedHeight);
                  }

                  Rectangle newSplitChildBounds = this.boundsWithYandHeight(bounds, y, newHeight);
                  this.layout2(splitChildxx, newSplitChildBounds);
                  availableHeight -= oldHeight - splitChildxx.getBounds().getHeight();
               } else {
                  double existingHeight = splitChildBoundsx.getHeight();
                  Rectangle newSplitChildBounds = this.boundsWithYandHeight(bounds, y, existingHeight);
                  this.layout2(splitChildxx, newSplitChildBounds);
               }

               y = splitChildxx.getBounds().getMaxY();
            }
         }
      }

      this.minimizeSplitBounds(split, bounds);
   }

   private boolean hasMoreVisibleSiblings(MultiSplitLayout.Node splitChild) {
      MultiSplitLayout.Node next = splitChild.nextSibling();
      if (next == null) {
         return false;
      } else {
         while (!next.isVisible()) {
            next = next.nextSibling();
            if (next == null) {
               return false;
            }
         }

         return true;
      }
   }

   private void layoutGrow(MultiSplitLayout.Split split, Rectangle bounds) {
      Rectangle splitBounds = split.getBounds();
      ListIterator<MultiSplitLayout.Node> splitChildren = split.getChildren().listIterator();
      MultiSplitLayout.Node lastWeightedChild = split.lastWeightedChild();
      if (split.isRowLayout()) {
         double x = bounds.getX();
         double extraWidth = bounds.getWidth() - splitBounds.getWidth();
         double availableWidth = extraWidth;

         while (splitChildren.hasNext()) {
            MultiSplitLayout.Node splitChild = (MultiSplitLayout.Node)splitChildren.next();
            if (splitChild.isVisible()) {
               Rectangle splitChildBounds = splitChild.getBounds();
               double splitChildWeight = splitChild.getWeight();
               if (!this.hasMoreVisibleSiblings(splitChild)) {
                  double newWidth = bounds.getMaxX() - x;
                  Rectangle newSplitChildBounds = this.boundsWithXandWidth(bounds, x, newWidth);
                  this.layout2(splitChild, newSplitChildBounds);
               } else if (availableWidth > 0.0 && splitChildWeight > 0.0) {
                  double allocatedWidth = splitChild.equals(lastWeightedChild) ? availableWidth : Math.rint(splitChildWeight * extraWidth);
                  double newWidth = splitChildBounds.getWidth() + allocatedWidth;
                  Rectangle newSplitChildBounds = this.boundsWithXandWidth(bounds, x, newWidth);
                  this.layout2(splitChild, newSplitChildBounds);
                  availableWidth -= allocatedWidth;
               } else {
                  double existingWidth = splitChildBounds.getWidth();
                  Rectangle newSplitChildBounds = this.boundsWithXandWidth(bounds, x, existingWidth);
                  this.layout2(splitChild, newSplitChildBounds);
               }

               x = splitChild.getBounds().getMaxX();
            }
         }
      } else {
         double y = bounds.getY();
         double extraHeight = bounds.getHeight() - splitBounds.getHeight();
         double availableHeight = extraHeight;

         while (splitChildren.hasNext()) {
            MultiSplitLayout.Node splitChild = (MultiSplitLayout.Node)splitChildren.next();
            if (splitChild.isVisible()) {
               Rectangle splitChildBounds = splitChild.getBounds();
               double splitChildWeight = splitChild.getWeight();
               if (!splitChildren.hasNext()) {
                  double newHeight = bounds.getMaxY() - y;
                  Rectangle newSplitChildBounds = this.boundsWithYandHeight(bounds, y, newHeight);
                  this.layout2(splitChild, newSplitChildBounds);
               } else if (availableHeight > 0.0 && splitChildWeight > 0.0) {
                  double allocatedHeight = splitChild.equals(lastWeightedChild) ? availableHeight : Math.rint(splitChildWeight * extraHeight);
                  double newHeight = splitChildBounds.getHeight() + allocatedHeight;
                  Rectangle newSplitChildBounds = this.boundsWithYandHeight(bounds, y, newHeight);
                  this.layout2(splitChild, newSplitChildBounds);
                  availableHeight -= allocatedHeight;
               } else {
                  double existingHeight = splitChildBounds.getHeight();
                  Rectangle newSplitChildBounds = this.boundsWithYandHeight(bounds, y, existingHeight);
                  this.layout2(splitChild, newSplitChildBounds);
               }

               y = splitChild.getBounds().getMaxY();
            }
         }
      }
   }

   private void layout2(MultiSplitLayout.Node root, Rectangle bounds) {
      if (root instanceof MultiSplitLayout.Leaf) {
         Component child = this.childForNode(root);
         if (child != null) {
            child.setBounds(bounds);
         }

         root.setBounds(bounds);
      } else if (root instanceof MultiSplitLayout.Divider) {
         root.setBounds(bounds);
      } else if (root instanceof MultiSplitLayout.Split) {
         MultiSplitLayout.Split split = (MultiSplitLayout.Split)root;
         boolean grow = split.isRowLayout() ? split.getBounds().width <= bounds.width : split.getBounds().height <= bounds.height;
         if (grow) {
            this.layoutGrow(split, bounds);
            root.setBounds(bounds);
         } else {
            this.layoutShrink(split, bounds);
         }
      }
   }

   private void layout1(MultiSplitLayout.Node root, Rectangle bounds) {
      if (root instanceof MultiSplitLayout.Leaf) {
         root.setBounds(bounds);
      } else if (root instanceof MultiSplitLayout.Split) {
         MultiSplitLayout.Split split = (MultiSplitLayout.Split)root;
         Iterator<MultiSplitLayout.Node> splitChildren = split.getChildren().iterator();
         Rectangle childBounds = null;
         int divSize = this.getDividerSize();
         boolean initSplit = false;
         if (split.isRowLayout()) {
            double x = bounds.getX();

            while (splitChildren.hasNext()) {
               MultiSplitLayout.Node splitChild = (MultiSplitLayout.Node)splitChildren.next();
               if (!splitChild.isVisible()) {
                  if (splitChildren.hasNext()) {
                     splitChildren.next();
                  }
               } else {
                  MultiSplitLayout.Divider dividerChild = splitChildren.hasNext() ? (MultiSplitLayout.Divider)splitChildren.next() : null;
                  double childWidth = 0.0;
                  if (this.getFloatingDividers()) {
                     childWidth = this.preferredNodeSize(splitChild).getWidth();
                  } else if (dividerChild != null && dividerChild.isVisible()) {
                     double cw = dividerChild.getBounds().getX() - x;
                     if (cw > 0.0) {
                        childWidth = cw;
                     } else {
                        childWidth = this.preferredNodeSize(splitChild).getWidth();
                        initSplit = true;
                     }
                  } else {
                     childWidth = split.getBounds().getMaxX() - x;
                  }

                  childBounds = this.boundsWithXandWidth(bounds, x, childWidth);
                  this.layout1(splitChild, childBounds);
                  if ((initSplit || this.getFloatingDividers()) && dividerChild != null && dividerChild.isVisible()) {
                     double dividerX = childBounds.getMaxX();
                     Rectangle dividerBounds = this.boundsWithXandWidth(bounds, dividerX, (double)divSize);
                     dividerChild.setBounds(dividerBounds);
                  }

                  if (dividerChild != null && dividerChild.isVisible()) {
                     x = dividerChild.getBounds().getMaxX();
                  }
               }
            }
         } else {
            double y = bounds.getY();

            while (splitChildren.hasNext()) {
               MultiSplitLayout.Node splitChild = (MultiSplitLayout.Node)splitChildren.next();
               if (!splitChild.isVisible()) {
                  if (splitChildren.hasNext()) {
                     splitChildren.next();
                  }
               } else {
                  MultiSplitLayout.Divider dividerChildx = splitChildren.hasNext() ? (MultiSplitLayout.Divider)splitChildren.next() : null;
                  double childHeight = 0.0;
                  if (this.getFloatingDividers()) {
                     childHeight = this.preferredNodeSize(splitChild).getHeight();
                  } else if (dividerChildx != null && dividerChildx.isVisible()) {
                     double cy = dividerChildx.getBounds().getY() - y;
                     if (cy > 0.0) {
                        childHeight = cy;
                     } else {
                        childHeight = this.preferredNodeSize(splitChild).getHeight();
                        initSplit = true;
                     }
                  } else {
                     childHeight = split.getBounds().getMaxY() - y;
                  }

                  childBounds = this.boundsWithYandHeight(bounds, y, childHeight);
                  this.layout1(splitChild, childBounds);
                  if ((initSplit || this.getFloatingDividers()) && dividerChildx != null && dividerChildx.isVisible()) {
                     double dividerY = childBounds.getMaxY();
                     Rectangle dividerBounds = this.boundsWithYandHeight(bounds, dividerY, (double)divSize);
                     dividerChildx.setBounds(dividerBounds);
                  }

                  if (dividerChildx != null && dividerChildx.isVisible()) {
                     y = dividerChildx.getBounds().getMaxY();
                  }
               }
            }
         }

         this.minimizeSplitBounds(split, bounds);
      }
   }

   public int getLayoutMode() {
      return this.layoutMode;
   }

   public void setLayoutMode(int layoutMode) {
      this.layoutMode = layoutMode;
   }

   public int getUserMinSize() {
      return this.userMinSize;
   }

   public void setUserMinSize(int minSize) {
      this.userMinSize = minSize;
   }

   public boolean getLayoutByWeight() {
      return this.layoutByWeight;
   }

   public void setLayoutByWeight(boolean state) {
      this.layoutByWeight = state;
   }

   private void throwInvalidLayout(String msg, MultiSplitLayout.Node node) {
      throw new MultiSplitLayout.InvalidLayoutException(msg, node);
   }

   private void checkLayout(MultiSplitLayout.Node root) {
      if (root instanceof MultiSplitLayout.Split) {
         MultiSplitLayout.Split split = (MultiSplitLayout.Split)root;
         if (split.getChildren().size() <= 2) {
            this.throwInvalidLayout("Split must have > 2 children", root);
         }

         Iterator<MultiSplitLayout.Node> splitChildren = split.getChildren().iterator();
         double weight = 0.0;

         while (splitChildren.hasNext()) {
            MultiSplitLayout.Node splitChild = (MultiSplitLayout.Node)splitChildren.next();
            if (!splitChild.isVisible()) {
               if (splitChildren.hasNext()) {
                  splitChildren.next();
               }
            } else if (!(splitChild instanceof MultiSplitLayout.Divider)) {
               if (splitChildren.hasNext()) {
                  MultiSplitLayout.Node dividerChild = (MultiSplitLayout.Node)splitChildren.next();
                  if (!(dividerChild instanceof MultiSplitLayout.Divider)) {
                     this.throwInvalidLayout("expected a Divider Node", dividerChild);
                  }
               }

               weight += splitChild.getWeight();
               this.checkLayout(splitChild);
            }
         }

         if (weight > 1.0) {
            this.throwInvalidLayout("Split children's total weight > 1.0", root);
         }
      }
   }

   public void layoutContainer(Container parent) {
      if (this.layoutByWeight && this.floatingDividers) {
         this.doLayoutByWeight(parent);
      }

      this.checkLayout(this.getModel());
      Insets insets = parent.getInsets();
      Dimension size = parent.getSize();
      int width = size.width - (insets.left + insets.right);
      int height = size.height - (insets.top + insets.bottom);
      Rectangle bounds = new Rectangle(insets.left, insets.top, width, height);
      this.layout1(this.getModel(), bounds);
      this.layout2(this.getModel(), bounds);
   }

   private MultiSplitLayout.Divider dividerAt(MultiSplitLayout.Node root, int x, int y) {
      if (root instanceof MultiSplitLayout.Divider) {
         MultiSplitLayout.Divider divider = (MultiSplitLayout.Divider)root;
         return divider.getBounds().contains(x, y) ? divider : null;
      } else {
         if (root instanceof MultiSplitLayout.Split) {
            MultiSplitLayout.Split split = (MultiSplitLayout.Split)root;

            for (MultiSplitLayout.Node child : split.getChildren()) {
               if (child.isVisible() && child.getBounds().contains(x, y)) {
                  return this.dividerAt(child, x, y);
               }
            }
         }

         return null;
      }
   }

   public MultiSplitLayout.Divider dividerAt(int x, int y) {
      return this.dividerAt(this.getModel(), x, y);
   }

   private boolean nodeOverlapsRectangle(MultiSplitLayout.Node node, Rectangle r2) {
      Rectangle r1 = node.getBounds();
      return r1.x <= r2.x + r2.width && r1.x + r1.width >= r2.x && r1.y <= r2.y + r2.height && r1.y + r1.height >= r2.y;
   }

   private List<MultiSplitLayout.Divider> dividersThatOverlap(MultiSplitLayout.Node root, Rectangle r) {
      if (this.nodeOverlapsRectangle(root, r) && root instanceof MultiSplitLayout.Split) {
         List<MultiSplitLayout.Divider> dividers = new ArrayList();

         for (MultiSplitLayout.Node child : ((MultiSplitLayout.Split)root).getChildren()) {
            if (child instanceof MultiSplitLayout.Divider) {
               if (this.nodeOverlapsRectangle(child, r)) {
                  dividers.add((MultiSplitLayout.Divider)child);
               }
            } else if (child instanceof MultiSplitLayout.Split) {
               dividers.addAll(this.dividersThatOverlap(child, r));
            }
         }

         return dividers;
      } else {
         return Collections.emptyList();
      }
   }

   public List<MultiSplitLayout.Divider> dividersThatOverlap(Rectangle r) {
      if (r == null) {
         throw new IllegalArgumentException("null Rectangle");
      } else {
         return this.dividersThatOverlap(this.getModel(), r);
      }
   }

   private static void throwParseException(StreamTokenizer st, String msg) throws Exception {
      throw new Exception("MultiSplitLayout.parseModel Error: " + msg);
   }

   private static void parseAttribute(String name, StreamTokenizer st, MultiSplitLayout.Node node) throws Exception {
      if (st.nextToken() != 61) {
         throwParseException(st, "expected '=' after " + name);
      }

      if (name.equalsIgnoreCase("WEIGHT")) {
         if (st.nextToken() == -2) {
            node.setWeight(st.nval);
         } else {
            throwParseException(st, "invalid weight");
         }
      } else if (name.equalsIgnoreCase("NAME")) {
         if (st.nextToken() == -3) {
            if (node instanceof MultiSplitLayout.Leaf) {
               ((MultiSplitLayout.Leaf)node).setName(st.sval);
            } else if (node instanceof MultiSplitLayout.Split) {
               ((MultiSplitLayout.Split)node).setName(st.sval);
            } else {
               throwParseException(st, "can't specify name for " + node);
            }
         } else {
            throwParseException(st, "invalid name");
         }
      } else {
         throwParseException(st, "unrecognized attribute \"" + name + "\"");
      }
   }

   private static void addSplitChild(MultiSplitLayout.Split parent, MultiSplitLayout.Node child) {
      List<MultiSplitLayout.Node> children = new ArrayList(parent.getChildren());
      if (children.isEmpty()) {
         children.add(child);
      } else {
         children.add(new MultiSplitLayout.Divider());
         children.add(child);
      }

      parent.setChildren(children);
   }

   private static void parseLeaf(StreamTokenizer st, MultiSplitLayout.Split parent) throws Exception {
      MultiSplitLayout.Leaf leaf = new MultiSplitLayout.Leaf();

      int token;
      while ((token = st.nextToken()) != -1 && token != 41) {
         if (token == -3) {
            parseAttribute(st.sval, st, leaf);
         } else {
            throwParseException(st, "Bad Leaf: " + leaf);
         }
      }

      addSplitChild(parent, leaf);
   }

   private static void parseSplit(StreamTokenizer st, MultiSplitLayout.Split parent) throws Exception {
      int token;
      while ((token = st.nextToken()) != -1 && token != 41) {
         if (token == -3) {
            if (st.sval.equalsIgnoreCase("WEIGHT")) {
               parseAttribute(st.sval, st, parent);
            } else if (st.sval.equalsIgnoreCase("NAME")) {
               parseAttribute(st.sval, st, parent);
            } else {
               addSplitChild(parent, new MultiSplitLayout.Leaf(st.sval));
            }
         } else if (token == 40) {
            if (st.nextToken() != -3) {
               throwParseException(st, "invalid node type");
            }

            String nodeType = st.sval.toUpperCase();
            if (nodeType.equals("LEAF")) {
               parseLeaf(st, parent);
            } else if (!nodeType.equals("ROW") && !nodeType.equals("COLUMN")) {
               throwParseException(st, "unrecognized node type '" + nodeType + "'");
            } else {
               MultiSplitLayout.Split split = new MultiSplitLayout.Split();
               split.setRowLayout(nodeType.equals("ROW"));
               addSplitChild(parent, split);
               parseSplit(st, split);
            }
         }
      }
   }

   private static MultiSplitLayout.Node parseModel(Reader r) {
      StreamTokenizer st = new StreamTokenizer(r);

      try {
         MultiSplitLayout.Split root = new MultiSplitLayout.Split();
         parseSplit(st, root);
         return (MultiSplitLayout.Node)root.getChildren().get(0);
      } catch (Exception var13) {
         System.err.println(var13);
      } finally {
         try {
            r.close();
         } catch (IOException var12) {
         }
      }

      return null;
   }

   public static MultiSplitLayout.Node parseModel(String s) {
      return parseModel(new StringReader(s));
   }

   private static void printModel(String indent, MultiSplitLayout.Node root) {
      if (root instanceof MultiSplitLayout.Split) {
         MultiSplitLayout.Split split = (MultiSplitLayout.Split)root;
         System.out.println(indent + split);

         for (MultiSplitLayout.Node child : split.getChildren()) {
            printModel(indent + "  ", child);
         }
      } else {
         System.out.println(indent + root);
      }
   }

   public static void printModel(MultiSplitLayout.Node root) {
      printModel("", root);
   }

   public static class ColSplit extends MultiSplitLayout.Split {
      public ColSplit() {
      }

      public ColSplit(MultiSplitLayout.Node... children) {
         this.setChildren(children);
      }

      @Override
      public final boolean isRowLayout() {
         return false;
      }
   }

   public static class Divider extends MultiSplitLayout.Node {
      public final boolean isVertical() {
         MultiSplitLayout.Split parent = this.getParent();
         return parent != null ? parent.isRowLayout() : false;
      }

      @Override
      public void setWeight(double weight) {
         throw new UnsupportedOperationException();
      }

      public String toString() {
         return "MultiSplitLayout.Divider " + this.getBounds().toString();
      }
   }

   public static class InvalidLayoutException extends RuntimeException {
      private final MultiSplitLayout.Node node;

      public InvalidLayoutException(String msg, MultiSplitLayout.Node node) {
         super(msg);
         this.node = node;
      }

      public MultiSplitLayout.Node getNode() {
         return this.node;
      }
   }

   public static class Leaf extends MultiSplitLayout.Node {
      private String name = "";

      public Leaf() {
      }

      public Leaf(String name) {
         if (name == null) {
            throw new IllegalArgumentException("name is null");
         } else {
            this.name = name;
         }
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         if (name == null) {
            throw new IllegalArgumentException("name is null");
         } else {
            this.name = name;
         }
      }

      public String toString() {
         StringBuilder sb = new StringBuilder("MultiSplitLayout.Leaf");
         sb.append(" \"");
         sb.append(this.getName());
         sb.append("\"");
         sb.append(" weight=");
         sb.append(this.getWeight());
         sb.append(" ");
         sb.append(this.getBounds());
         return sb.toString();
      }
   }

   public abstract static class Node implements Serializable {
      private MultiSplitLayout.Split parent = null;
      private Rectangle bounds = new Rectangle();
      private double weight = 0.0;
      private boolean isVisible = true;

      public void setVisible(boolean b) {
         this.isVisible = b;
      }

      public boolean isVisible() {
         return this.isVisible;
      }

      public MultiSplitLayout.Split getParent() {
         return this.parent;
      }

      public void setParent(MultiSplitLayout.Split parent) {
         this.parent = parent;
      }

      public Rectangle getBounds() {
         return new Rectangle(this.bounds);
      }

      public void setBounds(Rectangle bounds) {
         if (bounds == null) {
            throw new IllegalArgumentException("null bounds");
         } else {
            this.bounds = new Rectangle(bounds);
         }
      }

      public double getWeight() {
         return this.weight;
      }

      public void setWeight(double weight) {
         if (!(weight < 0.0) && !(weight > 1.0)) {
            this.weight = weight;
         } else {
            throw new IllegalArgumentException("invalid weight");
         }
      }

      private MultiSplitLayout.Node siblingAtOffset(int offset) {
         MultiSplitLayout.Split p = this.getParent();
         if (p == null) {
            return null;
         } else {
            List<MultiSplitLayout.Node> siblings = p.getChildren();
            int index = siblings.indexOf(this);
            if (index == -1) {
               return null;
            } else {
               index += offset;
               return index > -1 && index < siblings.size() ? (MultiSplitLayout.Node)siblings.get(index) : null;
            }
         }
      }

      public MultiSplitLayout.Node nextSibling() {
         return this.siblingAtOffset(1);
      }

      public MultiSplitLayout.Node previousSibling() {
         return this.siblingAtOffset(-1);
      }
   }

   public static class RowSplit extends MultiSplitLayout.Split {
      public RowSplit() {
      }

      public RowSplit(MultiSplitLayout.Node... children) {
         this.setChildren(children);
      }

      @Override
      public final boolean isRowLayout() {
         return true;
      }
   }

   public static class Split extends MultiSplitLayout.Node {
      private List<MultiSplitLayout.Node> children = Collections.emptyList();
      private boolean rowLayout = true;
      private String name;

      public Split(MultiSplitLayout.Node... children) {
         this.setChildren(children);
      }

      public Split() {
      }

      @Override
      public boolean isVisible() {
         for (MultiSplitLayout.Node child : this.children) {
            if (child.isVisible() && !(child instanceof MultiSplitLayout.Divider)) {
               return true;
            }
         }

         return false;
      }

      public boolean isRowLayout() {
         return this.rowLayout;
      }

      public void setRowLayout(boolean rowLayout) {
         this.rowLayout = rowLayout;
      }

      public List<MultiSplitLayout.Node> getChildren() {
         return new ArrayList(this.children);
      }

      public void remove(MultiSplitLayout.Node n) {
         if (n.nextSibling() instanceof MultiSplitLayout.Divider) {
            this.children.remove(n.nextSibling());
         } else if (n.previousSibling() instanceof MultiSplitLayout.Divider) {
            this.children.remove(n.previousSibling());
         }

         this.children.remove(n);
      }

      public void replace(MultiSplitLayout.Node target, MultiSplitLayout.Node replacement) {
         int idx = this.children.indexOf(target);
         this.children.remove(target);
         this.children.add(idx, replacement);
         replacement.setParent(this);
         target.setParent(this);
      }

      public void hide(MultiSplitLayout.Node target) {
         MultiSplitLayout.Node next = target.nextSibling();
         if (next instanceof MultiSplitLayout.Divider) {
            next.setVisible(false);
         } else {
            MultiSplitLayout.Node prev = target.previousSibling();
            if (prev instanceof MultiSplitLayout.Divider) {
               prev.setVisible(false);
            }
         }

         target.setVisible(false);
      }

      public void checkDividers(MultiSplitLayout.Split split) {
         ListIterator<MultiSplitLayout.Node> splitChildren = split.getChildren().listIterator();

         while (splitChildren.hasNext()) {
            MultiSplitLayout.Node splitChild = (MultiSplitLayout.Node)splitChildren.next();
            if (splitChild.isVisible() && splitChildren.hasNext()) {
               MultiSplitLayout.Node dividerChild = (MultiSplitLayout.Node)splitChildren.next();
               if (dividerChild instanceof MultiSplitLayout.Divider) {
                  if (splitChildren.hasNext()) {
                     MultiSplitLayout.Node rightChild = (MultiSplitLayout.Node)splitChildren.next();

                     while (!rightChild.isVisible()) {
                        rightChild = rightChild.nextSibling();
                        if (rightChild == null) {
                           dividerChild.setVisible(false);
                           break;
                        }
                     }

                     if (rightChild != null && rightChild instanceof MultiSplitLayout.Divider) {
                        dividerChild.setVisible(false);
                     }
                  }
               } else if (splitChild instanceof MultiSplitLayout.Divider && dividerChild instanceof MultiSplitLayout.Divider) {
                  splitChild.setVisible(false);
               }
            }
         }
      }

      public void restoreDividers(MultiSplitLayout.Split split) {
         boolean nextDividerVisible = false;
         ListIterator<MultiSplitLayout.Node> splitChildren = split.getChildren().listIterator();

         while (splitChildren.hasNext()) {
            MultiSplitLayout.Node splitChild = (MultiSplitLayout.Node)splitChildren.next();
            if (splitChild instanceof MultiSplitLayout.Divider) {
               MultiSplitLayout.Node prev = splitChild.previousSibling();
               if (prev.isVisible()) {
                  for (MultiSplitLayout.Node next = splitChild.nextSibling(); next != null; next = next.nextSibling()) {
                     if (next.isVisible()) {
                        splitChild.setVisible(true);
                        break;
                     }
                  }
               }
            }
         }

         if (split.getParent() != null) {
            this.restoreDividers(split.getParent());
         }
      }

      public void setChildren(List<MultiSplitLayout.Node> children) {
         if (children == null) {
            throw new IllegalArgumentException("children must be a non-null List");
         } else {
            for (MultiSplitLayout.Node child : this.children) {
               child.setParent(null);
            }

            this.children = new ArrayList(children);

            for (MultiSplitLayout.Node child : this.children) {
               child.setParent(this);
            }
         }
      }

      public void setChildren(MultiSplitLayout.Node... children) {
         this.setChildren(children == null ? null : Arrays.asList(children));
      }

      public final MultiSplitLayout.Node lastWeightedChild() {
         List<MultiSplitLayout.Node> kids = this.getChildren();
         MultiSplitLayout.Node weightedChild = null;

         for (MultiSplitLayout.Node child : kids) {
            if (child.isVisible() && child.getWeight() > 0.0) {
               weightedChild = child;
            }
         }

         return weightedChild;
      }

      public String getName() {
         return this.name;
      }

      public void setName(String name) {
         if (name == null) {
            throw new IllegalArgumentException("name is null");
         } else {
            this.name = name;
         }
      }

      public String toString() {
         int nChildren = this.getChildren().size();
         StringBuilder sb = new StringBuilder("MultiSplitLayout.Split");
         sb.append(" \"");
         sb.append(this.getName());
         sb.append("\"");
         sb.append(this.isRowLayout() ? " ROW [" : " COLUMN [");
         sb.append(nChildren + (nChildren == 1 ? " child" : " children"));
         sb.append("] ");
         sb.append(this.getBounds());
         return sb.toString();
      }
   }
}
