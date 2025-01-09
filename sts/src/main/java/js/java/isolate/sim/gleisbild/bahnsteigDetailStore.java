package js.java.isolate.sim.gleisbild;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import js.java.isolate.sim.gleis.gleis;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.AlphanumComparator;

public class bahnsteigDetailStore implements SessionClose {
   private final gleisbildModel glbModel;
   private TreeMap<String, bahnsteigDetailStore.subStore> alleBahnsteige;
   private final boolean highlight;

   public bahnsteigDetailStore(gleisbildModel glbModel) {
      this(glbModel, false);
   }

   public bahnsteigDetailStore(gleisbildModel glbModel, boolean highlight) {
      this.glbModel = glbModel;
      this.highlight = highlight;
      this.doCache();
   }

   @Override
   public void close() {
      this.alleBahnsteige.clear();
   }

   private void doCache() {
      Iterator<gleis> it = this.glbModel.findIterator(gleis.ALLE_BAHNSTEIGE);
      this.alleBahnsteige = new TreeMap();

      while (it.hasNext()) {
         gleis gl = (gleis)it.next();
         String bst = gl.getSWWert();
         bahnsteigDetailStore.subStore s = (bahnsteigDetailStore.subStore)this.alleBahnsteige.get(bst);
         if (s == null) {
            this.alleBahnsteige.put(bst, new bahnsteigDetailStore.subStore(gl));
         } else {
            s.gleise.add(gl);
         }
      }

      for (Entry<String, bahnsteigDetailStore.subStore> e : this.alleBahnsteige.entrySet()) {
         TreeSet<String> nachbarn = this.glbModel.findNeighborBahnsteig(((bahnsteigDetailStore.subStore)e.getValue()).gleise, this.highlight);
         ((bahnsteigDetailStore.subStore)e.getValue()).nachbarn.addAll(nachbarn);
         Set<gleis> connected = this.glbModel.findAllConnectedBahnsteig((String)e.getKey(), this.highlight);

         for (gleis g : connected) {
            ((bahnsteigDetailStore.subStore)e.getValue()).connected.add(g.getSWWert());
         }

         TreeSet<String> nachbarn2 = this.glbModel.findNeighborBahnsteig(connected, this.highlight);
         ((bahnsteigDetailStore.subStore)e.getValue()).nachbarn2.addAll(nachbarn2);

         for (String n : nachbarn2) {
            for (gleis g : this.glbModel.findAllConnectedBahnsteig(this.glbModel.findBahnsteig(n), this.highlight)) {
               ((bahnsteigDetailStore.subStore)e.getValue()).connected2.add(g.getSWWert());
            }
         }
      }
   }

   public Set<String> getAlleBahnsteig() {
      TreeSet<String> ret = new TreeSet(new AlphanumComparator());
      ret.addAll(this.alleBahnsteige.keySet());
      return ret;
   }

   public boolean isNeighborBahnsteigOf(String g1, gleis g2) {
      boolean r = false;
      bahnsteigDetailStore.subStore s = (bahnsteigDetailStore.subStore)this.alleBahnsteige.get(g1);
      if (s != null) {
         r = s.nachbarn.contains(g2.getSWWert());
      }

      return r;
   }

   public boolean isConnectedBahnsteigOf(String g1, gleis g2) {
      boolean r = false;
      bahnsteigDetailStore.subStore s = (bahnsteigDetailStore.subStore)this.alleBahnsteige.get(g1);
      if (s != null) {
         r = s.connected.contains(g2.getSWWert());
      }

      return r;
   }

   public boolean isConnectedBahnsteigOf(String g1, String zielgleis) {
      boolean r = false;
      bahnsteigDetailStore.subStore s = (bahnsteigDetailStore.subStore)this.alleBahnsteige.get(g1);
      if (s != null) {
         r = s.connected.contains(zielgleis);
      }

      return r;
   }

   public TreeSet<String> getAlternativebahnsteigeOf(String g) {
      TreeSet<String> ret = new TreeSet(new AlphanumComparator());
      bahnsteigDetailStore.subStore s = (bahnsteigDetailStore.subStore)this.alleBahnsteige.get(g);
      if (s != null) {
         ret.addAll(s.nachbarn);
         ret.addAll(s.nachbarn2);
         ret.addAll(s.connected);
         ret.addAll(s.connected2);
      }

      return ret;
   }

   public Set<String> findNeighborBahnsteig(String g) {
      HashSet<String> ret = new HashSet();
      bahnsteigDetailStore.subStore s = (bahnsteigDetailStore.subStore)this.alleBahnsteige.get(g);
      if (s != null) {
         ret.addAll(s.nachbarn);
      }

      return ret;
   }

   public boolean bahnsteigIsHaltepunkt(String bst) {
      Iterator<gleis> it = this.glbModel.findIterator(bst, gleis.ELEMENT_HALTEPUNKT);
      return it.hasNext();
   }

   private class subStore {
      final HashSet<gleis> gleise = new HashSet();
      final HashSet<String> nachbarn = new HashSet();
      final HashSet<String> nachbarn2 = new HashSet();
      final HashSet<String> connected = new HashSet();
      final HashSet<String> connected2 = new HashSet();

      private subStore(gleis gl) {
         this.gleise.add(gl);
      }
   }
}
