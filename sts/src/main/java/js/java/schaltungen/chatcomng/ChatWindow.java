package js.java.schaltungen.chatcomng;

import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.JToolBar.Separator;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.adapter.LaunchModule;
import js.java.schaltungen.adapter.RunningModulesCountEvent;
import js.java.schaltungen.adapter.closePrefs;
import js.java.schaltungen.adapter.simPrefs;
import js.java.schaltungen.audio.AudioPlayer;
import js.java.schaltungen.audio.AudioSettings;
import js.java.schaltungen.audio.AudioSettingsChangedEvent;
import js.java.schaltungen.cevents.BuildEvent;
import js.java.schaltungen.settings.ShowSettingsEvent;
import js.java.schaltungen.switchbase.DumpSwitchValueEvent;
import js.java.schaltungen.switchbase.SwitchValueEvent;
import js.java.schaltungen.webservice.GetTip;
import js.java.schaltungen.webservice.GetTipResponse;
import js.java.tools.AlphanumComparator;
import js.java.tools.HTMLEntities;
import js.java.tools.JavaKind;
import js.java.tools.gui.BoundedPlainDocument;
import js.java.tools.gui.HTMLEditorKitCustomCss;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.gui.dataTransferDisplay.DataTransferDisplayComponent;
import js.java.tools.gui.dataTransferDisplay.LedComponent;
import js.java.tools.gui.dataTransferDisplay.LedComponent.LEDCOLOR;
import js.java.tools.gui.prefs.SplitPanePrefsSaver;

public class ChatWindow extends JFrame implements ChatWindowMBean {
   private static final String BEEP = "PIEP";
   private static final long BEEPDELAY = TimeUnit.MINUTES.toMillis(10L);
   private final AudioPlayer chatCallPlayer;
   private final Preferences node;
   private final Preferences totdNode;
   private final UserContextMini uc;
   private boolean withTray;
   private final ChatWindow.ChannelModel cmodel = new ChatWindow.ChannelModel();
   private final DefaultListModel<String> umodel = new DefaultListModel();
   private final ArrayList<ChatUser> umodelUsers = new ArrayList();
   private String currentChannel = "";
   private final simPrefs chatWindowPrefs = new simPrefs("start");
   public static final String EXIT_ON_CLOSE = "exitOnCloseV2";
   private final AudioPlayer chatPlayer;
   private final DataTransferDisplayComponent dataMonitor;
   private final LedComponent runStatus;
   private final LedComponent ipv6Status;
   private final AudioSettings asettings;
   private final int miniFontSize;
   private final Timer tipOfTheDayTimer = new Timer(600000, a -> this.loadTip());
   private final Timer statusColorTimer = new Timer(50, a -> this.statusColorTimer());
   private final Set<String> ignoreUserList = new TreeSet();
   private long lastBeep = 0L;
   private boolean shownMaint = false;
   private boolean modulesRunning = false;
   private long lastActitity = System.currentTimeMillis();
   private int tipTryCount = 0;
   private String currentStatusText = "";
   private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
   private JButton aboutButton;
   private JToggleButton audioOnButton;
   private JTable channelList;
   private JScrollPane channelScrollPane;
   private JPopupMenu chatPopupMenu;
   private JButton consoleButton;
   private JLabel countLabel;
   private JButton exitButton;
   private JToggleButton exitOnCloseButton;
   private JCheckBoxMenuItem filterStatusMenu;
   private JButton hideButton;
   private JMenu ignoreUserListMenu;
   private JMenuItem ignoreUserMenu;
   private JPanel inputPanel;
   private JPanel ircPanel;
   private JTextField ircTextField;
   private JTextPane ircTextPane;
   private JScrollPane ircTextScrollPane;
   private JList ircUserList;
   private JMenu jMenu1;
   private JMenu jMenu2;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JPanel jPanel4;
   private JPanel jPanel5;
   private JScrollPane jScrollPane2;
   private Separator jSeparator1;
   private Separator jSeparator2;
   private Separator jSeparator3;
   private javax.swing.JPopupMenu.Separator jSeparator4;
   private JSeparator jSeparator5;
   private javax.swing.JPopupMenu.Separator jSeparator6;
   private JSplitPane jSplitPane1;
   private JSplitPane jSplitPane2;
   private JButton joinChannelButton;
   private JToggleButton noautoScrollButton;
   private JProgressBar progressBar;
   private JButton settingsButton;
   private ButtonGroup showByBG;
   private JRadioButtonMenuItem showByName;
   private JRadioButtonMenuItem showByPlay;
   private JRadioButtonMenuItem sortByName;
   private JRadioButtonMenuItem sortByPlay;
   private ButtonGroup sortingBG;
   private JPanel statusBar;
   private JTextPane statusField;
   private JPanel statusPanel;
   private JToolBar toolBar;

