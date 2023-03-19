package js.java.isolate.sim.gleis.colorSystem;

import java.awt.Color;
import java.util.TreeMap;
import js.java.isolate.sim.gleis.gleis;

public class gleisColor {
   public static final int DIMLEVELS = 20;
   protected static gleisColor instance = null;
   protected gleisColor.COLORTYPE type;
   protected colorStruct mycolorbase;
   protected colorStruct workingcolor;
   private colorStruct currentColors = new nullColor();

   public static gleisColor getInstance() {
      if (instance == null) {
         instance = new gleisColor(gleisColor.COLORTYPE.SIMULATOR_TAG);
      }

      return instance;
   }

   public static gleisColor getInstance(gleisColor.COLORTYPE t) {
      if (instance != null && instance.type != t) {
         instance = null;
      }

      if (instance == null) {
         instance = new gleisColor(t);
      }

      return instance;
   }

   protected gleisColor(gleisColor.COLORTYPE t) {
      super();
      this.type = t;
      this.mycolorbase = new colorStruct();
      this.init();
   }

   public void close() {
      this.mycolorbase = null;
      instance = null;
   }

   protected void init() {
      this.mycolorbase.col_text = new Color(0, 0, 0);
      this.mycolorbase.col_text_s = new Color(238, 238, 238);
      this.mycolorbase.col_stellwerk_bsttrenner = new Color(255, 255, 0);
      this.mycolorbase.col_marker = new Color(221, 0, 0);
      this.mycolorbase.col_pfeil = new Color(34, 102, 221);
      this.mycolorbase.col_aktiv = new Color(224, 255, 224);
      int j = 0;

      for(int i = 0; i < this.mycolorbase.col_aktiv2.length / 2; ++j) {
         this.mycolorbase.col_aktiv2[i] = new Color(224 - j * 8, 255, 224 - j * 8, 160);
         this.mycolorbase.col_aktiv3[i] = new Color(204, 0 + j * 24, 204 - j * 16, 128);
         ++i;
      }

      for(int i = this.mycolorbase.col_aktiv2.length / 2; i < this.mycolorbase.col_aktiv2.length; --j) {
         this.mycolorbase.col_aktiv2[i] = new Color(224 - j * 8, 255, 224 - j * 8, 160);
         this.mycolorbase.col_aktiv3[i] = new Color(204, 0 + j * 24, 204 - j * 16, 128);
         ++i;
      }

      j = 0;

      for(int i = 0; i < this.mycolorbase.col_highlight.length; ++j) {
         this.mycolorbase.col_highlight[i] = new Color(17 + j * 18, 17 + j * 18, Math.min(240 + j * 4, 255));
         ++i;
      }

      j = 208;
      this.mycolorbase.col_stellwerk_back = new Color(208, 208, 208);
      this.mycolorbase.col_stellwerk_backmulti = new TreeMap();
      this.mycolorbase.addColor("normal", 208, 208, 208);
      this.mycolorbase.addColor("grau", 170, 170, 170);
      this.mycolorbase.addColor("hellgrau", 238, 238, 238);
      this.mycolorbase.addColor("grün", 119, 238, 119);
      this.mycolorbase.addColor("gelb", 221, 221, 85);
      this.mycolorbase.addColor("rot", 238, 119, 119);
      this.mycolorbase.addColor("blau", 136, 136, 255);
      this.mycolorbase.addColor("orange", 255, 163, 0);
      this.mycolorbase.addColor("schwarz", 0, 0, 0);
      this.mycolorbase.addColor("weiß", 255, 255, 255);
      this.mycolorbase.col_stellwerk_raster = new Color(195, 195, 195);
      this.mycolorbase.col_stellwerk_gleis = new Color(0, 0, 0);
      this.mycolorbase.col_stellwerk_frei = new Color(119, 119, 119);
      this.mycolorbase.col_stellwerk_displayoff = new Color(51, 51, 51);
      this.mycolorbase.col_stellwerk_reserviert = new Color(238, 238, 187);
      this.mycolorbase.col_stellwerk_belegt = new Color(204, 0, 0);
      this.mycolorbase.col_stellwerk_schwarz = new Color(0, 0, 0);
      this.mycolorbase.col_stellwerk_rotein = new Color(255, 17, 17);
      this.mycolorbase.col_stellwerk_rotaus = new Color(102, 17, 17);
      this.mycolorbase.col_stellwerk_rot = new Color(221, 0, 0);
      this.mycolorbase.col_stellwerk_rot_locked = new Color(221, 0, 0, 187);
      this.mycolorbase.col_stellwerk_rot_beleuchtet = new Color(255, 170, 153);
      this.mycolorbase.col_stellwerk_rot_umleuchtung = new Color(255, 204, 204, 119);
      this.mycolorbase.col_stellwerk_gruenein = new Color(17, 238, 17);
      this.mycolorbase.col_stellwerk_gruenaus = new Color(17, 85, 17);
      this.mycolorbase.col_stellwerk_gelbein = new Color(255, 255, 17);
      this.mycolorbase.col_stellwerk_gelbaus = new Color(102, 102, 17);
      this.mycolorbase.col_stellwerk_weiss = new Color(255, 255, 255);
      this.mycolorbase.col_stellwerk_defekt = new Color(68, 255, 255);
      this.mycolorbase.col_stellwerk_grau = new Color(170, 170, 170);
      this.mycolorbase.col_stellwerk_grau_locked = new Color(187, 170, 170, 187);
      this.mycolorbase.col_stellwerk_geländer = new Color(153, 153, 255);
      this.mycolorbase.col_stellwerk_zs1 = new Color(238, 238, 221);
      this.mycolorbase.col_stellwerk_nummer = new Color(128, 128, 144);
      this.mycolorbase.col_stellwerk_signalnummer = new Color(0, 0, 0);
      this.mycolorbase.col_stellwerk_signalnummerhgr = new Color(255, 255, 255, 221);
      this.mycolorbase.col_stellwerk_zugdisplay = new Color(255, 136, 136);
      this.mycolorbase.col_stellwerk_aiddisplay = new Color(255, 255, 17);
      this.mycolorbase.col_stellwerk_knopfseite = new Color(221, 221, 221);
      this.mycolorbase.col_stellwerk_bstgfläche = new Color(170, 170, 170);
      this.mycolorbase.col_stellwerk_masstab = new Color[13];
      this.mycolorbase.col_stellwerk_masstab[0] = this.mycolorbase.col_stellwerk_back;
      int jx = 9;

      for(int i = 1; i < Math.min(this.mycolorbase.col_stellwerk_masstab.length, 12); ++i) {
         float h = 0.1F * (float)jx;
         float b = 1.0F;
         float s = 0.5F + (float)(jx % 2) * 0.5F;
         this.mycolorbase.col_stellwerk_masstab[i] = Color.getHSBColor(h, s, b);
         if (jx % 10 == 4) {
            this.mycolorbase.col_stellwerk_masstab[i + 1] = this.mycolorbase.col_stellwerk_masstab[i].darker();
            ++i;
         }

         ++jx;
      }

      for(int i = 12; i < this.mycolorbase.col_stellwerk_masstab.length; ++i) {
         if (i != 12 && (i & 1) == 0) {
            this.mycolorbase.col_stellwerk_masstab[i] = this.mycolorbase.col_stellwerk_masstab[i - 12].darker();
         } else {
            this.mycolorbase.col_stellwerk_masstab[i] = this.mycolorbase.col_stellwerk_masstab[i - 12].brighter();
         }
      }

      this.mycolorbase.backupColors();
      if (this.type == gleisColor.COLORTYPE.SIMULATOR_NACHT) {
         this.setNachtView();
      }

      this.workingcolor = new colorStruct(this.mycolorbase);
   }

