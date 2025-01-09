package js.java.tools.gui;

import javax.swing.JTextField;
import javax.swing.text.Document;
import js.java.tools.NumberCheckerDocument;

public class NumberTextField extends JTextField {
   protected Document createDefaultModel() {
      return new NumberCheckerDocument();
   }

   public NumberTextField(int cols) {
      super(cols);
   }

   public NumberTextField() {
   }

   public int getInt() {
      String t = this.getText();
      int r = 0;

      try {
         r = Integer.parseInt(t);
      } catch (NumberFormatException var4) {
         r = 0;
      }

      return r;
   }

   public void setInt(int i) {
      this.setText("" + i);
   }
}
