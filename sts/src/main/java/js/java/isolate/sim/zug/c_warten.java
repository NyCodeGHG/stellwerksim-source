package js.java.isolate.sim.zug;

class c_warten extends baseChain1Chain {
   c_warten() {
      super(new c_prerunnerWarten());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.rottime == 0L) {
         z.rottime = z.mytime;
         z.anruftime = z.rottime;
         z.anrufzaehler = 0;
         z.rotVerspaetung = z.verspaetung;
         if (zug.debugMode != null && z.next_gl != null) {
            zug.debugMode.writeln("zug (" + z.getName() + ")", "Signal rot ENR " + z.next_gl.getENR());
         }
      } else {
         z.verspaetung = (int)((long)z.rotVerspaetung + (z.mytime - z.rottime) / 60000L);
         z.updateHeat(false, z.verspaetung, z.lastVerspaetung);
      }

      return this.call(z);
   }
}
