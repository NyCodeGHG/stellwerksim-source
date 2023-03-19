package js.java.isolate.sim.gleisbild;

import java.util.LinkedList;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public interface fw_doppelt_interface {
   void add(fahrstrasse var1);

   void start();

   int getReturn();

   LinkedList<fahrstrasse> getDelList();
}
