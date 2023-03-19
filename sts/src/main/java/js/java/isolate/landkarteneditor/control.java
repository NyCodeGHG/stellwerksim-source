package js.java.isolate.landkarteneditor;

import java.awt.EventQueue;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.TextHelper;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

class control extends AbstractListModel implements xmllistener, ListDataListener, SessionClose {
   public final int SCALE = 25;
   private final landkarteneditor editorui;
   private final landkarte lk;
   private final int edrid;
   private final String url;
   private final xmlreader xmlLoad;
   private final xmlreader xmlStore;
   private final StringBuffer data1 = new StringBuffer();
   private final bahnhofList bhflist = new bahnhofList(this);
   private final knotenList klist = new knotenList(this);
   private editKnoten editK;
   private editVerbindung editV;
   private knoten selectedKnoten = null;
   private knoten moveKnoten = null;
   private verbindung selectedVerbindung = null;

   control(landkarte lk, landkarteneditor editor) {
      super();
      this.lk = lk;
      this.editorui = editor;
      this.edrid = Integer.parseInt(editor.getParameter("rid"));
      this.url = editor.getParameter("url");
      this.xmlLoad = new xmlreader();
      this.xmlLoad.registerTag("verbindung", this);
      this.xmlLoad.registerTag("region", this);
      this.xmlLoad.registerTag("bahnhof", this);
      this.xmlLoad.registerTag("knoten", this);
      this.data1.append("edrid=");
      this.data1.append(this.edrid);
      this.data1.append("&m=get");
      this.editK = new editKnoten(this, this.klist, this.bhflist, lk);
      this.editV = new editVerbindung(this, this.klist, this.bhflist, lk);
      Thread t = new Thread(new control.loadThread());
      t.start();
      this.xmlStore = new xmlreader();
      this.xmlStore.registerTag("result", this);
      editor.getContext().addCloseObject(this.bhflist);
      editor.getContext().addCloseObject(this.klist);
   }

   @Override
   public void close() {
      this.selectedKnoten = null;
      this.moveKnoten = null;
      this.selectedVerbindung = null;
      this.editK = null;
      this.editV = null;
      this.xmlLoad.clearRegistrations();
   }

   public void save() {
      Thread t = new Thread(new control.saveThread());
      t.start();
   }

   public void adjust00() {
      this.klist.adjust00();
      this.lk.updateSize();
      this.lk.repaint();
   }

   public int getMainRid() {
      return this.edrid;
   }

   public bahnhofList.bahnhofData getAid(int aid) {
      return this.bhflist.getAid(aid);
   }

   public bahnhofList.regionData getRegion(int rid) {
      return this.bhflist.getRegionOfRid(rid);
   }

   public bahnhofList.regionData getRegion(String name) {
      return this.bhflist.getRegionOfName(name);
   }

   public knoten getKnoten(int kid) {
      return this.klist.getKnoten(kid);
   }

   void setSelectedKnoten(knoten k) {
      this.setSelectedKnoten_impl(k);
      this.editorui.selectListItem(k);
   }

   private void setSelectedKnoten_impl(knoten k) {
      this.selectedKnoten = k;
      this.repaint();
      if (k != null) {
         aidMenuItem m = k.getData().menuItem;
         this.editorui.setKnotenValue(m);
      }

      this.editorui.enableAddKnoten(true);
      this.editorui.enableDelKnoten(k != null);
   }

   boolean isSelectedKnoten(knoten k) {
      return this.selectedKnoten == k;
   }

   knoten getSelectedKnoten() {
      return this.selectedKnoten;
   }

   void setMoveKnoten(knoten k) {
      if (this.moveKnoten != k) {
         this.moveKnoten = k;
         if (k == null) {
            this.lk.updateSize();
            this.setSelectedKnoten(null);
         }

         this.repaint();
      }
   }

   boolean isMoveKnoten(knoten k) {
      return this.moveKnoten == k;
   }

   void setSelectedVerbindung(verbindung k) {
      this.setSelectedVerbindung_impl(k);
      this.editorui.selectListItem(k);
   }

