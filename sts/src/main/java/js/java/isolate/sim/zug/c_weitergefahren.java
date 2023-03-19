package js.java.isolate.sim.zug;

class c_weitergefahren extends baseChain1Chain {
   c_weitergefahren() {
      super(new c_prerunner());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (!z.ambahnsteig) {
         z.before_gl = z.pos_gl;
         z.pos_gl = z.next_gl;
         z.lastmasstab = z.pos_gl.getMasstab();
         z.weiterfahren = false;
         z.abfahrtbefehl = false;
         if (!z.exitMode) {
            z.zugbelegt.addLast(z.pos_gl);
            return this.call(z);
         }
      }

      return false;
   }
}
