package js.java.isolate.statusapplet.karte;

import de.deltaga.eb.EventHandler;
import java.net.InetAddress;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.sim.botcom.BotCalling;
import js.java.isolate.sim.sim.botcom.chatInterface;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.chatcomng.ChannelsNameParser;
import js.java.schaltungen.chatcomng.ChatMessageEvent;
import js.java.schaltungen.chatcomng.PrivateMessageEvent;
import js.java.schaltungen.chatcomng.PublicControlMessage;
import js.java.schaltungen.chatcomng.RoomState;
import js.java.schaltungen.chatcomng.SimControlMessage;
import js.java.schaltungen.moduleapi.SessionClose;

public class MapBotChat implements chatInterface, SessionClose {
   private static FATwriter debugMode = null;
   private final BotCalling client;
   private final UserContext uc;
   private final String updateurl;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   static FATwriter getDebugMode() {
      return debugMode;
   }

   public MapBotChat(BotCalling client, UserContext uc, String updateurl) {
      this.client = client;
      this.uc = uc;
      this.updateurl = updateurl;
      uc.addCloseObject(this);
      uc.busSubscribe(this);
      uc.getChat().setRoomState(uc, RoomState.STATUS);
   }

   @Override
   public void close() {
      this.uc.getChat().setRoomState(this.uc, RoomState.READYROOM);
   }

   @EventHandler
   public void botEvent(SimControlMessage event) {
      Pattern p = Pattern.compile("\\[([a-zA-Z\\+]+):([0-9]+)\\](.*)");
      Matcher m = p.matcher(event.msg);
      boolean b = m.matches();
      if (b) {
         String cmd = m.group(1);
         int res = Integer.parseInt(m.group(2));
         String val = m.group(3);
         if (isDebug()) {
            getDebugMode().writeln("bot msg " + cmd + "," + res + "," + val + "," + event.ispublic);
         }

         if (event.instanz >= 0) {
            try {
               this.client.handleIRCresult(cmd, res, val, event.ispublic);
            } catch (Exception var9) {
               System.out.println("Exception: " + var9.getMessage());
               var9.printStackTrace();
            }
         }
      }
   }

   @EventHandler
   public void controlEvent(PublicControlMessage event) {
      if (isDebug()) {
         getDebugMode().writeln("other msg " + event.sender + "," + event.text);
      }

      if (event.instanz >= 0) {
         try {
            this.client.handleIRC(event.sender, event.text, event.ispublic);
         } catch (Exception var3) {
            System.out.println("Exception: " + var3.getMessage());
            var3.printStackTrace();
         }
      }
   }

   @EventHandler
   public void privMsg(PrivateMessageEvent event) {
      if (isDebug()) {
         getDebugMode().writeln("other msg " + event.sender + "," + event.text);
      }
   }

   private void send(String to, String text) {
      ChatMessageEvent event = new ChatMessageEvent(to, text);
      this.uc.busPublish(event);
   }

   @Override
   public Set<ChannelsNameParser.ChannelName> channelsSet() {
      return new TreeSet();
   }

   private void send(BOTCOMMAND command, String text) {
      this.send(this.uc.getControlBot(), command.command + " " + this.updateurl + " " + text.trim());
   }

   @Override
   public void quit() {
      this.sendStatus(BOTCOMMAND.LEAVING, "");
   }

   @Override
   public void sendStatus(BOTCOMMAND command, String message) {
      this.send(command, message);
   }

   @Override
   public void sendStatus(BOTCOMMAND command, int message) {
      this.send(command, Integer.toString(message));
   }

   @Override
   public void sendStatusToChannel(String channel, String message) {
      if (!channel.startsWith("'")) {
         channel = "#" + channel;
      }

      this.send(channel, message);
   }

   @Override
   public void sendXmlStatusToChannel(String channel, Object message) {
   }

   @Override
   public void sendStatusToUser(String to, String message) {
      this.send(to, message);
   }

   @Override
   public void quitBot() {
      this.sendStatus(BOTCOMMAND.LEAVING, "");
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
      return "MapBotChat";
   }

   @Override
   public boolean isConnected() {
      return false;
   }

   @Override
   public void refreshOutput() {
   }
}
