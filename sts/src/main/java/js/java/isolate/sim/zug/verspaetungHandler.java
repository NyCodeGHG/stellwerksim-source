package js.java.isolate.sim.zug;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import js.java.tools.ColorText;

class verspaetungHandler extends zugHandler {
   static ArrayList<verspaetungHandler.COMPARETYPES> compareOrder = new ArrayList();

   static void reset() {
      compareOrder.clear();
      compareOrder.add(verspaetungHandler.COMPARETYPES.fertig);
      compareOrder.add(verspaetungHandler.COMPARETYPES.visible);
      compareOrder.add(verspaetungHandler.COMPARETYPES.anversp);
      compareOrder.add(verspaetungHandler.COMPARETYPES.mytrain);
   }

   private int c_mytrain(frozenZug z, frozenZug otherz) {
      int r = 0;
      if (z.mytrain != otherz.mytrain) {
         r = z.mytrain ? -1 : 1;
      }

      return r;
   }

   private int c_fertig(frozenZug z, frozenZug otherz) {
      int r = 0;
      boolean bz = z.fertig || z.exitMode;
      boolean oz = otherz.fertig || otherz.exitMode;
      if (bz != oz) {
         r = bz ? 1 : -1;
      }

      return r;
   }

   private int c_visible(frozenZug z, frozenZug otherz) {
      int r = 0;
      if (z.isRedirect() != otherz.isRedirect()) {
         r = z.isRedirect() ? -1 : 1;
      } else if (z.visible != otherz.visible) {
         r = z.visible ? -1 : 1;
      }

      return r;
   }

   private int c_anversp(frozenZug z, frozenZug otherz) {
      int r = 0;
      long d1;
      if (z.ambahnsteig) {
         d1 = z.ab;
      } else {
         d1 = z.an + (long)z.verspaetung * 60000L;
      }

      long d2;
      if (otherz.ambahnsteig) {
         d2 = otherz.ab;
      } else {
         d2 = otherz.an + (long)otherz.verspaetung * 60000L;
      }

      if (d1 != d2) {
         r = d1 < d2 ? -1 : 1;
      }

      return r;
   }

   private int c_an(frozenZug z, frozenZug otherz) {
      int r = 0;
      long d1 = z.an;
      long d2 = otherz.an;
      if (d1 != d2) {
         r = d1 < d2 ? -1 : 1;
      }

      return r;
   }

   private int c_mark(frozenZug z, frozenZug otherz) {
      String d1 = z.getMarkNum();
      String d2 = otherz.getMarkNum();
      int r;
      if (d1.isEmpty() && d2.isEmpty()) {
         r = 0;
      } else if (d1.isEmpty()) {
         r = 1;
      } else if (d2.isEmpty()) {
         r = -1;
      } else {
         r = d1.compareToIgnoreCase(d2);
      }

      return r;
   }

   @Override
   protected int compareImpl(ZugColorText ct, frozenZug z, ZugColorText other, frozenZug otherz) {
      int r = 0;

      try {
         for (verspaetungHandler.COMPARETYPES p : compareOrder) {
            switch (p) {
               case mytrain:
                  r = this.c_mytrain(z, otherz);
                  break;
               case fertig:
                  r = this.c_fertig(z, otherz);
                  break;
               case visible:
                  r = this.c_visible(z, otherz);
                  break;
               case anversp:
                  r = this.c_anversp(z, otherz);
                  break;
               case an:
                  r = this.c_an(z, otherz);
                  break;
               case marknum:
                  r = this.c_mark(z, otherz);
            }

            if (r != 0) {
               break;
            }
         }
      } catch (Exception var8) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var8);
      }

      return r;
   }

   @Override
   void update(ColorText ct, zug z) {
      String t = z.getVerspaetungOrAnkunft();
      String n = z.getMarkNum();
      if (n.length() > 0) {
         t = "[" + n + "] " + t;
      }

      ct.setText("<html>" + t + "</html>");
   }

   static {
      reset();
   }

   static enum COMPARETYPES {
      mytrain,
      fertig,
      visible,
      anversp,
      an,
      marknum;
   }
}
