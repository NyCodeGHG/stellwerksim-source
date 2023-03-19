package js.java.isolate.statusapplet.players;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.adapter.AbstractTopFrame;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.clock.uhrComponent;

public class extractedView extends AbstractTopFrame implements SessionClose {
   private final extractedView.reverseExtract re;
   private final JPanel pan;
   private JSplitPane splitter;
   private JPanel timePanel;

   public extractedView(UserContext uc, JPanel pan, String hostname, extractedView.reverseExtract re, String titel, int instanz) {
      super(uc);
      this.re = re;
      this.pan = pan;
      this.initComponents();
      this.setTitle(titel);
      this.splitter.setTopComponent(pan);
      uhrComponent u1;
      this.timePanel.add(u1 = new uhrComponent("Sts Zeit", hostname, instanz));
      uhrComponent u2;
      this.timePanel.add(u2 = new uhrComponent("Realzeit"));
      this.pack();
      uc.addCloseObject(this);
      uc.addCloseObject(() -> {
         u1.finish();
         u2.finish();
      });
   }

   @Override
   public void close() {
      this.dispose();
   }

   private void initComponents() {
      this.splitter = new JSplitPane();
      this.timePanel = new JPanel();
      this.setDefaultCloseOperation(2);
      this.setLocationByPlatform(true);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            extractedView.this.formWindowClosing(evt);
         }
      });
      this.splitter.setOrientation(0);
      this.splitter.setResizeWeight(1.0);
      this.splitter.setOneTouchExpandable(true);
      this.timePanel.setLayout(new GridLayout(1, 0));
      this.splitter.setBottomComponent(this.timePanel);
      this.getContentPane().add(this.splitter, "Center");
      this.pack();
   }

   private void formWindowClosing(WindowEvent evt) {
      this.getContentPane().remove(this.pan);
      this.re.reverse(this.pan);
   }

   public interface reverseExtract {
      void reverse(JPanel var1);
   }
}
