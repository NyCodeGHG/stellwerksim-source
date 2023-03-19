package js.java.isolate.sim.gleisbild.fahrstrassen;

import java.util.Iterator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class fasIsFree extends fasChecker {
   public fasIsFree(fahrstrasseSelection fs) {
      super(fs);
   }

   @Override
   public boolean check() {
      if (!this.getStart().getFluentData().isGesperrt() && !this.getStop().getFluentData().isGesperrt()) {
         boolean ret = false;
         this.getFS().lastZD = null;
         Iterator<gleis> it = this.getFS().gleisweg.iterator();
         if (this.getStart().getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT) {
            this.getFS().falseReason = 0;
            ret = true;
            if (this.getStop().getFluentData().getStellung() != gleisElements.ST_SIGNAL_AUS) {
               boolean festgelegt = false;
               int wcc = this.getFS().weichen.size();

               while(it.hasNext() && ret) {
                  gleis g = (gleis)it.next();
                  if (!festgelegt) {
                     if (g.getElement() != gleis.ELEMENT_ZDECKUNGSSIGNAL || !this.getFS().zdeckungssignale.contains(g)) {
                        if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
                           --wcc;
                        }
                     } else if (!g.getFluentData().isFrei()) {
                        this.getFS().falseReason = 1;
                        ret = false;
                     } else {
                        this.getFS().lastZD = g;
                        festgelegt = g.getFluentData().getStellung() == gleis.ST_ZDSIGNAL_FESTGELEGT
                           || g.getFluentData().getStellung() == gleis.ST_ZDSIGNAL_ROT;
                     }
                  }

                  ret = ret && g.getFluentData().isFrei();
                  if (g.getFluentData().getStatus() == 1) {
                     this.getFS().falseReason = 3;
                     ret = false;
                     wcc = Integer.MAX_VALUE;
                  } else if (g.getElement().matches(gleis.ALLE_STARTSIGNALE)
                     && g != this.getStart()
                     && g != this.getStop()
                     && !this.getFS().zwerge.contains(g)
                     && g.getFluentData().getStellung() != gleis.ST_SIGNAL_ROT
                     && g.getFluentData().getStellung() != gleis.ST_SIGNAL_AUS) {
                     this.getFS().falseReason = 8;
                     ret = false;
                  }
               }

               if (ret && !festgelegt) {
                  this.getFS().lastZD = null;
               }

               if (!ret && this.getFS().lastZD != null) {
                  ret = true;
               }

               if (wcc > 0) {
                  ret = false;
               }

               if (ret) {
                  for(gleis g : this.getFS().flankenweichen.keySet()) {
                     if (g.getFluentData().getStellung() != this.getFS().flankenweichen.get(g)) {
                        ret = ret && g.getFluentData().isFrei();
                        if (!ret) {
                           this.getFS().falseReason = 4;
                        }
                     }
                  }
               }

               if (ret) {
                  for(gleis g : this.getFS().zwerge) {
                     if (g.getFluentData().getStellung() != gleisElements.ST_SIGNAL_ROT) {
                        this.getFS().falseReason = 5;
                        ret = false;
                     }
                  }
               }
            } else {
               this.getFS().falseReason = 6;
               ret = false;
            }
         }

         if (ret) {
            fasGetFS next = new fasGetFS(this.myfs);
            if (this.setNextState(next)) {
               next.check();
            } else {
               this.getFS().falseReason = 7;
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
      return newState instanceof fasNullState || newState instanceof fasGetFS;
   }
}
