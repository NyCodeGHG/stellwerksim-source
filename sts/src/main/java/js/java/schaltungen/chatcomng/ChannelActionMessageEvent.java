package js.java.schaltungen.chatcomng;

public class ChannelActionMessageEvent {
   public final String channelname;
   public final ChatUser sender;
   public final String text;

   ChannelActionMessageEvent(String channelname, ChatUser sender, String text) {
      this.channelname = channelname;
      this.sender = sender;
      this.text = text;
   }
}
