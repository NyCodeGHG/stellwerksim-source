package js.java.isolate.sim.sim.botcom;

import java.net.InetAddress;
import java.util.Set;
import js.java.isolate.sim.structServ.structinfo;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.chatcomng.ChannelsNameParser;

public interface chatInterface extends structinfo {
   String MYSELFNICK = "myselfnickAndI";

   Set<ChannelsNameParser.ChannelName> channelsSet();

   void quit();

   void sendStatus(BOTCOMMAND var1, String var2);

   void sendStatus(BOTCOMMAND var1, int var2);

   void sendStatusToChannel(String var1, String var2);

   void sendXmlStatusToChannel(String var1, Object var2);

   void quitBot();

   void kick();

   void sendStatusToUser(String var1, String var2);

   InetAddress getLocalAddress();

   void sendMemo(String var1);

   void sendText(String var1, String var2);

   void sendAction(String var1, String var2);

   String findMatchingChannelName(String var1);

   boolean isConnected();

   void refreshOutput();
}
