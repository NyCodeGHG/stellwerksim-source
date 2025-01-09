package js.java.tools.logging;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import js.java.tools.TextHelper;

public class HttpHandler extends Handler {
   private final HttpQueueWriter queue;
   private final int build;

   public HttpHandler(String url, int build) {
      this.build = build;
      this.setLevel(Level.ALL);
      this.queue = new HttpQueueWriter(url);
   }

   public void stop() {
      this.queue.exit();
   }

   public void publish(LogRecord record) {
      if (this.isLoggable(record)) {
         try {
            String msg = this.getFormatter().format(record);
            String kind = "message";
            if (record.getThrown() != null) {
               kind = "exception";
            }

            String classname = record.getSourceClassName();
            msg = "verifier=java667&message="
               + TextHelper.urlEncode(msg)
               + "&classname="
               + TextHelper.urlEncode(classname)
               + "&kind="
               + TextHelper.urlEncode(kind)
               + "&build="
               + this.build;
            this.queue.offer(msg);
         } catch (Exception var5) {
            this.reportError(null, var5, 5);
         }
      }
   }

   public void flush() {
   }

   public void close() throws SecurityException {
   }
}
