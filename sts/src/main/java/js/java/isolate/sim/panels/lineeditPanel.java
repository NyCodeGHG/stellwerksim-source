package js.java.isolate.sim.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecLineSelect;
import js.java.tools.actions.AbstractEvent;

public class lineeditPanel extends basePanel {
   private JButton delButton;
   private JButton insertButton;

   public lineeditPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof GecSelectEvent) {
         this.rcEnabled(true);
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      if (n.equals("column")) {
         ((TitledBorder)this.getBorder()).setTitle("Spalte");
      } else if (n.equals("row")) {
         ((TitledBorder)this.getBorder()).setTitle("Zeile");
      }

      gec.addChangeListener(this);
      this.rcEnabled(false);
   }

   private void rcEnabled(boolean e) {
      this.delButton.setEnabled(e);
      this.insertButton.setEnabled(e);
   }

   private void initComponents() {
      this.insertButton = new JButton();
      this.delButton = new JButton();
      this.setBorder(BorderFactory.createTitledBorder("Zeile/Spalte"));
      this.setLayout(new GridBagLayout());
      this.insertButton.setText("leere einfügen vor");
      this.insertButton.setFocusPainted(false);
      this.insertButton.setFocusable(false);
      this.insertButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            lineeditPanel.this.insertButtonActionPerformed(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.add(this.insertButton, gridBagConstraints);
      this.delButton.setText("löschen");
      this.delButton.setFocusPainted(false);
      this.delButton.setFocusable(false);
      this.delButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            lineeditPanel.this.delButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.add(this.delButton, gridBagConstraints);
   }

   private void delButtonActionPerformed(ActionEvent evt) {
      this.rcEnabled(false);
      ((gecLineSelect)this.glbControl.getMode()).delete();
   }

   private void insertButtonActionPerformed(ActionEvent evt) {
      this.rcEnabled(false);
      ((gecLineSelect)this.glbControl.getMode()).insert();
   }
}
