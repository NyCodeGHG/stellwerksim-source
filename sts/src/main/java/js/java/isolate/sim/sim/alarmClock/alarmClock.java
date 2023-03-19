package js.java.isolate.sim.sim.alarmClock;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.isolate.sim.sim.TEXTTYPE;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.schaltungen.audio.AudioController;

public class alarmClock extends JDialog {
   private static alarmClock instance = null;
   private final stellwerksim_main my_main;
   private boolean setting = false;
   private final DefaultListModel alarms;
   private JButton addButton;
   private JList alarmList;
   private JTextField alarmText;
   private JButton delButton;
   private JSpinner hourSpinner;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JPanel jPanel4;
   private JScrollPane jScrollPane1;
   private JSpinner minuteSpinner;

   public static void showInstance(stellwerksim_main my_main) {
      if (instance == null) {
         instance = new alarmClock(my_main);
      }

      instance.show();
   }

   private alarmClock(stellwerksim_main a) {
      super(a, false);
      this.my_main = a;
      this.alarms = new DefaultListModel();
      this.initComponents();
      FocusListener fl = new FocusListener() {
         public void focusGained(FocusEvent e) {
            JFormattedTextField textField = (JFormattedTextField)e.getComponent();
            textField.setText(textField.getText());
            textField.selectAll();
         }

         public void focusLost(FocusEvent e) {
         }
      };
      NumberEditor editor = (NumberEditor)this.hourSpinner.getEditor();
      JFormattedTextField editfield = editor.getTextField();
      editfield.addFocusListener(fl);
      editor = (NumberEditor)this.minuteSpinner.getEditor();
      editfield = editor.getTextField();
      editfield.addFocusListener(fl);
      this.updateAlarm();
      this.alarmText.getDocument().addDocumentListener(new DocumentListener() {
         public void insertUpdate(DocumentEvent e) {
            alarmClock.this.alarmChanged();
         }

         public void removeUpdate(DocumentEvent e) {
            alarmClock.this.alarmChanged();
         }

         public void changedUpdate(DocumentEvent e) {
            alarmClock.this.alarmChanged();
         }
      });
   }

   private void alarmChanged() {
      if (!this.setting) {
         alarmItem sel = (alarmItem)this.alarmList.getSelectedValue();
         if (sel != null) {
            sel.update(this.hourSpinner.getValue(), this.minuteSpinner.getValue(), this.alarmText.getText());
            this.alarmList.repaint();
         }
      }
   }

   private void updateAlarm() {
      alarmItem sel = (alarmItem)this.alarmList.getSelectedValue();
      this.delButton.setEnabled(sel != null);
      this.hourSpinner.setEnabled(sel != null);
      this.minuteSpinner.setEnabled(sel != null);
      this.alarmText.setEnabled(sel != null);
      if (sel != null) {
         this.setting = true;
         this.hourSpinner.setValue(sel.getH());
         this.minuteSpinner.setValue(sel.getM());
         this.alarmText.setText(sel.getText());
         this.setting = false;
         this.focusHour();
      }
   }

   void alarm(alarmItem ai) {
      this.my_main.showText("Wecker: " + ai.toString(), TEXTTYPE.MESSAGE, this);
      this.my_main.playFX(AudioController.FXSOUND.BEEP);
   }

   void removeAlarm(alarmItem ai) {
      ai.remove();
      this.alarms.removeElement(ai);
   }

   long getSimutime() {
      return this.my_main.getSimutime();
   }

   private void focusHour() {
      this.hourSpinner.requestFocusInWindow();
      NumberEditor editor = (NumberEditor)this.hourSpinner.getEditor();
      JFormattedTextField editfield = editor.getTextField();
      editfield.requestFocusInWindow();
   }

