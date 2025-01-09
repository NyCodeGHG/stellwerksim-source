package js.java.isolate.sim.zug;

class c_reportByUep extends baseChain {
   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.externalÜpReport) {
         z.outputValueChanged = z.von.setBGColor(1) | z.outputValueChanged;
      }

      return false;
   }
}
