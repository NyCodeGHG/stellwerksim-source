package js.java.isolate.sim.zug;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JTable.PrintMode;
import javax.swing.table.DefaultTableModel;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.eventmsg;
import js.java.isolate.sim.eventsys.zugmsg;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildModelEventsys;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.schaltungen.timesystem.TimeFormat;
import js.java.tools.gui.speedometer.ArcNeedlePainter;
import js.java.tools.gui.speedometer.SpeedometerPanel;

public class zugMeasure extends JDialog implements ActionListener {
   private final zug z;
   private zugMeasure.hookCall hc = null;
   private long starttime = 0L;
   private long lasttime = 0L;
   private long bahnsteigtime = 0L;
   private long signaltime = 0L;
   private final Simulator my_main;
   private final gleisbildModelEventsys my_gleisbild;
   private final TimeFormat df;
   private final JPanel speedPanel;
   private final SpeedometerPanel speed1;
   private final SpeedometerPanel speed2;
   private final Timer tm = new Timer(400, this);
   private double lastIst = 0.0;
   private gleis lastGl = null;
   private JTable dataTable;
   private JScrollPane jScrollPane1;
   private JButton printButton;
   private JTabbedPane tabPane;

   public void actionPerformed(ActionEvent e) {
      this.speed1.setValue(0, this.z.ist_tempo);
      this.speed1.setValue(1, (double)this.z.soll_tempo);
      this.speed1.setValue(2, this.z.calc_tempo());
      this.speed2.setValue(0, this.z.getLastCalcTempo() * 10.0);
      this.speed2.setValue(1, this.z.getHaltabstand());
      this.speed2.setValue(2, (this.z.ist_tempo - this.lastIst) * 10.0);
      if (this.lastIst != this.z.ist_tempo) {
         this.lastIst = this.z.ist_tempo;
      }
   }

   public zugMeasure(zug _z, stellwerksim_main m, gleisbildModelEventsys b) {
      super(m, false);
      this.z = _z;
      this.my_main = m;
      this.my_gleisbild = b;
      this.initComponents();
      this.speedPanel = new JPanel();
      this.speedPanel.setLayout(new GridLayout(1, 0));
      this.speed1 = new SpeedometerPanel(0, 3);
      this.speed1.setMaxValue(20);
      this.speed1.setPaintLabels(true);
      this.speed1.setText("ist\nmax\nsoll");
      this.speed1.setNeedlePainter(1, new ArcNeedlePainter(1));
      this.speedPanel.add(this.speed1);
      this.speed2 = new SpeedometerPanel(1, 3);
      this.speed2.setMaxValue(10);
      this.speed2.setPaintLabels(true);
      this.speed2.setText("delta\nAbst\ncdelta");
      this.speed2.setNeedlePainter(1, new ArcNeedlePainter(1));
      this.speedPanel.add(this.speed2);
      this.tabPane.add(this.speedPanel, "Spd");
      this.setTitle(this.z.getName() + " (" + this.z.getZID() + ")");
      this.df = TimeFormat.getInstance(TimeFormat.STYLE.HMS);
      this.installMeasure();
      this.setSize(300, 150);
      this.setLocationRelativeTo(m);
      this.tm.start();
   }

   public zug getZug() {
      return this.z;
   }

   private void installMeasure() {
      this.hc = new zugMeasure.hookCall(this.my_main);
   }

   private void abfahrt(gleis g) {
      this.addLine("Abfahrt " + g.getSWWert(), true);
      this.lastGl = g;
   }

   private void fahrt(gleis g, gleis before_gl) {
      if (this.starttime == 0L) {
         this.starttime = this.my_main.getSimutime();
         this.lasttime = this.starttime;
         this.bahnsteigtime = 0L;
         this.signaltime = 0L;
         this.addLine("Start " + before_gl.getSWWert() + " (ENR " + before_gl.getENR() + ")", false);
      }

      element e = g.getElement();
      if (gleis.ALLE_BAHNSTEIGE.matches(e)) {
         if (g != this.lastGl && g.forUs(before_gl)) {
            this.addLine("Halt " + g.getSWWert(), true);
         }
      } else if (e == gleis.ELEMENT_AUSFAHRT) {
         if (g != this.lastGl) {
            this.addLine("Ausfahrt " + g.getSWWert() + " (ENR " + g.getENR() + ")", true);
         }
      } else if (e == gleis.ELEMENT_ÜBERGABEPUNKT) {
         if (g != this.lastGl && g.forUs(before_gl)) {
            gleis g2 = this.my_gleisbild.findFirst(new Object[]{g.getENR(), gleis.ELEMENT_AUSFAHRT});
            if (g2 != null) {
               this.addLine("ÜP-Ausfahrt " + g2.getSWWert() + " (ENR " + g.getENR() + ")", true);
            } else {
               this.addLine("ÜP-Ausfahrt (ENR " + g.getENR() + ") passende Ausfahrt nicht gefunden!", true);
            }
         }
      } else if (gleis.ALLE_SIGNALE.matches(e) && g.forUs(before_gl)) {
         this.addLine("Signal " + g.getElementName() + " (ENR " + g.getENR() + ")", false);
      }
   }

