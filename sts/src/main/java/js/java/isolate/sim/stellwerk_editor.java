package js.java.isolate.sim;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.SoftBevelBorder;
import js.java.isolate.sim.eventsys.thema;
import js.java.isolate.sim.gleis.decor;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleisbild.CoordinatesEvent;
import js.java.isolate.sim.gleisbild.fw_doppelt_interface;
import js.java.isolate.sim.gleisbild.fw_doppelt_v2;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gleisbildModelStore;
import js.java.isolate.sim.gleisbild.gleisbildModelSts;
import js.java.isolate.sim.gleisbild.gleisbildViewPanel;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.gecWorker.gecBase;
import js.java.isolate.sim.gleisbild.gecWorker.gecDisplayEdit;
import js.java.isolate.sim.gleisbild.gecWorker.gecGBlockSelect;
import js.java.isolate.sim.gleisbild.gecWorker.gecGEdit;
import js.java.isolate.sim.gleisbild.gecWorker.gecGMassBlockSelect;
import js.java.isolate.sim.gleisbild.gecWorker.gecGSelect;
import js.java.isolate.sim.gleisbild.gecWorker.gecGSet;
import js.java.isolate.sim.gleisbild.gecWorker.gecGleisLine;
import js.java.isolate.sim.gleisbild.gecWorker.gecHLineSelect;
import js.java.isolate.sim.gleisbild.gecWorker.gecInsert;
import js.java.isolate.sim.gleisbild.gecWorker.gecVLineSelect;
import js.java.isolate.sim.panels.basePanel;
import js.java.isolate.sim.panels.blockcolorPanel;
import js.java.isolate.sim.panels.blockfillPanel;
import js.java.isolate.sim.panels.blockmassPanel;
import js.java.isolate.sim.panels.blockmovePanel;
import js.java.isolate.sim.panels.colorPanel;
import js.java.isolate.sim.panels.coordsPanel;
import js.java.isolate.sim.panels.createFWPanel;
import js.java.isolate.sim.panels.designtestCtrlPanel;
import js.java.isolate.sim.panels.designtestResultPanel;
import js.java.isolate.sim.panels.displayBarL;
import js.java.isolate.sim.panels.displayBarR;
import js.java.isolate.sim.panels.editFWPanel;
import js.java.isolate.sim.panels.emptyPanel;
import js.java.isolate.sim.panels.faultPanelL;
import js.java.isolate.sim.panels.faultPanelR;
import js.java.isolate.sim.panels.fwdoppeltPanel;
import js.java.isolate.sim.panels.fwrunPanel;
import js.java.isolate.sim.panels.gleisElementPanel;
import js.java.isolate.sim.panels.gleisElementPanelV2;
import js.java.isolate.sim.panels.gleisPropertyPanel;
import js.java.isolate.sim.panels.insertPanel;
import js.java.isolate.sim.panels.insertPreviewPanel;
import js.java.isolate.sim.panels.layerPanel;
import js.java.isolate.sim.panels.linePanel;
import js.java.isolate.sim.panels.lineeditPanel;
import js.java.isolate.sim.panels.listFWPanel;
import js.java.isolate.sim.panels.previewSimPanel;
import js.java.isolate.sim.panels.propertyPanel;
import js.java.isolate.sim.panels.savePanel;
import js.java.isolate.sim.panels.sizePanel;
import js.java.isolate.sim.panels.statusPanel;
import js.java.isolate.sim.panels.stoerungEditPanel;
import js.java.isolate.sim.panels.stoerungListPanel;
import js.java.isolate.sim.panels.structureAnalysesPanel;
import js.java.isolate.sim.panels.actionevents.coordsEvent;
import js.java.isolate.sim.panels.actionevents.fahrstrasseEvent;
import js.java.isolate.sim.panels.actionevents.progressEvent;
import js.java.isolate.sim.panels.actionevents.readUIEvent;
import js.java.isolate.sim.panels.actionevents.setUIEvent;
import js.java.isolate.sim.panels.actionevents.statusEvent;
import js.java.isolate.sim.panels.actionevents.warningEvent;
import js.java.isolate.sim.sim.GleisAdapterRouter;
import js.java.isolate.sim.sim.fsallocator;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.isolate.sim.toolkit.menuBorder;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.adapter.AbstractTopFrame;
import js.java.schaltungen.adapter.closePrefs;
import js.java.schaltungen.audio.AudioController;
import js.java.schaltungen.moduleapi.ModuleObject;
import js.java.schaltungen.moduleapi.SessionClose;
import js.java.schaltungen.moduleapi.SessionExit;
import js.java.schaltungen.timesystem.timedelivery;
import js.java.tools.prefs;
import js.java.tools.actions.AbstractEvent;
import js.java.tools.actions.AbstractListener;
import js.java.tools.actions.ListenerList;
import js.java.tools.gui.Awt;
import js.java.tools.gui.DropDownToggleButton;
import js.java.tools.gui.IsAwt;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.gui.animCard.AnimCardLayout;
import js.java.tools.gui.warningPopup.IconPopupButton;

