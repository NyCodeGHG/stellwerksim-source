package js.java.isolate.sim.gleisbild;

import js.java.tools.actions.AbstractEvent;

public class ModelChangeEvent extends AbstractEvent<gleisbildModel> {
   private gleisbildModel oldModel;
   private gleisbildModel newModel;

   public ModelChangeEvent(gleisbildModel oldModel, gleisbildModel newModel) {
      super(newModel);
      this.oldModel = oldModel;
      this.newModel = newModel;
   }

   public gleisbildModel getOldModel() {
      return this.oldModel;
   }

   public gleisbildModel getNewModel() {
      return this.newModel;
   }
}
