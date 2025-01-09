package js.java.isolate.sim.gleisbild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisGroup;
import js.java.isolate.sim.gleis.gleisGroupImpl;
import js.java.isolate.sim.gleis.gleisGroupNull;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasseSelection;
import js.java.isolate.sim.zug.zug;
import js.java.tools.ArrayListModel;
import js.java.tools.TextHelper;
import org.xml.sax.Attributes;

public class gleisbildModelFahrweg extends gleisbildModelStore {
   protected ArrayListModel<fahrstrasse> fahrwege = new ArrayListModel();
   public HashMap<String, Integer> wayTimeTable = null;
   protected HashMap<String, Integer> allWayTimeTable = null;
   protected HashMap<String, Integer> allWayTimeTableA = null;
   protected HashMap<String, Integer> allVerspaetungTable = new HashMap();
   private int old_fw = -1;
   private fahrstrasse old_fs = null;
   private int pet_fwanz = 0;

   public gleisbildModelFahrweg(GleisAdapter _theapplet) {
      super(_theapplet);
   }

   public boolean checkIfExists(fahrstrasse f2) {
      boolean ret = false;

      for (fahrstrasse f : this.fahrwege) {
         switch (f2.compare(f)) {
            case 2:
               ret = true;
         }
      }

      return ret;
   }

   public void resetGleisGroup() {
      for (gleis gl : this) {
         new gleisGroupNull().add(gl);
      }
   }

   private boolean isGroupSeperator(gleis gl) {
      element e = gl.getElement();
      return e == gleis.ELEMENT_KREUZUNG
         || e == gleis.ELEMENT_SIGNAL
         || e == gleis.ELEMENT_ZWERGSIGNAL
         || e == gleis.ELEMENT_ZDECKUNGSSIGNAL
         || e == gleis.ELEMENT_WEICHEOBEN
         || e == gleis.ELEMENT_WEICHEUNTEN
         || e == gleis.ELEMENT_AUTOBAHNÜBERGANG
         || e == gleis.ELEMENT_WBAHNÜBERGANG
         || e == gleis.ELEMENT_ANRUFÜBERGANG
         || e == gleis.ELEMENT_BAHNÜBERGANG;
   }

   public void buildGleisGroup() {
      gleisGroup gg = null;

      for (fahrstrasse fs : this.fahrwege) {
         new gleisGroupImpl().add(fs.getStart());
         new gleisGroupImpl().add(fs.getStop());
         gleisGroup var7 = new gleisGroupImpl();
         int gcc = 0;

         for (gleis gl : fs) {
            if (this.isGroupSeperator(gl)) {
               new gleisGroupImpl().add(gl);
               var7 = null;
               gcc = 0;
            } else {
               if (var7 == null) {
                  var7 = new gleisGroupImpl();
                  gcc = 0;
               }

               var7.add(gl);
               if (++gcc > 30) {
                  var7 = null;
                  gcc = 0;
               }
            }
         }
      }
   }

   public void buildWayTimeTable() {
      LinkedList ea_stack = new LinkedList();

      for (gleis gl : this) {
         gl.getFluentData().setStatus(0);
         if (gl.getElement() == gleis.ELEMENT_EINFAHRT) {
            gl.getFluentData().setStatus(2);
            ea_stack.addLast(gl);
         } else if (gleis.ALLE_SIGNALE.matches(gl.getElement())) {
            gl.getFluentData().setStellung(gleisElements.ST_SIGNAL_GRÜN);
         } else if (gleis.ALLE_WEICHEN.matches(gl.getElement())) {
            gl.getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE);
         }
      }

      this.wayTimeTable = new HashMap();
      this.allWayTimeTable = new HashMap();
      this.allWayTimeTableA = new HashMap();
      gleis.createName = true;
      gleis glx = null;
      gleis before_gl = null;
      gleis next_gl = null;