public class stellwerk_editor extends AbstractTopFrame implements GleisAdapter, SessionExit, SessionClose, ModuleObject {
   private static final String PANEL_BLOCKCOLOR = "blockcolor";
   private static final String PANEL_BLOCKFILL = "blockfill";
   private static final String PANEL_BLOCKMASS = "blockmass";
   private static final String PANEL_BLOCKMOVE = "blockmove";
   private static final String PANEL_EMPTYL = "emptyL";
   private static final String PANEL_EMPTYR = "emptyR";
   private static final String PANEL_FWCREATE = "createfw";
   private static final String PANEL_FWEDIT = "editfw";
   private static final String PANEL_FWLIST = "listfw";
   private static final String PANEL_FWRUN = "fwrun";
   private static final String PANEL_GLEISELEMENTS = "gleiselements";
   private static final String PANEL_GLEISPROPERTIES = "gleisproperties";
   private static final String PANEL_LINEEDIT = "lineedit";
   private static final String PANEL_PROPERTY = "property";
   private static final String PANEL_SAVE = "save";
   public static final String PANEL_PREVIEWSIM = "previewsim";
   private static final String PANEL_SIZE = "size";
   private static final String PANEL_STATUS = "status";
   public static final String PANEL_WAITSTATUS = "waitstatus";
   private static final String PANEL_LAYER = "layers";
   private static final String PANEL_STRUCTUREANALYSES = "sanalysis";
   private static final String PANEL_COORDS = "coords";
   private static final String PANEL_LINE = "line";
   private static final String PANEL_COLOR = "color";
   private static final String PANEL_FWDOUBLES = "fwdoppelt";
   private static final String PANEL_INSERT_L = "insertblockL";
   private static final String PANEL_INSERT_R = "insertblockR";
   private static final String PANEL_STOERUNG_L = "stoerungList";
   private static final String PANEL_STOERUNG_R = "stoerungEdit";
   private static final String PANEL_DESIGN_L = "designctrl";
   private static final String PANEL_DESIGN_R = "designlist";
   private static final String PANEL_FAULT_L = "faultL";
   private static final String PANEL_FAULT_R = "faultR";
   private static final String PANEL_CONNECTOR_L = "displayBarL";
   private static final String PANEL_CONNECTOR_R = "displayBarR";
   private stellwerksim_main simulator = null;
   private int bcnt = 0;
   private final prefs prefs;
   private FATwriter debugOutput = null;
   private javax.swing.Timer pingTimer = null;
   private gleisbildViewPanel glbPanel = null;
   private gleisbildEditorControl glbControl = null;
   private IconPopupButton infoLabel;
   protected gleisbildModelSts glbModel;
   private final GleisAdapterRouter gadapter;
   private final fsallocator fsalloc;
   private boolean loading = false;
   public static final int LT_COORDS = 1;
   @Deprecated
   public static final int LT_BLOCKENABLED = 2;
   @Deprecated
   public static final int LT_RCENABLED = 3;
   public static final int LT_PROGRESS = 4;
   public static final int LT_SHOWFS = 5;
   public static final int LT_STATUS = 6;
   public static final int LT_RESIZE = 7;
   public static final int LT_FSCHANGED = 8;
   @Deprecated
   public static final int LT_INSERTED = 9;
   public static final int LT_INTERPANEL_COM = 10;
   @Deprecated
   public static final int LT_GLEISKLICK = 11;
   public static final int LT_SETUI = 12;
   public static final int LT_READUI = 13;
   private final HashMap<Integer, ListenerList<AbstractEvent>> listeners = new HashMap();
   private final HashMap<String, basePanel> myRpanels = new HashMap();
   private final HashMap<String, basePanel> myLpanels = new HashMap();
   private final HashMap<String, basePanel> allmypanels = new HashMap();
   private final ButtonGroup menuButtonGroup = new ButtonGroup();
   private final HashMap<String, JPanel> grouppanels = new HashMap();
   private final HashMap<String, DropDownToggleButton> dropdowns = new HashMap();
   private final HashMap<String, JToggleButton> allbuttons = new HashMap();
   private final HashMap<String, gecBase> gecMode = new HashMap();
   private JToggleButton firstButton = null;
   private final Insets BMARGIN = new Insets(4, 7, 4, 7);
   private basePanel lastLeft = null;
   private basePanel lastRight = null;
   private boolean overwriteLastStatus = false;
   private boolean nextAutoOK = true;
   private boolean alterDisabled = false;
   private JPanel buttonPanel;
   private JPanel controlPanel;
   private JPanel dataPanel;
   private JScrollPane gleisedscroller;
   private JLabel jLabel3;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JScrollPane jScrollPane1;
   private JSplitPane jSplitPane1;
   private JPanel leftPanel;
   private JButton okButton;
   private JPanel rightPanel;
   private JPanel waitPanel;
   private JPanel waitPanelContent;

