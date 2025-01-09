package js.java.isolate.sim.zug;

import java.util.ArrayList;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.sim.TEXTTYPE;
import js.java.isolate.sim.toolkit.HyperlinkCaller;

class c_wflagNeuzug extends baseChain {
   private void prepareMsg(zug z) {
      z.wflagDelayed = 0L;
      String zielZug = "";
      zug neuzug = z.my_main.findZug(z.flags.dataOfFlag('K'));
      if (neuzug != null) {
         zielZug = " mit " + neuzug.getSpezialName();
      }

      z.my_main
         .showText(
            z.getSpezialName()
               + " kann bereitgestellt werden. Kuppelt an Gleis "
               + z.zielgleis
               + ""
               + zielZug
               + ".<ul><li><a href='yes'>ok, jetzt bereitstellen</a></li><li><a href='no'>nein, in 15 Minuten nochmal fragen</a></li></ul>",
            TEXTTYPE.ANRUF,
            z,
            new c_wflagNeuzug.wflagHyperlink(z)
         );
      z.my_main.playAnruf();
   }

   @Override
   boolean run(zug z) {
      this.visiting(z);
      if (z.wflagDelayed > 0L && z.wflagDelayed < z.mytime) {
         this.prepareMsg(z);
      }

      for (zug.wflagData wd : z.wflagList) {
         if (wd.an <= z.mytime && wd.flags.hasFlag('W')) {
            try {
               ArrayList<String> d = wd.flags.paramsOfFlag('W');
               int enr = Math.abs(Integer.parseInt((String)d.get(0)));
               if (z.glbModel.findFirst(new Object[]{enr, gleis.ELEMENT_EINFAHRT}) == null) {
                  enr = Math.abs(Integer.parseInt((String)d.get(1)));
               }

               zug nz = new zug(z, true, true, wd.zielgleis, enr);
               if (zug.debugMode != null) {
                  zug.debugMode.writeln("zug (" + z.getName() + ")", "W-Flag");
               }

               this.prepareMsg(nz);
            } catch (NumberFormatException var7) {
               System.out.println("Zugfehler: " + z.getSpezialName() + ": " + var7.getMessage());
               z.my_main.showText("Zugfehler: " + z.getSpezialName() + ": " + var7.getMessage(), TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
            } catch (IndexOutOfBoundsException var8) {
               System.out.println("Zugfehler: " + z.getSpezialName() + " Lokwechsel Einfahrt fehlt!");
               z.my_main.showText("Zugfehler: " + z.getSpezialName() + " Lokwechsel Einfahrt fehlt!", TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
            } catch (IllegalArgumentException var9) {
               System.out.println("Zugfehler: " + z.getSpezialName() + ": " + var9.getMessage());
               z.my_main.showText("Zugfehler: " + z.getSpezialName() + ": " + var9.getMessage(), TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
            } catch (NullPointerException var10) {
               System.out.println("Zugfehler: " + z.getSpezialName() + " Lokwechsel Einfahrt fehlt im übertragenen Fahrplan!");
               z.my_main.showText("Zugfehler: " + z.getSpezialName() + " Lokwechsel Einfahrt fehlt im übertragenen Fahrplan!", TEXTTYPE.ANRUF, z);
               z.my_main.playAnruf();
            }

            z.wflagList.remove(wd);
            break;
         }
      }

      return false;
   }

   private static class wflagHyperlink implements HyperlinkCaller {
      private final zug nzug;

      private wflagHyperlink(zug nz) {
         this.nzug = nz;
      }

      @Override
      public void clicked(String url) {
         if (url.equals("yes")) {
            this.nzug.mytrain = true;
         } else {
            this.nzug.wflagDelayed = this.nzug.mytime + 900000L + zug.randomTimeShift(-120000L, 0L, 120000L);
         }
      }
   }
}
