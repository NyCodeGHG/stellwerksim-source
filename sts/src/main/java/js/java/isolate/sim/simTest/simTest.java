package js.java.isolate.sim.simTest;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import js.java.isolate.sim.sim.stellwerksim_main;
import js.java.tools.TextHelper;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.streams.TextAreaWriter;

public class simTest extends JDialog implements Runnable {
   private final stellwerksim_main my_main;
   private final PrintWriter out;
   private ConcurrentHashMap<String, simCmd> cmdList = new ConcurrentHashMap();
   private JComboBox cmdCB;
   private JScrollPane jScrollPane1;
   private JTextArea outputArea;

   public simTest(stellwerksim_main m) {
      super(m, false);
      this.my_main = m;
      this.initComponents();
      this.out = TextAreaWriter.createPrintStream(this.outputArea);
      this.cmdCB.getEditor().getEditorComponent().addKeyListener(new KeyListener() {
         public void keyTyped(KeyEvent e) {
            simTest.this.cmdKey(e);
         }

         public void keyPressed(KeyEvent e) {
         }

         public void keyReleased(KeyEvent e) {
         }
      });
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
      Thread t = new Thread(this);
      t.run();
      this.setVisible(true);
      this.out.println("Befehl 'help' für Befehlsliste.");
   }

   public void run() {
      this.addCmd(new cmdList());
      this.addCmd(new cmdStartevent());
      this.addCmd(new cmdEventdetails());
      this.addCmd(new cmdCreateevent());
   }

   private void addCmd(simCmd c) {
      this.cmdList.put(c.getName(), c);
   }

   private void cmdKey(KeyEvent evt) {
      if (evt.getKeyChar() == '\n') {
         String cmd = (String)this.cmdCB.getEditor().getItem();
         this.cmdCB.addItem(cmd);
         this.cmdCB.setSelectedItem("");
         this.execute(cmd);
      }
   }

   private void execute(String cmd) {
      String[] opts = TextHelper.cmdSplit(cmd);
      if (opts.length > 0 && !opts[0].isEmpty()) {
         this.out.println(cmd + ":");
         if (opts[0].equalsIgnoreCase("help")) {
            if (opts.length > 1) {
               simCmd c = (simCmd)this.cmdList.get(opts[1]);
               if (c != null) {
                  this.out.println("- " + c.getName() + ":");
                  c.usage(this.out);
                  this.out.println();
               } else {
                  this.out.println(opts[1] + " unbekannt.");
               }
            } else {
               for(simCmd c : this.cmdList.values()) {
                  this.out.println("* " + c.getName() + ":");
                  c.usage(this.out);
                  this.out.println();
               }
            }
         } else {
            simCmd c = (simCmd)this.cmdList.get(opts[0]);
            if (c != null) {
               c.execute(this.my_main, this.out, opts);
            }
         }
      }

      this.out.println();
   }

   private void initComponents() {
      this.cmdCB = new JComboBox();
      this.jScrollPane1 = new JScrollPane();
      this.outputArea = new JTextArea();
      this.setDefaultCloseOperation(2);
      this.setTitle("Sim Test Tool");
      this.setLocationByPlatform(true);
      this.cmdCB.setEditable(true);
      this.getContentPane().add(this.cmdCB, "South");
      this.outputArea.setColumns(20);
      this.outputArea.setEditable(false);
      this.outputArea.setRows(5);
      this.jScrollPane1.setViewportView(this.outputArea);
      this.getContentPane().add(this.jScrollPane1, "Center");
      this.pack();
   }
}
