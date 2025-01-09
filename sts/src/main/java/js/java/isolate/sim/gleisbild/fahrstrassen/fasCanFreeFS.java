package js.java.isolate.sim.gleisbild.fahrstrassen;

import java.util.Iterator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class fasCanFreeFS extends fasChecker {
   public fasCanFreeFS(fahrstrasseSelection fs) {
      super(fs);
   }

   @Override
   public boolean check() {
      boolean ret = false;
      if (this.getStart().getFluentData().getStellung() == gleisElements.ST_SIGNAL_GRÜN
         || this.getStart().getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT) {
         ret = true;
         Iterator<gleis> it = this.getFS().gleisweg.iterator();

         while (it.hasNext() && ret) {
            gleis g = (gleis)it.next();
            if (this.getFS().lastZD == g) {
               break;
            }

            if (g != this.getFS().lastGleis) {
               ret = ret && (g.getFluentData().isReserviert() || g.getElement() == gleis.ELEMENT_KREUZUNGBRUECKE);
            }
         }
      }

      if (ret) {
         fasFreeFS next = new fasFreeFS(this.myfs);
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
      return newState instanceof fasNullState || newState instanceof fasFreeFS;
   }
}
