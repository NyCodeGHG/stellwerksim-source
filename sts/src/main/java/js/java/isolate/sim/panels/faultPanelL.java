package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.structServ.structListPanel;
import js.java.isolate.sim.structServ.structinfo;
import js.java.isolate.sim.structServ.structinfoTablePanel;
import js.java.tools.actions.AbstractEvent;

public class faultPanelL extends basePanel implements structListPanel.selectionListener {
   private final structListPanel structList;
   private final structinfoTablePanel structTable;
   private JRadioButton belegtR;
   private JPanel dataPanel;
   private JButton dumpButton;
   private JRadioButton freiR;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JLabel jLabel7;
   private JLabel jLabel8;
   private JLabel jLabel9;
   private JPanel jPanel1;
   private JScrollPane jScrollPane1;
   private JSeparator jSeparator2;
   private JCheckBox led1;
   private JCheckBox led2;
   private JCheckBox led3;
   private JCheckBox led4;
   private JTextField leftWtf;
   private JCheckBox lockElement;
   private JCheckBox poweroff_mode;
   private JRadioButton reserviertR;
   private JTextField rightWtf;
   private JCheckBox sim_mode;
   private JButton smoothOff;
   private JButton smoothOn;
   private ButtonGroup statusBG;
   private JComboBox stellungCB;

   public faultPanelL(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.structList = new structListPanel(this);
      this.structTable = new structinfoTablePanel();
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 20;
      gbc.fill = 1;
      gbc.weightx = 1.0;
      gbc.gridwidth = 8;
      this.dataPanel.add(this.structList, gbc);
      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 21;
      gbc.fill = 1;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      gbc.gridwidth = 8;
      this.dataPanel.add(this.structTable, gbc);
      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof GecSelectEvent) {
         gleis gl = this.glbControl.getSelectedGleis();
         if (gl != null) {
            switch (gl.getFluentData().getStatus()) {
               case 0:
                  this.freiR.setSelected(true);
                  break;
               case 1:
                  this.reserviertR.setSelected(true);
                  break;
               case 2:
                  this.belegtR.setSelected(true);
                  break;
               default:
                  this.freiR.setSelected(true);
            }

            this.stellungCB.removeAllItems();

            for (gleisElements.Stellungen s : gl.getFluentData().getPossibleStellung()) {
               this.stellungCB.addItem(s);
            }

            boolean[] r = gl.getLEDs();
            this.led1.setSelected(r[0]);
            this.led2.setSelected(r[1]);
            this.led3.setSelected(r[2]);
            this.led4.setSelected(r[3]);
            this.lockElement.setSelected(gl.getFluentData().isGesperrt());
            this.leftWtf.setText("");
            this.rightWtf.setText("");
            Iterator<gleis> it = gl.getNachbarn();

            while (it.hasNext()) {
               gleis igl = (gleis)it.next();
               if (gleis.ALLE_WEICHEN.matches(igl.getElement())) {
                  int i = igl.getCol() - gl.getCol();
                  boolean sr = igl.weicheSpitz(gl);
                  String t = "";
                  if (sr) {
                     t = "spitz";
                  } else {
                     t = "stumpf";
                  }

                  if (i < 0) {
                     this.leftWtf.setText(t);
                  } else if (i > 0) {
                     this.rightWtf.setText(t);
                  }
               }
            }
         }
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.glbControl.getMode().addChangeListener(this);
   }

   private Vector getStructInfo() {
      Vector ret = new Vector();
      ret.addAll(this.glbControl.getModel().getStructInfo());
      return ret;
   }

   @Override
   public void selected(structinfo si) {
      if (si != null) {
         this.structTable.add(si.getStructure());
      }
   }

   private void add1() {
      this.structList.clear();
      Vector v = this.getStructInfo();
      Enumeration e = v.elements();

      while (e.hasMoreElements()) {
         Vector vv = (Vector)e.nextElement();
         this.structList.add(vv);
      }
   }

