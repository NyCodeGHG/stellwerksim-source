package js.java.schaltungen;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jnlp.DownloadService2;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.jnlp.DownloadService2.ResourceSpec;

public class UpdateChecker implements Runnable {
   private final String url;
   private final UserContextMini uc;

   public UpdateChecker(UserContextMini uc) {
      super();
      this.uc = uc;
      this.url = "http://" + uc.getParameter(UserContextMini.DATATYPE.WEBSERVER) + "/.*";
   }

   public void run() {
      try {
         DownloadService2 service = (DownloadService2)ServiceManager.lookup("javax.jnlp.DownloadService2");
         ResourceSpec spec = new ResourceSpec(this.url, null, 4);
         ResourceSpec[] results = service.getCachedResources(spec);
         System.out.println("Cached:");

         for(ResourceSpec rs : results) {
            System.out.println(rs.getUrl() + "//" + rs.getLastModified() + "//" + new Date(rs.getLastModified()).toString());
         }

         ResourceSpec[] uresults = service.getUpdateAvailableResources(spec);
         System.out.println("Updates:");

         for(ResourceSpec rs : uresults) {
            System.out.println(rs.getUrl() + "//" + rs.getLastModified() + "//" + new Date(rs.getLastModified()).toString());
         }
      } catch (IOException | UnavailableServiceException var9) {
         Logger.getLogger(UpdateChecker.class.getName()).log(Level.SEVERE, null, var9);
      }
   }
}
