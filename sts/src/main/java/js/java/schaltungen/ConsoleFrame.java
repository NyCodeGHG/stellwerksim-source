package js.java.schaltungen;

import de.deltaga.eb.EventBusService;
import de.deltaga.eb.EventHandler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import js.java.schaltungen.chatcomng.IrcDisconnectedEvent;
import js.java.schaltungen.verifyTests.FunctionTestFailedEvent;
import js.java.schaltungen.webservice.StoreTextData;
import js.java.tools.HeapDumper;
import js.java.tools.gui.SwingTools;
import js.java.tools.gui.WindowStateSaver;
import js.java.tools.gui.WindowStateSaver.STORESTATES;
import js.java.tools.logging.MessageConsole;
import js.java.tools.streams.TeeOutputStream;

public class ConsoleFrame extends JFrame {
   private final UserContextMini uc;
   private final MessageConsole mc;
   private File heapDumpDir = null;
   private final Timer dumpTimer = new Timer();
   private TimerTask currentTask = null;
   private PrintStream outfile = null;
   private final TeeOutputStream outTree;
   private final TeeOutputStream errTree;
   private final SimpleDateFormat df = new SimpleDateFormat("yM_d_Hm");
   private JButton clearButton;
   private JButton clipButton;
   private JButton heapDumpButton;
   private JCheckBox heapDumpPerMinute;
   private JButton helpButton;
   private JLabel jLabel1;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JScrollPane jScrollPane1;
   private JButton okButton;
   private JButton saveButton;
   private JTextPane textComponent;
   private JCheckBox writeToFile;

