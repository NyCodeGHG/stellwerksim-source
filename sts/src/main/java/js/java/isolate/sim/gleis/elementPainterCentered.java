package js.java.isolate.sim.gleis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.tools.JarvisMarch.Point;

public class elementPainterCentered extends elementPainterBase {
   protected final int LSIZE = 1;
   protected final int OSIZE = 3;
   protected int scx1;
   protected int scx2;
   protected int sx1;
   protected int sx2;
   protected int scy1;
   protected int scy2;
   protected int sy1;
   protected int sy2;
   protected int dcx1;
   protected int dcx2;
   protected int dx1;
   protected int dx2;
   protected int dcy1;
   protected int dcy2;
   protected int dy1;
   protected int dy2;
   protected Point[] connector = new Point[4];
   protected Point[] connectorL = new Point[8];
   protected int connectorCnt = 0;
   protected int connectorLCnt = 0;
   protected Polygon[][] light = new Polygon[2][10];
   protected Color[][] lightCol = new Color[2][10];
   protected int[] lightCnt = new int[2];
   protected gleis w1 = null;
   protected gleis w2 = null;
   protected gleis w3 = null;

   protected boolean direkt11(int sdp, int ddp) {
      int k = sdp * 10 + ddp;
      return k == 12 || k == 24 || k == 21 || k == 42 || k == 13 || k == 31 || k == 34 || k == 43;
   }

   protected boolean direkt12(int sdp, int ddp) {
      int k = sdp * 10 + ddp;
      return k == 23 || k == 32 || k == 14 || k == 41;
   }

   protected void swapS() {
      int t = this.scx1;
      this.scx1 = this.scx2;
      this.scx2 = t;
      t = this.scy1;
      this.scy1 = this.scy2;
      this.scy2 = t;
   }

   protected boolean switchSifneeded(int sdp, int lastsdp, boolean swaped) {
      if (swaped != this.direkt11(lastsdp, sdp)) {
         this.swapS();
         swaped = true;
      }

      return swaped;
   }

   protected void vbPoly(int sdp, int ddp) {
      switch (sdp) {
         case 1:
            this.scy2++;
            break;
         case 2:
            this.scx2++;
            break;
         case 3:
            this.scx1++;
            this.scx2++;
            this.scy2++;
            break;
         case 4:
            this.scx2++;
            this.scy1++;
            this.scy2++;
      }

      switch (ddp) {
         case 1:
            this.dcy2++;
            break;
         case 2:
            this.dcx2++;
            break;
         case 3:
            this.dcx2++;
            this.dcx1++;
            this.dcy2++;
            break;
         case 4:
            this.dcx2++;
            this.dcy1++;
            this.dcy2++;
      }
   }

   protected void vbPoly2(int sdp, int ddp) {
      switch (sdp * 10 + ddp) {
         case 12:
            this.scx1++;
            this.scx2++;
            this.scy2++;
            this.dcx1 += 2;
            this.dcx2 += 3;
            break;
         case 13:
            this.scy2++;
            this.scx1++;
            this.dcy2++;
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 22:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 43:
         default:
            break;
         case 21:
            this.scx2++;
            this.scy1++;
            this.scy2++;
            this.dcx1 += 2;
            this.dcx2 += 3;
            break;
         case 23:
            this.scx1--;
            this.dcy1++;
            break;
         case 24:
            this.scx2++;
            this.dcx2++;
            this.scy1++;
            this.scy2++;
            break;
         case 31:
            this.scy2++;
            this.dcy2++;
            break;
         case 32:
            this.scy2++;
            this.dcy2++;
            this.dcx2--;
            break;
         case 42:
            this.scx2++;
            this.dcx2++;
      }
   }

