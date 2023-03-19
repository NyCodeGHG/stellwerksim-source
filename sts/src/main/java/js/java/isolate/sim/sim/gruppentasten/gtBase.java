package js.java.isolate.sim.sim.gruppentasten;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JRadioButton;
import js.java.isolate.sim.gleis.gleis;
import js.java.isolate.sim.gleisbild.gleisbildSimControl;
import js.java.isolate.sim.sim.stellwerksim_main;

public abstract class gtBase implements ActionListener {
   protected final gleisbildSimControl glbControl;
   protected final stellwerksim_main my_main;
   private TasterButton taster = null;

   public gtBase(stellwerksim_main m, gleisbildSimControl glb) {
      super();
      this.my_main = m;
      this.glbControl = glb;
   }

   public void setButton(TasterButton tb) {
      this.taster = tb;
   }

   public void actionPerformed(ActionEvent evt) {
      if (((JRadioButton)evt.getSource()).isSelected()) {
         try {
            this.runCommand(evt.getActionCommand());
         } catch (Exception var3) {
         }

         this.glbControl.clearSelection();
      }

      ((JRadioButton)evt.getSource()).setSelected(false);
   }

   protected void showGleisChange() {
      this.glbControl.fastPaint();
   }

   protected gleis gl_object1() {
      return this.glbControl.getSelection().gl_object1;
   }

   protected gleis signal1() {
      return this.glbControl.getSelection().signal1;
   }

   protected gleis signal2() {
      return this.glbControl.getSelection().signal2;
   }

   protected gleis signal1Garanty() {
      gleis sig = this.signal1();
      if (sig.getElement() == gleis.ELEMENT_SIGNALKNOPF) {
         sig = this.glbControl.getModel().findFirst(new Object[]{this.signal1().getENR(), gleis.ELEMENT_SIGNAL});
      }

      return sig;
   }

   protected gleis signal2Garanty() {
      gleis sig = this.signal2();
      if (sig.getElement() == gleis.ELEMENT_SIGNALKNOPF) {
         sig = this.glbControl.getModel().findFirst(new Object[]{this.signal2().getENR(), gleis.ELEMENT_SIGNAL});
      }

      return sig;
   }

   protected void setLight(TasterButton.LIGHTMODE l) {
      this.taster.setLight(l);
   }

   public abstract String getText();

   public abstract char getKey();

   protected abstract void runCommand(String var1);

   void verifyLight() {
   }

   public TasterButton createButton(boolean white, Color background, String fkey) {
      return new TasterButton(white, background, this, fkey);
   }
}
