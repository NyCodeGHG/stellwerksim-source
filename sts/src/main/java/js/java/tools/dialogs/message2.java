package js.java.tools.dialogs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class message2 extends messageDialog {
   public message2(Frame parent, boolean modal, String title, String text) {
      super(parent, modal, title);
      this.setText(text);
      JButton okButton = new JButton("Ok");
      okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            message2.this.doClose(1);
         }
      });
      this.addButton(okButton);
      JButton cancelButton = new JButton("Abbruch");
      cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            message2.this.doClose(0);
         }
      });
      this.addButton(cancelButton);
   }
}
