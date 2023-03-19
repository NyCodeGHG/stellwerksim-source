package js.java.isolate.sim.eventsys.events;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import js.java.tools.gui.speedometer.ArcNeedlePainter;
import js.java.tools.gui.speedometer.SpeedometerPanel;

class levelDisplayV1 extends JDialog implements ActionListener, Runnable {
   private Timer timer;
   private int maxlevel;
   private int con = 0;
   private SpeedometerPanel levelPanel;

   levelDisplayV1(Frame parent) {
      super(parent, false);
      this.initComponents();
      this.levelPanel = new SpeedometerPanel(0, 2);
      this.levelPanel.setText("Hilfsstrom\nWeichenmin.");
      this.levelPanel.setPaintLabels(false);
      this.levelPanel.setMaxValue(1000);
      this.levelPanel.setValue(0, 990.0);
      this.levelPanel.setValue(1, 300.0);
      this.levelPanel.setNeedlePainter(1, new ArcNeedlePainter());
      this.add(this.levelPanel, "Center");
      this.maxlevel = 1000;
      this.timer = new Timer(80, this);
      this.timer.start();
      this.setVisible(true);
   }

   public void hide() {
      this.timer.stop();
      super.hide();
   }

   public void setLevel(int l) {
      this.maxlevel = (20 - l) * 50;
   }

   public void addConsumer(boolean heavy) {
      this.con = heavy ? 80 : 20;
      SwingUtilities.invokeLater(this);
   }

   public void run() {
      this.levelPanel.setValue(this.levelPanel.getValue() - (double)this.con);
   }

   public void actionPerformed(ActionEvent e) {
      double v = this.levelPanel.getValue();
      if (v < (double)this.maxlevel) {
         this.levelPanel.setValue(v + 1.0);
      } else if (v > (double)this.maxlevel) {
         this.levelPanel.setValue(v - 1.0);
      }
   }

   public static final void main(String[] argv) {
      try {
         SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
               final levelDisplayV1 d = new levelDisplayV1(new Frame());
               Timer t = new Timer(10000, new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                     d.setLevel((int)(Math.random() * 20.0));
                  }
               });
               t.start();
               d.show();
            }
         });
      } catch (InvocationTargetException | InterruptedException var2) {
      }
   }

   private void initComponents() {
      this.setDefaultCloseOperation(0);
      this.setTitle("");
      this.setCursor(new Cursor(3));
      this.setMinimumSize(new Dimension(180, 120));
      this.pack();
   }
}
