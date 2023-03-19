package js.java.isolate.sim.gleisbild.fahrstrassen;

class fasRfSet extends fahrstrassenState {
   fasRfSet(fahrstrasseSelection fs) {
      super(fs);
   }

   @Override
   boolean ping() {
      if (this.getLastGleis().getFluentData().getStatus() != 1) {
         fasCanFreeRf ns = new fasCanFreeRf(this.myfs);
         if (this.setNextState(ns)) {
            ns.check();
         }
      } else if (this.getStart().getFluentData().getStatus() != 1) {
         this.setNextState(new fasNullState());
         return false;
      }

      return true;
   }

   @Override
   boolean stateAllowsState(fahrstrassenState newState) {
      return newState instanceof fasNullState || newState instanceof fasCanFreeRf;
   }
}
