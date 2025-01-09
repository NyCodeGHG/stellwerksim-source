package js.java.isolate.statusapplet.players;

import java.awt.Color;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class players_aid implements Comparable {
   private playersPanel my_main;
   int aid = 0;
   String name = "";
   String region = "";
   String tel = "";
   boolean sichtbar = false;
   String spieler = null;
   boolean canstitz = false;
   TreeSet<players_zug> zuege = new TreeSet();
   long starttime = 0L;
   long stoptime = 0L;
   private ConcurrentHashMap<String, OCCU_KIND> stoerungen = new ConcurrentHashMap();
   long heat = 0L;
   Color heatColor = Color.WHITE;

   players_aid(int _aid, playersPanel m) {
      this.my_main = m;
      this.aid = _aid;
      this.name = m.getParameter("aidname" + this.aid);
      this.region = m.getParameter("aidregion" + this.aid);
      this.sichtbar = m.getParameter("aidsichtbar" + this.aid).equalsIgnoreCase("J");
   }

   players_aid(playersPanel m, int _aid, String _name, String _tel) {
      this.my_main = m;
      this.aid = _aid;
      this.name = _name;
      this.tel = _tel;

      try {
         this.region = m.getParameter("aidregion" + this.aid);
         this.sichtbar = m.getParameter("aidsichtbar" + this.aid).equalsIgnoreCase("J");
      } catch (Exception var6) {
      }
   }

   void add(players_zug z) {
   }

   void remove(players_zug z) {
   }

   public int compareTo(Object o) {
      return this.name.compareTo(((players_aid)o).name);
   }

   public boolean equals(Object o) {
      return o instanceof players_aid ? this.aid == ((players_aid)o).aid : false;
   }

   public int hashCode() {
      int hash = 7;
      return 67 * hash + this.aid;
   }

   void update() {
      this.my_main.repaint();
   }

   public String toString() {
      return this.name + " (" + this.aid + ")" + (this.spieler != null ? "*" : "");
   }

   public void resetStCount() {
      this.stoerungen.clear();
   }

   public void handleST(String hash, OCCU_KIND k) {
      if (k == OCCU_KIND.NORMAL) {
         this.stoerungen.remove(hash);
      } else {
         this.stoerungen.put(hash, k);
      }
   }

   public void setHeat(long h) {
      this.heat = h;
   }

   public int getStoerungsCount() {
      return this.stoerungen.size();
   }
}
