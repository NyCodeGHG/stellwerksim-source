package js.java.isolate.sim.eventsys;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.zug.zug;

public class zugmsg extends eventmsg {
   public final gleis g;
   public final gleis before_gl;
   public final zug z;
   public final zug zielzug;

   public zugmsg(zug _z, gleis _g, gleis _before_gl) {
      this.g = _g;
      this.before_gl = _before_gl;
      this.z = _z;
      this.zielzug = null;
   }

   public zugmsg(zug _z, zug _zielzug) {
      this.g = null;
      this.before_gl = null;
      this.z = _z;
      this.zielzug = _zielzug;
   }

   public zugmsg(zug _z) {
      this.g = null;
      this.before_gl = null;
      this.z = _z;
      this.zielzug = null;
   }
}
