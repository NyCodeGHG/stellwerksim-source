package js.java.isolate.sim.gleis;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleis.gleisElements.gleisHelper;

public class gleisTypContainer {
   private static gleisTypContainer instance = null;
   private HashMap<Integer, gleisTypContainer.typentry> types_hash;
   private LinkedHashMap<gleisElements.RICHTUNG, String> richtung_hash;
   private Map<gleisElements.RICHTUNG, String> return_richtung_hash;
   private List<gleisTypContainer.block> return_blocks;

   private gleisTypContainer(GleisAdapter m) {
      super();
      String p = m.getParameter("typ");
      if (p != null) {
         this.types_hash = new HashMap();
         StringTokenizer pst = new StringTokenizer(p, ",");

         while(pst.hasMoreTokens()) {
            String tk1 = pst.nextToken();
            StringTokenizer vst = new StringTokenizer(tk1, "=");
            String tk_key = vst.nextToken().trim();
            String tk_value = vst.nextToken().trim();
            char[] tk_chars = tk_key.toCharArray();
            if (Character.isDigit(tk_chars[0])) {
               int v = Integer.parseInt(tk_key);
               gleisTypContainer.typentry te = new gleisTypContainer.typentry();
               te.name = tk_value;
               this.setElementNames(m.getParameter("element" + v), te);
               this.types_hash.put(v, te);
            }
         }

         this.richtung_hash = new LinkedHashMap();
         p = m.getParameter("richtung");
         StringTokenizer dst = new StringTokenizer(p, ",");

         while(dst.hasMoreTokens()) {
            String tk1 = dst.nextToken();
            StringTokenizer vst = new StringTokenizer(tk1, "=");
            String tk_key = vst.nextToken().trim();
            String tk_value = vst.nextToken().trim();
            gleisElements.RICHTUNG r = gleisHelper.findRichtung(tk_key);
            this.richtung_hash.put(r, tk_value);
         }

         this.return_richtung_hash = Collections.unmodifiableMap(this.richtung_hash);
         this.return_blocks = new LinkedList();
         p = m.getParameter("gelements:blocks");
         if (p != null) {
            dst = new StringTokenizer(p, ",");

            while(dst.hasMoreTokens()) {
               String tk1 = dst.nextToken();
               StringTokenizer vst = new StringTokenizer(tk1, "=");
               String tk_key = vst.nextToken().trim();
               String tk_value = vst.nextToken().trim();
               gleisTypContainer.block bl = new gleisTypContainer.block(Integer.parseInt(tk_key), tk_value);
               this.return_blocks.add(bl);
            }
         }

         p = m.getParameter("gelements:quick");
         if (p != null) {
            dst = new StringTokenizer(p, ",");

            while(dst.hasMoreTokens()) {
               String tk1 = dst.nextToken();
               int n = Integer.parseInt(tk1);

               for(gleisTypContainer.block bl : this.return_blocks) {
                  if (bl.number == n) {
                     bl.quick = true;
                  }
               }
            }
         }

         p = m.getParameter("gelements:elements");
         if (p != null) {
            dst = new StringTokenizer(p, ",");

            while(dst.hasMoreTokens()) {
               try {
                  String tk1 = dst.nextToken();
                  StringTokenizer vst = new StringTokenizer(tk1, "=");
                  String tk_key = vst.nextToken().trim();
                  String[] tk_value = vst.nextToken().trim().split(":");
                  int n = Integer.parseInt(tk_key);
                  int typ = Integer.parseInt(tk_value[0]);
                  int elm = Integer.parseInt(tk_value[1]);

                  for(gleisTypContainer.block bl : this.return_blocks) {
                     if (bl.number == n) {
                        bl.add(gleisHelper.findElement(typ, elm));
                     }
                  }
               } catch (Exception var13) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, p, var13);
               }
            }
         }

         this.return_blocks = Collections.unmodifiableList(this.return_blocks);
      }
   }

   private void setElementNames(String p, gleisTypContainer.typentry te) {
      if (p != null) {
         StringTokenizer pst = new StringTokenizer(p, ",");

         while(pst.hasMoreTokens()) {
            String tk1 = pst.nextToken();
            StringTokenizer vst = new StringTokenizer(tk1, "=");
            String tk_key = vst.nextToken().trim();
            String tk_value = vst.nextToken().trim();
            char[] tk_chars = tk_key.toCharArray();
            if (Character.isDigit(tk_chars[0])) {
               int v = Integer.parseInt(tk_key);
               te.elements.put(v, tk_value);
            }
         }
      }
   }

   public static void setTypNames(GleisAdapter m) {
      instance = new gleisTypContainer(m);
   }

   public static gleisTypContainer getInstance() {
      return instance;
   }

   public int[] getTypes() {
      int[] ret = new int[this.types_hash.size()];
      int i = 0;

      for(int t : this.types_hash.keySet()) {
         ret[i++] = t;
      }

      return ret;
   }

   public String getTypName(int t) {
      gleisTypContainer.typentry te = (gleisTypContainer.typentry)this.types_hash.get(t);
      return te != null ? te.name : null;
   }

   public String getTypName(element t) {
      return this.getTypName(t.getTyp());
   }

   public int[] getTypElements(int t) {
      int[] ret = null;
      gleisTypContainer.typentry te = (gleisTypContainer.typentry)this.types_hash.get(t);
      if (te != null) {
         ret = new int[te.elements.size()];
         int i = 0;

         for(int e : te.elements.keySet()) {
            ret[i++] = e;
         }
      }

      return ret;
   }

   public int[] getTypElements(element t) {
      return this.getTypElements(t.getTyp());
   }

   public String getTypElementName(int t, int e) {
      gleisTypContainer.typentry te = (gleisTypContainer.typentry)this.types_hash.get(t);
      return te != null ? (String)te.elements.get(e) : null;
   }

   public String getTypElementName(element elm) {
      return this.getTypElementName(elm.getTyp(), elm.getElement());
   }

   public String getTypElementName(gleis gl) {
      return this.getTypElementName(gl.getElement());
   }

   public Map<gleisElements.RICHTUNG, String> getRichtungen() {
      return this.return_richtung_hash;
   }

   public List<gleisTypContainer.block> getBlocks() {
      return this.return_blocks;
   }

   public static class block extends LinkedList<element> {
      private final String title;
      private boolean quick = false;
      private int number;

      block(int n, String title) {
         super();
         this.number = n;
         this.title = title;
      }

      public String getTitle() {
         return this.title;
      }

      public boolean isQuick() {
         return this.quick;
      }
   }

   private static class typentry {
      public String name = "";
      public HashMap<Integer, String> elements = new HashMap();

      private typentry() {
         super();
      }
   }
}
