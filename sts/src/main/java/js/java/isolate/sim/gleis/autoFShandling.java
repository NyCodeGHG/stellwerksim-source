package js.java.isolate.sim.gleis;

import java.awt.Color;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;

public class autoFShandling {
   private boolean autoFW = false;
   private boolean triggeredFW = false;
   private boolean setFS = false;
   private int fw_start_count = 0;
   private int fw_stop_count = 0;
   private fahrstrasse my_fahrstrasse = null;
   private fahrstrasse manualFS = null;
   private int manualFSdestination = 0;
   private long lastautofw = 0L;
   private static int manualFScount = 0;
   private final gleis my_gleis;
   private boolean uepExplizid = false;
   private static final int AUTO_NO = 0;
   private static final int AUTO_OK_GRÜN = 1;
   private static final int AUTO_OK_ROT = 4;
   private static final int AUTO_EXPLIZIT = 2;
   private static final int AUTO_ÜPEXPLIZIT = 8;

   public void setÜPExplizid(boolean e) {
      this.uepExplizid = e;
   }

   autoFShandling(gleis parent) {
      this.my_gleis = parent;
   }

   void init() {
      this.autoFW = false;
      this.triggeredFW = false;
      this.setFS = false;
      this.fw_start_count = 0;
      this.fw_stop_count = 0;
      this.my_fahrstrasse = null;
      this.manualFS = null;
      this.manualFSdestination = 0;
      this.uepExplizid = false;
   }

   void clear() {
      this.autoFW = false;
      this.triggeredFW = false;
      this.setFS = false;
      this.manualFS = null;
      this.manualFSdestination = 0;
      this.uepExplizid = false;
   }

   Color getLedColor() {
      if (this.triggeredFW) {
         if (this.setFS && !gleis.blinkon_3er) {
            return gleis.colors.col_stellwerk_frei;
         } else {
            return this.manualFS != null ? gleis.colors.col_stellwerk_gruenein : gleis.colors.col_stellwerk_defekt;
         }
      } else {
         return this.autoFW ? gleis.colors.col_stellwerk_weiss : gleis.colors.col_stellwerk_frei;
      }
   }

   Color getLedEndColor() {
      return this.manualFSdestination > 0 ? gleis.colors.col_stellwerk_gruenein : gleis.colors.col_stellwerk_frei;
   }

   Color getRandColor() {
      return (this.canAutoFW() & 4) > 0 ? gleis.colors.col_stellwerk_geländer : null;
   }

   boolean hasAutoFSled() {
      return (this.canAutoFW() & 15) > 0 || this.manualFS != null;
   }

   private int canAutoFW() {
      int ret = 0;
      if (this.my_gleis.telement == gleis.ELEMENT_SIGNAL && this.my_fahrstrasse != null && this.my_fahrstrasse.getStop().telement != gleis.ELEMENT_ZWERGSIGNAL) {
         if (this.my_fahrstrasse.getExtend().getFSType() == 0
            && this.fw_start_count == 1
            && !this.my_fahrstrasse.hasÜP()
            && this.my_fahrstrasse.getStop().autoFW.fw_stop_count == 1
            && this.my_fahrstrasse.countWeichen() == 0
            && this.my_fahrstrasse.countSignale() == 0
            && !this.my_fahrstrasse.hasElements(fahrstrasse.ELEMENTS_BÜ_EIN_KREUZ)) {
            ret = ret | 1 | 4;
         } else if (this.my_fahrstrasse.getExtend().getFSType() == 0
            && this.fw_start_count == 1
            && this.my_fahrstrasse.hasÜP()
            && this.my_fahrstrasse.getStop().autoFW.fw_stop_count == 1
            && this.my_fahrstrasse.countWeichen() == 0
            && this.my_fahrstrasse.countSignale() == 0
            && !this.my_fahrstrasse.hasElements(fahrstrasse.ELEMENTS_BÜ_EIN_KREUZ)) {
            ret |= 4;
         } else if (this.fw_start_count == 1
            && this.my_fahrstrasse.getStop().autoFW.fw_stop_count == 1
            && this.my_fahrstrasse.getStop().getElement().matches(gleis.ELEMENT_AUSFAHRT)
            && this.my_fahrstrasse.countWeichen() == 0
            && this.my_fahrstrasse.countSignale() == 0
            && !this.my_fahrstrasse.hasElements(fahrstrasse.ELEMENTS_BÜ_KREUZ)) {
            ret = ret | (this.uepExplizid ? 4 : 0) | 8;
         }

         if (this.my_fahrstrasse.getExtend().getFSType() == 2) {
            ret = ret | 1 | 2 | 4;
         }

         if (this.fw_start_count == 1
            && this.fw_stop_count >= 0
            && !this.my_fahrstrasse.hasÜP()
            && this.my_fahrstrasse.getStop().autoFW.fw_stop_count == 1
            && this.my_fahrstrasse.countWeichen() >= 0
            && this.my_fahrstrasse.countSignale() >= 0
            && !this.my_fahrstrasse.hasElements(fahrstrasse.ELEMENTS_BÜ_EIN)) {
            ret |= 4;
         }
      }

      return ret;
   }

