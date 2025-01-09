package js.java.isolate.sim.sim;

import de.deltaga.eb.EventHandler;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.SoftBevelBorder;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.fatcodeprovider;
import js.java.isolate.sim.triggerjobmanager;
import js.java.isolate.sim.eventsys.eventContainer;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.eventsys.eventHaeufigkeiten;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.sim.botcom.BotChat;
import js.java.isolate.sim.structServ.structinfo;
import js.java.isolate.sim.zug.zug;
import js.java.schaltungen.UserContext;
import js.java.tools.gui.border.DropShadowBorder;
import org.relayirc.util.Debug;

public class fat extends JFrame {
   private int responseCnt = 0;
   private static final SimpleDateFormat odate = new SimpleDateFormat("MMM d HH:mm:ss");
   private final UserContext uc;
   private final fatcodeprovider my_main;
   private JPanel buttonPanel;
   private JButton codeButton;
   private JTextField codeField;
   private JButton commentButton;
   private JTextField commentField;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JPanel jPanel4;
   private JTextField statusText;

   public static void FATwriteln(String modul, String text) {
      System.out.println(odate.format(new Date()) + " FAT [" + modul + "](" + Thread.currentThread().getId() + "): " + text);
   }

   public static void FATwriteln(String modul, structinfo si) {
      Vector v = si.getStructure();
      FATwriteln(modul, "DUMP start " + si.getStructName() + "-------------------");

      for (int i = 0; i < v.size(); i += 2) {
         FATwriteln(modul, i / 2 + 1 + ". " + v.elementAt(i) + ": " + v.elementAt(i + 1));
      }

      FATwriteln(modul, "DUMP end " + si.getStructName() + "-------------------");
   }