   public ChatWindow(UserContextMini uc, boolean withTray) {
      super();
      this.uc = uc;
      this.withTray = withTray;
      this.asettings = uc.getAudioSettings();
      Preferences root = Preferences.userNodeForPackage(this.getClass());
      this.node = root.node("menu");
      this.totdNode = root.node("totd");
      this.initComponents();
      this.dataMonitor = new DataTransferDisplayComponent();
      this.dataMonitor.setToolTipText("Datentransfer");
      this.statusPanel.add(this.dataMonitor);
      this.runStatus = new LedComponent();
      this.runStatus.setToolTipText("Anwendungsstatus");
      this.statusPanel.add(this.runStatus);
      this.ipv6Status = new LedComponent();
      this.ipv6Status.setToolTipText("IPv6");
      this.ipv6Status.setColor(LEDCOLOR.GREEN);
      this.statusPanel.add(this.ipv6Status);
      this.hideButton.setEnabled(withTray);
      this.exitOnCloseButton.setEnabled(withTray);
      ((CardLayout)this.inputPanel.getLayout()).show(this.inputPanel, "bar");
      this.progressBar.setIndeterminate(true);
      this.chatPlayer = new AudioPlayer(AudioPlayer.SAMPLES.CHAT);
      this.chatCallPlayer = new AudioPlayer(AudioPlayer.SAMPLES.PHONE1);
      this.setIconImage(uc.getWindowIcon());
      ListSelectionModel listSelectionModel = this.channelList.getSelectionModel();
      listSelectionModel.addListSelectionListener(e -> this.changeChannel());
      this.ignoreUserMenu.setEnabled(false);
      listSelectionModel = this.ircUserList.getSelectionModel();
      listSelectionModel.addListSelectionListener(e -> {
         if (!e.getValueIsAdjusting()) {
            this.userSelected();
         } else {
            this.ignoreUserMenu.setEnabled(false);
         }
      });
      this.channelList.setTableHeader(null);
      this.channelScrollPane.setColumnHeaderView(null);
      this.channelList.getColumnModel().getColumn(0).setMaxWidth(7);
      this.channelList.getColumnModel().getColumn(2).setMaxWidth(30);
      TableRowSorter sorter = new TableRowSorter(this.cmodel);
      sorter.setComparator(1, (o1, o2) -> {
         ChatWindow.JoinedChannel j1 = (ChatWindow.JoinedChannel)o1;
         ChatWindow.JoinedChannel j2 = (ChatWindow.JoinedChannel)o2;
         return j1.compareTo(j2);
      });
      this.channelList.setRowSorter(sorter);
      sorter.toggleSortOrder(1);
      if (!Boolean.parseBoolean(uc.getParameter(UserContextMini.DATATYPE.JOIN_ANY_CHANNEL))) {
         this.joinChannelButton.getParent().remove(this.joinChannelButton);
      }

      EventBusService.getInstance().subscribe(this);
      this.checkClose(null);
      this.checkAudio(null);
      this.pack();
      Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
      this.setLocation(dim.width - this.getWidth() - 50, 50);
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
      new SplitPanePrefsSaver(this.jSplitPane1, ChatWindow.class, "split1");
      new SplitPanePrefsSaver(this.jSplitPane2, ChatWindow.class, "split2");
      this.ircTextScrollPane
         .getVerticalScrollBar()
         .addAdjustmentListener(
            new AdjustmentListener() {
               public void adjustmentValueChanged(AdjustmentEvent ae) {
                  int extent = ChatWindow.this.ircTextScrollPane.getVerticalScrollBar().getModel().getExtent();
                  if (ChatWindow.this.ircTextScrollPane.getVerticalScrollBar().getValue() + extent
                     == ChatWindow.this.ircTextScrollPane.getVerticalScrollBar().getMaximum()) {
                     ChatWindow.this.noautoScrollButton.setSelected(false);
                  } else {
                     ChatWindow.this.noautoScrollButton.setSelected(true);
                  }
               }
            }
         );
      this.miniFontSize = 8;
      StyleSheet css = ((HTMLEditorKit)this.ircTextPane.getEditorKit()).getStyleSheet();
      css.addRule("body { margin: 0; font-family: sans-serif; font-size: 10px; font-style: normal; }");
      this.ircTextPane.setText("<html>Verbindung mit Server wird hergestellt und konfiguriert.<hr>Login Name: " + uc.getUsername() + "</html>");
      this.filterStatusMenu.setSelected(this.node.getBoolean("filterStatus", true));
      this.tipOfTheDayTimer.start();
      if (uc.getChat().isV6()) {
         this.ipv6Status.setLed(true);
      } else {
         this.ipv6Status.setLed(false);
      }

      this.loadTip();
      if (JavaKind.isOpenJdk()) {
         uc.showTrayMessage(
            "Achtung: Es wurde Java OpenJDK erkannt.",
            "Wegen STS-Case #2506 bzw. Bug JDK-8013099 kommt es zu einem Speicherüberlauf nach längerer Nutzung!",
            MessageType.INFO
         );
         uc.showTopLevelMessage(
            "Achtung: Es wurde Java OpenJDK erkannt. Wegen STS-Case #2506 bzw. Bug JDK-8013099 kommt es zu einem Speicherüberlauf nach längerer Nutzung!", 30
         );
      }
   }

   public void butNoTray() {
      this.withTray = false;
      this.hideButton.setEnabled(false);
      this.exitOnCloseButton.setEnabled(false);
      this.exitOnCloseButton.setSelected(true);
   }

   private void loadTip() {
      EventBusService.getInstance().publish(new GetTip());
      this.checkIdle();
   }

