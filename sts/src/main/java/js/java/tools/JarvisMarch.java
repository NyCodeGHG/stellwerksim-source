package js.java.tools;

public class JarvisMarch {
   private JarvisMarch.Point[] p;
   private int n;
   private int h;

   public JarvisMarch() {
      super();
   }

   public int computeHull(JarvisMarch.Point[] p) {
      this.p = p;
      this.n = p.length;
      this.h = 0;
      this.jarvisMarch();
      return this.h;
   }

   public int computeHull(JarvisMarch.Point[] p, int s) {
      this.p = p;
      this.n = s;
      this.h = 0;
      this.jarvisMarch();
      return this.h;
   }

   private void jarvisMarch() {
      int i = this.indexOfLowestPoint();

      do {
         this.exchange(this.h, i);
         i = this.indexOfRightmostPointFrom(this.p[this.h]);
         ++this.h;
      } while(i > 0);
   }

   private int indexOfLowestPoint() {
      int min = 0;

      for(int i = 1; i < this.n; ++i) {
         if (this.p[i].y < this.p[min].y || this.p[i].y == this.p[min].y && this.p[i].x < this.p[min].x) {
            min = i;
         }
      }

      return min;
   }

   private int indexOfRightmostPointFrom(JarvisMarch.Point q) {
      int i = 0;

      for(int j = 1; j < this.n; ++j) {
         if (this.p[j].relTo(q).isLess(this.p[i].relTo(q))) {
            i = j;
         }
      }

      return i;
   }

   private void exchange(int i, int j) {
      JarvisMarch.Point t = this.p[i];
      this.p[i] = this.p[j];
      this.p[j] = t;
   }

   public static class Point {
      public int x;
      public int y;

      public Point(int x, int y) {
         super();
         this.x = x;
         this.y = y;
      }

      public Point(JarvisMarch.Point p) {
         this(p.x, p.y);
      }

      public JarvisMarch.Point relTo(JarvisMarch.Point p) {
         return new JarvisMarch.Point(this.x - p.x, this.y - p.y);
      }

      public void makeRelTo(JarvisMarch.Point p) {
         this.x -= p.x;
         this.y -= p.y;
      }

      public JarvisMarch.Point moved(int x0, int y0) {
         return new JarvisMarch.Point(this.x + x0, this.y + y0);
      }

      public JarvisMarch.Point reversed() {
         return new JarvisMarch.Point(-this.x, -this.y);
      }

      public boolean isLower(JarvisMarch.Point p) {
         return this.y < p.y || this.y == p.y && this.x < p.x;
      }

      public double mdist() {
         return (double)(Math.abs(this.x) + Math.abs(this.y));
      }

      public double mdist(JarvisMarch.Point p) {
         return this.relTo(p).mdist();
      }

      public boolean isFurther(JarvisMarch.Point p) {
         return this.mdist() > p.mdist();
      }

      public boolean isBetween(JarvisMarch.Point p0, JarvisMarch.Point p1) {
         return p0.mdist(p1) >= this.mdist(p0) + this.mdist(p1);
      }

      public double cross(JarvisMarch.Point p) {
         return (double)(this.x * p.y - p.x * this.y);
      }

      public boolean isLess(JarvisMarch.Point p) {
         double f = this.cross(p);
         return f > 0.0 || f == 0.0 && this.isFurther(p);
      }

      public double area2(JarvisMarch.Point p0, JarvisMarch.Point p1) {
         return p0.relTo(this).cross(p1.relTo(this));
      }

      public boolean isConvex(JarvisMarch.Point p0, JarvisMarch.Point p1) {
         double f = this.area2(p0, p1);
         return f < 0.0 || f == 0.0 && !this.isBetween(p0, p1);
      }

      public double distance(JarvisMarch.Point p) {
         int px = p.x - this.x;
         int py = p.y - this.y;
         return Math.sqrt((double)(py * py + px * px));
      }

      public double length() {
         return Math.sqrt((double)(this.y * this.y + this.x * this.x));
      }

      public double arc00(JarvisMarch.Point p) {
         double h = (double)(this.x * p.x + this.y * p.y)
            / (Math.sqrt((double)(this.x * this.x + this.y * this.y)) * Math.sqrt((double)(p.x * p.x + p.y * p.y)));
         return Math.acos(h);
      }

      public double arc(JarvisMarch.Point p) {
         int px = p.x - this.x;
         int py = p.y - this.y;
         return Math.atan2((double)py, (double)px);
      }
   }
}
