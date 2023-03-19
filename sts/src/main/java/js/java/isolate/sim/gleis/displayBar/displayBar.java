package js.java.isolate.sim.gleis.displayBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.adapter.MessagingAdapter;
import org.xml.sax.Attributes;

public class displayBar {
   private final MessagingAdapter my_main;
   private final gleisbildModel glbModel;
   private final CopyOnWriteArrayList<connector> bar = new CopyOnWriteArrayList();
   private boolean legacy = true;

   public void cleanup() {
      for(connector e : this.bar) {
         if (e.autoremove) {
            this.bar.remove(e);
         } else {
            e.cleanup();
         }
      }
   }

   public void clear() {
      this.legacy = true;
      this.bar.clear();
   }

   public displayBar(gleisbildModel m, MessagingAdapter main) {
      super();
      this.glbModel = m;
      this.my_main = main;
   }

   public void handleLegacy() {
      if (this.legacy) {
         this.bar.clear();
         Iterator<gleis> it = this.glbModel.findIterator(gleis.ALLE_ZUGDISPLAYS);

         while(it.hasNext()) {
            gleis gl = (gleis)it.next();
            connector c = new connector(this.glbModel, gl, gl.getSWWert());
            this.bar.add(c);
         }
      }
   }

   public void addEntry(Attributes attrs) {
      this.legacy = false;

      try {
         connector c = new connector(this.glbModel, attrs);
         this.bar.add(c);
      } catch (UnknownDisplayException var3) {
         this.my_main.showStatus("Displayverdrahtung: Unbekanntes Display: " + var3.getDest(), 2);
      } catch (UndefinedSWWertException var4) {
         this.my_main.showStatus("Displayverdrahtung: Unbekannter SWWert für Display: " + var4.getDest(), 1);
      }
   }

   public void saveData(StringBuffer data) {
      if (!this.legacy) {
         int i = 0;

         for(connector e : this.bar) {
            if (!e.autoremove) {
               i += e.saveData(data, "displayconnect[" + i + "]");
            }
         }

         data.append("displayconnections=").append(i).append("&");
      }
   }

   public connector addEntry(gleis display, gleis dest, boolean fsconnector) {
      connector nc = new connector(this.glbModel, display, dest.getSWWert());
      nc.fsconnector = fsconnector;

      for(connector c : this.bar) {
         if (c.destinationDisplay.sameGleis(display) && c.swwert != null && c.swwert.equalsIgnoreCase(nc.swwert)) {
            nc = null;
            break;
         }
      }

      if (nc != null) {
         this.bar.add(nc);
         this.glbModel.changeC(6);
      }

      return nc;
   }

   public void delEntry(connector c) {
      this.bar.remove(c);
      this.glbModel.changeC(4);
   }

   public displayBar.listModel getDisplayList() {
      return new displayBar.listModel(this);
   }

   public LinkedList<gleis> getConnectedItems(gleis g) {
      LinkedList<gleis> ret = new LinkedList();
      LinkedList<connector> dis = new LinkedList();
      if (gleis.ALLE_GLEISE.matches(g.getElement()) && g.isDisplayTrigger()) {
         for(connector c : this.bar) {
            if (c.swwert != null && c.swwert.equalsIgnoreCase(g.getSWWert())) {
               dis.addAll(this.getDisplayData(c.destinationDisplay));
            }
         }
      } else if (gleis.ALLE_ZUGDISPLAYS.matches(g.getElement())) {
         dis = this.getDisplayData(g);
      }

      if (!dis.isEmpty()) {
         for(connector c : dis) {
            if (!ret.contains(c.destinationDisplay)) {
               ret.add(c.destinationDisplay);
            }

            if (c.swwert != null && !c.swwert.isEmpty()) {
               Iterator<gleis> it = this.glbModel.findIterator(gleis.ALLE_GLEISE, c.swwert);

               while(it.hasNext()) {
                  gleis gg = (gleis)it.next();
                  if (gg.isDisplayTriggerSelectable()) {
                     ret.add(gg);
                  }
               }
            }
         }
      }

      return ret;
   }

