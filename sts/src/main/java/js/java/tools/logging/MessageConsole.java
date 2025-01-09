package js.java.tools.logging;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class MessageConsole {
   private JTextComponent textComponent;
   private Document document;
   private boolean isAppend;
   private DocumentListener limitLinesListener;
   private final SimpleAttributeSet timeAttributes;
   private long lastTime = 0L;
   private final String EOL = System.getProperty("line.separator");
   private final SimpleDateFormat sdf = new SimpleDateFormat("d., HH:mm:ss");

   public MessageConsole(JTextComponent textComponent) {
      this(textComponent, true);
   }

   public MessageConsole(JTextComponent textComponent, boolean isAppend) {
      this.textComponent = textComponent;
      this.document = textComponent.getDocument();
      this.isAppend = isAppend;
      textComponent.setEditable(false);
      this.timeAttributes = new SimpleAttributeSet();
      StyleConstants.setForeground(this.timeAttributes, Color.BLUE);
      DefaultCaret caret = (DefaultCaret)textComponent.getCaret();
      caret.setUpdatePolicy(2);
   }

   public void redirectOut() {
      this.redirectOut(null, null);
   }

   public void redirectOut(Color textColor, PrintStream printStream) {
      MessageConsole.ConsoleOutputStream cos = new MessageConsole.ConsoleOutputStream(textColor, printStream);
      System.setOut(new PrintStream(cos, true));
   }

   public void redirectErr() {
      this.redirectErr(null, null);
   }

   public void redirectErr(Color textColor, PrintStream printStream) {
      MessageConsole.ConsoleOutputStream cos = new MessageConsole.ConsoleOutputStream(textColor, printStream);
      System.setErr(new PrintStream(cos, true));
   }

   public void setMessageLines(int lines) {
      if (this.limitLinesListener != null) {
         this.document.removeDocumentListener(this.limitLinesListener);
      }

      this.limitLinesListener = new LimitLinesDocumentListener(lines, this.isAppend);
      this.document.addDocumentListener(this.limitLinesListener);
   }

   public void clear() {
      try {
         this.document.remove(0, this.document.getLength());
      } catch (BadLocationException var2) {
         Logger.getLogger(MessageConsole.class.getName()).log(Level.SEVERE, null, var2);
      }
   }

   private void addTimeLine() {
      if (this.lastTime < System.currentTimeMillis() / 1000L / 60L) {
         try {
            String dline = "\n" + this.sdf.format(new Date()) + "\n";
            int offset = this.document.getLength();
            this.document.insertString(offset, dline, this.timeAttributes);
            this.lastTime = System.currentTimeMillis() / 1000L / 60L;
         } catch (BadLocationException var3) {
         }
      }
   }

   private class ConsoleOutputStream extends ByteArrayOutputStream {
      private SimpleAttributeSet attributes;
      private final PrintStream printStream;
      private final StringBuffer buffer = new StringBuffer(80);
      private boolean isFirstLine;

      public ConsoleOutputStream(Color textColor, PrintStream printStream) {
         if (textColor != null) {
            this.attributes = new SimpleAttributeSet();
            StyleConstants.setForeground(this.attributes, textColor);
         }

         this.printStream = printStream;
         if (MessageConsole.this.isAppend) {
            this.isFirstLine = true;
         }
      }

      public void flush() {
         String message = this.toString();
         if (message.length() != 0) {
            if (MessageConsole.this.isAppend) {
               this.handleAppend(message);
            } else {
               this.handleInsert(message);
            }

            this.reset();
         }
      }

      private void handleAppend(String message) {
         if (MessageConsole.this.document.getLength() == 0) {
            this.buffer.setLength(0);
         }

         this.buffer.append(message);
         this.clearBuffer();
      }

      private void handleInsert(String message) {
         this.buffer.append(message);
         if (MessageConsole.this.EOL.equals(message)) {
            this.clearBuffer();
         }
      }

      private void clearBuffer() {
         if (this.isFirstLine && MessageConsole.this.document.getLength() != 0) {
            this.buffer.insert(0, "\n");
         }

         this.isFirstLine = false;
         String line = this.buffer.toString();
         Runnable r = () -> {
            try {
               if (MessageConsole.this.isAppend) {
                  MessageConsole.this.addTimeLine();
                  int offset = MessageConsole.this.document.getLength();
                  MessageConsole.this.document.insertString(offset, line, this.attributes);
                  MessageConsole.this.textComponent.setCaretPosition(MessageConsole.this.document.getLength());
               } else {
                  MessageConsole.this.document.insertString(0, line, this.attributes);
                  MessageConsole.this.textComponent.setCaretPosition(0);
               }
            } catch (IllegalArgumentException | BadLocationException var3) {
            }
         };
         if (SwingUtilities.isEventDispatchThread()) {
            r.run();
         } else {
            SwingUtilities.invokeLater(r);
         }

         if (this.printStream != null) {
            this.printStream.print(line);
         }

         this.buffer.setLength(0);
      }
   }
}
