package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.gleismsg;
import js.java.isolate.sim.gleis.colorSystem.colorStruct;
import js.java.isolate.sim.gleis.colorSystem.nullColor;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleis.gleisElements.gleisHelper;
import js.java.isolate.sim.gleisbild.PaintSaveInterface;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.gleisbild.gleisbildViewPanel;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasseSelection;
import js.java.isolate.sim.zug.zug;
import js.java.tools.JavaVersion;
import js.java.tools.SystemUtils;
import js.java.tools.gui.GraphicTools;
import org.xml.sax.Attributes;

public class gleis extends trigger implements gleisElements, Comparable {
   static FATwriter debugMode = null;
   final GleisAdapter theapplet;
   gleisbildModel glbModel;
   element telement = ELEMENT_LEER;
   gleisElements.RICHTUNG richtung = gleisElements.RICHTUNG.right;
   int enr = 0;
   String swwert = "";
   int masstab = 0;
   private int count;
   fluentData fdata = null;
   @Deprecated
   boolean extrastatus = false;
   gleis verbundgleis = null;
   volatile gleisGroup gruppe = new gleisGroupNull();
   boolean firstverbund = false;
   int ein_enr = 0;
   boolean signalRfStart = false;
   boolean signalRfOnlyStop = true;
   boolean signalRfOnlyStart = true;
   boolean anyStoppingFS = false;
   Boolean kopfsignaldetect = null;
   Boolean hauptZwergSignaldetect = null;
   public static boolean blinkon = false;
   public static boolean blinkon_slow = false;
   public static boolean blinkon_3er = false;
   public static int blinkon_3erCC = 0;
   public static boolean michNervenBüs = false;
   int blinkcc = 0;
   int highlighted = 0;
   int mycol;
   int myrow;
   private static volatile int currentN = -1;
   HashMap<String, String> decorCache = new HashMap();
   private final gleis.nachbarMgnt nachbar = new gleis.nachbarMgnt();
   autoFShandling autoFW = new autoFShandling(this);
   boolean modus_masstab = false;
   boolean hideDisplay = false;
   public static boolean createName = false;
   int element_enr = 0;
   public static int element_enr_counter = 0;
   gleis_extend gleisExtend = null;
   private boolean FSSTART_TEST = false;
   private boolean FSEND_TEST = false;
   private boolean AUTOFW_TEST = false;
   private boolean FREELED_TEST = false;
   decorItem gleisdecor = null;
   private static long gleisHashCnt = 0L;
   private final long gleisHash = ++gleisHashCnt;
   private boolean guiwork = false;
   public static colorStruct colors = new nullColor();
   protected static final int LOCKSIZE = 3;
   @Deprecated
   Rectangle lastelemwidth = null;
   private static Font lcdFontBase = null;
   private static HashMap<Integer, Font> lcdFonts = new HashMap();
   static final int SMOOTH_GLEIS = 1;
   static final int SMOOTH_EDITOR = 2;
   static final int SMOOTH_OVAL = 3;
   static final int SMOOTH_SIGNAL = 4;
   static final int SMOOTH_KNOB = 5;
   static final int SMOOTH_TEXT = 6;
   private static int allowsmooth = 0;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public static void switchNachbar() {
      if (currentN != -1) {
         currentN = 1 - currentN;
      }
   }

   public static void prepareNachbar() {
      currentN = -1;
   }

   public static void startNachbar() {
      if (currentN == -1) {
         currentN = 0;
      }
   }

   public gleis(GleisAdapter _theapplet, gleisbildModel _my_gleisbild) {
      this.theapplet = _theapplet;
      this.glbModel = _my_gleisbild;
      this.gleisExtend = new gleis_extend();
      this.resetN();
      this.setTypElementDecor();
   }

   public gleis(GleisAdapter _theapplet, gleisbildModel _my_gleisbild, gleis gl) {
      this.theapplet = _theapplet;
      this.glbModel = _my_gleisbild;
      this.gleisExtend = new gleis_extend();
      this.resetN();
      this.telement = gl.telement;
      this.richtung = gl.richtung;
      this.enr = gl.enr;
      this.swwert = gl.swwert;
      this.masstab = gl.masstab;
      this.gleisExtend = gl.gleisExtend.clone();
      this.setTypElementDecor();
   }

   public void init() {
      this.telement = ELEMENT_LEER;
      this.richtung = gleisElements.RICHTUNG.right;
      this.enr = 0;
      this.swwert = "";
      this.masstab = 0;
      this.signalRfStart = false;
      this.signalRfOnlyStop = true;
      this.signalRfOnlyStart = true;
      this.kopfsignaldetect = null;
      this.anyStoppingFS = false;
      this.autoFW.init();
      this.setTypElementDecor();
      this.fdata.init();
      this.highlighted = 0;
   }

   public void init(element _element, gleisElements.RICHTUNG _richtung) {
      this.telement = _element;
      this.richtung = _richtung;
      this.enr = 0;
      this.swwert = "";
      this.masstab = 0;
      this.signalRfStart = false;
      this.signalRfOnlyStop = true;
      this.signalRfOnlyStart = true;
      this.kopfsignaldetect = null;
      this.anyStoppingFS = false;
      this.setTypElementDecor();
      this.fdata.init();
   }

   public void init(int _typ, int _element, String _richtung, int _enr, String _swwert, int _masstab) {
      this.init(gleisHelper.findElement(_typ, _element), gleisHelper.findRichtung(_richtung), _enr, _swwert, _masstab);
   }

   public void init(element _element, gleisElements.RICHTUNG _richtung, int _enr, String _swwert, int _masstab) {
      this.telement = _element;
      this.richtung = _richtung;
      this.enr = _enr;
      this.swwert = _swwert;
      this.masstab = _masstab;
      this.signalRfStart = false;
      this.signalRfOnlyStop = true;
      this.signalRfOnlyStart = true;
      this.kopfsignaldetect = null;
      this.anyStoppingFS = false;
      this.setTypElementDecor();
      this.fdata.init();
      if (!this.typShouldHaveSWwert() && !this.typRequiresSWwert() && !this.typAllowesSWwertedit()) {
         this.swwert = "";
      }
   }

   public void init(Attributes attrs, String pcdata) throws Exception {
      this.init(
         Integer.parseInt(attrs.getValue("typ").trim()),
         Integer.parseInt(attrs.getValue("element").trim()),
         attrs.getValue("richtung").trim(),
         Integer.parseInt(attrs.getValue("enr").trim()),
         attrs.getValue("swwert").trim(),
         Integer.parseInt(attrs.getValue("masstab").trim())
      );
      if (pcdata != null && pcdata.trim().length() > 0) {
         try {
            this.gleisExtend = gleis_extend.createFromBase64(pcdata.trim());
         } catch (Exception var4) {
            System.out.println(var4.toString() + ": '" + pcdata.trim() + "'");
            Logger.getLogger("stslogger").log(Level.SEVERE, "Gleis Extend:" + var4.toString() + ": '" + pcdata.trim() + "'", var4);
         }
      }

      if (this.gleisExtend == null) {
         this.gleisExtend = new gleis_extend();
      }
   }

   public int compareTo(Object obj) {
      if (obj == null) {
         return -1;
      } else {
         return ((gleis)obj).enr - this.enr != 0 ? ((gleis)obj).enr - this.enr : -this.swwert.compareTo(((gleis)obj).swwert);
      }
   }

   public int compareToGleis(gleis g2) {
      int r = this.myrow - g2.myrow;
      if (r == 0) {
         r = this.mycol - g2.mycol;
      }

      return r;
   }

   public boolean sameGleis(gleis g) {
      return g != null && this.gleisHash == g.gleisHash;
   }

   public boolean equals(Object o) {
      try {
         gleis g = (gleis)o;
         return this.sameGleis(g);
      } catch (Exception var3) {
         return false;
      }
   }

   public int hashCode() {
      return (int)this.gleisHash;
   }

   public void hideDisplay() {
      this.hideDisplay = true;
   }

   public void resetExtend() {
      this.gleisExtend = new gleis_extend();
   }

   public String toString() {
      return this.telement.toString()
         + ","
         + this.richtung
         + ","
         + this.enr
         + ","
         + this.swwert.replace(',', ';')
         + ","
         + this.masstab
         + ","
         + this.toString2();
   }

   public String getSaveString() {
      if (!this.typShouldHaveSWwert() && !this.typRequiresSWwert() && !this.typAllowesSWwertedit()) {
         this.swwert = "";
      }

      return this.telement.toString()
         + ","
         + this.richtung
         + ","
         + this.enr
         + ","
         + this.swwert.replace(',', ';')
         + ","
         + this.masstab
         + ","
         + this.toString2();
   }

   private String toString2() {
      return gleis_extend.toBase64(this.gleisExtend);
   }

