package js.java.isolate.sim.gleis;

import java.util.LinkedList;

public class gleisGroupImpl extends gleisGroup {
   private final LinkedList<gleis> gleise = new LinkedList();
   private boolean belegt = false;

   @Override
   void updateStatus(gleis gl, int status) {
      if (status == 2 || status == 0) {
         boolean b = false;

         for (gleis gls : this.gleise) {
            b |= gls.getFluentData().getStatus() == 2;
            if (b) {
               break;
            }
         }

         this.belegt = b;
      }
   }

   @Override
   int translateStatus(int status) {
      return !this.belegt || status != 0 && status != 1 ? status : 2;
   }

   @Override
   public void add(gleis gl) {
      gl.gruppe.remove(gl);
      this.gleise.add(gl);
      gl.gruppe = this;
   }

   @Override
   void remove(gleis gl) {
      this.gleise.remove(gl);
   }
}
