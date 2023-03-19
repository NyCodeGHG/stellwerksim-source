package js.java.isolate.sim.panels;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;

public class emptyPanel extends basePanel {
   private JLabel infoLabel;

   public emptyPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
   }

   @Override
   public void shown(String m, gecBase gec) {
      this.infoLabel.setText("");
   }

   private void initComponents() {
      this.infoLabel = new JLabel();
      this.setBorder(BorderFactory.createTitledBorder(null, "StellwerkSim", 3, 5));
      this.setLayout(new BoxLayout(this, 3));
      this.add(this.infoLabel);
   }
}
