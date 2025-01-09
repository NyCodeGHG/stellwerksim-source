package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public class klaengetest implements dtest {
   @Override
   public String getName() {
      return "altes Maßstabsystem";
   }

   @Override
   public String getVersion() {
      return "$Revision: 5027 $";
   }

   @Override
   public LinkedList<dtestresult> runTest(gleisbildModelSts glb) {
      LinkedList<dtestresult> r = new LinkedList();
      if (glb.getMasstabCalculatorName() != 2) {
         dtestresult d = new dtestresult(0, "Das Maßstabsystem (siehe Eigenschaften) ist im Kompatibilitätsmodus.");
         r.add(d);
      }

      return r;
   }
}
