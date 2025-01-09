package js.java.isolate.sim.eventsys;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import js.java.isolate.sim.structServ.structinfo;
import org.xml.sax.Attributes;

public class thema implements Comparable, structinfo {
   static TreeSet<thema> themen = new TreeSet();
   public String name = "";
   public String guiname = "";
   public themagruppe gruppe = null;
   public boolean sichtbar = false;
   public boolean voted = false;

   public static thema addThema(Attributes attrs) {
      thema r = new thema(attrs);
      themen.add(r);
      return r;
   }

   public static Iterator iterator() {
      return themen.iterator();
   }

   public static void clear() {
      themen.clear();
      themagruppe.gp.clear();
   }

   public static boolean isThema(String n) {
      for (thema t : themen) {
         if (t.name.equalsIgnoreCase(n)) {
            return true;
         }
      }

      return false;
   }

   public static boolean userVotedThema(String n) {
      for (thema t : themen) {
         if (t.name.equalsIgnoreCase(n)) {
            return t.voted;
         }
      }

      return false;
   }

   private thema(String n) {
      this.name = n;
   }

   private thema(Attributes attrs) {
      this.name = attrs.getValue("name");
      if (this.name == null) {
         this.name = "";
      }

      this.guiname = attrs.getValue("guiname");
      if (this.guiname == null) {
         this.guiname = "";
      }

      try {
         this.gruppe = themagruppe.getGruppe(attrs.getValue("gruppe"));
         this.sichtbar = attrs.getValue("sichtbar").equalsIgnoreCase("J");
      } catch (Exception var4) {
      }

      try {
         this.voted = attrs.getValue("voted").equalsIgnoreCase("true");
      } catch (Exception var3) {
      }
   }

   public int compareTo(Object o) {
      thema t = (thema)o;
      if (t.gruppe == this.gruppe) {
         return this.guiname.compareToIgnoreCase(t.guiname);
      } else if (t.gruppe != null && this.gruppe != null) {
         return this.gruppe.name.compareToIgnoreCase(t.gruppe.name);
      } else {
         return t.gruppe != null ? 1 : -1;
      }
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("name");
      v.addElement(this.name);
      v.addElement("guiname");
      v.addElement(this.guiname);
      return v;
   }

   @Override
   public String getStructName() {
      return "thema";
   }
}
