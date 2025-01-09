package js.java.isolate.sim.gleis;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import js.java.isolate.sim.eventsys.eventGenerator;
import js.java.isolate.sim.gleis.gleisElements.gleisElements;
import js.java.isolate.sim.gleisbild.gleisbildModel;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.isolate.sim.zug.zug;

public class fluentData implements gleisElements {
   private final gleis my_gleis;
   private LinkedList<gleisElements.Stellungen> possibleStellung = null;
   private boolean isStatusChangeAllowed = true;
   private boolean allowBlinkccReset = true;
   private fluentData.statusChangeTrigger sct = new fluentData.emptyStatusTrigger();
   private fluentData.hookCallChecker hcc = new fluentData.hookCallChecker();
   private fluentData.setStellungHandling ssh = new fluentData.setStellungHandling();
   boolean pressed = false;
   boolean gesperrt = false;
   boolean power_off = false;
   int status = 0;
   gleisElements.Stellungen stellung = gleisElements.Stellungen.undef;
   String display_stellung = "";
   long stellungChangeTime = 0L;
   private boolean allowStellung = false;
   fahrstrasse startingFS = null;
   fahrstrasse endingFS = null;
   fahrstrasse overFS = null;
   String display_new_stellung = "";
   boolean display_blink = false;
   int display_blink_count = 0;
   fahrstrasse fsspeicher = null;
   int fsspeicherend = 0;
   gleis einfahrtUmleitung = null;
   zug zugamgleis = null;
   long cnt_zug = 0L;
   long cnt_stellung = 0L;
   gleis connectedSignal = null;

   public fluentData(gleis gl) {
      this.my_gleis = gl;
      if (gleis.ALLE_GLEISE.matches(this.my_gleis.telement)) {
         this.initFDschiene();
      } else if (gleis.ALLE_DISPLAYS.matches(this.my_gleis.telement)) {
         this.initFDdisplay();
      } else if (gleis.ALLE_KNÖPFE.matches(this.my_gleis.telement)) {
         this.initFDknopf();
      }

      this.initPossibleStellung();
      this.init();
   }

