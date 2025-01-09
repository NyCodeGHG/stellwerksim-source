package js.java.isolate.statusapplet.karte;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import js.java.isolate.statusapplet.stellwerk_karte;
import js.java.isolate.statusapplet.common.szug_plan_base;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.chatcomng.OCCU_KIND;
import js.java.tools.TextHelper;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

public class kartePanel extends JComponent implements xmllistener, ActionListener {
   private static final int LISTWIDTH = 75;
   private static final int LISTHEIGHT = 70;
   private final Color bgcolor;
   private final Color linecol;
   private final Color üplinecol;
   private final Color markcol;
   private final int w = 0;
   private final int h = 0;
   private final int IMGFAKTOR = 25;
   private final int IMGFAKTOR_SMALL = 20;
   private String meldung = "";
   private final stellwerk_karte my_main;
   private String region;
   HashMap<Integer, karten_container> aids = null;
   HashMap<Integer, karten_container> namen = null;
   HashMap<Integer, karten_container> rnamen = null;
   HashMap<Integer, karten_container> kids = null;
   HashSet<connectColor> markVkids = null;
   HashMap<Integer, Color> markVcols = null;
   private JViewport myvp = null;
   private final connectColor currentZug = new connectColor(this, Color.CYAN);
   private JTextField selectionField = null;
   private final zugListPanel zuglist;
   private statisticsPanel stp;
   private boolean runnning = true;
   private Timer wtimer = null;
   private float scale = 0.0F;
   private CopyOnWriteArrayList<kartePanel.AnimDetails> anims = new CopyOnWriteArrayList();
   private long lastAnim = 0L;
   private final Timer animTimer = new Timer(100, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         if (!kartePanel.this.anims.isEmpty()) {
            kartePanel.this.repaint();
         } else {
            kartePanel.this.animTimer.stop();
         }
      }
   });

   public kartePanel(stellwerk_karte m, JScrollPane sp, zugListPanel zugList) {
      this.my_main = m;
      this.zuglist = zugList;
      this.meldung = "Initialer Datenempfang...";
      sp.setViewportView(this);
      this.myvp = sp.getViewport();
      this.setDoubleBuffered(true);
      this.scale = 25.0F;
      this.bgcolor = new Color(221, 221, 204);
      this.linecol = new Color(68, 68, 119);
      this.üplinecol = new Color(148, 102, 223);
      this.markcol = new Color(255, 0, 0);
      this.createMap();
      this.addMarkAid(this.currentZug);
      Timer heatTimer = new Timer(2000, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            kartePanel.this.recalcHeat();
            kartePanel.this.repaint();
         }
      });
      heatTimer.start();
      m.getContext().addCloseObject(() -> SwingUtilities.invokeLater(() -> heatTimer.stop()));
   }

   public void setStatisticsPanel(statisticsPanel stp) {
      this.stp = stp;
   }

   private void createMap() {
      this.runnning = false;
      this.cleanMap();
      String p_kids = this.getParameter("kids");
      String p_rnamens = this.getParameter("rnamens");
      String p_kidcon = this.getParameter("kidcon");
      this.region = this.getParameter("region");
      if (p_kids != null && p_rnamens != null && p_kidcon != null && this.region != null) {
         this.fillMap("namen", this.namen, p_kids);
         this.fillMap("netznames", this.namen, p_kids);
         this.fillMap("x", this.namen, p_kids);
         this.fillMap("y", this.namen, p_kids);
         this.fillMap("sichtbar", this.namen, p_kids);
         this.fillMap("aaid", this.namen, p_kids);
         this.fillMap("erid", this.namen, p_kids);
         this.fillMap("stitz", this.namen, p_kids);
         this.fillMap("rnamen", this.rnamen, p_rnamens);
         this.fillMap("kid1_", this.kids, p_kidcon);
         this.fillMap("kid2_", this.kids, p_kidcon);
         this.fillMap("kiduep_", this.kids, p_kidcon);
         this.aid4();
         TreeSet<aidPanel> sort = new TreeSet();

         for (karten_container kc : this.namen.values()) {
            kc.panel = new aidPanel(this, kc);
            sort.add(kc.panel);
            kc.panel.setViewport(this.myvp);
         }

         int i = 0;

         for (aidPanel a : sort) {
            this.add(a, i);
            a.resize();
            i++;
         }
      }
   }

   public void cleanMap() {
      if (this.namen != null) {
         for (karten_container kc : this.namen.values()) {
            kc.panel.remove();
         }
      }

      this.aids = new HashMap();
      this.namen = new HashMap();
      this.rnamen = new HashMap();
      this.kids = new HashMap();
      this.markVkids = new HashSet();
      this.markVcols = new HashMap();
   }

   public void run(MapBotChat my_chat) {
      System.out.println("run");

      for (int dlcnt = 0; this.runnning; dlcnt--) {
         if (dlcnt <= 0) {
            String updateurl = this.my_main.getParameter("url");
            xmlreader xmlr = new xmlreader(this.my_main.getDataLed());
            xmlr.registerTag("kzug", this);
            xmlr.registerTag("zeit", this);
            System.out.println("load");

            for (karten_zug z : this.zuglist.zuegelist) {
               if (z.status != 3) {
                  z.status = 1;
               }
            }

            try {
               xmlr.updateData(updateurl, new StringBuffer());
            } catch (IOException var9) {
               System.out.println("Ex: " + var9.getMessage());
               var9.printStackTrace();
            }

            my_chat.sendStatus(BOTCOMMAND.ALIVE, "");

            for (karten_zug zx : this.zuglist.zuegelist) {
               if (zx.status == 1) {
                  zx.status = 3;
               } else {
                  zx.status = 2;
               }
            }

            System.out.println("sort");

            try {
               this.cleanupZug();
               this.updateZugList();
            } catch (Exception var8) {
            }

            dlcnt = 6;
            this.my_main.setCursor(new Cursor(0));
         }

         try {
            System.out.println("sleep");
            Thread.sleep(300000L);
         } catch (InterruptedException var7) {
         }
      }
   }

   public float getScale() {
      return this.scale;
   }

   public int maxScale() {
      return 50;
   }

   public int initScale() {
      return 25;
   }

   public void setScale(int value) {
      this.scale = (float)value;
      if (this.wtimer == null) {
         this.wtimer = new Timer(600, this);
         this.wtimer.start();
      }

      this.wtimer.restart();
   }

   public void actionPerformed(ActionEvent e) {
      this.wtimer.stop();

      for (karten_container kc : this.namen.values()) {
         if (kc.panel != null) {
            kc.panel.resize();
         }
      }
   }

   private void cleanupZug() {
      System.out.println("alte Daten wegräumen");
      Iterator<karten_zug> it = this.zuglist.zuegelist.iterator();

      while (it.hasNext()) {
         karten_zug z = (karten_zug)it.next();
         if (z.status == 3) {
            it.remove();
            z.remove();
         }
      }

      this.zuglist.modifiedData();
   }

   public void handleIRC(String sender, String r, boolean publicmsg) {
      if (r.startsWith("ST:")) {
         String[] parts = r.split(":");
         if (parts.length >= 4) {
            try {
               int aid = Integer.parseInt(parts[1]);
               String hash = parts[2];
               OCCU_KIND k = OCCU_KIND.find(parts[3]);
               karten_container d = (karten_container)this.aids.get(aid);
               if (d != null) {
                  d.handleST(hash, k);
                  d.panel.repaint();
               }
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }
      } else if (r.startsWith("HT:")) {
         String[] parts = r.split(":");
         if (parts.length >= 3) {
            try {
               int aid = Integer.parseInt(parts[1]);
               long heat = Long.parseLong(parts[2]);
               karten_container d = (karten_container)this.aids.get(aid);
               if (d != null) {
                  d.heat = heat;
               }
            } catch (Exception var9) {
               var9.printStackTrace();
            }
         }
      }
   }

   public void handleIRCresult(String cmd, int res, String r, boolean publicmsg) {
      this.meldung = "Datenempfang...";

      try {
         if (cmd.compareTo("RUPDATE") == 0 && res == 200) {
            StringTokenizer zst = new StringTokenizer(r + " ", ":");
            if (zst.countTokens() >= 4) {
               int zid = Integer.parseInt(zst.nextToken().trim());
               int v = 0;
               int l = 0;
               int a = 0;

               try {
                  v = Integer.parseInt(zst.nextToken().trim());
                  l = Integer.parseInt(zst.nextToken().trim());
                  a = Integer.parseInt(zst.nextToken().trim());
               } catch (Exception var18) {
                  System.out.println("Ex: " + var18.getMessage());
                  var18.printStackTrace();
               }

               karten_zug kz = this.findZug(zid);
               if (kz != null) {
                  kz.verspaetung = v;
                  this.updateZugList(kz);
               }
            }
         } else if (cmd.compareTo("INFO") == 0 && res == 200) {
            StringTokenizer zst = new StringTokenizer(r + " ", ":");
            if (zst.countTokens() >= 8) {
               int zid = Integer.parseInt(zst.nextToken().trim());
               int h = 0;
               int m = 0;
               int s = 0;
               int pa = 0;
               int a = 0;
               int v = 0;
               int rzid = 0;
               int tcinfo = 0;
               String n = "";

               try {
                  h = Integer.parseInt(zst.nextToken().trim());
                  m = Integer.parseInt(zst.nextToken().trim());
                  s = Integer.parseInt(zst.nextToken().trim());
                  pa = Integer.parseInt(zst.nextToken().trim());
                  a = Integer.parseInt(zst.nextToken().trim());
                  v = Integer.parseInt(zst.nextToken().trim());
                  n = zst.nextToken().trim();
                  rzid = Integer.parseInt(zst.nextToken().trim());
                  tcinfo = Integer.parseInt(zst.nextToken().trim());
               } catch (Exception var17) {
               }

               karten_zug kz = this.findZug(zid);
               if (kz != null) {
                  if (tcinfo > 0) {
                     kz.status = 3;
                     kz.updateAid(0);
                     kz.visibleaid = 0;
                     kz.visible = false;
                     kz.laststopdone = false;
                  } else {
                     kz.updateAid(a);
                     kz.visibleaid = v;
                     kz.visible = v != 0;
                     kz.laststopdone = v == 2;
                  }

                  this.updateZugList(kz);
               }
            }
         } else if (cmd.compareTo("MESSAGE") == 0 && res == 200) {
            this.my_main.message(r.trim());
         } else if (cmd.compareTo("MESSAGE") == 0 && res == 201) {
            this.my_main.message(r.trim(), true);
         } else if (cmd.compareTo("USER") != 0 || res != 200 && res != 201) {
            if (cmd.compareTo("USER") == 0 && res == 210) {
               StringTokenizer zst = new StringTokenizer(r + " ", ":");
               if (zst.countTokens() >= 3) {
                  int aid = Integer.parseInt(zst.nextToken().trim());
                  String stw = zst.nextToken().trim();
                  String tel = zst.nextToken().trim();
                  karten_container d = (karten_container)this.aids.get(aid);
                  if (d != null) {
                     d.spieler = null;
                     d.heat = 0L;
                     d.panel.newPlayer();
                     this.recalcHeat();
                  }
               }
            } else if (cmd.equals("REDIRINFO")) {
               String[] tokens = TextHelper.tokenizerToArray(new StringTokenizer(r + " ", ":"));
               int zid = 0;

               for (int i = 0; i < tokens.length; i++) {
                  if (tokens[i].trim().equals("ZID")) {
                     zid = Integer.parseInt(tokens[i + 1].trim());
                     break;
                  }
               }

               if (zid > 0) {
                  karten_zug kz = this.findZug(zid);
                  if (kz != null) {
                     kz.updateRedirect(res, tokens);
                     this.zuglist.modifiedData(kz);
                  }
               }
            }
         } else {
            StringTokenizer zst = new StringTokenizer(r + " ", ":");
            if (zst.countTokens() >= 4) {
               int aid = Integer.parseInt(zst.nextToken().trim());
               String stw = zst.nextToken().trim();
               String tel = zst.nextToken().trim();
               String user = zst.nextToken().trim();
               karten_container d = (karten_container)this.aids.get(aid);
               if (d != null) {
                  d.spieler = user;
                  d.canstitz = res == 200;
                  d.panel.newPlayer();
               }
            }
         }
      } catch (NumberFormatException var19) {
         System.out.println("NFE: " + r);
      } catch (Exception var20) {
      }
   }

   private void recalcHeat() {
      if (!this.aids.isEmpty()) {
         long minHeat = Long.MAX_VALUE;
         long maxHeat = Long.MIN_VALUE;

         for (karten_container ad : this.aids.values()) {
            if (ad.spieler != null) {
               minHeat = Math.min(minHeat, ad.heat);
               maxHeat = Math.max(maxHeat, ad.heat);
            }
         }

         if (minHeat != maxHeat) {
            long sum = 0L;
            int cnt = 0;

            for (karten_container adx : this.aids.values()) {
               int r = 0;
               if (adx.heat > 0L) {
                  if (maxHeat != 0L) {
                     r = (int)((adx.heat - 0L) * 255L / (maxHeat - 0L));
                  }

                  adx.heatColor = new Color(255, 255 - r, 255 - r);
               } else {
                  if (minHeat != 0L) {
                     r = 255 - (int)((adx.heat - minHeat) * 255L / (0L - minHeat));
                  }

                  adx.heatColor = new Color(255 - r, 255, 255 - r);
               }

               adx.heatSize = r;
               if (adx.spieler != null) {
                  sum += adx.heat;
                  cnt++;
               }
            }

            if (cnt > 0) {
               sum /= (long)cnt;
               this.stp.updateHeatStats(sum, minHeat, maxHeat);
            } else {
               this.stp.updateHeatStats(0L, 0L, 0L);
            }

            this.repaint();
         }
      }
   }

   karten_zug findZug(int zid) {
      karten_zug z = null;

      for (karten_zug zz : this.zuglist.zuegelist) {
         if (zz.zid == zid) {
            z = zz;
            break;
         }
      }

      return z;
   }

   private karten_zug findOrAddZug(int zid) {
      karten_zug z = this.findZug(zid);
      if (z == null) {
         z = new karten_zug(this);
         z.zid = zid;
         this.zuglist.zuegelist.add(z);
         this.zuglist.modifiedData();
      }

      return z;
   }

   public void parseStartTag(String tag, Attributes attrs) {
   }

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
      if (tag.equalsIgnoreCase("kzug")) {
         int zid = Integer.parseInt(attrs.getValue("zid"));
         karten_zug s = this.findOrAddZug(zid);
         if (s.status == 0) {
            s.addPlan(attrs);
         } else {
            s.status = 2;
         }
      }
   }

   private void updateZugList(karten_zug kz) {
      this.updateZugList();
      if (kz.currentKC != null) {
         kz.currentKC.panel.update(kz);
      }

      this.zuglist.modifiedData(kz);
   }

   public String getParameter(String p) {
      return this.my_main.getParameter(p);
   }

   private void fillMap(String name, HashMap<Integer, karten_container> m, String keys) {
      String[] tt = keys.trim().split(",");

      for (String v : tt) {
         v = v.trim();
         if (!v.isEmpty()) {
            int vi = Integer.parseInt(v);
            String p = this.getParameter(name + vi);
            karten_container k;
            if (m.containsKey(vi)) {
               k = (karten_container)m.get(vi);
            } else {
               k = new karten_container();
               m.put(vi, k);
               k.set("kid", v);
            }

            k.set(name, p);
            if (name.equals("aaid")) {
               int aid = k.aaid;
               if (!this.aids.containsKey(aid)) {
                  this.aids.put(aid, k);
               }
            }
         }
      }
   }

   private void aid4() {
      int n = Integer.parseInt(this.getParameter("elements"));

      for (int i = 0; i < n; i++) {
         int aid4 = Integer.parseInt(this.getParameter("aid4aid" + i));
         int element4 = Integer.parseInt(this.getParameter("aid4element" + i));
         int enr4 = Integer.parseInt(this.getParameter("aid4enr" + i));
         String swwert4 = this.getParameter("aid4swwert" + i);
         if (this.aids.containsKey(aid4)) {
            karten_container kc = (karten_container)this.aids.get(aid4);
            kc.addElement(element4, enr4, swwert4);
         }
      }
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      this.showPic((Graphics2D)g);
   }

   public Dimension getMinimumSize() {
      return this.getMinsize();
   }

   public Dimension getMaximumSize() {
      return this.getMinsize();
   }

   public Dimension getPreferredSize() {
      return this.getMinsize();
   }

   void showZugWeg(karten_zug z) {
      this.currentZug.resetMark();
      String t = "";
      if (z != null) {
         t = z.getSpezialName();
         int aid1 = 0;
         int aid2 = 0;

         for (szug_plan_base pos : z.plan) {
            if (pos.aid != aid1) {
               aid1 = pos.aid;
               if (aid2 > 0) {
                  this.currentZug.addMarkAid(aid1, aid2);
               }

               aid2 = aid1;
            }
         }
      }

      try {
         this.selectionField.setText(t);
      } catch (NullPointerException var7) {
      }

      this.recalcMarks();
   }

   public void setCurrentZugPanel(JTextField selectionField) {
      this.selectionField = selectionField;
   }

   public void removeMark(connectColor cc) {
      this.markVkids.remove(cc);
      this.recalcMarks();
   }

   public void addMarkAid(connectColor cc) {
      this.markVkids.add(cc);
      this.recalcMarks();
   }

   public void recalcMarks() {
      this.markVcols.clear();

      for (connectColor cc : this.markVkids) {
         for (int kid : cc.markVkids) {
            this.markVcols.put(kid, cc.col);
         }
      }

      this.repaint();
   }

   private Dimension getMinsize() {
      Dimension d = new Dimension(1, 1);
      Iterator it = this.namen.keySet().iterator();

      while (it.hasNext()) {
         karten_container k = (karten_container)this.namen.get(it.next());
         int x2 = Math.round(((float)k.x + 4.0F + 1.0F) * 25.0F);
         int y2 = Math.round(((float)k.y + 1.0F) * 25.0F) + 70 + 40;
         d.width = Math.max(x2, d.width);
         d.height = Math.max(y2, d.height);
      }

      return d;
   }

   private void updateZugList() {
      for (karten_zug z : this.zuglist.zuegelist) {
         if (z.currentKC == null) {
            karten_container kc = (karten_container)this.aids.get(z.getAid());
            if (kc != null) {
               System.out.println("neuzug " + z.getName() + " nach " + z.getAid() + " (" + kc.aaid + ")");
               z.currentKC = kc;
               this.zuglist.modifiedData(z);
            }
         } else if (z.currentKC.aaid != z.getAid()) {
            karten_container kc = (karten_container)this.aids.get(z.getAid());
            if (kc != null) {
               System.out.println("zug " + z.getName() + " von " + z.currentKC.aaid + " nach " + z.getAid() + " (" + kc.aaid + ") H:" + kc.hashCode());
               this.addAnim(z.currentKC.aaid, z.getAid());
               z.currentKC = kc;
               this.zuglist.modifiedData(z);
            }
         }
      }

      this.repaint();
   }

   public String getRegion() {
      return this.region;
   }

   private void showPic(Graphics2D g) {
      if (this.kids != null && this.namen != null && this.rnamen != null) {
         g.setColor(this.bgcolor);
         g.fillRect(0, 0, 0, 0);

         for (karten_container k : this.namen.values()) {
            if (k.heatSize > 0 && k.spieler != null) {
               int x1 = Math.round((float)k.x * this.scale);
               int y1 = Math.round((float)k.y * this.scale);
               g.setColor(k.heatColor);
               int s = k.heatSize / 20;
               g.fillRect(x1 - s, y1 - s, Math.round(4.0F * this.scale) + s * 2, Math.round(1.0F * this.scale) + s * 2);
            }
         }

         boolean lastRepaint = false;
         boolean allowInc = false;
         if (!this.anims.isEmpty()) {
            long c = System.currentTimeMillis();
            if (this.lastAnim <= c) {
               this.lastAnim = c + 90L;
               allowInc = true;
            }
         }

         for (int vkid : this.kids.keySet()) {
            karten_container kx = (karten_container)this.kids.get(vkid);
            karten_container k1 = (karten_container)this.namen.get(kx.kid1);
            karten_container k2 = (karten_container)this.namen.get(kx.kid2);
            int x1 = Math.round(((float)k1.x + 2.0F) * this.scale);
            int y1 = Math.round(((float)k1.y + 0.5F) * this.scale);
            int x2 = Math.round(((float)k2.x + 2.0F) * this.scale);
            int y2 = Math.round(((float)k2.y + 0.5F) * this.scale);
            if (this.markVcols.containsKey(vkid)) {
               g.setColor((Color)this.markVcols.get(vkid));
            } else if (kx.kiduep) {
               g.setColor(this.üplinecol);
            } else {
               g.setColor(this.linecol);
            }

            if (k1.erid <= 0 && k1.netznames.equalsIgnoreCase(this.region) && k2.erid <= 0 && k2.netznames.equalsIgnoreCase(this.region)) {
               g.setStroke(new BasicStroke(3.0F));
            } else {
               float[] dash = new float[]{5.0F, 5.0F};
               g.setStroke(new BasicStroke(3.0F, 0, 2, 1.0F, dash, 0.0F));
            }

            if (!this.anims.isEmpty()) {
               for (kartePanel.AnimDetails ad : this.anims) {
                  if (k1.kid == ad.kid1 && k2.kid == ad.kid2 || ad.kid2 == k1.kid && ad.kid1 == k2.kid) {
                     if (allowInc) {
                        ad.position += 2;
                        if (ad.position >= 100) {
                           this.anims.remove(ad);
                           lastRepaint = true;
                        }

                        if (ad.kid2 == k2.kid && ad.kid1 == k1.kid) {
                           ad.cache = this.mkGradient(x1, y1, x2, y2, ad.position, g.getColor());
                        } else {
                           ad.cache = this.mkGradient(x2, y2, x1, y1, ad.position, g.getColor());
                        }
                     }

                     if (ad.cache != null) {
                        g.setPaint(ad.cache);
                     }
                     break;
                  }
               }
            }

            g.drawLine(x1, y1, x2, y2);
            g.setStroke(new BasicStroke(1.0F));
         }

         g.setColor(Color.BLACK);
         if (lastRepaint) {
            this.repaint();
         }
      }
   }

   private LinearGradientPaint mkGradient(int x1, int y1, int x2, int y2, int position, Color current) {
      if (position >= 98) {
         return null;
      } else {
         float[] dist = new float[]{0.0F, (float)((position + 1) / 2) / 100.0F, (float)(position + 1) / 100.0F, (float)(position + 2) / 100.0F, 1.0F};
         Color[] colors = new Color[]{current, current, Color.YELLOW, current, current};
         return new LinearGradientPaint((float)x1, (float)y1, (float)x2, (float)y2, dist, colors);
      }
   }

   private void addAnim(int oldaid, int newaid) {
      try {
         this.anims.add(new kartePanel.AnimDetails(((karten_container)this.aids.get(oldaid)).kid, ((karten_container)this.aids.get(newaid)).kid));
      } catch (NullPointerException var4) {
         System.out.println("Unk: " + oldaid + " or " + newaid);
      }

      this.animTimer.start();
   }

   private class AnimDetails {
      final int kid1;
      final int kid2;
      int position = 0;
      Paint cache = null;

      AnimDetails(int kid1, int kid2) {
         this.kid1 = kid1;
         this.kid2 = kid2;
      }
   }
}
