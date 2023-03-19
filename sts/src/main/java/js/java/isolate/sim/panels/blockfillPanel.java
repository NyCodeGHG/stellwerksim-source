package js.java.isolate.sim.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecGBlockSelect;
import js.java.tools.actions.AbstractEvent;

public class blockfillPanel extends basePanel {
   private JButton clearButton;
   private JButton fillButton;

   public blockfillPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof GecSelectEvent) {
         this.blockEnabled(((gecGBlockSelect)this.glbControl.getMode()).hasBlock());
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.blockEnabled(((gecGBlockSelect)gec).hasBlock());
      gec.addChangeListener(this);
   }

   private void blockEnabled(boolean e) {
      this.fillButton.setEnabled(e);
      this.clearButton.setEnabled(e);
   }

   private void initComponents() {
      this.clearButton = new JButton();
      this.fillButton = new JButton();
      this.setBorder(BorderFactory.createTitledBorder("Blockinhalt"));
      this.setLayout(new GridBagLayout());
      this.clearButton.setText("Inhalt löschen");
      this.clearButton.setFocusPainted(false);
      this.clearButton.setFocusable(false);
      this.clearButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            blockfillPanel.this.clearButtonActionPerformed(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.add(this.clearButton, gridBagConstraints);
      this.fillButton.setText("mit gewähltem Element füllen");
      this.fillButton.setFocusPainted(false);
      this.fillButton.setFocusable(false);
      this.fillButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            blockfillPanel.this.fillButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.add(this.fillButton, gridBagConstraints);
   }

   private void clearButtonActionPerformed(ActionEvent evt) {
      ((gecGBlockSelect)this.glbControl.getMode()).clearblock();
   }

   private void fillButtonActionPerformed(ActionEvent evt) {
      ((gecGBlockSelect)this.glbControl.getMode()).fillblock();
   }
}
