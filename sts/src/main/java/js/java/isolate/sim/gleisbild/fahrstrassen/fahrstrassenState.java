package js.java.isolate.sim.gleisbild.fahrstrassen;

import java.util.EnumSet;
import java.util.Map.Entry;
import js.java.isolate.sim.trigger;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.sim.fsallocator;

public abstract class fahrstrassenState {
   protected fahrstrasseSelection myfs;

   protected fahrstrassenState(fahrstrasseSelection f) {
      super();
      this.myfs = f;
   }

   abstract boolean ping();

   abstract boolean stateAllowsState(fahrstrassenState var1);

   protected fsallocator getFSallocator() {
      return this.myfs.my_main.getFSallocator();
   }

   protected fahrstrasse getFS() {
      return this.myfs.getFahrstrasse();
   }

   protected gleis getStart() {
      return this.myfs.getStart();
   }

   protected gleis getStop() {
      return this.myfs.getStop();
   }

   protected gleis getLastGleis() {
      return this.myfs.getFahrstrasse().lastGleis;
   }

   protected boolean hasÜP() {
      return this.myfs.hasÜP();
   }

   protected boolean setNextState(fahrstrassenState s) {
      if (!this.myfs.getFahrstrasse().allocState.stateAllowsState(s)) {
         if (fahrstrasse.isDebug()) {
            fahrstrasse.debugMode.writeln("setNextState(" + s.toString() + ") from " + this.myfs.getFahrstrasse().allocState.toString() + " failed");
         }

         return false;
      } else {
         for(Entry<fahrstrasseSelection.ChangeHook, EnumSet<fahrstrasseSelection.StateChangeTypes>> m : this.myfs.hooks.entrySet()) {
            for(fahrstrasseSelection.StateChangeTypes t : (EnumSet)m.getValue()) {
               if (t.match(this.myfs.getFahrstrasse().allocState, s)) {
                  this.myfs.callHook((fahrstrasseSelection.ChangeHook)m.getKey());
                  break;
               }
            }
         }

         this.myfs.getFahrstrasse().allocState = s;
         if (fahrstrasse.isDebug()) {
            fahrstrasse.debugMode.writeln("setNextState(" + s.toString() + ") from " + this.myfs.getFahrstrasse().allocState.toString() + " success");
         }

         return true;
      }
   }

   protected void tjm_add(trigger t) {
      this.myfs.getFahrstrasse().tjmAdd(t);
   }

   public String toString() {
      return "[" + this.getClass().getSimpleName() + "]";
   }
}
