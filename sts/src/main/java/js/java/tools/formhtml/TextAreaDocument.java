package js.java.tools.formhtml;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class TextAreaDocument extends PlainDocument {
   String initialText;

   TextAreaDocument() {
      super();
   }

   void reset() {
      try {
         this.remove(0, this.getLength());
         if (this.initialText != null) {
            this.insertString(0, this.initialText, null);
         }
      } catch (BadLocationException var2) {
      }
   }

   void storeInitialText() {
      try {
         this.initialText = this.getText(0, this.getLength());
      } catch (BadLocationException var2) {
      }
   }
}
