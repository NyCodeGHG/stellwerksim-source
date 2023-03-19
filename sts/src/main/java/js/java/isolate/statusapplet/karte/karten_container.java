package js.java.isolate.statusapplet.karte;

import java.awt.Color;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import js.java.schaltungen.chatcomng.OCCU_KIND;

class karten_container {
   public String namen;
   public String netznames;
   public int kid;
   public int x;
   public int y;
   public boolean sichtbar = false;
   public int aaid;
   public int erid;
   public String rnamen;
   public int kid1;
   public int kid2;
   public boolean kiduep = false;
   public String stitz;
   public int pos = 0;
   public String spieler = null;
   public aidPanel panel = null;
   public HashMap<Integer, String> einfahrten = new HashMap();
   public HashMap<Integer, String> ausfahrten = new HashMap();
   public boolean canstitz = false;
   public long heat = 0L;
   private ConcurrentHashMap<String, OCCU_KIND> stoerungen = new ConcurrentHashMap();
   public Color heatColor = Color.WHITE;
   public int heatSize = 0;

   karten_container() {
      super();
      this.einfahrten.put(0, "---");
      this.ausfahrten.put(0, "---");
   }

   void addElement(int element4, int enr4, String swwert4) {
      if (element4 == 6) {
         this.einfahrten.put(enr4, swwert4);
      } else if (element4 == 7) {
         this.ausfahrten.put(enr4, swwert4);
      }
   }

   void set(String k, String v) {
      try {
         if (k.compareTo("namen") == 0) {
            this.namen = v;
         } else if (k.compareTo("netznames") == 0) {
            this.netznames = v;
         } else if (k.compareTo("kid") == 0 && v != null) {
            this.kid = Integer.parseInt(v);
         } else if (k.compareTo("x") == 0) {
            this.x = Integer.parseInt(v);
         } else if (k.compareTo("y") == 0) {
            this.y = Integer.parseInt(v);
         } else if (k.compareTo("sichtbar") == 0 && v != null) {
            this.sichtbar = v.equalsIgnoreCase("J");
         } else if (k.compareTo("aaid") == 0 && v != null) {
            this.aaid = Integer.parseInt(v);
         } else if (k.compareTo("erid") == 0 && v != null) {
            this.erid = Integer.parseInt(v);
         } else if (k.compareTo("rnamen") == 0) {
            this.rnamen = v;
         } else if (k.compareTo("stitz") == 0) {
            this.stitz = v;
         } else if (k.compareTo("kid1_") == 0 && v != null) {
            this.kid1 = Integer.parseInt(v);
         } else if (k.compareTo("kid2_") == 0 && v != null) {
            this.kid2 = Integer.parseInt(v);
         } else if (k.compareTo("kiduep_") == 0 && v != null) {
            this.kiduep = v.equalsIgnoreCase("J");
         }
      } catch (Exception var4) {
         System.out.println(var4.getMessage() + ": " + k + ":" + v);
      }
   }

   public void resetStCount() {
      this.stoerungen.clear();
   }

   void handleST(String hash, OCCU_KIND k) {
      if (k == OCCU_KIND.NORMAL) {
         this.stoerungen.remove(hash);
      } else {
         this.stoerungen.put(hash, k);
      }
   }

   public int getStoerungsCount() {
      return this.stoerungen.size();
   }
}
