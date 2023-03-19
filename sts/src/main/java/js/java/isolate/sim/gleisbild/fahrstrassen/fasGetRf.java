package js.java.isolate.sim.gleisbild.fahrstrassen;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

class fasGetRf extends fahrstrassenState {
   fasGetRf(fahrstrasseSelection fs) {
      super(fs);
   }

   public void check() {
      boolean allesok = true;
      int rl = this.getFS().rangierlänge;

      for(gleis g : this.getFS().gleisweg) {
         if (rl > 0) {
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
            } else {
               g.getFluentData().setStatusByFs(1, this.getFS());
            }

            this.getFS().lastGleis = g;
         }

         --rl;
      }

      if (allesok) {
         fasWaitRf next = new fasWaitRf(this.myfs, gleisElements.ST_SIGNAL_RF);
         if (this.setNextState(next)) {
            this.getStart().getFluentData().setStartingFS(this.getFS());
            this.tjm_add(this.getFS());
         } else {
            allesok = false;
         }
      }

      if (!allesok) {
         rl = this.getFS().rangierlänge;

         for(gleis g : this.getFS().gleisweg) {
            if (rl > 0) {
               if (g.getElement() == gleis.ELEMENT_WEICHEUNTEN && g.getElement() == gleis.ELEMENT_WEICHEOBEN && g.getFluentData().getStatus() == 3) {
                  g.getFluentData().setStatusByFs(4, this.getFS());
               } else {
                  g.getFluentData().setStatusByFs(0, this.getFS());
               }
            }

            --rl;
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
      return newState instanceof fasNullState || newState instanceof fasWaitRf;
   }
}
