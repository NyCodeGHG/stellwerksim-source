package js.java.isolate.sim.sim.plugin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.sim.fsallocator;

public interface pluginDataAdapter {
   String getAnlagenname();

   int getAid();

   String getRegion();

   int getRid();

   int getBuild();

   Set<String> getAlleBahnsteige();

   boolean bahnsteigIsHaltepunkt(String var1);

   Iterator<String> findNeighborBahnsteig(String var1);

   Map<Integer, String> getZugList();

   pluginDataAdapter.zugDetails getZugDetails(int var1);

   pluginDataAdapter.pluginEventHandle registerEvent(int var1, pluginDataAdapter.EVENTKINDS var2, pluginDataAdapter.pluginEventCallback var3);

   List<pluginDataAdapter.zugPlanLine> getAllUnseenFahrplanzeilen(int var1);

   String getZugFahrplanHTML(int var1);

   long getSimutime();

   void message(String var1);

   void ircInject(String var1);

   Vector getStructInfo();

   List<pluginDataAdapter.pluginEventAdapter> getEvents();

   long getHeat();

   String getRegionTel();

   String getAllgemeintel();

   List<pluginDataAdapter.WegElement> getWege();

   gleisbildModelSts getGleisbild();

   fsallocator getFSallocator();

   boolean isOnline();

   public static enum EVENTKINDS {
      EINFAHRT,
      AUSFAHRT,
      ANKUNFT,
      ABFAHRT,
      ROTHALT,
      WURDEGRUEN,
      FLUEGELN,
      KUPPELN;
   }

   public static class WegElement {
      public final String xmlelement;
      public final Map<String, String> attrs;

      public WegElement(String xmlelement, Map<String, String> attrs) {
         super();
         this.xmlelement = xmlelement;
         this.attrs = attrs;
      }
   }

   public interface pluginEventAdapter {
      String funkName();

      String funkAntwort();
   }

   public interface pluginEventCallback {
      void eventOccured(int var1, pluginDataAdapter.EVENTKINDS var2);
   }

   public interface pluginEventHandle {
      void close();
   }

   public static class zugDetails {
      public int zid;
      public String name;
      public int verspaetung;
      public String gleis;
      public String plangleis;
      public String von;
      public String nach;
      public boolean sichtbar;
      public boolean amgleis;
      public String usertext;
      public String usertextsender;

      public zugDetails() {
         super();
      }
   }

   public static class zugPlanLine {
      public String plan;
      public String name;
      public String an;
      public String ab;
      public String flags;
      public String hinweistext;

      public zugPlanLine() {
         super();
      }
   }
}
