package js.java.isolate.sim.gleisbild.gecWorker;

import java.util.LinkedList;
import java.util.List;
import js.java.isolate.sim.gleis.gleis;

public class gecHLineSelect extends gecLineSelect {
   @Override
   public void insert() {
      if (this.line >= 0) {
         this.gec.getModel().insertRow(this.line);
      }

      this.gec.getModel().allOff();
   }

   @Override
   public void delete() {
      if (this.line >= 0) {
         this.gec.getModel().deleteRow(this.line);
      }

      this.gec.getModel().allOff();
   }

   @Override
   protected void setLine(gleis gl) {
      this.line = gl.getRow();
   }

   @Override
   protected List<gleis> getSel(gleis gl) {
      LinkedList<gleis> ret = new LinkedList();

      for (int i = 0; i < this.gec.getModel().getGleisWidth(); i++) {
         ret.add(this.gec.getModel().getXY_null(i, gl.getRow()));
      }

      return ret;
   }
}
