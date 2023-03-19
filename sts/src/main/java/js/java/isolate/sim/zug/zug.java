package js.java.isolate.sim.zug;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.flagdata;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.displayBar.displayBar;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;
import js.java.isolate.sim.sim.fahrplanRenderer.redirektInfoContainer;
import js.java.isolate.sim.structServ.structinfo;
import js.java.isolate.sim.zug.fahrplanCollection.zugPlan;
import js.java.isolate.sim.zug.fahrplanCollection.zugPlanLine;
import js.java.schaltungen.timesystem.TimeFormat;
import js.java.tools.HTMLEntities;
import org.xml.sax.Attributes;

public class zug extends trigger implements structinfo, Comparable {
   static FATwriter debugMode = null;
   static final int PRERUNCOUNT = 8;
   static final double MINTEMPO = 0.2;
   int zid = 0;
   int azid = 0;
   String name = null;
   String zielgleis = "";
   long an = 0L;
   long ab = 0L;
   flagdata flags = new flagdata();
   int ein_enr;
   int aus_enr;
   boolean ein_enr_changed = false;
   int soll_tempo = 10;
   int soll_calc_tempo = 10;
   int laenge = 4;
   private int parken = 0;
   private char parkenRichtung = ' ';
   int anzid = 0;
   private String anzug = null;
   String hinweistext = null;
   String ein_stw = null;
   String aus_stw = null;
   boolean bstgRedirectSpecial = false;
   double ist_tempo = 0.0;
   String befehl_zielgleis = null;
   long befehl_zielgleis_zeit = 0L;
   boolean mytrain = false;
   double tempo_pos = 0.0;
   int cur_azid = 1;
   boolean azid_besucht = false;
   String vorsignal = null;
   boolean firstSignalPassed = false;
   long rottime = 0L;
   long anruftime = 0L;
   boolean weiterfahren = false;
   boolean abfahrtbefehl = false;
   boolean wartenOK = false;
   int anrufzaehler = 0;
   boolean schongefahren = false;
   boolean positionMelden = false;
   private zugPositionListener positionListener = null;
   long melderTime = 0L;
   int verspaetung = 0;
   int rotVerspaetung = 0;
   boolean notBremsung = false;
   String gestopptgleis = null;
   boolean ambahnsteig = false;
   boolean gleiswarok = false;
   long warankunft = 0L;
   boolean ignoriereBahnsteig = false;
   int bahnsteigcnt = 0;
   long wflagDelayed = 0L;
   tempoLimit baseTempo = new tempoLimit();
   CopyOnWriteArrayList<tl_base> baseFilter = new CopyOnWriteArrayList();
   @Deprecated
   boolean fromRightExtra = false;
   boolean sichtstopp = false;
   int c_richtigbahnsteig = 0;
   int c_falschbahnsteig = 0;
   int c_geaendertbahnsteig = 0;
   int genommeneausfahrt = 0;
   boolean b_richtigeausfahrt = false;
   String userText = "";
   String userTextSender = "";
   boolean fertig = false;
   boolean visible = false;
   boolean ready2go = false;
   gleis pos_gl = null;
   gleis before_gl = null;
   gleis lastBahnsteig = null;
   int namefarbe = 5;
   boolean isBahnsteig = true;
   long lastAbfahrt = 0L;
   int lastVerspaetung = 0;
   int haltabstand = 0;
   int lasthaltabstand = 0;
   long haltabstandgesehen = 0L;
   int haltabstandcnt = 0;
   int haltabstandanrufcnt = 0;
   displayBar disbar = null;
   boolean waitLWdone = false;
   boolean waitLW = false;
   zug callme = null;
   boolean anyDirection = false;
   int laengeBackup = 0;
   int startCnt = 0;
   ZugColorText namect = null;
   ZugColorText ankunft = null;
   ZugColorText abfahrt = null;
   ZugColorText von = null;
   ZugColorText nach = null;
   ZugColorText verspaetungCT = null;
   ZugColorText gleisCT = null;
   gleisbildModelFahrweg glbModel = null;
   zugModelInterface my_main = null;
   private TimeFormat sdf;
   private TimeFormat mdf;
   LinkedList<gleis> zugbelegt;
   ConcurrentSkipListMap<Long, zug> unterzuege = null;
   zug parentZug = null;
   LinkedList<String> chainVisits = new LinkedList();
   LinkedList<String> lastChainVisits = new LinkedList();
   boolean exitMode = false;
   boolean leaveAfterÜP = false;
   int üpwaitc = 0;
   boolean gotüp = false;
   boolean externalÜpReport = false;
   boolean allowRemove = true;
   private int mytrainÜPcount = 0;
   private int markColor = 0;
   private String markNum = "";
   zugVariables variables = new zugVariables();
   boolean emitted = false;
   private String additionalDescription = "";
   private redirektInfoContainer additionalDescriptionRic = null;
   zug.RICHTUNGWECHELN umdrehen = zug.RICHTUNGWECHELN.NONE;
   boolean runningUmdrehen = false;
   boolean hideGleis = false;
   LinkedList<zug.wflagData> wflagList = new LinkedList();
   private static int internalZid = 0;
   static HashMap<Integer, zug> killEzug = new HashMap();
   static ConcurrentHashMap<String, Integer> flagZug = new ConcurrentHashMap();
   private String lastLine = "";
   private int sync_lastVerspaetung = -1;
   private boolean sync_lastVisible = false;
   private long sync_rottime = 0L;
   private int synccc = 0;
   static Random rnd = null;
   private static long totalHeat = 0L;
   private static int heat10 = 0;
   private int heat = 0;
   private int irrelevantHeat = 0;
   private int irrelevantInc = 0;
   private double last_calc_tempo = 0.0;
   int lastmasstab = 0;
   boolean eingangbelegt = false;
   boolean outputValueChanged = false;
   long mytime = 0L;
   gleis next_gl = null;
   private static baseChain chain = new c_notMytrain();
   private static ConcurrentHashMap<Integer, zug> üpstore = new ConcurrentHashMap();
   int üpenr = 0;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static FATwriter getDebug() {
      return debugMode;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public boolean equals(Object z) {
      if (z instanceof zug) {
         return ((zug)z).zid == this.zid;
      } else {
         return z instanceof ZugColorText ? this.equals(((ZugColorText)z).getZug()) : false;
      }
   }

   public boolean uequals(zug z) {
      return z.zid == this.zid && z.azid == this.azid;
   }

   public int getAusEnr() {
      return this.aus_enr;
   }

   public int hashCode() {
      return this.zid;
   }

   public zug() {
      super();
   }

   public zug(String z, gleisbildModelFahrweg glb, zugModelInterface m) throws Exception {
      super();
      this.glbModel = glb;
      this.my_main = m;
      this.mdf = TimeFormat.getInstance(TimeFormat.STYLE.HMS);
      this.sdf = TimeFormat.getInstance(TimeFormat.STYLE.HM);
      this.init(z);
      this.init2(false);
   }

   public zug(Attributes attrs, String freetext, gleisbildModelEventsys glb, zugModelInterface m) throws Exception {
      super();
      this.glbModel = glb;
      this.my_main = m;
      this.mdf = TimeFormat.getInstance(TimeFormat.STYLE.HMS);
      this.sdf = TimeFormat.getInstance(TimeFormat.STYLE.HM);
      this.init(attrs, freetext);
      this.init2(false);
   }

   public zug(gleisbildModelFahrweg glb, zugModelInterface m, int gzid, zug.emitData ed) {
      super();
      this.glbModel = glb;
      this.my_main = m;
      this.mdf = TimeFormat.getInstance(TimeFormat.STYLE.HMS);
      this.sdf = TimeFormat.getInstance(TimeFormat.STYLE.HM);
      this.parken = 0;
      this.zid = gzid;
      if (ed.name == null) {
         this.name = "Testzug ZID " + this.zid;
      } else {
         this.name = ed.name;
      }

      this.azid = 0;
      this.mytrain = ed.mytrain;
      this.soll_tempo = ed.soll_tempo;
      this.laenge = ed.länge;
      this.zielgleis = ed.zielgleis;
      this.ein_enr = ed.ein_enr;
      this.aus_enr = ed.aus_enr;
      this.verspaetung = ed.verspaetung;
      this.ein_stw = ed.ein_stw;
      this.aus_stw = ed.aus_stw;
      this.ignoriereBahnsteig = ed.lastStopDone;
      if (ed.flags == null) {
         this.flags = new flagdata("");
      } else {
         this.flags = ed.flags;
      }

      this.emitted = true;
      this.an = this.my_main.getSimutime();
      this.ab = this.my_main.getSimutime() + 60000L * (long)ed.aufenthalt;
      this.prepareWflag(this.flags, this.zielgleis, this.an);
      this.init2(!this.mytrain);
      if (this.mytrain) {
         this.my_main.playZug();
      }
   }

   public zug(zug referenz, boolean kuppeln, boolean einfahren, String bahnsteig) {
      this(referenz, kuppeln, einfahren, bahnsteig, 0);
   }

   public zug(zug referenz, boolean kuppeln, boolean einfahren, String bahnsteig, int enr) {
      super();
      this.glbModel = referenz.glbModel;
      this.my_main = referenz.my_main;
      this.mdf = TimeFormat.getInstance(TimeFormat.STYLE.HMS);
      this.sdf = TimeFormat.getInstance(TimeFormat.STYLE.HM);
      this.parken = 0;
      ++internalZid;
      this.zid = -internalZid;
      if (einfahren) {
         this.name = "Ersatzlok " + referenz.name;
      } else {
         this.name = "Lok " + referenz.name;
      }

      this.azid = 0;
      this.mytrain = true;
      this.soll_tempo = (int)randomTimeShift(1L, 3L, 4L);
      this.laenge = 2;
      this.zielgleis = bahnsteig;
      if (einfahren) {
         this.ein_enr = Math.abs(enr);
         if (this.glbModel.findFirst(new Object[]{this.ein_enr, gleis.ELEMENT_EINFAHRT}) == null) {
            throw new IllegalArgumentException("Einfahrt " + this.ein_enr + " nicht gefunden!");
         }
      } else {
         this.ein_enr = 0;
      }

      if (kuppeln) {
         this.aus_enr = 0;
      } else {
         this.aus_enr = Math.abs(enr);
         if (this.glbModel.findFirst(new Object[]{this.aus_enr, gleis.ELEMENT_AUSFAHRT}) == null) {
            throw new IllegalArgumentException("Ausfahrt " + this.aus_enr + " nicht gefunden!");
         }
      }

      this.verspaetung = 0;
      if (kuppeln) {
         this.flags = new flagdata("K", referenz.getZID(), "");
         this.callme = referenz;
      } else {
         this.flags = new flagdata();
      }

      this.anyDirection = true;
      this.mytrain = !einfahren;
      this.ignoriereBahnsteig = true;
      this.an = this.my_main.getSimutime() - 60000L;
      this.ab = this.my_main.getSimutime() + 10000L;
      this.init2(true);
      if (!einfahren) {
         this.visible = true;
         Iterator<gleis> it;
         if (enr < 0) {
            this.before_gl = (gleis)referenz.zugbelegt.get(1);
            this.pos_gl = (gleis)referenz.zugbelegt.get(0);
            it = referenz.zugbelegt.iterator();
         } else {
            this.before_gl = referenz.before_gl;
            this.pos_gl = referenz.pos_gl;
            it = referenz.zugbelegt.descendingIterator();
         }

         this.lastmasstab = this.pos_gl.getMasstab();
         this.zugbelegt = new LinkedList();

         for(int i = 0; it.hasNext() && i < 4; ++i) {
            gleis ig = (gleis)it.next();
            this.zugbelegt.addFirst(ig);
            ig.getFluentData().setZugAmGleis(null);
            ig.getFluentData().setStatusByZug(0, referenz);
            ig.getFluentData().setStatusByZug(2, this);
            if (referenz.zugbelegt.size() > 4) {
               it.remove();
            } else if (i > 2) {
               break;
            }
         }

         this.shortenZug();
      }

      this.tjm_add();
   }

   private void init(String z) throws Exception {
      StringTokenizer zst = new StringTokenizer(z + " ", ",");
      if (zst.countTokens() >= 13) {
         this.zid = Integer.parseInt(zst.nextToken().trim());
         this.azid = Integer.parseInt(zst.nextToken().trim());
         this.name = zst.nextToken().trim();
         this.mytrain = Integer.parseInt(zst.nextToken().trim()) > 0;
         this.soll_tempo = Integer.parseInt(zst.nextToken().trim());
         this.laenge = Integer.parseInt(zst.nextToken().trim());
         this.zielgleis = zst.nextToken().trim();

         try {
            this.an = this.mdf.parse(zst.nextToken().trim());
         } catch (ParseException var6) {
            throw new Exception("an-Fehler: " + var6.getMessage());
         }

         try {
            this.ab = this.mdf.parse(zst.nextToken().trim());
         } catch (ParseException var5) {
            throw new Exception("ab-Fehler: " + var5.getMessage());
         }

         this.ein_enr = Integer.parseInt(zst.nextToken().trim());
         this.aus_enr = Integer.parseInt(zst.nextToken().trim());
         String flagS = zst.nextToken().trim();
         String flagdataS = zst.nextToken().trim();
         this.flags = new flagdata(flagS, flagdataS, null);
         this.verspaetung = Integer.parseInt(zst.nextToken().trim());
         this.syncWith();
      }
   }

   private void init(Attributes attrs, String pcdata) throws Exception {
      this.zid = Integer.parseInt(attrs.getValue("zid").trim());
      this.mytrain = Integer.parseInt(attrs.getValue("aid").trim()) > 0;

      try {
         this.azid = Integer.parseInt(attrs.getValue("azid").trim());
      } catch (NumberFormatException | NullPointerException var18) {
         return;
      }

      this.name = attrs.getValue("name").trim();
      zug nz = this.my_main.findZugByFullName(this.name);
      if (nz != null && nz.zid != this.zid) {
         this.name = null;
      } else {
         try {
            this.an = this.mdf.parse(attrs.getValue("an").trim());
         } catch (NumberFormatException | ParseException var16) {
            throw new Exception("an-Fehler: " + var16.getMessage());
         } catch (NullPointerException var17) {
         }

         try {
            this.ab = this.mdf.parse(attrs.getValue("ab").trim());
         } catch (NumberFormatException | ParseException var14) {
            throw new Exception("ab-Fehler: " + var14.getMessage());
         } catch (NullPointerException var15) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var15);
         }

         try {
            this.zielgleis = attrs.getValue("zielgleis").trim();
            this.soll_tempo = Integer.parseInt(attrs.getValue("tempo").trim());
            this.soll_calc_tempo = this.soll_tempo;
            this.laenge = Integer.parseInt(attrs.getValue("laenge").trim());
            this.ein_enr = Integer.parseInt(attrs.getValue("ein_enr").trim());
            this.aus_enr = Integer.parseInt(attrs.getValue("aus_enr").trim());
            String flagS = "";
            String flagdataS = "";
            String flagparamS = "";
            if (attrs.getValue("flags") != null) {
               flagS = attrs.getValue("flags").trim();
            }

            if (attrs.getValue("flagdata") != null) {
               flagdataS = attrs.getValue("flagdata").trim();
            }

            if (attrs.getValue("flagparam") != null) {
               flagparamS = attrs.getValue("flagparam").trim();
            }

            this.flags = new flagdata(flagS, flagdataS, flagparamS);
            this.prepareWflag(this.flags, this.zielgleis, this.an);
         } catch (NumberFormatException | NullPointerException var13) {
         }

         try {
            this.verspaetung = Integer.parseInt(attrs.getValue("verspaetung").trim());
         } catch (NumberFormatException | NullPointerException var12) {
         }

         this.syncWith();

         try {
            int l = Integer.parseInt(attrs.getValue("istlaenge").trim());
            if (l > 0) {
               this.laenge = l;
            }
         } catch (NumberFormatException | NullPointerException var11) {
         }

         try {
            int m = Integer.parseInt(attrs.getValue("maxtempo").trim());
            if (m > 0) {
               this.soll_tempo = m;
            }
         } catch (NumberFormatException | NullPointerException var10) {
         }

         if (this.my_main.isFirstRun()) {
            try {
               this.parken = Integer.parseInt(attrs.getValue("parken").trim());
            } catch (NumberFormatException | NullPointerException var9) {
            }

            try {
               this.parkenRichtung = attrs.getValue("prichtung").trim().toLowerCase().charAt(0);
            } catch (IndexOutOfBoundsException | NullPointerException var8) {
            }
         }

         try {
            if (attrs.getValue("hinweis").trim().length() > 0) {
               this.hinweistext = attrs.getValue("hinweis").trim();
            }
         } catch (Exception var7) {
         }

         try {
            if (attrs.getValue("setparam").trim().length() > 0) {
               String r = attrs.getValue("setparam").trim();
               StringTokenizer zst = new StringTokenizer(r + " ", ":");

               while(zst.hasMoreTokens()) {
                  String p = zst.nextToken();
                  this.setParam(p);
               }
            }
         } catch (Exception var19) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var19);
         }
      }
   }

   private boolean init2(boolean additional) {
      if (this.parken == 1 && killEzug.containsKey(this.zid)) {
         return false;
      } else if (this.name == null) {
         zug z2 = this.my_main.findZug(this.zid);
         if (z2 != null) {
            z2.mytrain = this.mytrain;
         }

         return false;
      } else if (!additional && this.my_main.haveWeSeenIt(this)) {
         return false;
      } else {
         if (this.ein_enr == 0 && this.parken == 0 && !additional) {
         }

         if (this.glbModel.getAdapter() != null && this.glbModel.getAdapter().getSimPrefs() != null) {
            this.hideGleis = this.glbModel.getAdapter().getSimPrefs().getBoolean("noGleis", false);
         }

         this.ankunft = new ZugColorText(this, new ankunftHandler());
         this.abfahrt = new ZugColorText(this, new abfahrtHandler());
         this.von = new ZugColorText(this, new vonHandler());
         this.nach = new ZugColorText(this, new nachHandler());
         this.namect = new ZugColorText(this, new nameHandler());
         this.verspaetungCT = new ZugColorText(this, new verspaetungHandler(), "0");
         this.gleisCT = new ZugColorText(this, new gleisHandler());
         this.cur_azid = this.azid;
         zug z2 = null;
         if (this.parken != 2) {
            z2 = this.my_main.addZug(this);
         }

         if (this.flags.hasFlag('K')) {
            String k = this.zielgleis + "/" + this.flags.dataOfFlag('K') + "/" + this.ab;
            flagZug.put(k, this.zid);
         }

         this.parentZug = this;
         if (z2 == null || this.parken == 2) {
            this.zugbelegt = new LinkedList();
            if (this.parken == 2) {
               this.mytrain = true;
               this.visible = true;
               this.ambahnsteig = true;
               this.flags.killFlag('L');
               this.flags.killFlag('W');
               this.wflagList.clear();
               if (isDebug()) {
                  getDebug().writeln("zug (" + this.getName() + ")", "Parken " + this.zid);
               }

               LinkedList<gleis> fb_gl = this.glbModel.findBahnsteig(this.zielgleis);
               if (fb_gl.size() > 0) {
                  this.pos_gl = (gleis)fb_gl.getFirst();
                  gleisElements.RICHTUNG sricht = null;
                  switch(this.parkenRichtung) {
                     case 'd':
                        sricht = gleisElements.RICHTUNG.down;
                        break;
                     case 'l':
                        sricht = gleisElements.RICHTUNG.left;
                        break;
                     case 'r':
                        sricht = gleisElements.RICHTUNG.right;
                        break;
                     case 'u':
                        sricht = gleisElements.RICHTUNG.up;
                  }

                  if (sricht != null) {
                     for(gleis pgl : fb_gl) {
                        if (pgl.getRichtung() == sricht) {
                           this.pos_gl = pgl;
                           break;
                        }
                     }
                  }
               } else {
                  this.pos_gl = null;
               }

               if (this.pos_gl != null && this.pos_gl.getFluentData().getStatus() != 2) {
                  this.flags.killFlag('R');
                  z2 = this.my_main.addZug(this);
                  this.warankunft = this.my_main.getSimutime();
                  this.gestopptgleis = this.zielgleis;
                  this.gleiswarok = true;
                  this.ambahnsteig = true;
                  this.ist_tempo = 0.0;
                  this.my_main.setZugOnBahnsteig(this.gestopptgleis, this, this.pos_gl);
                  this.lastmasstab = this.pos_gl.getMasstab();
                  this.zugbelegt.addLast(this.pos_gl);
                  this.lastBahnsteig = this.pos_gl;
                  this.pos_gl.getFluentData().setStatusByZug(2, this);
                  this.before_gl = this.pos_gl;
                  this.pos_gl = this.pos_gl.nextByRichtung(true);
                  if (this.pos_gl != null) {
                     this.zugbelegt.addLast(this.pos_gl);
                  }

                  try {
                     this.pos_gl.getFluentData().setStatusByZug(2, this);

                     for(int i = 2; i < this.calcLaenge(this.lastmasstab); ++i) {
                        gleis next_gl = this.pos_gl.next(this.before_gl);
                        if (next_gl == null) {
                           break;
                        }

                        this.zugbelegt.addLast(next_gl);
                        next_gl.getFluentData().setStatusByZug(2, this);
                        this.before_gl = this.pos_gl;
                        this.pos_gl = next_gl;
                     }
                  } catch (NullPointerException var7) {
                  }

                  this.tjm_add();
               } else {
                  this.visible = false;
                  this.ambahnsteig = false;
                  this.fertig = true;
                  this.my_main.hideZug(this);
                  if (this.flags.hasFlag('E')) {
                     killEzug.put(this.flags.dataOfFlag('E'), this);
                     this.flags.killFlag('E');
                  }

                  if (isDebug()) {
                     getDebug()
                        .writeln(
                           "zug (" + this.getName() + ")",
                           "Gleisbild/Fahrplan Fehler: Zug " + this.zid + "/" + this.name + " an Bahnsteig '" + this.zielgleis + "' parken nicht möglich!"
                        );
                  }
               }
            } else if (this.ein_enr > 0 || this.ein_stw != null) {
               this.tjm_add();
            }
         } else if (z2.azid == this.azid) {
            z2.copyFrom(this);
         } else {
            if (z2.unterzuege == null) {
               z2.unterzuege = new ConcurrentSkipListMap();
            }

            if (!z2.unterzuege.containsKey(this.an * 100L + (long)this.azid)) {
               z2.unterzuege.put(this.an * 100L + (long)this.azid, this);
               z2.wflagList.addAll(this.wflagList);
            }

            this.wflagList.clear();
            this.parentZug = z2;
         }

         return z2 != null;
      }
   }

   void updateData() {
      this.ankunft.update();
      this.abfahrt.update();
      this.von.update();
      this.nach.update();
      this.namect.update();
      this.verspaetungCT.update();
      this.gleisCT.update();
   }

   public void close() {
      this.before_gl = null;
      this.pos_gl = null;
      this.next_gl = null;
      this.baseFilter.clear();
      this.chainVisits.clear();
      this.glbModel = null;
      this.lastBahnsteig = null;
      this.my_main = null;
      this.parentZug = null;
      if (this.unterzuege != null) {
         this.unterzuege.values().stream().forEach(zug::close);
         this.unterzuege.clear();
      }

      this.disbar = null;
      this.positionListener = null;
      this.wflagList.clear();
      this.variables.close();
      if (this.zugbelegt != null) {
         this.zugbelegt.clear();
      }
   }

   public void lifeRemove() {
      if (!this.fertig) {
         if (this.mytrain && this.visible) {
            while(this.zugbelegt.size() > 0) {
               gleis gl = (gleis)this.zugbelegt.removeFirst();
               gl.getFluentData().setStatusByZug(0, this);
               this.setRot(gl, true);
            }

            this.pos_gl.getFluentData().setStatusByZug(0, this);
            this.setRot(this.pos_gl, true);
            if (this.gotüp) {
               this.gotüp = false;
               this.my_main.getFSallocator().zugTakenMessage("OK", this.ein_enr, this.zid);
            }

            this.fertig = true;
            this.my_main.hideZug(this);
            this.successorRemove();
         } else {
            this.fertig = true;
            this.my_main.hideZug(this);
            this.my_main.delZug(this);
            this.successorRemove();
         }
      }
   }

   public void remove() {
      try {
         if (flagZug.containsValue(this.zid)) {
            for(Entry<String, Integer> k : flagZug.entrySet()) {
               if (k.getValue() == this.zid) {
                  flagZug.remove(k.getKey());
               }
            }
         }
      } catch (Exception var3) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "zug remove ex", var3);
      }
   }

   public void successorRemove() {
      Iterator<zug> it = this.getAllUnseenFahrplanzeilen();

      while(it.hasNext()) {
         zug zz = (zug)it.next();
         if (zz.flags.hasFlag('E') || zz.flags.hasFlag('F')) {
            zug dstz = this.my_main.findZug(zz.flags.getFlagdata());
            if (dstz != null) {
               dstz.fertig = true;
               if (zz.flags.hasFlag('E')) {
                  killEzug.put(this.zid, this);
               }

               this.my_main.hideZug(dstz);
            }
         }
      }
   }

   public void setMyTrain(boolean m) {
      if (!this.visible && !this.fertig && m) {
         this.mytrain = m;
         if (m) {
            if (debugMode != null) {
               debugMode.writeln("mytrain " + this.getName());
            }

            this.fertig = false;
            this.exitMode = false;
            this.allowRemove = false;
            this.leaveAfterÜP = false;
         }

         if (this.mytrain && this.namefarbe == 0) {
            this.namefarbe = 5;
            this.namect.setBGColor(this.namefarbe);
         }

         this.my_main.updateZug(this);
      }
   }

   public void setParam(String p) {
      if (debugMode != null) {
         debugMode.writeln("simparam (" + this.getName() + "):" + p);
      }

      this.variables.setParameter(p);
   }

   public void setUpdate(int v, int l, int t) {
      if (!this.visible && !this.fertig) {
         this.verspaetung = v;
         if (l > 0) {
            this.laenge = l;
         }

         if (t > 0) {
            this.soll_tempo = t;
         }

         this.my_main.updateZug(this);
      }
   }

   void forceSyncWith() {
      this.sync_lastVisible = !this.visible;
      this.syncWith();
   }

   void syncWith() {
      if (this.mytrain
         && (
            this.sync_rottime < this.rottime
               || this.sync_lastVerspaetung != this.verspaetung
               || this.sync_lastVisible != this.visible
               || this.lastLine.compareTo(this.toString()) != 0
         )) {
         ++this.synccc;
         if (this.synccc > (5 + this.zid % 5) * 60 || this.sync_lastVisible != this.visible || this.sync_rottime < this.rottime) {
            this.sync_lastVerspaetung = this.verspaetung;
            this.sync_lastVisible = this.visible;
            this.lastLine = this.toString();
            if (this.visible) {
               this.my_main.syncZug(this);
            }

            this.synccc = 0;
            this.sync_rottime = this.rottime;
         }
      }
   }

   public static long randomTimeShift(long min, long n, long max) {
      long ret = n;
      if (rnd == null) {
         rnd = new Random();
      }

      double v;
      do {
         v = rnd.nextGaussian();
      } while(v < -1.0 || v > 1.0);

      if (v > 0.0) {
         ret = Math.round((double)n + v * (double)(max - n));
      } else if (v < 0.0) {
         ret = Math.round((double)n + v * (double)(n - min));
      }

      return ret;
   }

   public static double random(double min, double n, double max) {
      double ret = n;
      if (rnd == null) {
         rnd = new Random();
      }

      double v;
      do {
         v = rnd.nextGaussian();
      } while(v < -1.0 || v > 1.0);

      if (v > 0.0) {
         ret = v * (max - n) + n;
      } else if (v < 0.0) {
         ret = -v * (min - n) + n;
      }

      return ret;
   }

   public static int calcMaxSpeed(gleisbildModelFahrweg glb, int masstab) {
      return glb.getMasstabCalculator().calcMaxSpeed(masstab);
   }

   public int calcMaxSpeed(int masstab) {
      return this.glbModel.getMasstabCalculator().calcMaxSpeed(masstab);
   }

   int calcLaenge(int masstab) {
      return this.glbModel.getMasstabCalculator().calcLaenge(masstab, this.laenge);
   }

   public static long getHeat() {
      return totalHeat;
   }

   void decHeat() {
      if (this.zid > 0) {
         totalHeat -= (long)this.heat;
         this.heat -= this.irrelevantHeat;
         this.heat -= this.irrelevantInc;
         this.irrelevantHeat = 0;
         this.irrelevantInc = 0;
         this.heat -= 5;
         totalHeat += (long)this.heat;
         this.my_main.updateHeat(totalHeat);
      }
   }

   void updateHeat(boolean relevant, int istVerspaetung, int lastVerspaetung) {
      if (this.zid > 0) {
         totalHeat -= (long)this.heat;
         int uheat = Math.max(istVerspaetung, 0) - Math.max(lastVerspaetung, 0);
         this.heat -= this.irrelevantHeat;
         this.irrelevantHeat = 0;
         if (relevant) {
            this.heat -= this.irrelevantInc;
            this.irrelevantInc = 0;
         }

         if (uheat > 0) {
            this.heat += uheat * 10;
            if (!relevant) {
               this.irrelevantHeat += uheat * 10;
               ++this.heat;
               ++this.irrelevantInc;
            }
         }

         totalHeat += (long)this.heat;
         if (uheat > 0) {
            this.my_main.updateHeat(totalHeat);
         }
      }
   }

   long wayTime() {
      long ret = 0L;
      gleis gl = this.glbModel.findFirst(new Object[]{this.ein_enr, gleis.ELEMENT_EINFAHRT});
      if (gl != null) {
         try {
            int w = this.glbModel.getWayTime(gl.getSWWert(), this.zielgleis);
            ret = (long)(600.0 * (double)w / (double)this.soll_calc_tempo);
            ret += ret / 6L;
         } catch (NullPointerException var5) {
         }
      }

      return ret;
   }

   long exitTime() {
      long ret = 0L;
      gleis gl = this.glbModel.findFirst(new Object[]{this.aus_enr, gleis.ELEMENT_AUSFAHRT});
      if (gl != null) {
         try {
            int w = this.glbModel.getWayTimeA(gl.getSWWert(), this.zielgleis);
            ret = 500L * (long)w / (long)this.soll_calc_tempo;
            ret += ret / 5L;
         } catch (NullPointerException var5) {
         }
      }

      return ret;
   }

   void gotoTempo(double mx) {
      if (this.ist_tempo > mx) {
         double r = random(0.05, 0.5, 0.95);
         this.ist_tempo -= r;
         this.last_calc_tempo = -r;
      } else if (this.ist_tempo == mx) {
         double r = random(-0.1, 0.05, 0.2);
         this.ist_tempo -= r;
         this.last_calc_tempo = -r;
      } else {
         double r;
         if (this.ist_tempo < (double)(1 + this.laenge / 50)) {
            r = random(0.0, 0.05, 0.2);
         } else {
            r = random(-0.6, 0.4, 1.0);
         }

         this.ist_tempo += r;
         this.last_calc_tempo = r;
         if (this.ist_tempo > mx) {
            this.ist_tempo = mx;
         }
      }

      if (this.ist_tempo < 0.0) {
         this.ist_tempo = 0.0;
      }
   }

   void addLimit(tl_base l) {
      this.baseFilter.add(l);
   }

   void removeLimit(Class<? extends tl_base> c) {
      for(tl_base t : this.baseFilter) {
         if (t.getClass() == c) {
            this.baseFilter.remove(t);
            break;
         }
      }
   }

   tl_base getLimit(Class<? extends tl_base> c) {
      for(tl_base t : this.baseFilter) {
         if (t.getClass() == c) {
            return t;
         }
      }

      return null;
   }

   double calc_tempo() {
      double mx = this.baseTempo.calc_tempo(this, 0.0);

      for(tl_base l : this.baseFilter) {
         mx = l.calc_tempo(this, mx);
      }

      return mx;
   }

   void calcAndSet_tempo() {
      this.gotoTempo(this.calc_tempo());
   }

   double getLastCalcTempo() {
      return this.last_calc_tempo;
   }

   double getHaltabstand() {
      return (double)this.haltabstand;
   }

   public double getIST() {
      return this.ist_tempo;
   }

   public void setIST(double t) {
      this.ist_tempo = t;
   }

   int unterzugOffen() {
      int cc = 0;
      if (this.unterzuege != null) {
         for(zug z : this.unterzuege.values()) {
            if (!z.azid_besucht) {
               ++cc;
            }
         }
      }

      return cc;
   }

   public Iterator<zug> getAllUnseenFahrplanzeilen() {
      LinkedList<zug> l = new LinkedList();
      l.add(this);
      if (this.unterzuege != null) {
         for(zug zz : this.unterzuege.values()) {
            if (!zz.azid_besucht) {
               l.add(zz);
            }
         }
      }

      return l.iterator();
   }

   zug unterzug(int i) {
      if (i != 0 && this.unterzuege != null) {
         int cc = i;

         for(zug zz : this.unterzuege.values()) {
            if (!zz.azid_besucht) {
               if (--cc == 0) {
                  return zz;
               }
            }
         }

         return null;
      } else {
         return this;
      }
   }

   zug unterzugByAZid(int azid) {
      if (azid != this.cur_azid && this.unterzuege != null) {
         for(zug zz : this.unterzuege.values()) {
            if (!zz.azid_besucht && zz.azid == azid) {
               return zz;
            }
         }

         return null;
      } else {
         return this;
      }
   }

   ConcurrentSkipListMap<Long, zug> transferUnterzuege(ConcurrentSkipListMap<Long, zug> u) {
      if (u == null) {
         return null;
      } else {
         for(zug l : u.values()) {
            l.parentZug = this;
         }

         return u;
      }
   }

   void nextUnterzug() {
      if (this.unterzuege != null && this.unterzuege.size() > 0) {
         zug minzz = null;
         long minz = 216000000L;

         for(zug zz : this.unterzuege.values()) {
            if (!zz.azid_besucht && zz.azid != this.cur_azid && zz.an - this.ab < minz && zz.an > this.ab) {
               minz = zz.an - this.ab;
               minzz = zz;
            }
         }

         if (minzz != null) {
            minzz.azid_besucht = true;
            this.cur_azid = minzz.azid;
            this.an = minzz.an;
            this.ab = minzz.ab;
            this.zielgleis = minzz.zielgleis;
            this.flags = minzz.flags;
            this.hinweistext = minzz.hinweistext;
            this.befehl_zielgleis = minzz.befehl_zielgleis;
            this.befehl_zielgleis_zeit = minzz.befehl_zielgleis_zeit;
            this.updateData();
            this.my_main.updateZug(this);
         } else {
            this.ignoriereBahnsteig = true;
            this.sync_lastVisible = false;
         }
      } else {
         this.ignoriereBahnsteig = true;
         this.sync_lastVisible = false;
      }
   }

   void refreshZugAmGleis() {
      for(gleis gl : this.zugbelegt) {
         gl.getFluentData().setZugAmGleis(this);
      }
   }

   void setRot(gleis gl, boolean zugende) {
      if (gl != null) {
         if (gl.getElement() == gleis.ELEMENT_SIGNAL && this.zugbelegt.contains(gl.nextByRichtung(false))) {
            gl.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
            if (this.gotüp && zugende) {
               this.gotüp = false;
               this.my_main.getFSallocator().zugTakenMessage("OK", this.ein_enr, this.zid);
               if (debugMode != null) {
                  debugMode.writeln("zug (" + this.getName() + ")", "ÜP Übergabe abgeschlossen");
               }
            }
         }

         if (gl.getElement() == gleis.ELEMENT_ZWERGSIGNAL && this.zugbelegt.contains(gl.nextByRichtung(false)) && zugende) {
            gl.getFluentData().setStellung(gleisElements.ST_SIGNAL_ROT);
         }
      }
   }

   void shortenZug() {
      for(int i = 0; this.zugbelegt.size() > this.calcLaenge(this.lastmasstab) && i < 2; ++i) {
         gleis gl = (gleis)this.zugbelegt.removeFirst();
         gl.getFluentData().setStatusByZug(0, this);
         this.setRot(gl, true);
      }
   }

   void tjmAdd() {
      this.tjm_add();
   }

   boolean call(eventGenerator.TYPES typ, zugmsg e) {
      return super.call(typ, e);
   }

   @Override
   public boolean ping() {
      if ((long)heat10 != this.mytime / 60000L / 5L) {
         heat10 = (int)(this.mytime / 60000L / 5L);
         if (totalHeat < -50L) {
            totalHeat += 25L;
         } else if (totalHeat > 50L) {
            totalHeat -= 40L;
         }
      }

      boolean ret = false;
      this.outputValueChanged = false;
      if (!this.fertig && this.my_main != null) {
         this.mytime = this.my_main.getSimutime();
         ret = chain.run(this);
         if (debugMode != null) {
            this.lastChainVisits = this.chainVisits;
            this.chainVisits = new LinkedList();
         }

         this.outputValueChanged |= this.namect.setBGColor(this.namefarbe);
         if (!this.fertig) {
            if (this.outputValueChanged) {
               this.updateData();
               this.my_main.updateZug(this);
               this.my_main.refreshZug();
            }

            this.tjm_add();
            this.syncWith();
         }
      }

      return ret;
   }

   void melde(String text) {
      this.positionMelden = false;
      long t = this.my_main.getSimutime();
      if ((t - this.melderTime) / 60000L < 1L) {
         text = "Ey Kollege, gehts noch?";
      } else if ((t - this.melderTime) / 60000L < 2L) {
         text = "Bin ich hier die Auskunft oder was?<br>" + text;
      } else if ((t - this.melderTime) / 60000L < 5L) {
         text = "He Kollege, du solltest dir das mal merken, ich hab hier noch anderes zu tun!<br>" + text;
      } else if ((t - this.melderTime) / 60000L < 10L) {
         text = "Schon wieder? Irgendwie leicht überfordert, was? Oder ist dir langweilig?<br>" + text;
      }

      this.positionListener.melde(this, text);
      this.melderTime = t;
   }

   private void copyFrom(zug z) {
      if (this.cur_azid == this.azid) {
         this.mytrain = z.mytrain;
         if (this.mytrain && this.namefarbe == 0) {
            this.namefarbe = 5;
         }

         if (!this.visible) {
            this.verspaetung = z.verspaetung;
            this.syncWith();
            this.laenge = z.laenge;
         }
      }
   }

   private void setStatus(gleis pos_gl, boolean belegt) {
      if (belegt) {
         this.my_main.getFSallocator().blockAusfahrt(pos_gl);
      } else {
         this.my_main.getFSallocator().unblockAusfahrt(pos_gl);
      }
   }

   void storeZugÜP(boolean belegt) {
      this.storeZugÜP(null, belegt);
   }

   void storeZugÜP(gleis pos_gl, boolean belegt) {
      if (belegt) {
         this.üpenr = pos_gl.getENR();
         üpstore.put(this.üpenr, this);
         this.setStatus(pos_gl, true);
      } else if (this.üpenr > 0) {
         üpstore.remove(this.üpenr);
         pos_gl = this.glbModel.findFirst(new Object[]{this.üpenr, gleis.ELEMENT_ÜBERGABEPUNKT});
         this.setStatus(pos_gl, false);
      }
   }

   public static zug findZugAtENR(int aus_enr) {
      return (zug)üpstore.get(aus_enr);
   }

   public String toString() {
      String t = "";
      if (this.fertig || this.exitMode) {
         t = "done";
      } else if (!this.mytrain) {
         t = "foreign";
      } else if (this.visible && this.ignoriereBahnsteig) {
         t = "visible_done";
      } else if (this.visible) {
         t = "visible";
      } else {
         t = "";
      }

      int v = this.verspaetung;
      if (this.rottime > 0L) {
         v += (int)(Math.random() * 10.0);
      }

      return "zid="
         + this.zid
         + ",verspaetung="
         + v
         + ",istlaenge="
         + this.laenge
         + ",fertig="
         + t
         + " ,c_richtig="
         + this.c_richtigbahnsteig
         + ",c_falsch="
         + this.c_falschbahnsteig
         + ",c_geaendert="
         + this.c_geaendertbahnsteig
         + ",ausfahrt="
         + this.genommeneausfahrt
         + ",richtig="
         + (this.b_richtigeausfahrt ? "ok" : "nok");
   }

   public boolean isFertig() {
      return this.fertig;
   }

   public boolean isNotVisible() {
      return !this.mytrain && !this.visible;
   }

   public boolean isMytrain() {
      return this.mytrain;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean isAnyDirection() {
      return this.anyDirection;
   }

   public boolean amBahnsteig() {
      return this.ambahnsteig;
   }

   public boolean isRedirect() {
      return this.ein_stw != null && this.aus_stw != null;
   }

   public boolean firstSignalPassed() {
      return this.firstSignalPassed;
   }

   public String getZID() {
      return this.zid + "";
   }

   public int getZID_num() {
      return this.zid;
   }

   public String getName() {
      return this.name;
   }

   public String getSpezialName() {
      return this.name.indexOf(37) >= 0 ? this.name.substring(0, this.name.indexOf(37)).trim() : this.name;
   }

   public int getCurAzid() {
      return this.cur_azid;
   }

   public ZugColorText getNameCT() {
      return this.namect;
   }

   public boolean lastStopDone() {
      return this.ignoriereBahnsteig;
   }

   public long diffToAn() {
      return (this.my_main.getSimutime() - this.an) / 1000L;
   }

   public long getAn() {
      return this.an;
   }

   public String getAnkunft() {
      return this.ein_stw != null && this.aus_stw != null ? "" : this.sdf.format(this.an);
   }

   public ZugColorText getAnkunftCT() {
      return this.ankunft;
   }

   public String getAbfahrt() {
      if (this.ein_stw != null && this.aus_stw != null) {
         return "";
      } else {
         return !this.flags.hadFlag('E') && !this.flags.hadFlag('K') ? this.sdf.format(this.ab) : "";
      }
   }

   public ZugColorText getAbfahrtCT() {
      return this.abfahrt;
   }

   String getGleis() {
      if (this.ein_stw != null && this.aus_stw != null) {
         return "Umleitung";
      } else if (this.befehl_zielgleis != null) {
         if (this.gleisCT != null) {
            this.gleisCT.setBGColor(9);
         }

         return this.befehl_zielgleis + " (/" + this.zielgleis + "/)";
      } else {
         if (this.gleisCT != null) {
            this.gleisCT.setBGColor(0);
         }

         return this.zielgleis;
      }
   }

   public ZugColorText getGleisCT() {
      return this.gleisCT;
   }

   public String getPlanGleis() {
      return this.zielgleis;
   }

   public String getZielGleis() {
      if (this.ambahnsteig && this.gestopptgleis != null) {
         return this.gestopptgleis;
      } else {
         return this.befehl_zielgleis != null ? this.befehl_zielgleis : this.zielgleis;
      }
   }

   public String getZielgleis(int unterzug) {
      zug z = this.unterzug(unterzug);
      if (z == null) {
         z = this;
      }

      return z.zielgleis;
   }

   public String getZielgleisByAZid(int unterzugAZid) {
      zug z = this.unterzugByAZid(unterzugAZid);
      if (z == null) {
         z = this;
      }

      return z.zielgleis;
   }

   public String getVon() {
      String ret = "";
      if (this.ein_stw != null) {
         return this.ein_stw;
      } else {
         if (this.ein_enr != 0) {
            gleis g = this.glbModel.findFirst(new Object[]{this.ein_enr, gleis.ELEMENT_EINFAHRT});

            try {
               ret = g.getSWWert_special().trim();
            } catch (NullPointerException var4) {
               ret = this.ein_enr + "";
            }
         }

         return ret;
      }
   }

   public ZugColorText getVonCT() {
      return this.von;
   }

   public String getNach() {
      String ret = "";
      if (this.aus_stw != null) {
         return this.aus_stw;
      } else {
         if (this.aus_enr == 0) {
            if (this.flags.hadFlag('E')) {
               ret = (this.hideGleis ? "" : "Gleis ") + this.getGleis();
            } else if (this.unterzuege != null) {
               for(zug zz : this.unterzuege.values()) {
                  if (!zz.azid_besucht && zz.azid != this.cur_azid && zz.flags.hadFlag('E')) {
                     ret = (this.hideGleis ? "" : "Gleis ") + zz.getGleis();
                     break;
                  }
               }
            }
         } else {
            gleis g = this.glbModel.findFirst(new Object[]{this.aus_enr, gleis.ELEMENT_AUSFAHRT});

            try {
               ret = g.getSWWert_special().trim();
            } catch (NullPointerException var4) {
               ret = this.aus_enr + "";
            }
         }

         return ret;
      }
   }

   public ZugColorText getNachCT() {
      return this.nach;
   }

   public String getVerspaetungOrAnkunft() {
      if (this.ein_stw != null && this.aus_stw != null) {
         return this.visible ? "<i>Umleitung</i>" : "Umleitung ()";
      } else {
         return !this.mytrain || !this.visible && !this.fertig && (this.zugbelegt == null || this.zugbelegt.size() == 0)
            ? this.sdf.format(this.an + (long)this.verspaetung * 60000L) + " (<b>" + (this.verspaetung > 0 ? "+" : "") + this.verspaetung + "</b>)"
            : this.getVerspaetung();
      }
   }

   public String getVerspaetung() {
      return " " + (this.verspaetung > 0 ? "+" : "") + this.verspaetung + " min";
   }

   public int getVerspaetung_num() {
      return this.verspaetung;
   }

   public ZugColorText getVerspaetungCT() {
      this.verspaetungCT.update();
      return this.verspaetungCT;
   }

   public String getHinweistext() {
      return this.hinweistext;
   }

   public String getReport() {
      return "Z:"
         + this.zid
         + "/Nme:"
         + this.name
         + "/Vis"
         + (this.visible ? "+" : "-")
         + "/MyT"
         + (this.mytrain ? "+" : "-")
         + "/TMPO:"
         + this.ist_tempo
         + "/ÜP"
         + (this.gotüp ? "+" : "-")
         + "/ver"
         + this.verspaetung
         + "/lver"
         + this.lastVerspaetung
         + "/iheat"
         + this.irrelevantHeat
         + "/+heat"
         + this.irrelevantInc
         + "/heat"
         + this.heat;
   }

   public zugPlan getZugDetails() {
      return this.getZugDetails(2);
   }

   private zug.planLineReturn getZugPlanDetails() {
      zugPlanLine ret = new zugPlanLine();
      ret.azid = this.azid;
      ret.flagD = this.flags.hadFlag('D');
      ret.flagA = this.flags.hadFlag('A') && !ret.flagD;
      ret.flagL = this.flags.hadFlag('L');
      ret.flagL_running = this.flags.hasFlag('l');
      ret.flagE = this.flags.hadFlag('E');
      ret.flagK = this.flags.hadFlag('K');
      ret.flagF = this.flags.hadFlag('F');
      ret.flagR = this.flags.hadFlag('R');
      ret.flagW = this.flags.hadFlag('W');
      ret.flagW_running = this.flags.hasFlag('w');
      ret.flagG = this.flags.hadFlag('G');
      ret.zielGleis = this.zielgleis;
      ret.befehlGleis = this.befehl_zielgleis;
      ret.befehlGleisAccepted = this.isGleisänderungTimeout();
      ret.an = this.getAnkunft();
      ret.ab = this.getAbfahrt();
      if (ret.flagW) {
         try {
            ArrayList<String> d = this.flags.paramsOfFlag('W');
            int enr = Integer.parseInt((String)d.get(0));
            if (enr < 0) {
               ret.flagWlokAmEnde = true;
            }

            enr = Integer.parseInt((String)d.get(1));
            if (enr < 0) {
               ret.flagWlokAmEnde = true;
            }
         } catch (Exception var6) {
         }
      }

      zug follow = null;
      if (ret.flagE) {
         zug neuzug = this.my_main.findZug(this.flags.dataOfFlag('E'));
         if (neuzug != null) {
            ret.flagZiel = neuzug.getSpezialName();
            ret.flagZielZid = neuzug.getZID_num();
            follow = neuzug;
         }
      }

      if (this.flags.hasFlag('F')) {
         zug neuzug = this.my_main.findZug(this.flags.dataOfFlag('F'));
         if (neuzug != null) {
            ret.flagZiel = neuzug.getSpezialName();
            ret.flagZielZid = neuzug.getZID_num();
         }
      }

      if (this.flags.hasFlag('K')) {
         zug neuzug = this.my_main.findZug(this.flags.dataOfFlag('K'));
         if (neuzug != null) {
            ret.flagZiel = neuzug.getSpezialName();
            ret.flagZielZid = neuzug.getZID_num();
         }
      }

      String k = this.zielgleis + "/" + this.zid + "/" + this.ab;
      if (flagZug.containsKey(k)) {
         int kzug = flagZug.get(k);
         if (kzug > 0) {
            zug neuzug = this.my_main.findZug(kzug);
            if (neuzug != null) {
               ret.kErwartet = neuzug.getSpezialName();
               ret.kErwartetZid = neuzug.getZID_num();
            }
         }
      }

      ret.hinweistext = this.hinweistext;
      return new zug.planLineReturn(ret, follow);
   }

   private zugPlan getZugDetails(int recursion) {
      zugPlan ret = new zugPlan(this);
      ret.anyDirection = this.anyDirection;
      if (this.vorsignal != null) {
         ret.halt = true;
         ret.haltsignal = this.vorsignal;
      } else if (this.ist_tempo < 0.1 && this.visible && !this.ambahnsteig) {
         ret.halt = true;
      }

      if (tl_langsam.get(this) != null) {
         ret.langsam = true;
      }

      if (this.wartenOK) {
         ret.warten = true;
      }

      if (this.notBremsung) {
         ret.notbremsung = true;
      }

      if (this.userText != null && !this.userText.isEmpty()) {
         ret.userText = this.userText + ", von " + this.userTextSender;
      }

      if (!this.getVon().isEmpty()) {
         ret.von = this.getVon();
         if (this.ein_enr_changed) {
            ret.vonGeaendert = true;
         }
      } else if (this.ein_stw == null && this.aus_stw == null) {
         label111:
         for(zug z : this.my_main.getZugList()) {
            Iterator<zug> it = z.getAllUnseenFahrplanzeilen();

            while(it.hasNext()) {
               zug z2 = (zug)it.next();
               int z2Zid = 0;
               if (z2.flags.hasFlag('F')) {
                  z2Zid = z2.flags.dataOfFlag('F');
               } else if (z2.flags.hasFlag('E')) {
                  z2Zid = z2.flags.dataOfFlag('E');
               }

               if (z2Zid == this.zid) {
                  ret.vorZug = z.getSpezialName();
                  ret.vorZugZid = z.zid;
                  break label111;
               }
            }
         }
      }

      if (!this.getNach().isEmpty()) {
         ret.nach = this.getNach();
      }

      if (this.exitMode && this.leaveAfterÜP && !this.allowRemove) {
         ret.falseAusfahrt = true;
      }

      if (this.ein_stw != null && this.aus_stw != null) {
         ret.umleitungEinfahrt = ret.umleitungAusfahrt = true;
         ret.umleitungText = this.additionalDescriptionRic;
      } else {
         if (this.ein_stw != null) {
            ret.umleitungEinfahrt = true;
         }

         if (this.aus_stw != null) {
            ret.umleitungAusfahrt = true;
         }

         zug.planLineReturn line = this.getZugPlanDetails();
         line.s.azid = this.cur_azid;
         ret.plan.add(line.s);
         zug additionalPlan = line.additionalPlan;
         if (this.unterzuege != null) {
            for(zug zz : this.unterzuege.values()) {
               if (!zz.azid_besucht && zz.azid != this.cur_azid) {
                  line = zz.getZugPlanDetails();
                  ret.plan.add(line.s);
                  if (line.additionalPlan != null) {
                     additionalPlan = line.additionalPlan;
                  }
               }
            }
         }

         if (recursion >= 0 && additionalPlan != null) {
            ret.follower = additionalPlan.getZugDetails(recursion - 1);
         }

         ret.umleitungText = this.additionalDescriptionRic;
      }

      return ret;
   }

   @Deprecated
   public String getDetails() {
      return this.getDetails(2);
   }

   @Deprecated
   private zug.planReturn getPlanDetails(String bgcol) {
      String s = "<tr valign=top bgcolor='#" + bgcol + "'>";
      s = s + "<td id='gleis'>";
      if (this.flags.hadFlag('D')) {
         s = s + "Durchfahrt";
      } else {
         s = s + "Halt";
      }

      s = s + " Gleis&nbsp;<b>" + HTMLEntities.unbreakSpace(HTMLEntities.htmlentities(this.zielgleis)) + "</b>";
      s = s
         + "</td><td id='zeit'>"
         + this.getAnkunft()
         + (!this.flags.hadFlag('E') && !this.flags.hadFlag('K') ? " bis " + this.getAbfahrt() : "")
         + "</td><td id='info'>";
      if (this.befehl_zielgleis != null) {
         s = s + " Gleisänderung nach " + HTMLEntities.unbreakSpace(HTMLEntities.htmlentities(this.befehl_zielgleis)) + "<br>";
      }

      if (this.flags.hadFlag('L')) {
         s = s + " Lok setzt um<br>";
      }

      if (this.flags.hadFlag('W')) {
         String r = "";

         try {
            ArrayList<String> d = this.flags.paramsOfFlag('W');
            int enr = Integer.parseInt((String)d.get(0));
            if (enr < 0) {
               r = ", Lok am Ende";
            }

            enr = Integer.parseInt((String)d.get(1));
            if (enr < 0) {
               r = ", Lok am Ende";
            }
         } catch (Exception var7) {
         }

         s = s + " Lokwechsel" + r + "<br>";
      }

      if (this.flags.hadFlag('R')) {
         s = s + " ändert Fahrtrichtung<br>";
      }

      zug additionalPlan = null;
      if (this.flags.hasFlag('E')) {
         zug neuzug = this.my_main.findZug(this.flags.dataOfFlag('E'));
         if (neuzug != null) {
            s = s + " ändert Zugname in " + HTMLEntities.unbreakSpace(HTMLEntities.htmlentities(neuzug.getSpezialName())) + "<br>";
            additionalPlan = neuzug;
         } else {
            s = s + " ändert Zugname<br>";
         }
      }

      if (this.flags.hasFlag('F')) {
         zug neuzug = this.my_main.findZug(this.flags.dataOfFlag('F'));
         if (neuzug != null) {
            s = s + " flügelt, hinterer Zugteil wird " + HTMLEntities.unbreakSpace(HTMLEntities.htmlentities(neuzug.getSpezialName())) + "<br>";
         } else {
            s = s + " flügelt<br>";
         }
      }

      if (this.flags.hasFlag('K')) {
         zug neuzug = this.my_main.findZug(this.flags.dataOfFlag('K'));
         if (neuzug != null) {
            s = s + " wird an Zug " + HTMLEntities.unbreakSpace(HTMLEntities.htmlentities(neuzug.getSpezialName())) + " gekuppelt<br>";
         } else {
            s = s + " wird an Zug gekuppelt<br>";
         }
      }

      String k = this.zielgleis + "/" + this.zid + "/" + this.ab;
      if (flagZug.containsKey(k)) {
         int kzug = flagZug.get(k);
         if (kzug > 0) {
            zug neuzug = this.my_main.findZug(kzug);
            if (neuzug != null) {
               s = s + " erwartet Kupplung von Zug " + HTMLEntities.unbreakSpace(HTMLEntities.htmlentities(neuzug.getSpezialName())) + "<br>";
            }
         }
      }

      s = s + "</td>";
      if (!this.flags.hadFlag('D') && this.flags.hadFlag('A')) {
         s = s + "</tr><tr bgcolor='#" + bgcol + "'><td colspan=3 id='fullinfo'>(vorzeitige Abfahrt per Befehl)</td>";
      }

      if (this.hinweistext != null && this.hinweistext.length() > 1) {
         s = s
            + "</tr><tr bgcolor='#"
            + bgcol
            + "'><td colspan=3 id='fullinfo'>Zu beachten: "
            + HTMLEntities.unbreakSpace(HTMLEntities.htmlentities(this.hinweistext))
            + "</td>";
      }

      return new zug.planReturn(s + "</tr>", additionalPlan);
   }

   @Deprecated
   private String getDetails(int recursion) {
      String s = "";
      s = s + "<b>Zug " + HTMLEntities.unbreakSpace(HTMLEntities.htmlentities(this.getSpezialName())) + "</b>";
      if (this.vorsignal != null) {
         s = s + " außerplan. Halt vor " + this.vorsignal;
      } else if (this.ist_tempo < 0.1 && this.visible && !this.ambahnsteig) {
         s = s + " außerplan. Halt";
      }

      if (tl_langsam.get(this) != null) {
         s = s + " Langsamfahrt angeordnet";
      }

      if (this.wartenOK) {
         s = s + " Warten angeordnet";
      }

      if (this.notBremsung) {
         s = s + " Notbremsung";
      }

      s = s + "<br>";
      if (!this.getVon().isEmpty()) {
         s = s + "von " + this.getVon() + " ";
      }

      if (!this.getNach().isEmpty()) {
         s = s + "nach " + this.getNach();
         if (this.ein_enr_changed) {
            s = s + " (außerplan. geändt.)";
         }
      }

      if (this.exitMode && this.leaveAfterÜP && !this.allowRemove) {
         if (!s.endsWith("<br>")) {
            s = s + "<br>";
         }

         s = s + "Achtung: Zug wurde über unplanmäßige Ausfahrt umgeleitet.<br>";
      }

      if (this.ein_stw != null && this.aus_stw != null) {
         s = s + "<br><b>Umgeleiteter Zug!</b>";
         s = s + this.additionalDescription;
      } else {
         if (this.ein_stw != null || this.aus_stw != null) {
            s = s + "<br><b>Umgeleiteter Zug!</b>";
         }

         s = s + "<table border=0 cellspacing=0 cellpadding=1 width='100%'>";
         String[] cols = new String[]{"ffffee", "ffffff"};
         zug.planReturn details = this.getPlanDetails(cols[0]);
         zug additionalPlan = details.additionalPlan;
         s = s + details.s;
         if (this.unterzuege != null) {
            int c = 0;

            for(zug zz : this.unterzuege.values()) {
               if (!zz.azid_besucht && zz.azid != this.cur_azid) {
                  if (!s.endsWith("<br>")) {
                     s = s + "<br>";
                  }

                  c = 1 - c;
                  details = zz.getPlanDetails(cols[c]);
                  if (details.additionalPlan != null) {
                     additionalPlan = details.additionalPlan;
                  }

                  s = s + details.s;
               }
            }
         }

         s = s + "</table>";
         if (recursion >= 0 && additionalPlan != null) {
            s = s + "<br><hr><i>Folgeplan</i>: " + additionalPlan.getDetails(recursion - 1);
         }

         s = s + this.additionalDescription;
      }

      return s;
   }

   public int updateEinAus(String ein_stw, String aus_stw) {
      int ret = 0;
      if (!ein_stw.isEmpty()) {
         this.ein_stw = ein_stw;
         this.ein_enr_changed = false;
         ret |= 1;
      }

      if (!aus_stw.isEmpty()) {
         this.aus_stw = aus_stw;
         this.aus_enr = 0;
         ret |= 2;
      }

      this.bstgRedirectSpecial = this.ein_stw != null && this.aus_stw == null;
      this.updateData();
      this.my_main.updateZug(this);
      return ret;
   }

   public void enableBstgRedirectSpecial() {
      this.bstgRedirectSpecial = true;
   }

   public void setGleis(String g) {
      this.befehl_zielgleis = g;
      this.befehl_zielgleis_zeit = this.my_main.getSimutime();
      this.my_main.updateZug(this);
   }

   public void setGleis(String g, int unterzug) {
      zug z = this.unterzug(unterzug);
      if (z == null) {
         z = this;
      }

      z.befehl_zielgleis = g;
      z.befehl_zielgleis_zeit = this.my_main.getSimutime();
      this.my_main.updateZug(this);
   }

   public void setGleisByAZid(String g, int azid) {
      zug z = this.unterzugByAZid(azid);
      if (z != null) {
         z.befehl_zielgleis = g;
         z.befehl_zielgleis_zeit = this.my_main.getSimutime();
         this.my_main.updateZug(this);
      }
   }

   public boolean isGleisänderungTimeout() {
      if (this.befehl_zielgleis != null) {
         long befehlAhead = (this.my_main.getSimutime() - this.befehl_zielgleis_zeit) / 60000L;
         return befehlAhead > (long)(this.my_main.isRealistic() ? 10 : 3)
            ? true
            : this.my_main.getBahnsteige().isConnectedBahnsteigOf(this.zielgleis, this.befehl_zielgleis);
      } else {
         return true;
      }
   }

   public flagdata getFlags() {
      return this.flags;
   }

   public String getMarkColor() {
      return this.markColor + "";
   }

   public void setMarkColor(String m) {
      try {
         this.markColor = Integer.parseInt(m);
         this.verspaetungCT.setBGColor(this.markColor);
         this.my_main.refreshZug();
      } catch (NumberFormatException var3) {
      }
   }

   public String getMarkNum() {
      return this.markNum;
   }

   public void setMarkNum(String cmd) {
      this.markNum = cmd;
      this.my_main.refreshZug();
   }

   public void setWeiterfahren() {
      this.weiterfahren = true;
      tl_sichtfahrt.add(this);
   }

   public void setAbfahrtbefehl() {
      this.abfahrtbefehl = true;
   }

   public void setNotbremsung() {
      this.notBremsung = true;
   }

   public void meldePosition(zugPositionListener p) {
      this.positionMelden = true;
      this.positionListener = p;
   }

   public void richtungWechseln(zug.RICHTUNGWECHELN r) {
      if (!this.runningUmdrehen && !this.flags.hasFlag('l')) {
         this.umdrehen = r;
      }
   }

   boolean richtungUmkehren() {
      if (this.vorsignal == null && !this.sichtstopp && !this.ambahnsteig) {
         return false;
      } else {
         if (this.zugbelegt.size() >= 2) {
            this.pos_gl = (gleis)this.zugbelegt.getFirst();
            this.before_gl = (gleis)this.zugbelegt.get(1);
         } else {
            gleis s = this.pos_gl;
            this.pos_gl = this.before_gl;
            this.before_gl = s;
         }

         LinkedList newzugbelegt = new LinkedList();

         try {
            while(this.zugbelegt.size() > 0) {
               newzugbelegt.addFirst(this.zugbelegt.removeFirst());
            }
         } catch (NoSuchElementException var3) {
         }

         this.zugbelegt = newzugbelegt;
         tl_sichtfahrt.add(this);
         this.triggerDisplayBar(displayBar.ZUGTRIGGER.RICHTUNG);
         return true;
      }
   }

   public void setMaxTempo(int t) {
      this.removeLimit(tl_langsam.class);
      if (t == 1) {
         tl_langsam.add(this, (double)(this.variables.soll_tempo(this.soll_tempo, this.zielgleis) / 2));
      }
   }

   public void warten() {
      if (this.visible) {
         this.wartenOK = true;
      }
   }

   public boolean stehtDummRum() {
      return !this.ambahnsteig && this.visible && !this.fertig && this.rottime > 0L && this.anrufzaehler > 1 || !this.visible && this.eingangbelegt;
   }

   public boolean allowesÄnderung() {
      return this.variables.var_allowesÄnderung(this.zielgleis);
   }

   public boolean isZugfahrt() {
      return this.getLimit(tl_sichtfahrt.class) == null && this.getLimit(tl_sh1.class) == null;
   }

   public void leaveAfterÜP(boolean _allowRemove) {
      this.leaveAfterÜP = true;
      this.allowRemove = _allowRemove;
      if (debugMode != null) {
         debugMode.writeln("zug (" + this.getName() + ")", "leave after ÜP");
      }
   }

   public boolean gotByÜP(gleis ein_gleis) {
      if (!this.visible && !this.fertig) {
         int nenr = ein_gleis.getENR();
         if (this.ein_stw == null) {
            this.ein_enr_changed |= nenr != this.ein_enr;
         }

         this.ein_enr = nenr;
         this.von.update();
         if (!this.gotüp || this.mytrain) {
            this.mytrainÜPcount = 0;
         } else if (this.mytrain) {
            ++this.mytrainÜPcount;
         }

         if (this.mytrainÜPcount > 10) {
            System.out.println("zug (" + this.getName() + ") Übergabefehler");
         }

         this.gotüp = true;
         this.namefarbe = 10;
         this.namect.setBGColor(this.namefarbe);
         if (debugMode != null) {
            debugMode.writeln("zug (" + this.getName() + ")", "got by ÜP");
         }

         return true;
      } else {
         return !this.fertig || this.gotüp;
      }
   }

   public void setExternalÜpReport() {
      this.externalÜpReport = true;
   }

   public String getUserText() {
      return this.userText;
   }

   public String getUserTextSender() {
      return this.userTextSender;
   }

   public void setUserText(String t, String sender) {
      this.userText = t;
      this.userTextSender = sender;
   }

   void reportPosition(int start_enr, int stop_enr) {
      this.my_main.reportZugPosition(this.zid, start_enr, stop_enr);
   }

   public zug.gleisData getGleisDataOfGleis(String bstg) {
      for(int i = 0; i <= (this.unterzuege == null ? 0 : this.unterzuege.size()); ++i) {
         zug z = this.unterzug(i);
         if (z != null) {
            String g = z.befehl_zielgleis;
            if (bstg.equals(z.zielgleis) || g != null && bstg.equals(g)) {
               zug.gleisData ret = new zug.gleisData();
               ret.an = z.an;
               ret.ab = z.ab;
               ret.gleis = z.zielgleis;
               ret.sollgleis = g != null ? g : z.zielgleis;
               ret.flags = z.flags;
               ret.befehlgleisNeu = g != null && bstg.equals(g);
               ret.befehlgleisAlt = g != null && !bstg.equals(g);
               return ret;
            }
         }
      }

      return null;
   }

   public void setAdditionalDescription(redirektInfoContainer ric, String s) {
      this.additionalDescription = "<p>" + s;
      this.additionalDescriptionRic = ric;
   }

   public Vector getStructInfo() {
      Vector v = new Vector();
      v.addElement("Zug");
      v.addElement(this.name + "/" + this.zid);
      v.addElement(this);
      return v;
   }

   @Override
   public String getStructName() {
      return this.name + "/" + this.zid;
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("zid");
      v.addElement(this.zid + "");
      v.addElement("azid");
      v.addElement(this.azid + "");
      v.addElement("name");
      v.addElement(this.name);
      v.addElement("zielgleis");
      v.addElement(this.zielgleis);
      v.addElement("befehl_zielgleis");
      v.addElement(this.befehl_zielgleis);
      v.addElement("gleisänderungtimeout");
      v.addElement(this.isGleisänderungTimeout() + "");
      v.addElement("An");
      v.addElement(this.getAnkunft());
      v.addElement("Ab");
      v.addElement(this.getAbfahrt());
      v.addElement("Flags");
      v.addElement(this.flags);
      v.addElement("hinweistext");
      v.addElement(this.hinweistext);
      v.addElement("ein_enr");
      v.addElement(this.ein_enr + "");
      if (this.ein_stw != null) {
         v.addElement("ein_stw");
         v.addElement(this.ein_stw + "");
      }

      v.addElement("aus_enr");
      v.addElement(this.aus_enr + "");
      if (this.aus_stw != null) {
         v.addElement("aus_stw");
         v.addElement(this.aus_stw + "");
      }

      v.addElement("soll_tempo");
      v.addElement(this.soll_tempo + "");
      v.addElement("ist_tempo");
      v.addElement(this.ist_tempo + "");
      v.addElement("haltabstand");
      v.addElement(this.haltabstand + "");
      v.addElement("lasthaltabstand");
      v.addElement(this.lasthaltabstand + "");
      v.addElement("haltabstandgesehen");
      v.addElement(this.haltabstandgesehen + "");
      v.addElement("haltabstandcnt");
      v.addElement(this.haltabstandcnt + "");
      v.addElement("haltabstandanrufcnt");
      v.addElement(this.haltabstandanrufcnt + "");
      v.addElement("lastBahnsteig");
      if (this.lastBahnsteig != null) {
         v.addElement(this.lastBahnsteig.getSWWert());
      } else {
         v.addElement("-");
      }

      v.addElement("calc_tempo()");
      v.addElement(this.calc_tempo() + "");
      v.addElement("sichtfahrt");
      v.addElement((tl_sichtfahrt.get(this) != null) + "");
      v.addElement("sichtstopp");
      v.addElement(this.sichtstopp + "");
      v.addElement("firstSignalPassed");
      v.addElement(this.firstSignalPassed + "");
      v.addElement("fromRightExtra");
      v.addElement(this.fromRightExtra + "");
      v.addElement("last_calc_tempo");
      v.addElement(this.last_calc_tempo + "");
      v.addElement("tempo_pos");
      v.addElement(this.tempo_pos + "");
      v.addElement("laenge");
      v.addElement(this.laenge + "");
      v.addElement("istlaenge");
      v.addElement(this.zugbelegt.size() + "");
      v.addElement("lastmasstab");
      v.addElement(this.lastmasstab + "");
      v.addElement("calcLaenge()");
      v.addElement(this.calcLaenge(this.lastmasstab) + "");
      if (this.next_gl != null) {
         v.addElement("x/y next");
         v.addElement(this.next_gl.getCol() + "/" + this.next_gl.getRow());
      }

      if (this.pos_gl != null) {
         v.addElement("x/y pos");
         v.addElement(this.pos_gl.getCol() + "/" + this.pos_gl.getRow());
      }

      if (this.before_gl != null) {
         v.addElement("x/y before");
         v.addElement(this.before_gl.getCol() + "/" + this.before_gl.getRow());
      }

      v.addElement("chain");
      if (!this.lastChainVisits.isEmpty()) {
         String vs = "";

         for(String c : this.lastChainVisits) {
            vs = vs + ">" + c;
         }

         v.addElement(vs);
      } else {
         v.addElement("-");
      }

      v.addElement("additionalDescription");
      v.addElement(this.additionalDescription);
      v.addElement("parken");
      v.addElement(this.parken + "");
      v.addElement("verspaetung");
      v.addElement(this.verspaetung + "");
      v.addElement("lastVerspaetung");
      v.addElement(this.lastVerspaetung + "");
      v.addElement("mytrain");
      v.addElement(this.mytrain + "");
      v.addElement("cur_azid");
      v.addElement(this.cur_azid + "");
      v.addElement("azid_besucht");
      v.addElement(this.azid_besucht + "");
      v.addElement("vorsignal");
      v.addElement(this.vorsignal);
      v.addElement("rottime");
      v.addElement(this.mdf.format(this.rottime) + "/" + this.rottime);
      v.addElement("anruftime");
      v.addElement(this.mdf.format(this.anruftime) + "/" + this.anruftime);
      v.addElement("weiterfahren");
      v.addElement(this.weiterfahren + "");
      v.addElement("abfahrtbefehl");
      v.addElement(this.abfahrtbefehl + "");
      v.addElement("notBremsung");
      v.addElement(this.notBremsung + "");
      v.addElement("wartenOK");
      v.addElement(this.wartenOK + "");
      v.addElement("anrufzaehler");
      v.addElement(this.anrufzaehler + "");
      v.addElement("gestopptgleis");
      v.addElement(this.gestopptgleis);
      v.addElement("ambahnsteig");
      v.addElement(this.ambahnsteig + "");
      v.addElement("gleiswarok");
      v.addElement(this.gleiswarok + "");
      v.addElement("warankunft");
      v.addElement(this.mdf.format(this.rottime) + "/" + this.warankunft);
      v.addElement("ignoriereBahnsteig");
      v.addElement(this.ignoriereBahnsteig + "");
      v.addElement("schongefahren");
      v.addElement(this.schongefahren + "");
      v.addElement("waitLWdone");
      v.addElement(this.waitLWdone + "");
      v.addElement("callme");
      v.addElement((this.callme != null) + "");
      v.addElement("fertig");
      v.addElement(this.fertig + "");
      v.addElement("exitMode");
      v.addElement(this.exitMode + "");
      v.addElement("visible");
      v.addElement(this.visible + "");
      v.addElement("gotüp");
      v.addElement(this.gotüp + "");
      v.addElement("leaveAfterÜP");
      v.addElement(this.leaveAfterÜP + "");
      v.addElement("externalÜpReport");
      v.addElement(this.externalÜpReport + "");
      v.addElement("heat");
      v.addElement(this.heat + "");
      v.addElement("irrelevantHeat");
      v.addElement(this.irrelevantHeat + "");
      v.addElement("irrelevantInc");
      v.addElement(this.irrelevantInc + "");
      v.addElement("allowRemove");
      v.addElement(this.allowRemove + "");
      v.addElement("ready2go");
      v.addElement(this.ready2go + "");
      v.addElement("wflagDelayed");
      v.addElement(this.wflagDelayed + "");
      v.addElement("c_richtigbahnsteig");
      v.addElement(this.c_richtigbahnsteig + "");
      v.addElement("c_falschbahnsteig");
      v.addElement(this.c_falschbahnsteig + "");
      v.addElement("c_geaendertbahnsteig");
      v.addElement(this.c_geaendertbahnsteig + "");
      v.addElement("genommeneausfahrt");
      v.addElement(this.genommeneausfahrt + "");
      v.addElement("b_richtigeausfahrt");
      v.addElement(this.b_richtigeausfahrt + "");
      v.addElement("userText");
      v.addElement(this.userText);
      v.addElement("userTextSender");
      v.addElement(this.userTextSender);
      int i = 0;
      TimeFormat tf = TimeFormat.getInstance(TimeFormat.STYLE.HMS);

      for(zug.wflagData wd : this.wflagList) {
         v.addElement("wflagData " + ++i + " flags");
         v.addElement(wd.flags.toString());
         v.addElement("wflagData " + i + " zielgleis");
         v.addElement(wd.zielgleis);
         v.addElement("wflagData " + i + " an");
         v.addElement(tf.format(wd.an));
      }

      v.addAll(this.variables.getStructure());
      if (this.unterzuege != null) {
         Iterator<zug> it = this.unterzuege.values().iterator();
         i = 0;

         while(it.hasNext()) {
            ++i;
            zug zz = (zug)it.next();
            v.addElement("");
            v.addElement("");
            v.addElement("unterzug " + i);
            v.addElement("");
            v.addElement("azid");
            v.addElement(zz.azid + "");
            v.addElement("azid_besucht");
            v.addElement(zz.azid_besucht + "");
            v.addElement("zielgleis");
            v.addElement(zz.zielgleis);
            v.addElement("Flags");
            v.addElement(zz.flags);
            v.addElement("hinweistext");
            v.addElement(zz.hinweistext);
            v.addElement("An");
            v.addElement(zz.getAnkunft());
            v.addElement("Ab");
            v.addElement(zz.getAbfahrt());
         }
      }

      return v;
   }

   public static void clear() {
      üpstore.clear();
      killEzug.clear();
      flagZug.clear();
   }

   public int compareTo(Object o) {
      if (o instanceof zug) {
         zug z = (zug)o;
         int r;
         if (this.an == z.an) {
            if (this.ab == z.ab) {
               r = this.name.compareToIgnoreCase(z.name);
               if (r == 0) {
                  r = this.zid - z.zid;
               }
            } else {
               r = this.ab > z.ab ? 1 : -1;
            }
         } else {
            r = this.an > z.an ? 1 : -1;
         }

         return r;
      } else {
         return 1;
      }
   }

   void triggerDisplayBar(displayBar.ZUGTRIGGER t) {
      this.glbModel.getDisplayBar().zug(this, t);
   }

   void kuppeln(zug z) {
      this.waitLWdone = true;
      this.waitLW = false;
   }

   private void prepareWflag(flagdata flags, String zielgleis, long an) {
      if (flags.hasFlag('W')) {
         this.wflagList.add(new zug.wflagData(an, zielgleis, new flagdata(flags)));
      }
   }

   public static enum RICHTUNGWECHELN {
      NONE,
      DAUERHAFT,
      ZURUECKSETZEN,
      LOK_UMSETZEN;
   }

   public static class emitData {
      public int länge = 3;
      public int soll_tempo = 5;
      public int ein_enr = 0;
      public int aus_enr = 0;
      public String ein_stw = null;
      public String aus_stw = null;
      public String zielgleis = "";
      public flagdata flags = null;
      public int aufenthalt = 0;
      public String name = null;
      public int verspaetung = 0;
      public boolean mytrain = true;
      public boolean lastStopDone = false;

      public emitData() {
         super();
      }
   }

   public class gleisData {
      public long an;
      public long ab;
      public String gleis;
      public String sollgleis;
      public flagdata flags;
      public boolean befehlgleisNeu = false;
      public boolean befehlgleisAlt = false;

      public gleisData() {
         super();
      }
   }

   private class planLineReturn {
      zugPlanLine s;
      zug additionalPlan;

      planLineReturn(zugPlanLine s, zug additionalPlan) {
         super();
         this.s = s;
         this.additionalPlan = additionalPlan;
      }
   }

   @Deprecated
   private class planReturn {
      String s;
      zug additionalPlan;

      planReturn(String s, zug additionalPlan) {
         super();
         this.s = s;
         this.additionalPlan = additionalPlan;
      }
   }

   static class wflagData {
      long an;
      String zielgleis;
      flagdata flags;

      wflagData(long an, String zielgleis, flagdata flags) {
         super();
         this.zielgleis = zielgleis;
         this.an = an - 900000L;
         this.flags = flags;
      }
   }
}
