package js.java.schaltungen.chatcomng;

import de.deltaga.eb.Message;
import java.util.Collection;

@Message(
   threadgroup = "irc"
)
public class ChannelUsersEvent {
   public final String channelname;
   public final Collection<ChatUser> users;

   ChannelUsersEvent(String channelname, Collection<ChatUser> users) {
      super();
      this.channelname = channelname;
      this.users = users;
   }
}
