package js.java.isolate.sim.sim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.gruppentasten.TasterButton;
import js.java.isolate.sim.sim.gruppentasten.gtAutoFS;
import js.java.isolate.sim.sim.gruppentasten.gtAutoFSoff;
import js.java.isolate.sim.sim.gruppentasten.gtAutoFStriggered;
import js.java.isolate.sim.sim.gruppentasten.gtAutoFStriggeredManual;
import js.java.isolate.sim.sim.gruppentasten.gtBase;
import js.java.isolate.sim.sim.gruppentasten.gtBue;
import js.java.isolate.sim.sim.gruppentasten.gtDisplayCall;
import js.java.isolate.sim.sim.gruppentasten.gtDisplayClear;
import js.java.isolate.sim.sim.gruppentasten.gtFSaufloesen;
import js.java.isolate.sim.sim.gruppentasten.gtSPloeschen;
import js.java.isolate.sim.sim.gruppentasten.gtSh;
import js.java.isolate.sim.sim.gruppentasten.gtUFGT;
import js.java.isolate.sim.sim.gruppentasten.gtUegNOk;
import js.java.isolate.sim.sim.gruppentasten.gtUegOk;
import js.java.isolate.sim.sim.gruppentasten.gtWeiche;
import js.java.isolate.sim.sim.gruppentasten.gtZS1;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.layout.RowLayout;

public class extrasPanel extends JPanel implements SessionClose {
   private final stellwerksim_main my_main;
   private final gleisbildSimControl glbControl;
   private final JLabel heatLabel;
   private final Timer htTimer;
   private final HashMap<String, JPanel> gruppenPanel = new HashMap();
   private JPanel buttonsPanel;
   private JPanel eastPanel;
   private JPanel jPanel7;
   private JTextField locateElementTF;
   private JPanel locatePanel;
   private JButton xtr_allautofsoff;
   private JButton xtr_allautofson;
   private JLabel xtr_zählwerk;

