package js.java.isolate.sim.zug;

import java.util.Objects;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.eventmsg;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.sim.TEXTTYPE;

class c_isvorsignal extends baseChain {
   c_isvorsignal() {
      super();
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if ((
            gleis.ELEMENT_VORSIGNAL.matches(z.next_gl.getElement())
               || gleis.ELEMENT_SIGNAL.matches(z.next_gl.getElement()) && z.next_gl.getGleisExtend().isVorsignal()
         )
         && z.next_gl.forUs(z.pos_gl)) {
         gleis gl = z.next_gl;
         gleis signal = gl.getFluentData().getConnectedSignal();
         if (signal != null && signal.getElement() == gleisElements.ELEMENT_SIGNAL) {
            if (signal.getFluentData().getStellung().getZugStellung() == gleisElements.ZugStellungen.stop) {
               tl_vorsignal.add(z);
            } else if (!z.hasDataHookRegistered(eventGenerator.T_ZUG_ROT, signal)) {
               z.unregisterHookTypes(eventGenerator.T_ZUG_ROT, c_isvorsignal.signalEvent.class);
               z.registerHook(eventGenerator.T_ZUG_ROT, new c_isvorsignal.signalEvent(signal));
            }
         }
      }

      return false;
   }

   static class signalEvent implements eventGenerator.eventCall {
      private final gleis signal;

      signalEvent(gleis signal) {
         super();
         this.signal = signal;
      }

      @Override
      public boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
         zugmsg zm = (zugmsg)e;
         zm.z.unregisterHook(typ, this);
         if (zm.g == this.signal && this.signal.getFluentData().getStellung().getZugStellung() == gleisElements.ZugStellungen.stop) {
            zm.z.setNotbremsung();
            zm.z
               .my_main
               .showText(
                  "Kollege: Hier Zug "
                     + zm.z.getSpezialName()
                     + ". Vorsignal zu "
                     + this.signal.getShortElementName()
                     + " hatte Fahrt angekündigt, Signal selbst steht auf Halt. Habe Notbremsung eingeleitet! Zugspitze steht allerdings hinter dem Signal, Weiterfahrt kann nur per Befehl erfolgen.",
                  TEXTTYPE.ANRUF,
                  zm.z
               );
            return false;
         } else {
            return true;
         }
      }

      @Override
      public String funkName() {
         return null;
      }

      public int compareTo(Object o) {
         return 0;
      }

      public boolean equals(Object o) {
         if (o instanceof gleis) {
            gleis g = (gleis)o;
            return g == this.signal;
         } else {
            return o == this;
         }
      }

      public int hashCode() {
         int hash = 3;
         return 37 * hash + Objects.hashCode(this.signal);
      }
   }
}