   protected void calcL(int sdp, int ddp) {
      this.calcS(sdp, ddp, true);
      int oldscx1 = this.scx1;
      int oldscx2 = this.scx2;
      int oldscy1 = this.scy1;
      int oldscy2 = this.scy2;
      int olddcx1 = this.dcx1;
      int olddcx2 = this.dcx2;
      int olddcy1 = this.dcy1;
      int olddcy2 = this.dcy2;
      this.scx1 = this.dcx1;
      this.scx2 = this.dcx2;
      this.scy1 = this.dcy1;
      this.scy2 = this.dcy2;
      this.mkL(ddp, ddp);
      olddcx1 = this.scx1;
      olddcx2 = this.scx2;
      olddcy1 = this.scy1;
      olddcy2 = this.scy2;
      this.scx1 = oldscx1;
      this.scx2 = oldscx2;
      this.scy1 = oldscy1;
      this.scy2 = oldscy2;
      this.mkL(sdp, ddp);
      oldscx1 = this.scx1;
      oldscx2 = this.scx2;
      oldscy1 = this.scy1;
      oldscy2 = this.scy2;
      double DIV = 4.0;
      boolean p11 = this.direkt11(sdp, ddp);
      Point sp1 = new Point(oldscx1, oldscy1);
      Point dp1 = new Point(p11 ? olddcx1 : olddcx2, p11 ? olddcy1 : olddcy2);
      Point sp2 = new Point(oldscx2, oldscy2);
      Point dp2 = new Point(p11 ? olddcx2 : olddcx1, p11 ? olddcy2 : olddcy1);
      double a1 = sp1.arc(dp1);
      if (sp1.x != dp1.x && sp1.y != dp1.y && sp1.x != dp2.x && sp1.y != dp2.y) {
         DIV = 2.5;
      }

      double lx1 = sp1.distance(dp1) / DIV;
      double lx2 = sp2.distance(dp2) / DIV;
      this.dcx1 = (int)((double)this.scx1 + lx1 * Math.cos(a1));
      this.dcy1 = (int)((double)this.scy1 + lx1 * Math.sin(a1));
      this.dcx2 = (int)((double)this.scx2 + lx2 * Math.cos(a1));
      this.dcy2 = (int)((double)this.scy2 + lx2 * Math.sin(a1));
   }

   protected void mkL(int sdp, int ddp) {
      switch (sdp) {
         case 1:
            this.scy1++;
            this.scy2--;
            this.dcx1 = this.scx1 - 3;
            this.dcx2 = this.scx2 - 3;
            this.dcy1 = this.scy1;
            this.dcy2 = this.scy2;
            break;
         case 2:
            this.scx1++;
            this.scx2--;
            this.dcy1 = this.scy1 - 3;
            this.dcy2 = this.scy2 - 3;
            this.dcx1 = this.scx1;
            this.dcx2 = this.scx2;
            break;
         case 3:
            this.scy1++;
            this.scy2--;
            this.dcx1 = this.scx1 + 3;
            this.dcx2 = this.scx2 + 3;
            this.dcy1 = this.scy1;
            this.dcy2 = this.scy2;
            break;
         case 4:
            this.scx1++;
            this.scx2--;
            this.dcy1 = this.scy1 + 3;
            this.dcy2 = this.scy2 + 3;
            this.dcx1 = this.scx1;
            this.dcx2 = this.scx2;
      }
   }

   protected void calcS(int sdp, int ddp, boolean inner) {
      int p = inner ? 0 : 1;
      switch (sdp) {
         case 1:
            this.scx1 = this.scx1 + this.sx2 + p;
            this.scy1 = this.scy1 + this.sy1;
            this.scx2 = this.scx2 + this.sx2 + p;
            this.scy2 = this.scy2 + this.sy2;
            break;
         case 2:
            this.scx1 = this.scx1 + this.sx1;
            this.scy1 = this.scy1 + this.sy2 + p;
            this.scx2 = this.scx2 + this.sx2;
            this.scy2 = this.scy2 + this.sy2 + p;
            break;
         case 3:
            this.scx1 = this.scx1 + (this.sx1 - p);
            this.scy1 = this.scy1 + this.sy1;
            this.scx2 = this.scx2 + (this.sx1 - p);
            this.scy2 = this.scy2 + this.sy2;
            break;
         case 4:
            this.scx1 = this.scx1 + this.sx1;
            this.scy1 = this.scy1 + (this.sy1 - p);
            this.scx2 = this.scx2 + this.sx2;
            this.scy2 = this.scy2 + (this.sy1 - p);
      }

      switch (ddp) {
         case 1:
            this.dcx1 = this.dcx1 + this.dx2 + p;
            this.dcy1 = this.dcy1 + this.dy1;
            this.dcx2 = this.dcx2 + this.dx2 + p;
            this.dcy2 = this.dcy2 + this.dy2;
            break;
         case 2:
            this.dcx1 = this.dcx1 + this.dx1;
            this.dcy1 = this.dcy1 + this.dy2 + p;
            this.dcx2 = this.dcx2 + this.dx2;
            this.dcy2 = this.dcy2 + this.dy2 + p;
            break;
         case 3:
            this.dcx1 = this.dcx1 + (this.dx1 - p);
            this.dcy1 = this.dcy1 + this.dy1;
            this.dcx2 = this.dcx2 + (this.dx1 - p);
            this.dcy2 = this.dcy2 + this.dy2;
            break;
         case 4:
            this.dcx1 = this.dcx1 + this.dx1;
            this.dcy1 = this.dcy1 + (this.dy1 - p);
            this.dcx2 = this.dcx2 + this.dx2;
            this.dcy2 = this.dcy2 + (this.dy1 - p);
      }
   }

