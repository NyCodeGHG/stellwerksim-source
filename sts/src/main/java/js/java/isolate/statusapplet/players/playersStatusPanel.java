package js.java.isolate.statusapplet.players;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.speedometer.ArcNeedlePainter;
import js.java.tools.gui.speedometer.ClassicNeedlePainter;
import js.java.tools.gui.speedometer.LineNeedlePainter;
import js.java.tools.gui.speedometer.SpeedometerPanel;

public class playersStatusPanel extends JPanel implements ircupdate, ActionListener, SessionClose {
   private final oneInstance my_main;
   private final playersPanel kp;
   private SpeedometerPanel verspätungSchnitt;
   private SpeedometerPanel heatValue;
   private SpeedometerPanel spielerCount;
   private final SpeedometerPanel zug5minStat;
   private SpeedometerPanel sichtbarCount;
   private final SpeedometerPanel spieldauerSchnitt;
   private JSlider sl;
   private final boolean testMode = false;
   private int seenMax = 0;
   private HashSet<Integer> seen5 = new HashSet();
   private HashSet<Integer> seenNow = new HashSet();
   private long seenTime = 0L;
   private final Timer seenTimer = new Timer(60000, this);
   private double spieldauerTotalmedian = 0.0;
   private double spieldauerTotalmax = 0.0;

   public playersStatusPanel(oneInstance m, playersPanel kp) {
      super();
      this.my_main = m;
      this.kp = kp;
      kp.registerHook(this);
      this.setLayout(new GridLayout(0, 3));
      this.verspätungSchnitt = new SpeedometerPanel(1, 2);
      this.verspätungSchnitt.setMaxValue(20);
      this.verspätungSchnitt.setAutoMax(true);
      this.verspätungSchnitt.setText("Durchschnittsverspätung\nMedian");
      this.add(this.verspätungSchnitt);
      this.heatValue = new SpeedometerPanel(1, 4);
      this.heatValue.setMaxValue(10);
      this.heatValue.setText("Hitze\nMedian\nobere 25%");
      this.heatValue.setAutoMax(true);
      this.heatValue.setPaintLabels(false);
      this.heatValue.setNeedlePainter(2, new ClassicNeedlePainter(1.5));
      this.heatValue.setNeedlePainter(3, new LineNeedlePainter());
      this.add(this.heatValue);
      this.spielerCount = new SpeedometerPanel(0, 2);
      this.spielerCount.setMaxValue(20);
      this.spielerCount.setText("Spieler in der Instanz\ndavon Stitz");
      this.spielerCount.setAutoMax(true);
      this.spielerCount.setNeedlePainter(1, new ArcNeedlePainter());
      this.add(this.spielerCount);
      this.sichtbarCount = new SpeedometerPanel(0, 3);
      this.sichtbarCount.setMaxValue(50);
      this.sichtbarCount.setText("sichtbare Züge\nletzten Halt passiert\ndavon Templates");
      this.sichtbarCount.setAutoMax(true);
      this.add(this.sichtbarCount);
      this.spieldauerSchnitt = new SpeedometerPanel(0, 4);
      this.spieldauerSchnitt.setMaxValue(10);
      this.spieldauerSchnitt.setText("Spieldauer Median (Minuten)\nSpieldauer max\ntotal median\ntotal max");
      this.spieldauerSchnitt.setAutoMax(true);
      this.spieldauerSchnitt.setNeedlePainter(2, new ArcNeedlePainter(0));
      this.spieldauerSchnitt.setNeedlePainter(3, new ArcNeedlePainter(1));
      this.add(this.spieldauerSchnitt);
      this.zug5minStat = new SpeedometerPanel(0, 3);
      this.zug5minStat.setMaxValue(1000);
      this.zug5minStat.setPaintLabels(false);
      this.zug5minStat.setText("5 Minuten Status\naktuell\nmax");
      this.zug5minStat.setAutoMax(true);
      this.zug5minStat.setNeedlePainter(2, new ArcNeedlePainter());
      this.add(this.zug5minStat);
      this.seenTimer.start();
      this.updateAid(null);
   }

   @Override
   public void close() {
      this.kp.unregisterHook(this);
      this.seenTimer.stop();
   }

   @Override
   public void updateAid(players_aid d) {
      this.spielerCount.setValue(0, (double)this.kp.numberOfSpieler());
      this.spielerCount.setValue(1, (double)this.kp.numberOfStitz());
      this.updateZug(null);
   }

   @Override
   public void updateZug(players_zug z) {
      this.sichtbarCount.setValue(0, (double)this.kp.numberOfSichtbar());
      this.sichtbarCount.setValue(1, (double)this.kp.numberOfAusfahrt());
      this.sichtbarCount.setValue(2, (double)this.kp.numberOfTemplates());
      double[] v = this.kp.verspätungsDurchschnitt();
      this.verspätungSchnitt.setValue(1, v[0]);
      this.verspätungSchnitt.setValue(0, v[1]);
      int[] vi = this.kp.heatValue();
      this.heatValue.setValue(0, (double)vi[0]);
      this.heatValue.setValue(1, (double)vi[1]);
      this.heatValue.setValue(2, (double)vi[2]);
      this.heatValue.setValue(3, (double)vi[3]);
      if (z != null) {
         this.seenNow.add(z.zid);
         this.zug5minStat.setValue(1, (double)this.seenNow.size());
      }
   }

   public void actionPerformed(ActionEvent e) {
      long[] sd = this.kp.spieldauer();
      this.spieldauerTotalmedian = Math.max(this.spieldauerTotalmedian, (double)(sd[0] / 1000L) / 60.0);
      this.spieldauerTotalmax = Math.max(this.spieldauerTotalmax, (double)(sd[1] / 1000L) / 60.0);
      this.spieldauerSchnitt.setValue(0, (double)(sd[0] / 1000L) / 60.0);
      this.spieldauerSchnitt.setValue(1, (double)(sd[1] / 1000L) / 60.0);
      this.spieldauerSchnitt.setValue(2, this.spieldauerTotalmedian);
      this.spieldauerSchnitt.setValue(3, this.spieldauerTotalmax);
      long t = System.currentTimeMillis();
      if (t > this.seenTime) {
         this.seenTime = t + 300000L;
         this.seen5.clear();
         this.seen5 = this.seenNow;
         this.seenNow = new HashSet();
         this.seenMax = Math.max(this.seenMax, this.seen5.size());
         this.zug5minStat.setValue(0, (double)this.seen5.size());
         this.zug5minStat.setValue(1, (double)this.seenNow.size());
         this.zug5minStat.setValue(2, (double)this.seenMax);
         if (this.seenMax > this.seen5.size()) {
            this.seenMax -= this.seenMax / 4;
         }
      }
   }
}
