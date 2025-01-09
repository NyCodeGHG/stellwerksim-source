package js.java.isolate.sim.gleisbild.gleisbildWorker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element_list;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;

public class elementConnectorFinder extends gleisbildWorkerBase<gleisbildModelFahrweg> {
   private HashSet<elementConnectorFinder.connection> cons = new HashSet();
   private TreeSet<elementConnectorFinder.shape> shapes = new TreeSet();

   private static String genElementName(gleis gl) {
      if (gl.typRequiresENR()) {
         return "E" + gl.getENR();
      } else {
         return gleis.ALLE_BAHNSTEIGE.matches(gl.getElement()) ? "H" + gl.getSWWert_special().hashCode() : "GL_" + gl.getCol() + "_" + gl.getRow();
      }
   }

   public elementConnectorFinder(gleisbildModelFahrweg gl, GleisAdapter main) {
      super(gl, main);
   }

   private elementConnectorFinder.shape shapeOf(gleis wg) {
      for (elementConnectorFinder.shape s : this.shapes) {
         if (s.g1 == wg) {
            return s;
         }
      }

      return null;
   }

   private elementConnectorFinder.connection connectionOf(elementConnectorFinder.connection c) {
      for (elementConnectorFinder.connection cc : this.cons) {
         if (cc.equals(c)) {
            return cc;
         }
      }

      return null;
   }

   public void run(elementConnectorFinder.analyser a) {
      a.parent = this;
      this.cons.clear();
      this.shapes.clear();
      a.run();
   }

   public elementConnectorFinder.formatter getFormatter() {
      return new elementConnectorFinder.formatter(this.glbModel);
   }

   public abstract static class analyser {
      protected elementConnectorFinder parent;

      protected abstract void run();
   }

   public static class connection {
      public gleis g1;
      public gleis g2;
      public boolean invisible;
      public boolean bothReachable = false;

      private connection(gleis g1, gleis g2) {
         this.g1 = g1;
         this.g2 = g2;
      }

      private connection(elementConnectorFinder.connection c) {
         this.g1 = c.g1;
         this.g2 = c.g2;
         this.invisible = c.invisible;
         this.bothReachable = c.bothReachable;
      }

      public String toDotString() {
         String n1 = elementConnectorFinder.genElementName(this.g1);
         String n2 = elementConnectorFinder.genElementName(this.g2);
         String c = "";
         if (gleis.ELEMENT_EINFAHRT.matches(this.g2.getElement()) || gleis.ELEMENT_AUSFAHRT.matches(this.g2.getElement())) {
            c = c + " constraint=false";
         }

         if (!gleis.ALLE_BAHNSTEIGE.matches(this.g1.getElement()) && !gleis.ALLE_BAHNSTEIGE.matches(this.g2.getElement())) {
            c = c + " dir=none";
         }

         if (this.invisible) {
            c = c + " style=invis";
         }

         return n1 + " -> " + n2 + "[" + c + "];";
      }

      public Map<String, String> toXml() {
         HashMap<String, String> ret = new HashMap();
         if (gleis.ALLE_BAHNSTEIGE.matches(this.g1.getElement())) {
            ret.put("name1", this.g1.getSWWert_special());
         } else {
            ret.put("enr1", Integer.toString(this.g1.getENR()));
         }

         if (gleis.ALLE_BAHNSTEIGE.matches(this.g2.getElement())) {
            ret.put("name2", this.g2.getSWWert_special());
         } else {
            ret.put("enr2", Integer.toString(this.g2.getENR()));
         }

         return ret;
      }

      public boolean equals(Object o) {
         try {
            elementConnectorFinder.connection oo = (elementConnectorFinder.connection)o;
            return oo.g1 == this.g1 && oo.g2 == this.g2 || oo.g2 == this.g1 && oo.g1 == this.g2;
         } catch (ClassCastException var3) {
            return false;
         }
      }

      public int hashCode() {
         int hash = 0;
         hash += this.g1 != null ? this.g1.hashCode() : 0;
         return hash + (this.g2 != null ? this.g2.hashCode() : 0);
      }
   }

