package js.java.tools.gui.tree;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class ChangableTreeModel extends DefaultTreeModel implements TreeModelListener {
   public ChangableTreeModel(ChangableTreeNode root) {
      this(root, false);
   }

   public ChangableTreeModel(ChangableTreeNode root, boolean asksAllowsChildren) {
      super(root, asksAllowsChildren);
      if (root != null) {
         root.addTreeModelListener(this);
      }
   }

   public void setRoot(TreeNode root) {
      ChangableTreeNode oldRoot = (ChangableTreeNode)this.getRoot();
      if (oldRoot != null) {
         oldRoot.removeTreeModelListener(this);
      }

      super.setRoot(root);
      if (root != null) {
         ((ChangableTreeNode)root).addTreeModelListener(this);
      }
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
}
