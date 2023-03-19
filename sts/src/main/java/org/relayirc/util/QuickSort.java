package org.relayirc.util;

import java.util.Enumeration;
import java.util.Vector;

public class QuickSort {
   public QuickSort() {
      super();
   }

   private static void swap(Vector v, int i, int j) {
      Object tmp = v.elementAt(i);
      v.setElementAt(v.elementAt(j), i);
      v.setElementAt(tmp, j);
   }

   private static void swap(Object[] arr, int i, int j) {
      Object tmp = arr[i];
      arr[i] = arr[j];
      arr[j] = tmp;
   }

   private static void quicksort(Vector v, int left, int right, boolean ascending) {
      if (left < right) {
         swap(v, left, (left + right) / 2);
         int last = left;

         for(int i = left + 1; i <= right; ++i) {
            IComparable ic = (IComparable)v.elementAt(i);
            IComparable icleft = (IComparable)v.elementAt(left);
            if (ascending && ic.compareTo(icleft) < 0) {
               swap(v, ++last, i);
            } else if (!ascending && ic.compareTo(icleft) > 0) {
               swap(v, ++last, i);
            }
         }

         swap(v, left, last);
         quicksort(v, left, last - 1, ascending);
         quicksort(v, last + 1, right, ascending);
      }
   }

   private static void quicksort(IComparable[] arr, int left, int right, boolean ascending) {
      if (left < right) {
         swap(arr, left, (left + right) / 2);
         int last = left;

         for(int i = left + 1; i <= right; ++i) {
            if (ascending && arr[i].compareTo(arr[left]) < 0) {
               swap(arr, ++last, i);
            } else if (!ascending && arr[i].compareTo(arr[left]) < 0) {
               swap(arr, ++last, i);
            }
         }

         swap(arr, left, last);
         quicksort(arr, left, last - 1, ascending);
         quicksort(arr, last + 1, right, ascending);
      }
   }

   public static boolean needsSorting(Vector v) {
      IComparable prev = null;

      IComparable curr;
      for(Enumeration e = v.elements(); e.hasMoreElements(); prev = curr) {
         curr = (IComparable)e.nextElement();
         if (prev != null && prev.compareTo(curr) != 0) {
            return true;
         }
      }

      return false;
   }

   public static void quicksort(IComparable[] arr, boolean ascending) {
      quicksort(arr, 0, arr.length - 1, ascending);
   }

   public static void quicksort(Vector v, boolean ascending) {
      if (needsSorting(v)) {
         quicksort(v, 0, v.size() - 1, ascending);
      }
   }
}
