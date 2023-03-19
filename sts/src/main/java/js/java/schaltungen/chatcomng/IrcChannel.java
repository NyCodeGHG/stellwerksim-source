package js.java.schaltungen.chatcomng;

import java.util.concurrent.ConcurrentHashMap;
import org.relayirc.chatengine.ChannelEvent;
import org.relayirc.chatengine.ChannelListener;

public class IrcChannel implements ChannelListener {
   public final ChannelsNameParser.ChannelName channel;
   public final boolean userChannel;
   public final ChatNG chat;
   public final ConcurrentHashMap<String, String> userNicks = new ConcurrentHashMap();

   public IrcChannel(ChatNG chat, String name, String title, boolean userChannel) {
      super();
      this.chat = chat;
      if (title == null) {
         throw new IllegalArgumentException("Title is null: " + name);
      } else {
         this.channel = new ChannelsNameParser.ChannelName(name, title, 1);
         this.userChannel = userChannel;
      }
   }

   public IrcChannel(ChatNG chat, ChannelsNameParser.ChannelName name, boolean userChannel) {
      super();
      this.chat = chat;
      this.channel = name;
      this.userChannel = userChannel;
   }

   public String getTopic() {
      return null;
   }

   public void sendPart() {
      this.chat.leaveChannel(this.channel.name);
   }

   public void onActivation(ChannelEvent event) {
   }

   public void onAction(ChannelEvent event) {
   }

   public void onConnect(ChannelEvent event) {
   }

   public void onDisconnect(ChannelEvent event) {
   }

   public void onMessage(ChannelEvent event) {
   }

   public void onJoin(ChannelEvent event) {
      String joiner = event.getOriginNick2();
      this.userNicks.put(joiner, joiner);
   }

   public void onJoins(ChannelEvent event) {
      String[] jusers = event.getValue().toString().split(" ");

      for(String u : jusers) {
         if (u.startsWith("@") || u.startsWith("+") || u.startsWith("%")) {
            u = u.substring(1);
         }

         this.userNicks.put(u, u);
      }
   }

   public void onPart(ChannelEvent event) {
      String leaver = event.getOriginNick2();
      this.userNicks.remove(leaver);
   }

   public void onBan(ChannelEvent event) {
      String leaver = event.getOriginNick2();
      this.userNicks.remove(leaver);
   }

   public void onKick(ChannelEvent event) {
      String leaver = event.getSubjectNick2();
      this.userNicks.remove(leaver);
   }

   public void onNick(ChannelEvent event) {
      String leaver = event.getOriginNick2();
      String newName = event.getValue().toString();
      if (newName.startsWith("@") || newName.startsWith("+") || newName.startsWith("%")) {
         newName = newName.substring(1);
      }

      this.userNicks.remove(leaver);
      this.userNicks.put(newName, newName);
   }

   public void onOp(ChannelEvent event) {
   }

   public void onQuit(ChannelEvent event) {
      String leaver = event.getOriginNick2();
      this.userNicks.remove(leaver);
   }

   public void onTopicChange(ChannelEvent event) {
   }
}
