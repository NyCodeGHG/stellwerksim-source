package js.java.isolate.sim.gleisbild;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.colorSystem.colorStruct;
import js.java.isolate.sim.gleis.colorSystem.gleisColor;
import js.java.isolate.sim.gleis.displayBar.displayBar;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleis.mass.massBase;
import js.java.isolate.sim.gleis.mass.massLenClassic;
import js.java.isolate.sim.gleis.mass.massLenNextGen;
import js.java.isolate.sim.gleis.mass.massLenRevised;
import js.java.isolate.sim.gleisbild.gleisbildWorker.bstflaecheAllConnectionSearch;
import js.java.isolate.sim.gleisbild.gleisbildWorker.bstflaecheOtherConnectionSearch;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.AlphanumComparator;
import js.java.tools.actions.AbstractListener;
import js.java.tools.actions.ListenerList;

public class gleisbildModel implements Iterable<gleis>, SessionClose {
   public gleisbild_extend gleisbildextend = new gleisbild_extend();
   protected final GleisAdapter theapplet;
   private final CopyOnWriteArrayList<CopyOnWriteArrayList<gleis>> rows = new CopyOnWriteArrayList();
   private int width = 0;
   private int height = 0;
   private final CopyOnWriteArraySet<gleis> markedGleis = new CopyOnWriteArraySet();
   private final CopyOnWriteArraySet<gleis> highlightedGleis = new CopyOnWriteArraySet();
   private final CopyOnWriteArraySet<gleis> rolloverGleis = new CopyOnWriteArraySet();
   private gleis selectedGleis = null;
   private final CopyOnWriteArraySet<element> disabledLayers = new CopyOnWriteArraySet();
   private gleis focusGleis = null;
   private int aid = 0;
   private String anlagenname = "";
   private int rid = 0;
   private String region = "";
   private final displayBar disBar;
   private gleisColor gColor;
   private ListenerList<StructureChangeEvent> gleisChangeListeners = new ListenerList();
   private massBase masstabCalc = new massLenClassic();
   private int masstabName = -1;
   public static final int C_GLEISTYP = 2;
   public static final int C_GLEISRICHTUNG = 1;
   public static final int C_GLEISELEMENT = 1;
   public static final int C_ENR = 5;
   public static final int C_SWWERT = 10;
   public static final int C_RENEWALLENR = 50;
   public static final int C_FWCHANGE = 1;
   public static final int C_STOERUNGNEU = 1;
   public static final int C_STOERUNGDEL = 1;
   public static final int C_LINEELEMENT = 1;
   public static final int C_BLOCKCLEAR = 1;
   public static final int C_DISPLAYBARADD = 6;
   public static final int C_DISPLAYBARDEL = 4;
   private long changecounter = 0L;
   private BitSet usedenr = new BitSet();
   public static final int MAXSIZE = 10000;

   public void addStructureChangeListener(AbstractListener<StructureChangeEvent> l) {
      this.gleisChangeListeners.addListener(l);
   }

   public void removeStructureChangeListener(AbstractListener<StructureChangeEvent> l) {
      this.gleisChangeListeners.removeListener(l);
   }

   public gleisbildModel(GleisAdapter _theapplet) {
      super();
      this.theapplet = _theapplet;
      this.gColor = gleisColor.getInstance();
      this.gColor.setNormalColor();
      this.disBar = new displayBar(this, _theapplet);
   }

   @Override
   public void close() {
      this.rolloverGleis.clear();
      this.highlightedGleis.clear();
      if (this.selectedGleis != null) {
         this.selectedGleis.clear();
      }

      this.disabledLayers.clear();
      this.markedGleis.clear();

      for(gleis g : this) {
         g.close();
      }

      this.rows.stream().forEach(CopyOnWriteArrayList::clear);
      this.rows.clear();
      this.disBar.clear();
      this.gColor.close();
      this.gColor = null;
   }

   public GleisAdapter getAdapter() {
      return this.theapplet;
   }

   public colorStruct getColor() {
      return this.gColor.getColor();
   }

   public Iterator<gleis> iterator() {
      return new gleisbildModel.gbIterator();
   }

