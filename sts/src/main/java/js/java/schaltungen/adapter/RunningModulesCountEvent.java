package js.java.schaltungen.adapter;

public class RunningModulesCountEvent {
   public final int count;
   public final boolean singleRun;

   RunningModulesCountEvent(int size, boolean singleRun) {
      this.count = size;
      this.singleRun = singleRun;
   }
}
