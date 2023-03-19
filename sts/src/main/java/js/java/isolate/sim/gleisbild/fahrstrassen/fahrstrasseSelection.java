package js.java.isolate.sim.gleisbild.fahrstrassen;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map.Entry;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;

public class fahrstrasseSelection {
   private final fahrstrasse fs;
   private boolean rangierModus;
   final HashMap<fahrstrasseSelection.ChangeHook, EnumSet<fahrstrasseSelection.StateChangeTypes>> hooks = new HashMap();
   private fahrstrasseSelection.ChangeHook hookToCall = null;
   private volatile boolean holdCall = false;
   final GleisAdapter my_main;

   public fahrstrasseSelection(fahrstrasse fs, boolean rangierModus, GleisAdapter my_main) {
      super();
      this.fs = fs;
      this.rangierModus = rangierModus;
      this.my_main = my_main;
   }

   public void addHook(fahrstrasseSelection.ChangeHook hook, EnumSet<fahrstrasseSelection.StateChangeTypes> s) {
      this.hooks.put(hook, s);
   }

   public boolean isRangiermodus() {
      return this.rangierModus;
   }

   public fahrstrasse getFahrstrasse() {
      return this.fs;
   }

   public gleis getStart() {
      return this.fs.getStart();
   }

   public gleis getStop() {
      return this.fs.getStop();
   }

   public boolean hasÜP() {
      return this.fs.hasÜP();
   }

   public boolean isValid() {
      if (this.fs.getExtend().isDeleted()) {
         return false;
      } else if (this.rangierModus && !this.fs.allowsRf()) {
         return false;
      } else if (!this.rangierModus && this.fs.getExtend().fstype == 8) {
         this.rangierModus = true;
         return this.isValid();
      } else {
         return true;
      }
   }

   public boolean get(boolean allowFSspeicher) {
      boolean ret = false;
      this.holdCall = true;
      if (this.isValid()) {
         fasChecker ns;
         if (this.rangierModus) {
            ns = new fasIsFreeRf(this);
         } else {
            ns = new fasIsFree(this);
         }

         if (this.fs.allocState.stateAllowsState(ns) && this.fs.checkGetHook()) {
            this.fs.rangierlänge = 0;
            this.fs.allocState = ns;
            ret = ns.check();
         }
      }

      this.hookToCall = null;
      if (ret) {
         this.getStart().getFluentData().clear_FW_speicher();
         this.fireHook(fahrstrasseSelection.StateChangeTypes.GOT_FS);
      } else if (allowFSspeicher) {
         if (!this.rangierModus && this.getStart().getFluentData().get_FW_speicher() == null) {
            this.getStart().getFluentData().set_FW_speicher(this.fs);
         } else {
            this.fireHook(fahrstrasseSelection.StateChangeTypes.CANTGET_FS);
         }
      } else {
         this.fireHook(fahrstrasseSelection.StateChangeTypes.CANTGET_FS);
      }

      this.holdCall = false;
      this.callHook();
      return ret;
   }

   public boolean free() {
      boolean ret = false;
      fasChecker ns = new fasCanFreeFS(this);
      if (this.fs.allocState.stateAllowsState(ns)) {
         if (this.fs.checkFreeHook()) {
            this.fs.allocState = ns;
            ret = ns.check();
         }
      } else {
         ns = new fasCanFreeRf(this);
         if (this.fs.allocState.stateAllowsState(ns) && this.fs.checkFreeHook()) {
            this.fs.allocState = ns;
            ret = ns.check();
         }
      }

      return ret;
   }

   private void fireHook(fahrstrasseSelection.StateChangeTypes sct) {
      for(Entry<fahrstrasseSelection.ChangeHook, EnumSet<fahrstrasseSelection.StateChangeTypes>> m : this.hooks.entrySet()) {
         if (((EnumSet)m.getValue()).contains(sct)) {
            this.callHook((fahrstrasseSelection.ChangeHook)m.getKey());
         }
      }
   }

   void callHook(fahrstrasseSelection.ChangeHook hk) {
      if (this.holdCall) {
         this.hookToCall = hk;
      } else {
         this.hookToCall = null;
         hk.call(this);
      }
   }

   private void callHook() {
      if (this.hookToCall != null) {
         this.hookToCall.call(this);
         this.hookToCall = null;
      }
   }

   public interface ChangeHook {
      void call(fahrstrasseSelection var1);
   }

   public static enum StateChangeTypes {
      GOT_FS(fasWaitFS.class, fasFSSet.class),
      CANTGET_FS(fasWaitFS.class, fasCanFreeFS.class),
      ERROR_GETFS(fahrstrassenState.class, fasNullState.class),
      GOT_RF(fasWaitRf.class, fasRfSet.class),
      CANTGET_RF(fasWaitRf.class, fasCanFreeRf.class),
      ERROR_GETRF(fahrstrassenState.class, fasNullState.class);

      private final Class<? extends fahrstrassenState> from;
      private final Class<? extends fahrstrassenState> to;

      private StateChangeTypes(Class<? extends fahrstrassenState> from, Class<? extends fahrstrassenState> to) {
         this.from = from;
         this.to = to;
      }

      public boolean match(fahrstrassenState from, fahrstrassenState to) {
         return this.from.getSimpleName().equals(from.getClass().getSimpleName()) && this.to.getSimpleName().equals(to.getClass().getSimpleName());
      }
   }
}
