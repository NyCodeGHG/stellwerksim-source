package js.java.isolate.sim.gleisbild.gecWorker;

import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleis.gleisElements.element;
import js.java.isolate.sim.gleisbild.gleisbildEditorControl;
import js.java.isolate.sim.gleisbild.gleisbildModel;

public class gecGBlockSelect extends gecSelect {
   protected gleis startGleis = null;
   protected gleis endGleis = null;
   protected int x1;
   protected int x2;
   protected int y1;
   protected int y2;
   protected LinkedList<gleis> box;

   public gecGBlockSelect() {
      super();
   }

   protected void disableBox() {
      this.startGleis = null;
      this.endGleis = null;
      this.gec.getModel().allOff();
      this.box = null;
      this.fireSelectEvent();
   }

   @Override
   public void init(gleisbildEditorControl gec, gecBase lastMode) {
      super.init(gec, lastMode);
      this.disableBox();
   }

   @Override
   public void mousePressed(MouseEvent e) {
      gleis gl = this.gec.gleisUnderMouse(e);
      if (gl != null) {
         this.gec.getModel().clearHighlightedGleis();
         this.gec.getModel().clearMarkedGleis();
         this.startGleis = gl;
      }
   }

   @Override
   public void mouseReleased(MouseEvent e) {
      if (this.startGleis != null) {
         gleis gl = this.gec.gleisUnderMouse(e);
         if (gl != null) {
            this.endGleis = gl;
            this.box = this.makeElements(this.startGleis, this.endGleis);
            this.gec.getModel().addHighlightedGleis(this.box);
            this.fireSelectEvent();
         }
      }
   }

   @Override
   public void mouseDragged(MouseEvent e) {
      if (this.startGleis != null) {
         this.gec.getModel().clearRolloverGleis();
         gleis gl = this.gec.gleisUnderMouse(e);
         if (gl != null) {
            List<gleis> hbox = this.makeElements(this.startGleis, gl);
            this.gec.getModel().addRolloverGleis(hbox);
         }
      }
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      gleis gl = this.gec.gleisUnderMouse(e);
      this.gec.getModel().clearRolloverGleis();
      if (gl != null) {
         this.gec.getModel().addRolloverGleis(gl);
      }
   }

   private LinkedList<gleis> makeElements(gleis startGleis, gleis gl) {
      LinkedList<gleis> ret = new LinkedList();
      this.x1 = Math.min(startGleis.getCol(), gl.getCol());
      this.x2 = Math.max(startGleis.getCol(), gl.getCol());
      this.y1 = Math.min(startGleis.getRow(), gl.getRow());
      this.y2 = Math.max(startGleis.getRow(), gl.getRow());

      for(int y = this.y1; y <= this.y2; ++y) {
         for(int x = this.x1; x <= this.x2; ++x) {
            ret.add(this.gec.getModel().getXY(x, y));
         }
      }

      return ret;
   }

   public boolean hasBlock() {
      return this.startGleis != null && this.endGleis != null;
   }

   public void clearblock() {
      for(gleis gl : this.box) {
         gl.init();
      }

      this.disableBox();
      this.gec.repaint();
   }

   public void fillblock() {
      for(gleis gl : this.box) {
         this.gec.getModel().setGleisValues(gl);
      }

      this.disableBox();
      this.gec.repaint();
   }

   public void fillValue(gleis.EXTENDS name, String value) {
      for(gleis gl : this.box) {
         gl.setExtendValue(name, value);
      }

      this.disableBox();
      this.gec.repaint();
   }

   public void setMasstab(int m) {
      for(gleis gl : this.box) {
         gl.setMasstab(m);
      }

      this.disableBox();
      this.gec.repaint();
   }

   private void scroll(Iterator<gleis> it, int dx, int dy) {
      gleisbildModel model = this.gec.getModel();

      while(it.hasNext()) {
         gleis g1 = (gleis)it.next();
         gleis g2 = model.getXY(g1.getCol() + dx, g1.getRow() + dy);
         model.swap(g1, g2);
      }
   }

   public void scrollUp(boolean over) {
      this.box = this.makeElements(this.startGleis, this.endGleis);
      if (this.y1 != 0) {
         gleisbildModel model = this.gec.getModel();
         model.startSwapOp();
         Iterator<gleis> it = this.box.iterator();
         this.scroll(it, 0, -1);
         model.endSwapOp();
      }
   }

   public void scrollDown(boolean over) {
      this.box = this.makeElements(this.startGleis, this.endGleis);
      gleisbildModel model = this.gec.getModel();
      if (this.y2 != model.getGleisHeight() - 1) {
         model.startSwapOp();
         Iterator<gleis> it = this.box.descendingIterator();
         this.scroll(it, 0, 1);
         model.endSwapOp();
      }
   }

   public void scrollLeft(boolean over) {
      this.box = this.makeElements(this.startGleis, this.endGleis);
      if (this.x1 != 0) {
         gleisbildModel model = this.gec.getModel();
         model.startSwapOp();
         Iterator<gleis> it = this.box.iterator();
         this.scroll(it, -1, 0);
         model.endSwapOp();
      }
   }

   public void scrollRight(boolean over) {
      this.box = this.makeElements(this.startGleis, this.endGleis);
      gleisbildModel model = this.gec.getModel();
      if (this.x2 != model.getGleisWidth() - 1) {
         model.startSwapOp();
         Iterator<gleis> it = this.box.descendingIterator();
         this.scroll(it, 1, 0);
         model.endSwapOp();
      }
   }

   public void mirrorHoriz() {
      this.box = this.makeElements(this.startGleis, this.endGleis);
      gleisbildModel model = this.gec.getModel();
      model.startSwapOp();
      int halfW = (this.x2 - this.x1) / 2;
      Iterator<gleis> it = this.box.descendingIterator();

      while(it.hasNext()) {
         gleis g1 = (gleis)it.next();
         if (g1.getCol() - this.x1 <= halfW) {
            gleis g2 = model.getXY(this.x2 - (g1.getCol() - this.x1), g1.getRow());
            if (!g1.sameGleis(g2)) {
               model.swap(g1, g2);
               g1.getFluentData().swapRichtungHoriz();
            }

            g2.getFluentData().swapRichtungHoriz();
         }
      }

      model.endSwapOp();
   }

   public void mirrorVert() {
      this.box = this.makeElements(this.startGleis, this.endGleis);
      gleisbildModel model = this.gec.getModel();
      model.startSwapOp();
      int halfH = (this.y2 - this.y1) / 2;
      Iterator<gleis> it = this.box.descendingIterator();

      while(it.hasNext()) {
         gleis g1 = (gleis)it.next();
         if (g1.getRow() - this.y1 <= halfH) {
            gleis g2 = model.getXY(g1.getCol(), this.y2 - (g1.getRow() - this.y1));
            if (!g1.sameGleis(g2)) {
               model.swap(g1, g2);
               g1.getFluentData().swapRichtungVert();
               this.swapElement(g1, gleis.ELEMENT_WEICHEOBEN, gleis.ELEMENT_WEICHEUNTEN);
            }

            g2.getFluentData().swapRichtungVert();
            this.swapElement(g2, gleis.ELEMENT_WEICHEOBEN, gleis.ELEMENT_WEICHEUNTEN);
         }
      }

      model.endSwapOp();
   }

   private void swapElement(gleis g, element e1, element e2) {
      if (g.getElement().matches(e1)) {
         g.setElement(e2);
      } else if (g.getElement().matches(e2)) {
         g.setElement(e1);
      }
   }
}
