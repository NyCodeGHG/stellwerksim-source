package js.java.isolate.statusapplet.players;

import java.lang.ref.SoftReference;
import js.java.isolate.sim.GleisAdapter;

public class gleisbildModelAdapter {
   private SoftReference<players_gleisbildModel> gleisbild = null;
   private final GleisAdapter my_main;
   private final players_aid paid;

   gleisbildModelAdapter(GleisAdapter m, players_aid aid) {
      super();
      this.my_main = m;
      this.paid = aid;
   }

   void createGleisbild() {
      if (this.gleisbild == null || this.gleisbild.get() == null) {
         if (this.gleisbild != null) {
            System.out.println("Krass, war weg!");
         }

         this.gleisbild = new SoftReference(new players_gleisbildModel(this.my_main, this.paid));
      }
   }

   void freeGleisbild() {
      try {
         ((players_gleisbildModel)this.gleisbild.get()).totalClear();
      } catch (NullPointerException var2) {
      }

      this.gleisbild = null;
   }

   players_gleisbildModel getGleisbild() {
      this.createGleisbild();
      return (players_gleisbildModel)this.gleisbild.get();
   }

   public void ping() {
      try {
         ((players_gleisbildModel)this.gleisbild.get()).ping();
      } catch (NullPointerException var2) {
      }
   }
}
