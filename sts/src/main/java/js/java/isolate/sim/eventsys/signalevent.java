package js.java.isolate.sim.eventsys;

import js.java.isolate.sim.Simulator;

public abstract class signalevent extends gleisevent {
   public static final int REDUCEDELAY = 20;

   protected signalevent(Simulator sim) {
      super(sim);
   }

   public abstract boolean init(int var1, int var2);
}
