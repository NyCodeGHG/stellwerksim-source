package js.java.tools;

import java.util.HashMap;

public class HTMLEntities {
   private static final Object[][] html_entities_table = new Object[][]{
      {"&Aacute;", new Integer(193)},
      {"&aacute;", new Integer(225)},
      {"&Acirc;", new Integer(194)},
      {"&acirc;", new Integer(226)},
      {"&acute;", new Integer(180)},
      {"&AElig;", new Integer(198)},
      {"&aelig;", new Integer(230)},
      {"&Agrave;", new Integer(192)},
      {"&agrave;", new Integer(224)},
      {"&alefsym;", new Integer(8501)},
      {"&Alpha;", new Integer(913)},
      {"&alpha;", new Integer(945)},
      {"&amp;", new Integer(38)},
      {"&and;", new Integer(8743)},
      {"&ang;", new Integer(8736)},
      {"&Aring;", new Integer(197)},
      {new String("&aring;"), new Integer(229)},
      {new String("&asymp;"), new Integer(8776)},
      {new String("&Atilde;"), new Integer(195)},
      {new String("&atilde;"), new Integer(227)},
      {new String("&Auml;"), new Integer(196)},
      {new String("&auml;"), new Integer(228)},
      {new String("&bdquo;"), new Integer(8222)},
      {new String("&Beta;"), new Integer(914)},
      {new String("&beta;"), new Integer(946)},
      {new String("&brvbar;"), new Integer(166)},
      {new String("&bull;"), new Integer(8226)},
      {new String("&cap;"), new Integer(8745)},
      {new String("&Ccedil;"), new Integer(199)},
      {new String("&ccedil;"), new Integer(231)},
      {new String("&cedil;"), new Integer(184)},
      {new String("&cent;"), new Integer(162)},
      {new String("&Chi;"), new Integer(935)},
      {new String("&chi;"), new Integer(967)},
      {new String("&circ;"), new Integer(710)},
      {new String("&clubs;"), new Integer(9827)},
      {new String("&cong;"), new Integer(8773)},
      {new String("&copy;"), new Integer(169)},
      {new String("&crarr;"), new Integer(8629)},
      {new String("&cup;"), new Integer(8746)},
      {new String("&curren;"), new Integer(164)},
      {new String("&dagger;"), new Integer(8224)},
      {new String("&Dagger;"), new Integer(8225)},
      {new String("&darr;"), new Integer(8595)},
      {new String("&dArr;"), new Integer(8659)},
      {new String("&deg;"), new Integer(176)},
      {new String("&Delta;"), new Integer(916)},
      {new String("&delta;"), new Integer(948)},
      {new String("&diams;"), new Integer(9830)},
      {new String("&divide;"), new Integer(247)},
      {new String("&Eacute;"), new Integer(201)},
      {new String("&eacute;"), new Integer(233)},
      {new String("&Ecirc;"), new Integer(202)},
      {new String("&ecirc;"), new Integer(234)},
      {new String("&Egrave;"), new Integer(200)},
      {new String("&egrave;"), new Integer(232)},
      {new String("&empty;"), new Integer(8709)},
      {new String("&emsp;"), new Integer(8195)},
      {new String("&ensp;"), new Integer(8194)},
      {new String("&Epsilon;"), new Integer(917)},
      {new String("&epsilon;"), new Integer(949)},
      {new String("&equiv;"), new Integer(8801)},
      {new String("&Eta;"), new Integer(919)},
      {new String("&eta;"), new Integer(951)},
      {new String("&ETH;"), new Integer(208)},
      {new String("&eth;"), new Integer(240)},
      {new String("&Euml;"), new Integer(203)},
      {new String("&euml;"), new Integer(235)},
      {new String("&euro;"), new Integer(8364)},
      {new String("&exist;"), new Integer(8707)},
      {new String("&fnof;"), new Integer(402)},
      {new String("&forall;"), new Integer(8704)},
      {new String("&frac12;"), new Integer(189)},
      {new String("&frac14;"), new Integer(188)},
      {new String("&frac34;"), new Integer(190)},
      {new String("&frasl;"), new Integer(8260)},
      {new String("&Gamma;"), new Integer(915)},
      {new String("&gamma;"), new Integer(947)},
      {new String("&ge;"), new Integer(8805)},
      {new String("&harr;"), new Integer(8596)},
      {new String("&hArr;"), new Integer(8660)},
      {new String("&hearts;"), new Integer(9829)},
      {new String("&hellip;"), new Integer(8230)},
      {new String("&Iacute;"), new Integer(205)},
      {new String("&iacute;"), new Integer(237)},
      {new String("&Icirc;"), new Integer(206)},
      {new String("&icirc;"), new Integer(238)},
      {new String("&iexcl;"), new Integer(161)},
      {new String("&Igrave;"), new Integer(204)},
      {new String("&igrave;"), new Integer(236)},
      {new String("&image;"), new Integer(8465)},
      {new String("&infin;"), new Integer(8734)},
      {new String("&int;"), new Integer(8747)},
      {new String("&Iota;"), new Integer(921)},
      {new String("&iota;"), new Integer(953)},
      {new String("&iquest;"), new Integer(191)},
      {new String("&isin;"), new Integer(8712)},
      {new String("&Iuml;"), new Integer(207)},
      {new String("&iuml;"), new Integer(239)},
      {new String("&Kappa;"), new Integer(922)},
      {new String("&kappa;"), new Integer(954)},
      {new String("&Lambda;"), new Integer(923)},
      {new String("&lambda;"), new Integer(955)},
      {new String("&lang;"), new Integer(9001)},
      {new String("&laquo;"), new Integer(171)},
      {new String("&larr;"), new Integer(8592)},
      {new String("&lArr;"), new Integer(8656)},
      {new String("&lceil;"), new Integer(8968)},
      {new String("&ldquo;"), new Integer(8220)},
      {new String("&le;"), new Integer(8804)},
      {new String("&lfloor;"), new Integer(8970)},
      {new String("&lowast;"), new Integer(8727)},
      {new String("&loz;"), new Integer(9674)},
      {new String("&lrm;"), new Integer(8206)},
      {new String("&lsaquo;"), new Integer(8249)},
      {new String("&lsquo;"), new Integer(8216)},
      {new String("&macr;"), new Integer(175)},
      {new String("&mdash;"), new Integer(8212)},
      {new String("&micro;"), new Integer(181)},
      {new String("&middot;"), new Integer(183)},
      {new String("&minus;"), new Integer(8722)},
      {new String("&Mu;"), new Integer(924)},
      {new String("&mu;"), new Integer(956)},
      {new String("&nabla;"), new Integer(8711)},
      {new String("&nbsp;"), new Integer(160)},
      {new String("&ndash;"), new Integer(8211)},
      {new String("&ne;"), new Integer(8800)},
      {new String("&ni;"), new Integer(8715)},
      {new String("&not;"), new Integer(172)},
      {new String("&notin;"), new Integer(8713)},
      {new String("&nsub;"), new Integer(8836)},
      {new String("&Ntilde;"), new Integer(209)},
      {new String("&ntilde;"), new Integer(241)},
      {new String("&Nu;"), new Integer(925)},
      {new String("&nu;"), new Integer(957)},
      {new String("&Oacute;"), new Integer(211)},
      {new String("&oacute;"), new Integer(243)},
      {new String("&Ocirc;"), new Integer(212)},
      {new String("&ocirc;"), new Integer(244)},
      {new String("&OElig;"), new Integer(338)},
      {new String("&oelig;"), new Integer(339)},
      {new String("&Ograve;"), new Integer(210)},
      {new String("&ograve;"), new Integer(242)},
      {new String("&oline;"), new Integer(8254)},
      {new String("&Omega;"), new Integer(937)},
      {new String("&omega;"), new Integer(969)},
      {new String("&Omicron;"), new Integer(927)},
      {new String("&omicron;"), new Integer(959)},
      {new String("&oplus;"), new Integer(8853)},
      {new String("&or;"), new Integer(8744)},
      {new String("&ordf;"), new Integer(170)},
      {new String("&ordm;"), new Integer(186)},
      {new String("&Oslash;"), new Integer(216)},
      {new String("&oslash;"), new Integer(248)},
      {new String("&Otilde;"), new Integer(213)},
      {new String("&otilde;"), new Integer(245)},
      {new String("&otimes;"), new Integer(8855)},
      {new String("&Ouml;"), new Integer(214)},
      {new String("&ouml;"), new Integer(246)},
      {new String("&para;"), new Integer(182)},
      {new String("&part;"), new Integer(8706)},
      {new String("&permil;"), new Integer(8240)},
      {new String("&perp;"), new Integer(8869)},
      {new String("&Phi;"), new Integer(934)},
      {new String("&phi;"), new Integer(966)},
      {new String("&Pi;"), new Integer(928)},
      {new String("&pi;"), new Integer(960)},
      {new String("&piv;"), new Integer(982)},
      {new String("&plusmn;"), new Integer(177)},
      {new String("&pound;"), new Integer(163)},
      {new String("&prime;"), new Integer(8242)},
      {new String("&Prime;"), new Integer(8243)},
      {new String("&prod;"), new Integer(8719)},
      {new String("&prop;"), new Integer(8733)},
      {new String("&Psi;"), new Integer(936)},
      {new String("&psi;"), new Integer(968)},
      {new String("&radic;"), new Integer(8730)},
      {new String("&rang;"), new Integer(9002)},
      {new String("&raquo;"), new Integer(187)},
      {new String("&rarr;"), new Integer(8594)},
      {new String("&rArr;"), new Integer(8658)},
      {new String("&rceil;"), new Integer(8969)},
      {new String("&rdquo;"), new Integer(8221)},
      {new String("&real;"), new Integer(8476)},
      {new String("&reg;"), new Integer(174)},
      {new String("&rfloor;"), new Integer(8971)},
      {new String("&Rho;"), new Integer(929)},
      {new String("&rho;"), new Integer(961)},
      {new String("&rlm;"), new Integer(8207)},
      {new String("&rsaquo;"), new Integer(8250)},
      {new String("&rsquo;"), new Integer(8217)},
      {new String("&sbquo;"), new Integer(8218)},
      {new String("&Scaron;"), new Integer(352)},
      {new String("&scaron;"), new Integer(353)},
      {new String("&sdot;"), new Integer(8901)},
      {new String("&sect;"), new Integer(167)},
      {new String("&shy;"), new Integer(173)},
      {new String("&Sigma;"), new Integer(931)},
      {new String("&sigma;"), new Integer(963)},
      {new String("&sigmaf;"), new Integer(962)},
      {new String("&sim;"), new Integer(8764)},
      {new String("&spades;"), new Integer(9824)},
      {new String("&sub;"), new Integer(8834)},
      {new String("&sube;"), new Integer(8838)},
      {new String("&sum;"), new Integer(8721)},
      {new String("&sup1;"), new Integer(185)},
      {new String("&sup2;"), new Integer(178)},
      {new String("&sup3;"), new Integer(179)},
      {new String("&sup;"), new Integer(8835)},
      {new String("&supe;"), new Integer(8839)},
      {new String("&szlig;"), new Integer(223)},
      {new String("&Tau;"), new Integer(932)},
      {new String("&tau;"), new Integer(964)},
      {new String("&there4;"), new Integer(8756)},
      {new String("&Theta;"), new Integer(920)},
      {new String("&theta;"), new Integer(952)},
      {new String("&thetasym;"), new Integer(977)},
      {new String("&thinsp;"), new Integer(8201)},
      {new String("&THORN;"), new Integer(222)},
      {new String("&thorn;"), new Integer(254)},
      {new String("&tilde;"), new Integer(732)},
      {new String("&times;"), new Integer(215)},
      {new String("&trade;"), new Integer(8482)},
      {new String("&Uacute;"), new Integer(218)},
      {new String("&uacute;"), new Integer(250)},
      {new String("&uarr;"), new Integer(8593)},
      {new String("&uArr;"), new Integer(8657)},
      {new String("&Ucirc;"), new Integer(219)},
      {new String("&ucirc;"), new Integer(251)},
      {new String("&Ugrave;"), new Integer(217)},
      {new String("&ugrave;"), new Integer(249)},
      {new String("&uml;"), new Integer(168)},
      {new String("&upsih;"), new Integer(978)},
      {new String("&Upsilon;"), new Integer(933)},
      {"&upsilon;", new Integer(965)},
      {"&Uuml;", new Integer(220)},
      {"&uuml;", new Integer(252)},
      {"&weierp;", new Integer(8472)},
      {"&Xi;", new Integer(926)},
      {"&xi;", new Integer(958)},
      {"&Yacute;", new Integer(221)},
      {"&yacute;", new Integer(253)},
      {"&yen;", new Integer(165)},
      {"&yuml;", new Integer(255)},
      {"&Yuml;", new Integer(376)},
      {"&Zeta;", new Integer(918)},
      {"&zeta;", new Integer(950)},
      {"&zwj;", new Integer(8205)},
      {"&zwnj;", new Integer(8204)}
   };
   private static final HashMap htmlentities_map = new HashMap();
   private static final HashMap unhtmlentities_map = new HashMap();

