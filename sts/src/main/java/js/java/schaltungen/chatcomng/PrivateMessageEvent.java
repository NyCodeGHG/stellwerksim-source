package js.java.schaltungen.chatcomng;

public class PrivateMessageEvent {
   public final String sender;
   public final String text;

   PrivateMessageEvent(String sender, String text) {
      this.sender = sender;
      this.text = text;
   }
}
