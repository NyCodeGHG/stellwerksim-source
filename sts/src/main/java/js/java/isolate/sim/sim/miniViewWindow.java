package js.java.isolate.sim.sim;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window.Type;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class miniViewWindow extends JDialog implements gleisbildSimControl.paintEventListener {
   private static volatile miniViewWindow instance = null;
   private gleisbildSimControl glbControl;
   private miniViewComponent viewer;

   public static void createInstance(stellwerksim_main parent, gleisbildSimControl my_gleisbild) {
      if (instance == null) {
         instance = new miniViewWindow(parent, my_gleisbild);
         instance.setVisible(true);
      }
   }

   protected miniViewWindow(Frame parent, gleisbildSimControl glb) {
      super(parent, false);
      this.glbControl = glb;
      this.initComponents();
      this.viewer = new miniViewComponent(this.glbControl);
      this.add(this.viewer, "Center");
      this.glbControl.addPainterListener(this);
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
   }

   private void initComponents() {
      this.setDefaultCloseOperation(2);
      this.setTitle("Gleisbild Miniaturansicht");
      this.setCursor(new Cursor(3));
      this.setFocusCycleRoot(false);
      this.setFocusable(false);
      this.setFocusableWindowState(false);
      this.setLocationByPlatform(true);
      this.setMinimumSize(new Dimension(150, 150));
      this.setType(Type.UTILITY);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            miniViewWindow.this.formWindowClosed(evt);
         }
      });
      this.pack();
   }

   private void formWindowClosed(WindowEvent evt) {
      this.glbControl.removePainterListener(this);
      instance = null;
   }

   @Override
   public void paintEvent() {
      this.viewer.repaint();
   }
}
