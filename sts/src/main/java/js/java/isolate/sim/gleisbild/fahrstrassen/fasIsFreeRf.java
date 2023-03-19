package js.java.isolate.sim.gleisbild.fahrstrassen;

import java.util.Iterator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class fasIsFreeRf extends fasChecker {
   public fasIsFreeRf(fahrstrasseSelection fs) {
      super(fs);
   }

   @Override
   public boolean check() {
      if (!this.getStart().getFluentData().isGesperrt() && !this.getStop().getFluentData().isGesperrt()) {
         boolean ret = false;
         if (this.getStart().getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT
            && this.getStop().getFluentData().getStellung() != gleisElements.ST_SIGNAL_AUS) {
            this.getFS().falseReason = 0;
            ret = true;
            this.getFS().rangierlänge = 0;
            int wcc = this.getFS().weichen.size();
            Iterator<gleis> it = this.getFS().gleisweg.iterator();

            while(it.hasNext() && ret) {
               gleis g = (gleis)it.next();
               if (g.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL && this.getFS().zdeckungssignale.contains(g)) {
                  if (g.getFluentData().getStellung() == gleis.ST_ZDSIGNAL_FESTGELEGT) {
                     break;
                  }
               } else if (g.getElement().matches(gleis.ALLE_STARTSIGNALE)
                  && g != this.getStart()
                  && g != this.getStop()
                  && g.getFluentData().getStellung() != gleis.ST_SIGNAL_ROT
                  && g.getFluentData().getStellung() != gleis.ST_SIGNAL_AUS) {
                  this.getFS().falseReason = 8;
                  ret = false;
                  break;
               }

               if (!g.getFluentData().isFrei()) {
                  if (g.getFluentData().getStatus() != 2) {
                     this.getFS().falseReason = 2;
                     ret = false;
                  }
                  break;
               }

               ++this.getFS().rangierlänge;
               if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
                  --wcc;
               }
            }

            if (wcc != 0) {
               this.getFS().falseReason = 3;
               ret = false;
            }
         }

         if (ret) {
            fasGetRf next = new fasGetRf(this.myfs);
            if (this.setNextState(next)) {
               next.check();
            } else {
               this.getFS().falseReason = 4;
               ret = false;
            }
         }

         if (!ret) {
            this.setNextState(new fasNullState());
         }

         return ret;
      } else {
         this.setNextState(new fasNullState());
         return false;
      }
   }

   @Override
   boolean ping() {
      return true;
   }

   @Override
   boolean stateAllowsState(fahrstrassenState newState) {
      return newState instanceof fasNullState || newState instanceof fasGetRf;
   }
}
