package js.java.isolate.landkarteneditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import js.java.schaltungen.moduleapi.SessionClose;

class bahnhofList implements SessionClose {
   private final control my_main;
   private final HashMap<Integer, bahnhofList.bahnhofData> bhflist = new HashMap();
   private final HashMap<Integer, bahnhofList.regionData> regionlist = new HashMap();
   private final TreeMap<String, bahnhofList.regionData> ridlist = new TreeMap();
   private final TreeMap<String, bahnhofList.bahnhofData> bhfsort = new TreeMap();

   bahnhofList(control main) {
      this.my_main = main;
   }

   @Override
   public void close() {
      this.bhflist.clear();
      this.regionlist.clear();
      this.ridlist.clear();
      this.bhfsort.clear();
   }

   public void addBhf(int aid, String name, String netzname) {
      bahnhofList.bahnhofData bd = new bahnhofList.bahnhofData(aid, name, netzname);
      this.bhflist.put(aid, bd);
      this.bhfsort.put(name, bd);
   }

   public void addRegion(int rid, String name) {
      bahnhofList.regionData rd = new bahnhofList.regionData(rid, name);
      this.regionlist.put(rid, rd);
      this.ridlist.put(name, rd);
   }

   public bahnhofList.bahnhofData getAid(int aid) {
      return (bahnhofList.bahnhofData)this.bhflist.get(aid);
   }

   public bahnhofList.regionData getRegionOfRid(int rid) {
      return (bahnhofList.regionData)this.regionlist.get(rid);
   }

   public bahnhofList.regionData getRegionOfName(String name) {
      return (bahnhofList.regionData)this.ridlist.get(name);
   }

   public Iterator<bahnhofList.regionData> ridIterator() {
      return this.ridlist.values().iterator();
   }

   public Iterator<bahnhofList.bahnhofData> aidIterator(int rid) {
      return new bahnhofList.aidIterator(((bahnhofList.regionData)this.regionlist.get(rid)).name);
   }

   private class aidIterator implements Iterator<bahnhofList.bahnhofData> {
      private Iterator<bahnhofList.bahnhofData> it;
      private bahnhofList.bahnhofData nextElement = null;
      private String netzname;

      private aidIterator(String netzname) {
         this.netzname = netzname;
         this.it = bahnhofList.this.bhfsort.values().iterator();
      }

      public boolean hasNext() {
         while (this.it.hasNext()) {
            this.nextElement = (bahnhofList.bahnhofData)this.it.next();
            if (this.nextElement.netzname.equals(this.netzname)) {
               return true;
            }
         }

         return false;
      }

      public bahnhofList.bahnhofData next() {
         return this.nextElement;
      }

      public void remove() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }

   public static class bahnhofData extends bahnhofList.bahnhofListData {
      public final int aid;
      public final String netzname;

      bahnhofData(int aid, String name, String netzname) {
         super(name);
         this.aid = aid;
         this.netzname = netzname;
      }

      @Override
      int getId() {
         return this.aid;
      }
   }

   public abstract static class bahnhofListData {
      public aidMenuItem menuItem = null;
      public final String name;

      protected bahnhofListData(String name) {
         this.name = name;
      }

      abstract int getId();
   }

   public static class regionData extends bahnhofList.bahnhofListData {
      public final int rid;

      regionData(int rid, String name) {
         super(name);
         this.rid = rid;
      }

      @Override
      int getId() {
         return this.rid;
      }
   }
}
