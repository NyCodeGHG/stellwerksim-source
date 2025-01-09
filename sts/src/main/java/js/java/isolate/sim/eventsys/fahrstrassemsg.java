package js.java.isolate.sim.eventsys;

import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class fahrstrassemsg extends eventmsg {
   public fahrstrasse f;

   public fahrstrassemsg(fahrstrasse _f) {
      this.f = _f;
   }
}
