package js.java.isolate.sim.gleis.gleisElements;

import java.util.EnumSet;

public interface gleisElements {
   EnumSet<gleisElements.RICHTUNG> R_ALL = EnumSet.allOf(gleisElements.RICHTUNG.class);
   EnumSet<gleisElements.RICHTUNG> R_NONE = EnumSet.noneOf(gleisElements.RICHTUNG.class);
   EnumSet<gleisElements.RICHTUNG> R_HORIZ = EnumSet.of(gleisElements.RICHTUNG.left, gleisElements.RICHTUNG.right);
   int TYP_LEER = 0;
   int TYP_SCHIENE = 1;
   int TYP_BSTTRENNER = 2;
   int TYP_MARKER = 3;
   int TYP_TEXT = 4;
   int TYP_DISPLAY = 5;
   int TYP_KNOPF = 6;
   element ELEMENT_LEER = new element_typElement(0, 0, R_NONE, true);
   element ALLE_GLEISE = new element_typ(1);
   element ELEMENT_STRECKE = new glc_schiene(0, R_NONE, true);
   @Deprecated
   element ELEMENT_KONTAKT = new glc_schiene(1, R_ALL);
   element ELEMENT_SIGNAL = new glc_schiene(2, R_ALL);
   element ELEMENT_WEICHEUNTEN = new glc_schiene(3, R_NONE);
   element ELEMENT_WEICHEOBEN = new glc_schiene(4, R_NONE);
   element ELEMENT_BAHNSTEIG = new glc_schiene(5, R_ALL);
   element ELEMENT_EINFAHRT = new glc_schiene(6, R_ALL);
   element ELEMENT_AUSFAHRT = new glc_schiene(7, R_ALL);
   element ELEMENT_KREUZUNGBRUECKE = new glc_schiene.glc_schieneNoLight(8);
   element ELEMENT_SPRUNG = new glc_schiene(9, R_ALL);
   element ELEMENT_KREUZUNG = new glc_schiene(10, R_NONE);
   element ELEMENT_BAHNÜBERGANG = new glc_schiene(11, R_NONE);
   element ELEMENT_HALTEPUNKT = new glc_schiene(12, R_ALL);
   element ELEMENT_DISPLAYKONTAKT = new glc_schiene(13, R_ALL);
   element ELEMENT_ÜBERGABEPUNKT = new glc_schiene(14, R_ALL);
   element ELEMENT_WIEDERVMAX = new glc_schiene(15, R_ALL);
   element ELEMENT_ANRUFÜBERGANG = new glc_schiene(16, R_NONE);
   element ELEMENT_SETVMAX = new glc_schiene(17, R_ALL);
   element ELEMENT_GLEISLABEL = new glc_schiene(18, R_ALL);
   element ELEMENT_WBAHNÜBERGANG = new glc_schiene(19, R_NONE);
   element ELEMENT_AUTOBAHNÜBERGANG = new glc_schiene(20, R_NONE);
   element ELEMENT_ZWERGSIGNAL = new glc_schiene(35, R_ALL);
   element ELEMENT_SIGNALKNOPF = new glc_schiene(36, R_ALL);
   element ELEMENT_ZDECKUNGSSIGNAL = new glc_schiene(37, R_ALL);
   element ELEMENT_STRECKELICHTLOS = new glc_schiene.glc_schieneNoLight(38);
   element ELEMENT_VORSIGNAL = new glc_schiene(39, R_ALL);
   element ELEMENT_VORSIGNALTRENNER = new glc_schiene(40, R_ALL);
   element ELEMENT_SIGNAL_ZIELKNOPF = new glc_schiene(41, R_ALL);
   element ELEMENT_AUSFAHRT_ZIELKNOPF = new glc_schiene(42, R_ALL);
   element ELEMENT_ANRUECKER = new glc_schiene(43, R_ALL);
   element ALLE_WEICHEN = new element_list(ELEMENT_WEICHEOBEN, ELEMENT_WEICHEUNTEN);
   element ALLE_SIGNALE = new element_list(ELEMENT_SIGNAL, ELEMENT_ZWERGSIGNAL, ELEMENT_ZDECKUNGSSIGNAL);
   element ALLE_STRECKENSIGNALE = new element_list(ELEMENT_SIGNAL, ELEMENT_ZWERGSIGNAL);
   element ALLE_BAHNÜBERGÄNGE = new element_list(ELEMENT_BAHNÜBERGANG, ELEMENT_ANRUFÜBERGANG, ELEMENT_WBAHNÜBERGANG, ELEMENT_AUTOBAHNÜBERGANG);
   element ALLE_STARTSIGNALE = new element_list(ELEMENT_SIGNAL, ELEMENT_ZWERGSIGNAL);
   element ALLE_STOPSIGNALE = new element_list(ELEMENT_SIGNAL, ELEMENT_ZWERGSIGNAL, ELEMENT_AUSFAHRT);
   element ALLE_BAHNSTEIGE = new element_list(ELEMENT_BAHNSTEIG, ELEMENT_HALTEPUNKT);
   element ALLE_NOLIGHT = new element_list(ELEMENT_KREUZUNGBRUECKE, ELEMENT_STRECKELICHTLOS);
   element ALLE_DISPLAYS = new element_typ(5);
   element ELEMENT_AIDDISPLAY = new glc_display(21);
   element ELEMENT_2ZDISPLAY = new glc_display(22, true);
   element ELEMENT_3ZDISPLAY = new glc_display(23);
   element ELEMENT_4ZDISPLAY = new glc_display(24);
   element ELEMENT_5ZDISPLAY = new glc_display(25);
   element ELEMENT_6ZDISPLAY = new glc_display(26);
   element ELEMENT_7ZDISPLAY = new glc_display(27);
   element ELEMENT_8ZDISPLAY = new glc_display(28);
   element ELEMENT_BÜDISPLAY = new glc_display(29);
   element ALLE_ZUGDISPLAYS = new element_list(
      ELEMENT_2ZDISPLAY, ELEMENT_3ZDISPLAY, ELEMENT_4ZDISPLAY, ELEMENT_5ZDISPLAY, ELEMENT_6ZDISPLAY, ELEMENT_7ZDISPLAY, ELEMENT_8ZDISPLAY
   );
   element ALLE_ENR_BAHNÜBERGÄNGE = new element_list(
      ELEMENT_BAHNÜBERGANG, ELEMENT_ANRUFÜBERGANG, ELEMENT_WBAHNÜBERGANG, ELEMENT_AUTOBAHNÜBERGANG, ELEMENT_BÜDISPLAY
   );
   element ALLE_TEXTE = new element_typ(4);
   element ELEMENT_OVAL = new glc_text(0, R_HORIZ, true);
   element ELEMENT_TEXTPLATTE = new glc_text(33, R_HORIZ);
   element ELEMENT_GRAVUR = new glc_text(34, R_ALL);
   element ELEMENT_LABEL = new glc_text(35, R_ALL);
   element ELEMENT_BAHNSTEIGFLÄCHE = new glc_text(36, R_HORIZ);
   element ALLE_KNÖPFE = new element_typ(6);
   element ELEMENT_ÜBERGABEAKZEPTOR = new glc_knopf(41, R_ALL, true);
   element ALLE_BSTTRENNER = new element_typ(2);
   element ELEMENT_TRENNER = new glc_trenner(0, true);
   int STATUS_FREI = 0;
   int STATUS_RESERVIERT = 1;
   int STATUS_BELEGT = 2;
   int STATUS_RESERVING = 3;
   int STATUS_SCHUTZ = 4;
   int BÜTIME_MINOPEN = 60;
   int BÜTIME_MAXCLOSE = 180;
   int BÜTIME_MAXOPEN = 180;
   gleisElements.Stellungen ST_SIGNAL_ROT = gleisElements.Stellungen.signal_rot;
   gleisElements.Stellungen ST_SIGNAL_GRÜN = gleisElements.Stellungen.signal_grün;
   gleisElements.Stellungen ST_SIGNAL_AUS = gleisElements.Stellungen.aus;
   gleisElements.Stellungen ST_SIGNAL_ZS1 = gleisElements.Stellungen.signal_zs1;
   gleisElements.Stellungen ST_SIGNAL_RF = gleisElements.Stellungen.signal_rf;
   gleisElements.Stellungen ST_ZDSIGNAL_ROT = gleisElements.Stellungen.signal_rot;
   gleisElements.Stellungen ST_ZDSIGNAL_GRÜN = gleisElements.Stellungen.signal_grün;
   gleisElements.Stellungen ST_ZDSIGNAL_AUS = gleisElements.Stellungen.aus;
   gleisElements.Stellungen ST_ZDSIGNAL_FESTGELEGT = gleisElements.Stellungen.signal_zdfest;
   gleisElements.Stellungen ST_WEICHE_GERADE = gleisElements.Stellungen.weiche_gerade;
   gleisElements.Stellungen ST_WEICHE_ABZWEIG = gleisElements.Stellungen.weiche_abzweig;
   gleisElements.Stellungen ST_WEICHE_AUS = gleisElements.Stellungen.aus;
   gleisElements.Stellungen ST_BAHNÜBERGANG_OFFEN = gleisElements.Stellungen.bahnübergang_offen;
   gleisElements.Stellungen ST_BAHNÜBERGANG_GESCHLOSSEN = gleisElements.Stellungen.bahnübergang_geschlossen;
   gleisElements.Stellungen ST_BAHNÜBERGANG_AUS = gleisElements.Stellungen.aus;
   gleisElements.Stellungen ST_ANRUFÜBERGANG_OFFEN = gleisElements.Stellungen.bahnübergang_offen;
   gleisElements.Stellungen ST_ANRUFÜBERGANG_GESCHLOSSEN = gleisElements.Stellungen.bahnübergang_geschlossen;
   gleisElements.Stellungen ST_ANRUFÜBERGANG_AUS = gleisElements.Stellungen.aus;
   gleisElements.Stellungen ST_ÜBERGABEAKZEPTOR_UNDEF = gleisElements.Stellungen.undef;
   gleisElements.Stellungen ST_ÜBERGABEAKZEPTOR_OK = gleisElements.Stellungen.übergabeakzeptor_ok;
   gleisElements.Stellungen ST_ÜBERGABEAKZEPTOR_NOK = gleisElements.Stellungen.übergabeakzeptor_nok;
   gleisElements.Stellungen ST_ÜBERGABEAKZEPTOR_ANFRAGE = gleisElements.Stellungen.übergabeakzeptor_anfrage;
   gleisElements.Stellungen ST_ÜBERGABEPUNKT_AUS = gleisElements.Stellungen.übergabepunkt_aus;
   gleisElements.Stellungen ST_ÜBERGABEPUNKT_ROT = gleisElements.Stellungen.übergabepunkt_rot;
   gleisElements.Stellungen ST_ÜBERGABEPUNKT_GRÜN = gleisElements.Stellungen.übergabepunkt_grün;

