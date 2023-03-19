package js.java.isolate.sim.sim;

import java.util.concurrent.ConcurrentHashMap;
import js.java.isolate.sim.sim.redirectInfo.RedirectStellwerkInfo;

public class currentPlayers implements RedirectStellwerkInfo {
   private ConcurrentHashMap<Integer, currentPlayers.aidData> aidMap = new ConcurrentHashMap();

   public currentPlayers() {
      super();
   }

   @Override
   public String getStellwerkName(int aid) {
      currentPlayers.aidData d = (currentPlayers.aidData)this.aidMap.get(aid);
      String name;
      if (d == null) {
         name = "AID " + aid;
      } else {
         name = d.name;
      }

      return name;
   }

   @Override
   public boolean isStellwerkUsed(int aid) {
      currentPlayers.aidData d = (currentPlayers.aidData)this.aidMap.get(aid);
      boolean ret;
      if (d == null) {
         ret = false;
      } else {
         ret = d.user != null;
      }

      return ret;
   }

   public void setStellwerk(int aid, String name, String user) {
      currentPlayers.aidData d = (currentPlayers.aidData)this.aidMap.get(aid);
      if (d == null) {
         d = new currentPlayers.aidData(aid, name);
         this.aidMap.put(aid, d);
      }

      d.user = user;
   }

   @Override
   public void addStellwerk(int aid, String aname) {
      currentPlayers.aidData d = (currentPlayers.aidData)this.aidMap.get(aid);
      if (d == null) {
         d = new currentPlayers.aidData(aid, aname);
         this.aidMap.put(aid, d);
      }
   }

   protected static class aidData {
      public final int aid;
      public final String name;
      public String user;

      private aidData(int aid, String name) {
         super();
         this.aid = aid;
         this.name = name;
         this.user = null;
      }
   }
}
