package js.java.isolate.sim.gleis;

import java.util.HashMap;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.schaltungen.UserContext;

public class decor {
   private static decor d = null;
   private final HashMap<element, decorItem> types = new HashMap();

   public static decor getDecor() {
      return d;
   }

   public static void createDecor(UserContext uc) {
      d = new decor();
      uc.addCloseObject(() -> d = null);
   }

   private decor() {
      super();
      this.addTyp1(this.types);
      this.addTyp2(this.types);
      this.addTyp3(this.types);
      this.addTyp4(this.types);
      this.addTyp5(this.types);
      this.addTyp6(this.types);
   }

   public decor replaceDecorPainter(element item, paint2Base painter) {
      try {
         decorItem di = (decorItem)this.types.get(item);
         if (di != null) {
            synchronized(di) {
               di.setD(painter);
            }
         }
      } catch (Exception var7) {
      }

      return this;
   }

   public decor replaceElementPainter(element item, int painter) {
      try {
         decorItem di = (decorItem)this.types.get(item);
         if (di != null) {
            synchronized(di) {
               di.setPainter(painter);
            }
         }
      } catch (Exception var7) {
      }

      return this;
   }

   public decor replaceElementPainter(element item, elementPainterBase painter) {
      try {
         decorItem di = (decorItem)this.types.get(item);
         if (di != null) {
            synchronized(di) {
               di.setPainter(painter);
            }
         }
      } catch (Exception var7) {
      }

      return this;
   }

   private boolean typAllowes(element element, decor.getDecorItemItem field) {
      decorItem di = (decorItem)this.types.get(element);
      return di != null ? field.getValue(di) : false;
   }

   public boolean typAllowesENRedit(element element) {
      return this.typAllowes(element, new decor.getallowsEditENR());
   }

   public boolean typRequiresENR(element element) {
      return this.typAllowes(element, new decor.getrequiredENR());
   }

   public boolean typAllowesSWwertedit(element element) {
      return this.typAllowes(element, new decor.getallowsEditSWwert());
   }

   public boolean typRequiresSWwert(element element) {
      return this.typAllowes(element, new decor.getrequiredSWwert());
   }

   public boolean typShouldHaveSWwert(element element) {
      return this.typAllowes(element, new decor.getshouldhaveSWwert());
   }

   public boolean typAllowsVerbund(element element) {
      return this.typAllowes(element, new decor.getallowsVerbund());
   }

   public boolean typKeepEnr(element element) {
      return this.typAllowes(element, new decor.getkeepEnr());
   }

   public boolean typHasEnrPartner(element element) {
      return this.typAllowes(element, new decor.gethasEnrPartner());
   }

   public boolean typHasLayer2Painter(element element) {
      return this.typAllowes(element, new decor.gethasLayer2Painter());
   }

   public boolean typHasElementName(element element) {
      return this.typAllowes(element, new decor.gethasName());
   }

   @Deprecated
   public boolean needExtraRight(element element) {
      decorItem di = (decorItem)this.types.get(element);
      return di != null ? di.elementPaint.needExtraRight() : false;
   }

   int getDisplayWeight(element element) {
      decorItem di = (decorItem)this.types.get(element);
      return di != null ? di.displayWeight : 0;
   }

   String getNamePrefix(element element) {
      decorItem di = (decorItem)this.types.get(element);
      return di != null ? di.namePrefix : null;
   }

   void setDecor(gleis gl) {
      try {
         decorItem di = (decorItem)this.types.get(gl.telement);
         if (di != null) {
            gl.gleisdecor = di;
         } else {
            gl.gleisdecor = new decorItem();
         }
      } catch (Exception var3) {
      }
   }