   private void initFDschiene() {
      if (this.my_gleis.telement == gleis.ELEMENT_KREUZUNGBRUECKE) {
         this.isStatusChangeAllowed = false;
      } else if (this.my_gleis.telement == gleis.ELEMENT_BAHNÜBERGANG) {
         this.set(new fluentData.statusChangeTrigger() {
            @Override
            public void statusChangeTrigger(fluentData parent, gleis my_gleis) {
               if (parent.status == 0) {
                  parent.setStellung(gleisElements.ST_BAHNÜBERGANG_OFFEN);
               } else if (parent.status == 3 && my_gleis.hasHook(eventGenerator.T_GLEIS_STATUS)) {
                  my_gleis.call(parent.status, null);
               } else if (gleis.michNervenBüs && parent.status == 3) {
                  parent.setStellung(gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN);
               }
            }
         });
      } else if (this.my_gleis.telement == gleis.ELEMENT_AUTOBAHNÜBERGANG) {
         this.set(new fluentData.statusChangeTrigger() {
            @Override
            public void statusChangeTrigger(fluentData parent, gleis my_gleis) {
               if (parent.status == 0) {
                  parent.setStellung(gleisElements.ST_BAHNÜBERGANG_OFFEN);
               } else if (parent.status == 3 && my_gleis.hasHook(eventGenerator.T_GLEIS_STATUS)) {
                  my_gleis.call(parent.status, null);
               }
            }
         });
      } else if (this.my_gleis.telement == gleis.ELEMENT_ANRUFÜBERGANG) {
         this.set(new fluentData.statusChangeTrigger() {
            @Override
            public void statusChangeTrigger(fluentData parent, gleis my_gleis) {
               if (gleis.michNervenBüs && parent.status == 3) {
                  parent.setStellung(gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN);
               }
            }
         });
      } else if (this.my_gleis.telement == ELEMENT_WBAHNÜBERGANG) {
         this.set(new fluentData.statusChangeTrigger() {
            @Override
            void statusChangeTrigger(fluentData parent, gleis gl) {
               if (parent.status == 0) {
                  parent.setStellung(gleisElements.ST_BAHNÜBERGANG_OFFEN);
               } else if (parent.status == 3) {
                  if (fluentData.this.my_gleis.hasHook(eventGenerator.T_GLEIS_STATUS)) {
                     fluentData.this.my_gleis.call(parent.status, null);
                  } else {
                     parent.setStellung(gleisElements.ST_BAHNÜBERGANG_GESCHLOSSEN);
                  }
               }
            }
         });
      } else if (this.my_gleis.telement == gleis.ELEMENT_EINFAHRT) {
         this.set(new fluentData.directionCallChecker());
         this.set(
            new fluentData.statusChangeTrigger() {
               @Override
               public void statusChangeTrigger(fluentData parent, gleis my_gleis) {
                  if (parent.status == 2 || parent.status == 0) {
                     Iterator<gleis> it = my_gleis.glbModel.findIterator(my_gleis.enr, gleis.ELEMENT_ÜBERGABEAKZEPTOR);

                     while (it.hasNext()) {
                        gleis gl = (gleis)it.next();
                        if (gl.fdata.stellung == gleisElements.ST_ÜBERGABEAKZEPTOR_OK) {
                           gl.fdata.setStellung(gleisElements.ST_ÜBERGABEAKZEPTOR_UNDEF);
                           gl.fdata.setStatus(0);
                           if (gleis.isDebug()) {
                              gleis.debugMode
                                 .writeln("[gleis::ÜG]", "clear " + gl.getCol() + "/" + gl.getRow() + " by " + my_gleis.getCol() + "/" + my_gleis.getRow());
                           }
                        }
                     }
                  }
               }

               @Override
               public void statusChangeTrigger(fluentData parent, gleis my_gleis, gleis before_gl) {
                  if ((parent.status == 2 || parent.status == 0) && (before_gl == null || my_gleis.forUs(before_gl))) {
                     this.statusChangeTrigger(parent, my_gleis);
                  }
               }
            }
         );
      } else if (this.my_gleis.telement == gleis.ELEMENT_SIGNAL) {
         this.set(new fluentData.directionCallChecker());
      }
   }

   private void initFDdisplay() {
   }

   private void initFDknopf() {
      if (this.my_gleis.telement == ELEMENT_ÜBERGABEAKZEPTOR) {
         this.allowBlinkccReset = false;
      }
   }

