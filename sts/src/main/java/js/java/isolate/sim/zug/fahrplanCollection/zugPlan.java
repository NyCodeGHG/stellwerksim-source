package js.java.isolate.sim.zug.fahrplanCollection;

import java.util.LinkedList;
import js.java.isolate.sim.sim.fahrplanRenderer.redirektInfoContainer;
import js.java.isolate.sim.zug.zug;

public class zugPlan {
   public final int zid;
   public final String name;
   public boolean halt = false;
   public boolean anyDirection = false;
   public String haltsignal = null;
   public boolean langsam = false;
   public boolean warten = false;
   public boolean notbremsung = false;
   public String von = null;
   public boolean vonGeaendert = false;
   public String nach = null;
   public boolean falseAusfahrt = false;
   public boolean umleitungEinfahrt = false;
   public boolean umleitungAusfahrt = false;
   public redirektInfoContainer umleitungText = null;
   public LinkedList<zugPlanLine> plan = new LinkedList();
   public zugPlan follower = null;
   public String vorZug = null;
   public int vorZugZid = 0;
   public String userText = "";

   public zugPlan(zug z) {
      super();
      this.zid = z.getZID_num();
      this.name = z.getSpezialName();
   }
}