   protected void initD(int xscal, int yscal) {
      this.dx1 = this.sx1 = (int)((double)(-1 * xscal) / 6.0 + (double)xscal / 2.0);
      this.dx2 = this.sx2 = (int)((double)(1 * xscal) / 6.0 + (double)xscal / 2.0);
      this.dy1 = this.sy1 = (int)((double)(-1 * yscal) / 6.0 + (double)yscal / 2.0);
      this.dy2 = this.sy2 = (int)((double)(1 * yscal) / 6.0 + (double)yscal / 2.0);
   }

   @Override
   public synchronized void paintelement(
      gleis gl, Graphics2D g2, int col, int row, int x, int y, int xscal, int yscal, int fscal, int cc, boolean geradeok, Color colr, int sdp, int ddp
   ) {
      if (g2 != null) {
         synchronized (gl) {
            this.scx1 = this.scx2 = col * xscal;
            this.scy1 = this.scy2 = row * yscal;
            this.dcx1 = this.dcx2 = x * xscal;
            this.dcy1 = this.dcy2 = y * yscal;
            this.initD(xscal, yscal);
            this.calcS(sdp, ddp, false);
            g2.setColor(colr);
            boolean mustSwap = (this.dcx1 <= this.scx1 || this.dcy1 <= this.scy1) && (this.dcx1 >= this.scx1 || this.dcy1 >= this.scy1);
            this.vbPoly(sdp, ddp);
            if (mustSwap) {
               int t = this.dcx1;
               this.dcx1 = this.dcx2;
               this.dcx2 = t;
               t = this.dcy1;
               this.dcy1 = this.dcy2;
               this.dcy2 = t;
            }

            Polygon pgn = new Polygon();
            pgn.addPoint(this.scx1, this.scy1);
            pgn.addPoint(this.scx2, this.scy2);
            pgn.addPoint(this.dcx1, this.dcy1);
            pgn.addPoint(this.dcx2, this.dcy2);
            g2.fillPolygon(pgn);
         }
      }
   }