   private void initComponents() {
      this.jPanel4 = new JPanel();
      this.jPanel1 = new JPanel();
      this.jPanel2 = new JPanel();
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.jLabel3 = new JLabel();
      this.hourSpinner = new JSpinner();
      this.minuteSpinner = new JSpinner();
      this.alarmText = new JTextField();
      this.jPanel3 = new JPanel();
      this.addButton = new JButton();
      this.delButton = new JButton();
      this.jScrollPane1 = new JScrollPane();
      this.alarmList = new JList();
      this.jLabel4 = new JLabel();
      this.setTitle("Wecker");
      this.setLocationByPlatform(true);
      this.jPanel4.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel4.setLayout(new BorderLayout(10, 10));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jPanel2.setBorder(BorderFactory.createTitledBorder("Alarmdaten"));
      this.jPanel2.setLayout(new GridBagLayout());
      this.jLabel1.setText("Stunde");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      this.jPanel2.add(this.jLabel1, gridBagConstraints);
      this.jLabel2.setText("Minute");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      this.jPanel2.add(this.jLabel2, gridBagConstraints);
      this.jLabel3.setText("Text");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      this.jPanel2.add(this.jLabel3, gridBagConstraints);
      this.hourSpinner.setModel(new SpinnerNumberModel(5, 5, 20, 1));
      this.hourSpinner.setNextFocusableComponent(this.minuteSpinner);
      this.hourSpinner.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent evt) {
            alarmClock.this.alarmChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      this.jPanel2.add(this.hourSpinner, gridBagConstraints);
      this.minuteSpinner.setModel(new SpinnerNumberModel(0, 0, 59, 1));
      this.minuteSpinner.setNextFocusableComponent(this.alarmText);
      this.minuteSpinner.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent evt) {
            alarmClock.this.alarmChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      this.jPanel2.add(this.minuteSpinner, gridBagConstraints);
      this.alarmText.setNextFocusableComponent(this.hourSpinner);
      this.alarmText.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent evt) {
            alarmClock.this.alarmTextFocusGained(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      this.jPanel2.add(this.alarmText, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.jPanel2, gridBagConstraints);
      this.jPanel3.setLayout(new GridLayout(0, 1));
      this.addButton.setMnemonic('n');
      this.addButton.setText("neu");
      this.addButton.setFocusable(false);
      this.addButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            alarmClock.this.addButtonActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.addButton);
      this.delButton.setMnemonic('l');
      this.delButton.setText("löschen");
      this.delButton.setFocusable(false);
      this.delButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            alarmClock.this.delButtonActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.delButton);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 3;
      gridBagConstraints.anchor = 13;
      this.jPanel1.add(this.jPanel3, gridBagConstraints);
      this.jPanel4.add(this.jPanel1, "South");
      this.alarmList.setModel(this.alarms);
      this.alarmList.setSelectionMode(0);
      this.alarmList.setFocusable(false);
      this.alarmList.setNextFocusableComponent(this.hourSpinner);
      this.alarmList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            alarmClock.this.alarmListValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.alarmList);
      this.jPanel4.add(this.jScrollPane1, "Center");
      this.jLabel4
         .setText(
            "<html>Ein Wecker gibt eine kurze Meldung, wenn die Zeit erreicht ist, z.B. um Abfahrten freizugeben.<br>Wecker sind sofort aktiv und können jederzeit geändert werden.</html>"
         );
      this.jPanel4.add(this.jLabel4, "North");
      this.getContentPane().add(this.jPanel4, "Center");
      this.pack();
   }

   private void alarmChanged(ChangeEvent evt) {
      this.alarmChanged();
   }

   private void alarmListValueChanged(ListSelectionEvent evt) {
      if (!evt.getValueIsAdjusting()) {
         this.updateAlarm();
      }
   }

   private void addButtonActionPerformed(ActionEvent evt) {
      alarmItem ai = new alarmItem(this);
      this.alarms.addElement(ai);
      this.alarmList.setSelectedIndex(this.alarms.getSize() - 1);
      this.alarmList.ensureIndexIsVisible(this.alarms.getSize() - 1);
      this.focusHour();
   }

   private void delButtonActionPerformed(ActionEvent evt) {
      alarmItem ai = (alarmItem)this.alarmList.getSelectedValue();
      if (ai != null) {
         this.removeAlarm(ai);
      }
   }

   private void alarmTextFocusGained(FocusEvent evt) {
      this.alarmText.selectAll();
   }
}
