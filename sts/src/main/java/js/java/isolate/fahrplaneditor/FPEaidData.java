package js.java.isolate.fahrplaneditor;

import java.io.IOException;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

class FPEaidData implements Comparable, xmllistener {
   private final int aid;
   private final fahrplaneditor my_main;
   private final TreeSet<enritem> einfahrten = new TreeSet();
   private final TreeSet<enritem> ausfahrten = new TreeSet();
   private final String name;
   private String region = null;
   private final boolean sichtbar;
   private final boolean erlaubt;
   private final TreeSet<gleisData> gleise = new TreeSet();
   private boolean isValid = false;
   private boolean validate_match = false;
   private final enritem enr0A = new enritem("Keine (E/K-Flag)", 0);
   private final enritem enr0E = new enritem("Keine (E/F-Ziel)", 0);
   private int linecnt = 0;

   public FPEaidData(int _aid, fahrplaneditor m) {
      this.aid = _aid;
      this.my_main = m;
      this.name = m.getParameter("aidname" + this.aid);
      this.region = m.getParameter("aidregion" + this.aid);
      if (m.getParameter("aidsichtbar" + this.aid) != null) {
         this.sichtbar = m.getParameter("aidsichtbar" + this.aid).equalsIgnoreCase("J");
      } else {
         this.sichtbar = false;
         Logger.getLogger(FPEaidData.class.getName()).log(Level.SEVERE, "aid sichtbar für {0} fehlt, Fallback auf FALSE", this.aid);
      }

      if (m.getParameter("aid4user" + this.aid) != null) {
         this.erlaubt = m.getParameter("aid4user" + this.aid).equalsIgnoreCase("J");
      } else {
         this.erlaubt = false;
         Logger.getLogger(FPEaidData.class.getName()).log(Level.SEVERE, "aid 4user für {0} fehlt, Fallback auf FALSE", this.aid);
      }

      this.my_main.addRegion(this.region);
   }

   enritem getENR0() {
      return this.enr0A;
   }

   private void validate() {
      System.out.println("Validate " + this.aid);
      this.validate_match = false;
      this.my_main.startLoad();
      String u = this.my_main.getBhfDataUrl(this.aid);
      xmlreader xmlr = new xmlreader();
      xmlr.registerTag("aiddata", this);
      xmlr.registerTag("aid", this);

      try {
         xmlr.updateData(u, new StringBuffer());
         this.isValid = true;
      } catch (IOException var4) {
         System.out.println("Ex: " + var4.getMessage());
      }

      this.my_main.endLoad();
      System.out.println("Validate " + this.aid + " end");
   }

   public void makeValid() {
      if (!this.isValid) {
         this.validate();
      }
   }

   public int getAid() {
      return this.aid;
   }

   TreeSet<enritem> getEinfahrten() {
      if (!this.isValid) {
         this.validate();
      }

      return this.einfahrten;
   }

   TreeSet<enritem> getAusfahrten() {
      if (!this.isValid) {
         this.validate();
      }

      return this.ausfahrten;
   }

   public String getName() {
      return this.name;
   }

   public String getRegion() {
      return this.region;
   }

   TreeSet<gleisData> getGleise() {
      if (!this.isValid) {
         this.validate();
      }

      return this.gleise;
   }

   public String toString() {
      return this.name + " (" + this.aid + ")";
   }

   public int compareTo(Object o) {
      FPEaidData e = (FPEaidData)o;
      int r = this.region.compareToIgnoreCase(e.region);
      if (r == 0) {
         r = this.name.compareToIgnoreCase(e.name);
      }

      if (r == 0) {
         r = this.aid - e.aid;
      }

      return r;
   }

   public void parseStartTag(String tag, Attributes attrs) {
      if (tag.equalsIgnoreCase("aiddata")) {
         int a = Integer.parseInt(attrs.getValue("aid"));
         if (a == this.aid) {
            this.validate_match = true;
            this.ausfahrten.add(this.enr0A);
            this.einfahrten.add(this.enr0E);
            this.my_main.setLoad(0, Integer.parseInt(attrs.getValue("lines")));
            this.linecnt = 0;
         }
      } else if (this.validate_match && tag.equalsIgnoreCase("aid")) {
         this.linecnt++;
         this.my_main.setLoad(this.linecnt);
         enritem ei = null;
         int e = Integer.parseInt(attrs.getValue("element"));
         if (e == gleisElements.ELEMENT_BAHNSTEIG.getElement()) {
            gleisData g = new gleisData(attrs.getValue("swwert"), false);
            this.gleise.add(g);
         } else if (e == gleisElements.ELEMENT_HALTEPUNKT.getElement()) {
            gleisData g = new gleisData(attrs.getValue("swwert"), true);
            this.gleise.add(g);
         } else if (e == gleisElements.ELEMENT_AUSFAHRT.getElement()) {
            ei = new enritem(attrs.getValue("swwert"), Integer.parseInt(attrs.getValue("enr")));
            this.ausfahrten.add(ei);
         } else if (e == gleisElements.ELEMENT_EINFAHRT.getElement()) {
            ei = new enritem(attrs.getValue("swwert"), Integer.parseInt(attrs.getValue("enr")));
            this.einfahrten.add(ei);
         }
      }
   }

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
   }

   public boolean isSichtbar() {
      return this.sichtbar;
   }

   public boolean isErlaubt() {
      return this.erlaubt;
   }
}