   public void incFWcount(fahrstrasse f) {
      if (!f.getExtend().isDeleted()) {
         this.fw_start_count++;
         if (f.getExtend().getFSType() == 2) {
            this.my_fahrstrasse = f;
         } else if (f.getExtend().getFSType() == 0 && this.my_fahrstrasse == null) {
            this.my_fahrstrasse = f;
         }

         f.getStop().autoFW.fw_stop_count++;
      }
   }

   boolean isAutoFWenabled() {
      return this.autoFW;
   }

   private void disableManualDestination() {
      try {
         this.manualFS.getStop().autoFW.manualFSdestination--;
         if (this.manualFS.getStop().autoFW.manualFSdestination < 0) {
            this.manualFS.getStop().autoFW.manualFSdestination = 0;
         }
      } catch (NullPointerException var2) {
      }
   }

   private void enableManualDestination() {
      try {
         this.manualFS.getStop().autoFW.manualFSdestination++;
      } catch (NullPointerException var2) {
      }
   }

   void disableAutoFW() {
      if (this.manualFS != null) {
         manualFScount--;
         if (manualFScount < 0) {
            manualFScount = 0;
         }
      }

      this.autoFW = false;
      this.triggeredFW = false;
      this.setFS = false;
      this.disableManualDestination();
      this.manualFS = null;
      this.uepExplizid = false;
   }

   void setTriggeredAutoFW(fahrstrasse f) {
      this.disableManualDestination();
      if (manualFScount < ((gleisbildModelFahrweg)this.my_gleis.getParentGleisbild()).countFahrwege() / 6) {
         if (this.manualFS == null) {
            manualFScount++;
         }

         this.autoFW = true;
         this.triggeredFW = true;
         this.manualFS = f;
         this.enableManualDestination();
         this.my_gleis.tjmAdd();
      }
   }

   void clearTriggeredAutoFW() {
      if (this.manualFS != null) {
         this.disableAutoFW();
      }
   }

   private void enableAutoFWimpl(boolean triggered) {
      this.disableManualDestination();
      this.manualFS = null;
      this.autoFW = true;
      this.triggeredFW = triggered;
      this.setFS = !this.triggeredFW;
      this.my_gleis.tjmAdd();
   }

   void enableAutoFW(boolean triggered) {
      int v = this.canAutoFW();
      if ((v & 7) > 0) {
         this.enableAutoFWimpl(triggered && (v & 4) > 0);
      }
   }

   @Deprecated
   void toggleAutoFW(boolean triggered) {
      int v = this.canAutoFW();
      if ((v & 7) > 0) {
         triggered = triggered && (v & 4) > 0;
         if (this.autoFW || ((v & 4) <= 0 || !triggered) && ((v & 1) <= 0 || triggered)) {
            if (this.triggeredFW == triggered) {
               this.disableAutoFW();
            } else if ((v & 4) > 0 && triggered || (v & 1) > 0 && !triggered) {
               this.enableAutoFWimpl(triggered);
            }
         } else {
            this.enableAutoFWimpl(triggered);
         }
      }
   }

   void triggerStartingFS() {
      if (this.autoFW) {
         this.setFS = !this.triggeredFW;
      }
   }

   void triggerApproachingFS() {
      if (this.autoFW) {
         this.setFS = this.setFS | this.triggeredFW;
         if (this.setFS && this.triggeredFW) {
            if (this.my_gleis.getFluentData().getStellung() != gleisElements.ST_SIGNAL_GRÜN && this.my_gleis.fdata.fsspeicher == null) {
               this.my_gleis.tjmAdd();
            } else {
               this.setFS = false;
            }
         }
      }
   }

   boolean pingGetAutoFS() {
      boolean ret = false;
      if (this.setFS) {
         if ((this.my_fahrstrasse != null || this.manualFS != null) && this.my_gleis.getFluentData().getStellung() == gleisElements.ST_SIGNAL_ROT) {
            boolean r = false;
            if (this.manualFS != null) {
               r = this.my_gleis.theapplet.getFSallocator().getFS(this.manualFS, fsAllocs.ALLOCM_GET);
            } else {
               r = this.my_gleis.theapplet.getFSallocator().getFS(this.my_fahrstrasse, fsAllocs.ALLOCM_GET);
            }

            if (r) {
               if (this.triggeredFW) {
                  this.setFS = false;
               }

               if (this.lastautofw > 0L && (System.currentTimeMillis() - this.lastautofw) / 1000L < 5L) {
                  this.disableAutoFW();
               }

               this.lastautofw = System.currentTimeMillis();
            }
         }

         this.my_gleis.tjmAdd();
         ret = true;
      }

      return ret;
   }
}
