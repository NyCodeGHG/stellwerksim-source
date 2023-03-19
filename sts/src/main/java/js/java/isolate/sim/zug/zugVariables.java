package js.java.isolate.sim.zug;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class zugVariables {
   private final ConcurrentHashMap<String, zugVariables.variables> values = new ConcurrentHashMap();

   public zugVariables() {
      super();
      this.values.put("maxrandom_*", new zugVariables.variables(4));
      this.values.put("minstop_*", new zugVariables.variables(0));
      this.values.put("minstopR_*", new zugVariables.variables(30));
      this.values.put("maxstop_*", new zugVariables.variables(5));
      this.values.put("soll_tempo_*", new zugVariables.variables(-1));
      this.values.put("anrufwartezeit_*", new zugVariables.variables(3));
      this.values.put("gleisänderung_*", new zugVariables.variables(1));
      this.values.put("randomdevisor_*", new zugVariables.variables(2));
      this.values.put("gleisfalschmin_*", new zugVariables.variables(90));
      this.values.put("gleisfalschmax_*", new zugVariables.variables(600));
      this.values.put("gleisfalschmitte_*", new zugVariables.variables(300));
   }

   public void close() {
      this.values.clear();
   }

   private zugVariables.variables get(String typ, String bahnsteig) {
      zugVariables.variables v = (zugVariables.variables)this.values.get(typ + "_" + bahnsteig);
      if (v == null) {
         v = (zugVariables.variables)this.values.get(typ + "_*");
      }

      return v;
   }

   public Vector getStructure() {
      Vector v = new Vector();

      for(String k : this.values.keySet()) {
         zugVariables.variables vs = (zugVariables.variables)this.values.get(k);
         vs.getStructure(k, v);
      }

      return v;
   }

   public void setParameter(String p) {
      try {
         String[] v = p.trim().split("=");
         if (v != null && v.length == 3) {
            zugVariables.variables vs = (zugVariables.variables)this.values.get(v[0].trim() + "_" + v[1].trim());
            if (vs == null) {
               vs = new zugVariables.variables();
               this.values.put(v[0].trim() + "_" + v[1].trim(), vs);
            }

            vs.setBahnsteig(v[1].trim());
            vs.value = Integer.parseInt(v[2].trim());
            if (zug.isDebug()) {
               zug.getDebug().writeln("simparam", "vsv: " + v[0].trim() + "_" + v[1].trim() + "=" + v[2].trim() + " vs: " + vs.toString());
            }
         }
      } catch (NumberFormatException var4) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught (" + p + ")", var4);
      } catch (Exception var5) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "Caught", var5);
      }
   }

   public int var_get(String typ, String zielgleis) throws NullPointerException {
      zugVariables.variables v = this.get(typ, zielgleis);
      if (v == null) {
         throw new NullPointerException(typ);
      } else {
         return v.value;
      }
   }

   private int var_maxrandom(String zielgleis) {
      zugVariables.variables v = this.get("maxrandom", zielgleis);
      return v.value;
   }

   private int var_maxstop(String zielgleis) {
      zugVariables.variables v = this.get("maxstop", zielgleis);
      return v.value;
   }

   private int var_minstop(String zielgleis) {
      zugVariables.variables v = this.get("minstop", zielgleis);
      return v.value;
   }

   private int var_randomdevisor(String zielgleis) {
      zugVariables.variables v = this.get("randomdevisor", zielgleis);
      return v.value;
   }

   public int var_anrufwartezeit(String zielgleis) {
      zugVariables.variables v = this.get("anrufwartezeit", zielgleis);
      return v.value;
   }

   public int var_richtungswechselwartezeit(String zielgleis) {
      zugVariables.variables v = this.get("minstopR", zielgleis);
      return v.value;
   }

   public int soll_tempo(int st, String zielgleis) {
      zugVariables.variables v = this.get("soll_tempo", zielgleis);
      return v.value > 0 && v.isBahnsteig(zielgleis) ? v.value : st;
   }

   private boolean minStopTime(long mytime, long warankunft, long an, long ab, String zielgleis, boolean gleiswarok) {
      if (gleiswarok) {
         return mytime - warankunft > 1000L * (long)this.var_minstop(zielgleis);
      } else {
         zugVariables.variables v1 = this.get("gleisfalschmin", zielgleis);
         zugVariables.variables v2 = this.get("gleisfalschmitte", zielgleis);
         zugVariables.variables v3 = this.get("gleisfalschmax", zielgleis);
         return mytime - warankunft > zug.randomTimeShift((long)v1.value * 1000L, (long)v2.value * 1000L, (long)v3.value * 1000L);
      }
   }

   private boolean minStopANAB(long mytime, long warankunft, long an, long ab, String zielgleis, boolean gleiswarok) {
      int r = this.var_randomdevisor(zielgleis);
      return mytime - warankunft > (ab - an) / zug.randomTimeShift((long)r, (long)r, (long)(r + 3));
   }

   private boolean minStopRandom(long mytime, long warankunft, long an, long ab, String zielgleis, boolean gleiswarok) {
      return mytime - warankunft > zug.randomTimeShift(25000L, 30000L, 60000L * (long)this.var_maxrandom(zielgleis));
   }

   private boolean maxStop(long mytime, long warankunft, long an, long ab, String zielgleis, boolean gleiswarok) {
      return mytime - warankunft > 60000L * (long)this.var_maxstop(zielgleis);
   }

   public boolean calcAbfahrt(long mytime, long warankunft, long an, long ab, String zielgleis, boolean gleiswarok) {
      try {
         return this.minStopTime(mytime, warankunft, an, ab, zielgleis, gleiswarok)
               && this.minStopANAB(mytime, warankunft, an, ab, zielgleis, gleiswarok)
               && this.minStopRandom(mytime, warankunft, an, ab, zielgleis, gleiswarok)
            || this.maxStop(mytime, warankunft, an, ab, zielgleis, gleiswarok);
      } catch (Exception var12) {
         Logger.getLogger("stslogger").log(Level.SEVERE, "setparam error", var12);
         return true;
      }
   }

   public boolean var_allowesÄnderung(String zielgleis) {
      zugVariables.variables v = this.get("gleisänderung", zielgleis);
      return v.value == 1;
   }

   static class variables {
      public int value;
      public String bahnsteig = null;
      public int default_value = 0;
      public long hash = 0L;
      public boolean isAll = false;

      variables() {
         super();
      }

      variables(int d) {
         super();
         this.value = d;
         this.default_value = d;
      }

      public String toString() {
         return this.bahnsteig + "(" + this.hash + "):" + this.value;
      }

      public boolean isBahnsteig(String zielgleis) {
         boolean r = this.isAll;
         if (!r && zielgleis != null && this.bahnsteig != null && this.hash == (long)zielgleis.toLowerCase().hashCode()) {
            r = true;
         }

         return r;
      }

      public void setBahnsteig(String b) {
         this.isAll = false;
         if (b != null) {
            this.bahnsteig = b;
            if (b.equals("*")) {
               this.isAll = true;
            } else {
               this.hash = (long)b.toLowerCase().hashCode();
            }
         } else {
            this.hash = 0L;
            this.bahnsteig = null;
         }
      }

      private void getStructure(String n, Vector v) {
         v.addElement("variable");
         v.addElement(n);
         v.addElement("default");
         v.addElement(this.default_value);
         v.addElement("value");
         v.addElement(this.value);
         v.addElement("bahnsteig");
         v.addElement(this.bahnsteig);
         v.addElement("hash");
         v.addElement(this.hash + "");
         v.addElement("isAll");
         v.addElement(this.isAll + "");
      }
   }
}
