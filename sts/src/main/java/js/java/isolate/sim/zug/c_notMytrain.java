package js.java.isolate.sim.zug;

class c_notMytrain extends baseChain2Chain {
   private final baseChain wflag = new c_wflagNeuzug();

   c_notMytrain() {
      super(new c_ausfahrt(), new c_reportByUep());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      this.wflag.run(z);
      if (!z.mytrain && !z.visible) {
         z.namefarbe = 0;
         return this.callFalse(z);
      } else {
         z.externalÜpReport = false;
         return this.callTrue(z);
      }
   }
}