   public extrasPanel(stellwerksim_main m, gleisbildSimControl glb) {
      super();
      this.my_main = m;
      this.glbControl = glb;
      this.initComponents();
      if (m.isExtraMode()) {
         this.heatLabel = new JLabel();
         this.heatLabel.setToolTipText("Stellwerk-Hitze");
         this.eastPanel.add(this.heatLabel, "West");
         this.htTimer = new Timer(30000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               extrasPanel.this.heatLabel.setText("H: " + Math.max(0L, zug.getHeat()));
            }
         });
         this.htTimer.start();
      } else {
         this.htTimer = null;
         this.heatLabel = null;
      }

      m.uc.addCloseObject(this);
   }

   @Override
   public void close() {
      if (this.htTimer != null) {
         SwingUtilities.invokeLater(() -> this.htTimer.stop());
      }

      this.gruppenPanel.clear();
   }

   public void setEnabled(boolean e) {
      this.setEnabled(e, this);
   }

   private void setEnabled(boolean e, Container cp) {
      Component[] c = cp.getComponents();

      for(Component cc : c) {
         cc.setEnabled(e);
         if (cc instanceof Container) {
            this.setEnabled(e, (Container)cc);
         }
      }

      cp.setEnabled(e);
   }

   private void addPanel(String name, Color col) {
      if (!this.gruppenPanel.containsKey(name)) {
         JPanel p = new JPanel();
         p.setBackground(col);
         p.setLayout(new RowLayout(2));
         this.buttonsPanel.add(p);
         this.gruppenPanel.put(name, p);
      }
   }

   private void addGruppentaste(String panelname, gtBase gtHandler, boolean white) {
      this.addGruppentaste(panelname, gtHandler, white, "");
   }

   private void addGruppentaste(String panelname, gtBase gtHandler, boolean white, String fkey) {
      JPanel p = (JPanel)this.gruppenPanel.get(panelname);
      TasterButton b = gtHandler.createButton(white, p.getBackground(), fkey);
      gtHandler.setButton(b);
      p.add(b);
      this.my_main.uc.addCloseObject(b);
   }

   private void addGruppentasteDummy(String panelname) {
      JPanel p = (JPanel)this.gruppenPanel.get(panelname);
      p.add(new JLabel());
   }

   private void setBGColor(String panelname, Color col) {
      JPanel p = (JPanel)this.gruppenPanel.get(panelname);
      if (p != null) {
         Component[] c = p.getComponents();
         p.setBackground(col);

         for(Component cc : c) {
            cc.setBackground(col);
         }
      }
   }

   void setAlterColor() {
      this.setBGColor("signal", new Color(0, 155, 255));
      this.setBGColor("signal2", new Color(100, 255, 255));
   }

   void setNormalColor() {
      this.setBGColor("signal", new Color(64, 191, 0));
      this.setBGColor("signal2", new Color(255, 0, 0));
   }

   void initGruppentaster() {
      this.addPanel("weiche", new Color(0, 0, 255));
      this.addGruppentaste("weiche", new gtWeiche(this.my_main, this.glbControl), true, "F1");
      this.addPanel("signal", new Color(64, 191, 0));
      this.addGruppentaste("signal", new gtZS1(this.my_main, this.glbControl), false, "F2");
      if (this.glbControl.getModel().hasZwerge()) {
         this.addGruppentaste("signal", new gtSh(this.my_main, this.glbControl), false, "F3");
      } else {
         this.addGruppentasteDummy("signal");
      }

      this.addGruppentaste("signal", new gtFSaufloesen(this.my_main, this.glbControl), false, "F4");
      if (this.glbControl.getModel().gleisbildextend.getSignalversion() != 0) {
         this.addGruppentaste("signal", new gtSPloeschen(this.my_main, this.glbControl), false, "F5");
      } else {
         this.addGruppentasteDummy("signal");
      }

      this.addGruppentaste("signal", new gtAutoFStriggered(this.my_main, this.glbControl), false, "F6");
      this.addGruppentaste("signal", new gtAutoFS(this.my_main, this.glbControl), false);
      if (this.glbControl.getModel().gleisbildextend.getSignalversion() != 0 && !this.my_main.isRealistic()) {
         this.addGruppentaste("signal", new gtAutoFStriggeredManual(this.my_main, this.glbControl), false, "F7");
      }

      this.addGruppentaste("signal", new gtAutoFSoff(this.my_main, this.glbControl), false, "F8");
      this.addGruppentaste("signal", new gtUFGT(this.my_main, this.glbControl), false, "F9");
      gleis g = this.glbControl.getModel().findFirst(new Object[]{gleis.ALLE_BAHNÜBERGÄNGE});
      if (g != null) {
         this.addPanel("bü", new Color(0, 0, 0));
         this.addGruppentaste("bü", new gtBue(this.my_main, this.glbControl), true, "F10");
      }

      g = this.glbControl.getModel().findFirst(new Object[]{gleis.ALLE_KNÖPFE});
      if (g != null) {
         this.addPanel("üg", new Color(255, 255, 0));
         this.addGruppentaste("üg", new gtUegOk(this.my_main, this.glbControl), false, "F11");
         this.addGruppentaste("üg", new gtUegNOk(this.my_main, this.glbControl), false, "F12");
      }

      g = this.glbControl.getModel().findFirst(new Object[]{gleis.ALLE_DISPLAYS});
      if (g != null) {
         this.addPanel("disp", new Color(170, 170, 170));
         this.addGruppentaste("disp", new gtDisplayClear(this.my_main, this.glbControl), false);
         g = this.glbControl.getModel().findFirst(new Object[]{gleis.ELEMENT_AIDDISPLAY});
         if (g != null && this.glbControl.getModel().getPhonebook().canCall()) {
            this.addGruppentaste("disp", new gtDisplayCall(this.my_main, this.glbControl), false);
         }
      }
   }

   void setZählwert(int zählwerk) {
      this.xtr_zählwerk.setText(zählwerk + " ");
   }

   public void setFocus() {
      this.locateElementTF.setCaretPosition(0);
      this.locateElementTF.setSelectionStart(0);
      this.locateElementTF.setSelectionEnd(this.locateElementTF.getText().length());
      this.locateElementTF.requestFocusInWindow();
   }

   private void initComponents() {
      this.buttonsPanel = new JPanel();
      this.eastPanel = new JPanel();
      this.jPanel7 = new JPanel();
      this.locatePanel = new JPanel();
      this.locateElementTF = new JTextField();
      this.xtr_allautofson = new JButton();
      this.xtr_zählwerk = new JLabel();
      this.xtr_allautofsoff = new JButton();
      this.setLayout(new BorderLayout());
      this.buttonsPanel.setBackground(gleis.colors.col_stellwerk_back);
      this.buttonsPanel.setAlignmentX(0.0F);
      this.buttonsPanel.setAlignmentY(0.0F);
      this.buttonsPanel.setLayout(new FlowLayout(0, 3, 0));
      this.add(this.buttonsPanel, "Center");
      this.eastPanel.setLayout(new BorderLayout());
      this.jPanel7.setAlignmentX(0.0F);
      this.jPanel7.setAlignmentY(0.0F);
      this.jPanel7.setLayout(new GridLayout(2, 0));
      this.locatePanel.setBackground(Color.black);
      this.locatePanel.setLayout(new BoxLayout(this.locatePanel, 2));
      this.locateElementTF.setBackground(Color.black);
      this.locateElementTF.setFont(new Font("Dialog", 0, 10));
      this.locateElementTF.setForeground(Color.orange);
      this.locateElementTF.setHorizontalAlignment(4);
      this.locateElementTF.setToolTipText("Element suchen, Elementnummer eingeben + Return");
      this.locateElementTF.setBorder(new LineBorder(Color.gray, 1, true));
      this.locateElementTF.setCaretColor(Color.orange);
      this.locateElementTF.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            extrasPanel.this.locateElementTFActionPerformed(evt);
         }
      });
      this.locatePanel.add(this.locateElementTF);
      this.jPanel7.add(this.locatePanel);
      this.xtr_allautofson.setFont(new Font("Dialog", 0, 10));
      this.xtr_allautofson.setText("alle autoFS ein");
      this.xtr_allautofson.setToolTipText("Schaltet alle automatischen Fahrstraßen (autoFS) ein.");
      this.xtr_allautofson.setFocusPainted(false);
      this.xtr_allautofson.setFocusable(false);
      this.xtr_allautofson.setMargin(new Insets(0, 0, 0, 0));
      this.xtr_allautofson.setMaximumSize(new Dimension(90, 14));
      this.xtr_allautofson.setMinimumSize(new Dimension(90, 14));
      this.xtr_allautofson.setPreferredSize(new Dimension(90, 14));
      this.xtr_allautofson.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            extrasPanel.this.xtr_allautofsonActionPerformed(evt);
         }
      });
      this.jPanel7.add(this.xtr_allautofson);
      this.xtr_zählwerk.setBackground(new Color(0, 0, 0));
      this.xtr_zählwerk.setFont(new Font("DialogInput", 1, 12));
      this.xtr_zählwerk.setForeground(new Color(255, 255, 255));
      this.xtr_zählwerk.setHorizontalAlignment(4);
      this.xtr_zählwerk.setText("0 ");
      this.xtr_zählwerk.setOpaque(true);
      this.jPanel7.add(this.xtr_zählwerk);
      this.xtr_allautofsoff.setFont(new Font("Dialog", 0, 10));
      this.xtr_allautofsoff.setText("alle autoFS aus");
      this.xtr_allautofsoff
         .setToolTipText("<html>Schaltet alle automatischen Fahrstrassen (autoFS) ab.<br>Die bereits anliegenden Fahrstraßen bleiben jedoch erhalten!</html>");
      this.xtr_allautofsoff.setFocusPainted(false);
      this.xtr_allautofsoff.setFocusable(false);
      this.xtr_allautofsoff.setMargin(new Insets(0, 0, 0, 0));
      this.xtr_allautofsoff.setMaximumSize(new Dimension(90, 14));
      this.xtr_allautofsoff.setMinimumSize(new Dimension(90, 14));
      this.xtr_allautofsoff.setPreferredSize(new Dimension(90, 14));
      this.xtr_allautofsoff.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            extrasPanel.this.xtr_allautofsoffActionPerformed(evt);
         }
      });
      this.jPanel7.add(this.xtr_allautofsoff);
      this.eastPanel.add(this.jPanel7, "Center");
      this.add(this.eastPanel, "East");
   }

   private void xtr_allautofsonActionPerformed(ActionEvent evt) {
      this.glbControl.getModel().enableAllAutoFS(true);
   }

   private void xtr_allautofsoffActionPerformed(ActionEvent evt) {
      this.glbControl.getModel().disableAllAutoFS();
   }

   private void locateElementTFActionPerformed(ActionEvent evt) {
      String search = this.locateElementTF.getText();
      this.locateElementTF.setText("");
      if (!search.isEmpty()) {
         Iterator<gleis> it = this.glbControl.getModel().findIteratorWithElementName(search, new Object[]{1});

         while(it.hasNext()) {
            ((gleis)it.next()).setHightlight(true);
         }
      }
   }
}
