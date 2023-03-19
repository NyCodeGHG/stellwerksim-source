package js.java.isolate.sim.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.colorSystem.gleisColor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.colorChooserColorEvent;
import js.java.isolate.sim.panels.actionevents.colorModifiedEvent;
import js.java.tools.actions.AbstractEvent;

public class colorChooserR extends basePanel {
   private String selectedColor = "";
   private JColorChooser colorChooser;
   private JLabel jLabel1;
   private JScrollPane jScrollPane1;
   private JButton restoreButton;
   private JButton setButton;

   public colorChooserR(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.setButton.setEnabled(false);
      this.restoreButton.setEnabled(false);
      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof colorChooserColorEvent) {
         TreeMap<String, Color> c = gleisColor.getInstance().getBGcolors();
         colorChooserColorEvent ccce = (colorChooserColorEvent)e;
         this.selectedColor = ccce.getColor();
         this.colorChooser.setColor((Color)c.get(this.selectedColor));
         this.setButton.setEnabled(true);
         this.restoreButton.setEnabled(true);
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
   }

   private void initComponents() {
      this.setButton = new JButton();
      this.restoreButton = new JButton();
      this.jLabel1 = new JLabel();
      this.jScrollPane1 = new JScrollPane();
      this.colorChooser = new JColorChooser();
      this.setBorder(BorderFactory.createTitledBorder("Farbemischung"));
      this.setLayout(new GridBagLayout());
      this.setButton.setText("setzen");
      this.setButton.setFocusable(false);
      this.setButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            colorChooserR.this.setButtonActionPerformed(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 17;
      this.add(this.setButton, gridBagConstraints);
      this.restoreButton.setText("auf Standard");
      this.restoreButton.setFocusable(false);
      this.restoreButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            colorChooserR.this.restoreButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 13;
      this.add(this.restoreButton, gridBagConstraints);
      this.jLabel1.setText("<html>Dies ist nur eine Fallstudie, die Werte werden <b>nicht</b> gespeichert!<br>(Bare en test, ikke gemt!)</html>");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 5, 0, 5);
      this.add(this.jLabel1, gridBagConstraints);
      this.jScrollPane1.setViewportView(this.colorChooser);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.add(this.jScrollPane1, gridBagConstraints);
   }

   private void restoreButtonActionPerformed(ActionEvent evt) {
      gleisColor.getInstance().restoreDefaultColor(this.selectedColor);
      this.my_main.interPanelCom(new colorModifiedEvent(this.selectedColor));
      this.glbControl.repaint();
   }

   private void setButtonActionPerformed(ActionEvent evt) {
      gleisColor.getInstance().changeColor(this.selectedColor, this.colorChooser.getColor());
      this.my_main.interPanelCom(new colorModifiedEvent(this.selectedColor));
      this.glbControl.repaint();
   }
}
