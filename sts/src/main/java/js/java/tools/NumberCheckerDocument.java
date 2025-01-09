package js.java.tools;

import javax.swing.text.PlainDocument;

public class NumberCheckerDocument extends PlainDocument {
   public NumberCheckerDocument() {
      this.setDocumentFilter(new NumberDocumentFilter());
   }
}
