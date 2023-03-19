package js.java.isolate.sim.sim.funk;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.tools.NumberCheckerDocument;
import js.java.tools.RunLater;

public class stCodeFrame extends JDialog {
   private final zugUndPlanPanel.funkAdapter parent;
   private String prevCode = "";
   private JButton cancelButton;
   private JTextField codeField;
   private JButton jButton0;
   private JButton jButton1;
   private JButton jButton2;
   private JButton jButton3;
   private JButton jButton4;
   private JButton jButton5;
   private JButton jButton6;
   private JButton jButton7;
   private JButton jButton8;
   private JButton jButton9;
   private JButton jButtonC;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JPanel jPanel4;
   private JButton okButton;

   public stCodeFrame(zugUndPlanPanel.funkAdapter a) {
      super(a.getFrame(), false);
      this.parent = a;
      this.initComponents();
      this.codeField.setDocument(new NumberCheckerDocument());
      this.codeField.getDocument().addDocumentListener(new DocumentListener() {
         public void insertUpdate(DocumentEvent e) {
            stCodeFrame.this.checkCode();
         }

         public void removeUpdate(DocumentEvent e) {
            stCodeFrame.this.checkCode();
         }

         public void changedUpdate(DocumentEvent e) {
            stCodeFrame.this.checkCode();
         }
      });
   }

   private void checkCode() {
      if (!this.prevCode.equals(this.codeField.getText())) {
         this.prevCode = this.codeField.getText();
         this.okButton.setEnabled(this.validCode());
      }
   }

   private boolean validCode() {
      try {
         int c = Integer.parseInt(this.codeField.getText());
         return this.codeField.getText().length() == 4;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   private void sendCode() {
      String code = this.codeField.getText();
      RunLater.runlater(new stCodeFrame.codeTimer(code), (long)(1000 * (int)(5.0 + 5.0 * Math.random())));
   }

   public void showDialog(JComponent c) {
      this.setLocationRelativeTo(c);
      this.okButton.setEnabled(false);
      this.codeField.setText("");
      this.setVisible(true);
      this.codeField.setCaretPosition(0);
      this.codeField.requestFocusInWindow();
   }

   private void initComponents() {
      this.jPanel2 = new JPanel();
      this.jPanel3 = new JPanel();
      this.jLabel1 = new JLabel();
      this.codeField = new JTextField();
      this.jPanel4 = new JPanel();
      this.jButton1 = new JButton();
      this.jButton2 = new JButton();
      this.jButton3 = new JButton();
      this.jButton4 = new JButton();
      this.jButton5 = new JButton();
      this.jButton6 = new JButton();
      this.jButton7 = new JButton();
      this.jButton8 = new JButton();
      this.jButton9 = new JButton();
      this.jLabel2 = new JLabel();
      this.jButton0 = new JButton();
      this.jButtonC = new JButton();
      this.jPanel1 = new JPanel();
      this.okButton = new JButton();
      this.cancelButton = new JButton();
      this.setTitle("Störungscode");
      this.setLocationByPlatform(true);
      this.setResizable(false);
      this.jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel2.setLayout(new BoxLayout(this.jPanel2, 3));
      this.jPanel3.setBorder(BorderFactory.createEmptyBorder(1, 1, 10, 1));
      this.jPanel3.setLayout(new BoxLayout(this.jPanel3, 2));
      this.jLabel1.setText("Code");
      this.jPanel3.add(this.jLabel1);
      this.codeField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.codeFieldActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.codeField);
      this.jPanel2.add(this.jPanel3);
      this.jPanel4.setBorder(new SoftBevelBorder(1));
      this.jPanel4.setLayout(new GridLayout(0, 3));
      this.jButton1.setText("1");
      this.jButton1.setFocusPainted(false);
      this.jButton1.setFocusable(false);
      this.jButton1.setMargin(new Insets(2, 2, 2, 2));
      this.jButton1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton1);
      this.jButton2.setText("2");
      this.jButton2.setFocusPainted(false);
      this.jButton2.setFocusable(false);
      this.jButton2.setMargin(new Insets(2, 2, 2, 2));
      this.jButton2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton2);
      this.jButton3.setText("3");
      this.jButton3.setFocusPainted(false);
      this.jButton3.setFocusable(false);
      this.jButton3.setMargin(new Insets(2, 2, 2, 2));
      this.jButton3.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton3);
      this.jButton4.setText("4");
      this.jButton4.setFocusPainted(false);
      this.jButton4.setFocusable(false);
      this.jButton4.setMargin(new Insets(2, 2, 2, 2));
      this.jButton4.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton4);
      this.jButton5.setText("5");
      this.jButton5.setFocusPainted(false);
      this.jButton5.setFocusable(false);
      this.jButton5.setMargin(new Insets(2, 2, 2, 2));
      this.jButton5.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton5);
      this.jButton6.setText("6");
      this.jButton6.setFocusPainted(false);
      this.jButton6.setFocusable(false);
      this.jButton6.setMargin(new Insets(2, 2, 2, 2));
      this.jButton6.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton6);
      this.jButton7.setText("7");
      this.jButton7.setFocusPainted(false);
      this.jButton7.setFocusable(false);
      this.jButton7.setMargin(new Insets(2, 2, 2, 2));
      this.jButton7.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton7);
      this.jButton8.setText("8");
      this.jButton8.setFocusPainted(false);
      this.jButton8.setFocusable(false);
      this.jButton8.setMargin(new Insets(2, 2, 2, 2));
      this.jButton8.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton8);
      this.jButton9.setText("9");
      this.jButton9.setFocusPainted(false);
      this.jButton9.setFocusable(false);
      this.jButton9.setMargin(new Insets(2, 2, 2, 2));
      this.jButton9.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton9);
      this.jPanel4.add(this.jLabel2);
      this.jButton0.setText("0");
      this.jButton0.setFocusPainted(false);
      this.jButton0.setFocusable(false);
      this.jButton0.setMargin(new Insets(2, 2, 2, 2));
      this.jButton0.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButton0ActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButton0);
      this.jButtonC.setText("C");
      this.jButtonC.setFocusPainted(false);
      this.jButtonC.setFocusable(false);
      this.jButtonC.setMargin(new Insets(2, 2, 2, 2));
      this.jButtonC.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.jButtonCActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.jButtonC);
      this.jPanel2.add(this.jPanel4);
      this.jPanel1.setLayout(new GridLayout(1, 0));
      this.okButton.setText("Ok");
      this.okButton.setEnabled(false);
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.okButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.okButton);
      this.cancelButton.setText("Abbruch");
      this.cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stCodeFrame.this.cancelButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.cancelButton);
      this.jPanel2.add(this.jPanel1);
      this.getContentPane().add(this.jPanel2, "Center");
      this.pack();
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      if (this.validCode()) {
         this.setVisible(false);
         this.sendCode();
      }
   }

   private void cancelButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
   }

   private void codeFieldActionPerformed(ActionEvent evt) {
      if (this.validCode()) {
         this.setVisible(false);
         this.sendCode();
      }
   }

   private void jButton0ActionPerformed(ActionEvent evt) {
      this.codeField.setText(this.codeField.getText() + evt.getActionCommand());
   }

   private void jButtonCActionPerformed(ActionEvent evt) {
      this.codeField.setText("");
   }

   private static class codeTimer implements Runnable {
      private final String code;

      codeTimer(String code) {
         super();
         this.code = code;
      }

      public void run() {
         event.startActivityCall(this.code, "");
      }
   }
}
