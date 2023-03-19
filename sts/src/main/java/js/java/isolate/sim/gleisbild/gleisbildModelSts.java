package js.java.isolate.sim.gleisbild;

import java.net.URL;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.structServ.structinfo;
import js.java.tools.dialogs.htmlmessage1;
import org.xml.sax.Attributes;

public class gleisbildModelSts extends gleisbildModelPhone implements structinfo {
   public gleisbildModelSts(GleisAdapter _theapplet) {
      super(_theapplet);
   }

   @Override
   protected void registerTags() {
      super.registerTags();
      this.xmlr.registerTag("meldungen", this);
   }

   @Override
   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
      if (tag.compareTo("meldungen") == 0) {
         if (attrs.getValue("url") != null) {
            try {
               URL u = new URL(attrs.getValue("url"));
               htmlmessage1 d = new htmlmessage1(new JFrame(), false, "Meldungen", u);
               d.show(400, 400);
            } catch (Exception var6) {
            }
         } else if (attrs.getValue("msg") != null) {
            htmlmessage1 d = new htmlmessage1(new JFrame(), false, "Hinweis", attrs.getValue("msg"));
            d.show(400, 400);
         }
      } else {
         super.parseEndTag(tag, attrs, pcdata);
      }
   }

   public Vector getStructInfo() {
      Vector ret = new Vector();
      Vector v = new Vector();
      v.addElement("gleisbild");
      v.addElement("glbModel");
      v.addElement(this);
      ret.add(v);
      v = new Vector();
      v.addElement("Eventsys");
      v.addElement("event häufigkeiten");
      v.addElement(this.getHaeufigkeiten());
      ret.add(v);

      for(eventContainer e : this.events) {
         try {
            Vector vx = e.getStructInfo();
            ret.addElement(vx);
         } catch (ClassCastException var9) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "getStructInfo caught", var9);
         }
      }

      for(event e : event.events) {
         try {
            Vector vx = e.getStructInfo();
            ret.addElement(vx);
         } catch (ClassCastException var8) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "getStructInfo caught", var8);
         }
      }

      for(fahrstrasse e : this.fahrwege) {
         try {
            Vector vx = e.getStructInfo();
            ret.addElement(vx);
         } catch (ClassCastException var7) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "getStructInfo caught", var7);
         }
      }

      return ret;
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("aid");
      v.addElement(this.getAid() + "/" + this.getAnlagenname());
      v.addElement("Einfahrten");
      v.addElement("waytime");

      for(Entry<String, Integer> e : this.allWayTimeTable.entrySet()) {
         v.addElement(e.getKey());
         v.addElement(e.getValue() + "");
      }

      v.addElement("Ausfahrten");
      v.addElement("waytime");

      for(Entry<String, Integer> e : this.allWayTimeTableA.entrySet()) {
         v.addElement(e.getKey());
         v.addElement(e.getValue() + "");
      }

      v.addElement("Einfahrten");
      v.addElement("Verspätung");

      for(Entry<String, Integer> e : this.allVerspaetungTable.entrySet()) {
         v.addElement(e.getKey());
         v.addElement(e.getValue() + "");
      }

      return v;
   }

   @Override
   public String getStructName() {
      return "gleisbildModel";
   }
}
