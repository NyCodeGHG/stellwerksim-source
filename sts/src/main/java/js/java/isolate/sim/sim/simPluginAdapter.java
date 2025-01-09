package js.java.isolate.sim.sim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.eventmsg;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.gleisbildWorker.elementConnectorFinder;
import js.java.isolate.sim.sim.plugin.pluginDataAdapter;
import js.java.isolate.sim.zug.zug;

class simPluginAdapter implements pluginDataAdapter {
   private final stellwerksim_main my_main;

   simPluginAdapter(stellwerksim_main m) {
      this.my_main = m;
   }

   @Override
   public String getAnlagenname() {
      return this.my_main.getGleisbild().getAnlagenname();
   }

   @Override
   public int getAid() {
      return this.my_main.getGleisbild().getAid();
   }

   @Override
   public String getRegion() {
      return this.my_main.getGleisbild().getRegion();
   }

   @Override
   public int getRid() {
      return this.my_main.getGleisbild().getRid();
   }

   @Override
   public int getBuild() {
      return this.my_main.getBuild();
   }

   @Override
   public Set<String> getAlleBahnsteige() {
      return this.my_main.getBahnsteige().getAlleBahnsteig();
   }

   @Override
   public Iterator<String> findNeighborBahnsteig(String bst) {
      return this.my_main.getBahnsteige().findNeighborBahnsteig(bst).iterator();
   }

   @Override
   public boolean bahnsteigIsHaltepunkt(String bst) {
      return this.my_main.getBahnsteige().bahnsteigIsHaltepunkt(bst);
   }

   @Override
   public Map<Integer, String> getZugList() {
      HashMap<Integer, String> ret = new HashMap();

      for (zug z : this.my_main.getZugList()) {
         if (!z.isFertig()) {
            ret.put(z.getZID_num(), z.getSpezialName());
         }
      }

      return ret;
   }

   @Override
   public pluginDataAdapter.zugDetails getZugDetails(int zid) {
      zug z = this.my_main.findZug(zid);
      if (z == null) {
         return null;
      } else {
         pluginDataAdapter.zugDetails ret = new pluginDataAdapter.zugDetails();
         ret.zid = z.getZID_num();
         ret.name = z.getSpezialName();
         ret.verspaetung = z.getVerspaetung_num();
         if (z.lastStopDone()) {
            ret.gleis = null;
            ret.plangleis = null;
         } else {
            ret.gleis = z.getZielGleis();
            ret.plangleis = z.getPlanGleis();
         }

         ret.von = z.getVon();
         ret.nach = z.getNach();
         ret.sichtbar = z.isVisible();
         ret.amgleis = z.amBahnsteig();
         ret.usertextsender = z.getUserTextSender();
         ret.usertext = z.getUserText();
         return ret;
      }
   }

   @Override
   public List<pluginDataAdapter.zugPlanLine> getAllUnseenFahrplanzeilen(int zid) {
      zug z = this.my_main.findZug(zid);
      if (z == null) {
         return null;
      } else {
         Iterator<zug> it = z.getAllUnseenFahrplanzeilen();
         LinkedList<pluginDataAdapter.zugPlanLine> ret = new LinkedList();

         while (it.hasNext()) {
            zug zz = (zug)it.next();
            if (zz != null) {
               pluginDataAdapter.zugPlanLine zpl = new pluginDataAdapter.zugPlanLine();
               zpl.plan = zz.getPlanGleis();
               zpl.name = zz.getZielGleis();
               zpl.an = zz.getAnkunft();
               zpl.ab = zz.getAbfahrt();
               zpl.flags = zz.getFlags().toString();
               zpl.hinweistext = z.getHinweistext();
               ret.add(zpl);
            }
         }

         return ret;
      }
   }

   @Override
   public void message(String msg) {
      this.my_main.message(msg, stellwerksim_main.MSGLEVELS.IMPORTANT);
   }

   @Override
   public long getSimutime() {
      return this.my_main.getSimutime();
   }

   @Override
   public void ircInject(String msg) {
      this.my_main.getGleisbild().IRCeventTrigger(msg);
   }

   @Override
   public Vector getStructInfo() {
      return this.my_main.getStructInfo();
   }

   @Override
   public String getZugFahrplanHTML(int zid) {
      String ret = null;
      zug z = this.my_main.findZug(zid);
      if (z != null) {
         ret = z.getDetails();
      }

      return ret;
   }

