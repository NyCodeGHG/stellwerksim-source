package js.java.tools.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import js.java.tools.gui.CurvesPanel;

public class aboutDialog extends JDialog {
   private final Frame parentWin;
   private JLabel jLabel1;
   private JLabel jLabel2;
   private JLabel jLabel3;
   private JLabel jLabel4;
   private JLabel jLabel5;
   private JLabel jLabel6;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JPanel jPanel3;
   private JScrollPane jScrollPane1;
   private JScrollPane jScrollPane2;
   private JTextPane jvmArea;
   private JButton okButton;
   private JTextPane textArea;
   private JTextField tf_author;
   private JTextField tf_copyright;
   private JTextField tf_email;
   private JTextField tf_program;
   private JTextField tf_version;
   private JTextField tf_web;

   public aboutDialog(
      Frame parent, boolean modal, String title, String program, String version, String copyright, String author, String email, String web, String text
   ) {
      super(parent, modal);
      this.parentWin = parent;
      this.initComponents();
      this.setTitle(title);
      this.tf_program.setText(program);
      this.tf_version.setText(version);
      this.tf_copyright.setText(copyright);
      this.tf_author.setText(author);
      this.tf_email.setText(email);
      this.tf_web.setText(web);
      this.textArea.setText(text);
      String fontfamily = this.jvmArea.getFont().getFamily();
      StringBuilder systemInfo = new StringBuilder();
      systemInfo.append("<html><div style='font-family: \"")
         .append(fontfamily)
         .append("\"; font-size:")
         .append(this.jvmArea.getFont().getSize())
         .append("pt;'>");
      systemInfo.append("<b>Java:</b> ").append(System.getProperty("java.version")).append("; ").append(System.getProperty("java.vm.name")).append("<br>\n");
      systemInfo.append("<b>Runtime:</b> ")
         .append(System.getProperty("java.runtime.name"))
         .append("; ")
         .append(System.getProperty("java.runtime.version"))
         .append("<br>\n");
      systemInfo.append("<b>Arch:</b> ")
         .append(System.getProperty("sun.arch.data.model"))
         .append("; running on ")
         .append(System.getProperty("os.arch"))
         .append("; ")
         .append(Runtime.getRuntime().availableProcessors())
         .append(" cores<br>\n");
      systemInfo.append("<b>OS:</b> ").append(System.getProperty("os.name")).append("; version ").append(System.getProperty("os.version")).append("<br>\n");
      systemInfo.append("<b>VM Memory:</b> ")
         .append(Runtime.getRuntime().maxMemory() / 1024L / 1024L)
         .append(" MB max; ")
         .append(Runtime.getRuntime().totalMemory() / 1024L / 1024L)
         .append(" MB used<br>\n");
      systemInfo.append("</div></html>");
      this.jvmArea.setText(systemInfo.toString());
      this.setLocationRelativeTo(parent);
   }

   public void show(int w, int h) {
      if (w > 0 && h > 0) {
         this.setSize(w, h);

         try {
            this.setLocationRelativeTo(this.parentWin);
         } catch (IllegalComponentStateException var5) {
            this.setLocation(10, 10);
         }
      } else {
         this.pack();

         try {
            this.setLocationRelativeTo(this.parentWin);
         } catch (IllegalComponentStateException var4) {
            this.setLocation(10, 10);
         }

         this.pack();
      }

      super.show();
   }

   public void show() {
      this.show(0, 0);
   }