   public HTMLEntities() {
      initializeEntitiesTables();
   }

   private static void initializeEntitiesTables() {
      for (int i = 0; i < html_entities_table.length; i++) {
         htmlentities_map.put(html_entities_table[i][1], html_entities_table[i][0]);
         unhtmlentities_map.put(html_entities_table[i][0], html_entities_table[i][1]);
      }
   }

   public static Object[][] getEntitiesTable() {
      return html_entities_table;
   }

   public static String htmlentities(String str) {
      if (str == null) {
         return "";
      } else {
         if (htmlentities_map.isEmpty()) {
            initializeEntitiesTables();
         }

         StringBuilder buf = new StringBuilder();

         for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            String entity = (String)htmlentities_map.get(new Integer(ch));
            if (entity == null) {
               if (ch > 128) {
                  buf.append("&#" + ch + ";");
               } else {
                  buf.append(ch);
               }
            } else {
               buf.append(entity);
            }
         }

         return buf.toString();
      }
   }

   public static String unhtmlentities(String str) {
      if (htmlentities_map.isEmpty()) {
         initializeEntitiesTables();
      }

      StringBuilder buf = new StringBuilder();

      for (int i = 0; i < str.length(); i++) {
         char ch = str.charAt(i);
         if (ch == '&') {
            int semi = str.indexOf(59, i + 1);
            if (semi != -1 && semi - i <= 7) {
               String entity = str.substring(i, semi + 1);
               if (entity.charAt(1) == ' ') {
                  buf.append(ch);
               } else {
                  Integer iso;
                  if (entity.charAt(1) == '#') {
                     if (entity.charAt(2) == 'x') {
                        iso = new Integer(Integer.parseInt(entity.substring(3, entity.length() - 1), 16));
                     } else {
                        iso = new Integer(entity.substring(2, entity.length() - 1));
                     }
                  } else {
                     iso = (Integer)unhtmlentities_map.get(entity);
                  }

                  if (iso == null) {
                     buf.append(entity);
                  } else {
                     buf.append((char)iso.intValue());
                  }

                  i = semi;
               }
            } else {
               buf.append(ch);
            }
         } else {
            buf.append(ch);
         }
      }

      return buf.toString();
   }

   public static String escape(String str) {
      return str == null ? "" : htmlAngleBrackets(htmlQuotes(htmlAmpersand(str)));
   }

   public static String unescape(String str) {
      return str == null ? "" : unhtmlAmpersand(unhtmlQuotes(unhtmlAngleBrackets(str)));
   }

   public static String htmlSingleQuotes(String str) {
      str = str.replaceAll("[']", "&rsquo;");
      str = str.replaceAll("&#039;", "&rsquo;");
      str = str.replaceAll("&#145;", "&rsquo;");
      return str.replaceAll("&#146;", "&rsquo;");
   }

   public static String unhtmlSingleQuotes(String str) {
      return str.replaceAll("&rsquo;", "'");
   }

   public static String htmlDoubleQuotes(String str) {
      str = str.replaceAll("[\"]", "&quot;");
      str = str.replaceAll("&#147;", "&quot;");
      return str.replaceAll("&#148;", "&quot;");
   }

   public static String unhtmlDoubleQuotes(String str) {
      return str.replaceAll("&quot;", "\"");
   }

   public static String htmlQuotes(String str) {
      str = htmlDoubleQuotes(str);
      return htmlSingleQuotes(str);
   }

   public static String unhtmlQuotes(String str) {
      str = unhtmlDoubleQuotes(str);
      return unhtmlSingleQuotes(str);
   }

   public static String htmlAngleBrackets(String str) {
      str = str.replaceAll("<", "&lt;");
      return str.replaceAll(">", "&gt;");
   }

   public static String unhtmlAngleBrackets(String str) {
      str = str.replaceAll("&lt;", "<");
      return str.replaceAll("&gt;", ">");
   }

   public static String htmlAmpersand(String str) {
      return str.replaceAll("&", "&amp;");
   }

   public static String unhtmlAmpersand(String str) {
      return str.replaceAll("&amp;", "&");
   }

   public static String unbreakSpace(String str) {
      return str.replaceAll(" ", "&nbsp;");
   }
}
