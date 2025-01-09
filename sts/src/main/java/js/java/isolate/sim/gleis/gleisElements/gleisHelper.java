package js.java.isolate.sim.gleis.gleisElements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class gleisHelper {
   static LinkedList<element_typElement> allElements = new LinkedList();
   static HashMap<Integer, element_typElement> defaultElements = new HashMap();

   public static element findElement(int typ, int element) {
      for (element_typElement e : allElements) {
         if (e.getTyp() == typ && e.getElement() == element) {
            return e;
         }
      }

      return defaultElements.containsKey(typ) ? (element)defaultElements.get(typ) : gleisElements.ELEMENT_LEER;
   }

   public static List<element> allElements() {
      LinkedList<element> l = new LinkedList();

      for (element_typElement e : allElements) {
         l.add(e);
      }

      return l;
   }

   public static int calcDisplaySize(element elm) {
      int r = 0;
      if (elm == gleisElements.ELEMENT_2ZDISPLAY) {
         r = 2;
      } else if (elm == gleisElements.ELEMENT_3ZDISPLAY) {
         r = 3;
      } else if (elm == gleisElements.ELEMENT_4ZDISPLAY) {
         r = 4;
      } else if (elm == gleisElements.ELEMENT_5ZDISPLAY) {
         r = 5;
      } else if (elm == gleisElements.ELEMENT_6ZDISPLAY) {
         r = 6;
      } else if (elm == gleisElements.ELEMENT_7ZDISPLAY) {
         r = 7;
      } else if (elm == gleisElements.ELEMENT_8ZDISPLAY) {
         r = 8;
      } else if (elm == gleisElements.ELEMENT_AIDDISPLAY) {
         r = 5;
      }

      return r;
   }

   public static element calcSizeDisplay(int size) {
      element r;
      switch (size) {
         case 2:
            r = gleisElements.ELEMENT_2ZDISPLAY;
            break;
         case 3:
            r = gleisElements.ELEMENT_3ZDISPLAY;
            break;
         case 4:
            r = gleisElements.ELEMENT_4ZDISPLAY;
            break;
         case 5:
            r = gleisElements.ELEMENT_5ZDISPLAY;
            break;
         case 6:
            r = gleisElements.ELEMENT_6ZDISPLAY;
            break;
         case 7:
            r = gleisElements.ELEMENT_7ZDISPLAY;
            break;
         case 8:
         default:
            r = gleisElements.ELEMENT_8ZDISPLAY;
      }

      return r;
   }

   public static gleisElements.RICHTUNG findRichtung(String _richtung) {
      if (!_richtung.isEmpty()) {
         char r = _richtung.charAt(0);

         for (gleisElements.RICHTUNG ra : gleisElements.RICHTUNG.values()) {
            if (ra.getChar() == r) {
               return ra;
            }
         }
      }

      return gleisElements.RICHTUNG.right;
   }
}
