package js.java.isolate.sim.gleisbild.fahrstrassen;

import java.util.Iterator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

public class fasWaitFS extends fahrstrassenState {
   private final gleisElements.Stellungen fahrtBild;

   public fasWaitFS(fahrstrasseSelection fs, gleisElements.Stellungen fahrtBild) {
      super(fs);
      this.fahrtBild = fahrtBild;
   }

   @Override
   boolean ping() {
      boolean checked = false;
      boolean cantget = false;
      int üpstate = 0;
      if (this.hasÜP()) {
         synchronized (this.getFSallocator()) {
            if (this.getFS().gleisüp.getFluentData().getStatus() == 1) {
               üpstate = 1;
            } else if (this.getFS().gleisüp.getFluentData().isFrei()) {
               üpstate = -1;
               cantget = true;
            }
         }
      }

      Iterator<gleis> it = this.getFS().gleisweg.iterator();

      while (it.hasNext() && !checked) {
         gleis g = (gleis)it.next();
         if (this.getFS().lastZD != null && this.getFS().lastZD.sameGleis(g)) {
            break;
         }

         if (this.hasÜP() && g.getElement() == gleis.ELEMENT_STRECKE && üpstate == 1) {
            g.getFluentData().setStatusByFs(1, this.getFS());
         }

         checked = checked || g.getFluentData().getStatus() == 3;
         if (g.getFluentData().getStatus() == 2) {
            checked = true;
            cantget = true;
         }
      }

      it = this.getFS().flankenweichen.keySet().iterator();

      while (it.hasNext() && !checked) {
         gleis gx = (gleis)it.next();
         checked = checked || gx.getFluentData().getStatus() == 4;
      }

      if (!checked || cantget) {
         if (cantget) {
            for (gleis gx : this.getFS().gleisweg) {
               if (gx.getFluentData().getStatus() != 2) {
                  gx.getFluentData().setStatusByFs(0, this.getFS());
               }
            }

            this.setNextState(new fasNullState());
         } else {
            boolean zwergeRot = false;

            for (gleis gl : this.getFS().zwerge) {
               zwergeRot |= gl.getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT;
            }

            if (zwergeRot) {
               boolean zwergStörung = false;

               for (gleis gl : this.getFS().zwerge) {
                  zwergStörung = zwergStörung || !gl.getFluentData().setStellung(gleisElements.ST_SIGNAL_GRÜN);
               }

               if (zwergStörung) {
                  for (gleis gl : this.getFS().zwerge) {
                     gl.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
                  }

                  for (gleis gxx : this.getFS().gleisweg) {
                     if (gxx.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT) {
                        this.getFSallocator().unreserveAusfahrt(gxx.getENR());
                     }

                     if (gxx.getFluentData().getStatus() != 2) {
                        gxx.getFluentData().setStatusByFs(0, this.getFS());
                     }
                  }

                  this.setNextState(new fasNullState());
               }
            } else if (this.getStart().getFluentData().setStellung(this.fahrtBild, this.getFS())) {
               this.getFS().connectVSigs();
               fasFSSet next = new fasFSSet(this.myfs);
               if (this.setNextState(next)) {
                  this.getStop().triggerApproachingFS();
                  this.getStart().triggerStartingFS();
               }
            } else {
               for (gleis gl : this.getFS().zwerge) {
                  gl.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
               }

               for (gleis gxx : this.getFS().gleisweg) {
                  if (gxx.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT) {
                     this.getFSallocator().unreserveAusfahrt(gxx.getENR());
                  }

                  if (gxx.getFluentData().getStatus() != 2) {
                     gxx.getFluentData().setStatusByFs(0, this.getFS());
                  }
               }

               this.setNextState(new fasNullState());
            }
         }
      }

      return true;
   }

   @Override
   boolean stateAllowsState(fahrstrassenState newState) {
      return newState instanceof fasNullState || newState instanceof fasFSSet || newState instanceof fasCanFreeFS;
   }
}
