package js.java.schaltungen.chatcomng;

public class UserLeftEvent {
   public final String channel;
   public final ChatUser user;
   public final UserLeftEvent.REASON reason;
   public final ChatUser originator;

   UserLeftEvent(String channel, ChatUser user, UserLeftEvent.REASON r) {
      super();
      this.channel = channel;
      this.user = user;
      this.reason = r;
      this.originator = null;
   }

   UserLeftEvent(String channel, ChatUser user, UserLeftEvent.REASON r, ChatUser originator) {
      super();
      this.channel = channel;
      this.user = user;
      this.reason = r;
      this.originator = originator;
   }

   public static enum REASON {
      LEFT,
      BAN,
      KICK,
      QUIT;
   }
}
