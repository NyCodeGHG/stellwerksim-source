package js.java.isolate.statusapplet.players;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildControl;
import js.java.isolate.sim.gleisbild.gleisbildViewPanel;
import js.java.isolate.sim.gleisbild.fahrstrassen.fahrstrasse;
import js.java.schaltungen.UserContext;
import js.java.schaltungen.chatcomng.OCCU_KIND;

public class players_gleisbildControl extends gleisbildControl implements MouseMotionListener {
   private BufferedImage[] img_gleise = new BufferedImage[]{null, null};
   private BufferedImage img_zuege = null;
   private final gleisbildFrame parent;
   private int currentImage = 0;
   private CopyOnWriteArraySet<fahrstrasse> setFsList = new CopyOnWriteArraySet();
   private static element[] selms = new element[]{
      gleis.ELEMENT_SIGNAL,
      gleis.ELEMENT_ZWERGSIGNAL,
      gleis.ELEMENT_ZDECKUNGSSIGNAL,
      gleis.ELEMENT_WEICHEOBEN,
      gleis.ELEMENT_WEICHEUNTEN,
      gleis.ELEMENT_AUSFAHRT,
      gleis.ELEMENT_EINFAHRT
   };

   players_gleisbildControl(UserContext uc, gleisbildFrame parent) {
      super(uc);
      this.parent = parent;
   }

   @Override
   public void register(gleisbildViewPanel panel) {
      super.register(panel);
      panel.addMouseMotionListener(this);
   }

   @Override
   public void unregister(gleisbildViewPanel panel) {
      super.unregister(panel);
      this.img_gleise[0] = this.img_gleise[1] = null;
      this.img_zuege = null;
      panel.removeMouseMotionListener(this);
   }

   void createImages() {
      try {
         if (this.img_gleise[0] == null) {
            this.img_gleise[0] = this.createCompatibleImage();
            this.img_gleise[1] = this.createCompatibleImage();
         }

         if (this.img_zuege == null) {
            this.img_zuege = this.createCompatibleImage();
            ((players_gleisbildPanel)this.panel).paintZuege();
         }
      } catch (OutOfMemoryError var2) {
         this.parent.closeMe();
      }
   }

   @Override
   protected void structureChanged(boolean fullChange) {
      if (this.img_gleise[0] == null
         || this.img_gleise[1] == null
         || this.img_zuege == null
         || this.storedWidth != this.model.getGleisWidth()
         || this.storedHeight != this.model.getGleisHeight()) {
         this.img_gleise[0] = this.img_gleise[1] = null;
         this.img_zuege = null;
         this.storedWidth = this.model.getGleisWidth();
         this.storedHeight = this.model.getGleisHeight();
         this.createImages();
      }

      ((players_gleisbildPanel)this.panel).forceRepaint();
   }

   public void flipImage() {
      this.currentImage = 1 - this.currentImage;
   }

   @Override
   public BufferedImage getPaintingImage() {
      return this.img_gleise[1 - this.currentImage];
   }

   @Override
   public BufferedImage getVisibleImage() {
      return this.img_gleise[this.currentImage];
   }

   BufferedImage getZuegeImage() {
      return this.img_zuege;
   }

   private void repaint() {
      ((players_gleisbildPanel)this.panel).paintZuege();
   }

   players_gleisbildModel.zugdata findOrAddZug(players_zug z) {
      if (((players_gleisbildModel)this.model).zuege.containsKey(z.zid)) {
         return (players_gleisbildModel.zugdata)((players_gleisbildModel)this.model).zuege.get(z.zid);
      } else {
         players_gleisbildModel.zugdata zd = new players_gleisbildModel.zugdata(z);
         ((players_gleisbildModel)this.model).zuege.put(z.zid, zd);
         return zd;
      }
   }

   players_gleisbildModel.zugdata findZug(players_zug z) {
      return ((players_gleisbildModel)this.model).zuege.containsKey(z.zid)
         ? (players_gleisbildModel.zugdata)((players_gleisbildModel)this.model).zuege.get(z.zid)
         : null;
   }

   void setZugOn(players_zug z, int enr, boolean passed) {
      players_gleisbildModel.zugdata zd = this.findOrAddZug(z);
      gleis gl = this.model.findFirst(enr, selms);
      if (gl != null) {
         zd.x = this.scaler.getXOfGleisCol(gl.getCol());
         zd.y = this.scaler.getYOfGleisRow(gl.getRow());
         zd.richtung = gl.getRichtung();
         zd.passed = passed || gl.getElement() == gleis.ELEMENT_EINFAHRT;
         this.repaint();
      }
   }

   void setZugOn(players_zug z, String bstg, int x, int y) {
      players_gleisbildModel.zugdata zd = this.findOrAddZug(z);
      gleis gl = this.model.findFirst(gleis.ALLE_BAHNSTEIGE, bstg);
      if (gl != null) {
         if (x >= 0 && y >= 0) {
            gl = this.model.getXY_null(x, y);
         }

         if (gl != null) {
            zd.x = this.scaler.getXOfGleisCol(x);
            zd.y = this.scaler.getYOfGleisRow(y);
            zd.richtung = gl.getRichtung();
            zd.passed = false;
            this.repaint();
         }
      }
   }

   void setZugOff(players_zug z) {
      players_gleisbildModel.zugdata zd = this.findZug(z);
      if (zd != null) {
         ((players_gleisbildModel)this.model).zuege.remove(z.zid);
         this.repaint();
      }
   }

   void setZugOffAll() {
      ((players_gleisbildModel)this.model).zuege.clear();
      Iterator<gleis> it = this.model.findIterator(gleis.ALLE_SIGNALE);

      while(it.hasNext()) {
         gleis signal = (gleis)it.next();
         signal.getFluentData().setStellung(gleis.ST_SIGNAL_ROT);
      }

      for(gleis g : this.model) {
         ((players_fluentData)g.getFluentData()).reset();
      }

      this.setFsList.clear();
      ((players_gleisbildPanel)this.panel).forceRepaint();
   }

   void setSt(int enr, OCCU_KIND kind) {
      Iterator<gleis> it = this.model.findIterator(enr, gleis.ELEMENT_SIGNAL, gleis.ALLE_WEICHEN);

      while(it.hasNext()) {
         gleis g = (gleis)it.next();
         ((players_fluentData)g.getFluentData()).setKind(kind);
      }

      this.repaint();
   }

   public void markFS(fahrstrasse fs) {
      fs.buildWeg(false);
      this.setFsList.add(fs);
   }

   void unmarkFS(fahrstrasse ofs) {
      if (this.setFsList.contains(ofs)) {
         this.setFsList.remove(ofs);
         ofs.freeWeg(false);
      }
   }

   public void mouseDragged(MouseEvent e) {
   }

   public void mouseMoved(MouseEvent e) {
      String enr = "";
      gleis gl = this.gleisUnderMouse(e);
      if (gl != null && gl.getENR() > 0) {
         enr = gl.getENR() + "";
      }

      this.parent.setENR(enr);
   }

   @Override
   public gleis gleisUnderMouse(int x, int y) {
      try {
         int sx = x / (this.panel.getWidth() / this.model.getGleisWidth());
         int sy = y / (this.panel.getHeight() / this.model.getGleisHeight());
         return this.model.getXY_null(sx, sy);
      } catch (ArithmeticException var5) {
         return null;
      }
   }
}
