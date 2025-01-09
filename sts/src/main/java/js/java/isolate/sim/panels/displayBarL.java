package js.java.isolate.sim.panels;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.QuadCurve2D.Double;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.displayBar.connector;
import js.java.isolate.sim.gleis.displayBar.displayBar;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gleisbildViewPanel;
import js.java.isolate.sim.gleisbild.scaleHolder;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecDisplayEdit;
import js.java.isolate.sim.panels.actionevents.displaySelectedEvent;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.GraphicTools;

public class displayBarL extends basePanel implements gleisbildViewPanel.glbOverlayPainter {
   private final displayBar.listModel model;
   private boolean shown = false;
   private JToggleButton alterVisibleMode;
   private JButton clearAllButton;
   private JList displayList;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JPanel jPanel4;
   private JScrollPane jScrollPane1;
   private JSeparator jSeparator1;
   private JCheckBox legacyModeCB;

   public displayBarL(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.model = this.glbControl.getModel().getDisplayBar().getDisplayList();
      this.initComponents();
      e.registerListener(10, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof GecSelectEvent && this.shown) {
         int i = this.model.findIndex(this.glbControl.getSelectedGleis());
         if (i >= 0) {
            this.displayList.setSelectedIndex(i);
            this.displayList.ensureIndexIsVisible(i);
         }
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.glbControl.setFocus(null);
      this.legacyModeCB.setSelected(this.glbControl.getModel().getDisplayBar().isLegacy());
      this.legacyModeCBItemStateChanged(null);
      this.shown = true;
      this.model.refresh();
      gec.addChangeListener(this);
      this.alterVisibleMode.setSelected(false);
   }

   @Override
   public void hidden(gecBase gec) {
      this.shown = false;
      this.glbControl.getPanel().removeOverlayPainer(this);
      this.glbControl.setFocus(null);
      ((gecDisplayEdit)gec).setDarkMode(false);
      this.alterVisibleMode.setSelected(false);
   }

   @Override
   public void paint(gleisbildViewPanel panel, Graphics g, scaleHolder scaler) {
      if (this.displayList.getSelectedIndex() >= 0) {
         Graphics2D g2 = (Graphics2D)g;
         gleis disp = this.model.getDisplay(this.displayList.getSelectedIndex());
         LinkedList<connector> d = this.model.getDisplayData(this.displayList.getSelectedIndex());
         GraphicTools.enableGfxAA(g2);
         g2.setStroke(new BasicStroke(3.0F));

         for (connector c : d) {
            if (c.isFSconnector()) {
               g2.setColor(Color.YELLOW);
            } else {
               g2.setColor(Color.RED);
            }

            String sw = c.getSWwert();
            Iterator<gleis> it = this.glbControl.getModel().findIterator(new Object[]{gleis.ALLE_GLEISE, sw});

            while (it.hasNext()) {
               gleis gl = (gleis)it.next();
               if (gl.isDisplayTriggerSelectable()) {
                  double cY = (double)disp.getRow() * scaler.getYScale() + scaler.getYScale() / 2.0;
                  if (disp.getRow() == gl.getRow()) {
                     if (disp.getRow() < 3) {
                        cY = (double)(disp.getRow() + 2) * scaler.getYScale() + scaler.getYScale() / 2.0;
                     } else {
                        cY = (double)(disp.getRow() - 2) * scaler.getYScale() + scaler.getYScale() / 2.0;
                     }
                  }

                  QuadCurve2D qc = new Double(
                     (double)disp.getCol() * scaler.getXScale() + scaler.getXScale() / 2.0,
                     (double)disp.getRow() * scaler.getYScale() + scaler.getYScale() / 2.0,
                     (double)gl.getCol() * scaler.getXScale()
                        + scaler.getXScale() / 2.0
                        + (
                              (double)disp.getCol() * scaler.getXScale()
                                 + scaler.getXScale() / 2.0
                                 - ((double)gl.getCol() * scaler.getXScale() + scaler.getXScale() / 2.0)
                           )
                           / 2.0,
                     cY,
                     (double)gl.getCol() * scaler.getXScale() + scaler.getXScale() / 2.0,
                     (double)gl.getRow() * scaler.getYScale() + scaler.getYScale() / 2.0
                  );
                  g2.draw(qc);
               }
            }
         }

         g2.setStroke(new BasicStroke(1.0F));
      }
   }

   private void initComponents() {
      this.jPanel2 = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.displayList = new JList();
      this.jPanel1 = new JPanel();
      this.jPanel4 = new JPanel();
      this.alterVisibleMode = new JToggleButton();
      this.jPanel3 = new JPanel();
      this.legacyModeCB = new JCheckBox();
      this.jSeparator1 = new JSeparator();
      this.clearAllButton = new JButton();
      this.setLayout(new BorderLayout());
      this.jPanel2.setBorder(BorderFactory.createTitledBorder("Displays"));
      this.jPanel2.setLayout(new BorderLayout());
      this.displayList.setModel(this.model);
      this.displayList.setSelectionMode(0);
      this.displayList.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent evt) {
            displayBarL.this.displayListValueChanged(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.displayList);
      this.jPanel2.add(this.jScrollPane1, "Center");
      this.add(this.jPanel2, "Center");
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("Details"));
      this.jPanel1.setPreferredSize(new Dimension(145, 25));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jPanel4.setLayout(new BoxLayout(this.jPanel4, 2));
      this.alterVisibleMode.setText("Verbindungen zeigen");
      this.alterVisibleMode.setToolTipText("Aktiviert eine andere Darstellung");
      this.alterVisibleMode.setFocusPainted(false);
      this.alterVisibleMode.setFocusable(false);
      this.alterVisibleMode.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            displayBarL.this.alterVisibleModeActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.alterVisibleMode);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.jPanel4, gridBagConstraints);
      this.jPanel3.setBorder(BorderFactory.createEtchedBorder());
      this.jPanel3.setLayout(new GridBagLayout());
      this.legacyModeCB.setText("<html>SW-Wert &lt;-&gt SW-Wert Modus</html>");
      this.legacyModeCB
         .setToolTipText(
            "<html>Verknüpft automatisch SW-Wert von<br>Auslösern mit SW-Wert von Display<p>\n<b>sonstige Verknüpfungen werden<br>dadurch gelöscht!</b></html>"
         );
      this.legacyModeCB.setFocusPainted(false);
      this.legacyModeCB.setFocusable(false);
      this.legacyModeCB.setPreferredSize(new Dimension(81, 24));
      this.legacyModeCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            displayBarL.this.legacyModeCBItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.jPanel3.add(this.legacyModeCB, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(3, 0, 3, 0);
      this.jPanel3.add(this.jSeparator1, gridBagConstraints);
      this.clearAllButton.setText("alles löschen...");
      this.clearAllButton.setEnabled(false);
      this.clearAllButton.setMinimumSize(new Dimension(50, 25));
      this.clearAllButton.setPreferredSize(new Dimension(50, 25));
      this.clearAllButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            displayBarL.this.clearAllButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.jPanel3.add(this.clearAllButton, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 15;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel1.add(this.jPanel3, gridBagConstraints);
      this.add(this.jPanel1, "West");
   }

   private void legacyModeCBItemStateChanged(ItemEvent evt) {
      boolean ok = false;
      if (this.shown && evt != null && this.legacyModeCB.isSelected()) {
         int r = JOptionPane.showConfirmDialog(this, "Löscht bestehende manuell erzeugte Verdrahtungen!", "Wirklich ändern?", 2, 2);
         if (r == 0) {
            ok = true;
         }
      } else {
         ok = true;
      }

      if (ok) {
         this.glbControl.getModel().getDisplayBar().setLegacy(this.legacyModeCB.isSelected());
         this.displayList.clearSelection();
         this.glbControl.setFocus(null);
         this.clearAllButton.setEnabled(!this.legacyModeCB.isSelected());
      } else {
         this.legacyModeCB.setSelected(!this.legacyModeCB.isSelected());
      }
   }

   private void displayListValueChanged(ListSelectionEvent evt) {
      this.glbControl.setFocus(null);
      this.glbControl.getModel().allOff();
      if (this.displayList.getSelectedIndex() >= 0) {
         LinkedList<connector> d = this.model.getDisplayData(this.displayList.getSelectedIndex());
         this.my_main.interPanelCom(new displaySelectedEvent(this.model.getDisplay(this.displayList.getSelectedIndex()), d));
         this.glbControl.setFocus(this.model.getDisplay(this.displayList.getSelectedIndex()));
      } else {
         this.my_main.interPanelCom(new displaySelectedEvent());
      }
   }

   private void clearAllButtonActionPerformed(ActionEvent evt) {
      int r = JOptionPane.showConfirmDialog(this, "Wirklich alle Verdrahtungen löschen?", "Verdrahtungen löschen?", 2, 2);
      if (r == 0) {
         this.glbControl.getModel().getDisplayBar().clear();
         this.glbControl.getModel().getDisplayBar().setLegacy(false);
         this.displayListValueChanged(null);
      }
   }

   private void alterVisibleModeActionPerformed(ActionEvent evt) {
      ((gecDisplayEdit)this.glbControl.getMode()).setDarkMode(this.alterVisibleMode.isSelected());
      if (this.alterVisibleMode.isSelected()) {
         this.glbControl.getPanel().addOverlayPainer(this);
      } else {
         this.glbControl.getPanel().removeOverlayPainer(this);
      }

      this.displayListValueChanged(null);
   }
}
