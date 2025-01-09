package js.java.schaltungen.chatcomng;

public class IrcLine {
   public final String line;
   public final String channel;
   public final String channeltitel;
   public final Object clickEvent;
   public final boolean statusMessage;

   public IrcLine(String channel, String channeltitel, String line, Object clickEvent, boolean statusMessage) {
      this.channel = channel;
      this.channeltitel = channeltitel;
      this.line = line;
      this.clickEvent = clickEvent;
      this.statusMessage = statusMessage;
   }
}
