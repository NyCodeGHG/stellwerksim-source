package js.java.isolate.sim.sim;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import js.java.isolate.sim.flagdata;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.structServ.structListPanel;
import js.java.isolate.sim.structServ.structinfo;
import js.java.isolate.sim.structServ.structinfoTablePanel;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;

public class internalInfo extends JDialog implements structListPanel.selectionListener, SessionClose {
   private final stellwerksim_main my_main;
   private final structListPanel structList;
   private final structinfoTablePanel structTable;
   private JTextField commandField;
   private JPanel dataPanel;
   private JCheckBox enableRemote;
   private JCheckBox enableTimer;
   private JPanel jPanel1;
   private JPanel jPanel4;
   private JSplitPane mainSplit;
   private JButton okButton;
   private JPasswordField passwort;
   private Timer updateTimer = null;

   public internalInfo(stellwerksim_main m) {
      super(m, false);
      this.my_main = m;
      this.initComponents();
      this.structList = new structListPanel(this);
      this.structTable = new structinfoTablePanel();
      this.mainSplit.setLeftComponent(this.structList);
      this.dataPanel.add(this.structTable, "Center");
      this.mainSplit.setVisible(false);
      this.enableRemote.setSelected(this.my_main.isCtrlSrv());
      m.uc.addCloseObject(this);
      this.setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/js/java/tools/resources/funk.gif")));
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
      this.setVisible(true);
   }

