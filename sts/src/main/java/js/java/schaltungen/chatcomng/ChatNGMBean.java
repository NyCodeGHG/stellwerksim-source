package js.java.schaltungen.chatcomng;

import java.util.Map;

public interface ChatNGMBean {
   Map<String, String> getKnownUsers();

   Map<String, String> getChannels();

   void disconnect();

   void simulateDisconnect();

   void joinChannel(String var1);

   void leaveChannel(String var1);

   void pingServer();
}
