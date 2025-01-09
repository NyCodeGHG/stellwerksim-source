package js.java.isolate.statusapplet.karte;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import js.java.tools.gui.speedometer.ArcNeedlePainter;
import js.java.tools.gui.speedometer.LineNeedlePainter;
import js.java.tools.gui.speedometer.SpeedometerPanel;

public class statisticsPanel extends JPanel {
   private final SpeedometerPanel verspätungSchnitt;
   private final SpeedometerPanel heat;

   public statisticsPanel() {
      super(new BorderLayout());
      this.setBorder(new LineBorder(Color.BLACK, 1));
      this.verspätungSchnitt = new SpeedometerPanel(0, 3);
      this.verspätungSchnitt.setMaxValue(5);
      this.verspätungSchnitt.setAutoMax(true);
      this.verspätungSchnitt.setText("Verspätungsschnitt\nmax\nmin>0");
      this.verspätungSchnitt.setPreferredSize(new Dimension(120, 120));
      this.verspätungSchnitt.setNeedlePainter(1, new ArcNeedlePainter());
      this.verspätungSchnitt.setNeedlePainter(2, new LineNeedlePainter());
      this.add(this.verspätungSchnitt, "Center");
      this.heat = new SpeedometerPanel(1, 3);
      this.heat.setMaxValue(5);
      this.heat.setAutoMax(true);
      this.heat.setText("Hitze Schnitt\nmax\nmin");
      this.heat.setPaintLabels(false);
      this.heat.setPreferredSize(new Dimension(120, 120));
      this.add(this.heat, "South");
   }

   void updateDelayStats(zugListPanel kp) {
      double[] v = kp.verspätungsDurchschnitt();

      for (int i = 0; i < v.length; i++) {
         this.verspätungSchnitt.setValue(i, v[i]);
      }
   }

   void updateHeatStats(long sum, long minHeat, long maxHeat) {
      this.heat.setValue(0, (double)sum);
      this.heat.setValue(2, (double)minHeat);
      this.heat.setValue(1, (double)maxHeat);
   }
}
