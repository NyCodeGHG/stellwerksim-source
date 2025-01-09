package js.java.schaltungen;

import de.deltaga.eb.EventBusService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import js.java.schaltungen.chatcomng.ShowConsoleEvent;
import js.java.schaltungen.verifyTests.FunctionTestFailedEvent;
import js.java.schaltungen.verifyTests.InitTestBase;
import js.java.schaltungen.verifyTests.testError;
import js.java.schaltungen.verifyTests.v_http;
import js.java.schaltungen.verifyTests.v_irc;
import js.java.schaltungen.verifyTests.v_plugin;
import js.java.schaltungen.verifyTests.v_soap;
import js.java.schaltungen.verifyTests.v_sound;
import js.java.schaltungen.verifyTests.v_timeserv;
import js.java.schaltungen.verifyTests.v_token;

public class StartVerify extends JFrame {
   private final UserContextMini uc;
   private final LinkedList<InitTestBase> tests = new LinkedList();
   private JLabel buildLabel;
   private JButton exitButton;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JLabel logoLabel;
   private JPanel testOutputPanel;

   public StartVerify(UserContextMini uc) {
      this.uc = uc;
      this.initComponents();
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
      this.setIconImage(uc.getWindowIcon());
      this.addTest(v_http.class);
      this.addTest(v_soap.class);
      this.addTest(v_timeserv.class);
      this.addTest(v_irc.class);
      this.addTest(v_sound.class);
      this.addTest(v_plugin.class);
      this.addTest(v_token.class);
      int y = 0;

      for (InitTestBase tb : this.tests) {
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = y;
         gbc.weightx = 1.0;
         gbc.fill = 2;
         JLabel l = new JLabel();
         l.setText(tb.name());
         tb.label = l;
         this.testOutputPanel.add(l, gbc);
         gbc = new GridBagConstraints();
         gbc.gridx = 1;
         gbc.gridy = y;
         this.testOutputPanel.add(tb.red, gbc);
         gbc = new GridBagConstraints();
         gbc.gridx = 2;
         gbc.gridy = y;
         this.testOutputPanel.add(tb.yellow, gbc);
         gbc = new GridBagConstraints();
         gbc.gridx = 3;
         gbc.gridy = y;
         this.testOutputPanel.add(tb.green, gbc);
         y++;
      }

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = y;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      JPanel p = new JPanel();
      p.setOpaque(false);
      this.testOutputPanel.add(p, gbc);
   }

   public void start(final Runnable continueFunc) {
      this.setVisible(true);
      Thread t = new Thread(new Runnable() {
         public void run() {
            try {
               StartVerify.this.runTests(continueFunc);
            } catch (InterruptedException var2) {
               Logger.getLogger(StartVerify.class.getName()).log(Level.SEVERE, null, var2);
               StartVerify.this.uc.exit();
            }
         }
      });
      t.start();
   }

   private void runTests(Runnable continueFunc) throws InterruptedException {
      boolean oneError = false;

      boolean oneMissing;
      do {
         oneMissing = false;

         for (InitTestBase tb : this.tests) {
            int v = tb.runtest(this.uc);
            if (v == 0) {
               oneMissing = true;
            }

            if (v < 0) {
               oneError = true;
            }
         }

         Thread.sleep(500L);
      } while (oneMissing);

      StringBuilder failedStr = new StringBuilder();

      for (InitTestBase tb : this.tests) {
         if (tb.runtest(this.uc) < 0) {
            failedStr.append(tb.name());
            failedStr.append('\n');
         }

         tb.close(this.uc);
      }

      boolean _oneError = oneError;
      SwingUtilities.invokeLater(() -> {
         if (_oneError) {
            EventBusService.getInstance().publish(new ShowConsoleEvent());
            EventBusService.getInstance().publish(new FunctionTestFailedEvent(failedStr.toString()));
            this.exitButton.setEnabled(true);
            this.logoLabel.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/error.png")));
         } else {
            this.setVisible(false);
            this.dispose();
            this.tests.clear();
            continueFunc.run();
         }
      });
   }

   private void addTest(Class<? extends InitTestBase> t) {
      try {
         InitTestBase b = (InitTestBase)t.newInstance();
         this.tests.add(b);
      } catch (Exception var3) {
         this.tests.add(new testError(t));
      }
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.jLabel1 = new JLabel();
      this.exitButton = new JButton();
      this.jPanel2 = new JPanel();
      this.testOutputPanel = new JPanel();
      this.jLabel2 = new JLabel();
      this.jPanel3 = new JPanel();
      this.logoLabel = new JLabel();
      this.buildLabel = new JLabel();
      this.setDefaultCloseOperation(0);
      this.setTitle("StellwerkSim Funktionstest");
      this.setUndecorated(true);
      this.setPreferredSize(new Dimension(650, 300));
      this.setResizable(false);
      this.jPanel1.setBackground(Color.white);
      this.jPanel1.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.lightGray), BorderFactory.createBevelBorder(0)));
      this.jPanel1.setLayout(new BorderLayout());
      this.jLabel1.setBackground(Color.black);
      this.jLabel1.setFont(this.jLabel1.getFont().deriveFont(this.jLabel1.getFont().getStyle() | 1, (float)(this.jLabel1.getFont().getSize() + 2)));
      this.jLabel1.setForeground(Color.white);
      this.jLabel1.setText("<html>StellwerkSim wird gestartet!<br>Initialer Funktionstest, ob STS von diesem System unterstützt wird.");
      this.jLabel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jLabel1.setOpaque(true);
      this.jPanel1.add(this.jLabel1, "North");
      this.exitButton.setText("Schließen");
      this.exitButton.setEnabled(false);
      this.exitButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            StartVerify.this.exitButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.exitButton, "South");
      this.jPanel2.setOpaque(false);
      this.jPanel2.setLayout(new BorderLayout());
      this.testOutputPanel.setOpaque(false);
      this.testOutputPanel.setLayout(new GridBagLayout());
      this.jPanel2.add(this.testOutputPanel, "Center");
      this.jLabel2.setForeground(Color.lightGray);
      this.jLabel2.setHorizontalAlignment(4);
      this.jLabel2.setText("<html>StellwerkSim Kommunikator Java Web Start Edition</html>");
      this.jPanel2.add(this.jLabel2, "South");
      this.jPanel1.add(this.jPanel2, "Center");
      this.jPanel3.setOpaque(false);
      this.jPanel3.setLayout(new BorderLayout());
      this.logoLabel.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/160.jpg")));
      this.logoLabel.setVerticalAlignment(1);
      this.logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
      this.logoLabel.setHorizontalTextPosition(0);
      this.logoLabel.setVerticalTextPosition(3);
      this.jPanel3.add(this.logoLabel, "North");
      this.buildLabel.setHorizontalAlignment(4);
      this.buildLabel.setText("Build: " + this.uc.getBuild());
      this.jPanel3.add(this.buildLabel, "South");
      this.jPanel1.add(this.jPanel3, "West");
      this.getContentPane().add(this.jPanel1, "Center");
      this.pack();
   }

   private void exitButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
      this.uc.exit();
   }
}
