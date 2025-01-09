package js.java.isolate.sim.zug;

public class frozenZug {
   private String von;
   private String gleis;
   private String nach;
   private String name;
   private boolean redirect;
   private String markNum;
   int zid;
   boolean mytrain;
   boolean exitMode;
   boolean fertig;
   boolean visible;
   boolean ambahnsteig;
   long ab;
   long an;
   int verspaetung;
   private String abfahrt;

   frozenZug() {
   }

   void update(zug z) {
      this.zid = z.zid;
      this.mytrain = z.mytrain;
      this.exitMode = z.exitMode;
      this.fertig = z.fertig;
      this.visible = z.visible;
      this.ambahnsteig = z.ambahnsteig;
      this.an = z.an;
      this.ab = z.ab;
      this.verspaetung = z.verspaetung;
      this.name = z.getName();
      this.von = z.getVon();
      this.nach = z.getNach();
      this.gleis = z.getGleis();
      this.redirect = z.isRedirect();
      this.markNum = z.getMarkNum();
      this.abfahrt = z.getAbfahrt();
   }

   String getVon() {
      return this.von;
   }

   String getNach() {
      return this.nach;
   }

   String getGleis() {
      return this.gleis;
   }

   String getName() {
      return this.name;
   }

   boolean isRedirect() {
      return this.redirect;
   }

   String getMarkNum() {
      return this.markNum;
   }

   String getAbfahrt() {
      return this.abfahrt;
   }

   int compareTo(frozenZug z) {
      int r;
      if (this.an == z.an) {
         if (this.ab == z.ab) {
            r = this.name.compareToIgnoreCase(z.name);
            if (r == 0) {
               r = this.zid - z.zid;
            }
         } else {
            r = this.ab > z.ab ? 1 : -1;
         }
      } else {
         r = this.an > z.an ? 1 : -1;
      }

      return r;
   }
}
