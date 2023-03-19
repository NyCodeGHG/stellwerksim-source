package js.java.isolate.sim.gleisbild.fahrstrassen;

public abstract class fasChecker extends fahrstrassenState {
   public fasChecker(fahrstrasseSelection fs) {
      super(fs);
   }

   public abstract boolean check();
}
