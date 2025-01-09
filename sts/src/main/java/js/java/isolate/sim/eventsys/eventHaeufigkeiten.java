package js.java.isolate.sim.eventsys;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.eventsys.events.bahnuebergangoffenfrage;
import js.java.isolate.sim.eventsys.events.bahnuebergangstoerung;
import js.java.isolate.sim.eventsys.events.bahnuebergangwaerter;
import js.java.isolate.sim.eventsys.events.displayausfall;
import js.java.isolate.sim.eventsys.events.randomsignalstoerung;
import js.java.isolate.sim.eventsys.events.randomweichestoerung;
import js.java.isolate.sim.eventsys.events.relaisgruppestoerung;
import js.java.isolate.sim.eventsys.events.sicherungausfall;
import js.java.isolate.sim.eventsys.events.stellwerkausfall;
import js.java.isolate.sim.eventsys.events.weichenfsstoerung;
import js.java.isolate.sim.eventsys.events.weichenheizungstoerung;
import js.java.isolate.sim.eventsys.events.weichenueberwachung;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.structServ.structinfo;
import js.java.schaltungen.adapter.simPrefs;
import js.java.schaltungen.timesystem.TimeFormat;

public class eventHaeufigkeiten extends trigger implements structinfo {
   private static FATwriter debugMode = null;
   public static boolean stoerungenein = true;
   private static eventHaeufigkeiten dumpInstance = null;
   private final LinkedList<eventHaeufigkeiten.dumpLog> eventLog = new LinkedList();
   private final TimeFormat sdf = TimeFormat.getInstance(TimeFormat.STYLE.HM);
   private final HashMap<eventHaeufigkeiten.HAEUFIGKEITEN, String> namen = new HashMap();
   private final gleisbildModelEventsys my_gleisbild;
   private final long startTime = System.currentTimeMillis();
   private long nextOccureUntil = 0L;
   private final Simulator sim;
   private double MODUS_FACTOR = 1.0;
   eventHaeufigkeiten.timereader treader;
   private final Random rnd = new Random();
   private static final int TICK = 15;
   private HashMap<eventHaeufigkeiten.HAEUFIGKEITEN, LinkedList<eventContainer>> events = new HashMap();
   private static long[] lastOccure = new long[eventHaeufigkeiten.HAEUFIGKEITEN.values().length];
   private static final long[] nextOccure = new long[eventHaeufigkeiten.HAEUFIGKEITEN.values().length];
   private long messuretime;
   private long lastping = 0L;

   public static void dump() {
      try {
         dumpInstance.dumpData();
      } catch (Exception var1) {
      }
   }

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public static eventHaeufigkeiten create(gleisbildModelEventsys my_gleisbild) {
      eventHaeufigkeiten eh = my_gleisbild.getHaeufigkeiten();
      dumpInstance = eh;
      return eh;
   }

   private void dumpData() {
      System.out.println("event log dump: start");

      for (eventHaeufigkeiten.dumpLog l : this.eventLog) {
         System.out.println(l.toString());
      }

      System.out.println("event log dump: end");
   }

   public void dumpData(PrintWriter out) {
      out.println("MFactor: " + this.MODUS_FACTOR);
      out.println("Runtime: " + (System.currentTimeMillis() - this.startTime) / 1000L / 60L + " Minuten");
      out.println("event log dump: start");

      for (eventHaeufigkeiten.dumpLog l : this.eventLog) {
         out.println(l.toString());
      }

      out.println("event log dump: end");
   }

