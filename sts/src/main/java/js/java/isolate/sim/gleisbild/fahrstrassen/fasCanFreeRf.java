package js.java.isolate.sim.gleisbild.fahrstrassen;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class fasCanFreeRf extends fasChecker {
   public fasCanFreeRf(fahrstrasseSelection fs) {
      super(fs);
   }

   @Override
   public boolean check() {
      boolean ret = false;
      if (this.getStart().getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT
         || this.getStart().getFluentData().getStellung() == gleisElements.ST_SIGNAL_RF) {
         ret = true;
         int rl = this.getFS().rangierlänge;

         for(gleis g : this.getFS().gleisweg) {
            boolean match = g.getFluentData().isReserviert() || g.getElement() == gleis.ELEMENT_KREUZUNGBRUECKE;
            if (rl <= 0) {
               if (!match) {
                  ret = true;
               }
               break;
            }

            if (!ret && match) {
               break;
            }

            ret = ret && match;
            --rl;
         }
      }

      if (ret) {
         fasFreeRf next = new fasFreeRf(this.myfs);
         if (this.setNextState(next)) {
            next.check();
         } else {
            ret = false;
         }
      }

      if (!ret) {
         this.setNextState(new fasNullState());
      }

      return ret;
   }

   @Override
   boolean ping() {
      return false;
   }

   @Override
   boolean stateAllowsState(fahrstrassenState newState) {
      return newState instanceof fasNullState || newState instanceof fasFreeRf;
   }
}