   protected ArrayList<gleis> getDisplays() {
      ArrayList<gleis> ret = new ArrayList();

      gleis gl;
      for(Iterator<gleis> it = this.glbModel.findIterator(gleis.ALLE_ZUGDISPLAYS); it.hasNext(); ret.add(gl)) {
         gl = (gleis)it.next();
         if (gl.getSWWert().isEmpty()) {
            gl.setSWWert("Namenloses Display " + gl.hashCode());
         }
      }

      return ret;
   }

   protected LinkedList<connector> getDisplayData(gleis g) {
      LinkedList<connector> ret = new LinkedList();

      for(connector c : this.bar) {
         if (c.destinationDisplay.sameGleis(g)) {
            ret.add(c);
         }
      }

      return ret;
   }

   private LinkedList<connector> findEntry(gleis g, boolean fsconnector) {
      LinkedList<connector> ret = new LinkedList();

      for(connector c : this.bar) {
         if (c.fsconnector == fsconnector
            && (
               c.swwert != null && g.getSWWert().equalsIgnoreCase(c.swwert) && g.isDisplayTriggerSelectable()
                  || c.sourceElement != null && c.sourceElement.sameGleis(g)
            )) {
            ret.add(c);
         }
      }

      return ret;
   }

   public boolean isFSkontakt(gleis g) {
      LinkedList<connector> gl = this.findEntry(g, true);
      return !gl.isEmpty();
   }

   private void clearDisplay(connector c, boolean now) {
      this.clearDisplay(null, c, now, false);
   }

   private void clearDisplay(gleis refgleis, connector c, boolean now) {
      this.clearDisplay(refgleis, c, now, false);
   }

   private void clearDisplay(connector c, boolean now, boolean blink) {
      this.clearDisplay(null, c, now, blink);
   }

   private void clearDisplay(gleis refgleis, connector c, boolean now, boolean blink) {
      if (refgleis == null || refgleis == c.setterGleis) {
         try {
            if (blink) {
               c.destinationDisplay.getFluentData().displayBlink(blink, 200);
            } else {
               c.destinationDisplay.getFluentData().displayClear(now);
            }
         } catch (Exception var6) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "display clear", var6);
         }

