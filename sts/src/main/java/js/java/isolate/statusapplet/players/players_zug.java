package js.java.isolate.statusapplet.players;

import js.java.isolate.statusapplet.common.status_zug_base;

public class players_zug extends status_zug_base implements Comparable {
   private playersPanel zp;
   players_aid currentaid = null;
   long lastchanged = 0L;
   int rzid;
   int verspaetungCounter = 0;

   public players_zug(playersPanel _zp, int _zid) {
      this.zp = _zp;
      this.zid = _zid;
   }

   public String getName() {
      if (this.name != null) {
         return this.name;
      } else {
         return this.zid < 0 ? "Lok (" + -this.zid + ")" : "ID" + this.zid;
      }
   }

   public String getSpezialName() {
      String n = this.getName();
      return n.indexOf(37) >= 0 ? n.substring(0, n.indexOf(37)) : n;
   }

   void updatePlan(String n) {
      this.name = n;
      this.lastchanged = System.currentTimeMillis();
   }

   public int compareTo(Object o) {
      if (!(o instanceof players_zug)) {
         return 1;
      } else {
         players_zug z2 = (players_zug)o;
         return this.name.compareTo(z2.name);
      }
   }

   boolean remove() {
      return this.currentaid == null && this.lastchanged / 1000L / 60L + 60L < System.currentTimeMillis() / 1000L / 60L;
   }

   void updateAid(int newaid) {
      if (this.aid != newaid) {
         if (this.currentaid != null) {
            this.currentaid.remove(this);
         }

         this.currentaid = this.zp.findAid(newaid);
         if (this.currentaid != null) {
            this.currentaid.add(this);
         }
      }

      this.aid = newaid;
      this.lastchanged = System.currentTimeMillis();
   }

   public int lastChangedMinutes() {
      return (int)((System.currentTimeMillis() - this.lastchanged) / 1000L / 60L);
   }

   public int getAid() {
      return this.aid;
   }
}
