package js.java.isolate.sim.panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.tools.actions.AbstractEvent;

public class createFWPanel extends basePanel {
   private JButton clearfw_Button;
   private JButton createfw_Button;
   private JButton renewENR_Button;

   public createFWPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      e.registerListener(5, this);
   }

   @Override
   public void action(AbstractEvent e) {
   }

   private void initComponents() {
      this.clearfw_Button = new JButton();
      this.renewENR_Button = new JButton();
      this.createfw_Button = new JButton();
      this.setBorder(BorderFactory.createTitledBorder("Fahrstraßen verwalten"));
      this.setLayout(new GridLayout(0, 1));
      this.clearfw_Button.setText("lösche alle Fahrstraßen...");
      this.clearfw_Button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            createFWPanel.this.clearfw_ButtonActionPerformed(evt);
         }
      });
      this.add(this.clearfw_Button);
      this.renewENR_Button.setText("alle ENRs neu durchnummerieren...");
      this.renewENR_Button.setToolTipText("Ändert alle ENRs.");
      this.renewENR_Button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            createFWPanel.this.renewENR_ButtonActionPerformed(evt);
         }
      });
      this.add(this.renewENR_Button);
      this.createfw_Button.setFont(this.createfw_Button.getFont().deriveFont(this.createfw_Button.getFont().getStyle() | 1));
      this.createfw_Button.setText("ermittle Fahrstraßen");
      this.createfw_Button.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            createFWPanel.this.createfw_ButtonActionPerformed(evt);
         }
      });
      this.add(this.createfw_Button);
   }

   private void clearfw_ButtonActionPerformed(ActionEvent evt) {
      this.my_main.setPanelInvisible(true);
      int r = JOptionPane.showConfirmDialog(this, "<html>Wirklich alle Fahrstraßen löschen?</html>", "Wirklich löschen?", 0, 2);
      this.my_main.setPanelInvisible(false);
      if (r == 0) {
         this.glbControl.getModel().clearFahrwege();
         this.my_main.FSchanged(null);
      }
   }

   private void createfw_ButtonActionPerformed(ActionEvent evt) {
      this.my_main.FSchanged(null);
      this.glbControl.calcFahrwege();
   }

   private void renewENR_ButtonActionPerformed(ActionEvent evt) {
      this.my_main.setPanelInvisible(true);
      int r = JOptionPane.showConfirmDialog(
         this,
         "<html><b>Achtung! Dies ändert ebenfalls die ENRs der<br>Ein- und Ausfahrten und löscht alle Fahrstraßen!</b><br>Damit könnten Änderungen an den Fahrplänen nötig sein.<br><br>Eine Säuberung der ENRs findet auch beim Fahrstraßenbau<br>statt (jedoch ohne Ein- und Ausfahrten zu ändern) und muss<br>deshalb nur bei Fehlern ausgeführt werden.</html>",
         "Wirklich ausführen?",
         0,
         2
      );
      this.my_main.setPanelInvisible(false);
      if (r == 0) {
         this.my_main.FSchanged(null);
         this.glbControl.renewAllENR();
      }
   }
}
