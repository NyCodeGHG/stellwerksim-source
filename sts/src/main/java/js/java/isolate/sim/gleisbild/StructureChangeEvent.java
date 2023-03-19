package js.java.isolate.sim.gleisbild;

import js.java.tools.actions.AbstractEvent;

public class StructureChangeEvent extends AbstractEvent<gleisbildModel> {
   private final boolean dataChanged;

   public StructureChangeEvent(gleisbildModel source, boolean dataChanged) {
      super(source);
      this.dataChanged = dataChanged;
   }

   public boolean dataChanged() {
      return this.dataChanged;
   }

   public boolean visibleChange() {
      return !this.dataChanged;
   }
}
