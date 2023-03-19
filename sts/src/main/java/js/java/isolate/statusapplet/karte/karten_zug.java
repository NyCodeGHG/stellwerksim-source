package js.java.isolate.statusapplet.karte;

import java.awt.Color;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import js.java.isolate.sim.flagdata;
import js.java.isolate.statusapplet.common.status_zug_base;
import js.java.isolate.statusapplet.common.szug_plan_base;
import org.xml.sax.Attributes;

class karten_zug extends status_zug_base implements Comparable {
   public karten_container currentKC = null;
   private Date an;
   private Date ab;
   private String ein = "";
   private String aus = "";
   private final kartePanel zp;
   private long lastChange = 0L;
   private final LinkedList<szug_plan_base> nextStops = new LinkedList();
   private final LinkedList<Integer> redirectAids = new LinkedList();
   private final HashSet<Integer> skipAids = new HashSet();
   private final connectColor cc;

   karten_zug(kartePanel _zp) {
      super();
      this.zp = _zp;
      this.sdf = DateFormat.getTimeInstance(3, Locale.GERMAN);
      this.lastChange = System.currentTimeMillis();
      this.cc = new connectColor(this.zp, Color.red);
   }

   public String getName() {
      return this.name != null ? this.name : "ID" + this.zid;
   }

   public String getSpezialName() {
      String n = this.getName();
      return n.indexOf(37) >= 0 ? n.substring(0, n.indexOf(37)) : n;
   }

   String getHtmlName() {
      String n = this.getSpezialName();
      n.replaceAll(" ", "&nbsp;");
      return n;
   }

   String getVerspaetung() {
      return (this.verspaetung > 0 ? "+" : "") + this.verspaetung;
   }

   public void addPlan(String _name, int _aid) {
      this.name = _name;
      szug_plan_base szp = new szug_plan_base();
      szp.parent = this;
      szp.aid = _aid;
      szp.azid = 1;
      szp.gleis = "1";

      try {
         szp.an = this.sdf.parse("12:00:00");
      } catch (Exception var6) {
      }

      try {
         szp.ab = this.sdf.parse("12:05:00");
      } catch (Exception var5) {
      }

      this.plan.add(szp);
   }

   public void addPlan(String _name, int _aid, int _flagdata) {
      this.name = _name;
      szug_plan_base szp = new szug_plan_base();
      szp.parent = this;
      szp.aid = _aid;
      szp.azid = 1;
      szp.gleis = "1";
      szp.flagdatazid.add(_flagdata);

      try {
         szp.an = this.sdf.parse("12:00:00");
      } catch (Exception var7) {
      }

      try {
         szp.ab = this.sdf.parse("12:05:00");
      } catch (Exception var6) {
      }

      this.plan.add(szp);
   }

   public void addPlan(Attributes attrs) {
      try {
         this.visibleaid = Integer.parseInt(attrs.getValue("visibleaid"));
      } catch (NumberFormatException var11) {
         this.visibleaid = 0;
      }

      try {
         this.verspaetung = Integer.parseInt(attrs.getValue("verspaetung"));
      } catch (NumberFormatException var10) {
      }

      this.name = attrs.getValue("name");
      this.thematag = attrs.getValue("thematag").charAt(0);
      szug_plan_base szp = new szug_plan_base();
      szp.parent = this;
      szp.aid = Integer.parseInt(attrs.getValue("aid"));
      szp.azid = Integer.parseInt(attrs.getValue("azid"));
      szp.gleis = attrs.getValue("gleis");
      szp.flags = new flagdata(attrs.getValue("flags"), attrs.getValue("flagdata"), attrs.getValue("flagparam"));

      for(int z : szp.flags) {
         if (z > 0) {
            szp.flagdatazid.add(z);
            this.refs.add(z);
         }
      }

      try {
         szp.an = this.sdf.parse(attrs.getValue("an"));
      } catch (Exception var9) {
      }

      try {
         szp.ab = this.sdf.parse(attrs.getValue("ab"));
      } catch (Exception var8) {
      }

      try {
         szp.ein_enr = Integer.parseInt(attrs.getValue("ein_enr"));
      } catch (NumberFormatException var7) {
      }

      try {
         szp.aus_enr = Integer.parseInt(attrs.getValue("aus_enr"));
      } catch (NumberFormatException var6) {
      }

      this.plan.add(szp);

      try {
         this.updateAid(Integer.parseInt(attrs.getValue("caid")));
      } catch (NumberFormatException var5) {
      }
   }

