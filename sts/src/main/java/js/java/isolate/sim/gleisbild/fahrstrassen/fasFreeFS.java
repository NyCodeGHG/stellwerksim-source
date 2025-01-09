package js.java.isolate.sim.gleisbild.fahrstrassen;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class fasFreeFS extends fahrstrassenState {
   fasFreeFS(fahrstrasseSelection fs) {
      super(fs);
   }

   public void check() {
      boolean suc = this.getStart().getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT, this.getFS());
      if (suc) {
         for (gleis g : this.getFS().gleisweg) {
            if (g.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT) {
               this.getFSallocator().unreserveAusfahrt(g.getENR());
            }

            if (this.getFS().lastZD == g) {
               if (this.getFS().lastZD.getFluentData().getStellung() != gleisElements.ST_ZDSIGNAL_FESTGELEGT) {
                  this.getFS().lastZD.getFluentData().setStellung(gleisElements.ST_SIGNAL_GRÜN);
               }
               break;
            }

            g.getFluentData().setStatusByFs(0, this.getFS());
         }

         for (gleis gl : this.getFS().zwerge) {
            boolean zsuc = gl.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
            if (!zsuc) {
            }
         }

         this.getStart().getFluentData().setStartingFS(null);
         this.getStop().getFluentData().setEndingFS(null);
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
