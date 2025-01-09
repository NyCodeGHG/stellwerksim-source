package js.java.schaltungen.adapter;

import de.deltaga.eb.DelayEvent;
import de.deltaga.eb.EventBus;
import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.Image;
import java.awt.TrayIcon.MessageType;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.swing.SwingUtilities;
import js.java.classloader.ClassLoaderFactory;
import js.java.classloader.IsolateClassLoader;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.audio.AudioController;
import js.java.schaltungen.audio.AudioControllerImpl;
import js.java.schaltungen.audio.AudioSettings;
import js.java.schaltungen.audio.AudioSettingsImpl;
import js.java.schaltungen.chatcomng.ChatNG;
import js.java.schaltungen.moduleapi.ModuleObject;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.moduleapi.SessionExit;
import js.java.schaltungen.switchbase.DataSwitch;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

public class UCProxy implements UserContext, xmllistener, UCProxyMBean {
   private final UserContextMini root;
   private final LinkedList<SessionExit> moduleCloseOverride = new LinkedList();
   private final MBeanServer mbs;
   private Module runningModul = null;
   private ModuleObject runningModulObject = null;
   private final ConcurrentHashMap<String, String> parameters = new ConcurrentHashMap();
   private final LinkedList<SessionClose> closeObjects = new LinkedList();
   private AudioController audio = null;
   private final EventBus bus;
   public final long launchTime;
   private final boolean noLoaderClose;

   UCProxy(UserContextMini root, boolean noLoaderClose) {
      this.root = root;
      this.noLoaderClose = noLoaderClose;
      this.launchTime = System.currentTimeMillis();
      this.mbs = ManagementFactory.getPlatformMBeanServer();
      this.bus = EventBusService.getInstance();
   }

   public void launch(LaunchModule event) {
      this.showTopLevelMessage("Modul " + event.modul.title + " wird gestartet.", 0);
      Logger.getLogger(MessageAdapter.class.getName()).log(Level.INFO, "Modulstart {0} (Step 1)-----------------------", event.modul.title);
      String parameterUrl = event.parameterUrl;
      if (event.parameterUrl.equals("-1")) {
         parameterUrl = event.modul.testfile;
      }

      this.parameters.clear();
      xmlreader reader = new xmlreader();
      reader.registerTag("param", this);

      try {
         reader.updateData(parameterUrl);
         this.launch(event.modul);
      } catch (Exception var5) {
         Logger.getLogger(MessageAdapter.class.getName()).log(Level.SEVERE, null, var5);
         this.showTopLevelMessage("Beim Modulstart ist ein Fehler aufgetreten: " + var5.getMessage(), 10);
         this.showException(var5);
      }
   }

