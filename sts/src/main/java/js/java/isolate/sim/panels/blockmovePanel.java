package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.GecSelectEvent;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecGBlockSelect;
import js.java.tools.actions.AbstractEvent;

public class blockmovePanel extends basePanel {
   private JButton clearButton;
   private JLabel jLabel5;
   private JPanel jPanel1;
   private JPanel jPanel11;
   private JSeparator jSeparator1;
   private JButton mirrorHorizButton;
   private JButton mirrorVertButton;
   private JButton mode_block_down;
   private JButton mode_block_left;
   private JButton mode_block_right;
   private JButton mode_block_up;

   public blockmovePanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof GecSelectEvent) {
         this.blockEnabled(((gecGBlockSelect)this.glbControl.getMode()).hasBlock());
      }
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.blockEnabled(((gecGBlockSelect)gec).hasBlock());
      gec.addChangeListener(this);
   }

   private void blockEnabled(boolean e) {
      this.mode_block_up.setEnabled(e);
      this.mode_block_down.setEnabled(e);
      this.mode_block_left.setEnabled(e);
      this.mode_block_right.setEnabled(e);
      this.clearButton.setEnabled(e);
      this.mirrorHorizButton.setEnabled(e);
      this.mirrorVertButton.setEnabled(e);
   }

   private void initComponents() {
      this.jPanel11 = new JPanel();
      this.mode_block_up = new JButton();
      this.mode_block_down = new JButton();
      this.mode_block_left = new JButton();
      this.mode_block_right = new JButton();
      this.jLabel5 = new JLabel();
      this.clearButton = new JButton();
      this.jSeparator1 = new JSeparator();
      this.jPanel1 = new JPanel();
      this.mirrorHorizButton = new JButton();
      this.mirrorVertButton = new JButton();
      this.setBorder(BorderFactory.createTitledBorder("bewegen"));
      this.setLayout(new BorderLayout());
      this.jPanel11.setFont(new Font("Dialog", 0, 12));
      this.jPanel11.setLayout(new GridBagLayout());
      this.mode_block_up.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/arrow_up.png")));
      this.mode_block_up.setActionCommand("up");
      this.mode_block_up.setAlignmentY(0.0F);
      this.mode_block_up.setEnabled(false);
      this.mode_block_up.setFocusPainted(false);
      this.mode_block_up.setFocusable(false);
      this.mode_block_up.setMargin(new Insets(0, 0, 0, 0));
      this.mode_block_up.setMaximumSize(new Dimension(30, 30));
      this.mode_block_up.setMinimumSize(new Dimension(25, 25));
      this.mode_block_up.setPreferredSize(new Dimension(30, 30));
      this.mode_block_up.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            blockmovePanel.this.blockActionPerformed(evt);
         }
      });
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 15;
      this.jPanel11.add(this.mode_block_up, gridBagConstraints);
      this.mode_block_down.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/arrow_down.png")));
      this.mode_block_down.setActionCommand("down");
      this.mode_block_down.setAlignmentY(0.0F);
      this.mode_block_down.setEnabled(false);
      this.mode_block_down.setFocusPainted(false);
      this.mode_block_down.setFocusable(false);
      this.mode_block_down.setMargin(new Insets(0, 0, 0, 0));
      this.mode_block_down.setMaximumSize(new Dimension(30, 30));
      this.mode_block_down.setMinimumSize(new Dimension(25, 25));
      this.mode_block_down.setPreferredSize(new Dimension(30, 30));
      this.mode_block_down.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            blockmovePanel.this.blockActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 11;
      this.jPanel11.add(this.mode_block_down, gridBagConstraints);
      this.mode_block_left.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/arrow_left.png")));
      this.mode_block_left.setActionCommand("left");
      this.mode_block_left.setAlignmentY(0.0F);
      this.mode_block_left.setEnabled(false);
      this.mode_block_left.setFocusPainted(false);
      this.mode_block_left.setFocusable(false);
      this.mode_block_left.setMargin(new Insets(0, 0, 0, 0));
      this.mode_block_left.setMaximumSize(new Dimension(30, 30));
      this.mode_block_left.setMinimumSize(new Dimension(25, 25));
      this.mode_block_left.setPreferredSize(new Dimension(30, 30));
      this.mode_block_left.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            blockmovePanel.this.blockActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 13;
      this.jPanel11.add(this.mode_block_left, gridBagConstraints);
      this.mode_block_right.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/arrow_right.png")));
      this.mode_block_right.setActionCommand("right");
      this.mode_block_right.setAlignmentY(0.0F);
      this.mode_block_right.setEnabled(false);
      this.mode_block_right.setFocusPainted(false);
      this.mode_block_right.setFocusable(false);
      this.mode_block_right.setMargin(new Insets(0, 0, 0, 0));
      this.mode_block_right.setMaximumSize(new Dimension(30, 30));
      this.mode_block_right.setMinimumSize(new Dimension(25, 25));
      this.mode_block_right.setPreferredSize(new Dimension(30, 30));
      this.mode_block_right.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            blockmovePanel.this.blockActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 17;
      this.jPanel11.add(this.mode_block_right, gridBagConstraints);
      this.jLabel5.setText("             ");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridheight = 2;
      this.jPanel11.add(this.jLabel5, gridBagConstraints);
      this.clearButton.setText("Inhalt löschen");
      this.clearButton.setEnabled(false);
      this.clearButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            blockmovePanel.this.clearButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      this.jPanel11.add(this.clearButton, gridBagConstraints);
      this.jSeparator1.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = 3;
      this.jPanel11.add(this.jSeparator1, gridBagConstraints);
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("spiegeln"));
      this.jPanel1
         .setToolTipText(
            "<html><b>zu beachten</a>: ändert auch die Richtung links<->rechts bzw. hoch<->runter, was nicht bei allen Elementen eine Wirkung hat, z.B. Displays</html>"
         );
      this.jPanel1.setLayout(new GridLayout());
      this.mirrorHorizButton.setText("horizontal");
      this.mirrorHorizButton.setToolTipText("");
      this.mirrorHorizButton.setEnabled(false);
      this.mirrorHorizButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            blockmovePanel.this.mirrorHorizButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.mirrorHorizButton);
      this.mirrorVertButton.setText("vertikal");
      this.mirrorVertButton.setEnabled(false);
      this.mirrorVertButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            blockmovePanel.this.mirrorVertButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.mirrorVertButton);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 15;
      this.jPanel11.add(this.jPanel1, gridBagConstraints);
      this.add(this.jPanel11, "Center");
   }

   private void blockActionPerformed(ActionEvent evt) {
      if (evt.getActionCommand().equals("left")) {
         ((gecGBlockSelect)this.glbControl.getMode()).scrollLeft(true);
      } else if (evt.getActionCommand().equals("right")) {
         ((gecGBlockSelect)this.glbControl.getMode()).scrollRight(true);
      } else if (evt.getActionCommand().equals("up")) {
         ((gecGBlockSelect)this.glbControl.getMode()).scrollUp(true);
      } else if (evt.getActionCommand().equals("down")) {
         ((gecGBlockSelect)this.glbControl.getMode()).scrollDown(true);
      }
   }

   private void clearButtonActionPerformed(ActionEvent evt) {
      ((gecGBlockSelect)this.glbControl.getMode()).clearblock();
   }

   private void mirrorHorizButtonActionPerformed(ActionEvent evt) {
      ((gecGBlockSelect)this.glbControl.getMode()).mirrorHoriz();
   }

   private void mirrorVertButtonActionPerformed(ActionEvent evt) {
      ((gecGBlockSelect)this.glbControl.getMode()).mirrorVert();
   }
}
