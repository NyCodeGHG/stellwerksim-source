package js.java.tools.streams;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.text.DefaultCaret;

public class TextAreaWriter extends StringWriter implements ActionListener {
   private final JTextArea outWriter;
   private Timer updateTimer = new Timer(1000, this);
   private int oldLen = 0;

   private TextAreaWriter(JTextArea textArea) {
      super();
      this.outWriter = textArea;
      DefaultCaret caret = (DefaultCaret)this.outWriter.getCaret();
      caret.setUpdatePolicy(2);
      this.updateTimer.start();
   }

   public static PrintWriter createPrintStream(JTextArea textArea) {
      return new PrintWriter(new TextAreaWriter(textArea));
   }

   public void close() {
   }

   public void actionPerformed(ActionEvent e) {
      if (this.oldLen != this.getBuffer().length()) {
         this.outWriter.setText(this.toString());
         this.oldLen = this.outWriter.getText().length();
      }
   }
}