   public Iterator<gleis> iterator(gleis start) {
      return new gleisbildModel.gbIterator(start);
   }

   public Iterator<gleis> findIterator(Object... search) {
      Iterator<gleis> it = this.findStart(search);
      LinkedList s = this.extractFindPart(Integer.class, search);
      if (!s.isEmpty()) {
         it = new gleisbildModel.findgIterator(it, s) {
            @Override
            protected boolean compare(gleis g, Object s) {
               Integer i = (Integer)s;
               return g.getENR() == i;
            }
         };
      }

      s = this.extractFindPart(element.class, search);
      if (!s.isEmpty()) {
         it = new gleisbildModel.findgIterator(it, s) {
            @Override
            protected boolean compare(gleis g, Object s) {
               element e = (element)s;
               return e.matches(g.getElement());
            }
         };
      }

      s = this.extractFindPart(String.class, search);
      if (!s.isEmpty()) {
         it = new gleisbildModel.findgIterator(it, s) {
            @Override
            protected boolean compare(gleis g, Object s) {
               String sw = (String)s;
               return g.getSWWert().equalsIgnoreCase(sw);
            }
         };
      }

      return it;
   }

   public Iterator<gleis> findIteratorWithElementName(String elementname, Object... search) {
      Iterator<gleis> it = this.findStart(search);
      return new gleisbildModel.findElementNameIterator(it, elementname);
   }

   public static LinkedList<gleis> iterator2list(Iterator<gleis> it) {
      LinkedList<gleis> ret = new LinkedList();

      while(it.hasNext()) {
         ret.add(it.next());
      }

      return ret;
   }

   public gleis findFirst(Object... search) {
      Iterator<gleis> it = this.findIterator(search);
      return it.hasNext() ? (gleis)it.next() : null;
   }

   private void searchSyntaxTest() {
      this.findIterator(1, 5);
      int[] e = new int[]{1, 5};
      this.findIterator(e, 4, 1);
   }

   private Iterator<gleis> findStart(Object... search) {
      gleis start = null;

      for(Object o : search) {
         if (o instanceof gleis) {
            start = (gleis)o;
            break;
         }
      }

      return this.iterator(start);
   }

   private LinkedList extractFindPart(Class c, Object... search) {
      LinkedList s = new LinkedList();

      for(Object o : search) {
         if (o.getClass().isArray()) {
            for(Object oo : o) {
               if (c.isInstance(oo)) {
                  s.add(oo);
               }
            }
         } else if (c.isInstance(o)) {
            s.add(o);
         }
      }

      return s;
   }

   public void repaint() {
      this.structureChanged();
   }

   protected void structureChanged() {
      this.gleisChangeListeners.fireEvent(new StructureChangeEvent(this, true));
   }

   protected void opticalChanged(int level) {
      this.gleisChangeListeners.fireEvent(new StructureChangeEvent(this, level > 1));
   }

   public void clear() {
      this.disabledLayers.clear();
      this.clearMarkedGleis();
      this.clearHighlightedGleis();

      for(gleis gl : this) {
         gl.init();
         gl.resetExtend();
      }

      this.disBar.clear();
      this.changecounter = 0L;
      this.structureChanged();
   }

   public void smallClearStatus() {
      for(gleis gl : this) {
         gl.createName();
         gl.getFluentData().setStatus(0);
         gl.getFluentData().setStellung(gl.getFluentData().getInitialStellung());
      }
   }

   public void smallFreeStatus() {
      for(gleis gl : this) {
         gl.getFluentData().setStatus(0);
      }
   }

   public gleis getSelectedGleis() {
      return this.selectedGleis;
   }

   public void setSelectedGleis(gleis g) {
      this.setSelectedGleis(g, false);
   }

   public void setSelectedGleis(gleis g, boolean storeValue) {
      if (storeValue && this.selectedGleis != null) {
         this.selectedGleis.readGUI();
         this.structureChanged();
      }

      this.selectedGleis = g;
      if (this.selectedGleis != null) {
         this.clearHighlightedGleis();
         this.selectedGleis.setGUI();
      }
   }

