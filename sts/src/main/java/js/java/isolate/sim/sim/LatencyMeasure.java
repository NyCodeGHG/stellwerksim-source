package js.java.isolate.sim.sim;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import js.java.isolate.sim.FATwriter;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.chatcomng.CheckLatencyNowEvent;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.webservice.StoreLatencies;

public class LatencyMeasure implements SessionClose {
   private static FATwriter debug = null;
   private final UserContext uc;
   private final String name;
   private final Map<String, Long> commands = new ConcurrentHashMap();

   public static boolean isDebug() {
      return debug != null;
   }

   public static void setDebug(FATwriter w) {
      debug = w;
   }

   public LatencyMeasure(UserContext uc, String name) {
      this.uc = uc;
      this.name = name;
      uc.addCloseObject(this);
   }

   @Override
   public void close() {
   }

   public void sendingCommand(String params) {
      this.commands.put(params, System.currentTimeMillis());
      this.uc.busPublish(new CheckLatencyNowEvent());
   }

   public void receivingCommand(String params) {
      if (this.commands.containsKey(params)) {
         long l = (Long)this.commands.remove(params);
         int delay = (int)(System.currentTimeMillis() - l);
         this.uc.busPublish(new StoreLatencies(this.name, params, delay));
         if (debug != null) {
            debug.writeln(params + ":" + delay);
         }
      }
   }
}
