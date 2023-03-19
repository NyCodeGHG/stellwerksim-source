package js.java.schaltungen.chatcomng;

import de.deltaga.eb.Message;

@Message(
   threadgroup = "irc"
)
public class EnterChannel {
   public final String channelname;

   public EnterChannel(String channelname) {
      super();
      this.channelname = channelname;
   }
}
