package js.java.isolate.sim.gleisbild.gecWorker;

public class gecSelect extends gecBase<GecSelectEvent> {
   public gecSelect() {
      super();
   }

   protected void fireSelectEvent() {
      this.fireEvent(new GecSelectEvent(this));
   }
}
