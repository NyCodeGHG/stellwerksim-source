package js.java.isolate.sim.zug;

public class EmptyZugColorText extends ZugColorText {
   EmptyZugColorText(ZugColorText zct, String text) {
      super(zct.getZug(), zct.getHandler(), text);
   }

   @Override
   void update() {
   }
}
