package js.java.schaltungen.chatcomng;

public class GameInfoEvent {
   public final String currentGame;

   public GameInfoEvent(String name) {
      this.currentGame = name;
   }

   public GameInfoEvent() {
      this.currentGame = null;
   }
}
