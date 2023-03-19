package js.java.schaltungen.injector;

import de.deltaga.eb.EventBusService;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import js.java.schaltungen.UserContextMini;
import js.java.schaltungen.adapter.LaunchModule;
import js.java.schaltungen.adapter.Module;
import js.java.schaltungen.chatcomng.BotCommandMessage;
import js.java.tools.gui.WindowSnapper;
import js.java.tools.xml.xmllistener;
import js.java.tools.xml.xmlreader;
import org.xml.sax.Attributes;

public class Injector extends JFrame implements xmllistener {
   private final UserContextMini uc;
   private final String sandboxUrl;
   private final Timer readTimer = new Timer();
   private Injector.ReadTask currentTask = null;
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JCheckBox runInjectorCB;
   private JTextField statusTF;

   public Injector(UserContextMini uc, String sandboxUrl) {
      super();
      this.uc = uc;
      this.sandboxUrl = sandboxUrl;
      this.initComponents();
      this.setIconImage(uc.getWindowIcon());
      if ("1".equals(System.getProperty("develop")) || System.getProperty("jnlp.develop-url") != null) {
         String url = System.getProperty("jnlp.develop-url", "-1");
         JToolBar tb = new JToolBar();
         tb.setOrientation(1);

         for(Module m : Module.values()) {
            if (!m.testfile.isEmpty()) {
               JButton b = new JButton(m.title);
               b.addActionListener(e -> EventBusService.getInstance().publish(new LaunchModule(m, url)));
               tb.add(b);
            }
         }

         this.add(tb, "West");
      }

      this.pack();
      this.addComponentListener(new WindowSnapper(1));
   }

   private void showCount(int rest) {
      try {
         SwingUtilities.invokeAndWait(() -> this.statusTF.setText(rest + "s"));
      } catch (InvocationTargetException | InterruptedException var3) {
         Logger.getLogger(Injector.class.getName()).log(Level.SEVERE, null, var3);
      }
   }

   private void readUrl() {
      try {
         SwingUtilities.invokeAndWait(() -> this.statusTF.setText("Laden..."));
         xmlreader reader = new xmlreader();
         reader.registerTag("event", this);
         reader.updateData(this.sandboxUrl);
         SwingUtilities.invokeAndWait(() -> this.statusTF.setText(""));
      } catch (InterruptedException | InvocationTargetException | IOException var2) {
         Logger.getLogger(Injector.class.getName()).log(Level.SEVERE, null, var2);
      }
   }

   public void parseStartTag(String tag, Attributes attrs) {
   }

   public void parseEndTag(String tag, Attributes attrs, String pcdata) {
      EventBusService.getInstance().publish(new BotCommandMessage(pcdata));
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.jLabel1 = new JLabel();
      this.runInjectorCB = new JCheckBox();
      this.statusTF = new JTextField();
      this.jPanel2 = new JPanel();
      this.setDefaultCloseOperation(0);
      this.setTitle("Sandbox Injektor");
      this.setLocationByPlatform(true);
      this.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
      this.setPreferredSize(new Dimension(400, 320));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            Injector.this.formWindowClosing(evt);
         }
      });
      this.jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel1
         .setText(
            "<html>Injektor: fragt ca. alle 10 Sekunden beim Sandbox-Server, ob ein Modul gestartet werden soll. Sorgt außerdem für eine Session, so dass die Webseite auch den Modulstart zu lässt - beendet die Session aber nie mehr!!\n<br><br>\nErsetzt damit den Bot. <b>Niemals</b> beide auf der selben Sandbox zeitgleich laufen lassen, da dies zu Modulstartproblemen führen wird.\n</html>"
         );
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(3, 0, 3, 0);
      this.jPanel1.add(this.jLabel1, gridBagConstraints);
      this.runInjectorCB.setText("benutzen");
      this.runInjectorCB.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            Injector.this.runInjectorCBItemStateChanged(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(3, 0, 3, 0);
      this.jPanel1.add(this.runInjectorCB, gridBagConstraints);
      this.statusTF.setEditable(false);
      this.statusTF.setColumns(20);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(3, 0, 3, 0);
      this.jPanel1.add(this.statusTF, gridBagConstraints);
      GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
      this.jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 0, 32767));
      jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING).addGap(0, 0, 32767));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel1.add(this.jPanel2, gridBagConstraints);
      this.getContentPane().add(this.jPanel1, "Center");
      this.pack();
   }

   private void runInjectorCBItemStateChanged(ItemEvent evt) {
      if (this.runInjectorCB.isSelected() && this.currentTask == null) {
         this.currentTask = new Injector.ReadTask();
         this.readTimer.scheduleAtFixedRate(this.currentTask, 500L, 1000L);
      } else if (!this.runInjectorCB.isSelected() && this.currentTask != null) {
         this.currentTask.cancel();
         this.currentTask = null;
         this.statusTF.setText("");
      }
   }

   private void formWindowClosing(WindowEvent evt) {
      JOptionPane.showMessageDialog(this, "Lass das, das willst du nicht!", "Warum?", 0);
   }

   private class ReadTask extends TimerTask {
      private static final int DELAY = 10;
      private int count = 0;

      private ReadTask() {
         super();
      }

      public void run() {
         ++this.count;
         if (this.count >= 10) {
            this.count = 0;
            Injector.this.readUrl();
         } else {
            Injector.this.showCount(10 - this.count);
         }
      }
   }
}
