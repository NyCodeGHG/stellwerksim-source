package js.java.schaltungen.chatcomng;

public class ChatMessageEvent {
   public final String channel;
   public final String msg;

   public ChatMessageEvent(String channel, String msg) {
      super();
      this.channel = channel;
      this.msg = msg;
   }
}
