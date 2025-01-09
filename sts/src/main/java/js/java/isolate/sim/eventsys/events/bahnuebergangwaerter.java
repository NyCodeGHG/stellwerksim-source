package js.java.isolate.sim.eventsys.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.gleisevent;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;

public class bahnuebergangwaerter extends gleisevent {
   private HashMap<Integer, bahnuebergangwaerter.timeStore> closeTime = new HashMap();

   public bahnuebergangwaerter(Simulator sim) {
      super(sim);
   }

   @Override
   public String getText() {
      return null;
   }

   @Override
   protected boolean init(eventContainer e) {
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_WBAHNÜBERGANG});

      while (it.hasNext()) {
         gleis b = (gleis)it.next();
         this.registerForStellung(b);
         this.registerForZug(b);
         bahnuebergangwaerter.timeStore ts = (bahnuebergangwaerter.timeStore)this.closeTime.get(b.getENR());
         if (ts == null) {
            ts = new bahnuebergangwaerter.timeStore(b);
            this.closeTime.put(b.getENR(), ts);
         } else {
            ts.addGleis(b);
         }
      }

      if (!this.closeTime.isEmpty()) {
         this.callMe();
      }

      return true;
   }

   public boolean init(int enr) {
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_BAHNÜBERGÄNGE, enr});

      while (it.hasNext()) {
         gleis b = (gleis)it.next();
         this.registerForStellung(b);
         this.registerForZug(b);
         bahnuebergangwaerter.timeStore ts = (bahnuebergangwaerter.timeStore)this.closeTime.get(b.getENR());
         if (ts == null) {
            ts = new bahnuebergangwaerter.timeStore(b);
            this.closeTime.put(b.getENR(), ts);
         } else {
            ts.addGleis(b);
         }
      }

      if (!this.closeTime.isEmpty()) {
         this.callMe();
      }

      return true;
   }

   public void close(int enr) {
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ALLE_BAHNÜBERGÄNGE, enr});

      while (it.hasNext()) {
         gleis b = (gleis)it.next();
         this.unregisterForStellung(b);
         this.unregisterForZug(b);
      }

      this.closeTime.clear();
      this.resetTimer();
   }

   private bahnuebergangwaerter.timeStore findStore(gleis g) {
      return (bahnuebergangwaerter.timeStore)this.closeTime.get(g.getENR());
   }

   @Override
   public boolean hookStatus(gleis g, int s, zug z) {
      if (z == null && s == 3) {
         bahnuebergangwaerter.timeStore t = this.findStore(g);
         t.reserving();
      } else if (s == 2) {
         bahnuebergangwaerter.timeStore t = this.findStore(g);
         t.zug(false);
      }

      return true;
   }

   @Override
   public boolean hookStellung(gleis g, gleisElements.Stellungen st, fahrstrasse f) {
      if (st == gleis.ST_BAHNÜBERGANG_GESCHLOSSEN && g.getFluentData().getStellung() != gleis.ST_BAHNÜBERGANG_GESCHLOSSEN) {
         bahnuebergangwaerter.timeStore t = this.findStore(g);
         t.closing(g.getFluentData().getCurrentFS());
      } else if (st == gleis.ST_BAHNÜBERGANG_OFFEN && g.getFluentData().getStellung() != gleis.ST_BAHNÜBERGANG_OFFEN) {
         bahnuebergangwaerter.timeStore t = this.findStore(g);
         t.opening();
      }

      return true;
   }

   @Override
   public boolean pong() {
      long t = this.my_main.getSimutime();

      for (bahnuebergangwaerter.timeStore ts : this.closeTime.values()) {
         ts.check(t);
      }

      return false;
   }

   @Override
   public Vector getStructure() {
      Vector v = super.getStructure();

      for (bahnuebergangwaerter.timeStore ts : this.closeTime.values()) {
         v.addElement(ts.enr + ": enr");
         v.addElement(Integer.toString(ts.enr));
         v.addElement(ts.enr + ": name");
         v.addElement(((gleis)ts.gls.getFirst()).getElementName());
         v.addElement(ts.enr + ": closeZugWait");
         v.addElement(Boolean.toString(ts.closeZugWait));
         v.addElement(ts.enr + ": reservingWait");
         v.addElement(Boolean.toString(ts.reservingWait));
         v.addElement(ts.enr + ": closetime");
         v.addElement(Long.toString(ts.closetime));
         v.addElement(ts.enr + ": overTime");
         v.addElement(Integer.toString(ts.overTime));
         v.addElement(ts.enr + ": plusDelay");
         v.addElement(Integer.toString(ts.plusDelay));
         v.addElement(ts.enr + ": opentime");
         v.addElement(Long.toString(ts.opentime));
         v.addElement(ts.enr + ": waitUntilTime");
         v.addElement(Long.toString(ts.waitUntilTime));
         v.addElement(ts.enr + ": currentTime");
         v.addElement(Long.toString(this.my_main.getSimutime()));
         v.addElement(ts.enr + ": monitorSignal");
         v.addElement(ts.monitorSignal != null ? ts.monitorSignal.getElementName() : "-");
         v.addElement(ts.enr + ": geschlossenzeit");
         if (ts.closeZugWait) {
            v.addElement(Long.toString((this.my_main.getSimutime() - ts.closetime) / 1000L));
         } else {
            v.addElement("-");
         }
      }

      return v;
   }

   private class timeStore {
      private long closetime = 0L;
      private long opentime = 0L;
      private int plusDelay = 0;
      private int overTime = 0;
      private int enr = 0;
      private LinkedList<gleis> gls = new LinkedList();
      private boolean closeZugWait = false;
      private boolean reservingWait = false;
      private long waitUntilTime = 0L;
      private gleis monitorSignal = null;

      timeStore(gleis g) {
         this.gls.add(g);
         this.enr = g.getENR();
      }

      void addGleis(gleis g) {
         this.gls.add(g);
      }

      void reserving() {
         if (!this.reservingWait) {
            this.reservingWait = true;
            int o = Math.min(180, 60 + this.overTime + this.plusDelay);
            this.waitUntilTime = this.opentime + (long)o * 1000L;
            this.overTime = 0;
         }
      }

      void closing(fahrstrasse fs) {
         this.closetime = bahnuebergangwaerter.this.my_main.getSimutime();
         this.closeZugWait = true;
         this.reservingWait = false;
         if (fs != null) {
            this.monitorSignal = fs.getStart();
         }
      }

      void opening() {
         this.opentime = bahnuebergangwaerter.this.my_main.getSimutime();
         this.closeZugWait = false;
         this.reservingWait = false;
         this.monitorSignal = null;
         int closedelay = (int)((this.opentime - this.closetime) / 1000L);
         if (closedelay > 180) {
            this.overTime = closedelay - 180;
         }
      }

      void zug(boolean bySignal) {
         if (this.closeZugWait) {
            int firstZafterClose = (int)((bahnuebergangwaerter.this.my_main.getSimutime() - this.closetime) / 1000L);
            this.closeZugWait = false;
            this.plusDelay += firstZafterClose - 120;
            if (this.plusDelay < 0) {
               this.plusDelay = 0;
            } else if (this.plusDelay > 30) {
               this.plusDelay = 30;
            }
         }
      }

      void check(long t) {
         if (this.reservingWait && this.waitUntilTime <= t) {
            for (gleis gl : this.gls) {
               int status = gl.getFluentData().getStatus();
               if (status == 3) {
                  gl.getFluentData().setStellung(gleis.ST_BAHNÜBERGANG_GESCHLOSSEN);
                  break;
               }
            }

            this.reservingWait = false;
         } else if (this.closeZugWait && this.monitorSignal != null && this.monitorSignal.getFluentData().getStatus() == 2) {
            this.zug(true);
         }
      }
   }
}
