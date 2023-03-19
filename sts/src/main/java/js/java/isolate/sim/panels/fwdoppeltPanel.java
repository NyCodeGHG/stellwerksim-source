package js.java.isolate.sim.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.fw_doppelt_interface;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.toolkit.fahrstrasseListRenderer;
import js.java.isolate.sim.toolkit.twoWayIcon;
import js.java.isolate.sim.toolkit.twoWayKnownIcon;
import js.java.isolate.sim.toolkit.twoWayQuestionIcon;
import js.java.schaltungen.moduleapi.SessionClose;

public class fwdoppeltPanel extends basePanel implements fw_doppelt_interface, ActionListener, SessionClose {
   private boolean static_testMode = false;
   private LinkedList<fahrstrasse> fahrstrassen = null;
   private DefaultListModel fw_imgr;
   private int returnStatus = -1;
   private LinkedList<fahrstrasse> returnDeletes = null;
   private ArrayList<fahrstrasse> oldfahrwege = null;
   private final twoWayIcon wayUnknown = new twoWayQuestionIcon();
   private final twoWayIcon wayKnown = new twoWayKnownIcon();
   private final Timer blinkTimer = new Timer(800, this);
   private fahrstrasse lastfs = null;
   private JCheckBox autoAcceptCB;
   private JButton debugButton;
   private JButton fw_double_buttonNone;
   private JButton fw_double_buttonOk;
   private JButton fw_double_buttonStop;
   private JList fw_double_list;
   private JLabel hintLabel;
   private JLabel jLabel2;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JScrollPane jScrollPane1;
   private JPanel statusPanel;

