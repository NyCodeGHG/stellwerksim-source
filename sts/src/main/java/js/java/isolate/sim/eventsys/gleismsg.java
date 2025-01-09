package js.java.isolate.sim.eventsys;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;

public class gleismsg extends eventmsg {
   public gleis g;
   public gleisElements.Stellungen st = gleisElements.Stellungen.undef;
   public fahrstrasse f = null;
   public int s = 0;
   public zug z = null;
   public boolean fsstart = false;

   public gleismsg(gleis _g, gleisElements.Stellungen _st, fahrstrasse _f) {
      this.g = _g;
      this.st = _st;
      this.f = _f;
   }

   public gleismsg(gleis _g, int _s, zug _z) {
      this.g = _g;
      this.s = _s;
      this.z = _z;
   }

   public gleismsg(gleis _g, fahrstrasse _f, boolean _fsstart) {
      this.g = _g;
      this.f = _f;
      this.fsstart = _fsstart;
   }
}
