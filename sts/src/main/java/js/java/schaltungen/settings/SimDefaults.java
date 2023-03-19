package js.java.schaltungen.settings;

import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.GroupLayout.Alignment;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.adapter.simPrefs;
import js.java.tools.prefs;

public class SimDefaults extends JPanel {
   private final UserContextMini uc;
   private final prefs prefs;
   private JCheckBox alterColorCB;
   private ButtonGroup buttonGroup1;
   private JRadioButton dauer1;
   private JRadioButton dauer2;
   private JRadioButton dauer3;
   private JRadioButton dauer4;
   private ButtonGroup dauerBG;
   private JRadioButton funMenuRB;
   private JCheckBox gleisGroupCB;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JSeparator jSeparator1;
   private JCheckBox longDualKeysCB;
   private JCheckBox nightViewCB;
   private JCheckBox noGleisCB;
   private JRadioButton realisticRB;

   public SimDefaults(UserContextMini uc) {
      super();
      this.uc = uc;
      this.initComponents();
      this.prefs = new simPrefs();
      EventBusService.getInstance().subscribe(this);
      this.updateData();
   }

   private void updateData() {
      this.gleisGroupCB.setSelected(this.prefs.getBoolean("gleisGroup", false));
      this.alterColorCB.setSelected(this.prefs.getBoolean("alterColor", false));
      this.nightViewCB.setSelected(this.prefs.getBoolean("isNightMode", false));
      this.funMenuRB.setSelected(!this.prefs.getBoolean("realistic", false));
      this.realisticRB.setSelected(this.prefs.getBoolean("realistic", false));
      this.longDualKeysCB.setSelected(this.prefs.getBoolean("longDualKey", false));
      this.noGleisCB.setSelected(this.prefs.getBoolean("noGleis", false));
      this.readDauer();
   }

   private void readDauer() {
      int idx = this.prefs.getInt("plannedtime", 1);
      switch(idx) {
         case 0:
            this.dauer1.setSelected(true);
            break;
         case 1:
         default:
            this.dauer2.setSelected(true);
            break;
         case 2:
            this.dauer3.setSelected(true);
            break;
         case 3:
            this.dauer4.setSelected(true);
      }
   }

   private void storeDauer() {
      int idx = 1;
      if (this.dauer1.isSelected()) {
         idx = 0;
      } else if (this.dauer2.isSelected()) {
         idx = 1;
      } else if (this.dauer3.isSelected()) {
         idx = 2;
      } else if (this.dauer4.isSelected()) {
         idx = 3;
      }

      this.prefs.putInt("plannedtime", idx);
   }

   @EventHandler
   public void prefsChanged(PrefsChangedEvent event) {
      if (!event.fromMe(this)) {
         this.updateData();
      }
   }

