package js.java.schaltungen;

import js.java.schaltungen.audio.AudioController;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.moduleapi.SessionExit;

public interface UserContext extends UserContextMini {
   String getParameter(String var1);

   void overrideModuleClose(SessionExit var1);

   void addCloseObject(SessionClose var1);

   AudioController getAudio();

   void busSubscribe(Object var1);

   void busUnsubscribe(Object var1);

   void busPublish(Object var1);

   void registerMBean(Object var1, String var2);
}
