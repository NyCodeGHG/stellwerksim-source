package js.java.isolate.sim.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gleisbildWorker.areaFinder;
import js.java.isolate.sim.gleisbild.gleisbildWorker.eaConnectionTracking;
import js.java.isolate.sim.gleisbild.gleisbildWorker.elementConnectorFinder;
import js.java.isolate.sim.gleisbild.gleisbildWorker.emptyLineFinder;
import js.java.tools.actions.AbstractEvent;

public class structureAnalysesPanel extends basePanel {
   private JComboBox areasCB;
   private JComboBox coresCB;
   private JTextArea dotText;
   private JButton eaConnectionButton;
   private JButton findAreasButton;
   private JButton findEmptyButton;
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JScrollPane jScrollPane2;
   private JComboBox reducedVGraph;
   private JButton vGraphButton;

   public structureAnalysesPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
   }

   @Override
   public void shown(String n, gecBase gec) {
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.jPanel1 = new JPanel();
      this.findEmptyButton = new JButton();
      this.findAreasButton = new JButton();
      this.areasCB = new JComboBox();
      this.vGraphButton = new JButton();
      this.reducedVGraph = new JComboBox();
      this.eaConnectionButton = new JButton();
      this.coresCB = new JComboBox();
      this.jScrollPane2 = new JScrollPane();
      this.dotText = new JTextArea();
      this.setBorder(BorderFactory.createTitledBorder("Struktur Analyse"));
      this.jLabel1.setHorizontalAlignment(2);
      this.jLabel1
         .setText(
            "<html>Dies ist ein experimentelles Panel, das einige Analysedaten zum Gleisbild liefert. Was man mit den Daten anfängt, ist jedem selbst überlassen und noch unklar.\n</html>"
         );
      this.jLabel1.setHorizontalTextPosition(2);
      this.jPanel1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
      this.jPanel1.setLayout(new GridBagLayout());
      this.findEmptyButton.setText("leere Zeilen suchen");
      this.findEmptyButton.setFocusPainted(false);
      this.findEmptyButton.setFocusable(false);
      this.findEmptyButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            structureAnalysesPanel.this.findEmptyButtonActionPerformed(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 3;
      this.jPanel1.add(this.findEmptyButton, gridBagConstraints);
      this.findAreasButton.setText("Areale suchen");
      this.findAreasButton.setFocusPainted(false);
      this.findAreasButton.setFocusable(false);
      this.findAreasButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            structureAnalysesPanel.this.findAreasButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 3;
      this.jPanel1.add(this.findAreasButton, gridBagConstraints);
      this.areasCB.setFocusable(false);
      this.areasCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            structureAnalysesPanel.this.areasCBItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.areasCB, gridBagConstraints);
      this.vGraphButton.setText("Verknüpfungsgraph");
      this.vGraphButton.setFocusPainted(false);
      this.vGraphButton.setFocusable(false);
      this.vGraphButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            structureAnalysesPanel.this.vGraphButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 3;
      this.jPanel1.add(this.vGraphButton, gridBagConstraints);
      this.reducedVGraph.setModel(new DefaultComboBoxModel(new String[]{"komplett", "Signal-Signal"}));
      this.reducedVGraph.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 1;
      this.jPanel1.add(this.reducedVGraph, gridBagConstraints);
      this.eaConnectionButton.setText("Ein/Ausfahrt-CSV");
      this.eaConnectionButton.setFocusPainted(false);
      this.eaConnectionButton.setFocusable(false);
      this.eaConnectionButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            structureAnalysesPanel.this.eaConnectionButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.eaConnectionButton, gridBagConstraints);
      this.coresCB.setModel(new DefaultComboBoxModel(new String[]{"1", "2", "4", "8"}));
      this.coresCB.setSelectedIndex(2);
      this.coresCB.setToolTipText("Anzahl Threads (CPU-Cores)");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.coresCB, gridBagConstraints);
      this.dotText.setColumns(20);
      this.dotText.setEditable(false);
      this.dotText.setRows(5);
      this.jScrollPane2.setViewportView(this.dotText);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addComponent(this.jScrollPane2, -1, 517, 32767)
            .addComponent(this.jLabel1, -1, 517, 32767)
            .addComponent(this.jPanel1, -1, 517, 32767)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addComponent(this.jLabel1, -2, -1, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jPanel1, -2, -1, -2)
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addComponent(this.jScrollPane2, -1, 118, 32767)
            )
      );
   }

   private void findEmptyButtonActionPerformed(ActionEvent evt) {
      emptyLineFinder gw = new emptyLineFinder(this.glbControl.getModel(), this.my_main);
      gw.findEmptyColRows();
   }

   private void findAreasButtonActionPerformed(ActionEvent evt) {
      this.areasCB.removeAllItems();
      Runnable r = new Runnable() {
         public void run() {
            areaFinder gw = new areaFinder(structureAnalysesPanel.this.glbControl.getModel(), structureAnalysesPanel.this.my_main);
            final LinkedList<Rectangle> rl = gw.getAreas();
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  for(Rectangle r : rl) {
                     structureAnalysesPanel.this.areasCB.addItem(r);
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
            elementConnectorFinder gw = new elementConnectorFinder(structureAnalysesPanel.this.glbControl.getModel(), structureAnalysesPanel.this.my_main);
            elementConnectorFinder.analyser aa;
            switch(structureAnalysesPanel.this.reducedVGraph.getSelectedIndex()) {
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
                  structureAnalysesPanel.this.dotText.setText(dot);
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
            eaConnectionTracking eac = new eaConnectionTracking(
               structureAnalysesPanel.this.glbControl.getModel(),
               structureAnalysesPanel.this.my_main,
               Integer.parseInt(structureAnalysesPanel.this.coresCB.getSelectedItem().toString())
            );
            LinkedList<eaConnectionTracking.eaConnection> result = eac.run();
            StringBuilder res = new StringBuilder();

            for(eaConnectionTracking.eaConnection ea : result) {
               res.append(ea.toString());
               res.append('\n');
            }

            final String txt = res.toString();
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  structureAnalysesPanel.this.dotText.setText(txt);
                  structureAnalysesPanel.this.eaConnectionButton.setEnabled(true);
               }
            });
         }
      };
      Thread t = new Thread(r);
      t.start();
   }
}
