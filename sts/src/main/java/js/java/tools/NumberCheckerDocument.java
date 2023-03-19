package js.java.tools;

import javax.swing.text.PlainDocument;

public class NumberCheckerDocument extends PlainDocument {
   public NumberCheckerDocument() {
      super();
      this.setDocumentFilter(new NumberDocumentFilter());
   }
}
