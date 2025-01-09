package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.colorSystem.gleisColor;
import js.java.isolate.sim.gleis.mass.massBase;
import js.java.isolate.sim.gleisbild.bahnsteigDetailStore;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.toolkit.MXbuttonGroup;
import js.java.tools.ColorText;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.BoundedPlainDocument;
import js.java.tools.gui.border.DropShadowBorder;
import js.java.tools.gui.renderer.ComboColorRenderer;

public class gleisPropertyPanel extends basePanel {
   private final DefaultListModel dataModel;
   private boolean shown = false;
   private final ArrayList<String> farbCBpos = new ArrayList();
   private JList dataList;
   private JTextField elementNameTF;
   private JCheckBox entscheiderSignalCB;
   private JComboBox farbeCB;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JSeparator jSeparator1;
   private JComboBox masstabCB;
   private JCheckBox updateAlternativeBahnsteige;
   private JCheckBox updateConnectedNachbar;
   private JCheckBox updateDisplay;
   private JCheckBox updateNachbar;
   private JCheckBox vorsignalSignalCB;

   public gleisPropertyPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.jPanel1.remove(this.entscheiderSignalCB);
      TreeMap<String, Color> cols = gleisColor.getInstance().getBGcolors();

      for (String k : cols.keySet()) {
         this.farbeCB.addItem(new ColorText(k, (Color)cols.get(k)));
         this.farbCBpos.add(k);
      }

      massBase.MasstabItem[] m = this.glbControl.getModel().getMasstabCalculator().getMasstabList();

      for (massBase.MasstabItem it : m) {
         this.masstabCB.addItem("<html>" + it.label.getText() + "</html>");
      }

