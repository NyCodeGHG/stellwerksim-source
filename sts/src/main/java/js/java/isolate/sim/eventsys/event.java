package js.java.isolate.sim.eventsys;

import java.lang.reflect.Constructor;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;
import js.java.isolate.sim.sim.TEXTTYPE;
import js.java.isolate.sim.structServ.structinfo;
import js.java.isolate.sim.toolkit.HyperlinkCaller;

public abstract class event extends trigger implements structinfo, Comparable, HyperlinkCaller, eventGenerator.eventCall, eventParent {
   public static ConcurrentLinkedQueue<event> events = new ConcurrentLinkedQueue();
   private static ConcurrentLinkedQueue<event> callWaits = new ConcurrentLinkedQueue();
   private static String basePath = event.class.getPackage().getName() + ".events.";
   private static int globalHash = 16;
   private final int hash = globalHash++;
   private long now = 0L;
   private int min = 0;
   private int pingmode = 0;
   eventContainer my_container = null;
   private eventParent parent = null;
   protected final String code;
   private callBehaviour callHook = null;
   private boolean called = false;
   private static int codeCounter = 1;
   private static final int PM_FINISH = 1;
   private static final int PM_PONG = 2;
   private static final int PM_FASTPONG = 3;
   public gleisbildModelEventsys glbModel;
   public final Simulator my_main;

   public static void clear() {
      events.clear();
      callWaits.clear();
   }

   public static event createEvent(eventContainer e, gleisbildModelEventsys glb, Simulator sim) {
      return createEvent(e, glb, null, sim);
   }

