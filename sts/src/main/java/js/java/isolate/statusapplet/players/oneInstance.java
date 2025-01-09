package js.java.isolate.statusapplet.players;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import js.java.isolate.sim.sim.botcom.BotCalling;
import js.java.isolate.statusapplet.stellwerk_players;
import js.java.schaltungen.UserContext;
import js.java.tools.gui.dataTransferDisplay.DataTransferDisplayComponent;

public class oneInstance extends JPanel implements BotCalling, playersIFrame.closeIFrame {
   private final stellwerk_players my_main;
   private final playersPanel kp;
   private final int instance;
   private gleisbildLoadPanel gmdi;
   private playersStatusPanel psp = null;
   private final JScrollPane outputscrollpane;
   private loadPanel lPanel = null;
   private DataTransferDisplayComponent dataMonitor = null;
   private final UserContext uc;
   private JLabel instanzLabel;
   private JSeparator jSeparator1;
   private JButton showStatusButton;
   private JButton showTimeButton;
   private JPanel statusPanel;

   public oneInstance(stellwerk_players m, UserContext uc, int c) {
      this.my_main = m;
      this.uc = uc;
      this.instance = c;
      this.setCursor(new Cursor(3));
      this.initComponents();
      this.dataMonitor = new DataTransferDisplayComponent();
      this.statusPanel.add(this.dataMonitor);
      this.instanzLabel.setText("Instanz " + (this.instance + 1) + " ");
      this.outputscrollpane = new JScrollPane();
      this.kp = new playersPanel(uc, this, this.outputscrollpane);
      if (this.my_main.getParameter("anlagenlesenbase") != null) {
         this.gmdi = new gleisbildLoadPanel(uc, this.my_main, this, this.kp);
         this.statusPanel.add(this.gmdi);
      }

      if (c == 1) {
         memPanel mp = new memPanel();
         uc.addCloseObject(mp);
         this.add(mp, "East");
      } else if (c == 0) {
         this.lPanel = new loadPanel(this);
         this.add(this.lPanel, "East");
      }
   }

   StatusBotChat getChat() {
      return (StatusBotChat)this.my_main.my_chat;
   }

   players_zug getZug(int zid) {
      return this.kp.findOrAddZug(zid);
   }

   players_aid getAnlage(int aid) {
      return this.kp.findAid(aid);
   }

   public JComponent getPlayerPanel() {
      return this.outputscrollpane;
   }

   public void addViewData(JComponent c) {
      this.my_main.addViewData(c);
   }

   public void removeViewData(JComponent c) {
      this.my_main.removeViewData(c);
   }

   public void registerHook(ircupdate i) {
      this.kp.registerHook(i);
   }

   public void unregisterHook(ircupdate i) {
      this.kp.unregisterHook(i);
   }

   public void setProgress(int p) {
      try {
         this.gmdi.setProgress(p);
         this.lPanel.setProgress(p);
      } catch (NullPointerException var3) {
      }
   }

   final void addSubWindow(JFrame f) {
      f.setTitle("(" + (this.instance + 1) + ") " + f.getTitle());
      f.setVisible(true);
      f.setIconImage(this.uc.getWindowIcon());
   }

   @Override
   public void checkAutoMsg(String nick, String channel, String msg) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void clean() {
      this.kp.clean();
   }

   @Override
   public void handleIRC(String sender, String r, boolean publicmsg) {
      if (publicmsg) {
         this.kp.handleIRC(sender, r);
      }
   }

   @Override
   public void handleIRCresult(String cmd, int res, String r, boolean publicmsg) {
      this.dataMonitor.gotData();
      this.kp.handleIRCresult(cmd, res, r, publicmsg);
   }

   @Override
   public void chatDisconnected(String msg) {
      System.out.println("Disconn");
   }

   int getInstanz() {
      return this.instance;
   }

   @Override
   public void onClose() {
      try {
         this.psp.close();
         this.psp = null;
      } catch (NullPointerException var2) {
      }

      this.showStatusButton.setEnabled(true);
   }

   private void initComponents() {
      this.statusPanel = new JPanel();
      this.instanzLabel = new JLabel();
      this.showStatusButton = new JButton();
      this.showTimeButton = new JButton();
      this.jSeparator1 = new JSeparator();
      this.setLayout(new BorderLayout());
      this.statusPanel.setBorder(BorderFactory.createEtchedBorder());
      this.statusPanel.setLayout(new BoxLayout(this.statusPanel, 2));
      this.instanzLabel.setText("Instanz");
      this.statusPanel.add(this.instanzLabel);
      this.showStatusButton.setFont(this.showStatusButton.getFont().deriveFont((float)this.showStatusButton.getFont().getSize() - 1.0F));
      this.showStatusButton.setText("Status...");
      this.showStatusButton.setFocusPainted(false);
      this.showStatusButton.setFocusable(false);
      this.showStatusButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            oneInstance.this.showStatusButtonActionPerformed(evt);
         }
      });
      this.statusPanel.add(this.showStatusButton);
      this.showTimeButton.setFont(this.showTimeButton.getFont().deriveFont((float)this.showTimeButton.getFont().getSize() - 1.0F));
      this.showTimeButton.setText("Uhr...");
      this.showTimeButton.setFocusPainted(false);
      this.showTimeButton.setFocusable(false);
      this.showTimeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            oneInstance.this.showTimeButtonActionPerformed(evt);
         }
      });
      this.statusPanel.add(this.showTimeButton);
      this.jSeparator1.setOrientation(1);
      this.jSeparator1.setPreferredSize(new Dimension(2, 5));
      this.statusPanel.add(this.jSeparator1);
      this.add(this.statusPanel, "West");
   }

   private void showStatusButtonActionPerformed(ActionEvent evt) {
      if (this.psp == null) {
         this.psp = new playersStatusPanel(this, this.kp);
         playersIFrame pif = new playersIFrame("Statusübersicht", this.psp, this);
         this.addSubWindow(pif);
         this.showStatusButton.setEnabled(false);
         this.uc.addCloseObject(pif);
         this.uc.addCloseObject(this.psp);
      }
   }

   private void showTimeButtonActionPerformed(ActionEvent evt) {
      clockIFrame clock = new clockIFrame(this, this.uc);
      this.addSubWindow(clock);
      this.uc.addCloseObject(clock);
   }
}
