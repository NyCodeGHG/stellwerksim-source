package js.java.isolate.sim.gleisbild.gleisbildWorker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fw_doppelt_interface;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse_extend;

public class fahrwegCalculator extends gleisbildWorkerBase<gleisbildModelFahrweg> {
   public static final int ED_FW_CTL = 0;
   public static final int ED_FW_RUN = 1;
   public static final int ED_FW_PROGRESS_VALUE = 2;
   public static final int ED_FW_PROGRESS_MAX = 3;
   public static final int ED_FW_DOUBLES = 4;
   private final fahrwegCalculator.callHook caller;
   private int fahrstrassennummer;
   private HashMap seenSignals = null;
   private boolean stopCalc = false;
   private HashSet<element> signalElements;

   public fahrwegCalculator(gleisbildModelFahrweg glb, GleisAdapter main, fahrwegCalculator.callHook c) {
      super(glb, main);
      this.caller = c;
   }

   private void showPanel(int p, int v) {
      this.caller.showPanel(p, v);
   }

   private boolean checkIfExists(fahrstrasse f2) {
      return this.glbModel.checkIfExists(f2);
   }

   private void calcStrecke(gleis start, gleis before_gl) throws Exception {
      boolean üpfound = false;
      gleis gl = start;
      LinkedList stack = new LinkedList();
      LinkedList<gleis> gleisweg = new LinkedList();
      HashMap<gleis, gleisElements.Stellungen> stellungen = new HashMap();
      boolean firstRun = true;
      DecimalFormat df = new DecimalFormat("000");
      this.repaint();

      while (!this.stopCalc) {
         gl.getFluentData().setStatus(2);
         if (this.isSignal(gl)) {
            gl.getFluentData().setStellung(gleisElements.ST_SIGNAL_GRÜN);
         }

         gleis next_gl = gl.next(before_gl);
         if (next_gl == null) {
            if (stack.isEmpty()) {
               break;
            }

            stellungen = (HashMap<gleis, gleisElements.Stellungen>)stack.removeLast();
            gleisweg = (LinkedList<gleis>)stack.removeLast();
            before_gl = (gleis)stack.removeLast();
            gl = (gleis)stack.removeLast();
            gl.getFluentData().setStellung(gleisElements.ST_WEICHE_ABZWEIG);
         } else if (gl.sameGleis(next_gl)) {
            gl.getFluentData().setStellung(gleisElements.ST_WEICHE_ABZWEIG);
         } else {
            if (next_gl.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT && next_gl.forUs(gl)) {
               üpfound = true;
            }

            if (üpfound
               && next_gl.getElement() != gleis.ELEMENT_ÜBERGABEPUNKT
               && next_gl.getElement() != gleis.ELEMENT_STRECKE
               && next_gl.getElement() != gleis.ELEMENT_AUSFAHRT
               && next_gl.getElement() != gleis.ELEMENT_EINFAHRT
               && next_gl.getElement() != gleis.ELEMENT_SPRUNG) {
               stack.clear();
               System.out.println("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt! " + next_gl.getElement());
               this.showStatus("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!", 2);
               throw new Exception("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!");
            }

            if (gl.getElement() == gleis.ELEMENT_WEICHEOBEN || gl.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
               if (üpfound) {
                  stack.clear();
                  System.out.println("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!");
                  this.showStatus("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!", 2);
                  throw new Exception("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!");
               }

               if (gl.getFluentData().getStellung() == gleisElements.ST_WEICHE_GERADE && gl.weicheSpitz(before_gl)) {
                  stack.addLast(gl);
                  stack.addLast(before_gl);
                  stack.addLast(gleisweg.clone());
                  stack.addLast(stellungen.clone());
                  firstRun = false;
                  this.glbModel.addMarkedGleis(gl);
               }
            }

            gleisweg.addLast(gl);
            if (gl.getElement() == gleis.ELEMENT_WEICHEOBEN || gl.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
               stellungen.put(gl, gl.getFluentData().getStellung());
            }

            if ((!this.isEndSignal(next_gl) || !next_gl.forUs(gl)) && next_gl.getElement() != gleis.ELEMENT_AUSFAHRT) {
               if (next_gl.getElement() == gleis.ELEMENT_WEICHEOBEN || next_gl.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
                  if (üpfound) {
                     stack.clear();
                     System.out.println("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!");
                     this.showStatus("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!", 2);
                     throw new Exception("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!");
                  }

                  next_gl.getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE);
               }

               before_gl = gl;
               gl = next_gl;
            } else {
               gleisweg.addLast(gl);
               this.fahrstrassennummer++;
               String name = "f" + df.format((long)this.fahrstrassennummer) + "/" + start.getENR() + "-" + next_gl.getENR();

               for (gleis g : stellungen.keySet()) {
                  g.getFluentData().setStellung((gleisElements.Stellungen)stellungen.get(g));
               }

               fahrstrasse f = new fahrstrasse(name, start, next_gl, gleisweg);
               if (!this.checkIfExists(f) && (start.getElement() == gleis.ELEMENT_SIGNAL || next_gl.getElement() != gleis.ELEMENT_AUSFAHRT || !üpfound)) {
                  this.glbModel.addFahrweg(f);
                  f.paintWeg();
               }

               if (this.isEndSignal(next_gl)) {
                  if (üpfound) {
                     stack.clear();
                     System.out.println("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!");
                     this.showStatus("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!", 2);
                     throw new Exception("Fehler im Gleisbild! Zwischen Übergabepunkte und Ausfahrt anderes Element nicht erlaubt!");
                  }

                  if (this.seenSignals.get(next_gl) == null) {
                     this.seenSignals.put(next_gl, next_gl);

                     try {
                        this.calcStrecke(next_gl, gl);
                     } catch (StackOverflowError var15) {
                        stack.clear();
                        System.out.println("Endlosloop Fehler im Gleisbild!");
                        this.showStatus("Endlosschleife Fehler im Gleisbild!", 2);
                        throw new Exception("Endlosschleife Fehler im Gleisbild!");
                     }
                  }
               }

               üpfound = false;
               if (stack.isEmpty()) {
                  break;
               }

               stellungen = (HashMap<gleis, gleisElements.Stellungen>)stack.removeLast();
               gleisweg = (LinkedList<gleis>)stack.removeLast();
               before_gl = (gleis)stack.removeLast();
               gl = (gleis)stack.removeLast();
               gl.getFluentData().setStellung(gleisElements.ST_WEICHE_ABZWEIG);
               üpfound = false;
            }
         }
      }
   }

   private void loopPerEingang(gleis gl) throws Exception {
      this.glbModel.setSelectedGleis(gl);
      this.repaint();
      this.loopPerEingang(gl, null);
      this.glbModel.setSelectedGleis(null);
      this.glbModel.clearMarkedGleis();
   }

   private void loopPerEingang(gleis gl, gleis before_gl) throws Exception {
      this.glbModel.smallClearStatus();
      this.seenSignals = new HashMap();
      this.showStatus("Startpunkt: " + gl.getCol() + "/" + gl.getRow() + " Enr: " + gl.getENR(), 3);
      synchronized (this) {
         gleis next_gl;
         do {
            next_gl = gl.next(before_gl);
            if (gleis.ALLE_WEICHEN.matches(gl.getElement())) {
               if (gl.weicheSpitz(before_gl)) {
                  try {
                     gleis g1 = gl.nextWeichenAst(false);
                     gleis g2 = gl.nextWeichenAst(true);
                     if (g1 != null) {
                        this.loopPerEingang(g1, gl);
                     }

                     if (g2 != null) {
                        this.loopPerEingang(g2, gl);
                     }
                     break;
                  } catch (StackOverflowError var8) {
                     System.out.println("Endlosloop Fehler im Gleisbild!");
                     this.showStatus("Endlosschleife Fehler im Gleisbild!", 2);
                     throw new Exception("Endlosschleife Fehler im Gleisbild!");
                  }
               }

               if (next_gl == null && gl.getFluentData().getStellung() == gleis.ST_WEICHE_GERADE) {
                  gl.getFluentData().setStellung(gleis.ST_WEICHE_ABZWEIG);
                  continue;
               }

               if (next_gl != null && gl.sameGleis(next_gl) && gl.getFluentData().getStellung() == gleis.ST_WEICHE_GERADE) {
                  gl.getFluentData().setStellung(gleis.ST_WEICHE_ABZWEIG);
                  continue;
               }
            }

            if (next_gl != null && !gl.sameGleis(next_gl)) {
               before_gl = gl;
               gl = next_gl;
               if (this.isEndSignal(next_gl) && next_gl.forUs(before_gl)) {
                  this.showStatus("Analyse ab Signal Enr: " + next_gl.getENR(), 3);
                  this.calcStrecke(next_gl, before_gl);
                  break;
               }
            } else {
               next_gl = null;
            }
         } while (next_gl != null);
      }
   }

   private boolean isEndSignal(gleis gl) {
      return this.signalElements.contains(gl.getElement());
   }

   private boolean isSignal(gleis gl) {
      return gl.getElement() == gleis.ELEMENT_SIGNAL || gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL || gl.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL;
   }

   private void calcFahrwege_run() {
      int oldfwnum = this.glbModel.countFahrwege();
      ArrayList<fahrstrasse> oldfahrwege = this.glbModel.cloneFahrwege();
      this.glbModel.clearFahrwege();
      renewEnr r = new renewEnr(this.glbModel, this.my_main);
      r.renew();
      fahrstrasse.fserror = false;
      LinkedList eingang_stack = new LinkedList();
      this.fahrstrassennummer = 0;

      for (gleis gls : this.glbModel) {
         gls.getFluentData().setStatus(0);

         try {
            if (gls.getElement() == gleis.ELEMENT_EINFAHRT) {
               gls.getFluentData().setStatus(2);
               eingang_stack.addLast(gls);
            } else if (gls.getElement() == gleis.ELEMENT_STRECKE && gls.openGleis() && gls.kopfGleis()) {
               gls.getFluentData().setStatus(2);
               eingang_stack.addLast(gls);
               this.showStatus("Kopfgleis gefunden (" + gls.getCol() + "/" + gls.getRow() + ")", 3);
            } else if (gls.getElement() == gleis.ELEMENT_AUSFAHRT && gls.nextByRichtung(true).getElement() != gleis.ELEMENT_EINFAHRT) {
               gls.getFluentData().setStatus(2);
               eingang_stack.addLast(gls);
            }
         } catch (NullPointerException var19) {
         }
      }

      this.showStatus("Startpunkte: " + eingang_stack.size(), 3);
      int size100 = eingang_stack.size() * 2 + 1;
      this.showPanel(3, size100);

      try {
         this.signalElements = new HashSet();
         this.signalElements.add(gleis.ELEMENT_SIGNAL);
         Iterator<gleis> eit = eingang_stack.iterator();
         int cc = 0;

         while (eit.hasNext() && !this.stopCalc) {
            gleis gl = (gleis)eit.next();
            this.showPanel(2, cc);
            this.loopPerEingang(gl);
            cc++;
            this.showStatus("Fahrstraßen: " + this.glbModel.countFahrwege(), 3);
         }

         this.signalElements = new HashSet();
         this.signalElements.add(gleis.ELEMENT_SIGNAL);
         this.signalElements.add(gleis.ELEMENT_ZWERGSIGNAL);
         eit = eingang_stack.iterator();

         while (eit.hasNext() && !this.stopCalc) {
            gleis gl = (gleis)eit.next();
            this.showPanel(2, cc);
            this.loopPerEingang(gl);
            cc++;
            this.showStatus("Fahrstraßen: " + this.glbModel.countFahrwege(), 3);
         }

         this.glbModel.smallClearStatus();
         this.repaint();
         this.writeOldFlags(oldfahrwege);
         boolean removed = false;
         boolean doubleMode = false;
         this.setProgress(0);
         int pMax = 0;

         do {
            boolean oneDeleted = false;
            removed = false;
            int pCnt = 0;
            Iterator<fahrstrasse> it = this.glbModel.iteratorFahrwege();

            while (!removed && it.hasNext()) {
               if (++pCnt > pMax) {
                  int p = 100 * pCnt / this.glbModel.countFahrwege();
                  pMax = pCnt;
                  this.setProgress(p);
               }

               this.glbModel.setFocus(null);
               fw_doppelt_interface dia = null;
               fahrstrasse f = (fahrstrasse)it.next();
               Iterator<fahrstrasse> it2 = this.glbModel.iteratorFahrwege();

               while (!removed && it2.hasNext()) {
                  fahrstrasse f2 = (fahrstrasse)it2.next();
                  if (f2.getExtend().isDeleted()) {
                     oneDeleted = true;
                  }

                  switch (f2.compare(f)) {
                     case 1:
                        if (dia == null) {
                           if (!doubleMode) {
                              this.showPanel(4, 0);
                              doubleMode = true;
                           }

                           dia = this.caller.new_fw_doppelt_class(oldfahrwege);
                           dia.add(f);
                           this.glbModel.setFocus(f.getStart());
                        }

                        dia.add(f2);
                  }
               }

               if (dia != null) {
                  removed = true;
                  dia.start();
                  if (dia.getReturn() == -2) {
                     this.glbModel.clearFahrwege();
                     break;
                  }

                  for (fahrstrasse f2 : dia.getDelList()) {
                     this.glbModel.removeFahrweg(f2);
                  }
               }
            }
         } while (removed);

         this.setProgress(100);
         this.writeOldFlags(oldfahrwege);
         this.glbModel.changeC(Math.abs(oldfwnum - this.glbModel.countFahrwege()) * 1);
      } catch (Exception var18) {
      }

      this.glbModel.smallClearStatus();
      this.showStatus("Fahrstraßen: " + this.glbModel.countFahrwege(), 3);
      this.showPanel(2, size100);
      this.repaint();
      System.gc();
   }

   private void writeOldFlags(ArrayList<fahrstrasse> oldfahrwege) {
      if (oldfahrwege != null) {
         Iterator<fahrstrasse> it = this.glbModel.iteratorFahrwege();

         while (it.hasNext()) {
            fahrstrasse f1 = (fahrstrasse)it.next();
            fahrstrasse_extend f1e = f1.getExtend();
            if (f1e.getFSType() != 4) {
               for (fahrstrasse f2 : oldfahrwege) {
                  if (f2.checkThisClever(f1.getStart(), f1.getStop())) {
                     fahrstrasse_extend f2e = f2.getExtend();
                     f1e.setFSType(f2e.getFSType());
                  }
               }
            }
         }
      }
   }

   public void stopCalcFahrwege() {
      this.stopCalc = true;
   }

   private void calcFahrwege() {
      this.showPanel(1, 0);
      this.stopCalc = false;
      this.calcFahrwege_run();
      this.showPanel(0, 0);
   }

   public void startThread() {
      Thread t = new Thread(new Runnable() {
         public void run() {
            fahrwegCalculator.this.calcFahrwege();
         }
      });
      t.setName("FScalc");
      t.start();
   }

   public interface callHook {
      void showPanel(int var1, int var2);

      fw_doppelt_interface new_fw_doppelt_class(ArrayList<fahrstrasse> var1);
   }
}