   private void initComponents() {
      this.jPanel2 = new JPanel();
      this.jPanel1 = new JPanel();
      this.jLabel1 = new JLabel();
      this.tf_program = new JTextField();
      this.jLabel2 = new JLabel();
      this.tf_version = new JTextField();
      this.jLabel3 = new JLabel();
      this.tf_copyright = new JTextField();
      this.jLabel4 = new JLabel();
      this.tf_author = new JTextField();
      this.jLabel5 = new JLabel();
      this.tf_email = new JTextField();
      this.jLabel6 = new JLabel();
      this.tf_web = new JTextField();
      this.jScrollPane1 = new JScrollPane();
      this.textArea = new JTextPane();
      this.jScrollPane2 = new JScrollPane();
      this.jvmArea = new JTextPane();
      this.jPanel3 = new CurvesPanel();
      this.okButton = new JButton();
      this.setDefaultCloseOperation(2);
      this.setTitle("About");
      this.setFont(new Font("Dialog", 0, 12));
      this.setPreferredSize(new Dimension(450, 400));
      this.jPanel2.setBackground(Color.white);
      this.jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.jPanel2.setLayout(new GridBagLayout());
      this.jPanel1.setBackground(Color.white);
      this.jPanel1.setLayout(new GridBagLayout());
      this.jLabel1.setFont(new Font("Dialog", 0, 12));
      this.jLabel1.setText("program");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(0, 4, 0, 2);
      this.jPanel1.add(this.jLabel1, gridBagConstraints);
      this.tf_program.setEditable(false);
      this.tf_program.setFont(new Font("Monospaced", 0, 12));
      this.tf_program.setBorder(null);
      this.tf_program.setMaximumSize(new Dimension(Integer.MAX_VALUE, 17));
      this.tf_program.setMinimumSize(new Dimension(150, 17));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 2);
      this.jPanel1.add(this.tf_program, gridBagConstraints);
      this.jLabel2.setFont(new Font("Dialog", 0, 12));
      this.jLabel2.setText("version");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(0, 4, 0, 2);
      this.jPanel1.add(this.jLabel2, gridBagConstraints);
      this.tf_version.setEditable(false);
      this.tf_version.setFont(new Font("Monospaced", 0, 12));
      this.tf_version.setBorder(null);
      this.tf_version.setMaximumSize(new Dimension(Integer.MAX_VALUE, 17));
      this.tf_version.setMinimumSize(new Dimension(150, 17));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 2);
      this.jPanel1.add(this.tf_version, gridBagConstraints);
      this.jLabel3.setFont(new Font("Dialog", 0, 12));
      this.jLabel3.setText("copyright");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(0, 4, 0, 2);
      this.jPanel1.add(this.jLabel3, gridBagConstraints);
      this.tf_copyright.setEditable(false);
      this.tf_copyright.setFont(new Font("Monospaced", 0, 12));
      this.tf_copyright.setBorder(null);
      this.tf_copyright.setMaximumSize(new Dimension(Integer.MAX_VALUE, 17));
      this.tf_copyright.setMinimumSize(new Dimension(200, 17));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 2);
      this.jPanel1.add(this.tf_copyright, gridBagConstraints);
      this.jLabel4.setFont(new Font("Dialog", 0, 12));
      this.jLabel4.setText("author");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(0, 4, 0, 2);
      this.jPanel1.add(this.jLabel4, gridBagConstraints);
      this.tf_author.setEditable(false);
      this.tf_author.setFont(new Font("Monospaced", 0, 12));
      this.tf_author.setBorder(null);
      this.tf_author.setMaximumSize(new Dimension(Integer.MAX_VALUE, 17));
      this.tf_author.setMinimumSize(new Dimension(150, 17));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 2);
      this.jPanel1.add(this.tf_author, gridBagConstraints);
      this.jLabel5.setFont(new Font("Dialog", 0, 12));
      this.jLabel5.setText("email");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(0, 4, 0, 2);
      this.jPanel1.add(this.jLabel5, gridBagConstraints);
      this.tf_email.setEditable(false);
      this.tf_email.setFont(new Font("Monospaced", 0, 12));
      this.tf_email.setBorder(null);
      this.tf_email.setMaximumSize(new Dimension(Integer.MAX_VALUE, 17));
      this.tf_email.setMinimumSize(new Dimension(150, 17));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 2);
      this.jPanel1.add(this.tf_email, gridBagConstraints);
      this.jLabel6.setFont(new Font("Dialog", 0, 12));
      this.jLabel6.setText("web");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(0, 4, 0, 2);
      this.jPanel1.add(this.jLabel6, gridBagConstraints);
      this.tf_web.setEditable(false);
      this.tf_web.setFont(new Font("Monospaced", 0, 12));
      this.tf_web.setText("http://www.js-home.org");
      this.tf_web.setBorder(null);
      this.tf_web.setMaximumSize(new Dimension(Integer.MAX_VALUE, 17));
      this.tf_web.setMinimumSize(new Dimension(150, 17));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 2);
      this.jPanel1.add(this.tf_web, gridBagConstraints);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      this.jPanel2.add(this.jPanel1, gridBagConstraints);
      this.jScrollPane1.setBackground(Color.white);
      this.jScrollPane1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.textArea.setEditable(false);
      this.textArea.setBorder(null);
      this.jScrollPane1.setViewportView(this.textArea);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 0.5;
      gridBagConstraints.insets = new Insets(10, 0, 0, 0);
      this.jPanel2.add(this.jScrollPane1, gridBagConstraints);
      this.jScrollPane2.setBackground(Color.white);
      this.jScrollPane2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jvmArea.setEditable(false);
      this.jvmArea.setBorder(null);
      this.jvmArea.setContentType("text/html");
      this.jScrollPane2.setViewportView(this.jvmArea);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(10, 0, 0, 0);
      this.jPanel2.add(this.jScrollPane2, gridBagConstraints);
      this.getContentPane().add(this.jPanel2, "Center");
      this.okButton.setText("OK");
      this.okButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            aboutDialog.this.okButtonActionPerformed(evt);
         }
      });
      this.jPanel3.add(this.okButton);
      this.getContentPane().add(this.jPanel3, "South");
      this.pack();
   }

   private void okButtonActionPerformed(ActionEvent evt) {
      this.setVisible(false);
      this.dispose();
   }
}