   public void setGleisValues(gleis gl) {
      gl.readGUI();
      if (gl.requiresENR()) {
         int m = this.nextENR();
         gl.setENR(m);
      }

      this.structureChanged();
   }

   public void ch_gleis_values() {
      if (this.selectedGleis != null) {
         this.selectedGleis.readGUI();
         if (this.selectedGleis.requiresENR()) {
            int m = this.nextENR();
            this.selectedGleis.setENR(m);
         }

         this.selectedGleis.setGUI();
         this.structureChanged();
      }
   }

   public void clearMarkedGleis() {
      this.markedGleis.clear();
      this.opticalChanged(1);
   }

   public void addMarkedGleis(gleis g) {
      if (g != null) {
         this.markedGleis.add(g);
         this.opticalChanged(1);
      }
   }

   public void addMarkedGleis(List<gleis> gl) {
      if (gl != null) {
         for(gleis g : gl) {
            this.markedGleis.add(g);
         }
      }

      this.opticalChanged(1);
   }

   public Set<gleis> getMarkedGleis() {
      return this.markedGleis;
   }

   public void removeMarkedGleis(gleis g) {
      if (g != null) {
         this.markedGleis.remove(g);
         this.opticalChanged(1);
      }
   }

   public void clearHighlightedGleis() {
      this.highlightedGleis.clear();
      this.opticalChanged(2);
   }

   public void addHighlightedGleis(gleis gl) {
      this.setSelectedGleis(null);
      this.highlightedGleis.add(gl);
      this.opticalChanged(2);
   }

   public void addHighlightedGleis(List<gleis> gl) {
      this.setSelectedGleis(null);
      if (gl != null) {
         for(gleis g : gl) {
            this.highlightedGleis.add(g);
         }
      }

      this.opticalChanged(2);
   }

   public Set<gleis> getHighlightedGleis() {
      return this.highlightedGleis;
   }

   public void clearRolloverGleis() {
      this.rolloverGleis.clear();
      this.opticalChanged(0);
   }

   public void addRolloverGleis(gleis gl) {
      this.rolloverGleis.add(gl);
      this.opticalChanged(0);
   }

   public void addRolloverGleis(List<gleis> gl) {
      if (gl != null) {
         for(gleis g : gl) {
            this.rolloverGleis.add(g);
         }
      }

      this.opticalChanged(0);
   }

   public Set<gleis> getRolloverGleis() {
      return this.rolloverGleis;
   }

   public void setFocus(gleis gl) {
      boolean changed = this.focusGleis != gl;
      this.focusGleis = gl;
      if (changed) {
         this.opticalChanged(0);
      }
   }

   public gleis getFocus() {
      return this.focusGleis;
   }

   public boolean isLayerDisabled(element element) {
      return this.disabledLayers.contains(element);
   }

   public void setLayerDisabled(element element, boolean disabled) {
      if (disabled) {
         this.disabledLayers.add(element);
         this.structureChanged();
      } else {
         this.disabledLayers.remove(element);
         this.structureChanged();
      }
   }

   public void clearDisabledLayers() {
      this.disabledLayers.clear();
   }

   public void allOff() {
      this.setSelectedGleis(null, true);
      this.clearDisabledLayers();
      this.clearHighlightedGleis();
      this.clearMarkedGleis();
      this.clearRolloverGleis();
      this.setFocus(null);
   }

   protected gleis createGleis() {
      return new gleis(this.theapplet, this);
   }

   public int getGleisWidth() {
      return this.width;
   }

   public int getGleisHeight() {
      return this.height;
   }

   public String getAnlagenname() {
      return this.anlagenname;
   }

   public int getAid() {
      return this.aid;
   }

   protected void setAid(int a) {
      this.aid = a;
   }

   public String getRegion() {
      return this.region;
   }

   public int getRid() {
      return this.rid;
   }

   protected void setRegion(String region, int r) {
      this.region = region;
      this.rid = r;
   }

   protected void setAnlagenname(String name) {
      this.anlagenname = name;
   }

   public displayBar getDisplayBar() {
      return this.disBar;
   }

