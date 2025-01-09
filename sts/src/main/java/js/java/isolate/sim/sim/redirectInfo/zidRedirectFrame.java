package js.java.isolate.sim.sim.redirectInfo;

import javax.swing.JFrame;
import javax.swing.JPanel;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.moduleapi.SessionClose;

public class zidRedirectFrame extends JFrame implements SessionClose {
   private final String titel;
   private final UserContext uc;

   public zidRedirectFrame(UserContext uc, JFrame parent, String titel, JPanel pan) {
      this.uc = uc;
      this.titel = titel;
      this.initComponents();
      this.add(pan, "Center");
      this.setIconImage(uc.getWindowIcon());
      uc.addCloseObject(this);
      this.pack();
   }

   private void initComponents() {
      this.setDefaultCloseOperation(2);
      this.setTitle(this.titel);
      this.setLocationByPlatform(true);
      this.pack();
   }

   @Override
   public void close() {
      this.dispose();
   }
}
