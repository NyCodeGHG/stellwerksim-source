package js.java.isolate.sim.eventsys;

public class cbCallMeIn implements callBehaviour {
   private int dauer;
   private boolean shortMessage;

   public cbCallMeIn(int dauer) {
      this(dauer, false);
   }

   public cbCallMeIn(int dauer, boolean shortMessage) {
      super();
      this.dauer = dauer;
      this.shortMessage = shortMessage;
   }

   @Override
   public void called(event e, String token) {
      e.callMeIn(this.dauer);
      String text = e.funkName() + ": Danke für den Anruf, die Arbeiten beginnen sofort.";
      if (!this.shortMessage) {
         text = text + " Reparaturzeit " + e.restTime() + " Minuten";
      }

      text = text + ".";
      e.showMessageNow(text);
   }
}
