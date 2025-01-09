package js.java.tools;

import java.awt.Color;

public class ColorText implements Comparable {
   private String text = "";
   private String spezialtext = "";
   private Color color = null;
   private Color fgcolor = null;
   private int colortype = 0;
   private boolean special = false;
   public static final int CT_NONE = 0;
   public static final int CT_RED = 1;
   public static final int CT_LIGHTRED = 2;
   public static final int CT_GREEN = 3;
   public static final int CT_YELLOW = 4;
   public static final int CT_GREY = 5;
   public static final int CT_MAGENTA = 6;
   public static final int CT_ORANGE = 7;
   public static final int CT_CYAN = 8;
   public static final int CT_LIGHTYELLOW = 9;
   public static final int CT_LIGHTGREY = 10;
   public static final int CT_LIGHTGREEN1 = 11;
   public static final int CT_LIGHTGREEN2 = 12;
   public static final int CT_LIGHTGREEN3 = 13;

   protected ColorText() {
   }

   public ColorText(String t) {
      this.text = t;
      this.spezialtext = t;
   }

   public ColorText(String t, String st) {
      this.text = t;
      this.spezialtext = st;
   }

   public ColorText(String t, int coltype) {
      this.text = t;
      this.spezialtext = t;
      this.setBGColor(coltype);
   }

   public ColorText(String t, String st, int coltype) {
      this.text = t;
      this.spezialtext = st;
      this.setBGColor(coltype);
   }

   public ColorText(String t, Color col) {
      this.text = t;
      this.spezialtext = t;
      this.color = col;
   }

   public String getText() {
      return this.spezialtext;
   }

   public void setText(String t) {
      this.spezialtext = t;
      this.text = t;
   }

   public void setText(String t, String st) {
      this.spezialtext = st;
      this.text = t;
   }

   public void setSpecial(boolean f) {
      this.special = f;
   }

   public boolean isSpecial() {
      return this.special;
   }

   public Color getBGColor() {
      return this.color;
   }

   public Color getFGColor() {
      return this.fgcolor;
   }

   public int getBGColorType() {
      return this.colortype;
   }

   public static Color[] getColorsOf(int coltype) {
      Color color = null;
      Color fgcolor = null;
      switch (coltype) {
         case 0:
            color = null;
            fgcolor = null;
            break;
         case 1:
            color = Color.red;
            fgcolor = Color.WHITE;
            break;
         case 2:
            color = new Color(134, 32, 32);
            fgcolor = Color.WHITE;
            break;
         case 3:
            color = Color.green;
            fgcolor = Color.BLACK;
            break;
         case 4:
            color = Color.yellow;
            fgcolor = Color.BLACK;
            break;
         case 5:
            color = Color.LIGHT_GRAY;
            fgcolor = Color.BLACK;
            break;
         case 6:
            color = Color.magenta;
            fgcolor = Color.WHITE;
            break;
         case 7:
            color = Color.orange;
            fgcolor = Color.BLACK;
            break;
         case 8:
            color = Color.cyan;
            fgcolor = Color.BLACK;
            break;
         case 9:
            color = new Color(226, 226, 178);
            fgcolor = Color.BLACK;
            break;
         case 10:
            color = Color.LIGHT_GRAY.brighter();
            fgcolor = Color.BLACK;
            break;
         case 11:
            color = new Color(238, 255, 238);
            fgcolor = Color.BLACK;
            break;
         case 12:
            color = new Color(221, 255, 221);
            fgcolor = Color.BLACK;
            break;
         case 13:
            color = new Color(204, 255, 204);
            fgcolor = Color.BLACK;
      }

      return new Color[]{color, fgcolor};
   }

   public static Color getFGColorOf(int coltype) {
      return getColorsOf(coltype)[0];
   }

   public static Color getBGColorOf(int coltype) {
      return getColorsOf(coltype)[1];
   }

   public boolean setBGColor(int coltype) {
      boolean changed = this.colortype != coltype;
      this.colortype = coltype;
      Color[] r = getColorsOf(coltype);
      this.color = r[0];
      this.fgcolor = r[1];
      return changed;
   }

   public int compareTo(Object o) {
      return this.text.compareTo(o.toString());
   }

   public boolean equals(Object o) {
      return o instanceof ColorText ? this.text.equals(((ColorText)o).text) : this.text.equals(o);
   }

   public int hashCode() {
      int hash = 5;
      return 19 * hash + (this.text != null ? this.text.hashCode() : 0);
   }

   public String toString() {
      return this.text;
   }
}
