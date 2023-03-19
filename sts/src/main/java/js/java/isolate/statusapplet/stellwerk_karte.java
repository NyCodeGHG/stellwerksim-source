package js.java.isolate.statusapplet;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import js.java.isolate.sim.Timer;
import js.java.isolate.sim.sim.botcom.BotCalling;
import js.java.isolate.statusapplet.karte.MapBotChat;
import js.java.isolate.statusapplet.karte.kartePanel;
import js.java.isolate.statusapplet.karte.statisticsPanel;
import js.java.isolate.statusapplet.karte.zugListPanel;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.adapter.AbstractTopFrame;
import js.java.schaltungen.moduleapi.ModuleObject;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.timesystem.simTimeHolder;
import js.java.tools.gui.SwingTools;
import js.java.tools.gui.textticker;
import js.java.tools.gui.dataTransferDisplay.DataTransferDisplayComponent;

public class stellwerk_karte extends AbstractTopFrame implements BotCalling, Runnable, SessionClose, ModuleObject {
   public MapBotChat my_chat = null;
   private kartePanel kp = null;
   private final Timer game_time;
   private final JScrollPane outputscrollpane;
   private final simTimeHolder simTime = new simTimeHolder();
   private final DataTransferDisplayComponent ircData;
   private final DataTransferDisplayComponent loadData;
   private final zugListPanel zugList;
   private final Thread spoolRunner;

   @Override
   public void handleIRC(String sender, String r, boolean publicmsg) {
      this.ircData.gotData();
      this.kp.handleIRC(sender, r, publicmsg);
   }

   @Override
   public void handleIRCresult(String cmd, int res, String r, boolean publicmsg) {
      this.ircData.gotData();
      this.kp.handleIRCresult(cmd, res, r, publicmsg);
   }

   @Override
   public void close() {
      if (this.my_chat != null) {
         this.my_chat.quit();
      }

      this.game_time.end();
      this.kp.cleanMap();
      this.spoolRunner.interrupt();
   }

   @Override
   public void terminate() {
      this.dispose();
      this.uc.moduleClosed();
   }

   public stellwerk_karte(final UserContext uc) {
      super(uc);
      this.setCursor(new Cursor(3));
      this.setTitle("Live Landkarte");
      uc.addCloseObject(this);
      uc.addCloseObject(this.simTime);
      this.simTime.setTimeDeliverer(this.getParameter("url"));
      this.setLayout(new BorderLayout());
      final JSplitPane sp = new JSplitPane(1);
      this.zugList = new zugListPanel();
      JScrollPane scroll = new JScrollPane();
      scroll.setViewportView(this.zugList);
      JPanel zugpanel = new JPanel(new BorderLayout());
      zugpanel.add(scroll, "Center");
      statisticsPanel stp = new statisticsPanel();
      zugpanel.add(stp, "South");
      this.outputscrollpane = new JScrollPane();
      this.kp = new kartePanel(this, this.outputscrollpane, this.zugList);
      this.zugList.setKartePanel(this.kp);
      this.zugList.setStatisticsPanel(stp);
      this.kp.setStatisticsPanel(stp);
      sp.setResizeWeight(1.0);
      sp.setLeftComponent(this.outputscrollpane);
      sp.setRightComponent(zugpanel);
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            sp.setDividerLocation(stellwerk_karte.this.getWidth() - 200);
         }
      });
      this.add(sp, "Center");
      JPanel newspanel = new JPanel();
      newspanel.setLayout(new BorderLayout());
      JTextField timeTextField = new JTextField();
      timeTextField.setEditable(false);
      timeTextField.setFont(new Font("Dialog", 0, 10));
      timeTextField.setHorizontalAlignment(0);
      timeTextField.setText("00:00:00");
      timeTextField.setAlignmentX(0.0F);
      timeTextField.setAlignmentY(0.0F);
      timeTextField.setBorder(null);
      timeTextField.setMargin(new Insets(2, 2, 0, 0));
      timeTextField.setMaximumSize(new Dimension(60, 20));
      timeTextField.setMinimumSize(new Dimension(60, 18));
      timeTextField.setPreferredSize(new Dimension(60, 18));
      newspanel.add(timeTextField, "West");
      textticker textTextField = new textticker();
      textTextField.setAlignmentX(0.0F);
      textTextField.setAlignmentY(0.0F);
      textTextField.setBorder(null);
      textTextField.setMaximumSize(new Dimension(10000, 20));
      textTextField.setMinimumSize(new Dimension(50, 18));
      textTextField.setPreferredSize(new Dimension(10000, 18));
      newspanel.add(textTextField, "Center");
      uc.addCloseObject(() -> textTextField.stopRunning());
      JTextField selectionField = new JTextField();
      selectionField.setEditable(false);
      selectionField.setFont(new Font("Dialog", 0, 10));
      selectionField.setHorizontalAlignment(0);
      selectionField.setText("--------");
      selectionField.setAlignmentX(0.0F);
      selectionField.setAlignmentY(0.0F);
      selectionField.setBorder(null);
      selectionField.setMargin(new Insets(2, 2, 0, 0));
      selectionField.setMaximumSize(new Dimension(60, 20));
      selectionField.setMinimumSize(new Dimension(60, 18));
      selectionField.setPreferredSize(new Dimension(60, 18));
      newspanel.add(selectionField, "East");
      this.kp.setCurrentZugPanel(selectionField);
      this.add(newspanel, "South");
      this.game_time = new Timer(this.simTime, timeTextField, textTextField);
      uc.addCloseObject(this.game_time);
      JPanel p = new JPanel();
      p.setLayout(new FlowLayout());
      this.add(p, "North");
      final JSlider sl = new JSlider();
      sl.setFocusable(false);
      sl.setMinimum(10);
      sl.setMaximum(this.kp.maxScale());
      sl.setValue(this.kp.initScale());
      sl.setPaintTicks(true);
      sl.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            stellwerk_karte.this.kp.setScale(sl.getValue());
         }
      });
      p.add(sl);
      this.ircData = new DataTransferDisplayComponent();
      p.add(this.ircData);
      this.loadData = new DataTransferDisplayComponent();
      p.add(this.loadData);
      this.my_chat = new MapBotChat(this, uc, this.getParameter("url"));
      System.out.println("Chat2");
      this.spoolRunner = new Thread(this);
      this.spoolRunner.setName("spoolRunner");
      this.spoolRunner.start();
      this.setLocationByPlatform(true);
      this.pack();
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      this.setSize(dim.width * 2 / 3, dim.height * 2 / 3);
      this.setVisible(true);
      SwingTools.toFront(this);
      this.setDefaultCloseOperation(2);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            uc.moduleClosed();
         }
      });
   }

   public void run() {
      this.kp.run(this.my_chat);
   }

   @Override
   public void chatDisconnected(String msg) {
   }

   public void message(String s) {
      this.game_time.addText(s);
   }

   public void message(String s, boolean important) {
      if (important) {
         this.game_time.addImportantText(s);
      } else {
         this.game_time.addText(s);
      }
   }

   @Override
   public void checkAutoMsg(String nick, String channel, String msg) {
   }

   public DataTransferDisplayComponent getDataLed() {
      return this.loadData;
   }
}
