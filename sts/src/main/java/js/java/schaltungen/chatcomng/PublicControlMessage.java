package js.java.schaltungen.chatcomng;

public class PublicControlMessage {
   public final String sender;
   public final String text;
   public final boolean ispublic;
   public int instanz;

   PublicControlMessage(String sender, String text, boolean ispublic, int instanz) {
      super();
      this.sender = sender;
      this.text = text;
      this.ispublic = ispublic;
      this.instanz = instanz;
   }
}
