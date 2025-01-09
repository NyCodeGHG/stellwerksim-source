package js.java.isolate.sim.gleisbild.fahrstrassen;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class fasFreeRf extends fahrstrassenState {
   fasFreeRf(fahrstrasseSelection fs) {
      super(fs);
   }

   public void check() {
      boolean suc = this.getStart().getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT, this.getFS());
      if (suc) {
         int rl = this.getFS().rangierlänge;

         for (gleis g : this.getFS().gleisweg) {
            if (rl <= 0) {
               break;
            }

            if (g.getFluentData().isReserviert()) {
               g.getFluentData().setStatusByFs(0, this.getFS());
            }

            rl--;
         }

         this.getStart().getFluentData().setStartingFS(null);
      }

      this.setNextState(new fasNullState());
   }

   @Override
   boolean ping() {
      return false;
   }

   @Override
   boolean stateAllowsState(fahrstrassenState newState) {
      return newState instanceof fasNullState;
   }
}