   private void initPossibleStellung() {
      LinkedList<gleisElements.Stellungen> ret = new LinkedList();
      if (this.my_gleis.telement == ELEMENT_SIGNAL) {
         ret.add(ST_SIGNAL_ROT);
         ret.add(ST_SIGNAL_GRÜN);
         ret.add(ST_SIGNAL_AUS);
         ret.add(ST_SIGNAL_ZS1);
         ret.add(ST_SIGNAL_RF);
         this.set(new fluentData.setStellungHandling() {
            @Override
            boolean set(fluentData parent, gleisElements.Stellungen newSt, fahrstrasse fs, gleis my_gleis) {
               boolean ret = true;
               if (newSt != gleisElements.ST_SIGNAL_AUS && newSt != gleisElements.ST_SIGNAL_RF && my_gleis.hasHook(eventGenerator.T_GLEIS_STELLUNG)) {
                  ret = my_gleis.call(newSt, fs);
               }

               if (ret) {
                  if (my_gleis.ein_enr > 0 && parent.stellung != newSt) {
                     my_gleis.theapplet.getFSallocator().sentEnterSignalMessage(my_gleis.ein_enr, newSt);
                  }

                  if (parent.stellung != newSt) {
                     my_gleis.theapplet.getFSallocator().reportSignalStellung(my_gleis.enr, newSt, fs);
                  }

                  super.set(parent, newSt, fs, my_gleis);
               }

               return ret;
            }
         });
      } else if (this.my_gleis.telement == ELEMENT_ZWERGSIGNAL) {
         ret.add(ST_SIGNAL_ROT);
         ret.add(ST_SIGNAL_GRÜN);
         ret.add(ST_SIGNAL_AUS);
         ret.add(ST_SIGNAL_RF);
         ret.add(ST_SIGNAL_ZS1);
         this.set(new fluentData.setStellungHandling() {
            @Override
            boolean set(fluentData parent, gleisElements.Stellungen newSt, fahrstrasse fs, gleis my_gleis) {
               boolean ret = true;
               if (newSt != gleisElements.ST_SIGNAL_AUS && newSt != gleisElements.ST_SIGNAL_RF && my_gleis.hasHook(eventGenerator.T_GLEIS_STELLUNG)) {
                  ret = my_gleis.call(newSt, fs);
               }

               if (ret) {
                  if (parent.stellung != newSt) {
                     my_gleis.theapplet.getFSallocator().reportSignalStellung(my_gleis.enr, newSt, fs);
                  }

                  super.set(parent, newSt, fs, my_gleis);
               }

               return ret;
            }
         });
      } else if (this.my_gleis.telement == ELEMENT_ZDECKUNGSSIGNAL) {
         ret.add(ST_SIGNAL_GRÜN);
         ret.add(ST_SIGNAL_ROT);
         ret.add(ST_SIGNAL_AUS);
         ret.add(ST_ZDSIGNAL_FESTGELEGT);
         this.set(
            new fluentData.setStellungHandling() {
               @Override
               boolean set(fluentData parent, gleisElements.Stellungen newSt, fahrstrasse fs, gleis my_gleis) {
                  boolean ret = true;
                  if (newSt != gleisElements.ST_SIGNAL_AUS
                     && newSt != gleisElements.ST_ZDSIGNAL_FESTGELEGT
                     && newSt != gleisElements.ST_SIGNAL_RF
                     && my_gleis.hasHook(eventGenerator.T_GLEIS_STELLUNG)) {
                     ret = my_gleis.call(newSt, fs);
                  }

                  if (newSt != gleisElements.ST_ZDSIGNAL_FESTGELEGT) {
                     my_gleis.tjmAdd();
                  }

                  if (ret) {
                     super.set(parent, newSt, fs, my_gleis);
                  }

                  return ret;
               }
            }
         );
      } else if (this.my_gleis.telement == ELEMENT_WEICHEUNTEN || this.my_gleis.telement == ELEMENT_WEICHEOBEN) {
         ret.add(ST_WEICHE_GERADE);
         ret.add(ST_WEICHE_ABZWEIG);
         ret.add(ST_WEICHE_AUS);
         this.set(new fluentData.setStellungHandling() {
            @Override
            boolean set(fluentData parent, gleisElements.Stellungen newSt, fahrstrasse fs, gleis my_gleis) {
               boolean ret = true;
               if (newSt != gleisElements.ST_WEICHE_AUS && my_gleis.hasHook(eventGenerator.T_GLEIS_STELLUNG)) {
                  ret = my_gleis.call(newSt, fs);
               }

               if (ret) {
                  super.set(parent, newSt, fs, my_gleis);
               }

               return ret;
            }
         });
      } else if (this.my_gleis.telement == ELEMENT_BAHNÜBERGANG) {
         ret.add(ST_BAHNÜBERGANG_OFFEN);
         ret.add(ST_BAHNÜBERGANG_GESCHLOSSEN);
         ret.add(ST_BAHNÜBERGANG_AUS);
         this.set(new fluentData.bueStellungsHandling());
      } else if (this.my_gleis.telement == ELEMENT_ANRUFÜBERGANG) {
         ret.add(ST_ANRUFÜBERGANG_GESCHLOSSEN);
         ret.add(ST_ANRUFÜBERGANG_OFFEN);
         ret.add(ST_ANRUFÜBERGANG_AUS);
         this.set(new fluentData.bueStellungsHandling());
      } else if (this.my_gleis.telement == ELEMENT_WBAHNÜBERGANG) {
         ret.add(ST_BAHNÜBERGANG_OFFEN);
         ret.add(ST_BAHNÜBERGANG_GESCHLOSSEN);
         this.set(new fluentData.bueStellungsHandling());
      } else if (this.my_gleis.telement == ELEMENT_AUTOBAHNÜBERGANG) {
         ret.add(ST_BAHNÜBERGANG_OFFEN);
         ret.add(ST_BAHNÜBERGANG_GESCHLOSSEN);
         this.set(new fluentData.bueStellungsHandling());
      } else if (this.my_gleis.telement == gleis.ELEMENT_ÜBERGABEAKZEPTOR) {
         ret.add(ST_ÜBERGABEAKZEPTOR_UNDEF);
         ret.add(ST_ÜBERGABEAKZEPTOR_OK);
         ret.add(ST_ÜBERGABEAKZEPTOR_NOK);
         ret.add(ST_ÜBERGABEAKZEPTOR_ANFRAGE);
         this.set(new fluentData.setStellungHandling() {
            @Override
            boolean set(fluentData parent, gleisElements.Stellungen newSt, fahrstrasse fs, gleis my_gleis) {
               if (parent.stellung != gleisElements.ST_ÜBERGABEAKZEPTOR_ANFRAGE) {
                  my_gleis.blinkcc = 0;
               }

               parent.setStellungTo(newSt);
               if (newSt == gleisElements.ST_ÜBERGABEAKZEPTOR_ANFRAGE || newSt == gleisElements.ST_ÜBERGABEAKZEPTOR_NOK) {
                  my_gleis.tjmAdd();
               }

               return true;
            }
         });
      } else if (this.my_gleis.telement == ELEMENT_ÜBERGABEPUNKT) {
         ret.add(ST_ÜBERGABEPUNKT_AUS);
         ret.add(ST_ÜBERGABEPUNKT_ROT);
         ret.add(ST_ÜBERGABEPUNKT_GRÜN);
      }

      this.possibleStellung = ret;
   }