   public class formatter {
      private final gleisbildModelFahrweg glbModel;
      private int cluster_n = 0;

      private formatter(gleisbildModelFahrweg glbModel) {
         this.glbModel = glbModel;
      }

      private void add2hash(HashMap<String, LinkedList<elementConnectorFinder.shape>> ea, String n, elementConnectorFinder.shape sp) {
         if (!ea.containsKey(n)) {
            ea.put(n, new LinkedList());
         }

         ((LinkedList)ea.get(n)).add(sp);
      }

      private HashMap<String, LinkedList<elementConnectorFinder.shape>> eaCluster() {
         element_list eaelm = new element_list(gleis.ELEMENT_AUSFAHRT, gleis.ELEMENT_EINFAHRT);
         HashMap<String, LinkedList<elementConnectorFinder.shape>> ea = new HashMap();

         for (elementConnectorFinder.shape sp : elementConnectorFinder.this.shapes) {
            if (eaelm.matches(sp.g1.getElement())) {
               String n = sp.g1.getSWWert_special();
               this.add2hash(ea, n, sp);
            }
         }

         return ea;
      }

      private HashMap<String, LinkedList<elementConnectorFinder.shape>> hpCluster() {
         Pattern p = Pattern.compile("(\\D*)(\\d.*)");
         HashMap<String, LinkedList<elementConnectorFinder.shape>> ea = new HashMap();

         for (elementConnectorFinder.shape sp : elementConnectorFinder.this.shapes) {
            if (gleis.ALLE_BAHNSTEIGE.matches(sp.g1.getElement())) {
               String n = sp.g1.getSWWert_special();
               Matcher m = p.matcher(n);
               if (m.matches() && m.groupCount() > 1) {
                  n = m.group(1);
               }

               this.add2hash(ea, n, sp);
            }
         }

         return ea;
      }

      private StringBuilder clusterShape(HashMap<String, LinkedList<elementConnectorFinder.shape>> ea) {
         StringBuilder s = new StringBuilder();

         for (Entry<String, LinkedList<elementConnectorFinder.shape>> cluster : ea.entrySet()) {
            s.append("subgraph cluster_").append(this.cluster_n).append(" {\n");
            s.append("color=lightgrey;\n");
            s.append("style=filled;\n");

            for (elementConnectorFinder.shape sp : (LinkedList)cluster.getValue()) {
               s.append(sp.toDotString());
               s.append("\n");
            }

            s.append("}\n");
            this.cluster_n++;
         }

         return s;
      }

      private StringBuilder groupShape(HashMap<String, LinkedList<elementConnectorFinder.shape>> ea, String rank) {
         StringBuilder s = new StringBuilder();

         for (Entry<String, LinkedList<elementConnectorFinder.shape>> cluster : ea.entrySet()) {
            s.append("subgraph a_").append(this.cluster_n).append(" {\n");
            s.append("group=\"").append(this.cluster_n).append("\";\n");
            s.append("rankdir=\"TB\";\n");
            int x = -1;

            for (elementConnectorFinder.shape sp : (LinkedList)cluster.getValue()) {
               x = sp.g1.getCol();
               s.append(sp.toDotString());
               s.append("\n");
            }

            String r = rank;
            if (rank == null) {
               int hw = this.glbModel.getGleisWidth() / 2;
               if (x > hw) {
                  r = "max";
               } else {
                  r = "min";
               }
            }

            s.append("rank=\"").append(r).append("\";\n");
            s.append("}\n");
            this.cluster_n++;
         }

         return s;
      }

