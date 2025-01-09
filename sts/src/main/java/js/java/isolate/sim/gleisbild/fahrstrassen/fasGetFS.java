package js.java.isolate.sim.gleisbild.fahrstrassen;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class fasGetFS extends fahrstrassenState {
   fasGetFS(fahrstrasseSelection fs) {
      super(fs);
   }

   public void check() {
      boolean allesok = true;

      for (gleis g : this.getFS().gleisweg) {
         if (this.getFS().lastZD != null && this.getFS().lastZD.sameGleis(g)) {
            break;
         }

         if (this.getFS().weichen.get(g) != null) {
            if (g.getFluentData().getStellung() == this.getFS().weichen.get(g)) {
               g.getFluentData().setStatusByFs(1, this.getFS());
            } else {
               boolean b = g.getFluentData().setStellung((gleisElements.Stellungen)this.getFS().weichen.get(g), this.getFS());
               allesok = b && allesok;
               if (b) {
                  g.getFluentData().setStatusByFs(3, this.getFS());
                  this.tjm_add(g);
               }
            }
         } else if (gleis.ALLE_BAHNÜBERGÄNGE.matches(g.getElement())) {
            if (g.getFluentData().getStellung() != gleis.ST_ANRUFÜBERGANG_GESCHLOSSEN) {
               g.getFluentData().setStatusByFs(3, this.getFS());
            } else {
               g.getFluentData().setStatusByFs(1, this.getFS());
            }

            this.tjm_add(g);
         } else if (g.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT) {
            g.getFluentData().setStatusByFs(3, this.getFS());
            if (allesok) {
               this.getFSallocator().reserveAusfahrt(g.getENR());
               this.tjm_add(g);
            }
         } else {
            g.getFluentData().setStatusByFs(this.hasÜP() && g.getElement() == gleis.ELEMENT_STRECKE ? 3 : 1, this.getFS());
         }

         if (g.getFluentData().getStatus() == 1 || g.getFluentData().getStatus() == 3) {
            this.getFS().lastGleis = g;
         }
      }

      for (gleis g : this.getFS().flankenweichen.keySet()) {
         if (g.getFluentData().getStellung() != this.getFS().flankenweichen.get(g)) {
            boolean b = g.getFluentData().setStellung((gleisElements.Stellungen)this.getFS().flankenweichen.get(g), this.getFS());
            if (b) {
               g.getFluentData().setStatusByFs(4, this.getFS());
               this.tjm_add(g);
            }
         }
      }

      if (allesok) {
         if (this.getFS().lastZD != null && this.getFS().lastZD.getFluentData().getStellung() != gleisElements.ST_ZDSIGNAL_FESTGELEGT) {
            this.getFS().lastZD.getFluentData().setStellung(gleisElements.ST_ZDSIGNAL_ROT, this.getFS());
         }

         fasWaitFS next = new fasWaitFS(this.myfs, gleisElements.ST_SIGNAL_GRÜN);
         if (this.setNextState(next)) {
            this.getStart().getFluentData().setStartingFS(this.getFS());
            this.getStop().getFluentData().setEndingFS(this.getFS());
            this.tjm_add(this.getFS());
         } else {
            allesok = false;
         }
      }

      if (!allesok) {
         for (gleis gx : this.getFS().gleisweg) {
            if (gx.getElement() == gleis.ELEMENT_WEICHEUNTEN && gx.getElement() == gleis.ELEMENT_WEICHEOBEN && gx.getFluentData().getStatus() == 3) {
               gx.getFluentData().setStatusByFs(4, this.getFS());
            } else {
               gx.getFluentData().setStatusByFs(0, this.getFS());
            }
         }

         this.setNextState(new fasNullState());
      }
   }

   @Override
   boolean ping() {
      return true;
   }

   @Override
   boolean stateAllowsState(fahrstrassenState newState) {
      return newState instanceof fasNullState || newState instanceof fasWaitFS;
   }
}
