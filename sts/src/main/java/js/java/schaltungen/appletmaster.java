package js.java.schaltungen;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class appletmaster extends simpleappletmaster {
   private static ConcurrentHashMap<String, appletmaster> parallels = new ConcurrentHashMap();
   public static appletmaster main = null;

   protected boolean addMe(String name) {
      if (parallels.containsKey(name)) {
         return false;
      } else {
         parallels.put(name, this);
         return true;
      }
   }

   protected boolean removeMe() {
      for(Entry<String, appletmaster> e : parallels.entrySet()) {
         if (e.getValue() == this) {
            parallels.remove(e.getKey());
            return true;
         }
      }

      return false;
   }

   protected boolean amIrunning(String name) {
      return parallels.containsKey(name);
   }

   @Override
   public void init() {
      super.init();
   }

   public appletmaster() {
      super();
      main = this;
   }

   @Deprecated
   public void setGUIEnable(boolean e) {
   }

   public void exit() {
   }

   @Override
   public void start() {
      super.start();
   }

   @Override
   public void stop() {
      super.stop();
   }

   @Override
   public void destroy() {
      super.destroy();
      this.removeMe();
      main = null;
   }
}
