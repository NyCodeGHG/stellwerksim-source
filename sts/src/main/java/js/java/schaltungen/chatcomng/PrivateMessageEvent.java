package js.java.schaltungen.chatcomng;

public class PrivateMessageEvent {
   public final String sender;
   public final String text;

   PrivateMessageEvent(String sender, String text) {
      super();
      this.sender = sender;
      this.text = text;
   }
}
