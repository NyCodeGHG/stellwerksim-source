package js.java.schaltungen.adapter;

import js.java.schaltungen.UserContext;

public class ModuleCloseEvent {
   public final boolean withError;
   public final UserContext uc;

   public ModuleCloseEvent(UserContext uc, boolean withError) {
      this.uc = uc;
      this.withError = withError;
   }
}
