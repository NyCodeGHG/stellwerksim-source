package js.java.isolate.sim.gleis;

public class gleisGroupNull extends gleisGroup {
   @Override
   void updateStatus(gleis gl, int status) {
   }

   @Override
   int translateStatus(int status) {
      return status;
   }

   @Override
   public void add(gleis gl) {
      gl.gruppe = this;
   }

   @Override
   void remove(gleis gl) {
   }
}
