package js.java.isolate.sim.gleis.mass;

import js.java.isolate.sim.gleis.gleis;
import js.java.tools.ColorText;

public abstract class massBase {
   private massBase.MasstabItem[] t = null;

   protected abstract int calcLaengeImpl(int var1, int var2);

   protected abstract int calcMaxSpeedImpl(int var1);

   protected abstract String[] getMasstabLabels();

   public abstract boolean isCompatible(massBase var1);

   protected abstract int[] getMasstabValues();

   public final massBase.MasstabItem[] getMasstabList() {
      if (this.t == null) {
         String[] s = this.getMasstabLabels();
         int[] is = this.getMasstabValues();
         this.t = new massBase.MasstabItem[Math.min(s.length, gleis.colors.col_stellwerk_masstab.length)];

         for (int i = 0; i < this.t.length; i++) {
            this.t[i] = new massBase.MasstabItem();
            this.t[i].label = new ColorText(s[i], gleis.colors.col_stellwerk_masstab[i]);
            this.t[i].value = is[i];
         }
      }

      return this.t;
   }

   public final int getColindexOfValue(int masstab) {
      int[] is = this.getMasstabValues();
      int cnt = 0;

      for (int i : is) {
         if (i == masstab) {
            return cnt;
         }

         cnt++;
      }

      return 0;
   }

   public final int calcLaenge(int masstab, int laenge) {
      int l = this.calcLaengeImpl(masstab, laenge);
      if (l < 4) {
         l = 4;
      }

      return l;
   }

   public final int calcMaxSpeed(int masstab) {
      return this.calcMaxSpeedImpl(masstab);
   }

   public static class MasstabItem {
      public ColorText label;
      public int value;
   }
}
