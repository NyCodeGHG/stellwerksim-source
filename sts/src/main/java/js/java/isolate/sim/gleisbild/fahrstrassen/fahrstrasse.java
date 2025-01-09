package js.java.isolate.sim.gleisbild.fahrstrassen;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Logger;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.fahrstrassemsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.element_list;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;
import js.java.isolate.sim.structServ.structinfo;
import js.java.tools.TextHelper;
import org.xml.sax.Attributes;

public class fahrstrasse extends trigger implements Comparable, Iterable<gleis>, structinfo {
   static FATwriter debugMode = null;
   private int startSignalEnr;
   private gleis startSignal;
   private int stopSignalEnr;
   private gleis stopSignal;
   private boolean startSignalHasKnopf = false;
   private boolean stopSignalHasKnopf = false;
   private boolean vSignalTrenner = false;
   gleis lastGleis = null;
   LinkedList<gleis> gleisweg;
   LinkedList<gleis> zwerge;
   HashSet<gleis> zdeckungssignale;
   HashSet<gleis> vorsignale;
   HashMap<gleis, gleisElements.Stellungen> weichen;
   HashMap<gleis, gleisElements.Stellungen> flankenweichen;
   private String name;
   boolean foundüp = false;
   gleis gleisüp = null;
   fahrstrasse_extend fahrstrasseExtend = new fahrstrasse_extend();
   fahrstrassenState allocState = new fasNullState();
   public static boolean fserror = false;
   public static boolean compactSave = true;
   public static boolean compactLoad = true;
   int falseReason = 0;
   private HashMap<Integer, Integer> count_special = new HashMap();
   public static final element[] ELEMENTS_BÜ_EIN_KREUZ = new element[]{
      gleis.ELEMENT_BAHNÜBERGANG, gleis.ELEMENT_AUTOBAHNÜBERGANG, gleis.ELEMENT_WBAHNÜBERGANG, gleis.ELEMENT_EINFAHRT, gleis.ELEMENT_KREUZUNG
   };
   public static final element[] ELEMENTS_BÜ_EIN = new element[]{
      gleis.ELEMENT_BAHNÜBERGANG, gleis.ELEMENT_AUTOBAHNÜBERGANG, gleis.ELEMENT_WBAHNÜBERGANG, gleis.ELEMENT_EINFAHRT
   };
   public static final element[] ELEMENTS_BÜ_KREUZ = new element[]{
      gleis.ELEMENT_BAHNÜBERGANG, gleis.ELEMENT_AUTOBAHNÜBERGANG, gleis.ELEMENT_WBAHNÜBERGANG, gleis.ELEMENT_KREUZUNG
   };
   int rangierlänge = 0;
   gleis lastZD = null;
   public static final int COMPARE_DIFFERENT = 0;
   public static final int COMPARE_SAMESTARTSTOP = 1;
   public static final int COMPARE_IDENTICAL = 2;
   public boolean wasChecked = false;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static FATwriter getDebug() {
      return debugMode;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public fahrstrasse(String n, gleis start, gleis stop, LinkedList<gleis> weg) {
      this.name = n;
      this.startSignal = start;
      this.startSignalEnr = start.getENR();
      this.stopSignal = stop;
      this.stopSignalEnr = stop.getENR();
      this.gleisweg = (LinkedList<gleis>)weg.clone();
      this.weichen = new HashMap();
      this.flankenweichen = new HashMap();
      this.zwerge = new LinkedList();
      this.zdeckungssignale = new HashSet();
      this.vorsignale = new HashSet();

      for (gleis g : this.gleisweg) {
         if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
            this.weichen.put(g, g.getFluentData().getStellung());
            this.createSchutz_Weiche(g);
         } else if (g.getElement() == gleis.ELEMENT_KREUZUNG) {
            this.createSchutz_Kreuzung(g);
         } else if (g.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT) {
            this.foundüp = true;
            this.gleisüp = g;
         }
      }

      this.findEmbeddedSignals();
   }

