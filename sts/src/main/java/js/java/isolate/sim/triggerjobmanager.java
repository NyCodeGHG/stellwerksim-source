package js.java.isolate.sim;

import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.structServ.structinfo;
import js.java.schaltungen.moduleapi.SessionClose;

public class triggerjobmanager extends TimerTask implements structinfo, SessionClose {
   private static FATwriter debugMode = null;
   private final gleisbildSimControl glbControl;
   private final java.util.Timer timer;
   private final triggerjobmanager.triggerList[] jobqueue = new triggerjobmanager.triggerList[2];
   private int currentQueue = 0;
   private boolean running = true;
   private long lastQueueSwitch = 0L;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public triggerjobmanager(gleisbildSimControl _my_gleisbild) {
      this.glbControl = _my_gleisbild;
      trigger.tjm = this;

      for (int i = 0; i < this.jobqueue.length; i++) {
         this.jobqueue[i] = new triggerjobmanager.triggerList();
      }

      this.timer = new java.util.Timer("TJM");
      this.timer.scheduleAtFixedRate(this, 2000L, 600L);
   }

   public void tjm_stop() {
      this.running = false;
      this.cancel();
      this.timer.cancel();

      for (triggerjobmanager.triggerList jobqueue1 : this.jobqueue) {
         jobqueue1.clear();
      }

      this.glbControl.paintBuffer();
      trigger.tjm = null;
   }

   @Override
   public void close() {
      this.tjm_stop();
   }

   public void add(trigger j) {
      if (this.running) {
         this.jobqueue[this.currentQueue].add(j);
      }
   }

   public void addDirectRun(final trigger j) {
      TimerTask tt = new TimerTask() {
         public void run() {
            j.ping();
         }
      };
      this.timer.schedule(tt, 10L);
   }

   public gleisbildSimControl getControl() {
      return this.glbControl;
   }

   public void run() {
      try {
         if (this.glbControl.getSimTime().isPause()) {
            return;
         }

         gleis.blinkon = !gleis.blinkon;
         if (gleis.blinkon) {
            gleis.blinkon_slow = !gleis.blinkon_slow;
         }

         if (!gleis.blinkon_3er) {
            gleis.blinkon_3er = true;
            gleis.blinkon_3erCC = 2;
         } else if (--gleis.blinkon_3erCC <= 0) {
            gleis.blinkon_3er = false;
         }

         if (this.jobqueue[this.currentQueue].isEmpty()) {
            return;
         }

         int runQueue = this.currentQueue;
         this.currentQueue = 1 - this.currentQueue;
         this.lastQueueSwitch = System.currentTimeMillis();
         if (debugMode != null) {
            debugMode.writeln(
               "TJM",
               "start queue["
                  + runQueue
                  + "]: "
                  + this.jobqueue[runQueue].size()
                  + ", queue["
                  + this.currentQueue
                  + "]: "
                  + this.jobqueue[this.currentQueue].size()
            );
         }

         boolean rp = false;

         trigger t;
         while ((t = this.jobqueue[runQueue].getNextElement()) != null && this.running) {
            try {
               rp = t.ping() || rp;
            } catch (Exception var5) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "Caught, TJM Ping", var5);
            }
         }

         if (rp) {
            this.glbControl.paintBuffer();
         }

         if (debugMode != null) {
            debugMode.writeln(
               "TJM",
               "end queue["
                  + runQueue
                  + "]: "
                  + this.jobqueue[runQueue].size()
                  + ", queue["
                  + this.currentQueue
                  + "]: "
                  + this.jobqueue[this.currentQueue].size()
            );
         }
      } catch (Exception var6) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught, TJM run", var6);
      }
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("running");
      v.addElement(this.running + "");
      v.addElement("currentQueue");
      v.addElement(this.currentQueue + "");

      for (int i = 0; i < this.jobqueue.length; i++) {
         v.addElement("jobqueue[" + i + "]");
         v.addElement(this.jobqueue[i].size() + "");
      }

      v.addElement("lastQueueSwitch");
      v.addElement(this.lastQueueSwitch + "");
      return v;
   }

   @Override
   public String getStructName() {
      return "TJM";
   }

   private static class triggerList {
      private ConcurrentLinkedQueue<trigger> queue = new ConcurrentLinkedQueue();

      private triggerList() {
      }

      void clear() {
         this.queue.clear();
      }

      void add(trigger t) {
         if (!this.queue.contains(t)) {
            this.queue.add(t);
         }
      }

      trigger getNextElement() {
         return (trigger)this.queue.poll();
      }

      boolean isEmpty() {
         return this.queue.isEmpty();
      }

      int size() {
         return this.queue.size();
      }
   }
}