   @Override
   public pluginDataAdapter.pluginEventHandle registerEvent(
      final int zid, final pluginDataAdapter.EVENTKINDS kind, final pluginDataAdapter.pluginEventCallback callback
   ) {
      final eventGenerator.TYPES egt;
      if (kind == pluginDataAdapter.EVENTKINDS.EINFAHRT) {
         egt = eventGenerator.T_ZUG_EINFAHRT;
      } else if (kind == pluginDataAdapter.EVENTKINDS.AUSFAHRT) {
         egt = eventGenerator.T_ZUG_AUSFAHRT;
      } else if (kind == pluginDataAdapter.EVENTKINDS.ANKUNFT) {
         egt = eventGenerator.T_ZUG_ANKUNFT;
      } else if (kind == pluginDataAdapter.EVENTKINDS.ABFAHRT) {
         egt = eventGenerator.T_ZUG_ABFAHRT;
      } else if (kind == pluginDataAdapter.EVENTKINDS.ROTHALT) {
         egt = eventGenerator.T_ZUG_ROT;
      } else if (kind == pluginDataAdapter.EVENTKINDS.WURDEGRUEN) {
         egt = eventGenerator.T_ZUG_WURDEGRUEN;
      } else if (kind == pluginDataAdapter.EVENTKINDS.FLUEGELN) {
         egt = eventGenerator.T_ZUG_FLÜGELN;
      } else {
         if (kind != pluginDataAdapter.EVENTKINDS.KUPPELN) {
            return null;
         }

         egt = eventGenerator.T_ZUG_KUPPELN;
      }

      pluginDataAdapter.pluginEventHandle ret = null;
      final zug z = this.my_main.findZug(zid);
      if (z != null) {
         final eventGenerator.eventCall ev = new eventGenerator.eventCall() {
            @Override
            public boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
               callback.eventOccured(zid, kind);
               return true;
            }

            @Override
            public String funkName() {
               return null;
            }

            public int compareTo(Object o) {
               return 1;
            }
         };
         z.registerHook(eventGenerator.HOOKKIND.POSTINFO, egt, ev);
         ret = new pluginDataAdapter.pluginEventHandle() {
            @Override
            public void close() {
               z.unregisterHook(egt, ev);
            }
         };
      }

      return ret;
   }

   @Override
   public long getHeat() {
      return zug.getHeat();
   }

   @Override
   public String getRegionTel() {
      return this.my_main.getGleisbild().getPhonebook().getRegionTel();
   }

   @Override
   public String getAllgemeintel() {
      return this.my_main.getGleisbild().getPhonebook().getAllgemeintel();
   }

   @Override
   public List<pluginDataAdapter.pluginEventAdapter> getEvents() {
      LinkedList<pluginDataAdapter.pluginEventAdapter> l = new LinkedList();

      for (event e : event.events) {
         l.add(new simPluginAdapter.simEventAdapter(e));
      }

      return l;
   }

   @Override
   public List<pluginDataAdapter.WegElement> getWege() {
      elementConnectorFinder gw = new elementConnectorFinder((gleisbildModelFahrweg)this.my_main.getGleisbild().getModel(), this.my_main);
      elementConnectorFinder.analyser aa = new elementConnectorFinder.fullAnalyser();
      gw.run(aa);
      List<pluginDataAdapter.WegElement> ret = new LinkedList();
      gw.getFormatter().forEach(s -> {
         pluginDataAdapter.WegElement we = new pluginDataAdapter.WegElement("shape", s);
         ret.add(we);
      }, c -> {
         pluginDataAdapter.WegElement we = new pluginDataAdapter.WegElement("connector", c);
         ret.add(we);
      });
      return ret;
   }

   @Override
   public gleisbildModelSts getGleisbild() {
      return this.my_main.getGleisbild();
   }

   @Override
   public fsallocator getFSallocator() {
      return this.my_main.getFSallocator();
   }

   @Override
   public boolean isOnline() {
      return this.my_main.isBotMode();
   }

   private static class simEventAdapter implements pluginDataAdapter.pluginEventAdapter {
      private final event e;

      simEventAdapter(event e) {
         this.e = e;
      }

      @Override
      public String funkName() {
         return this.e.funkName();
      }

      @Override
      public String funkAntwort() {
         return this.e.funkAntwort();
      }
   }
}
