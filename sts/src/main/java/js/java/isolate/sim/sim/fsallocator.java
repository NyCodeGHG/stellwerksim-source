package js.java.isolate.sim.sim;

import java.util.Iterator;
import java.util.LinkedList;
import js.java.isolate.sim.FATwriter;
import js.java.isolate.sim.GleisAdapter;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.gleisbild.gleisbildModelFahrweg;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasseSelection;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsAllocs;
import js.java.isolate.sim.gleisbild.fahrstrassen.fsUserAlloc;
import js.java.isolate.sim.zug.zug;

public final class fsallocator {
   private static FATwriter debugMode = null;
   private final GleisAdapter my_main;

   public static void setDebug(FATwriter b) {
      debugMode = b;
   }

   public static boolean isDebug() {
      return debugMode != null;
   }

   public fsallocator(GleisAdapter sim) {
      super();
      this.my_main = sim;
   }

   private int userRequest(int ein_enr, zug z) {
      gleisbildModel my_gleisbild = this.my_main.getGleisbild();
      if (debugMode != null) {
         debugMode.writeln("ÜG(" + ein_enr + ") request");
      }

      Iterator<gleis> it = my_gleisbild.findIterator(ein_enr, gleis.ELEMENT_ÜBERGABEAKZEPTOR);
      if (it.hasNext()) {
         gleis gl = (gleis)it.next();
         if (z != null) {
            gl.getFluentData().setStatusByZug(2, z);
         }

         if (gl.getFluentData().getStellung() == gleisElements.ST_ÜBERGABEAKZEPTOR_OK) {
            if (debugMode != null) {
               debugMode.writeln("ÜG(" + ein_enr + "): ok");
            }

            return 1;
         } else if (gl.getFluentData().getStellung() == gleisElements.ST_ÜBERGABEAKZEPTOR_NOK) {
            if (debugMode != null) {
               debugMode.writeln("ÜG(" + ein_enr + "): nok");
            }

            return -1;
         } else {
            gl.getFluentData().setStellung(gleisElements.ST_ÜBERGABEAKZEPTOR_ANFRAGE);
            if (debugMode != null) {
               debugMode.writeln("ÜG(" + ein_enr + "): anfrage");
            }

            return 0;
         }
      } else {
         return 1;
      }
   }

   public int isFreeToSignal(zug z, int ein_enr, boolean byüp) {
      gleisbildModelFahrweg my_gleisbild = this.my_main.getGleisbild();
      synchronized(this) {
         if (my_gleisbild.checkToSignal(ein_enr, byüp, z)) {
            int ur = this.userRequest(ein_enr, z);
            if (ur > 0) {
               return 1;
            } else {
               return ur == 0 ? 3 : 2;
            }
         } else {
            return 0;
         }
      }
   }

   public boolean getFS(fahrstrasse fs, fsAllocs modus) {
      return this.getFS(new fahrstrasseSelection(fs, false, this.my_main), modus);
   }

   public boolean getFS(fahrstrasseSelection fs, fsAllocs modus) {
      fsUserAlloc fua = fsUserAlloc.getAlloc(fs, modus);
      return fua != null ? fua.call() : false;
   }

   public void reserveAusfahrt(int enr) {
      this.my_main.getSim().reserveENR(enr);
      if (debugMode != null) {
         debugMode.writeln("RA: " + enr);
      }
   }

   public void unreserveAusfahrt(int enr) {
      this.my_main.getSim().unreserveENR(enr);
      if (debugMode != null) {
         debugMode.writeln("uRA: " + enr);
      }
   }

   public void zugMessage(int enr, zug z) {
      this.my_main.getSim().zugMessage(enr, z);
      if (debugMode != null) {
         debugMode.writeln("zM: " + enr + "/" + z.getName());
      }
   }

   public void zugTakenMessage(String msg, int enr, int z) {
      this.my_main.getSim().zugResponseMessage(msg, enr, z);
      if (debugMode != null) {
         debugMode.writeln("zTM: " + enr + "/zid " + z + "/" + msg);
      }
   }

   public void zugBlockMessage(int üpENR, zug z) {
      this.my_main.getSim().zugBlockMessage(üpENR, z);
   }

