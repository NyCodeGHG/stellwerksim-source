package js.java.isolate.sim.gleisbild.fahrstrassen;

public class fasNullState extends fahrstrassenState {
   public fasNullState() {
      super(null);
   }

   @Override
   boolean ping() {
      return false;
   }

   @Override
   boolean stateAllowsState(fahrstrassenState newState) {
      return newState instanceof fasChecker;
   }
}
