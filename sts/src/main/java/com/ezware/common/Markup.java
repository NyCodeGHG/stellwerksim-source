package com.ezware.common;

import java.awt.Color;
import java.awt.Font;

public final class Markup {
   private static final String HTML_START = "<html>";
   private static final String HTML_END = "</html>";
   private static final String HTML_BREAK = "<br>";

   private Markup() {
      super();
   }

   public static final String toHex(Color color) {
      color = color == null ? Color.BLACK : color;
      String rgb = Integer.toHexString(color.getRGB());
      return rgb.substring(2, rgb.length());
   }

   public static final String toHTML(String s, boolean finalize) {
      s = s == null ? "" : s.replaceAll("\n", "<br>");
      String tmp = s.trim().toLowerCase();
      StringBuilder sb = new StringBuilder(s);
      if (finalize) {
         if (!tmp.startsWith("<html>")) {
            sb.insert(0, "<html>");
         }

         if (!tmp.endsWith("</html>")) {
            sb.append("</html>");
         }
      }

      return sb.toString();
   }

   public static final String toHTML(String s) {
      return toHTML(s, true);
   }

   public static final String toCSS(Font font) {
      return String.format("font-family: \"%s\"; %s; %s;", font.getFamily(), toSizeCSS(font), toStyleCSS(font));
   }

   public static final String toSizeCSS(Font font) {
      return String.format("font-size: %fpx", (double)font.getSize() * 0.75);
   }

   public static final String toStyleCSS(Font font) {
      switch(font.getStyle()) {
         case 1:
            return "font-weight: bold";
         case 2:
            return "font-style : italic";
         default:
            return "font-weight: normal";
      }
   }

   public static final String toCSS(Color color) {
      return String.format("color: #%s;", toHex(color));
   }
}