   public static enum RICHTUNG {
      left('L'),
      right('R'),
      up('U'),
      down('D');

      private final char r;

      private RICHTUNG(char r) {
         this.r = r;
      }

      public String toString() {
         return Character.toString(this.r);
      }

      public char getChar() {
         return this.r;
      }
   }

   public static enum Stellungen {
      undef(""),
      aus("aus", gleisElements.ZugStellungen.stop),
      signal_rot("rot", gleisElements.ZugStellungen.stop),
      signal_grün("grün", gleisElements.ZugStellungen.fahrt),
      signal_zs1("zs1", gleisElements.ZugStellungen.langsamfahrt),
      signal_zdfest("fest", gleisElements.ZugStellungen.stop),
      signal_rf("rf", gleisElements.ZugStellungen.rangierfahrt),
      weiche_gerade("gerade"),
      weiche_abzweig("abzweig"),
      bahnübergang_offen("offen"),
      bahnübergang_geschlossen("geschlossen"),
      übergabeakzeptor_ok("ok"),
      übergabeakzeptor_nok("nok"),
      übergabeakzeptor_anfrage("anfrage"),
      übergabepunkt_aus("üpaus"),
      übergabepunkt_rot("üprot"),
      übergabepunkt_grün("üpgrün");

      private String savetext;
      private gleisElements.ZugStellungen zugStellung;

      private Stellungen(String savetext, gleisElements.ZugStellungen zst) {
         this.savetext = savetext;
         this.zugStellung = zst;
      }

      private Stellungen(String savetext) {
         this.savetext = savetext;
         this.zugStellung = gleisElements.ZugStellungen.undef;
      }

      public String getSaveText() {
         return this.savetext;
      }

      public gleisElements.ZugStellungen getZugStellung() {
         return this.zugStellung;
      }

      public String toString() {
         return this.getSaveText();
      }

      public static gleisElements.Stellungen string2stellung(String st) {
         for (gleisElements.Stellungen s : values()) {
            if (st.equalsIgnoreCase(s.getSaveText())) {
               return s;
            }
         }

         return undef;
      }
   }

   public static enum ZugStellungen {
      undef,
      stop,
      fahrt,
      langsamfahrt,
      rangierfahrt;
   }
}