   public massBase getMasstabCalculator() {
      if (this.masstabCalc == null || this.masstabName != this.gleisbildextend.getMasstabName()) {
         this.masstabName = this.gleisbildextend.getMasstabName();
         this.masstabCalc = createMasstabCalculator(this.masstabName);
      }

      return this.masstabCalc;
   }

   public static massBase createMasstabCalculator(int masstabName) {
      massBase masstabCalc;
      switch(masstabName) {
         case 0:
         default:
            masstabCalc = new massLenClassic();
            break;
         case 1:
            masstabCalc = new massLenRevised();
            break;
         case 2:
            masstabCalc = new massLenNextGen();
      }

      return masstabCalc;
   }

   public int getMasstabCalculatorName() {
      return this.gleisbildextend.getMasstabName();
   }

   public boolean isMasstabCalculatorCompatible(int name) {
      massBase temp = createMasstabCalculator(name);
      return this.masstabCalc != null ? temp.isCompatible(this.masstabCalc) : true;
   }

   public void setMasstabCalculator(int name) {
      massBase temp = createMasstabCalculator(name);
      if (this.masstabCalc != null && !temp.isCompatible(this.masstabCalc)) {
         this.clearMasstab();
      }

      this.masstabName = this.gleisbildextend.getMasstabName();
      this.masstabCalc = temp;
      this.gleisbildextend.setMasstabName(name);
   }

   public static LinkedHashMap<String, Integer> getMasstabNames(boolean fullText) {
      LinkedHashMap<String, Integer> ret = new LinkedHashMap();
      if (fullText) {
         ret.put("Klassisch (fehlerhafte Längen)", 0);
         ret.put("Überarbeitet (unpassendes Tempo)", 1);
      } else {
         ret.put("Klassisch", 0);
         ret.put("Überarbeitet", 1);
      }

      return ret;
   }

   protected void clearMasstab() {
      for(gleis gl : this) {
         gl.setMasstab(0);
      }
   }

   protected void resetChangeC() {
      this.changecounter = 0L;
   }

   public long getChangeC() {
      return this.changecounter;
   }

   public void changeC(int v) {
      this.changecounter += (long)v;
   }

   public int changeC(int newv, int oldv, int v) {
      if (newv != oldv) {
         this.changecounter += (long)v;
      }

      return newv;
   }

   public String changeC(String newv, String oldv, int v) {
      if (!newv.equals(oldv)) {
         this.changecounter += (long)v;
      }

      return newv;
   }

   public gleisElements.RICHTUNG changeC(gleisElements.RICHTUNG newv, gleisElements.RICHTUNG oldv, int v) {
      if (newv != oldv) {
         this.changecounter += (long)v;
      }

      return newv;
   }

   private int nextENR() {
      int m = 0;
      this.usedenr.clear();

      for(gleis gl2 : this) {
         m = gl2.getENR();
         if (m > 0) {
            this.usedenr.set(m);
         }
      }

      return this.usedenr.nextClearBit(1);
   }

   public int getFreeENR() {
      BitSet enrset = new BitSet();

      for(gleis gl : this) {
         if (gl.getENR() > 0) {
            enrset.set(gl.getENR());
         }
      }

      return enrset.nextClearBit(1);
   }

   public BitSet getENRbitset() {
      BitSet enrset = new BitSet();

      for(gleis gl : this) {
         if (gl.getENR() > 0) {
            enrset.set(gl.getENR());
         }
      }

      return enrset;
   }

   public int gl_overmaxsize(int w, int h) {
      return w * h - 10000;
   }

   public int gl_overmaxsize() {
      return this.width * this.height - 10000;
   }

   private void resizeRow(CopyOnWriteArrayList<gleis> r, int newwidth) {
      while(r.size() < newwidth) {
         gleis gl = this.createGleis();
         r.add(gl);
      }

      while(r.size() > newwidth) {
         r.remove(r.size() - 1);
      }
   }

   public void gl_resize(int newwidth, int newheight) {
      this.allOff();
      this.gl_simpleresize(newwidth, newheight);
   }

