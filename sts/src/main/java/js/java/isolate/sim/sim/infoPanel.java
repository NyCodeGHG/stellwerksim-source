package js.java.isolate.sim.sim;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import js.java.tools.gui.textticker;
import js.java.tools.gui.border.DropShadowBorder;

public class infoPanel extends JPanel {
   private final stellwerksim_main my_main;
   private final textticker textTextField;
   private JButton clearButton;
   private JPanel jPanel1;
   private JPanel jPanel6;
   private JTextField searchZug;
   private JTextField timeTextField;

   public infoPanel(stellwerksim_main m) {
      super();
      this.my_main = m;
      this.initComponents();
      this.textTextField = new textticker();
      this.textTextField.setAlignmentX(0.0F);
      this.textTextField.setAlignmentY(0.0F);
      this.textTextField.setBorder(null);
      this.textTextField.setMaximumSize(new Dimension(10000, 22));
      this.textTextField.setMinimumSize(new Dimension(50, 18));
      this.textTextField.setPreferredSize(new Dimension(10000, 20));
      this.add(this.textTextField, "Center");
      this.searchZug.getDocument().addDocumentListener(new DocumentListener() {
         public void insertUpdate(DocumentEvent e) {
            infoPanel.this.clearButton.setEnabled(e.getDocument().getLength() > 0);
         }

         public void removeUpdate(DocumentEvent e) {
            infoPanel.this.clearButton.setEnabled(e.getDocument().getLength() > 0);
         }

         public void changedUpdate(DocumentEvent e) {
            infoPanel.this.clearButton.setEnabled(e.getDocument().getLength() > 0);
         }
      });
   }

   public void setFocus() {
      this.searchZug.setCaretPosition(0);
      this.searchZug.setSelectionStart(0);
      this.searchZug.setSelectionEnd(this.searchZug.getText().length());
      this.searchZug.requestFocusInWindow();
   }

   private void initComponents() {
      this.jPanel6 = new JPanel();
      this.timeTextField = new JTextField();
      this.jPanel1 = new JPanel();
      this.searchZug = new JTextField();
      this.clearButton = new JButton();
      this.setBorder(new DropShadowBorder(true, true, true, true));
      this.setLayout(new BorderLayout());
      this.jPanel6.setLayout(new FlowLayout(0, 0, 0));
      this.timeTextField.setEditable(false);
      this.timeTextField.setFont(new Font("Dialog", 0, 11));
      this.timeTextField.setHorizontalAlignment(0);
      this.timeTextField.setText("00:00:00");
      this.timeTextField.setAlignmentX(0.0F);
      this.timeTextField.setAlignmentY(0.0F);
      this.timeTextField.setBorder(null);
      this.timeTextField.setMargin(new Insets(2, 2, 0, 0));
      this.timeTextField.setMaximumSize(new Dimension(60, 20));
      this.timeTextField.setMinimumSize(new Dimension(60, 18));
      this.timeTextField.setPreferredSize(new Dimension(60, 18));
      this.jPanel6.add(this.timeTextField);
      this.jPanel1.setLayout(new BorderLayout());
      this.searchZug.setColumns(5);
      this.searchZug.setFont(new Font("Dialog", 0, 10));
      this.searchZug.setToolTipText("Zugsuche");
      this.searchZug.setMargin(new Insets(2, 2, 0, 0));
      this.searchZug.setMaximumSize(new Dimension(100, 30));
      this.searchZug.setMinimumSize(new Dimension(50, 16));
      this.searchZug.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            infoPanel.this.searchZugActionPerformed(evt);
         }
      });
      this.searchZug.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent evt) {
            infoPanel.this.searchZugKeyReleased(evt);
         }
      });
      this.jPanel1.add(this.searchZug, "Center");
      this.clearButton.setBackground(UIManager.getDefaults().getColor("TextField.background"));
      this.clearButton.setText("x");
      this.clearButton.setToolTipText("Lösche Suchfeld");
      this.clearButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.clearButton.setContentAreaFilled(false);
      this.clearButton.setEnabled(false);
      this.clearButton.setFocusPainted(false);
      this.clearButton.setFocusable(false);
      this.clearButton.setMargin(new Insets(0, 0, 0, 0));
      this.clearButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            infoPanel.this.clearButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.clearButton, "East");
      this.jPanel6.add(this.jPanel1);
      this.add(this.jPanel6, "West");
   }

   private void searchZugActionPerformed(ActionEvent evt) {
      if (this.searchZug.getText().length() > 1) {
         this.my_main.searchZug(this.searchZug.getText(), false);
         this.searchZug.requestFocus();
      }
   }

   private void searchZugKeyReleased(KeyEvent evt) {
      if (this.searchZug.getText().length() > 1) {
         this.my_main.searchZug(this.searchZug.getText(), true);
         this.searchZug.requestFocus();
      }
   }

   private void clearButtonActionPerformed(ActionEvent evt) {
      this.searchZug.setText("");
   }

   public textticker getTextTextField() {
      return this.textTextField;
   }

   public JTextField getTimeTextField() {
      return this.timeTextField;
   }

   public void stopTextTextField() {
      this.textTextField.stopRunning();
   }

   void setPause(boolean on) {
      this.textTextField.setPause(on);
   }
}
