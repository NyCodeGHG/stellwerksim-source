package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventHaeufigkeiten;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.panels.actionevents.stoerungMessureEvent;

public class stoerungMessDialog extends JDialog {
   private static stoerungMessDialog instance = null;
   private final gleisbildModelSts glbModel;
   private final stellwerk_editor my_main;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JScrollPane jScrollPane1;
   private JSeparator jSeparator2;
   private JProgressBar jobProgress;
   private JSpinner numSpinner;
   private JTable resultTable;
   private JButton startButton;

   static void openText(Window parent, stellwerk_editor e, gleisbildModelSts _my_gleisbild) {
      if (instance == null) {
         instance = new stoerungMessDialog(parent, e, _my_gleisbild);
         instance.setVisible(true);
      }
   }

   static void openText(JComponent parent, stellwerk_editor e, gleisbildModelSts _my_gleisbild) {
      if (instance == null) {
         instance = new stoerungMessDialog(SwingUtilities.windowForComponent(parent), e, _my_gleisbild);
         instance.setVisible(true);
      }
   }

   protected stoerungMessDialog(Window parent, stellwerk_editor e, gleisbildModelSts _glbModel) {
      super(parent);
      this.my_main = e;
      this.glbModel = _glbModel;
      this.initComponents();
      this.setSize(500, 450);
      this.setLocationRelativeTo(parent);
   }

   private void initComponents() {
      this.jPanel2 = new JPanel();
      this.jLabel1 = new JLabel();
      this.jScrollPane1 = new JScrollPane();
      this.resultTable = new JTable();
      this.jPanel1 = new JPanel();
      this.jLabel2 = new JLabel();
      this.numSpinner = new JSpinner();
      this.jSeparator2 = new JSeparator();
      this.jobProgress = new JProgressBar();
      this.startButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Störungshäufigkeit messen");
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            stoerungMessDialog.this.formWindowClosing(evt);
         }
      });
      this.jPanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanel2.setLayout(new BorderLayout(0, 5));
      this.jLabel1
         .setText(
            "<html>\nHier können nun für die konfigurierten Störungen mehrere Stunden Spielzeit simuliert werden um einen ungefähren Überblick zu bekommen, wie oft eine Störung auftritt. <br>\n<b>Achtung</b>: Da es sich um Zufälle handelt, können diese Werte auch schonmal etwas extrem ausschlagen! \nNegative Zahlen besagen, dass diese Störung keine Häufigkeit hat und somit immer auftritt.\n</html>"
         );
      this.jPanel2.add(this.jLabel1, "North");
      this.resultTable.setModel(new DefaultTableModel(new Object[0][], new String[]{"Name", "Häufigkeit", "Anzahl", "Zeiten"}) {
         Class[] types = new Class[]{String.class, String.class, Integer.class, String.class};
         boolean[] canEdit = new boolean[]{false, false, false, false};

         public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
         }

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
         }
      });
      this.resultTable.setAutoCreateRowSorter(true);
      this.jScrollPane1.setViewportView(this.resultTable);
      this.jPanel2.add(this.jScrollPane1, "Center");
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel2.setForeground(SystemColor.windowBorder);
      this.jLabel2.setText("Zeitraum (Minuten)");
      this.jLabel2
         .setToolTipText(
            "<html>\nAnzahl der Testminuten: Wieviele Minuten Spielzeit soll simuliert werden.<br>\n<b>Dies entspricht nicht der Dauer des Tests!</b>\n</html>"
         );
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      this.numSpinner.setModel(new SpinnerNumberModel(300, 60, null, 60));
      this.numSpinner
         .setToolTipText(
            "<html> Anzahl der Testminuten: Wieviele Minuten Spielzeit soll simuliert werden.<br> <b>Dies entspricht nicht der Dauer des Tests!</b> </html>"
         );
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 0.1;
      this.jPanel1.add(this.numSpinner, gridBagConstraints);
      this.jSeparator2.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 3;
      gridBagConstraints.insets = new Insets(0, 5, 0, 0);
      this.jPanel1.add(this.jSeparator2, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.jobProgress, gridBagConstraints);
      this.startButton.setText("Start");
      this.startButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stoerungMessDialog.this.startButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 1;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 5, 0, 5);
      this.jPanel1.add(this.startButton, gridBagConstraints);
      this.jPanel2.add(this.jPanel1, "South");
      this.getContentPane().add(this.jPanel2, "Center");
      this.pack();
   }

   private void formWindowClosing(WindowEvent evt) {
      instance = null;
   }

   private void startButtonActionPerformed(ActionEvent evt) {
      this.startButton.setEnabled(false);
      this.my_main.interPanelCom(new stoerungMessureEvent());
      int n = (Integer)this.numSpinner.getValue() * 60;
      stoerungMessDialog.mworker w = new stoerungMessDialog.mworker(n);
      this.jobProgress.setMinimum(0);
      this.jobProgress.setMaximum(n);
      this.jobProgress.setValue(0);
      ((DefaultTableModel)this.resultTable.getModel()).setRowCount(0);
      w.execute();
   }

   class mworker extends SwingWorker<HashMap<eventContainer, eventHaeufigkeiten.messureResult>, Integer> {
      private int cnt;
      eventHaeufigkeiten.messureUpdate mu = new eventHaeufigkeiten.messureUpdate() {
         @Override
         public void update(int n) {
            mworker.this.publish(new Integer[]{n});
         }
      };

      mworker(int c) {
         this.cnt = c;
      }

      public HashMap<eventContainer, eventHaeufigkeiten.messureResult> doInBackground() {
         return eventHaeufigkeiten.create(stoerungMessDialog.this.glbModel).messure(this.cnt, this.mu);
      }

      protected void process(List<Integer> chunks) {
         for (int number : chunks) {
            stoerungMessDialog.this.jobProgress.setValue(number);
         }
      }

      protected void done() {
         try {
            eventHaeufigkeiten eh = eventHaeufigkeiten.create(stoerungMessDialog.this.glbModel);
            DefaultTableModel m = (DefaultTableModel)stoerungMessDialog.this.resultTable.getModel();
            HashMap<eventContainer, eventHaeufigkeiten.messureResult> res = (HashMap<eventContainer, eventHaeufigkeiten.messureResult>)this.get();

            for (eventContainer ev : res.keySet()) {
               if (ev.getFactory().isRandom()) {
                  eventHaeufigkeiten.HAEUFIGKEITEN h = ev.getFactory().getOccurrence(ev);
                  eventHaeufigkeiten.messureResult v = (eventHaeufigkeiten.messureResult)res.get(ev);
                  String tt = "";

                  for (Long l : v.time) {
                     tt = tt + " " + l;
                  }

                  Object[] d = new Object[]{ev.getName(), eh.get(h), v.cnt, tt};
                  m.addRow(d);
               } else {
                  Object[] d = new Object[]{ev.getName(), "permanent"};
                  m.addRow(d);
               }
            }
         } catch (Exception var11) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "ignore", var11);
         }

         stoerungMessDialog.this.startButton.setEnabled(true);
      }
   }
}
