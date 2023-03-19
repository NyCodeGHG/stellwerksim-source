package js.java.schaltungen.settings;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import js.java.schaltungen.UserContextMini;
import js.java.tools.prefs;

public class GraphicFixes extends JPanel {
   private final UserContextMini uc;
   private final prefs prefs;
   private JCheckBox d3dCB;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JSeparator jSeparator1;
   private JCheckBox openglCB;
   private JButton setButton;

   GraphicFixes(UserContextMini uc) {
      super();
      this.uc = uc;
      this.initComponents();
      this.prefs = new prefs("/org/js-home/stellwerksim/gfxfixes");
      this.refreshValues();
   }

   private void setValues() {
      this.prefs.putBoolean("opengl", this.openglCB.isSelected());
      this.prefs.putBoolean("d3d", this.d3dCB.isSelected());
      JOptionPane.showMessageDialog(
         this.setButton, "<html>Diese Werte gelten erst ab einem<br><b>Neustart des Kommunikators</b>!</html>", "Neustart erforderlich", 1
      );
   }

   private void refreshValues() {
      this.openglCB.setSelected(this.prefs.getBoolean("opengl", false));
      this.d3dCB.setSelected(this.prefs.getBoolean("d3d", false));
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.d3dCB = new JCheckBox();
      this.jLabel1 = new JLabel();
      this.openglCB = new JCheckBox();
      this.jLabel2 = new JLabel();
      this.jSeparator1 = new JSeparator();
      this.jLabel3 = new JLabel();
      this.setButton = new JButton();
      this.jPanel2 = new JPanel();
      this.setLayout(new BorderLayout());
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel1.setLayout(new GridBagLayout());
      this.d3dCB.setText("java2d d3d abschalten");
      this.d3dCB.setToolTipText("sun.java2d.d3d = false");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.jPanel1.add(this.d3dCB, gridBagConstraints);
      this.jLabel1.setText("<html>Bei Grafikproblemen mit dem Gleisbild im Sim kann dieser Schalter helfen.</html>");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.jPanel1.add(this.jLabel1, gridBagConstraints);
      this.openglCB.setText("opengl statt DirectX aktivieren");
      this.openglCB.setToolTipText("sun.java2d.noddraw = true");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(10, 0, 0, 0);
      this.jPanel1.add(this.openglCB, gridBagConstraints);
      this.jLabel2.setText("<html>Bei Grafikproblemen mit dem Gleisbild im Sim kann dieser Schalter helfen.</html>");
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
      this.jLabel3
         .setText(
            "<html>Diese Einstellungen gelten erst ab dem nächsten Programmstart. Den Kommunikator deshalb zum Aktivieren der Einstellungen beenden.</html>"
         );
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 20;
      gridBagConstraints.insets = new Insets(0, 5, 10, 5);
      this.jPanel1.add(this.jLabel3, gridBagConstraints);
      this.setButton.setText("ändern");
      this.setButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            GraphicFixes.this.setButtonActionPerformed(evt);
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