   public int compareTo(Object o) {
      if (!(o instanceof karten_zug)) {
         return 1;
      } else {
         karten_zug z2 = (karten_zug)o;
         if (z2.an == null || z2.ab == null) {
            return 1;
         } else if (this.an != null && this.ab != null) {
            long anl = this.an.getTime() + (long)(this.verspaetung * 60 * 1000);
            long abl = this.ab.getTime() + (long)(this.verspaetung * 60 * 1000);
            long zanl = z2.an.getTime() + (long)(z2.verspaetung * 60 * 1000);
            long zabl = z2.ab.getTime() + (long)(z2.verspaetung * 60 * 1000);
            int r = 0;
            if (anl < zanl) {
               r = -1;
            }

            if (anl > zanl) {
               r = 1;
            }

            if (r == 0) {
               if (abl < zabl) {
                  r = -1;
               }

               if (abl > zabl) {
                  r = 1;
               }
            }

            if (r == 0) {
               r = this.name.compareTo(z2.name);
            }

            return r;
         } else {
            return 1;
         }
      }
   }

   void remove() {
      if (this.currentKC != null) {
         this.currentKC = null;
      }
   }

   public int changedDelay() {
      return (int)((System.currentTimeMillis() - this.lastChange) / 1000L);
   }

   public String getAn() {
      return this.an != null ? this.sdf.format(this.an) : "";
   }

   public String getAb() {
      return this.ab != null ? this.sdf.format(this.ab) : "";
   }

   public String getEin() {
      String n = this.ein;
      return n.indexOf(37) >= 0 ? n.substring(0, n.indexOf(37)) : n;
   }

   public String getAus() {
      String n = this.aus;
      return n.indexOf(37) >= 0 ? n.substring(0, n.indexOf(37)) : n;
   }

   public String toHtmlString() {
      return (this.laststopdone ? "<i>" : "")
         + this.getHtmlName()
         + "&nbsp;("
         + (this.verspaetung > 0 ? "+" : "")
         + this.verspaetung
         + ")"
         + (this.laststopdone ? "</i>" : "");
   }

   public String getNextStopsHtml() {
      String ret = "<table border=0 cellspacing=1 cellpadding=0><tr><td>";
      Date nan = null;
      Date nab = null;
      int lastAid = 0;
      String flagstring = "";

      for(szug_plan_base pos : this.nextStops) {
         karten_container k = (karten_container)this.zp.aids.get(pos.aid);
         if (k != null && k.namen != null) {
            if (pos.aid != lastAid) {
               lastAid = pos.aid;
               nan = pos.an;
               if (nab != null) {
                  ret = ret + "-" + this.sdf.format(nab) + flagstring;
                  flagstring = "";
               }

               nab = null;
               ret = ret + "</td></tr><tr valign=top><td>-&gt; ";
               ret = ret + k.namen;
               if (nan != null && !this.skipAids.contains(pos.aid)) {
                  ret = ret + ": </td><td>" + this.sdf.format(nan);
               } else if (this.skipAids.contains(pos.aid)) {
                  ret = ret + " </td><td><b>entfällt</b>";
               }
            }

            if (!this.skipAids.contains(pos.aid)) {
               nab = pos.ab;
            }

            if (pos.flags.hasFlag('E')) {
               int nzid = pos.flags.dataOfFlag('E');
               karten_zug kz = this.zp.findZug(nzid);
               if (kz != null) {
                  flagstring = flagstring + "<br><i>wird " + kz.getSpezialName() + "</i>";
               }
            } else if (pos.flags.hasFlag('K')) {
               int nzid = pos.flags.dataOfFlag('K');
               karten_zug kz = this.zp.findZug(nzid);
               if (kz != null) {
                  flagstring = flagstring + "<br><i>an " + kz.getSpezialName() + "</i>";
               }
            } else if (pos.flags.hasFlag('F')) {
               int nzid = pos.flags.dataOfFlag('F');
               karten_zug kz = this.zp.findZug(nzid);
               if (kz != null) {
                  flagstring = flagstring + "<br><i>Teil zu " + kz.getSpezialName() + "</i>";
               }
            }
         }
      }

      if (nab != null) {
         ret = ret + "-" + this.sdf.format(nab) + flagstring;
      }

      ret = ret + "</td></tr>";
      if (!this.redirectAids.isEmpty()) {
         ret = ret + "<tr><td colspan=2>Umleitung über:<br>";
         String sep = "";

         for(int raid : this.redirectAids) {
            karten_container k = (karten_container)this.zp.aids.get(raid);
            if (k != null) {
               ret = ret + sep + k.namen;
            } else {
               ret = ret + sep + raid;
            }

            sep = ", ";
         }

         ret = ret + "</td></tr>";
      }

      return ret + "</table>";
   }

