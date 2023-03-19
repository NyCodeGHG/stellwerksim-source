package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;

public class propertyPanel extends basePanel {
   private JLabel abovebelow_L;
   private JRadioButton abovebelow_RB;
   private JLabel below4_L;
   private JRadioButton below4_RB;
   private JLabel classic_L;
   private JRadioButton classic_RB;
   private JCheckBox cleverSignalCB;
   private JScrollPane extrasPane;
   private JPanel extrasPanel;
   private JCheckBox hauptZwergSignalCB;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JPanel jPanel1;
   private JLabel left4_L;
   private JRadioButton left4_RB;
   private JLabel left_L;
   private JRadioButton left_RB1;
   private JLabel left_abovebelow_L;
   private JRadioButton left_abovebelow_RB;
   private JLabel left_below4_L;
   private JRadioButton left_below4_RB;
   private JPanel mainPanel;
   private JScrollPane signalStylePane;
   private JPanel signalStylePanel;
   private ButtonGroup signalstyle_bg;

   public propertyPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      if (this.my_main.getParameter("enable_prop_st") == null || this.my_main.getParameter("enable_prop_st").compareTo("true") != 0) {
         this.mainPanel.remove(this.signalStylePane);
      }

      if (this.my_main.getParameter("enable_prop_cs") == null || this.my_main.getParameter("enable_prop_cs").compareTo("true") != 0) {
         this.extrasPanel.remove(this.cleverSignalCB);
      }

      if (this.my_main.getParameter("enable_prop_l4") == null || this.my_main.getParameter("enable_prop_l4").compareTo("true") != 0) {
         this.signalStylePanel.remove(this.left_RB1);
         this.signalStylePanel.remove(this.left_L);
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      switch(this.glbControl.getModel().gleisbildextend.getSignalversion()) {
         case 0:
         default:
            this.classic_RB.setSelected(true);
            break;
         case 1:
            this.left4_RB.setSelected(true);
            break;
         case 2:
            this.abovebelow_RB.setSelected(true);
            break;
         case 3:
            this.below4_RB.setSelected(true);
            break;
         case 4:
            this.left_RB1.setSelected(true);
            break;
         case 5:
            this.left_abovebelow_RB.setSelected(true);
            break;
         case 6:
            this.left_below4_RB.setSelected(true);
      }

      this.cleverSignalCB.setSelected(this.glbControl.getModel().gleisbildextend.isCleverSignal());
      this.hauptZwergSignalCB.setSelected(this.glbControl.getModel().gleisbildextend.isHauptZwergSignal());
   }

