package js.java.isolate.sim.sim.plugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketPermission;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasseSelection;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.isolate.sim.structServ.structinfo;
import org.xml.sax.Attributes;

public class pluginServ extends ServImpl {
   private final element[] elmsSearch = new element[]{
      gleisElements.ELEMENT_SIGNAL,
      gleisElements.ELEMENT_WEICHEOBEN,
      gleisElements.ELEMENT_WEICHEUNTEN,
      gleisElements.ELEMENT_ZWERGSIGNAL,
      gleisElements.ELEMENT_EINFAHRT,
      gleisElements.ELEMENT_AUSFAHRT
   };
   private static final int PLUGIN_XML_PROTOKOLLVERSION_OLD = 1;
   private static final int PLUGIN_XML_PROTOKOLLVERSION = 2;
   private static final int PLUGIN_JSON_PROTOKOLLVERSION = 100;
   private ConcurrentHashMap<BufferedWriter, pluginServ.pluginData> pluginList = new ConcurrentHashMap();
   protected final pluginDataAdapter my_main;
   private final pluginServ.pluginData nullPD = new pluginServ.pluginData();
   private DatagramSocket bcastSocket = null;
   private Thread bcastThread = null;

   public static boolean mayCreateInstance(stellwerksim_main m) {
      try {
         SocketPermission p = new SocketPermission("localhost:3691", "accept,listen,resolve");
         AccessController.checkPermission(p);
         return true;
      } catch (AccessControlException var2) {
         return false;
      }
   }

   public pluginServ(pluginDataAdapter m) {
      super(3691);
      this.my_main = m;
      this.initInterface();
      System.out.println("Pluginverbindung bereit");
      this.my_main.message("Pluginverbindung bereit");

      try {
         this.bcastSocket = new DatagramSocket(3691);
         this.bcastSocket.setBroadcast(true);
         this.bcastThread = new Thread(new pluginServ.bcastRunner());
         this.bcastThread.setName("plugin-castThread");
         this.bcastThread.start();
      } catch (Exception var3) {
         this.bcastSocket = null;
      }
   }

   public pluginServ(pluginDataAdapter m, ServImpl.OutputWriter client) {
      super();
      this.my_main = m;
      this.initInterface();
      this.attachClient(client);
   }

   @Override
   public void close() {
      super.close();

      try {
         this.bcastSocket.close();
      } catch (Exception var2) {
      }

      this.bcastSocket = null;
      this.bcastThread.interrupt();
   }

   private void initInterface() {
      this.xmlr.registerTag("bahnsteigliste", this);
      this.xmlr.registerTag("zugliste", this);
      this.xmlr.registerTag("zugdetails", this);
      this.xmlr.registerTag("zugfahrplan", this);
      this.xmlr.registerTag("register", this);
      this.xmlr.registerTag("debug", this);
      this.xmlr.registerTag("anlageninfo", this);
      this.xmlr.registerTag("simzeit", this);
      this.xmlr.registerTag("ereignis", this);
      this.xmlr.registerTag("eventOccure", this);
      this.xmlr.registerTag("zugplan", this);
      this.xmlr.registerTag("struct", this);
      this.xmlr.registerTag("stitz", this);
      this.xmlr.registerTag("stoerungen", this);
      this.xmlr.registerTag("hitze", this);
      this.xmlr.registerTag("irc", this);
      this.xmlr.registerTag("wege", this);
      this.xmlr.registerTag("element2enr", this);
      this.xmlr.registerTag("enr2element", this);
      this.xmlr.registerTag("setfs", this);
   }

   private void debug(pluginServ.pluginData pd, String text) {
      if (pd.debug) {
         System.out.println("Plugin[" + pd.name + "]: " + text);
      }
   }

   private void debug(pluginServ.pluginData pd, HashMap<String, String> h) {
      if (pd.debug) {
         for(Entry<String, String> e : h.entrySet()) {
            System.out.println("Plugin[" + pd.name + "]: " + (String)e.getKey() + "=" + (String)e.getValue());
         }
      }
   }

   private void sendStatus(pluginServ.pluginData pd, BufferedWriter output, int code, String text) {
      HashMap<String, String> h = new HashMap();
      h.put("code", Integer.toString(code));

      try {
         pd.sender.sendPcData(output, "status", h, text);
         pd.sender.sendEOR(output);
      } catch (IOException var7) {
      }
   }

