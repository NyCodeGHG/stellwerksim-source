package js.java.isolate.sim.sim.funk;

import js.java.isolate.sim.sim.TEXTTYPE;
import js.java.isolate.sim.sim.zugUndPlanPanel;
import js.java.isolate.sim.zug.zug;
import js.java.isolate.sim.zug.zugPositionListener;

public class funk_zugPosition extends funk_zugBase implements zugPositionListener {
   public funk_zugPosition(zugUndPlanPanel.funkAdapter a, zug z, int unterzug) {
      super("Zug " + z.getSpezialName() + ": Position", a, z, unterzug);
      this.addValueItem(new funkAuftragBase.funkValueItem("melden", -1));
   }

   @Override
   public void selected(funkAuftragBase.funkValueItem sel) {
      this.z.meldePosition(this);
   }

   @Override
   public void melde(zug z, String text) {
      text = "Anruf von Triebfahrzeugführer " + z.getSpezialName() + ":<br><i>" + text + "</i>";
      this.my_main.showText(text, TEXTTYPE.ANRUF, z);
      this.my_main.playAnruf();
   }
}