   @Override
   public synchronized void paintelementL(gleis gl, Graphics2D g2, int col, int row, int xscal, int yscal, int fscal, Color colr) {
      synchronized (gl) {
         this.initD(xscal, yscal);
         g2.setColor(colr);
         this.w1 = null;
         this.w2 = null;
         this.w3 = null;
         this.connectorCnt = 0;
         this.connectorLCnt = 0;
         int lastsdpL = 0;
         boolean swaped = false;
         Iterator<gleis.nachbarGleis> it = gl.p_getNachbarn();

         while (it.hasNext()) {
            gleis.nachbarGleis gl2 = (gleis.nachbarGleis)it.next();
            gleis dgl = gl2.gl;
            if (this.w1 == null) {
               this.w1 = gl2.gl;
            } else if (this.w2 == null) {
               this.w2 = gl2.gl;
            } else if (this.w3 == null) {
               this.w3 = gl2.gl;
            }

            this.scx1 = this.scx2 = col * xscal;
            this.scy1 = this.scy2 = row * yscal;
            this.dcx1 = this.dcx2 = (dgl.getCol() - gl.getCol() + col) * xscal;
            this.dcy1 = this.dcy2 = (dgl.getRow() - gl.getRow() + row) * yscal;
            this.calcS(gl2.sdp, gl2.ddp, true);
            boolean contains = false;

            for (int i = 0; i < Math.min(this.connectorCnt, this.connector.length); i++) {
               if (this.connector[i].x == this.scx1 && this.connector[i].y == this.scy1) {
                  contains = true;
                  break;
               }
            }

            if (!contains && this.connectorCnt < this.connector.length) {
               try {
                  this.connector[this.connectorCnt] = new Point(this.scx1, this.scy1);
                  this.connectorCnt++;
               } catch (ArrayIndexOutOfBoundsException var23) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "Catch:" + var23.toString() + ":" + this.connectorCnt + "/" + gl.toString(), var23);
               }
            }

            contains = false;

            for (int ix = 0; ix < Math.min(this.connectorCnt, this.connector.length); ix++) {
               if (this.connector[ix].x == this.scx2 && this.connector[ix].y == this.scy2) {
                  contains = true;
                  break;
               }
            }

            if (!contains && this.connectorCnt < this.connector.length) {
               try {
                  this.connector[this.connectorCnt] = new Point(this.scx2, this.scy2);
                  this.connectorCnt++;
               } catch (ArrayIndexOutOfBoundsException var22) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "Catch:" + var22.toString() + ":" + this.connectorCnt + "/" + gl.toString(), var22);
               }
            }

            this.mkL(gl2.sdp, gl2.ddp);
            this.vbPoly2(gl2.sdp, gl2.ddp);
            swaped = this.switchSifneeded(gl2.sdp, lastsdpL, swaped);
            contains = false;
            boolean changed = false;

            for (int ixx = 0; ixx < Math.min(this.connectorLCnt, this.connectorL.length); ixx++) {
               if (this.connectorL[ixx].x == this.scx1 && this.connectorL[ixx].y == this.scy1) {
                  contains = true;
                  break;
               }
            }

            if (!contains) {
               try {
                  this.connectorL[this.connectorLCnt] = new Point(this.scx1, this.scy1);
                  this.connectorLCnt++;
                  changed = true;
               } catch (ArrayIndexOutOfBoundsException var21) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "Catch:" + var21.toString() + ":" + this.connectorCnt + "/" + gl.toString(), var21);
               }
            }

            contains = false;

            for (int ixxx = 0; ixxx < Math.min(this.connectorLCnt, this.connectorL.length); ixxx++) {
               if (this.connectorL[ixxx].x == this.scx2 && this.connectorL[ixxx].y == this.scy2) {
                  contains = true;
                  break;
               }
            }

            if (!contains) {
               try {
                  this.connectorL[this.connectorLCnt] = new Point(this.scx2, this.scy2);
                  this.connectorLCnt++;
                  changed = true;
               } catch (ArrayIndexOutOfBoundsException var20) {
                  Logger.getLogger("stslogger").log(Level.SEVERE, "Catch:" + var20.toString() + ":" + this.connectorCnt + "/" + gl.toString(), var20);
               }
            }

            if (changed) {
               lastsdpL = gl2.sdp;
            }
         }

         if (this.connectorLCnt == 2) {
            this.connectorLCnt = 4;
            this.connectorL[2] = new Point(this.dcx2, this.dcy2);
            this.connectorL[3] = new Point(this.dcx1, this.dcy1);
         }

         if (this.connectorCnt == 4) {
            if (this.connector[0].x == this.connector[2].x || this.connector[0].y == this.connector[2].y) {
               Point t = this.connector[2];
               this.connector[2] = this.connector[3];
               this.connector[3] = t;
            }
         } else if (this.connectorCnt != 3) {
            this.scx1 = this.scx2 = 0;
            this.scy1 = this.scy2 = 0;
            this.connectorCnt = 4;
            this.connector[0] = new Point(this.scx1 + this.sx1, this.scy1 + this.sy1);
            this.connector[1] = new Point(this.scx1 + this.sx2, this.scy1 + this.sy1);
            this.connector[2] = new Point(this.scx1 + this.sx2, this.scy1 + this.sy2);
            this.connector[3] = new Point(this.scx1 + this.sx1, this.scy1 + this.sy2);
         }

         if (this.connectorCnt > 0) {
            int maxx = 0;
            int maxy = 0;

            for (int ixxxx = 0; ixxxx < this.connectorCnt; ixxxx++) {
               maxx = Math.max(maxx, this.connector[ixxxx].x);
               maxy = Math.max(maxy, this.connector[ixxxx].y);
            }

            Polygon pgn = new Polygon();

            for (int ixxxx = 0; ixxxx < Math.min(this.connectorCnt, this.connector.length); ixxxx++) {
               int x = this.connector[ixxxx].x;
               int y = this.connector[ixxxx].y;
               if (x == maxx) {
                  x++;
               }

               if (y == maxy) {
                  y++;
               }

               pgn.addPoint(x, y);
            }

            if (pgn.npoints > 0) {
               g2.fillPolygon(pgn);
            }
         }
      }
   }

   @Override
   boolean needExtraRight() {
      return false;
   }
}
