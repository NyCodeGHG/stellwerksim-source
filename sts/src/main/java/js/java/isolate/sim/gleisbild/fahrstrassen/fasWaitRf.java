package js.java.isolate.sim.gleisbild.fahrstrassen;

import java.util.Iterator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class fasWaitRf extends fahrstrassenState {
   private gleisElements.Stellungen fahrtBild;

   public fasWaitRf(fahrstrasseSelection fs, gleisElements.Stellungen fahrtBild) {
      super(fs);
      this.fahrtBild = fahrtBild;
   }

   @Override
   boolean ping() {
      boolean checked = false;
      int rl = this.getFS().rangierlänge;

      for (Iterator<gleis> it = this.getFS().gleisweg.iterator(); it.hasNext() && !checked; rl--) {
         gleis g = (gleis)it.next();
         if (rl > 0) {
            checked = checked || g.getFluentData().getStatus() == 3;
         }
      }

      if (!checked) {
         if (this.getStart().getFluentData().setStellung(this.fahrtBild, this.getFS())) {
            fasRfSet next = new fasRfSet(this.myfs);
            this.setNextState(next);
         } else {
            rl = this.getFS().rangierlänge;

            for (gleis g : this.getFS().gleisweg) {
               if (rl > 0 && g.getFluentData().getStatus() != 2) {
                  g.getFluentData().setStatusByFs(0, this.getFS());
               }

               rl--;
            }

            this.setNextState(new fasNullState());
         }
      }

      return true;
   }

   @Override
   boolean stateAllowsState(fahrstrassenState newState) {
      return newState instanceof fasNullState || newState instanceof fasRfSet || newState instanceof fasCanFreeRf;
   }
}
