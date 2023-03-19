package js.java.isolate.sim.gleisbild;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class scaleHolder {
   private static final int REFERENCESCALE = 12;
   private final int _scale = 12;
   private final int _fscale = 8;
   private double xscale = 12.0;
   private double yscale = 12.0;
   public static final String[] possibleScales = new String[]{
      "60",
      "80",
      "85",
      "90",
      "95",
      "100",
      "115",
      "130",
      "145",
      "160",
      "175",
      "500",
      "145:115",
      "130:115",
      "130:100",
      "115:100",
      "100:80",
      "145:115",
      "130:115",
      "130:100",
      "115:100"
   };
   public static final String[] publicScales = new String[]{
      "60", "80", "100", "115", "130", "145", "160", "175", "145:115", "130:115", "130:100", "115:100", "100:80", "145:115", "130:115", "130:100", "115:100"
   };

   public scaleHolder() {
      super();
   }

   Graphics2D createScaledGraphics(Graphics2D _g) {
      Graphics2D g = (Graphics2D)_g.create();
      if (this.xscale != 12.0 || this.yscale != 12.0) {
         g.scale(this.xscale / 12.0, this.yscale / 12.0);
      }

      return g;
   }

   Graphics2D createScaledGraphics(Graphics _g) {
      Graphics2D g = (Graphics2D)_g.create();
      if (this.xscale != 12.0 || this.yscale != 12.0) {
         g.scale(this.xscale / 12.0, this.yscale / 12.0);
      }

      return g;
   }

   public int getGleisColOfMouseX(int x) {
      return (int)((double)x / this.xscale);
   }

   public int getGleisRowOfMouseY(int y) {
      return (int)((double)y / this.yscale);
   }

   public int getXOfGleisCol(int col) {
      return (int)((double)col * this.xscale);
   }

   public int getYOfGleisRow(int row) {
      return (int)((double)row * this.yscale);
   }

   public void setScale(double xscale, double yscale) {
      this.xscale = xscale;
      this.yscale = yscale;
   }

   public void setScalePreset(String newS) {
      String[] v = newS.split(":");
      int xsc = Integer.parseInt(v[0]);
      int ysc = Integer.parseInt(v[v.length > 1 ? 1 : 0]);
      double x = this.scaleOfNumber(xsc);
      double y = this.scaleOfNumber(ysc);
      this.setScale(x, y);
   }

   public double scaleOfNumber(int n) {
      return 12.0 * (double)n / 100.0;
   }

   public double numberOfScale(double n) {
      return n * 100.0 / 12.0;
   }

   public double getXScale() {
      return this.xscale;
   }

   public double getYScale() {
      return this.yscale;
   }

   int getPaintingScale() {
      return 12;
   }

   int getFontScale() {
      return 8;
   }
}
