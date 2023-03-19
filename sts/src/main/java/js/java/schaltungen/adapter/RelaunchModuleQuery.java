package js.java.schaltungen.adapter;

import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import js.java.schaltungen.UserContextMini;

public class RelaunchModuleQuery extends JFrame implements ActionListener {
   private static final long SHOWTIME = 30L;
   private final UserContextMini uc;
   private final LaunchModule event;
   private final Timer countDownTimer = new Timer(200, this);
   private final long startTime;
   private JButton cancelButton;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JButton retryButton;
   private JLabel timeLabel;

   public RelaunchModuleQuery(UserContextMini uc, LaunchModule event) {
      super();
      this.uc = uc;
      this.event = event;
      this.startTime = System.currentTimeMillis();
      this.initComponents();
      this.setIconImage(uc.getWindowIcon());
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      this.setLocation((dim.width - this.getWidth()) / 2, 20);
      this.countDownTimer.start();
      EventBusService.getInstance().subscribe(this);
   }

   @EventHandler
   public void event(SpoolLaunchEvent event) {
      this.dispose();
   }

   @EventHandler
   public void event(EndModule event) {
      this.retryButtonActionPerformed(null);
   }

   public void actionPerformed(ActionEvent e) {
      long rest = 30000L - (System.currentTimeMillis() - this.startTime);
      if (rest < 0L) {
         this.dispose();
      } else {
         this.timeLabel.setText(rest / 1000L + " s");
      }
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.timeLabel = new JLabel();
      this.jPanel2 = new JPanel();
      this.retryButton = new JButton();
      this.cancelButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Startversuch wiederholen");
      this.setPreferredSize(new Dimension(450, 320));
      this.setResizable(false);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            RelaunchModuleQuery.this.formWindowClosed(evt);
         }
      });
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel1
         .setText(
            "<html>\n<b>Das gewünschte Modul kann nicht mit den laufenden Modulen laufen.</b>\n<br><br>\nDiese können nun manuell beendet und geschlossen werden. Danach kann der Modulstart wiederholt werden.\n</html>"
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
      this.jLabel2.setText("Restzeit bis Startwiederholung nicht mehr möglich:");
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
      this.retryButton.setText("Jetzt Start wiederholen");
      this.retryButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            RelaunchModuleQuery.this.retryButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.retryButton);
      this.cancelButton.setText("Abbrechen");
      this.cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            RelaunchModuleQuery.this.cancelButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.cancelButton);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.insets = new Insets(10, 0, 10, 0);
      this.jPanel1.add(this.jPanel2, gridBagConstraints);
      this.getContentPane().add(this.jPanel1, "Center");
      this.pack();
   }

   private void formWindowClosed(WindowEvent evt) {
      this.countDownTimer.stop();
      EventBusService.getInstance().unsubscribe(this);
   }

   private void retryButtonActionPerformed(ActionEvent evt) {
      this.dispose();
      EventBusService.getInstance().publish(this.event);
   }

   private void cancelButtonActionPerformed(ActionEvent evt) {
      this.dispose();
   }
}
