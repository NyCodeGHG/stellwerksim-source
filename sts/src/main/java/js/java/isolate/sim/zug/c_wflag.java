package js.java.isolate.sim.zug;

import java.util.ArrayList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.sim.TEXTTYPE;

class c_wflag extends baseChain1Chain {
   c_wflag() {
      super(new c_rflag());
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.isBahnsteig && z.flags.hasFlag('W')) {
         if (z.mytime - z.warankunft > 30000L) {
            z.waitLWdone = false;
            z.waitLW = true;
            z.laengeBackup = z.laenge;
            z.shortenZug();

            try {
               ArrayList<String> d = z.flags.paramsOfFlag('W');
               z.flags.replaceFlag('W', 'w');
               int enr = Integer.parseInt((String)d.get(0));
               if (z.glbModel.findFirst(new Object[]{Math.abs(enr), gleis.ELEMENT_AUSFAHRT}) == null) {
                  enr = Integer.parseInt((String)d.get(1));
               }

               zug nz = new zug(z, false, false, z.gestopptgleis, enr);
               String text = "" + nz.getSpezialName() + " bereit zum Abstellen.";

               for(zug.wflagData wd : z.wflagList) {
                  if (wd.an == z.an && wd.zielgleis.equals(z.zielgleis)) {
                     text = text + " Ersatzlok wird erst zur Planankunft bereitgestellt!";
                     break;
                  }
               }

               nz.wartenOK = true;
               nz.my_main.showText(text, TEXTTYPE.ANRUF, z);
               nz.my_main.playAnruf();
               z.shortenZug();
               z.pos_gl = (gleis)z.zugbelegt.getLast();
               z.before_gl = (gleis)z.zugbelegt.get(z.zugbelegt.size() - 2);
            } catch (NumberFormatException var8) {
               System.out.println("Zugfehler: " + z.getSpezialName() + ": " + var8.getMessage());
               z.my_main.showText("Zugfehler: " + z.getSpezialName() + ": " + var8.getMessage(), TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
            } catch (IndexOutOfBoundsException var9) {
               System.out.println("Zugfehler: " + z.getSpezialName() + " Lokwechsel Ausfahrt fehlt!");
               z.my_main.showText("Zugfehler: " + z.getSpezialName() + " Lokwechsel Ausfahrt fehlt!", TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
            } catch (IllegalArgumentException var10) {
               System.out.println("Zugfehler: " + z.getSpezialName() + ": " + var10.getMessage());
               z.my_main.showText("Zugfehler: " + z.getSpezialName() + ": " + var10.getMessage(), TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
            }

            if (zug.debugMode != null) {
               zug.debugMode.writeln("zug (" + z.getName() + ")", "W-Flag");
            }
         }

         return false;
      } else if (z.isBahnsteig && z.flags.hasFlag('w')) {
         if (z.waitLWdone) {
            z.flags.removeFlag('w');
            z.laenge = z.laengeBackup;
            z.shortenZug();
            z.pos_gl = (gleis)z.zugbelegt.getLast();
            z.before_gl = (gleis)z.zugbelegt.get(z.zugbelegt.size() - 2);
            z.waitLWdone = false;
         }

         return false;
      } else {
         return this.callFalse(z);
      }
   }
}