   public fwdoppeltPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.fw_imgr = new DefaultListModel();
      this.fw_double_list.setModel(this.fw_imgr);
      this.statusPanel.add(new statusPanel(glb, e));
      this.initNewRun();
   }

   public void initNewRun() {
      this.autoAcceptCB.setSelected(false);
   }

   public void clear() {
      this.fahrstrassen = new LinkedList();
      this.fw_double_buttonOk.setEnabled(false);
      this.fw_double_buttonNone.setEnabled(false);
      this.fw_double_buttonStop.setEnabled(true);
      this.autoAcceptCB.setEnabled(false);
   }

   public void oldFahrwege(ArrayList<fahrstrasse> old) {
      this.oldfahrwege = old;
   }

   private fahrstrasse findFW(fahrstrasse f) {
      if (this.oldfahrwege == null) {
         return null;
      } else {
         for(fahrstrasse fs : this.oldfahrwege) {
            if (fs.checkThisClever(f.getStart(), f.getStop())) {
               return fs;
            }
         }

         return null;
      }
   }

   @Override
   public void add(fahrstrasse f) {
      this.fahrstrassen.add(f);
   }

   public void actionPerformed(ActionEvent e) {
      this.wayUnknown.blinkSwitch();
      this.wayKnown.blinkSwitch();
      this.hintLabel.repaint();
   }

   @Override
   public void start() {
      if (SwingUtilities.isEventDispatchThread()) {
         this.startAwt();
      } else {
         Runnable c = new Runnable() {
            public void run() {
               fwdoppeltPanel.this.startAwt();
            }
         };

         try {
            SwingUtilities.invokeAndWait(c);
         } catch (InvocationTargetException | InterruptedException var7) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "caught", var7);
         }
      }

      if (this.static_testMode) {
         this.fw_double_buttonOkActionPerformed(null);
      } else if (this.autoAcceptCB.isSelected() && this.lastfs != null) {
         this.fw_double_buttonOkActionPerformed(null);
      } else {
         synchronized(this.fahrstrassen) {
            try {
               this.fahrstrassen.wait();
            } catch (Exception var5) {
               this.my_main.showStatus(var5.getMessage(), 4);
            }
         }
      }

      if (SwingUtilities.isEventDispatchThread()) {
         this.endAwt();
      } else {
         Runnable c = new Runnable() {
            public void run() {
               fwdoppeltPanel.this.endAwt();
            }
         };

         try {
            SwingUtilities.invokeAndWait(c);
         } catch (InvocationTargetException | InterruptedException var4) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "caught", var4);
         }
      }
   }

   private void endAwt() {
      this.fahrstrassen = null;
      this.fw_double_list.setModel(new DefaultListModel());
      this.fw_imgr.clear();
      this.fw_double_buttonOk.setEnabled(false);
      this.fw_double_buttonNone.setEnabled(false);
      this.fw_double_buttonStop.setEnabled(false);
      this.fw_double_list.setEnabled(false);
      this.autoAcceptCB.setEnabled(false);
   }

   private void startAwt() {
      this.fw_imgr = new DefaultListModel();
      this.lastfs = null;
      int minc = Integer.MAX_VALUE;

      for(fahrstrasse f : this.fahrstrassen) {
         this.fw_imgr.addElement(f);
         fahrstrasse of = this.findFW(f);
         if (of != null) {
            int c = f.compareWay(of);
            if (c < minc) {
               minc = c;
               this.lastfs = f;
            }
         }
      }

      this.fw_double_list.setModel(this.fw_imgr);
      this.fw_double_list.setEnabled(true);
      this.autoAcceptCB.setEnabled(true);
      if (this.lastfs != null) {
         this.hintLabel.setIcon(this.wayKnown);
         this.fw_double_list.setSelectedValue(this.lastfs, true);
         this.fw_double_list.ensureIndexIsVisible(this.fw_double_list.getSelectedIndex());
      } else {
         this.hintLabel.setIcon(this.wayUnknown);
         this.fw_double_list.setSelectedIndex(0);
      }

      this.fw_double_list.requestFocusInWindow();
      this.returnStatus = -1;
      this.blinkTimer.start();
   }

   private void doClose(int retStatus) {
      this.blinkTimer.stop();
      this.returnStatus = retStatus;
      synchronized(this.fahrstrassen) {
         this.fahrstrassen.notify();
      }
   }

   @Override
   public int getReturn() {
      return this.returnStatus;
   }

   @Override
   public LinkedList<fahrstrasse> getDelList() {
      return this.returnDeletes;
   }

   @Override
   public void close() {
      this.blinkTimer.stop();
      this.fahrstrassen.clear();
      this.fw_imgr.clear();
      this.lastfs = null;
      this.oldfahrwege.clear();
      this.returnDeletes.clear();
   }

   private void initComponents() {
      this.jPanel2 = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.fw_double_list = new JList();
      this.autoAcceptCB = new JCheckBox();
      this.jPanel1 = new JPanel();
      this.hintLabel = new JLabel();
      this.debugButton = new JButton();
      this.jLabel2 = new JLabel();
      this.fw_double_buttonOk = new JButton();
      this.fw_double_buttonNone = new JButton();
      this.fw_double_buttonStop = new JButton();
      this.statusPanel = new JPanel();
      this.setBorder(BorderFactory.createTitledBorder("Doublettenanalyse"));
      this.setLayout(new GridLayout(1, 0));
      this.jPanel2.setLayout(new GridBagLayout());
      this.jScrollPane1.setPreferredSize(new Dimension(258, 80));
      this.fw_double_list.setSelectionMode(0);
      this.fw_double_list.setCellRenderer(new fahrstrasseListRenderer());
      this.fw_double_list.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            fwdoppeltPanel.this.fw_double_listValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.fw_double_list);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel2.add(this.jScrollPane1, gridBagConstraints);
      this.autoAcceptCB.setFont(this.autoAcceptCB.getFont().deriveFont((float)this.autoAcceptCB.getFont().getSize() - 1.0F));
      this.autoAcceptCB.setText("alle bereits definierten Alternativwege übernehmen");
      this.autoAcceptCB.setFocusPainted(false);
      this.autoAcceptCB.setFocusable(false);
      this.autoAcceptCB.setMargin(new Insets(0, 2, 0, 2));
      this.autoAcceptCB.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fwdoppeltPanel.this.autoAcceptCBActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = 2;
      this.jPanel2.add(this.autoAcceptCB, gridBagConstraints);
      this.jPanel1.setLayout(new BoxLayout(this.jPanel1, 3));
      this.hintLabel.setFont(new Font("Dialog", 0, 12));
      this.hintLabel.setVerticalAlignment(1);
      this.jPanel1.add(this.hintLabel);
      this.debugButton.setFont(new Font("MS Sans Serif", 0, 5));
      this.debugButton.setText(".");
      this.debugButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.debugButton.setFocusPainted(false);
      this.debugButton.setFocusable(false);
      this.debugButton.setMargin(new Insets(2, 2, 2, 2));
      this.debugButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fwdoppeltPanel.this.debugButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.debugButton);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weighty = 1.0;
      this.jPanel2.add(this.jPanel1, gridBagConstraints);
      this.jLabel2.setFont(this.jLabel2.getFont().deriveFont((float)this.jLabel2.getFont().getSize() - 1.0F));
      this.jLabel2
         .setText(
            "<html>Es wurden verschiedene Fahrwege zwischen 2 gleichen Signalen gefunden. Wählen Sie einen Fahrweg aus, um ihn gezeigt zu bekommen, \nwählen Sie dann \"diesen\" um diesen Fahrweg auszuwählen und damit die anderen zu löschen. Oder \"keinen\" um alle Fahrwege zu verwerfen (<i>setzt gewählten als gelöscht!</i>).</html>"
         );
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.jPanel2.add(this.jLabel2, gridBagConstraints);
      this.fw_double_buttonOk
         .setFont(
            this.fw_double_buttonOk
               .getFont()
               .deriveFont(this.fw_double_buttonOk.getFont().getStyle() | 1, (float)(this.fw_double_buttonOk.getFont().getSize() - 1))
         );
      this.fw_double_buttonOk.setText("diesen");
      this.fw_double_buttonOk.setEnabled(false);
      this.fw_double_buttonOk.setFocusPainted(false);
      this.fw_double_buttonOk.setFocusable(false);
      this.fw_double_buttonOk.setMargin(new Insets(0, 10, 0, 10));
      this.fw_double_buttonOk.setMinimumSize(new Dimension(63, 10));
      this.fw_double_buttonOk.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fwdoppeltPanel.this.fw_double_buttonOkActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weighty = 1.0;
      this.jPanel2.add(this.fw_double_buttonOk, gridBagConstraints);
      this.fw_double_buttonNone.setFont(this.fw_double_buttonNone.getFont().deriveFont((float)this.fw_double_buttonNone.getFont().getSize() - 1.0F));
      this.fw_double_buttonNone.setText("keiner");
      this.fw_double_buttonNone.setEnabled(false);
      this.fw_double_buttonNone.setFocusPainted(false);
      this.fw_double_buttonNone.setFocusable(false);
      this.fw_double_buttonNone.setMargin(new Insets(0, 10, 0, 10));
      this.fw_double_buttonNone.setMinimumSize(new Dimension(57, 10));
      this.fw_double_buttonNone.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fwdoppeltPanel.this.fw_double_buttonNoneActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weighty = 1.0;
      this.jPanel2.add(this.fw_double_buttonNone, gridBagConstraints);
      this.fw_double_buttonStop.setFont(this.fw_double_buttonStop.getFont().deriveFont((float)this.fw_double_buttonStop.getFont().getSize() - 1.0F));
      this.fw_double_buttonStop.setText("stopp...");
      this.fw_double_buttonStop.setToolTipText("Stop beendet Auswahl, löscht alle Fahrstraßen!");
      this.fw_double_buttonStop.setFocusPainted(false);
      this.fw_double_buttonStop.setFocusable(false);
      this.fw_double_buttonStop.setMargin(new Insets(0, 10, 0, 10));
      this.fw_double_buttonStop.setMinimumSize(new Dimension(63, 10));
      this.fw_double_buttonStop.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fwdoppeltPanel.this.fw_double_buttonStopActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weighty = 0.8;
      this.jPanel2.add(this.fw_double_buttonStop, gridBagConstraints);
      this.add(this.jPanel2);
      this.statusPanel.setLayout(new GridLayout(1, 0));
      this.add(this.statusPanel);
   }

   private void debugButtonActionPerformed(ActionEvent evt) {
      this.static_testMode = true;
   }

   private void fw_double_buttonOkActionPerformed(ActionEvent evt) {
      this.returnDeletes = new LinkedList();

      for(fahrstrasse f : this.fahrstrassen) {
         if (f != this.fw_double_list.getSelectedValue()) {
            this.returnDeletes.add(f);
         }
      }

      this.doClose(this.fw_double_list.getSelectedIndex());
   }

   private void fw_double_buttonNoneActionPerformed(ActionEvent evt) {
      this.returnDeletes = new LinkedList();

      for(fahrstrasse f : this.fahrstrassen) {
         if (f != this.fw_double_list.getSelectedValue()) {
            this.returnDeletes.add(f);
         }
      }

      ((fahrstrasse)this.fw_double_list.getSelectedValue()).getExtend().fstype = 4;
      this.doClose(-1);
   }

   private void fw_double_buttonStopActionPerformed(ActionEvent evt) {
      int r = JOptionPane.showConfirmDialog(
         this, "<html>Wirklich die Analyse abbrechen? Dies führt dazu, dass <b>alle</b> Fahrstraßen gelöscht werden!</html>", "Wirklich abbrechen?", 0, 2
      );
      if (r == 0) {
         this.doClose(-2);
      }
   }

   private void fw_double_listValueChanged(ListSelectionEvent evt) {
      if (!this.autoAcceptCB.isSelected() || this.lastfs == null) {
         fahrstrasse f = (fahrstrasse)this.fw_double_list.getSelectedValue();
         this.fw_double_buttonOk.setEnabled(f != null);
         this.fw_double_buttonNone.setEnabled(f != null);
         this.glbControl.getModel().showFahrweg(f);
         if (f != null && this.lastfs == f) {
            this.hintLabel.setIcon(this.wayKnown);
         } else {
            this.hintLabel.setIcon(this.wayUnknown);
         }
      }
   }

   private void autoAcceptCBActionPerformed(ActionEvent evt) {
      if (this.autoAcceptCB.isSelected()) {
         this.fw_double_buttonOkActionPerformed(null);
      }
   }
}
