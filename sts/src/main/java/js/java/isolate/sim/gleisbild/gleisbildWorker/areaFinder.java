package js.java.isolate.sim.gleisbild.gleisbildWorker;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D.Double;
import java.util.LinkedList;
import java.util.TreeSet;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildModel;

public class areaFinder extends gleisbildWorkerBase<gleisbildModel> {
   private boolean[][] kanten = (boolean[][])null;
   private final TreeSet<areaFinder.line> hlines = new TreeSet();
   private final TreeSet<areaFinder.line> vlines = new TreeSet();

   public areaFinder(gleisbildModel gl, GleisAdapter main) {
      super(gl, main);
   }

   public LinkedList<Rectangle> getAreas() {
      boolean debug = false;
      LinkedList<areaFinder.rectangle> ret = new LinkedList();
      boolean[][] kantenX = new boolean[this.glbModel.getGleisWidth()][this.glbModel.getGleisHeight()];
      boolean[][] kantenY = new boolean[this.glbModel.getGleisWidth()][this.glbModel.getGleisHeight()];

      for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
         for (int x = 0; x < this.glbModel.getGleisWidth(); x++) {
            kantenX[x][y] = false;
            kantenY[x][y] = false;
         }
      }

      for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
         gleis lastXe = null;
         int lastXc = 0;

         for (int x = 0; x < this.glbModel.getGleisWidth(); x++) {
            gleis gl = this.glbModel.getXY_null(x, y);
            if (!this.emptyGleis(gl)) {
               lastXe = gl;
               lastXc = 0;
               kantenX[x][y] = true;
            } else {
               if (lastXe != null) {
                  lastXc++;
                  kantenX[x][y] = true;
               }

               if (lastXc > 1) {
                  lastXe = null;

                  for (int i = lastXc; i > 0; i--) {
                     kantenX[x - i + 1][y] = false;
                  }

                  lastXc = 0;
               }
            }
         }

