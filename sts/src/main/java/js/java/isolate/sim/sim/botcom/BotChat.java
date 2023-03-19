package js.java.isolate.sim.sim.botcom;

import de.deltaga.eb.EventHandler;
import java.net.InetAddress;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import js.java.isolate.sim.FATwriter;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.chatcomng.ChannelMessageEvent;
import js.java.schaltungen.chatcomng.ChannelsNameParser;
import js.java.schaltungen.chatcomng.ChatMessageEvent;
import js.java.schaltungen.chatcomng.ExchangeMessageEvent;
import js.java.schaltungen.chatcomng.GameInfoEvent;
import js.java.schaltungen.chatcomng.PrivateMessageEvent;
import js.java.schaltungen.chatcomng.PublicControlMessage;
import js.java.schaltungen.chatcomng.RoomState;
import js.java.schaltungen.chatcomng.SimControlMessage;
import js.java.schaltungen.moduleapi.SessionClose;

public class BotChat extends XmlBotChat implements chatInterface, SessionClose {
   private static FATwriter debugMode = null;
   private final BotCalling client;
   private final String updatestring;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   static FATwriter getDebugMode() {
      return debugMode;
   }

   public BotChat(BotCalling client, UserContext uc, String updatestring) {
      super(uc);
      this.client = client;
      this.updatestring = updatestring.trim();
      uc.addCloseObject(this);
      uc.busSubscribe(this);
      uc.getChat().setRoomState(uc, RoomState.ONLINEGAME);
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

         try {
            this.client.handleIRCresult(cmd, res, val, event.ispublic);
         } catch (Exception var9) {
         }
      }
   }

   @EventHandler
   public void controlEvent(PublicControlMessage event) {
      if (isDebug()) {
         getDebugMode().writeln("other msg " + event.sender + "," + event.text);
      }
   }

   @EventHandler
   public void privMsg(ChannelMessageEvent event) {
      if (isDebug()) {
         getDebugMode().writeln("other msg " + event.sender + "," + event.text);
      }

      this.client.checkAutoMsg(event.sender.getName(), event.channelname, event.text);
   }

   @EventHandler
   public void privMsg(PrivateMessageEvent event) {
      if (isDebug()) {
         getDebugMode().writeln("other msg " + event.sender + "," + event.text);
      }

      this.client.handleIRC(event.sender, event.text, false);
   }

   @EventHandler
   public void message(ChatMessageEvent event) {
      this.client.handleIRC("myselfnickAndI", event.msg, false);
   }

   @EventHandler
   public void simExchange(ExchangeMessageEvent event) {
      this.publishAsEvent(event.text);
   }

   private void send(BOTCOMMAND command, String text) {
      this.send(this.uc.getControlBot(), command.command + " " + this.updatestring + " " + text.trim());
   }

   @Override
   public Set<ChannelsNameParser.ChannelName> channelsSet() {
      Set<ChannelsNameParser.ChannelName> ret = new TreeSet();
      String morechannels = this.uc.getParameter("dchannel");
      if (morechannels != null) {
         ret = new ChannelsNameParser(morechannels, 1).asSet();
      }

      return ret;
   }

   @Override
   public void quit() {
      this.uc.busPublish(new GameInfoEvent());
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
   public void sendStatusToUser(String to, String message) {
      this.send(to, message);
   }

   @Override
   public void quitBot() {
      this.quit();
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
      this.send(channel, message);
   }

   @Override
   public void sendAction(String chn, String msg) {
   }

   @Override
   public String findMatchingChannelName(String txt) {
      for(ChannelsNameParser.ChannelName cn : this.channelsSet()) {
         if (cn.title.equalsIgnoreCase(txt)) {
            return cn.name;
         }
      }

      return null;
   }

   @Override
   public Vector getStructure() {
      return new Vector();
   }

   @Override
   public String getStructName() {
      return "BotChat";
   }

   @Override
   public boolean isConnected() {
      return false;
   }

   @Override
   public void refreshOutput() {
   }
}
