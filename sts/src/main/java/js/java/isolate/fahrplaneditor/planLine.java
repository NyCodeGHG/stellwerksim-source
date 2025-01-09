package js.java.isolate.fahrplaneditor;

import js.java.isolate.sim.flagdata;

class planLine {
   private final fahrplaneditor my_main;
   public int aaid;
   public int azid;
   public String gleis;
   public String an;
   public String ab;
   public int ein_enr;
   public int aus_enr;
   public String hinweise;
   public String thematag;
   public flagdata flags;

   planLine(fahrplaneditor m, int n) {
      this.my_main = m;
      String prefix = this.my_main.getParameter("testprefix");
      if (prefix == null) {
         prefix = "";
      }

      this.aaid = Integer.parseInt(this.my_main.getParameter(prefix + "aaid" + n));
      this.azid = Integer.parseInt(this.my_main.getParameter(prefix + "azid" + n));
      this.ein_enr = Integer.parseInt(this.my_main.getParameter(prefix + "ein_enr" + n));
      this.aus_enr = Integer.parseInt(this.my_main.getParameter(prefix + "aus_enr" + n));
      this.gleis = this.my_main.getParameter(prefix + "gleis" + n);
      this.an = this.my_main.getParameter(prefix + "an" + n);
      this.ab = this.my_main.getParameter(prefix + "ab" + n);
      String fl = this.my_main.getParameter(prefix + "flags" + n);
      if (fl == null) {
         fl = "";
      }

      String fld = this.my_main.getParameter(prefix + "flagdata" + n);
      if (fld == null) {
         fld = "";
      }

      String flp = this.my_main.getParameter(prefix + "flagparam" + n);
      if (flp == null) {
         flp = "";
      }

      this.hinweise = this.my_main.getParameter(prefix + "hinweise" + n);
      this.thematag = this.my_main.getParameter(prefix + "thematag" + n);
      this.flags = new flagdata(fl, fld, flp);
   }
}
