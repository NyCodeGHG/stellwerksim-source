package js.java.isolate.statusapplet.players;

import java.util.concurrent.ConcurrentHashMap;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModelStore;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.tools.gui.dataTransferDisplay.DataTransferDisplayComponent;

public class players_gleisbildModel extends gleisbildModelSts {
   private boolean loaded = false;
   private final players_aid paid;
   ConcurrentHashMap<Integer, players_gleisbildModel.zugdata> zuege = new ConcurrentHashMap();

   players_gleisbildModel(GleisAdapter m, players_aid aid) {
      super(m);
      this.paid = aid;
   }

   @Override
   protected gleis createGleis() {
      return new players_gleis(this.theapplet, this);
   }

   public boolean loadIfNeeded(DataTransferDisplayComponent mon) {
      this.mon = mon;
      if (!this.loaded) {
         this.reload();
      } else {
         this.theapplet.setProgress(100);
      }

      return this.loaded;
   }

   public void reload() {
      this.loaded = false;
      this.theapplet.setProgress(-1);
      this.load(this.theapplet.getParameter("anlagenlesenbase") + "instanz=0&aid=" + this.paid.aid, new gleisbildModelStore.ioDoneMessage() {
         @Override
         public void done(boolean success) {
            players_gleisbildModel.this.theapplet.setProgress(100);
            players_gleisbildModel.this.loaded = success;
            players_gleisbildModel.this.repaint();
         }
      });
   }

   public void ping() {
      this.ping(this.theapplet.getParameter("anlagenlesenbase") + "ping=1");
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public void totalClear() {
      this.zuege.clear();
      this.events.clear();
      this.clearFahrwege();
      this.gl_resize(1, 1);
   }

   public static class zugdata {
      players_zug zug;
      int x = 0;
      int y = 0;
      gleisElements.RICHTUNG richtung = gleisElements.RICHTUNG.right;
      boolean passed = false;
      int paint_last_x = -1;
      int paint_last_y = -1;
      int paint_last_rot = 0;

      public zugdata(players_zug z) {
         super();
         this.zug = z;
      }
   }
}