   public fahrstrasse(Attributes attrs, String line, gleisbildModelFahrweg glb) throws Exception {
      this.gleisweg = new LinkedList();
      this.weichen = new HashMap();
      this.flankenweichen = new HashMap();
      this.zwerge = new LinkedList();
      this.zdeckungssignale = new HashSet();
      this.vorsignale = new HashSet();
      this.name = attrs.getValue("name").trim();
      this.startSignalEnr = Integer.parseInt(attrs.getValue("startenr").trim());
      this.startSignal = glb.findFirst(new Object[]{this.startSignalEnr, gleis.ALLE_STARTSIGNALE});
      if (this.startSignal == null) {
         throw new Exception("Start-Signal ENR " + this.startSignalEnr + " in FS " + this.name + " nicht gefunden!");
      } else {
         this.stopSignalEnr = Integer.parseInt(attrs.getValue("stopenr").trim());
         this.stopSignal = glb.findFirst(new Object[]{this.stopSignalEnr, gleis.ALLE_STOPSIGNALE});
         if (this.stopSignal == null) {
            throw new Exception("End-Signal ENR " + this.stopSignalEnr + " in FS " + this.name + " nicht gefunden!");
         } else {
            StringTokenizer fst = new StringTokenizer(line + " ", ",");
            if (fst.countTokens() > 0) {
               while (fst.hasMoreTokens()) {
                  String e = fst.nextToken().trim();
                  if (e.startsWith("(") && e.endsWith(")")) {
                     e = e.substring(1, e.length() - 1);
                     StringTokenizer gst = new StringTokenizer(e, ";");
                     if (gst.countTokens() >= 2) {
                        int row = Integer.parseInt(gst.nextToken());
                        int col = Integer.parseInt(gst.nextToken());
                        gleis g = glb.getXY_null(col, row);
                        if (g != null) {
                           if (Math.abs(row - this.stopSignal.getRow()) == 1 && Math.abs(col - this.stopSignal.getCol()) == 0
                              || Math.abs(row - this.stopSignal.getRow()) == 0 && Math.abs(col - this.stopSignal.getCol()) == 1
                              || Math.abs(row - this.stopSignal.getRow()) == 1 && Math.abs(col - this.stopSignal.getCol()) == 1) {
                              this.lastGleis = g;
                           }

                           this.gleisweg.add(g);
                           if (g.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT) {
                              this.foundüp = true;
                              this.gleisüp = g;
                           }

                           if (gst.hasMoreTokens()) {
                              gleisElements.Stellungen s = gleisElements.Stellungen.string2stellung(gst.nextToken());
                              this.weichen.put(g, s);
                              if (g.getElement() == gleis.ELEMENT_WEICHEOBEN || g.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
                                 try {
                                    g.getFluentData().setStellung(s);
                                    this.createSchutz_Weiche(g);
                                 } catch (Exception var13) {
                                    System.out.println("Flankenschutz " + var13.getMessage());
                                    var13.printStackTrace();
                                 }
                              }
                           }
                        }
                     }
                  } else if (e.startsWith("[") && e.endsWith("]")) {
                     e = e.substring(1, e.length() - 1);

                     try {
                        this.fahrstrasseExtend = fahrstrasse_extend.createFromBase64(e.trim());
                     } catch (Exception var12) {
                        System.out.println(e + ": '" + e.trim() + "'");
                        var12.printStackTrace();
                     }
                  }
               }
            }

            this.sortGleise(glb);
            this.findEmbeddedSignals();

            for (gleis g : this.gleisweg) {
               if (g.getElement() == gleis.ELEMENT_KREUZUNG) {
                  this.createSchutz_Kreuzung(g);
               }
            }
         }
      }
   }

   public void close() {
      this.flankenweichen.clear();
      this.weichen.clear();
      this.gleisweg.clear();
      this.zdeckungssignale.clear();
      this.vorsignale.clear();
      this.zwerge.clear();
      this.lastGleis = null;
      this.lastZD = null;
      this.startSignal = null;
      this.stopSignal = null;
      this.gleisüp = null;
   }

   public String toString() {
      return this.name + "," + this.startSignalEnr + "," + this.stopSignalEnr;
   }