      while (!ea_stack.isEmpty()) {
         HashSet<gleis> besuchteGleise = new HashSet();
         glx = (gleis)ea_stack.removeFirst();
         gleis eingangGleis = glx;
         String eingang = glx.getSWWert();
         this.theapplet.showStatus("-Rest-Startpunkte: " + ea_stack.size(), 0);
         before_gl = null;
         int t = 0;
         LinkedList weichenstack = new LinkedList();
         boolean findSignal = true;

         while (true) {
            int loopc = 0;

            do {
               t += zug.calcMaxSpeed(this, glx.getMasstab());
               besuchteGleise.add(glx);
               next_gl = glx.next(before_gl);
               if (next_gl != null) {
                  if (glx.sameGleis(next_gl)) {
                     loopc++;
                     if (glx.getFluentData().getStellung() == gleisElements.ST_WEICHE_GERADE) {
                        glx.getFluentData().setStellung(gleisElements.ST_WEICHE_ABZWEIG);
                     } else {
                        glx.getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE);
                     }

                     if (loopc > 2) {
                        break;
                     }
                  } else {
                     loopc = 0;
                     if (besuchteGleise.contains(next_gl)) {
                        break;
                     }

                     before_gl = glx;
                     glx = next_gl;
                     if (findSignal && next_gl.getElement() == gleis.ELEMENT_SIGNAL && next_gl.forUs(before_gl)) {
                        findSignal = false;
                        next_gl.setEinfahrt(eingangGleis);
                     } else if (next_gl.getElement() == gleis.ELEMENT_WEICHEOBEN || next_gl.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
                        findSignal = false;
                        weichenstack.add(before_gl);
                        weichenstack.add(next_gl);
                        weichenstack.add(new Integer(t));
                     } else if ((next_gl.getElement() == gleis.ELEMENT_BAHNSTEIG || next_gl.getElement() == gleis.ELEMENT_HALTEPUNKT)
                        && next_gl.forUs(before_gl)) {
                        this.wayTimeTable.put(eingang, t);
                        this.allWayTimeTable.put(eingang + "/" + next_gl.getSWWert(), t);
                     }
                  }
               }
            } while (next_gl != null);

            if (weichenstack.size() <= 0) {
               break;
            }

            this.theapplet.showStatus("-Rest-Startpunkte: " + ea_stack.size() + " /Zusatz: " + weichenstack.size(), 0);

            try {
               t = (Integer)weichenstack.removeLast();
               glx = (gleis)weichenstack.removeLast();
               before_gl = (gleis)weichenstack.removeLast();
            } catch (NoSuchElementException var15) {
               break;
            } catch (Exception var16) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "Caught FS Lauf", var16);
               break;
            }

            if (glx.getFluentData().getStellung() == gleisElements.ST_WEICHE_GERADE) {
               glx.getFluentData().setStellung(gleisElements.ST_WEICHE_ABZWEIG);
            } else {
               glx.getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE);
            }
         }
      }

      ea_stack = new LinkedList();

      for (gleis glxx : this) {
         glxx.getFluentData().setStatus(0);
         if (glxx.getElement() == gleis.ELEMENT_AUSFAHRT) {
            glxx.getFluentData().setStatus(2);
            ea_stack.addLast(glxx);
         } else if (gleis.ALLE_SIGNALE.matches(glxx.getElement())) {
            glxx.getFluentData().setStellung(gleisElements.ST_SIGNAL_GRÜN);
         } else if (gleis.ALLE_WEICHEN.matches(glxx.getElement())) {
            glxx.getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE);
         }
      }

      glx = null;
      before_gl = null;
      next_gl = null;

      while (!ea_stack.isEmpty()) {
         HashSet<gleis> besuchteGleise = new HashSet();
         glx = (gleis)ea_stack.removeFirst();
         String ausgang = glx.getSWWert();
         this.theapplet.showStatus("-Rest-Endpunkte: " + ea_stack.size(), 0);
         before_gl = null;
         int t = 0;
         LinkedList weichenstack = new LinkedList();

         while (true) {
            int loopc = 0;

            do {
               t += zug.calcMaxSpeed(this, glx.getMasstab());
               besuchteGleise.add(glx);
               next_gl = glx.next(before_gl);
               if (next_gl != null) {
                  if (glx.sameGleis(next_gl)) {
                     loopc++;
                     if (glx.getFluentData().getStellung() == gleisElements.ST_WEICHE_GERADE) {
                        glx.getFluentData().setStellung(gleisElements.ST_WEICHE_ABZWEIG);
                     } else {
                        glx.getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE);
                     }

                     if (loopc > 2) {
                        break;
                     }
                  } else {
                     loopc = 0;
                     if (besuchteGleise.contains(next_gl)) {
                        break;
                     }

                     before_gl = glx;
                     glx = next_gl;
                     if (next_gl.getElement() == gleis.ELEMENT_WEICHEOBEN || next_gl.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
                        weichenstack.add(before_gl);
                        weichenstack.add(next_gl);
                        weichenstack.add(new Integer(t));
                     }

                     if ((next_gl.getElement() == gleis.ELEMENT_BAHNSTEIG || next_gl.getElement() == gleis.ELEMENT_HALTEPUNKT) && !next_gl.forUs(before_gl)) {
                        this.allWayTimeTableA.put(ausgang + "/" + next_gl.getSWWert(), t);
                     }
                  }
               }
            } while (next_gl != null);

            if (weichenstack.size() <= 0) {
               break;
            }

            this.theapplet.showStatus("-Rest-Endpunkte: " + ea_stack.size() + " /Zusatz: " + weichenstack.size(), 0);

            try {
               t = (Integer)weichenstack.removeLast();
               glx = (gleis)weichenstack.removeLast();
               before_gl = (gleis)weichenstack.removeLast();
            } catch (NoSuchElementException var13) {
               break;
            } catch (Exception var14) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "Caught FS Lauf", var14);
               break;
            }

            if (glx.getFluentData().getStellung() == gleisElements.ST_WEICHE_GERADE) {
               glx.getFluentData().setStellung(gleisElements.ST_WEICHE_ABZWEIG);
            } else {
               glx.getFluentData().setStellung(gleisElements.ST_WEICHE_GERADE);
            }
         }
      }

      this.smallClearStatus();
      gleis.createName = false;
   }

   public int getWayTime(String eingang, String gleis) {
      Integer i = (Integer)this.allWayTimeTable.get(eingang + "/" + gleis);
      return i != null ? i : 0;
   }

   public int getWayTimeA(String ausgang, String gleis) {
      Integer i = (Integer)this.allWayTimeTableA.get(ausgang + "/" + gleis);
      return i != null ? i : 0;
   }

   public int getVerspaetung(String eingang) {
      synchronized (this.allVerspaetungTable) {
         Integer i = (Integer)this.allVerspaetungTable.get(eingang);
         return i != null ? i : 0;
      }
   }

   public int getVerspaetung(int ein_enr) {
      gleis gl = this.findFirst(new Object[]{ein_enr, gleis.ELEMENT_EINFAHRT});
      return gl != null ? this.getVerspaetung(gl.getSWWert()) : 0;
   }

   public void setVerspaetung(String eingang) {
      int v = (int)zug.randomTimeShift(-2L, 0L, 3L);
      synchronized (this.allVerspaetungTable) {
         this.allVerspaetungTable.put(eingang, new Integer(v));
      }
   }

   public void setVerspaetung(int ein_enr) {
      gleis gl = this.findFirst(new Object[]{ein_enr, gleis.ELEMENT_EINFAHRT});
      if (gl != null) {
         this.setVerspaetung(gl.getSWWert());
      }
   }

   public void setVerspaetungen() {
      if (this.wayTimeTable != null) {
         for (String b : this.wayTimeTable.keySet()) {
            this.setVerspaetung(b);
         }
      }
   }

   public void sendVorsignal() {
      LinkedList<gleis> elist = new LinkedList();
      gleis gl = null;
      Iterator<gleis> it = this.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

      while (it.hasNext()) {
         gl = (gleis)it.next();
         int enr = gl.getEinfahrtEnr();
         if (enr > 0) {
            elist.add(gl);
         }
      }

      this.theapplet.getFSallocator().sentEnterSignalMessage(elist);
   }

   public boolean checkToSignal(int ein_enr, boolean byüp, zug z) {
      boolean ret = false;
      gleis before_gl = null;
      gleis gl = this.findFirst(new Object[]{ein_enr, gleis.ELEMENT_ÜBERGABEAKZEPTOR});
      if (gl != null) {
         zug az = gl.getFluentData().getZugAmGleis();
         if (az != null && gl.getFluentData().getStatus() == 2 && (z == null || az.getZID_num() != z.getZID_num())) {
            return false;
         }
      }

      gl = this.findFirst(new Object[]{ein_enr, gleis.ELEMENT_EINFAHRT});
      if (gl != null && !gl.getFluentData().isGesperrt()) {
         before_gl = null;

         while (gl.getFluentData().isFrei() || byüp && gl.getFluentData().getStatus() == 1) {
            gleis next_gl = gl.next(before_gl);
            if (next_gl == null || gl.sameGleis(next_gl)) {
               break;
            }

            before_gl = gl;
            gl = next_gl;
            if (next_gl.getElement() == gleis.ELEMENT_SIGNAL && next_gl.forUs(before_gl)) {
               if (!next_gl.getFluentData().isGesperrt() && (next_gl.getFluentData().getStatus() == 0 || next_gl.getFluentData().getStatus() == 1)) {
                  ret = true;
               }
               break;
            }

            if (next_gl == null) {
               break;
            }
         }
      }

      return ret;
   }

   public boolean freeToSignal(int ein_enr) {
      boolean ret = false;
      gleis before_gl = null;
      gleis gl = this.findFirst(new Object[]{ein_enr, gleis.ELEMENT_EINFAHRT});
      if (gl != null) {
         before_gl = null;

         while ((gl.getElement() == gleis.ELEMENT_KREUZUNGBRUECKE || gl.getFluentData().getStatus() == 1) && !gl.getFluentData().hasCurrentFS()) {
            gleis next_gl = gl.next(before_gl);
            if (next_gl == null || gl.sameGleis(next_gl)) {
               break;
            }

            before_gl = gl;
            gl = next_gl;
            if (next_gl.getElement() == gleis.ELEMENT_SIGNAL && next_gl.forUs(before_gl)) {
               if (next_gl.getFluentData().getStatus() == 0 || next_gl.getFluentData().getStatus() == 1) {
                  ret = true;
               }
               break;
            }

            if (next_gl == null) {
               break;
            }
         }
      }

      if (ret) {
         gl = this.findFirst(new Object[]{ein_enr, gleis.ELEMENT_EINFAHRT});
         if (gl != null) {
            before_gl = null;

            gleis next_glx;
            do {
               gl.getFluentData().setStatus(0);
               next_glx = gl.next(before_gl);
               if (next_glx == null || gl.sameGleis(next_glx)) {
                  break;
               }

               before_gl = gl;
               gl = next_glx;
            } while ((next_glx.getElement() != gleis.ELEMENT_SIGNAL || !next_glx.forUs(before_gl)) && next_glx != null);
         }
      }

      return ret;
   }

   public void reserveToSignal(int ein_enr, boolean reserve) {
      gleis before_gl = null;
      gleis gl = this.findFirst(new Object[]{ein_enr, gleis.ELEMENT_EINFAHRT});
      if (gl != null) {
         before_gl = null;
         HashSet<gleis> vorsignale = new HashSet();
         gleis sig = null;

         gleis next_gl;
         do {
            if (reserve) {
               gl.getFluentData().setStatus(1);
            }

            next_gl = gl.next(before_gl);
            if (next_gl == null || gl.sameGleis(next_gl)) {
               break;
            }

            before_gl = gl;
            gl = next_gl;
            if (next_gl.getElement() == gleis.ELEMENT_SIGNAL && next_gl.forUs(before_gl)) {
               next_gl.triggerApproachingFS();
               sig = next_gl;
               break;
            }

            if (next_gl.getElement() == gleis.ELEMENT_VORSIGNAL && next_gl.forUs(before_gl)) {
               vorsignale.add(next_gl);
            }
         } while (next_gl != null);

         if (sig != null) {
            for (gleis vgl : vorsignale) {
               vgl.getFluentData().setConnectedSignal(sig);
            }
         }
      }
   }

   public gleis firstSignalAfterEinfahrt(int ein_enr) {
      gleis before_gl = null;
      gleis gl = this.findFirst(new Object[]{ein_enr, gleis.ELEMENT_EINFAHRT});
      if (gl != null) {
         before_gl = null;

         gleis next_gl;
         do {
            next_gl = gl.next(before_gl);
            if (next_gl == null || gl.sameGleis(next_gl)) {
               break;
            }

            before_gl = gl;
            gl = next_gl;
            if (next_gl.getElement() == gleis.ELEMENT_SIGNAL && next_gl.forUs(before_gl)) {
               return next_gl;
            }
         } while (next_gl != null);
      }

      return null;
   }

   public boolean isFreeToNextSignal(gleis pos_gl, gleis before_gl, boolean ausfahrtAsSignal) {
      boolean ret = true;
      gleis gl = pos_gl;

      gleis next_gl;
      do {
         next_gl = gl.next(before_gl);
         if (next_gl != null) {
            if (gl.sameGleis(next_gl)) {
               ret = false;
               break;
            }

            before_gl = gl;
            gl = next_gl;
            if (next_gl.forUs(before_gl) && (next_gl.getElement() == gleis.ELEMENT_SIGNAL || next_gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL)) {
               ret = true;
               break;
            }

            if (ausfahrtAsSignal && next_gl.getElement() == gleis.ELEMENT_AUSFAHRT) {
               ret = true;
               break;
            }

            if (next_gl.getFluentData().getStatus() == 2) {
               ret = false;
               break;
            }
         }
      } while (next_gl != null);

      return ret;
   }

   public gleisElements.Stellungen stellungOfNextSignal(gleis pos_gl, gleis before_gl, boolean ausfahrtAsSignal) {
      gleisElements.Stellungen ret = gleisElements.ST_SIGNAL_ROT;
      gleis gl = pos_gl;

      gleis next_gl;
      do {
         next_gl = gl.next(before_gl);
         if (next_gl != null) {
            if (gl.sameGleis(next_gl)) {
               ret = gleisElements.ST_SIGNAL_ROT;
               break;
            }

            before_gl = gl;
            gl = next_gl;
            if (next_gl.forUs(before_gl) && (next_gl.getElement() == gleis.ELEMENT_SIGNAL || next_gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL)) {
               ret = next_gl.getFluentData().getStellung();
               break;
            }

            if (next_gl.forUs(before_gl) && next_gl.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL) {
               if (next_gl.getFluentData().getStellung().getZugStellung() == gleisElements.ZugStellungen.stop) {
                  ret = next_gl.getFluentData().getStellung();
                  break;
               }
            } else if (ausfahrtAsSignal && next_gl.getElement() == gleis.ELEMENT_AUSFAHRT) {
               ret = gleisElements.ST_SIGNAL_GRÜN;
               break;
            }
         }
      } while (next_gl != null);

      return ret;
   }

   public void befreieBisSignal(gleis pos_gl, gleis before_gl) {
      gleis gl = pos_gl;
      element eelm = gleis.ELEMENT_SIGNAL;

      gleis next_gl;
      do {
         if (gl.getFluentData().getStatus() == 1) {
            gl.getFluentData().setStatus(0);
         }

         next_gl = gl.next(before_gl);

         try {
            if (next_gl.getFluentData().getCurrentFS().isRangiermodus()) {
               eelm = gleis.ELEMENT_ZWERGSIGNAL;
            }
         } catch (NullPointerException var7) {
         }

         if (gl.sameGleis(next_gl)) {
            break;
         }

         if (next_gl != null) {
            before_gl = gl;
            gl = next_gl;
            if ((next_gl.getElement() == gleis.ELEMENT_SIGNAL || next_gl.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL || next_gl.getElement() == eelm)
               && next_gl.forUs(before_gl)
               && (
                  next_gl.getElement() == gleis.ELEMENT_SIGNAL
                     || next_gl.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL
                        && next_gl.getFluentData().getStellung().getZugStellung() == gleisElements.ZugStellungen.stop
                     || next_gl.getElement() == eelm
               )) {
               break;
            }

            if (next_gl.getFluentData().getStatus() == 1) {
               next_gl.getFluentData().setStatus(0);
            }
         }
      } while (next_gl != null);
   }

   public void statusÜPAusfahrt(gleis pos_gl, int v) {
      gleis next_gl = pos_gl.nextByRichtung(false);

      do {
         pos_gl.getFluentData().setStatus(v);
         gleis before_gl = pos_gl;
         pos_gl = next_gl;
         next_gl = next_gl.next(before_gl);
      } while (next_gl != null && pos_gl != next_gl);
   }

   public int countFahrwege() {
      return this.fahrwege == null ? 0 : this.fahrwege.size();
   }

   @Override
   public void clear() {
      this.clearFahrwege();
      super.clear();
   }

   @Override
   public void close() {
      super.close();

      for (fahrstrasse fs : this.fahrwege) {
         fs.close();
      }

      this.fahrwege.clear();
      this.allVerspaetungTable.clear();
      if (this.allWayTimeTable != null) {
         this.allWayTimeTable.clear();
      }

      if (this.allWayTimeTableA != null) {
         this.allWayTimeTableA.clear();
      }
   }

   public ArrayListModel<fahrstrasse> getFahrwegModel() {
      return this.fahrwege;
   }

   public void clearFahrwege() {
      this.fahrwege.clear();
      fahrstrasse.fserror = false;
      this.old_fw = -1;
   }

   public ArrayList<fahrstrasse> cloneFahrwege() {
      return (ArrayList<fahrstrasse>)this.fahrwege.clone();
   }

   public void removeFahrweg(int f) {
      if (this.old_fw >= 0) {
         try {
            fahrstrasse fs = (fahrstrasse)this.fahrwege.get(this.old_fw);
            fs.freeWeg(true);
         } catch (IndexOutOfBoundsException var3) {
         }

         this.old_fw = -1;
      }

      this.fahrwege.remove(f);
   }

   public void removeFahrweg(fahrstrasse f) {
      if (this.old_fw >= 0) {
         try {
            fahrstrasse fs = (fahrstrasse)this.fahrwege.get(this.old_fw);
            fs.freeWeg(true);
         } catch (IndexOutOfBoundsException var3) {
         }

         this.old_fw = -1;
      }

      this.fahrwege.remove(f);
   }

   public void addFahrweg(fahrstrasse nfs) {
      this.fahrwege.add(nfs);
   }

   public void addFahrweg(int pos, fahrstrasse nfs) {
      this.fahrwege.add(pos, nfs);
   }

   public fahrstrasse getFahrweg(int f) {
      return (fahrstrasse)this.fahrwege.get(f);
   }

   public void showFahrweg(int fw) {
      if (this.old_fw >= 0) {
         try {
            fahrstrasse fs = (fahrstrasse)this.fahrwege.get(this.old_fw);
            fs.freeWeg(true);
         } catch (IndexOutOfBoundsException var5) {
         }
      }

      try {
         fahrstrasse fs = (fahrstrasse)this.fahrwege.get(fw);
         fs.buildWeg(true);
         this.old_fw = fw;
      } catch (IndexOutOfBoundsException var4) {
      }

      this.structureChanged();
   }

   public void showFahrweg(fahrstrasse fw) {
      if (this.old_fs != null) {
         this.old_fs.freeWeg(true);
      }

      if (fw != null) {
         fw.buildWeg(true);
      }

      this.old_fs = fw;
      this.structureChanged();
   }

   @Override
   public void allOff() {
      super.allOff();
      this.showFahrweg(null);
   }

   private gleis getItsSignal(gleis gl) {
      if (gl.getElement() == gleisElements.ELEMENT_SIGNALKNOPF || gl.getElement() == gleisElements.ELEMENT_SIGNAL_ZIELKNOPF) {
         gl = this.findFirst(new Object[]{gl.getENR(), gleisElements.ELEMENT_SIGNAL});
      }

      return gl;
   }

   public fahrstrasse findFahrwegByName(String name) {
      for (fahrstrasse fs : this.fahrwege) {
         if (fs.getName().equals(name)) {
            return fs;
         }
      }

      return null;
   }

   public fahrstrasseSelection findFahrweg(gleis signal1, gleis signal2, boolean allowUfgt) {
      boolean wantRf = false;
      wantRf |= signal1.getElement() == gleisElements.ELEMENT_SIGNALKNOPF;
      wantRf |= signal1.getElement() == gleisElements.ELEMENT_ZWERGSIGNAL;
      wantRf |= signal2.getElement() == gleisElements.ELEMENT_SIGNALKNOPF;
      wantRf |= signal2.getElement() == gleisElements.ELEMENT_ZWERGSIGNAL;
      signal1 = this.getItsSignal(signal1);
      signal2 = this.getItsSignal(signal2);
      fahrstrasse fs = null;
      if (allowUfgt) {
         try {
            for (fahrstrasse var15 : this.fahrwege) {
               if (var15.checkThis(signal1, signal2)) {
                  return this.findUFahrweg(signal1, signal2, wantRf, var15.getExtend().fstype);
               }

               if (var15.checkThis(signal2, signal1)) {
                  return this.findUFahrweg(signal2, signal1, wantRf, var15.getExtend().fstype);
               }
            }
         } catch (NullPointerException var8) {
         }
      } else {
         try {
            for (fahrstrasse var16 : this.fahrwege) {
               if (var16.checkThis(signal1, signal2)) {
                  return this.buildFSEL(var16, wantRf);
               }

               if (var16.checkThis(signal2, signal1)) {
                  return this.buildFSEL(var16, wantRf);
               }
            }
         } catch (NullPointerException var7) {
         }
      }

      return null;
   }

   private fahrstrasseSelection findUFahrweg(gleis signal1, gleis signal2, boolean wantRf, int fstype) {
      LinkedList<gleis> weg = new LinkedList();
      gleis before_gl = signal1;
      gleis gl = signal1.nextByRichtung(false);
      gleis next_gl = null;
      if (gl != null) {
         weg.add(signal1);

         for (int maxCount = 100; maxCount > 0; maxCount--) {
            boolean stopSignal = false;
            stopSignal |= gl.getElement() == gleis.ELEMENT_SIGNAL;
            stopSignal |= wantRf && gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL;
            if (stopSignal && gl.forUs(before_gl)) {
               if (gl == signal2) {
                  fahrstrasse fs = new fahrstrasse("UFGT" + signal1.getENR() + "-" + signal2.getENR(), signal1, signal2, weg);
                  fs.getExtend().fstype = fstype;
                  return this.buildFSEL(fs, wantRf);
               }
               break;
            }

            if (!gl.getFluentData().isFrei() || gl.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT && gl.forUs(before_gl) || weg.contains(gl)) {
               break;
            }

            weg.add(gl);
            next_gl = gl.next(before_gl);
            if (next_gl == null || gl.sameGleis(next_gl)) {
               break;
            }

            before_gl = gl;
            gl = next_gl;
         }
      }

      return null;
   }

   private fahrstrasseSelection buildFSEL(fahrstrasse fs, boolean wantRf) {
      fahrstrasseSelection fsel = new fahrstrasseSelection(fs, wantRf, this.theapplet);
      return fsel.isValid() ? fsel : null;
   }

   public boolean hasZwerge() {
      Iterator<gleis> it = this.findIterator(new Object[]{gleis.ELEMENT_ZWERGSIGNAL, gleis.ELEMENT_SIGNALKNOPF});
      return it.hasNext();
   }

   public void clearStatus() {
      for (gleis gl : this) {
         gl.clear();
         gl.getFluentData().setStatus(0);
         gl.unregisterAllHooks();
      }

      for (fahrstrasse fs : this.fahrwege) {
         if (!fs.getExtend().isDeleted()) {
            fs.init();
            fs.purgeCache();
            fs.getStart().informStartingFS(fs);
            fs.getStop().informStopingFS(fs);
         }
      }

      this.structureChanged();
   }

   public void purgeFahrwege() {
      Iterator<fahrstrasse> it = this.fahrstrassenIterator();

      while (it.hasNext()) {
         fahrstrasse fs = (fahrstrasse)it.next();
         fs.purgeCache();
      }
   }

   public Iterator<fahrstrasse> iteratorFahrwege() {
      return this.fahrwege.iterator();
   }

   public int getNumberOfStartingFahrwege(gleis signal, boolean includeDeleted) {
      int cnt = 0;

      for (fahrstrasse fs : this.fahrwege) {
         if ((includeDeleted || !fs.getExtend().isDeleted()) && fs.getStart() == signal) {
            cnt++;
         }
      }

      return cnt;
   }

   public void enableAllAutoFS(boolean triggered) {
      Iterator<gleis> it = this.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

      while (it.hasNext()) {
         ((gleis)it.next()).enableAutoFW(triggered);
      }

      this.structureChanged();
   }

   public void disableAllAutoFS() {
      Iterator<gleis> it = this.findIterator(new Object[]{gleis.ELEMENT_SIGNAL});

      while (it.hasNext()) {
         ((gleis)it.next()).disableAutoFW();
      }

      this.structureChanged();
   }

   @Override
   protected void registerTags() {
      super.registerTags();
      this.xmlr.registerTag("fahrwege", this);
      this.xmlr.registerTag("fahrweg", this);
   }

   @Override
   public void parseStartTag(String tag, Attributes attrs) {
      if (tag.compareTo("fahrwege") == 0) {
         this.pet_fwanz = Integer.parseInt(attrs.getValue("anzahl").trim());
         this.fahrwege.clear();
         this.theapplet.setProgress(50);
         this.theapplet.showStatus("Fahrstraßensortierung...", 0);
         fahrstrasse.fserror = false;
      } else {
         super.parseStartTag(tag, attrs);
      }
   }

   @Override
   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
      if (tag.compareTo("fahrwege") == 0) {
         this.theapplet.setProgress(70);
         this.smallClearStatus();
         this.theapplet.setProgress(75);
         this.buildWayTimeTable();
         this.theapplet.setProgress(80);
         this.smallClearStatus();
         this.theapplet.showStatus("Fahrstraßensortierung beendet.", 0);
         if (fahrstrasse.fserror) {
            this.theapplet.showStatus("Achtung! Es gibt Fehler in den gespeicherten Fahrstraßen!", 2);
         }

         this.theapplet.setProgress(85);
      } else if (tag.compareTo("fahrweg") == 0) {
         try {
            fahrstrasse f = new fahrstrasse(attrs, pcdata, this);
            this.fahrwege.add(f);
            this.theapplet.setProgress(50 + this.fahrwege.size() * 20 / this.pet_fwanz);
            if (!f.getExtend().isDeleted()) {
               gleis gl = f.getStart();
               if (gl != null) {
                  gl.informStartingFS(f);
               }

               gl = f.getStop();
               if (gl != null) {
                  gl.informStopingFS(f);
               }
            }
         } catch (Exception var6) {
            System.out.println("Fahrweg Fehler (" + var6.getMessage() + ")");
         }
      } else {
         super.parseEndTag(tag, attrs, pcdata);
      }
   }

   @Override
   protected StringBuffer createSaveData(StringBuffer data) {
      data = super.createSaveData(data);
      if (this.fahrwege == null) {
         data.append("fahrwege=0&");
      } else {
         int i = 0;
         data.append("fahrwege=").append(this.fahrwege.size()).append("&");

         for (fahrstrasse f : this.fahrwege) {
            data.append(TextHelper.urlEncode("fahrstrasse[]"));
            data.append('=');
            data.append(TextHelper.urlEncode(f.toSaveString()));
            data.append('&');
            i++;
         }
      }

      return data;
   }

   public Iterator<fahrstrasse> fahrstrassenIterator() {
      return this.iteratorFahrwege();
   }
}