         c.registeredZug = null;
         c.setterGleis = null;
         if (c.autoremove) {
            this.bar.remove(c);
         }
      }
   }

   private void setDisplay(connector c, zug z) {
      this.setDisplay(null, c, z, false);
   }

   private void setDisplay(gleis refgleis, connector c, zug z) {
      this.setDisplay(refgleis, c, z, false);
   }

   private void setDisplay(connector c, zug z, boolean blink) {
      this.setDisplay(null, c, z, blink);
   }

   private void setDisplay(gleis refgleis, connector c, zug z, boolean blink) {
      try {
         if (z.isZugfahrt()) {
            c.destinationDisplay.getFluentData().displaySet(z.getSpezialName());
            if (blink) {
               c.destinationDisplay.getFluentData().displayBlink(blink, 2);
            }
         }

         if (refgleis != null) {
            c.setterGleis = refgleis;
         }
      } catch (Exception var6) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "display set", var6);
      }
   }

   public void status(gleis g) {
      LinkedList<connector> gl = this.findEntry(g, false);
      if (g.getFluentData().isFrei()) {
         for(connector c : gl) {
            this.clearDisplay(c, c.disableconnector);
         }
      }
   }

   public void zug(gleis g, zug z) {
      for(connector c : this.findEntry(g, false)) {
         if (c.disableconnector) {
            this.clearDisplay(c, true);
         } else if (z != null && g.getFluentData().getStatus() == 2) {
            connector zc = c;

            try {
               this.setDisplay(g, c, z);
            } catch (Exception var9) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "zug display", var9);
            }

            if (c.referenz != null) {
               zc = c.referenz;
            }

            if (c.reffs != null) {
               gleis stopsig = c.reffs.getStop();
               connector cstop = new connector(this.glbModel, stopsig);
               cstop.autoremove = true;
               cstop.destinationDisplay = c.destinationDisplay;
               cstop.disableconnector = true;
               cstop.reffs = c.reffs;
               cstop.registeredZug = z;
               this.bar.add(cstop);
            } else {
               zc.registeredZug = z;
            }

            if (c.autoremove) {
               this.bar.remove(c);
            }
         } else {
            this.clearDisplay(g, c, false);
         }
      }
   }

   public void fahrstrasse(gleis g, fahrstrasse fs) {
      if (fs != null) {
         if (g.getFluentData().isFrei()) {
            for(connector c : this.bar) {
               if (c.reffs == fs) {
                  c.registeredZug = null;
                  this.bar.remove(c);
               }
            }
         } else if (!fs.hasÜP() && !fs.isRangiermodus()) {
            LinkedList<connector> gl = this.findEntry(g, true);
            gleis startsig = fs.getStart();

            for(connector c : gl) {
               connector cstart = new connector(this.glbModel, startsig);
               cstart.autoremove = true;
               cstart.destinationDisplay = c.destinationDisplay;
               cstart.reffs = fs;
               cstart.referenz = c;
               this.bar.add(cstart);
               if (c.autoremove) {
                  this.bar.remove(c);
               }
            }
         }
      }
   }

   public void zug(zug z, displayBar.ZUGTRIGGER t) {
      switch(t) {
         case KUPPELN:
            for(connector c : this.bar) {
               if (c.registeredZug != z || !c.disableconnector && c.reffs == null) {
                  if (c.registeredZug == z) {
                     this.clearDisplay(c, false, true);
                  }
               } else {
                  this.clearDisplay(c, false, true);
               }
            }
            break;
         case RICHTUNG:
            for(connector c : this.bar) {
               if (c.registeredZug == z && c.reffs != null) {
                  this.clearDisplay(c, false);
               }
            }
            break;
         case NAME:
            for(connector c : this.bar) {
               if (c.registeredZug == z && c.reffs == null) {
                  try {
                     this.setDisplay(c, z, true);
                  } catch (Exception var6) {
                     Logger.getLogger("stslogger").log(Level.SEVERE, "zug trigger", var6);
                  }
               }
            }
      }
   }

   public void setLegacy(boolean l) {
      this.legacy = l;
      this.handleLegacy();
   }

   public boolean isLegacy() {
      return this.legacy;
   }

   public static enum ZUGTRIGGER {
      RICHTUNG,
      NAME,
      KUPPELN;
   }

   public static class listModel extends AbstractListModel {
      private displayBar my_main;
      private ArrayList<gleis> gllist = new ArrayList();

      listModel(displayBar m) {
         super();
         this.my_main = m;
      }

      public void refresh() {
         int index0 = this.gllist.size();
         this.gllist = this.my_main.getDisplays();
         Collections.sort(this.gllist, new Comparator() {
            public int compare(Object o1, Object o2) {
               gleis g1 = (gleis)o1;
               gleis g2 = (gleis)o2;
               int r = g1.getSWWert().compareToIgnoreCase(g2.getSWWert());
               if (r == 0) {
                  r = g1.compareToGleis(g2);
               }

               return r;
            }
         });
         int index1 = this.gllist.size();
         if (index1 > index0) {
            this.fireIntervalAdded(this, index1 - index0, index1);
         } else {
            this.fireIntervalRemoved(this, index0 - index1, index0);
         }

         this.fireContentsChanged(this, 0, index1);
      }

      public int getSize() {
         return this.gllist.size();
      }

      public Object getElementAt(int i) {
         gleis g = (gleis)this.gllist.get(i);
         return g.getSWWert() + " (" + g.getCol() + "/" + g.getRow() + ")";
      }

      public gleis getDisplay(int i) {
         try {
            return (gleis)this.gllist.get(i);
         } catch (IndexOutOfBoundsException var3) {
            return null;
         }
      }

      public LinkedList<connector> getDisplayData(int i) {
         try {
            gleis g = (gleis)this.gllist.get(i);
            return this.my_main.getDisplayData(g);
         } catch (IndexOutOfBoundsException var3) {
            return null;
         }
      }

      public int findIndex(gleis g) {
         return this.gllist.indexOf(g);
      }
   }
}
