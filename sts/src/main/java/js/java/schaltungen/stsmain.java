package js.java.schaltungen;

import de.deltaga.eb.BusExceptionEvent;
import de.deltaga.eb.DelayEventManager;
import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.jnlp.ServiceManager;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import js.java.schaltungen.adapter.MessageAdapter;
import js.java.schaltungen.adapter.RelaunchModuleQuery;
import js.java.schaltungen.adapter.SpoolLaunchEvent;
import js.java.schaltungen.audio.AudioSettings;
import js.java.schaltungen.audio.AudioSettingsImpl;
import js.java.schaltungen.cevents.OccurancesSystem;
import js.java.schaltungen.chatcomng.ChatNG;
import js.java.schaltungen.chatcomng.ChatWindow;
import js.java.schaltungen.chatcomng.GlobalLatency;
import js.java.schaltungen.chatcomng.IrcConnectedEvent;
import js.java.schaltungen.chatcomng.ShowConsoleEvent;
import js.java.schaltungen.chatcomng.StartRoomState;
import js.java.schaltungen.injector.Injector;
import js.java.schaltungen.settings.Settings;
import js.java.schaltungen.settings.ShowSettingsEvent;
import js.java.schaltungen.switchbase.DataSwitch;
import js.java.schaltungen.switchbase.DumpSwitchValueEvent;
import js.java.schaltungen.switchbase.SwitchValueEvent;
import js.java.schaltungen.switchbase.SwitchValueManipulator;
import js.java.schaltungen.toplevelMessage.States;
import js.java.schaltungen.toplevelMessage.TopLevelMessage;
import js.java.schaltungen.webservice.WebServiceConnector;
import js.java.tools.prefs;
import js.java.tools.dialogs.aboutDialog;
import js.java.tools.gui.SwingTools;
import js.java.tools.logging.DialogHandler;
import js.java.tools.logging.ExceptionDialog;
import js.java.tools.logging.HttpHandler;

public class stsmain implements UserContextMini, stsmainMBean, SingleInstanceListener {
   public static final String LOGGER_NAME = "stslogger";
   private MessageAdapter madapter;
   public static final String majorRelease = "5.2 predicted web edition";
   public static final String copyright = "(c) JS 2004-2023";
   private final String token;
   private final String uid;
   private final String username;
   private ChatNG mychat;
   private TrayIcon trayIcon;
   private ChatWindow chatWindow;
   private BufferedImage icon;
   private boolean readyToJoin = false;
   private DelayEventManager delayManager;
   private ConsoleFrame console;
   private Settings diWindow;
   private final MBeanServer mbs;
   private ObjectName name;
   private OccurancesSystem oSystem;
   private final boolean onMac;
   private WebServiceConnector webService;
   private final DataSwitch dataSwitch = new DataSwitch();
   private WeakReference<TopLevelMessage> lastMessage = null;

