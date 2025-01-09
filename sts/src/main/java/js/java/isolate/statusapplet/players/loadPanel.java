package js.java.isolate.statusapplet.players;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class loadPanel extends JPanel {
   private final oneInstance my_instance;
   private JProgressBar loadingBar;

   public loadPanel(oneInstance irc) {
      this.my_instance = irc;
      this.initComponents();
   }

   public void setProgress(int p) {
      if (p < 0) {
         this.loadingBar.setValue(0);
         this.loadingBar.setIndeterminate(true);
      } else {
         this.loadingBar.setIndeterminate(false);
         this.loadingBar.setValue(p);
      }
   }

   private void initComponents() {
      this.loadingBar = new JProgressBar();
      this.setBorder(BorderFactory.createEtchedBorder());
      this.setLayout(new BoxLayout(this, 2));
      this.loadingBar.setFont(this.loadingBar.getFont().deriveFont((float)this.loadingBar.getFont().getSize() - 1.0F));
      this.loadingBar.setToolTipText("Gleisbild Ladeanzeige");
      this.loadingBar.setValue(100);
      this.loadingBar.setStringPainted(true);
      this.add(this.loadingBar);
   }
}
