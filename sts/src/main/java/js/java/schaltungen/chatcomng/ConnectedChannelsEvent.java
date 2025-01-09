package js.java.schaltungen.chatcomng;

import de.deltaga.eb.Message;
import java.util.Collection;

@Message(
   threadgroup = "irc"
)
public class ConnectedChannelsEvent {
   public final Collection<IrcChannel> channels;

   ConnectedChannelsEvent(Collection<IrcChannel> channels) {
      this.channels = channels;
   }
}
