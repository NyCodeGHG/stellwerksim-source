package js.java.isolate.statusapplet.players;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

@Deprecated
public class enablePanel extends JPanel {
   private String text;
   private ActionListener call;
   private JButton enableButton;

   public enablePanel(String text, ActionListener call) {
      this.text = text;
      this.call = call;
      this.initComponents();
   }

   private void initComponents() {
      this.enableButton = new JButton();
      this.enableButton.setText(this.getText());
      this.enableButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            enablePanel.this.enableButtonActionPerformed(evt);
         }
      });
      this.add(this.enableButton);
   }

   private void enableButtonActionPerformed(ActionEvent evt) {
      this.getParent().remove(this);
      this.call.actionPerformed(null);
   }

   String getText() {
      return this.text;
   }
}
