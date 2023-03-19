package js.java.isolate.sim.gleisbild.fahrstrassen;

import js.java.tools.BinaryStore;
import js.java.tools.BinaryStore.StoreElement;

public class fahrstrasse_extend extends BinaryStore {
   public static final int FSTYPE_DEFAULT = 0;
   public static final int FSTYPE_AUTODISABLED = 1;
   public static final int FSTYPE_AUTOENABLED = 2;
   public static final int FSTYPE_DELETED = 4;
   public static final int FSTYPE_RFONLY = 8;
   public static final int FSTYPE_FSONLY = 16;
   @StoreElement(
      storeName = "AFS"
   )
   public int fstype = 0;

   public static fahrstrasse_extend createFromBase64(String data) {
      fahrstrasse_extend fe = new fahrstrasse_extend();
      fe.fromBase64(data);
      return fe;
   }

   public fahrstrasse_extend() {
      super(1L);
   }

   public int getFSType() {
      return this.fstype;
   }

   public void setFSType(int type) {
      this.fstype = type;
   }

   public boolean isDeleted() {
      return this.fstype == 4;
   }
}
