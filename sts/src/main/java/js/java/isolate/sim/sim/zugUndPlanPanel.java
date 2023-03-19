package js.java.isolate.sim.sim;

import de.deltaga.eb.EventHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JPopupMenu.Separator;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.Simulator;
import js.java.isolate.sim.flagdata;
import js.java.isolate.sim.gleisbild.bahnsteigDetailStore;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;
import js.java.isolate.sim.sim.botcom.events.ZugUserText;
import js.java.isolate.sim.sim.funk.funkMenu;
import js.java.isolate.sim.toolkit.HyperlinkCaller;
import js.java.isolate.sim.zug.fahrplanModel;
import js.java.isolate.sim.zug.gleisModel;
import js.java.isolate.sim.zug.gleisSortModel;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.chatcomng.BOTCOMMAND;
import js.java.schaltungen.chatcomng.ChannelsNameParser;
import js.java.schaltungen.chatcomng.IrcLine;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.ColorText;
import js.java.tools.NumString;
import js.java.tools.TextHelper;
import js.java.tools.prefs;
import js.java.tools.gui.SwingTools;
import js.java.tools.gui.border.DropShadowBorder;
import js.java.tools.gui.border.TopLineBorder;
import js.java.tools.gui.clock.bahnhofsUhr;
import js.java.tools.gui.clock.bahnhofsUhr.timeDeliverer;
import js.java.tools.gui.multipane.MultiSplitLayout;
import js.java.tools.gui.multipane.MultiSplitPane;
import js.java.tools.gui.multipane.MultiSplitLayout.Divider;
import js.java.tools.gui.multipane.MultiSplitLayout.Node;
import js.java.tools.gui.multipane.MultiSplitPane.DividerPainter;
import js.java.tools.gui.table.ButtonColorRenderer;
import js.java.tools.gui.table.ButtonColorText;
import js.java.tools.gui.table.ButtonPressedListener;
import js.java.tools.gui.table.ColorRenderer;

public class zugUndPlanPanel extends JPanel implements ButtonPressedListener, timeDeliverer, SessionClose {
   private static FATwriter debugMode = null;
   private final stellwerksim_main my_main;
   private final gleisbildModelFahrweg my_gleisbild;
   private infoBoxPanel infoBox;
   private zugFahrplanPanel fahrplanBox;
   private ConcurrentHashMap<String, zug> alleZuege = null;
   private ConcurrentHashMap<String, Date> seen_ZuegeByName = null;
   private ConcurrentHashMap<String, Date> seen_ZuegeByZid = null;
   private fahrplanModel fahrplanMdl;
   private final gleisModel gleisModel = new gleisModel();
   private final JPanel chatPanel;
   private final prefs prefs;
   private funkMenu openFunk = null;
   private final JPopupMenu funkPopup;
   private final bahnsteigDetailStore alleBahnsteige;
   private VerspBegruendung visibleVerspBegr = null;
   private JRadioButtonMenuItem[] colMenuItems = new JRadioButtonMenuItem[8];
   private JRadioButtonMenuItem[] numMenuItems = new JRadioButtonMenuItem[10];
   private zug fahrplanTable_lastSelected = null;
   private JMenuItem qsMenu1 = null;
   private MultiSplitPane split;
   private JMenu autoMsgMenu;
   private JMenuItem begruendungMenu;
   private ButtonGroup colMarkerGroup;
   private JMenu colMenu;
   private JLabel fPopupLabel;
   private JPanel fahrplanPane;
   private JPopupMenu fahrplanPopup;
   private JTable fahrplanTable;
   private JPanel funkPane;
   private JButton funkSendeButton;
   private JScrollPane gleisPane;
   private JTable gleisTable;
   private JPanel infoPane;
   private Separator jSeparator2;
   private Separator jSeparator3;
   private ButtonGroup numMarkerGroup;
   private JMenu numMenu;
   private JPanel scrollerPanel;
   private JScrollPane zuegePane;
   private FATwriter dumperMode = null;
   private JMenuItem dumper_p = null;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public zugUndPlanPanel(stellwerksim_main m, gleisbildModelFahrweg glb, JPanel chatPanel, prefs prefs) {
      super();
      this.my_main = m;
      this.my_gleisbild = glb;
      m.uc.addCloseObject(this);
      m.uc.busSubscribe(this);
      this.chatPanel = chatPanel;
      this.prefs = prefs;
      this.alleBahnsteige = new bahnsteigDetailStore(this.my_gleisbild);
      m.uc.addCloseObject(this.alleBahnsteige);
      this.funkPopup = new JPopupMenu();
      this.funkPopup.setInvoker(this);
      this.funkPopup.setBorder(new DropShadowBorder(false, false, true, true));
      this.funkPopup.setBorderPainted(true);
      this.clear();
      this.fahrplanMdl = fahrplanModel.createModel();
      this.initComponents();
      this.initMyComponents();
      this.initZugOnBahnsteig();
      this.autoMsgMenu.getParent().remove(this.autoMsgMenu);
   }

   public void setZugOnBahnsteig(String bname, zug z) {
      zugUndPlanPanel.zugRunnable zr = new zugUndPlanPanel.zugRunnable(z, bname) {
         @Override
         public void run() {
            zugUndPlanPanel.this.gleisModel.setZugOnBahnsteig(new NumString(this.bname), this.z);
         }
      };
      this.awtInvoke(zr, false);
   }

   @EventHandler
   public void zugUserText(ZugUserText event) {
      zug z = this.findZug(event.zid);
      if (z != null && !z.isMytrain()) {
         if (!event.text.isEmpty() && !z.getUserText().equalsIgnoreCase(event.text)) {
            this.my_main
               .uc
               .busPublish(
                  new IrcLine(
                     null, z.getSpezialName(), "Fdl Mitteilung: " + event.text + ", von " + event.sender + "<br>", new ZugClickEvent(z.getZID_num()), false
                  )
               );
         }

         z.setUserText(event.text, event.sender);
      }
   }

   @EventHandler
   public void zugUserText(ZugClickEvent event) {
      SwingUtilities.invokeLater(() -> this.selectZug(event.zid));
   }

   @Override
   public void close() {
      if (SwingUtilities.isEventDispatchThread()) {
         if (this.alleZuege != null) {
            for(zug z : this.alleZuege.values()) {
               z.close();
            }

            this.alleZuege.clear();
            this.gleisModel.clear();
            this.fahrplanMdl.clear();
         }
      } else {
         SwingUtilities.invokeLater(() -> this.close());
      }
   }

   public void clear() {
      this.alleZuege = new ConcurrentHashMap();
      this.seen_ZuegeByName = new ConcurrentHashMap();
      this.seen_ZuegeByZid = new ConcurrentHashMap();
   }