   public eventHaeufigkeiten(gleisbildModelEventsys _my_gleisbild, Simulator sim) {
      this.my_gleisbild = _my_gleisbild;
      this.sim = sim;
      if (sim != null) {
         simPrefs prefs = new simPrefs();
         switch (prefs.getInt("plannedtime", 1)) {
            case 0:
               this.MODUS_FACTOR = 0.7;
               break;
            case 1:
            default:
               this.MODUS_FACTOR = 1.0;
               break;
            case 2:
               this.MODUS_FACTOR = 3.0;
               break;
            case 3:
               this.MODUS_FACTOR = 6.0;
         }
      }

      this.namen.put(eventHaeufigkeiten.HAEUFIGKEITEN.sehrselten, "<html>sehr selten <i>(ca. 1x pro Tag *)</i></html>");
      this.namen.put(eventHaeufigkeiten.HAEUFIGKEITEN.selten, "<html>selten <i>(ca. alle 3-4 Stunden *)</i></html>");
      this.namen.put(eventHaeufigkeiten.HAEUFIGKEITEN.gelegentlich, "<html>gelegentlich <i>(ca. alle 1-2 Stunden *)</i></html>");
      this.namen.put(eventHaeufigkeiten.HAEUFIGKEITEN.regelmaessig, "<html>regelmäßig <i>(ca. 1x pro Stunden *)</i></html>");
      this.namen.put(eventHaeufigkeiten.HAEUFIGKEITEN.oft, "<html>oft <i>(ca. mindestens alle 30 Minuten *)</i></html>");
      this.namen.put(eventHaeufigkeiten.HAEUFIGKEITEN.sehroft, "<html>sehr oft <i>(ca. mindestens alle 15 Minuten)</i></html>");
      this.namen.put(eventHaeufigkeiten.HAEUFIGKEITEN.immer, "<html>immer <i>(so oft wie möglich)</i></html>");
   }

   public String get(eventHaeufigkeiten.HAEUFIGKEITEN h) {
      return (String)this.namen.get(h);
   }

   public static int value2int(eventHaeufigkeiten.HAEUFIGKEITEN v) {
      return v.ordinal();
   }

   public static eventHaeufigkeiten.HAEUFIGKEITEN fromString(String v) {
      return v == null
         ? eventHaeufigkeiten.HAEUFIGKEITEN.gelegentlich
         : (eventHaeufigkeiten.HAEUFIGKEITEN)Enum.valueOf(eventHaeufigkeiten.HAEUFIGKEITEN.class, v);
   }

   public void close() {
      this.eventLog.clear();

      for (LinkedList<eventContainer> e : this.events.values()) {
         for (eventContainer ee : e) {
            ee.close();
         }

         e.clear();
      }

      this.events.clear();
      dumpInstance = null;
   }

   public Simulator getSim() {
      return this.sim;
   }

   private long getSimutime() {
      return this.treader.getSimutime();
   }

   private String getSimutimeString() {
      return this.treader.getSimutimeString();
   }

   private long rnd1(eventHaeufigkeiten.HAEUFIGKEITEN h, long t, int m) {
      long r = Math.round(((double)this.rnd.nextInt(m) + 6.0) / ((double)m + 2.0) * (double)m);
      this.eventLog.add(new eventHaeufigkeiten.dl_rnd(h, 1, m, r));
      return r;
   }

   private long rnd2(eventHaeufigkeiten.HAEUFIGKEITEN h, long t, int m) {
      long r = Math.round((double)m + this.rnd.nextGaussian() * ((double)m / 4.0));
      if (r < (long)(-m)) {
         r = (long)m;
      }

      this.eventLog.add(new eventHaeufigkeiten.dl_rnd(h, 2, m, r));
      return r;
   }

   public long random(eventHaeufigkeiten.HAEUFIGKEITEN h) {
      double ret = 0.0;
      long l = this.getSimutime();
      switch (h) {
         case sehrselten:
            ret = (double)this.rnd2(h, l, 300) * this.MODUS_FACTOR;
            break;
         case selten:
            ret = (double)this.rnd2(h, l, 210) * this.MODUS_FACTOR;
            break;
         case gelegentlich:
            ret = (double)this.rnd2(h, l, 100) * this.MODUS_FACTOR;
            break;
         case regelmaessig:
            ret = (double)this.rnd2(h, l, 60) * this.MODUS_FACTOR;
            break;
         case oft:
            ret = (double)this.rnd1(h, l, 30) * this.MODUS_FACTOR;
            break;
         case sehroft:
            ret = (double)this.rnd1(h, l, 15);
            break;
         case immer:
            ret = 0.0;
      }

      long t = (long)((double)l + ret * 60000.0);
      this.eventLog.add(new eventHaeufigkeiten.dl_random(h, t, l, ret, ret * 60000.0));
      if (debugMode != null) {
         debugMode.writeln("random(" + h + ")= " + t + " (" + l + " + " + ret + ")");
      }

      return t;
   }

