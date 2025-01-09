package js.java.isolate.sim.zug;

import js.java.isolate.sim.gleis.gleis;

class c_vorlauf extends baseChain1Chain {
   c_vorlauf() {
      super(new c_notBremsung());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      boolean ret = false;
      if (!z.visible && z.zugbelegt.size() == 0) {
         if (z.gotüp || z.an + (long)(z.verspaetung + z.glbModel.getVerspaetung(z.ein_enr)) * 60000L - 60000L - z.wayTime() <= z.mytime) {
            if (!z.gotüp) {
               gleis gl = z.glbModel.findFirst(new Object[]{z.ein_enr, gleis.ELEMENT_EINFAHRT});
               if (gl != null
                  && gl.getFluentData().getEinfahrtUmleitung() != null
                  && gl.getFluentData().getEinfahrtUmleitung().getElement() == gleis.ELEMENT_EINFAHRT) {
                  z.ein_enr = gl.getFluentData().getEinfahrtUmleitung().getENR();
                  if (zug.debugMode != null) {
                     zug.debugMode.writeln("zug (" + z.getName() + ")", "Einf-Um: " + z.ein_enr);
                  }
               }

               z.ein_stw = null;
            }

            z.outputValueChanged = z.von.setBGColor(5) | z.outputValueChanged;
            synchronized (z.my_main.getFSallocator()) {
               int al = z.my_main.getFSallocator().isFreeToSignal(z, z.ein_enr, z.gotüp);
               if (al > 0) {
                  if (al == 1) {
                     z.ist_tempo = z.calc_tempo();
                     z.visible = true;
                     z.lastAbfahrt = z.mytime;
                     z.lastVerspaetung = z.verspaetung;
                     z.decHeat();
                     if (!z.gotüp) {
                        z.glbModel.reserveToSignal(z.ein_enr, false);
                     }

                     z.pos_gl = z.glbModel.findFirst(new Object[]{z.ein_enr, gleis.ELEMENT_EINFAHRT});
                     z.pos_gl.getFluentData().setStatusByZug(2, z);
                     z.zugbelegt.addLast(z.pos_gl);
                     if (z.pos_gl.needExtraRight()) {
                        gleis g = z.glbModel.getXY_null(z.pos_gl.getCol() - 1, z.pos_gl.getRow());
                        if (g != null && g.getElement() != gleis.ELEMENT_SIGNAL) {
                           gleis next_gl = z.pos_gl.next(null);
                           if (g.sameGleis(next_gl)) {
                              g.getFluentData().setStatusByZug(2, z);
                              z.fromRightExtra = true;
                           }
                        }
                     }

                     z.before_gl = null;
                     if (z.gotüp) {
                        z.tempo_pos = -10.0;
                     } else if (z.eingangbelegt) {
                        z.tempo_pos = -z.ist_tempo * 90.66666666666667;
                     } else if (z.emitted) {
                        z.tempo_pos = -10.0;
                     } else {
                        z.tempo_pos = -z.ist_tempo * 90.66666666666667;
                     }

                     z.eingangbelegt = false;
                     if (zug.debugMode != null) {
                        zug.debugMode.writeln("zug (" + z.getName() + ")", "Vorlauf");
                     }

                     z.reportPosition(z.pos_gl.getENR(), 0);
                  }

                  z.startCnt = 5;
                  z.outputValueChanged = z.von.setBGColor(1) | z.outputValueChanged;
                  ret = true;
               } else {
                  z.eingangbelegt = true;
               }
            }
         }

         return ret;
      } else {
         return this.callFalse(z);
      }
   }
}
