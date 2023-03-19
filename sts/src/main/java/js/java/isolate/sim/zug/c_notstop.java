package js.java.isolate.sim.zug;

class c_notstop extends baseChain {
   c_notstop() {
      super();
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      z.ist_tempo = 1.0;
      z.namefarbe = 1;
      z.sichtstopp = true;
      tl_langsam.remove(z);
      tl_sichtfahrt.remove(z);
      tl_setvmax.remove(z);
      if (zug.debugMode != null) {
         zug.debugMode.writeln("zug (" + z.getName() + ")", "Notstopp");
      }

      return false;
   }
}
