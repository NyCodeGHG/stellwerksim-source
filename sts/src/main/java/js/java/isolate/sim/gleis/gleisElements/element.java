package js.java.isolate.sim.gleis.gleisElements;

import java.util.EnumSet;

public interface element {
   boolean matches(element var1);

   boolean matchesTyp(element var1);

   int getTyp();

   int getElement();

   EnumSet<gleisElements.RICHTUNG> getAllowedRichtung();

   boolean paintLight();
}