   public void gotReserveResponseMessage(String msg, int aus_enr, boolean reserve) {
      gleisbildModel my_gleisbild = this.my_main.getGleisbild();
      gleis gl = my_gleisbild.findFirst(aus_enr, gleis.ELEMENT_ÜBERGABEPUNKT);
      boolean suc = msg.compareToIgnoreCase("OK") == 0;
      boolean wit = msg.compareToIgnoreCase("WIT") == 0;
      if (debugMode != null) {
         debugMode.writeln("RRM: " + aus_enr + "/" + reserve + "/" + suc);
      }

      if (reserve && suc && gl.getFluentData().getStatus() == 3) {
         gl.getFluentData().setStatus(1);
      } else if (reserve && !suc && !wit && gl.getFluentData().getStatus() == 3) {
         gl.getFluentData().setStatus(0);
      } else if (!reserve && suc && gl.getFluentData().isReserviert()) {
         gl.getFluentData().setStatus(0);
      } else if (!reserve && !suc && gl.getFluentData().isReserviert()) {
         gl.getFluentData().setStatus(0);
      } else if (reserve && suc && gl.getFluentData().isFrei()) {
         this.unreserveAusfahrt(aus_enr);
      }
   }

   public String gotReserveMessage(int ein_enr, boolean reserve) {
      gleisbildModelFahrweg my_gleisbild = this.my_main.getGleisbild();
      String msg = "NOK";
      synchronized(this) {
         if (reserve) {
            boolean isfree = my_gleisbild.checkToSignal(ein_enr, false, null);
            if (isfree) {
               int ur = this.userRequest(ein_enr, null);
               if (ur > 0) {
                  my_gleisbild.reserveToSignal(ein_enr, true);
                  msg = "OK";
               } else if (ur == 0) {
                  msg = "WIT";
               } else {
                  msg = "NOK";
               }
            }
         } else {
            boolean r = my_gleisbild.freeToSignal(ein_enr);
            if (r) {
               msg = "OK";
            }
         }
      }

      if (msg.compareToIgnoreCase("OK") == 0) {
         my_gleisbild.repaint();
      }

      if (debugMode != null) {
         debugMode.writeln("RM: " + ein_enr + "/" + reserve + "=" + msg);
      }

      return msg;
   }

   public void gotEnterSignalMessage(int aus_enr, gleisElements.Stellungen stellung) {
      gleisbildModel my_gleisbild = this.my_main.getGleisbild();
      gleis gl = my_gleisbild.findFirst(aus_enr, gleis.ELEMENT_ÜBERGABEPUNKT);
      if (gl != null) {
         if (stellung != gleisElements.ST_ÜBERGABEPUNKT_GRÜN && stellung != gleisElements.ST_ÜBERGABEPUNKT_ROT) {
            gl.getFluentData().setStellung(gleisElements.ST_ÜBERGABEPUNKT_AUS);
         } else {
            gl.getFluentData().setStellung(stellung);
         }
      }
   }

   public void sentEnterSignalMessage(int ein_enr, gleisElements.Stellungen stellung) {
      if (this.my_main.getSim() != null) {
         this.my_main.getSim().sendEnterSignalMessage(ein_enr, stellung);
      }
   }

   public void reportSignalStellung(int enr, gleisElements.Stellungen stellung, fahrstrasse fs) {
      if (this.my_main.getSim() != null) {
         this.my_main.getSim().reportSignalStellung(enr, stellung, fs);
      }
   }

   public void sentEnterSignalMessage(LinkedList<gleis> elist) {
      if (this.my_main.getSim() != null) {
         this.my_main.getSim().sendEnterSignalMessage(elist);
      }
   }

   private void blockAusfahrt(gleis pos_gl, int v) {
      gleisbildModelFahrweg my_gleisbild = this.my_main.getGleisbild();
      synchronized(this) {
         my_gleisbild.statusÜPAusfahrt(pos_gl, v);
      }
   }

   public void blockAusfahrt(gleis pos_gl) {
      this.blockAusfahrt(pos_gl, 2);
   }

   public void unblockAusfahrt(gleis pos_gl) {
      this.blockAusfahrt(pos_gl, 0);
   }
}
