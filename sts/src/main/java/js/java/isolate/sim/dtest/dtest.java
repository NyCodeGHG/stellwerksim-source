package js.java.isolate.sim.dtest;

import java.util.LinkedList;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;

public interface dtest {
   String getVersion();

   String getName();

   LinkedList<dtestresult> runTest(gleisbildModelSts var1);
}
