package js.java.isolate.sim.eventsys;

public class cbChain implements callBehaviour {
   private final callBehaviour one;
   private final callBehaviour two;

   public cbChain(callBehaviour one, callBehaviour two) {
      super();
      this.one = one;
      this.two = two;
   }

   @Override
   public void called(event e, String token) {
      this.one.called(e, token);
      this.two.called(e, token);
   }
}
