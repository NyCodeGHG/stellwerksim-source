package js.java.schaltungen.adapter;

import js.java.schaltungen.UserContext;

public class EndModule {
   public final Module modul;
   public final boolean witherror;
   public final UserContext uc;

   public EndModule(Module modul, UserContext uc, boolean witherror) {
      super();
      this.modul = modul;
      this.uc = uc;
      this.witherror = witherror;
   }
}
