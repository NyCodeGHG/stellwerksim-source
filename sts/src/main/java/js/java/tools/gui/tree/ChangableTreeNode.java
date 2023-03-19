package js.java.tools.gui.tree;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class ChangableTreeNode extends DefaultMutableTreeNode implements TreeModelListener {
   protected EventListenerList listenerList = new EventListenerList();

   public ChangableTreeNode() {
      super();
   }

   public ChangableTreeNode(Object userObject) {
      super(userObject);
   }

   public ChangableTreeNode(Object userObject, boolean allowsChildren) {
      super(userObject, allowsChildren);
   }

   public void insert(MutableTreeNode newChild, int childIndex) {
      super.insert(newChild, childIndex);
      int[] newIndexs = new int[]{childIndex};
      this.nodesWereInserted(newIndexs);
      ((ChangableTreeNode)newChild).addTreeModelListener(this);
   }

   public void remove(int childIndex) {
      Object[] removedArray = new Object[1];
      ChangableTreeNode node = (ChangableTreeNode)this.getChildAt(childIndex);
      node.removeTreeModelListener(this);
      removedArray[0] = node;
      super.remove(childIndex);
      this.nodesWereRemoved(new int[]{childIndex}, removedArray);
   }

   public void setUserObject(Object userObject) {
      super.setUserObject(userObject);
      this.nodeChanged();
   }

   public void treeNodesChanged(TreeModelEvent e) {
      this.fireTreeNodesChanged(e.getSource(), e.getPath(), e.getChildIndices(), e.getChildren());
   }

   public void treeNodesInserted(TreeModelEvent e) {
      this.fireTreeNodesInserted(e.getSource(), e.getPath(), e.getChildIndices(), e.getChildren());
   }

   public void treeNodesRemoved(TreeModelEvent e) {
      this.fireTreeNodesRemoved(e.getSource(), e.getPath(), e.getChildIndices(), e.getChildren());
   }

   public void treeStructureChanged(TreeModelEvent e) {
      this.fireTreeStructureChanged(e.getSource(), e.getPath(), e.getChildIndices(), e.getChildren());
   }

   protected void nodeChanged() {
      if (this.listenerList != null) {
         ChangableTreeNode parent = (ChangableTreeNode)this.getParent();
         if (parent != null) {
            int anIndex = parent.getIndex(this);
            if (anIndex != -1) {
               int[] cIndexs = new int[]{anIndex};
               Object[] cChildren = new Object[]{this};
               this.fireTreeNodesChanged(parent.getPath(), cIndexs, cChildren);
            }
         } else if (this == this.getRoot()) {
            this.fireTreeNodesChanged(this.getPath(), null, null);
         }
      }
   }

   protected void nodesWereInserted(int[] childIndices) {
      if (this.listenerList != null && childIndices != null && childIndices.length > 0) {
         int cCount = childIndices.length;
         Object[] newChildren = new Object[cCount];

         for(int counter = 0; counter < cCount; ++counter) {
            newChildren[counter] = this.getChildAt(childIndices[counter]);
         }

         this.fireTreeNodesInserted(childIndices, newChildren);
      }
   }

   protected void nodesWereRemoved(int[] childIndices, Object[] removedChildren) {
      if (childIndices != null) {
         this.fireTreeNodesRemoved(childIndices, removedChildren);
      }
   }

   protected void nodeStructureChanged() {
      this.fireTreeStructureChanged();
   }

   public void addTreeModelListener(TreeModelListener l) {
      this.listenerList.add(TreeModelListener.class, l);
   }

   public void removeTreeModelListener(TreeModelListener l) {
      this.listenerList.remove(TreeModelListener.class, l);
   }

   protected void fireTreeNodesChanged(int[] childIndices, Object[] children) {
      this.fireTreeNodesChanged(this.getPath(), childIndices, children);
   }

   protected void fireTreeNodesChanged(Object[] path, int[] childIndices, Object[] children) {
      this.fireTreeNodesChanged(this, path, childIndices, children);
   }

   protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
      Object[] listeners = this.listenerList.getListenerList();
      TreeModelEvent e = null;

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == TreeModelListener.class) {
            if (e == null) {
               e = new TreeModelEvent(source, path, childIndices, children);
            }

            ((TreeModelListener)listeners[i + 1]).treeNodesChanged(e);
         }
      }
   }

   protected void fireTreeNodesInserted(int[] childIndices, Object[] children) {
      this.fireTreeNodesInserted(this, this.getPath(), childIndices, children);
   }

   protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
      Object[] listeners = this.listenerList.getListenerList();
      TreeModelEvent e = null;

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == TreeModelListener.class) {
            if (e == null) {
               e = new TreeModelEvent(source, path, childIndices, children);
            }

            ((TreeModelListener)listeners[i + 1]).treeNodesInserted(e);
         }
      }
   }

   protected void fireTreeNodesRemoved(int[] childIndices, Object[] children) {
      this.fireTreeNodesRemoved(this, this.getPath(), childIndices, children);
   }

   protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
      Object[] listeners = this.listenerList.getListenerList();
      TreeModelEvent e = null;

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == TreeModelListener.class) {
            if (e == null) {
               e = new TreeModelEvent(source, path, childIndices, children);
            }

            ((TreeModelListener)listeners[i + 1]).treeNodesRemoved(e);
         }
      }
   }

   protected void fireTreeStructureChanged() {
      this.fireTreeStructureChanged(new int[0], null);
   }

   protected void fireTreeStructureChanged(int[] childIndices, Object[] children) {
      this.fireTreeStructureChanged(this, this.getPath(), childIndices, children);
   }

   protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
      Object[] listeners = this.listenerList.getListenerList();
      TreeModelEvent e = null;

      for(int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == TreeModelListener.class) {
            if (e == null) {
               e = new TreeModelEvent(source, path, childIndices, children);
            }

            ((TreeModelListener)listeners[i + 1]).treeStructureChanged(e);
         }
      }
   }
}
