package js.java.isolate.sim.structServ;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import js.java.tools.gui.tree.ChangableTreeModel;
import js.java.tools.gui.tree.SortedTreeNode;

public class structListPanel extends JPanel {
   private ChangableTreeModel model;
   private SortedTreeNode rootNode;
   private HashMap<String, structListPanel.Node> parents = new HashMap();
   private structListPanel.selectionListener hook;
   private JTree infoTree;
   private JScrollPane jScrollPane1;

   public structListPanel(structListPanel.selectionListener l) {
      super();
      this.rootNode = new SortedTreeNode("Top");
      this.model = new ChangableTreeModel(this.rootNode);
      this.hook = l;
      this.initComponents();
   }

   public void clear() {
      this.rootNode.removeAllChildren();
      this.parents.clear();
   }

   public void add(Vector data) {
      String type = (String)data.get(0);
      String text = (String)data.get(1);
      structinfo values = (structinfo)data.get(2);
      structListPanel.Node p = (structListPanel.Node)this.parents.get(type);
      if (p == null) {
         p = new structListPanel.Node(type, null);
         this.parents.put(type, p);
         this.rootNode.add(p);
         this.infoTree.expandRow(0);
      }

      structListPanel.Node c = new structListPanel.Node(text, values);
      p.add(c);
   }

   public structinfo getSelected() {
      SortedTreeNode node = (SortedTreeNode)this.infoTree.getLastSelectedPathComponent();
      if (node != null && node instanceof structListPanel.Node) {
         structListPanel.Node o = (structListPanel.Node)node;
         return o.getData();
      } else {
         return null;
      }
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.infoTree = new JTree();
      this.setLayout(new BorderLayout());
      this.infoTree.setModel(this.model);
      this.infoTree.setShowsRootHandles(true);
      this.infoTree.setVisibleRowCount(5);
      this.infoTree.addTreeSelectionListener(new TreeSelectionListener() {
         public void valueChanged(TreeSelectionEvent evt) {
            structListPanel.this.infoTreeValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.infoTree);
      this.add(this.jScrollPane1, "Center");
   }

   private void infoTreeValueChanged(TreeSelectionEvent evt) {
      SortedTreeNode node = (SortedTreeNode)this.infoTree.getLastSelectedPathComponent();
      if (node != null) {
         if (node instanceof structListPanel.Node) {
            structListPanel.Node o = (structListPanel.Node)node;
            if (o.getData() != null) {
               this.hook.selected(o.getData());
            }
         }
      }
   }

   private class Node extends SortedTreeNode implements Comparable {
      private final String text;
      private final structinfo data;

      Node(String text, structinfo data) {
         super();
         this.text = text;
         this.data = data;
      }

      public int compareTo(Object o) {
         structListPanel.Node oo = (structListPanel.Node)o;
         return this.text.compareToIgnoreCase(oo.text);
      }

      public String toString() {
         return this.text;
      }

      public structinfo getData() {
         return this.data;
      }
   }

   public interface selectionListener {
      void selected(structinfo var1);
   }
}
