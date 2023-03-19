package js.java.isolate.sim.gleisbild.fahrstrassen;

import js.java.isolate.sim.trigger;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public abstract class fsUserAlloc extends trigger {
   protected final fahrstrasseSelection fs;

   public fsUserAlloc(fahrstrasseSelection fs) {
      super();
      this.fs = fs;
   }

   public abstract boolean call();

   public static fsUserAlloc getAlloc(fahrstrasseSelection fs, final fsAllocs modus) {
      fsUserAlloc ret = null;
      if (modus == fsAllocs.ALLOCM_USER_GET || modus == fsAllocs.ALLOCM_USER_GETORSTORE) {
         ret = new fsUserAlloc(fs) {
            @Override
            public boolean call() {
               return true;
            }

            @Override
            public boolean ping() {
               this.fs.get(modus == fsAllocs.ALLOCM_USER_GETORSTORE);
               return false;
            }
         };
      } else if (modus == fsAllocs.ALLOCM_USER_FREE) {
         ret = new fsUserAlloc(fs) {
            @Override
            public boolean call() {
               return this.fs.getStart().getFluentData().getStellung() == gleisElements.ST_SIGNAL_GRÜN;
            }

            @Override
            public boolean ping() {
               this.fs.free();
               return false;
            }
         };
      } else if (modus == fsAllocs.ALLOCM_GET || modus == fsAllocs.ALLOCM_GETORSTORE) {
         ret = new fsUserAlloc(fs) {
            @Override
            public boolean call() {
               return this.fs.get(modus == fsAllocs.ALLOCM_GETORSTORE);
            }

            @Override
            public boolean ping() {
               return false;
            }
         };
      } else if (modus == fsAllocs.ALLOCM_FREE) {
         ret = new fsUserAlloc(fs) {
            @Override
            public boolean call() {
               return this.fs.free();
            }

            @Override
            public boolean ping() {
               return false;
            }
         };
      }

      tjm.addDirectRun(ret);
      return ret;
   }
}
