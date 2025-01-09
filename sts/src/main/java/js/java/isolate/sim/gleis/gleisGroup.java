package js.java.isolate.sim.gleis;

public abstract class gleisGroup {
   abstract void updateStatus(gleis var1, int var2);

   abstract int translateStatus(int var1);

   public abstract void add(gleis var1);

   abstract void remove(gleis var1);
}
