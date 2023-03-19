package js.java.schaltungen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import js.java.tools.gui.CurvesPanel;

public class ConsoleHelp extends JDialog {
   private JButton closeButton;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JScrollPane jScrollPane1;
   private JTextPane jTextPane1;

   public ConsoleHelp(Frame parent, UserContextMini uc) {
      super(parent);
      this.initComponents();
      this.setIconImage(uc.getWindowIcon());
      this.jTextPane1.setCaretPosition(0);
   }

   public void showWithCountdown() {
      ConsoleHelp.CountDownData cdd = new ConsoleHelp.CountDownData();
      cdd.timer = new Timer(1000, a -> this.checkDelay(cdd));
      cdd.timer.start();
      this.checkDelay(cdd);
      this.setVisible(true);
   }

   private void checkDelay(ConsoleHelp.CountDownData cdd) {
      --cdd.counter;
      if (cdd.counter < 1) {
         this.closeButton.setEnabled(true);
         this.closeButton.setText("Ok");
         cdd.timer.stop();
      } else {
         this.closeButton.setEnabled(false);
         this.closeButton.setText("Ok in " + cdd.counter + "s");
      }
   }

   private void initComponents() {
      this.jPanel2 = new CurvesPanel();
      this.jScrollPane1 = new JScrollPane();
      this.jTextPane1 = new JTextPane();
      this.jPanel1 = new JPanel();
      this.closeButton = new JButton();
      this.setDefaultCloseOperation(0);
      this.setTitle("Hilfe zur Console");
      this.setLocationByPlatform(true);
      this.setModal(true);
      this.setModalityType(ModalityType.DOCUMENT_MODAL);
      this.setPreferredSize(new Dimension(700, 500));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            ConsoleHelp.this.formWindowClosing(evt);
         }
      });
      this.jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel2.setLayout(new BorderLayout(0, 5));
      this.jTextPane1.setEditable(false);
      this.jTextPane1.setContentType("text/html");
      this.jTextPane1
         .setText(
            "<html>\n<body style=\"font-family: sans-serif;\">\nDie Ausgabe auf der Console kann in vielen Fällen bei der Fehlersuche und -analyse hilfreich sein. \nAllerdings enthält sie auch sehr viele Informationen, die wenig nützlich sind! Es liegt deshalb beim Anwender,\nDir, diese zu filtern: \n<ol>\n<li>Falls Du einen Fehler hattest, speichere die gesamte Console in eine Datei.\n<li>Nur weil etwas in der Console steht, deutet das nicht auf einen Fehler hin, die Console sammelt nur alle Ausgaben aller Programmteile, wichtige und <b>auch unwichtige</b>.\n<li>Speichere ebenfalls den \"Heap Dump\" ein eine zweite Datei. Diese Datei wird nur in Einzelfällen benötigt!\n<li>Melde den Fehler im Forum, <b>kopiere niemals die gesamte Datei in einen Beitrag</b>.\n<li>Im Beitrag kannst du Teile der Detail bereits zitieren, beschränke dich dabei auf die Zeilen, die zum Zeitpunkt des Fehlers geschrieben wurden; die Meldungen enthalten dazu eine Zeitangabe.\n<li>Wirst du nach weiteren Details gefragt, betrifft das in der Regel nur das Modul, das zu dem Zeitpunkt des Fehlers lief. Kopiere nur aus dem Bereich, ein Modulstart und -ende sind in der Ausgabe klar zu erkennen.\n<li>Wirst du nach der gesamten Datei gefragt, packe diese <b>niemals</b> ins Forum, du erhälst eine Mail-Adresse, an die du die Datei schicken sollst.\n<li>Die Dateien können unter Umständen personenbezogene Daten beinhalten (<i>nichts, was Facebook & Co nicht ohnehin schon wissen</i>). Deshalb gebe sie niemand anderem, einzig an besagte Mail-Adresse.\n<li>Mit dem Versand stimmst du zu, die Dateien zum Zwecke der Fehlersuche zu speichern und zu lesen.\n<li>Lösche die Dateien erst, nachdem der Empfang bestätigt oder das Problem behoben wurde.\n<li>Bei der \"Heap Dump\" Datei handelt es sich um eine Binärdatei, <b>öffne sie nicht mit einem Texteditor</b>, das könnte die Datei beschädigen.\n<li>Tritt ein Fehler unvorhersehbar auf, kannst du dann jedoch keinen Heap-Dump mehr schreiben, kannst du minütlich einen Dump schreiben lassen - diese Dateien werden jedoch nicht automatisch gelöscht und sind sehr groß.\n<li>Fehlermeldungen sind in der Console in der Regel klar als solche zu erkennen, Hinweise, Informationen oder Warnungen sind in der Regel unwichtig, solange sie nicht in Zusammenhang mit einem Fehler stehen.\n<li>Vermeide unnötige Forenbeiträge und Consolen-Posts ohne weitere Informationen - bei (wiederholter) Zuwiderhandlung behalten wir uns die Löschen des Posts oder gar eine Sperrung der Kennung vor.\n<li>Der Consoleninhalt kann u.U. deinen Zugangscode enthalten. Solltest du diesen öffentlich ins Forum posten (was du besser vermeidest), wird dein Zugangscode gelöscht, so dass du einen neuen Download starten musst um einen neuen Zugangscode zu erhalten. Ohne den Download startet das Programm ansonsten nicht und zeigt nur eine Fehlermeldung.\n</body>\n</html>"
         );
      this.jScrollPane1.setViewportView(this.jTextPane1);
      this.jPanel2.add(this.jScrollPane1, "Center");
      this.jPanel1.setOpaque(false);
      this.closeButton.setText("Ok");
      this.closeButton.setMinimumSize(new Dimension(140, 23));
      this.closeButton.setPreferredSize(new Dimension(140, 23));
      this.closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            ConsoleHelp.this.closeButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.closeButton);
      this.jPanel2.add(this.jPanel1, "South");
      this.getContentPane().add(this.jPanel2, "Center");
      this.pack();
   }

   private void closeButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
      this.dispose();
   }

   private void formWindowClosing(WindowEvent evt) {
      if (this.closeButton.isEnabled()) {
         this.closeButtonActionPerformed(null);
      } else {
         Toolkit.getDefaultToolkit().beep();
      }
   }

   private static class CountDownData {
      int counter = 11;
      Timer timer;

      private CountDownData() {
         super();
      }
   }
}
