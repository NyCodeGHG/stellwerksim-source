package js.java.isolate.sim.gleisbild.fahrstrassen;

import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class fasFSSet extends fahrstrassenState {
   fasFSSet(fahrstrasseSelection fs) {
      super(fs);
   }

   @Override
   boolean ping() {
      if (this.getLastGleis() != null && this.getLastGleis().getFluentData().getStatus() != 1) {
         fasCanFreeFS n = new fasCanFreeFS(this.myfs);
         this.setNextState(n);
         if (!n.check()) {
            this.getStart().getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT, this.getFS());
         }
      }

      try {
         if (this.getStart().nextByRichtung(false).getFluentData().getStatus() != 1) {
            this.setNextState(new fasNullState());
            return false;
         }
      } catch (NullPointerException var2) {
         this.setNextState(new fasNullState());
      }

      return true;
   }

   @Override
   boolean stateAllowsState(fahrstrassenState newState) {
      return newState instanceof fasNullState || newState instanceof fasCanFreeFS;
   }
}
