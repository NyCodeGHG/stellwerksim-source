package js.java.isolate.statusapplet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.Timer;
import js.java.isolate.sim.gleis.decor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.sim.fsallocator;
import js.java.isolate.sim.sim.botcom.BotCalling;
import js.java.isolate.sim.sim.botcom.chatInterface;
import js.java.isolate.statusapplet.players.StatusBotChat;
import js.java.isolate.statusapplet.players.extractedView;
import js.java.isolate.statusapplet.players.oneInstance;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.adapter.AbstractTopFrame;
import js.java.schaltungen.audio.AudioController;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.moduleapi.ModuleObject;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.timesystem.simTimeHolder;
import js.java.schaltungen.timesystem.timedelivery;
import js.java.schaltungen.timesystem.timedeliveryRealtime;
import js.java.tools.prefs;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.gui.SwingTools;
import js.java.tools.gui.textticker;

public class stellwerk_players
   extends AbstractTopFrame
   implements SessionClose,
   GleisAdapter,
   BotCalling,
   ActionListener,
   MouseListener,
   extractedView.reverseExtract,
   ModuleObject {
   public chatInterface my_chat = null;
   private final Timer game_time;
   private final oneInstance[] instances = new oneInstance[2];
   private final JMenuBar menuBar;
   private final simTimeHolder simTime = new simTimeHolder();
   private final javax.swing.Timer aliveTimer = new javax.swing.Timer(360000, this);
   private final JPanel outputPanel;
   private final JTabbedPane viewPanel;
   private final fsallocator fsalloc;
   private int lastSelTab = -1;

   @Override
   public void close() {
      this.aliveTimer.stop();
      if (this.my_chat != null) {
         this.my_chat.quit();
      }

      this.game_time.end();

      for(oneInstance i : this.instances) {
         i.clean();
      }
   }

   @Override
   public void terminate() {
      this.dispose();
      this.uc.moduleClosed();
   }

   public stellwerk_players(final UserContext uc) {
      super(uc);
      this.simTime.setTimeDeliverer(new timedeliveryRealtime());
      uc.addCloseObject(this);
      uc.addCloseObject(this.simTime);
      decor.createDecor(uc);
      this.menuBar = new JMenuBar();
      this.setJMenuBar(this.menuBar);
      this.setLayout(new BorderLayout());
      this.viewPanel = new JTabbedPane();
      this.viewPanel.setFocusable(false);
      this.viewPanel.addMouseListener(this);
      this.viewPanel.setToolTipText("Doppelklick um Tab rauszulösen");
      JPanel topPan = new JPanel();
      this.add(topPan, "North");
      topPan.setLayout(new GridLayout(0, 1));

      for(int i = 0; i < 2; ++i) {
         this.instances[i] = new oneInstance(this, uc, i);
         topPan.add(this.instances[i]);
         JComponent pp = this.instances[i].getPlayerPanel();
         JPanel tp = new JPanel();
         tp.setName("Instanz " + (i + 1));
         tp.setBorder(new TitledBorder("Instanz " + (i + 1)));
         tp.setLayout(new BorderLayout());
         tp.add(pp, "Center");
         this.viewPanel.add(tp, "Instanz " + (i + 1));
      }

      this.outputPanel = new JPanel();
      this.outputPanel.setLayout(new GridLayout(0, 2));
      JSplitPane pane = new JSplitPane(1, this.viewPanel, this.outputPanel);
      pane.setOneTouchExpandable(true);
      this.add(pane, "Center");
      System.out.println("Chat1");
      this.my_chat = new StatusBotChat(this.instances, uc);
      System.out.println("Chat2");
      this.fsalloc = new fsallocator(this);
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
      this.add(newspanel, "South");
      this.pack();
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      this.setLocation(0, 0);
      this.setSize(dim.width - 20, dim.height / 3);
      this.setVisible(true);
      SwingTools.toFront(this);
      this.game_time = new Timer(this.simTime, timeTextField, textTextField);
      uc.addCloseObject(this.game_time);
      textTextField.addImportantText(
         "Hinweis: es dauert einige Minuten bis vom Server die ersten Daten übertragen und angezeigt werden, da diese nur alle 4-5 Minuten gesendet werden."
      );
      this.aliveTimer.start();
      this.setDefaultCloseOperation(2);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            uc.moduleClosed();
         }
      });
   }

   public void addViewData(JComponent c) {
      this.outputPanel.add(c);
   }

   public void removeViewData(JComponent c) {
      this.outputPanel.remove(c);
      this.outputPanel.repaint();
   }

   @Override
   public void setProgress(int p) {
      for(int i = 0; i < 2; ++i) {
         this.instances[i].setProgress(p);
      }
   }

   public chatInterface getChat() {
      return this.my_chat;
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

   @Override
   public void handleIRC(String sender, String r, boolean publicmsg) {
   }

   @Override
   public void handleIRCresult(String cmd, int res, String r, boolean publicmsg) {
   }

   @Override
   public void chatDisconnected(String msg) {
   }

   public void actionPerformed(ActionEvent e) {
      this.my_chat.sendStatus(BOTCOMMAND.ALIVE, "");
   }

   public void mouseClicked(MouseEvent e) {
      if (this.viewPanel.getSelectedIndex() == this.lastSelTab && e.getClickCount() == 2) {
         int instanz = 0;

         for(int i = 0; i < 2; ++i) {
            JPanel p = (JPanel)this.viewPanel.getSelectedComponent();
            JComponent comp = (JComponent)p.getComponent(0);
            if (this.instances[i].getPlayerPanel() == comp) {
               instanz = i;
               break;
            }
         }

         extractedView ev = new extractedView(
            this.uc,
            (JPanel)this.viewPanel.getSelectedComponent(),
            this.getParameter("ircserver"),
            this,
            this.viewPanel.getSelectedComponent().getName(),
            instanz
         );
         ev.setVisible(true);
      }

      this.lastSelTab = this.viewPanel.getSelectedIndex();
   }

   @Override
   public void reverse(JPanel pan) {
      this.viewPanel.add(pan, pan.getName());
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   @Override
   public void setUI(gleis.gleisUIcom gl) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void readUI(gleis.gleisUIcom gl) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void repaintGleisbild() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void incZählwert() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void interPanelCom(AbstractEvent e) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void setGUIEnable(boolean e) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public timedelivery getTimeSystem() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public fsallocator getFSallocator() {
      return this.fsalloc;
   }

   @Override
   public AudioController getAudio() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Simulator getSim() {
      return null;
   }

   @Override
   public prefs getSimPrefs() {
      return null;
   }

   @Override
   public void showStatus(String s, int type) {
   }

   @Override
   public void showStatus(String s) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public gleisbildModelSts getGleisbild() {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
