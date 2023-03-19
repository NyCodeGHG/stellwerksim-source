package js.java.isolate.sim.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;

public class fwrunPanel extends basePanel {
   private JProgressBar fw_ProgressBar;
   private JButton fw_stopp;
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JPanel statusPanel;

   public fwrunPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.statusPanel.add(new statusPanel(glb, e));
   }

   public void setMaximum(int v) {
      this.fw_ProgressBar.setMaximum(v);
   }

   public void setMinimum(int i) {
      this.fw_ProgressBar.setMinimum(i);
   }

   public void setValue(int v) {
      this.fw_ProgressBar.setValue(v);
   }

   public void setEnabled(boolean b) {
      this.fw_stopp.setEnabled(b);
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.jLabel1 = new JLabel();
      this.fw_ProgressBar = new JProgressBar();
      this.fw_stopp = new JButton();
      this.statusPanel = new JPanel();
      this.setBorder(BorderFactory.createTitledBorder("Fahrstraßenermittlung"));
      this.setLayout(new GridLayout(1, 2));
      this.jPanel1.setLayout(new GridLayout(0, 1));
      this.jLabel1.setFont(this.jLabel1.getFont().deriveFont(this.jLabel1.getFont().getStyle() | 1, (float)(this.jLabel1.getFont().getSize() + 5)));
      this.jLabel1.setHorizontalAlignment(0);
      this.jLabel1.setText("<html>Fahrstraße werden ermittelt, bitte warten...</html>");
      this.jPanel1.add(this.jLabel1);
      this.fw_ProgressBar.setStringPainted(true);
      this.jPanel1.add(this.fw_ProgressBar);
      this.fw_stopp.setText("stopp");
      this.fw_stopp.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fwrunPanel.this.fw_stoppActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.fw_stopp);
      this.add(this.jPanel1);
      this.statusPanel.setLayout(new GridLayout(1, 0));
      this.add(this.statusPanel);
   }

   private void fw_stoppActionPerformed(ActionEvent evt) {
      this.glbControl.stopCalcFahrwege();
   }
}
