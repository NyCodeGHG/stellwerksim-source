package js.java.isolate.sim.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gleisbildModelStore;
import js.java.isolate.sim.gleisbild.scaleHolder;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gleisbildWorker.areaFinder;
import js.java.isolate.sim.gleisbild.gleisbildWorker.eaConnectionTracking;
import js.java.isolate.sim.gleisbild.gleisbildWorker.elementConnectorFinder;
import js.java.isolate.sim.gleisbild.gleisbildWorker.emptyLineFinder;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.layout.ColumnLayout;

public class faultPanelR extends basePanel implements SessionClose {
   private JTextField aidField;
   private JComboBox areasCB;
   private JTextArea dotText;
   private JButton eaConnectionButton;
   private JButton findAreasButton;
   private JButton findEmptyButton;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JButton loadButton;
   private JComboBox reducedVGraph;
   private JPanel scalePanel;
   private JButton vGraphButton;

   public faultPanelR(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      ButtonGroup gp = new ButtonGroup();
      this.scalePanel.setLayout(new ColumnLayout(2));

      for (String scale : scaleHolder.possibleScales) {
         this.addScale(gp, scale);
      }

      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
   }

   @Override
   public void shown(String n, gecBase gec) {
   }

   @Override
   public void close() {
   }

   private void addScale(ButtonGroup gp, String s) {
      JRadioButton b = new JRadioButton(s + " %");
      gp.add(b);
      this.scalePanel.add(b);
      b.setActionCommand(s);
      b.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            faultPanelR.this.setScale(e);
         }
      });
   }

   private void setScale(ItemEvent evt) {
      String cmd = ((JRadioButton)evt.getSource()).getActionCommand();
      this.glbControl.setScalePreset(cmd);
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.jPanel2 = new JPanel();
      this.scalePanel = new JPanel();
      this.jPanel1 = new JPanel();
      this.aidField = new JTextField();
      this.loadButton = new JButton();
      this.findEmptyButton = new JButton();
      this.findAreasButton = new JButton();
      this.areasCB = new JComboBox();
      this.vGraphButton = new JButton();
      this.reducedVGraph = new JComboBox();
      this.eaConnectionButton = new JButton();
      this.jScrollPane2 = new JScrollPane();
      this.dotText = new JTextArea();
      this.setBorder(BorderFactory.createTitledBorder("Fehler-Tester"));
      this.setLayout(new BoxLayout(this, 1));
      this.jScrollPane1.setBorder(null);
      this.jPanel2.setLayout(new BoxLayout(this.jPanel2, 1));
      this.scalePanel.setLayout(null);
      this.jPanel2.add(this.scalePanel);
      this.jPanel1.setLayout(new GridBagLayout());
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.aidField, gridBagConstraints);
      this.loadButton.setText("laden");
      this.loadButton.setFocusable(false);
      this.loadButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelR.this.loadButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.loadButton, gridBagConstraints);
      this.findEmptyButton.setText("leere Zeilen suchen");
      this.findEmptyButton.setFocusable(false);
      this.findEmptyButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelR.this.findEmptyButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 13;
      this.jPanel1.add(this.findEmptyButton, gridBagConstraints);
      this.findAreasButton.setText("Areas suchen");
      this.findAreasButton.setFocusable(false);
      this.findAreasButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelR.this.findAreasButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.findAreasButton, gridBagConstraints);
      this.areasCB.setFocusable(false);
      this.areasCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            faultPanelR.this.areasCBItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.areasCB, gridBagConstraints);
      this.vGraphButton.setText("Verknüpfungsgraph");
      this.vGraphButton.setFocusable(false);
      this.vGraphButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelR.this.vGraphButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      this.jPanel1.add(this.vGraphButton, gridBagConstraints);
      this.reducedVGraph.setModel(new DefaultComboBoxModel(new String[]{"komplett", "Signal-Signal"}));
      this.reducedVGraph.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.reducedVGraph, gridBagConstraints);
      this.eaConnectionButton.setText("Ein/Ausfahrt-CSV");
      this.eaConnectionButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelR.this.eaConnectionButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 13;
      this.jPanel1.add(this.eaConnectionButton, gridBagConstraints);
      this.jPanel2.add(this.jPanel1);
      this.dotText.setColumns(20);
      this.dotText.setEditable(false);
      this.dotText.setRows(5);
      this.jScrollPane2.setViewportView(this.dotText);
      this.jPanel2.add(this.jScrollPane2);
      this.jScrollPane1.setViewportView(this.jPanel2);
      this.add(this.jScrollPane1);
   }

   private void loadButtonActionPerformed(ActionEvent evt) {
      this.my_main.setGUIEnable(false);
      this.my_main.setPanelInvisible(true);
      this.glbControl.getModel().load(this.my_main.getParameter("anlagenlesenPRE") + this.aidField.getText(), new gleisbildModelStore.ioDoneMessage() {
         @Override
         public void done(boolean success) {
            faultPanelR.this.my_main.setGUIEnable(true);
         }
      });
   }

   private void findEmptyButtonActionPerformed(ActionEvent evt) {
      emptyLineFinder gw = new emptyLineFinder(this.glbControl.getModel(), this.my_main);
      gw.findEmptyColRows();
   }

   private void findAreasButtonActionPerformed(ActionEvent evt) {
      this.areasCB.removeAllItems();
      Runnable r = new Runnable() {
         public void run() {
            areaFinder gw = new areaFinder(faultPanelR.this.glbControl.getModel(), faultPanelR.this.my_main);
            final LinkedList<Rectangle> rl = gw.getAreas();
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  for (Rectangle r : rl) {
                     faultPanelR.this.areasCB.addItem(r);
                  }
               }
            });
         }
      };
      Thread t = new Thread(r);
      t.start();
   }

   private void areasCBItemStateChanged(ItemEvent evt) {
      this.glbControl.getModel().clearMarkedGleis();
      areaFinder gw = new areaFinder(this.glbControl.getModel(), this.my_main);
      Rectangle r = (Rectangle)this.areasCB.getSelectedItem();
      if (r != null) {
         LinkedList<gleis> m = new LinkedList();
         gw.markLines(r, m);
         this.glbControl.getModel().addMarkedGleis(m);
      }
   }

   private void vGraphButtonActionPerformed(ActionEvent evt) {
      this.dotText.setText("");
      Runnable r = new Runnable() {
         public void run() {
            elementConnectorFinder gw = new elementConnectorFinder(faultPanelR.this.glbControl.getModel(), faultPanelR.this.my_main);
            elementConnectorFinder.analyser aa;
            switch (faultPanelR.this.reducedVGraph.getSelectedIndex()) {
               case 0:
               default:
                  aa = new elementConnectorFinder.fullAnalyser();
                  break;
               case 1:
                  aa = new elementConnectorFinder.signal2signalAnalyser();
            }

            gw.run(aa);
            final String dot = gw.getFormatter().getDotText();
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  faultPanelR.this.dotText.setText(dot);
               }
            });
         }
      };
      Thread t = new Thread(r);
      t.start();
   }

   private void eaConnectionButtonActionPerformed(ActionEvent evt) {
      this.dotText.setText("");
      this.eaConnectionButton.setEnabled(false);
      Runnable r = new Runnable() {
         public void run() {
            eaConnectionTracking eac = new eaConnectionTracking(faultPanelR.this.glbControl.getModel(), faultPanelR.this.my_main);
            LinkedList<eaConnectionTracking.eaConnection> result = eac.run();
            StringBuilder res = new StringBuilder();

            for (eaConnectionTracking.eaConnection ea : result) {
               res.append(ea.toString());
               res.append('\n');
            }

            final String txt = res.toString();
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  faultPanelR.this.dotText.setText(txt);
                  faultPanelR.this.eaConnectionButton.setEnabled(true);
               }
            });
         }
      };
      Thread t = new Thread(r);
      t.start();
   }
}