   public void attachClient(ServImpl.OutputWriter output) {
      this.connected(output);
   }

   public void injectCommand(String cmd, ServImpl.OutputWriter output) {
      if (cmd != null && !cmd.isEmpty()) {
         try {
            this.runCommand(cmd, output);
         } catch (InterruptedException var4) {
         }
      }
   }

   @Override
   protected void addNewClient(Socket client) {
      super.addNewClient(client);
      System.out.println("Pluginverbindung hergestellt mit " + client.getInetAddress().getHostAddress());
   }

   @Override
   protected void connected(ServImpl.OutputWriter output) {
      this.sendStatus(this.nullPD, output, 300, "STS Plugin Interface, bitte anmelden.");
      this.pluginList.put(output, new pluginServ.pluginData());
   }

   @Override
   protected void finish(ServImpl.OutputWriter output) {
      pluginServ.pluginData pd = (pluginServ.pluginData)this.pluginList.remove(output);
      if (pd != null) {
         for(pluginDataAdapter.pluginEventHandle peh : pd.registeredEvents) {
            peh.close();
         }
      }

      if (pd != null && !pd.irc) {
         System.out.println("Pluginverbindung beendet");
         this.my_main.message("Pluginverbindung beendet");
      }
   }

   protected void registered(BufferedWriter output, pluginServ.pluginData pd) {
      if (!pd.irc) {
         this.my_main.message("Plugin verbunden: " + pd.name);
      }

      Logger.getLogger("stslogger").log(Level.INFO, "Plugin verbunden: " + pd.name + " von " + pd.autor);
   }

   @Override
   protected void xmlError(Exception e) {
      this.sendStatus(this.nullPD, this.currentBuffer, 450, "XML Fehler.");
   }