   private boolean occure(eventHaeufigkeiten.HAEUFIGKEITEN h, LinkedList<eventContainer> a) {
      if (debugMode != null) {
         this.eventLog.add(new eventHaeufigkeiten.dl_str("occure(" + h + "," + a.size() + ")"));
      }

      if (h == eventHaeufigkeiten.HAEUFIGKEITEN.immer) {
         if (this.pauseOccure()) {
            nextOccure[h.ordinal()] = this.random(h);
         } else {
            Iterator<eventContainer> it = a.iterator();

            while (it.hasNext()) {
               eventContainer ev = (eventContainer)it.next();
               if (event.createEvent(ev, this.my_gleisbild, this.sim) != null) {
                  lastOccure[h.ordinal()] = this.getSimutime();
                  if (ev.isOnce()) {
                     it.remove();
                  }
               } else {
                  this.eventLog.add(new eventHaeufigkeiten.dl_str("100%EV not created: " + ev.getName()));
                  if (debugMode != null) {
                     debugMode.writeln("100%EV not created: " + ev.getName());
                  }
               }
            }
         }

         return false;
      } else {
         if (nextOccure[h.ordinal()] < this.getSimutime()) {
            if (this.pauseOccure()) {
               nextOccure[h.ordinal()] = this.random(h);
            } else {
               Collections.shuffle(a);
               eventContainer ev = (eventContainer)a.getFirst();
               if (event.createEvent(ev, this.my_gleisbild, this.sim) != null) {
                  this.eventLog.add(new eventHaeufigkeiten.dl_str("EV created: " + ev.getName()));
                  if (debugMode != null) {
                     debugMode.writeln("EV created: " + ev.getName());
                  }

                  if (a.size() < 3 || !ev.getFactory().isIndependantEvent()) {
                     lastOccure[h.ordinal()] = this.getSimutime();
                  }

                  if (ev.isOnce()) {
                     this.remove(h, ev);
                  }

                  nextOccure[h.ordinal()] = this.random(h);
                  return ev.getFactory().isStopFollowing();
               }

               this.eventLog.add(new eventHaeufigkeiten.dl_str("EV not created: " + ev.getName()));
               if (debugMode != null) {
                  debugMode.writeln("EV not created: " + ev.getName());
               }
            }
         }

         return false;
      }
   }

   private eventContainer occureMessure(eventHaeufigkeiten.HAEUFIGKEITEN h, LinkedList<eventContainer> a) {
      if (nextOccure[h.ordinal()] >= this.getSimutime()) {
         return null;
      } else {
         Collections.shuffle(a);
         eventContainer ev = (eventContainer)a.getFirst();
         if (a.size() < 3 || !ev.getFactory().isIndependantEvent()) {
            lastOccure[h.ordinal()] = this.getSimutime();
         }

         nextOccure[h.ordinal()] = this.random(h);
         System.out.println("next: " + nextOccure[h.ordinal()] + " // " + this.getSimutime());
         return ev;
      }
   }

