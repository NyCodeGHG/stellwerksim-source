package js.java.isolate.sim.zug;

public class c_notBremsung extends baseChain1Chain {
   c_notBremsung() {
      super(new c_richtungDrehen());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      boolean ret = false;
      if (z.notBremsung) {
         tl_notBremsung.add(z);
         z.notBremsung = false;
         z.weiterfahren = false;
         z.abfahrtbefehl = false;
      } else if (z.getLimit(tl_notBremsung.class) != null) {
         if (z.ist_tempo < 0.1) {
            if (!z.weiterfahren) {
               return ret;
            }

            z.ist_tempo = 0.1;
            tl_notBremsung.remove(z);
            tl_sichtfahrt.add(z);
         } else {
            ret = this.call(z);
         }
      } else {
         ret = this.call(z);
      }

      return ret;
   }
}
