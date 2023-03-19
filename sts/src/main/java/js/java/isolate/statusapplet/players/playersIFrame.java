package js.java.isolate.statusapplet.players;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;
import js.java.schaltungen.moduleapi.SessionClose;

public class playersIFrame extends JFrame implements SessionClose {
   private final playersIFrame.closeIFrame closeHook;

   public playersIFrame(String titel, JPanel pan, playersIFrame.closeIFrame cls) {
      super();
      this.closeHook = cls;
      this.initComponents();
      this.setContentPane(pan);
      this.setTitle(titel);
      this.pack();
      this.setSize(600, 300);
   }

   @Override
   public void close() {
      this.dispose();
   }

   private void initComponents() {
      this.setLocationByPlatform(true);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            playersIFrame.this.formWindowClosing(evt);
         }
      });
      this.getContentPane().setLayout(null);
      this.pack();
   }

   private void formInternalFrameClosed(InternalFrameEvent evt) {
      try {
         this.closeHook.onClose();
      } catch (Exception var3) {
      }
   }

   private void formWindowClosing(WindowEvent evt) {
      try {
         this.closeHook.onClose();
      } catch (Exception var3) {
      }
   }

   public interface closeIFrame {
      void onClose();
   }
}