   public String toSaveString() {
      StringBuilder str = new StringBuilder(this.name + "," + this.startSignalEnr + "," + this.stopSignalEnr + ",");

      for (gleis g : this.gleisweg) {
         if (this.weichen.containsKey(g)) {
            str.append("(")
               .append(g.getRow())
               .append(";")
               .append(g.getCol())
               .append(";")
               .append(((gleisElements.Stellungen)this.weichen.get(g)).getSaveText())
               .append("),");
         } else if (!compactSave) {
            str.append("(").append(g.getRow()).append(";").append(g.getCol()).append("),");
         }
      }

      str.append("[").append(this.fahrstrasseExtend.toBase64()).append("]");
      return str.toString();
   }

   public void saveData(StringBuffer data, String prefix) {
      data.append(TextHelper.urlEncode(prefix + "[name]"));
      data.append("=");
      data.append(TextHelper.urlEncode(this.name));
      data.append("&");
      data.append(TextHelper.urlEncode(prefix + "[startSignalEnr]"));
      data.append("=");
      data.append(this.startSignalEnr);
      data.append("&");
      data.append(TextHelper.urlEncode(prefix + "[stopSignalEnr]"));
      data.append("=");
      data.append(this.stopSignalEnr);
      data.append("&");
      if (compactSave) {
         for (gleis g : this.gleisweg) {
            if (this.weichen.containsKey(g)) {
               data.append(TextHelper.urlEncode(prefix + "[gleise]"));
               data.append("=");
               data.append(TextHelper.urlEncode("(" + g.getRow() + ";" + g.getCol() + ";" + this.weichen.get(g) + "),"));
               data.append("&");
            }
         }
      } else {
         for (gleis gx : this.gleisweg) {
            data.append(TextHelper.urlEncode(prefix + "[gleise]"));
            data.append("=");
            if (this.weichen.containsKey(gx)) {
               data.append(TextHelper.urlEncode("(" + gx.getRow() + ";" + gx.getCol() + ";" + this.weichen.get(gx) + "),"));
            } else {
               data.append(TextHelper.urlEncode("(" + gx.getRow() + ";" + gx.getCol() + "),"));
            }

            data.append("&");
         }
      }

      data.append(TextHelper.urlEncode(prefix + "[extend]"));
      data.append("=");
      data.append(this.fahrstrasseExtend.toBase64());
      data.append("&");
   }

   public void purgeCache() {
      this.zwerge.clear();
      this.vorsignale.clear();
      this.zdeckungssignale.clear();
      this.count_special.clear();
      this.findEmbeddedSignals();
   }

   private void findEmbeddedSignals() {
      gleis before_gl = this.startSignal;

      for (gleis g : this.gleisweg) {
         if (g.getElement() == gleis.ELEMENT_ZWERGSIGNAL) {
            if (g.forUs(before_gl)) {
               this.zwerge.add(g);
            }
         } else if (g.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL) {
            if (g.forUs(before_gl)) {
               this.zdeckungssignale.add(g);
            }
         } else if (g.getElement() == gleis.ELEMENT_VORSIGNAL) {
            if (g.forUs(before_gl)) {
               this.vorsignale.add(g);
            }
         } else if (g.getElement() == gleis.ELEMENT_VORSIGNALTRENNER && g.forUs(before_gl)) {
            this.vorsignale.clear();
            this.vSignalTrenner = true;
         }

         before_gl = g;
      }

      if (this.startSignal.getElement() == gleis.ELEMENT_SIGNAL) {
         this.startSignalHasKnopf = this.startSignal.getParentGleisbild().findFirst(this.startSignal.getENR(), gleis.ELEMENT_SIGNALKNOPF) != null;
      }

      if (this.stopSignal.getElement() == gleis.ELEMENT_SIGNAL) {
         this.stopSignalHasKnopf = this.stopSignal.getParentGleisbild().findFirst(this.stopSignal.getENR(), gleis.ELEMENT_SIGNALKNOPF) != null;
      }
   }

