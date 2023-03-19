package js.java.isolate.sim.gleis.colorSystem;

import java.awt.Color;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.TreeMap;

public class colorStruct {
   static final int CLIMIT1 = 12;
   static final int CLIMIT2 = 1;
   public static final int HLCNT = 10;
   public Color col_text = null;
   public Color col_text_s = null;
   public Color col_stellwerk_bsttrenner = null;
   public Color col_marker = null;
   public Color col_pfeil = null;
   public Color col_aktiv = null;
   public Color[] col_aktiv2 = new Color[10];
   public Color[] col_aktiv3 = new Color[10];
   public Color[] col_highlight = new Color[10];
   public Color col_stellwerk_back = null;
   public Color col_stellwerk_raster = null;
   public Color col_stellwerk_gleis = null;
   public Color col_stellwerk_frei = null;
   public Color col_stellwerk_displayoff = null;
   public Color col_stellwerk_reserviert = null;
   public Color col_stellwerk_reserviert_backup = null;
   public Color col_stellwerk_belegt = null;
   public Color col_stellwerk_belegt_backup = null;
   public Color col_stellwerk_schwarz = null;
   public Color col_stellwerk_rotein = null;
   public Color col_stellwerk_rotein_backup = null;
   public Color col_stellwerk_rotaus = null;
   public Color col_stellwerk_gruenein = null;
   public Color col_stellwerk_gruenein_backup = null;
   public Color col_stellwerk_gruenaus = null;
   public Color col_stellwerk_gelbein = null;
   public Color col_stellwerk_gelbein_backup = null;
   public Color col_stellwerk_gelbaus = null;
   public Color col_stellwerk_weiss = null;
   public Color col_stellwerk_rot = null;
   public Color col_stellwerk_rot_locked = null;
   public Color col_stellwerk_rot_beleuchtet = null;
   public Color col_stellwerk_rot_umleuchtung = null;
   public Color col_stellwerk_defekt = null;
   public Color col_stellwerk_defekt_backup = null;
   public Color col_stellwerk_grau = null;
   public Color col_stellwerk_grau_locked = null;
   public Color col_stellwerk_geländer = null;
   public Color col_stellwerk_zs1 = null;
   public Color col_stellwerk_zs1_backup = null;
   public Color col_stellwerk_nummer = null;
   public Color col_stellwerk_signalnummer = null;
   public Color col_stellwerk_signalnummerhgr = null;
   public TreeMap<String, Color> col_stellwerk_backmulti = null;
   public TreeMap<String, Color> col_stellwerk_backmulti_backup = null;
   public Color col_stellwerk_backms = null;
   public Color[] col_stellwerk_masstab = new Color[13];
   public Color col_stellwerk_zugdisplay = null;
   public Color col_stellwerk_zugdisplay_backup = null;
   public Color col_stellwerk_aiddisplay = null;
   public Color col_stellwerk_aiddisplay_backup = null;
   public Color col_stellwerk_knopfseite = null;
   public Color col_stellwerk_bstgfläche = null;

   void addColor(String name, int r, int g, int b) {
      this.col_stellwerk_backms = new Color(r, g, b);
      this.col_stellwerk_backmulti.put(name, this.col_stellwerk_backms);
   }

   void backupColors() {
      this.col_stellwerk_backmulti_backup = (TreeMap)this.col_stellwerk_backmulti.clone();
      this.col_stellwerk_belegt_backup = this.col_stellwerk_belegt;
      this.col_stellwerk_defekt_backup = this.col_stellwerk_defekt;
      this.col_stellwerk_gelbein_backup = this.col_stellwerk_gelbein;
      this.col_stellwerk_gruenein_backup = this.col_stellwerk_gruenein;
      this.col_stellwerk_reserviert_backup = this.col_stellwerk_reserviert;
      this.col_stellwerk_rotein_backup = this.col_stellwerk_rotein;
      this.col_stellwerk_zs1_backup = this.col_stellwerk_zs1;
      this.col_stellwerk_zugdisplay_backup = this.col_stellwerk_zugdisplay;
      this.col_stellwerk_aiddisplay_backup = this.col_stellwerk_aiddisplay;
   }

   void cloneTo(colorStruct other) {
      for(Class obj = this.getClass(); !obj.equals(Object.class); obj = obj.getSuperclass()) {
         Field[] fs = obj.getDeclaredFields();

         for(Field f : fs) {
            try {
               if (!f.getType().isArray()) {
                  Object o = f.get(this);
                  if (o instanceof TreeMap) {
                     o = ((TreeMap)o).clone();
                  }

                  f.set(other, o);
               } else {
                  Object o = f.get(this);
                  int length = Array.getLength(o);
                  Object newArray = Array.newInstance(Color.class, length);

                  for(int i = 0; i < length; ++i) {
                     Object element = Array.get(o, i);
                     Array.set(newArray, i, element);
                  }

                  f.set(other, newArray);
               }
            } catch (IllegalAccessException | IllegalArgumentException var13) {
            }
         }
      }

      other.backupColors();
   }

   colorStruct() {
      super();
   }

   colorStruct(colorStruct other) {
      super();
      other.cloneTo(this);
   }
}