   void updateAid(int newaid) {
      this.lastChange = System.currentTimeMillis();
      this.aid = newaid;
      this.an = null;
      this.ab = null;
      this.ein = "";
      this.aus = "";
      this.nextStops.clear();
      boolean addMode = false;

      for(szug_plan_base pos : this.plan) {
         if (pos.aid == this.aid) {
            addMode = true;
            if (this.an == null) {
               this.an = pos.an;
               karten_container kc = (karten_container)this.zp.aids.get(this.aid);
               if (kc != null) {
                  this.ein = (String)kc.einfahrten.get(pos.ein_enr);
                  this.aus = (String)kc.ausfahrten.get(pos.aus_enr);
               }
            }

            this.ab = pos.ab;
         }

         if (addMode) {
            this.nextStops.add(pos);
         }
      }
   }

   void updateRedirect(int res, String[] tokens) {
      if (res == 200 || res == 250) {
         int caid = 0;
         if (res == 200) {
            this.redirectAids.clear();
            this.skipAids.clear();
         }

         try {
            for(int i = 0; i < tokens.length - 1; ++i) {
               if (tokens[i].trim().equals("AID")) {
                  i += 2;
                  this.redirectAids.add(Integer.parseInt(tokens[i].trim()));
               } else if (tokens[i].trim().equals("SKIP")) {
                  i += 2;
                  this.skipAids.add(Integer.parseInt(tokens[i].trim()));
               } else if (tokens[i].trim().equals("CUR")) {
                  ++i;

                  try {
                     caid = Integer.parseInt(tokens[i].trim());
                  } catch (NumberFormatException var6) {
                  }
               }
            }
         } catch (ArrayIndexOutOfBoundsException var7) {
         }

         if (caid > 0 && this.redirectAids.contains(caid)) {
            while(!this.redirectAids.isEmpty() && this.redirectAids.peekFirst() != caid) {
               this.redirectAids.pollFirst();
            }

            if (this.redirectAids.isEmpty()) {
               this.skipAids.clear();
            }
         }

         this.prepareColor();
      }
   }

   private void prepareColor() {
      if (this.redirectAids.isEmpty()) {
         this.zp.removeMark(this.cc);
      } else {
         this.cc.resetMark();
         int aid2 = 0;

         for(int aid1 : this.redirectAids) {
            if (aid2 > 0) {
               this.cc.addMarkAid(aid1, aid2);
            }

            aid2 = aid1;
         }

         this.zp.addMarkAid(this.cc);
      }
   }

   boolean isInRedirect() {
      return this.redirectAids.contains(this.aid);
   }

   public int getAid() {
      return this.aid;
   }

   public int getZid() {
      return this.zid;
   }
}