   private void sortGleise(gleisbildModelFahrweg glb) {
      if (this.stopSignal.getElement() == gleis.ELEMENT_SIGNAL
         || this.stopSignal.getElement() == gleis.ELEMENT_ZWERGSIGNAL
         || this.stopSignal.getElement() == gleis.ELEMENT_AUSFAHRT) {
         LinkedList<gleis> gleiswegneu = new LinkedList();
         int oldcnt = this.gleisweg.size();
         gleis cg = this.startSignal;
         if (compactLoad) {
            fserror = false;
         }

         gleiswegneu.add(cg);
         this.buildWeg(true);

         try {
            gleis pos_gl = cg;
            gleis before_gl = cg.nextByRichtung(true);
            if (before_gl == null) {
               System.out.println("before_gl: NULL");
            }

            gleis next_gl;
            do {
               next_gl = pos_gl.next(before_gl);
               if (next_gl == null || next_gl.sameGleis(this.stopSignal) || next_gl == pos_gl || gleiswegneu.contains(next_gl)) {
                  if (next_gl == null) {
                     String m = "Fahrstraßenfehler [2b]: Abbruch FS Sortierung wegen null, Fahrstraße endet unverhofft. Bitte erst nach Fehlerbehebung speichern! (FS "
                        + this.name
                        + "/X"
                        + pos_gl.getCol()
                        + "/Y"
                        + pos_gl.getRow()
                        + ") AID "
                        + glb.getAid();
                     System.out.println(m);
                     Logger.getLogger(this.getClass().getName()).warning(m);
                  }

                  if (next_gl == pos_gl) {
                     String m = "Fahrstraßenfehler [2c]: Abbruch FS Sortierung Blockierung. Bitte erst nach Fehlerbehebung speichern! (FS "
                        + this.name
                        + "/X"
                        + pos_gl.getCol()
                        + "/Y"
                        + pos_gl.getRow()
                        + ") AID "
                        + glb.getAid();
                     System.out.println(m);
                     Logger.getLogger(this.getClass().getName()).warning(m);
                  }

                  if (gleiswegneu.contains(next_gl)) {
                     String m = "Fahrstraßenfehler [2d]: Abbruch FS Sortierung wegen Kreislauf. Bitte erst nach Fehlerbehebung speichern! (FS "
                        + this.name
                        + "/X"
                        + next_gl.getCol()
                        + "/Y"
                        + next_gl.getRow()
                        + ") AID "
                        + glb.getAid();
                     System.out.println(m);
                     Logger.getLogger(this.getClass().getName()).warning(m);
                  }
                  break;
               }

               int row = next_gl.getRow();
               int col = next_gl.getCol();
               if (Math.abs(row - this.stopSignal.getRow()) == 1 && Math.abs(col - this.stopSignal.getCol()) == 0
                  || Math.abs(row - this.stopSignal.getRow()) == 0 && Math.abs(col - this.stopSignal.getCol()) == 1
                  || Math.abs(row - this.stopSignal.getRow()) == 1 && Math.abs(col - this.stopSignal.getCol()) == 1) {
                  this.lastGleis = next_gl;
               }

               gleiswegneu.add(next_gl);
               if (next_gl.getElement() == gleis.ELEMENT_ÜBERGABEPUNKT) {
                  this.foundüp = true;
                  this.gleisüp = next_gl;
               }

               if (gleis.ALLE_WEICHEN.matches(next_gl.getElement()) && !this.weichen.containsKey(next_gl)) {
                  String m = "Fahrstraßenfehler [2a]: Unbekannte Weiche in Fahrstraße. Bitte erst nach Fehlerbehebung speichern! (FS "
                     + this.name
                     + "/X"
                     + next_gl.getCol()
                     + "/Y"
                     + next_gl.getRow()
                     + ") AID "
                     + glb.getAid();
                  System.out.println(m);
                  Logger.getLogger(this.getClass().getName()).warning(m);
               }

               before_gl = pos_gl;
               pos_gl = next_gl;
            } while (next_gl != null);
         } catch (Exception var11) {
            System.out.println("Caught exception " + var11.getMessage());
            var11.printStackTrace();
         }

         this.freeWeg(true);
         if (compactLoad) {
            this.gleisweg.clear();
            this.gleisweg = gleiswegneu;
         } else {
            int newcnt = gleiswegneu.size();
            if (oldcnt == newcnt) {
               this.gleisweg.clear();
               this.gleisweg = gleiswegneu;
            } else {
               fserror = true;
               System.out.println("Fehler in FS: " + this.name);
               Logger.getLogger(this.getClass().getName()).warning("Fehler in FS: " + this.name);
               gleiswegneu.clear();
            }
         }
      }
   }

