package js.java.schaltungen.verifyTests;

import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.timesystem.timeSync;
import js.java.tools.gui.clock.timerecipient;

public class v_timeserv extends InitTestBase implements timerecipient {
   private String error = "";
   private boolean received = false;
   private timeSync serv = null;
   private int delay = 10;

   public v_timeserv() {
      super();
   }

   @Override
   public int test(UserContextMini uc) {
      int ret = 0;
      if (this.serv == null) {
         try {
            this.serv = new timeSync(uc.getParameter(UserContextMini.DATATYPE.TIMESERVER), 0, this);
            this.serv.sync();
         } catch (Exception var4) {
            ret = -1;
            this.error = " Fehler: " + var4.getMessage();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, var4);
         }
      } else if (this.received) {
         ret = 1;
      } else if (--this.delay <= 0) {
         ret = -1;
      }

      return ret;
   }

   @Override
   public String name() {
      return "STS Zeitsystem Verbindungstest" + this.error;
   }

   public void timeChange(long offsetToLocal, short tagescode, int latency) {
      this.received = true;
      this.serv.stop();
   }
}