   private void set(fluentData.statusChangeTrigger sct) {
      this.sct = sct;
   }

   private void set(fluentData.hookCallChecker hcc) {
      this.hcc = hcc;
   }

   private void set(fluentData.setStellungHandling ssh) {
      this.ssh = ssh;
   }

   public void init() {
      this.fsspeicher = null;
      this.fsspeicherend = 0;
      this.status = 0;
      this.display_stellung = "";
      this.stellung = this.getInitialStellung();
      this.overFS = null;
      this.startingFS = null;
      this.endingFS = null;
      this.stellungChangeTime = System.currentTimeMillis();
      this.cnt_zug = 0L;
      this.cnt_stellung = 0L;
      this.connectedSignal = null;
   }

   public int getStatus() {
      return this.status;
   }

   public boolean isFrei() {
      return this.status == 0;
   }

   public boolean isReserviert() {
      return this.status == 1 || this.status == 3;
   }

   public boolean isPrepared() {
      return this.status == 1 || this.status == 3 || this.status == 4;
   }

   public void setStatus(int s) {
      this.setStatus(s, true);
   }

   private void setStatus(int s, boolean callDisplay) {
      this.my_gleis.blinkcc = 0;
      if (s == 0) {
         this.startingFS = null;
         this.endingFS = null;
         this.overFS = null;
         this.connectedSignal = null;
      }

      if (this.isStatusChangeAllowed) {
         this.status = s;
      }

      if (callDisplay && this.my_gleis.gleisdecor.displayTrigger) {
         this.my_gleis.glbModel.getDisplayBar().status(this.my_gleis);
      }

      this.statusChangeTrigger();
      this.my_gleis.gruppe.updateStatus(this.my_gleis, this.status);
   }

