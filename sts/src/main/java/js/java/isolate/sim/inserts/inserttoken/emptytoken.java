package js.java.isolate.sim.inserts.inserttoken;

public class emptytoken extends inserttoken {
   public emptytoken() {
      super();
   }

   @Override
   public boolean isElement() {
      return true;
   }

   @Override
   public boolean isVisible() {
      return false;
   }
}
