package js.java.isolate.statusapplet.players;

import java.awt.Color;
import java.awt.Graphics2D;
import js.java.isolate.sim.gleis.elementPainterCentered;
import js.java.isolate.sim.gleis.fluentData;
import js.java.isolate.sim.gleis.gleis;

public class players_elementPainter extends elementPainterCentered {
   public players_elementPainter() {
      super();
   }

   private Color getColor(gleis gl) {
      fluentData f = gl.getFluentData();
      return f.getStatus() == 1 ? Color.YELLOW : gleis.colors.col_stellwerk_gleis;
   }

   @Override
   public void paintelement(
      gleis gl, Graphics2D g2, int col, int row, int x, int y, int xscal, int yscal, int fscal, int cc, boolean geradeok, Color colr, int sdp, int ddp
   ) {
      super.paintelement(gl, g2, col, row, x, y, xscal, yscal, fscal, cc, geradeok, colr, sdp, ddp);
   }

   @Override
   public void paintelementL(gleis gl, Graphics2D g2, int col, int row, int xscal, int yscal, int fscal, Color colr) {
      colr = this.getColor(gl);
      super.paintelementL(gl, g2, col, row, xscal, yscal, fscal, colr);
   }
}
