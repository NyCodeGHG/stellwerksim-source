package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisTypContainer;
import js.java.isolate.sim.gleis.gleisElements.element;

public class gleisElementHelp extends JDialog {
   private JButton closeButton;
   private JLabel jLabel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JScrollPane jScrollPane2;
   private JEditorPane textField;

   public gleisElementHelp(JPanel parent) {
      super(SwingUtilities.windowForComponent(parent));
      this.initComponents();
      this.setSize(550, 260);
      this.setLocationRelativeTo(parent);
   }

   public static String formatRequiremendString(element element) {
      boolean nenr = gleis.typRequiresENR(element);
      boolean editenr = gleis.typAllowesENRedit(element);
      boolean nsw = gleis.typRequiresSWwert(element);
      boolean editsw = gleis.typAllowesSWwertedit(element);
      String txt = "<table>";
      txt = txt + "<tr><td>Gleiselement benötigt E-Nummer (ENR)?</td><td>" + (nenr ? "<b>ja</b>" : "nein") + "</td></tr>\n";
      txt = txt + "<tr><td>Gleiselement erlaubt E-Nummer-Eingabe?</td><td>" + (editenr ? "<b>ja</b>" : "nein") + "</td></tr>\n";
      txt = txt + "<tr><td>Gleiselement benötigt SW-Wert?</td><td>" + (nsw ? "<b>ja</b>" : "nein") + "</td></tr>\n";
      txt = txt + "<tr><td>Gleiselement erlaubt SW-Wert-Eingabe?</td><td>" + (editsw ? "<b>ja</b>" : "nein") + "</td></tr>\n";
      return txt + "</table>\n";
   }

   public static String formatHelpString(element element) {
      gleisTypContainer gtc = gleisTypContainer.getInstance();
      String txt = "";
      txt = txt + "<table>";
      txt = txt + "<tr><td>Gleis Typ:</td><td><b>" + gtc.getTypName(element) + "</b></td></tr>\n";

      try {
         txt = txt + "<tr><td>Gleis Element:</td><td><b>" + gtc.getTypElementName(element) + "</b></td></tr>\n";
      } catch (NullPointerException var6) {
      }

      txt = txt + "</table>\n";
      txt = txt + "<hr>";
      txt = txt + formatRequiremendString(element);
      String sw = "";
      String enr = "";
      String tip = "";
      if (gleis.ALLE_DISPLAYS.matches(element)) {
         if (element == gleis.ELEMENT_AIDDISPLAY) {
            sw = "Die <b>Stellwerks-ID</b> (AID), dessen <i>StiTz</i> Telefonnummer darin angezeigt werden soll, wenn das Stellwerk besetzt ist. Oder das Wort '<b>REGION</b>' (ohne Anführungszeichen, komplett groß) um die Regionsraumnummer anzuzeigen. <i>Hier nicht die Telefonnummer eintragen!</i>";
         } else {
            sw = "Der SW-Wert eines Bahnsteigs/ Haltepunkte/ Displaykontakts/ Einfahrt, bei dem darin der Zugname eines Zuges angezeigt werden soll. Oder beliebige Name für Displayverdrahtung.";
         }
      } else if (gleis.ALLE_TEXTE.matches(element)) {
         sw = "Der darzustellende Text.";
      } else if (gleis.ALLE_KNÖPFE.matches(element)) {
         enr = "Die ENR, zu der der Knopf gehören soll.";
      } else if (element == gleis.ELEMENT_ANRUFÜBERGANG) {
         tip = "Es muss eine Störung 'Bahnüberganganruf' erzeugt werden, damit jemand anruft.";
         enr = "Zusammengehörige Übergänge haben die gleiche ENR.";
      } else if (element == gleis.ELEMENT_BAHNÜBERGANG || element == gleis.ELEMENT_WBAHNÜBERGANG) {
         enr = "Zusammengehörige Übergänge haben die gleiche ENR.";
      } else if (element == gleis.ELEMENT_DISPLAYKONTAKT) {
         sw = "Der Name des Kontakts für das Display.";
      } else if (gleis.ALLE_BAHNSTEIGE.matches(element)) {
         sw = "Name des Bahnsteigs, ohne das Wort \"Gleis\"!";
      } else if (element == gleis.ELEMENT_SETVMAX) {
         sw = "Max-Geschwindigkeit, positiv absolut, negativ relative Änderung";
      } else if (element == gleis.ELEMENT_EINFAHRT) {
         sw = "Name der Einfahrt, Text hinter % wird im Sim später nicht angezeigt.";
         enr = "Eindeutige ENR, kann aber verändert werden.";
      } else if (element == gleis.ELEMENT_AUSFAHRT) {
         sw = "Name der Ausfahrt, Text hinter % wird im Sim später nicht angezeigt.";
         enr = "Eindeutige ENR, kann aber verändert werden.";
      } else if (element == gleis.ELEMENT_ÜBERGABEPUNKT) {
         enr = "Die ENR der Ausfahrt, zu der der ÜP gehört.";
      } else if (element == gleis.ELEMENT_SIGNALKNOPF) {
         enr = "ENR des direkt folgenden Hauptsignals.";
      }

      if (!tip.isEmpty()) {
         txt = txt + "<b>Hinweis</b>: " + tip + "<br>\n";
      }

      if (!enr.isEmpty()) {
         txt = txt + "<b>E-Nummer</b>: " + enr + "<br>\n";
      }

      if (!sw.isEmpty()) {
         txt = txt + "<b>SW-Wert</b>: " + sw + "<br>\n";
      }

      return txt;
   }

   void setHelp(element element) {
      this.textField.setText("<html>" + formatHelpString(element) + "</html>");
   }

   private void initComponents() {
      this.jPanel2 = new JPanel();
      this.jLabel1 = new JLabel();
      this.jPanel3 = new JPanel();
      this.closeButton = new JButton();
      this.jScrollPane2 = new JScrollPane();
      this.textField = new JEditorPane();
      this.setTitle("Gleiselementbeschreibung");
      this.jPanel2.setBackground(UIManager.getDefaults().getColor("Button.shadow"));
      this.jPanel2.setLayout(new BorderLayout());
      this.jLabel1.setIcon(new ImageIcon(this.getClass().getResource("/js/java/tools/resources/help64.png")));
      this.jLabel1.setVerticalAlignment(1);
      this.jLabel1.setOpaque(true);
      this.jPanel2.add(this.jLabel1, "North");
      this.getContentPane().add(this.jPanel2, "West");
      this.jPanel3.setBackground(UIManager.getDefaults().getColor("Button.disabledForeground"));
      this.jPanel3.setLayout(new FlowLayout(1, 1, 1));
      this.closeButton.setText("schliessen");
      this.closeButton.setFocusPainted(false);
      this.closeButton.setFocusable(false);
      this.closeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            gleisElementHelp.this.closeButtonActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.closeButton);
      this.getContentPane().add(this.jPanel3, "South");
      this.jScrollPane2.setBorder(null);
      this.textField.setBackground(UIManager.getDefaults().getColor("Button.background"));
      this.textField.setContentType("text/html");
      this.textField.setEditable(false);
      this.jScrollPane2.setViewportView(this.textField);
      this.getContentPane().add(this.jScrollPane2, "Center");
      this.pack();
   }

   private void closeButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
   }
}
