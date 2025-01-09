package js.java.tools.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.AbstractDocument.Content;

public class BoundedPlainDocument extends PlainDocument {
   protected BoundedPlainDocument.InsertErrorListener errorListener;
   protected int maxLength;

   public BoundedPlainDocument() {
      this.maxLength = 0;
   }

   public BoundedPlainDocument(int maxLength) {
      this.maxLength = maxLength;
   }

   public BoundedPlainDocument(Content content, int maxLength) {
      super(content);
      if (content.length() > maxLength) {
         throw new IllegalArgumentException("Initial content larger than maximum size");
      } else {
         this.maxLength = maxLength;
      }
   }

   public void setMaxLength(int maxLength) {
      if (this.getLength() > maxLength) {
         throw new IllegalArgumentException("Current content larger than new maximum size");
      } else {
         this.maxLength = maxLength;
      }
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
      if (str != null) {
         int capacity = this.maxLength + 1 - this.getContent().length();
         if (capacity >= str.length()) {
            super.insertString(offset, str, a);
         } else {
            if (capacity > 0) {
               super.insertString(offset, str.substring(0, capacity), a);
            }

            if (this.errorListener != null) {
               this.errorListener.insertFailed(this, offset, str, a);
            }
         }
      }
   }

   public void addInsertErrorListener(BoundedPlainDocument.InsertErrorListener l) {
      if (this.errorListener == null) {
         this.errorListener = l;
      } else {
         throw new IllegalArgumentException("InsertErrorListener already registered");
      }
   }

   public void removeInsertErrorListener(BoundedPlainDocument.InsertErrorListener l) {
      if (this.errorListener == l) {
         this.errorListener = null;
      }
   }

   public interface InsertErrorListener {
      void insertFailed(BoundedPlainDocument var1, int var2, String var3, AttributeSet var4);
   }
}