   private void addTyp1(HashMap<element, decorItem> ret) {
      ret.put(gleisElements.ELEMENT_STRECKE, new decorItem().allowVerbund().setD(new ping_schutzReservingTjm()).setD(new next_strecke()));
      ret.put(gleisElements.ELEMENT_STRECKELICHTLOS, new decorItem().allowVerbund().setD(new ping_schutzReservingTjm()).setD(new next_strecke()));
      ret.put(gleisElements.ELEMENT_KONTAKT, new decorItem().setD(new next_strecke()));
      ret.put(
         gleisElements.ELEMENT_SIGNAL,
         new decorItem()
            .namePrefix("S")
            .requireENR()
            .allowDisplayTrigger()
            .displayWeight(5)
            .enrPartner(gleisElements.ELEMENT_SIGNALKNOPF)
            .enrPartner(gleisElements.ELEMENT_SIGNAL_ZIELKNOPF)
            .setD(new ping_signalFs(new ping_schutzReservingTjm(new ping_stellungAusTjm(new ping_highlightCnt()))))
            .setD(new next_createName(new next_signal(new next_strecke())))
            .setD(new paint_signal())
      );
      ret.put(
         gleisElements.ELEMENT_WEICHEUNTEN,
         new decorItem()
            .namePrefix("")
            .requireENR()
            .setD(new ping_reserving(new ping_schutzReservingTjm(new ping_stellungAusTjm(new ping_highlightCnt()))))
            .setD(new next_createName(new next_weiche()))
            .setD(new paint_weiche())
      );
      ret.put(
         gleisElements.ELEMENT_WEICHEOBEN,
         new decorItem()
            .namePrefix("")
            .requireENR()
            .setD(new ping_reserving(new ping_schutzReservingTjm(new ping_stellungAusTjm(new ping_highlightCnt()))))
            .setD(new next_createName(new next_weiche()))
            .setD(new paint_weiche())
      );
      ret.put(
         gleisElements.ELEMENT_BAHNSTEIG,
         new decorItem()
            .requireSWwert()
            .allowEditSWwert()
            .allowVerbund()
            .displayWeight(10)
            .isUserDisplayTrigger()
            .setD(new ping_schutzReservingTjm())
            .setD(new next_strecke())
            .setD(new paint_halt())
      );
      ret.put(
         gleisElements.ELEMENT_EINFAHRT,
         new decorItem()
            .namePrefix("")
            .requireENR()
            .shouldhaveSWwert()
            .allowEditSWwert()
            .displayWeight(20)
            .allowEditENR()
            .keepEnr()
            .enrPartner(gleisElements.ELEMENT_ÜBERGABEAKZEPTOR)
            .isUserDisplayTrigger()
            .allowVerbund()
            .setD(new ping_schutzReservingTjm())
            .setD(new next_einfahrt(new next_strecke()))
            .setD(new paint_einfahrt())
      );
      ret.put(
         gleisElements.ELEMENT_AUSFAHRT,
         new decorItem()
            .namePrefix("")
            .requireENR()
            .shouldhaveSWwert()
            .allowDisplayTrigger()
            .allowEditENR()
            .allowEditSWwert()
            .keepEnr()
            .enrPartner(gleisElements.ELEMENT_ÜBERGABEPUNKT)
            .enrPartner(gleisElements.ELEMENT_AUSFAHRT_ZIELKNOPF)
            .allowVerbund()
            .setD(new ping_ausfahrt(new ping_schutzReservingTjm()))
            .setD(new next_ausfahrt(new next_strecke()))
            .setD(new paint_ausfahrt())
      );
      ret.put(
         gleisElements.ELEMENT_KREUZUNGBRUECKE,
         new decorItem().setD(new ping_schutzReservingTjm()).setD(new next_kreuzung(new next_strecke())).setD(new paint_kreuzungsbruecke())
      );
      ret.put(
         gleisElements.ELEMENT_SPRUNG,
         new decorItem()
            .requireENR()
            .allowEditENR()
            .enrPartner(gleisElements.ELEMENT_SPRUNG)
            .setD(new ping_schutzReservingTjm())
            .setD(new next_sprung(new next_strecke()))
            .setD(new paint_sprung())
      );
      ret.put(gleisElements.ELEMENT_KREUZUNG, new decorItem().setD(new ping_schutzReservingTjm()).setD(new next_kreuzung(new next_strecke())));
      ret.put(
         gleisElements.ELEMENT_HALTEPUNKT,
         new decorItem()
            .requireSWwert()
            .allowEditSWwert()
            .allowVerbund()
            .isUserDisplayTrigger()
            .setD(new ping_schutzReservingTjm())
            .setD(new next_strecke())
            .setD(new paint_halt())
      );
      ret.put(
         gleisElements.ELEMENT_DISPLAYKONTAKT,
         new decorItem()
            .requireSWwert()
            .allowEditSWwert()
            .isUserDisplayTrigger()
            .displayWeight(8)
            .allowDisplayFStrigger()
            .allowVerbund()
            .setD(new ping_schutzReservingTjm())
            .setD(new next_strecke())
            .setD(new paint_halt())
      );
      ret.put(
         gleisElements.ELEMENT_ÜBERGABEPUNKT,
         new decorItem()
            .requireENR()
            .allowEditENR()
            .keepEnr()
            .enrPartner(gleisElements.ELEMENT_AUSFAHRT)
            .enrPartner(gleisElements.ELEMENT_AUSFAHRT_ZIELKNOPF)
            .allowVerbund()
            .setD(new ping_uep(new ping_schutzReservingTjm()))
            .setD(new next_strecke())
            .setD(new paint_uebergabepunkt())
      );
      ret.put(
         gleisElements.ELEMENT_WIEDERVMAX, new decorItem().allowVerbund().setD(new ping_schutzReservingTjm()).setD(new next_strecke()).setD(new paint_vmax())
      );
      ret.put(
         gleisElements.ELEMENT_ANRUFÜBERGANG,
         new decorItem()
            .namePrefix("Bü*")
            .requireENR()
            .allowEditENR()
            .enrPartner(gleisElements.ELEMENT_ANRUFÜBERGANG)
            .enrPartner(gleisElements.ELEMENT_BÜDISPLAY)
            .allowVerbund()
            .setD(new ping_bueOffenVerwaltung(new ping_anrufBue(new ping_schutzReservingTjm(new ping_highlightCnt()))))
            .setD(new next_createName(new next_strecke()))
            .setD(new paint_anrufuebergang())
      );
      ret.put(
         gleisElements.ELEMENT_WBAHNÜBERGANG,
         new decorItem()
            .namePrefix("Bü")
            .requireENR()
            .allowEditENR()
            .enrPartner(gleisElements.ELEMENT_WBAHNÜBERGANG)
            .enrPartner(gleisElements.ELEMENT_BÜDISPLAY)
            .allowVerbund()
            .setD(new ping_bueWVerwaltung(new ping_schutzReservingTjm(new ping_highlightCnt())))
            .setD(new next_createName(new next_strecke()))
            .setD(new paint_wbahnuebergang())
      );
      ret.put(
         gleisElements.ELEMENT_BAHNÜBERGANG,
         new decorItem()
            .namePrefix("Bü")
            .requireENR()
            .allowEditENR()
            .enrPartner(gleisElements.ELEMENT_BAHNÜBERGANG)
            .enrPartner(gleisElements.ELEMENT_BÜDISPLAY)
            .allowVerbund()
            .setD(new ping_bueOffenVerwaltung(new ping_schutzReservingTjm(new ping_highlightCnt())))
            .setD(new next_createName(new next_strecke()))
            .setD(new paint_bahnuebergang())
      );
      ret.put(
         gleisElements.ELEMENT_AUTOBAHNÜBERGANG,
         new decorItem()
            .namePrefix("Bü")
            .requireENR()
            .allowEditENR()
            .enrPartner(gleisElements.ELEMENT_AUTOBAHNÜBERGANG)
            .enrPartner(gleisElements.ELEMENT_BÜDISPLAY)
            .allowVerbund()
            .setD(new ping_bueAutoVerwaltung(new ping_schutzReservingTjm(new ping_highlightCnt())))
            .setD(new next_createName(new next_strecke()))
            .setD(new paint_wbahnuebergang())
      );
      ret.put(
         gleisElements.ELEMENT_SETVMAX,
         new decorItem().requireSWwert().allowEditSWwert().allowVerbund().setD(new ping_schutzReservingTjm()).setD(new next_strecke()).setD(new paint_vmax())
      );
      ret.put(
         gleisElements.ELEMENT_GLEISLABEL,
         new decorItem()
            .requireSWwert()
            .allowEditSWwert()
            .allowVerbund()
            .setD(new ping_schutzReservingTjm())
            .setD(new next_strecke())
            .setD(new paint_gleislabel())
      );
      ret.put(
         gleisElements.ELEMENT_ZWERGSIGNAL,
         new decorItem()
            .namePrefix("W")
            .requireENR()
            .setD(new ping_schutzReservingTjm())
            .setD(new next_createName(new next_signal(new next_strecke())))
            .setD(new paint_zwerg())
      );
      ret.put(
         gleisElements.ELEMENT_SIGNALKNOPF,
         new decorItem()
            .requireENR()
            .allowEditENR()
            .enrPartner(gleisElements.ELEMENT_SIGNAL)
            .enrPartner(gleisElements.ELEMENT_SIGNAL_ZIELKNOPF)
            .setD(new ping_schutzReservingTjm())
            .setD(new next_strecke())
            .setD(new paint_knopfzwerg())
      );
      ret.put(
         gleisElements.ELEMENT_ZDECKUNGSSIGNAL,
         new decorItem()
            .requireENR()
            .setD(new ping_zdeckung(new ping_schutzReservingTjm()))
            .setD(new next_signal(new next_strecke()))
            .setD(new paint_zdeckung())
      );
      ret.put(
         gleisElements.ELEMENT_VORSIGNAL,
         new decorItem().allowVerbund().setD(new ping_schutzReservingTjm()).setD(new next_strecke()).setD(new paint_vorsignal())
      );
      ret.put(
         gleisElements.ELEMENT_VORSIGNALTRENNER,
         new decorItem().allowVerbund().setD(new ping_schutzReservingTjm()).setD(new next_strecke()).setD(new paint_vorsignaltrenner())
      );
      ret.put(
         gleisElements.ELEMENT_SIGNAL_ZIELKNOPF,
         new decorItem()
            .requireENR()
            .allowEditENR()
            .enrPartner(gleisElements.ELEMENT_SIGNAL)
            .enrPartner(gleisElements.ELEMENT_SIGNALKNOPF)
            .enrPartner(gleisElements.ELEMENT_SIGNAL_ZIELKNOPF)
            .setD(new ping_schutzReservingTjm())
            .setD(new next_strecke())
            .setD(new paint_knopfziel())
      );
      ret.put(
         gleisElements.ELEMENT_AUSFAHRT_ZIELKNOPF,
         new decorItem()
            .requireENR()
            .allowEditENR()
            .keepEnr()
            .enrPartner(gleisElements.ELEMENT_AUSFAHRT)
            .enrPartner(gleisElements.ELEMENT_AUSFAHRT_ZIELKNOPF)
            .enrPartner(gleisElements.ELEMENT_ÜBERGABEPUNKT)
            .setD(new ping_schutzReservingTjm())
            .setD(new next_strecke())
            .setD(new paint_knopfziel())
      );
      ret.put(
         gleisElements.ELEMENT_ANRUECKER,
         new decorItem().allowVerbund().setD(new ping_schutzReservingTjm()).setD(new next_strecke()).setD(new paint_anruecker())
      );
   }

