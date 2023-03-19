package js.java.tools.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.SwingUtilities;

public class DialogHandler extends Handler {
   public DialogHandler() {
      super();
      this.setLevel(Level.ALL);
   }

   public void publish(final LogRecord record) {
      if (this.isLoggable(record)) {
         if (record.getThrown() != null) {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                  new ExceptionDialog().handle(record.getThrown());
               }
            });
         }
      }
   }

   public void flush() {
   }

   public void close() throws SecurityException {
   }
}
