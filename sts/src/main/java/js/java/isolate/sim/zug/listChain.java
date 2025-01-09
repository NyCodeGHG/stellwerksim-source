package js.java.isolate.sim.zug;

import java.util.LinkedList;

class listChain {
   private final LinkedList<baseChain> cmds = new LinkedList();

   void add(baseChain c) {
      this.cmds.add(c);
   }

   void runChain(zug z) {
      for (baseChain c : this.cmds) {
         c.run(z);
      }
   }
}
