package js.java.schaltungen.settings;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import js.java.schaltungen.UserContextMini;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class Settings extends JFrame {
   private final UserContextMini uc;
   private JTabbedPane jTabbedPane1;

   public Settings(UserContextMini uc) {
      super();
      this.uc = uc;
      this.initComponents();
      this.setIconImage(uc.getWindowIcon());
      this.jTabbedPane1.add("Audio", new AudioLevelFrame(uc));
      this.jTabbedPane1.add("Aussehen", new LookAndFeel(uc));
      this.jTabbedPane1.add("Desktop Integration", new DesktopIntegration(uc));
      this.jTabbedPane1.add("Simulator", new SimDefaults(uc));
      this.jTabbedPane1.add("Fenster schließen Frage", new CloseQuestionDefaults(uc));
      this.pack();
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION);
   }

   private void initComponents() {
      this.jTabbedPane1 = new JTabbedPane();
      this.setTitle("Einstellungen");
      this.setLocationByPlatform(true);
      this.setResizable(false);
      this.getContentPane().add(this.jTabbedPane1, "Center");
      this.pack();
   }
}
