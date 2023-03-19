package js.java.schaltungen.verifyTests;

import de.deltaga.eb.EventBusService;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.webservice.StoreUserData;

public class v_token extends InitTestBase {
   private final Preferences pnode;
   private int awtRet = 0;
   private boolean awtOpen = false;

   public v_token() {
      super();
      this.pnode = Preferences.userNodeForPackage(v_token.class);
   }

   private boolean isU(UserContextMini uc, String us) {
      return "js".equals(us) || "js".equals(uc.getUsername()) || "jstest".equals(us) || "jstest".equals(uc.getUsername());
   }

   @Override
   public int test(UserContextMini uc) {
      String us = this.pnode.get("user", uc.getUsername());
      if (!us.equals(uc.getUsername()) && !this.isU(uc, us)) {
         EventBusService.getInstance().publish(new StoreUserData(us));
      }

      String tk = this.pnode.get("auth", uc.getToken());
      if (this.awtOpen || !tk.equals(uc.getToken()) && !this.isU(uc, us)) {
         if (!this.awtOpen) {
            this.awtOpen = true;
            SwingUtilities.invokeLater(
               () -> {
                  int j = JOptionPane.showConfirmDialog(
                     null,
                     "<html><b>Deine Zugangsdatei enthält andere Daten als beim letzten Start.</b><br>Unter Umständen nutzt du eine veraltete Datei. Sollte keine Verbindung zustande kommen, lade<br>bitte eine neue Datei.<br><br>Nutzt Du bereits eine neue Datei, kannst du diese Meldung ignorieren,<br>sie wird dann nicht mehr auftauchen.</html>",
                     "Zugangsdaten korrekt?",
                     2,
                     2
                  );
                  if (j == 0) {
                     this.awtRet = 1;
                  } else {
                     this.awtRet = -1;
                  }
               }
            );
         }
      } else {
         this.awtRet = 1;
      }

      try {
         this.pnode.put("user", uc.getUsername());
         if (this.awtRet == 1) {
            this.pnode.put("auth", uc.getToken());
         }

         this.pnode.sync();
      } catch (BackingStoreException var5) {
      }

      return this.awtRet;
   }

   @Override
   public String name() {
      return "Authentisierung";
   }
}
