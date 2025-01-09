package js.java.schaltungen.switchbase;

public class SwitchValueEvent {
   public final String name;
   public final boolean enabled;

   public SwitchValueEvent(String name, boolean enabled) {
      this.name = name;
      this.enabled = enabled;
   }
}
