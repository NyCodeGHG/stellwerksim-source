package js.java.isolate.sim.gleisbild;

import java.io.DataInputStream;
import java.io.IOException;
import js.java.tools.BinaryStore;
import js.java.tools.BinaryStore.StoreElement;

public class gleisbild_extend extends BinaryStore {
   public static final int SIGNAL_CLASSIC = 0;
   public static final int SIGNAL_BESIDE = 1;
   public static final int SIGNAL_AROUND = 2;
   public static final int SIGNAL_BELOW = 3;
   public static final int SIGNAL_LEFT = 4;
   public static final int SIGNAL_LEFT_AROUND = 5;
   public static final int SIGNAL_LEFT_BELOW = 6;
   public static final int MASSTAB_CLASSIC = 0;
   public static final int MASSTAB_REVISED = 1;
   public static final int MASSTAB_NEXTGEN = 2;
   @StoreElement(
      storeName = "SIGV"
   )
   public int signalversion = 0;
   @StoreElement(
      storeName = "NBS"
   )
   public int nachbarbahnsteighoriz = 5;
   @StoreElement(
      storeName = "NBLW"
   )
   public int nachbarbahnLookupWidth = 5;
   @StoreElement(
      storeName = "CSG"
   )
   public boolean cleverSignal = false;
   @StoreElement(
      storeName = "MSTB"
   )
   public int masstabName = 0;
   @StoreElement(
      storeName = "ZWS"
   )
   public boolean hauptZwergSignal = false;

   public static gleisbild_extend createFromBase64(String data) {
      gleisbild_extend sge = new gleisbild_extend();
      sge.fromBase64(data);
      return sge;
   }

   public gleisbild_extend() {
      super(2L);
   }

   protected void handleReadValue(DataInputStream in, String name) throws IOException {
      if (name.compareTo("AZLN") == 0) {
         if (this.readType(in) == 3) {
            boolean altezuglänge = this.readValueBoolean(in);
            if (altezuglänge) {
               this.masstabName = 0;
            } else {
               this.masstabName = 1;
            }
         }
      } else {
         super.handleReadValue(in, name);
      }
   }

   public int getSignalversion() {
      return this.signalversion;
   }

   public void setSignalversion(int signalversion) {
      this.signalversion = signalversion;
   }

   public int getNachbarbahnsteighoriz() {
      return this.nachbarbahnsteighoriz;
   }

   public void setNachbarbahnsteighoriz(int nachbarbahnsteighoriz) {
      this.nachbarbahnsteighoriz = nachbarbahnsteighoriz;
   }

   public int getNachbarbahnsteigLookupWidth() {
      return this.nachbarbahnLookupWidth;
   }

   public void setNachbarbahnsteigLookupWidth(int nachbarbahnLookupWidth) {
      this.nachbarbahnLookupWidth = nachbarbahnLookupWidth;
   }

   public boolean isCleverSignal() {
      return this.cleverSignal;
   }

   public void setCleverSignal(boolean cleverSignal) {
      this.cleverSignal = cleverSignal;
   }

   public int getMasstabName() {
      return this.masstabName;
   }

   public void setMasstabName(int name) {
      this.masstabName = name;
   }

   public boolean isHauptZwergSignal() {
      return this.hauptZwergSignal;
   }

   public void setHauptZwergSignal(boolean hauptZwergSignal) {
      this.hauptZwergSignal = hauptZwergSignal;
   }
}