   public void parseStartTag(String tag, Attributes attrs) {
      this.parameters.put(attrs.getValue("name"), attrs.getValue("value"));
   }

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
   }

   private void launch(Module module) {
      this.bus.publish(new StartingModule(module));
      SwingUtilities.invokeLater(() -> this.launchAwt(module));
   }

   private void launchAwt(Module module) {
      Logger.getLogger(MessageAdapter.class.getName()).log(Level.INFO, "Modulstart {0} (Step 2)-----------------------", module.title);
      IsolateClassLoader classLoader = new ClassLoaderFactory(module.title).getClassLoader();
      if (!this.noLoaderClose) {
         this.addCloseObject(classLoader);
      } else {
         Logger.getLogger(UCProxy.class.getName()).log(Level.INFO, "noLoaderClose was set");
      }

      try {
         Class<? extends ModuleObject> c = classLoader.loadClass(module.launch);
         Constructor<? extends ModuleObject> constr = c.getDeclaredConstructor(UserContext.class);
         if (constr != null) {
            this.audio = new AudioControllerImpl(this);
            this.runningModul = module;
            this.busSubscribe(this);
            this.registerMBean(this, "UCProxy");
            this.closeTopLevelMessage();
            ModuleObject inst = (ModuleObject)constr.newInstance(this);
            this.runningModulObject = inst;
            if (inst instanceof SessionClose) {
               this.addCloseObject((SessionClose)inst);
            } else {
               Logger.getLogger(UCProxy.class.getName()).log(Level.INFO, "Not SC: {0}", inst.getClass().getName());
            }
         } else {
            this.showTopLevelMessage("Beim Modulstart ist ein Fehler aufgetreten: Konstruktor fehlt", 10);
            this.bus.publish(new EndModule(module, this, true));
         }
      } catch (Exception var6) {
         Logger.getLogger(UCProxy.class.getName()).log(Level.SEVERE, null, var6);
         this.showTopLevelMessage("Beim Modulstart ist ein Fehler aufgetreten: " + var6.getMessage(), 10);
         this.bus.publish(new ModuleCloseEvent(this, true));
         this.showException(var6);
      }
   }

   @EventHandler
   public void moduleClose(ModuleCloseEvent event) {
      if (event.uc == this) {
         this.showTopLevelMessage("Modul " + this.runningModul.title + " wurde beendet.", 4);
         Logger.getLogger(MessageAdapter.class.getName()).log(Level.INFO, "Modulende {0} -----------------------", this.runningModul.title);
         synchronized (this.closeObjects) {
            this.runningModulObject = null;
            Iterator<SessionClose> it = this.closeObjects.descendingIterator();

            while (it.hasNext()) {
               SessionClose so = (SessionClose)it.next();

               try {
                  so.close();
               } catch (Exception var7) {
                  Logger.getLogger(MessageAdapter.class.getName()).log(Level.SEVERE, null, var7);
               }
            }

            this.closeObjects.clear();
            this.audio = null;
            this.bus.publish(new EndModule(this.runningModul, this, event.withError));
         }

         System.gc();
      }
   }

   @Override
   public String getToken() {
      return this.root.getToken();
   }

   @Override
   public String getUid() {
      return this.root.getUid();
   }

   @Override
   public String getUsername() {
      return this.root.getUsername();
   }

   @Override
   public String getIrcServer() {
      return this.root.getIrcServer();
   }

   @Override
   public String getControlBot() {
      return this.root.getControlBot();
   }

   @Override
   public String getReadyRoom() {
      return this.root.getReadyRoom();
   }

   @Override
   public Image getWindowIcon() {
      return this.root.getWindowIcon();
   }

   @Override
   public String getParameter(UserContextMini.DATATYPE d) {
      return this.root.getParameter(d);
   }

   @Override
   public String getParameter(String name) {
      return (String)this.parameters.get(name);
   }

   @Override
   public void showTrayMessage(String msg) {
      this.root.showTrayMessage(msg);
   }

   @Override
   public void showTrayMessage(String msg, String line2) {
      this.root.showTrayMessage(msg, line2);
   }

   @Override
   public void showTrayMessage(String msg, String line2, MessageType type) {
      this.root.showTrayMessage(msg, line2, type);
   }

   @Override
   public void overrideModuleClose(SessionExit se) {
      this.moduleCloseOverride.add(se);
   }

   @Override
   public void exit() {
      this.root.exit();
   }

   @Override
   public void forceExit() {
      this.root.forceExit();
   }

   @Override
   public void showAbout() {
      this.root.showAbout();
   }

   @Override
   public int getBuild() {
      return this.root.getBuild();
   }

   @Override
   public ChatNG getChat() {
      return this.root.getChat();
   }

   @Override
   public void showTopLevelMessage(String msg, int seconds) {
      this.root.showTopLevelMessage(msg, seconds);
   }

   @Override
   public void closeTopLevelMessage() {
      this.root.closeTopLevelMessage();
   }

   @Override
   public void moduleClosed() {
      SessionExit se = (SessionExit)this.moduleCloseOverride.pollLast();
      if (se != null) {
         se.exit();
      } else {
         this.root.moduleClosed();
         this.bus.publish(new DelayEvent(new ModuleCloseEvent(this, false), 1));
      }
   }

   @Override
   public void addCloseObject(SessionClose obj) {
      synchronized (this.closeObjects) {
         this.closeObjects.add(obj);
      }
   }

   @Override
   public AudioController getAudio() {
      return this.audio;
   }

   @Override
   public AudioSettings getAudioSettings() {
      return new AudioSettingsImpl(this);
   }

   @Override
   public void showException(Exception ex) {
      this.root.showException(ex);
   }

   @Override
   public void busSubscribe(Object subscriber) {
      this.addCloseObject(() -> this.bus.unsubscribe(subscriber));
      this.bus.subscribe(subscriber);
   }

   @Override
   public void busUnsubscribe(Object subscriber) {
      this.bus.unsubscribe(subscriber);
   }

   @Override
   public void busPublish(Object event) {
      this.bus.publish(event);
   }

   @Override
   public void registerMBean(Object clazz, String name) {
      try {
         Hashtable<String, String> keys = new Hashtable();
         keys.put("type", this.runningModul.name());
         keys.put("name", name);
         keys.put("instance", "" + System.currentTimeMillis() / 1000L % 1000L);
         final ObjectName oname = new ObjectName("de.deltaga.stellwerksim.module", keys);
         this.addCloseObject(new SessionClose() {
            @Override
            public void close() {
               try {
                  UCProxy.this.mbs.unregisterMBean(oname);
               } catch (MBeanRegistrationException | InstanceNotFoundException var2) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, null, var2);
               }
            }
         });
         this.mbs.registerMBean(clazz, oname);
      } catch (Exception var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, null, var5);
      }
   }

   @Override
   public boolean isModuleRunning() {
      return this.runningModul != null;
   }

   @Override
   public String getRunningModuleName() {
      return this.runningModul != null ? this.runningModul.title : "-";
   }

   @Override
   public int getRegisteredClosersCount() {
      return this.closeObjects.size();
   }

   @Override
   public Map<String, String> getParameters() {
      return this.parameters;
   }

   @Override
   public void finishModule(String token) {
      if ("terminate".equals(token)) {
         this.moduleClose(null);
      }
   }

   @Override
   public boolean isNoLoaderClose() {
      return this.noLoaderClose;
   }

   @Override
   public DataSwitch getDataSwitch() {
      return this.root.getDataSwitch();
   }
}
