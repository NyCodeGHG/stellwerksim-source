package js.java.schaltungen.chatcomng;

public class GameInfoEvent {
   public final String currentGame;

   public GameInfoEvent(String name) {
      super();
      this.currentGame = name;
   }

   public GameInfoEvent() {
      super();
      this.currentGame = null;
   }
}
