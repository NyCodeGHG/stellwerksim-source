package js.java.isolate.sim.eventsys;

import java.util.HashMap;

public class themagruppe {
   static HashMap<String, themagruppe> gp = new HashMap();
   public String name = "";

   static themagruppe getGruppe(String n) {
      if (n == null) {
         n = "";
      }

      themagruppe r = (themagruppe)gp.get(n);
      if (r == null) {
         r = new themagruppe(n);
      }

      return r;
   }

   private themagruppe(String n) {
      super();
      gp.put(n, this);
      this.name = n;
   }
}
