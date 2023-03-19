package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse_extend;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.fahrstrasseEvent;
import js.java.isolate.sim.toolkit.fahrstrasseListRenderer;
import js.java.isolate.sim.toolkit.fahrstrasseTreeRenderer;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.IconRadioButton;
import js.java.tools.gui.tree.ChangableTreeModel;
import js.java.tools.gui.tree.ChangableTreeNode;
import js.java.tools.gui.tree.SortedTreeNode;

public class editFWPanel extends basePanel {
   private final HashMap<Integer, JRadioButton> fstypes = new HashMap();
   private final HashMap<Integer, SortedTreeNode> fstreelist = new HashMap();
   private final HashMap<fahrstrasse, listFWPanel.FsNode> fsnodes = new HashMap();
   private final ChangableTreeNode rootNode;
   private final ChangableTreeModel model;
   private boolean pSelect = false;
   private ButtonGroup autoFSmodebuttonGroup;
   private JPanel fstypePanel;
   private JPanel jPanel1;
   private JScrollPane jScrollPane1;
   private JTree tree;

   public editFWPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.rootNode = new ChangableTreeNode("");
      this.model = new ChangableTreeModel(this.rootNode);
      this.initComponents();
      this.tree.getSelectionModel().setSelectionMode(1);
      e.registerListener(5, this);
      this.addRadio("automatisch", "Das System bestimmt automatisch was bei dieser Fahrstraße möglich ist.", fahrstrasseListRenderer.FSTYPE_DEFAULT, 0);
      this.addRadio(
         "nur Rf erlauben", "Nur eine Rf erlauben - sofern ein Rangierknopf (grauer Knopf) beteilig ist, sonst wird keine FS gelegt!", "disconnect16.png", 8
      );
      this.addRadio("nur Zf erlauben", "Nur eine Zug-FS erlauben", "subway16.png", 16);
      this.addRadio("Fahrstraße nicht wählbar (gelöscht)", "Diese Fahrstraße ist nicht wählbar.", "trash16.png", 4);
      this.addRadio("ist AutoFS", "Diese Fahrstraße wird zur AutoFS für das Startsignal.", "accept16.png", 2);
      this.addRadio("keine AutoFS", "Diese Fahrstraße wird niemals AutoFS.", "pause16.png", 1);
   }

   private void addRadio(String text, String tooltip, String image, final int fstype) {
      IconRadioButton b = new IconRadioButton(16);
      this.autoFSmodebuttonGroup.add(b);
      this.fstypes.put(fstype, b);
      if (image != null) {
         b.setExtraIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/" + image)));
      }

      b.setText("<html>" + text + "</html>");
      b.setToolTipText(tooltip);
      b.setFocusPainted(false);
      b.setFocusable(false);
      b.addActionListener(new ActionListener() {
         private final int type = fstype;

         public void actionPerformed(ActionEvent evt) {
            editFWPanel.this.fstypeActionPerformed(this.type);
         }
      });
      this.fstypePanel.add(b);
      SortedTreeNode n = new SortedTreeNode(text);
      this.rootNode.add(n);
      this.fstreelist.put(fstype, n);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof fahrstrasseEvent) {
         this.showFS(((fahrstrasseEvent)e).getFS());
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.fsnodes.clear();

      for(SortedTreeNode p : this.fstreelist.values()) {
         p.removeAllChildren();
      }

      for(fahrstrasse f : this.glbControl.getModel().getFahrwegModel()) {
         SortedTreeNode p = (SortedTreeNode)this.fstreelist.get(f.getExtend().getFSType());
         listFWPanel.FsNode nd = new listFWPanel.FsNode(f);
         p.add(nd);
         this.fsnodes.put(f, nd);
      }

      this.tree.expandPath(new TreePath(this.rootNode.getPath()));
      this.showFS(this.my_main.getSelectedFahrstrasse());
   }

   private void showFS(fahrstrasse fs) {
      for(Entry<Integer, JRadioButton> b : this.fstypes.entrySet()) {
         if (fs != null) {
            fahrstrasse_extend fe = fs.getExtend();
            if (b.getKey() == fe.getFSType()) {
               ((JRadioButton)b.getValue()).setSelected(true);
            }
         }

         ((JRadioButton)b.getValue()).setEnabled(fs != null);
      }

      this.tree.setSelectionPath(null);
      if (fs != null) {
         listFWPanel.FsNode nd = (listFWPanel.FsNode)this.fsnodes.get(fs);
         if (nd != null) {
            TreePath p = new TreePath(nd.getPath());
            this.tree.setSelectionPath(p);
            this.tree.scrollPathToVisible(p);
         }
      }
   }

   private void fstypeActionPerformed(int type) {
      fahrstrasse fs = this.my_main.getSelectedFahrstrasse();
      if (fs != null) {
         fahrstrasse_extend fe = fs.getExtend();
         fe.setFSType(type);
         this.my_main.interPanelCom(new fahrstrasseEvent(fs, 3));
         listFWPanel.FsNode nd = (listFWPanel.FsNode)this.fsnodes.get(fs);
         SortedTreeNode p = (SortedTreeNode)this.fstreelist.get(type);
         p.add(nd);
         TreePath tp = new TreePath(nd.getPath());
         this.tree.setSelectionPath(tp);
         this.tree.scrollPathToVisible(tp);
      }
   }

   private void initComponents() {
      this.autoFSmodebuttonGroup = new ButtonGroup();
      this.fstypePanel = new JPanel();
      this.jPanel1 = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.tree = new JTree();
      this.setBorder(BorderFactory.createTitledBorder("Fahrstraßenoptionen bearbeiten"));
      this.setMinimumSize(new Dimension(86, 100));
      this.setPreferredSize(new Dimension(86, 100));
      this.setLayout(new GridLayout(1, 0));
      this.fstypePanel.setBorder(BorderFactory.createTitledBorder("FS-Modus"));
      this.fstypePanel.setLayout(new BoxLayout(this.fstypePanel, 3));
      this.add(this.fstypePanel);
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("Zusammenfassung"));
      this.jPanel1.setLayout(new BorderLayout());
      this.tree.setModel(this.model);
      this.tree.setCellRenderer(new fahrstrasseTreeRenderer());
      this.tree.setRootVisible(false);
      this.tree.setShowsRootHandles(true);
      this.tree.setToggleClickCount(1);
      this.tree.addTreeSelectionListener(new TreeSelectionListener() {
         public void valueChanged(TreeSelectionEvent evt) {
            editFWPanel.this.treeValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.tree);
      this.jPanel1.add(this.jScrollPane1, "Center");
      this.add(this.jPanel1);
   }

   private void treeValueChanged(TreeSelectionEvent evt) {
      if (!this.pSelect) {
         this.pSelect = true;
         SortedTreeNode node = (SortedTreeNode)this.tree.getLastSelectedPathComponent();
         if (node instanceof listFWPanel.FsNode) {
            this.my_main.setSelectedFahrstrasse(((listFWPanel.FsNode)node).getFs());
         }

         this.pSelect = false;
      }
   }
}
