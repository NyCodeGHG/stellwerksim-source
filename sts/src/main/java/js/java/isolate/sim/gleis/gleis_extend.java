package js.java.isolate.sim.gleis;

import js.java.tools.BinaryStore;
import js.java.tools.BinaryStore.StoreElement;

public class gleis_extend extends BinaryStore implements Cloneable {
   @StoreElement(
      storeName = "FB"
   )
   public String farbe = "normal";
   @StoreElement(
      storeName = "SIGENT"
   )
   public boolean entscheiderSignal = false;
   @StoreElement(
      storeName = "VSIGSIG"
   )
   public boolean vorsignalSignal = false;
   @StoreElement(
      storeName = "ENAME"
   )
   public String elementName = "";

   public static gleis_extend createFromBase64(String data) {
      gleis_extend ge = new gleis_extend();
      ge.fromBase64(data);
      return ge;
   }

   public gleis_extend() {
      super(1L);
   }

   public gleis_extend clone() {
      gleis_extend ge = new gleis_extend();
      ge.farbe = this.farbe;
      return ge;
   }

   @Deprecated
   void setExtendValue(gleis.EXTENDS name, String value) {
      if (name == gleis.EXTENDS.FARBE) {
         this.farbe = value;
      }
   }

   @Deprecated
   String getExtendValue(gleis.EXTENDS name) {
      return name == gleis.EXTENDS.FARBE ? this.farbe : null;
   }

   public String getFarbe() {
      return this.farbe;
   }

   public void setFarbe(String farbe) {
      this.farbe = farbe;
   }

   public boolean isEntscheider() {
      return this.entscheiderSignal;
   }

   public void setEntscheider(boolean e) {
      this.entscheiderSignal = e;
   }

   public boolean isVorsignal() {
      return this.vorsignalSignal;
   }

   public void setVorsignal(boolean e) {
      this.vorsignalSignal = e;
   }

   public String getElementName() {
      return this.elementName;
   }

   public void setElementName(String elementName) {
      this.elementName = elementName.trim();
   }
}
