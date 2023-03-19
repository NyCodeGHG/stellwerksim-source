package js.java.isolate.sim.sim;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.StringTokenizer;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import js.java.isolate.sim.zug.zug;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class qscomment extends JDialog {
   private zug my_zug = null;
   private stellwerksim_main my_main = null;
   private final long simutime;
   private JButton cancelButton;
   private JTextArea commentArea;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JScrollPane jScrollPane1;
   private JButton okButton;
   private JTextField outputField;
   private JLabel outputHeading;
   private JComboBox textblockCB;
   private ButtonGroup typGroup;
   private JPanel typePanel;

   public qscomment(stellwerksim_main a, zug z) {
      super(a, false);
      this.my_main = a;
      this.my_zug = z;
      this.simutime = this.my_main.getSimutime();
      this.initComponents();
      this.initAdditionals();
      this.setSize(400, 300);
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION);
      this.setVisible(true);
   }

   private void initAdditionals() {
      JRadioButton rb = null;
      String l = this.my_main.getParameter("qslevels");
      if (l != null) {
         StringTokenizer pst = new StringTokenizer(l, ",");
         boolean firstItem = true;

         while(pst.hasMoreTokens()) {
            String tk1 = pst.nextToken();
            rb = new JRadioButton();
            rb.setText(tk1);
            rb.setActionCommand(tk1);
            rb.setFocusable(false);
            rb.setFocusPainted(false);
            this.typGroup.add(rb);
            this.typePanel.add(rb);
            if (firstItem) {
               rb.setSelected(true);
               firstItem = false;
            }
         }
      }

      this.outputField.setText(this.my_zug.getName());
      this.textblockCB.removeAllItems();

      try {
         l = this.my_main.getParameter("qstextblocks");
         int n = Integer.parseInt(l);

         for(int i = 0; i < n; ++i) {
            l = this.my_main.getParameter("qstextblock[" + i + "]");
            this.textblockCB.addItem(l);
         }
      } catch (Exception var6) {
      }

      this.textblockCB.addItem("-- sonstiges --");
      this.textblockCB.setSelectedItem(null);
   }

   private void updateGui() {
      this.okButton
         .setEnabled(
            this.textblockCB.getSelectedIndex() >= 0 && this.textblockCB.getSelectedIndex() < this.textblockCB.getItemCount() - 1
               || this.textblockCB.getSelectedIndex() == this.textblockCB.getItemCount() - 1
                  && this.commentArea.getText() != null
                  && this.commentArea.getText().length() > 5
         );
   }

   private void initComponents() {
      this.typGroup = new ButtonGroup();
      this.outputHeading = new JLabel();
      this.outputField = new JTextField();
      this.jLabel1 = new JLabel();
      this.typePanel = new JPanel();
      this.jLabel3 = new JLabel();
      this.textblockCB = new JComboBox();
      this.jLabel2 = new JLabel();
      this.jScrollPane1 = new JScrollPane();
      this.commentArea = new JTextArea();
      this.okButton = new JButton();
      this.cancelButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Fehlermeldung für einen Zug");
      this.setLocationByPlatform(true);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            qscomment.this.formWindowClosed(evt);
         }
      });
      this.getContentPane().setLayout(new GridBagLayout());
      this.outputHeading.setText("Meldung für Zug");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(10, 10, 2, 5);
      this.getContentPane().add(this.outputHeading, gridBagConstraints);
      this.outputField.setEditable(false);
      this.outputField.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(10, 2, 2, 10);
      this.getContentPane().add(this.outputField, gridBagConstraints);
      this.jLabel1.setText("Meldungstyp");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(2, 10, 2, 5);
      this.getContentPane().add(this.jLabel1, gridBagConstraints);
      this.typePanel.setBorder(this.outputField.getBorder());
      this.typePanel.setLayout(new GridLayout(0, 2));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 10);
      this.getContentPane().add(this.typePanel, gridBagConstraints);
      this.jLabel3.setText("Beschreibung");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(2, 10, 2, 5);
      this.getContentPane().add(this.jLabel3, gridBagConstraints);
      this.textblockCB.setFocusable(false);
      this.textblockCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            qscomment.this.textblockCBItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 10);
      this.getContentPane().add(this.textblockCB, gridBagConstraints);
      this.jLabel2.setText("Zusatz");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 18;
      gridBagConstraints.insets = new Insets(2, 10, 2, 5);
      this.getContentPane().add(this.jLabel2, gridBagConstraints);
      this.commentArea.setColumns(20);
      this.commentArea.setRows(5);
      this.commentArea.addKeyListener(new KeyAdapter() {
         public void keyTyped(KeyEvent evt) {
            qscomment.this.commentAreaKeyTyped(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.commentArea);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(2, 2, 2, 10);
      this.getContentPane().add(this.jScrollPane1, gridBagConstraints);
      this.okButton.setText("Speichern");
      this.okButton.setEnabled(false);
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            qscomment.this.okButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(5, 5, 10, 5);
      this.getContentPane().add(this.okButton, gridBagConstraints);
      this.cancelButton.setText("Abbrechen");
      this.cancelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            qscomment.this.cancelButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 10;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(5, 5, 10, 10);
      this.getContentPane().add(this.cancelButton, gridBagConstraints);
      this.pack();
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      String level = this.typGroup.getSelection().getActionCommand();
      String c = "";
      if (this.textblockCB.getSelectedIndex() < this.textblockCB.getItemCount() - 1) {
         c = this.textblockCB.getSelectedItem().toString();
      }

      if (this.commentArea.getText().length() > 2) {
         if (c.length() > 0) {
            c = c + "\n\n";
         }

         c = c + this.commentArea.getText();
      }

      this.setVisible(false);
      this.my_main.sendZugComment(this.my_zug, this.simutime, level, c);
   }

   private void cancelButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
   }

   private void formWindowClosed(WindowEvent evt) {
      this.setVisible(false);
   }

   private void commentAreaKeyTyped(KeyEvent evt) {
      this.updateGui();
   }

   private void textblockCBItemStateChanged(ItemEvent evt) {
      this.updateGui();
      this.commentArea.requestFocusInWindow();
   }
}
