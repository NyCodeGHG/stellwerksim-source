package net.miginfocom.layout;

import java.util.HashMap;

public final class PlatformDefaults {
   private static int DEF_H_UNIT = 1;
   private static int DEF_V_UNIT = 2;
   private static InCellGapProvider GAP_PROVIDER = null;
   private static volatile int MOD_COUNT = 0;
   private static final UnitValue LPX4 = new UnitValue(4.0F, 1, null);
   private static final UnitValue LPX6 = new UnitValue(6.0F, 1, null);
   private static final UnitValue LPX7 = new UnitValue(7.0F, 1, null);
   private static final UnitValue LPX9 = new UnitValue(9.0F, 1, null);
   private static final UnitValue LPX10 = new UnitValue(10.0F, 1, null);
   private static final UnitValue LPX11 = new UnitValue(11.0F, 1, null);
   private static final UnitValue LPX12 = new UnitValue(12.0F, 1, null);
   private static final UnitValue LPX14 = new UnitValue(14.0F, 1, null);
   private static final UnitValue LPX16 = new UnitValue(16.0F, 1, null);
   private static final UnitValue LPX18 = new UnitValue(18.0F, 1, null);
   private static final UnitValue LPX20 = new UnitValue(20.0F, 1, null);
   private static final UnitValue LPY4 = new UnitValue(4.0F, 2, null);
   private static final UnitValue LPY6 = new UnitValue(6.0F, 2, null);
   private static final UnitValue LPY7 = new UnitValue(7.0F, 2, null);
   private static final UnitValue LPY9 = new UnitValue(9.0F, 2, null);
   private static final UnitValue LPY10 = new UnitValue(10.0F, 2, null);
   private static final UnitValue LPY11 = new UnitValue(11.0F, 2, null);
   private static final UnitValue LPY12 = new UnitValue(12.0F, 2, null);
   private static final UnitValue LPY14 = new UnitValue(14.0F, 2, null);
   private static final UnitValue LPY16 = new UnitValue(16.0F, 2, null);
   private static final UnitValue LPY18 = new UnitValue(18.0F, 2, null);
   private static final UnitValue LPY20 = new UnitValue(20.0F, 2, null);
   public static final int WINDOWS_XP = 0;
   public static final int MAC_OSX = 1;
   public static final int GNOME = 2;
   private static int CUR_PLAF = 0;
   private static final UnitValue[] PANEL_INS = new UnitValue[4];
   private static final UnitValue[] DIALOG_INS = new UnitValue[4];
   private static String BUTTON_FORMAT = null;
   private static final HashMap<String, UnitValue> HOR_DEFS = new HashMap(32);
   private static final HashMap<String, UnitValue> VER_DEFS = new HashMap(32);
   private static BoundSize DEF_VGAP = null;
   private static BoundSize DEF_HGAP = null;
   static BoundSize RELATED_X = null;
   static BoundSize RELATED_Y = null;
   static BoundSize UNRELATED_X = null;
   static BoundSize UNRELATED_Y = null;
   private static UnitValue BUTT_WIDTH = null;
   private static Float horScale = null;
   private static Float verScale = null;
   public static final int BASE_FONT_SIZE = 100;
   public static final int BASE_SCALE_FACTOR = 101;
   public static final int BASE_REAL_PIXEL = 102;
   private static int LP_BASE = 101;
   private static Integer BASE_DPI_FORCED = null;
   private static int BASE_DPI = 96;
   private static boolean dra = true;

   public static int getCurrentPlatform() {
      String os = System.getProperty("os.name");
      if (os.startsWith("Mac OS")) {
         return 1;
      } else {
         return os.startsWith("Linux") ? 2 : 0;
      }
   }

   private PlatformDefaults() {
   }

