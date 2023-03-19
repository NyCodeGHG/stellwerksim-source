package js.java.isolate.sim.sim;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window.Type;
import javax.swing.JDialog;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.gui.clock.bahnhofsUhr;
import js.java.tools.gui.clock.bahnhofsUhr.timeDeliverer;

public class analogUhr extends JDialog implements timeDeliverer {
   private final stellwerksim_main my_main;

   public analogUhr(stellwerksim_main parent, boolean twentyFourMode) {
      super(parent, false);
      this.my_main = parent;
      this.initComponents();
      this.add(new bahnhofsUhr(this, "Sts", twentyFourMode), "Center");
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
   }

   private void initComponents() {
      this.setDefaultCloseOperation(2);
      this.setTitle("Uhr");
      this.setCursor(new Cursor(0));
      this.setLocationByPlatform(true);
      this.setMinimumSize(new Dimension(140, 180));
      this.setType(Type.UTILITY);
      this.pack();
   }

   public void timeQuery(bahnhofsUhr u) {
      if (!this.my_main.isPause()) {
         int h = (int)(this.my_main.getSimutime() / 3600000L);
         int m = (int)(this.my_main.getSimutime() / 60000L % 60L);
         int s = (int)(this.my_main.getSimutime() / 1000L % 60L);
         u.setTime(h, m, s);
      }
   }
}
