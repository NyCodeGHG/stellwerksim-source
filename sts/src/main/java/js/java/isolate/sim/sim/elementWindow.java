package js.java.isolate.sim.sim;

import java.awt.Dimension;
import java.awt.Window.Type;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.CoordinatesEvent;
import js.java.tools.actions.AbstractListener;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.gui.border.DropShadowBorder;

public class elementWindow extends JDialog implements AbstractListener<CoordinatesEvent> {
   private JCheckBoxMenuItem elementWindowMenu;
   private JLabel elementLabel;

   elementWindow(stellwerksim_main parent, JCheckBoxMenuItem elementWindowMenu) {
      super(parent, false);
      this.elementWindowMenu = elementWindowMenu;
      this.initComponents();
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION);
      this.setVisible(true);
   }

   private void initComponents() {
      this.elementLabel = new JLabel();
      this.setDefaultCloseOperation(0);
      this.setTitle("Elementname");
      this.setAlwaysOnTop(true);
      this.setFocusable(false);
      this.setFocusableWindowState(false);
      this.setLocationByPlatform(true);
      this.setMinimumSize(new Dimension(100, 80));
      this.setPreferredSize(new Dimension(100, 80));
      this.setResizable(false);
      this.setType(Type.UTILITY);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            elementWindow.this.formWindowClosing(evt);
         }
      });
      this.elementLabel.setFont(this.elementLabel.getFont().deriveFont((float)this.elementLabel.getFont().getSize() + 5.0F));
      this.elementLabel.setHorizontalAlignment(0);
      this.elementLabel.setBorder(new DropShadowBorder(true, true, true, true));
      this.elementLabel.setMaximumSize(new Dimension(500, 25));
      this.elementLabel.setMinimumSize(new Dimension(50, 15));
      this.elementLabel.setPreferredSize(new Dimension(50, 30));
      this.getContentPane().add(this.elementLabel, "Center");
      this.pack();
   }

   private void formWindowClosing(WindowEvent evt) {
      this.elementWindowMenu.setSelected(false);
   }

   private void showCoords(gleis gl) {
      if (gl != null && gl.typHasElementName()) {
         this.elementLabel.setText(gl.getElementName());
      } else {
         this.elementLabel.setText("");
      }
   }

   public void action(CoordinatesEvent e) {
      this.showCoords(e.getGleis());
   }
}