   private void setSelectedVerbindung_impl(verbindung k) {
      this.selectedVerbindung = k;
      this.repaint();
      this.editorui.enableDelVerbindung(k != null);
      if (k != null) {
         this.editorui.setVerbindungDirection(this.selectedVerbindung.getDirection());
      }
   }

   boolean isSelectedVerbindung(verbindung k) {
      return this.selectedVerbindung == k;
   }

   verbindung getSelectedVerbindung() {
      return this.selectedVerbindung;
   }

   void repaint() {
      this.lk.repaint();
   }

   public void blockUI(boolean b) {
      this.editorui.blockUI(b);
      if (b) {
         this.selectedKnoten = null;
         this.moveKnoten = null;
         this.selectedVerbindung = null;
      }
   }

   private void updateUI() {
      int NUM = 4;
      JPopupMenu popup = new JPopupMenu();
      JMenu[] letter = new JMenu[7];

      for(int i = 0; i < letter.length; ++i) {
         int c1 = i * 4;
         int c2 = Math.min((i + 1) * 4, 26);
         char s1 = (char)(65 + c1);
         char s2 = (char)(65 + c2 - 1);
         String name = s1 + ".." + s2;
         letter[i] = new JMenu(name);
         popup.add(letter[i]);
      }

      int prevl = -1;
      char prevc = 'A';
      Iterator<bahnhofList.regionData> it = this.bhflist.ridIterator();

      while(it.hasNext()) {
         bahnhofList.regionData rid = (bahnhofList.regionData)it.next();
         String region = rid.name;
         String region7 = TextHelper.deAccent(region);
         int l = (region7.charAt(0) - 'A') / 4;
         JMenu menu = new JMenu(region);
         if (prevl == l && prevc != region7.charAt(0)) {
            letter[l].add(new JSeparator());
         }

         prevc = region7.charAt(0);
         prevl = l;
         letter[l].add(menu);
         aidMenuItem item = new aidMenuItem(region, rid);
         rid.menuItem = item;
         menu.add(item);
         menu.add(new JSeparator());
         Iterator<bahnhofList.bahnhofData> aidit = this.bhflist.aidIterator(rid.rid);

         for(int cnt = 0; aidit.hasNext(); menu.add(item)) {
            bahnhofList.bahnhofData aid = (bahnhofList.bahnhofData)aidit.next();
            String bname = aid.name;
            item = new aidMenuItem(bname, aid);
            aid.menuItem = item;
            if (--cnt <= 0) {
               menu.add(new JSeparator());
               cnt = 5;
            }
         }
      }

      this.editorui.setKnotenListValue(popup);
      this.lk.setKnotenList(this.klist);
      this.blockUI(false);
      this.editorui.setKnotenMode(true);
      this.setKnotenMode(true);
   }

   void knotenListValueStateChanged(Object item) {
      if (item != null) {
         aidMenuItem mitem = (aidMenuItem)item;
         this.editorui.enableAddKnoten(true);
         if (this.selectedKnoten != null) {
            this.selectedKnoten.getData().menuItem = mitem;
            this.selectedKnoten.setData(mitem.data);
            this.repaint();
         }
      }
   }

   void setKnotenMode(boolean selected) {
      this.editorui.enableAddKnoten(selected);
      this.editorui.enableKnotenList(selected);
      this.editorui.enableDelKnoten(false);
      this.editorui.enableDelVerbindung(false);
      if (selected) {
         this.lk.addMouseListener(this.editK);
         this.lk.addMouseMotionListener(this.editK);
      }

      this.lk.removeMouseListener(this.editK);
      this.lk.removeMouseListener(this.editV);
      this.lk.removeMouseMotionListener(this.editK);
      this.lk.removeMouseMotionListener(this.editV);
      if (selected) {
         this.lk.addMouseListener(this.editK);
         this.lk.addMouseMotionListener(this.editK);
         this.klist.registerKnotenEvents();
      } else {
         this.lk.addMouseListener(this.editV);
         this.lk.addMouseMotionListener(this.editV);
         this.klist.registerVerbindungEvents();
      }

      this.setSelectedKnoten(null);
      this.setMoveKnoten(null);
      this.setSelectedVerbindung(null);
   }

