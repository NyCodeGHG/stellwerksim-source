package js.java.tools.DataTip;

import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

class TreeDataTipCell implements DataTipCell {
   private final JTree tree;
   private final int rowIndex;

   TreeDataTipCell(JTree tree, int rowIndex) {
      super();
      this.tree = tree;
      this.rowIndex = rowIndex;
   }

   @Override
   public boolean isSet() {
      return this.rowIndex >= 0;
   }

   @Override
   public Rectangle getCellBounds() {
      TreePath treePath = this.tree.getPathForRow(this.rowIndex);
      return this.tree.getPathBounds(treePath);
   }

   @Override
   public Component getRendererComponent() {
      TreeModel treeModel = this.tree.getModel();
      TreePath treePath = this.tree.getPathForRow(this.rowIndex);
      TreeCellRenderer renderer = this.tree.getCellRenderer();
      boolean isSelected = this.tree.isPathSelected(treePath);
      boolean isExpanded = this.tree.isExpanded(treePath);
      boolean hasFocus = this.tree.hasFocus() && this.rowIndex == this.tree.getLeadSelectionRow();
      Object item = treePath.getLastPathComponent();
      boolean isLeaf = treeModel.isLeaf(item);
      Component component = renderer.getTreeCellRendererComponent(this.tree, item, isSelected, isExpanded, isLeaf, this.rowIndex, hasFocus);
      component.setFont(this.tree.getFont());
      return component;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TreeDataTipCell treeDataTipCell = (TreeDataTipCell)o;
         return this.rowIndex == treeDataTipCell.rowIndex;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.rowIndex;
   }
}