   public HashMap<eventContainer, eventHaeufigkeiten.messureResult> messure(int num, eventHaeufigkeiten.messureUpdate mu) {
      this.events.clear();
      lastOccure = new long[eventHaeufigkeiten.HAEUFIGKEITEN.values().length];
      this.treader = new eventHaeufigkeiten.timereader() {
         private final TimeFormat tf = TimeFormat.getInstance(TimeFormat.STYLE.HM);

         @Override
         public long getSimutime() {
            return eventHaeufigkeiten.this.messuretime;
         }

         @Override
         public String getSimutimeString() {
            return this.tf.format(eventHaeufigkeiten.this.messuretime);
         }
      };
      this.messuretime = 86400000L;

      for (eventHaeufigkeiten.HAEUFIGKEITEN h : eventHaeufigkeiten.HAEUFIGKEITEN.values()) {
         this.events.put(h, new LinkedList());
         lastOccure[h.ordinal()] = this.getSimutime();
         nextOccure[h.ordinal()] = this.random(h);
      }

      HashMap<eventContainer, eventHaeufigkeiten.messureResult> ret = new HashMap();

      for (eventContainer ev : this.my_gleisbild.events) {
         if (ev != null && ev.getFactory() != null) {
            if (ev.getFactory().isRandom()) {
               eventHaeufigkeiten.HAEUFIGKEITEN v = ev.getFactory().getOccurrence(ev);
               int w = ev.getFactory().getWeight(ev);
               LinkedList<eventContainer> a = (LinkedList<eventContainer>)this.events.get(v);

               for (int i = 0; i < w; i++) {
                  a.add(ev);
               }
            }

            ret.put(ev, new eventHaeufigkeiten.messureResult());
         }
      }

      for (int n = 0; n < num / 15; n++) {
         this.messuretime = 86400000L + (long)(n * 15) * 1000L;
         if (mu != null) {
            mu.update(n * 15);
         }

         for (eventHaeufigkeiten.HAEUFIGKEITEN h : eventHaeufigkeiten.HAEUFIGKEITEN.values()) {
            LinkedList<eventContainer> a = (LinkedList<eventContainer>)this.events.get(h);
            if (!a.isEmpty()) {
               eventContainer evx = this.occureMessure(h, a);
               if (evx != null) {
                  eventHaeufigkeiten.messureResult cnt = (eventHaeufigkeiten.messureResult)ret.get(evx);
                  cnt.cnt++;
                  cnt.time.add((this.getSimutime() - 86400000L) / 60000L);
               }
            }
         }
      }

      if (mu != null) {
         mu.update(num);
      }

      return ret;
   }