   public void gl_simpleresize(int newwidth, int newheight) {
      if (newheight <= 0) {
         newheight = 1;
      }

      if (newwidth <= 0) {
         newwidth = 1;
      }

      if (this.rows.size() < newheight) {
         while(this.rows.size() < newheight) {
            CopyOnWriteArrayList<gleis> r = new CopyOnWriteArrayList();
            this.resizeRow(r, newwidth);
            this.rows.add(r);
         }
      } else if (this.rows.size() > newheight) {
         this.height = newheight;

         while(this.rows.size() > newheight) {
            this.rows.remove(this.rows.size() - 1);
         }
      }

      if (this.width != newwidth) {
         if (newwidth < this.width) {
            this.width = newwidth;
         }

         for(CopyOnWriteArrayList<gleis> r : this.rows) {
            this.resizeRow(r, newwidth);
         }
      }

      this.width = newwidth;
      this.height = newheight;
      this.setXYonGleis();
      this.structureChanged();
   }

   protected void setXYonGleis() {
      for(int y = 0; y < this.rows.size(); ++y) {
         CopyOnWriteArrayList<gleis> row = (CopyOnWriteArrayList)this.rows.get(y);

         for(int x = 0; x < row.size(); ++x) {
            gleis gl = (gleis)row.get(x);
            gl.setXY(x, y);
         }
      }
   }

   public void insertRow(int r) {
      CopyOnWriteArrayList<gleis> emptyr = new CopyOnWriteArrayList();
      this.resizeRow(emptyr, this.width);
      this.rows.add(emptyr);

      for(int y = this.rows.size() - 1; y > r; --y) {
         this.rows.set(y, this.rows.get(y - 1));
      }

      this.rows.set(r, emptyr);
      this.changeC(this.width / 10);
      ++this.height;
      this.setXYonGleis();
      this.structureChanged();
   }

   public void insertColumn(int c) {
      for(CopyOnWriteArrayList<gleis> col : this.rows) {
         gleis gl = this.createGleis();
         col.add(gl);

         for(int x = col.size() - 1; x > c; --x) {
            col.set(x, col.get(x - 1));
         }

         col.set(c, gl);
      }

      this.changeC(this.height / 10);
      ++this.width;
      this.setXYonGleis();
      this.structureChanged();
   }

   public void deleteRow(int r) {
      this.rows.remove(r);
      this.changeC(this.width / 10);
      --this.height;
      this.setXYonGleis();
      this.structureChanged();
   }

   public void deleteColumn(int c) {
      for(CopyOnWriteArrayList<gleis> col : this.rows) {
         col.remove(c);
      }

      this.changeC(this.height / 10);
      --this.width;
      this.setXYonGleis();
      this.structureChanged();
   }

   public void drawline(List<gleis> line, String color, boolean drawOver) {
      for(gleis gl : line) {
         if (drawOver || gl.getElement() == gleis.ELEMENT_LEER) {
            gl.init(gleis.ELEMENT_STRECKE, gleisElements.RICHTUNG.right, 0, "", 0);
            gl.setExtendFarbe(color);
            this.changeC(1);
         }
      }

      this.structureChanged();
   }

   public LinkedList<gleis> makeLine(int x1, int y1, int x2, int y2) {
      return this.mkline(x1, y1, x2, y2, false);
   }

   private LinkedList<gleis> mkline(int x1, int y1, int x2, int y2, boolean swapped) {
      LinkedList<gleis> ret = new LinkedList();
      int dx = Math.abs(x2 - x1);
      int dy = Math.abs(y2 - y1);
      if (dx == 0 && dy == 0) {
         return ret;
      } else if (dx < dy) {
         return swapped ? ret : this.mkline(y1, x1, y2, x2, true);
      } else if (x2 < x1) {
         return this.mkline(x2, y2, x1, y1, swapped);
      } else {
         int aq = dy - 1;
         int x11 = (dx - aq) / 2;
         int rounder = x11 * 2 == dx - aq ? 0 : 1;
         int mul = 1;
         if (y2 < y1) {
            mul = -1;
         }

         for(int x = 0; x < x11; ++x) {
            int kx = x1 + x;
            gleis gl;
            if (swapped) {
               gl = this.getXY_null(y1, kx);
            } else {
               gl = this.getXY_null(kx, y1);
            }

            if (gl != null) {
               ret.add(gl);
            }
         }

         for(int x = 0; x < aq; ++x) {
            int kx = x1 + x11 + x;
            int ky = y1 + (x + 1) * mul;
            gleis gl;
            if (swapped) {
               gl = this.getXY_null(ky, kx);
            } else {
               gl = this.getXY_null(kx, ky);
            }

            if (gl != null) {
               ret.add(gl);
            }
         }

         for(int x = x2 - x11 - rounder; x <= x2; ++x) {
            gleis gl;
            if (swapped) {
               gl = this.getXY_null(y2, x);
            } else {
               gl = this.getXY_null(x, y2);
            }

            if (gl != null) {
               ret.add(gl);
            }
         }

         return ret;
      }
   }

