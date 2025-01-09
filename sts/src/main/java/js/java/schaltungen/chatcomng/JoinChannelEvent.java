package js.java.schaltungen.chatcomng;

import de.deltaga.eb.Message;

@Message(
   threadgroup = "irc"
)
public class JoinChannelEvent {
   public final String channel;
   public final ICFactory<?> customHandler;
   public final boolean sessionBound;

   public JoinChannelEvent(String c) {
      this.channel = c;
      this.customHandler = null;
      this.sessionBound = false;
   }

   public JoinChannelEvent(String c, ICFactory<?> customHandler, boolean sessionBound) {
      this.channel = c;
      this.customHandler = customHandler;
      this.sessionBound = sessionBound;
   }
}
