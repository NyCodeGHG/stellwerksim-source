package js.java.tools;

public enum JavaVersion {
   JAVA_0_9(1.5F, "0.9"),
   JAVA_1_1(1.1F, "1.1"),
   JAVA_1_2(1.2F, "1.2"),
   JAVA_1_3(1.3F, "1.3"),
   JAVA_1_4(1.4F, "1.4"),
   JAVA_1_5(1.5F, "1.5"),
   JAVA_1_6(1.6F, "1.6"),
   JAVA_1_7(1.7F, "1.7"),
   JAVA_1_8(1.8F, "1.8"),
   @Deprecated
   JAVA_1_9(9.0F, "9"),
   JAVA_9(9.0F, "9"),
   JAVA_10(10.0F, "10"),
   JAVA_11(11.0F, "11"),
   JAVA_12(12.0F, "12"),
   JAVA_13(13.0F, "13"),
   JAVA_14(14.0F, "14"),
   JAVA_15(15.0F, "15"),
   JAVA_16(16.0F, "16"),
   JAVA_17(17.0F, "17"),
   JAVA_RECENT(maxVersion(), Float.toString(maxVersion()));

   private final float value;
   private final String name;

   private JavaVersion(float value, String name) {
      this.value = value;
      this.name = name;
   }

   public boolean atLeast(JavaVersion requiredVersion) {
      return this.value >= requiredVersion.value;
   }

   public boolean atMost(JavaVersion requiredVersion) {
      return this.value <= requiredVersion.value;
   }

   static JavaVersion getJavaVersion(String nom) {
      return get(nom);
   }

   static JavaVersion get(String versionStr) {
      if (versionStr == null) {
         return null;
      } else {
         switch(versionStr) {
            case "0.9":
               return JAVA_0_9;
            case "1.1":
               return JAVA_1_1;
            case "1.2":
               return JAVA_1_2;
            case "1.3":
               return JAVA_1_3;
            case "1.4":
               return JAVA_1_4;
            case "1.5":
               return JAVA_1_5;
            case "1.6":
               return JAVA_1_6;
            case "1.7":
               return JAVA_1_7;
            case "1.8":
               return JAVA_1_8;
            case "9":
               return JAVA_9;
            case "10":
               return JAVA_10;
            case "11":
               return JAVA_11;
            case "12":
               return JAVA_12;
            case "13":
               return JAVA_13;
            case "14":
               return JAVA_14;
            case "15":
               return JAVA_15;
            case "16":
               return JAVA_16;
            case "17":
               return JAVA_17;
            default:
               float v = toFloatVersion(versionStr);
               if ((double)v - 1.0 < 1.0) {
                  int firstComma = Math.max(versionStr.indexOf(46), versionStr.indexOf(44));
                  int end = Math.max(versionStr.length(), versionStr.indexOf(44, firstComma));
                  if (Float.parseFloat(versionStr.substring(firstComma + 1, end)) > 0.9F) {
                     return JAVA_RECENT;
                  }
               } else if (v > 10.0F) {
                  return JAVA_RECENT;
               }

               return null;
         }
      }
   }

   public String toString() {
      return this.name;
   }

   private static float maxVersion() {
      float v = toFloatVersion(System.getProperty("java.specification.version", "99.0"));
      return v > 0.0F ? v : 99.0F;
   }

   private static float toFloatVersion(String value) {
      int defaultReturnValue = -1;
      if (!value.contains(".")) {
         return TextHelper.toFloat(value, -1.0F);
      } else {
         String[] toParse = value.split("\\.");
         return toParse.length >= 2 ? TextHelper.toFloat(toParse[0] + '.' + toParse[1], -1.0F) : -1.0F;
      }
   }
}
