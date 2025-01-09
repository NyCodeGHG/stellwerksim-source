package js.java.tools;

import java.util.Iterator;
import java.util.LinkedList;

public class ExponentialSmoothing {
   private final LinkedList<Double> data = new LinkedList();
   private final int historySize;
   private final double smooth;

   public ExponentialSmoothing(int historySize, double smooth) {
      this.historySize = historySize;
      this.smooth = smooth;
   }

   public ExponentialSmoothing(int historySize) {
      this(historySize, 0.4);
   }

   public void addValue(double v) {
      this.data.addFirst(v);
      if (this.data.size() > this.historySize) {
         this.data.removeLast();
      }
   }

   private double calcForcast(Iterator<Double> it) {
      if (!it.hasNext()) {
         return (Double)this.data.getLast();
      } else {
         double y = (Double)it.next();
         return this.smooth * y + (1.0 - this.smooth) * this.calcForcast(it);
      }
   }

   public double getForcast() {
      Iterator<Double> it = this.data.iterator();
      return this.calcForcast(it);
   }
}
