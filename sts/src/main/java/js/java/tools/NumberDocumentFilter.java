package js.java.tools;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

public class NumberDocumentFilter extends DocumentFilter {
   public NumberDocumentFilter() {
      super();
   }

   private String filter(String string) {
      StringBuilder s = new StringBuilder();

      for(int i = 0; i < string.length(); ++i) {
         char c = string.charAt(i);
         if (Character.isDigit(c)) {
            s.append(c);
         }
      }

      return s.toString();
   }

   public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
      super.insertString(fb, offset, this.filter(text), attr);
   }

   public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
      super.replace(fb, offset, length, this.filter(text), attrs);
   }
}
