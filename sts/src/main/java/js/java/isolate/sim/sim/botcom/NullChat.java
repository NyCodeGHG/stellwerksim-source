package js.java.isolate.sim.sim.botcom;

import java.net.InetAddress;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.chatcomng.ChannelsNameParser;

public class NullChat implements chatInterface {
   @Override
   public Set<ChannelsNameParser.ChannelName> channelsSet() {
      return new TreeSet();
   }

   @Override
   public void quit() {
   }

   @Override
   public void quitBot() {
   }

   @Override
   public void kick() {
   }

   @Override
   public InetAddress getLocalAddress() {
      return null;
   }

   @Override
   public void sendMemo(String message) {
   }

   @Override
   public void sendText(String channel, String message) {
   }

   @Override
   public void sendStatus(BOTCOMMAND command, int message) {
   }

   @Override
   public void sendAction(String chn, String msg) {
   }

   @Override
   public String findMatchingChannelName(String txt) {
      return "";
   }

   @Override
   public Vector getStructure() {
      return new Vector();
   }

   @Override
   public String getStructName() {
      return "NullChat";
   }

   @Override
   public boolean isConnected() {
      return false;
   }

   @Override
   public void refreshOutput() {
   }

   @Override
   public void sendStatus(BOTCOMMAND command, String message) {
   }

   @Override
   public void sendStatusToChannel(String channel, String message) {
   }

   @Override
   public void sendXmlStatusToChannel(String channel, Object message) {
   }

   @Override
   public void sendStatusToUser(String to, String message) {
   }
}
