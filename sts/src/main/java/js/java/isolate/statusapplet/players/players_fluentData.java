package js.java.isolate.statusapplet.players;

import java.util.EnumSet;
import java.util.HashMap;
import js.java.isolate.sim.gleis.fluentData;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class players_fluentData extends fluentData {
   private final HashMap<Integer, OCCU_KIND> event = new HashMap();
   fahrstrasse fs = null;

   players_fluentData(gleis g) {
      super(g);
   }

   void setKind(OCCU_KIND k) {
      int g = k.getGroup();
      this.event.put(g, k);
   }

   EnumSet<OCCU_KIND> getKind() {
      EnumSet<OCCU_KIND> ret = EnumSet.noneOf(OCCU_KIND.class);

      for (OCCU_KIND k : this.event.values()) {
         ret.add(k);
      }

      return ret;
   }

   void reset() {
      this.event.clear();
      this.fs = null;
      this.setStatus(0);
   }

   @Override
   public boolean setStellung(gleisElements.Stellungen s, fahrstrasse f) {
      this.fs = f;
      return super.setStellungTo(s, null);
   }

   public fahrstrasse getFS() {
      return this.fs;
   }
}