      this.enable(null);
      this.dataModel = new DefaultListModel();
      this.dataList.setModel(this.dataModel);
      MXbuttonGroup bg = new MXbuttonGroup();
      bg.add(this.updateDisplay);
      bg.add(this.updateNachbar);
      bg.add(this.updateConnectedNachbar);
      bg.add(this.updateAlternativeBahnsteige);
   }

   private void enable(gleis gl) {
      boolean general = gl != null;
      boolean signal = general && gl.getElement() == gleis.ELEMENT_SIGNAL;
      this.farbeCB.setEnabled(general);
      this.masstabCB.setEnabled(general);
      this.entscheiderSignalCB.setEnabled(signal);
   }

   private void updateValues(gleis gl) {
      String f = gl.getExtendFarbe();
      this.farbeCB.setSelectedIndex(this.farbCBpos.indexOf(f));
      this.masstabCB.setSelectedIndex(gl.getMasstab());
      this.entscheiderSignalCB.setEnabled(gl.getElement() == gleis.ELEMENT_SIGNAL);
      this.entscheiderSignalCB.setSelected(gl.getElement() == gleis.ELEMENT_SIGNAL && gl.getGleisExtend().isEntscheider());
      this.vorsignalSignalCB.setEnabled(gl.getElement() == gleis.ELEMENT_SIGNAL);
      this.vorsignalSignalCB.setSelected(gl.getElement() == gleis.ELEMENT_SIGNAL && gl.getGleisExtend().isVorsignal());
      this.elementNameTF.setText(gl.getGleisExtend().getElementName());
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof GecSelectEvent && this.shown) {
         this.enable(this.glbControl.getSelectedGleis());
         this.updateValues(this.glbControl.getSelectedGleis());
         this.updateNachbarItemStateChanged(null);
         this.updateDisplayItemStateChanged(null);
         this.updateConnectedNachbarItemStateChanged(null);
         this.updateAlternativeBahnsteigeItemStateChanged(null);
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.shown = true;
      this.enable(null);
      this.glbControl.getMode().addChangeListener(this);
      this.dataModel.clear();
      this.updateNachbarItemStateChanged(null);
      this.updateDisplayItemStateChanged(null);
      this.updateConnectedNachbarItemStateChanged(null);
      this.updateAlternativeBahnsteigeItemStateChanged(null);
   }

   @Override
   public void hidden(gecBase gec) {
      this.shown = false;
      this.enable(null);
      this.dataModel.clear();
      this.glbControl.getModel().clearMarkedGleis();
      this.glbControl.getModel().clearRolloverGleis();
   }

   private void initComponents() {
      this.jPanel2 = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      this.dataList = new JList();
      this.jPanel1 = new JPanel();
      this.jLabel1 = new JLabel();
      this.farbeCB = new JComboBox();
      this.jLabel2 = new JLabel();
      this.masstabCB = new JComboBox();
      this.jLabel3 = new JLabel();
      this.elementNameTF = new JTextField();
      this.vorsignalSignalCB = new JCheckBox();
      this.entscheiderSignalCB = new JCheckBox();
      this.jSeparator1 = new JSeparator();
      this.jScrollPane1 = new JScrollPane();
      this.jPanel3 = new JPanel();
      this.updateDisplay = new JCheckBox();
      this.updateNachbar = new JCheckBox();
      this.updateConnectedNachbar = new JCheckBox();
      this.updateAlternativeBahnsteige = new JCheckBox();
      this.setBorder(BorderFactory.createTitledBorder("Gleiseigenschaften"));
      this.setLayout(new GridBagLayout());
      this.jPanel2.setBorder(new DropShadowBorder(true, true, true, true));
      this.jPanel2.setLayout(new BorderLayout());
      this.dataList.setSelectionMode(0);
      this.jScrollPane2.setViewportView(this.dataList);
      this.jPanel2.add(this.jScrollPane2, "Center");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 0.5;
      gridBagConstraints.weighty = 1.0;
      this.add(this.jPanel2, gridBagConstraints);
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel1.setText("Hintergrund");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel1, gridBagConstraints);
      this.farbeCB.setFocusable(false);
      this.farbeCB.setRenderer(new ComboColorRenderer());
      this.farbeCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            gleisPropertyPanel.this.farbeCBItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(0, 5, 1, 0);
      this.jPanel1.add(this.farbeCB, gridBagConstraints);
      this.jLabel2.setText("Maßstab");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      this.masstabCB.setFocusable(false);
      this.masstabCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            gleisPropertyPanel.this.masstabCBItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(1, 5, 0, 0);
      this.jPanel1.add(this.masstabCB, gridBagConstraints);
      this.jLabel3.setText("Name (opt)");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.jLabel3, gridBagConstraints);
      this.elementNameTF.setColumns(3);
      this.elementNameTF.setDocument(new BoundedPlainDocument(3));
      this.elementNameTF.setToolTipText("Name von Signalen, Weichen, BÜs, ...");
      this.elementNameTF.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent evt) {
            gleisPropertyPanel.this.elementNameTFFocusLost(evt);
         }
      });
      this.elementNameTF.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisPropertyPanel.this.elementNameTFActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(1, 5, 0, 0);
      this.jPanel1.add(this.elementNameTF, gridBagConstraints);
      this.vorsignalSignalCB.setText("Mit Vorsignal");
      this.vorsignalSignalCB.setFocusPainted(false);
      this.vorsignalSignalCB.setFocusable(false);
      this.vorsignalSignalCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisPropertyPanel.this.vorsignalSignalCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.vorsignalSignalCB, gridBagConstraints);
      this.entscheiderSignalCB.setText("Richtungsanzeiger");
      this.entscheiderSignalCB.setFocusPainted(false);
      this.entscheiderSignalCB.setFocusable(false);
      this.entscheiderSignalCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisPropertyPanel.this.entscheiderSignalCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.entscheiderSignalCB, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 11;
      this.jPanel1.add(this.jSeparator1, gridBagConstraints);
      this.jScrollPane1.setBorder(null);
      this.jScrollPane1.setHorizontalScrollBarPolicy(31);
      this.jScrollPane1.setMinimumSize(new Dimension(223, 5));
      this.jPanel3.setLayout(new BoxLayout(this.jPanel3, 3));
      this.updateDisplay.setText("Displayverbindungen zeigen");
      this.updateDisplay.setFocusable(false);
      this.updateDisplay.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            gleisPropertyPanel.this.updateDisplayItemStateChanged(evt);
         }
      });
      this.jPanel3.add(this.updateDisplay);
      this.updateNachbar.setText("Nachbarbahnsteige zeigen");
      this.updateNachbar.setFocusable(false);
      this.updateNachbar.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            gleisPropertyPanel.this.updateNachbarItemStateChanged(evt);
         }
      });
      this.jPanel3.add(this.updateNachbar);
      this.updateConnectedNachbar.setText("Verbundene Nachbarbahnsteige zeigen");
      this.updateConnectedNachbar.setFocusable(false);
      this.updateConnectedNachbar.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            gleisPropertyPanel.this.updateConnectedNachbarItemStateChanged(evt);
         }
      });
      this.jPanel3.add(this.updateConnectedNachbar);
      this.updateAlternativeBahnsteige.setText("Alternativbahnsteige zeigen");
      this.updateAlternativeBahnsteige.setFocusable(false);
      this.updateAlternativeBahnsteige.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            gleisPropertyPanel.this.updateAlternativeBahnsteigeItemStateChanged(evt);
         }
      });
      this.jPanel3.add(this.updateAlternativeBahnsteige);
      this.jScrollPane1.setViewportView(this.jPanel3);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel1.add(this.jScrollPane1, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.anchor = 18;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.add(this.jPanel1, gridBagConstraints);
   }

   private void farbeCBItemStateChanged(ItemEvent evt) {
      try {
         ColorText sel = (ColorText)this.farbeCB.getSelectedItem();
         this.glbControl.getSelectedGleis().setExtendFarbe(sel.getText());
         this.glbControl.repaint();
      } catch (NullPointerException var3) {
      }
   }

   private void masstabCBItemStateChanged(ItemEvent evt) {
      try {
         int m = this.masstabCB.getSelectedIndex();
         this.glbControl.getSelectedGleis().setMasstab(m);
         this.glbControl.repaint();
      } catch (NullPointerException var3) {
      }
   }

   private void updateNachbarItemStateChanged(ItemEvent evt) {
      if (this.updateNachbar.isSelected()) {
         this.glbControl.getModel().clearMarkedGleis();
         this.glbControl.getModel().clearRolloverGleis();
         this.dataModel.clear();
         gleis g = this.glbControl.getSelectedGleis();
         if (g != null && gleis.ALLE_BAHNSTEIGE.matches(g.getElement())) {
            String n = g.getSWWert();

            for (String s : this.glbControl.getModel().findNeighborBahnsteig(this.glbControl.getModel().findBahnsteig(n), true)) {
               this.dataModel.addElement(s);
            }
         }
      } else if (evt != null) {
         this.glbControl.getModel().clearMarkedGleis();
         this.glbControl.getModel().clearRolloverGleis();
         this.dataModel.clear();
      }
   }

   private void updateDisplayItemStateChanged(ItemEvent evt) {
      if (this.updateDisplay.isSelected()) {
         this.glbControl.getModel().clearMarkedGleis();
         this.glbControl.getModel().clearRolloverGleis();
         this.dataModel.clear();
         gleis g = this.glbControl.getSelectedGleis();
         if (g != null) {
            for (gleis s : this.glbControl.getModel().getDisplayBar().getConnectedItems(g)) {
               this.dataModel
                  .addElement(
                     s.getCol()
                        + "/"
                        + s.getRow()
                        + " ("
                        + gleisTypContainer.getInstance().getTypName(s.getElement())
                        + " - "
                        + gleisTypContainer.getInstance().getTypElementName(s.getElement())
                        + ")"
                  );
               this.glbControl.getModel().addMarkedGleis(s);
            }
         }
      } else if (evt != null) {
         this.glbControl.getModel().clearMarkedGleis();
         this.glbControl.getModel().clearRolloverGleis();
         this.dataModel.clear();
      }
   }

   private void entscheiderSignalCBActionPerformed(ActionEvent evt) {
      try {
         this.glbControl.getSelectedGleis().getGleisExtend().setEntscheider(this.entscheiderSignalCB.isSelected());
         this.glbControl.repaint();
      } catch (NullPointerException var3) {
      }
   }

   private void updateConnectedNachbarItemStateChanged(ItemEvent evt) {
      if (this.updateConnectedNachbar.isSelected()) {
         this.glbControl.getModel().clearMarkedGleis();
         this.glbControl.getModel().clearRolloverGleis();
         this.dataModel.clear();
         gleis g = this.glbControl.getSelectedGleis();
         if (g != null && gleis.ALLE_BAHNSTEIGE.matches(g.getElement())) {
            String n = g.getSWWert();
            Set<gleis> b = this.glbControl.getModel().findAllConnectedBahnsteig(n, true);
            HashSet<String> seen = new HashSet();

            for (gleis g2 : b) {
               this.glbControl.getModel().addMarkedGleis(g2);
               String n2 = g2.getSWWert_special();
               if (!seen.contains(n2)) {
                  this.dataModel.addElement(n2);
                  seen.add(n2);
               }
            }
         }
      } else if (evt != null) {
         this.glbControl.getModel().clearMarkedGleis();
         this.glbControl.getModel().clearRolloverGleis();
         this.dataModel.clear();
      }
   }

   private void updateAlternativeBahnsteigeItemStateChanged(ItemEvent evt) {
      if (this.updateAlternativeBahnsteige.isSelected()) {
         this.glbControl.getModel().clearMarkedGleis();
         this.glbControl.getModel().clearRolloverGleis();
         this.dataModel.clear();
         gleis g = this.glbControl.getSelectedGleis();
         if (g != null && gleis.ALLE_BAHNSTEIGE.matches(g.getElement())) {
            long startTime = System.currentTimeMillis();
            bahnsteigDetailStore bds = new bahnsteigDetailStore(this.glbControl.getModel());
            Set<String> b = bds.getAlternativebahnsteigeOf(g.getSWWert());
            System.out.println("Runtime Alternativsuche: " + (System.currentTimeMillis() - startTime) + " ms");

            for (String gl : b) {
               this.glbControl.getModel().addMarkedGleis(this.glbControl.getModel().findBahnsteig(gl));
               this.dataModel.addElement(gl);
            }

            bds.close();
         }
      } else if (evt != null) {
         this.glbControl.getModel().clearMarkedGleis();
         this.glbControl.getModel().clearRolloverGleis();
         this.dataModel.clear();
      }
   }

   private void vorsignalSignalCBActionPerformed(ActionEvent evt) {
      try {
         this.glbControl.getSelectedGleis().getGleisExtend().setVorsignal(this.vorsignalSignalCB.isSelected());
         this.glbControl.repaint();
      } catch (NullPointerException var3) {
      }
   }

   private void elementNameTFActionPerformed(ActionEvent evt) {
      try {
         this.glbControl.getSelectedGleis().getGleisExtend().setElementName(this.elementNameTF.getText());
         this.glbControl.repaint();
      } catch (NullPointerException var3) {
      }
   }

   private void elementNameTFFocusLost(FocusEvent evt) {
      this.elementNameTFActionPerformed(null);
   }
}
