package js.java.isolate.sim.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import js.java.isolate.sim.stellwerk_editor;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.panels.actionevents.progressEvent;
import js.java.isolate.sim.panels.actionevents.statusEvent;
import js.java.tools.actions.AbstractEvent;

public class statusPanel extends basePanel {
   private boolean overwriteLastStatus = false;
   private long lastSerial = 0L;
   private String statusText = "";
   private int statusCPos = 0;
   private JProgressBar queueFill;
   private JTextArea statusLine;
   private JScrollPane statusScroller;

   public statusPanel(gleisbildEditorControl glb, stellwerk_editor e) {
      super(glb, e);
      this.initComponents();
      e.registerListener(6, this);
      e.registerListener(4, this);
   }

   @Override
   public void action(AbstractEvent e) {
      if (e instanceof statusEvent) {
         if (e.getSerialNumber() > this.lastSerial) {
            this.showStatus(((statusEvent)e).getStatus());
            this.lastSerial = e.getSerialNumber();
         } else {
            this.statusLine.setText(this.statusText);
            this.statusLine.setCaretPosition(this.statusCPos);
         }
      } else if (e instanceof progressEvent) {
         this.setProgress(((progressEvent)e).getInt());
      }
   }

   private void showStatus(String s) {
      if (s != null) {
         boolean ols = this.overwriteLastStatus;
         GregorianCalendar cal = new GregorianCalendar();
         DateFormat df = DateFormat.getTimeInstance(2);
         if (s.startsWith("-")) {
            this.overwriteLastStatus = true;
            s = s.substring(1);
         } else {
            this.overwriteLastStatus = false;
         }

         int cpos;
         if (ols) {
            cpos = this.statusLine.getCaretPosition();
            String t = this.statusLine.getText();
            int l = t.lastIndexOf(10, t.length() - 2);
            if (l < 0) {
               l = 0;
            }

            this.statusText = t.substring(0, l + 1) + df.format(cal.getTime()) + ": " + s + "\n";
            this.statusLine.setText(this.statusText);
         } else {
            cpos = this.statusLine.getText().length();
            this.statusText = this.statusLine.getText() + df.format(cal.getTime()) + ": " + s + "\n";
            this.statusLine.setText(this.statusText);
         }

         this.statusCPos = Math.min(cpos, this.statusLine.getText().length());
         this.statusLine.setCaretPosition(this.statusCPos);
      }
   }

   public void setProgress(int p) {
      if (p > 0) {
         this.queueFill.setIndeterminate(false);
         this.queueFill.setValue(p);
      } else {
         this.queueFill.setIndeterminate(true);
      }
   }

   private void initComponents() {
      this.statusScroller = new JScrollPane();
      this.statusLine = new JTextArea();
      this.queueFill = new JProgressBar();
      this.setBorder(BorderFactory.createTitledBorder("Statusprotokoll"));
      this.setLayout(new BorderLayout());
      this.statusScroller.setAutoscrolls(true);
      this.statusScroller.setMinimumSize(new Dimension(22, 28));
      this.statusScroller.setPreferredSize(new Dimension(22, 28));
      this.statusLine.setEditable(false);
      this.statusLine.setRows(1);
      this.statusLine.setBorder(BorderFactory.createBevelBorder(1));
      this.statusScroller.setViewportView(this.statusLine);
      this.add(this.statusScroller, "Center");
      this.add(this.queueFill, "South");
   }
}