   public static event createEvent(eventContainer e, gleisbildModelEventsys glb, eventParent parent, Simulator sim) {
      if (e.runningEvent != null) {
         if (eventContainer.isDebug()) {
            eventContainer.getDebug().writeln("event twice: " + e.getName());
         }

         return null;
      } else {
         boolean running = false;
         event ret = null;
         String typ = e.getTyp();
         if (typ != null) {
            try {
               Class c = e.getFactory().getEventTyp();
               if (c != null) {
                  Constructor constr = c.getConstructor(Simulator.class);
                  Object o = constr.newInstance(sim);
                  ret = (event)o;
                  ret.glbModel = glb;
                  ret.my_container = e;
                  e.runningEvent = ret;
                  if (parent != null) {
                     ret.setParent(parent);
                  }

                  running = ret.init(e);
               }
            } catch (IllegalAccessException var10) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "no factory found (ill): " + e.getTyp(), var10);
            } catch (InstantiationException var11) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "no factory found (inst): " + e.getTyp(), var11);
            } catch (Exception var12) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "exception: " + e.getTyp(), var12);
            }

            if (eventContainer.isDebug()) {
               eventContainer.getDebug().writeln("init done for: " + e.getName() + " = " + running);
            }

            if (!running && ret != null) {
               ret.eventDone();
               ret = null;
               e.runningEvent = null;
            }
         }

         return ret;
      }
   }

   public static Class getEventTyp(eventContainer e) {
      Class ret = null;
      String typ = e.getTyp();
      if (typ != null) {
         try {
            ret = Class.forName(basePath + typ);
         } catch (ClassNotFoundException var4) {
         } catch (Exception var5) {
            System.out.println("Ex: " + var5);
            var5.printStackTrace();
            Logger.getLogger("stslogger").log(Level.SEVERE, "Caught getEventType", var5);
         }
      }

      return ret;
   }

   public Vector getStructInfo() {
      Vector v = new Vector();
      v.addElement("Event");
      if (this.my_container != null) {
         v.addElement(this.my_container.getName());
      } else {
         v.addElement(this.getClass().getSimpleName());
      }

      v.addElement(this);
      return v;
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("Name");
      if (this.my_container != null) {
         v.addElement(this.my_container.getName());
      } else {
         v.addElement(this.getClass().getSimpleName());
      }

      v.addElement("hash");
      v.addElement("0x" + Integer.toString(this.hash, 16));
      v.addElement("Class");
      v.addElement(this.getClass().getSimpleName());
      v.addElement("now");
      v.addElement(this.now + "");
      v.addElement("min");
      v.addElement(this.min + "");
      v.addElement("pingmode");
      v.addElement(this.pingmode + "");
      v.addElement("done?");
      v.addElement(Boolean.toString(this.isEventDone()));
      v.addElement("call enabled?");
      v.addElement(Boolean.toString(callWaits.contains(this)));
      v.addElement("called?");
      v.addElement(Boolean.toString(this.called));
      v.addElement("code");
      v.addElement(this.code);
      if (this.parent != null) {
         v.addElement("Parent");
         v.addElement(this.parent.getClass().getSimpleName());
      }

      return v;
   }

   @Override
   public String getStructName() {
      return "event";
   }

   public int getHash() {
      return this.hash;
   }

   public int compareTo(Object o) {
      if (o instanceof eventGenerator.eventCall) {
         eventGenerator.eventCall e = (eventGenerator.eventCall)o;
         String s1 = this.funkName();
         String s2 = e.funkName();
         if (s1 != null && s2 != null) {
            return s1.compareToIgnoreCase(s2);
         } else {
            return s1 != null ? -1 : 1;
         }
      } else {
         return -1;
      }
   }

   public String toString() {
      String s = this.funkName();
      return s != null ? s : "";
   }

   protected event(Simulator m) {
      super();
      this.my_main = m;
      String c = String.format("%1d%02d%1d", (int)(Math.random() * 8.0), ++codeCounter * 5 % 100, codeCounter % 10);
      this.code = c;
      events.add(this);
   }

   protected event(Simulator m, String code) {
      super();
      this.my_main = m;
      this.code = code;
      events.add(this);
   }

   @Override
   public final boolean ping() {
      if (this.pingmode == 3) {
         boolean r = this.pong();
         this.tjm_add();
         return r;
      } else if (this.now == 0L || this.min <= 0) {
         return false;
      } else {
         if ((this.my_main.getTimeSystem().getSimutime() - this.now) / 60000L >= (long)this.min) {
            this.now = 0L;
            this.min = 0;
            switch(this.pingmode) {
               case 1:
                  this.eventDone();
                  return false;
               case 2:
                  return this.pong();
            }
         } else {
            this.tjm_add();
         }

         return false;
      }
   }

   protected abstract boolean init(eventContainer var1);

   public abstract String getText();

   @Override
   public boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
      return true;
   }

   @Override
   public void clicked(String url) {
   }

   protected void startCall(String token) {
      if (this.callHook != null) {
         this.callHook.called(this, token);
      }
   }

   public boolean pong() {
      return false;
   }

   @Override
   public String funkName() {
      return null;
   }

   public String funkAntwort() {
      return null;
   }

   @Override
   public void done(event child) {
   }

   public void abort() {
   }

   protected final void showMessageNow(String s) {
      this.my_main.showText(s, TEXTTYPE.STMESSAGE, this);
      this.my_main.getAudio().playMessage();
   }

   protected final void showMessageNow(String s, TEXTTYPE t) {
      if (t == TEXTTYPE.STMESSAGE) {
         this.showMessageNow(s);
      } else if (t == TEXTTYPE.STANRUF) {
         this.showCallMessageNow(s);
      } else {
         this.my_main.showText(s, t, this);
      }
   }

   protected final void showCallMessageNow(String s) {
      this.my_main.showText(s, TEXTTYPE.STANRUF, this, this);
      this.my_main.getAudio().playAnruf();
   }

   public final void setParent(eventParent p) {
      this.parent = p;
   }

   public final boolean hasParent() {
      return this.parent != null;
   }

   protected final void eventDone() {
      events.remove(this);
      if (callWaits.contains(this)) {
         callWaits.remove(this);
      }

      this.resetTimer();

      try {
         this.my_container.runningEvent = null;
      } catch (Exception var3) {
      }

      try {
         this.parent.done(this);
      } catch (NullPointerException var2) {
      }

      this.parent = null;
      this.my_container = null;
      this.my_main.finishText(this);
   }

   public final boolean isEventDone() {
      return !events.contains(this);
   }

   private void setPong(int m, int pm) {
      boolean wasalready = this.now != 0L;
      this.now = this.my_main.getSimutime();
      if (m <= 0) {
         m = 0;
      }

      this.min = m;
      this.pingmode = pm;
      if (!wasalready) {
         this.tjm_add();
      }
   }

   protected final void resetTimer() {
      this.now = 0L;
      this.min = 0;
      this.pingmode = 0;
   }

   protected final void finishIn(int m) {
      this.setPong(m, 1);
   }

   protected final void callMeIn(int m) {
      this.setPong(m, 2);
   }

   protected final void callMe() {
      this.setPong(1, 3);
   }

   public final int restTime() {
      int ret = 0;
      if (this.now != 0L && this.min > 0) {
         ret = this.min - (int)((this.my_main.getSimutime() - this.now) / 60000L);
      }

      return ret;
   }

   public final int runTime() {
      int ret = 0;
      if (this.now != 0L && this.min > 0) {
         ret = (int)((this.my_main.getSimutime() - this.now) / 60000L);
      }

      return ret;
   }

   public final void reduceRestTime(int rmin) {
      if (this.pingmode == 2 || this.pingmode == 1) {
         this.min = Math.max(1, this.min - rmin);
      }
   }

   protected final void registerCallBehaviour(callBehaviour cb) {
      this.callHook = cb;
   }

   public static void startActivityCall(String code, String token) {
      for(event e : callWaits) {
         if (code.equals(e.code)) {
            startActivityCall(e, token);
         }
      }
   }

   protected static void callUncalled() {
      for(event e : callWaits) {
         if (!e.called) {
            startActivityCall(e, "");
         }
      }
   }

   public static void startActivityCall(event e, String token) {
      callWaits.remove(e);
      e.called = true;
      e.startCall(token);
   }

   protected final boolean stitzCalling() {
      return this.my_main.isCaller();
   }

   protected final String getCallText() {
      return this.getCallText("um die Reparatur zu beginnen.");
   }

   protected final String getCallText(String work) {
      String t = "<hr>";
      if (this.stitzCalling()) {
         t = t + "Rufen Sie via StiTz die 7863";
      } else {
         t = t + "Rufen Sie via Funk (Hotline) die Störungsannahme";
      }

      return t + " und geben den Störungscode <b>" + this.code + "</b> an " + work;
   }

   protected final String getCode() {
      return this.code;
   }

   protected final void acceptingCall() {
      if (!callWaits.contains(this)) {
         this.called = false;
         callWaits.add(this);
      }
   }

   protected boolean waitingForCall() {
      return callWaits.contains(this);
   }

   protected boolean isCalled() {
      return this.called;
   }

   public static int random(int min, int max) {
      return eventFactory.random(min, max);
   }
}
