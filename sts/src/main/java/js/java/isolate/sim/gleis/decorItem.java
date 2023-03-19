package js.java.isolate.sim.gleis;

import java.util.LinkedList;
import js.java.isolate.sim.gleis.gleisElements.element;

class decorItem {
   public boolean requiredENR = false;
   public boolean allowsEditENR = false;
   public boolean allowsEditSWwert = false;
   public boolean requiredSWwert = false;
   public boolean shouldhaveSWwert = false;
   public boolean keepEnr = false;
   public boolean hasEnrPartner = false;
   public boolean allowsVerbund = false;
   public boolean displayTrigger = false;
   public boolean displayFStrigger = false;
   public boolean userDisplayTrigger = false;
   public int displayWeight = 0;
   public LinkedList<element> enrPartner = new LinkedList();
   public paint2Base paint2 = new paint2Base(null);
   public pingBase ping = new pingBase(null);
   public nextGleisBase nextGleis = new nextGleisBase(null);
   public elementPainterBase elementPaint = new elementPainterCenteredLight();
   public String namePrefix = null;

   decorItem() {
      super();
   }

   decorItem allowVerbund() {
      this.allowsVerbund = true;
      return this;
   }

   decorItem requireENR() {
      this.requiredENR = true;
      return this;
   }

   decorItem allowEditENR() {
      this.allowsEditENR = true;
      return this;
   }

   decorItem allowEditSWwert() {
      this.allowsEditSWwert = true;
      return this;
   }

   decorItem requireSWwert() {
      this.requiredSWwert = true;
      return this;
   }

   decorItem shouldhaveSWwert() {
      this.shouldhaveSWwert = true;
      return this;
   }

   decorItem keepEnr() {
      this.keepEnr = true;
      return this;
   }

   decorItem enrPartner(element element) {
      this.hasEnrPartner = true;
      this.enrPartner.add(element);
      return this;
   }

   decorItem allowDisplayTrigger() {
      this.displayTrigger = true;
      return this;
   }

   decorItem isUserDisplayTrigger() {
      this.userDisplayTrigger = true;
      this.displayTrigger = true;
      return this;
   }

   decorItem allowDisplayFStrigger() {
      this.displayFStrigger = true;
      return this;
   }

   decorItem displayWeight(int w) {
      this.displayWeight = w;
      return this;
   }

   decorItem namePrefix(String w) {
      this.namePrefix = w;
      return this;
   }

   decorItem setD(paint2Base p) {
      this.paint2 = p;
      return this;
   }

   decorItem setD(pingBase p) {
      this.ping = p;
      return this;
   }

   decorItem setD(nextGleisBase p) {
      this.nextGleis = p;
      return this;
   }

   @Deprecated
   decorItem setClassicPainter() {
      this.elementPaint = new elementPainterClassic();
      return this;
   }

   decorItem setLightlessPainter() {
      this.elementPaint = new elementPainterCentered();
      return this;
   }

   decorItem setPainter(int painter) {
      if (painter == -1) {
         this.setClassicPainter();
      } else if (painter == 1) {
         this.setLightlessPainter();
      } else if (painter == 0) {
         this.elementPaint = new elementPainterCenteredLight();
      }

      return this;
   }

   decorItem setPainter(elementPainterBase painter) {
      this.elementPaint = painter;
      return this;
   }
}
