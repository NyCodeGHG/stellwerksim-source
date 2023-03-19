package js.java.isolate.sim.zug;

class c_mainloop extends baseChain2Chain {
   c_mainloop() {
      super(new c_bahnsteig(), new c_fahrt());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      return z.ambahnsteig ? this.callTrue(z) : this.callFalse(z);
   }
}
