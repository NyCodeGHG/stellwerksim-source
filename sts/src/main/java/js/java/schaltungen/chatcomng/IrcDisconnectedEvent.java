package js.java.schaltungen.chatcomng;

public class IrcDisconnectedEvent {
   public final String message;
   public final boolean isDisconnecting;

   IrcDisconnectedEvent(boolean isDisconnecting, String message) {
      this.isDisconnecting = isDisconnecting;
      this.message = message != null ? message : "";
   }
}
