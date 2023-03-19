package js.java.tools.dialogs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class message1 extends messageDialog {
   public message1(Frame parent, boolean modal, String title, String text) {
      super(parent, modal, title);
      this.setText(text);
      JButton okButton = new JButton("Ok");
      okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            message1.this.doClose();
         }
      });
      this.addButton(okButton);
   }
}
