package js.java.isolate.sim.zug;

abstract class baseChain2Chain extends baseChain1Chain {
   private final baseChain falseChain;

   baseChain2Chain(baseChain _trueChain, baseChain _falseChain) {
      super(_trueChain);
      this.falseChain = (baseChain)(_falseChain == null ? falseReturnNullPattern : _falseChain);
   }

   @Override
   boolean callFalse(zug z) {
      return this.falseChain.run(z);
   }
}
