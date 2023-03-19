package js.java.isolate.sim.toolkit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import js.java.tools.gui.tree.SortedTreeNode;

public class fahrstrasseTreeRenderer extends fahrstrasseListRenderer implements TreeCellRenderer {
   private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
   private JPanel topPanel = new JPanel();

   public fahrstrasseTreeRenderer() {
      super(false);
      this.topPanel.setOpaque(false);
      this.topPanel.setLayout(new BorderLayout());
      this.topPanel.add(this.renderer, "West");
      this.topPanel.add(this, "Center");
      this.setMinimumSize(new Dimension(180, 12));
      this.setPreferredSize(new Dimension(180, 18));
   }

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      this.renderer.getTreeCellRendererComponent(tree, "", selected, expanded, leaf, row, hasFocus);
      Color bcol = this.renderer.getBackgroundNonSelectionColor();
      if (selected) {
         bcol = this.renderer.getBackgroundSelectionColor();
      }

      if (value != null && value instanceof SortedTreeNode) {
         value = ((SortedTreeNode)value).getUserObject();
      }

      this.prepareCellRenderer(value, selected, hasFocus, this.renderer.getForeground(), bcol);
      return this.topPanel;
   }
}
