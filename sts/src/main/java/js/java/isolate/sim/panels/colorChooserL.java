package js.java.isolate.sim.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.panels.actionevents.colorChooserColorEvent;
import js.java.tools.ColorText;

public class colorChooserL extends colorPanel {
   public colorChooserL(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
   }

   @Override
   protected AbstractButton createButton(ColorText c) {
      JToggleButton b = new JToggleButton();
      b.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            String a = ((JToggleButton)e.getSource()).getActionCommand();
            colorChooserL.this.my_main.interPanelCom(new colorChooserColorEvent(a));
         }
      });
      this.colbg.add(b);
      return b;
   }

   private void initComponents() {
      this.setBorder(BorderFactory.createTitledBorder("Hintergrundfarben"));
      this.setLayout(null);
   }
}