   public stellwerk_editor(UserContext uc) {
      super(uc);
      uc.addCloseObject(this);
      this.setCursor(new Cursor(3));
      this.showMem("Start 1");
      thema.clear();
      fahrstrasse.fserror = false;
      decor.createDecor(uc);
      this.gadapter = new GleisAdapterRouter(this);
      this.fsalloc = new fsallocator(this.gadapter);
      new gleis(this, null);
      gleisTypContainer.setTypNames(this);
      if (this.getParameter("dev_output") != null && this.getParameter("dev_output").compareTo("true") == 0) {
         this.debugOutput = new FATwriter();
      }

      this.initComponents();
      this.timerGui();
      this.prefs = new prefs("/org/js-home/stellwerksim/editor");
      System.out.println("Init call ended.");
      this.showMem("Start 2");
      this.pack();
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.SIZE);
      this.setVisible(true);
      this.toFront();
   }

   @Override
   public void terminate() {
      if (this.simulator != null) {
         stellwerksim_main s = this.simulator;
         this.simulator = null;
         s.exit();
      }

      this.destroy();
   }

   @Override
   public void close() {
      this.glbModel = null;
      this.glbPanel = null;
      if (this.pingTimer != null) {
         this.pingTimer.stop();
         this.pingTimer = null;
      }

      if (this.infoLabel != null) {
         this.infoLabel.setBlinkEnabled(false);
      }

      this.listeners.clear();
      this.allbuttons.clear();
      this.allmypanels.clear();
      this.dropdowns.clear();
      this.gecMode.clear();
      this.gadapter.close();
      this.grouppanels.clear();
      this.lastLeft = null;
      this.lastRight = null;
      this.myLpanels.clear();
      this.myRpanels.clear();
      this.simulator = null;
   }

   private void timerGui() {
      boolean usetime = true;
      if (usetime) {
         javax.swing.Timer tm = new javax.swing.Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               System.out.println("Init phase 1");
               stellwerk_editor.this.mkGB();
               System.out.println("Init phase 2");
               stellwerk_editor.this.mkGui();
               System.out.println("Init phase 3");
            }
         });
         tm.setRepeats(false);
         tm.start();
      } else {
         EventQueue.invokeLater(new Runnable() {
            public void run() {
               System.out.println("Init phase 1");
               stellwerk_editor.this.mkGB();
               System.out.println("Init phase 2");
               stellwerk_editor.this.mkGui();
               System.out.println("Init phase 3");
            }
         });
      }
   }

   private void mkGui() {
      try {
         if (EventQueue.isDispatchThread()) {
            this.tipOfTheDay();
            this.leftPanel.setLayout(new AnimCardLayout());
            this.rightPanel.setLayout(new AnimCardLayout());
            this.initMyComponents();
         } else {
            EventQueue.invokeLater(new Runnable() {
               public void run() {
                  stellwerk_editor.this.tipOfTheDay();
                  stellwerk_editor.this.initMyComponents();
               }
            });
         }
      } catch (Exception var2) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var2);
      }
   }

   private void mkGB() {
      this.glbModel = new gleisbildModelSts(this);
      this.uc.addCloseObject(this.glbModel);
   }

   public void registerListener(int typ, AbstractListener l) {
      if (!this.listeners.containsKey(typ)) {
         this.listeners.put(typ, new ListenerList());
      }

      ListenerList ll = (ListenerList)this.listeners.get(typ);
      ll.addListener(l);
   }

   public void unregisterListener(int typ, AbstractListener l) {
      if (!this.listeners.containsKey(typ)) {
         this.listeners.put(typ, new ListenerList());
      }

      ListenerList<AbstractEvent> ll = (ListenerList<AbstractEvent>)this.listeners.get(typ);
      ll.removeListener(l);
   }

   private void addPanel(JPanel aPanel, HashMap<String, basePanel> mypanels) {
      for (String n : mypanels.keySet()) {
         basePanel b = (basePanel)mypanels.get(n);
         aPanel.add(b, n);
      }
   }

   private void placeInPanel(HashMap<String, basePanel> mypanels, String n) {
      if (this.allmypanels.containsKey(n)) {
         basePanel pn = (basePanel)this.allmypanels.get(n);
         mypanels.put(n, pn);
         if (pn instanceof SessionClose) {
            this.uc.addCloseObject((SessionClose)pn);
         }

         if (this.debugOutput != null) {
            this.debugOutput.writeln("placeInPanel", "added " + n);
         }
      } else {
         System.out.println("Panel key " + n + " unknown!");
      }
   }

   private JToggleButton addPanelButton(String group, String button, String name, String panelLeft, String panelRight, gecBase gec) {
      boolean addme = true;
      String comboName = panelLeft + ":" + panelRight + ":" + name;
      JToggleButton b;
      if (button.startsWith("|")) {
         String[] tb = button.split("\\|");
         DropDownToggleButton b2 = (DropDownToggleButton)this.dropdowns.get(tb[1]);
         if (b2 == null) {
            b2 = new DropDownToggleButton();
            b2.setMargin(this.BMARGIN);
            b2.setFocusPainted(false);
            this.dropdowns.put(tb[1], b2);
            this.bcnt++;
         } else {
            b2.setToolTipText("<html>Über Mausklick auf den Pfeil können<br>weitere Funktionen erreicht werden.</html>");
            addme = false;
         }

         b2.addItem(tb[2], comboName);
         if (this.debugOutput != null) {
            this.debugOutput.writeln("addPanelButton", "added item '" + button + "' " + comboName + "'");
         }

         b = b2;
      } else {
         b = new JToggleButton();
         b.setText(button);
         b.setActionCommand(comboName);
         b.setMargin(this.BMARGIN);
         if (this.debugOutput != null) {
            this.debugOutput.writeln("addPanelButton", "added '" + button + "' " + comboName + "'");
         }

         this.bcnt++;
         this.allbuttons.put(name, b);
      }

      b.setFocusPainted(false);
      b.setFocusable(false);
      this.gecMode.put(comboName, gec);
      if (addme) {
         this.menuButtonGroup.add(b);
         b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
               stellwerk_editor.this.cardButtonActionPerformed(evt);
            }
         });
         if (group == null) {
            this.buttonPanel.add(b);
         } else {
            JPanel pan = (JPanel)this.grouppanels.get(group);
            if (pan == null) {
               pan = new JPanel();
               pan.setLayout(new FlowLayout(0, 2, 0));
               this.buttonPanel.add(pan);
               this.grouppanels.put(group, pan);
               pan.setBorder(new menuBorder(group));
            }

            pan.add(b);
         }
      }

      return b;
   }

   private void cardButtonActionPerformed(ActionEvent evt) {
      String cmd = ((JToggleButton)evt.getSource()).getActionCommand();
      String[] c = cmd.split(":");
      if (this.debugOutput != null) {
         this.debugOutput.writeln("cardButtonActionPerformed", "clicked '" + cmd + "'");
      }

      if (c.length > 2 && c[2] != null) {
         if (this.lastLeft != null && this.allmypanels.containsKey(c[0]) && this.lastLeft != this.allmypanels.get(c[0])) {
            this.lastLeft.hidden(this.glbControl.getMode());
         }

         if (this.lastRight != null && this.allmypanels.containsKey(c[1]) && this.lastRight != this.allmypanels.get(c[1])) {
            this.lastRight.hidden(this.glbControl.getMode());
         }
      }

      this.glbControl.setMode((gecBase)this.gecMode.get(cmd));
      if (c.length > 2 && c[2] != null) {
         if (this.allmypanels.containsKey(c[0])) {
            ((basePanel)this.allmypanels.get(c[0])).shown(c[2], this.glbControl.getMode());
            this.lastLeft = (basePanel)this.allmypanels.get(c[0]);
         } else {
            System.out.println("ERROR L: " + c[0] + " missing (" + c[2] + ")");
         }

         if (this.allmypanels.containsKey(c[1])) {
            ((basePanel)this.allmypanels.get(c[1])).shown(c[2], this.glbControl.getMode());
            this.lastRight = (basePanel)this.allmypanels.get(c[1]);
         } else {
            System.out.println("ERROR R: " + c[1] + " missing (" + c[2] + ")");
         }
      }

      ((CardLayout)this.leftPanel.getLayout()).show(this.leftPanel, c[0]);
      ((CardLayout)this.rightPanel.getLayout()).show(this.rightPanel, c[1]);
      this.leftPanel.requestFocus();
      this.glbModel.repaint();
   }

   private void showDataPanel() {
      this.setPanelInvisible(false);
      ((CardLayout)this.controlPanel.getLayout()).show(this.controlPanel, "data");
      this.showMem("running");
   }

   public void showControlPanel() {
      this.showDataPanel();
   }

   public void showWaitPanel(String m, boolean okEnabled) {
      this.showWaitPanel(m);
      this.okButton.setEnabled(okEnabled);
   }

   private void showWaitPanel(String m) {
      ((CardLayout)this.controlPanel.getLayout()).show(this.controlPanel, "wait");
      ((CardLayout)this.waitPanelContent.getLayout()).show(this.waitPanelContent, m);
   }

   private void showWaitPanel() {
      this.showWaitPanel("waitstatus");
   }

   private void initMyComponents() {
      this.glbControl = new gleisbildEditorControl(this, this.uc);
      this.glbPanel = new gleisbildViewPanel(this.uc, this.glbControl, this.glbModel);
      this.showDataPanel();
      this.glbControl.addCoordinatesListener(new AbstractListener<CoordinatesEvent>() {
         public void action(CoordinatesEvent e) {
            stellwerk_editor.this.showCoords(e.getX(), e.getY());
         }
      });
      this.allmypanels.put("emptyL", new emptyPanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "emptyL");
      this.allmypanels.put("emptyR", new emptyPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "emptyR");
      this.allmypanels.put("save", new savePanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "save");
      this.allmypanels.put("size", new sizePanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "size");
      this.allmypanels.put("property", new propertyPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "property");
      this.allmypanels.put("previewsim", new previewSimPanel(this.glbControl, this));
      this.waitPanelContent.add((Component)this.allmypanels.get("previewsim"), "previewsim");
      this.allmypanels.put("status", new statusPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "status");
      this.allmypanels.put("layers", new layerPanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "layers");
      this.allmypanels.put("sanalysis", new structureAnalysesPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "sanalysis");
      this.allmypanels.put("waitstatus", new statusPanel(this.glbControl, this));
      this.waitPanelContent.add((Component)this.allmypanels.get("waitstatus"), "waitstatus");
      this.allmypanels.put("fwrun", new fwrunPanel(this.glbControl, this));
      this.waitPanelContent.add((Component)this.allmypanels.get("fwrun"), "fwrun");
      this.allmypanels.put("fwdoppelt", new fwdoppeltPanel(this.glbControl, this));
      this.waitPanelContent.add((Component)this.allmypanels.get("fwdoppelt"), "fwdoppelt");
      this.allmypanels.put("listfw", new listFWPanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "listfw");
      this.allmypanels.put("createfw", new createFWPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "createfw");
      this.allmypanels.put("editfw", new editFWPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "editfw");
      if (this.getParameter("gelements:blocks") != null) {
         this.allmypanels.put("gleiselements", new gleisElementPanelV2(this.glbControl, this));
      } else {
         this.allmypanels.put("gleiselements", new gleisElementPanel(this.glbControl, this));
         System.out.println("legacy: Element-Panel V1");
      }

      this.placeInPanel(this.myLpanels, "gleiselements");
      this.allmypanels.put("gleisproperties", new gleisPropertyPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "gleisproperties");
      this.allmypanels.put("faultL", new faultPanelL(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "faultL");
      this.allmypanels.put("faultR", new faultPanelR(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "faultR");
      this.allmypanels.put("displayBarL", new displayBarL(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "displayBarL");
      this.allmypanels.put("displayBarR", new displayBarR(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "displayBarR");
      this.allmypanels.put("coords", new coordsPanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "coords");
      this.allmypanels.put("line", new linePanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "line");
      this.allmypanels.put("color", new colorPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "color");
      this.allmypanels.put("lineedit", new lineeditPanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "lineedit");
      this.allmypanels.put("blockfill", new blockfillPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "blockfill");
      this.allmypanels.put("blockmove", new blockmovePanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "blockmove");
      this.allmypanels.put("blockcolor", new blockcolorPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "blockcolor");
      this.allmypanels.put("blockmass", new blockmassPanel(this.glbControl, this));
      this.placeInPanel(this.myRpanels, "blockmass");
      this.allmypanels.put("insertblockL", new insertPanel(this.glbControl, this));
      this.placeInPanel(this.myLpanels, "insertblockL");
      this.allmypanels.put("insertblockR", new insertPreviewPanel(this.glbControl, this, this.uc));
      this.placeInPanel(this.myRpanels, "insertblockR");
      if (this.getParameter("enable_stoerungpanel") != null && this.getParameter("enable_stoerungpanel").compareTo("true") == 0) {
         this.allmypanels.put("stoerungList", new stoerungListPanel(this.glbControl, this));
         this.placeInPanel(this.myLpanels, "stoerungList");
         this.allmypanels.put("stoerungEdit", new stoerungEditPanel(this.glbControl, this));
         this.placeInPanel(this.myRpanels, "stoerungEdit");
      }

      if (this.getParameter("enable_designtestpanel") != null && this.getParameter("enable_designtestpanel").compareTo("true") == 0) {
         this.allmypanels.put("designctrl", new designtestCtrlPanel(this.glbControl, this));
         this.placeInPanel(this.myLpanels, "designctrl");
         this.allmypanels.put("designlist", new designtestResultPanel(this.glbControl, this));
         this.placeInPanel(this.myRpanels, "designlist");
      }

      this.addPanel(this.leftPanel, this.myLpanels);
      this.addPanel(this.rightPanel, this.myRpanels);
      this.gleisedscroller.setViewportView(this.glbPanel);
      this.glbModel.gl_resize(10, 10);
      if (this.getParameter("dev_noload") == null || this.getParameter("dev_noload").compareTo("true") != 0) {
         if (this.getParameter("anlagenlesen") != null) {
            this.setGUIEnable(false);
            this.setPanelInvisible(true);
            this.loading = true;
            this.glbModel.load(this.getParameter("anlagenlesen"), new gleisbildModelStore.ioDoneMessage() {
               @Override
               public void done(boolean success) {
                  stellwerk_editor.this.loading = false;
                  stellwerk_editor.this.setGUIEnable(true);
               }
            });
         } else {
            this.setCursor(new Cursor(0));
            this.glbModel.gl_resize(100, 50);
         }
      }

      this.firstButton = this.addPanelButton("Gleisbild", "Datei", null, "save", "status", null);
      if (this.getParameter("enable_faultpanel") != null && this.getParameter("enable_faultpanel").compareTo("true") == 0) {
         this.addPanelButton("Gleisbild", "Fehleranalyse", "fault", "faultL", "faultR", new gecGSelect());
      }

      if (this.getParameter("enable_propertypanel") != null && this.getParameter("enable_propertypanel").compareTo("true") == 0) {
         this.addPanelButton("Gleisbild", "|glbaddons|Eigenschaften", "prop", "size", "property", null);
      } else {
         this.addPanelButton("Gleisbild", "|glbaddons|Eigenschaften", "prop", "size", "emptyR", null);
      }

      if (this.getParameter("enable_stoerungpanel") != null && this.getParameter("enable_stoerungpanel").compareTo("true") == 0) {
         this.addPanelButton("Gleisbild", "Störungen", "stoerung", "stoerungList", "stoerungEdit", new gecGSelect());
      }

      this.addPanelButton("Gleise", "|gedit|bearbeiten", "edit", "gleiselements", "gleisproperties", new gecGEdit());
      this.addPanelButton("Gleise", "|gedit|setzen", "set", "gleiselements", "emptyR", new gecGSet());
      if (this.getParameter("enable_linepanel") != null && this.getParameter("enable_linepanel").compareTo("true") == 0) {
         this.addPanelButton("Gleise", "Linie", "line", "line", "color", new gecGleisLine());
      }

      if (this.getParameter("enable_connectorpanel") != null && this.getParameter("enable_connectorpanel").compareTo("true") == 0) {
         this.addPanelButton("Gleise", "|gedit|Displayverdrahtung", "connector", "displayBarL", "displayBarR", new gecDisplayEdit());
      }

      this.addPanelButton("Einfügen", "|colrow|Zeile", "row", "lineedit", "emptyR", new gecHLineSelect());
      this.addPanelButton("Einfügen", "|colrow|Spalte", "column", "lineedit", "emptyR", new gecVLineSelect());
      this.addPanelButton("Einfügen", "Baugruppen", "insertblock", "insertblockL", "insertblockR", new gecInsert());
      this.addPanelButton("Block", "|blockcontent|bewegen&färben", "move", "blockmove", "blockcolor", new gecGBlockSelect());
      this.addPanelButton("Block", "|blockcontent|Inhalt", "fill", "gleiselements", "blockfill", new gecGBlockSelect());
      this.addPanelButton("Block", "Maßstab", "mass", "coords", "blockmass", new gecGMassBlockSelect());
      this.addPanelButton("Fahrstraßen", "verwalten", "create", "listfw", "createfw", new gecGSelect());
      this.addPanelButton("Fahrstraßen", "bearbeiten", "set", "listfw", "editfw", new gecGSelect());
      if (this.getParameter("enable_designtestpanel") != null && this.getParameter("enable_designtestpanel").compareTo("true") == 0) {
         this.addPanelButton("Gleisbild", "|glbaddons|Designtest", "designtest", "designctrl", "designlist", null);
         if (this.getParameter("enable_sanalysispanel") != null && this.getParameter("enable_sanalysispanel").compareTo("true") == 0) {
            this.addPanelButton("Gleisbild", "|glbaddons|Ebenen & Umfeld", "layers", "layers", "sanalysis", new gecGSelect());
         } else {
            this.addPanelButton("Gleisbild", "|glbaddons|Ebenen & Umfeld", "layers", "layers", "emptyR", new gecGSelect());
         }
      }

      this.infoLabel = new IconPopupButton();
      this.infoLabel.setBlinkEnabled(true);
      this.buttonPanel.add(this.infoLabel);
      this.firstButton.doClick();
      this.invalidate();
      this.pingTimer = new javax.swing.Timer(600000, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            stellwerk_editor.this.glbModel.ping(stellwerk_editor.this.getParameter("anlagenschreiben"));
         }
      });
      this.pingTimer.start();
   }

   @Override
   public timedelivery getTimeSystem() {
      return this.simulator;
   }

   @Override
   public AudioController getAudio() {
      return this.uc.getAudio();
   }

   @Override
   public Simulator getSim() {
      return null;
   }

   @Override
   public prefs getSimPrefs() {
      return null;
   }

   @Override
   public void exit() {
      if (this.simulator != null) {
         this.simulator.setVisible(false);
         this.simulator = null;
         this.setVisible(true);
         this.toFront();
         this.gadapter.drop();
         this.glbControl.setPausePainter(false);
         this.glbModel.clearStatus();
         JOptionPane.showMessageDialog(
            this,
            "<html>Achtung! Der Editor sollte beendet und neu gestartet werden,<br>der eingebaute Zugemitter kann zu Datenfehlern geführt haben!<p><b>Das Gleisbild sollte unter keinen Umständen jetzt gespeichert werden</b></html>",
            "Achtung",
            2
         );
         this.setCursor(new Cursor(0));
         this.nextAutoOK = true;
         this.setEnabled(true);
         this.setGUIEnable(true);
         this.glbPanel.repaint();
      }
   }

   public boolean startEmitter() {
      this.setCursor(new Cursor(3));

      try {
         if (this.simulator != null) {
            this.simulator.setVisible(true);
            return true;
         }

         this.setPanelInvisible(true);
         int r = JOptionPane.showConfirmDialog(
            this,
            "<html>Dies öffnet den Zugemitter.<p>Die kann zum <b>Verlust</b> der <b>ungesicherten</b> Daten führen.</html>",
            "Achtung, Zugemitter Warnung",
            2,
            2
         );
         if (r == 0) {
            JOptionPane.showMessageDialog(
               this,
               "<html>Für den Zugemitter über den Editor gilt:<ul><li>es gibt z.Z. keinen Ton.<li>bei fehlerhafter Fahrstraßen kommt es zu Störungen.<li>manuelle Autofahrstrassen funktionieren nicht immer<li>nicht beendete Störungen führen zur Blockade des Störungssystems<li>nachträglich verbaute ZD-Signale werden ohne FS-Lauf nicht sauber erkannt</ul></html>",
               "Hinweis",
               1
            );
            r = 0;
            if (fahrstrasse.fserror) {
               r = JOptionPane.showConfirmDialog(
                  this,
                  "<html>Beim Laden wurden fehlerhafte Fahrstraßen gefunden. Diese sollten<br><b>vor Start</b> des Zugemitters neu berechnet werden.<p><i>Soll der Zugemitter trotzdem gestartet werden?</i></html>",
                  "Fahrstraßenfehler",
                  2,
                  2
               );
            }

            if (r == 0) {
               this.setGUIEnable(false);
               this.setEnabled(false);
               this.glbControl.setPausePainter(true);
               this.glbModel.clearStatus();
               this.uc.overrideModuleClose(this);
               this.simulator = new stellwerksim_main(this.uc, this.glbModel, this.getParameter("running"), true);
               this.gadapter.add(this.simulator);
               this.simulator.setVisible(true);
               this.simulator.toFront();
               this.setVisible(false);
               return true;
            }
         }

         this.setPanelInvisible(false);
      } catch (Exception var2) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "Emitter start", var2);
         JOptionPane.showMessageDialog(this, "Der ZugEmitter konnte nicht gestartet werden!\n" + var2.getMessage(), "Problem", 0);
         if (this.simulator != null) {
            this.simulator.exit();
         } else {
            this.nextAutoOK = true;
            this.setEnabled(true);
            this.setGUIEnable(true);
         }
      }

      this.setCursor(new Cursor(0));
      return false;
   }

   @Awt
   @Override
   public void showStatus(final String s, final int type) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.showStatusAWT(s, type);
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               stellwerk_editor.this.showStatusAWT(s, type);
            }
         });
      }
   }

   @Override
   public void showStatus(String s) {
      this.showStatus(s, 0);
   }

   @IsAwt
   private void showStatusAWT(String s, int type) {
      if (s != null) {
         ListenerList<AbstractEvent> ll = (ListenerList<AbstractEvent>)this.listeners.get(6);
         if (ll != null) {
            ll.fireEvent(new statusEvent(s, type));
         }

         GregorianCalendar cal = new GregorianCalendar();
         DateFormat df = DateFormat.getTimeInstance(2);
         if (s.startsWith("-")) {
            this.overwriteLastStatus = true;
            s = s.substring(1);
         } else {
            this.overwriteLastStatus = false;
         }

         if (!this.overwriteLastStatus) {
            System.out.println(df.format(cal.getTime()) + ": " + s);
         }

         if (type == 1 || type == 4) {
            this.nextAutoOK = false;
         }

         if (type == 5) {
            JOptionPane.showMessageDialog(this, s, "Achtung", 2);
         }
      }
   }

   @IsAwt
   private void showCoords(int x, int y) {
      ListenerList<AbstractEvent> ll = (ListenerList<AbstractEvent>)this.listeners.get(1);
      if (ll != null) {
         ll.fireEvent(new coordsEvent(x, y));
      }
   }

   @Awt
   @Override
   public void setProgress(final int pp) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.setProgressAWT(pp);
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               stellwerk_editor.this.setProgressAWT(pp);
            }
         });
      }
   }

   @IsAwt
   private void setProgressAWT(int pp) {
      ListenerList<AbstractEvent> ll = (ListenerList<AbstractEvent>)this.listeners.get(4);
      if (ll != null) {
         ll.fireEvent(new progressEvent(pp));
      }
   }

   @IsAwt
   public void showFS(fahrstrasse fs) {
      ListenerList<AbstractEvent> ll = (ListenerList<AbstractEvent>)this.listeners.get(5);
      if (ll != null) {
         ll.fireEvent(new fahrstrasseEvent(fs, 0));
      }
   }

   @IsAwt
   public void FSchanged(fahrstrasse fs) {
      ListenerList<AbstractEvent> ll = (ListenerList<AbstractEvent>)this.listeners.get(8);
      if (ll != null) {
         ll.fireEvent(new fahrstrasseEvent(fs, 2));
      }
   }

   @IsAwt
   @Override
   public void interPanelCom(AbstractEvent e) {
      ListenerList<AbstractEvent> ll = (ListenerList<AbstractEvent>)this.listeners.get(10);
      if (ll != null) {
         ll.fireEvent(e);
      }
   }

   @IsAwt
   public void setPanelInvisible(boolean invisible) {
      this.glbPanel.setLockMode(invisible);
      this.glbPanel.repaint();
   }

   @Awt
   @Override
   public void setGUIEnable(final boolean e) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.setGUIEnableAWT(e);
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               stellwerk_editor.this.setGUIEnableAWT(e);
            }
         });
      }
   }

   @IsAwt
   private void setGUIEnableAWT(boolean e) {
      if (!this.alterDisabled) {
         if (e) {
            this.setPanelInvisible(false);
            if (this.nextAutoOK) {
               this.setCursor(new Cursor(0));
               this.showDataPanel();
               this.nextAutoOK = false;
               this.setTitle(this.glbModel.getAnlagenname() + " - Gleiseditor");
            } else {
               this.setCursor(new Cursor(0));
               this.okButton.setEnabled(true);
            }
         } else {
            this.nextAutoOK = true;
            this.setCursor(new Cursor(3));
            this.okButton.setEnabled(false);
            this.showWaitPanel();
         }
      }
   }

   public void showPanel(final int p, final int v) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.showPanelAWT(p, v);
      } else {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               stellwerk_editor.this.showPanelAWT(p, v);
            }
         });
      }
   }

   @IsAwt
   private void showPanelAWT(int p, int v) {
      fwrunPanel pn = (fwrunPanel)this.allmypanels.get("fwrun");
      switch (p) {
         case 0:
            this.setPanelInvisible(true);
            this.alterDisabled = false;
            this.okButton.setEnabled(true);
            pn.setEnabled(false);
            this.FSchanged(null);
            break;
         case 1:
            this.showWaitPanel("fwrun");
            this.okButton.setEnabled(false);
            pn.setEnabled(true);
            this.alterDisabled = true;
            break;
         case 2:
            pn.setValue(v);
            break;
         case 3:
            pn.setMinimum(0);
            pn.setMaximum(v);
            break;
         case 4:
            ((fwdoppeltPanel)this.allmypanels.get("fwdoppelt")).initNewRun();
            this.showWaitPanel("fwdoppelt");
            this.okButton.setEnabled(false);
            pn.setEnabled(false);
      }
   }

   public fw_doppelt_interface new_fw_doppelt_class(ArrayList<fahrstrasse> old) {
      fwdoppeltPanel pan = (fwdoppeltPanel)this.allmypanels.get("fwdoppelt");
      return new fw_doppelt_v2(this, pan, old);
   }

   public fahrstrasse getSelectedFahrstrasse() {
      listFWPanel p = (listFWPanel)this.allmypanels.get("listfw");
      return p.getSelection();
   }

   public void setSelectedFahrstrasse(fahrstrasse f) {
      listFWPanel p = (listFWPanel)this.allmypanels.get("listfw");
      p.setSelection(f);
   }

   public void setWarning(String text, int rank) {
      this.infoLabel.clearWarning();
      if (text != null) {
         this.infoLabel.setBlinkEnabled(rank == 2);
         this.infoLabel.setWarning(text, null);
      }

      this.interPanelCom(new warningEvent(text, rank));
   }

   public void clearWarning() {
      this.infoLabel.clearWarning();
      this.interPanelCom(new warningEvent(null, 0));
   }

   @Override
   public void setUI(final gleis.gleisUIcom gl) {
      if (SwingUtilities.isEventDispatchThread()) {
         ListenerList<AbstractEvent> ll = (ListenerList<AbstractEvent>)this.listeners.get(12);
         if (ll != null) {
            ll.fireEvent(new setUIEvent(gl));
         }
      } else {
         try {
            SwingUtilities.invokeAndWait(new Runnable() {
               public void run() {
                  stellwerk_editor.this.setUI(gl);
               }
            });
         } catch (Exception var3) {
            Logger.getLogger("stslogger").log(Level.SEVERE, null, var3);
         }
      }
   }

   @Override
   public void readUI(gleis.gleisUIcom gl) {
      ListenerList<AbstractEvent> ll = (ListenerList<AbstractEvent>)this.listeners.get(13);
      if (ll != null) {
         ll.fireEvent(new readUIEvent(gl));
      }
   }

   private void destroy() {
      try {
         if (this.pingTimer != null) {
            this.pingTimer.stop();
            this.pingTimer = null;
         }

         for (ListenerList<AbstractEvent> l : this.listeners.values()) {
            l.clear();
         }

         this.listeners.clear();
         this.glbModel.allOff();
         this.glbModel.clear();
         this.glbModel.clearFahrwege();
         this.glbModel.events.clear();
         this.glbModel.gl_resize(1, 1);
         this.myRpanels.clear();
         this.myLpanels.clear();
         this.allmypanels.clear();
         this.grouppanels.clear();
         this.dropdowns.clear();
         this.allbuttons.clear();
         thema.clear();
         fahrstrasse.fserror = false;
      } finally {
         this.uc.moduleClosed();
      }
   }

   @Override
   public void repaintGleisbild() {
      if (this.simulator != null) {
         this.simulator.repaintGleisbild();
      }

      this.glbPanel.repaint();
   }

   public void repaint() {
      super.repaint();
      if (this.simulator != null) {
         this.simulator.repaint();
      }
   }

   private void initComponents() {
      this.jSplitPane1 = new JSplitPane();
      this.gleisedscroller = new JScrollPane();
      this.jPanel3 = new JPanel();
      this.jLabel3 = new JLabel();
      this.controlPanel = new JPanel();
      this.jPanel1 = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.buttonPanel = new JPanel();
      this.dataPanel = new JPanel();
      this.leftPanel = new JPanel();
      this.rightPanel = new JPanel();
      this.waitPanel = new JPanel();
      this.waitPanelContent = new JPanel();
      this.jPanel2 = new JPanel();
      this.okButton = new JButton();
      this.setDefaultCloseOperation(0);
      this.setTitle("Gleiseditor");
      this.setLocationByPlatform(true);
      this.setPreferredSize(new Dimension(1000, 655));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            stellwerk_editor.this.formWindowClosed(evt);
         }

         public void windowClosing(WindowEvent evt) {
            stellwerk_editor.this.formWindowClosing(evt);
         }
      });
      this.jSplitPane1.setDividerLocation(-100);
      this.jSplitPane1.setOrientation(0);
      this.jSplitPane1.setResizeWeight(1.0);
      this.jSplitPane1.setLastDividerLocation(-100);
      this.gleisedscroller.setBorder(null);
      this.gleisedscroller.setDoubleBuffered(true);
      this.gleisedscroller.setPreferredSize(new Dimension(207, 146));
      this.jPanel3.setBorder(new SoftBevelBorder(0));
      this.jPanel3.setDoubleBuffered(false);
      this.jPanel3.setLayout(new GridLayout(0, 1));
      this.jLabel3.setFont(new Font("Dialog", 1, 18));
      this.jLabel3.setHorizontalAlignment(0);
      this.jLabel3.setText("Editor startet, einen Moment bitte...");
      this.jLabel3.setBorder(new SoftBevelBorder(1));
      this.jPanel3.add(this.jLabel3);
      this.gleisedscroller.setViewportView(this.jPanel3);
      this.jSplitPane1.setLeftComponent(this.gleisedscroller);
      this.controlPanel.setDoubleBuffered(false);
      this.controlPanel.setMinimumSize(new Dimension(500, 250));
      this.controlPanel.setPreferredSize(new Dimension(100, 300));
      this.controlPanel.setLayout(new CardLayout());
      this.controlPanel.setLayout(new AnimCardLayout());
      this.jPanel1.setDoubleBuffered(false);
      this.jPanel1.setLayout(new BorderLayout());
      this.jScrollPane1.setBorder(null);
      this.jScrollPane1.setHorizontalScrollBarPolicy(31);
      this.jScrollPane1.setVerticalScrollBarPolicy(21);
      this.buttonPanel.setLayout(new FlowLayout(0, 2, 0));
      this.jScrollPane1.setViewportView(this.buttonPanel);
      this.jPanel1.add(this.jScrollPane1, "North");
      this.dataPanel.setDoubleBuffered(false);
      this.dataPanel.setMaximumSize(new Dimension(32767, 160));
      this.dataPanel.setLayout(new GridLayout(1, 2));
      this.leftPanel.setDoubleBuffered(false);
      this.leftPanel.setLayout(new CardLayout());
      this.dataPanel.add(this.leftPanel);
      this.rightPanel.setDoubleBuffered(false);
      this.rightPanel.setLayout(new CardLayout());
      this.dataPanel.add(this.rightPanel);
      this.jPanel1.add(this.dataPanel, "Center");
      this.controlPanel.add(this.jPanel1, "data");
      this.waitPanel.setLayout(new BorderLayout());
      this.waitPanelContent.setLayout(new CardLayout());
      this.waitPanel.add(this.waitPanelContent, "Center");
      this.okButton.setText("Ok");
      this.okButton.setEnabled(false);
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            stellwerk_editor.this.okButtonActionPerformed(evt);
         }
      });
      this.jPanel2.add(this.okButton);
      this.waitPanel.add(this.jPanel2, "South");
      this.controlPanel.add(this.waitPanel, "wait");
      this.jSplitPane1.setRightComponent(this.controlPanel);
      this.getContentPane().add(this.jSplitPane1, "Center");
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      this.showDataPanel();
   }

   private void formWindowClosed(WindowEvent evt) {
      this.destroy();
   }

   private void formWindowClosing(WindowEvent evt) {
      if (this.loading) {
         JOptionPane.showMessageDialog(this, "Jetzt lass doch erstmal fertig laden!", "Schlechter Zeitpunkt", 2);
      } else {
         if (!closePrefs.Parts.GLEISEDITOR.ask(this.uc, this, "Wirklich Gleiseditor beenden?")) {
            return;
         }

         this.dispose();
      }
   }

   public void tipOfTheDay() {
   }

   public InputMap getInputMap(int condition) {
      return this.jSplitPane1.getInputMap(condition);
   }

   public ActionMap getActionMap() {
      return this.jSplitPane1.getActionMap();
   }

   @Override
   public void incZählwert() {
   }

   @Override
   public fsallocator getFSallocator() {
      return this.fsalloc;
   }

   @Override
   public gleisbildModelSts getGleisbild() {
      return this.glbModel;
   }
}
