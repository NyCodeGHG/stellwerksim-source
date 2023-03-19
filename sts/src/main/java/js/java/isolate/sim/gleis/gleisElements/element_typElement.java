package js.java.isolate.sim.gleis.gleisElements;

import java.util.EnumSet;

class element_typElement extends element_typ implements storableElement {
   protected final int element;
   protected final EnumSet<gleisElements.RICHTUNG> allowedRichtung;

   element_typElement(int typ, int element, EnumSet<gleisElements.RICHTUNG> r) {
      super(typ);
      this.element = element;
      gleisHelper.allElements.add(this);
      this.allowedRichtung = r;
   }

   element_typElement(int typ, int element, EnumSet<gleisElements.RICHTUNG> r, boolean isdefault) {
      this(typ, element, r);
      if (isdefault) {
         gleisHelper.defaultElements.put(typ, this);
      }
   }

   @Override
   public boolean matches(element other) {
      if (!(other instanceof element_typElement)) {
         return super.matches(other);
      } else {
         return this.typ == ((element_typElement)other).typ && this.element == ((element_typElement)other).element;
      }
   }

   public boolean equals(Object o) {
      if (!(o instanceof element_typElement)) {
         return false;
      } else {
         element_typElement e = (element_typElement)o;
         return e.typ == this.typ && e.element == this.element;
      }
   }

   public int hashCode() {
      return 100 * this.typ + this.element;
   }

   @Override
   public int getElement() {
      return this.element;
   }

   @Override
   public boolean paintLight() {
      return true;
   }

   public String toString() {
      return this.typ + "," + this.element;
   }

   @Override
   public EnumSet<gleisElements.RICHTUNG> getAllowedRichtung() {
      return this.allowedRichtung;
   }
}