   public void startSwapOp() {
   }

   public void endSwapOp() {
      this.setXYonGleis();
      this.structureChanged();
   }

   public void swap(gleis g1, gleis g2) {
      int x1 = g1.getCol();
      int y1 = g1.getRow();
      int x2 = g2.getCol();
      int y2 = g2.getRow();
      ((CopyOnWriteArrayList)this.rows.get(y1)).set(x1, g2);
      ((CopyOnWriteArrayList)this.rows.get(y2)).set(x2, g1);
      g1.setXY(y2, y2);
      g2.setXY(x1, y1);
   }

   public gleis getXY_null(int x, int y) {
      gleis g = null;

      try {
         g = (gleis)((CopyOnWriteArrayList)this.rows.get(y)).get(x);
      } catch (NullPointerException | ArrayIndexOutOfBoundsException var5) {
      }

      return g;
   }

   public gleis getXY(int x, int y) {
      gleis g = this.getXY_null(x, y);
      if (g == null) {
         g = this.createGleis();
      }

      return g;
   }

   public LinkedList<gleis> findAllWithStellungLevel(int mincntlevel, Object... search) {
      LinkedList<gleis> ll = new LinkedList();
      Iterator<gleis> it = this.findIterator(search);

      while(it.hasNext()) {
         gleis gl2 = (gleis)it.next();
         if (gl2.getCntStellung() > (long)mincntlevel) {
            ll.add(gl2);
         }
      }

      return ll;
   }

   public LinkedList<gleis> findAllWithZugLevel(int mincntlevel, Object... search) {
      LinkedList<gleis> ll = new LinkedList();
      Iterator<gleis> it = this.findIterator(search);

      while(it.hasNext()) {
         gleis gl2 = (gleis)it.next();
         if (gl2.getCntZug() > (long)mincntlevel) {
            ll.add(gl2);
         }
      }

      return ll;
   }

   public LinkedList<gleis> findBahnsteig(String name) {
      Iterator<gleis> it = this.findIterator(gleis.ALLE_BAHNSTEIGE, name);
      LinkedList<gleis> ret = new LinkedList();

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         ret.add(gl);
      }

