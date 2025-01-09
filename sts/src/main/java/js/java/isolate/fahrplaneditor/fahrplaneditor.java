package js.java.isolate.fahrplaneditor;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import js.java.isolate.sim.flagdata;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.adapter.AbstractTopFrame;
import js.java.schaltungen.adapter.closePrefs;
import js.java.schaltungen.moduleapi.ModuleObject;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.tools.TextHelper;
import js.java.tools.prefs;
import js.java.tools.balloontip.BalloonTip;
import js.java.tools.gui.SmoothViewport;
import js.java.tools.gui.SwingTools;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.textticker;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.gui.border.DropShadowBorder;

public class fahrplaneditor extends AbstractTopFrame implements planPanel.filterEventListener, SessionClose, ModuleObject {
   public static final int SIDERULERWIDTH = 20;
   public int errorcnt = 0;
   HashMap<Character, bahnhof> hasEKTM = new HashMap();
   HashMap<String, bahnhofHalt> hadEKF = new HashMap();
   private final ActionListener jumpMenuListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         fahrplaneditor.this.jumpMenuSelected((JMenuItem)e.getSource());
      }
   };
   private final prefs prefs;
   TreeSet<FPEaidData> aids = new TreeSet();
   TreeSet<String> regionen = new TreeSet();
   private final LinkedList<bahnhof> bhfs = new LinkedList();
   Font font_sans = new Font("SansSerif", 0, 11);
   Font font_mono = new Font("Monospaced", 0, 11);
   LinkedList<planLine> fahrplan = new LinkedList();
   private BalloonTip saveTip = null;
   private boolean checker_spool = false;
   public boolean checker_running = false;
   public boolean checker_stop = false;
   private textticker tipScroller;
   private JRadioButtonMenuItem allMarkerMenuItem;
   private JCheckBox showAll;
   private boolean exit = false;
   private ButtonGroup tmBg;
   private headingPanel zTitle;
   private boolean adding = false;
   private JButton addButton;
   private JMenu editMenu;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JPanel jPanel4;
   private JScrollPane jScrollPane2;
   private JMenu jumpMenu;
   private JProgressBar loadBar;
   private JMenuBar mainMenu;
   private JMenu markerMenu;
   private JButton saveButton;
   private JTextArea saveComment;
   private JButton sortButton;
   private JLabel startLabel;
   private JLabel statusLabel;
   private JPanel zPanel;
   private JScrollPane zScrollPane;
   private JLabel zugName;

   public void addScrollText(String string) {
      this.tipScroller.addImportantText(string);
   }

   void cleanSaveStatus() {
      this.bhfs.stream().forEach(b -> b.cleanSaveStatus());
   }

   private void adder() {
      HashMap<Integer, bahnhof> bhfmap = new HashMap();
      Iterator<planLine> it = this.fahrplan.iterator();
      this.checker_running = true;
      this.zPanel.removeAll();
      this.zPanel = new planPanel(20);
      this.zPanel.setLayout(new lineLayoutManager(-1, 20));
      this.zScrollPane.setViewportView(new SmoothViewport(this.zPanel));
      ((planPanel)this.zPanel).addFilterListener(this);

      while (it.hasNext()) {
         planLine p = (planLine)it.next();
         if (!bhfmap.containsKey(p.aaid)) {
            bahnhof lastB = this.addBhf(p.aaid);
            if (lastB != null) {
               bhfmap.put(p.aaid, lastB);
            }
         }

         if (bhfmap.containsKey(p.aaid)) {
            ((bahnhof)bhfmap.get(p.aaid)).addLine(p);
         }
      }

      for (bahnhof lastB : bhfmap.values()) {
         if (lastB.getAidData().isErlaubt()) {
            lastB.addBhf(true);
         }
      }

      this.zPanel.invalidate();
      this.zPanel.revalidate();
      this.zPanel.repaint();
      this.checker_running = false;
      EventQueue.invokeLater(new fahrplaneditor.checker());
   }

   private void sort() {
      this.checker_stop = true;
      TreeSet<bahnhof> sort = new TreeSet();

      for (bahnhof b : this.bhfs) {
         sort.add(b);
         b.sort();
      }

      this.bhfs.clear();
      this.zPanel.removeAll();
      this.jumpMenu.removeAll();

      for (bahnhof b : sort) {
         this.bhfs.add(b);
         this.zPanel.add(b);

         try {
            JMenuItem m = new JMenuItem(b.getAidData().getName());
            b.setMenu(m);
            m.addActionListener(this.jumpMenuListener);
            this.jumpMenu.add(m);
         } catch (Exception var5) {
         }
      }

      this.zPanel.revalidate();
      this.checker_stop = false;
      this.runChecker();
   }

   public void entriesChanged() {
      boolean canadd = this.bhfs.stream().noneMatch(b -> b.isEmpty());
      this.addButton.setEnabled(canadd);
   }

   public void runChecker() {
      this.checker_spool = true;
      this.startChecker();
   }

   @Override
   public void close() {
      this.exit = true;
      this.tipScroller.stopRunning();
   }

   @Override
   public void terminate() {
      this.closeEditor();
   }

   public void startChecker() {
      this.saveButton.setEnabled(this.errorcnt == 0);
      if (!this.checker_running && this.checker_spool && !this.exit) {
         this.checker_running = true;
         this.checker_spool = false;
         dataValidator d = new dataValidator(this);
         if (this.bhfs.isEmpty()) {
            this.errorcnt = 1;
         }

         d.execute();
      }
   }

   public void filterMarkers() {
      ((planPanel)this.zPanel).resetPos();
      String a = this.tmBg.getSelection().getActionCommand();
      char c = a.charAt(0);
      this.bhfs.stream().forEach(b -> b.filterMarker(c));
   }

   private void initMyComponents() {
      this.zTitle = new headingPanel(20);
      this.zScrollPane.setColumnHeaderView(this.zTitle);
      this.zugName.setText(this.getParameter("zname"));
      this.setTitle(this.getParameter("zname") + " - Fahrplaneditor - StellwerkSim");
      String prefix = this.getParameter("testprefix");
      if (prefix == null) {
         prefix = "";
      }

      int l = Integer.parseInt(this.getParameter(prefix + "plines"));

      for (int i = 0; i < l; i++) {
         planLine p = new planLine(this, i);
         this.fahrplan.add(p);
      }

      ActionListener mac = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            fahrplaneditor.this.filterMarkers();
         }
      };
      this.tmBg = new ButtonGroup();
      JRadioButtonMenuItem jm = new JRadioButtonMenuItem("alle Marker");
      this.allMarkerMenuItem = jm;
      jm.addActionListener(mac);
      jm.setActionCommand("*");
      this.tmBg.add(jm);
      jm.setSelected(true);
      this.markerMenu.add(jm);

      for (char c = 'A'; c <= 'Z'; c++) {
         jm = new JRadioButtonMenuItem(c + "");
         jm.addActionListener(mac);
         jm.setActionCommand(c + "");
         this.tmBg.add(jm);
         this.markerMenu.add(jm);
      }

      JMenu shiftMenu = new JMenu("Zeiten verschieben");
      int[] shifts = new int[]{-30, -10, -5, -1, 0, 1, 5, 10, 30};
      char c = '1';

      for (int s : shifts) {
         if (s == 0) {
            shiftMenu.add(new JSeparator());
         } else {
            JMenuItem mi = new JMenuItem((s > 0 ? "+" : "") + s + " Minute" + (Math.abs(s) != 1 ? "n" : ""));
            mi.setAccelerator(KeyStroke.getKeyStroke(c, 2));
            shiftMenu.add(mi);
            fahrplaneditor.ShiftListener smac = new fahrplaneditor.ShiftListener(s);
            mi.addActionListener(smac);
            c++;
         }
      }

      this.editMenu.add(shiftMenu);
      mac = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            ((planPanel)fahrplaneditor.this.zPanel).resetPos();
         }
      };
      this.editMenu.add(new JSeparator());
      JMenuItem mi = new JMenuItem("Filter aufheben");
      this.editMenu.add(mi);
      mi.setAccelerator(KeyStroke.getKeyStroke(68, 2));
      mi.addActionListener(mac);
      this.saveTip = new BalloonTip(this.saveComment);
      this.saveTip.setText("Zum Speichern muss ein Änderungkommentar eingegeben werden!");
      this.saveTip.setCloseButton(true);
      this.uc.addCloseObject(new fahrplaneditor.adderTimer());
      if (this.getParameter("simulate") != null) {
         this.saveButton.setToolTipText("Simulationsmodus, Speichern nicht möglich");
         BalloonTip saveSim = new BalloonTip(this.saveButton);
         saveSim.setText("Simulationsmodus, Speichern nicht möglich.");
         saveSim.setCloseButton(true);
         saveSim.setVisible(true);
      }

      this.tipScroller = new textticker();
      this.showAll = new JCheckBox();
      this.showAll.setText("Zeilen vollständig zeigen");
      this.showAll.setSelected(false);
      this.showAll.setFocusable(false);
      this.showAll.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            fahrplaneditor.this.showAllChanged(fahrplaneditor.this.showAll.isSelected());
         }
      });
      this.showAllChanged(false);
      JPanel mp = new JPanel();
      mp.setBorder(new DropShadowBorder(false, true, false, true));
      mp.setLayout(new BorderLayout());
      mp.add(this.showAll, "West");
      this.mainMenu.add(mp);
      this.tipScroller.addText("Speichern nur möglich, wenn alle gemeldeten Fehler behoben sind.");
      this.tipScroller.addText("Die kleinen Warndreiecke neben den Feldern melden Fehler.");
      this.tipScroller.addText("Drückt man mit der Maus auf eines der Warndreiecke kommt eine ausführliche Fehlermeldung und manchmal Lösungsvorschläge.");
      this.tipScroller.addText("Fehler werden an allen Stellen gemeldet, Lösungen aber nur an den Warnschildern des Feldes.");
      this.tipScroller.addText("Manche Lösungen sind nicht optimal, dann gibt es an einem anderen Feld eine bessere.");
      this.tipScroller.addText("Über die Schieber am link Rand kann man Stellwerke einschränken um so nur deren Zeiten über das Bearbeiten-Menü zu ändern.");
   }

   public void showFullLine(boolean s) {
      this.showAll.setSelected(s);
   }

   private void showAllChanged(boolean s) {
      planLayoutManager.setHiddenMode(!s);
      this.bhfs.stream().forEach(b -> b.updateLayout());
      this.zPanel.revalidate();
      this.zTitle.revalidate();
      this.zTitle.repaint();
   }

   private void addBhf() {
      if (!this.adding) {
         try {
            this.adding = true;
            bahnhof b = new bahnhof(this);
            this.bhfs.add(b);
            this.zPanel.add(b);
            this.zPanel.revalidate();
            JMenuItem m = new JMenuItem("neu");
            b.setMenu(m);
            m.addActionListener(this.jumpMenuListener);
            this.jumpMenu.add(m);
         } finally {
            this.entriesChanged();
            this.adding = false;
         }
      }
   }

   private bahnhof addBhf(int aid) {
      if (this.adding) {
         return null;
      } else {
         bahnhof var5;
         try {
            this.adding = true;
            FPEaidData a = null;

            for (FPEaidData d : this.aids) {
               if (d.getAid() == aid) {
                  a = d;
                  d.makeValid();
                  break;
               }
            }

            if (a == null) {
               return null;
            }

            bahnhof b = new bahnhof(this, a);
            this.bhfs.add(b);
            this.zPanel.add(b);
            this.zPanel.revalidate();
            JMenuItem m = new JMenuItem(a.getName());
            b.setMenu(m);
            m.addActionListener(this.jumpMenuListener);
            this.jumpMenu.add(m);
            var5 = b;
         } finally {
            this.adding = false;
         }

         return var5;
      }
   }

   LinkedList<bahnhof> getBhfs() {
      return (LinkedList<bahnhof>)this.bhfs.clone();
   }

   public void addRegion(String r) {
      if (!this.regionen.contains(r)) {
         this.regionen.add(r);
      }
   }

   void deleteBhf(bahnhof b) {
      if (b.getMenu() != null) {
         this.jumpMenu.remove(b.getMenu());
      }

      this.zPanel.remove(b);
      this.bhfs.remove(b);
      b.removed();
      this.zPanel.revalidate();
      this.zPanel.repaint();
      if (this.bhfs.isEmpty()) {
         this.errorcnt = 1;
      }

      this.runChecker();
   }

   public String getBhfDataUrl(int aid) {
      return this.getParameter("data") + "&aid=" + aid;
   }

   String getZugDataUrl(int zid, FPEaidData aid, flagdata flags) {
      return this.getParameter("zug")
         + "&zid="
         + zid
         + "&aid="
         + aid.getAid()
         + "&flags="
         + TextHelper.urlEncode(flags.getFlags())
         + "&flagdata="
         + TextHelper.urlEncode(flags.getFlagdata())
         + "&flagparam="
         + TextHelper.urlEncode(flags.getFlagparam());
   }

   public String getFahrplanUrl() {
      return this.getParameter("plan");
   }

   private void jumpMenuSelected(JMenuItem m) {
      System.out.println(m.getText());

      for (bahnhof b : this.bhfs) {
         if (b.getMenu() == m) {
            this.scrollZPanel(b.getBounds());
            b.jump();
            break;
         }
      }
   }

   public void showMessage(String msg) {
      this.addScrollText(msg);
      EventQueue.invokeLater(() -> this.statusLabel.setText(msg));
   }

   public void startLoad() {
      EventQueue.invokeLater(() -> {
         this.setCursor(new Cursor(3));
         this.addButton.setEnabled(false);
         this.saveButton.setEnabled(false);
         this.sortButton.setEnabled(false);
         this.loadBar.setIndeterminate(true);
         this.bhfs.stream().forEach(b -> b.setEnabled(false));
      });
   }

   public void setLoad(final int current, final int max) {
      EventQueue.invokeLater(new Runnable() {
         int rmax = max;
         int rcurrent = current;

         public void run() {
            fahrplaneditor.this.loadBar.setIndeterminate(false);
            fahrplaneditor.this.loadBar.setMaximum(this.rmax);
            fahrplaneditor.this.loadBar.setValue(this.rcurrent);
         }
      });
   }

   public void setLoad(final int current) {
      EventQueue.invokeLater(new Runnable() {
         int rcurrent = current;

         public void run() {
            fahrplaneditor.this.loadBar.setIndeterminate(false);
            fahrplaneditor.this.loadBar.setValue(this.rcurrent);
         }
      });
   }

   public void endLoad() {
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            fahrplaneditor.this.loadBar.setIndeterminate(false);
            fahrplaneditor.this.loadBar.setValue(0);
            fahrplaneditor.this.setCursor(new Cursor(0));
            fahrplaneditor.this.entriesChanged();
            fahrplaneditor.this.saveButton.setEnabled(fahrplaneditor.this.errorcnt == 0);
            fahrplaneditor.this.sortButton.setEnabled(true);
            fahrplaneditor.this.bhfs.stream().forEach(b -> b.setEnabled(true));
         }
      });
   }

   public fahrplaneditor(UserContext uc) {
      super(uc);
      uc.addCloseObject(this);
      this.setCursor(new Cursor(3));
      this.showMem("Start 1");
      String a = this.getParameter("aids");
      if (a != null) {
         StringTokenizer st = new StringTokenizer(a, ",");

         while (st.hasMoreTokens()) {
            String t = st.nextToken();
            int aid = Integer.parseInt(t);
            FPEaidData d = new FPEaidData(aid, this);
            this.aids.add(d);
         }
      }

      this.initComponents();
      this.initMyComponents();
      this.pack();
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.SIZE);
      this.prefs = new prefs("/org/js-home/stellwerksim/editor");
      System.out.println("Init call ended.");
      this.showMem("Start 2");
      this.setCursor(new Cursor(0));
      this.setVisible(true);
      SwingTools.toFront(this);
   }

   private void saveData() {
      StringBuffer data = new StringBuffer();
      this.saveData(data);
      data.append("kommentar=");
      data.append(TextHelper.urlEncode(this.saveComment.getText()));
      data.append('&');
      data.append("lastvalue=1&");
      saver s = new saver(this, this.getParameter("save"), data);
      if (this.getParameter("simulate") != null) {
         s.dump();
         JOptionPane.showMessageDialog(
            this, "Jetzt wären die Daten gespeichert worden.\nIm Simulation/Test Modus kann jedoch nicht gespeichert werden.", "Simulation", 1
         );
         s.exit();
      } else {
         s.save();
      }
   }

   private void saveData(StringBuffer data) {
      int counter = 0;
      synchronized (this.bhfs) {
         for (bahnhof b : this.bhfs) {
            counter = b.saveData(data, counter);
         }
      }
   }

   public void closeEditor() {
      this.tipScroller.stopRunning();
      this.dispose();
      this.uc.moduleClosed();
   }

   public void scrollZPanel(Rectangle rect) {
      this.zPanel.scrollRectToVisible(rect);
   }

   private void shiftBy(int minutes) {
      List<bahnhof> filtered = ((planPanel)this.zPanel).getSelected();

      for (bahnhof b : this.bhfs) {
         if ((filtered.isEmpty() || filtered.contains(b)) && b.isErlaubt()) {
            b.shiftMinutes(minutes);
         }
      }

      ((planPanel)this.zPanel).noresetPos();
   }

   @Override
   public void changed(boolean floating) {
      if (floating) {
         this.allMarkerMenuItem.setSelected(true);
      } else {
         List<bahnhof> filtered = ((planPanel)this.zPanel).getSelected();

         for (bahnhof b : this.bhfs) {
            if (filtered.isEmpty()) {
               b.fadeOff(false);
            } else if (filtered.contains(b)) {
               b.fade3Off(true);
            } else {
               b.fadeOff(true);
            }
         }
      }
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.zScrollPane = new JScrollPane();
      this.zPanel = new JPanel();
      this.startLabel = new JLabel();
      this.jPanel3 = new JPanel();
      this.zugName = new JLabel();
      this.addButton = new JButton();
      this.sortButton = new JButton();
      this.loadBar = new JProgressBar();
      this.jPanel2 = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      this.saveComment = new JTextArea();
      this.jPanel4 = new JPanel();
      this.saveButton = new JButton();
      this.statusLabel = new JLabel();
      this.mainMenu = new JMenuBar();
      this.jumpMenu = new JMenu();
      this.markerMenu = new JMenu();
      this.editMenu = new JMenu();
      this.setDefaultCloseOperation(0);
      this.setTitle("Fahrplaneditor");
      this.setLocationByPlatform(true);
      this.setPreferredSize(new Dimension(1000, 700));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            fahrplaneditor.this.formWindowClosed(evt);
         }

         public void windowClosing(WindowEvent evt) {
            fahrplaneditor.this.formWindowClosing(evt);
         }
      });
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanel1.setLayout(new BorderLayout());
      this.zPanel.setLayout(new BoxLayout(this.zPanel, 1));
      this.startLabel.setFont(this.startLabel.getFont().deriveFont((float)this.startLabel.getFont().getSize() + 15.0F));
      this.startLabel.setHorizontalAlignment(0);
      this.startLabel.setText("<html>Daten werden übertragen, bitte warten...</html>");
      this.zPanel.add(this.startLabel);
      this.zScrollPane.setViewportView(this.zPanel);
      this.jPanel1.add(this.zScrollPane, "Center");
      this.jPanel3.setLayout(new GridLayout(1, 0));
      this.zugName.setHorizontalAlignment(0);
      this.zugName.setText("zug");
      this.jPanel3.add(this.zugName);
      this.addButton.setText("weiteres Stellwerk");
      this.addButton.setEnabled(false);
      this.addButton.setFocusPainted(false);
      this.addButton.setFocusable(false);
      this.addButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fahrplaneditor.this.addButtonActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.addButton);
      this.sortButton.setText("sortieren");
      this.sortButton.setEnabled(false);
      this.sortButton.setFocusPainted(false);
      this.sortButton.setFocusable(false);
      this.sortButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fahrplaneditor.this.sortButtonActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.sortButton);
      this.jPanel3.add(this.loadBar);
      this.jPanel1.add(this.jPanel3, "South");
      this.getContentPane().add(this.jPanel1, "Center");
      this.jPanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanel2.setLayout(new BoxLayout(this.jPanel2, 1));
      this.jScrollPane2.setBorder(BorderFactory.createTitledBorder("Änderungskommentar"));
      this.saveComment.setColumns(20);
      this.saveComment.setRows(5);
      this.jScrollPane2.setViewportView(this.saveComment);
      this.jPanel2.add(this.jScrollPane2);
      this.jPanel4.setBackground(SystemColor.controlLtHighlight);
      this.jPanel4.setLayout(new GridLayout(1, 0));
      this.saveButton.setText("Fahrplan speichern");
      this.saveButton.setEnabled(false);
      this.saveButton.setOpaque(false);
      this.saveButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fahrplaneditor.this.saveButtonActionPerformed(evt);
         }
      });
      this.jPanel4.add(this.saveButton);
      this.statusLabel.setHorizontalAlignment(0);
      this.jPanel4.add(this.statusLabel);
      this.jPanel2.add(this.jPanel4);
      this.getContentPane().add(this.jPanel2, "South");
      this.jumpMenu.setText("Springen");
      this.mainMenu.add(this.jumpMenu);
      this.markerMenu.setText("zeige Themenmarker");
      this.mainMenu.add(this.markerMenu);
      this.editMenu.setText("Bearbeiten");
      this.mainMenu.add(this.editMenu);
      this.setJMenuBar(this.mainMenu);
   }

   private void addButtonActionPerformed(ActionEvent evt) {
      ((planPanel)this.zPanel).resetPos();
      this.addBhf();
      SwingUtilities.invokeLater(() -> this.scrollZPanel(((bahnhof)this.bhfs.getLast()).getBounds()));
   }

   private void saveButtonActionPerformed(ActionEvent evt) {
      ((planPanel)this.zPanel).resetPos();
      this.sort();
      String t = this.saveComment.getText();
      if (t.length() < 8) {
         this.saveTip.setVisible(true);
      } else {
         this.saveTip.setVisible(false);
         this.saveData();
      }
   }

   private void sortButtonActionPerformed(ActionEvent evt) {
      ((planPanel)this.zPanel).resetPos();
      this.sort();
   }

   private void formWindowClosed(WindowEvent evt) {
      this.closeEditor();
   }

   private void formWindowClosing(WindowEvent evt) {
      if (closePrefs.Parts.FAHRPLANEDITOR.ask(this.uc, this, "Wirklich Fahrplaneditor beenden?")) {
         this.dispose();
      }
   }

   private class ShiftListener implements ActionListener {
      int shift;

      ShiftListener(int shift) {
         this.shift = shift;
      }

      public void actionPerformed(ActionEvent e) {
         fahrplaneditor.this.shiftBy(this.shift);
      }
   }

   class adderTimer extends Timer implements ActionListener, SessionClose {
      adderTimer() {
         super(500, null);
         this.addActionListener(this);
         this.setRepeats(false);
         this.start();
      }

      public void actionPerformed(ActionEvent e) {
         fahrplaneditor.this.adder();
      }

      @Override
      public void close() {
         this.stop();
      }
   }

   class checker implements Runnable {
      public void run() {
         fahrplaneditor.this.entriesChanged();
         fahrplaneditor.this.runChecker();
      }
   }
}
