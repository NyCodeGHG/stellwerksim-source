package js.java.schaltungen.chatcomng;

public class BotMessageEvent {
   public final String sender;
   public final String text;
   public final boolean ispublic;
   public int instanz;

   BotMessageEvent(String sender, String text, boolean ispublic) {
      super();
      this.sender = sender;
      this.text = text;
      this.ispublic = ispublic;
      this.instanz = -1;
   }

   BotMessageEvent(String sender, String text, boolean ispublic, int instanz) {
      super();
      this.sender = sender;
      this.text = text;
      this.ispublic = ispublic;
      this.instanz = instanz;
   }
}
