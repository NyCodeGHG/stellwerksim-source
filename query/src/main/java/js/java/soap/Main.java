package js.java.soap;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;

public class Main {
   private static final String URL = "https://js.sandbox.stellwerksim.de/soap.php";
   private static ServiceAccessor webService;

   public static void main(String[] argv) throws InterruptedException, MalformedURLException {
      String user = System.getProperty("jnlp.netbeans_user");
      String pass = System.getProperty("jnlp.netbeans_pass");
      String token = System.getProperty("jnlp.token");
      if (user != null && pass != null) {
         System.out.println("HTTP_AUTH PASSWORT aktiv");
         Authenticator.setDefault(new Main.MyAuthenticator(user, pass));
      }

      webService = new ServiceAccessor("https://js.sandbox.stellwerksim.de/soap.php");
      webService.run(service -> {
         System.out.println("service");
         String[] friends = service.getFriendFoe(token, true);

         for (String f : friends) {
            System.out.println("Fr:" + f);
         }

         String[] foes = service.getFriendFoe(token, false);

         for (String f : foes) {
            System.out.println("Fo:" + f);
         }

         System.out.println("/service");
         TipAnswer tip = service.getTip(token);
         System.out.println("TIP: " + tip.getTitle() + " -> " + tip.getText());
         System.out.println("Name: " + service.getName(token));
         System.out.println("Uid: " + service.getUid(token));
      });
      Thread.sleep(10000L);
      System.exit(0);
   }

   private static class MyAuthenticator extends Authenticator {
      final String kuser;
      final String kpass;

      MyAuthenticator(String user, String pass) {
         this.kuser = user;
         this.kpass = pass;
      }

      public PasswordAuthentication getPasswordAuthentication() {
         System.err.println("Feeding username and password for " + this.getRequestingScheme());
         return new PasswordAuthentication(this.kuser, this.kpass.toCharArray());
      }
   }
}