   public gleisbildModel getParentGleisbild() {
      return this.glbModel;
   }

   boolean call(fahrstrasse f, boolean fsstart) {
      return this.call(eventGenerator.T_GLEIS_FSSPEICHER, new gleismsg(this, f, fsstart));
   }

   boolean call(gleisElements.Stellungen st, fahrstrasse f) {
      return this.call(eventGenerator.T_GLEIS_STELLUNG, new gleismsg(this, st, f));
   }

   boolean call(int s, zug z) {
      return this.call(eventGenerator.T_GLEIS_STATUS, new gleismsg(this, s, z));
   }

   @Deprecated
   public void setStellwerkModus(int m) {
   }

   public void setMasstabModus(boolean m) {
      this.modus_masstab = m;
   }

   public void readGUI() {
      if (!this.guiwork) {
         this.guiwork = true;
         gleis.gleisUIcom gu = new gleis.gleisUIcom(this);
         this.theapplet.readUI(gu);
         if (gu.changed) {
            int typ = this.glbModel.changeC(gu.element.getTyp(), this.telement.getTyp(), 2);
            if (typ > 0) {
               this.richtung = this.glbModel.changeC(gu.richtung, this.richtung, 1);
               this.glbModel.changeC(gu.element.getElement(), this.telement.getElement(), 1);
               this.telement = gu.element;
               this.enr = this.glbModel.changeC(gu.enr, this.enr, 5);

               try {
                  this.swwert = this.glbModel.changeC(gu.swwert.trim(), this.swwert, 10);
                  if (this.swwert.length() > 30) {
                     this.swwert = this.swwert.substring(0, 30);
                  }
               } catch (NullPointerException var4) {
                  this.swwert = "";
               }
            } else {
               this.telement = ELEMENT_LEER;
               this.enr = 0;
               this.swwert = "";
            }
         }

         this.setTypElementDecor();
         this.guiwork = false;
      }
   }

   public void setGUI() {
      if (!this.guiwork) {
         this.guiwork = true;
         this.theapplet.setUI(new gleis.gleisUIcom(this));
         this.guiwork = false;
      }
   }

   public boolean requiresENR() {
      return this.enr == 0 && this.typRequiresENR();
   }

   public boolean typRequiresENR() {
      return typRequiresENR(this.telement);
   }

   public static boolean typRequiresENR(element element) {
      return decor.getDecor().typRequiresENR(element);
   }

   public boolean typAllowesENRedit() {
      return typAllowesENRedit(this.telement);
   }

   public static boolean typAllowesENRedit(element element) {
      return decor.getDecor().typAllowesENRedit(element);
   }

   public boolean typAllowesSWwertedit() {
      return typAllowesSWwertedit(this.telement);
   }

   public static boolean typAllowesSWwertedit(element element) {
      return decor.getDecor().typAllowesSWwertedit(element);
   }

   public boolean typRequiresSWwert() {
      return typRequiresSWwert(this.telement);
   }

   public static boolean typRequiresSWwert(element element) {
      return decor.getDecor().typRequiresSWwert(element);
   }

   public boolean typHasElementName() {
      return decor.getDecor().typHasElementName(this.telement);
   }

   public boolean typShouldHaveSWwert() {
      return typShouldHaveSWwert(this.telement);
   }

   public static boolean typShouldHaveSWwert(element element) {
      return decor.getDecor().typShouldHaveSWwert(element);
   }

   @Deprecated
   public boolean typAllowsVerbund() {
      return this.gleisdecor.allowsVerbund;
   }

   @Deprecated
   public boolean needExtraRight() {
      return decor.getDecor().needExtraRight(this.telement);
   }

   public boolean typKeepEnr() {
      return this.gleisdecor.keepEnr;
   }

   public boolean typHasEnrPartner() {
      return this.gleisdecor.hasEnrPartner;
   }

   public List<element> typPartner() {
      return this.gleisdecor.enrPartner;
   }

   public boolean isEmpty() {
      return this.telement == ELEMENT_LEER;
   }

   public element getElement() {
      return this.telement;
   }

   public void setElement(element e) {
      if (e.getTyp() == this.telement.getTyp()) {
         this.glbModel.changeC(e.getElement(), this.telement.getElement(), 1);
         this.telement = e;
      }
   }

   public void setENR(int e) {
      this.enr = e;
   }

   public int getENR() {
      return this.enr;
   }

   public void setSWWert(String s) {
      this.swwert = s;
   }

   public String getSWWert() {
      return this.swwert;
   }

   public String getSWWert_special() {
      return this.swwert != null & this.swwert.indexOf(37) >= 0 ? this.swwert.substring(0, this.swwert.indexOf(37)) : this.swwert;
   }

   public fluentData getFluentData() {
      return this.fdata;
   }

   public void setMasstab(int m) {
      this.masstab = m;
   }

   public int getMasstab() {
      return this.masstab;
   }

   public boolean isDisplayTrigger() {
      return this.gleisdecor.displayTrigger;
   }

   public boolean isDisplayTriggerSelectable() {
      return this.gleisdecor.userDisplayTrigger;
   }

   public boolean isDisplayFStrigger() {
      return this.gleisdecor.displayFStrigger;
   }

   public void setHightlight(boolean b) {
      this.highlighted = b ? 20 : 0;
      this.tjm_add();
   }

   public int getCol() {
      return this.mycol;
   }

   public int getRow() {
      return this.myrow;
   }

   public void setXY(int col, int row) {
      this.mycol = col;
      this.myrow = row;
   }

   public gleis_extend getGleisExtend() {
      return this.gleisExtend;
   }

   @Deprecated
   public void setExtendFarbe(String f) {
      this.gleisExtend.setFarbe(f);
   }

   @Deprecated
   public String getExtendFarbe() {
      return this.gleisExtend.getFarbe();
   }

   @Deprecated
   public void setExtendValue(gleis.EXTENDS name, String value) {
      this.gleisExtend.setExtendValue(name, value);
   }

   @Deprecated
   public String getExtendValue(gleis.EXTENDS name) {
      return this.gleisExtend.getExtendValue(name);
   }

   public static void setColor(colorStruct c) {
      colors = c;
   }

   public colorStruct getColor() {
      return this.glbModel.getColor();
   }

   public void clear() {
      this.autoFW.init();
      this.fdata.init();
      this.hideDisplay = false;
      this.FSSTART_TEST = false;
      this.FSEND_TEST = false;
      this.AUTOFW_TEST = false;
      this.FREELED_TEST = false;
      this.signalRfStart = false;
      this.signalRfOnlyStop = true;
      this.signalRfOnlyStart = true;
      this.kopfsignaldetect = null;
      this.hauptZwergSignaldetect = null;
      this.anyStoppingFS = false;
      this.gruppe = new gleisGroupNull();
      this.highlighted = 0;
   }

   public void resetN() {
      this.nachbar.clear();
      this.extrastatus = false;
      this.verbundgleis = null;
      this.firstverbund = false;
   }

   public void close() {
      this.nachbar.close();
      this.gleisdecor = null;
      this.gruppe = null;
   }

   private void addN(gleis g, int sdp, int ddp) {
      if (!this.nachbar.contains(g)) {
         this.nachbar.add(g, sdp, ddp);
      }
   }

   public void reset() {
      if (ELEMENT_LEER.matches(this.telement)) {
         this.count = 0;
      } else if (this.telement == ELEMENT_KREUZUNGBRUECKE) {
         this.count = 4;
      } else if (this.telement == ELEMENT_KREUZUNG) {
         this.count = 4;
      } else if (this.telement != ELEMENT_WEICHEOBEN && this.telement != ELEMENT_WEICHEUNTEN) {
         this.count = 2;
      } else {
         this.count = 3;
      }
   }

   @Deprecated
   public void clear(gleisbildViewPanel panel, Graphics g, int xscal, int yscal) {
      if (panel.isEditorView()) {
         g.setColor(colors.col_stellwerk_raster);
         g.drawLine(0, 0, xscal, 0);
         g.drawLine(0, 0, 0, yscal);
         g.drawLine(0, yscal, xscal, yscal);
         g.drawLine(xscal, 0, xscal, yscal);
      }
   }

   public void aktiv(PaintSaveInterface panel, Graphics2D g, int xscal, int yscal) {
      try {
         g.setColor(colors.col_aktiv);
         g.fillRect(0, 0, xscal, yscal);
      } catch (NullPointerException var6) {
      }
   }

   public void aktiv2(PaintSaveInterface panel, Graphics2D g2, int xscal, int yscal, int level) {
      try {
         g2.setColor(colors.col_aktiv2[level % colors.col_aktiv2.length]);
         g2.fillRect(0, 0, xscal, yscal);
      } catch (NullPointerException var7) {
      }
   }

