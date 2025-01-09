package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.IllegalPathStateException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.tools.JarvisMarch;

class elementPainterCenteredLight extends elementPainterCentered {
   @Override
   public void paintelementL(gleis gl, Graphics2D g2, int col, int row, int xscal, int yscal, int fscal, Color colr) {
      int vstatus = gl.getFluentData().getStatus();
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
      }

      vstatus = gl.gruppe.translateStatus(vstatus);
      super.paintelementL(gl, g2, col, row, xscal, yscal, fscal, colr);
      if (!gleis.ALLE_BSTTRENNER.matches(gl.telement)) {
         if (gl.telement.paintLight()) {
            if (gl.telement == gleis.ELEMENT_WEICHEOBEN || gl.telement == gleis.ELEMENT_WEICHEUNTEN) {
               if (this.w1 != null && this.w2 != null && this.w3 != null) {
                  if (this.w1.getCol() == this.w2.getCol()) {
                     this.w3 = null;
                  } else if (this.w2.getCol() == this.w3.getCol()) {
                     this.w1 = this.w3;
                     this.w3 = null;
                  } else if (this.w1.getCol() == this.w3.getCol()) {
                     this.w2 = this.w3;
                     this.w3 = null;
                  }
               } else {
                  this.w1 = null;
               }
            }

            this.lightCnt[0] = 0;
            this.lightCnt[1] = 0;
            Color mcol = gleis.colors.col_stellwerk_frei;
            int mtype = 0;
            this.connectorCnt = 0;
            Iterator<gleis.nachbarGleis> it = gl.p_getNachbarn();

            while (it.hasNext()) {
               gleis.nachbarGleis gl2 = (gleis.nachbarGleis)it.next();
               gleis dgl = gl2.gl;
               int tstatus = vstatus;
               int vstatus2 = vstatus;
               if ((gl.telement == gleis.ELEMENT_WEICHEOBEN || gl.telement == gleis.ELEMENT_WEICHEUNTEN) && (this.w1 == dgl || this.w2 == dgl)) {
                  if (gl.fdata.stellung == gleisElements.ST_WEICHE_GERADE) {
                     if (dgl.getRow() != gl.getRow()) {
                        tstatus = 0;
                     } else if (vstatus == 0) {
                        tstatus = 1;
                     }

                     vstatus2 = tstatus;
                  } else if (gl.fdata.stellung == gleisElements.ST_WEICHE_ABZWEIG) {
                     if (dgl.getRow() == gl.getRow()) {
                        tstatus = 0;
                     } else if (vstatus == 0) {
                        tstatus = 1;
                        if (gl.telement == gleis.ELEMENT_WEICHEOBEN) {
                           vstatus2 = tstatus;
                        }
                     }
                  }
               }

               int ltype = 0;
               Color lcol = gleis.colors.col_stellwerk_frei;
               if (gl.fdata.power_off) {
                  lcol = gleis.colors.col_stellwerk_frei;
               } else if (tstatus == 2) {
                  lcol = gleis.colors.col_stellwerk_belegt;
                  ltype = 1;
               } else if (tstatus != 3 && tstatus != 4) {
                  if (tstatus == 1) {
                     lcol = gleis.colors.col_stellwerk_reserviert;
                     ltype = 1;
                  } else {
                     lcol = gleis.colors.col_stellwerk_frei;
                  }
               } else {
                  lcol = gleis.blinkon ? gleis.colors.col_stellwerk_reserviert : gleis.colors.col_stellwerk_frei;
                  ltype = 1;
               }

               if (gl.fdata.power_off) {
                  mcol = gleis.colors.col_stellwerk_frei;
               } else if (vstatus2 == 2) {
                  mcol = gleis.colors.col_stellwerk_belegt;
                  mtype = 1;
               } else if (vstatus2 != 3 && vstatus2 != 4) {
                  if (vstatus2 == 1) {
                     mcol = gleis.colors.col_stellwerk_reserviert;
                     mtype = 1;
                  }
               } else {
                  mcol = gleis.blinkon ? gleis.colors.col_stellwerk_reserviert : gleis.colors.col_stellwerk_frei;
                  mtype = 1;
               }

               this.scx1 = this.scx2 = col * xscal;
               this.scy1 = this.scy2 = row * yscal;
               this.dcx1 = this.dcx2 = (dgl.getCol() - gl.getCol() + col) * xscal;
               this.dcy1 = this.dcy2 = (dgl.getRow() - gl.getRow() + row) * yscal;
               this.calcL(gl2.sdp, gl2.ddp);
               this.vbPoly2(gl2.sdp, gl2.ddp);
               Polygon pgn = new Polygon();
               pgn.addPoint(this.scx1, this.scy1);
               pgn.addPoint(this.scx2, this.scy2);
               pgn.addPoint(this.dcx2, this.dcy2);
               pgn.addPoint(this.dcx1, this.dcy1);
               this.light[ltype][this.lightCnt[ltype]] = pgn;
               this.lightCol[ltype][this.lightCnt[ltype]] = lcol;
               this.lightCnt[ltype]++;
            }

            if (this.connectorLCnt > 0) {
               if (this.connectorLCnt >= 5) {
                  JarvisMarch j = new JarvisMarch();
                  this.connectorLCnt = j.computeHull(this.connectorL, this.connectorLCnt);
               }

               Polygon pgn = new Polygon();

               for (int i = 0; i < this.connectorLCnt; i++) {
                  int x = this.connectorL[i].x;
                  int y = this.connectorL[i].y;
                  pgn.addPoint(x, y);
               }

               this.light[mtype][this.lightCnt[mtype]] = pgn;
               this.lightCol[mtype][this.lightCnt[mtype]] = mcol;
               this.lightCnt[mtype]++;
            }

            gl.setSmooth(g2, false, 1);

            for (int t = 0; t < 2; t++) {
               for (int c = 0; c < this.lightCnt[t]; c++) {
                  g2.setColor(this.lightCol[t][c]);

                  try {
                     g2.fillPolygon(this.light[t][c]);
                  } catch (IllegalPathStateException var20) {
                     System.out.println("Caught: " + var20.getMessage());
                     var20.printStackTrace();
                     Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var20);
                  }
               }
            }
         }
      }
   }

   @Override
   boolean needExtraRight() {
      return false;
   }
}
