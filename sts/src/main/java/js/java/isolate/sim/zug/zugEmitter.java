package js.java.isolate.sim.zug;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import js.java.isolate.sim.flagdata;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.events.bahnuebergangwaerter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.isolate.sim.toolkit.ComboEventRenderer;
import js.java.isolate.sim.toolkit.ComboZugRenderer;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.balloontip.BalloonTip;
import js.java.tools.gui.CurvesPanel;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class zugEmitter extends JDialog implements SessionClose {
   private int nameCounter = 0;
   private final stellwerksim_main my_main;
   private final gleisbildModelEventsys my_gleisbild;
   private final int[] ein_array;
   private final int[] aus_array;
   private final Color fcolor;
   private final Color warncolor = Color.RED;
   private BalloonTip balloonTip = null;
   private JSpinner aufenthaltSp;
   private JComboBox ausfahrtCombo;
   private JComboBox bahnsteigCombo;
   private JComboBox einfahrtCombo;
   private JButton emitButton;
   private JPanel ereignisPanel;
   private JButton eventButton;
   private JComboBox eventCB;
   private JTextField flagsField;
   private JLabel jLabel1;
   private JLabel jLabel10;
   private JLabel jLabel11;
   private JLabel jLabel13;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JLabel jLabel8;
   private JLabel jLabel9;
   private JSeparator jSeparator1;
   private JSlider längeSlider;
   private JCheckBox measureCB;
   private JPanel optsPanel;
   private JSlider tempoSlider;
   private JButton wärterButton;
   private JTextField zidLabel;
   private JComboBox zugM_CB;
   private JPanel zugPanel;

   public zugEmitter(Frame parent, stellwerksim_main m, gleisbildModelEventsys b, boolean modal) {
      super(parent, modal);
      this.my_main = m;
      this.my_gleisbild = b;
      this.initComponents();
      TreeMap<String, Integer> austm = this.my_gleisbild.getAlleOfType(gleis.ELEMENT_AUSFAHRT);
      TreeMap<String, Integer> eintm = this.my_gleisbild.getAlleOfType(gleis.ELEMENT_EINFAHRT);
      TreeMap<String, Integer> bsttm = this.my_gleisbild.getAlleOfType(gleis.ELEMENT_BAHNSTEIG);
      this.zidLabel.setText(this.nameCounter + 1 + "");

      for (String l : bsttm.keySet()) {
         this.bahnsteigCombo.addItem(l);
      }

      int i = 0;
      this.aus_array = new int[austm.size() + 1];
      this.ausfahrtCombo.addItem("E/K Flag");
      this.aus_array[i] = 0;
      i++;

      for (String l : austm.keySet()) {
         this.ausfahrtCombo.addItem(l);
         this.aus_array[i] = (Integer)austm.get(l);
         i++;
      }

      i = 0;
      this.ein_array = new int[eintm.size() + 1];
      this.einfahrtCombo.addItem("E/K/F Flag");
      this.ein_array[i] = 0;
      i++;

      for (String l : eintm.keySet()) {
         this.einfahrtCombo.addItem(l);
         this.ein_array[i] = (Integer)eintm.get(l);
         i++;
      }

      this.eventCB.removeAllItems();
      this.eventCB.addItem(null);

      for (eventContainer e : this.my_gleisbild.events) {
         this.eventCB.addItem(e);
      }

      this.eventButton.setEnabled(this.my_gleisbild.events.size() > 0);
      this.fcolor = this.flagsField.getForeground();
      this.flagsField.addKeyListener(new KeyListener() {
         public void keyTyped(KeyEvent e) {
         }

         public void keyPressed(KeyEvent e) {
            zugEmitter.this.hideBalloon();
         }

         public void keyReleased(KeyEvent e) {
            zugEmitter.this.action_flagelement();
         }
      });
      this.setSize(550, 550);
      this.measureCB.setEnabled(true);
      this.measureCBActionPerformed(null);
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
   }

   private void hideBalloon() {
      if (this.balloonTip != null) {
         this.balloonTip.setVisible(false);
      }
   }

   private void showBalloon(String txt) {
      if (this.balloonTip == null) {
         this.balloonTip = new BalloonTip(this.flagsField);
      }

      this.balloonTip.setText(txt);
      this.balloonTip.setVisible(true);
   }

   private void action_flagelement() {
      String f = this.flagsField.getText();

      try {
         new flagdata(f);
         this.flagsField.setForeground(this.fcolor);
      } catch (Exception var3) {
         this.flagsField.setForeground(this.warncolor);
         this.showBalloon("Fehler im Syntax: " + var3.getMessage());
      }
   }

   @Override
   public void close() {
      this.formWindowClosed(null);
   }

   private void initComponents() {
      this.zugPanel = new CurvesPanel();
      this.jLabel9 = new JLabel();
      this.jLabel1 = new JLabel();
      this.einfahrtCombo = new JComboBox();
      this.jLabel5 = new JLabel();
      this.ausfahrtCombo = new JComboBox();
      this.jLabel2 = new JLabel();
      this.bahnsteigCombo = new JComboBox();
      this.jLabel8 = new JLabel();
      this.flagsField = new JTextField();
      this.jLabel10 = new JLabel();
      this.aufenthaltSp = new JSpinner();
      this.jLabel11 = new JLabel();
      this.jLabel4 = new JLabel();
      this.tempoSlider = new JSlider();
      this.jLabel3 = new JLabel();
      this.längeSlider = new JSlider();
      this.jLabel7 = new JLabel();
      this.zidLabel = new JTextField();
      this.emitButton = new JButton();
      this.optsPanel = new JPanel();
      this.measureCB = new JCheckBox();
      this.zugM_CB = new JComboBox();
      this.jLabel13 = new JLabel();
      this.jSeparator1 = new JSeparator();
      this.wärterButton = new JButton();
      this.ereignisPanel = new JPanel();
      this.jLabel6 = new JLabel();
      this.eventCB = new JComboBox();
      this.eventButton = new JButton();
      this.setDefaultCloseOperation(0);
      this.setTitle("Zugemitter");
      this.setFont(new Font("Dialog", 0, 12));
      this.setLocationByPlatform(true);
      this.setType(Type.UTILITY);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            zugEmitter.this.formWindowClosed(evt);
         }

         public void windowClosing(WindowEvent evt) {
            zugEmitter.this.formWindowClosing(evt);
         }
      });
      this.getContentPane().setLayout(new GridBagLayout());
      this.zugPanel.setBorder(BorderFactory.createTitledBorder("Zug erzeugen"));
      this.zugPanel.setLayout(new GridBagLayout());
      this.jLabel9
         .setText(
            "<html>Erzeugt einen Testzug der ab der angegebenen Einfahrt mit der gegeben Zuglänge und -geschwindigkeit. Alle anderen Werte werden in den Fahrplan übernommen.</html>"
         );
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridwidth = 6;
      gridBagConstraints.fill = 1;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.jLabel9, gridBagConstraints);
      this.jLabel1.setText("Einfahrt");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.jLabel1, gridBagConstraints);
      this.einfahrtCombo.setToolTipText("Einfahrt des Zuges");
      this.einfahrtCombo.setFocusable(false);
      this.einfahrtCombo.setMinimumSize(new Dimension(160, 21));
      this.einfahrtCombo.setPreferredSize(new Dimension(160, 21));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.einfahrtCombo, gridBagConstraints);
      this.jLabel5.setText("Ausfahrt");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.jLabel5, gridBagConstraints);
      this.ausfahrtCombo.setFocusable(false);
      this.ausfahrtCombo.setMinimumSize(new Dimension(160, 21));
      this.ausfahrtCombo.setPreferredSize(new Dimension(160, 21));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.ausfahrtCombo, gridBagConstraints);
      this.jLabel2.setText("Bahnsteig");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.jLabel2, gridBagConstraints);
      this.bahnsteigCombo.setToolTipText("Bahnsteig - für Fahrplan nötig");
      this.bahnsteigCombo.setFocusable(false);
      this.bahnsteigCombo.setMinimumSize(new Dimension(160, 21));
      this.bahnsteigCombo.setPreferredSize(new Dimension(160, 21));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.bahnsteigCombo, gridBagConstraints);
      this.jLabel8.setText("Flags");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.jLabel8, gridBagConstraints);
      this.flagsField.setToolTipText("<html>Zugflags in Flag(Flagdata) Schreibweise</html>");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.flagsField, gridBagConstraints);
      this.jLabel10.setText("Aufenthaltsdauer");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.jLabel10, gridBagConstraints);
      this.aufenthaltSp.setModel(new SpinnerNumberModel(1, 0, 60, 1));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.aufenthaltSp, gridBagConstraints);
      this.jLabel11.setText("Minuten");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 0, 5, 0);
      this.zugPanel.add(this.jLabel11, gridBagConstraints);
      this.jLabel4.setText("Tempo");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.jLabel4, gridBagConstraints);
      this.tempoSlider.setMajorTickSpacing(1);
      this.tempoSlider.setMaximum(15);
      this.tempoSlider.setMinimum(1);
      this.tempoSlider.setMinorTickSpacing(1);
      this.tempoSlider.setPaintLabels(true);
      this.tempoSlider.setPaintTicks(true);
      this.tempoSlider.setSnapToTicks(true);
      this.tempoSlider.setToolTipText("Zuggeschwindigkeit ist ein abstrakter Wert und ist in der Zugeditor-Dokumentation genauer beschrieben.");
      this.tempoSlider.setValue(5);
      this.tempoSlider.setFocusable(false);
      this.tempoSlider.setMinimumSize(new Dimension(200, 54));
      this.tempoSlider.setOpaque(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.tempoSlider, gridBagConstraints);
      this.jLabel3.setText("Länge");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.jLabel3, gridBagConstraints);
      this.längeSlider.setMajorTickSpacing(1);
      this.längeSlider.setMaximum(15);
      this.längeSlider.setMinimum(3);
      this.längeSlider.setMinorTickSpacing(1);
      this.längeSlider.setPaintLabels(true);
      this.längeSlider.setPaintTicks(true);
      this.längeSlider.setSnapToTicks(true);
      this.längeSlider.setToolTipText("Zuglänge ist ein abstrakter Wert und ist in der Zugeditor-Dokumentation genauer beschrieben.");
      this.längeSlider.setValue(5);
      this.längeSlider.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.längeSlider, gridBagConstraints);
      this.jLabel7.setText("nächste ZID:");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.jLabel7, gridBagConstraints);
      this.zidLabel.setEditable(false);
      this.zidLabel.setBorder(null);
      this.zidLabel.setOpaque(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.zidLabel, gridBagConstraints);
      this.emitButton.setFont(new Font("Tahoma", 1, 11));
      this.emitButton.setText("Zug erzeugen");
      this.emitButton.setToolTipText("erzeugt jetzt einen Zug an der Einfahrt");
      this.emitButton.setFocusable(false);
      this.emitButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            zugEmitter.this.emitButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      this.zugPanel.add(this.emitButton, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      this.getContentPane().add(this.zugPanel, gridBagConstraints);
      this.optsPanel.setBorder(BorderFactory.createTitledBorder("allgemeine Optionen"));
      this.optsPanel.setLayout(new GridBagLayout());
      this.measureCB.setText("Zeitmessung für alle neuen Züge");
      this.measureCB.setFocusPainted(false);
      this.measureCB.setFocusable(false);
      this.measureCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            zugEmitter.this.measureCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 0, 3, 0);
      this.optsPanel.add(this.measureCB, gridBagConstraints);
      this.zugM_CB.setFocusable(false);
      this.zugM_CB.setRenderer(new ComboZugRenderer());
      this.zugM_CB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            zugEmitter.this.zugM_CBItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 30, 0, 0);
      this.optsPanel.add(this.zugM_CB, gridBagConstraints);
      this.jLabel13.setText("darstellen");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 5;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 5, 0, 0);
      this.optsPanel.add(this.jLabel13, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 9;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(5, 0, 5, 0);
      this.optsPanel.add(this.jSeparator1, gridBagConstraints);
      this.wärterButton.setText("Bü Wärter einschalten");
      this.wärterButton.setFocusPainted(false);
      this.wärterButton.setFocusable(false);
      this.wärterButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            zugEmitter.this.wärterButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 10;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = 17;
      this.optsPanel.add(this.wärterButton, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.getContentPane().add(this.optsPanel, gridBagConstraints);
      this.ereignisPanel.setBorder(BorderFactory.createTitledBorder("Störungen"));
      this.ereignisPanel.setLayout(new GridBagLayout());
      this.jLabel6.setText("Störung");
      this.ereignisPanel.add(this.jLabel6, new GridBagConstraints());
      this.eventCB.setFocusable(false);
      this.eventCB.setRenderer(new ComboEventRenderer());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 10, 0, 10);
      this.ereignisPanel.add(this.eventCB, gridBagConstraints);
      this.eventButton.setText("starten");
      this.eventButton.setFocusable(false);
      this.eventButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            zugEmitter.this.eventButtonActionPerformed(evt);
         }
      });
      this.ereignisPanel.add(this.eventButton, new GridBagConstraints());
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.getContentPane().add(this.ereignisPanel, gridBagConstraints);
      this.pack();
   }

   private void emitButtonActionPerformed(ActionEvent evt) {
      this.nameCounter++;
      int gzid = this.nameCounter;
      int lg = this.längeSlider.getValue();
      int tm = this.tempoSlider.getValue();
      String zgl = (String)this.bahnsteigCombo.getSelectedItem();
      flagdata flag = null;

      try {
         flag = new flagdata(this.flagsField.getText());
      } catch (Exception var13) {
         flag = new flagdata("");
      }

      int ein = this.ein_array[this.einfahrtCombo.getSelectedIndex()];
      int aus = this.aus_array[this.ausfahrtCombo.getSelectedIndex()];
      int auf = ((SpinnerNumberModel)this.aufenthaltSp.getModel()).getNumber().intValue();
      zug.emitData ed = new zug.emitData();
      ed.länge = lg;
      ed.soll_tempo = tm;
      ed.ein_enr = ein;
      ed.aus_enr = aus;
      ed.zielgleis = zgl;
      ed.flags = flag;
      ed.aufenthalt = auf;
      zug z = this.my_main.emittZug(gzid, ed);
      System.out.println("emitted: " + gzid);
      this.zidLabel.setText(this.nameCounter + 1 + "");
      this.flagsField.setText("");
      if (this.measureCB.isSelected()) {
         zugMeasure zm = new zugMeasure(z, this.my_main, this.my_gleisbild);
         this.zugM_CB.addItem(zm);
         zm.setVisible(true);
      }
   }

   private void formWindowClosing(WindowEvent evt) {
      int j = JOptionPane.showConfirmDialog(this, "Echt jetzt, so willst du den Emitter beenden?", "Ende, oder was?", 0, 3);
      if (j == 0) {
         for (int i = 0; i < this.zugM_CB.getItemCount(); i++) {
            zugMeasure zm = (zugMeasure)this.zugM_CB.getItemAt(i);
            zm.close();
         }

         this.zugM_CB.removeAllItems();
         this.my_main.exit();
      }
   }

   private void eventButtonActionPerformed(ActionEvent evt) {
      eventContainer e = (eventContainer)this.eventCB.getSelectedItem();
      if (e != null) {
         event.createEvent(e, this.my_gleisbild, this.my_main);
         this.eventCB.setSelectedIndex(0);
      }
   }

   private void zugM_CBItemStateChanged(ItemEvent evt) {
      zugMeasure zm = (zugMeasure)this.zugM_CB.getSelectedItem();
      if (zm != null) {
         zm.setVisible(true);
         this.zugM_CB.setSelectedItem(null);
      }
   }

   private void measureCBActionPerformed(ActionEvent evt) {
      this.eventButton.setEnabled(!this.measureCB.isSelected());
      this.eventCB.setEnabled(!this.measureCB.isSelected());
   }

   private void wärterButtonActionPerformed(ActionEvent evt) {
      this.wärterButton.setEnabled(false);
      eventContainer ev = new eventContainer(this.my_gleisbild, bahnuebergangwaerter.class);
      event.createEvent(ev, this.my_gleisbild, this.my_main);
   }

   private void formWindowClosed(WindowEvent evt) {
      for (int i = 0; i < this.zugM_CB.getItemCount(); i++) {
         zugMeasure zm = (zugMeasure)this.zugM_CB.getItemAt(i);
         zm.close();
      }

      this.zugM_CB.removeAllItems();
   }

   public static zugEmitter show(JFrame parent, stellwerksim_main m, gleisbildModelEventsys b) {
      zugEmitter ze = new zugEmitter(parent, m, b, false);
      ze.show();
      return ze;
   }
}
