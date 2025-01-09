package js.java.isolate.sim.eventsys.events;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import js.java.tools.gui.speedometer.ArcNeedlePainter;
import js.java.tools.gui.speedometer.SpeedometerPanel;

class levelDisplay extends JDialog implements ActionListener, Runnable {
   private Timer timer;
   private static final int MAX = 200;
   private int maxlevel;
   private int con = 0;
   private SpeedometerPanel levelPanel;
   private stellwerkausfall ausfall;

   levelDisplay(Frame parent, stellwerkausfall ausfall) {
      super(parent, false);
      this.ausfall = ausfall;
      this.maxlevel = 200;
      this.initComponents();
      this.levelPanel = new SpeedometerPanel(0, 2);
      this.levelPanel.setText("Hilfsstrom (kap)");
      this.levelPanel.setPaintLabels(false);
      this.levelPanel.setMaxValue(this.maxlevel);
      this.levelPanel.setValue(0, (double)this.maxlevel);
      this.levelPanel.setValue(1, (double)(this.maxlevel / 2));
      this.levelPanel.setNeedlePainter(1, new ArcNeedlePainter());
      this.add(this.levelPanel, "Center");
      this.timer = new Timer(180, this);
      this.timer.start();
      this.setVisible(true);
   }

   public void hide() {
      this.timer.stop();
      super.hide();
   }

   public void setLevel(int l) {
      this.maxlevel = 200 * l / 100;
   }

   int getLevel() {
      return (int)(this.levelPanel.getValue() * 100.0 / 200.0);
   }

   public void addConsumer(levelDisplay.CONSUMENT c) {
      switch (c) {
         case LIGHT:
            this.con = 3;
            break;
         case MEDIUM:
            this.con = 11;
            break;
         case HEAVY:
            this.con = 20;
      }

      SwingUtilities.invokeLater(this);
   }

   public void run() {
      this.levelPanel.setValue(Math.max(0.0, this.levelPanel.getValue() - (double)this.con));
   }

   public void actionPerformed(ActionEvent e) {
      double v = this.levelPanel.getValue();
      if (v < (double)this.maxlevel) {
         this.levelPanel.setValue(v + 1.0);
      } else if (v > (double)this.maxlevel) {
         this.levelPanel.setValue(v - 1.0);
      }

      try {
         this.ausfall.levelReached((int)(v * 100.0 / 200.0), v < 100.0);
      } catch (Exception var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "catch: levelReached", var5);
      }
   }

   private void initComponents() {
      this.setDefaultCloseOperation(0);
      this.setTitle("");
      this.setCursor(new Cursor(3));
      this.setMinimumSize(new Dimension(180, 120));
      this.pack();
   }

   static enum CONSUMENT {
      LIGHT,
      MEDIUM,
      HEAVY;
   }
}
