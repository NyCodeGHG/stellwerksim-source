package js.java.isolate.sim.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.decor;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.gleisElements.gleisHelper;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.tools.gui.layout.CompactLayout;

public class layerPanel extends basePanel {
   private final HashMap<JCheckBox, layerPanel.elementEntry> boxes = new HashMap();
   private boolean shown = false;
   private JButton allButton;
   private JPanel itemsPanel;
   private JLabel jLabel1;
   private JScrollPane jScrollPane1;
   private JButton noneButton;

   public layerPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.initList();
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.refreshAll();
      this.shown = true;
   }

   @Override
   public void hidden(gecBase gec) {
      this.shown = false;
   }

   private void initList() {
      gleisTypContainer gtc = gleisTypContainer.getInstance();
      this.itemsPanel.setLayout(new CompactLayout(2));

      for(int t : gtc.getTypes()) {
         String typ = gtc.getTypName(t);
         JPanel pan = new JPanel();
         pan.setLayout(new BoxLayout(pan, 3));
         pan.setBorder(BorderFactory.createTitledBorder(typ));

         for(int e : gtc.getTypElements(t)) {
            if (decor.getDecor().typHasLayer2Painter(gleisHelper.findElement(t, e))) {
               String element = gtc.getTypElementName(t, e);
               JCheckBox cb = new JCheckBox(element);
               pan.add(cb);
               cb.addItemListener(new ItemListener() {
                  public void itemStateChanged(ItemEvent e) {
                     if (layerPanel.this.shown) {
                        layerPanel.this.cbChanged(e);
                     }
                  }
               });
               this.boxes.put(cb, new layerPanel.elementEntry(t, e, cb));
            }
         }

         if (pan.getComponentCount() > 0) {
            this.itemsPanel.add(pan);
         }
      }
   }

   private void cbChanged(ItemEvent e) {
      layerPanel.elementEntry ee = (layerPanel.elementEntry)this.boxes.get((JCheckBox)e.getItem());
      this.glbControl.getModel().setLayerDisabled(gleisHelper.findElement(ee.typ, ee.element), ee.cb.isSelected());
      this.glbControl.repaint();
   }

   private void selectAll(boolean selected) {
      for(JCheckBox cb : this.boxes.keySet()) {
         cb.setSelected(selected);
      }
   }

   private void refreshAll() {
      for(layerPanel.elementEntry ee : this.boxes.values()) {
         ee.cb.setSelected(this.glbControl.getModel().isLayerDisabled(gleisHelper.findElement(ee.typ, ee.element)));
      }
   }

   private void initComponents() {
      this.jScrollPane1 = new JScrollPane();
      this.itemsPanel = new JPanel();
      this.jLabel1 = new JLabel();
      this.allButton = new JButton();
      this.noneButton = new JButton();
      this.setBorder(BorderFactory.createTitledBorder("Ebenen schalten"));
      this.setLayout(new GridBagLayout());
      this.itemsPanel.setLayout(null);
      this.jScrollPane1.setViewportView(this.itemsPanel);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.add(this.jScrollPane1, gridBagConstraints);
      this.jLabel1.setText("<html>Einzelne Elementdetails im Editor unsichtbar schalten.</html>");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.add(this.jLabel1, gridBagConstraints);
      this.allButton.setText("alle");
      this.allButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            layerPanel.this.allButtonActionPerformed(evt);
         }
      });
      this.add(this.allButton, new GridBagConstraints());
      this.noneButton.setText("keine");
      this.noneButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            layerPanel.this.noneButtonActionPerformed(evt);
         }
      });
      this.add(this.noneButton, new GridBagConstraints());
   }

   private void allButtonActionPerformed(ActionEvent evt) {
      this.selectAll(true);
   }

   private void noneButtonActionPerformed(ActionEvent evt) {
      this.selectAll(false);
   }

   private static class elementEntry {
      int typ;
      int element;
      JCheckBox cb;

      elementEntry(int t, int e, JCheckBox cb) {
         super();
         this.typ = t;
         this.element = e;
         this.cb = cb;
      }
   }
}
