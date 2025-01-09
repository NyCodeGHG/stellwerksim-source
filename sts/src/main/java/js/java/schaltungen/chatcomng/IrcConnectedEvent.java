package js.java.schaltungen.chatcomng;

import de.deltaga.eb.Message;

@Message(
   threadgroup = "irc"
)
public class IrcConnectedEvent {
   public final boolean ipV6;

   IrcConnectedEvent(boolean v6) {
      this.ipV6 = v6;
   }
}
