package js.java.isolate.statusapplet.players;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import js.java.schaltungen.simpleappletmaster;
import js.java.schaltungen.moduleapi.SessionClose;

public class memPanel extends JPanel implements ActionListener, SessionClose {
   private final Timer memTimer = new Timer(10000, this);
   private JLabel jLabel2;
   private JProgressBar memoryBar;

   public memPanel() {
      this.initComponents();
      this.memTimer.start();
   }

   @Override
   public void close() {
      SwingUtilities.invokeLater(() -> this.memTimer.stop());
   }

   public void actionPerformed(ActionEvent e) {
      this.updateMem();
   }

   private void updateMem() {
      long heapMaxSize = Runtime.getRuntime().maxMemory();
      long heapFreeSize = Runtime.getRuntime().freeMemory();
      long heapSize = Runtime.getRuntime().totalMemory();
      this.memoryBar.setMaximum((int)(heapSize / 1000L));
      this.memoryBar.setValue((int)((heapSize - heapFreeSize) / 1000L));
      simpleappletmaster.showMem("update");
   }

   private void initComponents() {
      this.jLabel2 = new JLabel();
      this.memoryBar = new JProgressBar();
      this.setBorder(BorderFactory.createEtchedBorder());
      this.setLayout(new BoxLayout(this, 2));
      this.jLabel2.setFont(this.jLabel2.getFont().deriveFont((float)this.jLabel2.getFont().getSize() - 1.0F));
      this.jLabel2.setText("Speicher");
      this.add(this.jLabel2);
      this.memoryBar.setFont(this.memoryBar.getFont().deriveFont((float)this.memoryBar.getFont().getSize() - 2.0F));
      this.memoryBar.setToolTipText("Speicherverbrauch");
      this.add(this.memoryBar);
   }
}
