package js.java.isolate.sim.eventsys;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;

public abstract class gleisevent extends event {
   gleisevent.fq_stellung fq_st = new gleisevent.fq_stellung();
   gleisevent.fq_zug fq_zg = new gleisevent.fq_zug();

   protected gleisevent(Simulator sim) {
      super(sim);
   }

   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      return true;
   }

   public boolean hookStatus(gleis g, int s, zug z) {
      return true;
   }

   public boolean hookFSSpeicher(gleis g, fahrstrasse f, boolean fssstart) {
      return true;
   }

   @Override
   public final boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
      if (e != null && e instanceof gleismsg) {
         gleismsg ge = (gleismsg)e;
         if (typ == eventGenerator.T_GLEIS_STATUS) {
            return this.hookStatus(ge.g, ge.s, ge.z);
         }

         if (typ == eventGenerator.T_GLEIS_STELLUNG) {
            return this.hookStellung(ge.g, ge.st, ge.f);
         }

         if (typ == eventGenerator.T_GLEIS_FSSPEICHER) {
            return this.hookFSSpeicher(ge.g, ge.f, ge.fsstart);
         }
      }

      return true;
   }

   protected final boolean hasRegisteredForStellung(gleis g) {
      return g.hasHookRegistered(eventGenerator.T_GLEIS_STELLUNG, this.getClass());
   }

   protected final void registerForStellung(gleis g) {
      g.registerHook(eventGenerator.T_GLEIS_STELLUNG, this);
   }

   protected final void unregisterForStellung(gleis g) {
      g.unregisterHook(eventGenerator.T_GLEIS_STELLUNG, this);
   }

   protected final boolean hasRegisteredForZug(gleis g) {
      return g.hasHookRegistered(eventGenerator.T_GLEIS_STATUS, this.getClass());
   }

   protected final void registerForZug(gleis g) {
      g.registerHook(eventGenerator.T_GLEIS_STATUS, this);
   }

   protected final void unregisterForZug(gleis g) {
      g.unregisterHook(eventGenerator.T_GLEIS_STATUS, this);
   }

   protected final boolean hasRegisteredForSpeicher(gleis g) {
      return g.hasHookRegistered(eventGenerator.T_GLEIS_FSSPEICHER, this.getClass());
   }

   protected final void registerForSpeicher(gleis g) {
      g.registerHook(eventGenerator.T_GLEIS_FSSPEICHER, this);
   }

   protected final void unregisterForSpeicher(gleis g) {
      g.registerHook(eventGenerator.T_GLEIS_FSSPEICHER, this);
   }

   private long getFilterLimit(LinkedList<gleis> ll, gleisevent.filterQ f) {
      TreeSet<Long> max = new TreeSet();

      for(gleis gl : ll) {
         max.add(f.getCnt(gl));
      }

      long limit = 0L;
      int i = 0;

      for(Iterator<Long> itl = max.descendingIterator(); itl.hasNext(); ++i) {
         limit = itl.next();
         if (i > 20) {
            break;
         }
      }

      return limit;
   }

   protected void filterHeavyZug(LinkedList<gleis> ll) {
      long limit = this.getFilterLimit(ll, this.fq_zg);
      Iterator<gleis> it = ll.iterator();

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         if (gl.getCntZug() < limit) {
            it.remove();
         }
      }
   }

   protected void filterHeavyStellung(LinkedList<gleis> ll) {
      long limit = this.getFilterLimit(ll, this.fq_st);
      Iterator<gleis> it = ll.iterator();

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         if (gl.getCntStellung() < limit) {
            it.remove();
         }
      }
   }

   private interface filterQ {
      long getCnt(gleis var1);
   }

   private class fq_stellung implements gleisevent.filterQ {
      private fq_stellung() {
         super();
      }

      @Override
      public long getCnt(gleis gl) {
         return gl.getCntStellung();
      }
   }

   private class fq_zug implements gleisevent.filterQ {
      private fq_zug() {
         super();
      }

      @Override
      public long getCnt(gleis gl) {
         return gl.getCntZug();
      }
   }
}
