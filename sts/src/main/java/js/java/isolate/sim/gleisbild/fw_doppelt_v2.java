package js.java.isolate.sim.gleisbild;

import java.util.ArrayList;
import java.util.LinkedList;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.panels.fwdoppeltPanel;

public class fw_doppelt_v2 implements fw_doppelt_interface {
   private final stellwerk_editor ed;
   private final fwdoppeltPanel pan;

   public fw_doppelt_v2(stellwerk_editor e, fwdoppeltPanel p, ArrayList<fahrstrasse> old) {
      this.ed = e;
      this.pan = p;
      this.pan.clear();
      this.pan.oldFahrwege(old);
   }

   @Override
   public void add(fahrstrasse f) {
      this.pan.add(f);
   }

   @Override
   public void start() {
      this.pan.start();
   }

   @Override
   public int getReturn() {
      return this.pan.getReturn();
   }

   @Override
   public LinkedList<fahrstrasse> getDelList() {
      return this.pan.getDelList();
   }
}
