package js.java.isolate.sim.panels;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.StructureChangeEvent;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.NumberTextField;

public class sizePanel extends basePanel {
   private final LinkedHashMap<String, Integer> masse = gleisbildModel.getMasstabNames(true);
   private final HashMap<JRadioButton, Integer> massButtons = new HashMap();
   private NumberTextField myinput_width;
   private NumberTextField myinput_height;
   private JLabel elementCountLabel;
   private JLabel fahrstrasseCountLabel;
   private JLabel freeArea;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JPanel jPanel4;
   private JScrollPane jScrollPane1;
   private JPanel massPanel;
   private JButton minimizeButton;
   private JSpinner nHorizSpinner;
   private JSpinner nVertSpinner;
   private JPanel nachbarPanel;
   private JButton resizeButton;
   private JPanel sizePanel;
   private JPanel sizeSettingsPanel;
   private JLabel stoerungCountLabel;

   public sizePanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      ButtonGroup mgrp = new ButtonGroup();

      for(String n : this.masse.keySet()) {
         JRadioButton rb = new JRadioButton(n);
         this.massPanel.add(rb);
         mgrp.add(rb);
         this.massButtons.put(rb, this.masse.get(n));
         rb.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  Integer v = (Integer)sizePanel.this.massButtons.get((JRadioButton)e.getSource());
                  if (!sizePanel.this.glbControl.getModel().isMasstabCalculatorCompatible(v)) {
                     int r = JOptionPane.showConfirmDialog(
                        sizePanel.this,
                        "Das neue Maßstabsystem ist nicht kompatibel zum aktuellen,\nbestehende Maßstabsdaten werden gelöscht.",
                        "Inkompatibel!",
                        2,
                        2
                     );
                     if (r == 2) {
                        sizePanel.this.shown("", null);
                        return;
                     }
                  }
   
                  sizePanel.this.glbControl.getModel().setMasstabCalculator(v);
               }
            }
         );
      }

      this.glbControl.getModel().addStructureChangeListener(this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof StructureChangeEvent) {
         this.gleisWidthHeight(this.glbControl.getModel().getGleisWidth(), this.glbControl.getModel().getGleisHeight());
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      int v = this.glbControl.getModel().getMasstabCalculatorName();

      for(Entry<JRadioButton, Integer> m : this.massButtons.entrySet()) {
         if (m.getValue() == v) {
            ((JRadioButton)m.getKey()).setSelected(true);
            break;
         }
      }

      this.nHorizSpinner.setValue(this.glbControl.getModel().gleisbildextend.getNachbarbahnsteigLookupWidth());
      this.nVertSpinner.setValue(this.glbControl.getModel().gleisbildextend.getNachbarbahnsteighoriz());
      this.gleisWidthHeight(this.glbControl.getModel().getGleisWidth(), this.glbControl.getModel().getGleisHeight());
   }

   private void updateStatistics() {
      this.elementCountLabel.setText("" + this.glbControl.getModel().getGleisWidth() * this.glbControl.getModel().getGleisHeight());
      this.fahrstrasseCountLabel.setText("" + this.glbControl.getModel().countFahrwege());
      this.stoerungCountLabel.setText("" + this.glbControl.getModel().events.size());
   }

   private void gleisWidthHeight(int w, int h) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.myinput_width.setInt(w);
         this.myinput_height.setInt(h);
         this.updateStatistics();
      } else {
         SwingUtilities.invokeLater(() -> this.gleisWidthHeight(w, h));
      }
   }

   private void initComponents() {
      this.sizeSettingsPanel = new JPanel();
      this.myinput_width = new NumberTextField();
      this.myinput_height = new NumberTextField();
      this.myinput_width.setText("100");
      this.myinput_height.setText("100");
      this.myinput_width.setColumns(3);
      this.myinput_height.setColumns(3);
      this.sizePanel = new JPanel();
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.resizeButton = new JButton();
      this.minimizeButton = new JButton();
      this.jPanel1 = new JPanel();
      this.jLabel3 = new JLabel();
      this.elementCountLabel = new JLabel();
      this.jLabel4 = new JLabel();
      this.fahrstrasseCountLabel = new JLabel();
      this.jLabel5 = new JLabel();
      this.stoerungCountLabel = new JLabel();
      this.freeArea = new JLabel();
      this.jScrollPane1 = new JScrollPane();
      this.jPanel2 = new JPanel();
      this.massPanel = new JPanel();
      this.nachbarPanel = new JPanel();
      this.jPanel3 = new JPanel();
      this.jLabel6 = new JLabel();
      this.nHorizSpinner = new JSpinner();
      this.jLabel7 = new JLabel();
      this.nVertSpinner = new JSpinner();
      this.jPanel4 = new JPanel();
      this.setLayout(new GridBagLayout());
      this.sizeSettingsPanel.setBorder(BorderFactory.createTitledBorder("Gleisbildgröße"));
      this.sizeSettingsPanel.setLayout(new GridBagLayout());
      this.sizePanel.setLayout(new FlowLayout(0));
      this.jLabel1.setText("Größe");
      this.sizePanel.add(this.jLabel1);
      this.jLabel2.setText("x");
      this.sizePanel.add(this.myinput_width);
      this.sizePanel.add(this.jLabel2);
      this.sizePanel.add(this.myinput_height);
      this.resizeButton.setText("ändern");
      this.resizeButton.setToolTipText("<html>Die maximale Größe beträgt<br>10000 Felder (Breite x Höhe).</html>");
      this.resizeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            sizePanel.this.resizeButtonActionPerformed(evt);
         }
      });
      this.sizePanel.add(this.resizeButton);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      this.sizeSettingsPanel.add(this.sizePanel, gridBagConstraints);
      this.minimizeButton.setText("Größe minimieren");
      this.minimizeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            sizePanel.this.minimizeButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 18;
      gridBagConstraints.weightx = 1.0;
      this.sizeSettingsPanel.add(this.minimizeButton, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 0.5;
      this.add(this.sizeSettingsPanel, gridBagConstraints);
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("Statistik"));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel3.setText("Elementzahl:");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.jLabel3, gridBagConstraints);
      this.elementCountLabel.setHorizontalAlignment(4);
      this.elementCountLabel.setText("0");
      this.elementCountLabel.setBorder(BorderFactory.createEtchedBorder());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.elementCountLabel, gridBagConstraints);
      this.jLabel4.setText("Fahrstraßen:");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.jLabel4, gridBagConstraints);
      this.fahrstrasseCountLabel.setHorizontalAlignment(4);
      this.fahrstrasseCountLabel.setText("0");
      this.fahrstrasseCountLabel.setBorder(BorderFactory.createEtchedBorder());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.fahrstrasseCountLabel, gridBagConstraints);
      this.jLabel5.setText("Störungen:");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.jLabel5, gridBagConstraints);
      this.stoerungCountLabel.setHorizontalAlignment(4);
      this.stoerungCountLabel.setText("0");
      this.stoerungCountLabel.setBorder(BorderFactory.createEtchedBorder());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.stoerungCountLabel, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = 3;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel1.add(this.freeArea, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 0.5;
      gridBagConstraints.weighty = 1.0;
      this.add(this.jPanel1, gridBagConstraints);
      this.jScrollPane1.setBorder(null);
      this.jPanel2.setLayout(new GridBagLayout());
      this.massPanel.setBorder(BorderFactory.createTitledBorder("Maßstabsystem"));
      this.massPanel.setLayout(new BoxLayout(this.massPanel, 3));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel2.add(this.massPanel, gridBagConstraints);
      this.nachbarPanel.setBorder(BorderFactory.createTitledBorder("Nachbarerkennung"));
      this.nachbarPanel.setLayout(new GridBagLayout());
      this.jPanel3.setLayout(new GridBagLayout());
      this.jLabel6.setText("horizontale Such-Breite");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.jPanel3.add(this.jLabel6, gridBagConstraints);
      this.nHorizSpinner.setModel(new SpinnerNumberModel(5, 3, 40, 1));
      this.nHorizSpinner.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent evt) {
            sizePanel.this.nHorizSpinnerStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.insets = new Insets(0, 10, 0, 0);
      this.jPanel3.add(this.nHorizSpinner, gridBagConstraints);
      this.jLabel7.setText("vertikale Mindest-Leerzeilen");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.jPanel3.add(this.jLabel7, gridBagConstraints);
      this.nVertSpinner.setModel(new SpinnerNumberModel(5, 3, 20, 1));
      this.nVertSpinner.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent evt) {
            sizePanel.this.nVertSpinnerStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.insets = new Insets(0, 10, 0, 0);
      this.jPanel3.add(this.nVertSpinner, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 23;
      this.nachbarPanel.add(this.jPanel3, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.nachbarPanel.add(this.jPanel4, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel2.add(this.nachbarPanel, gridBagConstraints);
      this.jScrollPane1.setViewportView(this.jPanel2);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.add(this.jScrollPane1, gridBagConstraints);
   }

   private void resizeButtonActionPerformed(ActionEvent evt) {
      int w = this.myinput_width.getInt();
      int h = this.myinput_height.getInt();
      if (this.glbControl.getModel().gl_overmaxsize(w, h) > 0) {
         int r = JOptionPane.showConfirmDialog(
            this,
            "Achtung! Die Größe überschreitet "
               + 10000
               + " Felder um "
               + this.glbControl.getModel().gl_overmaxsize(w, h)
               + " Felder,\ndie Anlage wird sich dann mit hoher Wahrscheinlichkeit\nnicht mehr laden lassen.\n\nSoll die Größe trotzdem geändert werden?",
            "Anlage zu groß!",
            0,
            2
         );
         if (r == 1) {
            w = this.glbControl.getModel().getGleisWidth();
            h = this.glbControl.getModel().getGleisHeight();
         }
      }

      this.elementCountLabel.setText("Elemente: " + w * h);
      this.glbControl.getModel().gl_resize(w, h);
   }

   private void minimizeButtonActionPerformed(ActionEvent evt) {
      this.glbControl.minimize();
   }

   private void nHorizSpinnerStateChanged(ChangeEvent evt) {
      this.glbControl.getModel().gleisbildextend.setNachbarbahnsteigLookupWidth(this.nHorizSpinner.getValue());
   }

   private void nVertSpinnerStateChanged(ChangeEvent evt) {
      this.glbControl.getModel().gleisbildextend.setNachbarbahnsteighoriz(this.nVertSpinner.getValue());
   }
}
