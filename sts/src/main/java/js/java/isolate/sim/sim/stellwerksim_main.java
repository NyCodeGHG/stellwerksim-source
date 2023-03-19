package js.java.isolate.sim.sim;

import de.deltaga.eb.EventHandler;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.JPopupMenu.Separator;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.Timer;
import js.java.isolate.sim.fatcodeprovider;
import js.java.isolate.sim.autoMsg.control;
import js.java.isolate.sim.autoMsg.setupFrame;
import js.java.isolate.sim.eventsys.event;
import js.java.isolate.sim.eventsys.thema;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.colorSystem.gleisColor;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.bahnsteigDetailStore;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.gleisbild.gleisbildViewPanel;
import js.java.isolate.sim.gleisbild.scaleHolder;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.sim.alarmClock.alarmClock;
import js.java.isolate.sim.sim.botcom.BotCalling;
import js.java.isolate.sim.sim.botcom.BotChat;
import js.java.isolate.sim.sim.botcom.NullChat;
import js.java.isolate.sim.sim.botcom.SandboxChat;
import js.java.isolate.sim.sim.botcom.chatInterface;
import js.java.isolate.sim.sim.botcom.events.BPosChange;
import js.java.isolate.sim.sim.botcom.events.EPosChange;
import js.java.isolate.sim.sim.botcom.events.ElementOccurance;
import js.java.isolate.sim.sim.botcom.events.SBldChange;
import js.java.isolate.sim.sim.botcom.events.XPosChange;
import js.java.isolate.sim.sim.botcom.events.ZugUserText;
import js.java.isolate.sim.sim.goodies.cheatManager;
import js.java.isolate.sim.sim.plugin.ServImpl;
import js.java.isolate.sim.sim.plugin.controlServ;
import js.java.isolate.sim.sim.plugin.pluginServ;
import js.java.isolate.sim.sim.redirectInfo.zidRedirectPanel;
import js.java.isolate.sim.simTest.simTest;
import js.java.isolate.sim.structServ.structServer;
import js.java.isolate.sim.structServ.structinfo;
import js.java.isolate.sim.toolkit.HyperlinkCaller;
import js.java.isolate.sim.toolkit.threadHelper;
import js.java.isolate.sim.zug.verspaetungColumnSorterOption;
import js.java.isolate.sim.zug.zug;
import js.java.isolate.sim.zug.zugEmitter;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.adapter.closePrefs;
import js.java.schaltungen.adapter.simPrefs;
import js.java.schaltungen.audio.AudioController;
import js.java.schaltungen.audio.AudioSettings;
import js.java.schaltungen.audio.AudioSettingsChangedEvent;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.chatcomng.GameInfoEvent;
import js.java.schaltungen.chatcomng.OCCU_KIND;
import js.java.schaltungen.settings.PrefsChangedEvent;
import js.java.schaltungen.settings.RealisticSure;
import js.java.schaltungen.timesystem.TimeFormat;
import js.java.schaltungen.timesystem.simTimeHolder;
import js.java.schaltungen.timesystem.timedelivery;
import js.java.schaltungen.timesystem.timedeliveryBase;
import js.java.schaltungen.timesystem.timedeliveryEmitter;
import js.java.schaltungen.timesystem.timedeliveryLoaded;
import js.java.schaltungen.timesystem.timedeliverySynced;
import js.java.schaltungen.webservice.StoreEventOccured;
import js.java.tools.TextHelper;
import js.java.tools.prefs;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.dialogs.message0;
import js.java.tools.dialogs.message1;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

public class stellwerksim_main extends JFrame implements BotCalling, Runnable, xmllistener, structinfo, timedelivery, fatcodeprovider, Simulator, chatReport {
   private static FATwriter debugMode = null;
   protected final UserContext uc;
   public static final int OFFLINE_NO = 0;
   public static final int OFFLINE_SIMPEL = 1;
   public static final int OFFLINE_JWS = 2;
   public static final int OFFLINE_DOWNLOAD = 3;
   public static final int OFFLINE_TEST_TESTSYSTEM = 4;
   public static final int OFFLINE_TEST_AKTIVSYSTEM = 5;
   private final gleisbildModelSts glbModel;
   private gleisbildViewPanel glbPanel;
   private final gleisbildSimControl glbControl;
   private Timer game_time;
   private String updateurl;
   private chatInterface my_chat = new NullChat();
   private int offlinemode = 0;
   private final boolean realistic;
   private boolean emitterMode = false;
   private boolean botMode = false;
   private boolean noreserve = false;
   private boolean qsmode = false;
   private boolean invisibleMode = false;
   private String qsmodus = "";
   private final prefs prefs;
   private final AudioSettings asettings;
   private volatile boolean running = true;
   private boolean extraMode = false;
   private boolean syncedTime = false;
   private boolean redirectAllowedMode = false;
   private boolean allowOneRedirect = false;
   private chatPanelBase ircPanel = null;
   private extrasPanel extrasPanel = null;
   private infoPanel dataPanel = null;
   private zugUndPlanPanel fahrplanPanel = null;
   private structServer structserv = null;
   private long prefsDate = 0L;
   private controlServ ctrlSrv = null;
   private pluginServ pluginS = null;
   private ConcurrentHashMap<Integer, zidRedirectPanel> redirects = new ConcurrentHashMap();
   private cheatManager cheatMgr = null;
   private fsallocator fsalloc;
   private final MultiWindowManager mWindowManager;
   private final LatencyMeasure uepFsLatencyMeasure;
   private zugEmitter zugemitterWindow = null;
   private Thread runner = null;
   externalPanel eIRC = null;
   JRadioButtonMenuItem lastSel = null;
   private elementWindow elmWindow = null;
   private bueTimeWindow bueWindow = null;
   private simTimeHolder simTime = new simTimeHolder();
   private ButtonGroup StyleButtonGroup;
   private JMenuItem abfahrtMonitorMenu;
   private JMenuItem aboutMenuItem;
   private JCheckBoxMenuItem akzeptorSoundMenu;
   private JMenuItem alarmClockItem;
   private JCheckBoxMenuItem alterColorMenu;
   private JMenuItem autoMsgSetupMenu;
   private JMenuItem bahnhofsuhr24Menu;
   private JMenuItem bahnhofsuhrMenu;
   private JCheckBoxMenuItem bueTimeWindowMenu;
   private JCheckBoxMenuItem büSoundMenu;
   private ButtonGroup chatMenuButtonGroup;
   private JMenuItem cheatMenu;
   private JPanel controlPanel;
   private JMenuItem debugMenuItem;
   private JCheckBoxMenuItem elementWindowMenu;
   private JMenuItem exitMenuItem;
   private JMenu extrasMenu;
   private JPanel extrasPanelBar;
   private JMenu fileMenu;
   private JRadioButtonMenuItem funMenu;
   private JCheckBoxMenuItem funkSoundMenu;
   private JCheckBoxMenuItem gleisGruppeItem;
   private JPanel gleisbildHauptPanel;
   private JMenuItem gleisbildSmoothDecrementMenu;
   private JMenuItem gleisbildSmoothOffMenu;
   private JMenuItem gleisbildSmoothOnMenu;
   private JScrollPane gleisedscroller;
   private JMenu helpMenu;
   private JMenuItem infoMenuItem;
   private JMenu jMenu1;
   private Separator jSeparator10;
   private Separator jSeparator11;
   private Separator jSeparator2;
   private Separator jSeparator4;
   private Separator jSeparator5;
   private Separator jSeparator6;
   private Separator jSeparator7;
   private Separator jSeparator9;
   private JMenu layoutMenu;
   private JCheckBoxMenuItem longDualKeyMenu;
   private JSplitPane mainSplitPane;
   private JMenuBar menuBar;
   private JMenuItem miniViewMenu;
   private JCheckBoxMenuItem nightViewMenu;
   private JMenu optionsMenu;
   private JRadioButtonMenuItem realisticMenu;
   private ButtonGroup replayGroup;
   private ButtonGroup scaleButtonGroup;
   private JMenu scaleDifferentMenu;
   private JMenu scaleFixedMenu;
   private JMenu scaleMenu;
   private JMenuItem searchElementMenu;
   private JMenuItem searchMenu;
   private JMenuItem sortOrderMenu;
   private JMenu soundMenu;
   private JCheckBoxMenuItem startPluginInterface;
   private ButtonGroup stsModusGroup;
   private JMenu tuningMenu;
   private JMenu visualHelpMenu;
   private JRadioButtonMenuItem winLayout1;
   private JRadioButtonMenuItem winLayout2;
   private ButtonGroup winLayoutGroup;
   private JMenu winLayoutMenu;
   private JRadioButtonMenuItem winLayoutMulti;
   private JRadioButtonMenuItem winLayoutMulti2;
   private JMenuItem wswMenu;
   private JCheckBoxMenuItem zugSoundMenu;
   private JCheckBoxMenuItem zählwerkSoundMenu;
   private StringBuffer senddata = null;
   private xmlreader xmlr = null;
   private boolean firstRun = true;
   private int syncdelay = 120;
   private LinkedList mitteilungen = null;
   private JCheckBoxMenuItem pauseMenu1 = null;
   private JMenuItem audioPrefsMenu1 = null;
   private int zählwerk = 0;
   private JPanel over_pan = null;
   private boolean timerStarted = false;
   private static final String aMsgPre = "Automatische Zugmeldung: ";
   private TimeFormat msgdf = TimeFormat.getInstance(TimeFormat.STYLE.HMS);
   private control msgControl = null;
   private FATwriter dumperMode = null;
   private LinkedList<JMenuItem> dumperMenu = new LinkedList();

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public stellwerksim_main(UserContext uc, gleisbildModelSts gbd, String uurl) {
      this(uc, gbd, uurl, false);
   }

