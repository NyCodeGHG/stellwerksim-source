package js.java.isolate.sim.zug;

class c_wassignal extends baseChain {
   @Override
   boolean run(zug z) {
      this.visiting(z);
      z.setRot(z.before_gl, false);
      z.firstSignalPassed = true;
      return false;
   }
}