   public void setStatusByFs(int s, fahrstrasse fs) {
      this.setStatus(s, false);
      if (this.status == 3 || this.status == 1) {
         this.overFS = fs;
         if (this.status == 1 && this.my_gleis.gleisdecor.displayFStrigger) {
            this.my_gleis.glbModel.getDisplayBar().fahrstrasse(this.my_gleis, fs);
         }
      } else if (this.status == 0) {
         this.overFS = null;
         this.startingFS = null;
         this.endingFS = null;
         this.connectedSignal = null;
         if (this.my_gleis.gleisdecor.displayFStrigger) {
            this.my_gleis.glbModel.getDisplayBar().fahrstrasse(this.my_gleis, fs);
         }
      }
   }

   public boolean setStatusByZug(int s, zug z) {
      return this.setStatusByZug(s, z, null);
   }

   public boolean setStatusByZug(int s, zug z, gleis before_gl) {
      if (s == 2 && this.status == 2 && this.zugamgleis == z) {
         return true;
      } else {
         boolean ret = true;
         if (this.allowBlinkccReset) {
            this.my_gleis.blinkcc = 0;
         }

         ret = this.handleHook(s, z, before_gl);
         if (ret && this.isStatusChangeAllowed) {
            this.status = s;
         }

         if (ret && s == 2) {
            if (this.zugamgleis != z) {
               this.cnt_zug++;
            }

            this.zugamgleis = z;
            this.endingFS = null;
         } else if (s == 0) {
            this.overFS = null;
            this.startingFS = null;
            this.zugamgleis = null;
            this.connectedSignal = null;
         } else {
            this.zugamgleis = null;
         }

         if (this.my_gleis.gleisdecor.displayTrigger && (before_gl == null || this.my_gleis.forUs(before_gl))) {
            this.my_gleis.glbModel.getDisplayBar().zug(this.my_gleis, z);
         }

         this.statusChangeTrigger(before_gl);
         this.my_gleis.gruppe.updateStatus(this.my_gleis, this.status);
         return ret;
      }
   }

   public void setZugAmGleis(zug z) {
      if (this.status == 2) {
         if (this.zugamgleis != z) {
            this.cnt_zug++;
         }

         this.zugamgleis = z;
      }
   }

   public zug getZugAmGleis() {
      return this.zugamgleis;
   }

   private void statusChangeTrigger() {
      this.sct.statusChangeTrigger(this, this.my_gleis);
   }

   private void statusChangeTrigger(gleis before_gl) {
      this.sct.statusChangeTrigger(this, this.my_gleis, before_gl);
   }

   private boolean handleHook(int newstatus, zug z, gleis before_gl) {
      boolean ret = true;
      if (this.my_gleis.hasHook(eventGenerator.T_GLEIS_STATUS) && this.hcc.allowsCall(newstatus, this.my_gleis, before_gl)) {
         ret = this.my_gleis.call(newstatus, z);
      }

      return ret;
   }

   public void setStartingFS(fahrstrasse fs) {
      this.startingFS = fs;
   }

   public void setEndingFS(fahrstrasse fs) {
      this.endingFS = fs;
   }

   public gleisElements.Stellungen getStellung() {
      return this.stellung;
   }

   public long getStellungChangeTime() {
      return this.stellungChangeTime;
   }

   public boolean setStellung(gleisElements.Stellungen s) {
      return this.setStellung(s, null);
   }

   public boolean setStellung(gleisElements.Stellungen s, fahrstrasse f) {
      if (this.gesperrt && !this.allowStellung) {
         return false;
      } else {
         boolean ret = true;
         if (!this.possibleStellung.isEmpty()) {
            for (gleisElements.Stellungen st : this.possibleStellung) {
               if (s == st) {
                  ret = this.setStellungTo(st, f);
                  break;
               }
            }
         }

         return ret;
      }
   }