   public void setNachtView() {
      if (!(this.mycolorbase instanceof nachtColor)) {
         this.mycolorbase = new nachtColor(this.mycolorbase);
         this.restoreColorAdditonalOpts();
      }
   }

   public void setDayView() {
      if (this.mycolorbase instanceof nachtColor) {
         this.mycolorbase = ((nachtColor)this.mycolorbase).getOldColor();
         this.restoreColorAdditonalOpts();
      }
   }

   public boolean isNightView() {
      return this.mycolorbase instanceof nachtColor;
   }

   private void restoreColorAdditonalOpts() {
      if (this.workingcolor instanceof alternativeColor) {
         this.setAlterColor();
      } else {
         this.setNormalColor();
      }
   }

   protected void setColor(colorStruct color) {
      gleis.setColor(color);
      this.currentColors = color;
   }

   public colorStruct getColor() {
      return this.currentColors;
   }

   public void setAlterColor() {
      this.workingcolor = new alternativeColor(this.mycolorbase);
      this.setColor(this.workingcolor);
   }

   public void setNormalColor() {
      this.workingcolor = new colorStruct(this.mycolorbase);
      this.setColor(this.workingcolor);
   }

   public void setMasstabColor() {
      this.workingcolor = new massstabColor(this.mycolorbase);
      this.setColor(this.workingcolor);
   }

   public void dimmLight(int level) {
      this.setColor(new dimLightColor(this.workingcolor, level));
   }

   public void changeColor(String name, Color c) {
      this.mycolorbase.col_stellwerk_backmulti.put(name, c);
      this.workingcolor.col_stellwerk_backmulti.put(name, c);
   }

   public void restoreDefaultColor(String name) {
      this.mycolorbase.col_stellwerk_backmulti.put(name, this.mycolorbase.col_stellwerk_backmulti_backup.get(name));
      this.workingcolor.col_stellwerk_backmulti.put(name, this.workingcolor.col_stellwerk_backmulti_backup.get(name));
   }

   public TreeMap<String, Color> getBGcolors() {
      return this.workingcolor.col_stellwerk_backmulti;
   }

   public static enum COLORTYPE {
      SIMULATOR_TAG,
      SIMULATOR_NACHT,
      EDITOR;
   }
}
