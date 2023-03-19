package js.java.isolate.statusapplet.players;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.event.InternalFrameEvent;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.timesystem.timeSync;
import js.java.tools.gui.clock.bahnhofsUhr;
import js.java.tools.gui.clock.timerecipient;
import js.java.tools.gui.clock.bahnhofsUhr.timeDeliverer;

public class clockIFrame extends JFrame implements timeDeliverer, timerecipient, SessionClose {
   private oneInstance instance;
   private timeSync tsync = null;
   final UserContext uc;
   private long timeOffset = 0L;

   public clockIFrame(oneInstance cls, UserContext uc) {
      super();
      this.instance = cls;
      this.uc = uc;

      try {
         this.tsync = new timeSync(uc.getParameter(UserContextMini.DATATYPE.TIMESERVER), this.instance.getInstanz(), this);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      this.tsync.sync();
      uc.addCloseObject(this.tsync);
      this.initComponents();
      this.setTitle("Zeit");
      this.add(new bahnhofsUhr(this, "STS 24 Time", true), "Center");
      this.pack();
      this.setSize(200, 200);
   }

   private void setTime(long t, bahnhofsUhr u) {
      int s = (int)(t / 1000L % 60L);
      int m = (int)(t / 60000L % 60L);
      int h = (int)(t / 3600000L);
      u.setTime(h, m, s);
   }

   public void timeQuery(bahnhofsUhr u) {
      long t = System.currentTimeMillis() - this.timeOffset;
      this.setTime(t, u);
   }

   public void timeChange(long offsetToLocal, short tagescode, int latency) {
      this.timeOffset = offsetToLocal;
   }

   @Override
   public void close() {
      this.dispose();
   }

   private void initComponents() {
      this.setLocationByPlatform(true);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            clockIFrame.this.formWindowClosing(evt);
         }
      });
      this.pack();
   }

   private void formInternalFrameClosing(InternalFrameEvent evt) {
      this.tsync.stop();
   }

   private void formWindowClosing(WindowEvent evt) {
      this.tsync.stop();
   }
}
