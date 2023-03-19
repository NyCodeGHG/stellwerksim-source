package js.java.tools.gui;

import java.lang.ref.WeakReference;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

public class CheckThreadViolationRepaintManager extends RepaintManager {
   private boolean completeCheck = true;
   private WeakReference<JComponent> lastComponent;

   public CheckThreadViolationRepaintManager(boolean completeCheck) {
      super();
      this.completeCheck = completeCheck;
   }

   public CheckThreadViolationRepaintManager() {
      this(true);
   }

   public boolean isCompleteCheck() {
      return this.completeCheck;
   }

   public void setCompleteCheck(boolean completeCheck) {
      this.completeCheck = completeCheck;
   }

   public synchronized void addInvalidComponent(JComponent component) {
      this.checkThreadViolations(component);
      super.addInvalidComponent(component);
   }

   public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
      this.checkThreadViolations(component);
      super.addDirtyRegion(component, x, y, w, h);
   }

   private void checkThreadViolations(JComponent c) {
      if (!SwingUtilities.isEventDispatchThread() && (this.completeCheck || c.isShowing())) {
         boolean repaint = false;
         boolean fromSwing = false;
         boolean imageUpdate = false;
         StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

         for(StackTraceElement st : stackTrace) {
            if (repaint && st.getClassName().startsWith("javax.swing.") && !st.getClassName().startsWith("javax.swing.SwingWorker")) {
               fromSwing = true;
            }

            if (repaint && "imageUpdate".equals(st.getMethodName())) {
               imageUpdate = true;
            }

            if ("repaint".equals(st.getMethodName()) || "<init>".equals(st.getMethodName())) {
               repaint = true;
               fromSwing = false;
            }
         }

         if (imageUpdate) {
            return;
         }

         if (repaint && !fromSwing) {
            return;
         }

         if (this.lastComponent != null && c == this.lastComponent.get()) {
            return;
         }

         this.lastComponent = new WeakReference(c);
         this.violationFound(c, stackTrace);
      }
   }

   protected void violationFound(JComponent c, StackTraceElement[] stackTrace) {
      System.out.println();
      System.out.println("EDT violation detected");
      System.out.println(c);

      for(StackTraceElement st : stackTrace) {
         System.out.println("\tat " + st);
      }
   }

   public static void install() {
      RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
   }
}
