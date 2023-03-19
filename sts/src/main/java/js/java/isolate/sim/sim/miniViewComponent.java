package js.java.isolate.sim.sim;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;

public class miniViewComponent extends JComponent {
   private gleisbildSimControl my_gleisbild;

   public miniViewComponent(gleisbildSimControl glb) {
      super();
      this.my_gleisbild = glb;
   }

   public void paint(Graphics g) {
      this.my_gleisbild.paint2mini((Graphics2D)g, 0, 0, this.getWidth(), this.getHeight());
   }
}