         if (lastXc > 0) {
            lastXe = null;

            for (int i = lastXc; i > 0; i--) {
               kantenX[this.glbModel.getGleisWidth() - i][y] = false;
            }

            boolean var19 = false;
         }
      }

      for (int xx = 0; xx < this.glbModel.getGleisWidth(); xx++) {
         gleis lastXe = null;
         int lastXc = 0;

         for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
            gleis gl = this.glbModel.getXY_null(xx, y);
            if (!this.emptyGleis(gl)) {
               lastXe = gl;
               lastXc = 0;
               kantenY[xx][y] = true;
            } else {
               if (lastXe != null) {
                  lastXc++;
                  kantenY[xx][y] = true;
               }

               if (lastXc > 1) {
                  lastXe = null;

                  for (int i = lastXc; i > 0; i--) {
                     kantenY[xx][y - i + 1] = false;
                  }

                  lastXc = 0;
               }
            }
         }

         if (lastXc > 0) {
            lastXe = null;

            for (int i = lastXc; i > 0; i--) {
               kantenY[xx][this.glbModel.getGleisHeight() - i] = false;
            }

            boolean var21 = false;
         }
      }

      for (int yx = 0; yx < this.glbModel.getGleisHeight(); yx++) {
         for (int xx = 0; xx < this.glbModel.getGleisWidth(); xx++) {
            kantenX[xx][yx] = kantenX[xx][yx] | kantenY[xx][yx];
            if (yx == 0 || xx == 0 || yx == this.glbModel.getGleisHeight() - 1 || xx == this.glbModel.getGleisWidth() - 1) {
               kantenX[xx][yx] = false;
            }
         }
      }

      this.kanten = kantenX;

      do {
         this.findLines();
      } while (this.findRects(ret));

      System.out.println("done");
      return ret;
   }

   private void findLines() {
      this.hlines.clear();
      this.vlines.clear();

      for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
         int startx = 0;

         for (int x = 0; x < this.glbModel.getGleisWidth(); x++) {
            if (this.kanten[x][y]) {
               if (x - startx > 2) {
                  boolean add = false;

                  try {
                     for (int i = startx; i < x; i++) {
                        if (this.kanten[i][y - 1] || this.kanten[i][y + 1]) {
                           add = true;
                           break;
                        }
                     }
                  } catch (Exception var8) {
                     add = true;
                  }

                  if (add) {
                     areaFinder.line l = new areaFinder.line(startx, y, x, y);
                     this.hlines.add(l);
                  }
               }

               startx = x + 1;
            }
         }

         if (this.glbModel.getGleisWidth() - 1 - startx > 2) {
            boolean add = false;

            try {
               for (int ix = startx; ix < this.glbModel.getGleisWidth(); ix++) {
                  if (this.kanten[ix][y - 1] || this.kanten[ix][y + 1]) {
                     add = true;
                     break;
                  }
               }
            } catch (Exception var9) {
               add = true;
            }

            if (add) {
               areaFinder.line l = new areaFinder.line(startx, y, this.glbModel.getGleisWidth() - 1, y);
               this.hlines.add(l);
            }
         }
      }

      for (int xx = 0; xx < this.glbModel.getGleisWidth(); xx++) {
         int starty = 0;

         for (int y = 0; y < this.glbModel.getGleisHeight(); y++) {
            if (this.kanten[xx][y]) {
               if (y - starty > 2) {
                  boolean add = false;

                  try {
                     for (int ixx = starty; ixx < y; ixx++) {
                        if (this.kanten[xx - 1][ixx] || this.kanten[xx + 1][ixx]) {
                           add = true;
                           break;
                        }
                     }
                  } catch (Exception var6) {
                     add = true;
                  }

                  if (add) {
                     areaFinder.line l = new areaFinder.line(xx, starty, xx, y);
                     this.vlines.add(l);
                  }
               }

               starty = y + 1;
            }
         }

         if (this.glbModel.getGleisHeight() - 1 - starty > 2) {
            boolean add = false;

            try {
               for (int ixxx = starty; ixxx < this.glbModel.getGleisHeight(); ixxx++) {
                  if (this.kanten[xx - 1][ixxx] || this.kanten[xx + 1][ixxx]) {
                     add = true;
                     break;
                  }
               }
            } catch (Exception var7) {
               add = true;
            }

            if (add) {
               areaFinder.line l = new areaFinder.line(xx, starty, xx, this.glbModel.getGleisHeight() - 1);
               this.vlines.add(l);
            }
         }
      }

      for (areaFinder.line hl : this.hlines) {
         for (areaFinder.line vl : this.vlines) {
            if (hl.intersectsLine(vl)) {
               hl.intersects.add(vl);
               vl.intersects.add(hl);
            }
         }
      }
   }

   private boolean findRects(LinkedList<areaFinder.rectangle> rects) {
      for (areaFinder.line l : this.hlines) {
         this.glbModel.clearMarkedGleis();
         if (l.intersects.size() >= 2) {
            TreeSet<areaFinder.line> matching = new TreeSet();

            for (areaFinder.line vl : l.intersects) {
               if (vl.intersects.size() >= 2) {
                  matching.add(vl);
               }
            }

            if (!matching.isEmpty()) {
               TreeSet<areaFinder.rectangle> result = new TreeSet();

               for (areaFinder.line vlx : matching) {
                  for (areaFinder.line hl : vlx.intersects) {
                     if (hl.y1 != l.y1 && (l.x1 <= hl.x1 && l.x2 >= hl.x1 || l.x1 <= hl.x2 && l.x2 >= hl.x2)) {
                        for (areaFinder.line vl2 : matching) {
                           if (vl2 != vlx && vl2.intersects.contains(hl)) {
                              areaFinder.rectangle r = new areaFinder.rectangle(l.getIntersect(vlx));
                              r.add(hl.getIntersect(vl2));
                              if (this.containtKanten(r) && this.borderKanten(r)) {
                                 result.add(r);
                              }
                           }
                        }
                     }
                  }
               }

               if (!result.isEmpty()) {
                  areaFinder.rectangle min = (areaFinder.rectangle)result.first();
                  int minh = min.height;

                  for (areaFinder.rectangle r : result) {
                     if (r.height < minh) {
                        min = r;
                        minh = r.height;
                     }
                  }

                  if (min != null) {
                     rects.add(min);
                     this.clearKanten(min);
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean emptyGleis(gleis gl) {
      return gl == null || !gleis.ALLE_GLEISE.matches(gl.getElement());
   }

   private boolean containtKanten(Rectangle r) {
      for (int y = 0; y < r.height; y++) {
         for (int x = 0; x < r.width; x++) {
            if (this.kanten[x + r.x][y + r.y]) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean borderKanten(Rectangle r) {
      int x = r.x + 1;
      int y = r.y + 1;
      int width = r.width - 2;
      int height = r.height - 2;
      boolean ktop = false;
      boolean kbottom = false;
      boolean kleft = false;
      boolean kright = false;

      for (int xx = 0; xx < width; xx++) {
         ktop |= this.kanten[x + xx][y];
         kbottom |= this.kanten[x + xx][y + height];
      }

      for (int yy = 0; yy < height; yy++) {
         kleft |= this.kanten[x][y + yy];
         kright |= this.kanten[x + width][y + yy];
      }

      return ktop && kbottom && kleft && kright;
   }

   private void clearKanten(Rectangle r) {
      for (int y = 0; y < r.height; y++) {
         for (int x = 0; x < r.width; x++) {
            this.kanten[x + r.x][y + r.y] = false;
         }
      }
   }

   private class line extends Double implements Comparable {
      TreeSet<areaFinder.line> intersects = new TreeSet();

      line(int x1, int y1, int x2, int y2) {
         super((double)x1, (double)y1, (double)x2, (double)y2);
      }

      public int compareTo(Object o) {
         areaFinder.line l2 = (areaFinder.line)o;
         int lg1 = (int)(this.x2 - this.x1 + (this.y2 - this.y1));
         int lg2 = (int)(l2.x2 - l2.x1 + (l2.y2 - l2.y1));
         if (lg1 != lg2) {
            return lg1 - lg2;
         } else {
            return this.x1 != l2.x1 ? (int)(this.x1 - l2.x1) : (int)(this.y1 - l2.y1);
         }
      }

      Point getIntersect(areaFinder.line l2) {
         return new Point((int)Math.max(this.x1, l2.x1), (int)Math.max(this.y1, l2.y1));
      }

      public String toString() {
         return "(" + this.x1 + "/" + this.y1 + ")/(" + this.x2 + "/" + this.y2 + ")";
      }
   }

   private class rectangle extends Rectangle implements Comparable {
      rectangle(Point p) {
         super(p);
      }

      public int compareTo(Object o) {
         Rectangle r = (Rectangle)o;
         int s1 = this.width * this.height;
         int s2 = r.width * r.height;
         if (s1 != s2) {
            return s1 - s2;
         } else {
            return this.x != r.x ? this.x - r.x : this.y - r.y;
         }
      }

      public String toString() {
         return "x=" + this.x + "/y=" + this.y + "/w=" + this.width + "/h=" + this.height;
      }
   }
}