   public static void setPlatform(int plaf) {
      switch (plaf) {
         case 0:
            setRelatedGap(LPX4, LPY4);
            setUnrelatedGap(LPX7, LPY9);
            setParagraphGap(LPX14, LPY14);
            setIndentGap(LPX9, LPY9);
            setGridCellGap(LPX4, LPY4);
            setMinimumButtonWidth(new UnitValue(75.0F, 1, null));
            setButtonOrder("L_E+U+YNBXOCAH_R");
            setDialogInsets(LPY11, LPX11, LPY11, LPX11);
            setPanelInsets(LPY7, LPX7, LPY7, LPX7);
            break;
         case 1:
            setRelatedGap(LPX4, LPY4);
            setUnrelatedGap(LPX7, LPY9);
            setParagraphGap(LPX14, LPY14);
            setIndentGap(LPX10, LPY10);
            setGridCellGap(LPX4, LPY4);
            setMinimumButtonWidth(new UnitValue(68.0F, 1, null));
            setButtonOrder("L_HE+U+NYBXCOA_R");
            setDialogInsets(LPY14, LPX20, LPY20, LPX20);
            setPanelInsets(LPY16, LPX16, LPY16, LPX16);
            break;
         case 2:
            setRelatedGap(LPX6, LPY6);
            setUnrelatedGap(LPX12, LPY12);
            setParagraphGap(LPX18, LPY18);
            setIndentGap(LPX12, LPY12);
            setGridCellGap(LPX6, LPY6);
            setMinimumButtonWidth(new UnitValue(85.0F, 1, null));
            setButtonOrder("L_HE+UNYACBXIO_R");
            setDialogInsets(LPY12, LPX12, LPY12, LPX12);
            setPanelInsets(LPY6, LPX6, LPY6, LPX6);
            break;
         default:
            throw new IllegalArgumentException("Unknown platform: " + plaf);
      }

      CUR_PLAF = plaf;
      BASE_DPI = BASE_DPI_FORCED != null ? BASE_DPI_FORCED : getPlatformDPI(plaf);
   }

   private static int getPlatformDPI(int plaf) {
      switch (plaf) {
         case 0:
         case 2:
            return 96;
         case 1:
            try {
               return System.getProperty("java.version").compareTo("1.6") < 0 ? 72 : 96;
            } catch (Throwable var2) {
               return 72;
            }
         default:
            throw new IllegalArgumentException("Unknown platform: " + plaf);
      }
   }

   public static int getPlatform() {
      return CUR_PLAF;
   }

   public static int getDefaultDPI() {
      return BASE_DPI;
   }

   public static void setDefaultDPI(Integer dpi) {
      BASE_DPI = dpi != null ? dpi : getPlatformDPI(CUR_PLAF);
      BASE_DPI_FORCED = dpi;
   }

   public static Float getHorizontalScaleFactor() {
      return horScale;
   }

   public static void setHorizontalScaleFactor(Float f) {
      if (!LayoutUtil.equals(horScale, f)) {
         horScale = f;
         MOD_COUNT++;
      }
   }

   public static Float getVerticalScaleFactor() {
      return verScale;
   }

   public static void setVerticalScaleFactor(Float f) {
      if (!LayoutUtil.equals(verScale, f)) {
         verScale = f;
         MOD_COUNT++;
      }
   }

   public static int getLogicalPixelBase() {
      return LP_BASE;
   }

   public static void setLogicalPixelBase(int base) {
      if (LP_BASE != base) {
         if (base < 100 || base > 101) {
            throw new IllegalArgumentException("Unrecognized base: " + base);
         }

         LP_BASE = base;
         MOD_COUNT++;
      }
   }

   public static void setRelatedGap(UnitValue x, UnitValue y) {
      setUnitValue(new String[]{"r", "rel", "related"}, x, y);
      RELATED_X = new BoundSize(x, x, null, "rel:rel");
      RELATED_Y = new BoundSize(y, y, null, "rel:rel");
   }

   public static void setUnrelatedGap(UnitValue x, UnitValue y) {
      setUnitValue(new String[]{"u", "unrel", "unrelated"}, x, y);
      UNRELATED_X = new BoundSize(x, x, null, "unrel:unrel");
      UNRELATED_Y = new BoundSize(y, y, null, "unrel:unrel");
   }

   public static void setParagraphGap(UnitValue x, UnitValue y) {
      setUnitValue(new String[]{"p", "para", "paragraph"}, x, y);
   }

   public static void setIndentGap(UnitValue x, UnitValue y) {
      setUnitValue(new String[]{"i", "ind", "indent"}, x, y);
   }

   public static void setGridCellGap(UnitValue x, UnitValue y) {
      if (x != null) {
         DEF_HGAP = new BoundSize(x, x, null, null);
      }

      if (y != null) {
         DEF_VGAP = new BoundSize(y, y, null, null);
      }

      MOD_COUNT++;
   }

   public static void setMinimumButtonWidth(UnitValue width) {
      BUTT_WIDTH = width;
      MOD_COUNT++;
   }

   public static UnitValue getMinimumButtonWidth() {
      return BUTT_WIDTH;
   }

   public static UnitValue getUnitValueX(String unit) {
      return (UnitValue)HOR_DEFS.get(unit);
   }

   public static UnitValue getUnitValueY(String unit) {
      return (UnitValue)VER_DEFS.get(unit);
   }

   public static final void setUnitValue(String[] unitStrings, UnitValue x, UnitValue y) {
      for (String unitString : unitStrings) {
         String s = unitString.toLowerCase().trim();
         if (x != null) {
            HOR_DEFS.put(s, x);
         }

         if (y != null) {
            VER_DEFS.put(s, y);
         }
      }

      MOD_COUNT++;
   }

