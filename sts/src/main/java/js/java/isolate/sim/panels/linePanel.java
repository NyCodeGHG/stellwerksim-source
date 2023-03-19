package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecGleisLine;
import js.java.isolate.sim.panels.actionevents.coordsEvent;
import js.java.tools.actions.AbstractEvent;

public class linePanel extends basePanel {
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JRadioButton line_keep_RB;
   private JRadioButton line_over_RB;
   private ButtonGroup modeGB;
   private JLabel positionLabel;

   public linePanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      e.registerListener(1, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof coordsEvent) {
         this.showCoords(((coordsEvent)e).getX(), ((coordsEvent)e).getY());
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.line_keep_RB.setSelected(!((gecGleisLine)this.glbControl.getMode()).isDrawOver());
      this.line_over_RB.setSelected(((gecGleisLine)this.glbControl.getMode()).isDrawOver());
   }

   public void showCoords(int x, int y) {
      this.positionLabel.setText(String.format("%03d/%03d", x, y));
   }

   private void initComponents() {
      this.modeGB = new ButtonGroup();
      this.positionLabel = new JLabel();
      this.jPanel1 = new JPanel();
      this.jPanel2 = new JPanel();
      this.jPanel3 = new JPanel();
      this.line_over_RB = new JRadioButton();
      this.line_keep_RB = new JRadioButton();
      this.jLabel1 = new JLabel();
      this.setBorder(BorderFactory.createTitledBorder("Streckenlinie"));
      this.setLayout(new BorderLayout());
      this.positionLabel.setFont(this.positionLabel.getFont().deriveFont(this.positionLabel.getFont().getStyle() | 1));
      this.positionLabel.setHorizontalAlignment(4);
      this.positionLabel.setText("0/0 ");
      this.add(this.positionLabel, "South");
      this.jPanel1.setLayout(new BoxLayout(this.jPanel1, 3));
      this.jPanel2.setLayout(new BoxLayout(this.jPanel2, 2));
      this.jPanel3.setLayout(new BoxLayout(this.jPanel3, 3));
      this.modeGB.add(this.line_over_RB);
      this.line_over_RB.setSelected(true);
      this.line_over_RB.setText("<html>Strecke&nbsp;zeichnen<br>(überzeichnen)</html>");
      this.line_over_RB.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent evt) {
            linePanel.this.line_RBStateChanged(evt);
         }
      });
      this.jPanel3.add(this.line_over_RB);
      this.modeGB.add(this.line_keep_RB);
      this.line_keep_RB.setText("<html>Strecke&nbsp;zeichnen<br>(altes&nbsp;belassen)</html>");
      this.line_keep_RB.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent evt) {
            linePanel.this.line_RBStateChanged(evt);
         }
      });
      this.jPanel3.add(this.line_keep_RB);
      this.jPanel2.add(this.jPanel3);
      this.jLabel1
         .setText(
            "<html>Zuerst Startkoordinaten festlegen (Mausklick), dann Zielkoordinaten (Maus loslassen). Es werden nur einfache Gleiselemente gezeichnet, keine Weichen oder Kreuzungen!</html>\n"
         );
      this.jLabel1.setVerticalAlignment(1);
      this.jPanel2.add(this.jLabel1);
      this.jPanel1.add(this.jPanel2);
      this.add(this.jPanel1, "Center");
   }

   private void line_RBStateChanged(ChangeEvent evt) {
      JRadioButton a = (JRadioButton)evt.getSource();
      ((gecGleisLine)this.glbControl.getMode()).setDrawOver(a == this.line_over_RB);
   }
}
