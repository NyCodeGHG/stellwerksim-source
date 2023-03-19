package js.java.isolate.sim.eventsys;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;
import js.java.isolate.sim.structServ.structinfo;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.TextHelper;
import org.xml.sax.Attributes;

public class eventContainer implements Comparable, structinfo, SessionClose {
   static FATwriter debugMode = null;
   protected HashMap<eventContainer.dataItem, String> values = new HashMap();
   protected HashMap<eventContainer.dataItem, String> editvalues = new HashMap();
   protected HashMap<eventContainer.dataItem, String> workingvalues = this.values;
   private String typ = null;
   private String name = "";
   private String ename = "";
   private gleis zielgleis = null;
   private HashSet<gleis> gleislist = new HashSet();
   private HashSet<gleis> wgleislist = new HashSet();
   private gleisbildModelEventsys glbModel;
   private eventFactory ev = null;
   private boolean editMode = false;
   event runningEvent = null;
   private boolean allowUse = true;
   private boolean runOnlyOnce = false;
   static Random rnd = null;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static FATwriter getDebug() {
      return debugMode;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public eventContainer(gleisbildModelEventsys glb, Attributes attrs) {
      super();
      this.glbModel = glb;
      glb.events.addLast(this);
      this.name = "unbenannte Störung " + (glb.events.indexOf(this) + 1);
   }

   public eventContainer(gleisbildModelEventsys glb) {
      super();
      this.glbModel = glb;
      this.name = "habsNichtGeschafftDenNamenZuAendern." + (glb.events.size() + 1);
      glb.events.addLast(this);
      this.glbModel.changeC(1);
   }

   public eventContainer(gleisbildModelEventsys glb, Simulator sim, String t, boolean force) {
      super();
      this.glbModel = glb;
      this.name = "Störung " + this.hashCode();
      String pa = "";
      int p = t.indexOf(40);
      if (p >= 0) {
         pa = t.substring(p);
         pa = pa.substring(1, pa.length() - 1);
         t = t.substring(0, p);
      }

      this.typ = t;
      if (this.factoryByTyp() && (this.ev.serverEvent(this, this.glbModel, pa) || force)) {
         event.createEvent(this, this.glbModel, sim);
      }
   }

   public eventContainer(gleisbildModelEventsys glb, Class t) {
      super();
      this.glbModel = glb;
      this.name = "Störung " + this.hashCode() + " C" + t.getName();
      this.typ = t.getSimpleName();
      this.factoryByTyp();
   }

   @Override
   public void close() {
      this.editvalues.clear();
      this.gleislist.clear();
      this.workingvalues.clear();
      this.zielgleis = null;
   }

   public void setEditMode(boolean b) {
      if (b && !this.editMode) {
         this.editvalues.clear();
         this.editvalues.putAll(this.values);
         this.ename = this.name;
      }

      this.editMode = b;
      if (this.editMode) {
         this.workingvalues = this.editvalues;
         this.wgleislist.clear();
         this.wgleislist.addAll(this.gleislist);
      } else {
         this.workingvalues = this.values;
      }
   }

   private boolean factoryByTyp() {
      if (this.typ != null) {
         if (debugMode != null) {
            debugMode.writeln("factory for: " + this.typ);
         }

         try {
            Class c = Class.forName("js.java.isolate.sim.eventsys.events." + this.typ + "_factory");
            Object o = c.newInstance();
            this.ev = (eventFactory)o;
            this.ev.init(this.glbModel);
            return true;
         } catch (ClassNotFoundException var3) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "no factory found: " + this.typ, var3);
         } catch (IllegalAccessException var4) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "no factory found (ill): " + this.typ, var4);
         } catch (InstantiationException var5) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "no factory found (inst): " + this.typ, var5);
         }
      }

      return false;
   }

   public void addValue(Attributes attrs, String pcdata) {
      String k = attrs.getValue("key");
      if (k != null) {
         if (debugMode != null) {
            debugMode.writeln("key: " + k + " -> " + pcdata);
         }

         if (k.equals("typ")) {
            this.typ = pcdata;
            this.factoryByTyp();
         } else if (k.equals("name")) {
            this.name = pcdata;
            this.ename = this.name;
         } else if (k.equals("enr")) {
            this.zielgleis = this.glbModel.findFirst(new Object[]{Integer.parseInt(pcdata.trim())});
         } else if (k.equals("enrs")) {
            StringTokenizer tk = new StringTokenizer(pcdata.trim(), ",");

            while(tk.hasMoreTokens()) {
               String s = tk.nextToken();
               if (s != null && !s.isEmpty()) {
                  int v = Integer.parseInt(s);
                  if (v > 0) {
                     Iterator<gleis> it = this.glbModel.findIterator(new Object[]{v});

                     while(it.hasNext()) {
                        gleis g = (gleis)it.next();
                        this.gleislist.add(g);
                     }
                  }
               }
            }
         } else {
            this.values.put(new eventContainer.dataItem(k), pcdata);
         }
      }
   }

   public void remove() {
      this.glbModel.events.remove(this);
      this.glbModel.changeC(1);
   }

   public void setName(String _name) {
      if (!this.editMode) {
         this.name = _name.substring(0, Math.min(_name.length(), 20));
         this.ename = this.name;
      } else {
         this.ename = _name.substring(0, Math.min(_name.length(), 20));
      }
   }

   public String getName() {
      return this.editMode ? this.ename : this.name;
   }

   public eventFactory getFactory() {
      return this.ev;
   }

   public void setFactory(eventFactory f) {
      if (!this.editMode) {
         this.typ = f.getTyp();
         this.factoryByTyp();
      }
   }

   public String getTyp() {
      return this.ev != null ? this.ev.getTyp() : this.typ;
   }

   public int getENR() {
      return this.zielgleis != null ? this.zielgleis.getENR() : 0;
   }

   public void setGleis(gleis g) {
      if (!this.editMode) {
         this.zielgleis = g;
      }
   }

   public gleis getGleis() {
      return this.zielgleis;
   }

   public boolean isValue(String k) {
      return this.workingvalues.containsKey(new eventContainer.dataItem(k));
   }

   public String getValue(String k) {
      return (String)this.workingvalues.get(new eventContainer.dataItem(k));
   }

   public String getValue(gleis gl, String k) {
      return (String)this.workingvalues.get(new eventContainer.dataItem(k, gl));
   }

   public int getIntValue(String k) {
      try {
         return Integer.parseInt(((String)this.workingvalues.get(new eventContainer.dataItem(k))).trim());
      } catch (Exception var3) {
         return 0;
      }
   }

   public int getIntValue(String k, int defaultv) {
      try {
         return Integer.parseInt(((String)this.workingvalues.get(new eventContainer.dataItem(k))).trim());
      } catch (Exception var4) {
         return defaultv;
      }
   }

   public long getLongValue(String k) {
      try {
         return Long.parseLong(((String)this.workingvalues.get(new eventContainer.dataItem(k))).trim());
      } catch (Exception var3) {
         return 0L;
      }
   }

   public long getLongValue(String k, int defaultv) {
      try {
         return Long.parseLong(((String)this.workingvalues.get(new eventContainer.dataItem(k))).trim());
      } catch (Exception var4) {
         return (long)defaultv;
      }
   }

   public boolean getBoolValue(String k) {
      try {
         return Integer.parseInt(((String)this.workingvalues.get(new eventContainer.dataItem(k))).trim()) > 0;
      } catch (Exception var3) {
         return false;
      }
   }

   public boolean getBoolValue(String k, boolean defaultv) {
      try {
         return Integer.parseInt(((String)this.workingvalues.get(new eventContainer.dataItem(k))).trim()) > 0;
      } catch (Exception var4) {
         return defaultv;
      }
   }

   public Set<String> getThemeList(boolean excludelist) {
      return excludelist ? this.getListValue("excludetheme") : this.getListValue("includetheme");
   }

   public Set<String> getListValue(String k) {
      String b = this.getValue(k);
      HashSet<String> ret = new HashSet();
      if (b != null) {
         StringTokenizer tk = new StringTokenizer(b, ",");

         while(tk.hasMoreTokens()) {
            String s = tk.nextToken();
            if (s != null && !s.isEmpty()) {
               ret.add(s);
            }
         }
      }

      return ret;
   }

   public Set<gleis> getGleisList() {
      return this.editMode ? this.wgleislist : this.gleislist;
   }

   public void rmValue(String k) {
      this.workingvalues.remove(new eventContainer.dataItem(k));
   }

   public void rmValue(gleis gl, String k) {
      this.workingvalues.remove(new eventContainer.dataItem(k, gl));
   }

   public void setValue(String k, String v) {
      this.workingvalues.put(new eventContainer.dataItem(k), v);
   }

   public void setValue(gleis gl, String k, String v) {
      this.workingvalues.put(new eventContainer.dataItem(k, gl), v);
   }

   public void setValue(String k, int v) {
      this.workingvalues.put(new eventContainer.dataItem(k), v + "");
   }

   public void setValue(String k, long v) {
      this.workingvalues.put(new eventContainer.dataItem(k), v + "");
   }

   public void setValue(String k, boolean v) {
      this.workingvalues.put(new eventContainer.dataItem(k), (v ? 1 : 0) + "");
   }

   public void setIntValue(String k, int v) {
      this.workingvalues.put(new eventContainer.dataItem(k), v + "");
   }

   public void setLongValue(String k, long v) {
      this.workingvalues.put(new eventContainer.dataItem(k), v + "");
   }

   public void setThemeList(Set<String> v, boolean excludelist) {
      if (excludelist) {
         this.setListValue("excludetheme", v);
      } else {
         this.setListValue("includetheme", v);
      }
   }

   public void setListValue(String k, Set<String> v) {
      String b = "";

      for(String s : v) {
         b = b + s + ",";
      }

      if (!b.isEmpty()) {
         this.setValue(k, b);
      } else {
         this.rmValue(k);
      }
   }

   public void setGleisList(Set<gleis> gl) {
      if (this.editMode) {
         this.wgleislist.clear();
         this.wgleislist.addAll(gl);
      } else {
         this.gleislist.clear();
         this.gleislist.addAll(gl);
      }
   }

   public String toString() {
      return this.getTyp();
   }

   public void saveData(StringBuffer data, String prefix) {
      if (this.typ != null) {
         data.append(TextHelper.urlEncode(prefix + "[typ]"));
         data.append("=");
         data.append(TextHelper.urlEncode(this.typ));
         data.append("&");
         data.append(TextHelper.urlEncode(prefix + "[name]"));
         data.append("=");
         if (this.name == null || this.name.length() == 0) {
            this.name = "unbenannte Störung " + (this.glbModel.events.indexOf(this) + 1);
         }

         data.append(TextHelper.urlEncode(this.name));
         data.append("&");
         if (this.zielgleis != null) {
            data.append(TextHelper.urlEncode(prefix + "[enr]"));
            data.append("=");
            data.append(TextHelper.urlEncode(this.zielgleis.getENR() + ""));
            data.append("&");
         }

         if (!this.gleislist.isEmpty()) {
            String s = "";

            for(gleis g : this.gleislist) {
               if (g.getENR() > 0) {
                  if (!s.isEmpty()) {
                     s = s + ",";
                  }

                  s = s + g.getENR() + "";
               }
            }

            if (!s.isEmpty()) {
               data.append(TextHelper.urlEncode(prefix + "[enrs]"));
               data.append("=");
               data.append(TextHelper.urlEncode(s));
               data.append("&");
            }
         }

         for(eventContainer.dataItem k : this.values.keySet()) {
            if (this.values.get(k) != null) {
               data.append(TextHelper.urlEncode(prefix + "[" + k.toString() + "]"));
               data.append("=");
               data.append(TextHelper.urlEncode((String)this.values.get(k)));
               data.append("&");
            }
         }
      }
   }

   public int compareTo(Object o) {
      if (o instanceof eventContainer) {
         eventContainer e = (eventContainer)o;
         int r = this.getTyp().compareToIgnoreCase(e.getTyp());
         if (r == 0) {
            r = this.getName().compareToIgnoreCase(e.getName());
         }

         return r;
      } else {
         return -1;
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

      if (max == n && v > 0.0) {
         v = -v;
      }

      if (min == n && v < 0.0) {
         v = -v;
      }

      if (v > 0.0) {
         ret = Math.round(v * (double)max + (double)n);
      } else if (v < 0.0) {
         ret = Math.round(-v * (double)min + (double)n);
      }

      return ret;
   }

   public Vector getStructInfo() {
      Vector v = new Vector();
      v.addElement("EventContainer");
      v.addElement(this.name + "/" + this.typ);
      v.addElement(this);
      return v;
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("typ");
      v.addElement(this.typ);
      v.addElement("name");
      v.addElement(this.name);
      v.addElement("allowUse");
      v.addElement(this.allowUse + "");
      v.addElement("runOnlyOnce");
      v.addElement(this.runOnlyOnce + "");
      if (this.ev != null) {
         v.addElement("factory typ");
         v.addElement(this.ev.getTyp());
      }

      v.addElement("running event");
      if (this.runningEvent != null) {
         v.addElement(this.runningEvent.funkName());
      } else {
         v.addElement("---");
      }

      for(String tm : this.getThemeList(false)) {
         v.addElement("include theme");
         v.addElement(tm);
      }

      for(String tm : this.getThemeList(true)) {
         v.addElement("exclude theme");
         v.addElement(tm);
      }

      for(eventContainer.dataItem n : this.values.keySet()) {
         v.addElement(":" + n.toString());
         v.addElement(this.values.get(n));
         v.addElement(":- hash");
         v.addElement(n.hashCode());
         if (n.name != null) {
            v.addElement(":- name hash");
            v.addElement(n.name.hashCode());
         }

         if (n.gl != null) {
            v.addElement(":- gleis hash");
            v.addElement(n.gl.hashCode());
         }
      }

      return v;
   }

   @Override
   public String getStructName() {
      return "eventContainer";
   }

   public event getRunningEvent() {
      return this.runningEvent;
   }

   public boolean isAllowUse() {
      return this.allowUse;
   }

   public void setAllowUse(boolean allowUse) {
      this.allowUse = allowUse;
   }

   void setOnce(boolean b) {
      this.runOnlyOnce = b;
   }

   boolean isOnce() {
      return this.runOnlyOnce;
   }

   protected class dataItem implements Comparable {
      String name = null;
      gleis gl = null;
      LinkedList<gleis> allgl = null;

      private dataItem(String string) {
         super();
         this.name = string;
         if (string.contains("::")) {
            String k1 = string.substring(0, string.indexOf("::"));
            String k2 = string.substring(string.indexOf("::") + 2);
            int enr = Integer.parseInt(k2);
            this.name = k1;
            this.allgl = new LinkedList();

            gleis g2;
            for(Iterator<gleis> it = eventContainer.this.glbModel.findIterator(new Object[]{enr}); it.hasNext(); this.allgl.add(g2)) {
               g2 = (gleis)it.next();
               if (this.gl == null) {
                  this.gl = g2;
               }
            }
         }
      }

      private dataItem(String k, gleis g) {
         super();
         this.name = k;
         this.allgl = new LinkedList();

         gleis g2;
         for(Iterator<gleis> it = eventContainer.this.glbModel.findIterator(new Object[]{g.getENR()}); it.hasNext(); this.allgl.add(g2)) {
            g2 = (gleis)it.next();
            if (this.gl == null) {
               this.gl = g2;
            }
         }
      }

      public String toString() {
         return this.gl != null ? this.name + "::" + this.gl.getENR() : this.name;
      }

      private String compareValue() {
         return this.gl != null ? this.name + "::" + this.gl.hashCode() : this.name;
      }

      public boolean equals(Object o) {
         return o instanceof eventContainer.dataItem ? this.compareValue().equals(((eventContainer.dataItem)o).compareValue()) : false;
      }

      public int hashCode() {
         int hash = 3;
         hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
         return 47 * hash + (this.gl != null ? this.gl.hashCode() : 0);
      }

      public int compareTo(Object o) {
         return o instanceof eventContainer.dataItem ? this.compareValue().compareTo(((eventContainer.dataItem)o).compareValue()) : 1;
      }
   }
}