   @Override
   public void parseStartTag(String tag, Attributes attrs) {
      try {
         pluginServ.pluginData pd = (pluginServ.pluginData)this.pluginList.get(this.currentBuffer);
         if (pd == null) {
            this.sendStatus(this.nullPD, this.currentBuffer, 500, "Unbekannter Client.");
            return;
         }

         if (!pd.registered) {
            if (tag.equals("register")) {
               this.register(pd, this.currentBuffer, attrs);
            }
         } else {
            this.debug(pd, "Tag:" + tag + ":");
            if (tag.equals("debug")) {
               this.debug(pd, this.currentBuffer, attrs);
            } else if (tag.equals("anlageninfo")) {
               this.anlageninfo(pd, this.currentBuffer, attrs);
            } else if (tag.equals("bahnsteigliste")) {
               this.bahnsteigliste(pd, this.currentBuffer, attrs);
            } else if (tag.equals("zugliste")) {
               this.zugliste(pd, this.currentBuffer, attrs);
            } else if (tag.equals("zugdetails")) {
               this.zugdetails(pd, this.currentBuffer, attrs);
            } else if (tag.equals("zugfahrplan")) {
               this.zugfahrplan(pd, this.currentBuffer, attrs);
            } else if (tag.equals("ereignis")) {
               this.ereignis(pd, this.currentBuffer, attrs);
            } else if (tag.equals("eventOccure")) {
               this.eventOccure(pd, this.currentBuffer, attrs);
            } else if (tag.equals("simzeit")) {
               this.simzeit(pd, this.currentBuffer, attrs);
            } else if (tag.equals("hitze")) {
               this.heat(pd, this.currentBuffer, attrs);
            } else if (pd.irc && tag.equals("irc")) {
               this.ircInject(pd, this.currentBuffer, attrs);
            } else if (tag.equals("stoerungen")) {
               this.stoerungen(pd, this.currentBuffer, attrs);
            } else if (tag.equals("stitz")) {
               this.stitz(pd, this.currentBuffer, attrs);
            } else if (tag.equals("zugplan")) {
               this.zugplan(pd, this.currentBuffer, attrs);
            } else if (pd.irc && tag.equals("struct")) {
               this.struct(pd, this.currentBuffer, attrs);
            } else if (tag.equals("wege")) {
               this.wege(pd, this.currentBuffer, attrs);
            } else if (tag.equals("element2enr")) {
               this.element2enr(pd, this.currentBuffer, Integer.parseInt(attrs.getValue("element")));
            } else if (tag.equals("enr2element")) {
               this.enr2element(pd, this.currentBuffer, Integer.parseInt(attrs.getValue("enr")));
            } else if (tag.equals("setfs")) {
               this.setfs(pd, this.currentBuffer, Integer.parseInt(attrs.getValue("start")), Integer.parseInt(attrs.getValue("stop")));
            }
         }
      } catch (IOException var4) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "XML: " + tag, var4);
      }
   }

   private void register(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) {
      try {
         pd.name = attrs.getValue("name");
         pd.autor = attrs.getValue("autor");
         pd.version = attrs.getValue("version");
         pd.protokoll = Integer.parseInt(attrs.getValue("protokoll"));
         pd.text = attrs.getValue("text");
         pd.json = false;
         String key = attrs.getValue("ebene");
         if (key != null) {
            if (key.equals("tablet")) {
               pd.tablet = true;
            } else if (key.equals("fullAdminControlPanel")) {
               pd.tablet = true;
               pd.irc = true;
            }
         }

         if (pd.name == null || pd.autor == null || pd.version == null || pd.protokoll <= 0 || pd.text == null) {
            this.sendStatus(pd, output, 400, "Registrierungsfehler.");
         } else if (pd.protokoll == 2) {
            pd.sender = new xmlResponse(false);
            this.sendStatus(pd, output, 220, "Ok.");
            pd.registered = true;
            this.registered(output, pd);
         } else if (pd.protokoll == 1) {
            pd.sender = new xmlResponse(true);
            this.sendStatus(pd, output, 220, "Ok.");
            pd.registered = true;
            this.registered(output, pd);
         } else if (pd.protokoll == 100) {
            pd.json = true;
            pd.sender = new jsonResponse();
            this.sendStatus(pd, output, 220, "Ok.");
            pd.registered = true;
            this.registered(output, pd);
         } else {
            this.sendStatus(pd, output, 500, "Protokollversionsfehler.");
         }
      } catch (NumberFormatException var5) {
         this.sendStatus(pd, output, 500, "Protokollversionsfehler.");
      }
   }

   private void debug(pluginServ.pluginData pd, ServImpl.OutputWriter output, Attributes attrs) {
      pd.debug = attrs.getValue("mode").equalsIgnoreCase("true");
      this.debug(pd, "Debug: " + pd.debug);
      this.sendStatus(pd, output, 210, "Debug: " + Boolean.toString(pd.debug));
      output.setDebug(pd.debug);
   }

   private void anlageninfo(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      HashMap<String, String> h = new HashMap();
      h.put("aid", Integer.toString(this.my_main.getAid()));
      h.put("name", this.my_main.getAnlagenname());
      h.put("region", this.my_main.getRegion());
      h.put("simbuild", Integer.toString(this.my_main.getBuild()));
      h.put("online", Boolean.toString(this.my_main.isOnline()));
      if (pd.tablet) {
         h.put("rid", Integer.toString(this.my_main.getRid()));
      }

      pd.sender.sendLine(output, "anlageninfo", h);
      pd.sender.sendEOR(output);
   }

   private void bahnsteigliste(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      pd.sender.sendOpeningLine(output, "bahnsteigliste");

      for(String bst : this.my_main.getAlleBahnsteige()) {
         boolean isHp = this.my_main.bahnsteigIsHaltepunkt(bst);
         pd.sender.sendOpeningLine(output, "bahnsteig", "name", bst, "haltepunkt", Boolean.toString(isHp));
         Iterator<String> it = this.my_main.findNeighborBahnsteig(bst);

         while(it.hasNext()) {
            String n = (String)it.next();
            if (!n.equals(bst)) {
               pd.sender.sendLine(output, "n", "name", n);
            }
         }

         pd.sender.sendClosingLine(output, "bahnsteig");
      }

      pd.sender.sendClosingLine(output, "bahnsteigliste");
      pd.sender.sendEOR(output);
   }

   private void zugliste(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      pd.sender.sendOpeningLine(output, "zugliste");
      Map<Integer, String> zl = this.my_main.getZugList();

      for(Entry<Integer, String> z : zl.entrySet()) {
         pd.sender.sendLine(output, "zug", "zid", z.getKey() + "", "name", (String)z.getValue());
      }

      pd.sender.sendClosingLine(output, "zugliste");
      pd.sender.sendEOR(output);
   }

   private void zugdetails(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      try {
         int zid = Integer.parseInt(attrs.getValue("zid"));
         this.debug(pd, "zugdetails ZID " + zid);
         pluginDataAdapter.zugDetails z = this.my_main.getZugDetails(zid);
         if (z != null) {
            HashMap<String, String> h = new HashMap();
            h.put("zid", Integer.toString(z.zid));
            h.put("name", z.name);
            h.put("verspaetung", Integer.toString(z.verspaetung));
            if (z.gleis != null) {
               h.put("gleis", z.gleis);
            }

            if (z.plangleis != null) {
               h.put("plangleis", z.plangleis);
            }

            h.put("amgleis", Boolean.toString(z.amgleis));
            h.put("von", z.von);
            h.put("nach", z.nach);
            h.put("sichtbar", Boolean.toString(z.sichtbar));
            h.put("usertext", z.usertext);
            h.put("usertextsender", z.usertextsender);
            pd.sender.sendLine(output, "zugdetails", h);
            pd.sender.sendEOR(output);
         } else {
            this.debug(pd, "ZID " + attrs.getValue("zid") + " unbekannt");
            this.sendStatus(pd, output, 402, "ZID " + attrs.getValue("zid") + " unbekannt");
         }
      } catch (NumberFormatException var7) {
         this.debug(pd, "ZID " + attrs.getValue("zid") + " fehlerhaft");
         this.sendStatus(pd, output, 401, "ZID " + attrs.getValue("zid") + " fehlerhaft");
      }
   }

   private void zugfahrplan(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      try {
         int zid = Integer.parseInt(attrs.getValue("zid"));
         this.debug(pd, "zugfahrplan ZID " + zid);
         List<pluginDataAdapter.zugPlanLine> zl = this.my_main.getAllUnseenFahrplanzeilen(zid);
         if (zl != null) {
            pd.sender.sendOpeningLine(output, "zugfahrplan", "zid", zid + "");

            for(pluginDataAdapter.zugPlanLine zz : zl) {
               HashMap<String, String> h = new HashMap();
               h.put("plan", zz.plan);
               h.put("name", zz.name);
               h.put("an", zz.an);
               h.put("ab", zz.ab);
               h.put("flags", zz.flags);
               h.put("hinweistext", zz.hinweistext);
               pd.sender.sendLine(output, "gleis", h);
            }

            pd.sender.sendClosingLine(output, "zugfahrplan");
            pd.sender.sendEOR(output);
         } else {
            this.debug(pd, "ZID " + attrs.getValue("zid") + " unbekannt");
            this.sendStatus(pd, output, 402, "ZID " + attrs.getValue("zid") + " unbekannt");
         }
      } catch (NumberFormatException var9) {
         this.debug(pd, "ZID " + attrs.getValue("zid") + " fehlerhaft");
         this.sendStatus(pd, output, 401, "ZID " + attrs.getValue("zid") + " fehlerhaft");
      }
   }

   private void simzeit(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      pd.sender.sendLine(output, "simzeit", "sender", attrs.getValue("sender"), "zeit", this.my_main.getSimutime() + "");
      pd.sender.sendEOR(output);
   }

   private void heat(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      pd.sender.sendLine(output, "hitze", "hitze", Long.toString(this.my_main.getHeat()));
      pd.sender.sendEOR(output);
   }

   private void ereignis(pluginServ.pluginData pd, ServImpl.OutputWriter output, Attributes attrs) {
      try {
         int zid = Integer.parseInt(attrs.getValue("zid"));
         String kind = attrs.getValue("art");
         this.debug(pd, "ereignis ZID " + zid + ": " + kind);
         pluginDataAdapter.EVENTKINDS k = pluginDataAdapter.EVENTKINDS.valueOf(kind.toUpperCase());
         pluginDataAdapter.pluginEventHandle h = this.my_main.registerEvent(zid, k, new pluginServ.eventCallback(pd, output));
         if (h != null) {
            pd.registeredEvents.add(h);
            this.debug(pd, "ereignis ZID " + zid + ": " + kind + " ok");
         } else {
            this.debug(pd, "ZID " + attrs.getValue("zid") + " unbekannt");
            this.sendStatus(pd, output, 402, "ZID " + attrs.getValue("zid") + " unbekannt");
         }
      } catch (NumberFormatException var8) {
         this.debug(pd, "ZID " + attrs.getValue("zid") + " fehlerhaft");
         this.sendStatus(pd, output, 401, "ZID " + attrs.getValue("zid") + " fehlerhaft");
      } catch (IllegalArgumentException var9) {
         this.debug(pd, "Art " + attrs.getValue("art") + " fehlerhaft");
         this.sendStatus(pd, output, 403, "Art " + attrs.getValue("art") + " fehlerhaft");
      }
   }

   private void eventOccure(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      pluginServ.pluginData.eventOccureData eod;
      while((eod = (pluginServ.pluginData.eventOccureData)pd.eventQueue.poll()) != null) {
         pluginDataAdapter.zugDetails z = this.my_main.getZugDetails(eod.zid);
         if (z != null) {
            HashMap<String, String> h = new HashMap();
            h.put("art", eod.kind.name().toLowerCase());
            h.put("zid", Integer.toString(z.zid));
            h.put("name", z.name);
            h.put("verspaetung", Integer.toString(z.verspaetung));
            if (z.gleis != null) {
               h.put("gleis", z.gleis);
            }

            if (z.plangleis != null) {
               h.put("plangleis", z.plangleis);
            }

            h.put("amgleis", Boolean.toString(z.amgleis));
            h.put("von", z.von);
            h.put("nach", z.nach);
            h.put("sichtbar", Boolean.toString(z.sichtbar));
            pd.sender.sendLine(output, "ereignis", h);
            pd.sender.sendEOR(output);
            this.debug(pd, "Ereignis " + eod.kind.name().toLowerCase() + " für " + z.zid);
         }
      }
   }

   private void ircInject(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) {
      if (attrs.getValue("data") != null) {
         this.my_main.ircInject(attrs.getValue("data"));
      }
   }

   private void struct(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      String qid = attrs.getValue("id");
      if (qid == null) {
         pd.sender.sendOpeningLine(output, "struct");
         pd.idhash.clear();
         Vector v = this.my_main.getStructInfo();

         for(int i = 0; i < v.size(); ++i) {
            Vector vv = (Vector)v.get(i);
            String type = (String)vv.get(0);
            String name = (String)vv.get(1);
            structinfo obj = (structinfo)vv.get(2);
            int id = pd.idhash.size() + 1;
            pd.idhash.put(id, obj);
            pd.sender.sendLine(output, "structitem", "type", type, "name", name, "id", id + "");
         }

         pd.sender.sendClosingLine(output, "struct");
         pd.sender.sendEOR(output);
      } else {
         pd.sender.sendOpeningLine(output, "struct", "id", qid + "");
         int id = Integer.parseInt(qid);
         structinfo obj = (structinfo)pd.idhash.get(id);
         if (obj != null) {
            Vector v = obj.getStructure();
            int l = v.size() / 2;
            int c = 0;

            for(int i = 0; i < v.size(); i += 2) {
               String key = "";
               String value = "";

               try {
                  key = (String)v.get(i);
               } catch (Exception var15) {
                  key = var15.getMessage();
               }

               try {
                  value = v.get(i + 1).toString();
               } catch (Exception var14) {
                  value = var14.getMessage();
               }

               pd.sender.sendLine(output, "structentry", "line", c + "", "totallines", l + "", "key", key, "value", value);
               ++c;
            }
         }

         pd.sender.sendClosingLine(output, "struct");
         pd.sender.sendEOR(output);
      }
   }

   private void zugplan(pluginServ.pluginData pd, ServImpl.OutputWriter output, Attributes attrs) throws IOException {
      if (pd.tablet) {
         try {
            int zid = Integer.parseInt(attrs.getValue("zid"));
            this.debug(pd, "zugplan ZID " + zid);
            String z = this.my_main.getZugFahrplanHTML(zid);
            if (z != null) {
               z = z.replace('\n', ' ');
               HashMap<String, String> tags = new HashMap();
               tags.put("zid", zid + "");
               pd.sender.sendPcData(output, "zugplan", tags, z);
               pd.sender.sendEOR(output);
            }
         } catch (NumberFormatException var7) {
            this.debug(pd, "ZID " + attrs.getValue("zid") + " fehlerhaft");
            this.sendStatus(pd, output, 401, "ZID " + attrs.getValue("zid") + " fehlerhaft");
         }
      }
   }

   private void stoerungen(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      if (pd.tablet) {
         pd.sender.sendOpeningLine(output, "stoerungen");

         for(pluginDataAdapter.pluginEventAdapter e : this.my_main.getEvents()) {
            String s = e.funkName();
            if (s != null) {
               String aw = e.funkAntwort();
               if (aw == null) {
                  aw = "";
               }

               HashMap<String, String> tags = new HashMap();
               tags.put("name", s);
               pd.sender.sendPcData(output, "stoerung", tags, aw);
            }
         }

         pd.sender.sendClosingLine(output, "stoerungen");
         pd.sender.sendEOR(output);
      }
   }

   private void stitz(pluginServ.pluginData pd, BufferedWriter output, Attributes attrs) throws IOException {
      String r = this.my_main.getRegionTel();
      if (r == null) {
         r = "";
      }

      String a = this.my_main.getAllgemeintel();
      if (a == null) {
         a = "";
      }

      pd.sender.sendLine(output, "stitz", "region", r, "allgemein", a);
      pd.sender.sendEOR(output);
   }

   private void wege(pluginServ.pluginData pd, ServImpl.OutputWriter output, Attributes attrs) throws IOException {
      List<pluginDataAdapter.WegElement> weg = this.my_main.getWege();
      pd.sender.sendOpeningLine(output, "wege");

      for(pluginDataAdapter.WegElement e : weg) {
         pd.sender.sendLine(output, e.xmlelement, e.attrs);
      }

      pd.sender.sendClosingLine(output, "wege");
      pd.sender.sendEOR(output);
   }

   private void element2enr(pluginServ.pluginData pd, BufferedWriter output, int e) {
      if (this.my_main.getGleisbild() == null) {
         try {
            pd.sender.sendLine(output, "enr4", "element", Integer.toString(e), "enr", Integer.toString(e + 1000));
            pd.sender.sendEOR(output);
         } catch (IOException var7) {
         }
      } else {
         int enr = 0;
         Iterator<gleis> it = this.my_main.getGleisbild().findIteratorWithElementName(Integer.toString(e), new Object[]{this.elmsSearch});
         if (it.hasNext()) {
            enr = ((gleis)it.next()).getENR();
         }

         try {
            pd.sender.sendLine(output, "enr4", "element", Integer.toString(e), "enr", Integer.toString(enr));
            pd.sender.sendEOR(output);
         } catch (IOException var8) {
         }
      }
   }

   private void enr2element(pluginServ.pluginData pd, BufferedWriter output, int e) {
      if (this.my_main.getGleisbild() == null) {
         try {
            pd.sender.sendLine(output, "element4", "element", "123", "enr", Integer.toString(e));
            pd.sender.sendEOR(output);
         } catch (IOException var7) {
         }
      } else {
         String element = "0";
         gleis v = this.my_main.getGleisbild().findFirst(new Object[]{e, this.elmsSearch});

         try {
            element = v.getShortElementName();
         } catch (NullPointerException var9) {
         }

         try {
            pd.sender.sendLine(output, "element4", "element", element, "enr", Integer.toString(e));
            pd.sender.sendEOR(output);
         } catch (IOException var8) {
         }
      }
   }

   private void setfs(pluginServ.pluginData pd, BufferedWriter output, int enr1, int enr2) {
      if (this.my_main.getGleisbild() == null) {
         pluginServ.fsSetResponseHook resp = new pluginServ.fsSetResponseHook(pd, output, enr1, enr2, ServBase.FAILREASON.SUCCESS);
         resp.call(null);
      } else {
         gleis start = this.my_main.getGleisbild().findFirst(new Object[]{enr1, this.elmsSearch});
         gleis stop = this.my_main.getGleisbild().findFirst(new Object[]{enr2, this.elmsSearch});

         try {
            fahrstrasseSelection fs = this.my_main.getGleisbild().findFahrweg(start, stop, false);
            fs.addHook(
               new pluginServ.fsSetResponseHook(pd, output, enr1, enr2, ServBase.FAILREASON.SUCCESS), EnumSet.of(fahrstrasseSelection.StateChangeTypes.GOT_FS)
            );
            fs.addHook(
               new pluginServ.fsSetResponseHook(pd, output, enr1, enr2, ServBase.FAILREASON.BUSY),
               EnumSet.of(fahrstrasseSelection.StateChangeTypes.CANTGET_FS, fahrstrasseSelection.StateChangeTypes.ERROR_GETFS)
            );
            if (this.my_main.getGleisbild().gleisbildextend.getSignalversion() > 0
               ? this.my_main.getFSallocator().getFS(fs, fsAllocs.ALLOCM_USER_GETORSTORE)
               : this.my_main.getFSallocator().getFS(fs, fsAllocs.ALLOCM_USER_GET)) {
            }
         } catch (NullPointerException var10) {
            try {
               pd.sender
                  .sendLine(output, "fsset", "start", Integer.toString(enr1), "stop", Integer.toString(enr2), "result", ServBase.FAILREASON.UNKNOWN.name());
               pd.sender.sendEOR(output);
            } catch (IOException var9) {
            }
         }
      }
   }

   private class bcastRunner implements Runnable {
      private bcastRunner() {
         super();
      }

      public void run() {
         byte[] recvBuf = new byte["STSBCASTCLIENT".length() + 2];

         try {
            pluginServ.this.bcastSocket.setReceiveBufferSize("STSBCASTCLIENT".length() + 2);
            byte[] magic = "STSBCASTCLIENT".getBytes("latin1");
            DatagramPacket data = new DatagramPacket(recvBuf, recvBuf.length);

            while(pluginServ.this.running) {
               try {
                  pluginServ.this.bcastSocket.receive(data);
                  if (data.getLength() >= magic.length) {
                     boolean found = true;
                     int i = 0;

                     while(true) {
                        if (i < magic.length) {
                           if (recvBuf[i] == magic[i]) {
                              ++i;
                              continue;
                           }

                           found = false;
                        }

                        if (found) {
                           this.sendResponse(data);
                        }
                        break;
                     }
                  }
               } catch (Exception var6) {
               }
            }
         } catch (Exception var7) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "bcastRunner 2", var7);
         }
      }

      private void sendResponse(DatagramPacket data) {
         try {
            String aw = "STS:" + pluginServ.this.my_main.getAid() + ":" + pluginServ.this.my_main.getAnlagenname();
            byte[] sendBuf = aw.getBytes("UTF-8");
            DatagramPacket answer = new DatagramPacket(sendBuf, sendBuf.length, data.getAddress(), data.getPort());
            pluginServ.this.bcastSocket.send(answer);
         } catch (Exception var5) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "sendResponse", var5);
         }
      }
   }

   protected class eventCallback implements pluginDataAdapter.pluginEventCallback {
      private final pluginServ.pluginData pd;
      private final ServImpl.OutputWriter output;

      eventCallback(pluginServ.pluginData pd, ServImpl.OutputWriter output) {
         super();
         this.pd = pd;
         this.output = output;
      }

      @Override
      public void eventOccured(int zid, pluginDataAdapter.EVENTKINDS kind) {
         this.pd.eventQueue.add(new pluginServ.pluginData.eventOccureData(zid, kind));
         pluginServ.this.injectCommand("<eventOccure />", this.output);
      }
   }

   private class fsSetResponseHook implements fahrstrasseSelection.ChangeHook {
      private final ServBase.FAILREASON success;
      private final int enr1;
      private final int enr2;
      private final BufferedWriter output;
      private final pluginServ.pluginData pd;

      fsSetResponseHook(pluginServ.pluginData pd, BufferedWriter output, int enr1, int enr2, ServBase.FAILREASON success) {
         super();
         this.success = success;
         this.pd = pd;
         this.enr1 = enr1;
         this.enr2 = enr2;
         this.output = output;
      }

      @Override
      public void call(fahrstrasseSelection s) {
         try {
            this.pd
               .sender
               .sendLine(this.output, "fsset", "start", Integer.toString(this.enr1), "stop", Integer.toString(this.enr2), "result", this.success.name());
         } catch (IOException var3) {
         }
      }
   }

   protected static class pluginData {
      public boolean registered = false;
      public String name = null;
      public String autor = null;
      public String version = null;
      public int protokoll = 0;
      public String text = null;
      public boolean debug = false;
      public boolean json = false;
      public responseSender sender = new xmlResponse(true);
      public boolean tablet = false;
      public boolean irc = false;
      public final HashMap<Integer, structinfo> idhash = new HashMap();
      public final LinkedList<pluginDataAdapter.pluginEventHandle> registeredEvents = new LinkedList();
      public final ConcurrentLinkedQueue<pluginServ.pluginData.eventOccureData> eventQueue = new ConcurrentLinkedQueue();

      protected pluginData() {
         super();
      }

      static class eventOccureData {
         final int zid;
         final pluginDataAdapter.EVENTKINDS kind;

         eventOccureData(int zid, pluginDataAdapter.EVENTKINDS kind) {
            super();
            this.zid = zid;
            this.kind = kind;
         }
      }
   }
}
