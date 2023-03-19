package js.java.isolate.sim.eventsys;

import js.java.isolate.sim.Simulator;

public abstract class weicheevent extends gleisevent {
   public static final int REDUCEDELAY = 20;

   protected weicheevent(Simulator sim) {
      super(sim);
   }

   public abstract boolean init(int var1, int var2);
}