   private void addTyp2(HashMap<element, decorItem> ret) {
      ret.put(gleisElements.ELEMENT_TRENNER, new decorItem().setLightlessPainter());
   }

   private void addTyp3(HashMap<element, decorItem> ret) {
   }

   private void addTyp4(HashMap<element, decorItem> ret) {
      ret.put(gleisElements.ELEMENT_OVAL, new decorItem().requireSWwert().allowEditSWwert().setD(new paint_text_oval()));
      ret.put(gleisElements.ELEMENT_TEXTPLATTE, new decorItem().requireSWwert().allowEditSWwert().setD(new paint_text_platte()));
      ret.put(gleisElements.ELEMENT_GRAVUR, new decorItem().requireSWwert().allowEditSWwert().setD(new paint_text_gravur()));
      ret.put(gleisElements.ELEMENT_LABEL, new decorItem().requireSWwert().allowEditSWwert().setD(new paint_gleislabel()));
      ret.put(gleisElements.ELEMENT_BAHNSTEIGFLÄCHE, new decorItem().allowEditSWwert().setD(new paint_bstg_flaeche()));
   }

   private void addTyp5(HashMap<element, decorItem> ret) {
      ret.put(gleisElements.ELEMENT_AIDDISPLAY, new decorItem().requireSWwert().allowEditSWwert().setD(new ping_display()).setD(new paint_display()));
      ret.put(gleisElements.ELEMENT_2ZDISPLAY, new decorItem().requireSWwert().allowEditSWwert().setD(new ping_display()).setD(new paint_display()));
      ret.put(gleisElements.ELEMENT_3ZDISPLAY, new decorItem().requireSWwert().allowEditSWwert().setD(new ping_display()).setD(new paint_display()));
      ret.put(gleisElements.ELEMENT_4ZDISPLAY, new decorItem().requireSWwert().allowEditSWwert().setD(new ping_display()).setD(new paint_display()));
      ret.put(gleisElements.ELEMENT_5ZDISPLAY, new decorItem().requireSWwert().allowEditSWwert().setD(new ping_display()).setD(new paint_display()));
      ret.put(gleisElements.ELEMENT_6ZDISPLAY, new decorItem().requireSWwert().allowEditSWwert().setD(new ping_display()).setD(new paint_display()));
      ret.put(gleisElements.ELEMENT_7ZDISPLAY, new decorItem().requireSWwert().allowEditSWwert().setD(new ping_display()).setD(new paint_display()));
      ret.put(gleisElements.ELEMENT_8ZDISPLAY, new decorItem().requireSWwert().allowEditSWwert().setD(new ping_display()).setD(new paint_display()));
      ret.put(
         gleisElements.ELEMENT_BÜDISPLAY,
         new decorItem()
            .requireENR()
            .allowEditENR()
            .namePrefix("")
            .enrPartner(gleisElements.ELEMENT_WBAHNÜBERGANG)
            .setD(new ping_buedisplay())
            .setD(new paint_buedisplay())
      );
   }

