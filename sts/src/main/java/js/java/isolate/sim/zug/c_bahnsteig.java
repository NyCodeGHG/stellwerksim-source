package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;

class c_bahnsteig extends baseChain1Chain {
   c_bahnsteig() {
      super(new c_lflag());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      int nverspaetung = (int)((z.mytime - z.ab) / 60000L);
      if (nverspaetung > z.verspaetung) {
         z.verspaetung = nverspaetung;
         z.lastVerspaetung = z.verspaetung;
         z.outputValueChanged = true;
      }

      for(gleis gl : z.zugbelegt) {
         gl.getFluentData().setStatusByZug(2, z);
      }

      z.namefarbe = 3;
      return this.callTrue(z);
   }
}