   private void createSchutz_Kreuzung(gleis g) {
      Iterator<gleis> n = g.getNachbarn();

      while (n.hasNext()) {
         gleis gl = (gleis)n.next();
         if (!this.gleisweg.contains(gl)) {
            this.createSchutz(g, gl);
         }
      }
   }

   private void createSchutz_Weiche(gleis g) {
      this.createSchutz(g, g.nextWeichenAst(true));
   }

   private void createSchutz(gleis before_g, gleis g) {
      gleis next_gl = null;
      gleis before_gl = before_g;
      gleis gl = g;
      HashSet<gleis> seenGleis = new HashSet();

      while (gl != null) {
         if (gl.getElement() == gleis.ELEMENT_WEICHEOBEN || gl.getElement() == gleis.ELEMENT_WEICHEUNTEN) {
            boolean spitz = gl.weicheSpitz(before_gl);
            if (!spitz) {
               gleis tg = gl.nextWeichenAst(false);
               if (tg != null) {
                  gleisElements.Stellungen st = gl.getFluentData().getStellung();
                  if (tg.sameGleis(before_gl)) {
                     if (st == gleisElements.ST_WEICHE_GERADE) {
                        st = gleisElements.ST_WEICHE_ABZWEIG;
                     } else {
                        st = gleisElements.ST_WEICHE_GERADE;
                     }
                  }

                  this.flankenweichen.put(gl, st);
               }
            }
            break;
         }

         if (gl.getElement() == gleis.ELEMENT_SIGNAL
            || gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL
            || gl.getElement() == gleis.ELEMENT_AUSFAHRT
            || gl.getElement() == gleis.ELEMENT_EINFAHRT) {
            break;
         }

         seenGleis.add(gl);
         next_gl = gl.next(before_gl);
         if (next_gl == null || gl.sameGleis(next_gl)) {
            break;
         }

         if (seenGleis.contains(next_gl)) {
            String m = "Fahrstraßenfehler [2e]: Abbruch FS Sortierung wegen Kreislauf in der Flanke. Bitte erst nach Fehlerbehebung speichern! (FS "
               + this.name
               + "/X"
               + next_gl.getCol()
               + "/Y"
               + next_gl.getRow()
               + ")";
            System.out.println(m);
            Logger.getLogger(this.getClass().getName()).warning(m);
            break;
         }

         before_gl = gl;
         gl = next_gl;
      }
   }

   public String getName() {
      return this.name;
   }

   public boolean checkThis(gleis start, gleis stop) {
      return this.fahrstrasseExtend.isDeleted() ? false : this.startSignalEnr == start.getENR() && this.stopSignalEnr == stop.getENR();
   }

   public int getStartEnr() {
      return this.startSignalEnr;
   }

   public int getStopEnr() {
      return this.stopSignalEnr;
   }

   public gleis getStart() {
      return this.startSignal;
   }

   public gleis getStop() {
      return this.stopSignal;
   }

   public int countWeichen() {
      return this.countElements(new element[]{gleis.ELEMENT_WEICHEOBEN, gleis.ELEMENT_WEICHEUNTEN, gleis.ELEMENT_KREUZUNG});
   }

   public int countSignale() {
      return this.countElements(new element[]{gleis.ELEMENT_SIGNAL});
   }

   private int calcAhash(element[] elm) {
      int hash = elm.length;

      for (element v : elm) {
         hash += v.hashCode();
      }

      return hash;
   }

   public List<gleis> getElements(element[] elm) {
      LinkedList<gleis> ret = new LinkedList();

      for (gleis g : this.gleisweg) {
         if (g != this.startSignal && g != this.stopSignal) {
            for (element e : elm) {
               if (g.getElement().matches(e)) {
                  ret.add(g);
               }
            }
         }
      }

      return ret;
   }

