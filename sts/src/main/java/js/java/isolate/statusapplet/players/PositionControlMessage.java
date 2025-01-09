package js.java.isolate.statusapplet.players;

public class PositionControlMessage {
   public final String sender;
   public final String text;

   PositionControlMessage(String sender, String text) {
      this.sender = sender;
      this.text = text;
   }
}
