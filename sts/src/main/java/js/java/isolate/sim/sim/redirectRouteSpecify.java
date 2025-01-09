package js.java.isolate.sim.sim;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.sim.fahrplanRenderer.redirektInfoContainer;
import js.java.isolate.sim.sim.redirectInfo.RedirectStellwerkInfo;
import js.java.isolate.sim.zug.zug;
import js.java.tools.dialogs.htmlmessage1;
import js.java.tools.gui.ArrowBox;
import js.java.tools.gui.TimeoutButton;

public class redirectRouteSpecify extends JDialog {
   static FATwriter debugMode = null;
   private static final int DEFAULTTIMEOUT = 60;
   private static HashMap<Integer, ArrayList<String>> valueCollector = new HashMap();
   private static final String helpText = "<html>Die Zugumleitung erlaubt die Umfahrung von Stellwerken, z.B. bei Störungen.<p>Dazu wird von einem Stellwerk ausgehend eine Umleitung via Funk beantragt. Wurde die Umfahrung genehmigt erscheint ein Dialogfenster. In diesem Fenster ist zu spezifizieren, welches Stellwerk der nun statt des fahrplanmäßigen Folgestellwerks anfahren soll. In dem alternativen Stellwerk wird nun ebenfalls das Folgestellwerk vom dortigen Spieler spezifiziert, usw. Das initiierende Stellwerk muss außerdem den Grund der Umleitung angeben.<p>Dabei wird in dem Fenster rechts die bereits erstellte Ersatzroute gezeigt, links die Stellwerke, zu denen der Zug zurückfahren kann.<p>Bedingungen: <ul><li>Die Stellwerke der Umfahrungsstrecke müssen alle durchgehend besetzt und direkt verbunden sein.<li>Der Zug muss auf eins der links gezeigten Stellwerke zurück auf die fahrplanmäßige Streckenführung.<li>Der Zug darf auf kein Stellwerk treffen, das er bereits durchfahren hat oder noch durchfährt, aber nicht auf der linken Liste steht.<li>Es kann kein Stellwerk umfahren werden, an dem der Zug flügelt, an einen anderen kuppel oder gekuppelt wird, seinen Namen ändert oder endet.</ul>Wurde eine Umleitungsstrecke festgelegt, werden die Spieler der umfahrenen Stellwerke und des Endstellwerks der Umleitung um ihre Bestätigung gebeten - unbesetzte Stellwerke gelten automatisch als bestätigt.<p>Abschließend erhalten die Stellwerke der Umleitungsstrecke eine Fahrplanaktualisierung und eine Information über den neuen Zuglauf.</html>";
   private final stellwerksim_main my_main;
   private int zid;
   private String name;
   private LinkedList<Integer> planlist = new LinkedList();
   private TimeoutButton tbutton = null;
   private LinkedList<String> info_skip = new LinkedList();
   private LinkedList<String> info_newway = new LinkedList();
   private boolean iamInitiator = false;
   private final RedirectStellwerkInfo aidStore;
   private JPanel exitPanel;
   private JButton helpButton;
   private JLabel infoLabel;
   private JTextField initiatorTF;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JPanel jPanel4;
   private JPanel jPanel5;
   private JPanel jPanel7;
   private JPanel jPanel8;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JPanel middlePanel;
   private JComboBox reasonCB;
   private JLabel reasonLabel;
   private JPanel reasonPanel;
   private JPanel routePanel;
   private JPanel scrollPanel;
   private JPanel skipPanel;
   private JLabel titelLabel;
   private JTextField zugnameTF;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static FATwriter getDebug() {
      return debugMode;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   static void invokeLater(final stellwerksim_main parent, final String cmd, final int res, final StringTokenizer r, final RedirectStellwerkInfo m) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            int hash = 0;
            boolean show = false;
            ArrayList<String> l = new ArrayList();

            while (r.hasMoreTokens()) {
               String t = r.nextToken().trim();
               if (t.equals("SUP")) {
                  if (r.hasMoreTokens()) {
                     t = r.nextToken();
                     hash = Integer.parseInt(t);
                  }
               } else if (t.equals("SHOW")) {
                  show = true;
               } else {
                  l.add(t);
               }
            }

            if (hash > 0) {
               if (!redirectRouteSpecify.valueCollector.containsKey(hash)) {
                  redirectRouteSpecify.valueCollector.put(hash, l);
               } else {
                  ((ArrayList)redirectRouteSpecify.valueCollector.get(hash)).addAll(l);
               }
            }

            if (show || hash == 0) {
               if (hash > 0) {
                  l = (ArrayList<String>)redirectRouteSpecify.valueCollector.remove(hash);
               }

               new redirectRouteSpecify(parent, cmd, res, l, m);
            }
         }
      });
   }

   public redirectRouteSpecify(stellwerksim_main parent, String command, int res, ArrayList<String> tokens, RedirectStellwerkInfo m) {
      super(parent, false);
      this.my_main = parent;
      this.aidStore = m;
      this.initComponents();
      JLabel lastAddedAid = null;
      int lastAid = 0;
      int btncnt = 0;

      try {
         if (res >= 200 && res < 300) {
            int reason = res - 200;

            try {
               this.reasonCB.setSelectedIndex(reason);
               this.reasonLabel.setText((String)this.reasonCB.getSelectedItem());
            } catch (IllegalArgumentException var15) {
            }
         }

         ((CardLayout)this.reasonPanel.getLayout()).show(this.reasonPanel, "reasonS");
         boolean firstAid = true;
         boolean firstPlan = true;

         for (int i = 0; i < tokens.size(); i++) {
            if (debugMode != null) {
               debugMode.writeln("token: |" + (String)tokens.get(i) + "|");
            }

            if (((String)tokens.get(i)).equals("ZID")) {
               this.zid = Integer.parseInt((String)tokens.get(++i));
               this.name = (String)tokens.get(++i);
               this.zugnameTF.setText(this.name);
            } else if (((String)tokens.get(i)).equals("AID")) {
               int aid = Integer.parseInt((String)tokens.get(++i));
               String aname = (String)tokens.get(++i);
               this.info_newway.add(aname);
               this.aidStore.addStellwerk(aid, aname);
               if (!firstAid) {
                  lastAddedAid = this.addAid(aid, aname);
                  lastAid = aid;
               } else {
                  this.addFirstAid(aid, aname);
                  this.iamInitiator = aid == this.my_main.getGleisbild().getAid() && command.equals("ZREDIRECTWAY");
                  this.reasonCB.setEnabled(this.iamInitiator);
                  if (this.iamInitiator) {
                     ((CardLayout)this.reasonPanel.getLayout()).show(this.reasonPanel, "reasonQ");
                  }
               }

               firstAid = false;
               if (debugMode != null) {
                  debugMode.writeln("aid: " + aid + "/" + aname);
               }
            } else if (((String)tokens.get(i)).equals("SKIP")) {
               int aidx = Integer.parseInt((String)tokens.get(++i));
               String anamex = (String)tokens.get(++i);
               this.aidStore.addStellwerk(aidx, anamex);
               this.addSkip(aidx, anamex);
               this.planlist.add(aidx);
               this.info_skip.add(anamex);
               if (debugMode != null) {
                  debugMode.writeln("skip: " + aidx + "/" + anamex);
               }
            } else if (((String)tokens.get(i)).equals("PLAN")) {
               int aidx = Integer.parseInt((String)tokens.get(++i));
               String anamex = (String)tokens.get(++i);
               this.aidStore.addStellwerk(aidx, anamex);
               if (firstPlan) {
                  this.addSkipSeparator();
               }

               this.addPlanSkip(aidx, anamex);
               firstPlan = false;
               if (debugMode != null) {
                  debugMode.writeln("plan: " + aidx + "/" + anamex);
               }
            } else if (((String)tokens.get(i)).equals("EXIT")) {
               int aidxx = Integer.parseInt((String)tokens.get(++i));
               String anamexx = (String)tokens.get(++i);
               this.aidStore.addStellwerk(aidxx, anamexx);
               if (debugMode != null) {
                  debugMode.writeln("exit: " + aidxx + "/" + anamexx);
               }

               if (this.planlist.contains(aidxx)) {
                  anamexx = "<html><b>" + anamexx + "</b></html>";
               }

               this.addExit(aidxx, anamexx, false);
               btncnt++;
            }
         }
      } catch (ArrayIndexOutOfBoundsException var16) {
         if (command.equals("ZREDIRECTWAY")) {
            this.exitPressed(0);
         } else if (command.equals("ZREDIRECTACK")) {
            this.ackPressed(true);
         }

         return;
      }

      if (command.equals("ZREDIRECTWAY")) {
         this.infoLabel.setText("<html>Es wird der bisher geplante neue Laufweg angezeigt,<br>bitte das Anschluss-Stellwerk spezifizieren.</html>");

         for (int i = 2 - btncnt % 3; i > 0; i--) {
            this.addDummy();
         }

         this.addExit(0, "<html><i>ablehnen</i></html>", true);
         this.boxTitle("regulärer Weg", "bisherige Planung");
      } else if (command.equals("ZREDIRECTACK")) {
         if (lastAddedAid != null) {
            this.scrollPanel.add(lastAddedAid, "South");
            lastAddedAid.setHorizontalTextPosition(0);
            lastAddedAid.setHorizontalAlignment(0);
            if (lastAid == this.my_main.getGleisbild().getAid() && this.my_main.findZug(this.zid) == null) {
               this.my_main.sendRedirectAck(this.zid, false);
               return;
            }
         }

         this.infoLabel.setText("<html>Es wird der geplante neue Laufweg angezeigt,<br>bitte akzeptieren oder ablehnen.</html>");
         this.addAck(true, "akzeptieren");
         this.addDummy();
         this.addAck(false, "ablehnen");
         this.boxTitle("umfahrene Strecke", "neuer Weg");
      } else if (command.equals("ZREDIRECTINFO")) {
         if (lastAddedAid != null) {
            this.scrollPanel.add(lastAddedAid, "South");
            lastAddedAid.setHorizontalTextPosition(0);
            lastAddedAid.setHorizontalAlignment(0);
         }

         this.titelLabel.setText("Es wird ein Zug umgeleitet!");
         this.infoLabel.setText("<html>Umleitung bestätigt!<br>Es wird der neue Laufweg angezeigt.</html>");
         this.addDummy();
         this.addDummy();
         this.addClose("Ok");
         this.boxTitle("umfahrene Strecke", "neuer Weg");
         zug z = this.my_main.findZug(this.zid);
         if (z != null) {
            StringBuilder sb = new StringBuilder();
            redirektInfoContainer ric = new redirektInfoContainer();
            ric.von = (String)this.info_newway.pollFirst();
            ric.nach = (String)this.info_newway.pollLast();
            sb.append("<table border=0 cellpadding=1 cellspacing=1><tr bgcolor='eeeeee'>");
            sb.append("<td colspan=2>Zug fährt umgeleitet von ").append(ric.von).append(" nach ").append(ric.nach);
            sb.append("</td></tr>");
            sb.append("<tr valign=top>");
            if (!this.info_newway.isEmpty()) {
               sb.append("<td bgcolor='eeeeee'><b>über</b><p>");
               boolean needk = false;

               for (String s : this.info_newway) {
                  if (needk) {
                     sb.append("<br>");
                  }

                  sb.append(s);
                  needk = true;
                  ric.newway.add(s);
               }

               sb.append("</td>");
            }

            if (!this.info_skip.isEmpty()) {
               sb.append("<td bgcolor='eeeeee'><b>umfährt dabei</b><p>");
               boolean needk = false;

               for (String s : this.info_skip) {
                  if (needk) {
                     sb.append("<br>");
                  }

                  sb.append(s);
                  needk = true;
                  ric.skipway.add(s);
               }

               sb.append("</td>");
            }

            sb.append("</tr></table>");
            z.setAdditionalDescription(ric, sb.toString());
         }
      } else if (command.equals("ZREQUESTFPL")) {
         this.titelLabel.setText("Zug Fahrplan ab hier");
         this.addDummy();
         this.addDummy();
         this.addClose("Ok");
         this.boxTitle("Strecke", "-");
         this.reasonPanel.getParent().remove(this.reasonPanel);
         this.helpButton.getParent().remove(this.helpButton);
      }

      this.pack();
      this.setSize(660, 400);
      this.setVisible(true);
   }

   private void boxTitle(String skip, String route) {
      this.skipPanel.setBorder(new TitledBorder(skip));
      this.routePanel.setBorder(new TitledBorder(route));
   }

   private void addFirstAid(int aid, String name) {
      JLabel stop = new JLabel(name);
      stop.setForeground(UIManager.getDefaults().getColor("List.foreground"));
      stop.setHorizontalTextPosition(0);
      stop.setHorizontalAlignment(0);
      this.scrollPanel.add(stop, "North");
      this.initiatorTF.setText(name);
   }

   private JLabel addAid(int aid, String name) {
      JLabel stop = new JLabel(name);
      stop.setForeground(UIManager.getDefaults().getColor("List.foreground"));
      this.routePanel.add(stop);
      return stop;
   }

   private void addPlanSkip(int aid, String name) {
      JLabel stop = new JLabel(name);
      stop.setForeground(UIManager.getDefaults().getColor("List.foreground"));
      stop.setEnabled(false);
      this.skipPanel.add(stop);
   }

   private void addSkip(int aid, String name) {
      JLabel stop = new JLabel(name);
      stop.setForeground(UIManager.getDefaults().getColor("List.foreground"));
      this.skipPanel.add(stop);
   }

   private void addSkipSeparator() {
      this.addSkip(0, " ");
      JSeparator sep = new JSeparator(0);
      sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
      this.skipPanel.add(sep);
   }

   private void addExit(final int aid, String name, boolean def) {
      JButton exit;
      if (def) {
         this.tbutton = new TimeoutButton(name, 60);
         exit = this.tbutton;
      } else {
         exit = new JButton(name);
      }

      exit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            redirectRouteSpecify.this.exitPressed(aid);
         }
      });
      this.exitPanel.add(exit);
   }

   private void addAck(final boolean ack, String name) {
      JButton exit;
      if (ack) {
         this.tbutton = new TimeoutButton(name, 60);
         exit = this.tbutton;
      } else {
         exit = new JButton(name);
      }

      exit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            redirectRouteSpecify.this.ackPressed(ack);
         }
      });
      this.exitPanel.add(exit);
   }

   private void addClose(String name) {
      JButton exit = new JButton(name);
      exit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            redirectRouteSpecify.this.setVisible(false);
            redirectRouteSpecify.this.dispose();
         }
      });
      this.exitPanel.add(exit);
   }

   private void addDummy() {
      JLabel dummy = new JLabel("");
      this.exitPanel.add(dummy);
   }

   private void exitPressed(int aid) {
      this.tbutton.stop();
      if (aid > 0 || this.verifyAbort()) {
         int r = this.reasonCB.getSelectedIndex();
         this.my_main.sendRedirectMsg(this.zid, aid, this.reasonCB.getSelectedIndex());
         this.setVisible(false);
         this.dispose();
      }
   }

   private void ackPressed(boolean ack) {
      this.tbutton.stop();
      if (ack || this.verifyAbort()) {
         this.my_main.sendRedirectAck(this.zid, ack);
         this.setVisible(false);
         this.dispose();
      }
   }

   private boolean verifyAbort() {
      if (!this.tbutton.wasTimeout()) {
         int r = JOptionPane.showConfirmDialog(this, "Wirklich ablehnen?", "Bitte bestätigen", 0, 3);
         return r == 0;
      } else {
         return true;
      }
   }

   private void initComponents() {
      this.titelLabel = new JLabel();
      this.jPanel2 = new JPanel();
      this.infoLabel = new JLabel();
      this.jPanel3 = new JPanel();
      this.jPanel4 = new JPanel();
      this.jLabel2 = new JLabel();
      this.zugnameTF = new JTextField();
      this.jPanel5 = new JPanel();
      this.jLabel3 = new JLabel();
      this.initiatorTF = new JTextField();
      this.scrollPanel = new JPanel();
      this.jPanel1 = new JPanel();
      this.jScrollPane2 = new JScrollPane();
      this.skipPanel = new JPanel();
      this.middlePanel = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.routePanel = new JPanel();
      this.helpButton = new JButton();
      this.reasonPanel = new JPanel();
      this.jPanel7 = new JPanel();
      this.reasonCB = new JComboBox();
      this.jPanel8 = new JPanel();
      this.reasonLabel = new JLabel();
      this.exitPanel = new JPanel();
      this.setDefaultCloseOperation(0);
      this.setTitle("Zugumleitung");
      this.setLocationByPlatform(true);
      this.setMinimumSize(new Dimension(550, 300));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            redirectRouteSpecify.this.formWindowClosing(evt);
         }
      });
      this.titelLabel.setFont(this.titelLabel.getFont().deriveFont(this.titelLabel.getFont().getStyle() | 1, (float)(this.titelLabel.getFont().getSize() + 4)));
      this.titelLabel.setHorizontalAlignment(0);
      this.titelLabel.setText("Es wurde die Umleitung eines Zuges beantragt!");
      this.getContentPane().add(this.titelLabel, "North");
      this.jPanel2
         .setBorder(
            BorderFactory.createCompoundBorder(
               BorderFactory.createEmptyBorder(4, 4, 4, 4),
               BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(1), BorderFactory.createEmptyBorder(4, 4, 4, 4))
            )
         );
      this.jPanel2.setLayout(new GridBagLayout());
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 18;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(10, 0, 0, 0);
      this.jPanel2.add(this.infoLabel, gridBagConstraints);
      this.jPanel3.setBackground(UIManager.getDefaults().getColor("info"));
      this.jPanel3.setBorder(BorderFactory.createEtchedBorder());
      this.jPanel3.setLayout(new GridLayout(1, 0));
      this.jPanel4.setBackground(UIManager.getDefaults().getColor("info"));
      this.jPanel4.setLayout(new BoxLayout(this.jPanel4, 2));
      this.jLabel2.setFont(this.jLabel2.getFont().deriveFont((float)this.jLabel2.getFont().getSize() + 1.0F));
      this.jLabel2.setText("Zugname: ");
      this.jPanel4.add(this.jLabel2);
      this.zugnameTF.setColumns(10);
      this.zugnameTF.setEditable(false);
      this.zugnameTF.setFont(this.zugnameTF.getFont().deriveFont((float)this.zugnameTF.getFont().getSize() + 3.0F));
      this.zugnameTF.setBorder(null);
      this.zugnameTF.setOpaque(false);
      this.jPanel4.add(this.zugnameTF);
      this.jPanel3.add(this.jPanel4);
      this.jPanel5.setBackground(UIManager.getDefaults().getColor("info"));
      this.jPanel5.setLayout(new BoxLayout(this.jPanel5, 2));
      this.jLabel3.setFont(this.jLabel3.getFont().deriveFont((float)this.jLabel3.getFont().getSize() + 1.0F));
      this.jLabel3.setText("Initiator: ");
      this.jPanel5.add(this.jLabel3);
      this.initiatorTF.setBackground(UIManager.getDefaults().getColor("info"));
      this.initiatorTF.setEditable(false);
      this.initiatorTF.setFont(this.initiatorTF.getFont().deriveFont((float)this.initiatorTF.getFont().getSize() + 3.0F));
      this.initiatorTF.setBorder(null);
      this.initiatorTF.setOpaque(false);
      this.jPanel5.add(this.initiatorTF);
      this.jPanel3.add(this.jPanel5);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      this.jPanel2.add(this.jPanel3, gridBagConstraints);
      this.scrollPanel.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.scrollPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
      this.scrollPanel.setLayout(new BorderLayout());
      this.jPanel1.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jScrollPane2.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.jScrollPane2.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
      this.skipPanel.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.skipPanel.setLayout(new BoxLayout(this.skipPanel, 1));
      this.jScrollPane2.setViewportView(this.skipPanel);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel1.add(this.jScrollPane2, gridBagConstraints);
      this.middlePanel.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.middlePanel.setLayout(new BorderLayout());
      this.middlePanel.add(new ArrowBox(), "Center");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      this.jPanel1.add(this.middlePanel, gridBagConstraints);
      this.jScrollPane1.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.jScrollPane1.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
      this.routePanel.setBackground(UIManager.getDefaults().getColor("List.background"));
      this.routePanel.setLayout(new BoxLayout(this.routePanel, 1));
      this.jScrollPane1.setViewportView(this.routePanel);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel1.add(this.jScrollPane1, gridBagConstraints);
      this.scrollPanel.add(this.jPanel1, "Center");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridheight = 3;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel2.add(this.scrollPanel, gridBagConstraints);
      this.helpButton.setText("Hilfe...");
      this.helpButton.setFocusPainted(false);
      this.helpButton.setFocusable(false);
      this.helpButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            redirectRouteSpecify.this.helpButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.anchor = 17;
      this.jPanel2.add(this.helpButton, gridBagConstraints);
      this.reasonPanel.setBorder(BorderFactory.createTitledBorder("Umleitungsgrund"));
      this.reasonPanel.setLayout(new CardLayout());
      this.jPanel7.setLayout(new BorderLayout());
      this.reasonCB
         .setModel(new DefaultComboBoxModel(new String[]{"keine Angabe", "wie abgesprochen", "Störung", "Sperrung", "Streckenauslastung", "Verspätung", " "}));
      this.reasonCB.setEnabled(false);
      this.reasonCB.setFocusable(false);
      this.jPanel7.add(this.reasonCB, "Center");
      this.reasonPanel.add(this.jPanel7, "reasonQ");
      this.jPanel8.setLayout(new BorderLayout());
      this.jPanel8.add(this.reasonLabel, "Center");
      this.reasonPanel.add(this.jPanel8, "reasonS");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 15;
      this.jPanel2.add(this.reasonPanel, gridBagConstraints);
      this.getContentPane().add(this.jPanel2, "Center");
      this.exitPanel.setLayout(new GridLayout(0, 3, 4, 2));
      this.getContentPane().add(this.exitPanel, "South");
      this.pack();
   }

   private void formWindowClosing(WindowEvent evt) {
      JOptionPane.showMessageDialog(this, "Bitte einen der Knöpfe am unteren Rand nutzen.", "Bitte beachten", 1);
   }

   private void helpButtonActionPerformed(ActionEvent evt) {
      htmlmessage1 m = new htmlmessage1(
         this.my_main,
         false,
         "Umleitungshilfe",
         "<html>Die Zugumleitung erlaubt die Umfahrung von Stellwerken, z.B. bei Störungen.<p>Dazu wird von einem Stellwerk ausgehend eine Umleitung via Funk beantragt. Wurde die Umfahrung genehmigt erscheint ein Dialogfenster. In diesem Fenster ist zu spezifizieren, welches Stellwerk der nun statt des fahrplanmäßigen Folgestellwerks anfahren soll. In dem alternativen Stellwerk wird nun ebenfalls das Folgestellwerk vom dortigen Spieler spezifiziert, usw. Das initiierende Stellwerk muss außerdem den Grund der Umleitung angeben.<p>Dabei wird in dem Fenster rechts die bereits erstellte Ersatzroute gezeigt, links die Stellwerke, zu denen der Zug zurückfahren kann.<p>Bedingungen: <ul><li>Die Stellwerke der Umfahrungsstrecke müssen alle durchgehend besetzt und direkt verbunden sein.<li>Der Zug muss auf eins der links gezeigten Stellwerke zurück auf die fahrplanmäßige Streckenführung.<li>Der Zug darf auf kein Stellwerk treffen, das er bereits durchfahren hat oder noch durchfährt, aber nicht auf der linken Liste steht.<li>Es kann kein Stellwerk umfahren werden, an dem der Zug flügelt, an einen anderen kuppel oder gekuppelt wird, seinen Namen ändert oder endet.</ul>Wurde eine Umleitungsstrecke festgelegt, werden die Spieler der umfahrenen Stellwerke und des Endstellwerks der Umleitung um ihre Bestätigung gebeten - unbesetzte Stellwerke gelten automatisch als bestätigt.<p>Abschließend erhalten die Stellwerke der Umleitungsstrecke eine Fahrplanaktualisierung und eine Information über den neuen Zuglauf.</html>"
      );
      m.setVisible(true);
   }
}
