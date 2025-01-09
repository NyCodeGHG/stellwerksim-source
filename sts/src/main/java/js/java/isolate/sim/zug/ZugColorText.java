package js.java.isolate.sim.zug;

import js.java.tools.ColorText;

public class ZugColorText extends ColorText {
   private final zugHandler h;
   private final zug z;
   private final frozenZug fz;

   ZugColorText(zug _z, zugHandler zh) {
      this.z = _z;
      this.h = zh;
      this.fz = new frozenZug();
      this.update();
   }

   ZugColorText(zug _z, zugHandler zh, String text) {
      super(text);
      this.z = _z;
      this.h = zh;
      this.fz = new frozenZug();
   }

   ZugColorText(zug _z, zugHandler zh, String text, String spezialtext) {
      super(text, spezialtext);
      this.z = _z;
      this.h = zh;
      this.fz = new frozenZug();
   }

   public zug getZug() {
      return this.z;
   }

   public frozenZug getFZug() {
      return this.fz;
   }

   zugHandler getHandler() {
      return this.h;
   }

   public String getText() {
      this.update();
      return super.getText();
   }

   void update() {
      if (this.h != null) {
         this.h.update(this, this.z);
      }
   }

   void freeze() {
      this.fz.update(this.z);
   }
}
