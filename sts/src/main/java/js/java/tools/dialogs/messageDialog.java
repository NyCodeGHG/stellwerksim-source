package js.java.tools.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.IllegalComponentStateException;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import js.java.tools.gui.border.DropShadowBorder;

public class messageDialog extends JDialog {
   public static final int RET_CANCEL = 0;
   public static final int RET_OK = 1;
   protected Frame parentWin;
   protected int returnStatus = 0;
   private JPanel buttonPanel;
   private JPanel jPanel1;
   private JPanel jPanel2;
   private JScrollPane jScrollPane1;
   protected JTextPane textArea;

   protected messageDialog(Frame parent, boolean modal, String title) {
      super(parent, modal);
      this.initComponents();
      this.setTitle(title);
   }

   protected void setText(String text) {
      this.textArea.setText(text);
      this.textArea.setCaretPosition(0);
   }

   protected void addButton(JButton but) {
      this.buttonPanel.add(but);
   }

   protected void doClose() {
      this.doClose(0);
   }

   protected void doClose(int retStatus) {
      this.returnStatus = retStatus;
      this.setVisible(false);
      this.dispose();
   }

   public void close() {
      this.doClose(0);
   }

   protected void closePressed() {
      this.doClose();
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
      this.show(400, 200);
   }

   public int getReturnStatus() {
      return this.returnStatus;
   }

   private void initComponents() {
      this.jPanel2 = new JPanel();
      this.jPanel1 = new JPanel();
      this.jScrollPane1 = new JScrollPane();
      this.textArea = new JTextPane();
      this.buttonPanel = new JPanel();
      this.setDefaultCloseOperation(0);
      this.setLocationByPlatform(true);
      this.setMinimumSize(new Dimension(200, 200));
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent evt) {
            messageDialog.this.closeDialog(evt);
         }
      });
      this.jPanel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanel2.setLayout(new BorderLayout());
      this.jPanel1.setBorder(new DropShadowBorder(true, true, true, true));
      this.jPanel1.setLayout(new BorderLayout());
      this.jScrollPane1.setBorder(null);
      this.textArea.setBackground(SystemColor.info);
      this.textArea.setBorder(null);
      this.textArea.setEditable(false);
      this.textArea.setFont(this.textArea.getFont().deriveFont((float)this.textArea.getFont().getSize() + 2.0F));
      this.jScrollPane1.setViewportView(this.textArea);
      this.jPanel1.add(this.jScrollPane1, "Center");
      this.jPanel2.add(this.jPanel1, "Center");
      this.buttonPanel.setLayout(new GridLayout(1, 0));
      this.jPanel2.add(this.buttonPanel, "South");
      this.getContentPane().add(this.jPanel2, "Center");
      this.pack();
   }

   private void closeDialog(WindowEvent evt) {
      this.closePressed();
   }
}
