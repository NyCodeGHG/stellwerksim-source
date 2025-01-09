package js.java.tools.streams;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

public class TextAreaOutputStream extends OutputStream implements ActionListener {
   private static final PrintStream OUT = System.out;
   private static final PrintStream ERR = System.err;
   private final JTextArea outWriter;
   private final OutputStream old;
   private final StringBuffer buffer = new StringBuffer();
   private final Timer updateTimer = new Timer(1000, this);
   private int oldLen = 0;

   private TextAreaOutputStream(JTextArea textArea, OutputStream old) {
      this.outWriter = textArea;
      this.old = old;
      DefaultCaret caret = (DefaultCaret)this.outWriter.getCaret();
      caret.setUpdatePolicy(2);
      this.updateTimer.start();
   }

   public static PrintStream createPrintStream(JTextArea textArea) {
      try {
         return new PrintStream(new TextAreaOutputStream(textArea), false, "UTF-8");
      } catch (UnsupportedEncodingException var2) {
         return null;
      }
   }

   public TextAreaOutputStream(JTextArea textArea) {
      this(textArea, null);
   }

   public static void setNewStdout(JTextArea textArea) {
      System.setOut(new PrintStream(new TextAreaOutputStream(textArea, OUT)));
   }

   public static void restoreStdout() {
      System.setOut(OUT);
   }

   public static void setNewStderr(JTextArea textArea) {
      System.setErr(new PrintStream(new TextAreaOutputStream(textArea, ERR)));
   }

   public static void restoreStderr() {
      System.setErr(ERR);
   }

   public void write(int param) throws IOException {
      this.buffer.append((char)param);
      if (this.old != null) {
         this.old.write(param);
      }
   }

   public void actionPerformed(ActionEvent e) {
      if (this.oldLen != this.buffer.length()) {
         this.outWriter.setText(this.buffer.toString());
         this.oldLen = this.outWriter.getText().length();
      }
   }
}
