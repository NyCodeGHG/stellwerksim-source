package js.java.isolate.sim.sim;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeMap;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.gleisbild.scaleHolder;

public class scaleWindow extends JDialog implements ActionListener {
   private ButtonGroup scaleButtonGroup;
   private ArrayList<AbstractButton> zoomLevels;
   private Timer closeTimer = new Timer(30000, this);
   private final gleisbildSimControl glbControl;
   private final scaleHolder scaler;
   private JCheckBox bindXY;
   private JPanel jPanel1;
   private JButton okButton;
   private JSlider zoomXSlider;
   private JSlider zoomYSlider;

   public scaleWindow(stellwerksim_main parent, ButtonGroup scaleButtonGroup) {
      super(parent, false);
      this.glbControl = parent.getControl();
      this.scaler = this.glbControl.getScaler();
      this.scaleButtonGroup = scaleButtonGroup;
      this.initComponents();
      this.zoomXSlider.setValue((int)this.scaler.numberOfScale(this.scaler.getXScale()));
      this.zoomXSlider.setMinimum(50);
      this.zoomXSlider.setMaximum(250);
      this.zoomYSlider.setValue((int)this.scaler.numberOfScale(this.scaler.getYScale()));
      this.zoomYSlider.setMinimum(50);
      this.zoomYSlider.setMaximum(250);
      this.setLocationRelativeTo(parent);
      this.closeTimer.start();
      this.bindXY.setSelected(this.zoomXSlider.getValue() == this.zoomYSlider.getValue());
      this.bindXYItemStateChanged(null);
      this.zoomXSlider.addChangeListener(evt -> {
         this.setScale();
         this.closeTimer.restart();
      });
      this.zoomYSlider.addChangeListener(evt -> {
         if (!this.bindXY.isSelected()) {
            this.setScale();
         }

         this.closeTimer.restart();
      });
   }

   private void setScale() {
      int x = this.zoomXSlider.getValue();
      if (this.bindXY.isSelected()) {
         this.zoomYSlider.setValue(x);
      }

      int y = this.zoomYSlider.getValue();
      this.glbControl.setScale(this.scaler.scaleOfNumber(x), this.scaler.scaleOfNumber(y));
      this.scaleButtonGroup.clearSelection();
   }

   private void analyseButtons() {
      TreeMap<String, AbstractButton> labels = new TreeMap();

      AbstractButton ab;
      String scalev;
      for(Enumeration<AbstractButton> en = this.scaleButtonGroup.getElements(); en.hasMoreElements(); labels.put(scalev + "z", ab)) {
         ab = (AbstractButton)en.nextElement();
         scalev = ab.getActionCommand();
         if (scalev.charAt(0) > '2') {
            scalev = "0" + scalev;
         }
      }

      this.zoomLevels = new ArrayList(labels.values());
   }

   public void actionPerformed(ActionEvent e) {
      this.close();
   }

   private void close() {
      this.setVisible(false);
      this.dispose();
      this.closeTimer.stop();
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.zoomXSlider = new JSlider();
      this.bindXY = new JCheckBox();
      this.zoomYSlider = new JSlider();
      this.okButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Zoomregler");
      this.addWindowFocusListener(new WindowFocusListener() {
         public void windowGainedFocus(WindowEvent evt) {
         }

         public void windowLostFocus(WindowEvent evt) {
            scaleWindow.this.formWindowLostFocus(evt);
         }
      });
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("Zoomstufe"));
      this.jPanel1.setLayout(new GridBagLayout());
      this.zoomXSlider.setFont(this.zoomXSlider.getFont().deriveFont((float)this.zoomXSlider.getFont().getSize() - 3.0F));
      this.zoomXSlider.setMajorTickSpacing(10);
      this.zoomXSlider.setMaximum(180);
      this.zoomXSlider.setMinimum(60);
      this.zoomXSlider.setMinorTickSpacing(5);
      this.zoomXSlider.setPaintLabels(true);
      this.zoomXSlider.setPaintTicks(true);
      this.zoomXSlider.setFocusable(false);
      this.zoomXSlider.setMaximumSize(new Dimension(32767, 100));
      this.zoomXSlider.setMinimumSize(new Dimension(450, 60));
      this.zoomXSlider.setPreferredSize(new Dimension(450, 60));
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.zoomXSlider, gridBagConstraints);
      this.bindXY.setSelected(true);
      this.bindXY.setText("X/Y verbunden");
      this.bindXY.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            scaleWindow.this.bindXYItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      this.jPanel1.add(this.bindXY, gridBagConstraints);
      this.zoomYSlider.setFont(this.zoomYSlider.getFont().deriveFont((float)this.zoomYSlider.getFont().getSize() - 3.0F));
      this.zoomYSlider.setMajorTickSpacing(10);
      this.zoomYSlider.setMaximum(180);
      this.zoomYSlider.setMinimum(60);
      this.zoomYSlider.setMinorTickSpacing(5);
      this.zoomYSlider.setPaintLabels(true);
      this.zoomYSlider.setPaintTicks(true);
      this.zoomYSlider.setFocusable(false);
      this.zoomYSlider.setMaximumSize(new Dimension(32767, 100));
      this.zoomYSlider.setMinimumSize(new Dimension(450, 60));
      this.zoomYSlider.setPreferredSize(new Dimension(450, 60));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.zoomYSlider, gridBagConstraints);
      this.okButton.setText("Ok");
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            scaleWindow.this.okButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 22;
      this.jPanel1.add(this.okButton, gridBagConstraints);
      this.getContentPane().add(this.jPanel1, "Center");
      this.pack();
   }

   private void formWindowLostFocus(WindowEvent evt) {
      this.close();
   }

   private void bindXYItemStateChanged(ItemEvent evt) {
      this.zoomYSlider.setEnabled(!this.bindXY.isSelected());
      if (this.bindXY.isSelected()) {
         this.setScale();
      }

      this.closeTimer.restart();
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      this.close();
   }
}
