package js.java.isolate.sim.gleisbild.gecWorker;

public class gecDisplayEdit extends gecGSelect {
   public gecDisplayEdit() {
      super();
   }

   @Override
   public void deinit(gecBase nextMode) {
      super.deinit(nextMode);
      this.gec.setMassColor(false);
   }

   public void setDarkMode(boolean m) {
      this.gec.setMassColor(m);
   }
}
