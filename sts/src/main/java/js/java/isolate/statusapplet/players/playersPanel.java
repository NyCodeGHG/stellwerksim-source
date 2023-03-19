package js.java.isolate.statusapplet.players;

import java.awt.Color;
import java.awt.Cursor;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.chatcomng.OCCU_KIND;
import js.java.tools.TextHelper;

public class playersPanel extends JTable {
   private final TableRowSorter<DefaultTableModel> trs;
   private oneInstance my_main;
   private JViewport myvp = null;
   private ConcurrentHashMap<Integer, players_aid> aids = new ConcurrentHashMap();
   private ConcurrentHashMap<Integer, players_zug> zids = new ConcurrentHashMap();
   private final DefaultTableModel model;
   private int maintcnt = 0;
   private ConcurrentHashMap<Integer, zidRedirect> redirects = new ConcurrentHashMap();
   private final UserContext uc;
   private HashSet<ircupdate> hooks = new HashSet();
   private long simutime = 0L;
   private long simusynctime = 0L;

   public playersPanel(UserContext uc, oneInstance m, JScrollPane sp) {
      super();
      this.uc = uc;
      this.my_main = m;
      sp.setViewportView(this);
      this.myvp = sp.getViewport();
      this.setDoubleBuffered(true);
      this.model = new DefaultTableModel(new String[]{"Stellwerk", "User", "St", "Um/Ü", "Ht"}, 0) {
         public Class getColumnClass(int columnIndex) {
            return players_aid.class;
         }

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
         }
      };
      this.setAutoCreateRowSorter(true);
      this.trs = new TableRowSorter(this.model);
      this.setModel(this.model);
      this.setRowSorter(this.trs);
      this.trs.setSortsOnUpdates(true);
      this.setShowVerticalLines(false);
      this.getTableHeader().setReorderingAllowed(false);
      this.setDefaultRenderer(players_aid.class, new playerslistrenderer(this));
      this.setRowHeight(this.getRowHeight() * 2);
      this.getColumnModel().getColumn(0).setMinWidth(90);
      this.getColumnModel().getColumn(0).setPreferredWidth(140);
      this.getColumnModel().getColumn(0).setMaxWidth(200);
      this.trs.setComparator(0, new Comparator<players_aid>() {
         public int compare(players_aid o1, players_aid o2) {
            return o1.name.compareToIgnoreCase(o2.name);
         }
      });
      this.getColumnModel().getColumn(1).setMinWidth(60);
      this.getColumnModel().getColumn(1).setPreferredWidth(100);
      this.getColumnModel().getColumn(1).setMaxWidth(200);
      this.trs.setComparator(1, new Comparator<players_aid>() {
         public int compare(players_aid o1, players_aid o2) {
            if (o1.spieler == null && o2.spieler != null) {
               return 1;
            } else if (o1.spieler != null && o2.spieler == null) {
               return -1;
            } else {
               return o1.spieler == null && o2.spieler == null ? o1.name.compareToIgnoreCase(o2.name) : o1.spieler.compareToIgnoreCase(o2.spieler);
            }
         }
      });
      this.getColumnModel().getColumn(2).setMinWidth(20);
      this.getColumnModel().getColumn(2).setPreferredWidth(20);
      this.getColumnModel().getColumn(2).setMaxWidth(20);
      this.trs.setComparator(2, new Comparator<players_aid>() {
         public int compare(players_aid o1, players_aid o2) {
            int r = o1.getStoerungsCount() - o2.getStoerungsCount();
            return r != 0 ? r : o1.name.compareToIgnoreCase(o2.name);
         }
      });
      this.getColumnModel().getColumn(3).setMinWidth(40);
      this.getColumnModel().getColumn(3).setPreferredWidth(40);
      this.getColumnModel().getColumn(3).setMaxWidth(40);
      this.trs.setSortable(3, false);
      this.getColumnModel().getColumn(4).setMinWidth(40);
      this.getColumnModel().getColumn(4).setPreferredWidth(40);
      this.getColumnModel().getColumn(4).setMaxWidth(40);
      this.trs.setComparator(4, new Comparator<players_aid>() {
         public int compare(players_aid o1, players_aid o2) {
            if (o1.heat > o2.heat) {
               return 1;
            } else {
               return o1.heat < o2.heat ? -1 : o1.name.compareToIgnoreCase(o2.name);
            }
         }
      });
      this.trs.setSortKeys(Arrays.asList(new SortKey(0, SortOrder.ASCENDING)));
   }

   public void clean() {
   }

   public void handleIRC(String sender, String r) {
      if (r.startsWith("ST:")) {
         String[] parts = r.split(":");
         if (parts.length >= 4) {
            try {
               int aid = Integer.parseInt(parts[1]);
               String hash = parts[2];
               OCCU_KIND k = OCCU_KIND.find(parts[3]);
               players_aid a = this.findAid(aid);
               if (a != null) {
                  a.handleST(hash, k);
                  if (SwingUtilities.isEventDispatchThread()) {
                     this.updateAid(a);
                  } else {
                     try {
                        SwingUtilities.invokeAndWait(new playersPanel.aidRunnable(a));
                     } catch (Exception var11) {
                     }
                  }
               }
            } catch (Exception var12) {
               var12.printStackTrace();
            }
         }
      } else if (r.startsWith("HT:")) {
         String[] parts = r.split(":");
         if (parts.length >= 3) {
            try {
               int aid = Integer.parseInt(parts[1]);
               long heat = Long.parseLong(parts[2]);
               players_aid a = this.findAid(aid);
               if (a != null) {
                  a.setHeat(heat);
                  if (SwingUtilities.isEventDispatchThread()) {
                     this.updateAid(a);
                  } else {
                     try {
                        SwingUtilities.invokeAndWait(new playersPanel.aidRunnable(a));
                     } catch (Exception var9) {
                     }
                  }
               }
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }
      }
   }

   public void handleIRCresult(String cmd, int res, String r, boolean publicmsg) {
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
               } catch (Exception var24) {
                  System.out.println("Ex: " + var24.getMessage());
                  var24.printStackTrace();
               }

               players_zug pz = this.findOrAddZug(zid);
               if (pz != null) {
                  pz.prevverspaetung = pz.verspaetung;
                  pz.verspaetung = v;
                  pz.verspaetungSeen = false;
                  ++pz.verspaetungCounter;
                  if (SwingUtilities.isEventDispatchThread()) {
                     this.updateZug(pz);
                  } else {
                     try {
                        SwingUtilities.invokeAndWait(new playersPanel.pzRunnable(pz));
                     } catch (Exception var23) {
                     }
                  }
               }
            }
         } else if (cmd.compareTo("INFO") == 0 && res == 200) {
            StringTokenizer zst = new StringTokenizer(r + " ", ":");
            if (zst.countTokens() >= 9) {
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
               } catch (Exception var22) {
                  System.out.println("Ex: " + var22.getMessage());
                  var22.printStackTrace();
               }

               if (tcinfo > 0) {
                  final players_zug pz = this.findZug(zid);
                  if (pz != null) {
                     pz.status = 3;
                     pz.updatePlan(n);
                     pz.updateAid(0);
                     pz.rzid = rzid;
                     pz.visibleaid = 0;
                     pz.visible = false;
                     pz.laststopdone = false;
                     if (SwingUtilities.isEventDispatchThread()) {
                        this.updateZug(pz);
                     } else {
                        try {
                           SwingUtilities.invokeAndWait(new Runnable() {
                              public void run() {
                                 playersPanel.this.updateZug(pz);
                              }
                           });
                        } catch (Exception var21) {
                        }
                     }
                  }
               } else {
                  final players_zug pz = this.findOrAddZug(zid);
                  if (pz != null) {
                     pz.updatePlan(n);
                     pz.updateAid(a);
                     pz.rzid = rzid;
                     pz.visibleaid = v;
                     pz.visible = v != 0;
                     pz.laststopdone = v == 2;
                     if (SwingUtilities.isEventDispatchThread()) {
                        this.updateZug(pz);
                     } else {
                        try {
                           SwingUtilities.invokeAndWait(new Runnable() {
                              public void run() {
                                 playersPanel.this.updateZug(pz);
                              }
                           });
                        } catch (Exception var20) {
                        }
                     }
                  }
               }
            }
         } else if ((cmd.compareTo("MESSAGE") != 0 || res != 200) && (cmd.compareTo("MESSAGE") != 0 || res != 201)) {
            if (cmd.compareTo("USER") == 0 && (res == 200 || res == 201)) {
               StringTokenizer zst = new StringTokenizer(r + " ", ":");
               if (zst.countTokens() >= 4) {
                  int aid = Integer.parseInt(zst.nextToken().trim());
                  String stw = zst.nextToken().trim();
                  String tel = zst.nextToken().trim();
                  String user = zst.nextToken().trim();
                  players_aid d = this.findOrAddAid(aid, stw, tel);
                  if (d != null) {
                     if (d.spieler == null || !d.spieler.equals(user)) {
                        d.starttime = System.currentTimeMillis();
                        d.stoptime = 0L;
                     }

                     d.resetStCount();
                     d.spieler = user;
                     d.canstitz = res == 200;
                     if (SwingUtilities.isEventDispatchThread()) {
                        this.updateAid(d);
                     } else {
                        try {
                           SwingUtilities.invokeAndWait(new playersPanel.aidRunnable(d));
                        } catch (Exception var19) {
                        }
                     }
                  }

                  this.my_main.setCursor(new Cursor(0));
               }
            } else if (cmd.compareTo("USER") == 0 && res == 210) {
               StringTokenizer zst = new StringTokenizer(r + " ", ":");
               if (zst.countTokens() >= 3) {
                  int aid = Integer.parseInt(zst.nextToken().trim());
                  String stw = zst.nextToken().trim();
                  String tel = zst.nextToken().trim();
                  players_aid d = (players_aid)this.aids.get(aid);
                  if (d != null) {
                     d.resetStCount();
                     d.spieler = null;
                     d.stoptime = System.currentTimeMillis();
                     if (SwingUtilities.isEventDispatchThread()) {
                        this.updateAid(d);
                     } else {
                        try {
                           SwingUtilities.invokeAndWait(new playersPanel.aidRunnable(d));
                        } catch (Exception var18) {
                        }
                     }
                  }
               }
            } else if (cmd.equals("REDIRINFO")) {
               String[] tokens = TextHelper.tokenizerToArray(new StringTokenizer(r + " ", ":"));
               int zid = 0;

               for(int i = 0; i < tokens.length; ++i) {
                  if (tokens[i].equals("ZID")) {
                     zid = Integer.parseInt(tokens[i + 1].trim());
                     break;
                  }
               }

               if (zid > 0) {
                  zidRedirect zr = (zidRedirect)this.redirects.get(zid);
                  if (zr == null && res != 400) {
                     zr = new zidRedirect(this.my_main, zid);
                     this.redirects.put(zid, zr);
                  }

                  if (zr != null) {
                     zr.update(res, tokens);
                     if (res == 400) {
                        this.redirects.remove(zid);
                     }
                  }
               }
            }
         }
      } catch (NumberFormatException var25) {
         System.out.println("NFE: " + r);
      } catch (Exception var26) {
         System.out.println("IRC-EX:" + var26.getMessage());
         var26.printStackTrace();
      }

      ++this.maintcnt;
      if (this.maintcnt > 1000) {
         this.maintcnt = 0;
      }
   }

   public boolean wirdAidUmfahren(players_aid a) {
      for(zidRedirect zr : this.redirects.values()) {
         if (zr.wirdAidUmfahren(a)) {
            return true;
         }
      }

      return false;
   }

   public boolean hatAidUmleitung(players_aid a) {
      for(zidRedirect zr : this.redirects.values()) {
         if (zr.hatAidUmleitung(a)) {
            return true;
         }
      }

      return false;
   }

   players_zug findOrAddZug(int zid) {
      if (zid == 0) {
         return null;
      } else if (this.zids.containsKey(zid)) {
         return (players_zug)this.zids.get(zid);
      } else {
         players_zug z = new players_zug(this, zid);
         this.zids.put(zid, z);
         return z;
      }
   }

   players_zug findZug(int zid) {
      return this.zids.containsKey(zid) ? (players_zug)this.zids.get(zid) : null;
   }

   private players_aid findOrAddAid(int aid, String name, String tel) {
      if (this.aids.containsKey(aid)) {
         return (players_aid)this.aids.get(aid);
      } else {
         players_aid a = new players_aid(this, aid, name, tel);
         this.aids.put(aid, a);

         for(players_zug z : this.zids.values()) {
            if (z.currentaid == null && z.aid == aid) {
               z.aid = 0;
               z.updateAid(aid);
            }
         }

         return a;
      }
   }

   public players_aid findAid(int aid) {
      return this.aids.containsKey(aid) ? (players_aid)this.aids.get(aid) : null;
   }

   public TreeSet<players_aid> getSortedAids() {
      TreeSet<players_aid> sort = new TreeSet();

      for(players_aid ad : this.aids.values()) {
         sort.add(ad);
      }

      return sort;
   }

   void registerHook(ircupdate i) {
      this.hooks.add(i);
   }

   void unregisterHook(ircupdate i) {
      this.hooks.remove(i);
   }

   String getParameter(String p) {
      return this.uc.getParameter(p);
   }

   int getInstanz() {
      return this.my_main.getInstanz();
   }

   private void refreshHeat() {
      if (!this.aids.isEmpty()) {
         long minHeat = Long.MAX_VALUE;
         long maxHeat = Long.MIN_VALUE;

         for(players_aid ad : this.aids.values()) {
            minHeat = Math.min(minHeat, ad.heat);
            maxHeat = Math.max(maxHeat, ad.heat);
         }

         if (minHeat != maxHeat) {
            for(players_aid ad : this.aids.values()) {
               int r = 0;
               if (ad.heat > 0L) {
                  if (maxHeat != 0L) {
                     r = (int)(ad.heat * 255L / maxHeat);
                  }

                  ad.heatColor = new Color(255, 255 - r, 255 - r);
               } else {
                  if (minHeat != 0L) {
                     r = (int)(-ad.heat * 255L / -minHeat);
                  }

                  ad.heatColor = new Color(255 - r, 255, 255 - r);
               }
            }

            this.model.fireTableRowsUpdated(0, this.model.getRowCount() - 1);
         }
      }
   }

   private void updateZug(players_zug z) {
      if (z.currentaid != null) {
         z.currentaid.update();
      }

      for(ircupdate i : this.hooks) {
         i.updateZug(z);
      }
   }

   private void updateAid(players_aid d) {
      boolean found = false;

      for(int i = 0; i < this.model.getRowCount(); ++i) {
         if (d.spieler == null) {
            if (d == (players_aid)this.model.getValueAt(i, 0)) {
               this.model.removeRow(i);
               break;
            }
         } else if (d == (players_aid)this.model.getValueAt(i, 0)) {
            found = true;
            this.model.fireTableRowsUpdated(i, i);
            break;
         }
      }

      if (d.spieler != null && !found) {
         players_aid[] line = new players_aid[]{d, d, d, d, d, d};
         this.model.addRow(line);
      }

      for(ircupdate i : this.hooks) {
         i.updateAid(d);
      }

      this.refreshHeat();
   }

   public long getSimutime() {
      return this.simutime + (new Date().getTime() - this.simusynctime);
   }

   public void setTime(String t) throws ParseException {
      DateFormat mdf = DateFormat.getTimeInstance(2, Locale.GERMAN);

      try {
         this.simutime = mdf.parse(t.trim()).getTime();
         this.simusynctime = new Date().getTime();
      } catch (ParseException var4) {
      }
   }

   public int numberOfSpieler() {
      int ret = 0;

      for(players_aid a : this.aids.values()) {
         if (a.spieler != null) {
            ++ret;
         }
      }

      return ret;
   }

   public int numberOfStitz() {
      int ret = 0;

      for(players_aid a : this.aids.values()) {
         if (a.spieler != null && a.canstitz) {
            ++ret;
         }
      }

      return ret;
   }

   public int numberOfSichtbar() {
      int ret = 0;

      for(players_zug z : this.zids.values()) {
         if (z.visible && z.lastChangedMinutes() < 120) {
            ++ret;
         }
      }

      return ret;
   }

   public int numberOfAusfahrt() {
      int ret = 0;

      for(players_zug z : this.zids.values()) {
         if (z.laststopdone) {
            ++ret;
         }
      }

      return ret;
   }

   public int numberOfTemplates() {
      HashSet<Integer> cnt = new HashSet();

      for(players_zug z : this.zids.values()) {
         if (z.visible && z.lastChangedMinutes() < 120) {
            cnt.add(z.rzid);
         }
      }

      return cnt.size();
   }

   public double[] verspätungsDurchschnitt() {
      double[] ret = new double[]{0.0, 0.0};
      LinkedList<Integer> zeiten = new LinkedList();
      int c = 0;
      double sum = 0.0;

      for(players_zug z : this.zids.values()) {
         if (z.lastChangedMinutes() < 30) {
            sum += (double)z.verspaetung;
            ++c;
            zeiten.add(z.verspaetung);
         }
      }

      if (!zeiten.isEmpty()) {
         Integer[] v = (Integer[])zeiten.toArray(new Integer[zeiten.size()]);
         Arrays.sort(v);
         int middle = zeiten.size() / 2;
         if (zeiten.size() % 2 == 1) {
            ret[0] = (double)v[middle].intValue();
         } else {
            ret[0] = (double)(v[middle - 1] + v[middle]) / 2.0;
         }

         ret[1] = sum / (double)c;
      }

      return ret;
   }

   public int[] heatValue() {
      int[] ret = new int[]{0, 0, 0, 0};
      if (this.aids.isEmpty()) {
         return ret;
      } else {
         long minHeat = Long.MAX_VALUE;
         long maxHeat = Long.MIN_VALUE;
         long sum = 0L;
         long[] values = new long[this.aids.size()];
         int i = 0;

         for(players_aid ad : this.aids.values()) {
            minHeat = Math.min(minHeat, ad.heat);
            maxHeat = Math.max(maxHeat, ad.heat);
            sum += ad.heat;
            values[i] = ad.heat;
            ++i;
         }

         sum /= (long)this.aids.size();
         ret[0] = (int)sum;
         Arrays.sort(values);
         int middle = values.length / 2;
         if (values.length % 2 == 1) {
            ret[1] = (int)values[middle];
         } else {
            ret[1] = (int)((double)(values[middle - 1] + values[middle]) / 2.0);
         }

         int viertel = values.length / 4;
         ret[2] = (int)values[values.length - viertel - 1];
         ret[3] = (int)maxHeat;
         return ret;
      }
   }

   private static long median(Long[] m) {
      int middle = m.length / 2;
      if (m.length % 2 == 1) {
         return m[middle];
      } else {
         return middle > 0 ? (m[middle - 1] + m[middle]) / 2L : 0L;
      }
   }

   public long[] spieldauer() {
      LinkedList<Long> zeiten = new LinkedList();

      for(players_aid a : this.aids.values()) {
         if (a.starttime > 0L) {
            long stoptime = System.currentTimeMillis();
            if (a.stoptime > 0L) {
               stoptime = a.stoptime;
            }

            zeiten.add(stoptime - a.starttime);
         }
      }

      long[] ret = new long[2];
      Long[] z = (Long[])zeiten.toArray(new Long[zeiten.size()]);
      Arrays.sort(z);
      ret[0] = median(z);

      try {
         ret[1] = z[z.length - 1];
      } catch (ArrayIndexOutOfBoundsException var6) {
         ret[1] = 0L;
      }

      return ret;
   }

   private class aidRunnable implements Runnable {
      private final players_aid d;

      aidRunnable(players_aid d) {
         super();
         this.d = d;
      }

      public void run() {
         playersPanel.this.updateAid(this.d);
      }
   }

   private class pzRunnable implements Runnable {
      final players_zug pz;

      pzRunnable(players_zug pz) {
         super();
         this.pz = pz;
      }

      public void run() {
         playersPanel.this.updateZug(this.pz);
      }
   }
}
