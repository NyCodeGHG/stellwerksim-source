package js.java.schaltungen.settings;

import java.awt.Component;
import javax.swing.JOptionPane;

public class RealisticSure {
   public static boolean question(Component parent) {
      int j = JOptionPane.showConfirmDialog(
         parent,
         "<html>Wirklich den REALISTISCHEN Modus aktivieren?<br><br>Dies deaktiviert einige Komfort-Funktionen im Spiel, aktiviert einige andere Optionen.<br><br>Der Modus wird erst beim nächsten Spiel aktiv,<br>wirklich aktivieren?</html>",
         "Realistischer Modus, sicher?",
         2,
         0
      );
      if (j != 0) {
         return false;
      } else {
         j = JOptionPane.showConfirmDialog(
            parent,
            "<html>Sei gewarnt, dass es im Forum immer wieder Anfragen gibt,<br>weil Spieler diese Funktion doch nicht handhaben können!<br><br>Der Modus wird erst beim nächsten Spiel aktiv,<br>wirklich aktivieren?</html>",
            "Realistischer Modus, sicher?",
            2,
            0
         );
         return j == 0;
      }
   }
}