   private void addTyp6(HashMap<element, decorItem> ret) {
      ret.put(
         gleisElements.ELEMENT_ÜBERGABEAKZEPTOR,
         new decorItem()
            .requireENR()
            .allowEditENR()
            .allowEditSWwert()
            .keepEnr()
            .enrPartner(gleisElements.ELEMENT_EINFAHRT)
            .enrPartner(gleisElements.ELEMENT_ÜBERGABEAKZEPTOR)
            .setD(new ping_akzeptor())
            .setD(new paint_uebergabeakzeptor())
      );
   }

   private interface getDecorItemItem {
      boolean getValue(decorItem var1);
   }

   private static class getallowsEditENR implements decor.getDecorItemItem {
      private getallowsEditENR() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return di.allowsEditENR;
      }
   }

   private static class getallowsEditSWwert implements decor.getDecorItemItem {
      private getallowsEditSWwert() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return di.allowsEditSWwert;
      }
   }

   private static class getallowsVerbund implements decor.getDecorItemItem {
      private getallowsVerbund() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return di.allowsVerbund;
      }
   }

   private static class gethasEnrPartner implements decor.getDecorItemItem {
      private gethasEnrPartner() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return di.hasEnrPartner;
      }
   }

   private static class gethasLayer2Painter implements decor.getDecorItemItem {
      private gethasLayer2Painter() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return !di.paint2.getClass().isAssignableFrom(paint2Base.class);
      }
   }

   private static class gethasName implements decor.getDecorItemItem {
      private gethasName() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return di.namePrefix != null;
      }
   }

   private static class getkeepEnr implements decor.getDecorItemItem {
      private getkeepEnr() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return di.keepEnr;
      }
   }

   private static class getrequiredENR implements decor.getDecorItemItem {
      private getrequiredENR() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return di.requiredENR;
      }
   }

   private static class getrequiredSWwert implements decor.getDecorItemItem {
      private getrequiredSWwert() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return di.requiredSWwert;
      }
   }

   private static class getshouldhaveSWwert implements decor.getDecorItemItem {
      private getshouldhaveSWwert() {
         super();
      }

      @Override
      public boolean getValue(decorItem di) {
         return di.shouldhaveSWwert;
      }
   }
}
