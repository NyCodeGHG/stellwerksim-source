package js.java.isolate.sim.gleisbild.gleisbildWorker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class eaConnectionTracking extends gleisbildWorkerBase<gleisbildModelFahrweg> {
   private ConcurrentSkipListSet<eaConnectionTracking.eaConnection> iwege = new ConcurrentSkipListSet();
   private LinkedList<eaConnectionTracking.aWay> wege = new LinkedList();
   private ThreadPoolExecutor threads;

   public eaConnectionTracking(gleisbildModelFahrweg gl, GleisAdapter main) {
      this(gl, main, 4);
   }

   public eaConnectionTracking(gleisbildModelFahrweg gl, GleisAdapter main, int cores) {
      super(gl, main);
      this.threads = (ThreadPoolExecutor)Executors.newFixedThreadPool(cores);
   }

   public LinkedList<eaConnectionTracking.eaConnection> run() {
      try {
         this.runDirect();
         this.runHead();
      } catch (InterruptedException var2) {
      }

      return this.prepareResult();
   }

   private void runDirect() throws InterruptedException {
      System.out.println("Direkt-Test");
      LinkedList<Future> futures = new LinkedList();
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_EINFAHRT});

      while(it.hasNext()) {
         final gleis eingl = (gleis)it.next();
         gleis gl = eingl;
         gleis before_gl = null;

         final gleis next_gl;
         do {
            next_gl = gl.next(before_gl);
            if (next_gl == null || gl.sameGleis(next_gl)) {
               break;
            }

            before_gl = gl;
            gl = next_gl;
            if (next_gl.getElement() == gleis.ELEMENT_SIGNAL && next_gl.forUs(before_gl)) {
               futures.add(this.threads.submit(new Runnable() {
                  public void run() {
                     System.out.println("Start Einfahrt: " + eingl.getSWWert());
                     eaConnectionTracking.this.runFahrstrassen(next_gl, eingl, new LinkedList());
                     System.out.println("Ende  Einfahrt: " + eingl.getSWWert());
                  }
               }));
               break;
            }
         } while(next_gl == null);
      }

      for(Future f : futures) {
         if (!f.isDone()) {
            try {
               f.get();
            } catch (ExecutionException var8) {
               var8.printStackTrace();
            }
         }
      }

      System.out.println("/Direkt-Test");
   }

   private void runFahrstrassen(gleis signal, gleis einfahrt, LinkedList<fahrstrasse> weg) {
      Iterator<fahrstrasse> it = this.glbModel.fahrstrassenIterator();

      while(it.hasNext()) {
         fahrstrasse fs = (fahrstrasse)it.next();
         if (!weg.contains(fs) && !fs.getExtend().isDeleted() && fs.getStart() == signal && !fs.isRFonly()) {
            LinkedList<fahrstrasse> newweg = new LinkedList(weg);
            newweg.add(fs);
            if (fs.getStop().getElement().matches(gleis.ELEMENT_AUSFAHRT)) {
               System.out.println("+ " + einfahrt.getSWWert() + " -> " + fs.getStop().getSWWert() + ": " + newweg.size());
               this.addDirectWay(einfahrt, fs.getStop(), newweg);
            } else if (fs.getStop().getElement().matches(gleis.ELEMENT_SIGNAL)) {
               this.runFahrstrassen(fs.getStop(), einfahrt, newweg);
            }
         }
      }
   }

   private synchronized void addDirectWay(gleis einfahrt, gleis ausfahrt, LinkedList<fahrstrasse> weg) {
      this.wege.add(new eaConnectionTracking.aWay(einfahrt, ausfahrt, weg));
   }

   private void runHead() throws InterruptedException {
      System.out.println("Kreuz-Test");
      LinkedList<Future> futures = new LinkedList();

      for(eaConnectionTracking.aWay w1 : this.wege) {
         futures.add(this.searchSub(w1));
      }

      for(Future f : futures) {
         if (!f.isDone()) {
            try {
               f.get();
            } catch (ExecutionException var5) {
               var5.printStackTrace();
            }
         }
      }

      System.out.println("/Kreuz-Test");
   }

   private Future searchSub(final eaConnectionTracking.aWay w1) {
      return this.threads
         .submit(
            new Runnable() {
               public void run() {
                  System.out.println("K: " + w1.getEinfahrt().getSWWert() + " -> " + w1.getAusfahrt().getSWWert());
      
                  for(eaConnectionTracking.aWay w2 : eaConnectionTracking.this.wege) {
                     if (w1 != w2
                        && w1.getEinfahrt() != w2.getEinfahrt()
                        && w1.getAusfahrt() != w2.getAusfahrt()
                        && !eaConnectionTracking.this.alreadyAdded(w1.getEinfahrt(), w2.getAusfahrt())) {
                        for(fahrstrasse fs1 : w1.weg) {
                           for(fahrstrasse fs2 : w2.weg) {
                              if (fs2.intersects(fs1, gleis.ELEMENT_KREUZUNGBRUECKE, gleis.ELEMENT_KREUZUNG)) {
                                 eaConnectionTracking.this.addIndirectWay(w1.getEinfahrt(), w2.getAusfahrt());
                                 break;
                              }
                           }
                        }
                     }
                  }
               }
            }
         );
   }

   private void addIndirectWay(gleis einfahrt, gleis ausfahrt) {
      this.iwege.add(new eaConnectionTracking.eaConnection(einfahrt, ausfahrt, false));
   }

   private boolean alreadyAdded(gleis einfahrt, gleis ausfahrt) {
      for(eaConnectionTracking.eaConnection w1 : this.iwege) {
         if (w1.getEinfahrt() == einfahrt && w1.getAusfahrt() == ausfahrt) {
            return true;
         }
      }

      return false;
   }

   private LinkedList<eaConnectionTracking.eaConnection> prepareResult() {
      System.out.println("prepareResult");
      LinkedList<eaConnectionTracking.eaConnection> ret = new LinkedList();

      for(eaConnectionTracking.eaConnection w1 : this.wege) {
         boolean found = false;

         for(eaConnectionTracking.eaConnection w2 : ret) {
            if (w2.getEinfahrt() == w1.getEinfahrt() && w2.getAusfahrt() == w1.getAusfahrt()) {
               found = true;
               break;
            }
         }

         if (!found) {
            ret.add(w1);
         }
      }

      for(eaConnectionTracking.eaConnection w1 : this.iwege) {
         boolean found = false;

         for(eaConnectionTracking.eaConnection w2 : ret) {
            if (w2.getEinfahrt() == w1.getEinfahrt() && w2.getAusfahrt() == w1.getAusfahrt()) {
               found = true;
               break;
            }
         }

         if (!found) {
            ret.add(w1);
         }
      }

      System.out.println("/prepareResult");
      return ret;
   }

   private static class aWay extends eaConnectionTracking.eaConnection {
      final LinkedList<fahrstrasse> weg = new LinkedList();

      aWay(gleis einfahrt, gleis ausfahrt, LinkedList<fahrstrasse> weg) {
         super(einfahrt, ausfahrt, true);
         this.weg.addAll(weg);
      }
   }

   public static class eaConnection extends eaConnectionTracking.startStop implements Comparable {
      private final boolean direct;

      eaConnection(gleis einfahrt, gleis ausfahrt, boolean direct) {
         super(einfahrt, ausfahrt);
         this.direct = direct;
      }

      public boolean isDirect() {
         return this.direct;
      }

      public String toString() {
         return this.einfahrt.getSWWert_special()
            + ","
            + this.einfahrt.getENR()
            + ","
            + this.ausfahrt.getSWWert_special()
            + ","
            + this.ausfahrt.getENR()
            + ","
            + (this.direct ? "D" : "K");
      }

      public int compareTo(Object o) {
         eaConnectionTracking.eaConnection other = (eaConnectionTracking.eaConnection)o;
         int r = this.einfahrt.getENR() - other.einfahrt.getENR();
         if (r == 0) {
            r = this.ausfahrt.getENR() - other.ausfahrt.getENR();
         }

         return r;
      }
   }

   public static class startStop {
      protected final gleis einfahrt;
      protected final gleis ausfahrt;

      startStop(gleis einfahrt, gleis ausfahrt) {
         super();
         this.einfahrt = einfahrt;
         this.ausfahrt = ausfahrt;
      }

      public gleis getEinfahrt() {
         return this.einfahrt;
      }

      public gleis getAusfahrt() {
         return this.ausfahrt;
      }
   }
}
