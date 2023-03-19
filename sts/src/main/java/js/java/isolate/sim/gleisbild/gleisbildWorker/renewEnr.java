package js.java.isolate.sim.gleisbild.gleisbildWorker;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;

public class renewEnr extends gleisbildWorkerBase<gleisbildModelFahrweg> {
   public renewEnr(gleisbildModelFahrweg gl, GleisAdapter main) {
      super(gl, main);
   }

   public void renew() {
      this.doRenew(false);
   }

   public void fullRenew() {
      this.glbModel.changeC(50);
      this.glbModel.clearFahrwege();
      this.doRenew(true);
   }

   private void doRenew(boolean fullRenew) {
      this.glbModel.allOff();
      BitSet enrset = new BitSet();
      if (fullRenew) {
         this.loop(enrset, new renewEnr.initEnrFull());
      } else {
         this.loop(enrset, new renewEnr.initEnr());
      }

      if (fullRenew) {
         this.loop(enrset, new renewEnr.filterElement(gleis.ELEMENT_EINFAHRT), new renewEnr.partnerEnrOrSet(1));
         this.loop(enrset, new renewEnr.filterElement(gleis.ELEMENT_AUSFAHRT), new renewEnr.partnerEnrOrSet(21));
      } else {
         this.loopDoubleCheck(enrset, new renewEnr.filterElement(gleis.ELEMENT_EINFAHRT), new renewEnr.cleanDoubles(1));
         this.loopDoubleCheck(enrset, new renewEnr.filterElement(gleis.ELEMENT_AUSFAHRT), new renewEnr.cleanDoubles(21));
      }

      renewEnr.multiCall mc = new renewEnr.multiCall(new renewEnr.partnerEnr(60));
      mc.put(gleis.ELEMENT_SPRUNG, new renewEnr.partnerEnr(40, 2));
      this.loop(enrset, mc);
      this.loop(enrset, new renewEnr.setEnr(100));
      this.glbModel.repaint();
   }

   private void loop(BitSet enrset, renewEnr.caller cal) {
      this.loop(enrset, null, cal);
   }

   private void loop(BitSet enrset, renewEnr.condition cond, renewEnr.caller cal) {
      for(gleis gl : this.glbModel) {
         if (cond == null || cond.check(gl)) {
            cal.call(gl, enrset);
         }
      }
   }

   private void loopDoubleCheck(BitSet enrset, renewEnr.condition cond, renewEnr.caller cal) {
      BitSet seenset = new BitSet();

      for(gleis gl : this.glbModel) {
         if (cond == null || cond.check(gl)) {
            if (seenset.get(Math.abs(gl.getENR()))) {
               cal.call(gl, enrset);
            } else {
               seenset.set(Math.abs(gl.getENR()));
            }
         }
      }
   }

   private interface caller {
      void call(gleis var1, BitSet var2);
   }

   private class cleanDoubles extends renewEnr.setEnr {
      cleanDoubles() {
         super();
      }

      cleanDoubles(int base) {
         super(base);
      }

      @Override
      public void call(gleis gl, BitSet enrset) {
         int oldenr = Math.abs(gl.getENR());
         gl.setENR(0);
         super.call(gl, enrset);
         renewEnr.this.my_main.showStatus("Doppelte ENR für " + oldenr + ": " + gl.getSWWert() + ", ersetzt zu " + Math.abs(gl.getENR()), 2);
      }
   }

   private interface condition {
      boolean check(gleis var1);
   }

   private class filterElement implements renewEnr.condition {
      private final element te;

      filterElement(element element) {
         super();
         this.te = element;
      }

      @Override
      public boolean check(gleis gl) {
         return this.te.matches(gl.getElement());
      }
   }

   private class initEnr implements renewEnr.caller {
      private initEnr() {
         super();
      }

      @Override
      public void call(gleis gl, BitSet enrset) {
         if (!gl.typKeepEnr() && !gl.typHasEnrPartner()) {
            gl.setENR(0);
         } else if (gl.typHasEnrPartner() && !gl.typKeepEnr()) {
            gl.setENR(-gl.getENR());
         }

         if (gl.getENR() > 0) {
            enrset.set(gl.getENR());
         }
      }
   }

   private class initEnrFull implements renewEnr.caller {
      private initEnrFull() {
         super();
      }

      @Override
      public void call(gleis gl, BitSet enrset) {
         if (!gl.typHasEnrPartner()) {
            gl.setENR(0);
         } else if (gl.typHasEnrPartner()) {
            gl.setENR(-gl.getENR());
         }
      }
   }

   private class multiCall extends HashMap<element, renewEnr.caller> implements renewEnr.caller {
      private renewEnr.caller fallback;

      multiCall() {
         this(null);
      }

      multiCall(renewEnr.caller fallback) {
         super();
         this.fallback = fallback;
      }

      @Override
      public void call(gleis gl, BitSet enrset) {
         for(Entry<element, renewEnr.caller> e : this.entrySet()) {
            if (((element)e.getKey()).matches(gl.getElement())) {
               ((renewEnr.caller)e.getValue()).call(gl, enrset);
            }
         }

         if (this.fallback != null) {
            this.fallback.call(gl, enrset);
         }
      }
   }

   private class partnerEnr implements renewEnr.caller {
      protected final int base;
      protected final int max;

      partnerEnr() {
         this(1, 0);
      }

      partnerEnr(int base) {
         this(base, 0);
      }

      partnerEnr(int base, int max) {
         super();
         this.base = base;
         this.max = max;
      }

      @Override
      public void call(gleis gl, BitSet enrset) {
         if (gl.typHasEnrPartner() && gl.getENR() < 0) {
            int enr = enrset.nextClearBit(this.base);

            for(element te : gl.typPartner()) {
               int cc = 0;
               Iterator<gleis> it = renewEnr.this.glbModel.findIterator(new Object[]{te, gl.getENR()});

               while(it.hasNext()) {
                  gleis gl2 = (gleis)it.next();
                  if (gl2 != gl) {
                     if (this.max > 0 && cc >= this.max) {
                        gleisTypContainer gtc = gleisTypContainer.getInstance();
                        renewEnr.this.my_main.showStatus("Zu viele ENR-Verbindungen für " + gtc.getTypElementName(gl) + " " + enr + ": " + gl.getSWWert(), 2);
                        break;
                     }

                     gl2.setENR(enr);
                     ++cc;
                  }
               }
            }

            gl.setENR(enr);
            enrset.set(gl.getENR());
         }
      }
   }

   private class partnerEnrOrSet extends renewEnr.partnerEnr {
      private renewEnr.setEnr setter;

      partnerEnrOrSet() {
         this(1);
      }

      partnerEnrOrSet(int base) {
         super(base);
         this.setter = renewEnr.this.new setEnr(base);
      }

      @Override
      public void call(gleis gl, BitSet enrset) {
         super.call(gl, enrset);
         this.setter.call(gl, enrset);
      }
   }

   private class setEnr implements renewEnr.caller {
      protected final int base;

      setEnr() {
         this(1);
      }

      setEnr(int base) {
         super();
         this.base = base;
      }

      @Override
      public void call(gleis gl, BitSet enrset) {
         if (gl.typRequiresENR() && gl.getENR() == 0) {
            int enr = enrset.nextClearBit(this.base);
            gl.setENR(enr);
            enrset.set(gl.getENR());
         }
      }
   }
}
