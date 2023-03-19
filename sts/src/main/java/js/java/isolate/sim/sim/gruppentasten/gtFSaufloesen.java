package js.java.isolate.sim.sim.gruppentasten;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;
import js.java.isolate.sim.sim.stellwerksim_main;

public class gtFSaufloesen extends gtBase {
   public gtFSaufloesen(stellwerksim_main m, gleisbildSimControl glb) {
      super(m, glb);
   }

   @Override
   public String getText() {
      return "FS auflösen";
   }

   @Override
   public char getKey() {
      return 'F';
   }

   @Override
   protected void runCommand(String cmd) {
      gleis sig = this.signal1Garanty();
      fahrstrasse f = sig.getFluentData().getStartingFS();
      if (f != null && sig.getENR() == f.getStart().getENR()) {
         sig.disableAutoFW();
         if (this.my_main.getFSallocator().getFS(f, fsAllocs.ALLOCM_USER_FREE)) {
            this.my_main.incZählwert();
            this.showGleisChange();
         }
      }
   }
}
