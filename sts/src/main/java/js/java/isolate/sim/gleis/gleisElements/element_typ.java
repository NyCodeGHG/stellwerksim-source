package js.java.isolate.sim.gleis.gleisElements;

import java.util.EnumSet;

class element_typ implements element {
   protected final int typ;

   element_typ(int typ) {
      super();
      this.typ = typ;
   }

   @Override
   public boolean matches(element other) {
      if (other instanceof element_typ) {
         return this.typ == ((element_typ)other).typ;
      } else {
         return other instanceof element_list ? other.matches(this) : false;
      }
   }

   @Override
   public boolean matchesTyp(element other) {
      if (other instanceof element_typ) {
         return this.typ == ((element_typ)other).typ;
      } else {
         return false;
      }
   }

   @Override
   public int getTyp() {
      return this.typ;
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
