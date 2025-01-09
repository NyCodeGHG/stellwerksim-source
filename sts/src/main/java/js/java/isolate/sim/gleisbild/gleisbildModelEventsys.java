package js.java.isolate.sim.gleisbild;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventHaeufigkeiten;
import js.java.isolate.sim.eventsys.thema;
import org.xml.sax.Attributes;

public class gleisbildModelEventsys extends gleisbildModelFahrweg {
   public LinkedList<eventContainer> events = new LinkedList();
   private eventHaeufigkeiten evh_instance = null;
   private eventContainer lastevent = null;

   public gleisbildModelEventsys(GleisAdapter _theapplet) {
      super(_theapplet);
   }

   @Override
   public void close() {
      super.close();
      this.events.clear();
      if (this.evh_instance != null) {
         String botmode = "?";
         StringWriter strOut = new StringWriter();
         PrintWriter out = new PrintWriter(strOut);
         Throwable var5 = null;

         boolean bot;
         try {
            try {
               botmode = this.evh_instance.getSim().wasBotMode() ? "online" : "sandbox";
               bot = this.evh_instance.getSim().wasBotMode();
            } catch (Exception var15) {
               var15.printStackTrace(out);
               bot = true;
            }

            this.evh_instance.dumpData(out);
         } catch (Throwable var16) {
            var5 = var16;
            throw var16;
         } finally {
            if (out != null) {
               if (var5 != null) {
                  try {
                     out.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  out.close();
               }
            }
         }

         if (bot) {
         }

         this.evh_instance.close();
      }

      this.evh_instance = null;
      this.lastevent = null;
   }

   @Override
   public void clear() {
      this.events.clear();
      super.clear();
   }

   public eventHaeufigkeiten getHaeufigkeiten() {
      if (this.evh_instance == null) {
         this.evh_instance = new eventHaeufigkeiten(this, this.theapplet.getSim());
      }

      return this.evh_instance;
   }

   public void IRCeventTrigger(String r) {
      this.IRCeventTrigger(r, false);
   }

   public void IRCeventTrigger(String r, boolean force) {
      if (this.theapplet.getSim() != null && (eventHaeufigkeiten.stoerungenein || force)) {
         new eventContainer(this, this.theapplet.getSim(), r, force);
      }
   }

   public Iterator<eventContainer> eventsIterator() {
      return this.events.iterator();
   }

   @Override
   protected void registerTags() {
      super.registerTags();
      this.xmlr.registerTag("stoerungen", this);
      this.xmlr.registerTag("stoerung", this);
      this.xmlr.registerTag("selement", this);
      this.xmlr.registerTag("themen", this);
      this.xmlr.registerTag("thema", this);
   }

   @Override
   public void parseStartTag(String tag, Attributes attrs) {
      if (tag.compareTo("stoerungen") == 0) {
         this.events = new LinkedList();
         this.theapplet.setProgress(80);
         this.lastevent = null;
      } else if (tag.compareTo("stoerung") == 0) {
         this.lastevent = new eventContainer(this, attrs);
      } else {
         super.parseStartTag(tag, attrs);
      }
   }

   @Override
   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
      if (tag.compareTo("stoerung") == 0) {
         this.lastevent = null;
      } else if (tag.compareTo("stoerungen") == 0) {
         this.theapplet.setProgress(90);
      } else if (tag.compareTo("selement") == 0) {
         if (this.lastevent != null) {
            this.lastevent.addValue(attrs, pcdata);
         }
      } else if (tag.compareTo("themen") != 0) {
         if (tag.compareTo("thema") == 0) {
            thema.addThema(attrs);
         } else {
            super.parseEndTag(tag, attrs, pcdata);
         }
      }
   }

   @Override
   protected StringBuffer createSaveData(StringBuffer data) {
      data = super.createSaveData(data);
      if (this.events == null) {
         data.append("stoerungen=0&");
      } else {
         int i = 0;
         data.append("stoerungen=").append(this.events.size()).append("&");

         for (eventContainer e : this.events) {
            e.saveData(data, "stoerung[" + i + "]");
            i++;
         }
      }

      return data;
   }
}