      public String getDotText() {
         this.cluster_n = 0;
         StringBuilder s = new StringBuilder();
         s.append("digraph {\n");
         s.append("graph [center rankdir=LR];\n");
         element_list skipElm = new element_list(gleis.ELEMENT_AUSFAHRT, gleis.ELEMENT_EINFAHRT);
         s.append(this.clusterShape(this.eaCluster()));

         for (elementConnectorFinder.shape sp : elementConnectorFinder.this.shapes) {
            if (!skipElm.matches(sp.g1.getElement())) {
               s.append(sp.toDotString());
               s.append('\n');
            }
         }

         for (elementConnectorFinder.connection c : elementConnectorFinder.this.cons) {
            s.append(c.toDotString());
            s.append('\n');
         }

         s.append("}\n");
         return s.toString();
      }

      public void forEach(Consumer<Map<String, String>> s, Consumer<Map<String, String>> c) {
         for (elementConnectorFinder.shape sp : elementConnectorFinder.this.shapes) {
            s.accept(sp.toXml());
         }

         for (elementConnectorFinder.connection cn : elementConnectorFinder.this.cons) {
            c.accept(cn.toXml());
         }
      }
   }

   public static class fullAnalyser extends elementConnectorFinder.analyser {
      @Override
      protected void run() {
         this.runEinfahrten();
         this.runFahrstrassen();
      }

      private void runEinfahrten() {
         Iterator<gleis> it = this.parent.glbModel.findIterator(new Object[]{gleis.ELEMENT_EINFAHRT});

         while (it.hasNext()) {
            gleis eingl = (gleis)it.next();
            elementConnectorFinder.shape s = new elementConnectorFinder.shape(eingl);
            this.parent.shapes.add(s);
            gleis gl = eingl;
            gleis before_gl = null;

            gleis next_gl;
            do {
               next_gl = gl.next(before_gl);
               if (next_gl == null || gl.sameGleis(next_gl)) {
                  break;
               }

               before_gl = gl;
               gl = next_gl;
               if (next_gl.getElement() == gleis.ELEMENT_SIGNAL && next_gl.forUs(before_gl)) {
                  elementConnectorFinder.connection c = new elementConnectorFinder.connection(eingl, next_gl);
                  this.parent.cons.add(c);
                  break;
               }
            } while (next_gl == null);
         }
      }

      private void runFahrstrassen() {
         Iterator<fahrstrasse> it = this.parent.glbModel.fahrstrassenIterator();

         while (it.hasNext()) {
            fahrstrasse fs = (fahrstrasse)it.next();
            if (!fs.getExtend().isDeleted()
               && fs.getStart().getElement().matches(gleis.ELEMENT_SIGNAL)
               && (fs.getStop().getElement().matches(gleis.ELEMENT_SIGNAL) || fs.getStop().getElement().matches(gleis.ELEMENT_AUSFAHRT))) {
               gleis gl = fs.getStart();
               gleis before_gl = gl;
               elementConnectorFinder.shape s = new elementConnectorFinder.shape(gl);
               this.parent.shapes.add(s);

               for (gleis wg : fs.getGleisweg()) {
                  if (wg != fs.getStart() && wg != fs.getStop() && this.match(wg, before_gl)) {
                     s = null;
                     if (gleis.ALLE_WEICHEN.matches(wg.getElement())) {
                        s = this.parent.shapeOf(wg);
                     }

                     if (s == null) {
                        s = new elementConnectorFinder.shape(wg);
                        this.parent.shapes.add(s);
                     }

                     elementConnectorFinder.connection c = new elementConnectorFinder.connection(gl, wg);
                     if (gleis.ALLE_WEICHEN.matches(wg.getElement()) && wg.weicheSpitz(gl)) {
                        s.bothReachable = gl;
                     }

                     this.parent.cons.add(c);
                     gl = wg;
                  }

                  before_gl = wg;
               }

               elementConnectorFinder.connection c = new elementConnectorFinder.connection(gl, fs.getStop());
               this.parent.cons.add(c);
               s = new elementConnectorFinder.shape(fs.getStop());
               this.parent.shapes.add(s);
               elementConnectorFinder.connection fsc = new elementConnectorFinder.connection(fs.getStart(), fs.getStop());
               fsc.invisible = true;
               this.parent.cons.add(fsc);
            }
         }
      }

