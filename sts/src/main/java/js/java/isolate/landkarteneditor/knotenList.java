package js.java.isolate.landkarteneditor;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import js.java.schaltungen.moduleapi.SessionClose;

class knotenList implements ListModel, SessionClose {
   private final control my_main;
   private final knotenList.knotenModel kModel = new knotenList.knotenModel();
   private final knotenList.verbindungModel vModel = new knotenList.verbindungModel();
   private final TreeMap<Integer, knoten> knoten = new TreeMap();
   private final ArrayList<knoten> knotenList = new ArrayList();
   private final ArrayList<verbindung> verbindungen = new ArrayList();
   private knotenList.baseModel currentModel = null;

   knotenList(control main) {
      super();
      this.my_main = main;
      this.currentModel = this.kModel;
   }

   @Override
   public void close() {
      this.knoten.clear();
      this.knotenList.clear();
      this.verbindungen.clear();
   }

   public void generateSaveString(StringBuffer data) {
      for(knoten k : this.knotenList) {
         k.generateSaveString(data);
      }

      for(verbindung v : this.verbindungen) {
         v.generateSaveString(data);
      }
   }

   public void adjust00() {
      int minx = Integer.MAX_VALUE;
      int miny = Integer.MAX_VALUE;

      for(knoten k : this.knotenList) {
         minx = Math.min(minx, k.getKX());
         miny = Math.min(miny, k.getKY());
      }

      for(knoten k : this.knotenList) {
         k.moveLocationBy(-minx, -miny);
      }
   }

   private int generateKid() {
      int cc = 1;

      for(int m : this.knoten.keySet()) {
         if (m != cc) {
            if (!this.knoten.containsKey(cc)) {
               break;
            }

            cc = m;
         }

         ++cc;
      }

      return cc;
   }

   void refresh(knoten k) {
      int i = this.knotenList.indexOf(k);
      if (i >= 0) {
         this.kModel.changed(i);
      }
   }

   public void addKnoten(bahnhofList.bahnhofData aid, int kid, int x, int y) {
      knoten e = new knoten(this.my_main, this, aid, kid, x, y);
      this.knoten.put(kid, e);
      this.knotenList.add(e);
      this.kModel.added(e);
   }

   public void addKnoten(bahnhofList.regionData erid, int kid, int x, int y) {
      knoten e = new knoten(this.my_main, this, erid, kid, x, y);
      this.knoten.put(kid, e);
      this.knotenList.add(e);
      this.kModel.added(e);
   }

   public verbindung addVerbindung(int kid1, int kid2) {
      verbindung e = new verbindung(this.my_main, this, kid1, kid2);
      this.verbindungen.add(e);
      this.vModel.added(e);
      return e;
   }

   public knoten getKnoten(int kid) {
      return (knoten)this.knoten.get(kid);
   }

   public Iterator<knoten> knotenIterator() {
      return this.knoten.values().iterator();
   }

   public Iterator<verbindung> verbindungIterator() {
      return this.verbindungen.iterator();
   }

   public void removeVerbindungen(int kid) {
      int i = 0;
      Iterator<verbindung> it = this.verbindungen.iterator();

      while(it.hasNext()) {
         verbindung v = (verbindung)it.next();
         if (v.hasKid(kid)) {
            it.remove();
            this.vModel.removed(i);
         } else {
            ++i;
         }
      }
   }

   public void removeVerbindung(verbindung v) {
      int i = this.verbindungen.indexOf(v);
      if (i >= 0) {
         this.verbindungen.remove(v);
         this.vModel.removed(i);
      }
   }

   public void removeKnoten(knoten selectedKnoten) {
      int i = this.knotenList.indexOf(selectedKnoten);
      if (i >= 0) {
         this.knoten.remove(selectedKnoten.getKid());
         this.knotenList.remove(i);
         this.kModel.removed(i);
         this.removeVerbindungen(selectedKnoten.getKid());
      }
   }

   public void addKnoten(bahnhofList.bahnhofListData data, Point xy) {
      this.addKnoten(data, xy.x, xy.y);
   }

   public void addKnoten(bahnhofList.bahnhofListData data, int x, int y) {
      int kid = this.generateKid();
      knoten e = new knoten(this.my_main, this, data, kid, x, y);
      this.knoten.put(kid, e);
      this.knotenList.add(e);
      this.kModel.added(e);
   }

   public Point findFreeLocation() {
      Point p = new Point(0, 0);
      int minx = Integer.MAX_VALUE;
      int miny = Integer.MAX_VALUE;

      for(knoten k : this.knotenList) {
         minx = Math.min(minx, k.getKX());
         miny = Math.min(miny, k.getKY());
      }

      p.x = minx;
      p.y = miny - 1;
      return p;
   }

   public void registerKnotenEvents() {
      try {
         this.currentModel.updateDel();
         this.currentModel.removeListDataListener(this.my_main);
      } catch (NullPointerException var2) {
      }

      this.currentModel = this.kModel;
      this.currentModel.addListDataListener(this.my_main);
      this.currentModel.updateAdd();
   }

   public void registerVerbindungEvents() {
      try {
         this.currentModel.updateDel();
         this.currentModel.removeListDataListener(this.my_main);
      } catch (NullPointerException var2) {
      }

      this.currentModel = this.vModel;
      this.currentModel.addListDataListener(this.my_main);
      this.currentModel.updateAdd();
   }

   public int getSize() {
      return this.currentModel.getSize();
   }

   public Object getElementAt(int index) {
      return this.currentModel.getElementAt(index);
   }

   public void addListDataListener(ListDataListener l) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void removeListDataListener(ListDataListener l) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   private abstract class baseModel extends AbstractListModel {
      private baseModel() {
         super();
      }

      void updateAdd() {
         if (this.getSize() > 0) {
            this.fireIntervalAdded(this, 0, this.getSize() - 1);
         }
      }

      void updateDel() {
         this.fireIntervalRemoved(this, 0, Math.max(0, this.getSize() - 1));
      }

      void changed(int i) {
         this.fireContentsChanged(this, i, i);
      }

      void removed(int i) {
         this.fireIntervalRemoved(this, i, i);
      }
   }

   private class knotenModel extends knotenList.baseModel {
      private knotenModel() {
         super();
      }

      public int getSize() {
         return knotenList.this.knotenList.size();
      }

      public Object getElementAt(int index) {
         return knotenList.this.knotenList.get(index);
      }

      void added(knoten e) {
         int i = knotenList.this.knotenList.indexOf(e);
         this.fireIntervalAdded(this, i, i);
      }
   }

   private class verbindungModel extends knotenList.baseModel {
      private verbindungModel() {
         super();
      }

      public int getSize() {
         return knotenList.this.verbindungen.size();
      }

      public Object getElementAt(int index) {
         return knotenList.this.verbindungen.get(index);
      }

      void added(verbindung e) {
         int i = knotenList.this.verbindungen.indexOf(e);
         this.fireIntervalAdded(this, i, i);
      }
   }
}
