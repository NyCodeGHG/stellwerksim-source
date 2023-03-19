package js.java.isolate.sim.panels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecGBlockSelect;
import js.java.tools.ColorText;
import js.java.tools.actions.AbstractEvent;

public class blockcolorPanel extends colorPanel {
   @Override
   protected AbstractButton createButton(ColorText c) {
      JButton b = new JButton();
      b.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            String a = ((JButton)e.getSource()).getActionCommand();
            ((gecGBlockSelect)blockcolorPanel.this.glbControl.getMode()).fillValue(gleis.EXTENDS.FARBE, a);
         }
      });
      return b;
   }

   public blockcolorPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
   }

   @Override
   public void action(AbstractEvent e) {
      super.action(e);
      if (e instanceof GecSelectEvent) {
         this.blockEnabled(((gecGBlockSelect)this.glbControl.getMode()).hasBlock());
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.blockEnabled(((gecGBlockSelect)gec).hasBlock());
      gec.addChangeListener(this);
   }

   public void blockEnabled(boolean e) {
      for(Component c : this.getComponents()) {
         c.setEnabled(e);
      }
   }

   private void initComponents() {
      this.setBorder(BorderFactory.createTitledBorder("Hintergrundfarben"));
      this.setLayout(null);
   }
}
