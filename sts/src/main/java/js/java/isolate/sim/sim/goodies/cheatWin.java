package js.java.isolate.sim.sim.goodies;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class cheatWin extends JDialog {
   private final cheatManager parent;
   private boolean enabled = false;
   private boolean ready = true;
   private JButton checkButton;
   private JButton closeButton;
   private JLabel codeDescription;
   private JTextField codeField;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JPanel jPanel1;
   private JButton startButton;
   private JLabel testingCode;

   public cheatWin(cheatManager parent, JFrame window) {
      super(window);
      this.parent = parent;
      this.initComponents();
      this.showEnabled();
      this.codeField.requestFocusInWindow();
   }

   public void setEnabled(boolean e) {
      this.enabled = e;
      this.showEnabled();
   }

   private void showEnabled() {
      this.checkButton.setEnabled(this.enabled && this.ready);
      this.codeField.setEnabled(this.enabled && this.ready);
   }

   private void checkCode(String code) {
      this.testingCode.setText(code);
      this.codeDescription.setText("Code wird geprüft");
      this.parent.check(code);
   }

   void result(int res, String kind) {
      if (res != 200) {
         this.codeDescription.setText(kind);
         this.testingCode.setText(" ");
         this.ready = true;
      } else {
         this.startButton.setEnabled(true);
         this.testingCode.setText("Code gültig, Wirkung:");
         this.codeDescription.setText(kind);
         this.ready = false;
      }

      this.showEnabled();
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.closeButton = new JButton();
      this.jPanel1 = new JPanel();
      this.jLabel2 = new JLabel();
      this.codeField = new JTextField();
      this.checkButton = new JButton();
      this.testingCode = new JLabel();
      this.codeDescription = new JLabel();
      this.startButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("CheatCode");
      this.setLocationByPlatform(true);
      this.setMaximumSize(new Dimension(450, 350));
      this.setMinimumSize(new Dimension(450, 350));
      this.setPreferredSize(new Dimension(450, 350));
      this.setResizable(false);
      this.jLabel1.setBackground(new Color(0, 0, 0));
      this.jLabel1.setFont(this.jLabel1.getFont().deriveFont(this.jLabel1.getFont().getStyle() | 1));
      this.jLabel1.setForeground(new Color(255, 255, 255));
      this.jLabel1
         .setText(
            "<html><div align=justify>\nEffekt Codes können nach ca. 40 Minuten Spielzeit eingegeben werden. Danach ca. alle 10 Minuten. Der \"prüfen\" Knopf ist dann jeweils verfügbar.<br><br>\nCodes sind im Forum in neueren Beiträgen verstreut. Jeder Code kann pro Spiel nur 1x eingegeben werden und sind nur für begrenzte Zeit gültig. \nWurde der Code von anderen Spielern in letzter Zeit genutzt, ist er ebenfalls für einige Zeit gesperrt.</div>\n</html>"
         );
      this.jLabel1.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0), 5));
      this.jLabel1.setOpaque(true);
      this.getContentPane().add(this.jLabel1, "North");
      this.closeButton.setText("schließen");
      this.closeButton.setFocusPainted(false);
      this.closeButton.setFocusable(false);
      this.closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            cheatWin.this.closeButtonActionPerformed(evt);
         }
      });
      this.getContentPane().add(this.closeButton, "South");
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(1, 50, 1, 50));
      GridBagLayout jPanel1Layout = new GridBagLayout();
      jPanel1Layout.columnWidths = new int[]{0, 5, 0, 5, 0, 5, 0};
      jPanel1Layout.rowHeights = new int[]{0, 5, 0, 5, 0, 5, 0, 5, 0};
      this.jPanel1.setLayout(jPanel1Layout);
      this.jLabel2.setText("Code");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.codeField, gridBagConstraints);
      this.checkButton.setText("prüfen");
      this.checkButton.setEnabled(false);
      this.checkButton.setFocusPainted(false);
      this.checkButton.setFocusable(false);
      this.checkButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            cheatWin.this.checkButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      this.jPanel1.add(this.checkButton, gridBagConstraints);
      this.testingCode.setText(" ");
      this.testingCode.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 1));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.testingCode, gridBagConstraints);
      this.codeDescription.setText(" ");
      this.codeDescription.setBorder(BorderFactory.createEmptyBorder(1, 10, 1, 1));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.codeDescription, gridBagConstraints);
      this.startButton.setText("jetzt aktivieren");
      this.startButton.setEnabled(false);
      this.startButton.setFocusPainted(false);
      this.startButton.setFocusable(false);
      this.startButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            cheatWin.this.startButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 8;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      this.jPanel1.add(this.startButton, gridBagConstraints);
      this.getContentPane().add(this.jPanel1, "Center");
      this.pack();
   }

   private void closeButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
   }

   private void checkButtonActionPerformed(ActionEvent evt) {
      String code = this.codeField.getText().trim();
      if (!code.isEmpty()) {
         this.codeField.setText("");
         this.ready = false;
         this.showEnabled();
         this.checkCode(code);
      }
   }

   private void startButtonActionPerformed(ActionEvent evt) {
      this.startButton.setEnabled(false);
      this.setVisible(false);
      this.parent.start();
   }
}
