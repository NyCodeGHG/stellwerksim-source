package js.java.schaltungen.chatcomng;

public class UserChatMessageEvent {
   public final String channel;
   public final String msg;

   public UserChatMessageEvent(String channel, String msg) {
      super();
      this.channel = channel;
      this.msg = msg;
   }
}
