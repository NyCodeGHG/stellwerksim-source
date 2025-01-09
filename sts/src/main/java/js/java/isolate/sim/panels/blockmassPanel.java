package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleis.mass.massBase;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecGBlockSelect;
import js.java.tools.ColorTextIcon;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.layout.ColumnLayout;

public class blockmassPanel extends basePanel {
   private int currentMassMode = -1;
   private JPanel buttonsPanel;
   private JLabel jLabel1;

   public blockmassPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.buttonsPanel.setLayout(new ColumnLayout(2));
      this.updateButtons();
   }

   private void updateButtons() {
      if (this.currentMassMode != this.glbControl.getModel().getMasstabCalculatorName()) {
         this.buttonsPanel.removeAll();
         massBase.MasstabItem[] m = this.glbControl.getModel().getMasstabCalculator().getMasstabList();

         for (massBase.MasstabItem it : m) {
            JButton b = new JButton();
            b.setIcon(new ColorTextIcon(it.label));
            b.setText("<html>" + it.label.getText() + "</html>");
            b.setHorizontalAlignment(2);
            b.setFocusable(false);
            b.setFocusPainted(false);
            b.setActionCommand(it.value + "");
            b.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  String a = ((JButton)e.getSource()).getActionCommand();
                  int ai = Integer.parseInt(a);
                  ((gecGBlockSelect)blockmassPanel.this.glbControl.getMode()).setMasstab(ai);
               }
            });
            this.buttonsPanel.add(b);
         }

         this.currentMassMode = this.glbControl.getModel().getMasstabCalculatorName();
      }
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof GecSelectEvent) {
         this.blockEnabled(((gecGBlockSelect)this.glbControl.getMode()).hasBlock());
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.updateButtons();
      this.blockEnabled(((gecGBlockSelect)gec).hasBlock());
      gec.addChangeListener(this);
   }

   private void blockEnabled(boolean e) {
      for (Component c : this.buttonsPanel.getComponents()) {
         c.setEnabled(e);
      }
   }

   private String getTooltip() {
      return "<html>Der Maßstab ändert Tempo (T) und/oder Länge (L) in einem<br>auswählbaren Verhältnis.<br>Haben Länge und Tempo <b>unterschiedliche</b> Maßstäbe innerhalb<br>eines Maßstabfaktors, sind beide mit T und L angegeben.</html>";
   }

   private void initComponents() {
      this.buttonsPanel = new JPanel();
      this.jLabel1 = new JLabel();
      this.setBorder(BorderFactory.createTitledBorder("Maßstab"));
      this.setToolTipText(this.getTooltip());
      this.setLayout(new BorderLayout());
      this.buttonsPanel.setToolTipText(this.getTooltip());
      this.buttonsPanel.setLayout(null);
      this.add(this.buttonsPanel, "Center");
      this.jLabel1.setFont(this.jLabel1.getFont().deriveFont((float)this.jLabel1.getFont().getSize() - 4.0F));
      this.jLabel1.setForeground(SystemColor.textInactiveText);
      this.jLabel1.setHorizontalAlignment(4);
      this.jLabel1.setText("<html>Maus hier, für Hilfe. T: Tempo; L: Länge</html>");
      this.jLabel1.setToolTipText(this.getTooltip());
      this.add(this.jLabel1, "South");
   }
}