   void addReceived(stellwerksim_main.zugMsg zugMsg) {
      String m = "Zugmeldung: " + zugMsg.absender + " " + zugMsg.msg;
      this.my_main.message(m, stellwerksim_main.MSGLEVELS.IMPORTANT);
   }

   void addSent(stellwerksim_main.zugMsg zugMsg) {
   }

   void openWsw() {
      new WswWindow(this).setVisible(true);
   }

   private void initZugOnBahnsteig() {
      for(String bstg : this.alleBahnsteige.getAlleBahnsteig()) {
         NumString b = new NumString(bstg);
         this.gleisModel.addBahnsteig(b);
      }
   }

   public void refreshZug() {
      this.fahrplanTable.repaint();
      this.gleisTable.repaint();
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> this.fahrplanBox.refresh());
      } else {
         this.fahrplanBox.refresh();
      }
   }

   public boolean containsZug(int zid) {
      return this.alleZuege.containsKey(zid + "");
   }

   public boolean containsZug(String zid) {
      return this.alleZuege.containsKey(zid);
   }

   public zug findZug(int zid) {
      try {
         return (zug)this.alleZuege.get(zid + "");
      } catch (NullPointerException var3) {
         return null;
      }
   }

   public zug findZug(String zid) {
      try {
         return (zug)this.alleZuege.get(zid);
      } catch (NullPointerException var3) {
         return null;
      }
   }

   public zug findZugByShortName(String name) {
      zug ret = null;

      for(zug z : this.alleZuege.values()) {
         try {
            if (z.getSpezialName().compareToIgnoreCase(name) == 0) {
               ret = z;
               break;
            }
         } catch (ClassCastException var6) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "findZugByName caught", var6);
         }
      }

      return ret;
   }

   public zug findZugByFullName(String name) {
      zug ret = null;

      for(zug z : this.alleZuege.values()) {
         if (z.getName().compareToIgnoreCase(name) == 0) {
            ret = z;
            break;
         }
      }

      return ret;
   }

   public zug findZugByNameMatch(String name) {
      zug ret = null;
      if (name != null && name.length() > 1) {
         name = name.toLowerCase();

         for(zug z : this.alleZuege.values()) {
            if (z.getName().toLowerCase().indexOf(name) >= 0) {
               ret = z;
               break;
            }
         }
      }

      return ret;
   }

   public Collection<zug> findZugPointingMe(int zid) {
      LinkedList<zug> ret = new LinkedList();

      for(zug z : this.alleZuege.values()) {
         Iterator<zug> stops = z.getAllUnseenFahrplanzeilen();

         while(stops.hasNext()) {
            zug uz = (zug)stops.next();
            flagdata fd = uz.getFlags();
            if ((fd.hasFlag('K') || fd.hasFlag('F') || fd.hasFlag('E')) && Integer.parseInt(fd.getFlagdata()) == zid) {
               ret.add(uz);
            }
         }
      }

      return ret;
   }

   boolean searchZug(String search, boolean running) {
      RowSorter<? extends TableModel> Fsorter = this.fahrplanTable.getRowSorter();
      search = search.toLowerCase();
      int row = this.fahrplanTable.getSelectedRow();
      if (row < 0 && running) {
         row = 0;
      }

      for(int i = 0; i < this.fahrplanMdl.getRowCount(); ++i) {
         int ci = (i + row + (running ? 0 : 1)) % this.fahrplanMdl.getRowCount();
         int mi = Fsorter.convertRowIndexToModel(ci);
         zug z = this.fahrplanMdl.getZug(mi);
         if (z.getSpezialName().toLowerCase().indexOf(search) >= 0) {
            this.fahrplanTable.setRowSelectionInterval(ci, ci);
            this.fahrplanTable.scrollRectToVisible(this.fahrplanTable.getCellRect(ci, 0, true));
            return true;
         }
      }

      return false;
   }

   public void selectZug(zug z) {
      int i = this.fahrplanMdl.getIndexOf(z);
      if (i >= 0) {
         int i2 = this.fahrplanTable.convertRowIndexToView(i);
         this.fahrplanTable.setRowSelectionInterval(i2, i2);
         this.fahrplanTable.scrollRectToVisible(this.fahrplanTable.getCellRect(i2, 0, true));
      }
   }

   public void selectZug(int zid) {
      this.selectZug(this.findZug(zid));
   }

   public boolean haveWeSeenIt(zug z) {
      boolean ret = false;
      Date d1 = (Date)this.seen_ZuegeByName.get(z.getName());
      Date d2 = (Date)this.seen_ZuegeByZid.get(z.getZID());
      if (d1 != null || d2 != null) {
         GregorianCalendar cal1 = new GregorianCalendar();
         GregorianCalendar cal = new GregorianCalendar();
         if (d1 != null) {
            cal1.setTime(d1);
            cal1.add(1, 10);
            ret |= cal.before(cal1);
         }

         if (d2 != null) {
            cal1.setTime(d2);
            cal1.add(1, 10);
            ret |= cal.before(cal1);
         }
      }

      return ret;
   }

   public void updateZug(zug z) {
      zugUndPlanPanel.zugRunnable zr = new zugUndPlanPanel.zugRunnable(z) {
         @Override
         public void run() {
            if (zugUndPlanPanel.this.alleZuege.containsKey(this.z.getZID())) {
               zugUndPlanPanel.this.fahrplanMdl.updateZug(this.z);
               if (zugUndPlanPanel.this.my_main.uc.getDataSwitch().zugplan) {
                  ((TableRowSorter)zugUndPlanPanel.this.fahrplanTable.getRowSorter()).sort();
               }

               zugUndPlanPanel.this.gleisModel.updateZug(this.z);
            }

            if (zugUndPlanPanel.debugMode != null) {
               zugUndPlanPanel.debugMode.writeln("main/zug", "zuglist fire update");
            }
         }
      };
      this.awtInvoke(zr, false);
   }

   public zug addZug(zug z) {
      zugUndPlanPanel.zugRunnable zr = new zugUndPlanPanel.zugRunnable(z) {
         @Override
         public void run() {
            if (zugUndPlanPanel.this.alleZuege.containsKey(this.z.getZID())) {
               this.ret = (zug)zugUndPlanPanel.this.alleZuege.get(this.z.getZID());
               zugUndPlanPanel.this.fahrplanMdl.updateZug(this.z);
            } else {
               zugUndPlanPanel.this.alleZuege.put(this.z.getZID(), this.z);
               zugUndPlanPanel.this.fahrplanMdl.addZug(this.z);
               if (zugUndPlanPanel.debugMode != null) {
                  zugUndPlanPanel.debugMode.writeln("main/zug", "zuglist add/insert 3: (" + this.z.getName() + ")");
               }
            }
         }
      };
      this.awtInvoke(zr, true);
      return zr.ret;
   }

   public void hideZug(zug z) {
      this.seen_ZuegeByName.put(z.getName(), new Date());
      this.seen_ZuegeByZid.put(z.getZID(), new Date());
      zugUndPlanPanel.zugRunnable zr = new zugUndPlanPanel.zugRunnable(z) {
         @Override
         public void run() {
            if (zugUndPlanPanel.this.openFunk != null && zugUndPlanPanel.this.openFunk.getZug() == this.z) {
               zugUndPlanPanel.this.openFunk.close();
            }

            zugUndPlanPanel.this.fahrplanMdl.removeZug(this.z);
            if (zugUndPlanPanel.debugMode != null) {
               zugUndPlanPanel.debugMode.writeln("main/zug", "zuglist hide: " + this.z.getName());
            }
         }
      };
      this.awtInvoke(zr, false);
      this.finishText(z);
   }

   public void exchangeZug(zug z1, zug z2, int zid1, int zid2) {
      this.alleZuege.remove(zid1 + "");
      this.alleZuege.remove(zid2 + "");
      this.alleZuege.put(z1.getZID(), z1);
      this.alleZuege.put(z2.getZID(), z2);
   }

   public void renameZug(zug z, int oldzid, int newzid) {
      this.alleZuege.remove(oldzid + "");
      this.alleZuege.put(newzid + "", z);
   }

   public void delZug(zug z) {
      this.seen_ZuegeByName.put(z.getName(), new Date());
      this.seen_ZuegeByZid.put(z.getZID(), new Date());
      this.alleZuege.remove(z.getZID());
      zugUndPlanPanel.zugRunnable zr = new zugUndPlanPanel.zugRunnable(z) {
         @Override
         public void run() {
            zugUndPlanPanel.this.fahrplanMdl.removeZug(this.z);
            if (zugUndPlanPanel.debugMode != null) {
               zugUndPlanPanel.debugMode.writeln("main/zug", "zuglist remove: " + this.z.getName());
            }
         }
      };
      this.awtInvoke(zr, true);
      this.finishText(z);
      z.remove();
      z.close();
   }

   void dropZug(zug z) {
      this.delZug(z);
      z.successorRemove();
   }

   void sortTables() {
      if (this.my_main.uc.getDataSwitch().zugplan) {
         SwingUtilities.invokeLater(() -> ((TableRowSorter)this.fahrplanTable.getRowSorter()).sort());
      }
   }

   public Collection<zug> getZugList() {
      return Collections.unmodifiableCollection(this.alleZuege.values());
   }

   public void takeAll() {
      for(zug z : this.alleZuege.values()) {
         z.setMyTrain(true);
      }

      this.zuegePane.setBackground(UIManager.getDefaults().getColor("desktop"));
   }

   void autoMsgAction(ActionEvent evt) {
      String chn = ((JMenuItem)evt.getSource()).getActionCommand();

      for(int row : this.fahrplanTable.getSelectedRows()) {
         int selectedRow = this.fahrplanTable.convertRowIndexToModel(row);
         zug z = this.fahrplanMdl.getZug(selectedRow);
         if (z != null) {
            this.my_main.autoMsg(((JMenuItem)evt.getSource()).getText(), chn, z);
         }
      }
   }

   void updateMsgPopup() {
      Set<ChannelsNameParser.ChannelName> s = new TreeSet(this.my_main.getChat().channelsSet());
      this.autoMsgMenu.removeAll();

      for(ChannelsNameParser.ChannelName n : s) {
         JMenuItem mi = new JMenuItem(n.toString());
         mi.setActionCommand(n.name);
         mi.addActionListener(evt -> this.autoMsgAction(evt));
         this.autoMsgMenu.add(mi);
      }
   }

   void endQS() {
      this.zuegePane.setBackground(UIManager.getDefaults().getColor("desktop"));

      try {
         this.qsMenu1.setEnabled(false);
      } catch (NullPointerException var2) {
      }
   }

   boolean isInactive() {
      int stehtDummRum = 0;
      int sichtbar = 0;

      for(zug z : this.alleZuege.values()) {
         if (z.isVisible()) {
            ++sichtbar;
         }

         if (z.stehtDummRum()) {
            ++stehtDummRum;
         }
      }

      return sichtbar >= 4 && stehtDummRum > sichtbar / 4;
   }

   public StringBuffer zugReport(String r) {
      StringBuffer v = new StringBuffer("Zugreport:\n");
      if (!r.isEmpty() && !r.equalsIgnoreCase("visible") && !r.equalsIgnoreCase("mytrain")) {
         try {
            zug z = this.findZug(r);
            Vector st = z.getStructure();

            for(int i = 0; i < st.size() - 1; i += 2) {
               v.append(st.get(i)).append(':').append(st.get(i + 1)).append('\n');
            }
         } catch (Exception var7) {
         }
      } else {
         boolean visonly = r.equalsIgnoreCase("visible");
         boolean mytrain = r.equalsIgnoreCase("mytrain");

         for(zug z : this.alleZuege.values()) {
            if (!visonly && !mytrain || visonly && z.isVisible() || mytrain && z.isMytrain()) {
               v.append(z.getReport()).append('\n');
            }
         }
      }

      return v;
   }

   public void clicked(ChangeEvent e) {
      ButtonColorText b = (ButtonColorText)e.getSource();
      NumString n = (NumString)b.getData();
      new gleisBelegungDialog(this.my_main, n.getString(), this);
   }

   private void colorMarkerStateChanged(ItemEvent evt) {
      String cmd = ((JRadioButtonMenuItem)evt.getSource()).getActionCommand();

      for(int selectedRow : this.fahrplanTable.getSelectedRows()) {
         int i2 = this.fahrplanTable.convertRowIndexToModel(selectedRow);
         zug z = this.fahrplanMdl.getZug(i2);
         if (z != null) {
            z.setMarkColor(cmd);
            this.updateZug(z);
         }
      }
   }

   private void numMarkerStateChanged(ItemEvent evt) {
      String cmd = ((JRadioButtonMenuItem)evt.getSource()).getActionCommand();

      for(int selectedRow : this.fahrplanTable.getSelectedRows()) {
         int i2 = this.fahrplanTable.convertRowIndexToModel(selectedRow);
         zug z = this.fahrplanMdl.getZug(i2);
         if (z != null) {
            z.setMarkNum(cmd);
            this.updateZug(z);
         }
      }
   }

   private void addColMenu(String text, int command, int i) {
      this.colMenuItems[i] = new JRadioButtonMenuItem();
      this.colMarkerGroup.add(this.colMenuItems[i]);
      this.colMenuItems[i].setSelected(true);
      this.colMenuItems[i].setText(text);
      this.colMenuItems[i].setActionCommand(command + "");
      this.colMenuItems[i].setOpaque(true);
      this.colMenuItems[i].setAccelerator(KeyStroke.getKeyStroke(49 + i, 8));
      Color[] c = ColorText.getColorsOf(command);
      if (c[0] != null) {
         this.colMenuItems[i].setBackground(c[0]);
      }

      if (c[1] != null) {
         this.colMenuItems[i].setForeground(c[1]);
      }

      this.colMenuItems[i].addItemListener(evt -> this.colorMarkerStateChanged(evt));
      this.colMenu.add(this.colMenuItems[i]);
   }

   private void addNumMenu(int num, int i) {
      String t;
      String c;
      if (num == 0) {
         t = "-";
         c = "";
      } else {
         t = num + "";
         c = num + "";
      }

      this.numMenuItems[i] = new JRadioButtonMenuItem();
      this.numMarkerGroup.add(this.numMenuItems[i]);
      this.numMenuItems[i].setSelected(true);
      this.numMenuItems[i].setText(t);
      this.numMenuItems[i].setActionCommand(c);
      this.numMenuItems[i].setOpaque(true);
      this.numMenuItems[i].setAccelerator(KeyStroke.getKeyStroke(48 + i, 2));
      this.numMenuItems[i].addItemListener(evt -> this.numMarkerStateChanged(evt));
      this.numMenu.add(this.numMenuItems[i]);
   }

   void initZugmenu() {
      this.addColMenu("keine", 0, 0);
      this.addColMenu("rot", 1, 1);
      this.addColMenu("gelb", 4, 2);
      this.addColMenu("grün", 3, 3);
      this.addColMenu("violett", 6, 4);
      this.addColMenu("orange", 7, 5);
      this.addColMenu("türkis", 8, 6);
      this.addColMenu("hellgelb", 9, 7);

      for(int i = 0; i < 10; ++i) {
         this.addNumMenu(i, i);
      }

      JRootPane rp = this.my_main.getRootPane();
      this.bind(rp);
   }

   void bind(externalPanel ep) {
      this.bind(ep.getRootPane());
   }

   private void bind(JRootPane rp) {
      int i = 0;

      for(final JRadioButtonMenuItem c : this.colMenuItems) {
         rp.getInputMap(2).put(c.getAccelerator(), "colZug" + i);
         rp.getActionMap().put("colZug" + i, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               if (c.isEnabled()) {
                  c.setSelected(true);
               }
            }
         });
         ++i;
      }

      i = 0;

      for(final JRadioButtonMenuItem c : this.numMenuItems) {
         rp.getInputMap(2).put(c.getAccelerator(), "numZug" + i);
         rp.getActionMap().put("numZug" + i, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
               if (c.isEnabled()) {
                  c.setSelected(true);
               }
            }
         });
         ++i;
      }
   }

   private void awtInvoke(zugUndPlanPanel.zugRunnable zr, boolean sync) {
      if (SwingUtilities.isEventDispatchThread()) {
         zr.run();
      } else if (sync) {
         try {
            SwingUtilities.invokeAndWait(zr);
         } catch (InterruptedException var4) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "awtInvoke caught (1a)", var4);
            Logger.getLogger("stslogger").log(Level.SEVERE, "awtInvoke caught (2a)", var4.getCause());
         } catch (InvocationTargetException var5) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "awtInvoke caught (1b)", var5);
            Logger.getLogger("stslogger").log(Level.SEVERE, "awtInvoke caught (2b)", var5.getCause());
         }
      } else {
         SwingUtilities.invokeLater(zr);
      }
   }

   private void gleisTable_valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
         ListSelectionModel lsm = (ListSelectionModel)e.getSource();
         if (!lsm.isSelectionEmpty()) {
            int selectedRow = this.gleisTable.convertRowIndexToModel(lsm.getMinSelectionIndex());

            try {
               zug z = this.gleisModel.getZug(selectedRow);
               if (z != null) {
                  int i = this.fahrplanMdl.getIndexOf(z);
                  if (i >= 0) {
                     int i2 = this.fahrplanTable.convertRowIndexToView(i);
                     this.fahrplanTable.setRowSelectionInterval(i2, i2);
                     this.fahrplanTable.scrollRectToVisible(this.fahrplanTable.getCellRect(i2, 0, true));
                  }
               }
            } catch (Exception var7) {
            }
         }
      }
   }

   private void fahrplanTable_valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {
         this.fPopupLabel.setText("<html>&nbsp;</html>");
         if (this.fahrplanTable.getSelectedRow() == -1) {
            this.colMenu.setEnabled(false);
            this.numMenu.setEnabled(false);
            this.begruendungMenu.setEnabled(false);
            this.autoMsgMenu.setEnabled(false);
            if (this.qsMenu1 != null) {
               this.qsMenu1.setEnabled(false);
            }
         } else {
            int selectedRow = this.fahrplanTable.convertRowIndexToModel(this.fahrplanTable.getSelectedRow());
            zug z = this.fahrplanMdl.getZug(selectedRow);
            if (z != null) {
               this.begruendungMenu.setEnabled(this.fahrplanTable.getSelectedRowCount() == 1);
               if (this.qsMenu1 != null) {
                  this.qsMenu1.setEnabled(this.fahrplanTable.getSelectedRowCount() == 1);
               }

               if (this.fahrplanTable.getSelectedRowCount() == 1) {
                  this.fPopupLabel.setText("<html><b>" + z.getSpezialName() + "</b></html>");
               } else {
                  this.fPopupLabel.setText("<html><b>" + this.fahrplanTable.getSelectedRowCount() + " Züge</b></html>");
               }

               if (this.fahrplanTable_lastSelected != z) {
                  this.fahrplanTable_lastSelected = z;
                  this.showFahrplan(z);
                  this.colMenu.setEnabled(true);
                  this.numMenu.setEnabled(true);

                  for(JRadioButtonMenuItem ri : this.colMenuItems) {
                     if (ri.getActionCommand().equals(z.getMarkColor())) {
                        ri.setSelected(true);
                        break;
                     }
                  }

                  for(JRadioButtonMenuItem ri : this.numMenuItems) {
                     if (ri.getActionCommand().equals(z.getMarkNum())) {
                        ri.setSelected(true);
                        break;
                     }
                  }

                  this.gleisTable.getSelectionModel().clearSelection();
                  int i = this.gleisModel.getIndexOf(z);
                  if (i >= 0) {
                     int i2 = this.gleisTable.convertRowIndexToView(i);
                     this.gleisTable.setRowSelectionInterval(i2, i2);
                  }
               }
            }
         }
      }
   }

   public void showFahrplan(zug z) {
      this.fahrplanBox.showFahrplan(z);
   }

   public void showText(String t, TEXTTYPE type, Object reference, HyperlinkCaller caller) {
      this.infoBox.setText(t, type, reference, caller, this.my_main.getSimutimeString());
   }

   public void showText(String t, TEXTTYPE type, Object reference) {
      this.showText(t, type, reference, null);
   }

   public void finishText(Object reference) {
      this.infoBox.finishText(reference);
   }

   public void syncData(StringBuffer senddata) {
      this.zuegePane.setBackground(UIManager.getDefaults().getColor("Panel.background"));

      for(zug z : this.alleZuege.values()) {
         if (!z.isFertig()) {
            senddata.append(TextHelper.urlEncode("zid[]"));
            senddata.append('=');
            senddata.append(z.getZID_num());
            senddata.append('&');
         } else {
            this.delZug(z);
         }
      }
   }

   public bahnsteigDetailStore getBahnsteige() {
      return this.alleBahnsteige;
   }

   @Deprecated
   public AbstractTableModel getFahrplanModel() {
      return this.fahrplanMdl;
   }

   void setQSmenu() {
      this.qsMenu1 = new JMenuItem();
      this.qsMenu1.setText("QS: Meldung zum Zug schreiben");
      this.qsMenu1.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ListSelectionModel lsm = zugUndPlanPanel.this.fahrplanTable.getSelectionModel();
            int selectedRow = zugUndPlanPanel.this.fahrplanTable.convertRowIndexToModel(lsm.getMinSelectionIndex());
            zug z = zugUndPlanPanel.this.fahrplanMdl.getZug(selectedRow);
            if (z != null) {
               new qscomment(zugUndPlanPanel.this.my_main, z);
            }
         }
      });
      this.qsMenu1.setEnabled(false);
      this.fahrplanPopup.addSeparator();
      this.fahrplanPopup.add(this.qsMenu1);
   }

   public LinkedList<zugUndPlanPanel.gleisPlan> getBelegungsPlan(String bstg, int fromMins, int toMins) {
      long currenttime = this.my_main.getSimutime();
      long maxtime = currenttime + (long)toMins * 60000L;
      long mintime = currenttime - (long)fromMins * 60000L;
      LinkedList<zugUndPlanPanel.gleisPlan> ret = new LinkedList();

      for(zug z : this.alleZuege.values()) {
         if (!z.isFertig()) {
            zug.gleisData gd = z.getGleisDataOfGleis(bstg);
            if (gd != null) {
               if (gd.flags.hasFlag('E')) {
                  zug z2 = this.findZug(gd.flags.dataOfFlag('E'));
                  if (z2 != null) {
                     gd.ab = z2.getAn();
                  }
               } else if (gd.flags.hasFlag('K')) {
                  zug z2 = this.findZug(gd.flags.dataOfFlag('K'));
                  if (z2 != null && z2.getAn() > gd.ab) {
                     gd.ab = z2.getAn();
                  }
               }

               if (Math.min(gd.an, gd.an + (long)z.getVerspaetung_num() * 60000L) <= maxtime
                  && Math.max(gd.ab, gd.ab + (long)z.getVerspaetung_num() * 60000L) >= mintime) {
                  zugUndPlanPanel.gleisPlan gp = new zugUndPlanPanel.gleisPlan(z, gd);
                  ret.add(gp);
                  if (gd.flags.hasFlag('E')) {
                     zug z2 = this.findZug(gd.flags.dataOfFlag('E'));
                     if (z2 != null) {
                        gp.additionalText = "ändert Name zu " + z2.getSpezialName();
                     }
                  } else if (gd.flags.hasFlag('K')) {
                     zug z2 = this.findZug(gd.flags.dataOfFlag('K'));
                     if (z2 != null) {
                        gp.additionalText = "kuppelt an " + z2.getSpezialName();
                     }
                  } else if (gd.flags.hasFlag('F')) {
                     zug z2 = this.findZug(gd.flags.dataOfFlag('F'));
                     if (z2 != null) {
                        gp.additionalText = "flügelt zu " + z2.getSpezialName();
                     }
                  } else if (gd.flags.hasFlag('D')) {
                     gp.additionalText = "kein Halt";
                  }
               }
            }
         }
      }

      return ret;
   }

   void setScrollDataPanel(infoPanel dataPanel) {
      this.scrollerPanel.add(dataPanel, "Center");
   }

   void exit() {
      this.close();
   }

   private void defaultModel() {
      String layout = "(ROW (LEAF name=plan weight=0.3) (column weight=0.3 (LEAF name=zug weight=0.5) (leaf name=gleis weight=0.5)) (leaf weight=0.3 name=info)))";
      Node modelRoot = MultiSplitLayout.parseModel(
         "(ROW (LEAF name=plan weight=0.3) (column weight=0.3 (LEAF name=zug weight=0.5) (leaf name=gleis weight=0.5)) (leaf weight=0.3 name=info)))"
      );
      this.split.getMultiSplitLayout().setModel(modelRoot);
      this.split.getMultiSplitLayout().setLayoutByWeight(true);
   }

   private void initSplitComponent() {
      if (this.my_main.uc.getDataSwitch().multisplit) {
         this.split = new MultiSplitPane();
         this.add(this.split, "Center");
         this.split.setDividerPainter(new zugUndPlanPanel.dpainter());
         this.defaultModel();
         this.split.add(this.zuegePane, "plan");
         this.split.add(this.fahrplanPane, "zug");
         this.split.add(this.gleisPane, "gleis");
         this.split.add(this.infoPane, "info");
      } else {
         JPanel dummy = new JPanel();
         dummy.setLayout(new GridLayout(1, 0));
         dummy.add(this.zuegePane);
         dummy.add(this.fahrplanPane);
         dummy.add(this.gleisPane);
         dummy.add(this.infoPane);
         this.add(dummy, "Center");
      }
   }

   private JPanel embedScroll(JScrollPane p) {
      JPanel jp = new JPanel(new BorderLayout());
      jp.add(p, "Center");
      return jp;
   }

   private JPanel embedScroll(JPanel p) {
      return p;
   }

   public List<externalPanel> extractSplit(ActionListener closeAction, JSplitPane mainSplitPane, boolean dialog) {
      List<externalPanel> ret = new LinkedList();
      this.split.removeAll();
      this.remove(this.split);
      externalPanel ep = new externalPanel(this.my_main, closeAction, "Zugfolge", dialog);
      ep.setPanel(this.embedScroll(this.zuegePane), 500, 300);
      this.bind(ep);
      ret.add(ep);
      ep = new externalPanel(this.my_main, closeAction, "Fahrplan", dialog);
      ep.setPanel(this.embedScroll(this.fahrplanPane), 500, 300);
      ret.add(ep);
      ep = new externalPanel(this.my_main, closeAction, "Gleisbelegung", dialog);
      ep.setPanel(this.embedScroll(this.gleisPane), 500, 300);
      ret.add(ep);
      ep = new externalPanel(this.my_main, closeAction, "Info", dialog);
      ep.setPanel(this.embedScroll(this.infoPane), 500, 300);
      ret.add(ep);
      mainSplitPane.setBottomComponent(this.scrollerPanel);
      return ret;
   }

   public void restoreSplit(List<externalPanel> windows) {
      for(externalPanel ep : windows) {
         ep.rmPanel();
      }

      windows.clear();
      this.add(this.scrollerPanel, "North");
      this.initSplitComponent();
   }

   private void initMyComponents() {
      this.funkSendeButton.setToolTipText("PAUSE-Taste");
      this.funkSendeButton.getInputMap(2).put(KeyStroke.getKeyStroke(19, 0), "fpressed");
      this.funkSendeButton.getActionMap().put("fpressed", new AbstractAction() {
         public void actionPerformed(ActionEvent e) {
            ((JButton)e.getSource()).doClick();
         }
      });
      this.initSplitComponent();
      this.scrollerPanel.add(this.funkPane, "East");
      this.fahrplanTable.setDefaultRenderer(ColorText.class, new ColorRenderer(true));
      final FahrplanTableSorter Fsorter = new FahrplanTableSorter(this.fahrplanMdl);
      Fsorter.addRowSorterListener(new RowSorterListener() {
         public void sorterChanged(RowSorterEvent e) {
            List<? extends SortKey> l = Fsorter.getSortKeys();
            if (l == null || l.isEmpty()) {
               Logger.getLogger("stslogger").log(Level.SEVERE, "sorterChanged - no keys! " + e.getType());
            }
         }
      });

      for(int i = 0; i < this.fahrplanTable.getColumnCount(); ++i) {
         Fsorter.setComparator(i, ((fahrplanModel)Fsorter.getModel()).getComparator(i));
      }

      if (this.my_main.uc.getDataSwitch().zugplan) {
         this.fahrplanTable.setRowSorter(Fsorter);
         Fsorter.toggleSortOrder(((fahrplanModel)Fsorter.getModel()).getDefaultSortColumn());
         this.gleisTable.setDefaultRenderer(ColorText.class, new ColorRenderer());
         this.gleisTable.setDefaultRenderer(ButtonColorText.class, new ButtonColorRenderer());
         ButtonColorRenderer br = new ButtonColorRenderer();
         br.addButtonPressedListener(this);
         this.gleisTable.setDefaultEditor(ButtonColorText.class, br);
         TableRowSorter<gleisSortModel> Gsorter = new TableRowSorter(new gleisSortModel(this.gleisModel));
         Gsorter.setSortsOnUpdates(true);

         for(int i = 0; i < this.gleisTable.getColumnCount(); ++i) {
            Gsorter.setComparator(i, ((gleisSortModel)Gsorter.getModel()).getComparator(i));
         }

         this.gleisTable.setRowSorter(Gsorter);
         Gsorter.toggleSortOrder(((gleisSortModel)Gsorter.getModel()).getDefaultSortColumn());
         this.setColumnWidth(this.fahrplanTable, 1, "00:00", "Anfahrt");
         this.setColumnWidth(this.fahrplanTable, 2, "00:00", "Abfahrt");
         this.setColumnWidth(this.fahrplanTable, 6, "00:00 (+1001)", "Verspätung");
         this.setColumnWidth(this.gleisTable, 2, "00:00", "Abfahrt");
      } else {
         this.fahrplanTable.setModel(new DefaultTableModel());
         this.gleisTable.setModel(new DefaultTableModel());
      }

      this.infoBox.set2ndPanel(this.chatPanel);
   }

   public void timeQuery(bahnhofsUhr u) {
      if (!this.my_main.isPause()) {
         int h = (int)(this.my_main.getSimutime() / 3600000L);
         int m = (int)(this.my_main.getSimutime() / 60000L % 60L);
         int s = (int)(this.my_main.getSimutime() / 1000L % 60L);
         u.setTime(h, m, s);
      }
   }

   private void setColumnWidth(JTable tab, int col, String... text) {
      int w = 0;
      FontMetrics fm = tab.getFontMetrics(tab.getFont());

      for(String t : text) {
         w = Math.max(fm.stringWidth(t) + 12, w);
      }

      if (w > 0) {
         tab.getColumnModel().getColumn(col).setPreferredWidth(w);
         tab.getColumnModel().getColumn(col).setMaxWidth(w + 10);
         tab.getColumnModel().getColumn(col).setMinWidth(w - 10);
      }
   }

   private void initComponents() {
      this.fPopupLabel = new JLabel();
      this.fahrplanPopup = new JPopupMenu();
      this.fahrplanPopup.add(this.fPopupLabel);
      this.jSeparator2 = new Separator();
      this.begruendungMenu = new JMenuItem();
      this.jSeparator3 = new Separator();
      this.colMenu = new JMenu();
      this.numMenu = new JMenu();
      this.autoMsgMenu = new JMenu();
      this.colMarkerGroup = new ButtonGroup();
      this.numMarkerGroup = new ButtonGroup();
      this.zuegePane = new JScrollPane();
      this.fahrplanTable = new JTable();
      this.gleisPane = new JScrollPane();
      this.gleisTable = new JTable();
      this.funkPane = new JPanel();
      this.funkSendeButton = new JButton();
      this.infoPane = new JPanel();
      this.fahrplanPane = new JPanel();
      this.scrollerPanel = new JPanel();
      this.fPopupLabel.setBackground(UIManager.getDefaults().getColor("Button.foreground"));
      this.fPopupLabel.setForeground(UIManager.getDefaults().getColor("Button.background"));
      this.fPopupLabel.setHorizontalAlignment(0);
      this.fPopupLabel.setOpaque(true);
      this.fahrplanPopup.add(this.jSeparator2);
      this.begruendungMenu.setText("Verspätungsbegründung...");
      this.begruendungMenu.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            zugUndPlanPanel.this.begruendungMenuActionPerformed(evt);
         }
      });
      this.fahrplanPopup.add(this.begruendungMenu);
      this.fahrplanPopup.add(this.jSeparator3);
      this.colMenu.setText("Farbmarkierung");
      this.colMenu.setDoubleBuffered(true);
      this.colMenu.setEnabled(false);
      this.fahrplanPopup.add(this.colMenu);
      this.numMenu.setText("Numerische Markierung");
      this.numMenu.setEnabled(false);
      this.fahrplanPopup.add(this.numMenu);
      this.autoMsgMenu.setText("Numerische Markierung");
      this.autoMsgMenu.setEnabled(false);
      this.fahrplanPopup.add(this.autoMsgMenu);
      this.zuegePane.setBorder(BorderFactory.createTitledBorder(new TopLineBorder(), "Zugfolge", 0, 0, new Font("Dialog", 0, 9)));
      this.zuegePane.setFocusable(false);
      this.zuegePane.setFont(new Font("Dialog", 0, 10));
      this.fahrplanTable.setModel(this.fahrplanMdl);
      this.fahrplanTable.setComponentPopupMenu(this.fahrplanPopup);
      this.fahrplanTable.setFocusable(false);
      this.fahrplanTable.setSelectionMode(2);
      this.fahrplanTable.setShowHorizontalLines(false);
      this.fahrplanTable.setShowVerticalLines(false);
      ListSelectionModel rowSM = this.fahrplanTable.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            zugUndPlanPanel.this.fahrplanTable_valueChanged(e);
         }
      });
      this.zuegePane.setViewportView(this.fahrplanTable);
      this.gleisPane.setBorder(BorderFactory.createTitledBorder(new TopLineBorder(), "Gleisbelegung", 0, 0, new Font("Dialog", 0, 9)));
      this.gleisPane.setFocusable(false);
      this.gleisPane.setFont(new Font("Dialog", 0, 10));
      this.gleisTable.setModel(this.gleisModel);
      this.gleisTable.setFocusable(false);
      this.gleisTable.setSelectionMode(0);
      this.gleisTable.setShowHorizontalLines(false);
      this.gleisTable.setShowVerticalLines(false);
      ListSelectionModel rowGT = this.gleisTable.getSelectionModel();
      rowGT.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            zugUndPlanPanel.this.gleisTable_valueChanged(e);
         }
      });
      this.gleisTable.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent evt) {
            zugUndPlanPanel.this.gleisTableMouseClicked(evt);
         }
      });
      this.gleisPane.setViewportView(this.gleisTable);
      this.funkPane.setFont(new Font("Dialog", 0, 10));
      this.funkPane.setMinimumSize(new Dimension(260, 18));
      this.funkPane.setLayout(new BoxLayout(this.funkPane, 2));
      this.funkSendeButton.setFont(new Font("Dialog", 0, 10));
      this.funkSendeButton.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/broadcast12.png")));
      this.funkSendeButton.setText("Funk...");
      this.funkSendeButton.setToolTipText("Sende Auftrag ab");
      this.funkSendeButton.setFocusPainted(false);
      this.funkSendeButton.setFocusable(false);
      this.funkSendeButton.setHorizontalTextPosition(10);
      this.funkSendeButton.setMargin(new Insets(1, 8, 1, 8));
      this.funkSendeButton.setMaximumSize(new Dimension(70, 30));
      this.funkSendeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            zugUndPlanPanel.this.funkSendeButtonActionPerformed(evt);
         }
      });
      this.funkPane.add(this.funkSendeButton);
      this.infoPane.setBorder(BorderFactory.createTitledBorder(new TopLineBorder(), "Informationen & Funk", 0, 0, new Font("Dialog", 0, 9)));
      this.infoPane.setLayout(new BorderLayout());
      this.infoBox = new infoBoxPanel();
      this.infoPane.add(this.infoBox, "Center");
      this.fahrplanPane.setBorder(BorderFactory.createTitledBorder(new TopLineBorder(), "Fahrplan", 0, 0, new Font("Dialog", 0, 9)));
      this.fahrplanPane.setLayout(new BorderLayout());
      this.fahrplanBox = new zugFahrplanPanel(this);
      this.fahrplanPane.add(this.fahrplanBox, "Center");
      this.setBorder(new DropShadowBorder(false, false, true, true));
      this.setLayout(new BorderLayout());
      this.scrollerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
      this.scrollerPanel.setMinimumSize(new Dimension(0, 20));
      this.scrollerPanel.setLayout(new BorderLayout());
      this.add(this.scrollerPanel, "North");
   }

   private void funkSendeButtonActionPerformed(ActionEvent evt) {
      zug z = null;
      int unterzugAZid = 0;
      int selectedRow = this.fahrplanTable.getSelectedRow();
      if (selectedRow != -1 && this.fahrplanTable.getSelectedRowCount() == 1) {
         try {
            int i2 = this.fahrplanTable.convertRowIndexToModel(selectedRow);
            z = this.fahrplanMdl.getZug(i2);
         } catch (ArrayIndexOutOfBoundsException var10) {
         }
      }

      if (z != null) {
         unterzugAZid = this.fahrplanBox.getUnterzugAZid();
      }

      int POPUP_WIDTH = 450;
      int POPUP_HEIGHT = 400;
      this.openFunk = new funkMenu(new zugUndPlanPanel.funkAdapter(this.my_main), z, unterzugAZid);
      JScrollPane scroll = new JScrollPane();
      scroll.setViewportView(this.openFunk);
      scroll.setBackground(Color.WHITE);
      scroll.getViewport().setBackground(Color.WHITE);
      this.funkPopup.removeAll();
      this.funkPopup.add(scroll);
      this.funkPopup.revalidate();
      this.funkPopup.repaint();
      Point p = new Point(-450 + this.funkSendeButton.getWidth(), -400 + this.funkSendeButton.getHeight());
      SwingUtilities.convertPointToScreen(p, this.funkSendeButton);
      Rectangle screen = SwingTools.getScreenBounds(this.funkSendeButton);
      p.x = Math.max(p.x, screen.x);
      p.y = Math.max(p.y, screen.y);
      this.funkPopup.setLocation(p);
      this.funkPopup.setPopupSize(450, 400);
      this.funkPopup.show(this.funkSendeButton, -450 + this.funkSendeButton.getWidth(), -400);
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            zugUndPlanPanel.this.openFunk.updateLayout();
         }
      });
   }

   private void gleisTableMouseClicked(MouseEvent evt) {
      if (evt.getClickCount() == 2) {
         Point p = evt.getPoint();
         int row = this.gleisTable.convertRowIndexToModel(this.gleisTable.rowAtPoint(p));
         this.gleisModel.toggleGleisLower(row);
      }
   }

   private void begruendungMenuActionPerformed(ActionEvent evt) {
      ListSelectionModel lsm = this.fahrplanTable.getSelectionModel();
      int selectedRow = lsm.getMinSelectionIndex();
      if (selectedRow >= 0) {
         int i2 = this.fahrplanTable.convertRowIndexToModel(selectedRow);
         zug z = this.fahrplanMdl.getZug(i2);
         if (z != null) {
            if (this.visibleVerspBegr != null) {
               this.visibleVerspBegr.dispose();
               this.visibleVerspBegr = null;
            }

            if (z.getZID_num() > 0) {
               this.visibleVerspBegr = new VerspBegruendung(this.my_main, z);
            }
         }
      }
   }

   void setDumperP(FATwriter w) {
      this.dumperMode = w;
      if (this.dumperMode == null) {
         if (this.dumper_p != null) {
            try {
               this.dumper_p.getParent().remove(this.dumper_p);
            } catch (Exception var3) {
            }

            this.dumper_p = null;
         }
      } else {
         this.dumper_p = new JMenuItem();
         this.dumper_p.setText("Zugdump");
         this.dumper_p.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               ListSelectionModel lsm = zugUndPlanPanel.this.fahrplanTable.getSelectionModel();
               int selectedRow = zugUndPlanPanel.this.fahrplanTable.convertRowIndexToModel(lsm.getMinSelectionIndex());
               zug z = zugUndPlanPanel.this.fahrplanMdl.getZug(selectedRow);
               if (z != null && zugUndPlanPanel.this.dumperMode != null) {
                  zugUndPlanPanel.this.dumperMode.writeln(z);
               }
            }
         });
         this.fahrplanPopup.add(this.dumper_p);
      }
   }

   public Vector getStructInfo() {
      Vector ret = new Vector();

      for(zug z : this.alleZuege.values()) {
         try {
            Vector v = z.getStructInfo();
            ret.addElement(v);
         } catch (ClassCastException var5) {
            Logger.getLogger("stslogger").log(Level.SEVERE, "getStructInfo caught", var5);
         }
      }

      return ret;
   }

   private class dpainter extends DividerPainter {
      private dpainter() {
         super();
      }

      public void paint(Graphics g, Divider divider) {
         if (divider == zugUndPlanPanel.this.split.activeDivider() && !zugUndPlanPanel.this.split.isContinuousLayout()) {
            Graphics2D g2d = (Graphics2D)g;
            g2d.setColor(Color.black);
            g2d.fill(divider.getBounds());
         } else {
            Rectangle b = divider.getBounds();
            Graphics2D g2d = (Graphics2D)g.create(b.x, b.y, b.width, b.height);
            if (divider.isVertical()) {
               g2d.setColor(zugUndPlanPanel.this.getBackground().brighter());
               g2d.drawLine(0, 0, 0, b.height);
               g2d.setColor(zugUndPlanPanel.this.getBackground().darker());
               g2d.drawLine(b.width - 1, 0, b.width - 1, b.height);
            } else {
               g2d.setColor(zugUndPlanPanel.this.getBackground().brighter());
               g2d.drawLine(0, 0, b.width, 0);
               g2d.setColor(zugUndPlanPanel.this.getBackground().darker());
               g2d.drawLine(0, b.height - 1, b.width, b.height - 1);
            }

            g2d.dispose();
         }
      }
   }

   public class funkAdapter {
      private stellwerksim_main my_main;

      funkAdapter(stellwerksim_main m) {
         super();
         this.my_main = m;
      }

      public void close() {
         zugUndPlanPanel.this.funkPopup.setVisible(false);
         zugUndPlanPanel.this.openFunk = null;
      }

      public bahnsteigDetailStore alleBahnsteige() {
         return zugUndPlanPanel.this.alleBahnsteige;
      }

      public JComponent funkReferenceComponent() {
         return zugUndPlanPanel.this.funkPane;
      }

      public void showText(String s, TEXTTYPE t, Object reference) {
         this.my_main.showText(s, t, reference);
      }

      public void showFahrplan(zug z) {
         this.my_main.showFahrplan(z);
      }

      public gleisbildModelFahrweg my_gleisbild() {
         return zugUndPlanPanel.this.my_gleisbild;
      }

      public void playAnruf() {
         this.my_main.playAnruf();
      }

      public void playDingdong(int d) {
         this.my_main.playDingdong(d);
      }

      public void showText_replay(String s, Object reference) {
         this.my_main.showText_replay(s, reference);
      }

      public Iterator<zug> zugIterator() {
         return zugUndPlanPanel.this.alleZuege.values().iterator();
      }

      public JFrame getFrame() {
         return this.my_main;
      }

      public boolean isCaller() {
         return this.my_main.isCaller();
      }

      public boolean isOnline() {
         return this.my_main.isBotMode();
      }

      public boolean isExtraMode() {
         return this.my_main.isExtraMode();
      }

      public boolean isRedirectAllowedMode() {
         return this.my_main.isExtraMode() || this.my_main.isRedirectAllowedMode();
      }

      public void requestZugRedirect(zug z) {
         this.my_main.requestZugRedirect(z);
      }

      public void requestZugFahrweg(zug z) {
         this.my_main.requestZugFahrweg(z);
      }

      public void sendToBot(BOTCOMMAND cmd, String data) {
         this.my_main.send2Bot(cmd, data);
      }

      public boolean isDevSandbox() {
         return this.my_main.isDevSandbox();
      }

      public Simulator getSimulator() {
         return this.my_main;
      }
   }

   public static class gleisPlan implements Comparable {
      public final zug z;
      public final zug.gleisData gd;
      public String additionalText = "";

      private gleisPlan(zug z, zug.gleisData gd) {
         super();
         this.z = z;
         this.gd = gd;
      }

      public int compareTo(Object o) {
         zugUndPlanPanel.gleisPlan gp = (zugUndPlanPanel.gleisPlan)o;
         if (this.gd.ab != gp.gd.ab) {
            return this.gd.ab > gp.gd.ab ? 1 : -1;
         } else if (this.gd.an != gp.gd.an) {
            return this.gd.an > gp.gd.an ? 1 : -1;
         } else {
            return !this.gd.gleis.equals(gp.gd.gleis) ? this.gd.gleis.compareTo(gp.gd.gleis) : this.z.compareTo(gp.z);
         }
      }
   }

   private abstract class zugRunnable implements Runnable {
      zug z = null;
      String bname = null;
      zug ret = null;

      zugRunnable(zug _z) {
         super();
         this.z = _z;
      }

      zugRunnable(zug _z, String _bname) {
         super();
         this.z = _z;
         this.bname = _bname;
      }

      public abstract void run();
   }
}