   public static void main(final String[] argv) throws InterruptedException, InvocationTargetException {
      if (argv.length < 3) {
         System.err.println("Argumente fehlen.");
      } else {
         new prefs("/org/js-home/stellwerksim/gfxfixes");
         SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
               try {
                  new stsmain(argv[0], argv[1], argv[2]).start();
               } catch (Exception var2) {
                  Logger.getLogger(stsmain.class.getName()).log(Level.SEVERE, null, var2);
                  new ExceptionDialog().handle(var2);
                  throw new RuntimeException(var2);
               }
            }
         });
      }
   }

   private stsmain(String token, String uid, String username) throws RemoteException, MalformedURLException, IOException {
      super();
      this.token = token;
      this.uid = uid;
      this.username = username;
      this.onMac = System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0;
      String url = this.getParameter(UserContextMini.DATATYPE.LOG);
      if (url != null) {
         Logger.getGlobal().addHandler(new HttpHandler(url, this.getBuild()));
      }

      System.setProperty("java.rmi.server.randomIDs", "true");

      try {
         LocateRegistry.createRegistry(3000);
      } catch (Exception var6) {
      }

      this.mbs = ManagementFactory.getPlatformMBeanServer();
      Thread t = new Thread() {
         public void run() {
            try {
               Map env = new HashMap();
               env.put("com.sun.management.jmxremote.ssl", "false");
               env.put("com.sun.management.jmxremote.authenticate", "false");
               JMXServiceURL jurl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:3000/jmxrmi");
               JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(jurl, env, stsmain.this.mbs);
               cs.start();
               stsmain.this.registerPermanentMBean(stsmain.this, "stsmain");
            } catch (Exception var4) {
            }
         }
      };
      t.start();
      this.makeSingleton();
   }

   public void newActivation(String[] params) {
      if (this.chatWindow == null) {
         System.exit(0);
      } else {
         this.chatWindow.setVisible(true);
      }
   }

   private void makeSingleton() {
      Thread t = new Thread(() -> {
         try {
            SingleInstanceService isisx = (SingleInstanceService)ServiceManager.lookup("javax.jnlp.SingleInstanceService");
            isisx.removeSingleInstanceListener(this);
         } catch (Throwable var3) {
         }
      });

      try {
         SingleInstanceService isis = (SingleInstanceService)ServiceManager.lookup("javax.jnlp.SingleInstanceService");
         isis.addSingleInstanceListener(this);
         Runtime.getRuntime().addShutdownHook(t);
      } catch (Throwable var4) {
         Logger.getLogger("stslogger").log(Level.WARNING, "No javax.jnlp.SingleInstanceService: {0}", var4.getMessage());
      }
   }

   @Override
   public void allExceptionsDialog() {
      Logger.getLogger("stslogger").addHandler(new DialogHandler());
   }

   @Override
   public void noExceptionsDialog() {
      Handler[] h = Logger.getLogger("stslogger").getHandlers();

      for(int i = h.length - 1; i >= 0; --i) {
         if (h[i] instanceof DialogHandler) {
            Logger.getLogger("stslogger").removeHandler(h[i]);
         }
      }
   }

   @Override
   public void setProperty(String name, String value) {
      System.setProperty(name, value);
      System.out.println("setProperty(" + name + "," + value + ")");
   }

   @Override
   public final int getBuild() {
      int b = 0;

      try {
         Properties prop = new Properties();
         prop.load(this.getClass().getClassLoader().getResourceAsStream("js/java/build/build.property"));
         b = Integer.parseInt(prop.getProperty("build", b + ""));
      } catch (Exception var3) {
         System.out.println("Ex build loader: " + var3.getMessage());
         var3.printStackTrace();
      }

      return b;
   }

   private void setDefaultLookAndFeel() {
      SwingTools.setPLAF();
   }

   private void start() {
      this.setDefaultLookAndFeel();

      try {
         this.icon = ImageIO.read(this.getClass().getResourceAsStream("/js/java/tools/resources/sts-logo.png"));
      } catch (IOException var3) {
         Logger.getLogger("stslogger").log(Level.SEVERE, null, var3);
      }

      this.console = new ConsoleFrame(this);
      String user = System.getProperty("jnlp.netbeans_user");
      String pass = System.getProperty("jnlp.netbeans_pass");
      if (user != null && pass != null) {
         System.out.println("HTTP_AUTH PASSWORT aktiv: " + user);
         Authenticator.setDefault(new stsmain.MyAuthenticator(user, pass));
      }

      Logger.getLogger("stslogger").log(Level.WARNING, "Ohne einen Fehler ist eine Warnung nur eine Warnung und unkritisch.");
      Logger.getLogger("stslogger").log(Level.INFO, "Ohne einen Fehler ist eine Information nur eine Information und unkritisch.");
      this.delayManager = new DelayEventManager(EventBusService.getInstance());
      this.madapter = new MessageAdapter(this);
      this.oSystem = new OccurancesSystem(this);
      this.verify();
   }

   private void verify() {
      EventBusService.getInstance().subscribe(this);
      System.out.println("Build: " + this.getBuild());
      this.showTopLevelMessage("Darf ich mich vorstellen: Der Platz für Programm-Meldungen.", 10);

      try {
         this.webService = new WebServiceConnector(this, this.getParameter(UserContextMini.DATATYPE.WEBSERVICE));
      } catch (MalformedURLException var2) {
         Logger.getLogger(stsmain.class.getName()).log(Level.SEVERE, null, var2);
      }

      this.mychat = new ChatNG(this);
      new StartVerify(this).start(() -> this.startIRC());
      this.diWindow = new Settings(this);
   }

   @EventHandler
   public void ircConnected(IrcConnectedEvent e) {
      if (this.readyToJoin) {
         this.mychat.setRoomState(this, StartRoomState.INIT);
      } else {
         this.readyToJoin = true;
      }

      new GlobalLatency(this);
   }

   private void startIRC() {
      this.chatWindow = new ChatWindow(this, SystemTray.isSupported());
      this.chatWindow.setVisible(true);
      if (this.readyToJoin) {
         this.mychat.setRoomState(this, StartRoomState.INIT);
      } else {
         this.readyToJoin = true;
      }

      if (SystemTray.isSupported()) {
         try {
            SystemTray tray = SystemTray.getSystemTray();
            this.trayIcon = new TrayIcon(this.icon);
            this.trayIcon.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  stsmain.this.chatWindow.setVisible(true);
               }
            });
            PopupMenu popup = new PopupMenu();
            MenuItem chatItem = new MenuItem("Chat Fenster öffnen");
            chatItem.setFont(this.chatWindow.getFont().deriveFont(1));
            chatItem.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  stsmain.this.chatWindow.setVisible(true);
               }
            });
            MenuItem consoleItem = new MenuItem("Meldungskonsole");
            consoleItem.addActionListener(e -> this.showConsole(null));
            MenuItem exitItem = new MenuItem("Beenden");
            exitItem.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  stsmain.this.exit();
               }
            });
            popup.add(chatItem);
            popup.add(consoleItem);
            popup.add(exitItem);
            this.trayIcon.setPopupMenu(popup);
            this.trayIcon.setToolTip("StellwerkSim");
            this.trayIcon.setImageAutoSize(true);
            tray.add(this.trayIcon);
         } catch (AWTException var6) {
            Logger.getLogger("stslogger").log(Level.SEVERE, null, var6);
            this.trayIcon = null;
            this.chatWindow.butNoTray();
         }
      } else {
         System.out.println("NoTray");
      }

      String sandbox = System.getProperty("jnlp.sandbox");
      if (sandbox != null) {
         new Injector(this, sandbox).setVisible(true);
      }

      this.registerPermanentMBean(this.madapter, "MessageAdapter");
      this.registerPermanentMBean(this.mychat, "IRC");
      this.registerPermanentMBean(this.chatWindow, "ChatWindow");
   }

   @EventHandler
   public void showConsole(ShowConsoleEvent evt) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.console.setVisible(true);
      } else {
         SwingUtilities.invokeLater(() -> this.showConsole(evt));
      }
   }

   @EventHandler
   public void showDesktopSetup(ShowSettingsEvent evt) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.diWindow.setVisible(true);
      } else {
         SwingUtilities.invokeLater(() -> this.showDesktopSetup(evt));
      }
   }

   @EventHandler
   public void busEvent(BusExceptionEvent event) {
      Logger.getLogger("stslogger").log(Level.SEVERE, null, event.getCause());
   }

   @EventHandler
   public void relaunchModule(SpoolLaunchEvent event) {
      SwingUtilities.invokeLater(() -> new RelaunchModuleQuery(this, event.event).setVisible(true));
   }

   @EventHandler
   public void switchDataValueChange(SwitchValueEvent event) {
      new SwitchValueManipulator(this.dataSwitch).set(event.name, event.enabled);
   }

   @EventHandler
   public void dumpSwitchDataValueChange(DumpSwitchValueEvent event) {
      new SwitchValueManipulator(this.dataSwitch).dump();
   }

   @Override
   public String getToken() {
      return this.token;
   }

   @Override
   public String getUid() {
      return this.uid;
   }

   @Override
   public String getUsername() {
      return this.username;
   }

   @Override
   public String getIrcServer() {
      return this.getParameter(UserContextMini.DATATYPE.IRCSERVER);
   }

   @Override
   public String getControlBot() {
      return this.getParameter(UserContextMini.DATATYPE.CONTROLBOT);
   }

   @Override
   public String getReadyRoom() {
      return this.getParameter(UserContextMini.DATATYPE.READYROOM);
   }

   @Override
   public Image getWindowIcon() {
      return this.icon;
   }

   @Override
   public final String getParameter(UserContextMini.DATATYPE d) {
      return System.getProperty(d.propertyName, d.defaultValue);
   }

   @Override
   public void showTrayMessage(String msg) {
      if (this.onMac) {
         this.showTopLevelMessage(msg, 10);
      } else if (this.trayIcon != null) {
         this.trayIcon.displayMessage(msg, null, MessageType.INFO);
      }
   }

   @Override
   public void showTrayMessage(String msg, String line2) {
      if (this.onMac) {
         this.showTopLevelMessage(msg, 10);
      } else if (this.trayIcon != null) {
         this.trayIcon.displayMessage(msg, line2, MessageType.INFO);
      }
   }

   @Override
   public void showTrayMessage(String msg, String line2, MessageType type) {
      if (this.trayIcon != null) {
         this.trayIcon.displayMessage(msg, line2, type);
      }
   }

   @Override
   public void forceExit() {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> this.forceExit());
      } else {
         try {
            if (this.chatWindow != null) {
               this.chatWindow.dispose();
            }

            if (this.mychat != null) {
               this.mychat.setRoomState(this, StartRoomState.EXIT);
            }

            if (SystemTray.isSupported() && this.trayIcon != null) {
               SystemTray tray = SystemTray.getSystemTray();
               tray.remove(this.trayIcon);
            }
         } finally {
            SwingUtilities.invokeLater(() -> System.exit(0));
         }
      }
   }

   @Override
   public void exit() {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> this.exit());
      } else {
         if (this.madapter.isModuleRunning()) {
            int j = JOptionPane.showConfirmDialog(
               this.chatWindow,
               "<html><b>Es läuft noch ein Modul!</b><br>Durch beenden wird dieses abgebrochen,<br>ungesicherte Daten gehen verloren!</html>",
               "Es läuft noch ein Modul",
               2,
               2
            );
            if (j == 2) {
               return;
            }
         }

         this.forceExit();
      }
   }

   @Override
   public void showAbout() {
      aboutDialog d = new aboutDialog(
         null,
         true,
         "Über",
         "StellwerkSim",
         "5.2 predicted web edition / Build " + this.getBuild(),
         "Jürgen Schmitz & StellwerkSim Betriebsverein e.V.",
         "Jürgen Schmitz",
         "js@js-home.org",
         "http://www.js-home.org",
         "Dieses Programm und Teile daraus darf ausschließlich auf stellwerksim.de, js-home.org und Partnerseiten genutzt werden. Die Rechte der Software liegen beim Autor. Nutzung in nicht vorgesehener Form ist untersagt."
      );
      d.setIconImage(this.getWindowIcon());
      d.show(450, 400);
   }

   @Override
   public ChatNG getChat() {
      return this.mychat;
   }

   @Override
   public void showTopLevelMessage(String msg, int seconds) {
      if (SwingUtilities.isEventDispatchThread()) {
         if (this.lastMessage != null) {
            TopLevelMessage lm = (TopLevelMessage)this.lastMessage.get();
            if (lm != null) {
               lm.setState(States.MOVEOUT);
            }

            this.lastMessage.clear();
            this.lastMessage = null;
         }

         if (msg != null && !msg.isEmpty()) {
            TopLevelMessage lm = new TopLevelMessage(msg, seconds);
            lm.start();
            this.lastMessage = new WeakReference(lm);
         }
      } else {
         SwingUtilities.invokeLater(() -> this.showTopLevelMessage(msg, seconds));
      }
   }

   @Override
   public void closeTopLevelMessage() {
      if (SwingUtilities.isEventDispatchThread()) {
         if (this.lastMessage != null) {
            TopLevelMessage lm = (TopLevelMessage)this.lastMessage.get();
            if (lm != null) {
               lm.stop();
            }

            this.lastMessage.clear();
            this.lastMessage = null;
         }
      } else {
         SwingUtilities.invokeLater(() -> this.closeTopLevelMessage());
      }
   }

   @Override
   public void moduleClosed() {
      this.chatWindow.setVisible(true);
   }

   @Override
   public AudioSettings getAudioSettings() {
      return new AudioSettingsImpl(this);
   }

   @Override
   public void showException(Exception ex) {
      if (SwingUtilities.isEventDispatchThread()) {
         new ExceptionDialog().show(this.chatWindow, "Programmproblem!", ex);
      } else {
         SwingUtilities.invokeLater(() -> this.showException(ex));
      }
   }

   public void registerPermanentMBean(Object clazz, String name) {
      try {
         ObjectName oname = new ObjectName("de.deltaga.stellwerksim:name=" + name);
         this.mbs.registerMBean(clazz, oname);
      } catch (Exception var4) {
         Logger.getLogger("stslogger").log(Level.SEVERE, null, var4);
      }
   }

   @Override
   public DataSwitch getDataSwitch() {
      return this.dataSwitch;
   }

   private static class MyAuthenticator extends Authenticator {
      final String kuser;
      final String kpass;

      MyAuthenticator(String user, String pass) {
         super();
         this.kuser = user;
         this.kpass = pass;
      }

      public PasswordAuthentication getPasswordAuthentication() {
         System.err.println("Feeding username and password for " + this.getRequestingScheme());
         return new PasswordAuthentication(this.kuser, this.kpass.toCharArray());
      }
   }
}