   protected boolean setStellungTo(gleisElements.Stellungen s, fahrstrasse fs) {
      return this.ssh.set(this, s, fs, this.my_gleis);
   }

   void setStellungTo(gleisElements.Stellungen s) {
      gleisElements.Stellungen oldst = this.stellung;
      if (oldst != s) {
         this.stellung = s;
         this.stellungChangeTime = System.currentTimeMillis();
         this.cnt_stellung++;
      }
   }

   public gleisElements.Stellungen getInitialStellung() {
      return this.possibleStellung != null && !this.possibleStellung.isEmpty()
         ? (gleisElements.Stellungen)this.possibleStellung.get(0)
         : gleisElements.Stellungen.undef;
   }

   public List<gleisElements.Stellungen> getPossibleStellung() {
      return Collections.unmodifiableList(this.possibleStellung);
   }

   public void setGesperrt(boolean g) {
      this.gesperrt = g;
      this.allowStellung = false;
   }

   public void setGesperrtAndStellung(boolean g, boolean allowStellung) {
      this.gesperrt = g;
      this.allowStellung = allowStellung;
   }

   public boolean isGesperrt() {
      return this.gesperrt;
   }

   public fahrstrasse getCurrentFS() {
      return this.overFS;
   }

   public fahrstrasse getStartingFS() {
      return this.startingFS;
   }

   public fahrstrasse getEndingFS() {
      return this.endingFS;
   }

   public boolean hasCurrentFS() {
      return this.overFS != null;
   }

   public void setConnectedSignal(gleis signal) {
      this.connectedSignal = signal;
   }

   public gleis getConnectedSignal() {
      return this.connectedSignal;
   }

   public void setPressed(boolean p) {
      this.pressed = p;
   }

   public boolean isPowerOff() {
      return this.power_off;
   }

   public void setPowerOff(boolean power_off) {
      this.power_off = power_off;
   }

   public boolean set_FW_speicher(fahrstrasse f) {
      if (this.my_gleis.telement == gleis.ELEMENT_SIGNAL && f != null && f.getStop().fdata.fsspeicherend == 0) {
         this.fsspeicher = f;
         this.fsspeicher.getStop().fdata.fsspeicherend++;
         this.my_gleis.tjmAdd();
      }

      return this.fsspeicher != null;
   }

   public fahrstrasse get_FW_speicher() {
      return this.fsspeicher;
   }

   public synchronized boolean clear_FW_speicher() {
      if (this.my_gleis.telement == gleis.ELEMENT_SIGNAL && this.fsspeicher != null) {
         this.fsspeicher.getStop().fdata.fsspeicherend--;
         if (this.fsspeicher.getStop().fdata.fsspeicherend < 0) {
            this.fsspeicher.getStop().fdata.fsspeicherend = 0;
         }

         this.fsspeicher = null;
      }

      return this.fsspeicher != null;
   }

   public void setEinfahrtUmleitung(gleis eg) {
      this.einfahrtUmleitung = eg;
   }

   public gleis getEinfahrtUmleitung() {
      return this.einfahrtUmleitung;
   }

   public void displayClear(boolean now) {
      this.display_blink = false;
      if (now) {
         this.display_stellung = "";
      } else {
         this.display_new_stellung = "";
         this.my_gleis.blinkcc = 0;
         this.my_gleis.tjmAdd();
      }

      if (gleis.debugMode != null) {
         gleis.debugMode.writeln("gleis::Display", "sw:" + this.my_gleis.swwert + " clear " + this.my_gleis.getCol() + "/" + this.my_gleis.getRow());
      }
   }

   public void displaySet(String s) {
      this.display_blink = false;
      this.display_new_stellung = this.display_stellung = s;
   }

   public void displayBlink(boolean blink) {
      this.display_blink_count = -1;
      this.display_blink = blink;
      if (blink) {
         this.my_gleis.blinkcc = 0;
         this.my_gleis.tjmAdd();
      }
   }