   public fat(UserContext uc, fatcodeprovider m) {
      this.uc = uc;
      this.my_main = m;
      this.initComponents();
      this.setSize(400, 300);
      this.setIconImage(Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/js/java/tools/resources/funk.gif")));
      uc.busSubscribe(this);
   }

   private void initComponents() {
      this.jPanel4 = new JPanel();
      this.jPanel1 = new JPanel();
      this.codeField = new JTextField();
      this.jLabel1 = new JLabel();
      this.codeButton = new JButton();
      this.jLabel2 = new JLabel();
      this.jPanel2 = new JPanel();
      this.buttonPanel = new JPanel();
      this.statusText = new JTextField();
      this.jPanel3 = new JPanel();
      this.jLabel3 = new JLabel();
      this.commentField = new JTextField();
      this.commentButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("Fehler-Analyse-Tool");
      this.setCursor(new Cursor(0));
      this.setLocationByPlatform(true);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosed(WindowEvent evt) {
            fat.this.formWindowClosed(evt);
         }
      });
      this.jPanel4.setBorder(BorderFactory.createBevelBorder(0));
      this.jPanel4.setLayout(new BorderLayout());
      this.jPanel1.setBorder(new SoftBevelBorder(1));
      this.jPanel1.setLayout(new BorderLayout());
      this.jPanel1.add(this.codeField, "Center");
      this.jLabel1.setText("Tagescode");
      this.jPanel1.add(this.jLabel1, "West");
      this.codeButton.setText("Ok");
      this.codeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fat.this.codeButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.codeButton, "East");
      this.jPanel4.add(this.jPanel1, "North");
      this.jLabel2
         .setText(
            "<html>Dieses Fenster dient der Fehlersuche. Zum Aktivieren ist ein Tagescode nötig. Dieser gilt nur für einen spezifischen Tag und wird bei Bedarf mitgeteilt! Jede Codeeingabe wird vom Server protokolliert.\n</html>"
         );
      this.jLabel2.setBorder(new SoftBevelBorder(1));
      this.jPanel4.add(this.jLabel2, "South");
      this.jPanel2.setLayout(new BorderLayout());
      this.buttonPanel.setBorder(new SoftBevelBorder(1));
      this.jPanel2.add(this.buttonPanel, "Center");
      this.statusText.setEditable(false);
      this.statusText.setFocusable(false);
      this.jPanel2.add(this.statusText, "North");
      this.jPanel3.setBorder(new SoftBevelBorder(1));
      this.jPanel3.setLayout(new BoxLayout(this.jPanel3, 2));
      this.jLabel3.setText("eigener Hinweistext");
      this.jPanel3.add(this.jLabel3);
      this.commentField.setColumns(20);
      this.commentField.setToolTipText("Hier kann ein eigener hilfreicher Text eingegeben werden, der dann im FAT Log erscheint.");
      this.commentField.setEnabled(false);
      this.commentField.setFocusTraversalPolicyProvider(true);
      this.commentField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fat.this.commentFieldActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.commentField);
      this.commentButton.setText("Text jetzt anzeigen");
      this.commentButton.setEnabled(false);
      this.commentButton.setMargin(new Insets(0, 8, 0, 8));
      this.commentButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            fat.this.commentFieldActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.commentButton);
      this.jPanel2.add(this.jPanel3, "South");
      this.jPanel4.add(this.jPanel2, "Center");
      this.getContentPane().add(this.jPanel4, "Center");
      this.pack();
   }

   private void formWindowClosed(WindowEvent evt) {
      this.uc.busUnsubscribe(this);
   }

   private void codeButtonActionPerformed(ActionEvent evt) {
      String code = this.codeField.getText();
      if (code != null && code.length() > 1) {
         this.statusText.setText("Code wird vom Server geprüft, bitte warten.");
         this.checkCode(code);
      } else {
         this.statusText.setText("Bitte Code eingeben.");
      }

      this.buttonPanel.removeAll();
   }

   private void commentFieldActionPerformed(ActionEvent evt) {
      String comment = this.commentField.getText();
      if (comment != null && !comment.isEmpty()) {
         FATwriteln("FAT user comment", comment);
      }

      this.commentField.setText("");
   }

   public static void open(UserContext uc, fatcodeprovider m) {
      new fat(uc, m).setVisible(true);
   }

   private void setIRCDebug(boolean v) {
      Debug.setDebug(v);
   }

   private boolean isIRCDebug() {
      return Debug.isDebug();
   }

   private void setGleisDebug(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("Gleis");
      }

      gleis.setDebug(w);
   }

   boolean isGleisDebug() {
      return gleis.isDebug();
   }

   private void setZugDebug(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("Zug");
      }

      zug.setDebug(w);
   }

   private boolean isEventSysDebug() {
      return eventGenerator.isDebug();
   }

   private void setEventSysDebug(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("eventsys");
      }

      eventGenerator.setDebug(w);
      eventContainer.setDebug(w);
      eventHaeufigkeiten.setDebug(w);
   }

   private void dumpEventsys(boolean v) {
      eventHaeufigkeiten.dump();
   }

   private boolean isZugDebug() {
      return zug.isDebug();
   }

   private void setFSAllocDebug(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("FSAllocator");
      }

      fsallocator.setDebug(w);
   }

   private boolean isFSAllocDebug() {
      return fsallocator.isDebug();
   }

   private void setMainDebug(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("main");
      }

      stellwerksim_main.setDebug(w);
      zugUndPlanPanel.setDebug(w);
   }

   private boolean isMainDebug() {
      return stellwerksim_main.isDebug();
   }

   private void setChatDebug(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("Chat");
      }

      BotChat.setDebug(w);
   }

   private boolean isChatDebug() {
      return BotChat.isDebug();
   }

   private void setTJMDebug(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("TJM");
      }

      triggerjobmanager.setDebug(w);
   }

   private boolean isTJMDebug() {
      return triggerjobmanager.isDebug();
   }

   private void setFSDebug(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("TJM");
      }

      fahrstrasse.setDebug(w);
   }

   private boolean isFSDebug() {
      return fahrstrasse.isDebug();
   }

   private void setRedirectDebug(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("Redirect");
      }

      redirectRouteSpecify.setDebug(w);
   }

   private boolean isRedirectDebug() {
      return redirectRouteSpecify.isDebug();
   }

   private void setDumper(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("DUMPER");
      }

      this.my_main.setDumper(w);
   }

   private boolean isDumper() {
      return this.my_main.isDumper();
   }

   private void setUepLatenz(boolean v) {
      FATwriter w = null;
      if (v) {
         w = new FATwriter("Latenz");
      }

      LatencyMeasure.setDebug(w);
   }

   private boolean isUepLatenz() {
      return LatencyMeasure.isDebug();
   }

   private void addButton(final String string, boolean b, final fat.toggleValue t) {
      final JToggleButton tb = new JCheckBox();
      tb.setText(string);
      tb.setSelected(b);
      tb.setFocusPainted(false);
      tb.setFocusable(false);
      tb.setBorder(new DropShadowBorder(true, true, true, true));
      tb.setBorderPainted(true);
      tb.addActionListener(new ActionListener() {
         String modul = string;
         JToggleButton me = tb;
         fat.toggleValue callme = t;

         public void actionPerformed(ActionEvent e) {
            fat.this.statusText.setText("FAT für " + string + " " + (this.me.isSelected() ? "aktiviert" : "abgeschaltet"));
            this.callme.action(this.me.isSelected());
         }
      });
      this.buttonPanel.add(tb);
   }

   private void checkCode(String code) {
      this.my_main.FATmessage(code);
   }

   @EventHandler
   public void codeResponse(fat.FatResponseEvent res) {
      if (res.res == 200) {
         this.responseCnt = 0;
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               fat.this.statusText.setText("Code akzeptiert.");
               fat.this.setButtons();
               fat.this.codeButton.setEnabled(false);
               fat.this.codeField.setEnabled(false);
               fat.this.commentField.setEnabled(true);
               fat.this.commentButton.setEnabled(true);
            }
         });
      } else {
         this.responseCnt++;
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               fat.this.statusText.setText("Code NICHT akzeptiert.");
               if (fat.this.responseCnt > 4) {
                  fat.this.codeButton.setEnabled(false);
                  fat.this.codeField.setEnabled(false);
               }
            }
         });
      }
   }

   private void setButtons() {
      this.buttonPanel.removeAll();
      this.addButton("Dumper Menüs", this.isDumper(), this::setDumper);
      this.addButton("ÜP Latenz", this.isUepLatenz(), this::setUepLatenz);
      this.addButton("Chat", this.isChatDebug(), this::setChatDebug);
      this.addButton("Gleis", this.isGleisDebug(), this::setGleisDebug);
      this.addButton("Zug", this.isZugDebug(), this::setZugDebug);
      this.addButton("FSallocator", this.isFSAllocDebug(), this::setFSAllocDebug);
      this.addButton("main", this.isMainDebug(), this::setMainDebug);
      this.addButton("IRC", this.isIRCDebug(), this::setIRCDebug);
      this.addButton("TJM", this.isTJMDebug(), this::setTJMDebug);
      this.addButton("FS", this.isFSDebug(), this::setFSDebug);
      this.addButton("Eventsys", this.isEventSysDebug(), this::setEventSysDebug);
      this.addButton("Redirect", this.isRedirectDebug(), this::setRedirectDebug);
      this.addButton("Dump Störungen", false, this::dumpEventsys);
      this.buttonPanel.revalidate();
      this.buttonPanel.repaint();
      this.statusText.setText("Build: " + this.uc.getBuild());
   }

   public static class FatResponseEvent {
      final int res;

      public FatResponseEvent(int res) {
         this.res = res;
      }
   }

   private interface toggleValue {
      void action(boolean var1);
   }
}