   public int countElements(element[] elm) {
      int hash = this.calcAhash(elm);
      if (this.count_special.containsKey(hash)) {
         return (Integer)this.count_special.get(hash);
      } else {
         int cnt = 0;

         for (gleis g : this.gleisweg) {
            if (g != this.startSignal && g != this.stopSignal) {
               for (element e : elm) {
                  if (g.getElement().matches(e)) {
                     cnt++;
                  }
               }
            }
         }

         this.count_special.put(hash, cnt);
         return cnt;
      }
   }

   public boolean hasElements(element[] elm) {
      return this.countElements(elm) > 0;
   }

   boolean checkGetHook() {
      if (this.hasHook(eventGenerator.T_FS_SETZEN)) {
         boolean r = this.call(eventGenerator.T_FS_SETZEN, new fahrstrassemsg(this));
         if (!r) {
            return false;
         }
      }

      return true;
   }

   boolean checkFreeHook() {
      if (this.hasHook(eventGenerator.T_FS_LOESCHEN)) {
         boolean r = this.call(eventGenerator.T_FS_LOESCHEN, new fahrstrassemsg(this));
         if (!r) {
            return false;
         }
      }

      return true;
   }

   void tjmAdd(trigger t) {
      this.tjm_add(t);
   }

   @Override
   public boolean ping() {
      boolean ret = false;
      if (this.allocState != null) {
         boolean tjmAdd = this.allocState.ping();
         if (tjmAdd) {
            this.tjm_add(this);
            ret = true;
         }

         return ret;
      } else {
         if (this.lastGleis != null && this.startSignal.getFluentData().getStellung() == gleisElements.ST_SIGNAL_GRÜN) {
            if (this.lastGleis.getFluentData().getStatus() != 1) {
               this.startSignal.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT, this);

               for (gleis g : this.gleisweg) {
                  if (g.getFluentData().getStatus() != 2) {
                     g.getFluentData().setStatusByFs(0, this);
                  }
               }

               ret = true;
            } else {
               this.tjm_add(this);
            }
         }

         return ret;
      }
   }

   public void init() {
      this.allocState = new fasNullState();
      this.freeWeg(true);
   }

   public void paint() {
      this.buildWeg(true);
   }

   public void buildWeg(boolean setSignale) {
      for (gleis g : this.gleisweg) {
         if (this.weichen.get(g) != null) {
            g.getFluentData().setStellung((gleisElements.Stellungen)this.weichen.get(g));
         }

         g.getFluentData().setStatusByFs(1, this);
      }

      if (setSignale) {
         for (gleis g : this.zwerge) {
            g.getFluentData().setStellung(gleis.ST_SIGNAL_GRÜN, this);
         }

         this.startSignal.getFluentData().setStellung(gleisElements.ST_SIGNAL_GRÜN);
      }
   }

   public void paintWeg() {
      for (gleis g : this.gleisweg) {
         g.getFluentData().setStatusByFs(1, this);
      }
   }

   public void unpaint() {
      this.freeWeg(true);
   }

   public void freeWeg(boolean setSignale) {
      if (setSignale) {
         this.startSignal.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
      }

      for (gleis g : this.gleisweg) {
         g.getFluentData().setStatusByFs(0, this);
      }

      if (setSignale) {
         for (gleis g : this.zwerge) {
            g.getFluentData().setStellung(gleis.ST_SIGNAL_ROT, this);
         }
      }
   }

   public int compare(fahrstrasse f) {
      int ret = 0;
      if (this.startSignalEnr == f.startSignalEnr && this.stopSignalEnr == f.stopSignalEnr) {
         ret = 1;
         if (this.gleisweg.size() == f.gleisweg.size()) {
            ret = 2;
            Iterator<gleis> it = this.gleisweg.iterator();
            Iterator<gleis> it2 = f.gleisweg.iterator();

            while (it.hasNext()) {
               gleis g = (gleis)it.next();
               gleis g2 = (gleis)it2.next();
               if (!g.sameGleis(g2)) {
                  ret = 1;
                  break;
               }
            }
         }
      }

      return ret;
   }

   public int compareTo(Object o) {
      int c = this.compare((fahrstrasse)o);
      return c == 0 ? 1 : 0;
   }

   public fahrstrasse_extend getExtend() {
      return this.fahrstrasseExtend;
   }

   public boolean checkThisClever(gleis start, gleis stop) {
      return this.startSignal.sameGleis(start) && this.stopSignal.sameGleis(stop);
   }

   public int compareWay(fahrstrasse f) {
      int ret = 0;

      for (gleis w1 : this.weichen.keySet()) {
         gleisElements.Stellungen st1 = (gleisElements.Stellungen)this.weichen.get(w1);
         if (f.weichen.containsKey(w1)) {
            gleisElements.Stellungen st2 = (gleisElements.Stellungen)f.weichen.get(w1);
            if (st1 != st2) {
               ret++;
            }
         } else {
            ret += 2;
         }
      }

      return ret;
   }

   public boolean hasÜP() {
      return this.foundüp;
   }

   public int getÜpENR() {
      try {
         return this.gleisüp.getENR();
      } catch (NullPointerException var2) {
         return 0;
      }
   }

   public boolean isRangiermodus() {
      return this.rangierlänge > 0;
   }

   public boolean allowsRf() {
      boolean rf = false;
      if (!this.fahrstrasseExtend.isDeleted() && this.zwerge.isEmpty() && !this.hasÜP() && this.fahrstrasseExtend.fstype != 16) {
         rf |= this.startSignalHasKnopf;
         rf |= this.startSignal.getElement() == gleis.ELEMENT_ZWERGSIGNAL;
         rf |= this.stopSignalHasKnopf;
         rf |= this.stopSignal.getElement() == gleis.ELEMENT_ZWERGSIGNAL;
      }

      return rf;
   }

   public boolean isRFonly() {
      boolean rf = false;
      if (!this.fahrstrasseExtend.isDeleted() && this.zwerge.isEmpty() && !this.hasÜP()) {
         rf |= this.startSignal.getElement() == gleis.ELEMENT_ZWERGSIGNAL;
         rf |= this.stopSignal.getElement() == gleis.ELEMENT_ZWERGSIGNAL;
         rf |= this.fahrstrasseExtend.fstype == 8;
      }

      return rf;
   }

   public boolean hasVSigTrenner() {
      return this.vSignalTrenner;
   }

   public boolean hasZwerg(gleis gl) {
      return this.zwerge.contains(gl);
   }

   public boolean hasZDSignel(gleis gl) {
      return this.zdeckungssignale.contains(gl);
   }

   public LinkedList<gleis> getGleisweg() {
      return this.gleisweg;
   }

   public List<gleis> getFlanken() {
      return new LinkedList(this.flankenweichen.keySet());
   }

   public HashMap<gleis, gleisElements.Stellungen> getWeichen() {
      return this.weichen;
   }

   public HashSet<gleis> getZDSignale() {
      return this.zdeckungssignale;
   }

   public boolean extendWeg() {
      boolean ret = false;
      if (this.lastZD != null) {
         gleis gl = this.lastZD;
         if (gl.getFluentData().getStellung() == gleis.ST_ZDSIGNAL_ROT) {
            LinkedList<gleis> gls = new LinkedList();
            gls.add(gl);
            gleis bgl = gl.nextByRichtung(false);
            gleis before = gl;

            while (bgl != null && bgl != before && bgl.getFluentData().getStatus() != 2) {
               if (bgl == this.stopSignal || bgl.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL && this.zdeckungssignale.contains(bgl)) {
                  if (bgl.getElement() == gleis.ELEMENT_ZDECKUNGSSIGNAL) {
                     this.lastZD = bgl;
                     if (bgl.getFluentData().getStellung() != gleis.ST_ZDSIGNAL_FESTGELEGT) {
                        bgl.getFluentData().setStellung(gleis.ST_ZDSIGNAL_ROT);
                        this.tjm_add(bgl);
                     }
                  } else {
                     this.lastZD = null;
                  }

                  for (gleis gg : gls) {
                     gg.getFluentData().setStatusByFs(1, this);
                     if (gg.getFluentData().getStatus() == 1 || gg.getFluentData().getStatus() == 3) {
                        this.lastGleis = gg;
                     }
                  }

                  gl.getFluentData().setStellung(gleis.ST_ZDSIGNAL_GRÜN);
                  ret = true;
                  break;
               }

               gls.add(bgl);
               gleis next = bgl.next(before);
               before = bgl;
               bgl = next;
            }

            this.connectVSigs();
         }
      }

      return ret;
   }

   public void connectVSigs() {
      gleis sig = this.getStop();
      if (this.lastZD != null) {
         sig = this.lastZD;
      }

      for (gleis gl : this.vorsignale) {
         if (gl.getFluentData().getStatus() == 1) {
            gl.getFluentData().setConnectedSignal(sig);
         }
      }

      if (!this.hasVSigTrenner() && this.getStart().getFluentData().getStellung() == gleisElements.ST_SIGNAL_GRÜN) {
         this.getStart().getFluentData().setConnectedSignal(sig);
      }
   }

   public Iterator<gleis> iterator() {
      return this.gleisweg.iterator();
   }

   public Vector getStructInfo() {
      Vector v = new Vector();
      v.addElement("Fahrstrasse");
      v.addElement(this.elementString(this.startSignal) + "-" + this.elementString(this.stopSignal));
      v.addElement(this);
      return v;
   }

   private String elementString(gleis gl) {
      if (gl == null) {
         return "---";
      } else {
         String en = "";
         if (gl.typHasElementName()) {
            en = gl.getElementName();
         } else if (gl.typShouldHaveSWwert()) {
            en = gl.getSWWert();
         } else {
            en = "X" + gl.getCol() + "/Y" + gl.getRow();
         }

         if (gl.getENR() > 0) {
            en = en + " (E" + gl.getENR() + ")";
         }

         return en;
      }
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("start");
      v.addElement(this.elementString(this.startSignal));
      v.addElement("stop");
      v.addElement(this.elementString(this.stopSignal));
      v.addElement("name");
      v.addElement(this.name);
      v.addElement("allowRf");
      v.addElement(this.allowsRf() + "");
      v.addElement("state");
      v.addElement(this.allocState.toString());
      v.addElement("zwerge");
      v.addElement(this.zwerge.size() + "");
      v.addElement("zdsignal");
      v.addElement(this.zdeckungssignale.size() + "");
      v.addElement("vorsignal");
      v.addElement(this.vorsignale.size() + "");
      v.addElement("vorsignaltrenner");
      v.addElement(this.vSignalTrenner + "");
      v.addElement("rangl");
      v.addElement(this.rangierlänge + "");
      v.addElement("dsig");
      v.addElement(this.elementString(this.lastZD));
      v.addElement("falseReason");
      v.addElement(this.falseReason + "");
      v.addElement("lastGl");
      v.addElement(this.elementString(this.lastGleis));

      for (gleis gl : this.gleisweg) {
         v.addElement(this.elementString(gl));
         String s = "";
         s = s + "S:" + gl.getFluentData().getStatus();
         s = s + " CF:" + (gl.getFluentData().getCurrentFS() != null);
         s = s + " St:" + gl.getFluentData().getStellung().toString();
         s = s + " Z:" + (gl.getFluentData().getZugAmGleis() != null);
         s = s + " Sp:" + gl.getFluentData().isGesperrt();
         s = s + " Hs:" + gl.hasHook(eventGenerator.T_GLEIS_STATUS);
         s = s + " Hst:" + gl.hasHook(eventGenerator.T_GLEIS_STELLUNG);
         v.addElement(s);
      }

      return v;
   }

   @Override
   public String getStructName() {
      return "fahrstrasse";
   }

   public boolean isDeleted() {
      return this.fahrstrasseExtend.isDeleted();
   }

   public boolean intersects(fahrstrasse fs1) {
      for (gleis gl : this.gleisweg) {
         if (fs1.gleisweg.contains(gl)) {
            return true;
         }
      }

      return false;
   }

   public boolean intersects(fahrstrasse fs1, element... ignoreElements) {
      element_list elist = new element_list(ignoreElements);

      for (gleis gl : this.gleisweg) {
         if (!elist.matches(gl.getElement()) && fs1.gleisweg.contains(gl)) {
            return true;
         }
      }

      return false;
   }
}
