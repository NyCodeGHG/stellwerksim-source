package js.java.schaltungen.settings;

import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.adapter.plafPrefs;

public class LookAndFeel extends JPanel {
   private final UserContextMini uc;
   private final plafPrefs prefs;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JPanel jPanel1;
   private JCheckBox largeScrollersCB;
   private JCheckBox scrollButtonsCB;
   private JCheckBox titleMenuCB;

   LookAndFeel(UserContextMini uc) {
      this.uc = uc;
      this.initComponents();
      this.prefs = new plafPrefs();
      EventBusService.getInstance().subscribe(this);
      this.updateData();
   }

   private void updateData() {
      this.titleMenuCB.setSelected(this.prefs.is(plafPrefs.Parts.EMBEDD_MENU));
      this.scrollButtonsCB.setSelected(this.prefs.is(plafPrefs.Parts.SCROLL_BUTTONS));
      this.largeScrollersCB.setSelected(this.prefs.is(plafPrefs.Parts.LARGE_SCROLLERS));
   }

   private void store(plafPrefs.Parts p, JCheckBox cb) {
      this.prefs.set(p, cb.isSelected());
      p.configure();
      EventBusService.getInstance().publish(new PrefsChangedEvent(this));
   }

   @EventHandler
   public void prefsChanged(PrefsChangedEvent event) {
      if (!event.fromMe(this)) {
         this.updateData();
      }
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.titleMenuCB = new JCheckBox();
      this.jLabel2 = new JLabel();
      this.scrollButtonsCB = new JCheckBox();
      this.jLabel3 = new JLabel();
      this.largeScrollersCB = new JCheckBox();
      this.jLabel4 = new JLabel();
      this.setLayout(new BorderLayout());
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel1.setLayout(new GridBagLayout());
      this.titleMenuCB.setText("Menü nicht im Fenstertitel");
      this.titleMenuCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            LookAndFeel.this.titleMenuCBActionPerformed(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.jPanel1.add(this.titleMenuCB, gridBagConstraints);
      this.jLabel2.setText("<html>Menü klassisch darunter</html>");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      this.scrollButtonsCB.setText("Scroll Buttons zeigen");
      this.scrollButtonsCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            LookAndFeel.this.scrollButtonsCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(10, 0, 0, 0);
      this.jPanel1.add(this.scrollButtonsCB, gridBagConstraints);
      this.jLabel3.setText("<html>Pfeilknöpfe an Scrollleisten darstellen, erst bei neuen Fenstern</html>");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.jPanel1.add(this.jLabel3, gridBagConstraints);
      this.largeScrollersCB.setText("Breite Scrollleisten");
      this.largeScrollersCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            LookAndFeel.this.largeScrollersCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(10, 0, 0, 0);
      this.jPanel1.add(this.largeScrollersCB, gridBagConstraints);
      this.jLabel4.setText("<html>Scrollleisten breiter darstellen, erst bei neuen Fenstern</html>");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.jPanel1.add(this.jLabel4, gridBagConstraints);
      this.add(this.jPanel1, "North");
   }

   private void formWindowGainedFocus(WindowEvent evt) {
   }

   private void titleMenuCBActionPerformed(ActionEvent evt) {
      this.store(plafPrefs.Parts.EMBEDD_MENU, this.titleMenuCB);
   }

   private void scrollButtonsCBActionPerformed(ActionEvent evt) {
      this.store(plafPrefs.Parts.SCROLL_BUTTONS, this.scrollButtonsCB);
   }

   private void largeScrollersCBActionPerformed(ActionEvent evt) {
      this.store(plafPrefs.Parts.LARGE_SCROLLERS, this.largeScrollersCB);
   }
}
