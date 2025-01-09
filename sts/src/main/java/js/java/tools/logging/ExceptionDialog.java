package js.java.tools.logging;

import com.ezware.common.Strings;
import com.ezware.dialog.task.TaskDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.lang.Thread.UncaughtExceptionHandler;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ExceptionDialog implements UncaughtExceptionHandler {
   private String title = "Error during process execution";
   private String heading = "An error occurred during execution:";
   private boolean inShowing = false;

   public void show(String atext, Throwable ex) {
      this.show(null, atext, ex);
   }

   public void show(Component parent, String atext, Throwable ex) {
      if (this.inShowing) {
         System.out.println("Exception in Exception!");
      } else {
         try {
            this.inShowing = true;
            ex.printStackTrace();
            if (atext == null) {
               atext = "";
            }

            String className = ex.getClass().getName();
            String msg = ex.getMessage();
            if (msg == null || msg.isEmpty()) {
               msg = className;
            }

            TaskDialog dialog = new TaskDialog(parent, this.title);
            dialog.setIcon(TaskDialog.StandardIcon.ERROR);
            dialog.setInstruction(this.heading + "\n" + atext);
            dialog.setText(msg);
            dialog.setCommands(TaskDialog.StandardCommand.CANCEL.derive(TaskDialog.makeKey("Close")));
            JTextArea text = new JTextArea();
            text.setEditable(false);
            text.setFont(UIManager.getFont("Label.font"));
            text.setText(Strings.stackStraceAsString(ex));
            text.setCaretPosition(0);
            JScrollPane scroller = new JScrollPane(text);
            scroller.setPreferredSize(new Dimension(400, 200));
            dialog.getDetails().setExpandableComponent(scroller);
            dialog.getDetails().setExpanded(false);
            dialog.setResizable(true);
            dialog.show();
         } catch (Exception var12) {
            var12.printStackTrace();
         } finally {
            this.inShowing = false;
         }
      }
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getHeading() {
      return this.heading;
   }

   public void setHeading(String heading) {
      this.heading = heading;
   }

   public void handle(Throwable e) {
      this.show(null, e);
   }

   public void uncaughtException(Thread t, Throwable e) {
      if (SwingUtilities.isEventDispatchThread()) {
         this.show(null, e);
      } else {
         SwingUtilities.invokeLater(() -> this.show(null, e));
      }
   }
}
