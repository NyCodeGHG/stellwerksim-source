package js.java.schaltungen.chatcomng;

import java.util.concurrent.ConcurrentHashMap;
import org.relayirc.chatengine.Channel;
import org.relayirc.chatengine.ChannelEvent;

public class IC_PublicChannel extends IrcChannel {
   public final ConcurrentHashMap<String, ChatUser> users = new ConcurrentHashMap();
   private String topic;

   public IC_PublicChannel(Channel chl, ChatNG chat, ChannelsNameParser.ChannelName channel) {
      super(chat, channel, true);
      this.topic = chl.getTopic();
   }

   @Override
   public String getTopic() {
      return this.topic;
   }

   @Override
   public void onActivation(ChannelEvent event) {
   }

   @Override
   public void onAction(ChannelEvent event) {
      this.chat.bus.publish(new ChannelActionMessageEvent(this.channel.name, this.chat.getKnownUser(event.getOriginNick2()), event.getValue().toString()));
   }

   @Override
   public void onConnect(ChannelEvent event) {
   }

   @Override
   public void onDisconnect(ChannelEvent event) {
   }

   @Override
   public void onMessage(ChannelEvent event) {
      this.chat.bus.publish(new ChannelMessageEvent(this.channel.name, this.chat.getKnownUser(event.getOriginNick2()), event.getValue().toString()));
   }

   @Override
   public void onJoin(ChannelEvent event) {
      ChatUser joiner = this.chat.getKnownUser(event.getOriginNick2());
      this.users.put(event.getOriginNick2(), joiner);
      this.chat.bus.publish(new ChannelUsersEvent(this.channel.name, this.users.values()));
      this.chat.bus.publish(new UserJoinedEvent(this.channel.name, joiner));
   }

   @Override
   public void onJoins(ChannelEvent event) {
      String[] jusers = event.getValue().toString().split(" ");

      for (String u : jusers) {
         if (u.startsWith("@") || u.startsWith("+") || u.startsWith("%")) {
            u = u.substring(1);
         }

         this.users.put(u, this.chat.getKnownUser(u));
      }

      this.chat.bus.publish(new ChannelUsersEvent(this.channel.name, this.users.values()));
   }

   @Override
   public void onPart(ChannelEvent event) {
      ChatUser leaver = this.chat.getKnownUser(event.getOriginNick2());
      if (this.users.containsKey(event.getOriginNick2())) {
         this.users.remove(event.getOriginNick2());
         this.chat.bus.publish(new ChannelUsersEvent(this.channel.name, this.users.values()));
         this.chat.bus.publish(new UserLeftEvent(this.channel.name, leaver, UserLeftEvent.REASON.LEFT));
      }
   }

   @Override
   public void onBan(ChannelEvent event) {
      ChatUser leaver = this.chat.getKnownUser(event.getOriginNick2());
      if (this.users.containsKey(event.getOriginNick2())) {
         this.users.remove(event.getOriginNick2());
         this.chat.bus.publish(new ChannelUsersEvent(this.channel.name, this.users.values()));
         this.chat.bus.publish(new UserLeftEvent(this.channel.name, leaver, UserLeftEvent.REASON.BAN));
      }
   }

   @Override
   public void onKick(ChannelEvent event) {
      ChatUser admin = this.chat.getKnownUser(event.getOriginNick2());
      ChatUser leaver = this.chat.getKnownUser(event.getSubjectNick2());
      if (this.users.containsKey(event.getSubjectNick2())) {
         this.users.remove(event.getSubjectNick2());
         this.chat.bus.publish(new ChannelUsersEvent(this.channel.name, this.users.values()));
         this.chat.bus.publish(new UserLeftEvent(this.channel.name, leaver, UserLeftEvent.REASON.KICK, admin));
      }
   }

   @Override
   public void onNick(ChannelEvent event) {
      ChatUser leaver = this.chat.getKnownUser(event.getOriginNick2());
      ChatUser newName = this.chat.getKnownUser(event.getValue().toString());
      this.users.remove(event.getOriginNick2());
      this.users.put(event.getValue().toString(), newName);
      this.chat.bus.publish(new ChannelUsersEvent(this.channel.name, this.users.values()));
   }

   @Override
   public void onOp(ChannelEvent event) {
   }

   @Override
   public void onQuit(ChannelEvent event) {
      ChatUser leaver = this.chat.getKnownUser(event.getOriginNick2());
      if (this.users.containsKey(event.getOriginNick2())) {
         this.users.remove(event.getOriginNick2());
         this.chat.bus.publish(new ChannelUsersEvent(this.channel.name, this.users.values()));
         this.chat.bus.publish(new UserLeftEvent(this.channel.name, leaver, UserLeftEvent.REASON.QUIT));
      }
   }

   @Override
   public void onTopicChange(ChannelEvent event) {
      this.topic = event.getChannel().getTopic();
   }
}
