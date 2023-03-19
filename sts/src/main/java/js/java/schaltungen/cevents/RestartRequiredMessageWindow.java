package js.java.schaltungen.cevents;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import js.java.schaltungen.UserContextMini;

public class RestartRequiredMessageWindow extends JFrame implements ActionListener {
   private static final long SHOWTIME = 600L;
   private static boolean shown = false;
   private final UserContextMini uc;
   private final Timer countDownTimer = new Timer(200, this);
   private final long startTime;
   private JButton cancelButton;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JLabel timeLabel;

   static void showMessage(UserContextMini uc) {
      SwingUtilities.invokeLater(() -> {
         if (!shown) {
            new RestartRequiredMessageWindow(uc).setVisible(true);
         }
      });
   }

   public RestartRequiredMessageWindow(UserContextMini uc) {
      super();
      this.uc = uc;
      this.startTime = System.currentTimeMillis();
      this.initComponents();
      this.setIconImage(uc.getWindowIcon());
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      this.setLocation((dim.width - this.getWidth()) / 2, 20);
      this.countDownTimer.start();
      shown = true;
   }

   public void actionPerformed(ActionEvent e) {
      long rest = 600000L - (System.currentTimeMillis() - this.startTime);
      if (rest < 0L) {
         this.uc.forceExit();
      } else {
         String pre = "";
         String post = "";
         if (rest / 1000L < 30L) {
            pre = "<html><b>";
            post = "</b></html>";
         }

         this.timeLabel.setText(pre + rest / 1000L / 60L + " Minuten " + rest / 1000L % 60L + " Sekunden" + post);
      }
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.timeLabel = new JLabel();
      this.jPanel2 = new JPanel();
      this.cancelButton = new JButton();
      this.jLabel3 = new JLabel();
      this.setDefaultCloseOperation(0);
      this.setTitle("Dringendes STS Update");
      this.setPreferredSize(new Dimension(700, 300));
      this.setResizable(false);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            RestartRequiredMessageWindow.this.formWindowClosed(evt);
         }
      });
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel1
         .setText(
            "<html><b>Es ist ein dringender Neustart des Programms nötig.</b><br><br>Das Programm muss dringend aktualisiert werden, ein außergewöhnlicher Neustart ist dazu nötig. Das Programm wird sich deshalb in den nächsten 10 Minuten selbst beenden!\n<br><br>Dieser Neustart ist zwingend zeitnah nötig, um die allgemeine Funktionsfähigkeit aufrecht erhalten zu können. Da dies ist nicht bei jeder Aktualisierungen nötig ist, wird hier explizit darauf hingewiesen.\n</html>"
         );
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.anchor = 19;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(10, 0, 10, 0);
      this.jPanel1.add(this.jLabel1, gridBagConstraints);
      this.jLabel2.setText("Restzeit bis Programmende:");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.insets = new Insets(10, 0, 10, 0);
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(10, 5, 10, 0);
      this.jPanel1.add(this.timeLabel, gridBagConstraints);
      this.jPanel2.setLayout(new GridLayout(0, 1, 0, 10));
      this.cancelButton.setText("Jetzt Programm beenden");
      this.cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            RestartRequiredMessageWindow.this.cancelButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.cancelButton);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = 24;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(10, 0, 10, 0);
      this.jPanel1.add(this.jPanel2, gridBagConstraints);
      this.jLabel3.setForeground(SystemColor.textInactiveText);
      this.jLabel3
         .setText(
            "<html>Dieser Text wurde präsentiert von: ZEIT, die wir nicht haben um unnötig lange zu begründen, warum so ein sofortiger Neustart mal sein muss und ein anderes mal nicht - wobei letzteres bevorzugt wird.</html>"
         );
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 20;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(10, 0, 10, 0);
      this.jPanel1.add(this.jLabel3, gridBagConstraints);
      this.getContentPane().add(this.jPanel1, "Center");
      this.pack();
   }

   private void formWindowClosed(WindowEvent evt) {
      int j = JOptionPane.showConfirmDialog(this, "Jetzt beenden?", "Jetzt beenden?", 0, 3);
      if (j == 0) {
         this.cancelButtonActionPerformed(null);
      }
   }

   private void cancelButtonActionPerformed(ActionEvent evt) {
      this.uc.exit();
   }
}
