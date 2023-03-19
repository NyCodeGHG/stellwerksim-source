package js.java.tools.actions;

public abstract class AbstractEvent<T> {
   private static long serialnumbercnt = 0L;
   private long serialnumber = 0L;
   protected transient T source;

   protected AbstractEvent(T source) {
      super();
      this.source = source;
      this.serialnumber = ++serialnumbercnt;
   }

   public T getSource() {
      return this.source;
   }

   public final long getSerialNumber() {
      return this.serialnumber;
   }
}
