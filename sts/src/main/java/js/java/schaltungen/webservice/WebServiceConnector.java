package js.java.schaltungen.webservice;

import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.net.MalformedURLException;
import js.java.schaltungen.UserContextMini;
import js.java.soap.ServiceAccessor;
import js.java.soap.TipAnswer;

public class WebServiceConnector {
   private final ServiceAccessor webService;
   private final UserContextMini uc;

   public WebServiceConnector(UserContextMini uc, String url) throws MalformedURLException {
      super();
      this.uc = uc;
      this.webService = new ServiceAccessor(url);
      EventBusService.getInstance().subscribe(this);
   }

   @EventHandler
   public void getFriends(GetFriends event) {
      this.webService.run(service -> {
         String[] friends = service.getFriendFoe(this.uc.getToken(), true);
         String[] foes = service.getFriendFoe(this.uc.getToken(), false);
         EventBusService.getInstance().publish(new GetFriendsResponse(friends, foes));
      });
   }

   @EventHandler
   public void getTip(GetTip event) {
      this.webService.run(service -> {
         TipAnswer tip = service.getTip(this.uc.getToken());
         EventBusService.getInstance().publish(new GetTipResponse(tip));
      });
   }

   @EventHandler
   public void isLoginAllowed(GetIsLoginAllowed event) {
      this.webService.run(service -> {
         boolean allowed = service.isLoginAllowed(this.uc.getToken());
         String uname = service.getName(this.uc.getToken());
         String uid = service.getUid(this.uc.getToken());
         EventBusService.getInstance().publish(new IsLoginAllowedAnswer(allowed, uname, uid));
      });
   }

   @EventHandler
   public void storeLatency(StoreLatencies event) {
   }

   @EventHandler
   public void userData(StoreUserData event) {
      this.webService.run(service -> service.userData(this.uc.getToken(), event.name));
   }

   @EventHandler
   public void userData(StoreTextData event) {
      this.webService.run(service -> service.userConsole(this.uc.getToken(), event.reason, event.text));
   }

   @EventHandler
   public void eventOccured(StoreEventOccured event) {
      this.webService.run(service -> service.eventOccured(this.uc.getToken(), event.aid, event.name, event.code));
   }
}