   private void checkIdle() {
      if (!this.modulesRunning && TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - this.lastActitity) > 2L) {
         this.uc.exit();
      }
   }

   @EventHandler
   public void gotTip(GetTipResponse event) {
      if (this.checkTipText(event.tip.getText())) {
         this.tipTryCount = 0;
         SwingUtilities.invokeLater(() -> this.setStatus(event.tip.getText()));
      } else {
         ++this.tipTryCount;
         if (this.tipTryCount < 10) {
            this.loadTip();
         }
      }
   }

   private boolean checkTipText(String text) {
      try {
         String[] names = this.totdNode.keys();
         Arrays.sort(names);
         LinkedList<Integer> hashList = new LinkedList();

         for(String n : names) {
            hashList.add(this.totdNode.getInt(n, 0));
         }

         if (hashList.contains(text.hashCode())) {
            return true;
         }

         while(hashList.size() > 10) {
            hashList.removeFirst();
         }

         hashList.addLast(text.hashCode());
         this.totdNode.clear();
         int n = 100;

         for(int hash : hashList) {
            this.totdNode.putInt(Integer.toString(n), hash);
            ++n;
         }

         this.totdNode.flush();
      } catch (BackingStoreException var8) {
      }

      return true;
   }

   private void setStatus(String text) {
      this.showStatus("");
      this.currentStatusText = text.trim();
      this.statusColorTimer.restart();
   }

   private void statusColorTimer() {
      String t = this.statusField.getText();
      if (t.length() < this.currentStatusText.length()) {
         this.showStatus(this.currentStatusText);
      } else {
         this.statusColorTimer.stop();
      }
   }

   private void showStatus(String txt) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.statusField.setText("<html><span style='font-family:sans-serif;font-size:8px;'>" + txt + "</span></html>");
      } else {
         SwingUtilities.invokeLater(() -> this.showStatus(txt));
      }
   }

   @EventHandler
   public void launchListener(LaunchModule event) {
      SwingUtilities.invokeLater(() -> {
         if (!this.isVisible()) {
            this.requestFocus();
         }
      });
   }

   @EventHandler
   public void checkClose(ExitOnCloseChangedEvent event) {
      this.exitOnCloseButton.setSelected(this.chatWindowPrefs.getBoolean("exitOnCloseV2", true) || !this.withTray);
   }

   @EventHandler
   public void checkAudio(AudioSettingsChangedEvent event) {
      this.audioOnButton.setSelected(this.asettings.playChatSettings().isEnabled());
      this.chatPlayer.setGain(this.asettings.playChatSettings().getGain());
      this.chatCallPlayer.setGain(this.asettings.playChatSettings().getGain());
   }

   @EventHandler
   public void disconnected(IrcDisconnectedEvent ch) {
      if (!ch.isDisconnecting) {
         SwingUtilities.invokeLater(() -> this.disconnected(ch.message));
      }
   }

   private void disconnected(String message) {
      this.newChannelAwt(new ConnectedChannelsEvent(new LinkedList()));
      this.umodel.clear();
      this.ircTextPane
         .setText(
            "<html>Die Internet-Verbindung zum Server wurde unterbrochen. Bitte das Programm beenden und ggf. neu starten.<br><br>Ursache: <b>"
               + message
               + "</b></html>"
         );
      this.uc.showTopLevelMessage("Fehler: Die Internet-Verbindung zum Server wurde unterbrochen!", 10);
      this.uc.showTrayMessage("Die Internet-Verbindung zum Server wurde unterbrochen.", "Bitte das Programm beenden und ggf. neu starten: " + message);
      this.withTray = false;
      this.hideButton.setEnabled(false);
      this.exitOnCloseButton.setEnabled(false);
   }

   @EventHandler
   public void commandEvents(BotCommandMessage event) {
      if (event.msg.equals(":ACCEPT")) {
         this.runStatus.setColor(LEDCOLOR.GREEN);
      } else if (event.msg.equals(":NOTACCEPT")) {
         this.runStatus.setColor(LEDCOLOR.RED);
         this.showStatus("Serverlogin nicht möglich, JNLP ungültig?");
      } else if (event.msg.equals(":MAINTENANCE")) {
         this.runStatus.setColor(LEDCOLOR.YELLOW);
         this.ircPanel.setBackground(Color.yellow);
         this.showStatus("Systemwartung! Alle Online-Spiele werden beendet!");
         if (!this.shownMaint) {
            this.uc.showTopLevelMessage("Systemwartung! Alle Online-Spiele werden beendet.", 45);
            this.uc.showTrayMessage("Systemwartung! Alle Online-Spiele werden beendet.", "Systemwartung! Alle Online-Spiele werden beendet.");
            this.shownMaint = true;
         }
      } else if (event.msg.equals(":UNMAINTENANCE")) {
         this.shownMaint = false;
         this.runStatus.setColor(LEDCOLOR.YELLOW);
         this.ircPanel.setBackground(Color.red);
         this.showStatus("Wartung beendet! Bitte das Programm neu starten.");
         this.uc.showTopLevelMessage("Wartung beendet! Bitte das Programm neu starten.", 60);
         this.uc.showTrayMessage("Wartung beendet! Bitte das Programm neu starten.", "Wartung beendet! Bitte das Programm neu starten.");
      }

      this.runStatus.setLed(true);
   }

   @EventHandler
   public void launchEvent(RunningModulesCountEvent event) {
      this.runStatus.setColor(event.count > 0 ? LEDCOLOR.YELLOW : LEDCOLOR.GREEN);
      this.runStatus.setLed(true);
      this.lastActitity = System.currentTimeMillis();
      this.modulesRunning = event.count > 0;
   }

   @EventHandler
   public void newChannel(ConnectedChannelsEvent ch) {
      SwingUtilities.invokeLater(() -> this.newChannelAwt(ch));
   }

   private void newChannelAwt(ConnectedChannelsEvent ch) {
      for(IrcChannel i : ch.channels) {
         if (i.userChannel) {
            boolean found = false;

            for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
               if (c.channelName.equals(i.channel)) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               if (this.progressBar.isIndeterminate()) {
                  ((CardLayout)this.inputPanel.getLayout()).show(this.inputPanel, "input");
                  this.progressBar.setIndeterminate(false);
               }

               this.cmodel.add(new ChatWindow.JoinedChannel(i));
            }
         }
      }

      boolean repeatUntil;
      do {
         repeatUntil = false;

         for(ChatWindow.JoinedChannel e : this.cmodel.channels) {
            boolean found = false;

            for(IrcChannel i : ch.channels) {
               if (i.channel.equals(e.channelName)) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               this.cmodel.remove(e);
               repeatUntil = true;
               if (this.currentChannel.equals(e.channelName.name) && this.cmodel.getSize() > 0) {
                  this.channelList.setRowSelectionInterval(0, 0);
               }
               break;
            }
         }
      } while(repeatUntil);

      if (this.currentChannel.isEmpty() && this.cmodel.getSize() > 0) {
         this.channelList.setRowSelectionInterval(0, 0);
      }

      this.channelList.repaint();
   }

   @EventHandler
   public void enterChannel(EnterChannel event) {
      SwingUtilities.invokeLater(() -> this.enterChannelAwt(event));
   }

   private void enterChannelAwt(EnterChannel event) {
      int idx = 0;

      for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
         if (c.channelName.name.equals(event.channelname)) {
            idx = this.channelList.convertRowIndexToView(idx);
            this.channelList.setRowSelectionInterval(idx, idx);
            this.channelList.scrollRectToVisible(new Rectangle(this.channelList.getCellRect(idx, 0, true)));
            break;
         }

         ++idx;
      }
   }

   @EventHandler
   public void users(ChannelUsersEvent cu) {
      SwingUtilities.invokeLater(() -> this.usersAwt(cu));
   }

   private void usersAwt(ChannelUsersEvent cu) {
      for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
         if (c.channelName.equals(cu.channelname)) {
            this.dataMonitor.gotData();
            c.users.clear();
            c.users.addAll(cu.users);
            this.cmodel.fireTableCellUpdated(this.cmodel.getIndex(c), 2);
            if (this.currentChannel.equals(c.channelName.name)) {
               this.updateUsers(c);
            }
            break;
         }
      }
   }

   @EventHandler
   public void users(UserJoinedEvent u) {
      SwingUtilities.invokeLater(() -> this.userEvent(true, u.channel, u.user, null, null));
   }

   @EventHandler
   public void users(UserLeftEvent u) {
      SwingUtilities.invokeLater(() -> this.userEvent(false, u.channel, u.user, u.reason, u.originator));
   }

   private void userEvent(boolean join, String channelname, ChatUser user, UserLeftEvent.REASON reason, ChatUser originator) {
      for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
         if (c.channelName.name.equals(channelname)) {
            this.dataMonitor.gotData();
            String color = "9999ff";
            String mcolor = "ffff00";
            StringBuilder msg = new StringBuilder();
            msg.append("<span style='font-size:").append(this.miniFontSize).append("px;'>").append(user.getName());
            if (join) {
               msg.append(" hat Kanal ").append(c.channelName.title).append(" betreten");
            } else if (reason == UserLeftEvent.REASON.LEFT) {
               msg.append(" hat Kanal ").append(c.channelName.title).append(" verlassen");
               mcolor = "008800";
            } else if (reason == UserLeftEvent.REASON.QUIT) {
               msg.append(" hat Kanal ").append(c.channelName.title).append(" verlassen");
               mcolor = "008800";
            } else if (reason == UserLeftEvent.REASON.BAN) {
               msg.append(" verbannt");
               if (originator != null) {
                  msg.append(" von ").append(originator.getName());
               }

               color = "990000";
               mcolor = "ffff00";
            } else if (reason == UserLeftEvent.REASON.KICK) {
               msg.append(" rausgeworfen");
               if (originator != null) {
                  msg.append(" von ").append(originator.getName());
               }

               color = "990000";
               mcolor = "ffff00";
            }

            msg.append("</span>");
            if (!this.filterStatusMenu.isSelected()) {
               this.messageAdd(c, this.buildInfoLine(msg.toString(), color, mcolor), true, false);
            }
            break;
         }
      }
   }

   @EventHandler
   public void user(ChatUser u) {
      SwingUtilities.invokeLater(() -> this.userAwt(u));
   }

   public void userAwt(ChatUser u) {
      for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
         if (c.channelName.name.equals(this.currentChannel)) {
            this.updateUsers(c);
            break;
         }
      }
   }

   @EventHandler
   public void message(ChannelActionMessageEvent msg) {
      SwingUtilities.invokeLater(() -> this.messageAwt(msg.channelname, msg.sender.getName(), msg.text, true, false));
   }

   @EventHandler
   public void message(ChannelMessageEvent msg) {
      SwingUtilities.invokeLater(() -> this.messageAwt(msg.channelname, msg.sender.getName(), msg.text, false, false));
   }

   private void messageAwt(String channelname, String sender, String text, boolean actionMessage, boolean myself) {
      if (!this.ignoreUserList.contains(sender)) {
         text = HTMLEntities.htmlAngleBrackets(text);
         if ("PIEP".equals(text)) {
            ChatWindow.JoinedChannel beepc = null;

            for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
               if (c.channelName.name.equals(channelname)) {
                  beepc = c;
                  break;
               }
            }

            if (beepc != null) {
               for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
                  if (c != beepc && "dchannel".equals(c.channelName.customdata)) {
                     this.chatCallPlayer.play();
                     this.uc.showTopLevelMessage(sender + " möchte dich auf Kanal " + beepc.channelName.title + " hinweisen.", 20);
                     this.uc.showTrayMessage("Chat Hinweis", sender + " möchte dich auf Kanal " + beepc.channelName.title + " hinweisen.", MessageType.INFO);
                     break;
                  }
               }
            }
         }

         for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
            if (c.channelName.name.equals(channelname)) {
               this.dataMonitor.gotData();
               if (this.audioOnButton.isSelected() && this.isVisible() && !c.unread) {
                  this.chatPlayer.play();
               }

               if (actionMessage) {
                  this.messageAdd(c, "<i>" + sender + " " + text + "</i><br>", false, false);
               } else {
                  this.messageAdd(c, "<b>" + sender + "</b>: " + text + "<br>", false, myself);
               }

               if (text.toLowerCase().contains(this.uc.getUsername().toLowerCase())) {
                  if (text.toLowerCase().contains("@" + this.uc.getUsername().toLowerCase())) {
                     this.chatCallPlayer.play();
                  }

                  this.uc.showTrayMessage("Chat Meldung", c.channelName.title + ": " + text, MessageType.INFO);
               }

               if (!channelname.equals(this.currentChannel)) {
                  c.unread = true;
                  this.cmodel.fireTableCellUpdated(this.cmodel.getIndex(c), 0);
               }
               break;
            }
         }
      }
   }

   @EventHandler
   public void link(ChatLinkEvent msg) {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> this.link(msg));
      } else {
         for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
            if (c.channelName.name.equals(msg.channel)) {
               this.setVisible(true);
               int idx = this.cmodel.getIndex(c);
               idx = this.channelList.convertRowIndexToView(idx);
               this.channelList.setRowSelectionInterval(idx, idx);
               break;
            }
         }
      }
   }

   @EventHandler
   public void link(ChatShowChannelEvent event) {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> this.link(event));
      } else {
         for(ChatWindow.JoinedChannel c : this.cmodel.channels) {
            if (c.channelName.name.equals(event.channel)) {
               this.setVisible(true);
               int idx = this.cmodel.getIndex(c);
               idx = this.channelList.convertRowIndexToView(idx);
               this.channelList.setRowSelectionInterval(idx, idx);
               break;
            }
         }
      }
   }

   private String buildInfoLine(String text, String bgcolor, String mcolor) {
      return "<div style='font-size:"
         + this.miniFontSize
         + "px;background-color:#"
         + bgcolor
         + ";color:#ffffff;'><span style='background-color:#"
         + mcolor
         + ";'>&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;"
         + text
         + "</div>";
   }

   private void messageAdd(ChatWindow.JoinedChannel c, String line, boolean statusMessage, boolean myself) {
      int cursorPos = this.ircTextPane.getCaretPosition();
      if (!statusMessage && c.lastMessageTime < System.currentTimeMillis() / 1000L / 60L) {
         String dline = this.buildInfoLine(this.sdf.format(new Date()), "999999", "ffff00");
         c.text.add(dline);
         c.lastMessageTime = System.currentTimeMillis() / 1000L / 60L;
         if (this.currentChannel.equals(c.channelName.name)) {
            try {
               ((HTMLEditorKit)this.ircTextPane.getEditorKit())
                  .insertHTML((HTMLDocument)this.ircTextPane.getDocument(), this.ircTextPane.getDocument().getLength(), dline, 0, 0, null);
            } catch (IOException | BadLocationException var9) {
               Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, var9);
            }
         }
      }

      c.text.add(line);

      while(c.text.size() > 100) {
         c.text.removeFirst();
      }

      if (this.currentChannel.equals(c.channelName.name)) {
         try {
            ((HTMLEditorKit)this.ircTextPane.getEditorKit())
               .insertHTML((HTMLDocument)this.ircTextPane.getDocument(), this.ircTextPane.getDocument().getLength(), line, 0, 0, null);
         } catch (IOException | BadLocationException var8) {
            Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, var8);
         }

         if (!this.noautoScrollButton.isSelected()) {
            this.ircTextPane.setCaretPosition(this.ircTextPane.getDocument().getLength());
         } else {
            this.ircTextPane.setCaretPosition(cursorPos);
         }
      }

      EventBusService.getInstance().publish(new IrcLine(c.channelName.name, c.channelName.title, line, new ChatLinkEvent(c.channelName.name), statusMessage));
   }

   private void userSelected() {
      this.ignoreUserMenu.setEnabled(false);
      this.ignoreUserMenu.setText("Ignoriere User");
      int u = this.ircUserList.getSelectedIndex();
      if (u >= 0) {
         ChatUser user = (ChatUser)this.umodelUsers.get(u);
         this.ignoreUserMenu.setText("Ignoriere User: " + user.getName());
         this.ignoreUserMenu.setEnabled(true);
      }
   }

   private void addIgnoreUser() {
      int ui = this.ircUserList.getSelectedIndex();
      if (ui >= 0) {
         ChatUser user = (ChatUser)this.umodelUsers.get(ui);
         this.ignoreUserList.add(user.getName());
         this.buildUnignoreMenu();
      }
   }

   private void removeIgnoreUser(String u) {
      this.ignoreUserList.remove(u);
      this.buildUnignoreMenu();
   }

   private void buildUnignoreMenu() {
      this.ignoreUserListMenu.removeAll();

      for(String u : this.ignoreUserList) {
         JMenuItem m = new JMenuItem(u);
         m.addActionListener(l -> this.removeIgnoreUser(u));
         this.ignoreUserListMenu.add(m);
      }
   }

   private void changeChannel() {
      int idx = this.channelList.getSelectedRow();
      if (idx >= 0) {
         idx = this.channelList.convertRowIndexToModel(idx);
         ChatWindow.JoinedChannel ch = this.cmodel.getElementAt(idx);
         if (ch != null) {
            ch.unread = false;
            this.cmodel.fireTableCellUpdated(this.cmodel.getIndex(ch), 0);
            this.currentChannel = ch.channelName.name;
            this.noautoScrollButton.setSelected(false);
            this.updateUsers(ch);
            this.ircTextPane.setText("<html>" + (String)ch.text.stream().collect(Collectors.joining()) + "</html>");
            this.ircTextPane.setCaretPosition(this.ircTextPane.getDocument().getLength());
         }
      }
   }

   private void resortUser() {
      int idx = this.channelList.getSelectedRow();
      if (idx >= 0) {
         idx = this.channelList.convertRowIndexToModel(idx);
         ChatWindow.JoinedChannel ch = this.cmodel.getElementAt(idx);
         if (ch != null) {
            this.updateUsers(ch);
         }
      }
   }

   private void updateUsers(ChatWindow.JoinedChannel c) {
      Comparator<ChatUser> cmp;
      if (this.sortByPlay.isSelected()) {
         cmp = Comparator.comparing(ChatUser::getPlay, Comparator.nullsLast(new AlphanumComparator()))
            .thenComparing(ChatUser::getNameNoA, new AlphanumComparator())
            .thenComparing(ChatUser::getNick, new AlphanumComparator())
            .thenComparingLong(ChatUser::getEntrytime)
            .thenComparing(ChatUser::getUuid)
            .thenComparingInt(Object::hashCode);
      } else {
         cmp = Comparator.comparing(ChatUser::getNameNoA, new AlphanumComparator())
            .thenComparing(ChatUser::getNick, new AlphanumComparator())
            .thenComparingLong(ChatUser::getEntrytime)
            .thenComparing(ChatUser::getUuid)
            .thenComparingInt(Object::hashCode);
      }

      c.users.sort(cmp);
      this.umodelUsers.clear();
      this.umodel.clear();
      int playing = 0;

      for(ChatUser cu : c.users) {
         if (cu.getPlay() != null) {
            ++playing;
         }

         this.umodel.addElement(this.showByName.isSelected() ? cu.toStringByName() : cu.toStringByPlay());
         this.umodelUsers.add(cu);
      }

      this.countLabel.setText(playing + "/" + c.users.size());
   }

   private void showTrayMessage() {
      this.uc.showTrayMessage("Chat weiterhin verbunden!", "Fenster kann hier erneut geöffnet werden.");
   }

   @EventHandler
   public void buildReceiver(BuildEvent event) {
      if (this.uc.getBuild() < event.build) {
         this.showStatus("Eine neue Version ist verfügbar, bitte das Programm zur Aktualisierung neu starten.");
      }
   }

   @EventHandler
   public void sendUserChatText(UserChatMessageEvent event) {
      if (!event.msg.isEmpty()) {
         if (!this.currentChannel.isEmpty()) {
            this.noautoScrollButton.setSelected(false);
            if (event.msg.startsWith("!sts ")) {
               if (event.msg.startsWith("!sts set ")) {
                  Pattern p = Pattern.compile("([a-zA-Z\\.0-9]+) *= *([a-zA-Z]+)");
                  Matcher m = p.matcher(event.msg.substring(9).trim());
                  if (m.matches()) {
                     boolean enabled = Boolean.parseBoolean(m.group(2));
                     EventBusService.getInstance().publish(new SwitchValueEvent(m.group(1), enabled));
                     this.messageAwt(event.channel, "ich", "!sts command: " + m.group(1) + "=" + enabled, false, true);
                  }
               } else if (event.msg.startsWith("!sts dump")) {
                  EventBusService.getInstance().publish(new DumpSwitchValueEvent());
                  this.messageAwt(event.channel, "ich", "!sts dump zur Console", false, true);
               } else if (event.msg.startsWith("!sts event1")) {
                  EventBusService.getInstance().publish(new BuildEvent(this.uc.getBuild() + 1));
               } else if (event.msg.startsWith("!sts event2")) {
                  BuildEvent be = new BuildEvent(this.uc.getBuild() + 1);
                  ++be.apiLevel;
                  EventBusService.getInstance().publish(be);
               }
            } else {
               if (event.msg.equals("PIEP")) {
                  if (System.currentTimeMillis() - this.lastBeep < BEEPDELAY) {
                     this.messageAwt(event.channel, "ich", "Piep so oft nicht erlaubt!", true, true);
                     return;
                  }

                  this.lastBeep = System.currentTimeMillis();
               }

               this.lastActitity = System.currentTimeMillis();
               EventBusService.getInstance().publish(new ChatMessageEvent(event.channel, event.msg));
               this.messageAwt(event.channel, "ich", event.msg, false, true);
            }
         }
      }
   }

   private void initComponents() {
      this.chatPopupMenu = new JPopupMenu();
      this.filterStatusMenu = new JCheckBoxMenuItem();
      this.jSeparator4 = new javax.swing.JPopupMenu.Separator();
      this.jMenu1 = new JMenu();
      this.sortByName = new JRadioButtonMenuItem();
      this.sortByPlay = new JRadioButtonMenuItem();
      this.jMenu2 = new JMenu();
      this.showByName = new JRadioButtonMenuItem();
      this.showByPlay = new JRadioButtonMenuItem();
      this.jSeparator6 = new javax.swing.JPopupMenu.Separator();
      this.ignoreUserMenu = new JMenuItem();
      this.ignoreUserListMenu = new JMenu();
      this.sortingBG = new ButtonGroup();
      this.showByBG = new ButtonGroup();
      this.ircPanel = new JPanel();
      this.jSplitPane1 = new JSplitPane();
      this.jPanel5 = new JPanel();
      this.jSplitPane2 = new JSplitPane();
      this.jPanel2 = new JPanel();
      this.channelScrollPane = new JScrollPane();
      this.channelList = new JTable();
      this.jPanel1 = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      this.ircUserList = new JList();
      this.jPanel3 = new JPanel();
      this.ircTextScrollPane = new JScrollPane();
      this.ircTextPane = new JTextPane();
      this.inputPanel = new JPanel();
      this.ircTextField = new JTextField() {
         private static final String HINT = "Hier Chat-Text eingeben, RETURN zum Senden";

         public void paint(Graphics g) {
            super.paint(g);
            if (this.getText().length() == 0) {
               int h = this.getHeight();
               ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
               Insets ins = this.getInsets();
               FontMetrics fm = g.getFontMetrics();
               int c0 = this.getBackground().getRGB();
               int c1 = this.getForeground().getRGB();
               int m = -16843010;
               int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
               g.setColor(new Color(c2, true));
               g.drawString("Hier Chat-Text eingeben, RETURN zum Senden", ins.left + 5, h / 2 + fm.getAscent() / 2 - 2);
            }
         }
      };
      this.progressBar = new JProgressBar();
      this.toolBar = new JToolBar();
      this.settingsButton = new JButton();
      this.jSeparator3 = new Separator();
      this.audioOnButton = new JToggleButton();
      this.noautoScrollButton = new JToggleButton();
      this.joinChannelButton = new JButton();
      this.jSeparator1 = new Separator();
      this.hideButton = new JButton();
      this.exitButton = new JButton();
      this.exitOnCloseButton = new JToggleButton();
      this.jSeparator2 = new Separator();
      this.consoleButton = new JButton();
      this.aboutButton = new JButton();
      this.jPanel4 = new JPanel();
      this.countLabel = new JLabel();
      this.statusBar = new JPanel();
      this.statusField = new JTextPane();
      this.jSeparator5 = new JSeparator();
      this.statusPanel = new JPanel();
      this.filterStatusMenu.setText("neue Statusmeldungen verstecken");
      this.filterStatusMenu.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            ChatWindow.this.filterStatusMenuItemStateChanged(evt);
         }
      });
      this.chatPopupMenu.add(this.filterStatusMenu);
      this.chatPopupMenu.add(this.jSeparator4);
      this.jMenu1.setText("Sortiere Teilnehmer nach");
      this.sortingBG.add(this.sortByName);
      this.sortByName.setSelected(true);
      this.sortByName.setText("Name");
      this.sortByName.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.sortByActionPerformed(evt);
         }
      });
      this.jMenu1.add(this.sortByName);
      this.sortingBG.add(this.sortByPlay);
      this.sortByPlay.setText("Stellwerk");
      this.sortByPlay.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.sortByActionPerformed(evt);
         }
      });
      this.jMenu1.add(this.sortByPlay);
      this.chatPopupMenu.add(this.jMenu1);
      this.jMenu2.setText("Zeige Teilnehmer als");
      this.showByBG.add(this.showByName);
      this.showByName.setSelected(true);
      this.showByName.setText("Name");
      this.showByName.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.showByNamesortByActionPerformed(evt);
         }
      });
      this.jMenu2.add(this.showByName);
      this.showByBG.add(this.showByPlay);
      this.showByPlay.setText("Spiel");
      this.showByPlay.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.showByPlaysortByActionPerformed(evt);
         }
      });
      this.jMenu2.add(this.showByPlay);
      this.chatPopupMenu.add(this.jMenu2);
      this.chatPopupMenu.add(this.jSeparator6);
      this.ignoreUserMenu.setText("Ignoriere User:");
      this.ignoreUserMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.ignoreUserMenuActionPerformed(evt);
         }
      });
      this.chatPopupMenu.add(this.ignoreUserMenu);
      this.ignoreUserListMenu.setText("Ignorierte User");
      this.chatPopupMenu.add(this.ignoreUserListMenu);
      this.setDefaultCloseOperation(0);
      this.setTitle("StellwerkSim Kommunikator");
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            ChatWindow.this.formWindowClosing(evt);
         }
      });
      this.ircPanel.setBorder(BorderFactory.createLineBorder(UIManager.getDefaults().getColor("SplitPane.shadow")));
      this.ircPanel.setComponentPopupMenu(this.chatPopupMenu);
      this.ircPanel.setInheritsPopupMenu(true);
      this.ircPanel.setPreferredSize(new Dimension(530, 300));
      this.ircPanel.setLayout(new BorderLayout());
      this.jSplitPane1.setBorder(null);
      this.jSplitPane1.setDividerLocation(200);
      this.jSplitPane1.setResizeWeight(0.2);
      this.jSplitPane1.setContinuousLayout(true);
      this.jSplitPane1.setInheritsPopupMenu(true);
      this.jSplitPane1.setOpaque(false);
      this.jPanel5.setInheritsPopupMenu(true);
      this.jPanel5.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
      this.jPanel5.setMinimumSize(new Dimension(150, 40));
      this.jPanel5.setOpaque(false);
      this.jPanel5.setPreferredSize(new Dimension(210, 42));
      this.jPanel5.setLayout(new BorderLayout());
      this.jSplitPane2.setBorder(null);
      this.jSplitPane2.setDividerLocation(130);
      this.jSplitPane2.setOrientation(0);
      this.jSplitPane2.setResizeWeight(0.2);
      this.jSplitPane2.setContinuousLayout(true);
      this.jSplitPane2.setInheritsPopupMenu(true);
      this.jSplitPane2.setOpaque(false);
      this.jPanel2.setBorder(BorderFactory.createTitledBorder(null, "Kanal", 2, 0));
      this.jPanel2.setInheritsPopupMenu(true);
      this.jPanel2.setMinimumSize(new Dimension(35, 90));
      this.jPanel2.setOpaque(false);
      this.jPanel2.setLayout(new BorderLayout());
      this.channelScrollPane.setVerticalScrollBarPolicy(22);
      this.channelScrollPane.setInheritsPopupMenu(true);
      this.channelList.setModel(this.cmodel);
      this.channelList.setFillsViewportHeight(true);
      this.channelList.setInheritsPopupMenu(true);
      this.channelList.setIntercellSpacing(new Dimension(0, 1));
      this.channelList.setSelectionMode(0);
      this.channelList.setShowHorizontalLines(false);
      this.channelList.setShowVerticalLines(false);
      this.channelScrollPane.setViewportView(this.channelList);
      this.jPanel2.add(this.channelScrollPane, "Center");
      this.jSplitPane2.setLeftComponent(this.jPanel2);
      this.jPanel1.setBorder(BorderFactory.createTitledBorder(null, "Teilnehmer", 2, 0));
      this.jPanel1.setInheritsPopupMenu(true);
      this.jPanel1.setMinimumSize(new Dimension(36, 90));
      this.jPanel1.setOpaque(false);
      this.jPanel1.setLayout(new BorderLayout());
      this.jScrollPane2.setVerticalScrollBarPolicy(22);
      this.jScrollPane2.setFocusable(false);
      this.jScrollPane2.setInheritsPopupMenu(true);
      this.jScrollPane2.setMinimumSize(new Dimension(24, 40));
      this.jScrollPane2.setPreferredSize(new Dimension(276, 40));
      this.ircUserList.setFont(this.ircUserList.getFont().deriveFont((float)this.ircUserList.getFont().getSize() - 1.0F));
      this.ircUserList.setModel(this.umodel);
      this.ircUserList.setSelectionMode(0);
      this.ircUserList.setFocusable(false);
      this.ircUserList.setInheritsPopupMenu(true);
      this.jScrollPane2.setViewportView(this.ircUserList);
      this.jPanel1.add(this.jScrollPane2, "Center");
      this.jSplitPane2.setRightComponent(this.jPanel1);
      this.jPanel5.add(this.jSplitPane2, "Center");
      this.jSplitPane1.setLeftComponent(this.jPanel5);
      this.jPanel3.setBorder(BorderFactory.createTitledBorder(null, "Text", 2, 0));
      this.jPanel3.setInheritsPopupMenu(true);
      this.jPanel3.setMinimumSize(new Dimension(200, 52));
      this.jPanel3.setOpaque(false);
      this.jPanel3.setLayout(new BorderLayout(0, 3));
      this.ircTextScrollPane.setInheritsPopupMenu(true);
      this.ircTextPane.setEditable(false);
      this.ircTextPane.setContentType("text/html");
      this.ircTextPane.setEditorKit(new HTMLEditorKitCustomCss());
      this.ircTextPane.setInheritsPopupMenu(true);
      this.ircTextScrollPane.setViewportView(this.ircTextPane);
      this.jPanel3.add(this.ircTextScrollPane, "Center");
      this.inputPanel.setLayout(new CardLayout());
      this.ircTextField.setDocument(new BoundedPlainDocument(200));
      this.ircTextField.setToolTipText("Textfeld zum chatten, RETURN zum Senden des Texts.");
      this.ircTextField.setInheritsPopupMenu(true);
      this.ircTextField.setMinimumSize(new Dimension(140, 18));
      this.ircTextField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.ircTextFieldActionPerformed(evt);
         }
      });
      this.inputPanel.add(this.ircTextField, "input");
      this.inputPanel.add(this.progressBar, "bar");
      this.jPanel3.add(this.inputPanel, "South");
      this.jSplitPane1.setRightComponent(this.jPanel3);
      this.ircPanel.add(this.jSplitPane1, "Center");
      this.getContentPane().add(this.ircPanel, "Center");
      this.toolBar.setFloatable(false);
      this.toolBar.setRollover(true);
      this.settingsButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/settings_16x16.png")));
      this.settingsButton.setText("Einstellungen");
      this.settingsButton.setFocusable(false);
      this.settingsButton.setHorizontalTextPosition(0);
      this.settingsButton.setVerticalAlignment(3);
      this.settingsButton.setVerticalTextPosition(3);
      this.settingsButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.settingsButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.settingsButton);
      this.toolBar.add(this.jSeparator3);
      this.audioOnButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/bell_16x16.png")));
      this.audioOnButton.setText("Chat Ton");
      this.audioOnButton.setToolTipText("Ton bei Meldungen");
      this.audioOnButton.setFocusable(false);
      this.audioOnButton.setHorizontalTextPosition(0);
      this.audioOnButton.setVerticalAlignment(3);
      this.audioOnButton.setVerticalTextPosition(3);
      this.audioOnButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.audioOnButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.audioOnButton);
      this.noautoScrollButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/pausescroll_16x16.png")));
      this.noautoScrollButton.setText("Chat Pause");
      this.noautoScrollButton.setToolTipText("Chat nicht mehr automatisch scrollen");
      this.noautoScrollButton.setFocusable(false);
      this.noautoScrollButton.setHorizontalTextPosition(0);
      this.noautoScrollButton.setVerticalAlignment(3);
      this.noautoScrollButton.setVerticalTextPosition(3);
      this.noautoScrollButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.noautoScrollButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.noautoScrollButton);
      this.joinChannelButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/chat1_16x16.png")));
      this.joinChannelButton.setText("Weiterer Raum");
      this.joinChannelButton.setFocusable(false);
      this.joinChannelButton.setHorizontalTextPosition(0);
      this.joinChannelButton.setVerticalAlignment(3);
      this.joinChannelButton.setVerticalTextPosition(3);
      this.joinChannelButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.joinChannelButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.joinChannelButton);
      this.toolBar.add(this.jSeparator1);
      this.hideButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/hide_16x16.png")));
      this.hideButton.setText("Verstecken");
      this.hideButton.setToolTipText("Versteckt das Chat-Fenster, kann per Tray wieder geöffnet werden.");
      this.hideButton.setFocusable(false);
      this.hideButton.setHorizontalTextPosition(0);
      this.hideButton.setVerticalAlignment(3);
      this.hideButton.setVerticalTextPosition(3);
      this.hideButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.hideButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.hideButton);
      this.exitButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/exit_16x16.png")));
      this.exitButton.setText("Beenden");
      this.exitButton.setToolTipText("Beendet das STS Programm direkt.");
      this.exitButton.setFocusable(false);
      this.exitButton.setHorizontalTextPosition(0);
      this.exitButton.setVerticalAlignment(3);
      this.exitButton.setVerticalTextPosition(3);
      this.exitButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.exitButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.exitButton);
      this.exitOnCloseButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/Close-to-exit_16x16.png")));
      this.exitOnCloseButton.setText("Schließen beendet");
      this.exitOnCloseButton.setToolTipText("Wenn aktiv, bewirkt das Schließen des Chat-Fensters das Beenden vom STS.");
      this.exitOnCloseButton.setFocusable(false);
      this.exitOnCloseButton.setHorizontalTextPosition(0);
      this.exitOnCloseButton.setVerticalAlignment(3);
      this.exitOnCloseButton.setVerticalTextPosition(3);
      this.exitOnCloseButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.exitOnCloseButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.exitOnCloseButton);
      this.toolBar.add(this.jSeparator2);
      this.consoleButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/console_16x16.png")));
      this.consoleButton.setText("Console");
      this.consoleButton.setToolTipText("Öffnet die Meldungskonsole, wird ggf. bei Fehlern benötigt.");
      this.consoleButton.setFocusable(false);
      this.consoleButton.setHorizontalTextPosition(0);
      this.consoleButton.setVerticalAlignment(3);
      this.consoleButton.setVerticalTextPosition(3);
      this.consoleButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.consoleButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.consoleButton);
      this.aboutButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/schaltungen/p/about_16x16.png")));
      this.aboutButton.setText("Über");
      this.aboutButton.setFocusable(false);
      this.aboutButton.setHorizontalTextPosition(0);
      this.aboutButton.setMaximumSize(new Dimension(51, 43));
      this.aboutButton.setMinimumSize(new Dimension(51, 43));
      this.aboutButton.setPreferredSize(new Dimension(51, 43));
      this.aboutButton.setVerticalAlignment(3);
      this.aboutButton.setVerticalTextPosition(3);
      this.aboutButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ChatWindow.this.aboutButtonActionPerformed(evt);
         }
      });
      this.toolBar.add(this.aboutButton);
      this.jPanel4.setLayout(new BorderLayout());
      this.countLabel.setHorizontalAlignment(4);
      this.countLabel.setText("Spieler/Anwesende");
      this.countLabel.setToolTipText("Spieler/Anwesende");
      this.jPanel4.add(this.countLabel, "South");
      this.toolBar.add(this.jPanel4);
      this.getContentPane().add(this.toolBar, "North");
      this.statusBar.setMaximumSize(new Dimension(32767, 20));
      this.statusBar.setMinimumSize(new Dimension(10, 20));
      this.statusBar.setPreferredSize(new Dimension(10, 20));
      this.statusBar.setLayout(new GridBagLayout());
      this.statusField.setEditable(false);
      this.statusField.setBorder(null);
      this.statusField.setContentType("text/html");
      this.statusField.setOpaque(false);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(0, 3, 0, 0);
      this.statusBar.add(this.statusField, gridBagConstraints);
      this.jSeparator5.setOrientation(1);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 3;
      this.statusBar.add(this.jSeparator5, gridBagConstraints);
      this.statusPanel.setLayout(new FlowLayout(2, 1, 0));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      this.statusBar.add(this.statusPanel, gridBagConstraints);
      this.getContentPane().add(this.statusBar, "South");
      this.pack();
   }

   private void ircTextFieldActionPerformed(ActionEvent evt) {
      String msg = this.ircTextField.getText().trim();
      this.ircTextField.setText("");
      if (!msg.isEmpty() && !this.currentChannel.isEmpty()) {
         this.sendUserChatText(new UserChatMessageEvent(this.currentChannel, msg));
      }
   }

   private void formWindowClosing(WindowEvent evt) {
      if (!this.exitOnCloseButton.isSelected() && this.withTray) {
         this.setVisible(false);
         this.showTrayMessage();
      } else if (closePrefs.Parts.COMMUNICATOR.ask(this.uc, this, "Wirklich alles beenden?")) {
         this.uc.exit();
      }
   }

   private void hideButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
      this.showTrayMessage();
   }

   private void exitButtonActionPerformed(ActionEvent evt) {
      this.uc.exit();
   }

   private void exitOnCloseButtonActionPerformed(ActionEvent evt) {
      this.chatWindowPrefs.putBoolean("exitOnCloseV2", this.exitOnCloseButton.isSelected());
      this.chatWindowPrefs.flush();
      EventBusService.getInstance().publish(new ExitOnCloseChangedEvent());
   }

   private void aboutButtonActionPerformed(ActionEvent evt) {
      this.uc.showAbout();
   }

   private void audioOnButtonActionPerformed(ActionEvent evt) {
      this.asettings.playChatSettings().setEnabled(this.audioOnButton.isSelected());
   }

   private void consoleButtonActionPerformed(ActionEvent evt) {
      EventBusService.getInstance().publish(new ShowConsoleEvent());
   }

   private void settingsButtonActionPerformed(ActionEvent evt) {
      EventBusService.getInstance().publish(new ShowSettingsEvent());
   }

   private void sortByActionPerformed(ActionEvent evt) {
      this.resortUser();
   }

   private void filterStatusMenuItemStateChanged(ItemEvent evt) {
      try {
         this.node.putBoolean("filterStatus", this.filterStatusMenu.isSelected());
         this.node.flush();
      } catch (BackingStoreException var3) {
      }
   }

   private void noautoScrollButtonActionPerformed(ActionEvent evt) {
   }

   private void ignoreUserMenuActionPerformed(ActionEvent evt) {
      this.addIgnoreUser();
   }

   private void showByNamesortByActionPerformed(ActionEvent evt) {
      this.resortUser();
   }

   private void showByPlaysortByActionPerformed(ActionEvent evt) {
      this.resortUser();
   }

   private void joinChannelButtonActionPerformed(ActionEvent evt) {
      String channel = JOptionPane.showInputDialog(this, "Name des zu betretenden weiteren Raums", "Raumname", 3);
      if (channel != null) {
         channel = channel.trim().replaceAll("\\W", "").toLowerCase();
         if (!channel.isEmpty()) {
            String jchannel = "p_" + channel;
            if (jchannel.length() > 8) {
               jchannel = jchannel.substring(0, 8);
            }

            this.uc.getChat().joinChannel(jchannel, channel);
         }
      }
   }

   private class ChannelModel extends AbstractTableModel {
      private final ArrayList<ChatWindow.JoinedChannel> channels = new ArrayList();

      private ChannelModel() {
         super();
      }

      public Class<?> getColumnClass(int columnIndex) {
         return columnIndex == 2 ? Integer.class : String.class;
      }

      public String getColumnName(int column) {
         return null;
      }

      public boolean isCellEditable(int rowIndex, int columnIndex) {
         return false;
      }

      public int getRowCount() {
         return this.getSize();
      }

      public int getColumnCount() {
         return 3;
      }

      public int getSize() {
         return this.channels.size();
      }

      public ChatWindow.JoinedChannel getElementAt(int index) {
         return (ChatWindow.JoinedChannel)this.channels.get(index);
      }

      public int getIndex(ChatWindow.JoinedChannel ch) {
         return this.channels.indexOf(ch);
      }

      private void remove(ChatWindow.JoinedChannel e) {
         int idx = this.channels.indexOf(e);
         if (idx >= 0) {
            this.channels.remove(e);
            this.fireTableRowsDeleted(idx, idx);
         }
      }

      private void add(ChatWindow.JoinedChannel joinedChannel) {
         if (joinedChannel != null) {
            this.channels.add(joinedChannel);
            this.fireTableRowsInserted(this.channels.size() - 1, this.channels.size() - 1);
         }
      }

      public Object getValueAt(int rowIndex, int columnIndex) {
         ChatWindow.JoinedChannel ch = (ChatWindow.JoinedChannel)this.channels.get(rowIndex);
         switch(columnIndex) {
            case 0:
               return ch.unread ? "!" : "";
            case 1:
               return ch;
            case 2:
               return ch.users.size();
            default:
               return null;
         }
      }
   }

   private static class JoinedChannel implements Comparable<ChatWindow.JoinedChannel> {
      long lastMessageTime = 0L;
      final ChannelsNameParser.ChannelName channelName;
      final LinkedList<String> text = new LinkedList();
      final ArrayList<ChatUser> users = new ArrayList();
      boolean unread = false;
      private final String topic;

      private JoinedChannel(IrcChannel i) {
         super();
         this.channelName = i.channel;
         String t = i.getTopic();
         if (t != null) {
            t = t.trim();
         }

         if (t != null && t.isEmpty()) {
            t = null;
         }

         this.topic = t;
      }

      public String toString() {
         return this.topic != null ? this.channelName.title + " (" + this.topic + ")" : this.channelName.title;
      }

      public int compareTo(ChatWindow.JoinedChannel o) {
         return this.channelName.compareTo(o.channelName);
      }
   }
}
