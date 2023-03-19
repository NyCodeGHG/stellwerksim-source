package js.java.schaltungen.verifyTests;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import js.java.schaltungen.UserContextMini;
import js.java.tools.gui.dataTransferDisplay.LedComponent;
import js.java.tools.gui.dataTransferDisplay.LedComponent.LEDCOLOR;

public abstract class InitTestBase {
   private int lastret = 0;
   public JLabel label = null;
   public final LedComponent red = new LedComponent(LEDCOLOR.RED);
   public final LedComponent yellow = new LedComponent(LEDCOLOR.YELLOW);
   public final LedComponent green = new LedComponent(LEDCOLOR.GREEN);
   private boolean blicked = true;

   public InitTestBase() {
      super();
   }

   public final int runtest(UserContextMini uc) {
      if (this.lastret != 0) {
         return this.lastret;
      } else {
         try {
            this.lastret = this.test(uc);
         } catch (Exception var3) {
            Logger.getLogger(InitTestBase.class.getName()).log(Level.SEVERE, null, var3);
            this.lastret = -1;
         }

         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               InitTestBase.this.label.setText(InitTestBase.this.name());
               InitTestBase.this.red.setLed(InitTestBase.this.lastret == -1);
               InitTestBase.this.yellow.setLed(InitTestBase.this.lastret == 0 && InitTestBase.this.blicked);
               InitTestBase.this.green.setLed(InitTestBase.this.lastret == 1);
               InitTestBase.this.blicked = !InitTestBase.this.blicked;
            }
         });
         return this.lastret;
      }
   }

   public abstract int test(UserContextMini var1);

   public abstract String name();

   public void close(UserContextMini uc) {
   }
}
