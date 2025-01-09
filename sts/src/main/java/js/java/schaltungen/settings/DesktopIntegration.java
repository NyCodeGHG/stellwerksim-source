package js.java.schaltungen.settings;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.jnlp.IntegrationService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import js.java.schaltungen.UserContextMini;

public class DesktopIntegration extends JPanel {
   private IntegrationService is = null;
   private final UserContextMini uc;
   private JCheckBox desktopIconCB;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JSeparator jSeparator1;
   private JButton setButton;
   private JCheckBox startMenuCB;

   DesktopIntegration(UserContextMini uc) {
      this.uc = uc;
      this.initComponents();

      try {
         this.is = (IntegrationService)ServiceManager.lookup("javax.jnlp.IntegrationService");
      } catch (UnavailableServiceException var3) {
      }

      this.setButton.setEnabled(this.is != null);
      this.desktopIconCB.setEnabled(this.is != null);
      this.startMenuCB.setEnabled(this.is != null);
      this.refreshValues();
   }

   private void setValues() {
      if (this.is != null) {
         if (this.is.hasDesktopShortcut() != this.desktopIconCB.isSelected() || this.is.hasMenuShortcut() != this.startMenuCB.isSelected()) {
            this.is.removeShortcuts();
            this.is.requestShortcut(this.desktopIconCB.isSelected(), this.startMenuCB.isSelected(), "StellwerkSim");
         }
      }
   }

   private void refreshValues() {
      if (this.is != null) {
         this.desktopIconCB.setSelected(this.is.hasDesktopShortcut());
         this.startMenuCB.setSelected(this.is.hasMenuShortcut());
      }
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.startMenuCB = new JCheckBox();
      this.jLabel1 = new JLabel();
      this.desktopIconCB = new JCheckBox();
      this.jLabel2 = new JLabel();
      this.jSeparator1 = new JSeparator();
      this.setButton = new JButton();
      this.jPanel2 = new JPanel();
      this.setLayout(new BorderLayout());
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel1.setLayout(new GridBagLayout());
      this.startMenuCB.setText("Startmenü Eintrag");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.jPanel1.add(this.startMenuCB, gridBagConstraints);
      this.jLabel1.setText("<html>Erzeugt oder löscht einen Einstrag im Startmenü um das Programm zu starten.</html>");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.jPanel1.add(this.jLabel1, gridBagConstraints);
      this.desktopIconCB.setText("Desktop Icon");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(10, 0, 0, 0);
      this.jPanel1.add(this.desktopIconCB, gridBagConstraints);
      this.jLabel2.setText("<html>Erzeugt oder löscht ein Symbol (Icon) auf dem Desktop um das Programm zu starten.</html>");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 0, 5, 0);
      this.jPanel1.add(this.jSeparator1, gridBagConstraints);
      this.setButton.setText("ändern");
      this.setButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            DesktopIntegration.this.setButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      this.jPanel1.add(this.setButton, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel1.add(this.jPanel2, gridBagConstraints);
      this.add(this.jPanel1, "Center");
   }

   private void setButtonActionPerformed(ActionEvent evt) {
      this.setValues();
   }

   private void formWindowGainedFocus(WindowEvent evt) {
   }
}