   private void initComponents() {
      this.buttonGroup1 = new ButtonGroup();
      this.dauerBG = new ButtonGroup();
      this.gleisGroupCB = new JCheckBox();
      this.nightViewCB = new JCheckBox();
      this.alterColorCB = new JCheckBox();
      this.longDualKeysCB = new JCheckBox();
      this.noGleisCB = new JCheckBox();
      this.jSeparator1 = new JSeparator();
      this.jLabel1 = new JLabel();
      this.funMenuRB = new JRadioButton();
      this.realisticRB = new JRadioButton();
      this.jPanel1 = new JPanel();
      this.jLabel2 = new JLabel();
      this.dauer1 = new JRadioButton();
      this.dauer2 = new JRadioButton();
      this.dauer3 = new JRadioButton();
      this.dauer4 = new JRadioButton();
      this.jLabel3 = new JLabel();
      this.jPanel2 = new JPanel();
      this.setLayout(new GridBagLayout());
      this.gleisGroupCB.setText("realistischere Gleisausleuchtung");
      this.gleisGroupCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.gleisGroupCBActionPerformed(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.add(this.gleisGroupCB, gridBagConstraints);
      this.nightViewCB.setText("Gleisbild Nachtansicht");
      this.nightViewCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.nightViewCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.add(this.nightViewCB, gridBagConstraints);
      this.alterColorCB.setText("Alternative Rot-Farbe");
      this.alterColorCB.setToolTipText("geeigneter für Rot/Grün-Schwäche");
      this.alterColorCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.alterColorCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.add(this.alterColorCB, gridBagConstraints);
      this.longDualKeysCB.setText("Längere Reaktionszeiten");
      this.longDualKeysCB.setToolTipText("verlängert die Zeiten von Gruppentasten mit Folgetasten unter Zeitbegrenzung wie UFGT und man. gl. FS");
      this.longDualKeysCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.longDualKeysCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.add(this.longDualKeysCB, gridBagConstraints);
      this.noGleisCB.setText("kein \"Gleis\" in \"nach\"");
      this.noGleisCB.setToolTipText("zeigt in der \"Nach\" Spalte nicht das Wort \"Gleis\" an");
      this.noGleisCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.noGleisCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      this.add(this.noGleisCB, gridBagConstraints);
      this.jSeparator1.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridheight = 5;
      gridBagConstraints.fill = 3;
      gridBagConstraints.insets = new Insets(0, 10, 0, 10);
      this.add(this.jSeparator1, gridBagConstraints);
      this.jLabel1.setText("Simulationsmodus");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.anchor = 21;
      this.add(this.jLabel1, gridBagConstraints);
      this.buttonGroup1.add(this.funMenuRB);
      this.funMenuRB.setSelected(true);
      this.funMenuRB.setText("Unterhaltung");
      this.funMenuRB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.funMenuRBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.anchor = 21;
      this.add(this.funMenuRB, gridBagConstraints);
      this.buttonGroup1.add(this.realisticRB);
      this.realisticRB.setText("Realer");
      this.realisticRB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.realisticRBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.anchor = 21;
      this.add(this.realisticRB, gridBagConstraints);
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("Geplante Spielzeit"));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel2
         .setText(
            "<html>Über diese Optionen kann die schlecht funktionierende Zukunftsprognose deiner Spieldauer unterstützt werden. Dies wirkt sich u.U. auf die Menge bzw. Frequenz der Störungen aus.</html>"
         );
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 10, 5);
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      this.dauerBG.add(this.dauer1);
      this.dauer1.setText("nur kurz, gibt mir schnell was");
      this.dauer1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.dauer1ActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(3, 20, 3, 3);
      this.jPanel1.add(this.dauer1, gridBagConstraints);
      this.dauerBG.add(this.dauer2);
      this.dauer2.setSelected(true);
      this.dauer2.setText("eine Stunde, mehr aber eher nicht (Standardverhalten wie vom Erbauer gesetzt)");
      this.dauer2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.dauer1ActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(3, 20, 3, 3);
      this.jPanel1.add(this.dauer2, gridBagConstraints);
      this.dauerBG.add(this.dauer3);
      this.dauer3.setText("länger, lass mir Zeit");
      this.dauer3.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.dauer1ActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(3, 20, 3, 3);
      this.jPanel1.add(this.dauer3, gridBagConstraints);
      this.dauerBG.add(this.dauer4);
      this.dauer4.setText("alter, richtig lang, in echt passiert auch nicht ständig etwas");
      this.dauer4.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            SimDefaults.this.dauer1ActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(3, 20, 3, 3);
      this.jPanel1.add(this.dauer4, gridBagConstraints);
      this.jLabel3
         .setText(
            "<html>Die Einstellung wirkt erst ab dem nächsten Spielstart. Die Auswirkungen sind nicht zwingend konstant und nicht zeitlich genau spezifizierbar.</html>"
         );
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(10, 5, 5, 5);
      this.jPanel1.add(this.jLabel3, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(10, 0, 10, 0);
      this.add(this.jPanel1, gridBagConstraints);
      GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 0, 32767));
      jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 0, 32767));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.weighty = 1.0;
      this.add(this.jPanel2, gridBagConstraints);
   }

   private void gleisGroupCBActionPerformed(ActionEvent evt) {
      this.prefs.putBoolean("gleisGroup", this.gleisGroupCB.isSelected());
      this.prefs.flush();
      EventBusService.getInstance().publish(new PrefsChangedEvent(this));
   }

   private void nightViewCBActionPerformed(ActionEvent evt) {
      this.prefs.putBoolean("isNightMode", this.nightViewCB.isSelected());
      this.prefs.flush();
      EventBusService.getInstance().publish(new PrefsChangedEvent(this));
   }

   private void alterColorCBActionPerformed(ActionEvent evt) {
      this.prefs.putBoolean("alterColor", this.alterColorCB.isSelected());
      this.prefs.flush();
      EventBusService.getInstance().publish(new PrefsChangedEvent(this));
   }

   private void funMenuRBActionPerformed(ActionEvent evt) {
      this.prefs.putBoolean("realistic", !this.funMenuRB.isSelected());
      this.prefs.flush();
      EventBusService.getInstance().publish(new PrefsChangedEvent(this));
   }

   private void realisticRBActionPerformed(ActionEvent evt) {
      if (RealisticSure.question(this)) {
         this.prefs.putBoolean("realistic", this.realisticRB.isSelected());
         this.prefs.flush();
         EventBusService.getInstance().publish(new PrefsChangedEvent(this));
      } else {
         this.funMenuRB.setSelected(true);
      }
   }

   private void dauer1ActionPerformed(ActionEvent evt) {
      this.storeDauer();
   }

   private void longDualKeysCBActionPerformed(ActionEvent evt) {
      this.prefs.putBoolean("longDualKey", this.longDualKeysCB.isSelected());
      this.prefs.flush();
      EventBusService.getInstance().publish(new PrefsChangedEvent(this));
   }

   private void noGleisCBActionPerformed(ActionEvent evt) {
      this.prefs.putBoolean("noGleis", this.noGleisCB.isSelected());
      this.prefs.flush();
      EventBusService.getInstance().publish(new PrefsChangedEvent(this));
   }
}
