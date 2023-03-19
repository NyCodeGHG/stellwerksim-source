package js.java.schaltungen.verifyTests;

import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.webservice.GetIsLoginAllowed;
import js.java.schaltungen.webservice.IsLoginAllowedAnswer;

public class v_soap extends InitTestBase {
   private String error = "";
   private boolean subscribed = false;
   private IsLoginAllowedAnswer response = null;
   private int waitCount = 10;

   public v_soap() {
      super();
   }

   @Override
   public int test(UserContextMini uc) {
      if (!this.subscribed) {
         EventBusService.getInstance().subscribe(this);
         EventBusService.getInstance().publish(new GetIsLoginAllowed());
         this.subscribed = true;
      } else {
         --this.waitCount;
         if (this.waitCount < 0) {
            this.error = ": Keine Antwort vom Server";
            return -1;
         }
      }

      if (this.response == null) {
         return 0;
      } else if (!uc.getUsername().equals(this.response.uname) || !this.numCompare(uc, this.response.uid)) {
         this.error = ": Datensatz Ungleichheit";
         return -1;
      } else {
         return this.response.allowed ? 1 : -1;
      }
   }

   private boolean numCompare(UserContextMini uc, String _uid) {
      try {
         int uid = Integer.parseInt(_uid);
         int ucuid = Integer.parseInt(uc.getUid());
         return uid == ucuid;
      } catch (NumberFormatException var5) {
         return false;
      }
   }

   @EventHandler
   public void isLoginAllowed(IsLoginAllowedAnswer event) {
      if (!event.allowed) {
         this.error = ": Kennung gesperrt!";
      }

      this.response = event;
   }

   @Override
   public String name() {
      return "Berechtigung" + this.error;
   }

   @Override
   public void close(UserContextMini uc) {
   }
}
