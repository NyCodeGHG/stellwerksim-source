package js.java.isolate.sim.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.fahrstrasseEvent;
import js.java.isolate.sim.toolkit.fahrstrasseTreeRenderer;
import js.java.tools.NumString;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.tree.ChangableTreeModel;
import js.java.tools.gui.tree.SortedTreeNode;

public class listFWPanel extends basePanel {
   private final listFWPanel.FwSignal start = new listFWPanel.FwSignal();
   private final listFWPanel.FwSignal stop = new listFWPanel.FwSignal();
   private boolean pSelect = false;
   private JMenuItem jMenuItem1;
   private JPopupMenu jPopupMenu1;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JTree startTree;
   private JTree stopTree;

   public listFWPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.start.rootNode = new SortedTreeNode("Startsignale");
      this.start.model = new ChangableTreeModel(this.start.rootNode);
      this.stop.rootNode = new SortedTreeNode("Endsignale");
      this.stop.model = new ChangableTreeModel(this.stop.rootNode);
      this.initComponents();
      this.startTree.getSelectionModel().setSelectionMode(1);
      this.stopTree.getSelectionModel().setSelectionMode(1);
      this.start.tree = this.startTree;
      this.stop.tree = this.stopTree;
      e.registerListener(8, this);
      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof fahrstrasseEvent) {
         if (((fahrstrasseEvent)e).getFS() == null && ((fahrstrasseEvent)e).getCommand() == 2) {
            this.buildModel();
         } else if (((fahrstrasseEvent)e).getFS() != null && ((fahrstrasseEvent)e).getCommand() == 2) {
            this.buildModel();
         } else if (((fahrstrasseEvent)e).getCommand() == 3) {
            this.startTree.repaint();
            this.stopTree.repaint();
         }
      } else if (e instanceof GecSelectEvent) {
         gleis gl = this.glbControl.getSelectedGleis();
         if (gl != null) {
            listFWPanel.SignalNode sn = (listFWPanel.SignalNode)this.start.signals.get(gl);
            if (sn == null) {
               sn = (listFWPanel.SignalNode)this.stop.signals.get(gl);
            }

            if (sn != null) {
               this.select(sn, this.start, true);
               this.select(sn, this.stop, true);
            }
         }
      }
   }

   private void clearModel(listFWPanel.FwSignal s) {
      s.rootNode.removeAllChildren();
      s.rootNode.removeAllChildren();
      s.signals.clear();
      s.fs.clear();
   }

   private void buildModel() {
      this.clearModel(this.start);
      this.clearModel(this.stop);

      for(fahrstrasse f : this.glbControl.getModel().getFahrwegModel()) {
         gleis gl = f.getStart();
         this.checkNode(gl, this.start, f);
         gl = f.getStop();
         this.checkNode(gl, this.stop, f);
      }

      this.startTree.expandRow(0);
      this.stopTree.expandRow(0);
   }

   private listFWPanel.SignalNode checkNode(gleis gl, listFWPanel.FwSignal s, fahrstrasse f) {
      listFWPanel.SignalNode p = (listFWPanel.SignalNode)s.signals.get(gl);
      if (p == null) {
         p = new listFWPanel.SignalNode(gl);
         s.signals.put(gl, p);
         s.rootNode.add(p);
      }

      listFWPanel.FsNode sn = new listFWPanel.FsNode(f);
      s.fs.put(f, sn);
      p.add(sn);
      return p;
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.buildModel();
      gec.addChangeListener(this);
   }

   @Override
   public void hidden(gecBase gec) {
      this.glbControl.getModel().showFahrweg(null);
      this.glbControl.getModel().clearMarkedGleis();
      this.glbControl.setFocus(null);
   }

   public fahrstrasse getSelection() {
      SortedTreeNode node = (SortedTreeNode)this.startTree.getLastSelectedPathComponent();
      return node != null && node instanceof listFWPanel.FsNode ? ((listFWPanel.FsNode)node).getFs() : null;
   }

   public void setSelection(fahrstrasse fs) {
      listFWPanel.FsNode n = (listFWPanel.FsNode)this.start.fs.get(fs);
      if (n == null) {
         n = (listFWPanel.FsNode)this.stop.fs.get(fs);
      }

      if (n != null) {
         this.select(n, this.start, false);
         this.select(n, this.stop, false);
      }
   }

   private void select(SortedTreeNode node, listFWPanel.FwSignal other, boolean expand) {
      if (!this.pSelect) {
         this.pSelect = true;
         this.glbControl.getModel().clearMarkedGleis();
         if (node instanceof listFWPanel.SignalNode) {
            listFWPanel.SignalNode sn = (listFWPanel.SignalNode)node;
            this.glbControl.getModel().setSelectedGleis(null);
            this.glbControl.getModel().setFocus(sn.gl);
            this.glbControl.getModel().showFahrweg(null);
            node = (SortedTreeNode)other.signals.get(sn.gl);
            this.my_main.showFS(null);
         } else if (node instanceof listFWPanel.FsNode) {
            listFWPanel.FsNode fn = (listFWPanel.FsNode)node;
            node = (SortedTreeNode)other.fs.get(fn.fs);
            this.glbControl.getModel().setFocus(fn.getFs().getStart());
            this.glbControl.getModel().showFahrweg(fn.getFs());
            this.my_main.showFS(fn.getFs());
         }

         if (node != null) {
            TreePath p = new TreePath(node.getPath());
            other.tree.setSelectionPath(p);
            other.tree.scrollPathToVisible(p);
            if (expand) {
               other.tree.expandPath(p);
            }
         } else {
            other.tree.setSelectionPath(null);
         }

         this.pSelect = false;
      }
   }

   private void closeExpands(listFWPanel.FwSignal signal) {
      for(int r = 1; r < signal.tree.getRowCount(); ++r) {
         signal.tree.collapseRow(r);
      }
   }

   private void initComponents() {
      this.jPopupMenu1 = new JPopupMenu();
      this.jMenuItem1 = new JMenuItem();
      this.jScrollPane1 = new JScrollPane();
      this.startTree = new JTree();
      this.jScrollPane2 = new JScrollPane();
      this.stopTree = new JTree();
      this.jMenuItem1.setText("alle schließen");
      this.jMenuItem1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            listFWPanel.this.jMenuItem1ActionPerformed(evt);
         }
      });
      this.jPopupMenu1.add(this.jMenuItem1);
      this.setBorder(BorderFactory.createTitledBorder("definierte Fahrstraßen"));
      this.setComponentPopupMenu(this.jPopupMenu1);
      this.setLayout(new GridLayout(1, 0));
      this.startTree.setModel(this.start.model);
      this.startTree.setCellRenderer(new fahrstrasseTreeRenderer());
      this.startTree.setComponentPopupMenu(this.jPopupMenu1);
      this.startTree.addTreeSelectionListener(new TreeSelectionListener() {
         public void valueChanged(TreeSelectionEvent evt) {
            listFWPanel.this.startTreeValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.startTree);
      this.add(this.jScrollPane1);
      this.stopTree.setModel(this.stop.model);
      this.stopTree.setCellRenderer(new fahrstrasseTreeRenderer());
      this.stopTree.setComponentPopupMenu(this.jPopupMenu1);
      this.stopTree.addTreeSelectionListener(new TreeSelectionListener() {
         public void valueChanged(TreeSelectionEvent evt) {
            listFWPanel.this.stopTreeValueChanged(evt);
         }
      });
      this.jScrollPane2.setViewportView(this.stopTree);
      this.add(this.jScrollPane2);
   }

   private void startTreeValueChanged(TreeSelectionEvent evt) {
      SortedTreeNode node = (SortedTreeNode)this.startTree.getLastSelectedPathComponent();
      this.select(node, this.stop, false);
   }

   private void stopTreeValueChanged(TreeSelectionEvent evt) {
      SortedTreeNode node = (SortedTreeNode)this.stopTree.getLastSelectedPathComponent();
      this.select(node, this.start, false);
   }

   private void jMenuItem1ActionPerformed(ActionEvent evt) {
      this.closeExpands(this.start);
      this.closeExpands(this.stop);
   }

   static class FsNode extends SortedTreeNode implements Comparable {
      private final fahrstrasse fs;

      FsNode(fahrstrasse fs) {
         super(fs);
         this.fs = fs;
      }

      public fahrstrasse getFs() {
         return this.fs;
      }

      public int compareTo(Object o) {
         listFWPanel.FsNode oo = (listFWPanel.FsNode)o;
         return this.fs.getName().compareToIgnoreCase(oo.fs.getName());
      }

      public String toString() {
         return this.fs.getName();
      }
   }

   private static class FwSignal {
      ChangableTreeModel model;
      SortedTreeNode rootNode;
      HashMap<gleis, listFWPanel.SignalNode> signals = new HashMap();
      HashMap<fahrstrasse, listFWPanel.FsNode> fs = new HashMap();
      JTree tree;

      private FwSignal() {
         super();
      }
   }

   private static class SignalNode extends SortedTreeNode implements Comparable {
      private final gleis gl;
      private final NumString text;

      SignalNode(gleis gl) {
         super();
         this.gl = gl;
         gleisTypContainer gtc = gleisTypContainer.getInstance();
         this.text = new NumString(gl.getENR() + " (X" + gl.getCol() + "/Y" + gl.getRow() + "): " + gtc.getTypElementName(gl));
         this.setUserObject(this);
      }

      public int compareTo(Object o) {
         listFWPanel.SignalNode oo = (listFWPanel.SignalNode)o;
         return this.text.compareTo(oo.text);
      }

      public String toString() {
         return this.text.toString();
      }
   }
}