   void listSelectionChanged(Object k) {
      if (k instanceof knoten) {
         this.setSelectedKnoten_impl((knoten)k);
      } else if (k instanceof verbindung) {
         this.setSelectedVerbindung_impl((verbindung)k);
      }
   }

   public int getSize() {
      return this.klist.getSize();
   }

   public Object getElementAt(int index) {
      return this.klist.getElementAt(index);
   }

   void addKnoten() {
      try {
         this.klist.addKnoten(this.editorui.getKnotenValue().data, this.klist.findFreeLocation());
         this.lk.updateSize();
         this.repaint();
      } catch (NullPointerException var2) {
      }
   }

   void delKnoten() {
      this.klist.removeKnoten(this.selectedKnoten);
      this.setSelectedKnoten(null);
      this.lk.updateSize();
      this.repaint();
   }

   void delVerbindung() {
      this.klist.removeVerbindung(this.selectedVerbindung);
      this.setSelectedVerbindung(null);
      this.repaint();
   }

   void setVerbindungDirection(int direction) {
      this.selectedVerbindung.setDirection(direction);
      this.repaint();
   }

   public void intervalAdded(ListDataEvent e) {
      this.fireIntervalAdded(this, e.getIndex0(), e.getIndex1());
   }

   public void intervalRemoved(ListDataEvent e) {
      this.fireIntervalRemoved(this, e.getIndex0(), e.getIndex1());
   }

   public void contentsChanged(ListDataEvent e) {
      this.fireContentsChanged(this, e.getIndex0(), e.getIndex1());
   }

   public void parseStartTag(String tag, Attributes attrs) {
      if (tag.equals("verbindung")) {
         this.klist.addVerbindung(Integer.parseInt(attrs.getValue("v1")), Integer.parseInt(attrs.getValue("v2")));
      } else if (tag.equals("knoten")) {
         if (attrs.getValue("aid") != null) {
            this.klist
               .addKnoten(
                  this.bhflist.getAid(Integer.parseInt(attrs.getValue("aid"))),
                  Integer.parseInt(attrs.getValue("kid")),
                  Integer.parseInt(attrs.getValue("x")),
                  Integer.parseInt(attrs.getValue("y"))
               );
         } else {
            this.klist
               .addKnoten(
                  this.bhflist.getRegionOfRid(Integer.parseInt(attrs.getValue("erid"))),
                  Integer.parseInt(attrs.getValue("kid")),
                  Integer.parseInt(attrs.getValue("x")),
                  Integer.parseInt(attrs.getValue("y"))
               );
         }
      } else if (tag.equals("region")) {
         this.bhflist.addRegion(Integer.parseInt(attrs.getValue("rid")), attrs.getValue("name"));
      } else if (tag.equals("bahnhof")) {
         this.bhflist.addBhf(Integer.parseInt(attrs.getValue("aid")), attrs.getValue("name"), attrs.getValue("netzname"));
      } else if (tag.equals("result")) {
      }
   }

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
   }

   private class loadThread implements Runnable {
      private loadThread() {
         super();
      }

      public void run() {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               control.this.blockUI(true);
            }
         });

         try {
            control.this.xmlLoad.updateData(control.this.url, control.this.data1);
         } catch (IOException var2) {
            var2.printStackTrace();
         }

         EventQueue.invokeLater(new Runnable() {
            public void run() {
               control.this.updateUI();
            }
         });
      }
   }

   private class saveThread implements Runnable {
      private saveThread() {
         super();
      }

      public void run() {
         try {
            EventQueue.invokeAndWait(new Runnable() {
               public void run() {
                  control.this.blockUI(true);
               }
            });
         } catch (InvocationTargetException | InterruptedException var3) {
            Logger.getLogger(control.class.getName()).log(Level.SEVERE, null, var3);
         }

         try {
            StringBuffer data = new StringBuffer();
            data.append("edrid=");
            data.append(control.this.edrid);
            data.append("&m=store&");
            control.this.klist.generateSaveString(data);
            control.this.xmlStore.updateData(control.this.url, data);
         } catch (IOException var2) {
            var2.printStackTrace();
         }

         EventQueue.invokeLater(new Runnable() {
            public void run() {
               control.this.updateUI();
            }
         });
      }
   }
}