   public ConsoleFrame(UserContextMini uc) {
      super();
      this.uc = uc;
      this.initComponents();
      this.setIconImage(uc.getWindowIcon());
      SwingTools.addStandardEditingPopupMenu(new JTextComponent[]{this.textComponent});
      this.outTree = new TeeOutputStream(System.out, (OutputStream)null);
      this.errTree = new TeeOutputStream(System.err, (OutputStream)null);
      this.mc = new MessageConsole(this.textComponent);
      this.mc.redirectOut(Color.BLACK, new PrintStream(this.outTree));
      this.mc.redirectErr(Color.MAGENTA, new PrintStream(this.errTree));
      this.mc.setMessageLines(400);
      this.setName(this.getClass().getSimpleName());
      new WindowStateSaver(this, STORESTATES.LOCATION_AND_SIZE);
      EventBusService.getInstance().subscribe(this);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> this.exitClose()));
   }

   private void exitClose() {
      if (this.currentTask != null) {
         this.currentTask.cancel();
         this.currentTask = null;
      }

      if (this.outfile != null) {
         try {
            this.outTree.flush();
            this.errTree.flush();
         } catch (IOException var2) {
         }

         this.outTree.setEnable(false);
         this.errTree.setEnable(false);
         this.outfile.close();
         this.outfile = null;
      }
   }

   private void sendLog(String title, String message) {
      try {
         StringWriter str = new StringWriter();
         BufferedWriter out = new BufferedWriter(str);
         this.addSysinfo(out);
         if (message != null && !message.isEmpty()) {
            out.write(message);
            out.newLine();
         }

         out.write(this.textComponent.getText());
         out.flush();
         out.close();
         EventBusService.getInstance().publish(new StoreTextData(title, "|" + title + "|\n" + str.toString()));
      } catch (IOException var5) {
         Logger.getLogger(ConsoleFrame.class.getName()).log(Level.SEVERE, null, var5);
      }
   }

   @EventHandler
   public void disconnected(IrcDisconnectedEvent ch) {
      if (!ch.isDisconnecting) {
         this.sendLog(ch.message, "");
      }
   }

   @EventHandler
   public void functionTestFailed(FunctionTestFailedEvent event) {
      this.sendLog("Failed FTest " + this.uc.getUsername(), event.message);
   }

   public void setVisible(boolean v) {
      if (v && !this.isVisible()) {
         new ConsoleHelp(this, this.uc).showWithCountdown();
      }

      super.setVisible(v);
   }

   private void addSysinfo(BufferedWriter systemInfo) throws IOException {
      systemInfo.append("Build: ").append(Integer.toString(this.uc.getBuild()));
      systemInfo.newLine();
      systemInfo.append("Java: ").append(System.getProperty("java.version")).append("; ").append(System.getProperty("java.vm.name"));
      systemInfo.newLine();
      systemInfo.append("Runtime: ").append(System.getProperty("java.runtime.name")).append("; ").append(System.getProperty("java.runtime.version"));
      systemInfo.newLine();
      systemInfo.append("Arch: ")
         .append(System.getProperty("sun.arch.data.model"))
         .append("; running on ")
         .append(System.getProperty("os.arch"))
         .append("; ")
         .append(Runtime.getRuntime().availableProcessors() + "")
         .append(" cores");
      systemInfo.newLine();
      systemInfo.append("OS: ").append(System.getProperty("os.name")).append("; version ").append(System.getProperty("os.version"));
      systemInfo.newLine();
      systemInfo.append("VM Memory: ")
         .append(Long.toString(Runtime.getRuntime().maxMemory() / 1024L / 1024L))
         .append(" MB max; ")
         .append(Long.toString(Runtime.getRuntime().totalMemory() / 1024L / 1024L))
         .append(" MB used");
      systemInfo.newLine();
      systemInfo.append("User: ").append(this.uc.getUsername()).append(" UID: " + this.uc.getUid());
      systemInfo.newLine();
      systemInfo.append("IPv6: ").append(Boolean.toString(this.uc.getChat().isV6()));
      systemInfo.newLine();
      systemInfo.newLine();
      systemInfo.newLine();
   }

   private void dataToClip() {
      StringWriter sw = new StringWriter();

      try {
         BufferedWriter out = new BufferedWriter(sw);
         Throwable clpbrd = null;

         try {
            this.addSysinfo(out);
         } catch (Throwable var13) {
            clpbrd = var13;
            throw var13;
         } finally {
            if (out != null) {
               if (clpbrd != null) {
                  try {
                     out.close();
                  } catch (Throwable var12) {
                     clpbrd.addSuppressed(var12);
                  }
               } else {
                  out.close();
               }
            }
         }
      } catch (IOException var15) {
         Logger.getLogger(ConsoleFrame.class.getName()).log(Level.SEVERE, null, var15);
      }

      StringSelection stringSelection = new StringSelection(sw.toString());
      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      clpbrd.setContents(stringSelection, null);
   }

   private void saveHeapDumpTimer() {
      File dest = new File(this.heapDumpDir, "dump-" + this.df.format(new Date()) + ".hprof");

      try {
         HeapDumper.dumpHeap(dest.getAbsolutePath(), true);
      } catch (Exception var3) {
         SwingUtilities.invokeLater(() -> {
            this.heapDumpPerMinute.setSelected(false);
            JOptionPane.showMessageDialog(this.saveButton, "Speichern als Datei " + dest.getName() + " fehlgeschlagen.", "Speichern fehlgeschlagen", 0);
         });
      }
   }

   private void initComponents() {
      this.jPanel1 = new JPanel();
      this.jLabel1 = new JLabel();
      this.jScrollPane1 = new JScrollPane();
      this.textComponent = new JTextPane();
      this.helpButton = new JButton();
      this.saveButton = new JButton();
      this.heapDumpButton = new JButton();
      this.clipButton = new JButton();
      this.writeToFile = new JCheckBox();
      this.heapDumpPerMinute = new JCheckBox();
      this.jPanel2 = new JPanel();
      this.clearButton = new JButton();
      this.okButton = new JButton();
      this.setTitle("Console");
      this.setLocationByPlatform(true);
      this.setPreferredSize(new Dimension(650, 400));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            ConsoleFrame.this.formWindowClosing(evt);
         }
      });
      this.jPanel1.setPreferredSize(new Dimension(500, 300));
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel1.setBackground(Color.black);
      this.jLabel1.setForeground(Color.white);
      this.jLabel1
         .setText(
            "<html>Sollte ein Fehler während des Programmlaufs auftreten, so kann der Inhalt dieser \"Console\" für die Fehleranalyse hilfreich sein. Klicke auf \"Hilfe\" für weitere Details.\n</html>"
         );
      this.jLabel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jLabel1.setOpaque(true);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 6;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.jLabel1, gridBagConstraints);
      this.textComponent.setEditable(false);
      this.jScrollPane1.setViewportView(this.textComponent);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridwidth = 6;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel1.add(this.jScrollPane1, gridBagConstraints);
      this.helpButton.setText("Hilfe");
      this.helpButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ConsoleFrame.this.helpButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(10, 10, 10, 10);
      this.jPanel1.add(this.helpButton, gridBagConstraints);
      this.saveButton.setText("Text speichern");
      this.saveButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ConsoleFrame.this.saveButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(10, 10, 5, 10);
      this.jPanel1.add(this.saveButton, gridBagConstraints);
      this.heapDumpButton.setText("Heap Dump");
      this.heapDumpButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ConsoleFrame.this.heapDumpButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.insets = new Insets(10, 10, 5, 10);
      this.jPanel1.add(this.heapDumpButton, gridBagConstraints);
      this.clipButton.setText("Analysedaten");
      this.clipButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ConsoleFrame.this.clipButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.insets = new Insets(10, 10, 5, 10);
      this.jPanel1.add(this.clipButton, gridBagConstraints);
      this.writeToFile.setText("permanent in Datei schreiben");
      this.writeToFile.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            ConsoleFrame.this.writeToFileItemStateChanged(evt);
         }
      });
      this.writeToFile.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ConsoleFrame.this.writeToFileActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(5, 10, 10, 10);
      this.jPanel1.add(this.writeToFile, gridBagConstraints);
      this.heapDumpPerMinute.setText("minütlicher Dump");
      this.heapDumpPerMinute.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent evt) {
            ConsoleFrame.this.heapDumpPerMinuteItemStateChanged(evt);
         }
      });
      this.heapDumpPerMinute.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ConsoleFrame.this.heapDumpPerMinuteActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 21;
      gridBagConstraints.insets = new Insets(5, 10, 10, 10);
      this.jPanel1.add(this.heapDumpPerMinute, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 4;
      gridBagConstraints.weightx = 1.0;
      this.jPanel1.add(this.jPanel2, gridBagConstraints);
      this.clearButton.setText("Löschen");
      this.clearButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ConsoleFrame.this.clearButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.insets = new Insets(10, 10, 5, 10);
      this.jPanel1.add(this.clearButton, gridBagConstraints);
      this.okButton.setText("Schliessen");
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ConsoleFrame.this.okButtonActionPerformed(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 5;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 22;
      gridBagConstraints.insets = new Insets(5, 10, 10, 10);
      this.jPanel1.add(this.okButton, gridBagConstraints);
      this.getContentPane().add(this.jPanel1, "Center");
      this.pack();
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
   }

   private void saveButtonActionPerformed(ActionEvent evt) {
      JFileChooser fc = new JFileChooser();
      fc.setDialogTitle("Dateiname für Text");
      fc.setDialogType(1);
      fc.setSelectedFile(new File("sts-console-log.txt"));
      int j = fc.showSaveDialog(this.saveButton);
      if (j == 0) {
         File dest = fc.getSelectedFile();

         try {
            BufferedWriter out = new BufferedWriter(new FileWriter(dest));
            Throwable var6 = null;

            try {
               this.addSysinfo(out);
               out.write(this.textComponent.getText());
               out.close();
               JOptionPane.showMessageDialog(this.saveButton, "Erfolgreich gespeichert.", "Gespeichert", 1);
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (out != null) {
                  if (var6 != null) {
                     try {
                        out.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     out.close();
                  }
               }
            }
         } catch (IOException var18) {
            JOptionPane.showMessageDialog(this.saveButton, "Speichern als Datei " + dest.getName() + " fehlgeschlagen.", "Speichern fehlgeschlagen", 0);
         }
      }
   }

   private void helpButtonActionPerformed(ActionEvent evt) {
      new ConsoleHelp(this, this.uc).setVisible(true);
   }

   private void heapDumpButtonActionPerformed(ActionEvent evt) {
      JFileChooser fc = new JFileChooser();
      fc.setDialogTitle("Dateiname für Heap Dump");
      fc.setDialogType(1);
      fc.setSelectedFile(new File("sts-heap-dump.hprof"));
      int j = fc.showSaveDialog(this.saveButton);
      if (j == 0) {
         File dest = fc.getSelectedFile();
         if (!dest.getName().endsWith(".hprof")) {
            dest = new File(dest.getParentFile(), dest.getName() + ".hprof");
         }

         try {
            HeapDumper.dumpHeap(dest.getAbsolutePath(), true);
            JOptionPane.showMessageDialog(this.saveButton, "Erfolgreich gespeichert.", "Gespeichert", 1);
         } catch (Exception var6) {
            JOptionPane.showMessageDialog(this.saveButton, "Speichern als Datei " + dest.getName() + " fehlgeschlagen.", "Speichern fehlgeschlagen", 0);
         }
      }
   }

   private void clearButtonActionPerformed(ActionEvent evt) {
      this.mc.clear();
   }

   private void clipButtonActionPerformed(ActionEvent evt) {
      this.dataToClip();
      JOptionPane.showMessageDialog(
         this.clipButton,
         "Analysedaten wurden in die Zwischenablage kopiert.\nDiese können nun z.B. in einen Forenbeitrag kopiert werden.",
         "Analysedaten verfügbar",
         1
      );
   }

   private void heapDumpPerMinuteItemStateChanged(ItemEvent evt) {
      if (!this.heapDumpPerMinute.isSelected() && this.currentTask != null) {
         this.currentTask.cancel();
         this.currentTask = null;
      }
   }

   private void formWindowClosing(WindowEvent evt) {
   }

   private void heapDumpPerMinuteActionPerformed(ActionEvent evt) {
      if (this.heapDumpPerMinute.isSelected()) {
         JFileChooser fc = new JFileChooser();
         fc.setDialogTitle("Ordner für Heap Dumps");
         fc.setFileSelectionMode(1);
         if (this.heapDumpDir != null) {
            fc.setSelectedFile(this.heapDumpDir);
         }

         int j = fc.showOpenDialog(this.heapDumpPerMinute);
         if (j == 0) {
            this.heapDumpDir = fc.getSelectedFile();
            this.currentTask = new TimerTask() {
               public void run() {
                  ConsoleFrame.this.saveHeapDumpTimer();
               }
            };
            this.dumpTimer.schedule(this.currentTask, 1000L, 60000L);
         } else {
            this.heapDumpPerMinute.setSelected(false);
         }
      }
   }

   private void writeToFileItemStateChanged(ItemEvent evt) {
      if (!this.writeToFile.isSelected() && this.outfile != null) {
         try {
            this.outTree.flush();
            this.errTree.flush();
         } catch (IOException var3) {
         }

         this.outTree.setEnable(false);
         this.errTree.setEnable(false);
         this.outfile.close();
         this.outfile = null;
      }
   }

   private void writeToFileActionPerformed(ActionEvent evt) {
      if (this.writeToFile.isSelected()) {
         JFileChooser fc = new JFileChooser();
         fc.setDialogTitle("Dateiname für Log");
         fc.setSelectedFile(new File("sts-console-log.txt"));
         int j = fc.showOpenDialog(this.writeToFile);
         if (j == 0) {
            File logfile = fc.getSelectedFile();

            try {
               this.outfile = new PrintStream(logfile);
               StringWriter sw = new StringWriter();

               try {
                  BufferedWriter bw = new BufferedWriter(sw);
                  Throwable var7 = null;

                  try {
                     this.addSysinfo(bw);
                  } catch (Throwable var18) {
                     var7 = var18;
                     throw var18;
                  } finally {
                     if (bw != null) {
                        if (var7 != null) {
                           try {
                              bw.close();
                           } catch (Throwable var17) {
                              var7.addSuppressed(var17);
                           }
                        } else {
                           bw.close();
                        }
                     }
                  }
               } catch (IOException var20) {
                  Logger.getLogger(ConsoleFrame.class.getName()).log(Level.SEVERE, null, var20);
               }

               this.outfile.append(sw.toString());
               this.outfile.append(this.textComponent.getText());
               this.outTree.setEnable(true, this.outfile);
               this.errTree.setEnable(true, this.outfile);
            } catch (FileNotFoundException var21) {
               Logger.getLogger(ConsoleFrame.class.getName()).log(Level.SEVERE, null, var21);
            }
         } else {
            this.writeToFile.setSelected(false);
         }
      }
   }
}
