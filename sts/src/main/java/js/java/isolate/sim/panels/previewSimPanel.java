package js.java.isolate.sim.panels;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;

public class previewSimPanel extends basePanel {
   private JButton doneButton;

   public previewSimPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
   }

   private void initComponents() {
      this.doneButton = new JButton();
      this.setBorder(BorderFactory.createTitledBorder("Vorschau"));
      this.doneButton.setText("zurück zum Editor");
      this.doneButton.setFocusPainted(false);
      this.doneButton.setFocusable(false);
      this.doneButton.setMargin(new Insets(20, 40, 20, 40));
      this.doneButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            previewSimPanel.this.doneButtonActionPerformed(evt);
         }
      });
      this.add(this.doneButton);
   }

   private void doneButtonActionPerformed(ActionEvent evt) {
      this.glbControl.setSimViewStyle(false);
      this.glbControl.repaint();
      this.my_main.showControlPanel();
   }
}