   public void displayBlink(boolean blink, int delay) {
      this.display_blink_count = delay;
      this.display_blink = blink;
      if (blink) {
         this.my_gleis.blinkcc = 0;
         this.my_gleis.tjmAdd();
      }
   }

   public String displayGetValue() {
      return this.display_stellung;
   }

   public void swapRichtung(gleisElements.RICHTUNG... swap) {
      if (swap.length % 2 != 0) {
         throw new IllegalArgumentException("Gerade Anzahl Parameter erwartet");
      } else {
         for (int i = 0; i < swap.length; i += 2) {
            if (this.my_gleis.richtung == swap[i]) {
               this.my_gleis.richtung = swap[i + 1];
               break;
            }
         }
      }
   }

   public void swapRichtungHoriz() {
      this.swapRichtung(gleisElements.RICHTUNG.left, gleisElements.RICHTUNG.right, gleisElements.RICHTUNG.right, gleisElements.RICHTUNG.left);
   }

   public void swapRichtungVert() {
      this.swapRichtung(gleisElements.RICHTUNG.up, gleisElements.RICHTUNG.down, gleisElements.RICHTUNG.down, gleisElements.RICHTUNG.up);
   }

   static class bueStellungsHandling extends fluentData.setStellungHandling {
      @Override
      boolean set(fluentData parent, gleisElements.Stellungen newSt, fahrstrasse fs, gleis my_gleis) {
         LinkedList<gleis> ll = gleisbildModel.iterator2list(my_gleis.glbModel.findIterator(my_gleis.enr, my_gleis.telement));
         boolean rr = true;
         if (newSt == gleisElements.ST_BAHNÜBERGANG_OFFEN) {
            Iterator<gleis> it2 = ll.iterator();

            while (rr && it2.hasNext()) {
               gleis gl = (gleis)it2.next();
               rr &= gl.fdata.isFrei();
            }
         }

         if (newSt != gleisElements.ST_BAHNÜBERGANG_AUS) {
            Iterator<gleis> it2 = ll.iterator();

            while (rr && it2.hasNext()) {
               gleis gl = (gleis)it2.next();
               if (gl.hasHook(eventGenerator.HOOKKIND.WORKER, eventGenerator.T_GLEIS_STELLUNG)) {
                  rr &= gl.call(newSt, fs);
               }
            }
         }

         if (rr) {
            for (gleis gl : ll) {
               gl.fdata.setStellungTo(newSt);
            }

            Iterator<gleis> it = my_gleis.glbModel.findIterator(my_gleis.enr, gleis.ELEMENT_BÜDISPLAY);

            while (it.hasNext()) {
               ((gleis)it.next()).tjmAdd();
            }
         }

         my_gleis.blinkcc = 0;
         return true;
      }
   }

   static class directionCallChecker extends fluentData.hookCallChecker {
      @Override
      boolean allowsCall(int newstatus, gleis my_gleis, gleis before_gl) {
         return before_gl == null || my_gleis.forUs(before_gl);
      }
   }

   private static class emptyStatusTrigger extends fluentData.statusChangeTrigger {
      private emptyStatusTrigger() {
      }

      @Override
      public void statusChangeTrigger(fluentData parent, gleis gl) {
      }
   }

   static class hookCallChecker {
      boolean allowsCall(int newstatus, gleis my_gleis, gleis before_gl) {
         return true;
      }
   }

   static class setStellungHandling {
      boolean set(fluentData parent, gleisElements.Stellungen newSt, fahrstrasse fs, gleis my_gleis) {
         parent.setStellungTo(newSt);
         my_gleis.tjmAdd();
         return true;
      }
   }

   abstract static class statusChangeTrigger {
      abstract void statusChangeTrigger(fluentData var1, gleis var2);

      void statusChangeTrigger(fluentData parent, gleis my_gleis, gleis before_gl) {
         this.statusChangeTrigger(parent, my_gleis);
      }
   }
}
