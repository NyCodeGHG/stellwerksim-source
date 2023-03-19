package js.java.schaltungen.adapter;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import js.java.schaltungen.UserContextMini;

public class CloseConfirmDialog extends JDialog {
   private boolean confirm = false;
   private final closePrefs.Parts p;
   private JButton cancelButton;
   private JCheckBox dontAskCB;
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JSeparator jSeparator1;
   private JButton okButton;
   private JLabel textLabel;
   private JLabel title;

   public CloseConfirmDialog(UserContextMini uc, Component parent, closePrefs.Parts p, String text) {
      super(SwingUtilities.getWindowAncestor(parent));
      this.p = p;
      this.initComponents();
      this.getRootPane().setDefaultButton(this.okButton);
      this.setIconImage(uc.getWindowIcon());
      this.textLabel.setText("<html>" + text + "</html>");
      this.pack();
      this.setLocationRelativeTo(parent);
   }

   public boolean confirm() {
      this.setVisible(true);
      return this.confirm;
   }

   private void initComponents() {
      this.title = new JLabel();
      this.textLabel = new JLabel();
      this.jSeparator1 = new JSeparator();
      this.jPanel1 = new JPanel();
      this.dontAskCB = new JCheckBox();
      this.jLabel1 = new JLabel();
      this.okButton = new JButton();
      this.cancelButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Wirklich schließen?");
      this.setModal(true);
      this.getContentPane().setLayout(new GridBagLayout());
      this.title.setBackground(Color.black);
      this.title.setFont(this.title.getFont().deriveFont(this.title.getFont().getStyle() | 1));
      this.title.setForeground(Color.white);
      this.title.setText("Fenster schließen");
      this.title.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
      this.title.setOpaque(true);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 23;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.getContentPane().add(this.title, gridBagConstraints);
      this.textLabel.setText("jLabel1");
      this.textLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 20, 10));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 23;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.getContentPane().add(this.textLabel, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.getContentPane().add(this.jSeparator1, gridBagConstraints);
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel1.setLayout(new GridLayout(0, 2, 50, 5));
      this.dontAskCB.setText("Dafür nicht nochmal fragen");
      this.dontAskCB.setFocusPainted(false);
      this.jPanel1.add(this.dontAskCB);
      this.jLabel1.setText(" ");
      this.jPanel1.add(this.jLabel1);
      this.okButton.setText("Ok");
      this.okButton.setMargin(new Insets(5, 14, 5, 14));
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            CloseConfirmDialog.this.okButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.okButton);
      this.cancelButton.setText("Cancel");
      this.cancelButton.setMargin(new Insets(5, 14, 5, 14));
      this.cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            CloseConfirmDialog.this.cancelButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.cancelButton);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.getContentPane().add(this.jPanel1, gridBagConstraints);
      this.pack();
   }

   private void cancelButtonActionPerformed(ActionEvent evt) {
      this.dispose();
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      this.confirm = true;
      this.dispose();
      if (this.dontAskCB.isSelected()) {
         this.p.clear();
      }
   }
}
