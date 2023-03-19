package js.java.isolate.statusapplet.players;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EnumSet;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.fluentData;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.PaintSaveInterface;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class players_gleis extends gleis {
   public players_gleis(GleisAdapter _theapplet, gleisbildModel _my_gleisbild) {
      super(_theapplet, _my_gleisbild);
   }

   @Override
   public void paint0(PaintSaveInterface panel, Graphics2D g, int xscal, int yscal, int fscal) {
      g.setColor(colors.col_stellwerk_back);
      g.fillRect(0, 0, xscal, yscal);
   }

   @Override
   public void paint2(PaintSaveInterface panel, Graphics2D g, int xscal, int yscal, int fscal) {
      super.paint2(panel, g, xscal, yscal, fscal);
      EnumSet<OCCU_KIND> kk = ((players_fluentData)this.getFluentData()).getKind();
      if (kk.contains(OCCU_KIND.LOCKED)) {
         int x0 = 0;
         int y0 = 0;
         x0 += (int)((double)xscal / 2.0);
         y0 += (int)((double)yscal / 2.0);
         int o = 2 * xscal - 17;
         g.setColor(colors.col_stellwerk_grau_locked);
         g.fillOval(x0 - o / 2 - 3, y0 - o / 2 - 2 - 3, o + 6, o + 6);
      }

      if (kk.contains(OCCU_KIND.HOOKED) || kk.contains(OCCU_KIND.OCCURED)) {
         int x0 = 0;
         int y0 = 0;
         if (kk.contains(OCCU_KIND.HOOKED)) {
            g.setColor(Color.YELLOW);
         } else {
            g.setColor(Color.RED);
         }

         g.drawLine(x0, y0, x0 + 5, y0 + 5);
         g.drawLine(x0, y0 + 5, x0 + 5, y0);
      }
   }

   @Override
   protected fluentData createFluentData() {
      return new players_fluentData(this);
   }

   static {
      new players_colors().setNormalColor();
   }
}