   public void initTJM(final gleisbildSimControl control) {
      this.events.clear();
      this.treader = new eventHaeufigkeiten.timereader() {
         @Override
         public long getSimutime() {
            return control.getSimTime().getSimutime();
         }

         @Override
         public String getSimutimeString() {
            return control.getSimTime().getSimutimeString();
         }
      };

      for (eventHaeufigkeiten.HAEUFIGKEITEN h : eventHaeufigkeiten.HAEUFIGKEITEN.values()) {
         this.events.put(h, new LinkedList());
         nextOccure[h.ordinal()] = this.random(h);
         lastOccure[h.ordinal()] = this.getSimutime();
      }

      if (stoerungenein) {
         boolean randomSig = false;
         boolean randomWei = false;
         boolean wfsstoerung = true;
         boolean dispstoerung = true;
         boolean relaisstoerung = false;
         boolean weichenuebstoerung = true;
         boolean sicherungausfall = false;
         boolean stellwerkausfall = true;
         boolean buestoerung = false;

         for (eventContainer ev : this.my_gleisbild.events) {
            if (ev.isAllowUse()) {
               if (ev.getFactory() != null) {
                  Class<? extends event> e = ev.getFactory().getEventTyp();
                  randomWei = randomWei || e.isAssignableFrom(randomweichestoerung.class);
                  randomSig = randomSig || e.isAssignableFrom(randomsignalstoerung.class);
                  wfsstoerung = wfsstoerung || e.isAssignableFrom(weichenfsstoerung.class);
                  dispstoerung = dispstoerung || e.isAssignableFrom(displayausfall.class);
                  relaisstoerung = relaisstoerung || e.isAssignableFrom(relaisgruppestoerung.class);
                  weichenuebstoerung = weichenuebstoerung || e.isAssignableFrom(weichenueberwachung.class);
                  sicherungausfall = sicherungausfall || e.isAssignableFrom(sicherungausfall.class);
                  stellwerkausfall = stellwerkausfall || e.isAssignableFrom(stellwerkausfall.class);
                  buestoerung = buestoerung || e.isAssignableFrom(bahnuebergangstoerung.class);
                  if (!ev.getFactory().isRandom()) {
                     if (event.createEvent(ev, this.my_gleisbild, this.sim) == null) {
                     }
                  } else {
                     eventHaeufigkeiten.HAEUFIGKEITEN v = ev.getFactory().getOccurrence(ev);
                     int w = ev.getFactory().getWeight(ev);
                     LinkedList<eventContainer> a = (LinkedList<eventContainer>)this.events.get(v);

                     for (int i = 0; i < w; i++) {
                        a.add(ev);
                     }
                  }
               } else {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "no factory: " + ev.getName() + " type " + ev.getTyp());
               }
            }
         }

         if (this.my_gleisbild.events.size() < 30) {
            if (!randomWei) {
               eventContainer evx = new eventContainer(this.my_gleisbild, randomweichestoerung.class);
               evx.setIntValue("dauer", eventFactory.random(7, 20));
               evx.setValue("stark", true);
               this.add2(eventHaeufigkeiten.HAEUFIGKEITEN.regelmaessig, evx);
            }

            if (!randomSig) {
               eventContainer evx = new eventContainer(this.my_gleisbild, randomsignalstoerung.class);
               evx.setIntValue("dauer", eventFactory.random(7, 20));
               evx.setValue("stark", true);
               this.add2(eventHaeufigkeiten.HAEUFIGKEITEN.regelmaessig, evx);
            }

            if (!wfsstoerung) {
               eventContainer evx = new eventContainer(this.my_gleisbild, weichenfsstoerung.class);
               evx.setIntValue("dauer", eventFactory.random(7, 15));
               this.add2(eventHaeufigkeiten.HAEUFIGKEITEN.selten, evx);
            }

            if (!dispstoerung) {
               eventContainer evx = new eventContainer(this.my_gleisbild, displayausfall.class);
               evx.setIntValue("dauer", eventFactory.random(4, 18));
               this.add2(eventHaeufigkeiten.HAEUFIGKEITEN.selten, evx);
            }

            if (!relaisstoerung) {
               eventContainer evx = new eventContainer(this.my_gleisbild, relaisgruppestoerung.class);
               evx.setIntValue("dauer", eventFactory.random(4, 18));
               evx.setValue("stark", true);
               this.add2(eventHaeufigkeiten.HAEUFIGKEITEN.gelegentlich, evx);
            }

            if (!weichenuebstoerung) {
               eventContainer evx = new eventContainer(this.my_gleisbild, weichenueberwachung.class);
               evx.setIntValue("dauer", eventFactory.random(4, 10));
               evx.setValue("stark", true);
               this.add2(eventHaeufigkeiten.HAEUFIGKEITEN.gelegentlich, evx);
            }

            if (!sicherungausfall) {
               eventContainer evx = new eventContainer(this.my_gleisbild, sicherungausfall.class);
               evx.setIntValue("dauer", eventFactory.random(4, 10));
               this.add2(eventHaeufigkeiten.HAEUFIGKEITEN.gelegentlich, evx);
            }

            if (!stellwerkausfall) {
               eventContainer evx = new eventContainer(this.my_gleisbild, stellwerkausfall.class);
               this.add2(eventHaeufigkeiten.HAEUFIGKEITEN.gelegentlich, evx);
            }
         }

         eventContainer evx = new eventContainer(this.my_gleisbild, bahnuebergangoffenfrage.class);
         this.add(eventHaeufigkeiten.HAEUFIGKEITEN.immer, evx);
         evx = new eventContainer(this.my_gleisbild, bahnuebergangwaerter.class);
         this.add(eventHaeufigkeiten.HAEUFIGKEITEN.immer, evx);
         if (thema.isThema("schwerer_Winter") && thema.userVotedThema("schwerer_Winter") && eventFactory.random(0, 20) > 14) {
            evx = new eventContainer(this.my_gleisbild, weichenheizungstoerung.class);
            this.add(eventHaeufigkeiten.HAEUFIGKEITEN.immer, evx);
         }

         this.tjm_add();
      }
   }

   @Override
   public boolean ping() {
      if (stoerungenein) {
         this.tjm_add();
         if ((this.getControl().getSimTime().getSimutime() - this.lastping) / 1000L > 15L) {
            this.lastping = this.getControl().getSimTime().getSimutime();
            boolean stopNext = false;

            for (eventHaeufigkeiten.HAEUFIGKEITEN h : eventHaeufigkeiten.HAEUFIGKEITEN.values()) {
               if (h != eventHaeufigkeiten.HAEUFIGKEITEN.immer && stopNext) {
                  nextOccure[h.ordinal()] = this.random(h);
               } else {
                  LinkedList<eventContainer> a = (LinkedList<eventContainer>)this.events.get(h);
                  if (!a.isEmpty()) {
                     stopNext |= this.occure(h, a);
                  }
               }
            }
         }
      }

      return false;
   }

   public void add(eventHaeufigkeiten.HAEUFIGKEITEN h, eventContainer ev) {
      LinkedList<eventContainer> a = (LinkedList<eventContainer>)this.events.get(h);
      ev.setOnce(true);

      try {
         a.add(ev);
      } catch (NullPointerException var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "eventH catch add", var5);
      }
   }

   public void add2(eventHaeufigkeiten.HAEUFIGKEITEN h, eventContainer ev) {
      LinkedList<eventContainer> a = (LinkedList<eventContainer>)this.events.get(h);

      try {
         a.add(ev);
      } catch (NullPointerException var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "eventH catch add2", var5);
      }
   }

   private void remove(eventHaeufigkeiten.HAEUFIGKEITEN h, eventContainer ev) {
      LinkedList<eventContainer> a = (LinkedList<eventContainer>)this.events.get(h);
      a.remove(ev);
   }

   private boolean pauseOccure() {
      return this.nextOccureUntil > 0L && this.getControl().getSimTime().getSimutime() < this.nextOccureUntil;
   }

   public void pauseEvents(int minutes) {
      this.nextOccureUntil = this.getControl().getSimTime().getSimutime() + (long)minutes * 60000L;
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("time");
      v.addElement(this.getSimutime());

      for (eventHaeufigkeiten.HAEUFIGKEITEN h : eventHaeufigkeiten.HAEUFIGKEITEN.values()) {
         v.addElement("next " + h.name());
         v.addElement(nextOccure[h.ordinal()] + "");
      }

      for (eventHaeufigkeiten.HAEUFIGKEITEN h : eventHaeufigkeiten.HAEUFIGKEITEN.values()) {
         v.addElement("last " + h.name());
         v.addElement(lastOccure[h.ordinal()] + "");
      }

      return v;
   }

   @Override
   public String getStructName() {
      return "eventHaeufigkeiten";
   }

   public static enum HAEUFIGKEITEN {
      sehrselten,
      selten,
      gelegentlich,
      regelmaessig,
      oft,
      sehroft,
      immer;
   }

   private class dl_random extends eventHaeufigkeiten.dumpLog {
      private final eventHaeufigkeiten.HAEUFIGKEITEN h;
      private final long t;
      private final long l;
      private final long ret;
      private final long tret;

      private dl_random(eventHaeufigkeiten.HAEUFIGKEITEN h, long t, long l, double ret, double tret) {
         this.h = h;
         this.t = t;
         this.l = l;
         this.ret = Math.round(ret);
         this.tret = Math.round(tret);
      }

      @Override
      public String toString() {
         return this.time
            + ": random("
            + this.h
            + ") = "
            + eventHaeufigkeiten.this.sdf.format(this.t)
            + " ("
            + eventHaeufigkeiten.this.sdf.format(this.l)
            + " + "
            + this.ret
            + ") # "
            + this.tret;
      }
   }

   private class dl_rnd extends eventHaeufigkeiten.dumpLog {
      private final eventHaeufigkeiten.HAEUFIGKEITEN h;
      private final long r;
      private final int m;
      private final int mode;

      private dl_rnd(eventHaeufigkeiten.HAEUFIGKEITEN h, int mode, int m, long r) {
         this.h = h;
         this.r = r;
         this.mode = mode;
         this.m = m;
      }

      @Override
      public String toString() {
         return this.time + ": rnd" + this.mode + "(" + this.h + "," + this.m + ")=" + this.r;
      }
   }

   private class dl_str extends eventHaeufigkeiten.dumpLog {
      private final String s;

      dl_str(String s) {
         this.s = s;
      }

      @Override
      public String toString() {
         return this.time + ": " + this.s;
      }
   }

   private class dumpLog {
      protected String time = eventHaeufigkeiten.this.getSimutimeString();

      private dumpLog() {
      }

      public String toString() {
         return "dummy";
      }
   }

   public class messureResult {
      public int cnt = 0;
      public LinkedList<Long> time = new LinkedList();
   }

   public interface messureUpdate {
      void update(int var1);
   }

   interface timereader {
      long getSimutime();

      String getSimutimeString();
   }
}
