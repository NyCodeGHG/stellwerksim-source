package js.java.schaltungen.chatcomng;

import de.deltaga.eb.Message;

@Message(
   threadgroup = "irc"
)
public class UserJoinedEvent {
   public final String channel;
   public final ChatUser user;

   UserJoinedEvent(String channel, ChatUser user) {
      super();
      this.channel = channel;
      this.user = user;
   }
}
