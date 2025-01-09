package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.actions.AbstractListener;

public class basePanel extends JPanel implements AbstractListener {
   protected gleisbildEditorControl glbControl;
   protected stellwerk_editor my_main;

   public basePanel(gleisbildEditorControl glb, stellwerk_editor e) {
      this.glbControl = glb;
      this.my_main = e;
   }

   public void shown(String name, gecBase gec) {
   }

   public void hidden(gecBase gec) {
   }

   private void initComponents() {
      this.setLayout(new BorderLayout());
   }

   public void action(AbstractEvent e) {
   }
}
