package js.java.soap;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.xml.ws.BindingProvider;

public class ServiceAccessor {
   private final STSService service = new STSService();
   private final STSPort port = this.service.getSTSPort();
   private final ExecutorService tpool;

   public ServiceAccessor(String url) throws MalformedURLException {
      BindingProvider bp = (BindingProvider)this.port;
      bp.getRequestContext().put("javax.xml.ws.service.endpoint.address", url);
      this.tpool = Executors.newSingleThreadExecutor();
   }

   public void run(ServiceAccessor.ServiceRunner job) {
      this.tpool.submit(() -> this.runNow(job, null));
   }

   public void run(ServiceAccessor.ServiceRunner job, Runnable awt) {
      this.tpool.submit(() -> this.runNow(job, awt));
   }

   private void runNow(ServiceAccessor.ServiceRunner job, Runnable awt) {
      try {
         job.run(this.port);
      } catch (Exception var5) {
         Logger.getLogger(ServiceAccessor.class.getName()).log(Level.SEVERE, null, var5);
      }

      if (awt != null) {
         try {
            SwingUtilities.invokeLater(awt);
         } catch (Exception var4) {
            Logger.getLogger(ServiceAccessor.class.getName()).log(Level.SEVERE, null, var4);
         }
      }
   }

   public interface ServiceRunner {
      void run(STSPort var1);
   }
}
