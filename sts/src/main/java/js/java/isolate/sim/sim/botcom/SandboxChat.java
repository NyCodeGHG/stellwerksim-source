package js.java.isolate.sim.sim.botcom;

import de.deltaga.eb.EventHandler;
import java.net.InetAddress;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.chatcomng.ChannelsNameParser;
import js.java.schaltungen.chatcomng.ChatMessageEvent;
import js.java.schaltungen.chatcomng.GameInfoEvent;
import js.java.schaltungen.chatcomng.PrivateMessageEvent;
import js.java.schaltungen.chatcomng.RoomState;
import js.java.schaltungen.moduleapi.SessionClose;

public class SandboxChat implements chatInterface, SessionClose {
   private final UserContext uc;
   private final BotCalling client;

   public SandboxChat(BotCalling client, UserContext uc) {
      this.client = client;
      this.uc = uc;
      uc.addCloseObject(this);
      uc.busSubscribe(this);
      uc.getChat().setRoomState(uc, RoomState.SANDBOXGAME);
   }

   @EventHandler
   public void privMsg(PrivateMessageEvent event) {
      if (BotChat.isDebug()) {
         BotChat.getDebugMode().writeln("other msg " + event.sender + "," + event.text);
      }

      this.client.handleIRC(event.sender, event.text, false);
   }

   @EventHandler
   public void message(ChatMessageEvent event) {
      this.client.handleIRC("myselfnickAndI", event.msg, false);
   }

   @Override
   public void close() {
      this.uc.getChat().setRoomState(this.uc, RoomState.READYROOM);
   }

   @Override
   public Set<ChannelsNameParser.ChannelName> channelsSet() {
      return new TreeSet();
   }

   @Override
   public void quit() {
      this.uc.busPublish(new GameInfoEvent());
      this.sendStatus(BOTCOMMAND.LEAVING, "");
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
      return "SandboxChat";
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
