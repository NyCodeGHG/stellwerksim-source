package js.java.schaltungen.chatcomng;

public class SimControlMessage {
   public final String msg;
   public final boolean ispublic;
   public final int instanz;

   public SimControlMessage(String text, boolean ispublic, int instanz) {
      super();
      this.msg = text;
      this.ispublic = ispublic;
      this.instanz = instanz;
   }
}
