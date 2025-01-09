package js.java.schaltungen.settings;

import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.adapter.closePrefs;

public class CloseQuestionDefaults extends JPanel {
   private final UserContextMini uc;
   private final closePrefs prefs;
   private JCheckBox communicatorCloseCB;
   private JCheckBox fahrplanEditorCB;
   private JCheckBox gleisEditorCloseCB;
   private JLabel jLabel1;
   private JPanel jPanel2;
   private JCheckBox landkarteEditorCloseCB;
   private JCheckBox simCloseCB;

   public CloseQuestionDefaults(UserContextMini uc) {
      this.uc = uc;
      this.initComponents();
      this.prefs = new closePrefs();
      EventBusService.getInstance().subscribe(this);
      this.updateData();
   }

   private void updateData() {
      this.communicatorCloseCB.setSelected(this.prefs.is(closePrefs.Parts.COMMUNICATOR));
      this.simCloseCB.setSelected(this.prefs.is(closePrefs.Parts.SIM));
      this.gleisEditorCloseCB.setSelected(this.prefs.is(closePrefs.Parts.GLEISEDITOR));
      this.landkarteEditorCloseCB.setSelected(this.prefs.is(closePrefs.Parts.LANDKARTE));
      this.fahrplanEditorCB.setSelected(this.prefs.is(closePrefs.Parts.FAHRPLANEDITOR));
   }

   private void store(closePrefs.Parts p, JCheckBox cb) {
      this.prefs.set(p, cb.isSelected());
      EventBusService.getInstance().publish(new PrefsChangedEvent(this));
   }

   @EventHandler
   public void prefsChanged(PrefsChangedEvent event) {
      if (!event.fromMe(this)) {
         this.updateData();
      }
   }

   private void initComponents() {
      this.jLabel1 = new JLabel();
      this.communicatorCloseCB = new JCheckBox();
      this.simCloseCB = new JCheckBox();
      this.gleisEditorCloseCB = new JCheckBox();
      this.fahrplanEditorCB = new JCheckBox();
      this.landkarteEditorCloseCB = new JCheckBox();
      this.jPanel2 = new JPanel();
      this.setLayout(new GridBagLayout());
      this.jLabel1.setText("Abfrage bei Fenster schließen ");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(4, 4, 4, 4);
      this.add(this.jLabel1, gridBagConstraints);
      this.communicatorCloseCB.setText("Komunikator");
      this.communicatorCloseCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            CloseQuestionDefaults.this.communicatorCloseCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(0, 10, 0, 0);
      this.add(this.communicatorCloseCB, gridBagConstraints);
      this.simCloseCB.setText("Simulator");
      this.simCloseCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            CloseQuestionDefaults.this.simCloseCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(0, 10, 0, 0);
      this.add(this.simCloseCB, gridBagConstraints);
      this.gleisEditorCloseCB.setText("Gleiseditor");
      this.gleisEditorCloseCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            CloseQuestionDefaults.this.gleisEditorCloseCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(0, 10, 0, 0);
      this.add(this.gleisEditorCloseCB, gridBagConstraints);
      this.fahrplanEditorCB.setText("Fahrplaneditor");
      this.fahrplanEditorCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            CloseQuestionDefaults.this.fahrplanEditorCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(0, 10, 0, 0);
      this.add(this.fahrplanEditorCB, gridBagConstraints);
      this.landkarteEditorCloseCB.setText("Landkarteneditor");
      this.landkarteEditorCloseCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            CloseQuestionDefaults.this.landkarteEditorCloseCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(0, 10, 0, 0);
      this.add(this.landkarteEditorCloseCB, gridBagConstraints);
      GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 0, 32767));
      jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 0, 32767));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.add(this.jPanel2, gridBagConstraints);
   }

   private void simCloseCBActionPerformed(ActionEvent evt) {
      this.store(closePrefs.Parts.SIM, this.simCloseCB);
   }

   private void communicatorCloseCBActionPerformed(ActionEvent evt) {
      this.store(closePrefs.Parts.COMMUNICATOR, this.communicatorCloseCB);
   }

   private void gleisEditorCloseCBActionPerformed(ActionEvent evt) {
      this.store(closePrefs.Parts.GLEISEDITOR, this.gleisEditorCloseCB);
   }

   private void fahrplanEditorCBActionPerformed(ActionEvent evt) {
      this.store(closePrefs.Parts.FAHRPLANEDITOR, this.fahrplanEditorCB);
   }

   private void landkarteEditorCloseCBActionPerformed(ActionEvent evt) {
      this.store(closePrefs.Parts.LANDKARTE, this.landkarteEditorCloseCB);
   }
}