      protected boolean match(gleis wg, gleis before_gl) {
         return gleis.ALLE_WEICHEN.matches(wg.getElement())
            || gleis.ELEMENT_SIGNAL.matches(wg.getElement())
            || gleis.ALLE_BAHNSTEIGE.matches(wg.getElement()) && wg.forUs(before_gl);
      }
   }

   public static class shape implements Comparable {
      public final gleis g1;
      public gleis bothReachable = null;

      private shape(gleis g1) {
         this.g1 = g1;
      }

      private shape(elementConnectorFinder.shape s) {
         this.g1 = s.g1;
         this.bothReachable = s.bothReachable;
      }

      public String toDotString() {
         String n1 = elementConnectorFinder.genElementName(this.g1);
         String label = "";
         String shape = "shape=diamond,style=filled,height=.1,width=.1,fillcolor=black";
         if (this.g1.getElement().matches(gleis.ELEMENT_EINFAHRT)) {
            label = "EIN: " + this.g1.getSWWert_special();
            shape = "style=filled,fillcolor=white";
         } else if (this.g1.getElement().matches(gleis.ELEMENT_AUSFAHRT)) {
            label = "AUS: " + this.g1.getSWWert_special();
            shape = "style=filled,fillcolor=white";
         } else if (this.g1.getElement().matches(gleis.ALLE_BAHNSTEIGE)) {
            label = "H: " + this.g1.getSWWert_special();
            shape = "style=filled,fillcolor=grey";
         } else if (!this.g1.getElement().matches(gleis.ELEMENT_SIGNAL) && this.g1.getElement().matches(gleis.ALLE_WEICHEN)) {
         }

         shape = shape + ",label=\"" + label + "\"";
         return n1 + " [" + shape + "];";
      }

      public Map<String, String> toXml() {
         int e = this.g1.getElement().getElement();
         int enr = this.g1.getENR();
         String label = null;
         if (this.g1.getElement().matches(gleis.ELEMENT_EINFAHRT)) {
            label = this.g1.getSWWert_special();
         } else if (this.g1.getElement().matches(gleis.ELEMENT_AUSFAHRT)) {
            label = this.g1.getSWWert_special();
         } else if (this.g1.getElement().matches(gleis.ALLE_BAHNSTEIGE)) {
            enr = 0;
            label = this.g1.getSWWert_special();
         } else if (this.g1.getElement().matches(gleis.ELEMENT_SIGNAL)) {
            label = this.g1.getElementName();
         } else if (this.g1.getElement().matches(gleis.ALLE_WEICHEN)) {
            label = this.g1.getElementName();
         }

         HashMap<String, String> ret = new HashMap();
         ret.put("type", Integer.toString(e));
         if (label != null) {
            ret.put("name", label);
         }

         if (enr > 0) {
            ret.put("enr", Integer.toString(enr));
         }

         return ret;
      }

      public boolean equals(Object o) {
         try {
            elementConnectorFinder.shape oo = (elementConnectorFinder.shape)o;
            return oo.g1 == this.g1;
         } catch (ClassCastException var3) {
            return false;
         }
      }

      public int hashCode() {
         int hash = 5;
         return 97 * hash + (this.g1 != null ? this.g1.hashCode() : 0);
      }

      public int compareTo(Object o) {
         elementConnectorFinder.shape oo = (elementConnectorFinder.shape)o;
         int r = this.g1.getENR() - oo.g1.getENR();
         if (r == 0) {
            r = this.g1.getSWWert().compareToIgnoreCase(oo.g1.getSWWert());
         }

         if (r == 0) {
            r = this.g1.compareToGleis(oo.g1);
         }

         return r;
      }
   }

   public static class signal2signalAnalyser extends elementConnectorFinder.fullAnalyser {
      @Override
      protected boolean match(gleis wg, gleis before_gl) {
         return gleis.ELEMENT_SIGNAL.matches(wg.getElement()) || gleis.ALLE_BAHNSTEIGE.matches(wg.getElement()) && wg.forUs(before_gl);
      }
   }
}