   @Override
   public void close() {
      this.dispose();
      this.formWindowClosed(null);
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.passwort = new JPasswordField();
      this.okButton = new JButton();
      this.mainSplit = new JSplitPane();
      this.dataPanel = new JPanel();
      this.jPanel4 = new JPanel();
      this.enableTimer = new JCheckBox();
      this.enableRemote = new JCheckBox();
      this.commandField = new JTextField();
      this.setDefaultCloseOperation(2);
      this.setTitle("Strukturen");
      this.setLocationByPlatform(true);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            internalInfo.this.formWindowClosed(evt);
         }
      });
      this.jPanel1.setBorder(BorderFactory.createTitledBorder("Passwort"));
      this.jPanel1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
      this.jPanel1.setMinimumSize(new Dimension(47, 45));
      this.jPanel1.setPreferredSize(new Dimension(47, 55));
      this.jPanel1.setLayout(new BorderLayout());
      this.passwort.setMinimumSize(new Dimension(6, 25));
      this.passwort.setPreferredSize(new Dimension(6, 25));
      this.passwort.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            internalInfo.this.okButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.passwort, "Center");
      this.okButton.setText("Ok");
      this.okButton.setMaximumSize(new Dimension(47, 30));
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            internalInfo.this.okButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.okButton, "East");
      this.getContentPane().add(this.jPanel1, "North");
      this.mainSplit.setBorder(null);
      this.mainSplit.setDividerLocation(180);
      this.mainSplit.setDividerSize(8);
      this.mainSplit.setMinimumSize(new Dimension(34, 186));
      this.mainSplit.setOneTouchExpandable(true);
      this.mainSplit.setPreferredSize(new Dimension(480, 438));
      this.dataPanel.setLayout(new BorderLayout());
      this.jPanel4.setLayout(new BoxLayout(this.jPanel4, 2));
      this.enableTimer.setText("Timer");
      this.enableTimer.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            internalInfo.this.enableTimerItemStateChanged(evt);
         }
      });
      this.jPanel4.add(this.enableTimer);
      this.enableRemote.setText("remote");
      this.enableRemote.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            internalInfo.this.enableRemoteActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.enableRemote);
      this.commandField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            internalInfo.this.commandFieldActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.commandField);
      this.dataPanel.add(this.jPanel4, "North");
      this.mainSplit.setRightComponent(this.dataPanel);
      this.getContentPane().add(this.mainSplit, "Center");
      this.pack();
   }

   private void enableTimerItemStateChanged(ItemEvent evt) {
      if (this.enableTimer.isSelected()) {
         if (this.updateTimer == null) {
            this.updateTimer = new Timer();
         }

         this.updateTimer.schedule(new TimerTask() {
            public void run() {
               internalInfo.this.selected();
            }
         }, 0L, 1000L);
      } else if (this.updateTimer != null) {
         this.updateTimer.cancel();
         this.updateTimer = null;
      }
   }

   private void formWindowClosed(WindowEvent evt) {
      if (this.updateTimer != null) {
         this.updateTimer.cancel();
      }
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      String p = new String(this.passwort.getPassword());
      if (p.compareTo("ändern!") == 0) {
         this.mainSplit.setVisible(true);
         this.mainSplit.revalidate();
         this.add1();
      } else if (p.compareTo("hilfe") != 0) {
         if (p.compareTo("throw") == 0) {
            throw new IllegalArgumentException("Exception test");
         }

         this.passwort.setText("");
      }
   }

   private void commandFieldActionPerformed(ActionEvent evt) {
      String cmd = this.commandField.getText();
      if (cmd != null && !cmd.isEmpty()) {
         this.commandField.setText("");
         this.runCommand(cmd);
      }
   }

   private void enableRemoteActionPerformed(ActionEvent evt) {
      this.enableRemote.setSelected(this.my_main.enableCtrlSrv(this.enableRemote.isSelected()));
   }

   private void selected() {
      this.selected(this.structList.getSelected());
   }

   @Override
   public void selected(structinfo si) {
      if (si != null) {
         this.structTable.add(si.getStructure());
      }
   }

   private void add1() {
      this.structList.clear();
      Vector v = this.my_main.getStructInfo();
      Enumeration e = v.elements();

      while (e.hasMoreElements()) {
         Vector vv = (Vector)e.nextElement();
         this.structList.add(vv);
      }
   }

   private void runCommand(String cmd) {
      String[] p = cmd.split(" ");

      try {
         if (p[0].equalsIgnoreCase("help")) {
            System.out.println("set");
            System.out.println(" signaltyp N");
            System.out.println(" tempo Z T");
            System.out.println(" aflag Z");
            System.out.println(" bü on|off");
            System.out.println("irc LINE");
            System.out.println("call CODE");
         } else if (p[0].equalsIgnoreCase("set")) {
            this.cmd_set(p);
         } else if (p[0].equalsIgnoreCase("irc")) {
            this.cmd_irc(p);
         } else if (p[0].equalsIgnoreCase("call")) {
            this.cmd_call(p);
         }
      } catch (Exception var4) {
      }
   }

   private void cmd_set(String[] p) {
      if (p[1].equalsIgnoreCase("signaltyp")) {
         int t = Integer.parseInt(p[2]);
         if (t >= 0 && t <= 4) {
            this.my_main.getGleisbild().gleisbildextend.setSignalversion(t);
         }
      } else if (p[1].equalsIgnoreCase("tempo")) {
         zug z;
         try {
            int zid = Integer.parseInt(p[2]);
            z = this.my_main.findZug(zid);
         } catch (Exception var6) {
            z = this.my_main.findZugByShortName(p[2]);
         }

         int t = Integer.parseInt(p[3]);
         if (z != null) {
            String pa = "soll_tempo=*=" + t;
            z.setParam(pa);
         }
      } else if (p[1].equalsIgnoreCase("aflag")) {
         zug zx;
         try {
            int zid = Integer.parseInt(p[2]);
            zx = this.my_main.findZug(zid);
         } catch (Exception var5) {
            zx = this.my_main.findZugByShortName(p[2]);
         }

         if (zx != null) {
            flagdata fd = zx.getFlags();
            fd.addFlag('A');
         }
      } else if (p[1].equalsIgnoreCase("bü")) {
         gleis.michNervenBüs = p[2].equalsIgnoreCase("off");
      }
   }

   private void cmd_irc(String[] p) {
      this.my_main.getGleisbild().IRCeventTrigger(p[1], true);
   }

   private void cmd_call(String[] p) {
      String code = "";
      String token = "";

      try {
         code = p[0];
         token = p[1];
      } catch (ArrayIndexOutOfBoundsException var5) {
      }

      event.startActivityCall(code, token);
   }
}
