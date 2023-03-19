package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;

@Deprecated
class elementPainterClassic extends elementPainterBase {
   elementPainterClassic() {
      super();
   }

   @Override
   public void paintelement(
      gleis gl, Graphics2D g2, int col, int row, int x, int y, int xscal, int yscal, int fscal, int cc, boolean geradeok, Color colr, int sdp, int ddp
   ) {
      if (g2 != null) {
         int vstatus = gl.getFluentData().getStatus();
         int scx2 = 0;
         int scx1 = 0;
         int scy2 = 0;
         int scy1 = 0;
         int dcx2;
         int dcx1 = dcx2 = (x - col) * xscal;
         int dcy2;
         int dcy1 = dcy2 = (y - row) * yscal;
         int sx1;
         int dx1 = sx1 = (int)((double)(-1 * xscal) / 6.0 + (double)xscal / 2.0);
         int sx2;
         int dx2 = sx2 = (int)((double)(1 * xscal) / 6.0 + (double)xscal / 2.0);
         int sy1;
         int dy1 = sy1 = (int)((double)(-1 * yscal) / 6.0 + (double)yscal / 2.0);
         int sy2;
         int dy2 = sy2 = (int)((double)(1 * yscal) / 6.0 + (double)yscal / 2.0);
         g2.setColor(colr);
         Polygon opgn = null;
         Polygon pgn = new Polygon();
         Polygon vpgn = null;
         if (gl.verbundgleis != null && gleis.ALLE_GLEISE.matches(gl.telement)) {
            vstatus = 0;
            if (gl.getFluentData().getStatus() == 2 || gl.verbundgleis.getFluentData().getStatus() == 2) {
               vstatus = 2;
            } else if (gl.getFluentData().getStatus() == 1 || gl.verbundgleis.getFluentData().getStatus() == 1) {
               vstatus = 1;
            } else if (gl.getFluentData().getStatus() == 3 || gl.verbundgleis.getFluentData().getStatus() == 3) {
               vstatus = 3;
            } else if (gl.getFluentData().getStatus() == 4 || gl.verbundgleis.getFluentData().getStatus() == 4) {
               vstatus = 4;
            }

            vpgn = new Polygon();
            vpgn.addPoint(scx1 + sx1 + 1, scy1 + sy1 + 1);
            vpgn.addPoint(scx1 + sx2 + 1 - 1, scy1 + sy1 + 1);
            vpgn.addPoint(scx1 + sx2 + 1 - 1, scy1 + sy2 + 1 - 1);
            vpgn.addPoint(scx1 + sx1 + 1, scy1 + sy2 + 1 - 1);
         }

         if (gl.verbundgleis == null || gl.firstverbund) {
            pgn.addPoint(scx1 + sx1, scy1 + sy1);
            pgn.addPoint(scx1 + sx2 + 1, scy1 + sy1);
            pgn.addPoint(scx1 + sx2 + 1, scy1 + sy2 + 1);
            pgn.addPoint(scx1 + sx1, scy1 + sy2 + 1);
            g2.fillPolygon(pgn);
         }

         pgn = new Polygon();
         pgn.addPoint(dcx1 + sx1, dcy1 + sy1);
         pgn.addPoint(dcx1 + sx2 + 1, dcy1 + sy1);
         pgn.addPoint(dcx1 + sx2 + 1, dcy1 + sy2 + 1);
         pgn.addPoint(dcx1 + sx1, dcy1 + sy2 + 1);
         g2.fillPolygon(pgn);
         switch(sdp) {
            case 1:
               scx1 += sx2;
               scy1 += sy1;
               scx2 += sx2;
               scy2 += sy2;
               break;
            case 2:
               scx1 += sx1;
               scy1 += sy2;
               scx2 += sx2;
               scy2 += sy2;
               break;
            case 3:
               scx1 += sx1;
               scy1 += sy1;
               scx2 += sx1;
               scy2 += sy2;
               break;
            case 4:
               scx1 += sx1;
               scy1 += sy1;
               scx2 += sx2;
               scy2 += sy1;
         }

         switch(ddp) {
            case 1:
               dcx1 += dx2;
               dcy1 += dy1;
               dcx2 += dx2;
               dcy2 += dy2;
               break;
            case 2:
               dcx1 += dx1;
               dcy1 += dy2;
               dcx2 += dx2;
               dcy2 += dy2;
               break;
            case 3:
               dcx1 += dx1;
               dcy1 += dy1;
               dcx2 += dx1;
               dcy2 += dy2;
               break;
            case 4:
               dcx1 += dx1;
               dcy1 += dy1;
               dcx2 += dx2;
               dcy2 += dy1;
         }

         pgn = new Polygon();
         opgn = new Polygon();
         pgn.addPoint(scx1, scy1);
         opgn.addPoint(scx1, scy1);
         if (scx1 == scx2) {
            pgn.addPoint(scx2, scy2 + 1);
         } else {
            pgn.addPoint(scx2 + 1, scy2);
         }

         opgn.addPoint(scx2, scy2);
         if (dcx1 > scx1 && dcy1 > scy1 || dcx1 < scx1 && dcy1 < scy1) {
            pgn.addPoint(dcx1, dcy1);
            opgn.addPoint(dcx1, dcy1);
         }

         if (dcx1 == dcx2) {
            pgn.addPoint(dcx2, dcy2 + 1);
         } else {
            pgn.addPoint(dcx2 + 1, dcy2);
         }

         opgn.addPoint(dcx2, dcy2);
         if ((dcx1 <= scx1 || dcy1 <= scy1) && (dcx1 >= scx1 || dcy1 >= scy1)) {
            pgn.addPoint(dcx1, dcy1);
            opgn.addPoint(dcx1, dcy1);
         }

         g2.fillPolygon(pgn);
         if (!gleis.ALLE_BSTTRENNER.matches(gl.telement)) {
            if (scx1 != scx2) {
               int dif = (scx2 - scx1) / 4;
               scx1 += dif;
               scx2 -= dif - 1;
            }

            if (scy1 != scy2) {
               int dif = (scy2 - scy1) / 4;
               scy1 += dif;
               scy2 -= dif - 1;
            }

            if (dcx1 != dcx2) {
               int dif = (dcx2 - dcx1) / 4;
               dcx1 += dif;
               dcx2 -= dif - 1;
            }

            if (dcy1 != dcy2) {
               int dif = (dcy2 - dcy1) / 4;
               dcy1 += dif;
               dcy2 -= dif - 1;
            }

            if (gl.fdata.power_off) {
               g2.setColor(gleis.colors.col_stellwerk_frei);
            } else if (vstatus == 2) {
               g2.setColor(gleis.colors.col_stellwerk_belegt);
            } else if ((vstatus == 3 || vstatus == 4) && gl.extrastatus) {
               g2.setColor(gleis.blinkon ? gleis.colors.col_stellwerk_reserviert : gleis.colors.col_stellwerk_frei);
            } else if (vstatus == 1 || gl.extrastatus) {
               g2.setColor(gleis.colors.col_stellwerk_reserviert);
            } else if (vstatus == 3) {
               g2.setColor(gleis.blinkon ? gleis.colors.col_stellwerk_reserviert : gleis.colors.col_stellwerk_frei);
            } else {
               g2.setColor(gleis.colors.col_stellwerk_frei);
            }

            if (gl.telement == gleis.ELEMENT_WEICHEUNTEN || gl.telement == gleis.ELEMENT_WEICHEOBEN) {
               g2.setColor(gleis.colors.col_stellwerk_frei);
               if (!gl.fdata.power_off) {
                  if (cc != 1 && cc != 2) {
                     if (gl.fdata.stellung == gleisElements.ST_WEICHE_GERADE && (geradeok || vstatus == 3 || vstatus == 4 || vstatus == 1 || vstatus == 2)) {
                        g2.setColor(
                           vstatus != 3 && vstatus != 4
                              ? (vstatus == 2 ? gleis.colors.col_stellwerk_belegt : gleis.colors.col_stellwerk_reserviert)
                              : (gleis.blinkon ? gleis.colors.col_stellwerk_reserviert : gleis.colors.col_stellwerk_frei)
                        );
                     } else if (gl.fdata.stellung == gleisElements.ST_WEICHE_ABZWEIG
                        && (vstatus == 3 || vstatus == 4 || vstatus == 1 || vstatus == 2)
                        && !geradeok) {
                        g2.setColor(
                           vstatus != 3 && vstatus != 4
                              ? (vstatus == 2 ? gleis.colors.col_stellwerk_belegt : gleis.colors.col_stellwerk_reserviert)
                              : (gleis.blinkon ? gleis.colors.col_stellwerk_reserviert : gleis.colors.col_stellwerk_frei)
                        );
                     }
                  } else if (gl.fdata.stellung == gleisElements.ST_WEICHE_ABZWEIG) {
                     g2.setColor(
                        vstatus != 3 && vstatus != 4
                           ? (vstatus == 2 ? gleis.colors.col_stellwerk_belegt : gleis.colors.col_stellwerk_reserviert)
                           : (gleis.blinkon ? gleis.colors.col_stellwerk_reserviert : gleis.colors.col_stellwerk_frei)
                     );
                  }
               }
            }

            pgn = new Polygon();
            pgn.addPoint(scx1, scy1);
            pgn.addPoint(scx2, scy2);
            if ((dcx1 <= scx1 || dcy1 <= scy1) && (dcx1 >= scx1 || dcy1 >= scy1)) {
               pgn.addPoint(dcx2, dcy2);
               pgn.addPoint(dcx1, dcy1);
            } else {
               pgn.addPoint(dcx1, dcy1);
               pgn.addPoint(dcx2, dcy2);
            }

            if (gl.telement == gleis.ELEMENT_KREUZUNG && !gl.fdata.power_off) {
               g2.setColor(gleis.colors.col_stellwerk_reserviert);
            }

            if (gl.telement != gleis.ELEMENT_KREUZUNGBRUECKE) {
               g2.fillPolygon(pgn);
            }

            if (gl.verbundgleis != null) {
               if (!gl.firstverbund) {
                  if (vpgn != null) {
                     g2.fillPolygon(vpgn);
                  }
               } else {
                  g2.drawLine(dcx1, dcy1, dcx2 - (dcy1 == dcy2 ? 1 : 0), dcy2 - (dcx1 == dcx2 ? 1 : 0));
               }
            }
         }
      }
   }

   @Override
   void paintelementL(gleis gl, Graphics2D g2, int col, int row, int xscal, int yscal, int fscal, Color colr) {
   }

   @Override
   boolean needExtraRight() {
      return true;
   }
}