      return ret;
   }

   public TreeMap<String, Integer> getAlleOfType(element e) {
      Iterator<gleis> it = this.findIterator(e);
      TreeMap<String, Integer> ret = new TreeMap();

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         ret.put(gl.getSWWert(), gl.getENR());
      }

      return ret;
   }

   @Deprecated
   public TreeSet<String> findNeighborBahnsteig(gleis g1) {
      LinkedList<gleis> r1 = this.findNeighbor(g1, gleis.ELEMENT_BAHNSTEIG, gleis.ALLE_BSTTRENNER, false);
      TreeSet<String> ret = new TreeSet(new AlphanumComparator());

      for(gleis gl : r1) {
         ret.add(gl.getSWWert());
      }

      return ret;
   }

   public TreeSet<String> findNeighborBahnsteig(Collection<gleis> lgl) {
      return this.findNeighborBahnsteig(lgl, false);
   }

   public TreeSet<String> findNeighborBahnsteig(Collection<gleis> lgl, boolean mark) {
      TreeSet<String> ret = new TreeSet(new AlphanumComparator());

      for(gleis g1 : lgl) {
         for(gleis gl : this.findNeighbor(g1, gleis.ELEMENT_BAHNSTEIG, gleis.ALLE_BSTTRENNER, mark)) {
            ret.add(gl.getSWWert());
         }
      }

      return ret;
   }

   @Deprecated
   public boolean isNeighborBahnsteigOf(gleis g1, gleis g2) {
      return this.isNeighborOf(g1, g2, gleis.ELEMENT_BAHNSTEIG, gleis.ALLE_BSTTRENNER, false);
   }

   @Deprecated
   public boolean isNeighborBahnsteigOf(Collection<gleis> g1, gleis g2) {
      boolean r = false;

      for(gleis gl : g1) {
         r |= this.isNeighborOf(gl, g2, gleis.ELEMENT_BAHNSTEIG, gleis.ALLE_BSTTRENNER, false);
      }

      return r;
   }

   @Deprecated
   public gleis findConnectedBahnsteig(String name, String other) {
      for(gleis b : this.findBahnsteig(name)) {
         bstflaecheOtherConnectionSearch bscs = new bstflaecheOtherConnectionSearch(this, this.theapplet, b, name, other, false);
         gleis match = bscs.find();
         if (match != null) {
            return match;
         }
      }

      return null;
   }

   public Set<gleis> findAllConnectedBahnsteig(String name, boolean highlight) {
      return this.findAllConnectedBahnsteig(this.findBahnsteig(name), highlight);
   }

   public Set<gleis> findAllConnectedBahnsteig(Collection<gleis> bst, boolean highlight) {
      HashSet<gleis> ret = new HashSet();

      for(gleis b : bst) {
         bstflaecheAllConnectionSearch bscs = new bstflaecheAllConnectionSearch(this, this.theapplet, b, b.getSWWert(), highlight);
         bscs.find();
         Set<gleis> match1 = bscs.getResult();
         if (match1 != null) {
            ret.addAll(match1);
         }
      }

      return ret;
   }

   private int getLookWidth() {
      return this.gleisbildextend.getNachbarbahnsteigLookupWidth();
   }

   public LinkedList<gleis> findNeighbor(gleis g1, element element, element stoptyp, boolean mark) {
      int yd = 1;
      LinkedList<gleis> ret = new LinkedList();
      if (g1 != null && g1.getElement() == element) {
         ret.add(g1);
         int yr = 1;
         int leerc = 0;
         int lockWidth = this.getLookWidth();

         while(true) {
            int y = g1.getRow() + yr;
            boolean leerb = true;
            if (y >= 0 && y < this.rows.size()) {
               for(int xr = -lockWidth; xr <= lockWidth; ++xr) {
                  gleis gl = this.getXY_null(g1.getCol() + xr, y);
                  if (gl != null) {
                     if (gleis.ALLE_GLEISE.matches(gl.getElement())) {
                        leerb = false;
                        if (gl.getElement() == element && gl.getRichtung().compareTo(g1.getRichtung()) == 0 && g1.compareTo(gl) != 0) {
                           ret.add(gl);
                        }
                     } else if (gleis.ELEMENT_BAHNSTEIGFLÄCHE.matches(gl.getElement())) {
                        leerb = false;
                     } else if (stoptyp.matches(gl.getElement())) {
                        leerb = true;
                        leerc = 99;
                        break;
                     }

                     if (mark) {
                        this.addMarkedGleis(gl);
                     }
                  }
               }
            } else {
               leerc = 99;
            }

            if (leerb) {
               ++leerc;
            } else {
               leerc = 0;
            }

            if (leerc >= this.gleisbildextend.getNachbarbahnsteighoriz()) {
               if (yd != 1) {
                  return ret;
               }

               yd = -1;
               yr = 0;
               leerc = 0;
            }

            yr += yd;
         }
      } else {
         return ret;
      }
   }

   public boolean isNeighborOf(gleis g1, gleis g2, element element, element stoptyp, boolean ignoreRichtung) {
      int yd = 1;
      if (g1 != null && g2 != null && g1.getElement() == element) {
         int yr = 1;
         int leerc = 0;
         int lookWidth = this.getLookWidth();

         while(true) {
            int y = g1.getRow() + yr;
            boolean leerb = true;
            if (y >= 0 && y < this.rows.size()) {
               for(int xr = -lookWidth; xr <= lookWidth; ++xr) {
                  gleis gl = this.getXY_null(g1.getCol() + xr, y);
                  if (gl != null) {
                     if (gleis.ALLE_GLEISE.matches(gl.getElement())) {
                        leerb = false;
                        if (gl.getElement() == element && (ignoreRichtung || gl.getRichtung().compareTo(g1.getRichtung()) == 0) && g2.compareTo(gl) == 0) {
                           return true;
                        }
                     } else if (gleis.ELEMENT_BAHNSTEIGFLÄCHE.matches(gl.getElement())) {
                        leerb = false;
                     } else if (stoptyp.matches(gl.getElement())) {
                        leerb = true;
                        leerc = 99;
                        break;
                     }
                  }
               }
            } else {
               leerc = 99;
            }

            if (leerb) {
               ++leerc;
            } else {
               leerc = 0;
            }

            if (leerc > 4) {
               if (yd != 1) {
                  return false;
               }

               yd = -1;
               yr = 0;
               leerc = 0;
            }

            yr += yd;
         }
      } else {
         return false;
      }
   }

   private static class findElementNameIterator extends gleisbildModel.findgIterator {
      private final String elementname;

      findElementNameIterator(Iterator<gleis> it, String elementname) {
         super(it);
         this.elementname = elementname;
      }

      @Override
      protected boolean compare(gleis g, Object s) {
         return g.getElementName().equalsIgnoreCase(this.elementname) || g.getShortElementName().equalsIgnoreCase(this.elementname);
      }
   }

   private abstract static class findgIterator implements Iterator<gleis> {
      private final Iterator<gleis> parent;
      private gleis current = null;
      private final LinkedList search;

      findgIterator(Iterator<gleis> parent, LinkedList search) {
         super();
         this.parent = parent;
         this.search = search;
      }

      findgIterator(Iterator<gleis> parent) {
         super();
         this.parent = parent;
         this.search = null;
      }

      protected abstract boolean compare(gleis var1, Object var2);

      public boolean hasNext() {
         label26:
         while(this.parent.hasNext()) {
            this.current = (gleis)this.parent.next();
            if (this.search == null) {
               if (this.compare(this.current, null)) {
                  return true;
               }
            } else {
               Iterator var1 = this.search.iterator();

               Object e;
               do {
                  if (!var1.hasNext()) {
                     continue label26;
                  }

                  e = var1.next();
               } while(!this.compare(this.current, e));

               return true;
            }
         }

         return false;
      }

      public gleis next() {
         return this.current;
      }

      public void remove() {
         throw new UnsupportedOperationException("Can't remove.");
      }
   }

   private class gbIterator implements Iterator<gleis> {
      int crow;
      int ccol;

      gbIterator() {
         super();
         this.crow = 0;
         this.ccol = 0;
      }

      gbIterator(gleis start) {
         super();

         try {
            this.crow = start.getRow();
            this.ccol = start.getCol();
            ++this.ccol;
            if (this.ccol >= gleisbildModel.this.width) {
               this.ccol = 0;
               ++this.crow;
            }
         } catch (NullPointerException var4) {
            this.crow = 0;
            this.ccol = 0;
         }
      }

      public boolean hasNext() {
         return this.crow < gleisbildModel.this.height - 1 || this.crow == gleisbildModel.this.height - 1 && this.ccol < gleisbildModel.this.width;
      }

      public gleis next() {
         int c = this.ccol;
         int r = this.crow;
         ++this.ccol;
         if (this.ccol >= gleisbildModel.this.width) {
            this.ccol = 0;
            ++this.crow;
         }

         if (c >= gleisbildModel.this.width) {
            c = 0;
            if (++r >= gleisbildModel.this.height) {
               System.out.println("error");
            }
         }

         return (gleis)((CopyOnWriteArrayList)gleisbildModel.this.rows.get(r)).get(c);
      }

      public void remove() {
         throw new UnsupportedOperationException("Can't remove.");
      }
   }
}
