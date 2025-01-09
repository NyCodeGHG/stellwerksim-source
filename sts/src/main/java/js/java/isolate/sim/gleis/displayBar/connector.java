package js.java.isolate.sim.gleis.displayBar;

import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;
import js.java.tools.TextHelper;
import org.xml.sax.Attributes;

public class connector {
   gleis destinationDisplay = null;
   String swwert = null;
   boolean fsconnector = false;
   gleis sourceElement = null;
   gleis setterGleis = null;
   boolean autoremove = false;
   boolean disableconnector = false;
   fahrstrasse reffs = null;
   zug registeredZug = null;
   connector referenz = null;

   void cleanup() {
      this.referenz = null;
      this.registeredZug = null;
      this.reffs = null;
   }

   connector(gleisbildModel glbModel, gleis g) {
      this.sourceElement = g;
   }

   connector(gleisbildModel glbModel, gleis g, String swwert) {
      this.destinationDisplay = g;
      this.swwert = swwert;
   }

   connector(gleisbildModel glbModel, Attributes attrs) throws UnknownDisplayException, UndefinedSWWertException {
      String dest = attrs.getValue("destination");
      String ssw = attrs.getValue("source");
      String fsc = attrs.getValue("fsconnector");
      this.destinationDisplay = glbModel.findFirst(gleis.ALLE_ZUGDISPLAYS, dest);
      if (this.destinationDisplay == null) {
         throw new UnknownDisplayException(dest);
      } else if (ssw != null && !ssw.isEmpty()) {
         this.swwert = ssw;
         if (fsc != null) {
            this.fsconnector = Boolean.parseBoolean(fsc);
         }
      } else {
         throw new UndefinedSWWertException(dest);
      }
   }

   int saveData(StringBuffer data, String prefix) {
      int r = 0;
      if (this.destinationDisplay != null && (this.sourceElement != null || this.swwert != null) && !this.autoremove) {
         r = 1;
         data.append(prefix).append("[destination]=");
         data.append(TextHelper.urlEncode(this.destinationDisplay.getSWWert()));
         data.append('&');
         data.append(prefix).append("[source]=");
         data.append(TextHelper.urlEncode(this.swwert));
         data.append('&');
         if (this.fsconnector) {
            data.append(prefix).append("[fsconnector]=");
            data.append(TextHelper.urlEncode(Boolean.toString(this.fsconnector)));
            data.append('&');
         }
      }

      return r;
   }

   public boolean isFSconnector() {
      return this.fsconnector;
   }

   public String getSWwert() {
      return this.swwert;
   }

   public void setFSconnector(boolean selected) {
      this.fsconnector = selected;
   }
}
