package js.java.isolate.sim.simTest;

import java.io.PrintWriter;
import js.java.isolate.sim.sim.stellwerksim_main;

public interface simCmd {
   String getName();

   void execute(stellwerksim_main var1, PrintWriter var2, String[] var3);

   void usage(PrintWriter var1);
}
