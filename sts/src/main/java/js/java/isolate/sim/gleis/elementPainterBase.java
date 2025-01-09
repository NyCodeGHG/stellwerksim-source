package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

public abstract class elementPainterBase {
   void paintelement(gleis gl, Graphics2D g2, int col, int row, int xscal, int yscal, int fscal, Color colr) {
      if (g2 != null) {
         int scx1 = col * xscal;
         int scy1 = row * yscal;
         int sx1 = (int)((double)(-1 * xscal) / 6.0 + (double)xscal / 2.0);
         int sx2 = (int)((double)(1 * xscal) / 6.0 + (double)xscal / 2.0);
         int sy1 = (int)((double)(-1 * yscal) / 6.0 + (double)yscal / 2.0);
         int sy2 = (int)((double)(1 * yscal) / 6.0 + (double)yscal / 2.0);
         g2.setColor(colr);
         Polygon pgn = new Polygon();
         pgn.addPoint(scx1 + sx1, scy1 + sy1);
         pgn.addPoint(scx1 + sx2 + 1, scy1 + sy1);
         pgn.addPoint(scx1 + sx2 + 1, scy1 + sy2 + 1);
         pgn.addPoint(scx1 + sx1, scy1 + sy2 + 1);
         g2.fillPolygon(pgn);
      }
   }

   abstract void paintelement(
      gleis var1,
      Graphics2D var2,
      int var3,
      int var4,
      int var5,
      int var6,
      int var7,
      int var8,
      int var9,
      int var10,
      boolean var11,
      Color var12,
      int var13,
      int var14
   );

   abstract void paintelementL(gleis var1, Graphics2D var2, int var3, int var4, int var5, int var6, int var7, Color var8);

   @Deprecated
   abstract boolean needExtraRight();
}
