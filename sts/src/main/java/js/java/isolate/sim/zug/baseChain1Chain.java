package js.java.isolate.sim.zug;

abstract class baseChain1Chain extends baseChain {
   protected static final baseChain1Chain.FalseReturn falseReturnNullPattern = new baseChain1Chain.FalseReturn();
   private final baseChain trueChain;

   baseChain1Chain(baseChain _trueChain) {
      this.trueChain = (baseChain)(_trueChain == null ? falseReturnNullPattern : _trueChain);
   }

   @Override
   boolean call(zug z) {
      return this.callTrue(z);
   }

   @Override
   boolean callTrue(zug z) {
      return this.trueChain.run(z);
   }

   protected static final class FalseReturn extends baseChain {
      @Override
      boolean run(zug z) {
         return false;
      }
   }
}
