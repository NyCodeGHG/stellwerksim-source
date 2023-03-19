package js.java.tools.gui.tree;

import java.util.Collections;
import java.util.Comparator;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.MutableTreeNode;

public class SortedTreeNode extends ChangableTreeNode {
   protected Comparator comparator;

   public SortedTreeNode() {
      this(null);
   }

   public SortedTreeNode(Object userObject) {
      this(userObject, null);
   }

   public SortedTreeNode(Object userObject, boolean allowsChildren) {
      this(userObject, allowsChildren, null);
   }

   public SortedTreeNode(Object userObject, Comparator newComparator) {
      this(userObject, true, newComparator);
   }

   public SortedTreeNode(Object userObject, boolean allowsChildren, Comparator newComparator) {
      super(userObject, allowsChildren);
      this.comparator = newComparator;
   }

   public void setComparator(Comparator newComparator) {
      this.comparator = newComparator;
      this.sortChildren(this.children.toArray());
   }

   public void add(MutableTreeNode newChild) {
      if (newChild != null && newChild.getParent() == this) {
         this.remove(newChild);
      }

      int index;
      if (this.children == null) {
         index = 0;
      } else {
         index = Collections.binarySearch(this.children, newChild, this.comparator);
      }

      if (index < 0) {
         index = -index - 1;
      }

      this.insert(newChild, index);
   }

   @Override
   public void treeNodesChanged(TreeModelEvent e) {
      super.treeNodesChanged(e);
      if (e.getTreePath().getLastPathComponent() == this) {
         this.sortChildren(e.getChildren());
      }
   }

   @Override
   public void treeStructureChanged(TreeModelEvent e) {
      super.treeStructureChanged(e);
      if (e.getTreePath().getLastPathComponent() == this) {
         this.sortChildren(this.children.toArray());
      }
   }

   protected void sortChildren(Object[] changedChildren) {
      int cCount = changedChildren.length;
      if (cCount > 0) {
         for(int counter = 0; counter < cCount; ++counter) {
            this.remove((MutableTreeNode)changedChildren[counter]);
         }

         for(int var4 = 0; var4 < cCount; ++var4) {
            this.add((MutableTreeNode)changedChildren[var4]);
         }
      }
   }
}
