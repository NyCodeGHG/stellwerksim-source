package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.dtest.dtestresult;
import js.java.isolate.sim.dtest.tester;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.panels.actionevents.dtestresultEvent;
import js.java.schaltungen.moduleapi.SessionClose;

public class designtestCtrlPanel extends basePanel implements ActionListener, SessionClose {
   private static final int RUNDELAY = 10;
   private final tester.callback foregroundCallback = new tester.callback() {
      @Override
      public void setGUIEnable(boolean b) {
         designtestCtrlPanel.this.my_main.setGUIEnable(b);
      }

      @Override
      public void setProgress(int i) {
         designtestCtrlPanel.this.my_main.setProgress(i);
      }

      @Override
      public void showStatus(String text, int message) {
         designtestCtrlPanel.this.my_main.showStatus(text, message);
      }
   };
   private final tester.callback backgroundCallback = new tester.callback() {
      @Override
      public void setGUIEnable(boolean b) {
      }

      @Override
      public void setProgress(int i) {
      }

      @Override
      public void showStatus(String text, int message) {
         designtestCtrlPanel.this.my_main.showStatus(text, message);
      }
   };
   private boolean shown = false;
   private final Timer bgRunner = new Timer(600000, this);
   private JLabel freetext;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JButton startButton;

   public designtestCtrlPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      this.bgRunner.start();
   }

   @Override
   public void close() {
      this.bgRunner.stop();
   }

   @Override
   public void shown(String n, gecBase gec) {
      this.shown = true;
      this.bgRunner.stop();
   }

   @Override
   public void hidden(gecBase gec) {
      this.shown = false;
      this.bgRunner.start();
   }

   public void actionPerformed(ActionEvent e) {
      if (!this.shown) {
         this.runtest(this.backgroundCallback);
      }
   }

   private void runtest(final tester.callback cb) {
      SwingWorker<TreeSet<dtestresult>, Integer> w = new SwingWorker<TreeSet<dtestresult>, Integer>() {
         tester tt;

         protected TreeSet<dtestresult> doInBackground() throws Exception {
            this.tt = new tester(cb, designtestCtrlPanel.this.glbControl.getModel());
            return this.tt.runTest();
         }

         protected void done() {
            try {
               TreeSet<dtestresult> res = (TreeSet<dtestresult>)this.get();
               dtestresultEvent e = new dtestresultEvent(res);
               designtestCtrlPanel.this.my_main.interPanelCom(e);
               designtestCtrlPanel.this.freetext
                  .setText("<html>Durchgeführte Tests: " + this.tt.testList() + "<br>Gefundene Meldungen: " + res.size() + "</html>");
               String warning = null;
               int rank = 0;

               for (dtestresult d : e.getResults()) {
                  if (d.getRank() == 2) {
                     warning = "Es gibt mindestens eine Fehlermeldung in den Designtests.";
                     rank = 2;
                     break;
                  }

                  if (d.getRank() == 1) {
                     warning = "Es gibt mindestens eine Warnung in den Designtests.";
                     rank = 1;
                  }
               }

               designtestCtrlPanel.this.my_main.setWarning(warning, rank);
            } catch (Exception var7) {
            }
         }
      };
      w.execute();
   }

   private void initComponents() {
      this.startButton = new JButton();
      this.jPanel1 = new JPanel();
      this.jLabel2 = new JLabel();
      this.jLabel3 = new JLabel();
      this.jLabel4 = new JLabel();
      this.jPanel2 = new JPanel();
      this.jLabel1 = new JLabel();
      this.freetext = new JLabel();
      this.setBorder(BorderFactory.createTitledBorder("Designtest"));
      this.setLayout(new BorderLayout());
      this.startButton.setText("starten");
      this.startButton.setMargin(new Insets(14, 14, 14, 14));
      this.startButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            designtestCtrlPanel.this.startButtonActionPerformed(evt);
         }
      });
      this.add(this.startButton, "South");
      this.jPanel1.setLayout(new BoxLayout(this.jPanel1, 3));
      this.jLabel2.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/info16.png")));
      this.jLabel2.setText("Hinweis");
      this.jPanel1.add(this.jLabel2);
      this.jLabel3.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/warning16.png")));
      this.jLabel3.setText("Warnung");
      this.jPanel1.add(this.jLabel3);
      this.jLabel4.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/error16.png")));
      this.jLabel4.setText("Fehler");
      this.jPanel1.add(this.jLabel4);
      this.add(this.jPanel1, "East");
      this.jPanel2.setLayout(new BorderLayout());
      this.jLabel1.setFont(this.jLabel1.getFont().deriveFont((float)this.jLabel1.getFont().getSize() - 2.0F));
      this.jLabel1
         .setText(
            "<html>Bei dem Test werden einige grundlegende und typische Fehler überprüft und darauf hingewiesen. Ausserdem werden zusätzliche Empfehlungen gegeben, die keine Fehler sind, aber zu Problemen führen können. Der Test wird ebenfalls automatisch alle 10 Minuten ausgeführt.</html>"
         );
      this.jLabel1.setVerticalAlignment(1);
      this.jPanel2.add(this.jLabel1, "North");
      this.freetext.setFont(new Font("DialogInput", 0, 11));
      this.jPanel2.add(this.freetext, "Center");
      this.add(this.jPanel2, "Center");
   }

   private void startButtonActionPerformed(ActionEvent evt) {
      this.my_main.setGUIEnable(false);
      this.runtest(this.foregroundCallback);
   }
}
