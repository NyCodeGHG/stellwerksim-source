package js.java.isolate.sim.gleisbild.gecWorker;

public class gecSelect extends gecBase<GecSelectEvent> {
   protected void fireSelectEvent() {
      this.fireEvent(new GecSelectEvent(this));
   }
}