   private void initComponents() {
      this.signalstyle_bg = new ButtonGroup();
      this.mainPanel = new JPanel();
      this.signalStylePane = new JScrollPane();
      this.signalStylePanel = new JPanel();
      this.jLabel3 = new JLabel();
      this.classic_RB = new JRadioButton();
      this.classic_L = new JLabel();
      this.left4_RB = new JRadioButton();
      this.left4_L = new JLabel();
      this.abovebelow_RB = new JRadioButton();
      this.abovebelow_L = new JLabel();
      this.below4_RB = new JRadioButton();
      this.below4_L = new JLabel();
      this.jLabel2 = new JLabel();
      this.left_RB1 = new JRadioButton();
      this.left_L = new JLabel();
      this.left_abovebelow_RB = new JRadioButton();
      this.left_abovebelow_L = new JLabel();
      this.left_below4_RB = new JRadioButton();
      this.left_below4_L = new JLabel();
      this.jPanel1 = new JPanel();
      this.extrasPane = new JScrollPane();
      this.extrasPanel = new JPanel();
      this.cleverSignalCB = new JCheckBox();
      this.hauptZwergSignalCB = new JCheckBox();
      this.setLayout(new BorderLayout());
      this.mainPanel.setLayout(new GridLayout(1, 0));
      this.signalStylePane.setBorder(BorderFactory.createTitledBorder("Signaltyp"));
      this.signalStylePanel.setLayout(new GridBagLayout());
      this.jLabel3.setFont(this.jLabel3.getFont().deriveFont(this.jLabel3.getFont().getStyle() | 1, (float)(this.jLabel3.getFont().getSize() - 2)));
      this.jLabel3.setHorizontalAlignment(2);
      this.jLabel3.setText("Rechtsseitig");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.weightx = 1.0;
      this.signalStylePanel.add(this.jLabel3, gridBagConstraints);
      this.signalstyle_bg.add(this.classic_RB);
      this.classic_RB.setSelected(true);
      this.classic_RB.setText("klassische Form");
      this.classic_RB.setToolTipText("kein Fahrstraßenspeicher");
      this.classic_RB.setActionCommand("0");
      this.classic_RB.setFocusable(false);
      this.classic_RB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            propertyPanel.this.stActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      this.signalStylePanel.add(this.classic_RB, gridBagConstraints);
      this.classic_L.setFont(this.classic_L.getFont().deriveFont((float)this.classic_L.getFont().getSize() - 3.0F));
      this.classic_L.setText("[K]");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.anchor = 13;
      this.signalStylePanel.add(this.classic_L, gridBagConstraints);
      this.signalstyle_bg.add(this.left4_RB);
      this.left4_RB.setText("4 Lichter links");
      this.left4_RB.setToolTipText("erlaubt Fahrstraßenspeicher");
      this.left4_RB.setActionCommand("1");
      this.left4_RB.setFocusable(false);
      this.left4_RB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            propertyPanel.this.stActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      this.signalStylePanel.add(this.left4_RB, gridBagConstraints);
      this.left4_L.setFont(this.left4_L.getFont().deriveFont((float)this.left4_L.getFont().getSize() - 3.0F));
      this.left4_L.setText("[L4]");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.anchor = 13;
      this.signalStylePanel.add(this.left4_L, gridBagConstraints);
      this.signalstyle_bg.add(this.abovebelow_RB);
      this.abovebelow_RB.setText("je 2 Lichter oben+unten");
      this.abovebelow_RB.setToolTipText("erlaubt Fahrstraßenspeicher");
      this.abovebelow_RB.setActionCommand("2");
      this.abovebelow_RB.setFocusable(false);
      this.abovebelow_RB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            propertyPanel.this.stActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      this.signalStylePanel.add(this.abovebelow_RB, gridBagConstraints);
      this.abovebelow_L.setFont(this.abovebelow_L.getFont().deriveFont((float)this.abovebelow_L.getFont().getSize() - 3.0F));
      this.abovebelow_L.setText("[O2U2]");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.anchor = 13;
      this.signalStylePanel.add(this.abovebelow_L, gridBagConstraints);
      this.signalstyle_bg.add(this.below4_RB);
      this.below4_RB.setText("4 Lichter unten");
      this.below4_RB.setToolTipText("erlaubt Fahrstraßenspeicher");
      this.below4_RB.setActionCommand("3");
      this.below4_RB.setFocusable(false);
      this.below4_RB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            propertyPanel.this.stActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      this.signalStylePanel.add(this.below4_RB, gridBagConstraints);
      this.below4_L.setFont(this.below4_L.getFont().deriveFont((float)this.below4_L.getFont().getSize() - 3.0F));
      this.below4_L.setText("[U4]");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.anchor = 13;
      this.signalStylePanel.add(this.below4_L, gridBagConstraints);
      this.jLabel2.setFont(this.jLabel2.getFont().deriveFont(this.jLabel2.getFont().getStyle() | 1, (float)(this.jLabel2.getFont().getSize() - 2)));
      this.jLabel2.setHorizontalAlignment(2);
      this.jLabel2.setText("Linksseitig");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 3;
      gridBagConstraints.weightx = 1.0;
      this.signalStylePanel.add(this.jLabel2, gridBagConstraints);
      this.signalstyle_bg.add(this.left_RB1);
      this.left_RB1.setText("4 Lichter rechts");
      this.left_RB1.setToolTipText("erlaubt Fahrstraßenspeicher");
      this.left_RB1.setActionCommand("4");
      this.left_RB1.setFocusable(false);
      this.left_RB1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            propertyPanel.this.stActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      this.signalStylePanel.add(this.left_RB1, gridBagConstraints);
      this.left_L.setFont(this.left_L.getFont().deriveFont((float)this.left_L.getFont().getSize() - 3.0F));
      this.left_L.setText("[R4]");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.anchor = 13;
      this.signalStylePanel.add(this.left_L, gridBagConstraints);
      this.signalstyle_bg.add(this.left_abovebelow_RB);
      this.left_abovebelow_RB.setText("je 2 Lichter oben+unten");
      this.left_abovebelow_RB.setToolTipText("erlaubt Fahrstraßenspeicher");
      this.left_abovebelow_RB.setActionCommand("5");
      this.left_abovebelow_RB.setFocusable(false);
      this.left_abovebelow_RB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            propertyPanel.this.stActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      this.signalStylePanel.add(this.left_abovebelow_RB, gridBagConstraints);
      this.left_abovebelow_L.setFont(this.left_abovebelow_L.getFont().deriveFont((float)this.left_abovebelow_L.getFont().getSize() - 3.0F));
      this.left_abovebelow_L.setText("[RO2U2]");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.anchor = 13;
      this.signalStylePanel.add(this.left_abovebelow_L, gridBagConstraints);
      this.signalstyle_bg.add(this.left_below4_RB);
      this.left_below4_RB.setText("4 Lichter unten");
      this.left_below4_RB.setToolTipText("erlaubt Fahrstraßenspeicher");
      this.left_below4_RB.setActionCommand("6");
      this.left_below4_RB.setFocusable(false);
      this.left_below4_RB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            propertyPanel.this.stActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.weightx = 1.0;
      this.signalStylePanel.add(this.left_below4_RB, gridBagConstraints);
      this.left_below4_L.setFont(this.left_below4_L.getFont().deriveFont((float)this.left_below4_L.getFont().getSize() - 3.0F));
      this.left_below4_L.setText("[RU4]");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.anchor = 13;
      this.signalStylePanel.add(this.left_below4_L, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.signalStylePanel.add(this.jPanel1, gridBagConstraints);
      this.signalStylePane.setViewportView(this.signalStylePanel);
      this.mainPanel.add(this.signalStylePane);
      this.extrasPane.setBorder(BorderFactory.createTitledBorder("Darstellung"));
      this.extrasPanel.setLayout(new BoxLayout(this.extrasPanel, 3));
      this.cleverSignalCB.setText("Signalkopfgleiserkennung");
      this.cleverSignalCB.setFocusable(false);
      this.cleverSignalCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            propertyPanel.this.cleverSignalCBActionPerformed(evt);
         }
      });
      this.extrasPanel.add(this.cleverSignalCB);
      this.hauptZwergSignalCB.setText("Hauptsignal zu Zwerg bei nur Ra");
      this.hauptZwergSignalCB.setFocusable(false);
      this.hauptZwergSignalCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            propertyPanel.this.hauptZwergSignalCBActionPerformed(evt);
         }
      });
      this.extrasPanel.add(this.hauptZwergSignalCB);
      this.extrasPane.setViewportView(this.extrasPanel);
      this.mainPanel.add(this.extrasPane);
      this.add(this.mainPanel, "Center");
   }

   private void stActionPerformed(ActionEvent evt) {
      String cmd = ((JRadioButton)evt.getSource()).getActionCommand();
      this.glbControl.getModel().gleisbildextend.setSignalversion(Integer.parseInt(cmd));
      this.glbControl.getModel().changeC(3);
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            propertyPanel.this.glbControl.repaint();
         }
      });
   }

   private void cleverSignalCBActionPerformed(ActionEvent evt) {
      this.glbControl.getModel().gleisbildextend.setCleverSignal(this.cleverSignalCB.isSelected());
      this.glbControl.getModel().changeC(1);
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            propertyPanel.this.glbControl.repaint();
         }
      });
   }

   private void hauptZwergSignalCBActionPerformed(ActionEvent evt) {
      this.glbControl.getModel().gleisbildextend.setHauptZwergSignal(this.hauptZwergSignalCB.isSelected());
      this.glbControl.getModel().changeC(1);
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            propertyPanel.this.glbControl.repaint();
         }
      });
   }
}