   private void addLine(String ort, boolean bst) {
      long current = this.my_main.getSimutime();
      SwingUtilities.invokeLater(
         new zugMeasure.addRunner(
            ort,
            current - this.lasttime,
            current - this.starttime,
            this.signaltime > 0L ? current - this.signaltime : -1L,
            this.bahnsteigtime > 0L ? current - this.bahnsteigtime : -1L,
            current,
            bst
         )
      );
      this.lasttime = current;
      if (bst) {
         this.bahnsteigtime = current;
      } else {
         this.signaltime = current;
      }
   }

   public void close() {
      this.tm.stop();
      this.setVisible(false);
   }

   private void initComponents() {
      this.tabPane = new JTabbedPane();
      this.jScrollPane1 = new JScrollPane();
      this.dataTable = new JTable();
      this.printButton = new JButton();
      this.addComponentListener(new ComponentAdapter() {
         public void componentHidden(ComponentEvent evt) {
            zugMeasure.this.formComponentHidden(evt);
         }

         public void componentShown(ComponentEvent evt) {
            zugMeasure.this.formComponentShown(evt);
         }
      });
      this.tabPane.setTabPlacement(2);
      this.tabPane.setFocusable(false);
      this.dataTable
         .setModel(new DefaultTableModel(new Object[0][], new String[]{"Ort", "Fahrdauer", "seit Signal", "seit Bahnsteig", "Gesamtzeit", "Simzeit"}) {
            Class[] types = new Class[]{String.class, String.class, String.class, String.class, String.class, String.class};
            boolean[] canEdit = new boolean[]{false, false, false, false, false, false};

            public Class getColumnClass(int columnIndex) {
               return this.types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
               return this.canEdit[columnIndex];
            }
         });
      this.jScrollPane1.setViewportView(this.dataTable);
      this.tabPane.addTab("Zeit", this.jScrollPane1);
      this.getContentPane().add(this.tabPane, "Center");
      this.printButton.setText("drucken");
      this.printButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            zugMeasure.this.printButtonActionPerformed(evt);
         }
      });
      this.getContentPane().add(this.printButton, "South");
      this.pack();
   }

   private void printButtonActionPerformed(ActionEvent evt) {
      try {
         MessageFormat header = new MessageFormat(this.z.getName() + " (" + this.z.getZID() + ")");
         MessageFormat footer = null;
         this.dataTable.print(PrintMode.FIT_WIDTH, header, footer);
      } catch (PrinterException var4) {
         System.out.println("drucken geht nicht");
         JOptionPane.showMessageDialog(this.printButton, "Drucken nicht möglich gewesen:\n" + var4.getMessage(), "Exception", 0);
      }
   }

   private void formComponentShown(ComponentEvent evt) {
      this.tm.start();
   }

   private void formComponentHidden(ComponentEvent evt) {
      this.tm.stop();
   }

   private class addRunner implements Runnable {
      Object[] o;

      private addRunner(String ort, long last, long start, long ssigt, long sbstt, long sim, boolean bst) {
         long d = last / 1000L;
         int sec = (int)(d % 60L);
         int min = (int)(d / 60L);
         String ct = String.format("%02d:%02d", min, sec / 10 * 10);
         d = start / 1000L;
         sec = (int)(d % 60L);
         min = (int)(d / 60L);
         String tt = String.format("%02d:%02d", min, sec / 10 * 10);
         d = ssigt / 1000L;
         sec = (int)(d % 60L);
         min = (int)(d / 60L);
         String ssig = String.format("%02d:%02d", min, sec / 10 * 10);
         if (ssigt < 0L) {
            ssig = "";
         }

         d = sbstt / 1000L;
         sec = (int)(d % 60L);
         min = (int)(d / 60L);
         String sbst = String.format("%02d:%02d", min, sec / 10 * 10);
         if (sbstt < 0L) {
            sbst = "";
         }

         String st = zugMeasure.this.df.format(sim);
         Object[] _o = new Object[]{ort, ct, ssig, sbst, tt, st};
         this.o = _o;
      }

      public void run() {
         DefaultTableModel dtm = (DefaultTableModel)zugMeasure.this.dataTable.getModel();
         dtm.addRow(this.o);
      }
   }

   private class hookCall extends event {
      hookCall(Simulator sim) {
         super(sim);
         zugMeasure.this.z.registerHook(eventGenerator.T_ZUG_FAHRT, this);
         zugMeasure.this.z.registerHook(eventGenerator.T_ZUG_WURDEGRUEN, this);
         zugMeasure.this.z.registerHook(eventGenerator.T_ZUG_ABFAHRT, this);
         zugMeasure.this.z.registerHook(eventGenerator.T_ZUG_AUSFAHRT, this);
      }

      @Override
      protected boolean init(eventContainer e) {
         return true;
      }

      @Override
      public String getText() {
         return null;
      }

      @Override
      public boolean hookCall(eventGenerator.TYPES typ, eventmsg e) {
         if (e != null && e instanceof zugmsg) {
            zugmsg ge = (zugmsg)e;
            if (zugMeasure.this.z != ge.z) {
               System.out.println("hook call mit falschem Zug! Bitte rufen Sie die Auskunft an!");
            } else if (typ == eventGenerator.T_ZUG_FAHRT || typ == eventGenerator.T_ZUG_AUSFAHRT) {
               zugMeasure.this.fahrt(ge.g, ge.before_gl);
            } else if (typ != eventGenerator.T_ZUG_WURDEGRUEN && typ == eventGenerator.T_ZUG_ABFAHRT) {
               zugMeasure.this.abfahrt(ge.g);
            }
         }

         return true;
      }
   }
}
