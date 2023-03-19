package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.panels.actionevents.coordsEvent;
import js.java.tools.actions.AbstractEvent;

public class coordsPanel extends basePanel {
   private JButton calcButton;
   private JTextField dist;
   private JTextField distX;
   private JTextField distY;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JLabel positionLabel;
   private JTextField x1;
   private JTextField x2;
   private JTextField y1;
   private JTextField y2;

   public coordsPanel(gleisbildEditorControl glb, stellwerk_editor e) {
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

   public void showCoords(int x, int y) {
      this.positionLabel.setText(String.format("%03d/%03d", x, y));
   }

   private void initComponents() {
      this.positionLabel = new JLabel();
      this.jPanel1 = new JPanel();
      this.jPanel2 = new JPanel();
      this.x1 = new JTextField();
      this.y1 = new JTextField();
      this.jPanel3 = new JPanel();
      this.x2 = new JTextField();
      this.y2 = new JTextField();
      this.calcButton = new JButton();
      this.jLabel1 = new JLabel();
      this.jLabel2 = new JLabel();
      this.jLabel3 = new JLabel();
      this.distX = new JTextField();
      this.distY = new JTextField();
      this.dist = new JTextField();
      this.setBorder(BorderFactory.createTitledBorder("Koordinaten"));
      this.setLayout(new BorderLayout());
      this.positionLabel.setFont(this.positionLabel.getFont().deriveFont(this.positionLabel.getFont().getStyle() | 1));
      this.positionLabel.setHorizontalAlignment(4);
      this.positionLabel.setText("0/0 ");
      this.add(this.positionLabel, "South");
      this.jPanel1.setLayout(new GridBagLayout());
      this.jPanel2.setBorder(BorderFactory.createTitledBorder("1. X/Y"));
      this.jPanel2.setLayout(new GridLayout(1, 0));
      this.x1.setColumns(3);
      this.jPanel2.add(this.x1);
      this.y1.setColumns(2);
      this.jPanel2.add(this.y1);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.jPanel2, gridBagConstraints);
      this.jPanel3.setBorder(BorderFactory.createTitledBorder("2. X/Y"));
      this.jPanel3.setLayout(new GridLayout(1, 0));
      this.x2.setColumns(3);
      this.jPanel3.add(this.x2);
      this.y2.setColumns(2);
      this.jPanel3.add(this.y2);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.jPanel3, gridBagConstraints);
      this.calcButton.setText("Abstand berechnen");
      this.calcButton.setFocusPainted(false);
      this.calcButton.setFocusable(false);
      this.calcButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            coordsPanel.this.calcButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      this.jPanel1.add(this.calcButton, gridBagConstraints);
      this.jLabel1.setForeground(SystemColor.windowBorder);
      this.jLabel1.setText("X-Abstand");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 13;
      this.jPanel1.add(this.jLabel1, gridBagConstraints);
      this.jLabel2.setForeground(SystemColor.windowBorder);
      this.jLabel2.setText("Y-Abstand");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 13;
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      this.jLabel3.setForeground(SystemColor.windowBorder);
      this.jLabel3.setText("Abstand");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = 13;
      this.jPanel1.add(this.jLabel3, gridBagConstraints);
      this.distX.setColumns(5);
      this.distX.setEditable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.distX, gridBagConstraints);
      this.distY.setColumns(5);
      this.distY.setEditable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.distY, gridBagConstraints);
      this.dist.setColumns(5);
      this.dist.setEditable(false);
      this.dist.setHorizontalAlignment(2);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 4;
      gridBagConstraints.anchor = 17;
      this.jPanel1.add(this.dist, gridBagConstraints);
      this.add(this.jPanel1, "Center");
   }

   private void calcButtonActionPerformed(ActionEvent evt) {
      try {
         int _x1 = Integer.parseInt(this.x1.getText());
         int _x2 = Integer.parseInt(this.x2.getText());
         int _y1 = Integer.parseInt(this.y1.getText());
         int _y2 = Integer.parseInt(this.y2.getText());
         int dx = Math.abs(_x2 - _x1);
         int dy = Math.abs(_y2 - _y1);
         double d = Math.sqrt((double)(dx * dx + dy + dy));
         this.distX.setText(Integer.toString(dx));
         this.distY.setText(Integer.toString(dy));
         this.dist.setText(Double.toString(d));
      } catch (NumberFormatException var10) {
         this.distX.setText("Zahlenfehler");
         this.distY.setText("Zahlenfehler");
         this.dist.setText("Zahlenfehler");
      }
   }
}