   public void aktiv3(PaintSaveInterface panel, Graphics2D g2, int xscal, int yscal, int level) {
      try {
         g2.setColor(colors.col_aktiv3[level % colors.col_aktiv3.length]);
         g2.fillRect(0, 0, xscal, yscal);
      } catch (NullPointerException var7) {
      }
   }

   public void highlight(PaintSaveInterface panel, Graphics g, int xscal, int yscal, int level) {
      try {
         g.setColor(colors.col_highlight[level % colors.col_highlight.length]);
         g.drawLine(0, 0, xscal, 0);
         g.drawLine(0, 0, 0, yscal);
         g.drawLine(0, yscal, xscal, yscal);
         g.drawLine(xscal, 0, xscal, yscal);
      } catch (NullPointerException var7) {
      }
   }

   void paintelement(Graphics2D g, int col, int row, int xscal, int yscal, int fscal, Color colr) {
      try {
         this.gleisdecor.elementPaint.paintelement(this, g, col, row, xscal, yscal, fscal, colr);
      } catch (Exception var9) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "paintelement", var9);
      }
   }

   void paintelement(Graphics2D g, int col, int row, int x, int y, int xscal, int yscal, int fscal, int cc, boolean geradeok, Color colr, int sdp, int ddp) {
      try {
         this.gleisdecor.elementPaint.paintelement(this, g, col, row, x, y, xscal, yscal, fscal, cc, geradeok, colr, sdp, ddp);
      } catch (Exception var15) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "paintelement", var15);
      }
   }

   void paintelementL(Graphics2D g, int col, int row, int xscal, int yscal, int fscal, Color colr) {
      try {
         this.gleisdecor.elementPaint.paintelementL(this, g, col, row, xscal, yscal, fscal, colr);
      } catch (NullPointerException var9) {
      } catch (Exception var10) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "paintelementL", var10);
      }
   }

   public void paint0(PaintSaveInterface panel, Graphics2D g, int xscal, int yscal, int fscal) {
      try {
         g.setColor(colors.col_stellwerk_back);
         if (this.gleisExtend != null) {
            colors.col_stellwerk_backms = (Color)colors.col_stellwerk_backmulti.get(this.gleisExtend.getFarbe());
            if (colors.col_stellwerk_backms != null) {
               g.setColor(colors.col_stellwerk_backms);
            }
         }

         if (this.modus_masstab && this.masstab > 0) {
            g.setColor(colors.col_stellwerk_masstab[this.glbModel.getMasstabCalculator().getColindexOfValue(this.masstab)]);
         }

         g.fillRect(0, 0, xscal, yscal);
      } catch (NullPointerException var7) {
      }
   }

   public void paint1(PaintSaveInterface panel, Graphics2D g, int xscal, int yscal, int fscal) {
      boolean geradeok = true;
      if (!ALLE_BSTTRENNER.matches(this.telement) || panel != null && panel.isEditorView()) {
         int col = this.mycol;
         int row = this.myrow;
         if (g != null) {
            try {
               if (panel.isEditorView()) {
                  this.gleisdecor.paint2.paint1Editor(this, g, xscal, yscal, fscal);
                  g.setColor(colors.col_stellwerk_raster);
                  g.drawLine(0, 0, xscal, 0);
                  g.drawLine(0, 0, 0, yscal);
               } else {
                  this.gleisdecor.paint2.paint1Sim(this, g, xscal, yscal, fscal);
               }
            } catch (NullPointerException var20) {
            }
         }

         if (ALLE_GLEISE.matches(this.telement) || ALLE_BSTTRENNER.matches(this.telement)) {
            Color colr;
            if (ALLE_GLEISE.matches(this.telement)) {
               colr = colors.col_stellwerk_gleis;
            } else if (ALLE_BSTTRENNER.matches(this.telement)) {
               colr = colors.col_stellwerk_bsttrenner;
            } else {
               colr = colors.col_stellwerk_back;
            }

            boolean painted = false;
            if (g != null) {
               this.setSmooth(g, true, 1);
            }

            int cc = 0;

            while (this.count > 0 && cc < 7) {
               int x = -1;
               int y = -1;
               int sdp = 0;
               int ddp = 0;
               if (this.glbModel.getXY(col - 1, row + 1).telement.matchesTyp(this.glbModel.getXY(col, row).telement)
                  && (
                     this.glbModel.getXY(col - 1, row + 1).telement == ELEMENT_WEICHEOBEN
                        || this.glbModel.getXY(col, row).telement == ELEMENT_WEICHEUNTEN
                        || this.glbModel.getXY(col - 1, row + 1).telement == ELEMENT_KREUZUNGBRUECKE
                        || this.glbModel.getXY(col - 1, row + 1).telement == ELEMENT_KREUZUNG
                  )
                  && cc <= 0) {
                  x = col - 1;
                  y = row + 1;
                  sdp = 2;
                  ddp = 1;
                  cc = 1;
               } else if (this.glbModel.getXY(col + 1, row + 1).telement.matchesTyp(this.glbModel.getXY(col, row).telement)
                  && (
                     this.glbModel.getXY(col + 1, row + 1).telement == ELEMENT_WEICHEOBEN
                        || this.glbModel.getXY(col, row).telement == ELEMENT_WEICHEUNTEN
                        || this.glbModel.getXY(col + 1, row + 1).telement == ELEMENT_KREUZUNGBRUECKE
                        || this.glbModel.getXY(col + 1, row + 1).telement == ELEMENT_KREUZUNG
                  )
                  && cc <= 1) {
                  x = col + 1;
                  y = row + 1;
                  sdp = 2;
                  ddp = 3;
                  cc = 2;
               } else if (this.glbModel.getXY(col + 1, row).telement.matchesTyp(this.glbModel.getXY(col, row).telement) && cc <= 2) {
                  x = col + 1;
                  y = row;
                  sdp = 1;
                  ddp = 3;
                  cc = 3;
                  if (this.telement == ELEMENT_WEICHEOBEN || this.telement == ELEMENT_WEICHEUNTEN) {
                     for (gleis gls : this.nachbar) {
                        if (gls.myrow < row && gls.mycol > col && this.fdata.stellung == gleisElements.ST_WEICHE_ABZWEIG) {
                           gls.extrastatus = true;
                           painted = true;
                           gls.paintelement(g, gls.mycol - this.mycol, gls.myrow - this.myrow, 0, 0, xscal, yscal, fscal, 5, true, colr, 2, 1);
                           break;
                        }

                        if (gls.myrow < row && gls.mycol < col && this.fdata.stellung == gleisElements.ST_WEICHE_ABZWEIG) {
                           gls.extrastatus = true;
                           painted = true;
                           gls.paintelement(g, gls.mycol - this.mycol, gls.myrow - this.myrow, 0, 0, xscal, yscal, fscal, 6, true, colr, 2, 3);
                           geradeok = false;
                           break;
                        }

                        if (gls.myrow == row && gls.mycol < col && this.fdata.stellung == gleisElements.ST_WEICHE_GERADE) {
                           for (gleis gls2 : this.nachbar) {
                              if (gls2 != gls && gls2.mycol < col) {
                                 gls.extrastatus = true;
                                 painted = true;
                                 gls.paintelement(g, gls.mycol - this.mycol, gls.myrow - this.myrow, 0, 0, xscal, yscal, fscal, 3, true, colr, 1, 3);
                                 geradeok = false;
                                 break;
                              }
                           }

                           if (!geradeok) {
                              break;
                           }
                        } else if (gls.myrow > row && gls.mycol < col && this.fdata.stellung == gleisElements.ST_WEICHE_ABZWEIG) {
                        }
                     }
                  }
               } else if (this.glbModel.getXY(col, row + 1).telement.matchesTyp(this.glbModel.getXY(col, row).telement) && cc <= 3) {
                  x = col;
                  y = row + 1;
                  sdp = 2;
                  ddp = 4;
                  cc = 4;
               } else if (this.glbModel.getXY(col - 1, row + 1).telement.matchesTyp(this.glbModel.getXY(col, row).telement) && cc <= 4) {
                  x = col - 1;
                  y = row + 1;
                  sdp = 2;
                  ddp = 1;
                  cc = 5;
               } else if (this.glbModel.getXY(col + 1, row + 1).telement.matchesTyp(this.glbModel.getXY(col, row).telement) && cc <= 5) {
                  x = col + 1;
                  y = row + 1;
                  sdp = 2;
                  ddp = 3;
                  cc = 6;
               }

               if (x >= 0 && y >= 0 && this.glbModel.getXY(x, y).count > 0) {
                  this.count--;
                  gleis gls = this.glbModel.getXY(x, y);
                  gls.count--;
                  this.nachbar.add(gls, sdp, ddp);
                  gls.addN(this, ddp, sdp);
                  painted = true;
                  this.paintelement(g, 0, 0, x - col, y - row, xscal, yscal, fscal, cc, geradeok, colr, sdp, ddp);
               } else {
                  cc++;
               }
            }

            if (!painted && g != null) {
               this.paintelement(g, 0, 0, xscal, yscal, fscal, colr);
            }

            if (g != null) {
               this.setSmooth(g, false, 1);
            }
         }
      }
   }

   public void paint1b(PaintSaveInterface panel, Graphics2D g, int xscal, int yscal, int fscal) {
      if (!ALLE_BSTTRENNER.matches(this.telement) || panel.isEditorView()) {
         if (ALLE_GLEISE.matches(this.telement) || ALLE_BSTTRENNER.matches(this.telement)) {
            Color colr;
            if (ALLE_GLEISE.matches(this.telement)) {
               colr = colors.col_stellwerk_gleis;
            } else if (ALLE_BSTTRENNER.matches(this.telement)) {
               colr = colors.col_stellwerk_bsttrenner;
            } else {
               colr = colors.col_stellwerk_back;
            }

            if (g != null) {
               this.setSmooth(g, true, 1);
               this.paintelementL(g, 0, 0, xscal, yscal, fscal, colr);
               this.setSmooth(g, false, 1);
            }
         }
      }
   }

   public void paint2(PaintSaveInterface panel, Graphics2D g, int xscal, int yscal, int fscal) {
      try {
         if (panel.isEditorView()) {
            if (!this.glbModel.isLayerDisabled(this.telement)) {
               this.gleisdecor.paint2.paint2Editor(this, g, xscal, yscal, fscal);
            }
         } else {
            this.gleisdecor.paint2.paint2Sim(this, g, xscal, yscal, fscal);
         }
      } catch (NullPointerException var7) {
      }
   }

   public void paint3(PaintSaveInterface panel, Graphics2D g, int xscal, int yscal, int fscal) {
      try {
         if (panel.isEditorView()) {
            if (!this.glbModel.isLayerDisabled(this.telement)) {
               this.gleisdecor.paint2.paint3Editor(this, g, xscal, yscal, fscal);
            }
         } else {
            this.gleisdecor.paint2.paint3Sim(this, g, xscal, yscal, fscal);
         }
      } catch (NullPointerException var7) {
      }
   }

   void paintSignalLED(Graphics g2, int x, int y, boolean used, Color lcol) {
      this.paintSignalLED(g2, x, y, used, lcol, null);
   }

   void paintSignalLED(Graphics g2, int x, int y, boolean used, Color lcol, Color randcol) {
      if (used) {
         if (randcol != null) {
            g2.setColor(randcol);
            g2.fillRect(x - 1, y - 1, 7, 7);
         }

         g2.setColor(colors.col_stellwerk_schwarz);
      } else {
         g2.setColor(colors.col_stellwerk_grau);
      }

      g2.fillRect(x, y, 5, 5);
      if (used && !this.fdata.power_off) {
         g2.setColor(lcol);
         g2.fillRect(x + 1, y + 1, 3, 3);
      }
   }

   public void paintVSignal(Graphics2D g, int x, int y, int fscal, Color col_rot, Color col_gruen, Color col_mast, Color col_back, double theta, int dx, int dy) {
      if (this.fdata.power_off) {
         col_rot = colors.col_stellwerk_gelbaus;
         col_gruen = colors.col_stellwerk_gruenaus;
      }

      Graphics2D g2 = (Graphics2D)g.create(x - fscal * 4, y - fscal * 4, fscal * 8, fscal * 8);
      g2.translate(fscal * 4, fscal * 4);
      g2.rotate(theta);
      g2.translate(fscal / 2, -4);
      int sx1 = (int)((double)(-1 * fscal) / 6.0 + (double)fscal / 2.0) - fscal / 2 - 1;
      int sx2 = (int)((double)(1 * fscal) / 6.0 + (double)fscal / 2.0) - fscal / 2 + 1;
      g2.setColor(col_back);
      g2.fillRect(sx1, -fscal / 3 - 2, sx2 - sx1, fscal * 2 + fscal / 3 + 2);
      this.setSmooth(g2, true, 4);
      g2.setColor(col_mast);
      g2.fillRect(-2, 0 - fscal / 3, 2, fscal * 2);
      g2.fillRect(-fscal / 2 - 1, 0 - fscal / 3, fscal, 4);
      g2.translate(0, fscal);
      g2.rotate(-0.6108652381980153);
      Polygon p = new Polygon();
      p.addPoint(-fscal - 1, 0);
      p.addPoint(-fscal - 1 + fscal / 3, -fscal / 2);
      p.addPoint(fscal - fscal / 3, -fscal / 2);
      p.addPoint(fscal, 0);
      p.addPoint(fscal - fscal / 3, fscal / 2);
      p.addPoint(-fscal - 1 + fscal / 3, fscal / 2);
      g2.fillPolygon(p);
      g2.setColor(col_gruen);
      g2.fillOval(-fscal + 1, -fscal / 3, 4, 4);
      g2.setColor(col_rot);
      g2.fillOval(fscal / 2 - 4, -fscal / 3, 4, 4);
      this.setSmooth(g2, false, 4);
      g2.dispose();
   }

   public boolean isKopfSignal() {
      boolean kopfgleis = false;
      int dx = 0;
      int dy = 0;
      switch (this.richtung) {
         case right:
            dx = 1;
            break;
         case left:
            dx = -1;
            break;
         case down:
            dy = 1;
            break;
         case up:
            dy = -1;
      }

      gleis eg = this.glbModel.getXY_null(this.mycol + dx, this.myrow + dy);
      if (eg != null && eg.telement == ELEMENT_STRECKE && eg.nachbar.size() == 1) {
         kopfgleis = true;
      }

      return kopfgleis;
   }

   public boolean isHauptZwergSignal() {
      return this.signalRfOnlyStart && (!this.signalRfOnlyStop || !this.anyStoppingFS) && !this.isKopfSignal();
   }

   public void paintSignal(
      Graphics2D g,
      int x,
      int y,
      int fscal,
      Color col_signal,
      Color col_rot,
      Color col_gruen,
      Color col_zs1,
      Color col_rf,
      Color col_vrot,
      Color col_vgruen,
      double theta,
      boolean kopfgleis,
      boolean hauptZwerg
   ) {
      if (this.fdata.power_off) {
         col_rot = colors.col_stellwerk_rotaus;
         col_gruen = colors.col_stellwerk_gruenaus;
         col_zs1 = colors.col_stellwerk_gruenaus;
         col_vgruen = colors.col_stellwerk_gruenaus;
         col_vrot = colors.col_stellwerk_gelbaus;
      }

      hauptZwerg &= this.glbModel.gleisbildextend.isHauptZwergSignal();
      int v = this.glbModel.gleisbildextend.getSignalversion();
      kopfgleis &= this.glbModel.gleisbildextend.isCleverSignal();
      Graphics2D g2 = (Graphics2D)g.create();
      g2.translate(x - fscal * 4, y - fscal * 4);
      Graphics2D ledg = g2;
      g2.translate(fscal * 4, fscal * 4);
      g2.rotate(theta);
      if (v == 4 || v == 5 || v == 6) {
         AffineTransform a = g2.getTransform();
         ledg = (Graphics2D)g2.create();
         AffineTransform b = new AffineTransform(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
         a.concatenate(b);
         ledg.setTransform(a);
         ledg.translate(-fscal - fscal, v == 5 ? -6 : 0);
         g2.translate(fscal + 1, v == 5 ? -6 : 0);
      } else if (v == 2) {
         g2.translate(-fscal - 1, -6);
      } else {
         g2.translate(-fscal - 1, 0);
      }

      if (hauptZwerg) {
         this.paintZwergSignal(g, x, y, fscal, col_rot, col_rf, theta);
         this.paintSignalLeds(ledg, fscal, -6, false, true);
         g2.dispose();
      } else {
         int bottomY = 0;
         if (this.signalRfStart) {
            bottomY += 12;
         }

         if (this.gleisExtend.vorsignalSignal) {
            bottomY += 12;
         }

         this.setSmooth(g2, true, 4);
         this.setSmooth(ledg, true, 4);
         g2.setColor(col_signal);
         if (kopfgleis) {
            g2.fillRoundRect(0, fscal, fscal, fscal, 5, 5);
         } else {
            g2.fillRoundRect(0, fscal - bottomY, fscal, 2 * fscal + bottomY, 5, 5);
         }

         if (v != 2 && v != 5) {
            g2.fillRect(fscal / 2 - 1, 0 - fscal - bottomY, 2, fscal * 2 + bottomY);
            g2.fillRect(0, 0 - fscal - bottomY, fscal, 4);
         } else {
            g2.fillRect(fscal / 2 - 1, -bottomY, 2, fscal * 1 + bottomY);
            g2.fillRect(0, -bottomY, fscal, 2);
         }

         if (!kopfgleis) {
            g2.fillRect(1, fscal - 6 - bottomY, fscal - 2, 5);
            g2.setColor(col_zs1);
            g2.fillRect(2, fscal - 5 - bottomY, fscal - 4, 3);
            g2.setColor(col_gruen);
            g2.fillOval(1, 2 * fscal + 1, fscal - 2, fscal - 2);
         }

         g2.setColor(col_rot);
         g2.fillOval(1, fscal + 1, fscal - 2, fscal - 2);
         if (this.signalRfStart) {
            g2.setColor(col_rf);
            Polygon p = new Polygon();
            p.addPoint(fscal - 2, fscal - 8);
            p.addPoint(fscal - 2, fscal - 6);
            p.addPoint(2, fscal - 3);
            p.addPoint(2, fscal - 5);
            g2.fill(p);
         }

         this.paintSignalLeds(ledg, fscal, bottomY, kopfgleis, false);
         if (this.gleisExtend.vorsignalSignal) {
            g2.translate(v != 4 && v != 5 && v != 6 ? 3 : 6, -fscal + 10 - (this.signalRfStart ? 8 : 0));
            g2.rotate(-0.6108652381980153);
            Polygon p = new Polygon();
            p.addPoint(-fscal - 1, 0);
            p.addPoint(-fscal - 1 + fscal / 3, -fscal / 2);
            p.addPoint(fscal - fscal / 3, -fscal / 2);
            p.addPoint(fscal, 0);
            p.addPoint(fscal - fscal / 3, fscal / 2);
            p.addPoint(-fscal - 1 + fscal / 3, fscal / 2);
            g2.setColor(col_signal);
            g2.fillPolygon(p);
            g2.setColor(col_vgruen);
            g2.fillOval(-fscal + 1, -fscal / 3, 4, 4);
            g2.setColor(col_vrot);
            g2.fillOval(fscal / 2 - 4, -fscal / 3, 4, 4);
         }

         this.setSmooth(g2, false, 4);
         g2.dispose();
      }
   }

   private void paintSignalLeds(Graphics2D g2, int fscal, int bottomY, boolean kopfgleis, boolean hauptZwerg) {
      int v = this.glbModel.gleisbildextend.getSignalversion();
      boolean autoFSled = this.autoFW.hasAutoFSled();
      if (v == 0) {
         if (autoFSled) {
            g2.setColor(this.autoFW.getRandColor());
            g2.fillOval(-4, 4 - fscal - bottomY, 6, 6);
            g2.setColor(this.autoFW.getLedColor());
            g2.fillOval(-2, 6 - fscal - bottomY, 2, 2);
         }
      } else {
         int x0;
         int y0;
         int x1;
         int y1;
         int x2;
         int y2;
         int x3;
         int y3;
         switch (v) {
            case 1:
            case 4:
            default:
               x0 = fscal * 2 + 2;
               y0 = -6;
               x1 = x0;
               y1 = y0 + 8;
               x2 = x0;
               y2 = y1 + 8;
               x3 = x0;
               y3 = y2 + 8;
               break;
            case 2:
            case 5:
               x2 = -3;
               y2 = fscal * 3 + 2;
               x3 = x2 + fscal / 2 + 2;
               y3 = y2;
               x1 = -3;
               y1 = -6 - bottomY;
               x0 = x1 + fscal / 2 + 2;
               y0 = y1;
               break;
            case 3:
            case 6:
               x2 = -3;
               y2 = -fscal - 6 - bottomY;
               x3 = x2 + fscal / 2 + 2;
               y3 = y2;
               x1 = x2;
               y1 = y2 - 6;
               x0 = x3;
               y0 = y2 - 6;
         }

         this.paintSignalLED(
            g2,
            x0,
            y0,
            true,
            this.mixColor(
               this.FSEND_TEST,
               colors.col_stellwerk_gelbein,
               this.mixColor(this.fdata.fsspeicherend > 0 && blinkon, colors.col_stellwerk_gelbein, this.autoFW.getLedEndColor())
            )
         );
         if (!kopfgleis && !hauptZwerg) {
            this.paintSignalLED(
               g2,
               x3,
               y3,
               true,
               this.mixColor(
                  this.FSSTART_TEST,
                  colors.col_stellwerk_gelbein,
                  this.mixColor(this.fdata.fsspeicher != null && blinkon, colors.col_stellwerk_gelbein, colors.col_stellwerk_gelbaus)
               )
            );
            this.paintSignalLED(
               g2,
               x2,
               y2,
               this.AUTOFW_TEST || autoFSled,
               this.mixColor(this.AUTOFW_TEST, colors.col_stellwerk_weiss, this.autoFW.getLedColor()),
               this.autoFW.getRandColor()
            );
         }

         this.paintSignalLED(g2, x1, y1, this.FREELED_TEST, this.mixColor(this.FREELED_TEST, colors.col_stellwerk_gruenein, colors.col_stellwerk_gruenaus));
      }
   }

   public void paintZwergSignal(Graphics2D g, int x, int y, int fscal, Color col_rot, Color col_gruen, double theta) {
      if (this.fdata.power_off) {
         col_rot = colors.col_stellwerk_rotaus;
         col_gruen = colors.col_stellwerk_gruenaus;
      }

      Graphics2D g2 = (Graphics2D)g.create(x - fscal * 4, y - fscal * 4, fscal * 8, fscal * 8);
      g2.translate(fscal * 4, fscal * 4);
      g2.rotate(theta);
      if (this.glbModel.gleisbildextend.getSignalversion() != 4
         && this.glbModel.gleisbildextend.getSignalversion() != 5
         && this.glbModel.gleisbildextend.getSignalversion() != 6) {
         g2.translate(-fscal - 1, 0);
      } else {
         g2.translate(fscal - 2, 0);
      }

      this.setSmooth(g2, true, 4);
      g2.setColor(colors.col_stellwerk_schwarz);
      g2.fillRoundRect(0, fscal / 2, fscal, fscal, 5, 5);
      g2.fillRect(fscal / 2 - 1, fscal / 4, 2, fscal);
      g2.fillRect(0, fscal / 4, fscal, 2);
      g2.setColor(col_rot);
      g2.fillRect(2, fscal / 2 + fscal - 3, fscal - 4, 2);
      g2.setColor(col_gruen);
      Polygon p = new Polygon();
      p.addPoint(fscal - 2, fscal - 4);
      p.addPoint(fscal - 2, fscal - 2);
      p.addPoint(2, fscal + 1);
      p.addPoint(2, fscal - 1);
      g2.fill(p);
      this.setSmooth(g2, false, 4);
      g2.dispose();
   }

   public void paintDeckungsSignal(Graphics2D g, int x, int y, int fscal, Color col_rot, Color col_zs1, double theta) {
      if (this.fdata.power_off) {
         col_rot = colors.col_stellwerk_rotaus;
         col_zs1 = colors.col_stellwerk_frei;
      }

      Graphics2D g2 = (Graphics2D)g.create(x - fscal * 4, y - fscal * 4, fscal * 8, fscal * 8);
      int v = this.glbModel.gleisbildextend.getSignalversion();
      g2.translate(fscal * 4, fscal * 4);
      g2.rotate(theta);
      if (v != 4 && v != 5 && v != 6) {
         g2.translate(-fscal - 1, -5);
      } else {
         AffineTransform a = g2.getTransform();
         AffineTransform b = new AffineTransform(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
         a.concatenate(b);
         g2.setTransform(a);
         g2.translate(-fscal - fscal + 2, -5);
      }

      this.setSmooth(g2, true, 4);
      g2.setColor(colors.col_stellwerk_schwarz);
      g2.fillRoundRect(0, fscal / 2, fscal, 2 * fscal, 5, 5);
      g2.fillRect(fscal / 2 - 1, 0 - fscal / 4, 2, fscal * 2);
      g2.fillRect(0, 0 - fscal / 4, fscal, 4);
      g2.setColor(col_zs1);
      g2.fillOval(2, fscal / 2 + fscal + 2, fscal - 4, fscal - 4);
      g2.setColor(col_rot);
      g2.fillOval(1, fscal / 2 + 1, fscal - 2, fscal - 2);
      int x0 = fscal * 2 + 2;
      int y0 = 4;
      this.paintSignalLED(
         g2,
         x0,
         y0,
         true,
         this.mixColor(this.fdata.stellung == ST_ZDSIGNAL_FESTGELEGT || this.FSSTART_TEST, colors.col_stellwerk_gelbein, colors.col_stellwerk_gelbaus)
      );
      this.setSmooth(g2, false, 4);
      g2.dispose();
   }

   public void paintVorsignal(Graphics2D g, int x, int y, int fscal, Color col_signal, Color col_rot, Color col_gruen, double theta) {
      if (this.fdata.power_off) {
         col_rot = colors.col_stellwerk_gelbaus;
         col_gruen = colors.col_stellwerk_gruenaus;
      }

      int v = this.glbModel.gleisbildextend.getSignalversion();
      Graphics2D g2 = (Graphics2D)g.create(x - fscal * 4, y - fscal * 4, fscal * 8, fscal * 8);
      g2.translate(fscal * 4, fscal * 4);
      g2.rotate(theta);
      if (v != 4 && v != 5 && v != 6) {
         g2.translate(-fscal + 2, 0);
      } else {
         g2.translate(fscal + 5, 0);
      }

      this.setSmooth(g2, true, 4);
      g2.setColor(col_signal);
      g2.fillRect(-2, 0 - fscal / 3, 2, fscal * 2);
      g2.fillRect(-fscal / 2 - 1, 0 - fscal / 3, fscal, 4);
      g2.translate(0, fscal);
      g2.rotate(-0.6108652381980153);
      Polygon p = new Polygon();
      p.addPoint(-fscal - 1, 0);
      p.addPoint(-fscal - 1 + fscal / 3, -fscal / 2);
      p.addPoint(fscal - fscal / 3, -fscal / 2);
      p.addPoint(fscal, 0);
      p.addPoint(fscal - fscal / 3, fscal / 2);
      p.addPoint(-fscal - 1 + fscal / 3, fscal / 2);
      g2.fillPolygon(p);
      g2.setColor(col_gruen);
      g2.fillOval(-fscal + 1, -fscal / 3, 4, 4);
      g2.setColor(col_rot);
      g2.fillOval(fscal / 2 - 4, -fscal / 3, 4, 4);
      this.setSmooth(g2, false, 4);
      g2.dispose();
   }

   private Color mixColor(boolean value, Color truecol, Color falsecol) {
      return value ? truecol : falsecol;
   }

   public void paintAusfahrt(Graphics2D g, int x, int y, int fscal, double theta) {
      Graphics2D g2 = (Graphics2D)g.create(x - fscal * 4, y - fscal * 4, fscal * 8, fscal * 8);
      int v = 0;
      v = this.glbModel.gleisbildextend.getSignalversion();
      g2.translate(fscal * 4, fscal * 4);
      g2.rotate(theta);
      if (v != 4 && v != 5 && v != 6) {
         g2.translate(-fscal, 0);
      } else {
         AffineTransform a = g2.getTransform();
         AffineTransform b = new AffineTransform(-1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
         a.concatenate(b);
         g2.setTransform(a);
      }

      if (v != 0) {
         int x0;
         if (v == 1) {
            x0 = fscal + fscal;
         } else {
            x0 = fscal - fscal / 2 - 2;
         }

         int y0 = -fscal + fscal / 3;
         this.paintSignalLED(
            g2, x0, y0, true, this.mixColor(this.fdata.fsspeicherend > 0 && blinkon, colors.col_stellwerk_gelbein, this.autoFW.getLedEndColor())
         );
      }
   }

   public void paintSmallKnob(Graphics2D g, int x0, int y0, int xscal) {
      int o = 2 * xscal - 17;
      int o2 = o - 4;
      this.setSmooth(g, true, 5);
      if (!this.fdata.pressed) {
      }

      g.setColor(colors.col_stellwerk_schwarz);
      g.fillOval(
         x0 - o / 2 + (this.fdata.pressed ? 2 : 0),
         y0 - o / 2 - 2 + (this.fdata.pressed ? 2 : 0),
         o - (this.fdata.pressed ? 2 : 0),
         o - (this.fdata.pressed ? 2 : 0)
      );
      g.setColor(colors.col_stellwerk_grau);
      g.fillOval(
         x0 - o2 / 2 + (this.fdata.pressed ? 2 : 0),
         y0 - o2 / 2 - 2 + (this.fdata.pressed ? 2 : 0),
         o2 - (this.fdata.pressed ? 1 : 0),
         o2 - (this.fdata.pressed ? 1 : 0)
      );
      if (this.fdata.gesperrt) {
         g.setColor(colors.col_stellwerk_grau_locked);
         g.fillOval(x0 - o / 2 - 3, y0 - o / 2 - 2 - 3, o + 6, o + 6);
      }

      this.setSmooth(g, false, 5);
   }

   public void paintBigKnob(Graphics2D g, int x0, int y0, int xscal) {
      int o = xscal - 7;
      int o2 = o - 2;
      this.setSmooth(g, true, 5);
      if (!this.fdata.pressed) {
         g.setColor(colors.col_stellwerk_knopfseite);
         g.fillOval(
            x0 - o + (this.fdata.pressed ? 2 : 0) + 1,
            y0 - o + (this.fdata.pressed ? 2 : 0) + 1,
            o * 2 - (this.fdata.pressed ? 2 : 0),
            o * 2 - (this.fdata.pressed ? 2 : 0)
         );
      }

      g.setColor(colors.col_stellwerk_schwarz);
      g.fillOval(
         x0 - o + (this.fdata.pressed ? 2 : 0),
         y0 - o + (this.fdata.pressed ? 2 : 0),
         o * 2 - (this.fdata.pressed ? 2 : 0),
         o * 2 - (this.fdata.pressed ? 2 : 0)
      );
      g.setColor(colors.col_stellwerk_rot);
      g.fillOval(
         x0 - o2 + (this.fdata.pressed ? 2 : 0),
         y0 - o2 + (this.fdata.pressed ? 2 : 0),
         o2 * 2 - (this.fdata.pressed ? 1 : 0),
         o2 * 2 - (this.fdata.pressed ? 1 : 0)
      );
      if (this.fdata.gesperrt) {
         g.setColor(colors.col_stellwerk_rot_locked);
         g.fillOval(x0 - o - 3, y0 - o - 3, o * 2 + 6, o * 2 + 6);
      }

      this.setSmooth(g, false, 5);
   }

   @Deprecated
   public Rectangle getElemSize(double xscal) {
      Rectangle ret;
      if (ALLE_TEXTE.matches(this.telement) && this.lastelemwidth != null) {
         ret = this.lastelemwidth;
      } else {
         ret = new Rectangle();
      }

      return ret;
   }

   public int printwidth(Graphics2D g, String text, int fontsize) {
      int fonttype = 1;
      return this.printwidth(g, text, fontsize, fonttype);
   }

   public int printwidth(Graphics2D g, String text, int fontsize, int fonttype) {
      Font f = new Font("SansSerif", fonttype, fontsize);
      g.setFont(f);
      FontMetrics fm = g.getFontMetrics();
      return fm.stringWidth(text);
   }

   public int printtextright(Graphics2D g2, String text, Color col, int x, int y, int fontsize) {
      int fonttype = 1;
      return this.printtextright(g2, text, col, x, y, fontsize, fonttype);
   }

   public int printtextright(Graphics2D g2, String text, Color col, int x, int y, int fontsize, int fonttype) {
      int w = this.printwidth(g2, text, fontsize, fonttype);
      this.printtext(g2, text, col, x - w, y, fontsize, fonttype);
      return w;
   }

   public int printtextcentered(Graphics2D g2, String text, Color col, int x, int y, int fontsize) {
      int fonttype = 1;
      return this.printtextcentered(g2, text, col, x, y, fontsize, fonttype);
   }

   public int printtextcentered(Graphics2D g2, String text, Color col, int x, int y, int fontsize, int fonttype) {
      int w = this.printwidth(g2, text, fontsize, fonttype);
      this.printtext(g2, text, col, x - w / 2, y, fontsize, fonttype);
      return w / 2;
   }

   public int printtext(Graphics2D g2, String text, Color col, int x, int y, int fontsize) {
      int fonttype = 1;
      return this.printtext(g2, text, col, x, y, fontsize, fonttype);
   }

   public int printtext(Graphics2D g2, String text, Color col, int x, int y, int fontsize, int fonttype) {
      Font f = new Font("SansSerif", fonttype, fontsize);
      g2.setColor(col);
      g2.setFont(f);
      FontMetrics fm = g2.getFontMetrics();
      this.setSmoothText(g2, true);
      g2.drawString(text, x + 2, y + fm.getAscent());
      return fm.stringWidth(text);
   }

   @Deprecated
   public void gleislabel(Graphics2D g, String name, int col, int xscal, int row, int yscal, int rot) {
      int fontsize = xscal - 3;
      Graphics2D g2 = (Graphics2D)g.create((col - 4) * xscal, (row - 4) * yscal, xscal * 8, yscal * 8);
      g2.translate(xscal * 4 + xscal / 2, yscal * 4 + yscal / 2);
      g2.setColor(colors.col_stellwerk_signalnummerhgr);
      int LENGTH = this.printwidth(g, name, fontsize, 0);
      int HEIGHT = fontsize + 2;
      int XM = -xscal / 2;
      double theta = 0.0;
      int xs;
      int ys;
      switch (rot) {
         case 0:
            xs = -XM - LENGTH;
            ys = -HEIGHT / 2;
            break;
         case 90:
            xs = XM;
            ys = -HEIGHT / 2;
            theta = -Math.PI / 2;
            break;
         case 180:
         default:
            xs = XM;
            ys = -HEIGHT / 2;
            break;
         case 270:
            xs = -XM - LENGTH;
            ys = -HEIGHT / 2;
            theta = -Math.PI / 2;
      }

      g2.rotate(theta);
      g2.fillRect(xs, ys, LENGTH + 3, HEIGHT);
      this.printtext(g2, name, colors.col_stellwerk_signalnummer, xs, ys, fontsize, 0);
      g2.dispose();
   }

   public void elementlabel(Graphics2D g, String name, int xscal, int yscal, int rot) {
      int fontsize = xscal - 5;
      Graphics2D g2 = (Graphics2D)g.create(-4 * xscal, -4 * yscal, xscal * 8, yscal * 8);
      g2.translate(xscal * 4 + xscal / 2, yscal * 4 + yscal / 2);
      g2.setColor(colors.col_stellwerk_signalnummerhgr);
      int LENGTH = xscal + xscal / 3 - 2;
      int HEIGHT = fontsize + 2;
      int XM = 4;
      double theta = 0.0;
      int xs;
      int ys;
      switch (rot) {
         case 0:
            xs = -XM - LENGTH;
            ys = -HEIGHT / 2;
            break;
         case 90:
            xs = XM;
            ys = -HEIGHT / 2;
            theta = -Math.PI / 2;
            break;
         case 180:
         default:
            xs = XM;
            ys = -HEIGHT / 2;
            break;
         case 270:
            xs = -XM - LENGTH;
            ys = -HEIGHT / 2;
            theta = -Math.PI / 2;
      }

      g2.rotate(theta);
      g2.fillRect(xs, ys, LENGTH, HEIGHT);
      this.printtext(g2, name, colors.col_stellwerk_signalnummer, xs - 1, ys, fontsize, 0);
      g2.dispose();
   }

   private Font getLcdFont(int w) {
      if (!lcdFonts.containsKey(w)) {
         gleis.FONT_K fk = gleis.FONT_K.DS;
         if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_11)) {
            fk = gleis.FONT_K.DM;
         }

         if (lcdFontBase == null) {
            InputStream fontStream = this.getClass().getResourceAsStream(fk.n);
            if (fontStream != null) {
               try {
                  lcdFontBase = Font.createFont(0, fontStream);
               } catch (IOException | FontFormatException var5) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "font", var5);
               }
            } else {
               Logger.getLogger("stslogger").log(Level.SEVERE, "no font");
            }
         }

         Font f;
         if (lcdFontBase != null) {
            f = lcdFontBase.deriveFont(1, (float)(w + fk.P));
         } else {
            f = new Font("Sans-Serif", 1, w);
         }

         lcdFonts.put(w, f);
      }

      return (Font)lcdFonts.get(w);
   }

   void paintDisplay(Graphics2D g, int x, int y, int w) {
      if (!this.hideDisplay) {
         x += 2;
         Font f = this.getLcdFont(w);
         char[] text = new char[1];
         g.setColor(colors.col_stellwerk_displayoff);
         g.setFont(f);
         FontMetrics fm = g.getFontMetrics();
         this.setSmoothText(g, true);

         for (char t = '1'; t < '9'; t = (char)(t + 3)) {
            text[0] = t;
            int ww = fm.charWidth(t);
            g.drawChars(text, 0, 1, x + (w - ww) / 2, y + fm.getAscent());
         }
      }
   }

   void printDisplay(Graphics2D g, char t, int x, int y, int w, Color col, boolean lcd) {
      if (!this.hideDisplay && !this.fdata.power_off) {
         x += 2;
         Font f;
         if (lcd) {
            f = this.getLcdFont(w);
         } else {
            f = new Font("Sans-Serif", 1, w);
            y -= 3;
         }

         char[] text = new char[]{t};
         this.setSmoothText(g, true);
         g.setColor(col);
         g.setFont(f);
         FontMetrics fm = g.getFontMetrics();
         int ww = fm.charWidth(t);
         g.drawChars(text, 0, 1, x + (w - ww) / 2, y + fm.getAscent());
      }
   }

   public static void setAllowSmooth(boolean s) {
      if (s) {
         allowsmooth = 0;
      } else {
         allowsmooth++;
      }
   }

   public static void forceSmoothOff() {
      allowsmooth = 100;
   }

   public static void forceSmoothOn() {
      allowsmooth = 0;
   }

   void setSmoothText(Graphics2D g2, boolean on) {
      if (on && allowsmooth < 6) {
         GraphicTools.enableTextAA(g2);
      } else {
         GraphicTools.disableTextAA(g2);
      }
   }

   void setSmooth(Graphics2D g2, boolean on, int level) {
      if (on && allowsmooth < level) {
         GraphicTools.enableGfxAA(g2);
      } else {
         GraphicTools.disableGfxAA(g2);
      }
   }

   public boolean openGleis() {
      return this.nachbar.size() == 1 && this.getElement() != ELEMENT_SPRUNG;
   }

   public boolean kopfSignal() {
      gleis g = this.nextByRichtung(false);
      return g != null && g.openGleis();
   }

   public boolean kopfGleis() {
      if (this.nachbar.size() == 1) {
         gleis g = this.nachbar.get(0);
         return g.getElement() == ELEMENT_SIGNAL;
      } else {
         return false;
      }
   }

   public Iterator<gleis> getNachbarn() {
      return this.nachbar.iterator();
   }

   gleis getFirstNachbar() {
      return this.nachbar.get(0);
   }

   gleis getNachbar(int p) {
      return this.nachbar.get(p);
   }

   public int getNachbarCount() {
      return this.nachbar.size();
   }

   Iterator<gleis.nachbarGleis> p_getNachbarn() {
      return this.nachbar.e_iterator();
   }

   public boolean isNachbar(gleis ig) {
      for (gleis g : this.nachbar) {
         if (g.sameGleis(ig)) {
            return true;
         }
      }

      return false;
   }

   public gleis next(gleis before) {
      gleis ret = null;

      try {
         ret = this.gleisdecor.nextGleis.nextGleis(this, before);
      } catch (IndexOutOfBoundsException var4) {
         ret = null;
      } catch (Exception var5) {
         System.out.println("Nachbar Fehler!");
         Logger.getLogger("stslogger").log(Level.SEVERE, "Nachbar Fehler ok", var5);
         ret = null;
      }

      return ret;
   }

   public gleis nextWeichenAst(boolean kontra) {
      gleis ret = null;
      if (this.telement == ELEMENT_WEICHEOBEN || this.telement == ELEMENT_WEICHEUNTEN) {
         Iterator<gleis> it = this.nachbar.iterator();

         while (it.hasNext() && ret == null) {
            gleis gl = (gleis)it.next();
            Iterator<gleis> it2 = this.nachbar.iterator();

            while (it2.hasNext() && ret == null) {
               gleis gl2 = (gleis)it2.next();
               if (!gl.sameGleis(gl2) && gl2.mycol == gl.mycol) {
                  if (this.fdata.stellung == gleisElements.ST_WEICHE_GERADE) {
                     if (gl2.myrow == this.myrow) {
                        ret = kontra ? gl : gl2;
                     } else {
                        ret = kontra ? gl2 : gl;
                     }
                  } else if (gl2.myrow != this.myrow) {
                     ret = kontra ? gl : gl2;
                  } else {
                     ret = kontra ? gl2 : gl;
                  }
               }
            }
         }
      }

      return ret;
   }

   public boolean forUs(gleis before) {
      boolean ret = false;
      switch (this.richtung) {
         case right:
            ret = before.mycol < this.mycol;
            break;
         case left:
            ret = before.mycol > this.mycol;
            break;
         case down:
            ret = before.myrow < this.myrow;
            break;
         case up:
            ret = before.myrow > this.myrow;
      }

      return ret;
   }

   public boolean weicheSpitz(gleis before_gl) {
      boolean r = false;
      boolean r1 = this.weicheOfDirection(before_gl, false);
      boolean r2 = this.weicheOfDirection(before_gl, true);
      return r1 && !r2;
   }

   public gleis weicheSpitzgleis() {
      gleis ret = null;
      if (this.telement == ELEMENT_WEICHEOBEN || this.telement == ELEMENT_WEICHEUNTEN) {
         gleis temp = null;

         for (gleis gl : this.nachbar) {
            for (gleis gl2 : this.nachbar) {
               if (gl != gl2) {
                  if (gl.mycol != gl2.mycol) {
                     ret = temp;
                     temp = gl;
                  } else {
                     temp = ret;
                  }
               }
            }
         }
      }

      return ret;
   }

   public boolean weicheOfDirection(gleis before_gl, boolean b) {
      if (this.telement == ELEMENT_WEICHEOBEN || this.telement == ELEMENT_WEICHEUNTEN) {
         for (gleis gl : this.nachbar) {
            for (gleis gl2 : this.nachbar) {
               if (gl != gl2 && gl.mycol == gl2.mycol) {
                  if (gl != before_gl && gl2 != before_gl) {
                     return !b;
                  }

                  return b;
               }
            }
         }
      }

      return false;
   }

   public gleis nextByRichtung(boolean invers) {
      gleis ret = null;

      for (gleis gl : this.nachbar) {
         if (invers && this.forUs(gl) || !invers && !this.forUs(gl)) {
            ret = gl;
            break;
         }
      }

      return ret;
   }

   public gleisElements.RICHTUNG getRichtung() {
      return this.richtung;
   }

   public void setRichtung(gleisElements.RICHTUNG r) {
      this.richtung = r;
   }

   @Override
   public boolean ping() {
      return this.gleisdecor.ping.ping(this);
   }

   public void decrementBlinkcc(int i) {
      this.blinkcc -= i;
   }

   public void informStartingFS(fahrstrasse f) {
      this.autoFW.incFWcount(f);
      this.signalRfStart = this.signalRfStart | f.allowsRf();
      this.signalRfOnlyStart = this.signalRfOnlyStart & f.isRFonly();
   }

   public void informStopingFS(fahrstrasse f) {
      this.signalRfOnlyStop = this.signalRfOnlyStop & f.isRFonly();
      this.anyStoppingFS = true;
   }

   public void enableAutoFW(boolean triggered) {
      this.autoFW.enableAutoFW(triggered);
   }

   public void disableAutoFW() {
      this.autoFW.disableAutoFW();
   }

   public boolean isAutoFWenabled() {
      return this.autoFW.isAutoFWenabled();
   }

   public void triggerApproachingFS() {
      this.autoFW.triggerApproachingFS();
   }

   public void triggerStartingFS() {
      this.autoFW.triggerStartingFS();
   }

   public void setTriggeredAutoFW(fahrstrasseSelection f) {
      if (!f.isRangiermodus()) {
         this.autoFW.setTriggeredAutoFW(f.getFahrstrasse());
      }
   }

   public void clearTriggeredAutoFW() {
      this.autoFW.clearTriggeredAutoFW();
   }

   public void setAutoFWuepExplizid() {
      this.autoFW.setÜPExplizid(true);
   }

   int getElementNumber() {
      return this.element_enr + 100;
   }

   public void createName() {
      if (this.typHasElementName() && this.element_enr == 0) {
         if (ALLE_ENR_BAHNÜBERGÄNGE.matches(this.telement)) {
            gleis gl2 = null;
            LinkedList<gleis> ll = new LinkedList();
            int has_e_enr = this.element_enr;
            ll.add(this);
            Iterator<gleis> it = this.glbModel.findIterator(ALLE_ENR_BAHNÜBERGÄNGE, this.getENR(), this);

            while (it.hasNext()) {
               gl2 = (gleis)it.next();
               ll.add(gl2);
               if (gl2.element_enr > 0) {
                  has_e_enr = gl2.element_enr;
               }
            }

            if (has_e_enr == 0) {
               element_enr_counter++;
               has_e_enr = element_enr_counter;
            }

            for (gleis gl : ll) {
               gl.element_enr = has_e_enr;
            }
         } else {
            element_enr_counter++;
            this.element_enr = element_enr_counter;
         }
      }
   }

   public String getElementName() {
      return (this.gleisdecor.namePrefix != null ? this.gleisdecor.namePrefix : "") + this.getShortElementName();
   }

   public String getShortElementName() {
      return this.gleisExtend.elementName != null && !this.gleisExtend.elementName.isEmpty()
         ? this.gleisExtend.elementName
         : Integer.toString(this.getElementNumber());
   }

   public void setLEDs(boolean[] r) {
      this.FSSTART_TEST = r[0];
      this.FSEND_TEST = r[1];
      this.AUTOFW_TEST = r[2];
      this.FREELED_TEST = r[3];
   }

   public boolean[] getLEDs() {
      return new boolean[]{this.FSSTART_TEST, this.FSEND_TEST, this.AUTOFW_TEST, this.FREELED_TEST};
   }

   private void setTypElementDecor() {
      this.decorCache.clear();
      decor.getDecor().setDecor(this);
      this.fdata = this.createFluentData();
   }

   protected fluentData createFluentData() {
      return new fluentData(this);
   }

   public void tjmAdd() {
      this.tjm_add(this);
   }

   public long getCntZug() {
      return this.fdata.cnt_zug;
   }

   public long getCntStellung() {
      return this.fdata.cnt_stellung;
   }

   public void setEinfahrt(gleis eing) {
      this.ein_enr = eing.enr;
   }

   public int getEinfahrtEnr() {
      return this.ein_enr;
   }

   public static enum EXTENDS {
      FARBE;
   }

   private static enum FONT_K {
      DS(2, "/js/java/tools/resources/digitalism.ttf"),
      D7(1, "/js/java/tools/resources/digital-7.ttf"),
      DM(1, "/js/java/tools/resources/DOTMATRI.TTF");

      public final int P;
      public final String n;

      private FONT_K(int P, String n) {
         this.P = P;
         this.n = n;
      }
   }

   public static class gleisUIcom {
      public element element;
      public gleisElements.RICHTUNG richtung;
      public int enr;
      public String swwert;
      public boolean changed = false;
      private gleis gl;

      gleisUIcom(gleis gl) {
         this.gl = gl;
         this.element = gl.telement;
         this.richtung = gl.richtung;
         this.enr = gl.enr;
         if (gl.typAllowesSWwertedit()) {
            this.swwert = gl.swwert;
         } else {
            this.swwert = "";
         }
      }

      public boolean typAllowesENRedit() {
         return this.gl.typAllowesENRedit();
      }

      public boolean typAllowesSWwertedit() {
         return this.gl.typAllowesSWwertedit();
      }
   }

   public class nachbarGleis {
      public gleis gl;
      int sdp = 0;
      int ddp = 0;

      nachbarGleis(gleis gl, int sdp, int ddp) {
         this.gl = gl;
         this.sdp = sdp;
         this.ddp = ddp;
      }

      void init(gleis gl, int sdp, int ddp) {
         this.gl = gl;
         this.sdp = sdp;
         this.ddp = ddp;
      }
   }

   class nachbarMgnt {
      private final CopyOnWriteArrayList<gleis> nachbar1 = new CopyOnWriteArrayList();
      private final CopyOnWriteArrayList<gleis> nachbar2 = new CopyOnWriteArrayList();
      private final ConcurrentLinkedQueue<gleis.nachbarGleis> nachbarExtra = new ConcurrentLinkedQueue();
      private final ConcurrentLinkedQueue<gleis.nachbarGleis> nachbarCache = new ConcurrentLinkedQueue();

      private CopyOnWriteArrayList<gleis> c_() {
         return gleis.currentN == 0 ? this.nachbar1 : this.nachbar2;
      }

      private CopyOnWriteArrayList<gleis> b_() {
         return gleis.currentN == 0 ? this.nachbar2 : this.nachbar1;
      }

      void add(gleis g, int spd, int ddp) {
         this.c_().add(g);
         if (gleis.currentN == -1) {
            this.b_().add(g);
         }

         gleis.nachbarGleis ng = (gleis.nachbarGleis)this.nachbarCache.poll();
         if (ng == null) {
            ng = gleis.this.new nachbarGleis(g, spd, ddp);
         } else {
            ng.init(g, spd, ddp);
         }

         this.nachbarExtra.add(ng);
      }

      void close() {
         this.nachbar1.clear();
         this.nachbar2.clear();
         this.nachbarCache.clear();
         this.nachbarExtra.clear();
      }

      void clear() {
         this.c_().clear();
         if (gleis.currentN == -1) {
            this.b_().clear();
         }

         this.nachbarCache.addAll(this.nachbarExtra);
         this.nachbarExtra.clear();
      }

      boolean contains(gleis g) {
         return this.c_().contains(g);
      }

      int size() {
         return this.b_().size();
      }

      gleis get(int p) {
         return (gleis)this.b_().get(p);
      }

      Iterator<gleis> iterator() {
         return this.b_().iterator();
      }

      Iterator<gleis.nachbarGleis> e_iterator() {
         return this.nachbarExtra.iterator();
      }
   }
}
