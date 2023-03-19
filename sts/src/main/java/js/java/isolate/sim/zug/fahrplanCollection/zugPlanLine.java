package js.java.isolate.sim.zug.fahrplanCollection;

public class zugPlanLine {
   public int azid;
   public String an;
   public String ab;
   public String zielGleis;
   public String befehlGleis;
   public boolean befehlGleisAccepted;
   public boolean flagA;
   public boolean flagD;
   public boolean flagL;
   public boolean flagL_running;
   public boolean flagE;
   public boolean flagK;
   public boolean flagF;
   public boolean flagR;
   public boolean flagW;
   public boolean flagW_running;
   public boolean flagG;
   public boolean flagWlokAmEnde = false;
   public String flagZiel = null;
   public int flagZielZid = 0;
   public String kErwartet = null;
   public int kErwartetZid = 0;
   public String hinweistext;
   public boolean gMode = false;

   public zugPlanLine() {
      super();
   }
}
