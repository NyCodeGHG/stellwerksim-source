package js.java.schaltungen.settings;

public class PrefsChangedEvent {
   private final int sender;

   public PrefsChangedEvent(Object sender) {
      super();
      this.sender = sender.hashCode();
   }

   public boolean fromMe(Object other) {
      return other.hashCode() == this.sender;
   }
}
