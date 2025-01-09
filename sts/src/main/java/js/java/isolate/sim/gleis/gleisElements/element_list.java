package js.java.isolate.sim.gleis.gleisElements;

import java.util.EnumSet;

public class element_list implements element {
   protected final element[] list;

   public element_list(element... l) {
      this.list = l;
   }

   @Override
   public boolean matches(element other) {
      for (element e : this.list) {
         if (e.matches(other)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean matchesTyp(element other) {
      for (element e : this.list) {
         if (e.matchesTyp(other)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int getTyp() {
      return 0;
   }

   @Override
   public int getElement() {
      return 0;
   }

   @Override
   public EnumSet<gleisElements.RICHTUNG> getAllowedRichtung() {
      return gleisElements.R_ALL;
   }

   @Override
   public boolean paintLight() {
      return false;
   }
}
