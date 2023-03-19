package js.java.isolate.sim.panels;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;

public class dummyPanel extends basePanel {
   public dummyPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
   }

   @Override
   public void shown(String n, gecBase gec) {
   }

   private void initComponents() {
      this.setBorder(BorderFactory.createTitledBorder("Dummy"));
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 384, 32767));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addGap(0, 268, 32767));
   }
}