   public stellwerksim_main(UserContext uc, gleisbildModelSts gbd, String uurl, boolean forceNorealistic) {
      super();
      this.uc = uc;
      this.glbModel = gbd;
      this.updateurl = uurl;
      this.uepFsLatencyMeasure = new LatencyMeasure(uc, "Uep-Fs");

      try {
         File homeFile = new File(System.getProperty("user.home"));
         File betaFile = new File(homeFile, "sim.sts.betamode");
         if (betaFile.canRead()) {
            BufferedReader in = new BufferedReader(new FileReader(betaFile));
            Throwable var8 = null;

            try {
               String line = in.readLine().trim();
               this.extraMode = "sie sind hier".equals(line);
            } catch (Throwable var20) {
               var8 = var20;
               throw var20;
            } finally {
               if (in != null) {
                  if (var8 != null) {
                     try {
                        in.close();
                     } catch (Throwable var19) {
                        var8.addSuppressed(var19);
                     }
                  } else {
                     in.close();
                  }
               }
            }
         }
      } catch (Exception var22) {
      }

      if (this.extraMode) {
         Logger.getLogger("stslogger").log(Level.INFO, "Extra Mode by " + this.getParameter("nick3"));
         System.out.println("ßMode");
         message0 m = new message0(this, "ßMode", "Beta Test Modus:\nZur besseren Fehleranalyse werden alle Aktivitäten geloggt.");
         m.setVisible(true);
      }

      this.glbControl = new gleisbildSimControl(uc);
      this.glbControl.setSimTime(this);
      if ("true".equals(uc.getParameter("emittermode"))) {
         this.emitterMode = true;
      }

      if ("true".equals(uc.getParameter("usebot"))) {
         this.botMode = true;
         if ("true".equals(uc.getParameter("redirectAllowed"))) {
            this.redirectAllowedMode = true;
         }
      } else if (!this.emitterMode) {
         Iterator<gleis> it = this.glbModel.findIterator(new Object[]{gleis.ELEMENT_AIDDISPLAY});

         while(it.hasNext()) {
            gleis gl = (gleis)it.next();
            gl.hideDisplay();
         }
      }

      if ("true".equals(uc.getParameter("noreserve"))) {
         this.noreserve = true;
      }

      if (uc.getParameter("offline") != null) {
         try {
            this.offlinemode = Integer.parseInt(uc.getParameter("offline"));
         } catch (NumberFormatException var18) {
            System.out.println("Offline-Value NFE!");
         }
      }

      this.asettings = uc.getAudioSettings();
      this.prefs = new simPrefs();
      boolean _realistic = false;
      _realistic = this.prefs.getBoolean("realistic", false);
      this.realistic = !forceNorealistic && _realistic;
      this.initComponents();
      this.initMyComponents();
      this.initValues();
      this.realisticMenu.setSelected(this.realistic);
      this.nightViewMenu.setSelected(this.prefs.getBoolean("isNightMode", false));
      if (this.nightViewMenu.isSelected()) {
         gleisColor.getInstance().setNachtView();
         this.glbControl.fastPaint();
      }

      if (this.botMode) {
         this.prefsDate = this.prefs.getLong("timestamp", 0L);
      }

      this.setStoreAudioPrefsMenu();
      this.syncStoreAudioPrefsMenu(null);
      this.alterColorMenu.setSelected(this.prefs.getBoolean("alterColor", false));
      this.longDualKeyMenu.setSelected(this.prefs.getBoolean("longDualKey", false));
      if (pluginServ.mayCreateInstance(this)) {
         this.startPluginInterface.setEnabled(true);
         this.startPluginInterface.setSelected(this.prefs.getBoolean("startPlugin", false));
         this.startPluginInterfaceItemStateChanged(null);
      } else {
         this.startPluginInterface.setEnabled(false);
      }

      this.addRestartHint(this.funMenu);
      this.addRestartHint(this.realisticMenu);
      this.msgControl = new control(this, this.glbModel);
      this.gleisGruppeItem.setSelected(this.prefs.getBoolean("gleisGroup", false));
      this.gleisGruppeItem.setEnabled(!this.realistic);
      if (this.botMode) {
         this.prefs.putLong("timestamp", System.currentTimeMillis());
         this.prefs.flush();
      }

      if ("true".equals(uc.getParameter("qsmode"))) {
         this.qsmode = true;
         this.fahrplanPanel.setQSmenu();
         if (uc.getParameter("qstestmodus") != null) {
            this.qsmodus = uc.getParameter("qstestmodus");
         }
      }

      if ("true".equals(uc.getParameter("invisible"))) {
         this.invisibleMode = true;
      }

      this.fsalloc = this.glbModel.getAdapter().getFSallocator();
      if (this.fsalloc == null) {
         this.fsalloc = new fsallocator(this);
      }

      if (this.emitterMode) {
         this.setSimTime(new timedeliveryEmitter());
      } else if (this.offlinemode == 0 || this.offlinemode == 5 || this.offlinemode == 1) {
         timedeliverySynced td = new timedeliverySynced(this.getParameter("running"));
         this.setSimTime(td);
         uc.addCloseObject(td);
      }

      uc.addCloseObject(this.simTime);
      this.setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/js/java/tools/resources/funk.gif")));
      this.runner = new Thread(this);
      this.runner.setName("main loop");
      if (this.botMode && !this.extraMode && this.prefsDate + 600000L > System.currentTimeMillis()) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "P-Zeit+10: " + this.getParameter("nick3"));
      }

      if (!this.botMode || this.realistic) {
         this.cheatMenu.getParent().remove(this.cheatMenu);
      }

      this.setName("sim-main-window");
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
      this.mWindowManager = new MultiWindowManager(
         this, this.mainSplitPane, this.controlPanel, this.fahrplanPanel, this.winLayout1, this.winLayout2, this.winLayoutMulti, this.winLayoutMulti2
      );
      uc.busSubscribe(this);
   }

   @EventHandler
   public void prefsChanged(PrefsChangedEvent event) {
      if (!event.fromMe(this)) {
         this.gleisGruppeItem.setSelected(this.prefs.getBoolean("gleisGroup", false));
         this.nightViewMenu.setSelected(this.prefs.getBoolean("isNightMode", false));
         this.nightViewMenuActionPerformed(null);
         this.alterColorMenu.setSelected(this.prefs.getBoolean("alterColor", false));
         this.funMenu.setSelected(!this.prefs.getBoolean("realistic", false));
         this.realisticMenu.setSelected(this.prefs.getBoolean("realistic", false));
         this.longDualKeyMenu.setSelected(this.prefs.getBoolean("longDualKey", false));
      }
   }

   @Override
   public fsallocator getFSallocator() {
      return this.fsalloc;
   }

   public void setVisible(boolean v) {
      super.setVisible(v);
      if (v) {
         if (this.botMode) {
            this.cheatMgr = new cheatManager(this, this.cheatMenu);
            this.uc.addCloseObject(this.cheatMgr);
         }

         if (this.emitterMode) {
            this.zugemitterWindow = zugEmitter.show(this, this, this.glbModel);
            this.uc.addCloseObject(this.zugemitterWindow);
         }

         this.runner.start();
      } else if (this.zugemitterWindow != null) {
         this.zugemitterWindow.setVisible(false);
         this.zugemitterWindow = null;
      }
   }

   private void addRestartHint(JMenuItem m) {
      String spaceholder = "                                        ";
      String t = m.getText() + "                                        ";
      m.setLayout(new BorderLayout());
      m.setText(t);
      m.add(new JLabel("<html><i>(ab nächstem Spiel)</i></html>"), "East");
   }

   public boolean isExtraMode() {
      return this.extraMode;
   }

   @Override
   public boolean isRealistic() {
      return this.realistic;
   }

   public boolean isRedirectAllowedMode() {
      return this.redirectAllowedMode || this.allowOneRedirect;
   }

   @Override
   public void allowOneRedirect() {
      this.allowOneRedirect = true;
   }

   private void setAlterColor() {
      gleisColor.getInstance().setAlterColor();
      this.extrasPanel.setAlterColor();
      this.glbControl.fastPaint();
   }

   private void setNormalColor() {
      gleisColor.getInstance().setNormalColor();
      this.extrasPanel.setNormalColor();
      this.glbControl.fastPaint();
   }

   @Override
   public gleisbildModelSts getGleisbild() {
      return this.glbModel;
   }

   public gleisbildSimControl getControl() {
      return this.glbControl;
   }

   public chatInterface getChat() throws NoSuchElementException {
      if (this.my_chat == null || this.emitterMode && !this.botMode && !this.qsmode) {
         throw new NoSuchElementException("no chat");
      } else {
         return this.my_chat;
      }
   }

   private void initMyComponents() {
      this.glbPanel = new gleisbildViewPanel(this.uc, this.glbModel, this.glbControl);
      this.gleisedscroller.setViewportView(this.glbPanel);
      this.gleisedscroller.getViewport().setScrollMode(0);
      if (this.botMode && this.glbModel.getPhonebook().getOwn() != null && !this.glbModel.getPhonebook().getOwn().isEmpty()) {
         JPanel p = new JPanel();
         p.setOpaque(false);
         JMenu m = new JMenu("StiTz Telefonbuch");
         m.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/phone16.png")));
         m.add(new phonebookpanel(this.glbModel.getPhonebook()));
         this.menuBar.add(m);
      }

      this.initMyMenus();
      this.ircPanel = new chatPanel(this.uc);
      this.fahrplanPanel = new zugUndPlanPanel(this, this.glbModel, this.ircPanel, this.prefs);
      this.fahrplanPanel.initZugmenu();
      this.controlPanel.add(this.fahrplanPanel, "Center");
      this.dataPanel = new infoPanel(this);
      this.fahrplanPanel.setScrollDataPanel(this.dataPanel);
      this.extrasPanel = new extrasPanel(this, this.glbControl);
      this.extrasPanelBar.add(this.extrasPanel, "Center");
      this.extrasPanel.initGruppentaster();
   }

   private void initMyMenus() {
      for(String scale : scaleHolder.publicScales) {
         JMenu sMenu = this.scaleFixedMenu;
         if (scale.contains(":")) {
            sMenu = this.scaleDifferentMenu;
         }

         this.addScaleMenu(sMenu, scale + "%", scale, scale.equals("100"));
      }

      this.scaleMenu.addSeparator();
      JMenuItem scaleWindowButton = new JMenuItem("Regler...");
      scaleWindowButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            new scaleWindow(stellwerksim_main.this, stellwerksim_main.this.scaleButtonGroup).setVisible(true);
         }
      });
      scaleWindowButton.setAccelerator(KeyStroke.getKeyStroke(90, 2));
      this.scaleMenu.add(scaleWindowButton);
      if (this.isDevSandbox()) {
         JMenuItem simTestMenu = new JMenuItem("Tester...");
         simTestMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               new simTest(stellwerksim_main.this).setVisible(true);
            }
         });
         this.helpMenu.addSeparator();
         this.helpMenu.add(simTestMenu);
      }

      int cnt = this.menuBar.getMenuCount();

      for(int i = 0; i < cnt; ++i) {
         JMenu m = this.menuBar.getMenu(i);
         if (m != null) {
            m.setMargin(new Insets(2, 4, 2, 4));
            this.setMenuMargins(m);
         }
      }
   }

   private void setMenuMargins(JMenu menu) {
      for(Component m : menu.getMenuComponents()) {
         if (m instanceof JMenuItem) {
            JMenuItem mi = (JMenuItem)m;
            mi.setMargin(new Insets(2, 0, 2, 0));
            if (mi instanceof JMenu) {
               this.setMenuMargins((JMenu)mi);
            }
         }
      }
   }

   private void addScaleMenu(JMenu sMenu, String label, String cmd, boolean selected) {
      JRadioButtonMenuItem radioButton = new JRadioButtonMenuItem();
      this.scaleButtonGroup.add(radioButton);
      radioButton.setText(label);
      radioButton.setActionCommand(cmd);
      radioButton.setSelected(selected);
      radioButton.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == 1) {
               stellwerksim_main.this.scaleChanged(evt);
            }
         }
      });
      sMenu.add(radioButton);
   }

   private void scaleChanged(ItemEvent evt) {
      try {
         String cmd = ((JRadioButtonMenuItem)evt.getSource()).getActionCommand();
         this.glbControl.setScalePreset(cmd);
      } catch (Exception var3) {
      }
   }

   private void initValues() {
      if (this.getParameter("overlay") != null) {
         this.showOverlay(this.getParameter("overlay"));
      }

      this.game_time = new Timer(this, this.dataPanel.getTimeTextField(), this.dataPanel.getTextTextField());
      this.uc.addCloseObject(this.game_time);
      if (!this.emitterMode) {
         this.message("Starten: Lade Zeit und Fahrplan...", stellwerksim_main.MSGLEVELS.NORMAL);
      } else {
         this.message("Starten: Emitterfenster wird geöffnet.", stellwerksim_main.MSGLEVELS.NORMAL);
         this.message("Falls das Emitterfenster nicht sichtbar ist, wurde es verdeckt.", stellwerksim_main.MSGLEVELS.HINT);
      }

      String n = "";
      if (this.getParameter("night") != null && this.getParameter("night").equals("true")) {
         n = n + "  ### Nachtansicht aktiviert";
      }

      if (this.realistic) {
         this.glbModel.buildGleisGroup();
         this.uc.showTopLevelMessage("Der REALISTISCHE Simulationsmodus ist aktiviert.", 20);
         n = n + " /R/";
      }

      String t = this.glbModel.getAnlagenname() + " - StellwerkSim - www.stellwerksim.de - " + "(c) JS 2004-2023" + " " + n;
      this.setTitle(t);
   }

   private void initComponents() {
      this.StyleButtonGroup = new ButtonGroup();
      this.replayGroup = new ButtonGroup();
      this.scaleButtonGroup = new ButtonGroup();
      this.chatMenuButtonGroup = new ButtonGroup();
      this.stsModusGroup = new ButtonGroup();
      this.winLayoutGroup = new ButtonGroup();
      this.mainSplitPane = new JSplitPane();
      this.gleisbildHauptPanel = new JPanel();
      this.extrasPanelBar = new JPanel();
      this.gleisedscroller = new JScrollPane();
      this.controlPanel = new JPanel();
      this.menuBar = new JMenuBar();
      this.fileMenu = new JMenu();
      this.searchMenu = new JMenuItem();
      this.searchElementMenu = new JMenuItem();
      this.jSeparator6 = new Separator();
      this.bahnhofsuhrMenu = new JMenuItem();
      this.bahnhofsuhr24Menu = new JMenuItem();
      this.miniViewMenu = new JMenuItem();
      this.jSeparator7 = new Separator();
      this.exitMenuItem = new JMenuItem();
      this.layoutMenu = new JMenu();
      this.scaleMenu = new JMenu();
      this.scaleFixedMenu = new JMenu();
      this.scaleDifferentMenu = new JMenu();
      this.gleisGruppeItem = new JCheckBoxMenuItem();
      this.nightViewMenu = new JCheckBoxMenuItem();
      this.winLayoutMenu = new JMenu();
      this.winLayout1 = new JRadioButtonMenuItem();
      this.winLayout2 = new JRadioButtonMenuItem();
      this.winLayoutMulti = new JRadioButtonMenuItem();
      this.winLayoutMulti2 = new JRadioButtonMenuItem();
      this.jSeparator10 = new Separator();
      this.visualHelpMenu = new JMenu();
      this.alterColorMenu = new JCheckBoxMenuItem();
      this.longDualKeyMenu = new JCheckBoxMenuItem();
      this.elementWindowMenu = new JCheckBoxMenuItem();
      this.jSeparator2 = new Separator();
      this.tuningMenu = new JMenu();
      this.gleisbildSmoothOffMenu = new JMenuItem();
      this.gleisbildSmoothOnMenu = new JMenuItem();
      this.gleisbildSmoothDecrementMenu = new JMenuItem();
      this.extrasMenu = new JMenu();
      this.alarmClockItem = new JMenuItem();
      this.autoMsgSetupMenu = new JMenuItem();
      this.jSeparator9 = new Separator();
      this.bueTimeWindowMenu = new JCheckBoxMenuItem();
      this.abfahrtMonitorMenu = new JMenuItem();
      this.wswMenu = new JMenuItem();
      this.cheatMenu = new JMenuItem();
      this.optionsMenu = new JMenu();
      this.jMenu1 = new JMenu();
      this.funMenu = new JRadioButtonMenuItem();
      this.realisticMenu = new JRadioButtonMenuItem();
      this.sortOrderMenu = new JMenuItem();
      this.jSeparator4 = new Separator();
      this.soundMenu = new JMenu();
      this.büSoundMenu = new JCheckBoxMenuItem();
      this.akzeptorSoundMenu = new JCheckBoxMenuItem();
      this.funkSoundMenu = new JCheckBoxMenuItem();
      this.zugSoundMenu = new JCheckBoxMenuItem();
      this.zählwerkSoundMenu = new JCheckBoxMenuItem();
      this.jSeparator5 = new Separator();
      this.startPluginInterface = new JCheckBoxMenuItem();
      this.helpMenu = new JMenu();
      this.aboutMenuItem = new JMenuItem();
      this.jSeparator11 = new Separator();
      this.infoMenuItem = new JMenuItem();
      this.debugMenuItem = new JMenuItem();
      this.setDefaultCloseOperation(0);
      this.setLocationByPlatform(true);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            stellwerksim_main.this.formWindowClosing(evt);
         }
      });
      this.mainSplitPane.setBorder(null);
      this.mainSplitPane.setDividerLocation(-160);
      this.mainSplitPane.setOrientation(0);
      this.mainSplitPane.setResizeWeight(0.85);
      this.mainSplitPane.setLastDividerLocation(-160);
      this.gleisbildHauptPanel.setDoubleBuffered(false);
      this.gleisbildHauptPanel.setLayout(new BorderLayout());
      this.extrasPanelBar.setAlignmentX(0.0F);
      this.extrasPanelBar.setAlignmentY(0.0F);
      this.extrasPanelBar.setDoubleBuffered(false);
      this.extrasPanelBar.setFont(new Font("Dialog", 0, 10));
      this.extrasPanelBar.setMaximumSize(new Dimension(0, 30));
      this.extrasPanelBar.setMinimumSize(new Dimension(0, 30));
      this.extrasPanelBar.setPreferredSize(new Dimension(0, 30));
      this.extrasPanelBar.setLayout(new BorderLayout());
      this.gleisbildHauptPanel.add(this.extrasPanelBar, "North");
      this.gleisedscroller.setBorder(null);
      this.gleisedscroller.setPreferredSize(new Dimension(81, 400));
      this.gleisbildHauptPanel.add(this.gleisedscroller, "Center");
      this.mainSplitPane.setTopComponent(this.gleisbildHauptPanel);
      this.controlPanel.setDoubleBuffered(false);
      this.controlPanel.setFont(new Font("Dialog", 0, 10));
      this.controlPanel.setMaximumSize(new Dimension(32767, 32813));
      this.controlPanel.setMinimumSize(new Dimension(164, 140));
      this.controlPanel.setPreferredSize(new Dimension(800, 220));
      this.controlPanel.setLayout(new BorderLayout());
      this.mainSplitPane.setRightComponent(this.controlPanel);
      this.getContentPane().add(this.mainSplitPane, "Center");
      this.fileMenu.setText("Simulator");
      this.searchMenu.setAccelerator(KeyStroke.getKeyStroke(70, 2));
      this.searchMenu.setText("Suche Zug");
      this.searchMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.searchMenuActionPerformed(evt);
         }
      });
      this.fileMenu.add(this.searchMenu);
      this.searchElementMenu.setAccelerator(KeyStroke.getKeyStroke(70, 3));
      this.searchElementMenu.setText("Suche Element");
      this.searchElementMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.searchElementMenuActionPerformed(evt);
         }
      });
      this.fileMenu.add(this.searchElementMenu);
      this.fileMenu.add(this.jSeparator6);
      this.bahnhofsuhrMenu.setAccelerator(KeyStroke.getKeyStroke(85, 2));
      this.bahnhofsuhrMenu.setText("Analoge Uhr...");
      this.bahnhofsuhrMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.bahnhofsuhrMenuActionPerformed(evt);
         }
      });
      this.fileMenu.add(this.bahnhofsuhrMenu);
      this.bahnhofsuhr24Menu.setText("Analoge 24 Stundenuhr...");
      this.bahnhofsuhr24Menu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.bahnhofsuhr24MenuActionPerformed(evt);
         }
      });
      this.fileMenu.add(this.bahnhofsuhr24Menu);
      this.miniViewMenu.setAccelerator(KeyStroke.getKeyStroke(77, 2));
      this.miniViewMenu.setText("Miniansicht Gleisbild...");
      this.miniViewMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.miniViewMenuActionPerformed(evt);
         }
      });
      this.fileMenu.add(this.miniViewMenu);
      this.fileMenu.add(this.jSeparator7);
      this.exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(81, 2));
      this.exitMenuItem.setText("Spiel beenden");
      this.exitMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.exitMenuItemActionPerformed(evt);
         }
      });
      this.fileMenu.add(this.exitMenuItem);
      this.menuBar.add(this.fileMenu);
      this.layoutMenu.setText("Darstellung");
      this.scaleMenu.setText("Zoomstufe");
      this.scaleMenu.setActionCommand("Scale");
      this.scaleMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.scaleMenuActionPerformed(evt);
         }
      });
      this.scaleFixedMenu.setText("gleiches Seitenverhältnis");
      this.scaleMenu.add(this.scaleFixedMenu);
      this.scaleDifferentMenu.setText("unterschiedliches Verhältnis");
      this.scaleMenu.add(this.scaleDifferentMenu);
      this.layoutMenu.add(this.scaleMenu);
      this.gleisGruppeItem.setAccelerator(KeyStroke.getKeyStroke(82, 2));
      this.gleisGruppeItem.setText("realistischere Gleisausleuchtung");
      this.gleisGruppeItem.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            stellwerksim_main.this.gleisGruppeItemItemStateChanged(evt);
         }
      });
      this.layoutMenu.add(this.gleisGruppeItem);
      this.nightViewMenu.setAccelerator(KeyStroke.getKeyStroke(78, 2));
      this.nightViewMenu.setText("Gleisbild Nachtansicht");
      this.nightViewMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.nightViewMenuActionPerformed(evt);
         }
      });
      this.layoutMenu.add(this.nightViewMenu);
      this.winLayoutMenu.setText("Layout");
      this.winLayoutGroup.add(this.winLayout1);
      this.winLayout1.setFont(this.winLayout1.getFont().deriveFont(this.winLayout1.getFont().getStyle() | 1));
      this.winLayout1.setSelected(true);
      this.winLayout1.setText("1 Fenster");
      this.winLayoutMenu.add(this.winLayout1);
      this.winLayoutGroup.add(this.winLayout2);
      this.winLayout2.setText("2 Fenster");
      this.winLayoutMenu.add(this.winLayout2);
      this.winLayoutGroup.add(this.winLayoutMulti);
      this.winLayoutMulti.setText("Multi-Fenster (T)");
      this.winLayoutMenu.add(this.winLayoutMulti);
      this.winLayoutGroup.add(this.winLayoutMulti2);
      this.winLayoutMulti2.setText("Multi-Fenster (S)");
      this.winLayoutMenu.add(this.winLayoutMulti2);
      this.layoutMenu.add(this.winLayoutMenu);
      this.layoutMenu.add(this.jSeparator10);
      this.visualHelpMenu.setText("Visuelle und Bedienungshilfen");
      this.alterColorMenu.setAccelerator(KeyStroke.getKeyStroke(65, 2));
      this.alterColorMenu.setText("Alternative Rot-Farbe");
      this.alterColorMenu.setToolTipText("geeigneter für Rot/Grün-Schwäche");
      this.alterColorMenu.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            stellwerksim_main.this.alterColorMenuItemStateChanged(evt);
         }
      });
      this.visualHelpMenu.add(this.alterColorMenu);
      this.longDualKeyMenu.setText("Längere Reaktionszeiten");
      this.longDualKeyMenu.setToolTipText("verlängert die Zeiten von Gruppentasten mit Folgetasten unter Zeitbegrenzung wie UFGT und man. gl. FS");
      this.longDualKeyMenu.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            stellwerksim_main.this.longDualKeyMenuItemStateChanged(evt);
         }
      });
      this.visualHelpMenu.add(this.longDualKeyMenu);
      this.elementWindowMenu.setAccelerator(KeyStroke.getKeyStroke(69, 2));
      this.elementWindowMenu.setText("Elementanzeige...");
      this.elementWindowMenu.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            stellwerksim_main.this.elementWindowMenuItemStateChanged(evt);
         }
      });
      this.visualHelpMenu.add(this.elementWindowMenu);
      this.layoutMenu.add(this.visualHelpMenu);
      this.layoutMenu.add(this.jSeparator2);
      this.tuningMenu.setText("Tuning");
      this.gleisbildSmoothOffMenu.setText("Kantenglättung (Antialiasing) abschalten erzwingen");
      this.gleisbildSmoothOffMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.gleisbildSmoothOffMenuActionPerformed(evt);
         }
      });
      this.tuningMenu.add(this.gleisbildSmoothOffMenu);
      this.gleisbildSmoothOnMenu.setText("Kantenglättung (Antialiasing) erzwingen");
      this.gleisbildSmoothOnMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.gleisbildSmoothOnMenuActionPerformed(evt);
         }
      });
      this.tuningMenu.add(this.gleisbildSmoothOnMenu);
      this.gleisbildSmoothDecrementMenu.setText("Kantenglättung (Antialiasing) eine Stufe weniger");
      this.gleisbildSmoothDecrementMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.gleisbildSmoothDecrementMenuActionPerformed(evt);
         }
      });
      this.tuningMenu.add(this.gleisbildSmoothDecrementMenu);
      this.layoutMenu.add(this.tuningMenu);
      this.menuBar.add(this.layoutMenu);
      this.extrasMenu.setText("Extras");
      this.alarmClockItem.setAccelerator(KeyStroke.getKeyStroke(87, 2));
      this.alarmClockItem.setText("Wecker...");
      this.alarmClockItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.alarmClockItemActionPerformed(evt);
         }
      });
      this.extrasMenu.add(this.alarmClockItem);
      this.autoMsgSetupMenu.setAccelerator(KeyStroke.getKeyStroke(71, 2));
      this.autoMsgSetupMenu.setText("Vollautomatische Zugmeldungen...");
      this.autoMsgSetupMenu.setEnabled(!this.realistic);
      this.autoMsgSetupMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.autoMsgSetupMenuActionPerformed(evt);
         }
      });
      this.extrasMenu.add(this.autoMsgSetupMenu);
      this.extrasMenu.add(this.jSeparator9);
      this.bueTimeWindowMenu.setAccelerator(KeyStroke.getKeyStroke(66, 2));
      this.bueTimeWindowMenu.setText("BÜ Zeiten anzeigen...");
      this.bueTimeWindowMenu.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            stellwerksim_main.this.bueTimeWindowMenuItemStateChanged(evt);
         }
      });
      this.extrasMenu.add(this.bueTimeWindowMenu);
      this.abfahrtMonitorMenu.setAccelerator(KeyStroke.getKeyStroke(84, 2));
      this.abfahrtMonitorMenu.setText("Abfahrtmonitor...");
      this.abfahrtMonitorMenu.setActionCommand("Abfahrtsmonitor...");
      this.abfahrtMonitorMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.abfahrtMonitorMenuActionPerformed(evt);
         }
      });
      this.extrasMenu.add(this.abfahrtMonitorMenu);
      this.wswMenu.setAccelerator(KeyStroke.getKeyStroke(83, 2));
      this.wswMenu.setText("Was steht wo...");
      this.wswMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.wswMenuActionPerformed(evt);
         }
      });
      this.extrasMenu.add(this.wswMenu);
      this.cheatMenu.setText("Effektcode eingeben...");
      this.cheatMenu.setEnabled(false);
      this.cheatMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.cheatMenuActionPerformed(evt);
         }
      });
      this.extrasMenu.add(this.cheatMenu);
      this.menuBar.add(this.extrasMenu);
      this.optionsMenu.setText("Optionen");
      this.jMenu1.setText("Simulationsmodus");
      this.stsModusGroup.add(this.funMenu);
      this.funMenu.setFont(this.funMenu.getFont().deriveFont(this.funMenu.getFont().getStyle() | 1));
      this.funMenu.setSelected(true);
      this.funMenu.setText("Unterhaltung");
      this.funMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.funMenuActionPerformed(evt);
         }
      });
      this.jMenu1.add(this.funMenu);
      this.stsModusGroup.add(this.realisticMenu);
      this.realisticMenu.setText("Realer");
      this.realisticMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.funMenuActionPerformed(evt);
         }
      });
      this.jMenu1.add(this.realisticMenu);
      this.optionsMenu.add(this.jMenu1);
      this.sortOrderMenu.setText("Fahrplansortierung...");
      this.sortOrderMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.sortOrderMenuActionPerformed(evt);
         }
      });
      this.optionsMenu.add(this.sortOrderMenu);
      this.optionsMenu.add(this.jSeparator4);
      this.soundMenu.setText("Ton");
      this.büSoundMenu.setSelected(true);
      this.büSoundMenu.setText("Bü offen Meldung");
      this.soundMenu.add(this.büSoundMenu);
      this.akzeptorSoundMenu.setSelected(true);
      this.akzeptorSoundMenu.setText("Akzeptor Meldung");
      this.soundMenu.add(this.akzeptorSoundMenu);
      this.funkSoundMenu.setSelected(true);
      this.funkSoundMenu.setText("Zugfunk Meldung");
      this.soundMenu.add(this.funkSoundMenu);
      this.zugSoundMenu.setSelected(true);
      this.zugSoundMenu.setText("Zugeinfahrt Meldung");
      this.soundMenu.add(this.zugSoundMenu);
      this.zählwerkSoundMenu.setSelected(true);
      this.zählwerkSoundMenu.setText("Zählwerkgeräusch");
      this.soundMenu.add(this.zählwerkSoundMenu);
      this.optionsMenu.add(this.soundMenu);
      this.optionsMenu.add(this.jSeparator5);
      this.startPluginInterface.setSelected(true);
      this.startPluginInterface.setText("Pluginschnittstelle starten");
      this.startPluginInterface.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            stellwerksim_main.this.startPluginInterfaceItemStateChanged(evt);
         }
      });
      this.optionsMenu.add(this.startPluginInterface);
      this.menuBar.add(this.optionsMenu);
      this.helpMenu.setText("Hilfe");
      this.aboutMenuItem.setText("Über...");
      this.aboutMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.aboutMenuItemActionPerformed(evt);
         }
      });
      this.helpMenu.add(this.aboutMenuItem);
      this.helpMenu.add(this.jSeparator11);
      this.infoMenuItem.setText("Intern...");
      this.infoMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.infoMenuItemActionPerformed(evt);
         }
      });
      this.helpMenu.add(this.infoMenuItem);
      this.debugMenuItem.setText("F.a.t. ...");
      this.debugMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerksim_main.this.debugMenuItemActionPerformed(evt);
         }
      });
      this.helpMenu.add(this.debugMenuItem);
      this.menuBar.add(this.helpMenu);
      this.setJMenuBar(this.menuBar);
      this.pack();
   }

   private void searchMenuActionPerformed(ActionEvent evt) {
      this.dataPanel.setFocus();
   }

   private void scaleMenuActionPerformed(ActionEvent evt) {
   }

   private void alterColorMenuItemStateChanged(ItemEvent evt) {
      if (this.alterColorMenu.isSelected()) {
         this.setAlterColor();
         this.prefs.putBoolean("alterColor", true);
      } else {
         this.setNormalColor();
         this.prefs.putBoolean("alterColor", false);
      }

      this.glbPanel.repaint();
      this.prefs.flush();
      this.uc.busPublish(new PrefsChangedEvent(this));
   }

   private void infoMenuItemActionPerformed(ActionEvent evt) {
      new internalInfo(this);
   }

   private void formWindowClosing(WindowEvent evt) {
      if (this.firstRun && !this.emitterMode) {
         JOptionPane.showMessageDialog(this, "Jetzt lass doch erstmal fertig laden!", "Schlechter Zeitpunkt", 2);
      } else if (closePrefs.Parts.SIM.ask(this.uc, this, "Wirklich Simulator beenden?")) {
         try {
            this.uc.busUnsubscribe(this);
            this.setVisible(false);
            this.mWindowManager.close();
            this.dispose();
            this.game_time.end();
            this.running = false;
            if (this.offlinemode == 1) {
               if (this.my_chat != null) {
                  this.my_chat.quit();
               }
            } else if (!this.emitterMode && this.my_chat != null) {
               this.my_chat.quit();
            }

            if (this.pluginS != null) {
               this.pluginS.close();
               this.pluginS = null;
            }

            if (this.fahrplanPanel != null) {
               this.fahrplanPanel.exit();
            }

            event.clear();
            thema.clear();
            zug.clear();
            this.setNormalColor();
            this.dataPanel.stopTextTextField();
            fahrstrasse.fserror = false;
            this.glbModel.clearStatus();
            this.glbControl.setScalePreset("100");
            this.glbPanel.setControl(null);
            this.gleisedscroller.setViewportView(null);
            this.fahrplanPanel = null;
            this.cheatMgr = null;
            this.runner.interrupt();
            this.runner = null;
            this.setJMenuBar(null);
            this.removeAll();
         } finally {
            this.uc.moduleClosed();
         }
      }
   }

   @Override
   public void showFahrplan(zug z) {
      this.fahrplanPanel.showFahrplan(z);
   }

   @Override
   public void showText(String t, TEXTTYPE type, Object reference) {
      this.fahrplanPanel.showText(t, type, reference);
   }

   @Override
   public void showText(String t, TEXTTYPE type, Object reference, HyperlinkCaller caller) {
      this.fahrplanPanel.showText(t, type, reference, caller);
   }

   public void showText_replay(String t, Object reference) {
      this.showText(t, TEXTTYPE.REPLY, reference);
   }

   @Override
   public void finishText(Object reference) {
      this.fahrplanPanel.finishText(reference);
   }

   private void aboutMenuItemActionPerformed(ActionEvent evt) {
      this.uc.showAbout();
   }

   private void exitMenuItemActionPerformed(ActionEvent evt) {
      this.formWindowClosing(null);
   }

   private void debugMenuItemActionPerformed(ActionEvent evt) {
      fat.open(this.uc, this);
   }

   private void sortOrderMenuActionPerformed(ActionEvent evt) {
      new verspaetungColumnSorterOption(this);
   }

   private void autoMsgSetupMenuActionPerformed(ActionEvent evt) {
      new setupFrame(this, this.msgControl).setVisible(true);
   }

   private void gleisbildSmoothOffMenuActionPerformed(ActionEvent evt) {
      this.glbPanel.setSmoothOff();
   }

   private void miniViewMenuActionPerformed(ActionEvent evt) {
      miniViewWindow.createInstance(this, this.glbControl);
   }

   private void bahnhofsuhrMenuActionPerformed(ActionEvent evt) {
      new analogUhr(this, false).show();
   }

   private void elementWindowMenuItemStateChanged(ItemEvent evt) {
      if (this.elementWindowMenu.isSelected() && this.elmWindow == null) {
         elementWindow t = new elementWindow(this, this.elementWindowMenu);
         this.elmWindow = t;
         this.glbControl.addCoordinatesListener(this.elmWindow);
      } else if (!this.elementWindowMenu.isSelected() && this.elmWindow != null) {
         this.glbControl.removeCoordinatesListener(this.elmWindow);
         elementWindow t = this.elmWindow;
         this.elmWindow = null;
         t.dispose();
      }
   }

   private void bahnhofsuhr24MenuActionPerformed(ActionEvent evt) {
      new analogUhr(this, true).show();
   }

   private void gleisGruppeItemItemStateChanged(ItemEvent evt) {
      if (this.gleisGruppeItem.isSelected()) {
         this.glbModel.buildGleisGroup();
      } else {
         this.glbModel.resetGleisGroup();
      }

      this.prefs.putBoolean("gleisGroup", this.gleisGruppeItem.isSelected());
      this.prefs.flush();
      this.uc.busPublish(new PrefsChangedEvent(this));
   }

   private void abfahrtMonitorMenuActionPerformed(ActionEvent evt) {
      new gleisBelegungDialog(this, this.fahrplanPanel);
   }

   private void alarmClockItemActionPerformed(ActionEvent evt) {
      alarmClock.showInstance(this);
   }

   private void nightViewMenuActionPerformed(ActionEvent evt) {
      if (this.nightViewMenu.isSelected()) {
         gleisColor.getInstance().setNachtView();
         this.glbControl.fastPaint();
      } else {
         gleisColor.getInstance().setDayView();
         this.glbControl.fastPaint();
      }

      this.prefs.putBoolean("isNightMode", this.nightViewMenu.isSelected());
      this.prefs.flush();
      this.uc.busPublish(new PrefsChangedEvent(this));
   }

   private void gleisbildSmoothOnMenuActionPerformed(ActionEvent evt) {
      this.glbPanel.setSmoothOn();
   }

   private void gleisbildSmoothDecrementMenuActionPerformed(ActionEvent evt) {
      this.glbPanel.setSmoothOneOff();
   }

   private void startPluginInterfaceItemStateChanged(ItemEvent evt) {
      if (this.startPluginInterface.isSelected()) {
         if (this.pluginS == null) {
            this.pluginS = new pluginServ(new simPluginAdapter(this));
            if (!this.pluginS.isWorking()) {
               this.pluginS = null;
               this.startPluginInterface.setSelected(false);
            } else {
               this.uc.addCloseObject(this.pluginS);
            }
         }
      } else if (this.pluginS != null) {
         this.pluginS.close();
         this.pluginS = null;
      }

      this.prefs.putBoolean("startPlugin", this.startPluginInterface.isSelected());
   }

   private void bueTimeWindowMenuItemStateChanged(ItemEvent evt) {
      if (this.bueTimeWindowMenu.isSelected() && this.bueWindow == null) {
         bueTimeWindow t = new bueTimeWindow(this, this.bueTimeWindowMenu);
         this.bueWindow = t;
      } else if (!this.bueTimeWindowMenu.isSelected() && this.bueWindow != null) {
         bueTimeWindow t = this.bueWindow;
         this.bueWindow = null;
         t.close();
         t.dispose();
      }
   }

   private void wswMenuActionPerformed(ActionEvent evt) {
      this.fahrplanPanel.openWsw();
   }

   private void cheatMenuActionPerformed(ActionEvent evt) {
      this.cheatMgr.menuAction();
   }

   private void funMenuActionPerformed(ActionEvent evt) {
      if (this.realisticMenu.isSelected() && !RealisticSure.question(this)) {
         this.funMenu.setSelected(true);
      } else {
         this.prefs.putBoolean("realistic", this.realisticMenu.isSelected());
         this.prefs.flush();
         this.uc.busPublish(new PrefsChangedEvent(this));
         this.showRestartInfo();
      }
   }

   private void searchElementMenuActionPerformed(ActionEvent evt) {
      this.extrasPanel.setFocus();
   }

   private void longDualKeyMenuItemStateChanged(ItemEvent evt) {
      this.glbControl.setDualKeyTimeLong(this.longDualKeyMenu.isSelected());
      this.prefs.putBoolean("longDualKey", this.longDualKeyMenu.isSelected());
      this.glbPanel.repaint();
      this.prefs.flush();
      this.uc.busPublish(new PrefsChangedEvent(this));
   }

   public pluginServ attachPluginClient(ServImpl.OutputWriter client) {
      if (this.pluginS != null) {
         this.pluginS.attachClient(client);
         return this.pluginS;
      } else {
         pluginServ pl = new pluginServ(new simPluginAdapter(this), client);
         this.uc.addCloseObject(pl);
         return pl;
      }
   }

   private void showRestartInfo() {
      message1 m = new message1(this, false, "Hinweis", "Diese Option wird erst beim nächsten Sim-Start aktiv sein.");
      m.show();
   }

   @Override
   public void repaintGleisbild() {
      this.glbPanel.repaint();
   }

   @Override
   public bahnsteigDetailStore getBahnsteige() {
      return this.fahrplanPanel.getBahnsteige();
   }

   @Override
   public void setZugOnBahnsteig(String bname, zug z, gleis pos_gl) {
      this.fahrplanPanel.setZugOnBahnsteig(bname, z);
      this.reportZugPosition(z != null ? z.getZID_num() : 0, bname, pos_gl);
      if (z != null) {
         this.sendZugUserText(z, false);
      }
   }

   @Override
   public void refreshZug() {
      this.fahrplanPanel.refreshZug();
   }

   @Override
   public zug findZug(int zid) {
      return this.fahrplanPanel == null ? null : this.fahrplanPanel.findZug(zid);
   }

   @Override
   public zug findZug(String zid) {
      return this.fahrplanPanel.findZug(zid);
   }

   @Override
   public zug findZugByShortName(String name) {
      return this.fahrplanPanel.findZugByShortName(name);
   }

   @Override
   public zug findZugByFullName(String name) {
      return this.fahrplanPanel.findZugByFullName(name);
   }

   @Override
   public Collection<zug> findZugPointingMe(int zid) {
      return this.fahrplanPanel.findZugPointingMe(zid);
   }

   boolean searchZug(String search, boolean running) {
      return this.fahrplanPanel.searchZug(search, running);
   }

   @Override
   public boolean haveWeSeenIt(zug z) {
      return this.fahrplanPanel.haveWeSeenIt(z);
   }

   @Override
   public void updateZug(zug z) {
      this.fahrplanPanel.updateZug(z);
   }

   @Override
   public zug addZug(zug z) {
      return this.fahrplanPanel.addZug(z);
   }

   public void sendZugUserText(zug z, boolean emptyAlso) {
      if (this.botMode) {
         String controlName = this.uc.getParameter("ochannel") + "e";
         String t = z.getUserText();
         if (!t.isEmpty()) {
            this.my_chat.sendXmlStatusToChannel(controlName, new ZugUserText(z.getZID_num(), t, z.getUserTextSender()));
         } else if (emptyAlso) {
            this.my_chat.sendXmlStatusToChannel(controlName, new ZugUserText(z.getZID_num()));
         }
      }
   }

   @Override
   public void hideZug(zug z) {
      this.fahrplanPanel.hideZug(z);
      if (this.botMode) {
         this.my_chat.sendStatus(BOTCOMMAND.UPDATE, z.toString());
         this.sendZugUserText(z, false);
      }

      this.reportZugPosition(z.getZID_num());
   }

   @Override
   public void syncZug(zug z) {
      if (this.botMode && !z.isFertig()) {
         this.my_chat.sendStatus(BOTCOMMAND.UPDATE, z.toString());
         this.sendZugUserText(z, false);
      }
   }

   @Override
   public void syncZug1(zug z) {
      if (this.botMode && !z.isFertig()) {
         this.my_chat.sendStatus(BOTCOMMAND.UPDATE, z.toString());
      }
   }

   @Override
   public void reportZugPosition(int zid, int start_enr, int stop_enr) {
      if (this.botMode) {
         String channel = this.getParameter("mchannel");
         if (channel != null) {
            this.my_chat.sendXmlStatusToChannel(channel, new EPosChange(zid, start_enr, stop_enr));
         }
      }
   }

   @Override
   public void reportZugPosition(int zid, String bstg, gleis pos_gl) {
      if (this.botMode) {
         String channel = this.getParameter("mchannel");
         if (channel != null) {
            if (pos_gl != null) {
               this.my_chat.sendXmlStatusToChannel(channel, new BPosChange(zid, bstg, pos_gl.getCol(), pos_gl.getRow()));
            } else {
               this.my_chat.sendXmlStatusToChannel(channel, new BPosChange(zid, bstg));
            }
         }
      }
   }

   @Override
   public void reportZugPosition(int zid) {
      if (this.botMode) {
         String channel = this.getParameter("mchannel");
         if (channel != null) {
            this.my_chat.sendXmlStatusToChannel(channel, new XPosChange(zid));
         }
      }
   }

   @Override
   public void reportFahrplanAb(int zid, int azid, int verspaetung) {
      if (this.botMode && zid > 0) {
         this.my_chat.sendStatus(BOTCOMMAND.FAHRPLAN, "ab=1&zid=" + zid + "&azid=" + azid + "&v=" + verspaetung);
      }
   }

   @Override
   public void reportFahrplanAn(int zid, int azid, String gestopptgleis, boolean gleiswarok, int verspaetung, int warVerspaetung) {
      if (this.botMode && zid > 0) {
         this.my_chat
            .sendStatus(
               BOTCOMMAND.FAHRPLAN,
               "an=1&zid="
                  + zid
                  + "&azid="
                  + azid
                  + "&v="
                  + verspaetung
                  + "&wv="
                  + warVerspaetung
                  + "&ok="
                  + gleiswarok
                  + "&g="
                  + TextHelper.urlEncode(gestopptgleis)
            );
      }
   }

   @Override
   public void reportSignalStellung(int enr, gleisElements.Stellungen stellung, fahrstrasse fs) {
      if (this.botMode) {
         String channel = this.getParameter("mchannel");
         if (channel != null) {
            this.my_chat.sendXmlStatusToChannel(channel, new SBldChange(this.glbModel.getAid(), enr, stellung.ordinal(), fs != null ? fs.getName() : null));
         }
      }
   }

   @Override
   public void reportElementOccurance(gleis pos_gl, OCCU_KIND kind, String name, String code) {
      if (this.botMode) {
         String channel = this.getParameter("mchannel");
         if (channel != null) {
            this.my_chat.sendXmlStatusToChannel(channel, new ElementOccurance(this.glbModel.getAid(), pos_gl.getENR(), kind));
         }

         if (kind == OCCU_KIND.OCCURED || kind == OCCU_KIND.NORMAL) {
            this.reportOccurance("G" + pos_gl.hashCode(), kind, name, code);
         }
      }
   }

   @Override
   public void reportOccurance(String hash, OCCU_KIND kind, String name, String code) {
      if (this.botMode) {
         String channel = this.getParameter("ochannel");
         if (channel != null) {
            this.my_chat.sendStatusToUser(channel, "ST:" + this.glbModel.getAid() + ":" + hash + ":" + kind.getChar());
         }

         if (kind == OCCU_KIND.OCCURED) {
            this.uc.busPublish(new StoreEventOccured(this.glbModel.getAid(), name, code));
         }
      }
   }

   @Override
   public void updateHeat(long heat) {
      if (this.botMode) {
         String channel = this.getParameter("ochannel");
         if (channel != null) {
         }
      }
   }

   @Override
   public void reserveENR(int enr) {
      if (this.botMode && !this.noreserve) {
         this.my_chat.sendStatus(BOTCOMMAND.RESERVE, "R " + enr);
         this.uepFsLatencyMeasure.sendingCommand("R " + enr);
      } else {
         this.fsalloc.gotReserveResponseMessage("OK", enr, true);
      }
   }

   @Override
   public void unreserveENR(int enr) {
      if (this.botMode && !this.noreserve) {
         this.my_chat.sendStatus(BOTCOMMAND.RESERVE, "U " + enr);
         this.uepFsLatencyMeasure.sendingCommand("U " + enr);
      }
   }

   @Override
   public void FATmessage(String code) {
      if (this.getParameter("fathash") != null) {
         try {
            String h = this.getParameter("fathash");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(code.getBytes());
            byte[] result = md5.digest();
            StringBuilder hexString = new StringBuilder();

            for(int i = 0; i < result.length; ++i) {
               hexString.append(Integer.toHexString(255 & result[i]));
            }

            if (h.compareToIgnoreCase(hexString.toString()) == 0) {
               if (this.structserv == null) {
                  this.structserv = new structServer(this);
               }

               this.uc.busPublish(new fat.FatResponseEvent(200));
               return;
            }
         } catch (Exception var7) {
         }
      } else if (this.getParameter("fatcode") != null && this.getParameter("fatcode").compareTo(code) == 0) {
         if (this.structserv == null) {
            this.structserv = new structServer(this);
         }

         this.uc.busPublish(new fat.FatResponseEvent(200));
         return;
      }

      this.my_chat.sendStatus(BOTCOMMAND.FATCODECHECK, code);
   }

   @Override
   public void zugMessage(int enr, zug z) {
      if (this.botMode && !this.noreserve) {
         this.sendZugUserText(z, false);
         this.my_chat.sendStatus(BOTCOMMAND.TRAIN, z.getZID() + " " + enr);
      } else {
         z.leaveAfterÜP(true);
      }
   }

   @Override
   public void zugResponseMessage(String msg, int enr, int zid) {
      if (this.botMode) {
         this.my_chat.sendStatus(BOTCOMMAND.TRAINRESPONSE, msg + " " + enr + " " + zid);
      }
   }

   @Override
   public void zugBlockMessage(int enr, zug z) {
      if (this.botMode && !this.noreserve) {
         this.my_chat.sendStatus(BOTCOMMAND.TRAINBLOCK, z.getZID() + " " + enr);
      }
   }

   @Override
   public void exchangeZug(zug z1, zug z2, int zid1, int zid2) {
      this.fahrplanPanel.exchangeZug(z1, z2, zid1, zid2);
   }

   @Override
   public void renameZug(zug z, int oldzid, int newzid) {
      this.fahrplanPanel.renameZug(z, oldzid, newzid);
   }

   @Override
   public void delZug(zug z) {
      this.fahrplanPanel.delZug(z);
   }

   public void dropZug(zug z) {
      this.fahrplanPanel.dropZug(z);
   }

   @Override
   public Collection<zug> getZugList() {
      return this.fahrplanPanel.getZugList();
   }

   public void requestZugRedirect(zug z) {
      zidRedirectPanel zr = (zidRedirectPanel)this.redirects.get(z.getZID_num());
      if (zr == null) {
         zr = new zidRedirectPanel(this.uc, this, this.glbModel.getPhonebook(), z);
         this.redirects.put(z.getZID_num(), zr);
      }

      this.my_chat.sendStatus(BOTCOMMAND.ZREDIRECTREQ, z.getZID());
      this.showText_replay("Umleitungsanfrage für " + z.getSpezialName() + " wurde gestellt.", z);
      this.allowOneRedirect = false;
   }

   @Override
   public void requestZugRedirect(zug z, String weg) {
      this.my_chat.sendStatus(BOTCOMMAND.ZREDIRECTREQ, z.getZID() + " " + weg);
   }

   public void requestZugFahrweg(zug z) {
      this.my_chat.sendStatus(BOTCOMMAND.ZREQUESTFPL, z.getZID());
   }

   public void sendZugComment(zug z, long time, String level, String c) {
      if (this.botMode || this.qsmode) {
         String c2 = c.replace("\n", "\\n");
         TimeFormat tf = TimeFormat.getInstance(TimeFormat.STYLE.HMS);
         String stime = tf.format(time);
         this.my_chat.sendStatus(BOTCOMMAND.QSCOMMENT, z.getZID() + " " + stime + " " + this.qsmodus + " " + level + " " + c2);
         if (debugMode != null) {
            debugMode.writeln("main/zug/QS", "zuglist remove: " + z.getZID() + " " + stime + " " + this.qsmodus + " " + level + " " + c2);
         }
      }
   }

   @Override
   public void sendEnterSignalMessage(int enr, gleisElements.Stellungen stellung) {
      if (this.botMode) {
         this.my_chat.sendStatus(BOTCOMMAND.ENTERSIGNAL, enr + " " + stellung.getSaveText());
      }
   }

   @Override
   public void sendEnterSignalMessage(LinkedList<gleis> elist) {
      if (this.botMode) {
         String r = "";

         for(gleis gl : elist) {
            r = r + " " + gl.getEinfahrtEnr() + " " + gl.getFluentData().getStellung().getSaveText();
         }

         this.my_chat.sendStatus(BOTCOMMAND.ENTERSIGNAL, r);
      }
   }

   public void sendRedirectMsg(int zid, int aid, int reasonIndex) {
      if (this.botMode) {
         String r = " " + zid + " " + aid + " " + reasonIndex;
         this.my_chat.sendStatus(BOTCOMMAND.ZREDIRECTWAY, r);
      }
   }

   public void sendRedirectAck(int zid, boolean accepted) {
      if (this.botMode) {
         String r = " " + zid + " " + (accepted ? "1" : "0");
         this.my_chat.sendStatus(BOTCOMMAND.ZREDIRECTACK, r);
      }
   }

   public void send2Bot(BOTCOMMAND cmd, String data) {
      if (this.botMode) {
         this.my_chat.sendStatus(cmd, data);
      }
   }

   @Override
   public boolean isBotMode() {
      return this.botMode;
   }

   @Override
   public boolean wasBotMode() {
      return "true".equals(this.uc.getParameter("usebot"));
   }

   public boolean isDevSandbox() {
      return this.getParameter("sandbox") != null;
   }

   @Override
   public boolean isCaller() {
      return this.botMode && this.getParameter("callurl") != null;
   }

   @Override
   public Vector getStructure() {
      Vector v = new Vector();
      v.addElement("build");
      v.addElement(this.getBuild() + "");
      v.addElement("syncdelay");
      v.addElement(this.syncdelay + "");
      long heapSize = Runtime.getRuntime().totalMemory();
      long heapMaxSize = Runtime.getRuntime().maxMemory();
      long heapFreeSize = Runtime.getRuntime().freeMemory();
      v.addElement("max (heap) mem");
      v.addElement(heapMaxSize + "");
      v.addElement("free (heap) mem");
      v.addElement(heapFreeSize + "");
      v.addElement("cur (heap) mem");
      v.addElement(heapSize + "");
      v.addElement("stellwerk");
      v.addElement(this.uc.getParameter("stellwerk"));
      v.addElement("develop");
      v.addElement(this.uc.getParameter("develop"));
      v.addElement("offline");
      v.addElement(this.uc.getParameter("offline"));
      v.addElement("sound1");
      v.addElement(this.uc.getParameter("sound1"));
      v.addElement("sound2");
      v.addElement(this.uc.getParameter("sound2"));
      v.addElement("sound3");
      v.addElement(this.uc.getParameter("sound3"));
      v.addElement("sound4");
      v.addElement(this.uc.getParameter("sound4"));
      v.addElement("sound5");
      v.addElement(this.uc.getParameter("sound5"));
      v.addElement("richtung");
      v.addElement(this.uc.getParameter("richtung"));
      v.addElement("typ");
      v.addElement(this.uc.getParameter("typ"));
      v.addElement("richtung");
      v.addElement(this.uc.getParameter("richtung"));
      v.addElement("element");
      v.addElement(this.uc.getParameter("element"));
      v.addElement("ircserver");
      v.addElement(this.uc.getParameter("ircserver"));
      v.addElement("nick1");
      v.addElement(this.uc.getParameter("nick1"));
      v.addElement("nick2");
      v.addElement(this.uc.getParameter("nick2"));
      v.addElement("nick3");
      v.addElement(this.uc.getParameter("nick3"));
      return v;
   }

   @Override
   public String getStructName() {
      return "main";
   }

   public Vector getStructInfo() {
      Vector ret = new Vector();
      Vector v = new Vector();
      v.addElement("Applet-Daten");
      v.addElement("Main");
      v.addElement(this);
      ret.addElement(v);
      v = new Vector();
      v.addElement("Applet-Daten");
      v.addElement("Chat");
      v.addElement(this.my_chat);
      ret.addElement(v);
      ret.addAll(this.fahrplanPanel.getStructInfo());
      ret.addAll(this.glbModel.getStructInfo());
      ret.addAll(this.glbControl.getStructInfo());
      ret.addAll(threadHelper.getStructInfo());
      return ret;
   }

   private void setSimTime(timedeliveryBase t) {
      this.simTime.setTimeDeliverer(t);
      this.syncedTime = t instanceof timedeliverySynced;
   }

   public void setPause(boolean on) {
      this.glbControl.setEnabled(!on);
      this.dataPanel.setPause(on);
      this.simTime.setPause(on);
   }

   @Override
   public boolean isPause() {
      return this.simTime.isPause();
   }

   @Override
   public timedelivery getTimeSystem() {
      return this;
   }

   @Override
   public long getSimutime() {
      return this.simTime.getSimutime();
   }

   @Override
   public String getSimutimeString() {
      return this.simTime.getSimutimeString();
   }

   @Override
   public String getSimutimeString(long t) {
      return this.simTime.getSimutimeString(t);
   }

   private void setTime(long t) {
      this.simTime.setTime(t);
   }

   @Override
   public AudioController getAudio() {
      return this.uc.getAudio();
   }

   @Override
   public Simulator getSim() {
      return this;
   }

   @Override
   public prefs getSimPrefs() {
      return this.prefs;
   }

   @Override
   public Frame getFrame() {
      return this;
   }

   @Override
   public void setUI(gleis.gleisUIcom gl) {
   }

   @Override
   public void readUI(gleis.gleisUIcom gl) {
   }

   @Override
   public void interPanelCom(AbstractEvent e) {
   }

   @Override
   public void setGUIEnable(boolean e) {
   }

   @Override
   public void showStatus(String s, int type) {
   }

   @Override
   public void showStatus(String s) {
   }

   @Override
   public void setProgress(int p) {
   }

   public void message(String s, stellwerksim_main.MSGLEVELS level) {
      switch(level) {
         case IMPORTANT:
            this.game_time.addImportantText(s);
            break;
         case HINT:
            this.game_time.addText(s, true);
            break;
         default:
            this.game_time.addText(s);
      }
   }

   @Deprecated
   public void message(String s, boolean important) {
      if (important) {
         this.game_time.addImportantText(s);
      } else {
         this.game_time.addText(s);
      }
   }

   @Override
   public void playZug() {
      this.uc.getAudio().playZug();
   }

   public void playBü(int hash) {
      this.uc.getAudio().playBÜ(hash);
   }

   public void playÜG(int hash) {
      this.uc.getAudio().playÜG(hash);
   }

   public void playMessage() {
      this.uc.getAudio().playMessage();
   }

   @Override
   public void playCounter() {
      this.uc.getAudio().playCounter();
   }

   @Override
   public void playAlarm(int cnt) {
      this.uc.getAudio().playAlarm(cnt);
   }

   @Override
   public void playFX(AudioController.FXSOUND f) {
      this.uc.getAudio().playFX(f);
   }

   @Override
   public void playDingdong(int d) {
      this.uc.getAudio().playDingdong(d);
   }

   public void playChatAnruf() {
      this.uc.getAudio().playChatAnruf();
   }

   @Override
   public void playAnruf() {
      this.uc.getAudio().playAnruf();
   }

   public void playChat() {
      this.uc.getAudio().playChat();
   }

   private void syncData() throws IOException {
      this.syncData(false);
   }

   @Override
   public boolean isFirstRun() {
      return this.firstRun;
   }

   private void syncData(boolean last) throws IOException {
      if (this.senddata == null) {
         this.senddata = new StringBuffer();
      }

      this.fahrplanPanel.syncData(this.senddata);
      if (this.offlinemode > 0) {
         this.senddata.append(TextHelper.urlEncode("offline"));
         this.senddata.append('=');
         this.senddata.append(TextHelper.urlEncode(Integer.parseInt(this.uc.getParameter("offline")) + ""));
         this.senddata.append('&');
         if (this.offlinemode == 2) {
            this.senddata.append(TextHelper.urlEncode("expensive"));
            this.senddata.append('=');
            this.senddata.append(TextHelper.urlEncode(Integer.parseInt(this.uc.getParameter("offline")) + ""));
            this.senddata.append('&');
         }
      }

      if (last) {
         this.senddata.append(TextHelper.urlEncode("finish"));
         this.senddata.append('=');
         this.senddata.append("true");
         this.senddata.append('&');
      }

      if (this.firstRun) {
         this.senddata.append(TextHelper.urlEncode("firstRun"));
         this.senddata.append('=');
         this.senddata.append("true");
         this.senddata.append('&');
      }

      this.xmlr.updateData(this.updateurl, this.senddata);
      this.fahrplanPanel.sortTables();
      this.senddata.setLength(0);
      if (this.botMode) {
         this.my_chat.sendStatus(BOTCOMMAND.ALIVE, this.getBuild());
      }

      if (this.firstRun) {
         this.glbControl.paintBuffer();
      }

      this.firstRun = false;
   }

   @Override
   public void handleIRCresult(String cmd, int res, String r, boolean publicmsg) {
      try {
         if (cmd.compareTo("UPDATE") == 0 && res == 200 && !publicmsg) {
            int zid = Integer.parseInt(r.trim());
            if (this.botMode) {
               zug z = this.findZug(zid);
               if (z != null && z.isFertig()) {
                  this.delZug(z);
               }
            }
         } else if (cmd.compareTo("UPDATE") != 0 || res != 300 && res != 310 || publicmsg) {
            if (cmd.compareTo("RUPDATE") == 0 && res == 200) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 4) {
                     int zid = Integer.parseInt(zst.nextToken().trim());
                     int v = 0;
                     int l = 0;
                     int a = 0;
                     int t = 0;

                     try {
                        v = Integer.parseInt(zst.nextToken().trim());
                     } catch (NullPointerException var19) {
                     }

                     try {
                        l = Integer.parseInt(zst.nextToken().trim());
                     } catch (NullPointerException var18) {
                     }

                     try {
                        a = Integer.parseInt(zst.nextToken().trim());
                     } catch (NullPointerException var17) {
                     }

                     if (zst.hasMoreTokens()) {
                        try {
                           t = Integer.parseInt(zst.nextToken().trim());
                        } catch (NullPointerException var16) {
                        }
                     }

                     zug z2 = this.findZug(zid);
                     if (z2 != null && !z2.isFertig()) {
                        z2.setUpdate(v, l, t);
                        z2.setMyTrain(a > 0);

                        String p;
                        for(; zst.hasMoreTokens(); z2.setParam(p)) {
                           p = zst.nextToken();
                           if (debugMode != null) {
                              debugMode.writeln("main/zug", "simparam: " + p);
                           }
                        }
                     } else if (a > 0) {
                        this.my_chat.sendStatus(BOTCOMMAND.NORUPDATE, zid);
                     }
                  }
               }
            } else if (cmd.compareTo("STILLALIVE") == 0 && res == 200) {
               if (this.botMode && !this.firstRun) {
                  this.my_chat.sendStatus(BOTCOMMAND.ALIVE, this.getBuild());
               }
            } else if (cmd.compareTo("MESSAGE") == 0) {
               try {
                  this.message(r.trim(), stellwerksim_main.MSGLEVELS.values()[res - 200]);
               } catch (Exception var15) {
               }
            } else if (cmd.compareTo("USER") == 0 && res == 200) {
               StringTokenizer zst = new StringTokenizer(r + " ", ":");
               if (zst.countTokens() >= 4) {
                  String aid = zst.nextToken().trim();
                  String stw = zst.nextToken().trim();
                  String tel = zst.nextToken().trim();
                  String user = zst.nextToken().trim();
                  this.glbModel.getPhonebook().setTel(aid, tel, stw, user);
               }
            } else if (cmd.compareTo("USER") == 0 && res == 210) {
               StringTokenizer zst = new StringTokenizer(r + " ", ":");
               if (zst.countTokens() >= 3) {
                  String aid = zst.nextToken().trim();
                  String stw = zst.nextToken().trim();
                  String tel = zst.nextToken().trim();
                  this.glbModel.getPhonebook().setTel(aid, tel, stw);
               }
            } else if (cmd.compareTo("SOMETHINGWEIRDHAPPEND") == 0 && res == 200) {
               if (this.botMode) {
                  this.fahrplanPanel.takeAll();
                  this.my_chat.quitBot();
                  this.botMode = false;
               }
            } else if (cmd.compareTo("MAINTENANCE") == 0 && res == 200) {
               if (this.botMode) {
                  this.fahrplanPanel.takeAll();
                  this.my_chat.quitBot();
                  this.botMode = false;
                  this.showText(
                     "<b>Systemwartung</b>, Verbindung zum Server wird getrennt. Alle Züge wurden übergeben. Es erfolgt keine Aktualisierung mehr! Bitte das Spiel beenden!",
                     TEXTTYPE.MESSAGE,
                     this
                  );
                  this.playChatAnruf();

                  for(int i = 0; i < 2; ++i) {
                     this.message(
                        "Systemwartung, Verbindung zum Server wird getrennt. Alle Züge wurden übergeben. Es erfolgt keine Aktualisierung mehr!",
                        stellwerksim_main.MSGLEVELS.IMPORTANT
                     );
                  }
               }
            } else if (cmd.compareTo("XML") == 0 && res == 200 && !publicmsg) {
               this.xmlr.updateDataByString(r.trim());
            } else if (cmd.compareTo("RESERVE") == 0 && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 2) {
                     int enr = Integer.parseInt(zst.nextToken().trim());
                     char type = zst.nextToken().trim().charAt(0);
                     String msg = this.fsalloc.gotReserveMessage(enr, type == 'R');
                     if (res == 200) {
                        this.my_chat.sendStatus(BOTCOMMAND.RESERVERESPONSE, msg + " " + enr + " " + type);
                     }
                  }
               }
            } else if (cmd.compareTo("RESERVERESPONSE") == 0 && res == 200 && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 3) {
                     String msg = zst.nextToken().trim();
                     int enr = Integer.parseInt(zst.nextToken().trim());
                     char type = zst.nextToken().trim().charAt(0);
                     this.uepFsLatencyMeasure.receivingCommand(type + " " + enr);
                     this.fsalloc.gotReserveResponseMessage(msg, enr, type == 'R');
                  }
               }
            } else if (cmd.compareTo("RESERVERESPONSE") == 0 && res == 300 && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 3) {
                     String msg = zst.nextToken().trim();
                     int enr = Integer.parseInt(zst.nextToken().trim());
                     char type = zst.nextToken().trim().charAt(0);
                     this.uepFsLatencyMeasure.receivingCommand(type + " " + enr);
                     this.fsalloc.gotReserveResponseMessage(msg, enr, type == 'R');
                  }
               }
            } else if (cmd.compareTo("TRAIN") == 0 && res == 200 && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 2) {
                     int enr = Integer.parseInt(zst.nextToken().trim());
                     int zid = Integer.parseInt(zst.nextToken().trim());
                     zug z = this.findZug(zid);
                     gleis gl = this.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_EINFAHRT});
                     if (z == null || gl == null) {
                        this.fsalloc.zugTakenMessage("UNK", enr, zid);
                        this.fsalloc.gotReserveMessage(enr, false);
                     } else if (!z.gotByÜP(gl)) {
                        this.fsalloc.zugTakenMessage("UNK", enr, zid);
                        this.fsalloc.gotReserveMessage(enr, false);
                     }
                  } else {
                     Logger.getLogger("stslogger").log(Level.SEVERE, "TRAIN too short " + r + ":" + zst.countTokens());
                  }
               } else {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "TRAIN not bot " + r);
               }
            } else if (cmd.compareTo("TRAINBLOCK") == 0 && res == 200 && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 2) {
                     int enr = Integer.parseInt(zst.nextToken().trim());
                     int zid = Integer.parseInt(zst.nextToken().trim());
                     zug z = this.findZug(zid);
                     gleis gl = this.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_EINFAHRT});
                     if (z != null && gl != null) {
                        z.setExternalÜpReport();
                     }
                  } else {
                     Logger.getLogger("stslogger").log(Level.SEVERE, "TRAIN too short " + r + ":" + zst.countTokens());
                  }
               } else {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "TRAIN not bot " + r);
               }
            } else if (cmd.compareTo("TRAINRESPONSE") == 0 && (res == 200 || res == 210 || res == 300) && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 3) {
                     String msg = zst.nextToken().trim();
                     int enr = Integer.parseInt(zst.nextToken().trim());
                     int zid = Integer.parseInt(zst.nextToken().trim());
                     zug z = zug.findZugAtENR(enr);
                     if (z != null && z.getZID_num() == zid) {
                        z.leaveAfterÜP(res != 210);
                     }
                  }
               }
            } else if (cmd.compareTo("TRAINRESPONSE") == 0 && res == 310 && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 2) {
                     String msg = zst.nextToken().trim();
                     int enr = Integer.parseInt(zst.nextToken().trim());
                     if (msg.compareToIgnoreCase("LFT") == 0) {
                        zug z = zug.findZugAtENR(enr);
                        if (z != null) {
                           z.leaveAfterÜP(true);
                        }
                     }
                  }
               }
            } else if (cmd.compareTo("ENTERSIGNAL") == 0 && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 2) {
                     int enr = Integer.parseInt(zst.nextToken().trim());
                     String stellung = "üp" + zst.nextToken().trim();
                     this.fsalloc.gotEnterSignalMessage(enr, gleisElements.Stellungen.string2stellung(stellung));
                  }
               }
            } else if (cmd.compareTo("FATCODECHECK") == 0 && !publicmsg) {
               if (res == 200 && this.structserv == null) {
                  this.structserv = new structServer(this);
               }

               this.uc.busPublish(new fat.FatResponseEvent(res));
            } else if (cmd.compareTo("EVENT") == 0) {
               this.glbModel.IRCeventTrigger(r);
            } else if (cmd.compareTo("DIALOG") == 0) {
               SwingUtilities.invokeLater(new stellwerksim_main.msgRunnable(r, res == 200));
            } else if (cmd.equals("ZREDIRECTWAY") && !publicmsg) {
               if (this.botMode) {
                  redirectRouteSpecify.invokeLater(this, cmd, res, new StringTokenizer(r + " ", ":"), this.glbModel.getPhonebook());
               }
            } else if (cmd.equals("ZREDIRECTACK") && !publicmsg) {
               if (this.botMode) {
                  redirectRouteSpecify.invokeLater(this, cmd, res, new StringTokenizer(r + " ", ":"), this.glbModel.getPhonebook());
               }
            } else if (cmd.equals("ZREDIRECTINFO") && !publicmsg) {
               if (this.botMode) {
                  Thread.yield();
                  redirectRouteSpecify.invokeLater(this, cmd, res, new StringTokenizer(r + " ", ":"), this.glbModel.getPhonebook());
               }
            } else if (cmd.equals("ZREDIRECTUPD") && res == 200 && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 3) {
                     int zid = Integer.parseInt(zst.nextToken().trim());
                     zug z = this.findZug(zid);
                     if (z != null) {
                        int rd = z.updateEinAus(zst.nextToken().trim(), zst.nextToken().trim());
                        if (rd == 1) {
                           zidRedirectPanel zr = (zidRedirectPanel)this.redirects.get(z.getZID_num());
                           if (zr == null) {
                              zr = new zidRedirectPanel(this.uc, this, this.glbModel.getPhonebook(), z);
                              this.redirects.put(z.getZID_num(), zr);
                           }

                           zr.setVisible(true);
                        }
                     }
                  }
               }
            } else if (cmd.equals("ZREDIRECTMSG") && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 1) {
                     int zid = Integer.parseInt(zst.nextToken().trim());
                     zug z = this.findZug(zid);
                     if (z != null) {
                        String msg = "Zug " + z.getSpezialName() + " wird " + (res != 200 ? "nicht " : "") + "umgeleitet.";
                        if (res > 300) {
                           msg = msg + " (G " + (res - 300) + ")";
                        }

                        this.showText(msg, TEXTTYPE.MESSAGE, z);
                        if (res != 200) {
                           this.playAnruf();
                        }
                     }
                  }
               }
            } else if (cmd.equals("REDIRINFO") && publicmsg) {
               if (this.botMode) {
                  String[] tokens = TextHelper.tokenizerToArray(new StringTokenizer(r + " ", ":"));
                  zug z = null;

                  for(int i = 0; i < tokens.length; ++i) {
                     if (tokens[i].equals("ZID")) {
                        int zid = Integer.parseInt(tokens[i + 1].trim());
                        z = this.findZug(zid);
                        break;
                     }
                  }

                  if (z != null) {
                     zidRedirectPanel zr = (zidRedirectPanel)this.redirects.get(z.getZID_num());
                     if (zr != null) {
                        zr.update(res, tokens);
                        if (res == 400) {
                           this.redirects.remove(z.getZID_num());
                        }
                     }
                  }
               }
            } else if (cmd.compareTo("DROP") == 0 && res == 200 && !publicmsg) {
               if (this.botMode) {
                  int zid = Integer.parseInt(r.trim());
                  zug z = this.findZug(zid);
                  if (z != null) {
                     if (!z.isVisible()) {
                        this.dropZug(z);
                     }

                     zidRedirectPanel zr = (zidRedirectPanel)this.redirects.get(z.getZID_num());
                     if (zr != null) {
                        zr.close();
                        this.redirects.remove(z.getZID_num());
                     }
                  }
               }
            } else if (cmd.compareTo("NEWTRAIN") == 0 && res == 200 && !publicmsg) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 7) {
                     int zid = Integer.parseInt(zst.nextToken().trim());
                     if (!this.fahrplanPanel.containsZug(zid)) {
                        zug.emitData ed = new zug.emitData();
                        ed.mytrain = false;
                        ed.name = zst.nextToken().trim();
                        ed.verspaetung = 0;
                        ed.lastStopDone = true;

                        try {
                           ed.länge = Integer.parseInt(zst.nextToken().trim());
                        } catch (NullPointerException var14) {
                        }

                        try {
                           ed.soll_tempo = Integer.parseInt(zst.nextToken().trim());
                        } catch (NullPointerException var13) {
                        }

                        ed.ein_stw = zst.nextToken().trim();

                        for(ed.aus_stw = zst.nextToken().trim(); zst.hasMoreTokens(); ed.name = zst.nextToken()) {
                           if (ed.name == null) {
                              ed.name = "";
                           } else {
                              ed.name = ed.name + ":";
                           }
                        }

                        zug nz = this.additionalZug(zid, ed);
                        String text = "Achtung! Zugumleitung " + nz.getSpezialName() + "!";
                        this.showText(text, TEXTTYPE.MESSAGE, nz);
                     }
                  }
               }
            } else if (cmd.equals("ZREQUESTFPL") && res == 200 && !publicmsg) {
               if (this.botMode) {
                  redirectRouteSpecify.invokeLater(this, cmd, res, new StringTokenizer(r + " ", ":"), this.glbModel.getPhonebook());
               }
            } else if (cmd.equals("HOSTINFO")) {
               this.sendHostinfo();
            } else if (cmd.equals("EVENTCALL")) {
               if (this.botMode) {
                  StringTokenizer zst = new StringTokenizer(r + " ", ":");
                  if (zst.countTokens() >= 2) {
                     event.startActivityCall(zst.nextToken(), zst.nextToken());
                  }
               }
            } else if (cmd.equals("CRESULT") && !publicmsg) {
               if (this.botMode && this.cheatMgr != null) {
                  this.cheatMgr.result(res, r);
               }
            } else if (cmd.compareTo("REPORT") != 0 || res != 200) {
               if (cmd.compareTo("REPORT") == 0 && res == 210) {
                  ;
               }
            }
         }
      } catch (NumberFormatException var20) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "handleIrc NFE", var20);
      } catch (Exception var21) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "handleIrc EX", var21);
      }
   }

   @Override
   public void handleIRC(String sender, String r, boolean publicmsg) {
      if (!publicmsg) {
         if (r.compareTo("WHAT") == 0) {
            String v = this.getParameter("nick1")
               + "/"
               + this.getParameter("nick2")
               + "/"
               + this.getParameter("nick3")
               + " playing on "
               + this.glbModel.getAnlagenname()
               + " with version: "
               + this.uc.getBuild();
            this.my_chat.sendStatusToChannel(sender, v);
         } else if (r.startsWith("TROLL")) {
            this.my_chat.kick();
         } else if (r.startsWith("ZUEGE")) {
            try {
               r = r.substring(6);
            } catch (Exception var9) {
               r = "";
            }

            StringBuffer v = this.fahrplanPanel.zugReport(r.trim());

            String t;
            try {
               for(int i = 0; i < v.length(); i += t.length() + 1) {
                  int l = Math.min(i + 150, v.length());
                  t = v.substring(i, l);
                  if (t.contains("\n")) {
                     t = t.substring(0, t.indexOf("\n"));
                  }

                  this.my_chat.sendStatusToUser(sender, t);
               }
            } catch (Exception var10) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "caught EX", var10);
            }
         } else if (r.startsWith("DEBUG")) {
            this.my_chat.sendStatusToUser(sender, "Version: " + this.uc.getBuild());
            String v = "Properties: ";
            this.my_chat.sendStatusToUser(sender, v);

            for(String p : System.getProperties().stringPropertyNames()) {
               try {
                  v = p + ":" + System.getProperty(p);
               } catch (Exception var8) {
                  v = p + ": EXCEPTION " + var8.getMessage();
               }

               this.my_chat.sendStatusToUser(sender, v);
            }
         } else if (r.startsWith("HOSTINFO")) {
            this.sendHostinfo(sender);
         } else if (r.startsWith("help")) {
            this.my_chat.sendStatusToUser(sender, "Introduce yourself with the answer 'IAM yourname'.");
         } else if (r.equals("PROFILEURL")) {
            if (this.getParameter("profileurl") != null) {
               this.my_chat.sendStatusToUser(sender, this.getParameter("profileurl"));
            }
         } else if (r.equals("STARTSTRUCTSERV")) {
            if (this.structserv == null) {
               this.structserv = new structServer(this);
            }
         } else if (r.equals("STOPSTRUCTSERV")) {
            if (this.structserv != null) {
               this.structserv.stop();
               this.structserv = null;
            }
         } else if (r.equals("Ich mag keine BÜs.") && sender.equals("myselfnickAndI")) {
            gleis.michNervenBüs = !gleis.michNervenBüs;
         }
      }
   }

   private void sendHostinfo() {
      this.my_chat.sendStatus(BOTCOMMAND.HOSTINFO, this.getHostinfo());
   }

   private void sendHostinfo(String sender) {
      this.my_chat.sendStatusToUser(sender, "HOSTINFO: " + this.getHostinfo());
   }

   private String getHostinfo() {
      String ret = "-";
      if (this.my_chat != null) {
         ret = this.my_chat.getLocalAddress().getHostAddress();
      }

      return ret;
   }

   @Override
   public void chatDisconnected(String msg) {
      if (this.botMode) {
         this.fahrplanPanel.takeAll();
         this.botMode = false;
         message1 m = new message1(
            this,
            false,
            "Störung",
            "Die Internet-Verbindung zum Server wurde unterbrochen!\nBitte die eigene Internetverbindung überprüfen.\nEs werden keine Zugdaten mehr aktualisiert, Sim am besten neu starten."
         );
         m.show();
      }

      if (this.qsmode) {
         this.qsmode = false;
         this.fahrplanPanel.endQS();
         message1 m = new message1(
            this,
            false,
            "Störung",
            "Die Internet-Verbindung zum Server wurde unterbrochen!\nBitte die eigene Internetverbindung überprüfen.\nEs können keine Fehlermeldungen mehr gesendet werden. Sim am besten neu starten."
         );
         m.show();
      }
   }

   public void simulateClick() {
      this.glbControl.simulateClick();
   }

   public void run() {
      this.glbControl.setEnabled(false);
      int inaktivität = 0;
      this.firstRun = true;
      this.fahrplanPanel.clear();
      if (!this.emitterMode && (this.botMode || this.qsmode)) {
         this.my_chat = new BotChat(this, this.uc, this.updateurl);
         this.fahrplanPanel.updateMsgPopup();
         if (this.botMode && !this.invisibleMode) {
            this.uc.busPublish(new GameInfoEvent(this.glbModel.getAnlagenname()));
            this.message("Online vernetztes Spiel von " + this.glbModel.getAnlagenname(), stellwerksim_main.MSGLEVELS.NORMAL);
         }
      } else {
         this.my_chat = new SandboxChat(this, this.uc);
         if (!this.emitterMode && !this.invisibleMode) {
            this.uc.busPublish(new GameInfoEvent("übt " + this.glbModel.getAnlagenname()));
            this.message("Übungsmodus von " + this.glbModel.getAnlagenname(), stellwerksim_main.MSGLEVELS.NORMAL);
         }
      }

      this.syncdelay = 120;
      this.xmlr = new xmlreader();
      this.xmlr.registerTag("zeit", this);
      this.xmlr.registerTag("zug", this);
      this.xmlr.registerTag("syncdelay", this);
      this.xmlr.registerTag("limitstart", this);
      this.xmlr.registerTag("mitteilung", this);
      this.mitteilungen = new LinkedList();
      this.glbControl.setEnabled(true);
      long localTimeCheck = System.currentTimeMillis();

      try {
         label118:
         while(this.running) {
            if (this.emitterMode) {
               Thread.sleep((long)(1000 * this.syncdelay));
            } else {
               try {
                  this.syncData();
                  this.hideOverlay();
               } catch (IOException var15) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "Zug Sync Fehler IO", var15);
                  this.syncdelay = 120;
               } catch (Exception var16) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "Zug Sync Fehler EX", var16);
                  this.syncdelay = 120;
               }

               try {
                  while(true) {
                     this.message((String)this.mitteilungen.removeFirst(), stellwerksim_main.MSGLEVELS.NORMAL);
                  }
               } catch (NoSuchElementException var17) {
                  this.glbModel.setVerspaetungen();
                  this.glbModel.sendVorsignal();
                  long localTimeCheck2 = System.currentTimeMillis();
                  if (localTimeCheck2 < localTimeCheck) {
                     Logger.getLogger("stslogger").log(Level.SEVERE, "Lokale Zeit reduziert sich!");
                  }

                  localTimeCheck = localTimeCheck2;
                  int sd = this.syncdelay;

                  while(sd > 0) {
                     int p = 60;
                     if (sd < p) {
                        p = sd;
                     }

                     sd -= p;

                     try {
                        Thread.sleep((long)(1000 * p));
                     } catch (InterruptedException var14) {
                     }

                     if (!this.running) {
                        break label118;
                     }

                     if (this.botMode) {
                        this.my_chat.sendStatus(BOTCOMMAND.ALIVE, this.getBuild());
                        if (this.syncdelay > 10000) {
                           if (debugMode != null) {
                              debugMode.writeln("main/sync", "syncdelay>10000 OK");
                           }

                           Date d = new Date(this.getSimutime());
                           if (d.getMinutes() >= 50) {
                              if (debugMode != null) {
                                 debugMode.writeln("main/sync", "syncdelay>10000, minutes>=50 OK");
                              }

                              this.my_chat.quitBot();
                              this.botMode = false;
                              String endMsg = "Simulation hat sich vom Online-Spiel gelöst, da die Tagesgrenze bald erreicht wird.";
                              this.message("Simulation hat sich vom Online-Spiel gelöst, da die Tagesgrenze bald erreicht wird.", true);
                              message1 m = new message1(
                                 this, false, "Hinweis", "Simulation hat sich vom Online-Spiel gelöst, da die Tagesgrenze bald erreicht wird."
                              );
                              m.show();
                              this.fahrplanPanel.takeAll();
                              this.simTime.endOfTime();
                           }
                        }
                     }

                     if (this.botMode) {
                        long lc = this.glbControl.getLastClick();
                        if (lc > 0L) {
                           long current = System.currentTimeMillis() - 420000L;
                           if (lc < current && this.fahrplanPanel.isInactive() && !this.isExtraMode()) {
                              String text = "Achtung! Es wurde längere Zeit keine Aktivität mehr festgestellt! Das Spiel beendet sich bei weiterer Inaktivität in Kürze automatisch!";
                              this.showText(text, TEXTTYPE.MESSAGE, this);
                              this.playAnruf();
                              this.message(text, stellwerksim_main.MSGLEVELS.IMPORTANT);
                              if (inaktivität > 1) {
                                 this.formWindowClosing(null);
                              } else if (inaktivität > 0) {
                                 message1 m = new message1(this, false, "Achtung!", text);
                                 m.show();
                              }

                              ++inaktivität;
                           } else {
                              inaktivität = 0;
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (InterruptedException var18) {
      }

      if (this.ctrlSrv != null) {
         this.enableCtrlSrv(false);
      }
   }

   boolean enableCtrlSrv(boolean m) {
      if (this.ctrlSrv == null && m) {
         this.ctrlSrv = new controlServ(this);
      } else if (!m) {
         this.ctrlSrv.close();
         this.ctrlSrv = null;
      }

      return this.ctrlSrv != null;
   }

   boolean isCtrlSrv() {
      return this.ctrlSrv != null;
   }

   public zug emittZug(int gzid, zug.emitData ed) {
      this.message("Neuer Zug: ZID " + gzid, stellwerksim_main.MSGLEVELS.NORMAL);
      return new zug(this.glbModel, this, gzid, ed);
   }

   public zug additionalZug(int gzid, zug.emitData ed) {
      return new zug(this.glbModel, this, gzid, ed);
   }

   public void parseStartTag(String tag, Attributes attrs) {
   }

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
      if (debugMode != null) {
         debugMode.writeln("main/xml", "XML: " + tag + "//pcdata: " + pcdata);
      }

      if (tag.compareTo("syncdelay") == 0) {
         try {
            this.syncdelay = Integer.parseInt(pcdata.trim());
         } catch (NumberFormatException var9) {
            this.syncdelay = 120;
         }

         if (debugMode != null) {
            debugMode.writeln("main/xml", "syncdelay: " + this.syncdelay);
         }

         if (this.offlinemode == 3) {
            String n = attrs.getValue("next");
            if (n != null) {
               this.updateurl = this.uc.getParameter("nextFile" + n.trim());
            } else {
               this.syncdelay = 999;
            }
         }
      } else if (tag.compareTo("zeit") == 0) {
         if (this.syncedTime) {
            String v = attrs.getValue("filesync");
            if (v != null && v.equalsIgnoreCase("y")) {
               this.setSimTime(new timedeliveryLoaded());
            }
         }

         if (this.firstRun) {
            try {
               TimeFormat mdf = TimeFormat.getInstance(TimeFormat.STYLE.HMS);
               long newsimutime = mdf.parse(pcdata.trim());
               this.setTime(newsimutime);
            } catch (ParseException var8) {
            }

            this.glbControl.initEventTJM();
         }
      } else if (tag.compareTo("zug") == 0) {
         try {
            new zug(attrs, pcdata != null ? pcdata.trim() : null, this.glbModel, this);
         } catch (Exception var7) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "Zug Fehler", var7);
         }
      } else if (tag.compareTo("mitteilung") == 0) {
         this.mitteilungen.addLast(pcdata.trim());
      }
   }

   @Override
   public String getParameter(String p) {
      return this.uc.getParameter(p);
   }

   public String getUpdateURL() {
      return this.updateurl;
   }

   private void setPauseMenu() {
      this.pauseMenu1 = new JCheckBoxMenuItem();
      this.pauseMenu1.setText("Pause");
      this.pauseMenu1.setAccelerator(KeyStroke.getKeyStroke(80, 2));
      this.pauseMenu1.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            stellwerksim_main.this.setPause(stellwerksim_main.this.pauseMenu1.isSelected());
            if (stellwerksim_main.this.isPause()) {
               JOptionPane.showMessageDialog(stellwerksim_main.this, "Pause", "Pause", 1);
               stellwerksim_main.this.pauseMenu1.setSelected(false);
            }
         }
      });
      this.optionsMenu.addSeparator();
      this.optionsMenu.add(this.pauseMenu1);
   }

   private void setStoreAudioPrefsMenu() {
      ActionListener al = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            stellwerksim_main.this.asettings.open();

            try {
               stellwerksim_main.this.asettings.playBÜSettings().setEnabled(stellwerksim_main.this.büSoundMenu.isSelected());
               stellwerksim_main.this.asettings.playÜGSettings().setEnabled(stellwerksim_main.this.akzeptorSoundMenu.isSelected());
               stellwerksim_main.this.asettings.playMessageSettings().setEnabled(stellwerksim_main.this.funkSoundMenu.isSelected());
               stellwerksim_main.this.asettings.playZugSettings().setEnabled(stellwerksim_main.this.zugSoundMenu.isSelected());
               stellwerksim_main.this.asettings.playCounterSettings().setEnabled(stellwerksim_main.this.zählwerkSoundMenu.isSelected());
            } finally {
               stellwerksim_main.this.asettings.close();
            }
         }
      };
      this.büSoundMenu.addActionListener(al);
      this.akzeptorSoundMenu.addActionListener(al);
      this.funkSoundMenu.addActionListener(al);
      this.zugSoundMenu.addActionListener(al);
      this.zählwerkSoundMenu.addActionListener(al);
   }

   @EventHandler
   public void syncStoreAudioPrefsMenu(AudioSettingsChangedEvent event) {
      this.büSoundMenu.setSelected(this.asettings.playBÜSettings().isEnabled());
      this.akzeptorSoundMenu.setSelected(this.asettings.playÜGSettings().isEnabled());
      this.funkSoundMenu.setSelected(this.asettings.playMessageSettings().isEnabled());
      this.zugSoundMenu.setSelected(this.asettings.playZugSettings().isEnabled());
      this.zählwerkSoundMenu.setSelected(this.asettings.playCounterSettings().isEnabled());
   }

   @Override
   public void incZählwert() {
      ++this.zählwerk;
      this.extrasPanel.setZählwert(this.zählwerk);
      this.playCounter();
   }

   public void exit() {
      this.formWindowClosing(null);
   }

   @Override
   public int getBuild() {
      return this.uc.getBuild();
   }

   private void showOverlay(String name) {
      try {
         URL url = new URL(name);
         JLayeredPane lp = this.getLayeredPane();
         JLabel l = new JLabel();
         l.setIcon(new ImageIcon(url));
         this.over_pan = new JPanel();
         this.over_pan.setOpaque(false);
         this.over_pan.setLayout(new BorderLayout());
         this.over_pan.add(l, "North");
         lp.add(this.over_pan, JLayeredPane.DRAG_LAYER);
         Dimension d = this.getSize();
         this.over_pan.setSize(d);
         this.over_pan.setLocation(0, this.menuBar.getHeight());
      } catch (MalformedURLException var6) {
      }
   }

   private void hideOverlay() {
      if (this.over_pan != null && !this.timerStarted) {
         this.timerStarted = true;
         javax.swing.Timer t = new javax.swing.Timer(3000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (stellwerksim_main.this.over_pan != null) {
                  stellwerksim_main.this.over_pan.getParent().remove(stellwerksim_main.this.over_pan);
                  stellwerksim_main.this.over_pan = null;
                  stellwerksim_main.this.repaint();
               }
            }
         });
         t.start();
      }
   }

   void autoMsg(String txt, String chn, zug z) {
      if (this.my_chat != null) {
         String m = z.getSpezialName() + " " + z.getVerspaetung();
         this.my_chat.sendText(chn, "Automatische Zugmeldung: " + m);
         this.fahrplanPanel.addSent(new stellwerksim_main.zugMsg(m, txt));
      }
   }

   public void autoMsg(String txt, zug z) {
      if (this.my_chat != null) {
         String chn = this.my_chat.findMatchingChannelName(txt);
         if (chn != null) {
            this.autoMsg(txt, chn, z);
         }
      }
   }

   @Override
   public void checkAutoMsg(String nick, String channel, String msg) {
      if (msg.startsWith("Automatische Zugmeldung: ")) {
         try {
            String m = msg.substring("Automatische Zugmeldung: ".length());
            this.fahrplanPanel.addReceived(new stellwerksim_main.zugMsg(m, nick));
         } catch (Exception var5) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "checkAutoMsg", var5);
         }
      }
   }

   @Override
   public control getMsgControl() throws NullPointerException {
      return this.msgControl;
   }

   @Override
   public boolean isDumper() {
      return this.dumperMode != null;
   }

   private void addDumper(String text, ActionListener action) {
      JMenuItem m = new JMenuItem();
      m.setText(text);
      m.addActionListener(action);
      this.helpMenu.add(m);
      this.dumperMenu.add(m);
   }

   @Override
   public void setDumper(FATwriter w) {
      this.dumperMode = w;
      this.fahrplanPanel.setDumperP(w);
      if (this.dumperMode == null) {
         for(JMenuItem m : this.dumperMenu) {
            try {
               m.getParent().remove(m);
            } catch (Exception var5) {
            }
         }
      } else {
         this.addDumper("IRCdump", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               if (stellwerksim_main.this.dumperMode != null) {
                  stellwerksim_main.this.dumperMode.writeln(stellwerksim_main.this.my_chat);
               }
            }
         });
         this.addDumper("Störungesdump", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               if (stellwerksim_main.this.dumperMode != null) {
                  stellwerksim_main.this.dumperMode.writeln(thema.iterator());
                  stellwerksim_main.this.dumperMode.writeln(stellwerksim_main.this.glbModel.eventsIterator());
               }
            }
         });
         this.addDumper("FSdump", new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               if (stellwerksim_main.this.dumperMode != null) {
                  stellwerksim_main.this.dumperMode.writeln(stellwerksim_main.this.glbModel.fahrstrassenIterator());
               }
            }
         });
      }
   }

   public static enum MSGLEVELS {
      NORMAL,
      IMPORTANT,
      HINT;
   }

   private class msgRunnable implements Runnable {
      private String msg;
      private boolean modal;

      msgRunnable(String _msg, boolean modal) {
         super();
         this.msg = _msg;
         this.modal = modal;
      }

      public void run() {
         if (this.modal) {
            JOptionPane.showMessageDialog(stellwerksim_main.this, this.msg);
         } else {
            message1 m = new message1(stellwerksim_main.this, false, "Hinweis", this.msg);
            m.show();
         }
      }
   }

   class zugMsg {
      long timestamp = 0L;
      String msg = "";
      String absender = "";

      zugMsg(String m) {
         super();
         this.msg = m;
         this.timestamp = stellwerksim_main.this.getSimutime();
      }

      zugMsg(String m, String a) {
         this(m);
         this.absender = a;
      }

      String getTime() {
         return stellwerksim_main.this.msgdf.format(this.timestamp);
      }
   }
}
