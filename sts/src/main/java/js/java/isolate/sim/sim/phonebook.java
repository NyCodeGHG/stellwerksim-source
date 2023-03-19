package js.java.isolate.sim.sim;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelPhone;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.TextHelper;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

public class phonebook extends currentPlayers implements Iterable<phonebookentry>, xmllistener, SessionClose {
   private final gleisbildModelPhone glbModel;
   private final GleisAdapter sim;
   private phonebookpanel ph_panel = null;
   private final ConcurrentHashMap<String, phonebookentry> book = new ConcurrentHashMap();
   private String owntel = null;
   private String ownfulltel = null;
   private String usertel = null;
   private String regiontel = null;
   private String regioninstanz = null;
   private String regionname = null;
   private String allgemeintel = null;
   private String allgemeinname = null;
   private final TreeMap<String, String> additionals = new TreeMap();
   private boolean canCall = false;

   public phonebook(gleisbildModelPhone gb, GleisAdapter sim) {
      super();
      this.glbModel = gb;
      this.sim = sim;
      this.canCall = sim.getParameter("callurl") != null;
   }

   @Override
   public void close() {
      this.additionals.clear();
      this.book.clear();
   }

   public void setTel(String aid, String tel, String stw, String user) {
      this.setStellwerk(Integer.parseInt(aid), stw, user);
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_AIDDISPLAY, aid});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();
         if (user != null) {
            boolean wasEmpty = gl.getFluentData().displayGetValue().isEmpty();
            gl.getFluentData().displaySet(tel);
            if (wasEmpty) {
               gl.getFluentData().displayBlink(true, 3);
            }
         } else {
            gl.getFluentData().displayClear(true);
         }
      }

      if (user == null) {
         this.book.remove(aid);
      } else {
         this.book.put(aid, new phonebookentry(aid, tel, stw, user));
      }

      if (this.ph_panel != null) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               phonebook.this.ph_panel.updateTel();
            }
         });
      }
   }

   public Iterator<phonebookentry> iterator() {
      return this.book.values().iterator();
   }

   public void setTel(String aid, String tel, String stw) {
      this.setTel(aid, tel, stw, null);
   }

   public void setGui(phonebookpanel a) {
      this.ph_panel = a;
   }

   void setRegion(String tel, String instanz, String name) {
      this.regiontel = tel;
      this.regioninstanz = instanz;
      this.regionname = name;
      Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_AIDDISPLAY});

      while(it.hasNext()) {
         gleis gl = (gleis)it.next();

         try {
            boolean rdisplay = gl.getSWWert().equals("REGION");
            if (rdisplay) {
               gl.getFluentData().displaySet(tel);
               gl.getFluentData().displayBlink(true, 3);
            }
         } catch (Exception var7) {
         }
      }
   }

   void setOwn(String tel) {
      this.owntel = tel;
   }

   public String getRegionTel() {
      return this.regiontel;
   }

   public String getRegionInstanz() {
      return this.regioninstanz;
   }

   public String getRegionName() {
      return this.regionname;
   }

   public String getOwn() {
      return this.owntel;
   }

   public String getUsertel() {
      return this.usertel;
   }

   public void xml(Attributes attrs) {
      String typ = attrs.getValue("typ");
      if (typ.equals("user")) {
         String aid = attrs.getValue("aid");
         String tel = attrs.getValue("tel");
         String stw = attrs.getValue("stw");
         String netz = attrs.getValue("netz");
         String user = attrs.getValue("user");
         this.setTel(aid, tel, stw, user);
      } else if (typ.equals("region")) {
         String tel = attrs.getValue("tel");
         String instanz = attrs.getValue("instanz");
         String name = attrs.getValue("name");
         this.setRegion(tel, instanz, name);
      } else if (typ.equals("self")) {
         this.owntel = attrs.getValue("tel");
         this.ownfulltel = attrs.getValue("fulltel");
         this.usertel = attrs.getValue("usertel");
      } else if (typ.equals("open")) {
         this.allgemeintel = attrs.getValue("tel");
         this.allgemeinname = attrs.getValue("name");
      } else if (typ.equals("additional")) {
         String k = attrs.getValue("name");
         String v = attrs.getValue("tel");
         this.additionals.put(k, v);
      }
   }

   public String getAllgemeintel() {
      return this.allgemeintel;
   }

   public String getAllgemeinname() {
      return this.allgemeinname;
   }

   public TreeMap<String, String> getAdditionals() {
      return this.additionals;
   }

   public boolean canCall() {
      return this.canCall;
   }

   public void call(String tel) {
      Thread t = new Thread(new phonebook.dialRunner(this, tel));
      t.setName("dialRunner");
      t.start();
   }

   public void callAtStart() {
      if (!this.regiontel.isEmpty()) {
         this.call(this.regiontel);
      }
   }

   public void parseStartTag(String tag, Attributes attrs) {
   }

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
   }

   private class dialRunner implements Runnable {
      private final String tel;
      private final phonebook pb;

      dialRunner(phonebook p, String tel) {
         super();
         this.tel = tel;
         this.pb = p;
      }

      public void run() {
         String u = phonebook.this.sim.getParameter("callurl");
         if (u != null) {
            u = u + TextHelper.urlEncode(this.tel);

            try {
               xmlreader xmlr = new xmlreader();
               xmlr.registerTag("result", this.pb);
               xmlr.updateData(u);
            } catch (Exception var3) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "call url", var3);
            }
         }
      }
   }
}
