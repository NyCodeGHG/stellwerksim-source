package js.java.tools.logging;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class LimitLinesDocumentListener implements DocumentListener {
   private int maximumLines;
   private boolean isRemoveFromStart;

   public LimitLinesDocumentListener(int maximumLines) {
      this(maximumLines, true);
   }

   public LimitLinesDocumentListener(int maximumLines, boolean isRemoveFromStart) {
      this.setLimitLines(maximumLines);
      this.isRemoveFromStart = isRemoveFromStart;
   }

   public int getLimitLines() {
      return this.maximumLines;
   }

   public void setLimitLines(int maximumLines) {
      if (maximumLines < 1) {
         String message = "Maximum lines must be greater than 0";
         throw new IllegalArgumentException(message);
      } else {
         this.maximumLines = maximumLines;
      }
   }

   public void insertUpdate(final DocumentEvent e) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            LimitLinesDocumentListener.this.removeLines(e);
         }
      });
   }

   public void removeUpdate(DocumentEvent e) {
   }

   public void changedUpdate(DocumentEvent e) {
   }

   private void removeLines(DocumentEvent e) {
      Document document = e.getDocument();
      Element root = document.getDefaultRootElement();

      while (root.getElementCount() > this.maximumLines) {
         if (this.isRemoveFromStart) {
            this.removeFromStart(document, root);
         } else {
            this.removeFromEnd(document, root);
         }
      }
   }

   private void removeFromStart(Document document, Element root) {
      Element line = root.getElement(0);
      int end = line.getEndOffset();

      try {
         document.remove(0, end);
      } catch (BadLocationException var6) {
         System.out.println(var6);
      }
   }

   private void removeFromEnd(Document document, Element root) {
      Element line = root.getElement(root.getElementCount() - 1);
      int start = line.getStartOffset();
      int end = line.getEndOffset();

      try {
         document.remove(start - 1, end - start);
      } catch (BadLocationException var7) {
         System.out.println(var7);
      }
   }
}
