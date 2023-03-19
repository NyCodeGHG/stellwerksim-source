package js.java.isolate.statusapplet.common;

import java.util.Date;
import java.util.LinkedList;
import js.java.isolate.sim.flagdata;

public class szug_plan_base {
   public status_zug_base parent = null;
   public Date an = null;
   public Date ab = null;
   public int aid = 0;
   public int azid = 0;
   public String gleis = null;
   public flagdata flags = null;
   public int ein_enr = 0;
   public int aus_enr = 0;
   public LinkedList<Integer> flagdatazid = new LinkedList();

   public szug_plan_base() {
      super();
   }
}