   private void initComponents() {
      this.statusBG = new ButtonGroup();
      this.jScrollPane1 = new JScrollPane();
      this.dataPanel = new JPanel();
      this.jLabel1 = new JLabel();
      this.jPanel1 = new JPanel();
      this.freiR = new JRadioButton();
      this.reserviertR = new JRadioButton();
      this.belegtR = new JRadioButton();
      this.jLabel2 = new JLabel();
      this.stellungCB = new JComboBox();
      this.jLabel3 = new JLabel();
      this.led1 = new JCheckBox();
      this.led2 = new JCheckBox();
      this.led3 = new JCheckBox();
      this.led4 = new JCheckBox();
      this.jLabel4 = new JLabel();
      this.sim_mode = new JCheckBox();
      this.jLabel5 = new JLabel();
      this.poweroff_mode = new JCheckBox();
      this.jLabel6 = new JLabel();
      this.lockElement = new JCheckBox();
      this.jLabel9 = new JLabel();
      this.leftWtf = new JTextField();
      this.rightWtf = new JTextField();
      this.jLabel7 = new JLabel();
      this.jLabel8 = new JLabel();
      this.smoothOff = new JButton();
      this.smoothOn = new JButton();
      this.jSeparator2 = new JSeparator();
      this.dumpButton = new JButton();
      this.setBorder(BorderFactory.createTitledBorder("Fehler-Tester"));
      this.setLayout(new BorderLayout());
      this.jScrollPane1.setBorder(null);
      this.jScrollPane1.setHorizontalScrollBarPolicy(31);
      this.dataPanel.setLayout(new GridBagLayout());
      this.jLabel1.setText("Status");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.jLabel1, gridBagConstraints);
      this.jPanel1.setLayout(new FlowLayout(0, 5, 0));
      this.statusBG.add(this.freiR);
      this.freiR.setText("frei");
      this.freiR.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.freiRActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.freiR);
      this.statusBG.add(this.reserviertR);
      this.reserviertR.setText("reserviert");
      this.reserviertR.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.reserviertRActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.reserviertR);
      this.statusBG.add(this.belegtR);
      this.belegtR.setText("belegt");
      this.belegtR.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.belegtRActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.belegtR);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 7;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.dataPanel.add(this.jPanel1, gridBagConstraints);
      this.jLabel2.setText("Stellung");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.jLabel2, gridBagConstraints);
      this.stellungCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            faultPanelL.this.stellungCBItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 7;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.dataPanel.add(this.stellungCB, gridBagConstraints);
      this.jLabel3.setText("Led");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.jLabel3, gridBagConstraints);
      this.led1.setText("L1");
      this.led1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.ledActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.led1, gridBagConstraints);
      this.led2.setText("L2");
      this.led2.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.ledActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.led2, gridBagConstraints);
      this.led3.setText("L3");
      this.led3.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.ledActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.led3, gridBagConstraints);
      this.led4.setText("L4");
      this.led4.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.ledActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.led4, gridBagConstraints);
      this.jLabel4.setText("Ansicht");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.jLabel4, gridBagConstraints);
      this.sim_mode.setText("Simulatoransicht");
      this.sim_mode.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.sim_modeActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 4;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.sim_mode, gridBagConstraints);
      this.jLabel5.setText("Power");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.jLabel5, gridBagConstraints);
      this.poweroff_mode.setText("off");
      this.poweroff_mode.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.poweroff_modeActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 4;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.poweroff_mode, gridBagConstraints);
      this.jLabel6.setText("Element");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.jLabel6, gridBagConstraints);
      this.lockElement.setText("sperren");
      this.lockElement.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.lockElementActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 4;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.lockElement, gridBagConstraints);
      this.jLabel9.setHorizontalAlignment(2);
      this.jLabel9.setText("NWeiche");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      this.dataPanel.add(this.jLabel9, gridBagConstraints);
      this.leftWtf.setColumns(5);
      this.leftWtf.setEditable(false);
      this.leftWtf.setBorder(BorderFactory.createBevelBorder(1));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 6;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.anchor = 13;
      gridBagConstraints.weightx = 1.0;
      this.dataPanel.add(this.leftWtf, gridBagConstraints);
      this.rightWtf.setColumns(5);
      this.rightWtf.setEditable(false);
      this.rightWtf.setBorder(BorderFactory.createBevelBorder(1));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 7;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.anchor = 13;
      this.dataPanel.add(this.rightWtf, gridBagConstraints);
      this.jLabel7.setText("Smoothing");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.jLabel7, gridBagConstraints);
      this.jLabel8.setText("erzwinge");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 10;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 6, 0, 0);
      this.dataPanel.add(this.jLabel8, gridBagConstraints);
      this.smoothOff.setText("aus");
      this.smoothOff.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.smoothOffActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 10;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = 13;
      this.dataPanel.add(this.smoothOff, gridBagConstraints);
      this.smoothOn.setText("ein");
      this.smoothOn.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.smoothOnActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 10;
      gridBagConstraints.anchor = 17;
      this.dataPanel.add(this.smoothOn, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 12;
      gridBagConstraints.gridwidth = 8;
      gridBagConstraints.fill = 2;
      this.dataPanel.add(this.jSeparator2, gridBagConstraints);
      this.dumpButton.setText("dump");
      this.dumpButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            faultPanelL.this.dumpButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 2;
      this.dataPanel.add(this.dumpButton, gridBagConstraints);
      this.jScrollPane1.setViewportView(this.dataPanel);
      this.add(this.jScrollPane1, "Center");
   }

   private void stellungCBItemStateChanged(ItemEvent evt) {
      gleisElements.Stellungen s = (gleisElements.Stellungen)this.stellungCB.getSelectedItem();
      gleis gl = this.glbControl.getSelectedGleis();
      if (s != null && gl != null) {
         gl.getFluentData().setStellung(s);
         this.glbControl.repaint();
      }
   }

   private void freiRActionPerformed(ActionEvent evt) {
      gleis gl = this.glbControl.getSelectedGleis();
      if (gl != null) {
         gl.getFluentData().setStatus(0);
         this.glbControl.repaint();
      }
   }

   private void reserviertRActionPerformed(ActionEvent evt) {
      gleis gl = this.glbControl.getSelectedGleis();
      if (gl != null) {
         gl.getFluentData().setStatus(1);
         this.glbControl.repaint();
      }
   }

   private void belegtRActionPerformed(ActionEvent evt) {
      gleis gl = this.glbControl.getSelectedGleis();
      if (gl != null) {
         gl.getFluentData().setStatus(2);
         this.glbControl.repaint();
      }
   }

   private void ledActionPerformed(ActionEvent evt) {
      gleis gl = this.glbControl.getSelectedGleis();
      if (gl != null) {
         boolean[] r = new boolean[]{this.led1.isSelected(), this.led2.isSelected(), this.led3.isSelected(), this.led4.isSelected()};
         gl.setLEDs(r);
         this.glbControl.repaint();
      }
   }

   private void sim_modeActionPerformed(ActionEvent evt) {
      this.glbControl.setSimViewStyle(this.sim_mode.isSelected());
      this.glbControl.repaint();
   }

   private void poweroff_modeActionPerformed(ActionEvent evt) {
      for (gleis gl : this.glbControl.getModel()) {
         gl.getFluentData().setPowerOff(this.poweroff_mode.isSelected());
      }

      this.glbControl.repaint();
   }

   private void lockElementActionPerformed(ActionEvent evt) {
      gleis gl = this.glbControl.getSelectedGleis();
      if (gl != null) {
         gl.getFluentData().setGesperrt(this.lockElement.isSelected());
         this.glbControl.repaint();
      }
   }

   private void smoothOffActionPerformed(ActionEvent evt) {
      this.glbControl.getPanel().setSmoothOff();
      this.glbControl.repaint();
   }

   private void smoothOnActionPerformed(ActionEvent evt) {
      this.glbControl.getPanel().setSmoothOn();
      this.glbControl.repaint();
   }

   private void dumpButtonActionPerformed(ActionEvent evt) {
      this.add1();
   }
}
