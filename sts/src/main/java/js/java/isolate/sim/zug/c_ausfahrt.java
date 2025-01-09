package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;

class c_ausfahrt extends baseChain1Chain {
   c_ausfahrt() {
      super(new c_vorlauf());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      boolean ret = false;
      if ((z.visible || z.zugbelegt.size() <= 0) && !z.exitMode) {
         return this.callFalse(z);
      } else {
         if (z.callme != null) {
            z.callme.kuppeln(z);
            z.callme = null;
         }

         if (z.exitMode && z.zugbelegt.size() == 0) {
            z.üpwaitc++;
            if (z.üpwaitc > 60) {
               z.üpwaitc = 0;
            }

            if (z.leaveAfterÜP) {
               if (z.allowRemove) {
                  z.fertig = true;
               }

               z.storeZugÜP(false);
               if (z.allowRemove) {
                  z.my_main.hideZug(z);
                  z.successorRemove();
               }

               if (z.gotüp) {
                  z.gotüp = false;
                  z.my_main.getFSallocator().zugTakenMessage("OK", z.ein_enr, z.zid);
               }

               z.visible = false;
               ret = true;
               if (zug.debugMode != null) {
                  zug.debugMode.writeln("zug (" + z.getName() + ")", "Fertig");
               }
            }
         } else {
            z.namefarbe = 0;
            z.tempo_pos = z.tempo_pos + z.ist_tempo;
            if (z.tempo_pos > (double)z.calcMaxSpeed(z.lastmasstab)) {
               z.tempo_pos = z.tempo_pos % (double)z.calcMaxSpeed(z.lastmasstab);

               for (int i = 0; i < 2 && z.zugbelegt.size() > 0; i++) {
                  gleis gl = (gleis)z.zugbelegt.removeFirst();
                  gl.getFluentData().setStatusByZug(0, z);
                  ret = true;
                  z.setRot(gl, true);
               }

               if (!z.exitMode && z.zugbelegt.size() == 0) {
                  z.fertig = true;
                  z.my_main.hideZug(z);
                  z.successorRemove();
               }
            }

            z.calcAndSet_tempo();
         }

         return ret;
      }
   }
}
