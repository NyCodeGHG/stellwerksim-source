package js.java.tools.formhtml;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

public class FormMain extends JFrame {
   private final HTMLEditorKit editor = new HTMLEditorKit();
   private static final String htmlText = "<html>\n<body><form>R:<input type='radio' name='r1' value='aaaa'><input type='radio' name='r1' value='bbbb'>\n<br>CB1:<input type='checkbox' name='c1'>\n<br>CB2:<input type='checkbox' name='c2'>\n<br>TXT1:<input name='i1' value='test' length='50'>\n<br>TXT2:<input name='i2' value='test' length='50' title='Prompt Text'>\n<br><select name='s1'><option>111</option><option>222</option></select>\n<br><select name='label.xml.s2'><option>111</option><option>222</option><option value='555'>33333333</option></select>\n</form></body>\n</html>";
   private ButtonGroup buttonGroup1;
   private JTextPane edit;
   private JPanel jPanel1;
   private JScrollPane jScrollPane1;
   private JButton resetButton;
   private JButton saveButton;

   public FormMain() {
      this.initComponents();
      System.out.println("-------- START setText() ----------");
      this.edit
         .setText(
            "<html>\n<body><form>R:<input type='radio' name='r1' value='aaaa'><input type='radio' name='r1' value='bbbb'>\n<br>CB1:<input type='checkbox' name='c1'>\n<br>CB2:<input type='checkbox' name='c2'>\n<br>TXT1:<input name='i1' value='test' length='50'>\n<br>TXT2:<input name='i2' value='test' length='50' title='Prompt Text'>\n<br><select name='s1'><option>111</option><option>222</option></select>\n<br><select name='label.xml.s2'><option>111</option><option>222</option><option value='555'>33333333</option></select>\n</form></body>\n</html>"
         );
   }

   private void initComponents() {
      this.buttonGroup1 = new ButtonGroup();
      this.jScrollPane1 = new JScrollPane();
      this.edit = new JTextPane();
      this.jPanel1 = new JPanel();
      this.saveButton = new JButton();
      this.resetButton = new JButton();
      this.setDefaultCloseOperation(3);
      this.setLocationByPlatform(true);
      this.jScrollPane1.setMinimumSize(new Dimension(300, 200));
      this.jScrollPane1.setPreferredSize(new Dimension(300, 200));
      this.edit.setEditable(false);
      this.edit.setContentType("text/html");
      this.edit.setEditorKit(this.editor);
      this.edit.addHyperlinkListener(new HyperlinkListener() {
         public void hyperlinkUpdate(HyperlinkEvent evt) {
            FormMain.this.editHyperlinkUpdate(evt);
         }
      });
      this.jScrollPane1.setViewportView(this.edit);
      this.getContentPane().add(this.jScrollPane1, "Center");
      this.jPanel1.setLayout(new FlowLayout(1, 5, 0));
      this.saveButton.setText("save");
      this.saveButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            FormMain.this.saveButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.saveButton);
      this.resetButton.setText("reset");
      this.resetButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            FormMain.this.resetButtonActionPerformed(evt);
         }
      });
      this.jPanel1.add(this.resetButton);
      this.getContentPane().add(this.jPanel1, "South");
      this.pack();
   }

   private void editHyperlinkUpdate(HyperlinkEvent evt) {
      if (evt.getEventType() == EventType.ACTIVATED) {
         System.out.println(evt.getDescription());
      }
   }

   private void saveButtonActionPerformed(ActionEvent evt) {
      this.editor.save();
   }

   private void resetButtonActionPerformed(ActionEvent evt) {
      HashMap<String, String> v = new HashMap();
      v.put("i1", "Text Feld 1");
      v.put("i2", "2. Text Feld: " + System.currentTimeMillis());
      v.put("c1", "on");
      v.put("c2", "off");
      v.put("r1", "bbbb");
      v.put("s1", "222");
      this.editor.setValues(v);
   }

   public static void main(String[] args) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassNotFoundException var2) {
         Logger.getLogger(FormMain.class.getName()).log(Level.SEVERE, null, var2);
      }

      EventQueue.invokeLater(new Runnable() {
         public void run() {
            new FormMain().setVisible(true);
         }
      });
   }
}
