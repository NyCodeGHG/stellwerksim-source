package js.java.isolate.sim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.timesystem.timedelivery;
import js.java.tools.gui.textticker;

public class Timer implements ActionListener, SessionClose {
   private final JTextField zeitFeld;
   private final textticker textFeld;
   private final timedelivery timed;
   private final javax.swing.Timer tickTimer = new javax.swing.Timer(500, this);

   public Timer(timedelivery tm, JTextField t, textticker f) {
      super();
      this.timed = tm;
      this.zeitFeld = t;
      this.textFeld = f;
      this.tickTimer.start();
   }

   public void actionPerformed(ActionEvent ae) {
      if (this.timed.isPause()) {
         this.zeitFeld.setText("*Pause*");
      } else {
         this.zeitFeld.setText(this.timed.getSimutimeString());
      }
   }

   public void end() {
      this.tickTimer.stop();
   }

   public void addText(String t) {
      this.textFeld.addText(t);
   }

   public void addText(String t, boolean hint) {
      this.textFeld.addText(t, hint);
   }

   public void addImportantText(String t) {
      this.textFeld.addImportantText(t);
   }

   @Override
   public void close() {
      SwingUtilities.invokeLater(() -> this.tickTimer.stop());
   }
}