   static int convertToPixels(float value, String unit, boolean isHor, float ref, ContainerWrapper parent, ComponentWrapper comp) {
      UnitValue uv = (UnitValue)(isHor ? HOR_DEFS : VER_DEFS).get(unit);
      return uv != null ? Math.round(value * (float)uv.getPixels(ref, parent, comp)) : -87654312;
   }

   public static String getButtonOrder() {
      return BUTTON_FORMAT;
   }

   public static void setButtonOrder(String order) {
      BUTTON_FORMAT = order;
      MOD_COUNT++;
   }

   static String getTagForChar(char c) {
      switch (c) {
         case 'a':
            return "apply";
         case 'b':
            return "back";
         case 'c':
            return "cancel";
         case 'd':
         case 'f':
         case 'g':
         case 'j':
         case 'k':
         case 'm':
         case 'p':
         case 'q':
         case 's':
         case 't':
         case 'v':
         case 'w':
         default:
            return null;
         case 'e':
            return "help2";
         case 'h':
            return "help";
         case 'i':
            return "finish";
         case 'l':
            return "left";
         case 'n':
            return "no";
         case 'o':
            return "ok";
         case 'r':
            return "right";
         case 'u':
            return "other";
         case 'x':
            return "next";
         case 'y':
            return "yes";
      }
   }

   public static BoundSize getGridGapX() {
      return DEF_HGAP;
   }

   public static BoundSize getGridGapY() {
      return DEF_VGAP;
   }

   public static UnitValue getDialogInsets(int side) {
      return DIALOG_INS[side];
   }

   public static void setDialogInsets(UnitValue top, UnitValue left, UnitValue bottom, UnitValue right) {
      if (top != null) {
         DIALOG_INS[0] = top;
      }

      if (left != null) {
         DIALOG_INS[1] = left;
      }

      if (bottom != null) {
         DIALOG_INS[2] = bottom;
      }

      if (right != null) {
         DIALOG_INS[3] = right;
      }

      MOD_COUNT++;
   }

   public static UnitValue getPanelInsets(int side) {
      return PANEL_INS[side];
   }

   public static void setPanelInsets(UnitValue top, UnitValue left, UnitValue bottom, UnitValue right) {
      if (top != null) {
         PANEL_INS[0] = top;
      }

      if (left != null) {
         PANEL_INS[1] = left;
      }

      if (bottom != null) {
         PANEL_INS[2] = bottom;
      }

      if (right != null) {
         PANEL_INS[3] = right;
      }

      MOD_COUNT++;
   }

   public static float getLabelAlignPercentage() {
      return CUR_PLAF == 1 ? 1.0F : 0.0F;
   }

   static BoundSize getDefaultComponentGap(ComponentWrapper comp, ComponentWrapper adjacentComp, int adjacentSide, String tag, boolean isLTR) {
      if (GAP_PROVIDER != null) {
         return GAP_PROVIDER.getDefaultGap(comp, adjacentComp, adjacentSide, tag, isLTR);
      } else if (adjacentComp == null) {
         return null;
      } else {
         return adjacentSide != 2 && adjacentSide != 4 ? RELATED_Y : RELATED_X;
      }
   }

   public static InCellGapProvider getGapProvider() {
      return GAP_PROVIDER;
   }

   public static void setGapProvider(InCellGapProvider provider) {
      GAP_PROVIDER = provider;
   }

   public static int getModCount() {
      return MOD_COUNT;
   }

   public void invalidate() {
      MOD_COUNT++;
   }

   public static int getDefaultHorizontalUnit() {
      return DEF_H_UNIT;
   }

   public static void setDefaultHorizontalUnit(int unit) {
      if (unit >= 0 && unit <= 27) {
         if (DEF_H_UNIT != unit) {
            DEF_H_UNIT = unit;
            MOD_COUNT++;
         }
      } else {
         throw new IllegalArgumentException("Illegal Unit: " + unit);
      }
   }

   public static int getDefaultVerticalUnit() {
      return DEF_V_UNIT;
   }

   public static void setDefaultVerticalUnit(int unit) {
      if (unit >= 0 && unit <= 27) {
         if (DEF_V_UNIT != unit) {
            DEF_V_UNIT = unit;
            MOD_COUNT++;
         }
      } else {
         throw new IllegalArgumentException("Illegal Unit: " + unit);
      }
   }

   public static boolean getDefaultRowAlignmentBaseline() {
      return dra;
   }

   public static void setDefaultRowAlignmentBaseline(boolean b) {
      dra = b;
   }

   static {
      setPlatform(getCurrentPlatform());
      MOD_COUNT = 0;
   }
}
