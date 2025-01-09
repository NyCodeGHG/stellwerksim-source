package js.java.schaltungen.adapter;

import javax.swing.JFrame;
import js.java.schaltungen.UserContext;

public class AbstractTopFrame extends JFrame {
   protected final UserContext uc;

   protected AbstractTopFrame(UserContext uc) {
      this.uc = uc;
      this.setIconImage(uc.getWindowIcon());
   }

   public final String getParameter(String name) {
      return this.uc.getParameter(name);
   }

   public final UserContext getContext() {
      return this.uc;
   }

   public final int getBuild() {
      return this.uc.getBuild();
   }

   public final void showMem(String pos) {
      long heapSize = Runtime.getRuntime().totalMemory();
      long heapMaxSize = Runtime.getRuntime().maxMemory();
      long heapFreeSize = Runtime.getRuntime().freeMemory();
      System.out.println("Mem @ " + pos + " max: " + heapMaxSize + " free: " + heapFreeSize + " cur: " + heapSize);
   }
}
